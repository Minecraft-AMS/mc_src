/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.OrderedText;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class AdvancementToast
implements Toast {
    public static final int field_41812 = 5000;
    private final Advancement advancement;
    private boolean soundPlayed;

    public AdvancementToast(Advancement advancement) {
        this.advancement = advancement;
    }

    @Override
    public Toast.Visibility draw(MatrixStack matrices, ToastManager manager, long startTime) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        AdvancementDisplay advancementDisplay = this.advancement.getDisplay();
        DrawableHelper.drawTexture(matrices, 0, 0, 0, 0, this.getWidth(), this.getHeight());
        if (advancementDisplay != null) {
            int i;
            List<OrderedText> list = manager.getClient().textRenderer.wrapLines(advancementDisplay.getTitle(), 125);
            int n = i = advancementDisplay.getFrame() == AdvancementFrame.CHALLENGE ? 0xFF88FF : 0xFFFF00;
            if (list.size() == 1) {
                manager.getClient().textRenderer.draw(matrices, advancementDisplay.getFrame().getToastText(), 30.0f, 7.0f, i | 0xFF000000);
                manager.getClient().textRenderer.draw(matrices, list.get(0), 30.0f, 18.0f, -1);
            } else {
                int j = 1500;
                float f = 300.0f;
                if (startTime < 1500L) {
                    int k = MathHelper.floor(MathHelper.clamp((float)(1500L - startTime) / 300.0f, 0.0f, 1.0f) * 255.0f) << 24 | 0x4000000;
                    manager.getClient().textRenderer.draw(matrices, advancementDisplay.getFrame().getToastText(), 30.0f, 11.0f, i | k);
                } else {
                    int k = MathHelper.floor(MathHelper.clamp((float)(startTime - 1500L) / 300.0f, 0.0f, 1.0f) * 252.0f) << 24 | 0x4000000;
                    int l = this.getHeight() / 2 - list.size() * manager.getClient().textRenderer.fontHeight / 2;
                    for (OrderedText orderedText : list) {
                        manager.getClient().textRenderer.draw(matrices, orderedText, 30.0f, (float)l, 0xFFFFFF | k);
                        l += manager.getClient().textRenderer.fontHeight;
                    }
                }
            }
            if (!this.soundPlayed && startTime > 0L) {
                this.soundPlayed = true;
                if (advancementDisplay.getFrame() == AdvancementFrame.CHALLENGE) {
                    manager.getClient().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f));
                }
            }
            manager.getClient().getItemRenderer().renderInGui(matrices, advancementDisplay.getIcon(), 8, 8);
            return (double)startTime >= 5000.0 * manager.getNotificationDisplayTimeMultiplier() ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
        }
        return Toast.Visibility.HIDE;
    }
}

