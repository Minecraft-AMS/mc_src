/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class RecipeBookDataC2SPacket
implements Packet<ServerPlayPacketListener> {
    private Mode mode;
    private Identifier recipeId;
    private boolean guiOpen;
    private boolean filteringCraftable;
    private boolean furnaceGuiOpen;
    private boolean furnaceFilteringCraftable;
    private boolean blastFurnaceGuiOpen;
    private boolean blastFurnaceFilteringCraftable;
    private boolean smokerGuiOpen;
    private boolean smokerGuiFilteringCraftable;

    public RecipeBookDataC2SPacket() {
    }

    public RecipeBookDataC2SPacket(Recipe<?> recipe) {
        this.mode = Mode.SHOWN;
        this.recipeId = recipe.getId();
    }

    @Environment(value=EnvType.CLIENT)
    public RecipeBookDataC2SPacket(boolean bl, boolean bl2, boolean bl3, boolean bl4, boolean bl5, boolean bl6) {
        this.mode = Mode.SETTINGS;
        this.guiOpen = bl;
        this.filteringCraftable = bl2;
        this.furnaceGuiOpen = bl3;
        this.furnaceFilteringCraftable = bl4;
        this.blastFurnaceGuiOpen = bl5;
        this.blastFurnaceFilteringCraftable = bl6;
        this.smokerGuiOpen = bl5;
        this.smokerGuiFilteringCraftable = bl6;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.mode = buf.readEnumConstant(Mode.class);
        if (this.mode == Mode.SHOWN) {
            this.recipeId = buf.readIdentifier();
        } else if (this.mode == Mode.SETTINGS) {
            this.guiOpen = buf.readBoolean();
            this.filteringCraftable = buf.readBoolean();
            this.furnaceGuiOpen = buf.readBoolean();
            this.furnaceFilteringCraftable = buf.readBoolean();
            this.blastFurnaceGuiOpen = buf.readBoolean();
            this.blastFurnaceFilteringCraftable = buf.readBoolean();
            this.smokerGuiOpen = buf.readBoolean();
            this.smokerGuiFilteringCraftable = buf.readBoolean();
        }
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeEnumConstant(this.mode);
        if (this.mode == Mode.SHOWN) {
            buf.writeIdentifier(this.recipeId);
        } else if (this.mode == Mode.SETTINGS) {
            buf.writeBoolean(this.guiOpen);
            buf.writeBoolean(this.filteringCraftable);
            buf.writeBoolean(this.furnaceGuiOpen);
            buf.writeBoolean(this.furnaceFilteringCraftable);
            buf.writeBoolean(this.blastFurnaceGuiOpen);
            buf.writeBoolean(this.blastFurnaceFilteringCraftable);
            buf.writeBoolean(this.smokerGuiOpen);
            buf.writeBoolean(this.smokerGuiFilteringCraftable);
        }
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onRecipeBookData(this);
    }

    public Mode getMode() {
        return this.mode;
    }

    public Identifier getRecipeId() {
        return this.recipeId;
    }

    public boolean isGuiOpen() {
        return this.guiOpen;
    }

    public boolean isFilteringCraftable() {
        return this.filteringCraftable;
    }

    public boolean isFurnaceGuiOpen() {
        return this.furnaceGuiOpen;
    }

    public boolean isFurnaceFilteringCraftable() {
        return this.furnaceFilteringCraftable;
    }

    public boolean isBlastFurnaceGuiOpen() {
        return this.blastFurnaceGuiOpen;
    }

    public boolean isBlastFurnaceFilteringCraftable() {
        return this.blastFurnaceFilteringCraftable;
    }

    public boolean isSmokerGuiOpen() {
        return this.smokerGuiOpen;
    }

    public boolean isSmokerGuiFilteringCraftable() {
        return this.smokerGuiFilteringCraftable;
    }

    public static enum Mode {
        SHOWN,
        SETTINGS;

    }
}

