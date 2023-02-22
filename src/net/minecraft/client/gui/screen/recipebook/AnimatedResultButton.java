/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.recipebook;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.container.CraftingContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class AnimatedResultButton
extends AbstractButtonWidget {
    private static final Identifier BG_TEX = new Identifier("textures/gui/recipe_book.png");
    private CraftingContainer<?> craftingContainer;
    private RecipeBook recipeBook;
    private RecipeResultCollection results;
    private float time;
    private float bounce;
    private int currentResultIndex;

    public AnimatedResultButton() {
        super(0, 0, 25, 25, "");
    }

    public void showResultCollection(RecipeResultCollection recipeResultCollection, RecipeBookResults recipeBookResults) {
        this.results = recipeResultCollection;
        this.craftingContainer = (CraftingContainer)recipeBookResults.getMinecraftClient().player.container;
        this.recipeBook = recipeBookResults.getRecipeBook();
        List<Recipe<?>> list = recipeResultCollection.getResults(this.recipeBook.isFilteringCraftable(this.craftingContainer));
        for (Recipe<?> recipe : list) {
            if (!this.recipeBook.shouldDisplay(recipe)) continue;
            recipeBookResults.onRecipesDisplayed(list);
            this.bounce = 15.0f;
            break;
        }
    }

    public RecipeResultCollection getResultCollection() {
        return this.results;
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float delta) {
        boolean bl;
        if (!Screen.hasControlDown()) {
            this.time += delta;
        }
        DiffuseLighting.enableForItems();
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        minecraftClient.getTextureManager().bindTexture(BG_TEX);
        GlStateManager.disableLighting();
        int i = 29;
        if (!this.results.hasCraftableRecipes()) {
            i += 25;
        }
        int j = 206;
        if (this.results.getResults(this.recipeBook.isFilteringCraftable(this.craftingContainer)).size() > 1) {
            j += 25;
        }
        boolean bl2 = bl = this.bounce > 0.0f;
        if (bl) {
            float f = 1.0f + 0.1f * (float)Math.sin(this.bounce / 15.0f * (float)Math.PI);
            GlStateManager.pushMatrix();
            GlStateManager.translatef(this.x + 8, this.y + 12, 0.0f);
            GlStateManager.scalef(f, f, 1.0f);
            GlStateManager.translatef(-(this.x + 8), -(this.y + 12), 0.0f);
            this.bounce -= delta;
        }
        this.blit(this.x, this.y, i, j, this.width, this.height);
        List<Recipe<?>> list = this.getResults();
        this.currentResultIndex = MathHelper.floor(this.time / 30.0f) % list.size();
        ItemStack itemStack = list.get(this.currentResultIndex).getOutput();
        int k = 4;
        if (this.results.method_2656() && this.getResults().size() > 1) {
            minecraftClient.getItemRenderer().renderGuiItem(itemStack, this.x + k + 1, this.y + k + 1);
            --k;
        }
        minecraftClient.getItemRenderer().renderGuiItem(itemStack, this.x + k, this.y + k);
        if (bl) {
            GlStateManager.popMatrix();
        }
        GlStateManager.enableLighting();
        DiffuseLighting.disable();
    }

    private List<Recipe<?>> getResults() {
        List<Recipe<?>> list = this.results.getRecipes(true);
        if (!this.recipeBook.isFilteringCraftable(this.craftingContainer)) {
            list.addAll(this.results.getRecipes(false));
        }
        return list;
    }

    public boolean hasResults() {
        return this.getResults().size() == 1;
    }

    public Recipe<?> currentRecipe() {
        List<Recipe<?>> list = this.getResults();
        return list.get(this.currentResultIndex);
    }

    public List<String> method_2644(Screen screen) {
        ItemStack itemStack = this.getResults().get(this.currentResultIndex).getOutput();
        List<String> list = screen.getTooltipFromItem(itemStack);
        if (this.results.getResults(this.recipeBook.isFilteringCraftable(this.craftingContainer)).size() > 1) {
            list.add(I18n.translate("gui.recipebook.moreRecipes", new Object[0]));
        }
        return list;
    }

    @Override
    public int getWidth() {
        return 25;
    }

    @Override
    protected boolean isValidClickButton(int i) {
        return i == 0 || i == 1;
    }
}

