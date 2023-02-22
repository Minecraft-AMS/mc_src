/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.toast;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class AdvancementToast
implements Toast {
    private final Advancement advancement;
    private boolean soundPlayed;

    public AdvancementToast(Advancement advancement) {
        this.advancement = advancement;
    }

    @Override
    public Toast.Visibility draw(ToastManager manager, long currentTime) {
        manager.getGame().getTextureManager().bindTexture(TOASTS_TEX);
        GlStateManager.color3f(1.0f, 1.0f, 1.0f);
        AdvancementDisplay advancementDisplay = this.advancement.getDisplay();
        manager.blit(0, 0, 0, 0, 160, 32);
        if (advancementDisplay != null) {
            int i;
            List<String> list = manager.getGame().textRenderer.wrapStringToWidthAsList(advancementDisplay.getTitle().asFormattedString(), 125);
            int n = i = advancementDisplay.getFrame() == AdvancementFrame.CHALLENGE ? 0xFF88FF : 0xFFFF00;
            if (list.size() == 1) {
                manager.getGame().textRenderer.draw(I18n.translate("advancements.toast." + advancementDisplay.getFrame().getId(), new Object[0]), 30.0f, 7.0f, i | 0xFF000000);
                manager.getGame().textRenderer.draw(advancementDisplay.getTitle().asFormattedString(), 30.0f, 18.0f, -1);
            } else {
                int j = 1500;
                float f = 300.0f;
                if (currentTime < 1500L) {
                    int k = MathHelper.floor(MathHelper.clamp((float)(1500L - currentTime) / 300.0f, 0.0f, 1.0f) * 255.0f) << 24 | 0x4000000;
                    manager.getGame().textRenderer.draw(I18n.translate("advancements.toast." + advancementDisplay.getFrame().getId(), new Object[0]), 30.0f, 11.0f, i | k);
                } else {
                    int k = MathHelper.floor(MathHelper.clamp((float)(currentTime - 1500L) / 300.0f, 0.0f, 1.0f) * 252.0f) << 24 | 0x4000000;
                    int l = 16 - list.size() * manager.getGame().textRenderer.fontHeight / 2;
                    for (String string : list) {
                        manager.getGame().textRenderer.draw(string, 30.0f, l, 0xFFFFFF | k);
                        l += manager.getGame().textRenderer.fontHeight;
                    }
                }
            }
            if (!this.soundPlayed && currentTime > 0L) {
                this.soundPlayed = true;
                if (advancementDisplay.getFrame() == AdvancementFrame.CHALLENGE) {
                    manager.getGame().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f));
                }
            }
            DiffuseLighting.enableForItems();
            manager.getGame().getItemRenderer().renderGuiItem(null, advancementDisplay.getIcon(), 8, 8);
            return currentTime >= 5000L ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
        }
        return Toast.Visibility.HIDE;
    }
}
