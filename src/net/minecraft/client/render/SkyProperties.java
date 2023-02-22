/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class SkyProperties {
    private static final Object2ObjectMap<Identifier, SkyProperties> BY_IDENTIFIER = (Object2ObjectMap)Util.make(new Object2ObjectArrayMap(), object2ObjectArrayMap -> {
        Overworld overworld = new Overworld();
        object2ObjectArrayMap.defaultReturnValue((Object)overworld);
        object2ObjectArrayMap.put((Object)DimensionType.OVERWORLD_ID, (Object)overworld);
        object2ObjectArrayMap.put((Object)DimensionType.THE_NETHER_ID, (Object)new Nether());
        object2ObjectArrayMap.put((Object)DimensionType.THE_END_ID, (Object)new End());
    });
    private final float[] rgba = new float[4];
    private final float cloudsHeight;
    private final boolean alternateSkyColor;
    private final SkyType skyType;
    private final boolean brightenLighting;
    private final boolean darkened;

    public SkyProperties(float cloudsHeight, boolean alternateSkyColor, SkyType skyType, boolean brightenLighting, boolean darkened) {
        this.cloudsHeight = cloudsHeight;
        this.alternateSkyColor = alternateSkyColor;
        this.skyType = skyType;
        this.brightenLighting = brightenLighting;
        this.darkened = darkened;
    }

    public static SkyProperties byDimensionType(DimensionType dimensionType) {
        return (SkyProperties)BY_IDENTIFIER.get((Object)dimensionType.getSkyProperties());
    }

    @Nullable
    public float[] getFogColorOverride(float skyAngle, float tickDelta) {
        float f = 0.4f;
        float g = MathHelper.cos(skyAngle * ((float)Math.PI * 2)) - 0.0f;
        float h = -0.0f;
        if (g >= -0.4f && g <= 0.4f) {
            float i = (g - -0.0f) / 0.4f * 0.5f + 0.5f;
            float j = 1.0f - (1.0f - MathHelper.sin(i * (float)Math.PI)) * 0.99f;
            j *= j;
            this.rgba[0] = i * 0.3f + 0.7f;
            this.rgba[1] = i * i * 0.7f + 0.2f;
            this.rgba[2] = i * i * 0.0f + 0.2f;
            this.rgba[3] = j;
            return this.rgba;
        }
        return null;
    }

    public float getCloudsHeight() {
        return this.cloudsHeight;
    }

    public boolean isAlternateSkyColor() {
        return this.alternateSkyColor;
    }

    public abstract Vec3d adjustFogColor(Vec3d var1, float var2);

    public abstract boolean useThickFog(int var1, int var2);

    public SkyType getSkyType() {
        return this.skyType;
    }

    public boolean shouldBrightenLighting() {
        return this.brightenLighting;
    }

    public boolean isDarkened() {
        return this.darkened;
    }

    @Environment(value=EnvType.CLIENT)
    public static class End
    extends SkyProperties {
        public End() {
            super(Float.NaN, false, SkyType.END, true, false);
        }

        @Override
        public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
            return color.multiply(0.15f);
        }

        @Override
        public boolean useThickFog(int camX, int camY) {
            return false;
        }

        @Override
        @Nullable
        public float[] getFogColorOverride(float skyAngle, float tickDelta) {
            return null;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Overworld
    extends SkyProperties {
        public Overworld() {
            super(128.0f, true, SkyType.NORMAL, false, false);
        }

        @Override
        public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
            return color.multiply(sunHeight * 0.94f + 0.06f, sunHeight * 0.94f + 0.06f, sunHeight * 0.91f + 0.09f);
        }

        @Override
        public boolean useThickFog(int camX, int camY) {
            return false;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Nether
    extends SkyProperties {
        public Nether() {
            super(Float.NaN, true, SkyType.NONE, false, true);
        }

        @Override
        public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
            return color;
        }

        @Override
        public boolean useThickFog(int camX, int camY) {
            return true;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum SkyType {
        NONE,
        NORMAL,
        END;

    }
}

