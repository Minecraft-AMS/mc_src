/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.toast;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class RecipeToast
implements Toast {
    private static final long DURATION = 5000L;
    private static final Text TITLE = new TranslatableText("recipe.toast.title");
    private static final Text DESCRIPTION = new TranslatableText("recipe.toast.description");
    private final List<Recipe<?>> recipes = Lists.newArrayList();
    private long startTime;
    private boolean justUpdated;

    public RecipeToast(Recipe<?> recipes) {
        this.recipes.add(recipes);
    }

    @Override
    public Toast.Visibility draw(MatrixStack matrices, ToastManager manager, long startTime) {
        if (this.justUpdated) {
            this.startTime = startTime;
            this.justUpdated = false;
        }
        if (this.recipes.isEmpty()) {
            return Toast.Visibility.HIDE;
        }
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        manager.drawTexture(matrices, 0, 0, 0, 32, this.getWidth(), this.getHeight());
        manager.getClient().textRenderer.draw(matrices, TITLE, 30.0f, 7.0f, -11534256);
        manager.getClient().textRenderer.draw(matrices, DESCRIPTION, 30.0f, 18.0f, -16777216);
        Recipe<?> recipe = this.recipes.get((int)(startTime / Math.max(1L, 5000L / (long)this.recipes.size()) % (long)this.recipes.size()));
        ItemStack itemStack = recipe.createIcon();
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.scale(0.6f, 0.6f, 1.0f);
        RenderSystem.applyModelViewMatrix();
        manager.getClient().getItemRenderer().renderInGui(itemStack, 3, 3);
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
        manager.getClient().getItemRenderer().renderInGui(recipe.getOutput(), 8, 8);
        return startTime - this.startTime >= 5000L ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
    }

    private void addRecipes(Recipe<?> recipes) {
        this.recipes.add(recipes);
        this.justUpdated = true;
    }

    public static void show(ToastManager manager, Recipe<?> recipes) {
        RecipeToast recipeToast = manager.getToast(RecipeToast.class, TYPE);
        if (recipeToast == null) {
            manager.add(new RecipeToast(recipes));
        } else {
            recipeToast.addRecipes(recipes);
        }
    }
}

