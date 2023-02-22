/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.pack;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ExperimentalWarningScreen
extends Screen {
    private static final Text TITLE = Text.translatable("selectWorld.experimental.title");
    private static final Text MESSAGE = Text.translatable("selectWorld.experimental.message");
    private static final Text DETAILS = Text.translatable("selectWorld.experimental.details");
    private static final int field_40446 = 20;
    private final BooleanConsumer callback;
    final Collection<ResourcePackProfile> enabledProfiles;
    private MultilineText message = MultilineText.EMPTY;

    public ExperimentalWarningScreen(Collection<ResourcePackProfile> enabledProfiles, BooleanConsumer callback) {
        super(TITLE);
        this.enabledProfiles = enabledProfiles;
        this.callback = callback;
    }

    @Override
    public Text getNarratedTitle() {
        return ScreenTexts.joinSentences(super.getNarratedTitle(), MESSAGE);
    }

    private int getMessageHeight() {
        return this.message.count() * this.textRenderer.fontHeight;
    }

    private int getTitleY() {
        int i = (this.height - this.getMessageHeight()) / 2;
        return MathHelper.clamp(i - 20 - this.textRenderer.fontHeight, 10, 80);
    }

    @Override
    protected void init() {
        super.init();
        this.message = MultilineText.create(this.textRenderer, (StringVisitable)MESSAGE, this.width - 50);
        int i = MathHelper.clamp(this.getTitleY() + 20 + this.getMessageHeight() + 20, this.height / 6 + 96, this.height - 24);
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.PROCEED, button -> this.callback.accept(true)).dimensions(this.width / 2 - 50 - 105, i, 100, 20).build());
        this.addDrawableChild(ButtonWidget.builder(DETAILS, button -> this.client.setScreen(new DetailsScreen())).dimensions(this.width / 2 - 50, i, 100, 20).build());
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.BACK, button -> this.callback.accept(false)).dimensions(this.width / 2 - 50 + 105, i, 100, 20).build());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        ExperimentalWarningScreen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, this.getTitleY(), 0xFFFFFF);
        this.message.drawCenterWithShadow(matrices, this.width / 2, this.getTitleY() + 20);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        this.callback.accept(false);
    }

    @Environment(value=EnvType.CLIENT)
    class DetailsScreen
    extends Screen {
        private PackListWidget packList;

        DetailsScreen() {
            super(Text.translatable("selectWorld.experimental.details.title"));
        }

        @Override
        public void close() {
            this.client.setScreen(ExperimentalWarningScreen.this);
        }

        @Override
        protected void init() {
            super.init();
            this.addDrawableChild(ButtonWidget.builder(ScreenTexts.BACK, button -> this.close()).dimensions(this.width / 2 - 100, this.height / 4 + 120 + 24, 200, 20).build());
            this.packList = new PackListWidget(this.client, ExperimentalWarningScreen.this.enabledProfiles);
            this.addSelectableChild(this.packList);
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            this.renderBackground(matrices);
            this.packList.render(matrices, mouseX, mouseY, delta);
            DetailsScreen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 10, 0xFFFFFF);
            super.render(matrices, mouseX, mouseY, delta);
        }

        @Environment(value=EnvType.CLIENT)
        class PackListWidget
        extends AlwaysSelectedEntryListWidget<PackListWidgetEntry> {
            public PackListWidget(MinecraftClient client, Collection<ResourcePackProfile> enabledProfiles) {
                super(client, DetailsScreen.this.width, DetailsScreen.this.height, 32, DetailsScreen.this.height - 64, (client.textRenderer.fontHeight + 2) * 3);
                for (ResourcePackProfile resourcePackProfile : enabledProfiles) {
                    String string = FeatureFlags.printMissingFlags(FeatureFlags.VANILLA_FEATURES, resourcePackProfile.getRequestedFeatures());
                    if (string.isEmpty()) continue;
                    MutableText text = Texts.setStyleIfAbsent(resourcePackProfile.getDisplayName().copy(), Style.EMPTY.withBold(true));
                    MutableText text2 = Text.translatable("selectWorld.experimental.details.entry", string);
                    this.addEntry(new PackListWidgetEntry(text, text2, MultilineText.create(DetailsScreen.this.textRenderer, (StringVisitable)text2, this.getRowWidth())));
                }
            }

            @Override
            public int getRowWidth() {
                return this.width * 3 / 4;
            }

            @Override
            public boolean isFocused() {
                return DetailsScreen.this.getFocused() == this;
            }
        }

        @Environment(value=EnvType.CLIENT)
        class PackListWidgetEntry
        extends AlwaysSelectedEntryListWidget.Entry<PackListWidgetEntry> {
            private final Text displayName;
            private final Text details;
            private final MultilineText multilineDetails;

            PackListWidgetEntry(Text displayName, Text details, MultilineText multilineDetails) {
                this.displayName = displayName;
                this.details = details;
                this.multilineDetails = multilineDetails;
            }

            @Override
            public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                DrawableHelper.drawTextWithShadow(matrices, ((DetailsScreen)DetailsScreen.this).client.textRenderer, this.displayName, x, y, 0xFFFFFF);
                this.multilineDetails.drawWithShadow(matrices, x, y + 12, ((DetailsScreen)DetailsScreen.this).textRenderer.fontHeight, 0xFFFFFF);
            }

            @Override
            public Text getNarration() {
                return Text.translatable("narrator.select", ScreenTexts.joinSentences(this.displayName, this.details));
            }
        }
    }
}
