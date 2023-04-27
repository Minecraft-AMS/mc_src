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
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class RecipeToast
implements Toast {
    private static final long DURATION = 5000L;
    private static final Text TITLE = Text.translatable("recipe.toast.title");
    private static final Text DESCRIPTION = Text.translatable("recipe.toast.description");
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
        RenderSystem.setShaderTexture(0, TEXTURE);
        DrawableHelper.drawTexture(matrices, 0, 0, 0, 32, this.getWidth(), this.getHeight());
        manager.getClient().textRenderer.draw(matrices, TITLE, 30.0f, 7.0f, -11534256);
        manager.getClient().textRenderer.draw(matrices, DESCRIPTION, 30.0f, 18.0f, -16777216);
        Recipe<?> recipe = this.recipes.get((int)((double)startTime / Math.max(1.0, 5000.0 * manager.getNotificationDisplayTimeMultiplier() / (double)this.recipes.size()) % (double)this.recipes.size()));
        ItemStack itemStack = recipe.createIcon();
        matrices.push();
        matrices.scale(0.6f, 0.6f, 1.0f);
        manager.getClient().getItemRenderer().renderInGui(matrices, itemStack, 3, 3);
        matrices.pop();
        manager.getClient().getItemRenderer().renderInGui(matrices, recipe.getOutput(manager.getClient().world.getRegistryManager()), 8, 8);
        return (double)(startTime - this.startTime) >= 5000.0 * manager.getNotificationDisplayTimeMultiplier() ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
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

