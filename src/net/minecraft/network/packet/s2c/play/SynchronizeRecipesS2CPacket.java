/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class SynchronizeRecipesS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final List<Recipe<?>> recipes;

    public SynchronizeRecipesS2CPacket(Collection<Recipe<?>> recipes) {
        this.recipes = Lists.newArrayList(recipes);
    }

    public SynchronizeRecipesS2CPacket(PacketByteBuf buf) {
        this.recipes = buf.readList(SynchronizeRecipesS2CPacket::readRecipe);
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeCollection(this.recipes, SynchronizeRecipesS2CPacket::writeRecipe);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onSynchronizeRecipes(this);
    }

    public List<Recipe<?>> getRecipes() {
        return this.recipes;
    }

    public static Recipe<?> readRecipe(PacketByteBuf buf) {
        Identifier identifier = buf.readIdentifier();
        Identifier identifier2 = buf.readIdentifier();
        return Registries.RECIPE_SERIALIZER.getOrEmpty(identifier).orElseThrow(() -> new IllegalArgumentException("Unknown recipe serializer " + identifier)).read(identifier2, buf);
    }

    public static <T extends Recipe<?>> void writeRecipe(PacketByteBuf buf, T recipe) {
        buf.writeIdentifier(Registries.RECIPE_SERIALIZER.getId(recipe.getSerializer()));
        buf.writeIdentifier(recipe.getId());
        recipe.getSerializer().write(buf, recipe);
    }
}

