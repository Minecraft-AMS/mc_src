/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class UnlockRecipesS2CPacket
implements Packet<ClientPlayPacketListener> {
    private Action action;
    private List<Identifier> recipeIdsToChange;
    private List<Identifier> recipeIdsToInit;
    private boolean guiOpen;
    private boolean filteringCraftable;
    private boolean furnaceGuiOpen;
    private boolean furnaceFilteringCraftable;

    public UnlockRecipesS2CPacket() {
    }

    public UnlockRecipesS2CPacket(Action action, Collection<Identifier> recipeIdsToChange, Collection<Identifier> recipeIdsToInit, boolean guiOpen, boolean filteringCraftable, boolean furnaceGuiOpen, boolean furnaceFilteringCraftable) {
        this.action = action;
        this.recipeIdsToChange = ImmutableList.copyOf(recipeIdsToChange);
        this.recipeIdsToInit = ImmutableList.copyOf(recipeIdsToInit);
        this.guiOpen = guiOpen;
        this.filteringCraftable = filteringCraftable;
        this.furnaceGuiOpen = furnaceGuiOpen;
        this.furnaceFilteringCraftable = furnaceFilteringCraftable;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onUnlockRecipes(this);
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        int j;
        this.action = buf.readEnumConstant(Action.class);
        this.guiOpen = buf.readBoolean();
        this.filteringCraftable = buf.readBoolean();
        this.furnaceGuiOpen = buf.readBoolean();
        this.furnaceFilteringCraftable = buf.readBoolean();
        int i = buf.readVarInt();
        this.recipeIdsToChange = Lists.newArrayList();
        for (j = 0; j < i; ++j) {
            this.recipeIdsToChange.add(buf.readIdentifier());
        }
        if (this.action == Action.INIT) {
            i = buf.readVarInt();
            this.recipeIdsToInit = Lists.newArrayList();
            for (j = 0; j < i; ++j) {
                this.recipeIdsToInit.add(buf.readIdentifier());
            }
        }
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeEnumConstant(this.action);
        buf.writeBoolean(this.guiOpen);
        buf.writeBoolean(this.filteringCraftable);
        buf.writeBoolean(this.furnaceGuiOpen);
        buf.writeBoolean(this.furnaceFilteringCraftable);
        buf.writeVarInt(this.recipeIdsToChange.size());
        for (Identifier identifier : this.recipeIdsToChange) {
            buf.writeIdentifier(identifier);
        }
        if (this.action == Action.INIT) {
            buf.writeVarInt(this.recipeIdsToInit.size());
            for (Identifier identifier : this.recipeIdsToInit) {
                buf.writeIdentifier(identifier);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public List<Identifier> getRecipeIdsToChange() {
        return this.recipeIdsToChange;
    }

    @Environment(value=EnvType.CLIENT)
    public List<Identifier> getRecipeIdsToInit() {
        return this.recipeIdsToInit;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isGuiOpen() {
        return this.guiOpen;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isFilteringCraftable() {
        return this.filteringCraftable;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isFurnaceGuiOpen() {
        return this.furnaceGuiOpen;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isFurnaceFilteringCraftable() {
        return this.furnaceFilteringCraftable;
    }

    @Environment(value=EnvType.CLIENT)
    public Action getAction() {
        return this.action;
    }

    public static enum Action {
        INIT,
        ADD,
        REMOVE;

    }
}

