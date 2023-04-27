/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DynamicOps
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.block.entity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SignText;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class SignBlockEntity
extends BlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAX_TEXT_WIDTH = 90;
    private static final int TEXT_LINE_HEIGHT = 10;
    @Nullable
    private UUID editor;
    private SignText frontText = this.createText();
    private SignText backText = this.createText();
    private boolean waxed;

    public SignBlockEntity(BlockPos pos, BlockState state) {
        this((BlockEntityType)BlockEntityType.SIGN, pos, state);
    }

    public SignBlockEntity(BlockEntityType blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    protected SignText createText() {
        return new SignText();
    }

    public boolean isPlayerFacingFront(PlayerEntity player) {
        Block block = this.getCachedState().getBlock();
        if (block instanceof AbstractSignBlock) {
            float g;
            AbstractSignBlock abstractSignBlock = (AbstractSignBlock)block;
            Vec3d vec3d = abstractSignBlock.getCenter(this.getCachedState());
            double d = player.getX() - ((double)this.getPos().getX() + vec3d.x);
            double e = player.getZ() - ((double)this.getPos().getZ() + vec3d.z);
            float f = abstractSignBlock.getRotationDegrees(this.getCachedState());
            return MathHelper.angleBetween(f, g = (float)(MathHelper.atan2(e, d) * 57.2957763671875) - 90.0f) <= 90.0f;
        }
        return false;
    }

    public SignText getTextFacing(PlayerEntity player) {
        return this.getText(this.isPlayerFacingFront(player));
    }

    public SignText getText(boolean front) {
        return front ? this.frontText : this.backText;
    }

    public SignText getFrontText() {
        return this.frontText;
    }

    public SignText getBackText() {
        return this.backText;
    }

    public int getTextLineHeight() {
        return 10;
    }

    public int getMaxTextWidth() {
        return 90;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        SignText.CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)this.frontText).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent(frontText -> nbt.put("front_text", (NbtElement)frontText));
        SignText.CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)this.backText).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent(backText -> nbt.put("back_text", (NbtElement)backText));
        nbt.putBoolean("is_waxed", this.waxed);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("front_text")) {
            SignText.CODEC.parse((DynamicOps)NbtOps.INSTANCE, (Object)nbt.getCompound("front_text")).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent(signText -> {
                this.frontText = this.parseLines((SignText)signText);
            });
        }
        if (nbt.contains("back_text")) {
            SignText.CODEC.parse((DynamicOps)NbtOps.INSTANCE, (Object)nbt.getCompound("back_text")).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent(signText -> {
                this.backText = this.parseLines((SignText)signText);
            });
        }
        this.waxed = nbt.getBoolean("is_waxed");
    }

    private SignText parseLines(SignText signText) {
        for (int i = 0; i < 4; ++i) {
            Text text = this.parseLine(signText.getMessage(i, false));
            Text text2 = this.parseLine(signText.getMessage(i, true));
            signText = signText.withMessage(i, text, text2);
        }
        return signText;
    }

    private Text parseLine(Text text) {
        World world = this.world;
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            try {
                return Texts.parse(SignBlockEntity.createCommandSource(null, serverWorld, this.pos), text, null, 0);
            }
            catch (CommandSyntaxException commandSyntaxException) {
                // empty catch block
            }
        }
        return text;
    }

    public void tryChangeText(PlayerEntity player, boolean front, List<FilteredMessage> messages) {
        if (this.isWaxed() || !player.getUuid().equals(this.getEditor()) || this.world == null) {
            LOGGER.warn("Player {} just tried to change non-editable sign", (Object)player.getName().getString());
            return;
        }
        this.changeText(text -> this.getTextWithMessages(player, messages, (SignText)text), front);
        this.setEditor(null);
        this.world.updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
    }

    public boolean changeText(UnaryOperator<SignText> textChanger, boolean front) {
        SignText signText = this.getText(front);
        return this.setText((SignText)textChanger.apply(signText), front);
    }

    private SignText getTextWithMessages(PlayerEntity player, List<FilteredMessage> messages, SignText text) {
        for (int i = 0; i < messages.size(); ++i) {
            FilteredMessage filteredMessage = messages.get(i);
            Style style = text.getMessage(i, player.shouldFilterText()).getStyle();
            text = player.shouldFilterText() ? text.withMessage(i, Text.literal(filteredMessage.getString()).setStyle(style)) : text.withMessage(i, Text.literal(filteredMessage.raw()).setStyle(style), Text.literal(filteredMessage.getString()).setStyle(style));
        }
        return text;
    }

    public boolean setText(SignText text, boolean front) {
        return front ? this.setFrontText(text) : this.setBackText(text);
    }

    private boolean setBackText(SignText backText) {
        if (backText != this.backText) {
            this.backText = backText;
            this.updateListeners();
            return true;
        }
        return false;
    }

    private boolean setFrontText(SignText frontText) {
        if (frontText != this.frontText) {
            this.frontText = frontText;
            this.updateListeners();
            return true;
        }
        return false;
    }

    public boolean canRunCommandClickEvent(boolean front, PlayerEntity player) {
        return this.isWaxed() && this.getText(front).hasRunCommandClickEvent(player);
    }

    public boolean runCommandClickEvent(PlayerEntity player, World world, BlockPos pos, boolean front) {
        boolean bl = false;
        for (Text text : this.getText(front).getMessages(player.shouldFilterText())) {
            Style style = text.getStyle();
            ClickEvent clickEvent = style.getClickEvent();
            if (clickEvent == null || clickEvent.getAction() != ClickEvent.Action.RUN_COMMAND) continue;
            player.getServer().getCommandManager().executeWithPrefix(SignBlockEntity.createCommandSource(player, world, pos), clickEvent.getValue());
            bl = true;
        }
        return bl;
    }

    private static ServerCommandSource createCommandSource(@Nullable PlayerEntity player, World world, BlockPos pos) {
        String string = player == null ? "Sign" : player.getName().getString();
        Text text = player == null ? Text.literal("Sign") : player.getDisplayName();
        return new ServerCommandSource(CommandOutput.DUMMY, Vec3d.ofCenter(pos), Vec2f.ZERO, (ServerWorld)world, 2, string, text, world.getServer(), player);
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    @Override
    public boolean copyItemDataRequiresOperator() {
        return true;
    }

    public void setEditor(@Nullable UUID editor) {
        this.editor = editor;
    }

    @Nullable
    public UUID getEditor() {
        return this.editor;
    }

    private void updateListeners() {
        this.markDirty();
        this.world.updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
    }

    public boolean isWaxed() {
        return this.waxed;
    }

    public boolean setWaxed(boolean waxed) {
        if (this.waxed != waxed) {
            this.waxed = waxed;
            this.updateListeners();
            return true;
        }
        return false;
    }

    public boolean isPlayerTooFarToEdit(UUID uuid) {
        PlayerEntity playerEntity = this.world.getPlayerByUuid(uuid);
        return playerEntity == null || playerEntity.squaredDistanceTo(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ()) > 64.0;
    }

    public static void tick(World world, BlockPos pos, BlockState state, SignBlockEntity blockEntity) {
        UUID uUID = blockEntity.getEditor();
        if (uUID != null) {
            blockEntity.tryClearInvalidEditor(blockEntity, world, uUID);
        }
    }

    private void tryClearInvalidEditor(SignBlockEntity blockEntity, World world, UUID uuid) {
        if (blockEntity.isPlayerTooFarToEdit(uuid)) {
            blockEntity.setEditor(null);
        }
    }

    public /* synthetic */ Packet toUpdatePacket() {
        return this.toUpdatePacket();
    }
}

