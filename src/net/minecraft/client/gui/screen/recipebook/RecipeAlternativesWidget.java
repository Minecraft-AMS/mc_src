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
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeGridAligner;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RecipeAlternativesWidget
implements Drawable,
Element {
    static final Identifier BACKGROUND_TEXTURE = new Identifier("textures/gui/recipe_book.png");
    private static final int field_32406 = 4;
    private static final int field_32407 = 5;
    private static final float field_33739 = 0.375f;
    public static final int field_42162 = 25;
    private final List<AlternativeButtonWidget> alternativeButtons = Lists.newArrayList();
    private boolean visible;
    private int buttonX;
    private int buttonY;
    private MinecraftClient client;
    private RecipeResultCollection resultCollection;
    @Nullable
    private Recipe<?> lastClickedRecipe;
    float time;
    boolean furnace;

    public void showAlternativesForResult(MinecraftClient client, RecipeResultCollection results, int buttonX, int buttonY, int areaCenterX, int areaCenterY, float delta) {
        float o;
        float n;
        float m;
        float h;
        float g;
        this.client = client;
        this.resultCollection = results;
        if (client.player.currentScreenHandler instanceof AbstractFurnaceScreenHandler) {
            this.furnace = true;
        }
        boolean bl = client.player.getRecipeBook().isFilteringCraftable((AbstractRecipeScreenHandler)client.player.currentScreenHandler);
        List<Recipe<?>> list = results.getRecipes(true);
        List list2 = bl ? Collections.emptyList() : results.getRecipes(false);
        int i = list.size();
        int j = i + list2.size();
        int k = j <= 16 ? 4 : 5;
        int l = (int)Math.ceil((float)j / (float)k);
        this.buttonX = buttonX;
        this.buttonY = buttonY;
        float f = this.buttonX + Math.min(j, k) * 25;
        if (f > (g = (float)(areaCenterX + 50))) {
            this.buttonX = (int)((float)this.buttonX - delta * (float)((int)((f - g) / delta)));
        }
        if ((h = (float)(this.buttonY + l * 25)) > (m = (float)(areaCenterY + 50))) {
            this.buttonY = (int)((float)this.buttonY - delta * (float)MathHelper.ceil((h - m) / delta));
        }
        if ((n = (float)this.buttonY) < (o = (float)(areaCenterY - 100))) {
            this.buttonY = (int)((float)this.buttonY - delta * (float)MathHelper.ceil((n - o) / delta));
        }
        this.visible = true;
        this.alternativeButtons.clear();
        for (int p = 0; p < j; ++p) {
            boolean bl2 = p < i;
            Recipe recipe = bl2 ? list.get(p) : (Recipe)list2.get(p - i);
            int q = this.buttonX + 4 + 25 * (p % k);
            int r = this.buttonY + 5 + 25 * (p / k);
            if (this.furnace) {
                this.alternativeButtons.add(new FurnaceAlternativeButtonWidget(q, r, recipe, bl2));
                continue;
            }
            this.alternativeButtons.add(new AlternativeButtonWidget(q, r, recipe, bl2));
        }
        this.lastClickedRecipe = null;
    }

    public RecipeResultCollection getResults() {
        return this.resultCollection;
    }

    @Nullable
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
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!this.visible) {
            return;
        }
        this.time += delta;
        RenderSystem.enableBlend();
        context.getMatrices().push();
        context.getMatrices().translate(0.0f, 0.0f, 170.0f);
        int i = this.alternativeButtons.size() <= 16 ? 4 : 5;
        int j = Math.min(this.alternativeButtons.size(), i);
        int k = MathHelper.ceil((float)this.alternativeButtons.size() / (float)i);
        int l = 4;
        context.drawNineSlicedTexture(BACKGROUND_TEXTURE, this.buttonX, this.buttonY, j * 25 + 8, k * 25 + 8, 4, 32, 32, 82, 208);
        RenderSystem.disableBlend();
        for (AlternativeButtonWidget alternativeButtonWidget : this.alternativeButtons) {
            alternativeButtonWidget.render(context, mouseX, mouseY, delta);
        }
        context.getMatrices().pop();
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public void setFocused(boolean focused) {
    }

    @Override
    public boolean isFocused() {
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    class FurnaceAlternativeButtonWidget
    extends AlternativeButtonWidget {
        public FurnaceAlternativeButtonWidget(int i, int j, Recipe<?> recipe, boolean bl) {
            super(i, j, recipe, bl);
        }

        @Override
        protected void alignRecipe(Recipe<?> recipe) {
            ItemStack[] itemStacks = recipe.getIngredients().get(0).getMatchingStacks();
            this.slots.add(new AlternativeButtonWidget.InputSlot(10, 10, itemStacks));
        }
    }

    @Environment(value=EnvType.CLIENT)
    class AlternativeButtonWidget
    extends ClickableWidget
    implements RecipeGridAligner<Ingredient> {
        final Recipe<?> recipe;
        private final boolean craftable;
        protected final List<InputSlot> slots;

        public AlternativeButtonWidget(int x, int y, Recipe<?> recipe, boolean craftable) {
            super(x, y, 200, 20, ScreenTexts.EMPTY);
            this.slots = Lists.newArrayList();
            this.width = 24;
            this.height = 24;
            this.recipe = recipe;
            this.craftable = craftable;
            this.alignRecipe(recipe);
        }

        protected void alignRecipe(Recipe<?> recipe) {
            this.alignRecipeToGrid(3, 3, -1, recipe, recipe.getIngredients().iterator(), 0);
        }

        @Override
        public void appendClickableNarrations(NarrationMessageBuilder builder) {
            this.appendDefaultNarrations(builder);
        }

        @Override
        public void acceptAlignedInput(Iterator<Ingredient> inputs, int slot, int amount, int gridX, int gridY) {
            ItemStack[] itemStacks = inputs.next().getMatchingStacks();
            if (itemStacks.length != 0) {
                this.slots.add(new InputSlot(3 + gridY * 7, 3 + gridX * 7, itemStacks));
            }
        }

        @Override
        public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            int j;
            int i = 152;
            if (!this.craftable) {
                i += 26;
            }
            int n = j = RecipeAlternativesWidget.this.furnace ? 130 : 78;
            if (this.isSelected()) {
                j += 26;
            }
            context.drawTexture(BACKGROUND_TEXTURE, this.getX(), this.getY(), i, j, this.width, this.height);
            context.getMatrices().push();
            context.getMatrices().translate((double)(this.getX() + 2), (double)(this.getY() + 2), 150.0);
            for (InputSlot inputSlot : this.slots) {
                context.getMatrices().push();
                context.getMatrices().translate((double)inputSlot.y, (double)inputSlot.x, 0.0);
                context.getMatrices().scale(0.375f, 0.375f, 1.0f);
                context.getMatrices().translate(-8.0, -8.0, 0.0);
                context.drawItem(inputSlot.stacks[MathHelper.floor(RecipeAlternativesWidget.this.time / 30.0f) % inputSlot.stacks.length], 0, 0);
                context.getMatrices().pop();
            }
            context.getMatrices().pop();
        }

        @Environment(value=EnvType.CLIENT)
        protected class InputSlot {
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
}

