/*
 * MIT License
 *
 * Copyright (c) 2022 Marvin (DerFrZocker)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package uk.co.notnull.customworldsettings;

import ca.spottedleaf.starlight.common.light.StarLightInterface;
import ca.spottedleaf.starlight.common.util.WorldUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.event.EventHandler;

import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public class CustomWorldSettings extends JavaPlugin implements Listener {
    private final static Field dimensionTypeHolderField;
    private final static Field dimensionTypeIdField;

    //Pufferfish inlined fields
    private final static Field minBuildHeightField;
    private final static Field minSectionField;
    private final static Field heightField;
    private final static Field maxBuildHeightField;
    private final static Field maxSectionField;

    //Starlight
    private final static Field starlightMinSectionField;
    private final static Field starlightMaxSectionField;
    private final static Field starlightMinLightSectionField;
    private final static Field starlightMaxLightSectionField;

    static {
        try {
            dimensionTypeHolderField = Level.class.getDeclaredField("E");
            dimensionTypeHolderField.setAccessible(true);
            dimensionTypeIdField = Level.class.getDeclaredField("D");
            dimensionTypeIdField.setAccessible(true);

            minBuildHeightField = Level.class.getDeclaredField("minBuildHeight");
            minBuildHeightField.setAccessible(true);
            minSectionField = Level.class.getDeclaredField("minSection");
            minSectionField.setAccessible(true);
            heightField = Level.class.getDeclaredField("height");
            heightField.setAccessible(true);
            maxBuildHeightField = Level.class.getDeclaredField("maxBuildHeight");
            maxBuildHeightField.setAccessible(true);
            maxSectionField = Level.class.getDeclaredField("maxSection");
            maxSectionField.setAccessible(true);

            starlightMinSectionField = StarLightInterface.class.getDeclaredField("minSection");
            starlightMinSectionField.setAccessible(true);
            starlightMaxSectionField = StarLightInterface.class.getDeclaredField("maxSection");
            starlightMaxSectionField.setAccessible(true);
            starlightMinLightSectionField = StarLightInterface.class.getDeclaredField("minLightSection");
            starlightMinLightSectionField.setAccessible(true);
            starlightMaxLightSectionField = StarLightInterface.class.getDeclaredField("maxLightSection");
            starlightMaxLightSectionField.setAccessible(true);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onWorldInit(WorldInitEvent event) {
        ServerLevel level = ((CraftWorld) event.getWorld()).getHandle();

        try {
            ResourceLocation dimensionTypeID = new ResourceLocation("minecraft", event.getWorld().getName());
            Registry<DimensionType> registry = level.getServer().registryAccess().registryOrThrow(Registries.DIMENSION_TYPE);
            DimensionType type = registry.get(dimensionTypeID);

            if(type != null) {
                getLogger().info("Found dimension type for world " + event.getWorld().getName());
                //Update dimension type and dimension type id fields
                dimensionTypeHolderField.set(level, Holder.direct(type));
                dimensionTypeIdField.set(level, ResourceKey.create(Registries.DIMENSION_TYPE, dimensionTypeID));

                //Update pufferfish fields that are assigned once from the above in the Level constructor
                minBuildHeightField.set(level, type.minY());
                minSectionField.set(level, SectionPos.blockToSectionCoord(type.minY()));
                heightField.set(level, type.height());
                maxBuildHeightField.set(level, type.minY() + type.height());
                maxSectionField.set(level, SectionPos.blockToSectionCoord(type.minY() + type.height() - 1) + 1);

                //Update starlight fields that are also assigned from the above in the ServerLevel constructor
                StarLightInterface theLightEngine = ((ThreadedLevelLightEngine) level.getLightEngine()).theLightEngine;
                starlightMinSectionField.set(theLightEngine, WorldUtil.getMinSection(level));
                starlightMaxSectionField.set(theLightEngine, WorldUtil.getMaxSection(level));
                starlightMinLightSectionField.set(theLightEngine, WorldUtil.getMinLightSection(level));
                starlightMaxLightSectionField.set(theLightEngine, WorldUtil.getMaxLightSection(level));

            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
