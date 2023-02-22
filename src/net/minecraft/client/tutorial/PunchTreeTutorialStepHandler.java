/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.tutorial;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.client.tutorial.FindTreeTutorialStepHandler;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.client.tutorial.TutorialStepHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

@Environment(value=EnvType.CLIENT)
public class PunchTreeTutorialStepHandler
implements TutorialStepHandler {
    private static final Text TITLE = new TranslatableText("tutorial.punch_tree.title", new Object[0]);
    private static final Text DESCRIPTION = new TranslatableText("tutorial.punch_tree.description", TutorialManager.getKeybindName("attack"));
    private final TutorialManager manager;
    private TutorialToast field_5637;
    private int ticks;
    private int field_5635;

    public PunchTreeTutorialStepHandler(TutorialManager tutorialManager) {
        this.manager = tutorialManager;
    }

    @Override
    public void tick() {
        ClientPlayerEntity clientPlayerEntity;
        ++this.ticks;
        if (this.manager.getGameMode() != GameMode.SURVIVAL) {
            this.manager.setStep(TutorialStep.NONE);
            return;
        }
        if (this.ticks == 1 && (clientPlayerEntity = this.manager.getClient().player) != null) {
            if (clientPlayerEntity.inventory.contains(ItemTags.LOGS)) {
                this.manager.setStep(TutorialStep.CRAFT_PLANKS);
                return;
            }
            if (FindTreeTutorialStepHandler.method_4896(clientPlayerEntity)) {
                this.manager.setStep(TutorialStep.CRAFT_PLANKS);
                return;
            }
        }
        if ((this.ticks >= 600 || this.field_5635 > 3) && this.field_5637 == null) {
            this.field_5637 = new TutorialToast(TutorialToast.Type.TREE, TITLE, DESCRIPTION, true);
            this.manager.getClient().getToastManager().add(this.field_5637);
        }
    }

    @Override
    public void destroy() {
        if (this.field_5637 != null) {
            this.field_5637.hide();
            this.field_5637 = null;
        }
    }

    @Override
    public void onBlockAttacked(ClientWorld client, BlockPos pos, BlockState state, float f) {
        boolean bl = state.matches(BlockTags.LOGS);
        if (bl && f > 0.0f) {
            if (this.field_5637 != null) {
                this.field_5637.setProgress(f);
            }
            if (f >= 1.0f) {
                this.manager.setStep(TutorialStep.OPEN_INVENTORY);
            }
        } else if (this.field_5637 != null) {
            this.field_5637.setProgress(0.0f);
        } else if (bl) {
            ++this.field_5635;
        }
    }

    @Override
    public void onSlotUpdate(ItemStack stack) {
        if (ItemTags.LOGS.contains(stack.getItem())) {
            this.manager.setStep(TutorialStep.CRAFT_PLANKS);
            return;
        }
    }
}
