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

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.LoomScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class LoomScreen
extends HandledScreen<LoomScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/loom.png");
    private static final int field_32345 = 1;
    private static final int PATTERN_LIST_COLUMNS = 4;
    private static final int PATTERN_LIST_ROWS = 4;
    private static final int PATTERN_BUTTON_ROW_COUNT = (BannerPattern.COUNT - BannerPattern.HAS_PATTERN_ITEM_COUNT - 1 + 4 - 1) / 4;
    private static final int SCROLLBAR_WIDTH = 12;
    private static final int SCROLLBAR_HEIGHT = 15;
    private static final int PATTERN_ENTRY_SIZE = 14;
    private static final int SCROLLBAR_AREA_HEIGHT = 56;
    private static final int PATTERN_LIST_OFFSET_X = 60;
    private static final int PATTERN_LIST_OFFSET_Y = 13;
    private ModelPart bannerField;
    @Nullable
    private List<Pair<BannerPattern, DyeColor>> bannerPatterns;
    private ItemStack banner = ItemStack.EMPTY;
    private ItemStack dye = ItemStack.EMPTY;
    private ItemStack pattern = ItemStack.EMPTY;
    private boolean canApplyDyePattern;
    private boolean canApplySpecialPattern;
    private boolean hasTooManyPatterns;
    private float scrollPosition;
    private boolean scrollbarClicked;
    private int firstPatternButtonId = 1;

    public LoomScreen(LoomScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        handler.setInventoryChangeListener(this::onInventoryChanged);
        this.titleY -= 2;
    }

    @Override
    protected void init() {
        super.init();
        this.bannerField = this.client.getEntityModelLoader().getModelPart(EntityModelLayers.BANNER).getChild("flag");
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        this.renderBackground(matrices);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = this.x;
        int j = this.y;
        this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        Slot slot = ((LoomScreenHandler)this.handler).getBannerSlot();
        Slot slot2 = ((LoomScreenHandler)this.handler).getDyeSlot();
        Slot slot3 = ((LoomScreenHandler)this.handler).getPatternSlot();
        Slot slot4 = ((LoomScreenHandler)this.handler).getOutputSlot();
        if (!slot.hasStack()) {
            this.drawTexture(matrices, i + slot.x, j + slot.y, this.backgroundWidth, 0, 16, 16);
        }
        if (!slot2.hasStack()) {
            this.drawTexture(matrices, i + slot2.x, j + slot2.y, this.backgroundWidth + 16, 0, 16, 16);
        }
        if (!slot3.hasStack()) {
            this.drawTexture(matrices, i + slot3.x, j + slot3.y, this.backgroundWidth + 32, 0, 16, 16);
        }
        int k = (int)(41.0f * this.scrollPosition);
        this.drawTexture(matrices, i + 119, j + 13 + k, 232 + (this.canApplyDyePattern ? 0 : 12), 0, 12, 15);
        DiffuseLighting.disableGuiDepthLighting();
        if (this.bannerPatterns != null && !this.hasTooManyPatterns) {
            VertexConsumerProvider.Immediate immediate = this.client.getBufferBuilders().getEntityVertexConsumers();
            matrices.push();
            matrices.translate(i + 139, j + 52, 0.0);
            matrices.scale(24.0f, -24.0f, 1.0f);
            matrices.translate(0.5, 0.5, 0.5);
            float f = 0.6666667f;
            matrices.scale(0.6666667f, -0.6666667f, -0.6666667f);
            this.bannerField.pitch = 0.0f;
            this.bannerField.pivotY = -32.0f;
            BannerBlockEntityRenderer.renderCanvas(matrices, immediate, 0xF000F0, OverlayTexture.DEFAULT_UV, this.bannerField, ModelLoader.BANNER_BASE, true, this.bannerPatterns);
            matrices.pop();
            immediate.draw();
        } else if (this.hasTooManyPatterns) {
            this.drawTexture(matrices, i + slot4.x - 2, j + slot4.y - 2, this.backgroundWidth, 17, 17, 16);
        }
        if (this.canApplyDyePattern) {
            int l = i + 60;
            int m = j + 13;
            int n = this.firstPatternButtonId + 16;
            for (int o = this.firstPatternButtonId; o < n && o < BannerPattern.COUNT - BannerPattern.HAS_PATTERN_ITEM_COUNT; ++o) {
                int p = o - this.firstPatternButtonId;
                int q = l + p % 4 * 14;
                int r = m + p / 4 * 14;
                RenderSystem.setShaderTexture(0, TEXTURE);
                int s = this.backgroundHeight;
                if (o == ((LoomScreenHandler)this.handler).getSelectedPattern()) {
                    s += 14;
                } else if (mouseX >= q && mouseY >= r && mouseX < q + 14 && mouseY < r + 14) {
                    s += 28;
                }
                this.drawTexture(matrices, q, r, 0, s, 14, 14);
                this.drawBanner(o, q, r);
            }
        } else if (this.canApplySpecialPattern) {
            int l = i + 60;
            int m = j + 13;
            RenderSystem.setShaderTexture(0, TEXTURE);
            this.drawTexture(matrices, l, m, 0, this.backgroundHeight, 14, 14);
            int n = ((LoomScreenHandler)this.handler).getSelectedPattern();
            this.drawBanner(n, l, m);
        }
        DiffuseLighting.enableGuiDepthLighting();
    }

    private void drawBanner(int pattern, int x, int y) {
        NbtCompound nbtCompound = new NbtCompound();
        NbtList nbtList = new BannerPattern.Patterns().add(BannerPattern.BASE, DyeColor.GRAY).add(BannerPattern.values()[pattern], DyeColor.WHITE).toNbt();
        nbtCompound.put("Patterns", nbtList);
        ItemStack itemStack = new ItemStack(Items.GRAY_BANNER);
        BlockItem.setBlockEntityNbt(itemStack, BlockEntityType.BANNER, nbtCompound);
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.push();
        matrixStack.translate((float)x + 0.5f, y + 16, 0.0);
        matrixStack.scale(6.0f, -6.0f, 1.0f);
        matrixStack.translate(0.5, 0.5, 0.0);
        matrixStack.translate(0.5, 0.5, 0.5);
        float f = 0.6666667f;
        matrixStack.scale(0.6666667f, -0.6666667f, -0.6666667f);
        VertexConsumerProvider.Immediate immediate = this.client.getBufferBuilders().getEntityVertexConsumers();
        this.bannerField.pitch = 0.0f;
        this.bannerField.pivotY = -32.0f;
        List<Pair<BannerPattern, DyeColor>> list = BannerBlockEntity.getPatternsFromNbt(DyeColor.GRAY, BannerBlockEntity.getPatternListNbt(itemStack));
        BannerBlockEntityRenderer.renderCanvas(matrixStack, immediate, 0xF000F0, OverlayTexture.DEFAULT_UV, this.bannerField, ModelLoader.BANNER_BASE, true, list);
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
                if (!(d >= 0.0) || !(e >= 0.0) || !(d < 14.0) || !(e < 14.0) || !((LoomScreenHandler)this.handler).onButtonClick(this.client.player, l)) continue;
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_LOOM_SELECT_PATTERN, 1.0f));
                this.client.interactionManager.clickButton(((LoomScreenHandler)this.handler).syncId, l);
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
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (this.canApplyDyePattern) {
            int i = PATTERN_BUTTON_ROW_COUNT - 4;
            float f = (float)amount / (float)i;
            this.scrollPosition = MathHelper.clamp(this.scrollPosition - f, 0.0f, 1.0f);
            this.firstPatternButtonId = 1 + (int)(this.scrollPosition * (float)i + 0.5f) * 4;
        }
        return true;
    }

    @Override
    protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
        return mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.backgroundWidth) || mouseY >= (double)(top + this.backgroundHeight);
    }

    private void onInventoryChanged() {
        ItemStack itemStack = ((LoomScreenHandler)this.handler).getOutputSlot().getStack();
        this.bannerPatterns = itemStack.isEmpty() ? null : BannerBlockEntity.getPatternsFromNbt(((BannerItem)itemStack.getItem()).getColor(), BannerBlockEntity.getPatternListNbt(itemStack));
        ItemStack itemStack2 = ((LoomScreenHandler)this.handler).getBannerSlot().getStack();
        ItemStack itemStack3 = ((LoomScreenHandler)this.handler).getDyeSlot().getStack();
        ItemStack itemStack4 = ((LoomScreenHandler)this.handler).getPatternSlot().getStack();
        NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(itemStack2);
        boolean bl = this.hasTooManyPatterns = nbtCompound != null && nbtCompound.contains("Patterns", 9) && !itemStack2.isEmpty() && nbtCompound.getList("Patterns", 10).size() >= 6;
        if (this.hasTooManyPatterns) {
            this.bannerPatterns = null;
        }
        if (!(ItemStack.areEqual(itemStack2, this.banner) && ItemStack.areEqual(itemStack3, this.dye) && ItemStack.areEqual(itemStack4, this.pattern))) {
            this.canApplyDyePattern = !itemStack2.isEmpty() && !itemStack3.isEmpty() && itemStack4.isEmpty() && !this.hasTooManyPatterns;
            this.canApplySpecialPattern = !this.hasTooManyPatterns && !itemStack4.isEmpty() && !itemStack2.isEmpty() && !itemStack3.isEmpty();
        }
        this.banner = itemStack2.copy();
        this.dye = itemStack3.copy();
        this.pattern = itemStack4.copy();
    }
}

