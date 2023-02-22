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
import com.mojang.blaze3d.platform.GlStateManager;
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
import net.minecraft.client.render.DiffuseLighting;
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
    private final List<AlternateButtonWidget> alternativeButtons = Lists.newArrayList();
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
                this.alternativeButtons.add(new class_511(r, s, recipe, bl2));
                continue;
            }
            this.alternativeButtons.add(new AlternateButtonWidget(r, s, recipe, bl2));
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
        for (AlternateButtonWidget alternateButtonWidget : this.alternativeButtons) {
            if (!alternateButtonWidget.mouseClicked(mouseX, mouseY, button)) continue;
            this.lastClickedRecipe = alternateButtonWidget.recipe;
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
        DiffuseLighting.enableForItems();
        GlStateManager.enableBlend();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(BG_TEX);
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.0f, 0.0f, 170.0f);
        int i = this.alternativeButtons.size() <= 16 ? 4 : 5;
        int j = Math.min(this.alternativeButtons.size(), i);
        int k = MathHelper.ceil((float)this.alternativeButtons.size() / (float)i);
        int l = 24;
        int m = 4;
        int n = 82;
        int o = 208;
        this.method_2618(j, k, 24, 4, 82, 208);
        GlStateManager.disableBlend();
        DiffuseLighting.disable();
        for (AlternateButtonWidget alternateButtonWidget : this.alternativeButtons) {
            alternateButtonWidget.render(mouseX, mouseY, delta);
        }
        GlStateManager.popMatrix();
    }

    private void method_2618(int i, int j, int k, int l, int m, int n) {
        this.blit(this.buttonX, this.buttonY, m, n, l, l);
        this.blit(this.buttonX + l * 2 + i * k, this.buttonY, m + k + l, n, l, l);
        this.blit(this.buttonX, this.buttonY + l * 2 + j * k, m, n + k + l, l, l);
        this.blit(this.buttonX + l * 2 + i * k, this.buttonY + l * 2 + j * k, m + k + l, n + k + l, l, l);
        for (int o = 0; o < i; ++o) {
            this.blit(this.buttonX + l + o * k, this.buttonY, m + l, n, k, l);
            this.blit(this.buttonX + l + (o + 1) * k, this.buttonY, m + l, n, l, l);
            for (int p = 0; p < j; ++p) {
                if (o == 0) {
                    this.blit(this.buttonX, this.buttonY + l + p * k, m, n + l, l, k);
                    this.blit(this.buttonX, this.buttonY + l + (p + 1) * k, m, n + l, l, l);
                }
                this.blit(this.buttonX + l + o * k, this.buttonY + l + p * k, m + l, n + l, k, k);
                this.blit(this.buttonX + l + (o + 1) * k, this.buttonY + l + p * k, m + l, n + l, l, k);
                this.blit(this.buttonX + l + o * k, this.buttonY + l + (p + 1) * k, m + l, n + l, k, l);
                this.blit(this.buttonX + l + (o + 1) * k - 1, this.buttonY + l + (p + 1) * k - 1, m + l, n + l, l + 1, l + 1);
                if (o != i - 1) continue;
                this.blit(this.buttonX + l * 2 + i * k, this.buttonY + l + p * k, m + k + l, n + l, l, k);
                this.blit(this.buttonX + l * 2 + i * k, this.buttonY + l + (p + 1) * k, m + k + l, n + l, l, l);
            }
            this.blit(this.buttonX + l + o * k, this.buttonY + l * 2 + j * k, m + l, n + k + l, k, l);
            this.blit(this.buttonX + l + (o + 1) * k, this.buttonY + l * 2 + j * k, m + l, n + k + l, l, l);
        }
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return this.visible;
    }

    @Environment(value=EnvType.CLIENT)
    class AlternateButtonWidget
    extends AbstractButtonWidget
    implements RecipeGridAligner<Ingredient> {
        private final Recipe<?> recipe;
        private final boolean isCraftable;
        protected final List<InputSlot> slots;

        public AlternateButtonWidget(int x, int y, Recipe<?> recipe, boolean isCraftable) {
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
            DiffuseLighting.enableForItems();
            GlStateManager.enableAlphaTest();
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
                GlStateManager.pushMatrix();
                float f = 0.42f;
                int k = (int)((float)(this.x + inputSlot.field_3119) / 0.42f - 3.0f);
                int l = (int)((float)(this.y + inputSlot.field_3118) / 0.42f - 3.0f);
                GlStateManager.scalef(0.42f, 0.42f, 1.0f);
                GlStateManager.enableLighting();
                RecipeAlternativesWidget.this.client.getItemRenderer().renderGuiItem(inputSlot.field_3120[MathHelper.floor(RecipeAlternativesWidget.this.time / 30.0f) % inputSlot.field_3120.length], k, l);
                GlStateManager.disableLighting();
                GlStateManager.popMatrix();
            }
            GlStateManager.disableAlphaTest();
            DiffuseLighting.disable();
        }

        @Environment(value=EnvType.CLIENT)
        public class InputSlot {
            public final ItemStack[] field_3120;
            public final int field_3119;
            public final int field_3118;

            public InputSlot(int i, int j, ItemStack[] itemStacks) {
                this.field_3119 = i;
                this.field_3118 = j;
                this.field_3120 = itemStacks;
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class class_511
    extends AlternateButtonWidget {
        public class_511(int x, int y, Recipe<?> recipe, boolean isCraftable) {
            super(x, y, recipe, isCraftable);
        }

        @Override
        protected void alignRecipe(Recipe<?> recipe) {
            ItemStack[] itemStacks = recipe.getPreviewInputs().get(0).getMatchingStacksClient();
            this.slots.add(new AlternateButtonWidget.InputSlot(10, 10, itemStacks));
        }
    }
}

