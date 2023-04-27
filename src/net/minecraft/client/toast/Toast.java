/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.toast;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public interface Toast {
    public static final Identifier TEXTURE = new Identifier("textures/gui/toasts.png");
    public static final Object TYPE = new Object();
    public static final int BASE_HEIGHT = 32;

    public Visibility draw(DrawContext var1, ToastManager var2, long var3);

    default public Object getType() {
        return TYPE;
    }

    default public int getWidth() {
        return 160;
    }

    default public int getHeight() {
        return 32;
    }

    default public int getRequiredSpaceCount() {
        return MathHelper.ceilDiv(this.getHeight(), 32);
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Visibility
    extends Enum<Visibility> {
        public static final /* enum */ Visibility SHOW = new Visibility(SoundEvents.UI_TOAST_IN);
        public static final /* enum */ Visibility HIDE = new Visibility(SoundEvents.UI_TOAST_OUT);
        private final SoundEvent sound;
        private static final /* synthetic */ Visibility[] field_2212;

        public static Visibility[] values() {
            return (Visibility[])field_2212.clone();
        }

        public static Visibility valueOf(String string) {
            return Enum.valueOf(Visibility.class, string);
        }

        private Visibility(SoundEvent sound) {
            this.sound = sound;
        }

        public void playSound(SoundManager soundManager) {
            soundManager.play(PositionedSoundInstance.master(this.sound, 1.0f, 1.0f));
        }

        private static /* synthetic */ Visibility[] method_36872() {
            return new Visibility[]{SHOW, HIDE};
        }

        static {
            field_2212 = Visibility.method_36872();
        }
    }
}

