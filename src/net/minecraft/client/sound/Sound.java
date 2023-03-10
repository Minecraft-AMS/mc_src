/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.SoundContainer;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class Sound
implements SoundContainer<Sound> {
    private final Identifier id;
    private final float volume;
    private final float pitch;
    private final int weight;
    private final RegistrationType registrationType;
    private final boolean stream;
    private final boolean preload;
    private final int attenuation;

    public Sound(String id, float volume, float pitch, int weight, RegistrationType registrationType, boolean stream, boolean preload, int attenuation) {
        this.id = new Identifier(id);
        this.volume = volume;
        this.pitch = pitch;
        this.weight = weight;
        this.registrationType = registrationType;
        this.stream = stream;
        this.preload = preload;
        this.attenuation = attenuation;
    }

    public Identifier getIdentifier() {
        return this.id;
    }

    public Identifier getLocation() {
        return new Identifier(this.id.getNamespace(), "sounds/" + this.id.getPath() + ".ogg");
    }

    public float getVolume() {
        return this.volume;
    }

    public float getPitch() {
        return this.pitch;
    }

    @Override
    public int getWeight() {
        return this.weight;
    }

    @Override
    public Sound getSound() {
        return this;
    }

    @Override
    public void preload(SoundSystem soundSystem) {
        if (this.preload) {
            soundSystem.addPreloadedSound(this);
        }
    }

    public RegistrationType getRegistrationType() {
        return this.registrationType;
    }

    public boolean isStreamed() {
        return this.stream;
    }

    public boolean isPreloaded() {
        return this.preload;
    }

    public int getAttenuation() {
        return this.attenuation;
    }

    public String toString() {
        return "Sound[" + this.id + "]";
    }

    @Override
    public /* synthetic */ Object getSound() {
        return this.getSound();
    }

    @Environment(value=EnvType.CLIENT)
    public static final class RegistrationType
    extends Enum<RegistrationType> {
        public static final /* enum */ RegistrationType FILE = new RegistrationType("file");
        public static final /* enum */ RegistrationType SOUND_EVENT = new RegistrationType("event");
        private final String name;
        private static final /* synthetic */ RegistrationType[] field_5471;

        public static RegistrationType[] values() {
            return (RegistrationType[])field_5471.clone();
        }

        public static RegistrationType valueOf(String string) {
            return Enum.valueOf(RegistrationType.class, string);
        }

        private RegistrationType(String name) {
            this.name = name;
        }

        @Nullable
        public static RegistrationType getByName(String name) {
            for (RegistrationType registrationType : RegistrationType.values()) {
                if (!registrationType.name.equals(name)) continue;
                return registrationType;
            }
            return null;
        }

        private static /* synthetic */ RegistrationType[] method_36926() {
            return new RegistrationType[]{FILE, SOUND_EVENT};
        }

        static {
            field_5471 = RegistrationType.method_36926();
        }
    }
}

