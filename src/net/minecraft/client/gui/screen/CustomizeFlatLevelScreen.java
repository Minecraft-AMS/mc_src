/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.PresetsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CustomizeFlatLevelScreen
extends Screen {
    private static final int ICON_TEXTURE_SIZE = 128;
    private static final int ICON_SIZE = 18;
    private static final int BUTTON_HEIGHT = 20;
    private static final int ICON_BACKGROUND_OFFSET_X = 1;
    private static final int ICON_BACKGROUND_OFFSET_Y = 1;
    private static final int ICON_OFFSET_X = 2;
    private static final int ICON_OFFSET_Y = 2;
    protected final CreateWorldScreen parent;
    private final Consumer<FlatChunkGeneratorConfig> configConsumer;
    FlatChunkGeneratorConfig config;
    private Text tileText;
    private Text heightText;
    private SuperflatLayersListWidget layers;
    private ButtonWidget widgetButtonRemoveLayer;

    public CustomizeFlatLevelScreen(CreateWorldScreen parent, Consumer<FlatChunkGeneratorConfig> configConsumer, FlatChunkGeneratorConfig config) {
        super(new TranslatableText("createWorld.customize.flat.title"));
        this.parent = parent;
        this.configConsumer = configConsumer;
        this.config = config;
    }

    public FlatChunkGeneratorConfig getConfig() {
        return this.config;
    }

    public void setConfig(FlatChunkGeneratorConfig config) {
        this.config = config;
    }

    @Override
    protected void init() {
        this.tileText = new TranslatableText("createWorld.customize.flat.tile");
        this.heightText = new TranslatableText("createWorld.customize.flat.height");
        this.layers = new SuperflatLayersListWidget();
        this.addSelectableChild(this.layers);
        this.widgetButtonRemoveLayer = this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, this.height - 52, 150, 20, new TranslatableText("createWorld.customize.flat.removeLayer"), button -> {
            if (!this.hasLayerSelected()) {
                return;
            }
            List<FlatChunkGeneratorLayer> list = this.config.getLayers();
            int i = this.layers.children().indexOf(this.layers.getSelectedOrNull());
            int j = list.size() - i - 1;
            list.remove(j);
            this.layers.setSelected(list.isEmpty() ? null : (SuperflatLayersListWidget.SuperflatLayerEntry)this.layers.children().get(Math.min(i, list.size() - 1)));
            this.config.updateLayerBlocks();
            this.layers.updateLayers();
            this.updateRemoveLayerButton();
        }));
        this.addDrawableChild(new ButtonWidget(this.width / 2 + 5, this.height - 52, 150, 20, new TranslatableText("createWorld.customize.presets"), button -> {
            this.client.setScreen(new PresetsScreen(this));
            this.config.updateLayerBlocks();
            this.updateRemoveLayerButton();
        }));
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, ScreenTexts.DONE, button -> {
            this.configConsumer.accept(this.config);
            this.client.setScreen(this.parent);
            this.config.updateLayerBlocks();
        }));
        this.addDrawableChild(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, ScreenTexts.CANCEL, button -> {
            this.client.setScreen(this.parent);
            this.config.updateLayerBlocks();
        }));
        this.config.updateLayerBlocks();
        this.updateRemoveLayerButton();
    }

    void updateRemoveLayerButton() {
        this.widgetButtonRemoveLayer.active = this.hasLayerSelected();
    }

    private boolean hasLayerSelected() {
        return this.layers.getSelectedOrNull() != null;
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.layers.render(matrices, mouseX, mouseY, delta);
        CustomizeFlatLevelScreen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
        int i = this.width / 2 - 92 - 16;
        CustomizeFlatLevelScreen.drawTextWithShadow(matrices, this.textRenderer, this.tileText, i, 32, 0xFFFFFF);
        CustomizeFlatLevelScreen.drawTextWithShadow(matrices, this.textRenderer, this.heightText, i + 2 + 213 - this.textRenderer.getWidth(this.heightText), 32, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Environment(value=EnvType.CLIENT)
    class SuperflatLayersListWidget
    extends AlwaysSelectedEntryListWidget<SuperflatLayerEntry> {
        public SuperflatLayersListWidget() {
            super(CustomizeFlatLevelScreen.this.client, CustomizeFlatLevelScreen.this.width, CustomizeFlatLevelScreen.this.height, 43, CustomizeFlatLevelScreen.this.height - 60, 24);
            for (int i = 0; i < CustomizeFlatLevelScreen.this.config.getLayers().size(); ++i) {
                this.addEntry(new SuperflatLayerEntry());
            }
        }

        @Override
        public void setSelected(@Nullable SuperflatLayerEntry superflatLayerEntry) {
            super.setSelected(superflatLayerEntry);
            CustomizeFlatLevelScreen.this.updateRemoveLayerButton();
        }

        @Override
        protected boolean isFocused() {
            return CustomizeFlatLevelScreen.this.getFocused() == this;
        }

        @Override
        protected int getScrollbarPositionX() {
            return this.width - 70;
        }

        public void updateLayers() {
            int i = this.children().indexOf(this.getSelectedOrNull());
            this.clearEntries();
            for (int j = 0; j < CustomizeFlatLevelScreen.this.config.getLayers().size(); ++j) {
                this.addEntry(new SuperflatLayerEntry());
            }
            List list = this.children();
            if (i >= 0 && i < list.size()) {
                this.setSelected((SuperflatLayerEntry)list.get(i));
            }
        }

        @Environment(value=EnvType.CLIENT)
        class SuperflatLayerEntry
        extends AlwaysSelectedEntryListWidget.Entry<SuperflatLayerEntry> {
            SuperflatLayerEntry() {
            }

            @Override
            public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                FlatChunkGeneratorLayer flatChunkGeneratorLayer = CustomizeFlatLevelScreen.this.config.getLayers().get(CustomizeFlatLevelScreen.this.config.getLayers().size() - index - 1);
                BlockState blockState = flatChunkGeneratorLayer.getBlockState();
                ItemStack itemStack = this.createItemStackFor(blockState);
                this.renderIcon(matrices, x, y, itemStack);
                CustomizeFlatLevelScreen.this.textRenderer.draw(matrices, itemStack.getName(), (float)(x + 18 + 5), (float)(y + 3), 0xFFFFFF);
                TranslatableText text = index == 0 ? new TranslatableText("createWorld.customize.flat.layer.top", flatChunkGeneratorLayer.getThickness()) : (index == CustomizeFlatLevelScreen.this.config.getLayers().size() - 1 ? new TranslatableText("createWorld.customize.flat.layer.bottom", flatChunkGeneratorLayer.getThickness()) : new TranslatableText("createWorld.customize.flat.layer", flatChunkGeneratorLayer.getThickness()));
                CustomizeFlatLevelScreen.this.textRenderer.draw(matrices, text, (float)(x + 2 + 213 - CustomizeFlatLevelScreen.this.textRenderer.getWidth(text)), (float)(y + 3), 0xFFFFFF);
            }

            private ItemStack createItemStackFor(BlockState state) {
                Item item = state.getBlock().asItem();
                if (item == Items.AIR) {
                    if (state.isOf(Blocks.WATER)) {
                        item = Items.WATER_BUCKET;
                    } else if (state.isOf(Blocks.LAVA)) {
                        item = Items.LAVA_BUCKET;
                    }
                }
                return new ItemStack(item);
            }

            @Override
            public Text getNarration() {
                FlatChunkGeneratorLayer flatChunkGeneratorLayer = CustomizeFlatLevelScreen.this.config.getLayers().get(CustomizeFlatLevelScreen.this.config.getLayers().size() - SuperflatLayersListWidget.this.children().indexOf(this) - 1);
                ItemStack itemStack = this.createItemStackFor(flatChunkGeneratorLayer.getBlockState());
                if (!itemStack.isEmpty()) {
                    return new TranslatableText("narrator.select", itemStack.getName());
                }
                return LiteralText.EMPTY;
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (button == 0) {
                    SuperflatLayersListWidget.this.setSelected(this);
                    return true;
                }
                return false;
            }

            private void renderIcon(MatrixStack matrices, int x, int y, ItemStack iconItem) {
                this.renderIconBackgroundTexture(matrices, x + 1, y + 1);
                if (!iconItem.isEmpty()) {
                    CustomizeFlatLevelScreen.this.itemRenderer.renderGuiItemIcon(iconItem, x + 2, y + 2);
                }
            }

            private void renderIconBackgroundTexture(MatrixStack matrices, int x, int y) {
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                RenderSystem.setShaderTexture(0, DrawableHelper.STATS_ICON_TEXTURE);
                DrawableHelper.drawTexture(matrices, x, y, CustomizeFlatLevelScreen.this.getZOffset(), 0.0f, 0.0f, 18, 18, 128, 128);
            }
        }
    }
}

