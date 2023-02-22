/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ibm.icu.text.Collator
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen;

import com.ibm.icu.text.Collator;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CustomizeBuffetLevelScreen
extends Screen {
    private static final Text BUFFET_BIOME_TEXT = new TranslatableText("createWorld.customize.buffet.biome");
    private final Screen parent;
    private final Consumer<RegistryEntry<Biome>> onDone;
    final Registry<Biome> biomeRegistry;
    private BuffetBiomesListWidget biomeSelectionList;
    RegistryEntry<Biome> biome;
    private ButtonWidget confirmButton;

    public CustomizeBuffetLevelScreen(Screen parent, DynamicRegistryManager registryManager, Consumer<RegistryEntry<Biome>> onDone, RegistryEntry<Biome> registryEntry) {
        super(new TranslatableText("createWorld.customize.buffet.title"));
        this.parent = parent;
        this.onDone = onDone;
        this.biome = registryEntry;
        this.biomeRegistry = registryManager.get(Registry.BIOME_KEY);
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Override
    protected void init() {
        this.client.keyboard.setRepeatEvents(true);
        this.biomeSelectionList = new BuffetBiomesListWidget();
        this.addSelectableChild(this.biomeSelectionList);
        this.confirmButton = this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, ScreenTexts.DONE, button -> {
            this.onDone.accept(this.biome);
            this.client.setScreen(this.parent);
        }));
        this.addDrawableChild(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, ScreenTexts.CANCEL, button -> this.client.setScreen(this.parent)));
        this.biomeSelectionList.setSelected((BuffetBiomesListWidget.BuffetBiomeItem)this.biomeSelectionList.children().stream().filter(entry -> Objects.equals(entry.biome, this.biome)).findFirst().orElse(null));
    }

    void refreshConfirmButton() {
        this.confirmButton.active = this.biomeSelectionList.getSelectedOrNull() != null;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(0);
        this.biomeSelectionList.render(matrices, mouseX, mouseY, delta);
        CustomizeBuffetLevelScreen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
        CustomizeBuffetLevelScreen.drawCenteredText(matrices, this.textRenderer, BUFFET_BIOME_TEXT, this.width / 2, 28, 0xA0A0A0);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Environment(value=EnvType.CLIENT)
    class BuffetBiomesListWidget
    extends AlwaysSelectedEntryListWidget<BuffetBiomeItem> {
        BuffetBiomesListWidget() {
            super(CustomizeBuffetLevelScreen.this.client, CustomizeBuffetLevelScreen.this.width, CustomizeBuffetLevelScreen.this.height, 40, CustomizeBuffetLevelScreen.this.height - 37, 16);
            Collator collator = Collator.getInstance((Locale)Locale.getDefault());
            CustomizeBuffetLevelScreen.this.biomeRegistry.streamEntries().map(reference -> new BuffetBiomeItem((RegistryEntry.Reference<Biome>)reference)).sorted(Comparator.comparing(biome -> biome.text.getString(), collator)).forEach(entry -> this.addEntry(entry));
        }

        @Override
        protected boolean isFocused() {
            return CustomizeBuffetLevelScreen.this.getFocused() == this;
        }

        @Override
        public void setSelected(@Nullable BuffetBiomeItem buffetBiomeItem) {
            super.setSelected(buffetBiomeItem);
            if (buffetBiomeItem != null) {
                CustomizeBuffetLevelScreen.this.biome = buffetBiomeItem.biome;
            }
            CustomizeBuffetLevelScreen.this.refreshConfirmButton();
        }

        @Environment(value=EnvType.CLIENT)
        class BuffetBiomeItem
        extends AlwaysSelectedEntryListWidget.Entry<BuffetBiomeItem> {
            final RegistryEntry.Reference<Biome> biome;
            final Text text;

            public BuffetBiomeItem(RegistryEntry.Reference<Biome> reference) {
                this.biome = reference;
                Identifier identifier = reference.registryKey().getValue();
                String string = "biome." + identifier.getNamespace() + "." + identifier.getPath();
                this.text = Language.getInstance().hasTranslation(string) ? new TranslatableText(string) : new LiteralText(identifier.toString());
            }

            @Override
            public Text getNarration() {
                return new TranslatableText("narrator.select", this.text);
            }

            @Override
            public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                DrawableHelper.drawTextWithShadow(matrices, CustomizeBuffetLevelScreen.this.textRenderer, this.text, x + 5, y + 2, 0xFFFFFF);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (button == 0) {
                    BuffetBiomesListWidget.this.setSelected(this);
                    return true;
                }
                return false;
            }
        }
    }
}

