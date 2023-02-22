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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.container.AbstractFurnaceContainer;
import net.minecraft.container.CraftingContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeGridAligner;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class RecipeAlternativesWidget
extends DrawableHelper
implements Drawable,
Element {
    private static final Identifier BG_TEX = new Identifier("textures/gui/recipe_book.png");
    private final List<AlternativeButtonWidget> alternativeButtons = Lists.newArrayList();
    private boolean visible;
    private int buttonX;
    private int buttonY;
    private MinecraftClient client;
    private RecipeResultCollection resultCollection;
    private Recipe<?> lastClickedRecipe;
    private float time;
    private boolean furnace;

    public void showAlternativesForResult(MinecraftClient client, RecipeResultCollection results, int buttonX, int buttonY, int areaCenterX, int areaCenterY, float delta) {
        float p;
        float o;
        float n;
        float h;
        float g;
        this.client = client;
        this.resultCollection = results;
        if (client.player.container instanceof AbstractFurnaceContainer) {
            this.furnace = true;
        }
        boolean bl = client.player.getRecipeBook().isFilteringCraftable((CraftingContainer)client.player.container);
        List<Recipe<?>> list = results.getRecipes(true);
        List list2 = bl ? Collections.emptyList() : results.getRecipes(false);
        int i = list.size();
        int j = i + list2.size();
        int k = j <= 16 ? 4 : 5;
        int l = (int)Math.ceil((float)j / (float)k);
        this.buttonX = buttonX;
        this.buttonY = buttonY;
        int m = 25;
        float f = this.buttonX + Math.min(j, k) * 25;
        if (f > (g = (float)(areaCenterX + 50))) {
            this.buttonX = (int)((float)this.buttonX - delta * (float)((int)((f - g) / delta)));
        }
        if ((h = (float)(this.buttonY + l * 25)) > (n = (float)(areaCenterY + 50))) {
            this.buttonY = (int)((float)this.buttonY - delta * (float)MathHelper.ceil((h - n) / delta));
        }
        if ((o = (float)this.buttonY) < (p = (float)(areaCenterY - 100))) {
            this.buttonY = (int)((float)this.buttonY - delta * (float)MathHelper.ceil((o - p) / delta));
        }
        this.visible = true;
        this.alternativeButtons.clear();
        for (int q = 0; q < j; ++q) {
            boolean bl2 = q < i;
            Recipe recipe = bl2 ? list.get(q) : (Recipe)list2.get(q - i);
            int r = this.buttonX + 4 + 25 * (q % k);
            int s = this.buttonY + 5 + 25 * (q / k);
            if (this.furnace) {
                this.alternativeButtons.add(new FurnaceAlternativeButtonWidget(r, s, recipe, bl2));
                continue;
            }
            this.alternativeButtons.add(new AlternativeButtonWidget(r, s, recipe, bl2));
        }
        this.lastClickedRecipe = null;
    }

    @Override
    public boolean changeFocus(boolean bl) {
        return false;
    }

    public RecipeResultCollection getResults() {
        return this.resultCollection;
    }

    public Recipe<?> getLastClickedRecipe() {
        return this.lastClickedRecipe;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        for (AlternativeButtonWidget alternativeButtonWidget : this.alternativeButtons) {
            if (!alternativeButtonWidget.mouseClicked(mouseX, mouseY, button)) continue;
            this.lastClickedRecipe = alternativeButtonWidget.recipe;
            return true;
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return false;
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        if (!this.visible) {
            return;
        }
        this.time += delta;
        RenderSystem.enableBlend();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(BG_TEX);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(0.0f, 0.0f, 170.0f);
        int i = this.alternativeButtons.size() <= 16 ? 4 : 5;
        int j = Math.min(this.alternativeButtons.size(), i);
        int k = MathHelper.ceil((float)this.alternativeButtons.size() / (float)i);
        int l = 24;
        int m = 4;
        int n = 82;
        int o = 208;
        this.renderGrid(j, k, 24, 4, 82, 208);
        RenderSystem.disableBlend();
        for (AlternativeButtonWidget alternativeButtonWidget : this.alternativeButtons) {
            alternativeButtonWidget.render(mouseX, mouseY, delta);
        }
        RenderSystem.popMatrix();
    }

    private void renderGrid(int columns, int rows, int squareSize, int borderSize, int u, int v) {
        this.blit(this.buttonX, this.buttonY, u, v, borderSize, borderSize);
        this.blit(this.buttonX + borderSize * 2 + columns * squareSize, this.buttonY, u + squareSize + borderSize, v, borderSize, borderSize);
        this.blit(this.buttonX, this.buttonY + borderSize * 2 + rows * squareSize, u, v + squareSize + borderSize, borderSize, borderSize);
        this.blit(this.buttonX + borderSize * 2 + columns * squareSize, this.buttonY + borderSize * 2 + rows * squareSize, u + squareSize + borderSize, v + squareSize + borderSize, borderSize, borderSize);
        for (int i = 0; i < columns; ++i) {
            this.blit(this.buttonX + borderSize + i * squareSize, this.buttonY, u + borderSize, v, squareSize, borderSize);
            this.blit(this.buttonX + borderSize + (i + 1) * squareSize, this.buttonY, u + borderSize, v, borderSize, borderSize);
            for (int j = 0; j < rows; ++j) {
                if (i == 0) {
                    this.blit(this.buttonX, this.buttonY + borderSize + j * squareSize, u, v + borderSize, borderSize, squareSize);
                    this.blit(this.buttonX, this.buttonY + borderSize + (j + 1) * squareSize, u, v + borderSize, borderSize, borderSize);
                }
                this.blit(this.buttonX + borderSize + i * squareSize, this.buttonY + borderSize + j * squareSize, u + borderSize, v + borderSize, squareSize, squareSize);
                this.blit(this.buttonX + borderSize + (i + 1) * squareSize, this.buttonY + borderSize + j * squareSize, u + borderSize, v + borderSize, borderSize, squareSize);
                this.blit(this.buttonX + borderSize + i * squareSize, this.buttonY + borderSize + (j + 1) * squareSize, u + borderSize, v + borderSize, squareSize, borderSize);
                this.blit(this.buttonX + borderSize + (i + 1) * squareSize - 1, this.buttonY + borderSize + (j + 1) * squareSize - 1, u + borderSize, v + borderSize, borderSize + 1, borderSize + 1);
                if (i != columns - 1) continue;
                this.blit(this.buttonX + borderSize * 2 + columns * squareSize, this.buttonY + borderSize + j * squareSize, u + squareSize + borderSize, v + borderSize, borderSize, squareSize);
                this.blit(this.buttonX + borderSize * 2 + columns * squareSize, this.buttonY + borderSize + (j + 1) * squareSize, u + squareSize + borderSize, v + borderSize, borderSize, borderSize);
            }
            this.blit(this.buttonX + borderSize + i * squareSize, this.buttonY + borderSize * 2 + rows * squareSize, u + borderSize, v + squareSize + borderSize, squareSize, borderSize);
            this.blit(this.buttonX + borderSize + (i + 1) * squareSize, this.buttonY + borderSize * 2 + rows * squareSize, u + borderSize, v + squareSize + borderSize, borderSize, borderSize);
        }
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return this.visible;
    }

    @Environment(value=EnvType.CLIENT)
    class AlternativeButtonWidget
    extends AbstractButtonWidget
    implements RecipeGridAligner<Ingredient> {
        private final Recipe<?> recipe;
        private final boolean isCraftable;
        protected final List<InputSlot> slots;

        public AlternativeButtonWidget(int x, int y, Recipe<?> recipe, boolean isCraftable) {
            super(x, y, 200, 20, "");
            this.slots = Lists.newArrayList();
            this.width = 24;
            this.height = 24;
            this.recipe = recipe;
            this.isCraftable = isCraftable;
            this.alignRecipe(recipe);
        }

        protected void alignRecipe(Recipe<?> recipe) {
            this.alignRecipeToGrid(3, 3, -1, recipe, recipe.getPreviewInputs().iterator(), 0);
        }

        @Override
        public void acceptAlignedInput(Iterator<Ingredient> inputs, int slot, int amount, int gridX, int gridY) {
            ItemStack[] itemStacks = inputs.next().getMatchingStacksClient();
            if (itemStacks.length != 0) {
                this.slots.add(new InputSlot(3 + gridY * 7, 3 + gridX * 7, itemStacks));
            }
        }

        @Override
        public void renderButton(int mouseX, int mouseY, float delta) {
            int j;
            RenderSystem.enableAlphaTest();
            RecipeAlternativesWidget.this.client.getTextureManager().bindTexture(BG_TEX);
            int i = 152;
            if (!this.isCraftable) {
                i += 26;
            }
            int n = j = RecipeAlternativesWidget.this.furnace ? 130 : 78;
            if (this.isHovered()) {
                j += 26;
            }
            this.blit(this.x, this.y, i, j, this.width, this.height);
            for (InputSlot inputSlot : this.slots) {
                RenderSystem.pushMatrix();
                float f = 0.42f;
                int k = (int)((float)(this.x + inputSlot.y) / 0.42f - 3.0f);
                int l = (int)((float)(this.y + inputSlot.x) / 0.42f - 3.0f);
                RenderSystem.scalef(0.42f, 0.42f, 1.0f);
                RecipeAlternativesWidget.this.client.getItemRenderer().renderGuiItem(inputSlot.stacks[MathHelper.floor(RecipeAlternativesWidget.this.time / 30.0f) % inputSlot.stacks.length], k, l);
                RenderSystem.popMatrix();
            }
            RenderSystem.disableAlphaTest();
        }

        @Environment(value=EnvType.CLIENT)
        public class InputSlot {
            public final ItemStack[] stacks;
            public final int y;
            public final int x;

            public InputSlot(int y, int x, ItemStack[] stacks) {
                this.y = y;
                this.x = x;
                this.stacks = stacks;
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class FurnaceAlternativeButtonWidget
    extends AlternativeButtonWidget {
        public FurnaceAlternativeButtonWidget(int x, int y, Recipe<?> recipe, boolean isCraftable) {
            super(x, y, recipe, isCraftable);
        }

        @Override
        protected void alignRecipe(Recipe<?> recipe) {
            ItemStack[] itemStacks = recipe.getPreviewInputs().get(0).getMatchingStacksClient();
            this.slots.add(new AlternativeButtonWidget.InputSlot(10, 10, itemStacks));
        }
    }
}

