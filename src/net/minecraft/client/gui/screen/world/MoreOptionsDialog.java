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
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
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
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.BufferedReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.GeneratorType;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.LifecycledResourceManagerImpl;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.SaveLoader;
import net.minecraft.text.LiteralText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.gen.GeneratorOptions;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class MoreOptionsDialog
implements Drawable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Text CUSTOM_TEXT = new TranslatableText("generator.custom");
    private static final Text AMPLIFIED_INFO_TEXT = new TranslatableText("generator.amplified.info");
    private static final Text MAP_FEATURES_INFO_TEXT = new TranslatableText("selectWorld.mapFeatures.info");
    private static final Text SELECT_SETTINGS_FILE_TEXT = new TranslatableText("selectWorld.import_worldgen_settings.select_file");
    private MultilineText amplifiedInfoText = MultilineText.EMPTY;
    private TextRenderer textRenderer;
    private int parentWidth;
    private TextFieldWidget seedTextField;
    private CyclingButtonWidget<Boolean> mapFeaturesButton;
    private CyclingButtonWidget<Boolean> bonusItemsButton;
    private CyclingButtonWidget<GeneratorType> mapTypeButton;
    private ButtonWidget unchangeableMapTypeButton;
    private ButtonWidget customizeTypeButton;
    private ButtonWidget importSettingsButton;
    private DynamicRegistryManager.Immutable registryManager;
    private GeneratorOptions generatorOptions;
    private Optional<GeneratorType> generatorType;
    private OptionalLong seed;

    public MoreOptionsDialog(DynamicRegistryManager.Immutable registryManager, GeneratorOptions generatorOptions, Optional<GeneratorType> generatorType, OptionalLong seed) {
        this.registryManager = registryManager;
        this.generatorOptions = generatorOptions;
        this.generatorType = generatorType;
        this.seed = seed;
    }

    public void init(CreateWorldScreen parent, MinecraftClient client, TextRenderer textRenderer) {
        this.textRenderer = textRenderer;
        this.parentWidth = parent.width;
        this.seedTextField = new TextFieldWidget(this.textRenderer, this.parentWidth / 2 - 100, 60, 200, 20, new TranslatableText("selectWorld.enterSeed"));
        this.seedTextField.setText(MoreOptionsDialog.seedToString(this.seed));
        this.seedTextField.setChangedListener(seedText -> {
            this.seed = GeneratorOptions.parseSeed(this.seedTextField.getText());
        });
        parent.addSelectableChild(this.seedTextField);
        int i = this.parentWidth / 2 - 155;
        int j = this.parentWidth / 2 + 5;
        this.mapFeaturesButton = parent.addDrawableChild(CyclingButtonWidget.onOffBuilder(this.generatorOptions.shouldGenerateStructures()).narration(button -> ScreenTexts.joinSentences(button.getGenericNarrationMessage(), new TranslatableText("selectWorld.mapFeatures.info"))).build(i, 100, 150, 20, new TranslatableText("selectWorld.mapFeatures"), (button, generateStructures) -> {
            this.generatorOptions = this.generatorOptions.toggleGenerateStructures();
        }));
        this.mapFeaturesButton.visible = false;
        this.mapTypeButton = parent.addDrawableChild(CyclingButtonWidget.builder(GeneratorType::getDisplayName).values(GeneratorType.VALUES.stream().filter(GeneratorType::isNotDebug).collect(Collectors.toList()), GeneratorType.VALUES).narration(button -> {
            if (button.getValue() == GeneratorType.AMPLIFIED) {
                return ScreenTexts.joinSentences(button.getGenericNarrationMessage(), AMPLIFIED_INFO_TEXT);
            }
            return button.getGenericNarrationMessage();
        }).build(j, 100, 150, 20, new TranslatableText("selectWorld.mapType"), (button, generatorType) -> {
            this.generatorType = Optional.of(generatorType);
            this.generatorOptions = generatorType.createDefaultOptions(this.registryManager, this.generatorOptions.getSeed(), this.generatorOptions.shouldGenerateStructures(), this.generatorOptions.hasBonusChest());
            parent.setMoreOptionsOpen();
        }));
        this.generatorType.ifPresent(this.mapTypeButton::setValue);
        this.mapTypeButton.visible = false;
        this.unchangeableMapTypeButton = parent.addDrawableChild(new ButtonWidget(j, 100, 150, 20, ScreenTexts.composeGenericOptionText(new TranslatableText("selectWorld.mapType"), CUSTOM_TEXT), button -> {}));
        this.unchangeableMapTypeButton.active = false;
        this.unchangeableMapTypeButton.visible = false;
        this.customizeTypeButton = parent.addDrawableChild(new ButtonWidget(j, 120, 150, 20, new TranslatableText("selectWorld.customizeType"), button -> {
            GeneratorType.ScreenProvider screenProvider = GeneratorType.SCREEN_PROVIDERS.get(this.generatorType);
            if (screenProvider != null) {
                client.setScreen(screenProvider.createEditScreen(parent, this.generatorOptions));
            }
        }));
        this.customizeTypeButton.visible = false;
        this.bonusItemsButton = parent.addDrawableChild(CyclingButtonWidget.onOffBuilder(this.generatorOptions.hasBonusChest() && !parent.hardcore).build(i, 151, 150, 20, new TranslatableText("selectWorld.bonusItems"), (button, bonusChest) -> {
            this.generatorOptions = this.generatorOptions.toggleBonusChest();
        }));
        this.bonusItemsButton.visible = false;
        this.importSettingsButton = parent.addDrawableChild(new ButtonWidget(i, 185, 150, 20, new TranslatableText("selectWorld.import_worldgen_settings"), button -> {
            DataResult dataResult;
            String string = TinyFileDialogs.tinyfd_openFileDialog((CharSequence)SELECT_SETTINGS_FILE_TEXT.getString(), null, null, null, (boolean)false);
            if (string == null) {
                return;
            }
            DynamicRegistryManager.Mutable mutable = DynamicRegistryManager.createAndLoad();
            try (ResourcePackManager resourcePackManager = new ResourcePackManager(ResourceType.SERVER_DATA, new VanillaDataPackProvider(), new FileResourcePackProvider(parent.getDataPackTempDir().toFile(), ResourcePackSource.PACK_SOURCE_WORLD));){
                MinecraftServer.loadDataPacks(resourcePackManager, createWorldScreen.dataPackSettings, false);
                try (LifecycledResourceManagerImpl lifecycledResourceManager = new LifecycledResourceManagerImpl(ResourceType.SERVER_DATA, resourcePackManager.createResourcePacks());){
                    RegistryOps dynamicOps = RegistryOps.ofLoaded(JsonOps.INSTANCE, mutable, lifecycledResourceManager);
                    try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(string, new String[0]));){
                        JsonElement jsonElement = JsonParser.parseReader((Reader)bufferedReader);
                        dataResult = GeneratorOptions.CODEC.parse(dynamicOps, (Object)jsonElement);
                    }
                    catch (Exception exception) {
                        dataResult = DataResult.error((String)("Failed to parse file: " + exception.getMessage()));
                    }
                    if (dataResult.error().isPresent()) {
                        TranslatableText text = new TranslatableText("selectWorld.import_worldgen_settings.failure");
                        String string2 = ((DataResult.PartialResult)dataResult.error().get()).message();
                        LOGGER.error("Error parsing world settings: {}", (Object)string2);
                        LiteralText text2 = new LiteralText(string2);
                        client.getToastManager().add(SystemToast.create(client, SystemToast.Type.WORLD_GEN_SETTINGS_TRANSFER, text, text2));
                        return;
                    }
                }
            }
            Lifecycle lifecycle = dataResult.lifecycle();
            dataResult.resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent(generatorOptions -> {
                BooleanConsumer booleanConsumer = confirmed -> {
                    client.setScreen(parent);
                    if (confirmed) {
                        this.importOptions(mutable.toImmutable(), (GeneratorOptions)generatorOptions);
                    }
                };
                if (lifecycle == Lifecycle.stable()) {
                    this.importOptions(mutable.toImmutable(), (GeneratorOptions)generatorOptions);
                } else if (lifecycle == Lifecycle.experimental()) {
                    client.setScreen(new ConfirmScreen(booleanConsumer, new TranslatableText("selectWorld.import_worldgen_settings.experimental.title"), new TranslatableText("selectWorld.import_worldgen_settings.experimental.question")));
                } else {
                    client.setScreen(new ConfirmScreen(booleanConsumer, new TranslatableText("selectWorld.import_worldgen_settings.deprecated.title"), new TranslatableText("selectWorld.import_worldgen_settings.deprecated.question")));
                }
            });
        }));
        this.importSettingsButton.visible = false;
        this.amplifiedInfoText = MultilineText.create(textRenderer, (StringVisitable)AMPLIFIED_INFO_TEXT, this.mapTypeButton.getWidth());
    }

    private void importOptions(DynamicRegistryManager.Immutable registryManager, GeneratorOptions generatorOptions) {
        this.registryManager = registryManager;
        this.generatorOptions = generatorOptions;
        this.generatorType = GeneratorType.fromGeneratorOptions(generatorOptions);
        this.setMapTypeButtonVisible(true);
        this.seed = OptionalLong.of(generatorOptions.getSeed());
        this.seedTextField.setText(MoreOptionsDialog.seedToString(this.seed));
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
        if (this.generatorType.equals(Optional.of(GeneratorType.AMPLIFIED))) {
            this.amplifiedInfoText.drawWithShadow(matrices, this.mapTypeButton.x + 2, this.mapTypeButton.y + 22, this.textRenderer.fontHeight, 0xA0A0A0);
        }
    }

    protected void setGeneratorOptions(GeneratorOptions generatorOptions) {
        this.generatorOptions = generatorOptions;
    }

    private static String seedToString(OptionalLong seed) {
        if (seed.isPresent()) {
            return Long.toString(seed.getAsLong());
        }
        return "";
    }

    public GeneratorOptions getGeneratorOptions(boolean hardcore) {
        OptionalLong optionalLong = GeneratorOptions.parseSeed(this.seedTextField.getText());
        return this.generatorOptions.withHardcore(hardcore, optionalLong);
    }

    public boolean isDebugWorld() {
        return this.generatorOptions.isDebugWorld();
    }

    public void setVisible(boolean visible) {
        this.setMapTypeButtonVisible(visible);
        if (this.generatorOptions.isDebugWorld()) {
            this.mapFeaturesButton.visible = false;
            this.bonusItemsButton.visible = false;
            this.customizeTypeButton.visible = false;
            this.importSettingsButton.visible = false;
        } else {
            this.mapFeaturesButton.visible = visible;
            this.bonusItemsButton.visible = visible;
            this.customizeTypeButton.visible = visible && GeneratorType.SCREEN_PROVIDERS.containsKey(this.generatorType);
            this.importSettingsButton.visible = visible;
        }
        this.seedTextField.setVisible(visible);
    }

    private void setMapTypeButtonVisible(boolean visible) {
        if (this.generatorType.isPresent()) {
            this.mapTypeButton.visible = visible;
            this.unchangeableMapTypeButton.visible = false;
        } else {
            this.mapTypeButton.visible = false;
            this.unchangeableMapTypeButton.visible = visible;
        }
    }

    public DynamicRegistryManager getRegistryManager() {
        return this.registryManager;
    }

    void loadDatapacks(SaveLoader saveLoader) {
        this.generatorOptions = saveLoader.saveProperties().getGeneratorOptions();
        this.registryManager = saveLoader.dynamicRegistryManager();
    }

    public void disableBonusItems() {
        this.bonusItemsButton.active = false;
        this.bonusItemsButton.setValue(false);
    }

    public void enableBonusItems() {
        this.bonusItemsButton.active = true;
        this.bonusItemsButton.setValue(this.generatorOptions.hasBonusChest());
    }
}

