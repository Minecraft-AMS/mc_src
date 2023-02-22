/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParser
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DataResult$PartialResult
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.Lifecycle
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.util.tinyfd.TinyFileDialogs
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screen.world;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.io.BufferedReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.LevelScreenProvider;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.registry.tag.WorldPresetTags;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.integrated.IntegratedServerLoader;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.level.WorldGenSettings;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class MoreOptionsDialog
implements Drawable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Text CUSTOM_TEXT = Text.translatable("generator.custom");
    private static final Text AMPLIFIED_INFO_TEXT = Text.translatable("generator.minecraft.amplified.info");
    private static final Text MAP_FEATURES_INFO_TEXT = Text.translatable("selectWorld.mapFeatures.info");
    private static final Text SELECT_SETTINGS_FILE_TEXT = Text.translatable("selectWorld.import_worldgen_settings.select_file");
    private MultilineText amplifiedInfoText = MultilineText.EMPTY;
    private TextRenderer textRenderer;
    private int parentWidth;
    private TextFieldWidget seedTextField;
    private CyclingButtonWidget<Boolean> mapFeaturesButton;
    private CyclingButtonWidget<Boolean> bonusItemsButton;
    private CyclingButtonWidget<RegistryEntry<WorldPreset>> mapTypeButton;
    private ButtonWidget unchangeableMapTypeButton;
    private ButtonWidget customizeTypeButton;
    private ButtonWidget importSettingsButton;
    private GeneratorOptionsHolder generatorOptionsHolder;
    private Optional<RegistryEntry<WorldPreset>> presetEntry;
    private OptionalLong seed;

    public MoreOptionsDialog(GeneratorOptionsHolder generatorOptionsHolder, Optional<RegistryKey<WorldPreset>> presetKey, OptionalLong seed) {
        this.generatorOptionsHolder = generatorOptionsHolder;
        this.presetEntry = MoreOptionsDialog.createPresetEntry(generatorOptionsHolder, presetKey);
        this.seed = seed;
    }

    private static Optional<RegistryEntry<WorldPreset>> createPresetEntry(GeneratorOptionsHolder generatorOptionsHolder, Optional<RegistryKey<WorldPreset>> presetKey) {
        return presetKey.flatMap(key -> generatorOptionsHolder.getCombinedRegistryManager().get(RegistryKeys.WORLD_PRESET).getEntry((RegistryKey<WorldPreset>)key));
    }

    public void init(CreateWorldScreen parent, MinecraftClient client, TextRenderer textRenderer) {
        this.textRenderer = textRenderer;
        this.parentWidth = parent.width;
        this.seedTextField = new TextFieldWidget(this.textRenderer, this.parentWidth / 2 - 100, 60, 200, 20, Text.translatable("selectWorld.enterSeed"));
        this.seedTextField.setText(MoreOptionsDialog.toString(this.seed));
        this.seedTextField.setChangedListener(seedText -> {
            this.seed = GeneratorOptions.parseSeed(this.seedTextField.getText());
        });
        parent.addSelectableChild(this.seedTextField);
        int i = this.parentWidth / 2 - 155;
        int j = this.parentWidth / 2 + 5;
        this.mapFeaturesButton = parent.addDrawableChild(CyclingButtonWidget.onOffBuilder(this.generatorOptionsHolder.generatorOptions().shouldGenerateStructures()).narration(button -> ScreenTexts.joinSentences(button.getGenericNarrationMessage(), Text.translatable("selectWorld.mapFeatures.info"))).build(i, 100, 150, 20, Text.translatable("selectWorld.mapFeatures"), (button, structures) -> this.apply(generatorOptions -> generatorOptions.withStructures((boolean)structures))));
        this.mapFeaturesButton.visible = false;
        Registry<WorldPreset> registry = this.generatorOptionsHolder.getCombinedRegistryManager().get(RegistryKeys.WORLD_PRESET);
        List list = MoreOptionsDialog.collectPresets(registry, WorldPresetTags.NORMAL).orElseGet(() -> registry.streamEntries().collect(Collectors.toUnmodifiableList()));
        List<RegistryEntry<WorldPreset>> list2 = MoreOptionsDialog.collectPresets(registry, WorldPresetTags.EXTENDED).orElse(list);
        this.mapTypeButton = parent.addDrawableChild(CyclingButtonWidget.builder(MoreOptionsDialog::getText).values(list, list2).narration(button -> {
            if (MoreOptionsDialog.isAmplified((RegistryEntry)button.getValue())) {
                return ScreenTexts.joinSentences(button.getGenericNarrationMessage(), AMPLIFIED_INFO_TEXT);
            }
            return button.getGenericNarrationMessage();
        }).build(j, 100, 150, 20, Text.translatable("selectWorld.mapType"), (button, presetEntry) -> {
            this.presetEntry = Optional.of(presetEntry);
            this.apply((dynamicRegistryManager, dimensionsRegistryHolder) -> ((WorldPreset)presetEntry.value()).createDimensionsRegistryHolder());
            parent.setMoreOptionsOpen();
        }));
        this.presetEntry.ifPresent(this.mapTypeButton::setValue);
        this.mapTypeButton.visible = false;
        this.unchangeableMapTypeButton = parent.addDrawableChild(ButtonWidget.builder(ScreenTexts.composeGenericOptionText(Text.translatable("selectWorld.mapType"), CUSTOM_TEXT), button -> {}).dimensions(j, 100, 150, 20).build());
        this.unchangeableMapTypeButton.active = false;
        this.unchangeableMapTypeButton.visible = false;
        this.customizeTypeButton = parent.addDrawableChild(ButtonWidget.builder(Text.translatable("selectWorld.customizeType"), button -> {
            LevelScreenProvider levelScreenProvider = LevelScreenProvider.WORLD_PRESET_TO_SCREEN_PROVIDER.get(this.presetEntry.flatMap(RegistryEntry::getKey));
            if (levelScreenProvider != null) {
                client.setScreen(levelScreenProvider.createEditScreen(parent, this.generatorOptionsHolder));
            }
        }).dimensions(j, 120, 150, 20).build());
        this.customizeTypeButton.visible = false;
        this.bonusItemsButton = parent.addDrawableChild(CyclingButtonWidget.onOffBuilder(this.generatorOptionsHolder.generatorOptions().hasBonusChest() && !parent.hardcore).build(i, 151, 150, 20, Text.translatable("selectWorld.bonusItems"), (button, bonusChest) -> this.apply(generationOptions -> generationOptions.withBonusChest((boolean)bonusChest))));
        this.bonusItemsButton.visible = false;
        this.importSettingsButton = parent.addDrawableChild(ButtonWidget.builder(Text.translatable("selectWorld.import_worldgen_settings"), button -> {
            DataResult dataResult;
            String string = TinyFileDialogs.tinyfd_openFileDialog((CharSequence)SELECT_SETTINGS_FILE_TEXT.getString(), null, null, null, (boolean)false);
            if (string == null) {
                return;
            }
            RegistryOps dynamicOps = RegistryOps.of(JsonOps.INSTANCE, this.generatorOptionsHolder.getCombinedRegistryManager());
            try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(string, new String[0]));){
                JsonElement jsonElement = JsonParser.parseReader((Reader)bufferedReader);
                dataResult = WorldGenSettings.CODEC.parse(dynamicOps, (Object)jsonElement);
            }
            catch (Exception exception) {
                dataResult = DataResult.error((String)("Failed to parse file: " + exception.getMessage()));
            }
            if (dataResult.error().isPresent()) {
                MutableText text = Text.translatable("selectWorld.import_worldgen_settings.failure");
                String string2 = ((DataResult.PartialResult)dataResult.error().get()).message();
                LOGGER.error("Error parsing world settings: {}", (Object)string2);
                MutableText text2 = Text.literal(string2);
                client.getToastManager().add(SystemToast.create(client, SystemToast.Type.WORLD_GEN_SETTINGS_TRANSFER, text, text2));
                return;
            }
            Lifecycle lifecycle = dataResult.lifecycle();
            dataResult.resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent(worldGenSettings -> IntegratedServerLoader.tryLoad(client, parent, lifecycle, () -> this.importOptions(worldGenSettings.generatorOptions(), worldGenSettings.dimensionOptionsRegistryHolder())));
        }).dimensions(i, 185, 150, 20).build());
        this.importSettingsButton.visible = false;
        this.amplifiedInfoText = MultilineText.create(textRenderer, (StringVisitable)AMPLIFIED_INFO_TEXT, this.mapTypeButton.getWidth());
    }

    private static Optional<List<RegistryEntry<WorldPreset>>> collectPresets(Registry<WorldPreset> presetRegistry, TagKey<WorldPreset> tag) {
        return presetRegistry.getEntryList(tag).map(entryList -> entryList.stream().toList()).filter(entries -> !entries.isEmpty());
    }

    private static boolean isAmplified(RegistryEntry<WorldPreset> presetEntry) {
        return presetEntry.getKey().filter(key -> key.equals(WorldPresets.AMPLIFIED)).isPresent();
    }

    private static Text getText(RegistryEntry<WorldPreset> presetEntry) {
        return presetEntry.getKey().map(key -> Text.translatable(key.getValue().toTranslationKey("generator"))).orElse(CUSTOM_TEXT);
    }

    private void importOptions(GeneratorOptions generatorOptions, DimensionOptionsRegistryHolder dimensionsRegistryHolder) {
        this.generatorOptionsHolder = this.generatorOptionsHolder.with(generatorOptions, dimensionsRegistryHolder);
        this.presetEntry = MoreOptionsDialog.createPresetEntry(this.generatorOptionsHolder, WorldPresets.getWorldPreset(dimensionsRegistryHolder.dimensions()));
        this.setMapTypeButtonVisible(true);
        this.seed = OptionalLong.of(generatorOptions.getSeed());
        this.seedTextField.setText(MoreOptionsDialog.toString(this.seed));
    }

    public void tickSeedTextField() {
        this.seedTextField.tick();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.mapFeaturesButton.visible) {
            this.textRenderer.drawWithShadow(matrices, MAP_FEATURES_INFO_TEXT, (float)(this.parentWidth / 2 - 150), 122.0f, -6250336);
        }
        this.seedTextField.render(matrices, mouseX, mouseY, delta);
        if (this.presetEntry.filter(MoreOptionsDialog::isAmplified).isPresent()) {
            this.amplifiedInfoText.drawWithShadow(matrices, this.mapTypeButton.getX() + 2, this.mapTypeButton.getY() + 22, this.textRenderer.fontHeight, 0xA0A0A0);
        }
    }

    void apply(GeneratorOptionsHolder.RegistryAwareModifier modifier) {
        this.generatorOptionsHolder = this.generatorOptionsHolder.apply(modifier);
    }

    private void apply(GeneratorOptionsHolder.Modifier modifier) {
        this.generatorOptionsHolder = this.generatorOptionsHolder.apply(modifier);
    }

    void setGeneratorOptionsHolder(GeneratorOptionsHolder generatorOptionsHolder) {
        this.generatorOptionsHolder = generatorOptionsHolder;
    }

    private static String toString(OptionalLong seed) {
        if (seed.isPresent()) {
            return Long.toString(seed.getAsLong());
        }
        return "";
    }

    public GeneratorOptions getGeneratorOptionsHolder(boolean debug, boolean hardcore) {
        OptionalLong optionalLong = GeneratorOptions.parseSeed(this.seedTextField.getText());
        GeneratorOptions generatorOptions = this.generatorOptionsHolder.generatorOptions();
        if (debug || hardcore) {
            generatorOptions = generatorOptions.withBonusChest(false);
        }
        if (debug) {
            generatorOptions = generatorOptions.withStructures(false);
        }
        return generatorOptions.withSeed(optionalLong);
    }

    public boolean isDebugWorld() {
        return this.generatorOptionsHolder.selectedDimensions().isDebug();
    }

    public void setVisible(boolean visible) {
        this.setMapTypeButtonVisible(visible);
        if (this.isDebugWorld()) {
            this.mapFeaturesButton.visible = false;
            this.bonusItemsButton.visible = false;
            this.customizeTypeButton.visible = false;
            this.importSettingsButton.visible = false;
        } else {
            this.mapFeaturesButton.visible = visible;
            this.bonusItemsButton.visible = visible;
            this.customizeTypeButton.visible = visible && LevelScreenProvider.WORLD_PRESET_TO_SCREEN_PROVIDER.containsKey(this.presetEntry.flatMap(RegistryEntry::getKey));
            this.importSettingsButton.visible = visible;
        }
        this.seedTextField.setVisible(visible);
    }

    private void setMapTypeButtonVisible(boolean visible) {
        if (this.presetEntry.isPresent()) {
            this.mapTypeButton.visible = visible;
            this.unchangeableMapTypeButton.visible = false;
        } else {
            this.mapTypeButton.visible = false;
            this.unchangeableMapTypeButton.visible = visible;
        }
    }

    public GeneratorOptionsHolder getGeneratorOptionsHolder() {
        return this.generatorOptionsHolder;
    }

    public DynamicRegistryManager getRegistryManager() {
        return this.generatorOptionsHolder.getCombinedRegistryManager();
    }

    public void disableBonusItems() {
        this.bonusItemsButton.active = false;
        this.bonusItemsButton.setValue(false);
    }

    public void enableBonusItems() {
        this.bonusItemsButton.active = true;
        this.bonusItemsButton.setValue(this.generatorOptionsHolder.generatorOptions().hasBonusChest());
    }
}

