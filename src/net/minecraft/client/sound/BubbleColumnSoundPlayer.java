/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BubbleColumnBlock;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.ClientPlayerTickable;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class BubbleColumnSoundPlayer
implements ClientPlayerTickable {
    private final ClientPlayerEntity player;
    private boolean hasPlayedForCurrentColumn;
    private boolean firstTick = true;

    public BubbleColumnSoundPlayer(ClientPlayerEntity player) {
        this.player = player;
    }

    @Override
    public void tick() {
        World world = this.player.world;
        BlockState blockState2 = world.method_29556(this.player.getBoundingBox().expand(0.0, -0.4f, 0.0).contract(0.001)).filter(blockState -> blockState.isOf(Blocks.BUBBLE_COLUMN)).findFirst().orElse(null);
        if (blockState2 != null) {
            if (!this.hasPlayedForCurrentColumn && !this.firstTick && blockState2.isOf(Blocks.BUBBLE_COLUMN) && !this.player.isSpectator()) {
                boolean bl = blockState2.get(BubbleColumnBlock.DRAG);
                if (bl) {
                    this.player.playSound(SoundEvents.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_INSIDE, 1.0f, 1.0f);
                } else {
                    this.player.playSound(SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_INSIDE, 1.0f, 1.0f);
                }
            }
            this.hasPlayedForCurrentColumn = true;
        } else {
            this.hasPlayedForCurrentColumn = false;
        }
        this.firstTick = false;
    }
}

