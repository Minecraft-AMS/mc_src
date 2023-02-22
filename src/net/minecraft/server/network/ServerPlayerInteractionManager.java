/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.network;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.NameableContainerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlayerInteractionManager {
    private static final Logger field_20325 = LogManager.getLogger();
    public ServerWorld world;
    public ServerPlayerEntity player;
    private GameMode gameMode = GameMode.NOT_SET;
    private boolean field_14003;
    private int field_20326;
    private BlockPos field_20327 = BlockPos.ORIGIN;
    private int field_14000;
    private boolean field_20328;
    private BlockPos field_20329 = BlockPos.ORIGIN;
    private int field_20330;
    private int field_20331 = -1;

    public ServerPlayerInteractionManager(ServerWorld serverWorld) {
        this.world = serverWorld;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
        gameMode.setAbilitites(this.player.abilities);
        this.player.sendAbilitiesUpdate();
        this.player.server.getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_GAME_MODE, this.player));
        this.world.updatePlayersSleeping();
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    public boolean isSurvivalLike() {
        return this.gameMode.isSurvivalLike();
    }

    public boolean isCreative() {
        return this.gameMode.isCreative();
    }

    public void setGameModeIfNotPresent(GameMode gameMode) {
        if (this.gameMode == GameMode.NOT_SET) {
            this.gameMode = gameMode;
        }
        this.setGameMode(this.gameMode);
    }

    public void update() {
        ++this.field_14000;
        if (this.field_20328) {
            BlockState blockState = this.world.getBlockState(this.field_20329);
            if (blockState.isAir()) {
                this.field_20328 = false;
            } else {
                float f = this.method_21716(blockState, this.field_20329);
                if (f >= 1.0f) {
                    this.field_20328 = false;
                    this.tryBreakBlock(this.field_20329);
                }
            }
        } else if (this.field_14003) {
            BlockState blockState = this.world.getBlockState(this.field_20327);
            if (blockState.isAir()) {
                this.world.setBlockBreakingInfo(this.player.getEntityId(), this.field_20327, -1);
                this.field_20331 = -1;
                this.field_14003 = false;
            } else {
                this.method_21716(blockState, this.field_20327);
            }
        }
    }

    private float method_21716(BlockState blockState, BlockPos blockPos) {
        int i = this.field_14000 - this.field_20330;
        float f = blockState.calcBlockBreakingDelta(this.player, this.player.world, blockPos) * (float)(i + 1);
        int j = (int)(f * 10.0f);
        if (j != this.field_20331) {
            this.world.setBlockBreakingInfo(this.player.getEntityId(), blockPos, j);
            this.field_20331 = j;
        }
        return f;
    }

    public void method_14263(BlockPos blockPos, PlayerActionC2SPacket.Action action, Direction direction, int i) {
        double f;
        double e;
        double d = this.player.x - ((double)blockPos.getX() + 0.5);
        double g = d * d + (e = this.player.y - ((double)blockPos.getY() + 0.5) + 1.5) * e + (f = this.player.z - ((double)blockPos.getZ() + 0.5)) * f;
        if (g > 36.0) {
            this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(blockPos, this.world.getBlockState(blockPos), action, false));
            return;
        }
        if (blockPos.getY() >= i) {
            this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(blockPos, this.world.getBlockState(blockPos), action, false));
            return;
        }
        if (action == PlayerActionC2SPacket.Action.START_DESTROY_BLOCK) {
            if (!this.world.canPlayerModifyAt(this.player, blockPos)) {
                this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(blockPos, this.world.getBlockState(blockPos), action, false));
                return;
            }
            if (this.isCreative()) {
                if (!this.world.method_8506(null, blockPos, direction)) {
                    this.method_21717(blockPos, action);
                } else {
                    this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(blockPos, this.world.getBlockState(blockPos), action, true));
                }
                return;
            }
            if (this.player.method_21701(this.world, blockPos, this.gameMode)) {
                this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(blockPos, this.world.getBlockState(blockPos), action, false));
                return;
            }
            this.world.method_8506(null, blockPos, direction);
            this.field_20326 = this.field_14000;
            float h = 1.0f;
            BlockState blockState = this.world.getBlockState(blockPos);
            if (!blockState.isAir()) {
                blockState.onBlockBreakStart(this.world, blockPos, this.player);
                h = blockState.calcBlockBreakingDelta(this.player, this.player.world, blockPos);
            }
            if (!blockState.isAir() && h >= 1.0f) {
                this.method_21717(blockPos, action);
            } else {
                this.field_14003 = true;
                this.field_20327 = blockPos;
                int j = (int)(h * 10.0f);
                this.world.setBlockBreakingInfo(this.player.getEntityId(), blockPos, j);
                this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(blockPos, this.world.getBlockState(blockPos), action, true));
                this.field_20331 = j;
            }
        } else if (action == PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK) {
            if (blockPos.equals(this.field_20327)) {
                int k = this.field_14000 - this.field_20326;
                BlockState blockState = this.world.getBlockState(blockPos);
                if (!blockState.isAir()) {
                    float l = blockState.calcBlockBreakingDelta(this.player, this.player.world, blockPos) * (float)(k + 1);
                    if (l >= 0.7f) {
                        this.field_14003 = false;
                        this.world.setBlockBreakingInfo(this.player.getEntityId(), blockPos, -1);
                        this.method_21717(blockPos, action);
                        return;
                    }
                    if (!this.field_20328) {
                        this.field_14003 = false;
                        this.field_20328 = true;
                        this.field_20329 = blockPos;
                        this.field_20330 = this.field_20326;
                    }
                }
            }
            this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(blockPos, this.world.getBlockState(blockPos), action, true));
        } else if (action == PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK) {
            this.field_14003 = false;
            this.world.setBlockBreakingInfo(this.player.getEntityId(), this.field_20327, -1);
            this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(blockPos, this.world.getBlockState(blockPos), action, true));
        }
    }

    public void method_21717(BlockPos blockPos, PlayerActionC2SPacket.Action action) {
        if (this.tryBreakBlock(blockPos)) {
            this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(blockPos, this.world.getBlockState(blockPos), action, true));
        } else {
            this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(blockPos, this.world.getBlockState(blockPos), action, false));
        }
    }

    public boolean tryBreakBlock(BlockPos blockPos) {
        BlockState blockState = this.world.getBlockState(blockPos);
        if (!this.player.getMainHandStack().getItem().canMine(blockState, this.world, blockPos, this.player)) {
            return false;
        }
        BlockEntity blockEntity = this.world.getBlockEntity(blockPos);
        Block block = blockState.getBlock();
        if ((block instanceof CommandBlock || block instanceof StructureBlock || block instanceof JigsawBlock) && !this.player.isCreativeLevelTwoOp()) {
            this.world.updateListeners(blockPos, blockState, blockState, 3);
            return false;
        }
        if (this.player.method_21701(this.world, blockPos, this.gameMode)) {
            return false;
        }
        block.onBreak(this.world, blockPos, blockState, this.player);
        boolean bl = this.world.removeBlock(blockPos, false);
        if (bl) {
            block.onBroken(this.world, blockPos, blockState);
        }
        if (this.isCreative()) {
            return true;
        }
        ItemStack itemStack = this.player.getMainHandStack();
        boolean bl2 = this.player.isUsingEffectiveTool(blockState);
        itemStack.postMine(this.world, blockState, blockPos, this.player);
        if (bl && bl2) {
            ItemStack itemStack2 = itemStack.isEmpty() ? ItemStack.EMPTY : itemStack.copy();
            block.afterBreak(this.world, this.player, blockPos, blockState, blockEntity, itemStack2);
        }
        return true;
    }

    public ActionResult interactItem(PlayerEntity player, World world, ItemStack stack, Hand hand) {
        if (this.gameMode == GameMode.SPECTATOR) {
            return ActionResult.PASS;
        }
        if (player.getItemCooldownManager().isCoolingDown(stack.getItem())) {
            return ActionResult.PASS;
        }
        int i = stack.getCount();
        int j = stack.getDamage();
        TypedActionResult<ItemStack> typedActionResult = stack.use(world, player, hand);
        ItemStack itemStack = typedActionResult.getValue();
        if (itemStack == stack && itemStack.getCount() == i && itemStack.getMaxUseTime() <= 0 && itemStack.getDamage() == j) {
            return typedActionResult.getResult();
        }
        if (typedActionResult.getResult() == ActionResult.FAIL && itemStack.getMaxUseTime() > 0 && !player.isUsingItem()) {
            return typedActionResult.getResult();
        }
        player.setStackInHand(hand, itemStack);
        if (this.isCreative()) {
            itemStack.setCount(i);
            if (itemStack.isDamageable()) {
                itemStack.setDamage(j);
            }
        }
        if (itemStack.isEmpty()) {
            player.setStackInHand(hand, ItemStack.EMPTY);
        }
        if (!player.isUsingItem()) {
            ((ServerPlayerEntity)player).openContainer(player.playerContainer);
        }
        return typedActionResult.getResult();
    }

    public ActionResult interactBlock(PlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult) {
        boolean bl2;
        BlockPos blockPos = hitResult.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        if (this.gameMode == GameMode.SPECTATOR) {
            NameableContainerFactory nameableContainerFactory = blockState.createContainerFactory(world, blockPos);
            if (nameableContainerFactory != null) {
                player.openContainer(nameableContainerFactory);
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }
        boolean bl = !player.getMainHandStack().isEmpty() || !player.getOffHandStack().isEmpty();
        boolean bl3 = bl2 = player.isSneaking() && bl;
        if (!bl2 && blockState.activate(world, player, hand, hitResult)) {
            return ActionResult.SUCCESS;
        }
        if (stack.isEmpty() || player.getItemCooldownManager().isCoolingDown(stack.getItem())) {
            return ActionResult.PASS;
        }
        ItemUsageContext itemUsageContext = new ItemUsageContext(player, hand, hitResult);
        if (this.isCreative()) {
            int i = stack.getCount();
            ActionResult actionResult = stack.useOnBlock(itemUsageContext);
            stack.setCount(i);
            return actionResult;
        }
        return stack.useOnBlock(itemUsageContext);
    }

    public void setWorld(ServerWorld world) {
        this.world = world;
    }
}

