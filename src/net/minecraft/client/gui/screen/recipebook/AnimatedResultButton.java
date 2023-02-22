/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class AnimatedResultButton
extends ClickableWidget {
    private static final Identifier BACKGROUND_TEXTURE = new Identifier("textures/gui/recipe_book.png");
    private static final Text MORE_RECIPES_TEXT = new TranslatableText("gui.recipebook.moreRecipes");
    private AbstractRecipeScreenHandler<?> craftingScreenHandler;
    private RecipeBook recipeBook;
    private RecipeResultCollection results;
    private float time;
    private float bounce;
    private int currentResultIndex;

    public AnimatedResultButton() {
        super(0, 0, 25, 25, LiteralText.EMPTY);
    }

    public void showResultCollection(RecipeResultCollection recipeResultCollection, RecipeBookResults recipeBookResults) {
        this.results = recipeResultCollection;
        this.craftingScreenHandler = (AbstractRecipeScreenHandler)recipeBookResults.getMinecraftClient().player.currentScreenHandler;
        this.recipeBook = recipeBookResults.getRecipeBook();
        List<Recipe<?>> list = recipeResultCollection.getResults(this.recipeBook.isFilteringCraftable(this.craftingScreenHandler));
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
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        boolean bl;
        if (!Screen.hasControlDown()) {
            this.time += delta;
        }
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        minecraftClient.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        int i = 29;
        if (!this.results.hasCraftableRecipes()) {
            i += 25;
        }
        int j = 206;
        if (this.results.getResults(this.recipeBook.isFilteringCraftable(this.craftingScreenHandler)).size() > 1) {
            j += 25;
        }
        boolean bl2 = bl = this.bounce > 0.0f;
        if (bl) {
            float f = 1.0f + 0.1f * (float)Math.sin(this.bounce / 15.0f * (float)Math.PI);
            RenderSystem.pushMatrix();
            RenderSystem.translatef(this.x + 8, this.y + 12, 0.0f);
            RenderSystem.scalef(f, f, 1.0f);
            RenderSystem.translatef(-(this.x + 8), -(this.y + 12), 0.0f);
            this.bounce -= delta;
        }
        this.drawTexture(matrices, this.x, this.y, i, j, this.width, this.height);
        List<Recipe<?>> list = this.getResults();
        this.currentResultIndex = MathHelper.floor(this.time / 30.0f) % list.size();
        ItemStack itemStack = list.get(this.currentResultIndex).getOutput();
        int k = 4;
        if (this.results.hasSingleOutput() && this.getResults().size() > 1) {
            minecraftClient.getItemRenderer().renderInGuiWithOverrides(itemStack, this.x + k + 1, this.y + k + 1);
            --k;
        }
        minecraftClient.getItemRenderer().renderInGui(itemStack, this.x + k, this.y + k);
        if (bl) {
            RenderSystem.popMatrix();
        }
    }

    private List<Recipe<?>> getResults() {
        List<Recipe<?>> list = this.results.getRecipes(true);
        if (!this.recipeBook.isFilteringCraftable(this.craftingScreenHandler)) {
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

    public List<Text> getTooltip(Screen screen) {
        ItemStack itemStack = this.getResults().get(this.currentResultIndex).getOutput();
        ArrayList list = Lists.newArrayList(screen.getTooltipFromItem(itemStack));
        if (this.results.getResults(this.recipeBook.isFilteringCraftable(this.craftingScreenHandler)).size() > 1) {
            list.add(MORE_RECIPES_TEXT);
        }
        return list;
    }

    @Override
    public int getWidth() {
        return 25;
    }

    @Override
    protected boolean isValidClickButton(int button) {
        return button == 0 || button == 1;
    }
}

