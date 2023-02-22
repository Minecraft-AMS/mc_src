/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.recipebook;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.recipe.book.ClientRecipeBook;
import net.minecraft.client.recipe.book.RecipeBookGroup;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.container.CraftingContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;

@Environment(value=EnvType.CLIENT)
public class RecipeGroupButtonWidget
extends ToggleButtonWidget {
    private final RecipeBookGroup category;
    private float bounce;

    public RecipeGroupButtonWidget(RecipeBookGroup category) {
        super(0, 0, 35, 27, false);
        this.category = category;
        this.setTextureUV(153, 2, 35, 0, RecipeBookWidget.TEXTURE);
    }

    public void checkForNewRecipes(MinecraftClient client) {
        ClientRecipeBook clientRecipeBook = client.player.getRecipeBook();
        List<RecipeResultCollection> list = clientRecipeBook.getResultsForGroup(this.category);
        if (!(client.player.container instanceof CraftingContainer)) {
            return;
        }
        for (RecipeResultCollection recipeResultCollection : list) {
            for (Recipe<?> recipe : recipeResultCollection.getResults(clientRecipeBook.isFilteringCraftable((CraftingContainer)client.player.container))) {
                if (!clientRecipeBook.shouldDisplay(recipe)) continue;
                this.bounce = 15.0f;
                return;
            }
        }
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float delta) {
        if (this.bounce > 0.0f) {
            float f = 1.0f + 0.1f * (float)Math.sin(this.bounce / 15.0f * (float)Math.PI);
            RenderSystem.pushMatrix();
            RenderSystem.translatef(this.x + 8, this.y + 12, 0.0f);
            RenderSystem.scalef(1.0f, f, 1.0f);
            RenderSystem.translatef(-(this.x + 8), -(this.y + 12), 0.0f);
        }
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        minecraftClient.getTextureManager().bindTexture(this.texture);
        RenderSystem.disableDepthTest();
        int i = this.u;
        int j = this.v;
        if (this.toggled) {
            i += this.pressedUOffset;
        }
        if (this.isHovered()) {
            j += this.hoverVOffset;
        }
        int k = this.x;
        if (this.toggled) {
            k -= 2;
        }
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.blit(k, this.y, i, j, this.width, this.height);
        RenderSystem.enableDepthTest();
        this.renderIcons(minecraftClient.getItemRenderer());
        if (this.bounce > 0.0f) {
            RenderSystem.popMatrix();
            this.bounce -= delta;
        }
    }

    private void renderIcons(ItemRenderer itemRenderer) {
        int i;
        List<ItemStack> list = this.category.getIcons();
        int n = i = this.toggled ? -2 : 0;
        if (list.size() == 1) {
            itemRenderer.renderGuiItem(list.get(0), this.x + 9 + i, this.y + 5);
        } else if (list.size() == 2) {
            itemRenderer.renderGuiItem(list.get(0), this.x + 3 + i, this.y + 5);
            itemRenderer.renderGuiItem(list.get(1), this.x + 14 + i, this.y + 5);
        }
    }

    public RecipeBookGroup getCategory() {
        return this.category;
    }

    public boolean hasKnownRecipes(ClientRecipeBook recipeBook) {
        List<RecipeResultCollection> list = recipeBook.getResultsForGroup(this.category);
        this.visible = false;
        if (list != null) {
            for (RecipeResultCollection recipeResultCollection : list) {
                if (!recipeResultCollection.isInitialized() || !recipeResultCollection.hasFittingRecipes()) continue;
                this.visible = true;
                break;
            }
        }
        return this.visible;
    }
}

