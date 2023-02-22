/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class PlayerActionC2SPacket
implements Packet<ServerPlayPacketListener> {
    private final BlockPos pos;
    private final Direction direction;
    private final Action action;

    public PlayerActionC2SPacket(Action action, BlockPos pos, Direction direction) {
        this.action = action;
        this.pos = pos.toImmutable();
        this.direction = direction;
    }

    public PlayerActionC2SPacket(PacketByteBuf buf) {
        this.action = buf.readEnumConstant(Action.class);
        this.pos = buf.readBlockPos();
        this.direction = Direction.byId(buf.readUnsignedByte());
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeEnumConstant(this.action);
        buf.writeBlockPos(this.pos);
        buf.writeByte(this.direction.getId());
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onPlayerAction(this);
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public Action getAction() {
        return this.action;
    }

    public static final class Action
    extends Enum<Action> {
        public static final /* enum */ Action START_DESTROY_BLOCK = new Action();
        public static final /* enum */ Action ABORT_DESTROY_BLOCK = new Action();
        public static final /* enum */ Action STOP_DESTROY_BLOCK = new Action();
        public static final /* enum */ Action DROP_ALL_ITEMS = new Action();
        public static final /* enum */ Action DROP_ITEM = new Action();
        public static final /* enum */ Action RELEASE_USE_ITEM = new Action();
        public static final /* enum */ Action SWAP_ITEM_WITH_OFFHAND = new Action();
        private static final /* synthetic */ Action[] field_12972;

        public static Action[] values() {
            return (Action[])field_12972.clone();
        }

        public static Action valueOf(String string) {
            return Enum.valueOf(Action.class, string);
        }

        private static /* synthetic */ Action[] method_36957() {
            return new Action[]{START_DESTROY_BLOCK, ABORT_DESTROY_BLOCK, STOP_DESTROY_BLOCK, DROP_ALL_ITEMS, DROP_ITEM, RELEASE_USE_ITEM, SWAP_ITEM_WITH_OFFHAND};
        }

        static {
            field_12972 = Action.method_36957();
        }
    }
}

