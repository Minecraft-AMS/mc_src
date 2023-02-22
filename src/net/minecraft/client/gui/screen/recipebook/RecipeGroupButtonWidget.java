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
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.AbstractRecipeScreenHandler;

@Environment(value=EnvType.CLIENT)
public class RecipeGroupButtonWidget
extends ToggleButtonWidget {
    private final RecipeBookGroup category;
    private static final float field_32412 = 15.0f;
    private float bounce;

    public RecipeGroupButtonWidget(RecipeBookGroup category) {
        super(0, 0, 35, 27, false);
        this.category = category;
        this.setTextureUV(153, 2, 35, 0, RecipeBookWidget.TEXTURE);
    }

    public void checkForNewRecipes(MinecraftClient client) {
        ClientRecipeBook clientRecipeBook = client.player.getRecipeBook();
        List<RecipeResultCollection> list = clientRecipeBook.getResultsForGroup(this.category);
        if (!(client.player.currentScreenHandler instanceof AbstractRecipeScreenHandler)) {
            return;
        }
        for (RecipeResultCollection recipeResultCollection : list) {
            for (Recipe<?> recipe : recipeResultCollection.getResults(clientRecipeBook.isFilteringCraftable((AbstractRecipeScreenHandler)client.player.currentScreenHandler))) {
                if (!clientRecipeBook.shouldDisplay(recipe)) continue;
                this.bounce = 15.0f;
                return;
            }
        }
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.bounce > 0.0f) {
            float f = 1.0f + 0.1f * (float)Math.sin(this.bounce / 15.0f * (float)Math.PI);
            matrices.push();
            matrices.translate(this.getX() + 8, this.getY() + 12, 0.0f);
            matrices.scale(1.0f, f, 1.0f);
            matrices.translate(-(this.getX() + 8), -(this.getY() + 12), 0.0f);
        }
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, this.texture);
        RenderSystem.disableDepthTest();
        int i = this.u;
        int j = this.v;
        if (this.toggled) {
            i += this.pressedUOffset;
        }
        if (this.isHovered()) {
            j += this.hoverVOffset;
        }
        int k = this.getX();
        if (this.toggled) {
            k -= 2;
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        this.drawTexture(matrices, k, this.getY(), i, j, this.width, this.height);
        RenderSystem.enableDepthTest();
        this.renderIcons(minecraftClient.getItemRenderer());
        if (this.bounce > 0.0f) {
            matrices.pop();
            this.bounce -= delta;
        }
    }

    private void renderIcons(ItemRenderer itemRenderer) {
        int i;
        List<ItemStack> list = this.category.getIcons();
        int n = i = this.toggled ? -2 : 0;
        if (list.size() == 1) {
            itemRenderer.renderInGui(list.get(0), this.getX() + 9 + i, this.getY() + 5);
        } else if (list.size() == 2) {
            itemRenderer.renderInGui(list.get(0), this.getX() + 3 + i, this.getY() + 5);
            itemRenderer.renderInGui(list.get(1), this.getX() + 14 + i, this.getY() + 5);
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

