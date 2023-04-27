/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.Lifecycle
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.mutable.MutableObject
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screen.world;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.ExperimentalWarningScreen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.client.gui.screen.world.ExperimentsScreen;
import net.minecraft.client.gui.screen.world.LevelScreenProvider;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.client.gui.screen.world.WorldScreenOptionGrid;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TabNavigationWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.SaveLoading;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.integrated.IntegratedServerLoader;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.PathUtil;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.WorldGenSettings;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class CreateWorldScreen
extends Screen {
    private static final int field_42165 = 1;
    private static final int field_42166 = 210;
    private static final int field_42167 = 36;
    private static final int field_42168 = 1;
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String TEMP_DIR_PREFIX = "mcworld-";
    static final Text GAME_MODE_TEXT = Text.translatable("selectWorld.gameMode");
    static final Text ENTER_NAME_TEXT = Text.translatable("selectWorld.enterName");
    static final Text EXPERIMENTS_TEXT = Text.translatable("selectWorld.experiments");
    static final Text ALLOW_COMMANDS_INFO_TEXT = Text.translatable("selectWorld.allowCommands.info");
    private static final Text PREPARING_TEXT = Text.translatable("createWorld.preparing");
    private static final int field_42170 = 10;
    private static final int field_42171 = 8;
    public static final Identifier HEADER_SEPARATOR_TEXTURE = new Identifier("textures/gui/header_separator.png");
    public static final Identifier FOOTER_SEPARATOR_TEXTURE = new Identifier("textures/gui/footer_separator.png");
    final WorldCreator worldCreator;
    private final TabManager tabManager = new TabManager(this::addDrawableChild, child -> this.remove((Element)child));
    private boolean recreated;
    @Nullable
    private final Screen parent;
    @Nullable
    private Path dataPackTempDir;
    @Nullable
    private ResourcePackManager packManager;
    @Nullable
    private GridWidget grid;
    @Nullable
    private TabNavigationWidget tabNavigation;

    public static void create(MinecraftClient client, @Nullable Screen parent) {
        CreateWorldScreen.showMessage(client, PREPARING_TEXT);
        ResourcePackManager resourcePackManager = new ResourcePackManager(new VanillaDataPackProvider());
        SaveLoading.ServerConfig serverConfig = CreateWorldScreen.createServerConfig(resourcePackManager, DataConfiguration.SAFE_MODE);
        CompletableFuture<GeneratorOptionsHolder> completableFuture = SaveLoading.load(serverConfig, context -> new SaveLoading.LoadContext<WorldCreationSettings>(new WorldCreationSettings(new WorldGenSettings(GeneratorOptions.createRandom(), WorldPresets.createDemoOptions(context.worldGenRegistryManager())), context.dataConfiguration()), context.dimensionsRegistryManager()), (resourceManager, dataPackContents, combinedDynamicRegistries, generatorOptions) -> {
            resourceManager.close();
            return new GeneratorOptionsHolder(generatorOptions.worldGenSettings(), combinedDynamicRegistries, dataPackContents, generatorOptions.dataConfiguration());
        }, Util.getMainWorkerExecutor(), client);
        client.runTasks(completableFuture::isDone);
        client.setScreen(new CreateWorldScreen(client, parent, completableFuture.join(), Optional.of(WorldPresets.DEFAULT), OptionalLong.empty()));
    }

    public static CreateWorldScreen create(MinecraftClient client, @Nullable Screen parent, LevelInfo levelInfo, GeneratorOptionsHolder generatorOptionsHolder, @Nullable Path dataPackTempDir) {
        CreateWorldScreen createWorldScreen = new CreateWorldScreen(client, parent, generatorOptionsHolder, WorldPresets.getWorldPreset(generatorOptionsHolder.selectedDimensions().dimensions()), OptionalLong.of(generatorOptionsHolder.generatorOptions().getSeed()));
        createWorldScreen.recreated = true;
        createWorldScreen.worldCreator.setWorldName(levelInfo.getLevelName());
        createWorldScreen.worldCreator.setCheatsEnabled(levelInfo.areCommandsAllowed());
        createWorldScreen.worldCreator.setDifficulty(levelInfo.getDifficulty());
        createWorldScreen.worldCreator.getGameRules().setAllValues(levelInfo.getGameRules(), null);
        if (levelInfo.isHardcore()) {
            createWorldScreen.worldCreator.setGameMode(WorldCreator.Mode.HARDCORE);
        } else if (levelInfo.getGameMode().isSurvivalLike()) {
            createWorldScreen.worldCreator.setGameMode(WorldCreator.Mode.SURVIVAL);
        } else if (levelInfo.getGameMode().isCreative()) {
            createWorldScreen.worldCreator.setGameMode(WorldCreator.Mode.CREATIVE);
        }
        createWorldScreen.dataPackTempDir = dataPackTempDir;
        return createWorldScreen;
    }

    private CreateWorldScreen(MinecraftClient client, @Nullable Screen parent, GeneratorOptionsHolder generatorOptionsHolder, Optional<RegistryKey<WorldPreset>> defaultWorldType, OptionalLong seed) {
        super(Text.translatable("selectWorld.create"));
        this.parent = parent;
        this.worldCreator = new WorldCreator(client.getLevelStorage().getSavesDirectory(), generatorOptionsHolder, defaultWorldType, seed);
    }

    public WorldCreator getWorldCreator() {
        return this.worldCreator;
    }

    @Override
    public void tick() {
        this.tabManager.tick();
    }

    @Override
    protected void init() {
        this.tabNavigation = TabNavigationWidget.builder(this.tabManager, this.width).tabs(new GameTab(), new WorldTab(), new MoreTab()).build();
        this.addDrawableChild(this.tabNavigation);
        this.grid = new GridWidget().setColumnSpacing(10);
        GridWidget.Adder adder = this.grid.createAdder(2);
        adder.add(ButtonWidget.builder(Text.translatable("selectWorld.create"), button -> this.createLevel()).build());
        adder.add(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.onCloseScreen()).build());
        this.grid.forEachChild(child -> {
            child.setNavigationOrder(1);
            this.addDrawableChild(child);
        });
        this.tabNavigation.selectTab(0, false);
        this.worldCreator.update();
        this.initTabNavigation();
    }

    @Override
    public void initTabNavigation() {
        if (this.tabNavigation == null || this.grid == null) {
            return;
        }
        this.tabNavigation.setWidth(this.width);
        this.tabNavigation.init();
        this.grid.refreshPositions();
        SimplePositioningWidget.setPos(this.grid, 0, this.height - 36, this.width, 36);
        int i = this.tabNavigation.getNavigationFocus().getBottom();
        ScreenRect screenRect = new ScreenRect(0, i, this.width, this.grid.getY() - i);
        this.tabManager.setTabArea(screenRect);
    }

    private static void showMessage(MinecraftClient client, Text text) {
        client.setScreenAndRender(new MessageScreen(text));
    }

    private void createLevel() {
        GeneratorOptionsHolder generatorOptionsHolder = this.worldCreator.getGeneratorOptionsHolder();
        DimensionOptionsRegistryHolder.DimensionsConfig dimensionsConfig = generatorOptionsHolder.selectedDimensions().toConfig(generatorOptionsHolder.dimensionOptionsRegistry());
        CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries = generatorOptionsHolder.combinedDynamicRegistries().with(ServerDynamicRegistryType.DIMENSIONS, dimensionsConfig.toDynamicRegistryManager());
        Lifecycle lifecycle = FeatureFlags.isNotVanilla(generatorOptionsHolder.dataConfiguration().enabledFeatures()) ? Lifecycle.experimental() : Lifecycle.stable();
        Lifecycle lifecycle2 = combinedDynamicRegistries.getCombinedRegistryManager().getRegistryLifecycle();
        Lifecycle lifecycle3 = lifecycle2.add(lifecycle);
        boolean bl = !this.recreated && lifecycle2 == Lifecycle.stable();
        IntegratedServerLoader.tryLoad(this.client, this, lifecycle3, () -> this.startServer(dimensionsConfig.specialWorldProperty(), combinedDynamicRegistries, lifecycle3), bl);
    }

    private void startServer(LevelProperties.SpecialProperty specialProperty, CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries, Lifecycle lifecycle) {
        CreateWorldScreen.showMessage(this.client, PREPARING_TEXT);
        Optional<LevelStorage.Session> optional = this.createSession();
        if (optional.isEmpty()) {
            return;
        }
        this.clearDataPackTempDir();
        boolean bl = specialProperty == LevelProperties.SpecialProperty.DEBUG;
        GeneratorOptionsHolder generatorOptionsHolder = this.worldCreator.getGeneratorOptionsHolder();
        LevelInfo levelInfo = this.createLevelInfo(bl);
        LevelProperties saveProperties = new LevelProperties(levelInfo, generatorOptionsHolder.generatorOptions(), specialProperty, lifecycle);
        this.client.createIntegratedServerLoader().start(optional.get(), generatorOptionsHolder.dataPackContents(), combinedDynamicRegistries, saveProperties);
    }

    private LevelInfo createLevelInfo(boolean debugWorld) {
        String string = this.worldCreator.getWorldName().trim();
        if (debugWorld) {
            GameRules gameRules = new GameRules();
            gameRules.get(GameRules.DO_DAYLIGHT_CYCLE).set(false, null);
            return new LevelInfo(string, GameMode.SPECTATOR, false, Difficulty.PEACEFUL, true, gameRules, DataConfiguration.SAFE_MODE);
        }
        return new LevelInfo(string, this.worldCreator.getGameMode().defaultGameMode, this.worldCreator.isHardcore(), this.worldCreator.getDifficulty(), this.worldCreator.areCheatsEnabled(), this.worldCreator.getGameRules(), this.worldCreator.getGeneratorOptionsHolder().dataConfiguration());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.tabNavigation.trySwitchTabsWithKey(keyCode)) {
            return true;
        }
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == 257 || keyCode == 335) {
            this.createLevel();
            return true;
        }
        return false;
    }

    @Override
    public void close() {
        this.onCloseScreen();
    }

    public void onCloseScreen() {
        this.client.setScreen(this.parent);
        this.clearDataPackTempDir();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        RenderSystem.setShaderTexture(0, FOOTER_SEPARATOR_TEXTURE);
        CreateWorldScreen.drawTexture(matrices, 0, MathHelper.roundUpToMultiple(this.height - 36 - 2, 2), 0.0f, 0.0f, this.width, 2, 32, 2);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void renderBackgroundTexture(MatrixStack matrices) {
        RenderSystem.setShaderTexture(0, LIGHT_DIRT_BACKGROUND_TEXTURE);
        int i = 32;
        CreateWorldScreen.drawTexture(matrices, 0, 0, 0, 0.0f, 0.0f, this.width, this.height, 32, 32);
    }

    @Override
    protected <T extends Element & Selectable> T addSelectableChild(T child) {
        return super.addSelectableChild(child);
    }

    @Override
    protected <T extends Element & Drawable> T addDrawableChild(T drawableElement) {
        return super.addDrawableChild(drawableElement);
    }

    @Nullable
    private Path getDataPackTempDir() {
        if (this.dataPackTempDir == null) {
            try {
                this.dataPackTempDir = Files.createTempDirectory(TEMP_DIR_PREFIX, new FileAttribute[0]);
            }
            catch (IOException iOException) {
                LOGGER.warn("Failed to create temporary dir", (Throwable)iOException);
                SystemToast.addPackCopyFailure(this.client, this.worldCreator.getWorldDirectoryName());
                this.onCloseScreen();
            }
        }
        return this.dataPackTempDir;
    }

    void openExperimentsScreen(DataConfiguration dataConfiguration) {
        Pair<Path, ResourcePackManager> pair = this.getScannedPack(dataConfiguration);
        if (pair != null) {
            this.client.setScreen(new ExperimentsScreen(this, (ResourcePackManager)pair.getSecond(), resourcePackManager -> this.applyDataPacks((ResourcePackManager)resourcePackManager, false, this::openExperimentsScreen)));
        }
    }

    void openPackScreen(DataConfiguration dataConfiguration) {
        Pair<Path, ResourcePackManager> pair = this.getScannedPack(dataConfiguration);
        if (pair != null) {
            this.client.setScreen(new PackScreen((ResourcePackManager)pair.getSecond(), resourcePackManager -> this.applyDataPacks((ResourcePackManager)resourcePackManager, true, this::openPackScreen), (Path)pair.getFirst(), Text.translatable("dataPack.title")));
        }
    }

    private void applyDataPacks(ResourcePackManager dataPackManager, boolean fromPackScreen, Consumer<DataConfiguration> configurationSetter) {
        List list2;
        ImmutableList list = ImmutableList.copyOf(dataPackManager.getEnabledNames());
        DataConfiguration dataConfiguration = new DataConfiguration(new DataPackSettings((List<String>)list, list2 = (List)dataPackManager.getNames().stream().filter(arg_0 -> CreateWorldScreen.method_29983((List)list, arg_0)).collect(ImmutableList.toImmutableList())), this.worldCreator.getGeneratorOptionsHolder().dataConfiguration().enabledFeatures());
        if (this.worldCreator.updateDataConfiguration(dataConfiguration)) {
            this.client.setScreen(this);
            return;
        }
        FeatureSet featureSet = dataPackManager.getRequestedFeatures();
        if (FeatureFlags.isNotVanilla(featureSet) && fromPackScreen) {
            this.client.setScreen(new ExperimentalWarningScreen(dataPackManager.getEnabledProfiles(), confirmed -> {
                if (confirmed) {
                    this.validateDataPacks(dataPackManager, dataConfiguration, configurationSetter);
                } else {
                    configurationSetter.accept(this.worldCreator.getGeneratorOptionsHolder().dataConfiguration());
                }
            }));
        } else {
            this.validateDataPacks(dataPackManager, dataConfiguration, configurationSetter);
        }
    }

    private void validateDataPacks(ResourcePackManager dataPackManager, DataConfiguration dataConfiguration, Consumer<DataConfiguration> configurationSetter) {
        this.client.setScreenAndRender(new MessageScreen(Text.translatable("dataPack.validation.working")));
        SaveLoading.ServerConfig serverConfig = CreateWorldScreen.createServerConfig(dataPackManager, dataConfiguration);
        ((CompletableFuture)SaveLoading.load(serverConfig, context -> {
            if (context.worldGenRegistryManager().get(RegistryKeys.WORLD_PRESET).size() == 0) {
                throw new IllegalStateException("Needs at least one world preset to continue");
            }
            if (context.worldGenRegistryManager().get(RegistryKeys.BIOME).size() == 0) {
                throw new IllegalStateException("Needs at least one biome continue");
            }
            GeneratorOptionsHolder generatorOptionsHolder = this.worldCreator.getGeneratorOptionsHolder();
            RegistryOps dynamicOps = RegistryOps.of(JsonOps.INSTANCE, generatorOptionsHolder.getCombinedRegistryManager());
            DataResult dataResult = WorldGenSettings.encode(dynamicOps, generatorOptionsHolder.generatorOptions(), generatorOptionsHolder.selectedDimensions()).setLifecycle(Lifecycle.stable());
            RegistryOps dynamicOps2 = RegistryOps.of(JsonOps.INSTANCE, context.worldGenRegistryManager());
            WorldGenSettings worldGenSettings = (WorldGenSettings)dataResult.flatMap(json -> WorldGenSettings.CODEC.parse(dynamicOps2, json)).getOrThrow(false, Util.addPrefix("Error parsing worldgen settings after loading data packs: ", arg_0 -> ((Logger)LOGGER).error(arg_0)));
            return new SaveLoading.LoadContext<WorldCreationSettings>(new WorldCreationSettings(worldGenSettings, context.dataConfiguration()), context.dimensionsRegistryManager());
        }, (resourceManager, dataPackContents, combinedDynamicRegistries, context) -> {
            resourceManager.close();
            return new GeneratorOptionsHolder(context.worldGenSettings(), combinedDynamicRegistries, dataPackContents, context.dataConfiguration());
        }, Util.getMainWorkerExecutor(), this.client).thenAcceptAsync(this.worldCreator::setGeneratorOptionsHolder, (Executor)this.client)).handle((void_, throwable) -> {
            if (throwable != null) {
                LOGGER.warn("Failed to validate datapack", throwable);
                this.client.setScreen(new ConfirmScreen(confirmed -> {
                    if (confirmed) {
                        configurationSetter.accept(this.worldCreator.getGeneratorOptionsHolder().dataConfiguration());
                    } else {
                        configurationSetter.accept(DataConfiguration.SAFE_MODE);
                    }
                }, Text.translatable("dataPack.validation.failed"), ScreenTexts.EMPTY, Text.translatable("dataPack.validation.back"), Text.translatable("dataPack.validation.reset")));
            } else {
                this.client.setScreen(this);
            }
            return null;
        });
    }

    private static SaveLoading.ServerConfig createServerConfig(ResourcePackManager dataPackManager, DataConfiguration dataConfiguration) {
        SaveLoading.DataPacks dataPacks = new SaveLoading.DataPacks(dataPackManager, dataConfiguration, false, true);
        return new SaveLoading.ServerConfig(dataPacks, CommandManager.RegistrationEnvironment.INTEGRATED, 2);
    }

    private void clearDataPackTempDir() {
        if (this.dataPackTempDir != null) {
            try (Stream<Path> stream = Files.walk(this.dataPackTempDir, new FileVisitOption[0]);){
                stream.sorted(Comparator.reverseOrder()).forEach(path -> {
                    try {
                        Files.delete(path);
                    }
                    catch (IOException iOException) {
                        LOGGER.warn("Failed to remove temporary file {}", path, (Object)iOException);
                    }
                });
            }
            catch (IOException iOException) {
                LOGGER.warn("Failed to list temporary dir {}", (Object)this.dataPackTempDir);
            }
            this.dataPackTempDir = null;
        }
    }

    private static void copyDataPack(Path srcFolder, Path destFolder, Path dataPackFile) {
        try {
            Util.relativeCopy(srcFolder, destFolder, dataPackFile);
        }
        catch (IOException iOException) {
            LOGGER.warn("Failed to copy datapack file from {} to {}", (Object)dataPackFile, (Object)destFolder);
            throw new UncheckedIOException(iOException);
        }
    }

    private Optional<LevelStorage.Session> createSession() {
        Optional<LevelStorage.Session> optional;
        String string;
        block12: {
            LevelStorage.Session session;
            block11: {
                string = this.worldCreator.getWorldDirectoryName();
                session = this.client.getLevelStorage().createSession(string);
                if (this.dataPackTempDir != null) break block11;
                return Optional.of(session);
            }
            Stream<Path> stream = Files.walk(this.dataPackTempDir, new FileVisitOption[0]);
            try {
                Path path2 = session.getDirectory(WorldSavePath.DATAPACKS);
                PathUtil.createDirectories(path2);
                stream.filter(path -> !path.equals(this.dataPackTempDir)).forEach(path -> CreateWorldScreen.copyDataPack(this.dataPackTempDir, path2, path));
                optional = Optional.of(session);
                if (stream == null) break block12;
            }
            catch (Throwable throwable) {
                try {
                    try {
                        if (stream != null) {
                            try {
                                stream.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    catch (IOException | UncheckedIOException exception) {
                        LOGGER.warn("Failed to copy datapacks to world {}", (Object)string, (Object)exception);
                        session.close();
                    }
                }
                catch (IOException | UncheckedIOException exception2) {
                    LOGGER.warn("Failed to create access for {}", (Object)string, (Object)exception2);
                }
            }
            stream.close();
        }
        return optional;
        SystemToast.addPackCopyFailure(this.client, string);
        this.onCloseScreen();
        return Optional.empty();
    }

    @Nullable
    public static Path copyDataPack(Path srcFolder, MinecraftClient client) {
        MutableObject mutableObject = new MutableObject();
        try (Stream<Path> stream = Files.walk(srcFolder, new FileVisitOption[0]);){
            stream.filter(dataPackFile -> !dataPackFile.equals(srcFolder)).forEach(dataPackFile -> {
                Path path2 = (Path)mutableObject.getValue();
                if (path2 == null) {
                    try {
                        path2 = Files.createTempDirectory(TEMP_DIR_PREFIX, new FileAttribute[0]);
                    }
                    catch (IOException iOException) {
                        LOGGER.warn("Failed to create temporary dir");
                        throw new UncheckedIOException(iOException);
                    }
                    mutableObject.setValue((Object)path2);
                }
                CreateWorldScreen.copyDataPack(srcFolder, path2, dataPackFile);
            });
        }
        catch (IOException | UncheckedIOException exception) {
            LOGGER.warn("Failed to copy datapacks from world {}", (Object)srcFolder, (Object)exception);
            SystemToast.addPackCopyFailure(client, srcFolder.toString());
            return null;
        }
        return (Path)mutableObject.getValue();
    }

    @Nullable
    private Pair<Path, ResourcePackManager> getScannedPack(DataConfiguration dataConfiguration) {
        Path path = this.getDataPackTempDir();
        if (path != null) {
            if (this.packManager == null) {
                this.packManager = VanillaDataPackProvider.createManager(path);
                this.packManager.scanPacks();
            }
            this.packManager.setEnabledProfiles(dataConfiguration.dataPacks().getEnabled());
            return Pair.of((Object)path, (Object)this.packManager);
        }
        return null;
    }

    private static /* synthetic */ boolean method_29983(List list, String name) {
        return !list.contains(name);
    }

    @Environment(value=EnvType.CLIENT)
    class GameTab
    extends GridScreenTab {
        private static final Text GAME_TAB_TITLE_TEXT = Text.translatable("createWorld.tab.game.title");
        private static final Text ALLOW_COMMANDS_TEXT = Text.translatable("selectWorld.allowCommands");
        private final TextFieldWidget worldNameField;

        GameTab() {
            super(GAME_TAB_TITLE_TEXT);
            GridWidget.Adder adder = this.grid.setRowSpacing(8).createAdder(1);
            Positioner positioner = adder.copyPositioner();
            GridWidget.Adder adder2 = new GridWidget().setRowSpacing(4).createAdder(1);
            adder2.add(new TextWidget(ENTER_NAME_TEXT, ((CreateWorldScreen)CreateWorldScreen.this).client.textRenderer), adder2.copyPositioner().marginLeft(1));
            this.worldNameField = adder2.add(new TextFieldWidget(CreateWorldScreen.this.textRenderer, 0, 0, 208, 20, Text.translatable("selectWorld.enterName")), adder2.copyPositioner().margin(1));
            this.worldNameField.setText(CreateWorldScreen.this.worldCreator.getWorldName());
            this.worldNameField.setChangedListener(CreateWorldScreen.this.worldCreator::setWorldName);
            CreateWorldScreen.this.worldCreator.addListener(creator -> this.worldNameField.setTooltip(Tooltip.of(Text.translatable("selectWorld.targetFolder", Text.literal(creator.getWorldDirectoryName()).formatted(Formatting.ITALIC)))));
            CreateWorldScreen.this.setInitialFocus(this.worldNameField);
            adder.add(adder2.getGridWidget(), adder.copyPositioner().alignHorizontalCenter());
            CyclingButtonWidget<WorldCreator.Mode> cyclingButtonWidget = adder.add(CyclingButtonWidget.builder(value -> value.name).values((WorldCreator.Mode[])new WorldCreator.Mode[]{WorldCreator.Mode.SURVIVAL, WorldCreator.Mode.HARDCORE, WorldCreator.Mode.CREATIVE}).build(0, 0, 210, 20, GAME_MODE_TEXT, (button, value) -> CreateWorldScreen.this.worldCreator.setGameMode((WorldCreator.Mode)((Object)value))), positioner);
            CreateWorldScreen.this.worldCreator.addListener(creator -> {
                cyclingButtonWidget.setValue(creator.getGameMode());
                cyclingButtonWidget.active = !creator.isDebug();
                cyclingButtonWidget.setTooltip(Tooltip.of(creator.getGameMode().getInfo()));
            });
            CyclingButtonWidget<Difficulty> cyclingButtonWidget2 = adder.add(CyclingButtonWidget.builder(Difficulty::getTranslatableName).values((Difficulty[])Difficulty.values()).build(0, 0, 210, 20, Text.translatable("options.difficulty"), (button, value) -> CreateWorldScreen.this.worldCreator.setDifficulty((Difficulty)value)), positioner);
            CreateWorldScreen.this.worldCreator.addListener(creator -> {
                cyclingButtonWidget2.setValue(CreateWorldScreen.this.worldCreator.getDifficulty());
                cyclingButtonWidget.active = !CreateWorldScreen.this.worldCreator.isHardcore();
                cyclingButtonWidget2.setTooltip(Tooltip.of(CreateWorldScreen.this.worldCreator.getDifficulty().getInfo()));
            });
            CyclingButtonWidget<Boolean> cyclingButtonWidget3 = adder.add(CyclingButtonWidget.onOffBuilder().tooltip(value -> Tooltip.of(ALLOW_COMMANDS_INFO_TEXT)).build(0, 0, 210, 20, ALLOW_COMMANDS_TEXT, (button, value) -> CreateWorldScreen.this.worldCreator.setCheatsEnabled((boolean)value)));
            CreateWorldScreen.this.worldCreator.addListener(creator -> {
                cyclingButtonWidget3.setValue(CreateWorldScreen.this.worldCreator.areCheatsEnabled());
                cyclingButtonWidget.active = !CreateWorldScreen.this.worldCreator.isDebug() && !CreateWorldScreen.this.worldCreator.isHardcore();
            });
        }

        @Override
        public void tick() {
            this.worldNameField.tick();
        }

        private /* synthetic */ void method_49012(ButtonWidget button) {
            CreateWorldScreen.this.openExperimentsScreen(CreateWorldScreen.this.worldCreator.getGeneratorOptionsHolder().dataConfiguration());
        }
    }

    @Environment(value=EnvType.CLIENT)
    class WorldTab
    extends GridScreenTab {
        private static final Text WORLD_TAB_TITLE_TEXT = Text.translatable("createWorld.tab.world.title");
        private static final Text AMPLIFIED_GENERATOR_INFO_TEXT = Text.translatable("generator.minecraft.amplified.info");
        private static final Text MAP_FEATURES_TEXT = Text.translatable("selectWorld.mapFeatures");
        private static final Text MAP_FEATURES_INFO_TEXT = Text.translatable("selectWorld.mapFeatures.info");
        private static final Text BONUS_ITEMS_TEXT = Text.translatable("selectWorld.bonusItems");
        private static final Text ENTER_SEED_TEXT = Text.translatable("selectWorld.enterSeed");
        static final Text SEED_INFO_TEXT = Text.translatable("selectWorld.seedInfo").formatted(Formatting.DARK_GRAY);
        private static final int field_42190 = 310;
        private final TextFieldWidget seedField;
        private final ButtonWidget customizeButton;

        WorldTab() {
            super(WORLD_TAB_TITLE_TEXT);
            GridWidget.Adder adder = this.grid.setColumnSpacing(10).setRowSpacing(8).createAdder(2);
            CyclingButtonWidget<WorldCreator.WorldType> cyclingButtonWidget2 = adder.add(CyclingButtonWidget.builder(WorldCreator.WorldType::getName).values(this.getWorldTypes()).narration(WorldTab::getWorldTypeNarrationMessage).build(0, 0, 150, 20, Text.translatable("selectWorld.mapType"), (cyclingButtonWidget, worldType) -> CreateWorldScreen.this.worldCreator.setWorldType((WorldCreator.WorldType)worldType)));
            cyclingButtonWidget2.setValue(CreateWorldScreen.this.worldCreator.getWorldType());
            CreateWorldScreen.this.worldCreator.addListener(creator -> {
                WorldCreator.WorldType worldType = creator.getWorldType();
                cyclingButtonWidget2.setValue(worldType);
                if (worldType.isAmplified()) {
                    cyclingButtonWidget2.setTooltip(Tooltip.of(AMPLIFIED_GENERATOR_INFO_TEXT));
                } else {
                    cyclingButtonWidget2.setTooltip(null);
                }
                cyclingButtonWidget.active = CreateWorldScreen.this.worldCreator.getWorldType().preset() != null;
            });
            this.customizeButton = adder.add(ButtonWidget.builder(Text.translatable("selectWorld.customizeType"), button -> this.openCustomizeScreen()).build());
            CreateWorldScreen.this.worldCreator.addListener(creator -> {
                this.customizeButton.active = !creator.isDebug() && creator.getLevelScreenProvider() != null;
            });
            GridWidget.Adder adder2 = new GridWidget().setRowSpacing(4).createAdder(1);
            adder2.add(new TextWidget(ENTER_SEED_TEXT, CreateWorldScreen.this.textRenderer).alignLeft());
            this.seedField = adder2.add(new TextFieldWidget(CreateWorldScreen.this.textRenderer, 0, 0, 308, 20, Text.translatable("selectWorld.enterSeed")){

                @Override
                protected MutableText getNarrationMessage() {
                    return super.getNarrationMessage().append(ScreenTexts.SENTENCE_SEPARATOR).append(SEED_INFO_TEXT);
                }
            }, adder.copyPositioner().margin(1));
            this.seedField.setPlaceholder(SEED_INFO_TEXT);
            this.seedField.setText(CreateWorldScreen.this.worldCreator.getSeed());
            this.seedField.setChangedListener(seed -> CreateWorldScreen.this.worldCreator.setSeed(this.seedField.getText()));
            adder.add(adder2.getGridWidget(), 2);
            WorldScreenOptionGrid.Builder builder = WorldScreenOptionGrid.builder(310).marginLeft(1);
            builder.add(MAP_FEATURES_TEXT, CreateWorldScreen.this.worldCreator::shouldGenerateStructures, CreateWorldScreen.this.worldCreator::setGenerateStructures).toggleable(() -> !CreateWorldScreen.this.worldCreator.isDebug()).tooltip(MAP_FEATURES_INFO_TEXT);
            builder.add(BONUS_ITEMS_TEXT, CreateWorldScreen.this.worldCreator::isBonusChestEnabled, CreateWorldScreen.this.worldCreator::setBonusChestEnabled).toggleable(() -> !CreateWorldScreen.this.worldCreator.isHardcore() && !CreateWorldScreen.this.worldCreator.isDebug());
            WorldScreenOptionGrid worldScreenOptionGrid = builder.build(widget -> adder.add(widget, 2));
            CreateWorldScreen.this.worldCreator.addListener(creator -> worldScreenOptionGrid.refresh());
        }

        private void openCustomizeScreen() {
            LevelScreenProvider levelScreenProvider = CreateWorldScreen.this.worldCreator.getLevelScreenProvider();
            if (levelScreenProvider != null) {
                CreateWorldScreen.this.client.setScreen(levelScreenProvider.createEditScreen(CreateWorldScreen.this, CreateWorldScreen.this.worldCreator.getGeneratorOptionsHolder()));
            }
        }

        private CyclingButtonWidget.Values<WorldCreator.WorldType> getWorldTypes() {
            return new CyclingButtonWidget.Values<WorldCreator.WorldType>(){

                @Override
                public List<WorldCreator.WorldType> getCurrent() {
                    return CyclingButtonWidget.HAS_ALT_DOWN.getAsBoolean() ? CreateWorldScreen.this.worldCreator.getExtendedWorldTypes() : CreateWorldScreen.this.worldCreator.getNormalWorldTypes();
                }

                @Override
                public List<WorldCreator.WorldType> getDefaults() {
                    return CreateWorldScreen.this.worldCreator.getNormalWorldTypes();
                }
            };
        }

        private static MutableText getWorldTypeNarrationMessage(CyclingButtonWidget<WorldCreator.WorldType> worldTypeButton) {
            if (worldTypeButton.getValue().isAmplified()) {
                return ScreenTexts.joinSentences(worldTypeButton.getGenericNarrationMessage(), AMPLIFIED_GENERATOR_INFO_TEXT);
            }
            return worldTypeButton.getGenericNarrationMessage();
        }

        @Override
        public void tick() {
            this.seedField.tick();
        }
    }

    @Environment(value=EnvType.CLIENT)
    class MoreTab
    extends GridScreenTab {
        private static final Text MORE_TAB_TITLE_TEXT = Text.translatable("createWorld.tab.more.title");
        private static final Text GAME_RULES_TEXT = Text.translatable("selectWorld.gameRules");
        private static final Text DATA_PACKS_TEXT = Text.translatable("selectWorld.dataPacks");

        MoreTab() {
            super(MORE_TAB_TITLE_TEXT);
            GridWidget.Adder adder = this.grid.setRowSpacing(8).createAdder(1);
            adder.add(ButtonWidget.builder(GAME_RULES_TEXT, button -> this.openGameRulesScreen()).width(210).build());
            adder.add(ButtonWidget.builder(EXPERIMENTS_TEXT, button -> CreateWorldScreen.this.openExperimentsScreen(CreateWorldScreen.this.worldCreator.getGeneratorOptionsHolder().dataConfiguration())).width(210).build());
            adder.add(ButtonWidget.builder(DATA_PACKS_TEXT, button -> CreateWorldScreen.this.openPackScreen(CreateWorldScreen.this.worldCreator.getGeneratorOptionsHolder().dataConfiguration())).width(210).build());
        }

        private void openGameRulesScreen() {
            CreateWorldScreen.this.client.setScreen(new EditGameRulesScreen(CreateWorldScreen.this.worldCreator.getGameRules().copy(), gameRules -> {
                CreateWorldScreen.this.client.setScreen(CreateWorldScreen.this);
                gameRules.ifPresent(CreateWorldScreen.this.worldCreator::setGameRules);
            }));
        }
    }

    @Environment(value=EnvType.CLIENT)
    record WorldCreationSettings(WorldGenSettings worldGenSettings, DataConfiguration dataConfiguration) {
    }
}

