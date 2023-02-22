/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.biome;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.sound.BiomeAdditionsSound;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeParticleConfig;
import org.jetbrains.annotations.Nullable;

public class BiomeEffects {
    public static final Codec<BiomeEffects> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("fog_color").forGetter(biomeEffects -> biomeEffects.fogColor), (App)Codec.INT.fieldOf("water_color").forGetter(biomeEffects -> biomeEffects.waterColor), (App)Codec.INT.fieldOf("water_fog_color").forGetter(biomeEffects -> biomeEffects.waterFogColor), (App)Codec.INT.fieldOf("sky_color").forGetter(biomeEffects -> biomeEffects.skyColor), (App)Codec.INT.optionalFieldOf("foliage_color").forGetter(biomeEffects -> biomeEffects.foliageColor), (App)Codec.INT.optionalFieldOf("grass_color").forGetter(biomeEffects -> biomeEffects.grassColor), (App)GrassColorModifier.CODEC.optionalFieldOf("grass_color_modifier", (Object)GrassColorModifier.NONE).forGetter(biomeEffects -> biomeEffects.grassColorModifier), (App)BiomeParticleConfig.CODEC.optionalFieldOf("particle").forGetter(biomeEffects -> biomeEffects.particleConfig), (App)SoundEvent.CODEC.optionalFieldOf("ambient_sound").forGetter(biomeEffects -> biomeEffects.loopSound), (App)BiomeMoodSound.CODEC.optionalFieldOf("mood_sound").forGetter(biomeEffects -> biomeEffects.moodSound), (App)BiomeAdditionsSound.CODEC.optionalFieldOf("additions_sound").forGetter(biomeEffects -> biomeEffects.additionsSound), (App)MusicSound.CODEC.optionalFieldOf("music").forGetter(biomeEffects -> biomeEffects.music)).apply((Applicative)instance, BiomeEffects::new));
    private final int fogColor;
    private final int waterColor;
    private final int waterFogColor;
    private final int skyColor;
    private final Optional<Integer> foliageColor;
    private final Optional<Integer> grassColor;
    private final GrassColorModifier grassColorModifier;
    private final Optional<BiomeParticleConfig> particleConfig;
    private final Optional<SoundEvent> loopSound;
    private final Optional<BiomeMoodSound> moodSound;
    private final Optional<BiomeAdditionsSound> additionsSound;
    private final Optional<MusicSound> music;

    BiomeEffects(int fogColor, int waterColor, int waterFogColor, int skyColor, Optional<Integer> foliageColor, Optional<Integer> grassColor, GrassColorModifier grassColorModifier, Optional<BiomeParticleConfig> particleConfig, Optional<SoundEvent> loopSound, Optional<BiomeMoodSound> moodSound, Optional<BiomeAdditionsSound> additionsSound, Optional<MusicSound> music) {
        this.fogColor = fogColor;
        this.waterColor = waterColor;
        this.waterFogColor = waterFogColor;
        this.skyColor = skyColor;
        this.foliageColor = foliageColor;
        this.grassColor = grassColor;
        this.grassColorModifier = grassColorModifier;
        this.particleConfig = particleConfig;
        this.loopSound = loopSound;
        this.moodSound = moodSound;
        this.additionsSound = additionsSound;
        this.music = music;
    }

    public int getFogColor() {
        return this.fogColor;
    }

    public int getWaterColor() {
        return this.waterColor;
    }

    public int getWaterFogColor() {
        return this.waterFogColor;
    }

    public int getSkyColor() {
        return this.skyColor;
    }

    public Optional<Integer> getFoliageColor() {
        return this.foliageColor;
    }

    public Optional<Integer> getGrassColor() {
        return this.grassColor;
    }

    public GrassColorModifier getGrassColorModifier() {
        return this.grassColorModifier;
    }

    public Optional<BiomeParticleConfig> getParticleConfig() {
        return this.particleConfig;
    }

    public Optional<SoundEvent> getLoopSound() {
        return this.loopSound;
    }

    public Optional<BiomeMoodSound> getMoodSound() {
        return this.moodSound;
    }

    public Optional<BiomeAdditionsSound> getAdditionsSound() {
        return this.additionsSound;
    }

    public Optional<MusicSound> getMusic() {
        return this.music;
    }

    /*
     * Uses 'sealed' constructs - enablewith --sealed true
     */
    public static abstract class GrassColorModifier
    extends Enum<GrassColorModifier>
    implements StringIdentifiable {
        public static final /* enum */ GrassColorModifier NONE = new GrassColorModifier("none"){

            @Override
            public int getModifiedGrassColor(double x, double z, int color) {
                return color;
            }
        };
        public static final /* enum */ GrassColorModifier DARK_FOREST = new GrassColorModifier("dark_forest"){

            @Override
            public int getModifiedGrassColor(double x, double z, int color) {
                return (color & 0xFEFEFE) + 2634762 >> 1;
            }
        };
        public static final /* enum */ GrassColorModifier SWAMP = new GrassColorModifier("swamp"){

            @Override
            public int getModifiedGrassColor(double x, double z, int color) {
                double d = Biome.FOLIAGE_NOISE.sample(x * 0.0225, z * 0.0225, false);
                if (d < -0.1) {
                    return 5011004;
                }
                return 6975545;
            }
        };
        private final String name;
        public static final Codec<GrassColorModifier> CODEC;
        private static final /* synthetic */ GrassColorModifier[] field_26432;

        public static GrassColorModifier[] values() {
            return (GrassColorModifier[])field_26432.clone();
        }

        public static GrassColorModifier valueOf(String string) {
            return Enum.valueOf(GrassColorModifier.class, string);
        }

        public abstract int getModifiedGrassColor(double var1, double var3, int var5);

        GrassColorModifier(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        private static /* synthetic */ GrassColorModifier[] method_36701() {
            return new GrassColorModifier[]{NONE, DARK_FOREST, SWAMP};
        }

        static {
            field_26432 = GrassColorModifier.method_36701();
            CODEC = StringIdentifiable.createCodec(GrassColorModifier::values);
        }
    }

    public static class Builder {
        private OptionalInt fogColor = OptionalInt.empty();
        private OptionalInt waterColor = OptionalInt.empty();
        private OptionalInt waterFogColor = OptionalInt.empty();
        private OptionalInt skyColor = OptionalInt.empty();
        private Optional<Integer> foliageColor = Optional.empty();
        private Optional<Integer> grassColor = Optional.empty();
        private GrassColorModifier grassColorModifier = GrassColorModifier.NONE;
        private Optional<BiomeParticleConfig> particleConfig = Optional.empty();
        private Optional<SoundEvent> loopSound = Optional.empty();
        private Optional<BiomeMoodSound> moodSound = Optional.empty();
        private Optional<BiomeAdditionsSound> additionsSound = Optional.empty();
        private Optional<MusicSound> musicSound = Optional.empty();

        public Builder fogColor(int fogColor) {
            this.fogColor = OptionalInt.of(fogColor);
            return this;
        }

        public Builder waterColor(int waterColor) {
            this.waterColor = OptionalInt.of(waterColor);
            return this;
        }

        public Builder waterFogColor(int waterFogColor) {
            this.waterFogColor = OptionalInt.of(waterFogColor);
            return this;
        }

        public Builder skyColor(int skyColor) {
            this.skyColor = OptionalInt.of(skyColor);
            return this;
        }

        public Builder foliageColor(int foliageColor) {
            this.foliageColor = Optional.of(foliageColor);
            return this;
        }

        public Builder grassColor(int grassColor) {
            this.grassColor = Optional.of(grassColor);
            return this;
        }

        public Builder grassColorModifier(GrassColorModifier grassColorModifier) {
            this.grassColorModifier = grassColorModifier;
            return this;
        }

        public Builder particleConfig(BiomeParticleConfig particleConfig) {
            this.particleConfig = Optional.of(particleConfig);
            return this;
        }

        public Builder loopSound(SoundEvent sound) {
            this.loopSound = Optional.of(sound);
            return this;
        }

        public Builder moodSound(BiomeMoodSound moodSound) {
            this.moodSound = Optional.of(moodSound);
            return this;
        }

        public Builder additionsSound(BiomeAdditionsSound additionsSound) {
            this.additionsSound = Optional.of(additionsSound);
            return this;
        }

        public Builder music(@Nullable MusicSound music) {
            this.musicSound = Optional.ofNullable(music);
            return this;
        }

        public BiomeEffects build() {
            return new BiomeEffects(this.fogColor.orElseThrow(() -> new IllegalStateException("Missing 'fog' color.")), this.waterColor.orElseThrow(() -> new IllegalStateException("Missing 'water' color.")), this.waterFogColor.orElseThrow(() -> new IllegalStateException("Missing 'water fog' color.")), this.skyColor.orElseThrow(() -> new IllegalStateException("Missing 'sky' color.")), this.foliageColor, this.grassColor, this.grassColorModifier, this.particleConfig, this.loopSound, this.moodSound, this.additionsSound, this.musicSound);
        }
    }
}

