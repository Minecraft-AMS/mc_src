/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen.recipebook;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.AnimatedResultButton;
import net.minecraft.client.gui.screen.recipebook.RecipeAlternativesWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeDisplayListener;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.RecipeBook;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RecipeBookResults {
    private final List<AnimatedResultButton> resultButtons = Lists.newArrayListWithCapacity((int)20);
    private AnimatedResultButton hoveredResultButton;
    private final RecipeAlternativesWidget alternatesWidget = new RecipeAlternativesWidget();
    private MinecraftClient client;
    private final List<RecipeDisplayListener> recipeDisplayListeners = Lists.newArrayList();
    private List<RecipeResultCollection> resultCollections;
    private ToggleButtonWidget nextPageButton;
    private ToggleButtonWidget prevPageButton;
    private int pageCount;
    private int currentPage;
    private RecipeBook recipeBook;
    private Recipe<?> lastClickedRecipe;
    private RecipeResultCollection resultCollection;

    public RecipeBookResults() {
        for (int i = 0; i < 20; ++i) {
            this.resultButtons.add(new AnimatedResultButton());
        }
    }

    public void initialize(MinecraftClient minecraftClient, int parentLeft, int parentTop) {
        this.client = minecraftClient;
        this.recipeBook = minecraftClient.player.getRecipeBook();
        for (int i = 0; i < this.resultButtons.size(); ++i) {
            this.resultButtons.get(i).setPos(parentLeft + 11 + 25 * (i % 5), parentTop + 31 + 25 * (i / 5));
        }
        this.nextPageButton = new ToggleButtonWidget(parentLeft + 93, parentTop + 137, 12, 17, false);
        this.nextPageButton.setTextureUV(1, 208, 13, 18, RecipeBookWidget.TEXTURE);
        this.prevPageButton = new ToggleButtonWidget(parentLeft + 38, parentTop + 137, 12, 17, true);
        this.prevPageButton.setTextureUV(1, 208, 13, 18, RecipeBookWidget.TEXTURE);
    }

    public void setGui(RecipeBookWidget recipeBookWidget) {
        this.recipeDisplayListeners.remove(recipeBookWidget);
        this.recipeDisplayListeners.add(recipeBookWidget);
    }

    public void setResults(List<RecipeResultCollection> list, boolean resetCurrentPage) {
        this.resultCollections = list;
        this.pageCount = (int)Math.ceil((double)list.size() / 20.0);
        if (this.pageCount <= this.currentPage || resetCurrentPage) {
            this.currentPage = 0;
        }
        this.refreshResultButtons();
    }

    private void refreshResultButtons() {
        int i = 20 * this.currentPage;
        for (int j = 0; j < this.resultButtons.size(); ++j) {
            AnimatedResultButton animatedResultButton = this.resultButtons.get(j);
            if (i + j < this.resultCollections.size()) {
                RecipeResultCollection recipeResultCollection = this.resultCollections.get(i + j);
                animatedResultButton.showResultCollection(recipeResultCollection, this);
                animatedResultButton.visible = true;
                continue;
            }
            animatedResultButton.visible = false;
        }
        this.hideShowPageButtons();
    }

    private void hideShowPageButtons() {
        this.nextPageButton.visible = this.pageCount > 1 && this.currentPage < this.pageCount - 1;
        this.prevPageButton.visible = this.pageCount > 1 && this.currentPage > 0;
    }

    public void draw(int left, int top, int mouseX, int mouseY, float delta) {
        if (this.pageCount > 1) {
            String string = this.currentPage + 1 + "/" + this.pageCount;
            int i = this.client.textRenderer.getStringWidth(string);
            this.client.textRenderer.draw(string, left - i / 2 + 73, top + 141, -1);
        }
        DiffuseLighting.disable();
        this.hoveredResultButton = null;
        for (AnimatedResultButton animatedResultButton : this.resultButtons) {
            animatedResultButton.render(mouseX, mouseY, delta);
            if (!animatedResultButton.visible || !animatedResultButton.isHovered()) continue;
            this.hoveredResultButton = animatedResultButton;
        }
        this.prevPageButton.render(mouseX, mouseY, delta);
        this.nextPageButton.render(mouseX, mouseY, delta);
        this.alternatesWidget.render(mouseX, mouseY, delta);
    }

    public void drawTooltip(int i, int j) {
        if (this.client.currentScreen != null && this.hoveredResultButton != null && !this.alternatesWidget.isVisible()) {
            this.client.currentScreen.renderTooltip(this.hoveredResultButton.method_2644(this.client.currentScreen), i, j);
        }
    }

    @Nullable
    public Recipe<?> getLastClickedRecipe() {
        return this.lastClickedRecipe;
    }

    @Nullable
    public RecipeResultCollection getLastClickedResults() {
        return this.resultCollection;
    }

    public void hideAlternates() {
        this.alternatesWidget.setVisible(false);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button, int areaLeft, int areaTop, int areaWidth, int areaHeight) {
        this.lastClickedRecipe = null;
        this.resultCollection = null;
        if (this.alternatesWidget.isVisible()) {
            if (this.alternatesWidget.mouseClicked(mouseX, mouseY, button)) {
                this.lastClickedRecipe = this.alternatesWidget.getLastClickedRecipe();
                this.resultCollection = this.alternatesWidget.getResults();
            } else {
                this.alternatesWidget.setVisible(false);
            }
            return true;
        }
        if (this.nextPageButton.mouseClicked(mouseX, mouseY, button)) {
            ++this.currentPage;
            this.refreshResultButtons();
            return true;
        }
        if (this.prevPageButton.mouseClicked(mouseX, mouseY, button)) {
            --this.currentPage;
            this.refreshResultButtons();
            return true;
        }
        for (AnimatedResultButton animatedResultButton : this.resultButtons) {
            if (!animatedResultButton.mouseClicked(mouseX, mouseY, button)) continue;
            if (button == 0) {
                this.lastClickedRecipe = animatedResultButton.currentRecipe();
                this.resultCollection = animatedResultButton.getResultCollection();
            } else if (button == 1 && !this.alternatesWidget.isVisible() && !animatedResultButton.hasResults()) {
                this.alternatesWidget.showAlternativesForResult(this.client, animatedResultButton.getResultCollection(), animatedResultButton.x, animatedResultButton.y, areaLeft + areaWidth / 2, areaTop + 13 + areaHeight / 2, animatedResultButton.getWidth());
            }
            return true;
        }
        return false;
    }

    public void onRecipesDisplayed(List<Recipe<?>> list) {
        for (RecipeDisplayListener recipeDisplayListener : this.recipeDisplayListeners) {
            recipeDisplayListener.onRecipesDisplayed(list);
        }
    }

    public MinecraftClient getMinecraftClient() {
        return this.client;
    }

    public RecipeBook getRecipeBook() {
        return this.recipeBook;
    }
}
