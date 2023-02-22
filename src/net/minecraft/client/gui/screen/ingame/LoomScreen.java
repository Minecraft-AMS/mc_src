/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen.ingame;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.container.LoomContainer;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class LoomScreen
extends ContainerScreen<LoomContainer> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/loom.png");
    private static final int PATTERN_BUTTON_ROW_COUNT = (BannerPattern.COUNT - 5 - 1 + 4 - 1) / 4;
    private final ModelPart field_21694;
    @Nullable
    private List<Pair<BannerPattern, DyeColor>> field_21841;
    private ItemStack banner = ItemStack.EMPTY;
    private ItemStack dye = ItemStack.EMPTY;
    private ItemStack pattern = ItemStack.EMPTY;
    private boolean canApplyDyePattern;
    private boolean canApplySpecialPattern;
    private boolean hasTooManyPatterns;
    private float scrollPosition;
    private boolean scrollbarClicked;
    private int firstPatternButtonId = 1;

    public LoomScreen(LoomContainer container, PlayerInventory inventory, Text title) {
        super(container, inventory, title);
        this.field_21694 = BannerBlockEntityRenderer.createField();
        container.setInventoryChangeListener(this::onInventoryChanged);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        super.render(mouseX, mouseY, delta);
        this.drawMouseoverTooltip(mouseX, mouseY);
    }

    @Override
    protected void drawForeground(int mouseX, int mouseY) {
        this.font.draw(this.title.asFormattedString(), 8.0f, 4.0f, 0x404040);
        this.font.draw(this.playerInventory.getDisplayName().asFormattedString(), 8.0f, this.containerHeight - 96 + 2, 0x404040);
    }

    @Override
    protected void drawBackground(float delta, int mouseX, int mouseY) {
        this.renderBackground();
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        int i = this.x;
        int j = this.y;
        this.blit(i, j, 0, 0, this.containerWidth, this.containerHeight);
        Slot slot = ((LoomContainer)this.container).getBannerSlot();
        Slot slot2 = ((LoomContainer)this.container).getDyeSlot();
        Slot slot3 = ((LoomContainer)this.container).getPatternSlot();
        Slot slot4 = ((LoomContainer)this.container).getOutputSlot();
        if (!slot.hasStack()) {
            this.blit(i + slot.xPosition, j + slot.yPosition, this.containerWidth, 0, 16, 16);
        }
        if (!slot2.hasStack()) {
            this.blit(i + slot2.xPosition, j + slot2.yPosition, this.containerWidth + 16, 0, 16, 16);
        }
        if (!slot3.hasStack()) {
            this.blit(i + slot3.xPosition, j + slot3.yPosition, this.containerWidth + 32, 0, 16, 16);
        }
        int k = (int)(41.0f * this.scrollPosition);
        this.blit(i + 119, j + 13 + k, 232 + (this.canApplyDyePattern ? 0 : 12), 0, 12, 15);
        DiffuseLighting.disableGuiDepthLighting();
        if (this.field_21841 != null && !this.hasTooManyPatterns) {
            VertexConsumerProvider.Immediate immediate = this.minecraft.getBufferBuilders().getEntityVertexConsumers();
            MatrixStack matrixStack = new MatrixStack();
            matrixStack.translate(i + 139, j + 52, 0.0);
            matrixStack.scale(24.0f, -24.0f, 1.0f);
            matrixStack.translate(0.5, 0.5, 0.5);
            float f = 0.6666667f;
            matrixStack.scale(0.6666667f, -0.6666667f, -0.6666667f);
            this.field_21694.pitch = 0.0f;
            this.field_21694.pivotY = -32.0f;
            BannerBlockEntityRenderer.method_23802(matrixStack, immediate, 0xF000F0, OverlayTexture.DEFAULT_UV, this.field_21694, ModelLoader.BANNER_BASE, true, this.field_21841);
            immediate.draw();
        } else if (this.hasTooManyPatterns) {
            this.blit(i + slot4.xPosition - 2, j + slot4.yPosition - 2, this.containerWidth, 17, 17, 16);
        }
        if (this.canApplyDyePattern) {
            int l = i + 60;
            int m = j + 13;
            int n = this.firstPatternButtonId + 16;
            for (int o = this.firstPatternButtonId; o < n && o < BannerPattern.COUNT - 5; ++o) {
                int p = o - this.firstPatternButtonId;
                int q = l + p % 4 * 14;
                int r = m + p / 4 * 14;
                this.minecraft.getTextureManager().bindTexture(TEXTURE);
                int s = this.containerHeight;
                if (o == ((LoomContainer)this.container).getSelectedPattern()) {
                    s += 14;
                } else if (mouseX >= q && mouseY >= r && mouseX < q + 14 && mouseY < r + 14) {
                    s += 28;
                }
                this.blit(q, r, 0, s, 14, 14);
                this.method_22692(o, q, r);
            }
        } else if (this.canApplySpecialPattern) {
            int l = i + 60;
            int m = j + 13;
            this.minecraft.getTextureManager().bindTexture(TEXTURE);
            this.blit(l, m, 0, this.containerHeight, 14, 14);
            int n = ((LoomContainer)this.container).getSelectedPattern();
            this.method_22692(n, l, m);
        }
        DiffuseLighting.enableGuiDepthLighting();
    }

    private void method_22692(int i, int j, int k) {
        ItemStack itemStack = new ItemStack(Items.GRAY_BANNER);
        CompoundTag compoundTag = itemStack.getOrCreateSubTag("BlockEntityTag");
        ListTag listTag = new BannerPattern.Patterns().add(BannerPattern.BASE, DyeColor.GRAY).add(BannerPattern.values()[i], DyeColor.WHITE).toTag();
        compoundTag.put("Patterns", listTag);
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.push();
        matrixStack.translate((float)j + 0.5f, k + 16, 0.0);
        matrixStack.scale(6.0f, -6.0f, 1.0f);
        matrixStack.translate(0.5, 0.5, 0.0);
        matrixStack.translate(0.5, 0.5, 0.5);
        float f = 0.6666667f;
        matrixStack.scale(0.6666667f, -0.6666667f, -0.6666667f);
        VertexConsumerProvider.Immediate immediate = this.minecraft.getBufferBuilders().getEntityVertexConsumers();
        this.field_21694.pitch = 0.0f;
        this.field_21694.pivotY = -32.0f;
        List<Pair<BannerPattern, DyeColor>> list = BannerBlockEntity.method_24280(DyeColor.GRAY, BannerBlockEntity.method_24281(itemStack));
        BannerBlockEntityRenderer.method_23802(matrixStack, immediate, 0xF000F0, OverlayTexture.DEFAULT_UV, this.field_21694, ModelLoader.BANNER_BASE, true, list);
        matrixStack.pop();
        immediate.draw();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.scrollbarClicked = false;
        if (this.canApplyDyePattern) {
            int i = this.x + 60;
            int j = this.y + 13;
            int k = this.firstPatternButtonId + 16;
            for (int l = this.firstPatternButtonId; l < k; ++l) {
                int m = l - this.firstPatternButtonId;
                double d = mouseX - (double)(i + m % 4 * 14);
                double e = mouseY - (double)(j + m / 4 * 14);
                if (!(d >= 0.0) || !(e >= 0.0) || !(d < 14.0) || !(e < 14.0) || !((LoomContainer)this.container).onButtonClick(this.minecraft.player, l)) continue;
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_LOOM_SELECT_PATTERN, 1.0f));
                this.minecraft.interactionManager.clickButton(((LoomContainer)this.container).syncId, l);
                return true;
            }
            i = this.x + 119;
            j = this.y + 9;
            if (mouseX >= (double)i && mouseX < (double)(i + 12) && mouseY >= (double)j && mouseY < (double)(j + 56)) {
                this.scrollbarClicked = true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.scrollbarClicked && this.canApplyDyePattern) {
            int i = this.y + 13;
            int j = i + 56;
            this.scrollPosition = ((float)mouseY - (float)i - 7.5f) / ((float)(j - i) - 15.0f);
            this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0f, 1.0f);
            int k = PATTERN_BUTTON_ROW_COUNT - 4;
            int l = (int)((double)(this.scrollPosition * (float)k) + 0.5);
            if (l < 0) {
                l = 0;
            }
            this.firstPatternButtonId = 1 + l * 4;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double d, double e, double amount) {
        if (this.canApplyDyePattern) {
            int i = PATTERN_BUTTON_ROW_COUNT - 4;
            this.scrollPosition = (float)((double)this.scrollPosition - amount / (double)i);
            this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0f, 1.0f);
            this.firstPatternButtonId = 1 + (int)((double)(this.scrollPosition * (float)i) + 0.5) * 4;
        }
        return true;
    }

    @Override
    protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
        return mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.containerWidth) || mouseY >= (double)(top + this.containerHeight);
    }

    private void onInventoryChanged() {
        ItemStack itemStack = ((LoomContainer)this.container).getOutputSlot().getStack();
        this.field_21841 = itemStack.isEmpty() ? null : BannerBlockEntity.method_24280(((BannerItem)itemStack.getItem()).getColor(), BannerBlockEntity.method_24281(itemStack));
        ItemStack itemStack2 = ((LoomContainer)this.container).getBannerSlot().getStack();
        ItemStack itemStack3 = ((LoomContainer)this.container).getDyeSlot().getStack();
        ItemStack itemStack4 = ((LoomContainer)this.container).getPatternSlot().getStack();
        CompoundTag compoundTag = itemStack2.getOrCreateSubTag("BlockEntityTag");
        boolean bl = this.hasTooManyPatterns = compoundTag.contains("Patterns", 9) && !itemStack2.isEmpty() && compoundTag.getList("Patterns", 10).size() >= 6;
        if (this.hasTooManyPatterns) {
            this.field_21841 = null;
        }
        if (!(ItemStack.areEqualIgnoreDamage(itemStack2, this.banner) && ItemStack.areEqualIgnoreDamage(itemStack3, this.dye) && ItemStack.areEqualIgnoreDamage(itemStack4, this.pattern))) {
            this.canApplyDyePattern = !itemStack2.isEmpty() && !itemStack3.isEmpty() && itemStack4.isEmpty() && !this.hasTooManyPatterns;
            this.canApplySpecialPattern = !this.hasTooManyPatterns && !itemStack4.isEmpty() && !itemStack2.isEmpty() && !itemStack3.isEmpty();
        }
        this.banner = itemStack2.copy();
        this.dye = itemStack3.copy();
        this.pattern = itemStack4.copy();
    }
}

