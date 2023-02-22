/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.network.packet.c2s.play;

import net.minecraft.advancement.Advancement;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class AdvancementTabC2SPacket
implements Packet<ServerPlayPacketListener> {
    private final Action action;
    @Nullable
    private final Identifier tabToOpen;

    public AdvancementTabC2SPacket(Action action, @Nullable Identifier tab) {
        this.action = action;
        this.tabToOpen = tab;
    }

    public static AdvancementTabC2SPacket open(Advancement advancement) {
        return new AdvancementTabC2SPacket(Action.OPENED_TAB, advancement.getId());
    }

    public static AdvancementTabC2SPacket close() {
        return new AdvancementTabC2SPacket(Action.CLOSED_SCREEN, null);
    }

    public AdvancementTabC2SPacket(PacketByteBuf buf) {
        this.action = buf.readEnumConstant(Action.class);
        this.tabToOpen = this.action == Action.OPENED_TAB ? buf.readIdentifier() : null;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeEnumConstant(this.action);
        if (this.action == Action.OPENED_TAB) {
            buf.writeIdentifier(this.tabToOpen);
        }
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onAdvancementTab(this);
    }

    public Action getAction() {
        return this.action;
    }

    @Nullable
    public Identifier getTabToOpen() {
        return this.tabToOpen;
    }

    public static final class Action
    extends Enum<Action> {
        public static final /* enum */ Action OPENED_TAB = new Action();
        public static final /* enum */ Action CLOSED_SCREEN = new Action();
        private static final /* synthetic */ Action[] field_13022;

        public static Action[] values() {
            return (Action[])field_13022.clone();
        }

        public static Action valueOf(String string) {
            return Enum.valueOf(Action.class, string);
        }

        private static /* synthetic */ Action[] method_36962() {
            return new Action[]{OPENED_TAB, CLOSED_SCREEN};
        }

        static {
            field_13022 = Action.method_36962();
        }
    }
}

