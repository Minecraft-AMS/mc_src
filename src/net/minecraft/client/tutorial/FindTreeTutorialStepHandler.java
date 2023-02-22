/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.tutorial;

import com.google.common.collect.Sets;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.client.tutorial.TutorialStepHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.GameMode;

@Environment(value=EnvType.CLIENT)
public class FindTreeTutorialStepHandler
implements TutorialStepHandler {
    private static final Set<Block> TREE_BLOCKS = Sets.newHashSet((Object[])new Block[]{Blocks.OAK_LOG, Blocks.SPRUCE_LOG, Blocks.BIRCH_LOG, Blocks.JUNGLE_LOG, Blocks.ACACIA_LOG, Blocks.DARK_OAK_LOG, Blocks.OAK_WOOD, Blocks.SPRUCE_WOOD, Blocks.BIRCH_WOOD, Blocks.JUNGLE_WOOD, Blocks.ACACIA_WOOD, Blocks.DARK_OAK_WOOD, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.BIRCH_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES});
    private static final Text TITLE = new TranslatableText("tutorial.find_tree.title", new Object[0]);
    private static final Text DESCRIPTION = new TranslatableText("tutorial.find_tree.description", new Object[0]);
    private final TutorialManager tutorialManager;
    private TutorialToast toast;
    private int ticks;

    public FindTreeTutorialStepHandler(TutorialManager tutorialManager) {
        this.tutorialManager = tutorialManager;
    }

    @Override
    public void tick() {
        ClientPlayerEntity clientPlayerEntity;
        ++this.ticks;
        if (this.tutorialManager.getGameMode() != GameMode.SURVIVAL) {
            this.tutorialManager.setStep(TutorialStep.NONE);
            return;
        }
        if (this.ticks == 1 && (clientPlayerEntity = this.tutorialManager.getClient().player) != null) {
            for (Block block : TREE_BLOCKS) {
                if (!clientPlayerEntity.inventory.contains(new ItemStack(block))) continue;
                this.tutorialManager.setStep(TutorialStep.CRAFT_PLANKS);
                return;
            }
            if (FindTreeTutorialStepHandler.method_4896(clientPlayerEntity)) {
                this.tutorialManager.setStep(TutorialStep.CRAFT_PLANKS);
                return;
            }
        }
        if (this.ticks >= 6000 && this.toast == null) {
            this.toast = new TutorialToast(TutorialToast.Type.TREE, TITLE, DESCRIPTION, false);
            this.tutorialManager.getClient().getToastManager().add(this.toast);
        }
    }

    @Override
    public void destroy() {
        if (this.toast != null) {
            this.toast.hide();
            this.toast = null;
        }
    }

    @Override
    public void onTarget(ClientWorld world, HitResult hitResult) {
        BlockState blockState;
        if (hitResult.getType() == HitResult.Type.BLOCK && TREE_BLOCKS.contains((blockState = world.getBlockState(((BlockHitResult)hitResult).getBlockPos())).getBlock())) {
            this.tutorialManager.setStep(TutorialStep.PUNCH_TREE);
        }
    }

    @Override
    public void onSlotUpdate(ItemStack stack) {
        for (Block block : TREE_BLOCKS) {
            if (stack.getItem() != block.asItem()) continue;
            this.tutorialManager.setStep(TutorialStep.CRAFT_PLANKS);
            return;
        }
    }

    public static boolean method_4896(ClientPlayerEntity clientPlayerEntity) {
        for (Block block : TREE_BLOCKS) {
            if (clientPlayerEntity.getStatHandler().getStat(Stats.MINED.getOrCreateStat(block)) <= 0) continue;
            return true;
        }
        return false;
    }
}
