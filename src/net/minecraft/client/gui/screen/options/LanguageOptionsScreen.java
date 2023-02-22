/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen.options;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class LanguageOptionsScreen
extends Screen {
    protected final Screen parent;
    private LanguageSelectionListWidget languageSelectionList;
    private final GameOptions options;
    private final LanguageManager languageManager;
    private OptionButtonWidget forceUnicodeButton;
    private ButtonWidget doneButton;

    public LanguageOptionsScreen(Screen parent, GameOptions options, LanguageManager languageManager) {
        super(new TranslatableText("options.language", new Object[0]));
        this.parent = parent;
        this.options = options;
        this.languageManager = languageManager;
    }

    @Override
    protected void init() {
        this.languageSelectionList = new LanguageSelectionListWidget(this.minecraft);
        this.children.add(this.languageSelectionList);
        this.forceUnicodeButton = this.addButton(new OptionButtonWidget(this.width / 2 - 155, this.height - 38, 150, 20, Option.FORCE_UNICODE_FONT, Option.FORCE_UNICODE_FONT.getDisplayString(this.options), buttonWidget -> {
            Option.FORCE_UNICODE_FONT.set(this.options);
            this.options.write();
            buttonWidget.setMessage(Option.FORCE_UNICODE_FONT.getDisplayString(this.options));
            this.minecraft.onResolutionChanged();
        }));
        this.doneButton = this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 38, 150, 20, I18n.translate("gui.done", new Object[0]), buttonWidget -> {
            LanguageSelectionListWidget.LanguageEntry languageEntry = (LanguageSelectionListWidget.LanguageEntry)this.languageSelectionList.getSelected();
            if (languageEntry != null && !languageEntry.languageDefinition.getCode().equals(this.languageManager.getLanguage().getCode())) {
                this.languageManager.setLanguage(languageEntry.languageDefinition);
                this.options.language = languageEntry.languageDefinition.getCode();
                this.minecraft.reloadResources();
                this.font.setRightToLeft(this.languageManager.isRightToLeft());
                this.doneButton.setMessage(I18n.translate("gui.done", new Object[0]));
                this.forceUnicodeButton.setMessage(Option.FORCE_UNICODE_FONT.getDisplayString(this.options));
                this.options.write();
            }
            this.minecraft.openScreen(this.parent);
        }));
        super.init();
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.languageSelectionList.render(mouseX, mouseY, delta);
        this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 16, 0xFFFFFF);
        this.drawCenteredString(this.font, "(" + I18n.translate("options.languageWarning", new Object[0]) + ")", this.width / 2, this.height - 56, 0x808080);
        super.render(mouseX, mouseY, delta);
    }

    @Environment(value=EnvType.CLIENT)
    class LanguageSelectionListWidget
    extends AlwaysSelectedEntryListWidget<LanguageEntry> {
        public LanguageSelectionListWidget(MinecraftClient client) {
            super(client, LanguageOptionsScreen.this.width, LanguageOptionsScreen.this.height, 32, LanguageOptionsScreen.this.height - 65 + 4, 18);
            for (LanguageDefinition languageDefinition : LanguageOptionsScreen.this.languageManager.getAllLanguages()) {
                LanguageEntry languageEntry = new LanguageEntry(languageDefinition);
                this.addEntry(languageEntry);
                if (!LanguageOptionsScreen.this.languageManager.getLanguage().getCode().equals(languageDefinition.getCode())) continue;
                this.setSelected(languageEntry);
            }
            if (this.getSelected() != null) {
                this.centerScrollOn(this.getSelected());
            }
        }

        @Override
        protected int getScrollbarPosition() {
            return super.getScrollbarPosition() + 20;
        }

        @Override
        public int getRowWidth() {
            return super.getRowWidth() + 50;
        }

        @Override
        public void setSelected(@Nullable LanguageEntry languageEntry) {
            super.setSelected(languageEntry);
            if (languageEntry != null) {
                NarratorManager.INSTANCE.narrate(new TranslatableText("narrator.select", languageEntry.languageDefinition).getString());
            }
        }

        @Override
        protected void renderBackground() {
            LanguageOptionsScreen.this.renderBackground();
        }

        @Override
        protected boolean isFocused() {
            return LanguageOptionsScreen.this.getFocused() == this;
        }

        @Override
        public /* synthetic */ void setSelected(@Nullable EntryListWidget.Entry entry) {
            this.setSelected((LanguageEntry)entry);
        }

        @Environment(value=EnvType.CLIENT)
        public class LanguageEntry
        extends AlwaysSelectedEntryListWidget.Entry<LanguageEntry> {
            private final LanguageDefinition languageDefinition;

            public LanguageEntry(LanguageDefinition languageDefinition) {
                this.languageDefinition = languageDefinition;
            }

            @Override
            public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
                LanguageOptionsScreen.this.font.setRightToLeft(true);
                LanguageSelectionListWidget.this.drawCenteredString(LanguageOptionsScreen.this.font, this.languageDefinition.toString(), LanguageSelectionListWidget.this.width / 2, j + 1, 0xFFFFFF);
                LanguageOptionsScreen.this.font.setRightToLeft(LanguageOptionsScreen.this.languageManager.getLanguage().isRightToLeft());
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (button == 0) {
                    this.onPressed();
                    return true;
                }
                return false;
            }

            private void onPressed() {
                LanguageSelectionListWidget.this.setSelected(this);
            }
        }
    }
}

