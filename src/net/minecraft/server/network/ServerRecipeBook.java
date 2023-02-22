/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.network;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.packet.s2c.play.UnlockRecipesS2CPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerRecipeBook
extends RecipeBook {
    private static final Logger LOGGER = LogManager.getLogger();
    private final RecipeManager manager;

    public ServerRecipeBook(RecipeManager recipeManager) {
        this.manager = recipeManager;
    }

    public int unlockRecipes(Collection<Recipe<?>> recipes, ServerPlayerEntity player) {
        ArrayList list = Lists.newArrayList();
        int i = 0;
        for (Recipe<?> recipe : recipes) {
            Identifier identifier = recipe.getId();
            if (this.recipes.contains(identifier) || recipe.isIgnoredInRecipeBook()) continue;
            this.add(identifier);
            this.display(identifier);
            list.add(identifier);
            Criterions.RECIPE_UNLOCKED.trigger(player, recipe);
            ++i;
        }
        this.sendUnlockRecipesPacket(UnlockRecipesS2CPacket.Action.ADD, player, list);
        return i;
    }

    public int lockRecipes(Collection<Recipe<?>> recipes, ServerPlayerEntity player) {
        ArrayList list = Lists.newArrayList();
        int i = 0;
        for (Recipe<?> recipe : recipes) {
            Identifier identifier = recipe.getId();
            if (!this.recipes.contains(identifier)) continue;
            this.remove(identifier);
            list.add(identifier);
            ++i;
        }
        this.sendUnlockRecipesPacket(UnlockRecipesS2CPacket.Action.REMOVE, player, list);
        return i;
    }

    private void sendUnlockRecipesPacket(UnlockRecipesS2CPacket.Action action, ServerPlayerEntity player, List<Identifier> recipeIds) {
        player.networkHandler.sendPacket(new UnlockRecipesS2CPacket(action, recipeIds, Collections.emptyList(), this.guiOpen, this.filteringCraftable, this.furnaceGuiOpen, this.furnaceFilteringCraftable));
    }

    public CompoundTag toTag() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putBoolean("isGuiOpen", this.guiOpen);
        compoundTag.putBoolean("isFilteringCraftable", this.filteringCraftable);
        compoundTag.putBoolean("isFurnaceGuiOpen", this.furnaceGuiOpen);
        compoundTag.putBoolean("isFurnaceFilteringCraftable", this.furnaceFilteringCraftable);
        ListTag listTag = new ListTag();
        for (Identifier identifier : this.recipes) {
            listTag.add(new StringTag(identifier.toString()));
        }
        compoundTag.put("recipes", listTag);
        ListTag listTag2 = new ListTag();
        for (Identifier identifier2 : this.toBeDisplayed) {
            listTag2.add(new StringTag(identifier2.toString()));
        }
        compoundTag.put("toBeDisplayed", listTag2);
        return compoundTag;
    }

    public void fromTag(CompoundTag tag) {
        this.guiOpen = tag.getBoolean("isGuiOpen");
        this.filteringCraftable = tag.getBoolean("isFilteringCraftable");
        this.furnaceGuiOpen = tag.getBoolean("isFurnaceGuiOpen");
        this.furnaceFilteringCraftable = tag.getBoolean("isFurnaceFilteringCraftable");
        ListTag listTag = tag.getList("recipes", 8);
        this.method_20732(listTag, this::add);
        ListTag listTag2 = tag.getList("toBeDisplayed", 8);
        this.method_20732(listTag2, this::display);
    }

    private void method_20732(ListTag listTag, Consumer<Recipe<?>> consumer) {
        for (int i = 0; i < listTag.size(); ++i) {
            String string = listTag.getString(i);
            try {
                Identifier identifier = new Identifier(string);
                Optional<Recipe<?>> optional = this.manager.get(identifier);
                if (!optional.isPresent()) {
                    LOGGER.error("Tried to load unrecognized recipe: {} removed now.", (Object)identifier);
                    continue;
                }
                consumer.accept(optional.get());
                continue;
            }
            catch (InvalidIdentifierException invalidIdentifierException) {
                LOGGER.error("Tried to load improperly formatted recipe: {} removed now.", (Object)string);
            }
        }
    }

    public void sendInitRecipesPacket(ServerPlayerEntity player) {
        player.networkHandler.sendPacket(new UnlockRecipesS2CPacket(UnlockRecipesS2CPacket.Action.INIT, this.recipes, this.toBeDisplayed, this.guiOpen, this.filteringCraftable, this.furnaceGuiOpen, this.furnaceFilteringCraftable));
    }
}
