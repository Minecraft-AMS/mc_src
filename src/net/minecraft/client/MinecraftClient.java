/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Queues
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.GameProfileRepository
 *  com.mojang.authlib.exceptions.AuthenticationException
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.authlib.minecraft.UserApiService
 *  com.mojang.authlib.minecraft.UserApiService$UserFlag
 *  com.mojang.authlib.properties.PropertyMap
 *  com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.Lifecycle
 *  it.unimi.dsi.fastutil.objects.Object2BooleanFunction
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.io.FileUtils
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.util.tinyfd.TinyFileDialogs
 *  org.slf4j.Logger
 */
package net.minecraft.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Queues;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.blaze3d.platform.GlDebugInfo;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClientGame;
import net.minecraft.client.Mouse;
import net.minecraft.client.RunArgs;
import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.font.FontManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlDebug;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gl.WindowFramebuffer;
import net.minecraft.client.gui.WorldGenerationProgressTracker;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.BackupPromptScreen;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.DatapackFailureScreen;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.LevelLoadingScreen;
import net.minecraft.client.gui.screen.OutOfMemoryScreen;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.gui.screen.SaveLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.SleepingChatScreen;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.gui.screen.world.EditWorldScreen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.option.AoMode;
import net.minecraft.client.option.ChatVisibility;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.HotbarStorage;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Option;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.resource.ClientBuiltinResourcePackProvider;
import net.minecraft.client.resource.FoliageColormapResourceSupplier;
import net.minecraft.client.resource.Format3ResourcePack;
import net.minecraft.client.resource.Format4ResourcePack;
import net.minecraft.client.resource.GrassColormapResourceSupplier;
import net.minecraft.client.resource.PeriodicNotificationManager;
import net.minecraft.client.resource.ResourceReloadLogger;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import net.minecraft.client.resource.VideoWarningManager;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.search.IdentifierSearchableContainer;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.search.SearchableContainer;
import net.minecraft.client.search.TextSearchableContainer;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.MusicType;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.texture.PaintingManager;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.client.util.ClientSamplerSource;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.client.util.Session;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.WindowProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.telemetry.TelemetrySender;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.datafixer.Schemas;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SkullItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.QueueingWorldGenerationProgressListener;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.sound.MusicSound;
import net.minecraft.tag.TagKey;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.KeybindText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.FileNameUtil;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.MetricsData;
import net.minecraft.util.ModStatus;
import net.minecraft.util.SystemDetails;
import net.minecraft.util.TickDurationMonitor;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.Unit;
import net.minecraft.util.UserCache;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.ZipCompressor;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashMemoryReserve;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.profiler.DebugRecorder;
import net.minecraft.util.profiler.DummyProfiler;
import net.minecraft.util.profiler.DummyRecorder;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.ProfilerTiming;
import net.minecraft.util.profiler.RecordDumper;
import net.minecraft.util.profiler.Recorder;
import net.minecraft.util.profiler.TickTimeTracker;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class MinecraftClient
extends ReentrantThreadExecutor<Runnable>
implements WindowEventHandler {
    private static MinecraftClient instance;
    private static final Logger LOGGER;
    public static final boolean IS_SYSTEM_MAC;
    private static final int field_32145 = 10;
    public static final Identifier DEFAULT_FONT_ID;
    public static final Identifier UNICODE_FONT_ID;
    public static final Identifier ALT_TEXT_RENDERER_ID;
    private static final Identifier REGIONAL_COMPLIANCIES_ID;
    private static final CompletableFuture<Unit> COMPLETED_UNIT_FUTURE;
    private static final Text SOCIAL_INTERACTIONS_NOT_AVAILABLE;
    public static final String GL_ERROR_DIALOGUE = "Please make sure you have up-to-date drivers (see aka.ms/mcdriver for instructions).";
    private final File resourcePackDir;
    private final PropertyMap sessionPropertyMap;
    private final TextureManager textureManager;
    private final DataFixer dataFixer;
    private final WindowProvider windowProvider;
    private final Window window;
    private final RenderTickCounter renderTickCounter = new RenderTickCounter(20.0f, 0L);
    private final BufferBuilderStorage bufferBuilders;
    public final WorldRenderer worldRenderer;
    private final EntityRenderDispatcher entityRenderDispatcher;
    private final ItemRenderer itemRenderer;
    private final HeldItemRenderer heldItemRenderer;
    public final ParticleManager particleManager;
    private final SearchManager searchManager = new SearchManager();
    private final Session session;
    public final TextRenderer textRenderer;
    public final GameRenderer gameRenderer;
    public final DebugRenderer debugRenderer;
    private final AtomicReference<WorldGenerationProgressTracker> worldGenProgressTracker = new AtomicReference();
    public final InGameHud inGameHud;
    public final GameOptions options;
    private final HotbarStorage creativeHotbarStorage;
    public final Mouse mouse;
    public final Keyboard keyboard;
    public final File runDirectory;
    private final String gameVersion;
    private final String versionType;
    private final Proxy networkProxy;
    private final LevelStorage levelStorage;
    public final MetricsData metricsData = new MetricsData();
    private final boolean is64Bit;
    private final boolean isDemo;
    private final boolean multiplayerEnabled;
    private final boolean onlineChatEnabled;
    private final ReloadableResourceManagerImpl resourceManager;
    private final ClientBuiltinResourcePackProvider builtinPackProvider;
    private final ResourcePackManager resourcePackManager;
    private final LanguageManager languageManager;
    private final BlockColors blockColors;
    private final ItemColors itemColors;
    private final Framebuffer framebuffer;
    private final SoundManager soundManager;
    private final MusicTracker musicTracker;
    private final FontManager fontManager;
    private final SplashTextResourceSupplier splashTextLoader;
    private final VideoWarningManager videoWarningManager;
    private final PeriodicNotificationManager regionalComplianciesManager = new PeriodicNotificationManager(REGIONAL_COMPLIANCIES_ID, (Object2BooleanFunction<String>)((Object2BooleanFunction)MinecraftClient::isCountrySetTo));
    private final MinecraftSessionService sessionService;
    private final UserApiService userApiService;
    private final PlayerSkinProvider skinProvider;
    private final BakedModelManager bakedModelManager;
    private final BlockRenderManager blockRenderManager;
    private final PaintingManager paintingManager;
    private final StatusEffectSpriteManager statusEffectSpriteManager;
    private final ToastManager toastManager;
    private final MinecraftClientGame game = new MinecraftClientGame(this);
    private final TutorialManager tutorialManager;
    private final SocialInteractionsManager socialInteractionsManager;
    private final EntityModelLoader entityModelLoader;
    private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
    private final UUID deviceSessionId = UUID.randomUUID();
    @Nullable
    public ClientPlayerInteractionManager interactionManager;
    @Nullable
    public ClientWorld world;
    @Nullable
    public ClientPlayerEntity player;
    @Nullable
    private IntegratedServer server;
    @Nullable
    private ServerInfo currentServerEntry;
    @Nullable
    private ClientConnection integratedServerConnection;
    private boolean integratedServerRunning;
    @Nullable
    public Entity cameraEntity;
    @Nullable
    public Entity targetedEntity;
    @Nullable
    public HitResult crosshairTarget;
    private int itemUseCooldown;
    protected int attackCooldown;
    private volatile boolean paused;
    private float pausedTickDelta;
    private long lastMetricsSampleTime = Util.getMeasuringTimeNano();
    private long nextDebugInfoUpdateTime;
    private int fpsCounter;
    public boolean skipGameRender;
    @Nullable
    public Screen currentScreen;
    @Nullable
    private Overlay overlay;
    private boolean connectedToRealms;
    private Thread thread;
    private volatile boolean running = true;
    @Nullable
    private Supplier<CrashReport> crashReportSupplier;
    private static int currentFps;
    public String fpsDebugString = "";
    public boolean wireFrame;
    public boolean debugChunkInfo;
    public boolean debugChunkOcclusion;
    public boolean chunkCullingEnabled = true;
    private boolean windowFocused;
    private final Queue<Runnable> renderTaskQueue = Queues.newConcurrentLinkedQueue();
    @Nullable
    private CompletableFuture<Void> resourceReloadFuture;
    @Nullable
    private TutorialToast socialInteractionsToast;
    private Profiler profiler = DummyProfiler.INSTANCE;
    private int trackingTick;
    private final TickTimeTracker tickTimeTracker = new TickTimeTracker(Util.nanoTimeSupplier, () -> this.trackingTick);
    @Nullable
    private ProfileResult tickProfilerResult;
    private Recorder recorder = DummyRecorder.INSTANCE;
    private final ResourceReloadLogger resourceReloadLogger = new ResourceReloadLogger();
    private String openProfilerSection = "root";

    public MinecraftClient(RunArgs args) {
        super("Client");
        int i;
        String string;
        instance = this;
        this.runDirectory = args.directories.runDir;
        File file = args.directories.assetDir;
        this.resourcePackDir = args.directories.resourcePackDir;
        this.gameVersion = args.game.version;
        this.versionType = args.game.versionType;
        this.sessionPropertyMap = args.network.profileProperties;
        this.builtinPackProvider = new ClientBuiltinResourcePackProvider(new File(this.runDirectory, "server-resource-packs"), args.directories.getResourceIndex());
        this.resourcePackManager = new ResourcePackManager(MinecraftClient::createResourcePackProfile, this.builtinPackProvider, new FileResourcePackProvider(this.resourcePackDir, ResourcePackSource.PACK_SOURCE_NONE));
        this.networkProxy = args.network.netProxy;
        YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(this.networkProxy);
        this.sessionService = yggdrasilAuthenticationService.createMinecraftSessionService();
        this.userApiService = this.createUserApiService(yggdrasilAuthenticationService, args);
        this.session = args.network.session;
        LOGGER.info("Setting user: {}", (Object)this.session.getUsername());
        LOGGER.debug("(Session ID is {})", (Object)this.session.getSessionId());
        this.isDemo = args.game.demo;
        this.multiplayerEnabled = !args.game.multiplayerDisabled;
        this.onlineChatEnabled = !args.game.onlineChatDisabled;
        this.is64Bit = MinecraftClient.checkIs64Bit();
        this.server = null;
        if (this.isMultiplayerEnabled() && args.autoConnect.serverAddress != null) {
            string = args.autoConnect.serverAddress;
            i = args.autoConnect.serverPort;
        } else {
            string = null;
            i = 0;
        }
        KeybindText.setTranslator(KeyBinding::getLocalizedName);
        this.dataFixer = Schemas.getFixer();
        this.toastManager = new ToastManager(this);
        this.thread = Thread.currentThread();
        this.options = new GameOptions(this, this.runDirectory);
        this.tutorialManager = new TutorialManager(this, this.options);
        this.creativeHotbarStorage = new HotbarStorage(this.runDirectory, this.dataFixer);
        LOGGER.info("Backend library: {}", (Object)RenderSystem.getBackendDescription());
        WindowSettings windowSettings = this.options.overrideHeight > 0 && this.options.overrideWidth > 0 ? new WindowSettings(this.options.overrideWidth, this.options.overrideHeight, args.windowSettings.fullscreenWidth, args.windowSettings.fullscreenHeight, args.windowSettings.fullscreen) : args.windowSettings;
        Util.nanoTimeSupplier = RenderSystem.initBackendSystem();
        this.windowProvider = new WindowProvider(this);
        this.window = this.windowProvider.createWindow(windowSettings, this.options.fullscreenResolution, this.getWindowTitle());
        this.onWindowFocusChanged(true);
        if (!IS_SYSTEM_MAC) {
            try {
                InputStream inputStream = this.getResourcePackProvider().getPack().open(ResourceType.CLIENT_RESOURCES, new Identifier("icons/icon_16x16.png"));
                InputStream inputStream2 = this.getResourcePackProvider().getPack().open(ResourceType.CLIENT_RESOURCES, new Identifier("icons/icon_32x32.png"));
                this.window.setIcon(inputStream, inputStream2);
            }
            catch (IOException iOException) {
                LOGGER.error("Couldn't set icon", (Throwable)iOException);
            }
        }
        this.window.setFramerateLimit(this.options.maxFps);
        this.mouse = new Mouse(this);
        this.mouse.setup(this.window.getHandle());
        this.keyboard = new Keyboard(this);
        this.keyboard.setup(this.window.getHandle());
        RenderSystem.initRenderer(this.options.glDebugVerbosity, false);
        this.framebuffer = new WindowFramebuffer(this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
        this.framebuffer.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        this.framebuffer.clear(IS_SYSTEM_MAC);
        this.resourceManager = new ReloadableResourceManagerImpl(ResourceType.CLIENT_RESOURCES);
        this.resourcePackManager.scanPacks();
        this.options.addResourcePackProfilesToManager(this.resourcePackManager);
        this.languageManager = new LanguageManager(this.options.language);
        this.resourceManager.registerReloader(this.languageManager);
        this.textureManager = new TextureManager(this.resourceManager);
        this.resourceManager.registerReloader(this.textureManager);
        this.skinProvider = new PlayerSkinProvider(this.textureManager, new File(file, "skins"), this.sessionService);
        this.levelStorage = new LevelStorage(this.runDirectory.toPath().resolve("saves"), this.runDirectory.toPath().resolve("backups"), this.dataFixer);
        this.soundManager = new SoundManager(this.resourceManager, this.options);
        this.resourceManager.registerReloader(this.soundManager);
        this.splashTextLoader = new SplashTextResourceSupplier(this.session);
        this.resourceManager.registerReloader(this.splashTextLoader);
        this.musicTracker = new MusicTracker(this);
        this.fontManager = new FontManager(this.textureManager);
        this.textRenderer = this.fontManager.createTextRenderer();
        this.resourceManager.registerReloader(this.fontManager.getResourceReloadListener());
        this.initFont(this.forcesUnicodeFont());
        this.resourceManager.registerReloader(new GrassColormapResourceSupplier());
        this.resourceManager.registerReloader(new FoliageColormapResourceSupplier());
        this.window.setPhase("Startup");
        RenderSystem.setupDefaultState(0, 0, this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
        this.window.setPhase("Post startup");
        this.blockColors = BlockColors.create();
        this.itemColors = ItemColors.create(this.blockColors);
        this.bakedModelManager = new BakedModelManager(this.textureManager, this.blockColors, this.options.mipmapLevels);
        this.resourceManager.registerReloader(this.bakedModelManager);
        this.entityModelLoader = new EntityModelLoader();
        this.resourceManager.registerReloader(this.entityModelLoader);
        this.blockEntityRenderDispatcher = new BlockEntityRenderDispatcher(this.textRenderer, this.entityModelLoader, this::getBlockRenderManager);
        this.resourceManager.registerReloader(this.blockEntityRenderDispatcher);
        BuiltinModelItemRenderer builtinModelItemRenderer = new BuiltinModelItemRenderer(this.blockEntityRenderDispatcher, this.entityModelLoader);
        this.resourceManager.registerReloader(builtinModelItemRenderer);
        this.itemRenderer = new ItemRenderer(this.textureManager, this.bakedModelManager, this.itemColors, builtinModelItemRenderer);
        this.entityRenderDispatcher = new EntityRenderDispatcher(this.textureManager, this.itemRenderer, this.textRenderer, this.options, this.entityModelLoader);
        this.resourceManager.registerReloader(this.entityRenderDispatcher);
        this.heldItemRenderer = new HeldItemRenderer(this);
        this.resourceManager.registerReloader(this.itemRenderer);
        this.bufferBuilders = new BufferBuilderStorage();
        this.gameRenderer = new GameRenderer(this, this.resourceManager, this.bufferBuilders);
        this.resourceManager.registerReloader(this.gameRenderer);
        this.socialInteractionsManager = new SocialInteractionsManager(this, this.userApiService);
        this.blockRenderManager = new BlockRenderManager(this.bakedModelManager.getBlockModels(), builtinModelItemRenderer, this.blockColors);
        this.resourceManager.registerReloader(this.blockRenderManager);
        this.worldRenderer = new WorldRenderer(this, this.bufferBuilders);
        this.resourceManager.registerReloader(this.worldRenderer);
        this.initializeSearchableContainers();
        this.resourceManager.registerReloader(this.searchManager);
        this.particleManager = new ParticleManager(this.world, this.textureManager);
        this.resourceManager.registerReloader(this.particleManager);
        this.paintingManager = new PaintingManager(this.textureManager);
        this.resourceManager.registerReloader(this.paintingManager);
        this.statusEffectSpriteManager = new StatusEffectSpriteManager(this.textureManager);
        this.resourceManager.registerReloader(this.statusEffectSpriteManager);
        this.videoWarningManager = new VideoWarningManager();
        this.resourceManager.registerReloader(this.videoWarningManager);
        this.resourceManager.registerReloader(this.regionalComplianciesManager);
        this.inGameHud = new InGameHud(this);
        this.debugRenderer = new DebugRenderer(this);
        RenderSystem.setErrorCallback(this::handleGlErrorByDisableVsync);
        if (this.framebuffer.textureWidth != this.window.getFramebufferWidth() || this.framebuffer.textureHeight != this.window.getFramebufferHeight()) {
            StringBuilder stringBuilder = new StringBuilder("Recovering from unsupported resolution (" + this.window.getFramebufferWidth() + "x" + this.window.getFramebufferHeight() + ").\nPlease make sure you have up-to-date drivers (see aka.ms/mcdriver for instructions).");
            if (GlDebug.isDebugMessageEnabled()) {
                stringBuilder.append("\n\nReported GL debug messages:\n").append(String.join((CharSequence)"\n", GlDebug.collectDebugMessages()));
            }
            this.window.setWindowedSize(this.framebuffer.textureWidth, this.framebuffer.textureHeight);
            TinyFileDialogs.tinyfd_messageBox((CharSequence)"Minecraft", (CharSequence)stringBuilder.toString(), (CharSequence)"ok", (CharSequence)"error", (boolean)false);
        } else if (this.options.fullscreen && !this.window.isFullscreen()) {
            this.window.toggleFullscreen();
            this.options.fullscreen = this.window.isFullscreen();
        }
        this.window.setVsync(this.options.enableVsync);
        this.window.setRawMouseMotion(this.options.rawMouseInput);
        this.window.logOnGlError();
        this.onResolutionChanged();
        this.gameRenderer.preloadShaders(this.getResourcePackProvider().getPack());
        SplashOverlay.init(this);
        List<ResourcePack> list = this.resourcePackManager.createResourcePacks();
        this.resourceReloadLogger.reload(ResourceReloadLogger.ReloadReason.INITIAL, list);
        this.setOverlay(new SplashOverlay(this, this.resourceManager.reload(Util.getMainWorkerExecutor(), this, COMPLETED_UNIT_FUTURE, list), throwable -> Util.ifPresentOrElse(throwable, this::handleResourceReloadException, () -> {
            if (SharedConstants.isDevelopment) {
                this.checkGameData();
            }
            this.resourceReloadLogger.finish();
        }), false));
        if (string != null) {
            ConnectScreen.connect(new TitleScreen(), this, new ServerAddress(string, i), null);
        } else {
            this.setScreen(new TitleScreen(true));
        }
    }

    private static boolean isCountrySetTo(Object country) {
        try {
            return Locale.getDefault().getISO3Country().equals(country);
        }
        catch (MissingResourceException missingResourceException) {
            return false;
        }
    }

    public void updateWindowTitle() {
        this.window.setTitle(this.getWindowTitle());
    }

    private String getWindowTitle() {
        StringBuilder stringBuilder = new StringBuilder("Minecraft");
        if (MinecraftClient.getModStatus().isModded()) {
            stringBuilder.append("*");
        }
        stringBuilder.append(" ");
        stringBuilder.append(SharedConstants.getGameVersion().getName());
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.getNetworkHandler();
        if (clientPlayNetworkHandler != null && clientPlayNetworkHandler.getConnection().isOpen()) {
            stringBuilder.append(" - ");
            if (this.server != null && !this.server.isRemote()) {
                stringBuilder.append(I18n.translate("title.singleplayer", new Object[0]));
            } else if (this.isConnectedToRealms()) {
                stringBuilder.append(I18n.translate("title.multiplayer.realms", new Object[0]));
            } else if (this.server != null || this.currentServerEntry != null && this.currentServerEntry.isLocal()) {
                stringBuilder.append(I18n.translate("title.multiplayer.lan", new Object[0]));
            } else {
                stringBuilder.append(I18n.translate("title.multiplayer.other", new Object[0]));
            }
        }
        return stringBuilder.toString();
    }

    private UserApiService createUserApiService(YggdrasilAuthenticationService authService, RunArgs runArgs) {
        try {
            return authService.createUserApiService(runArgs.network.session.getAccessToken());
        }
        catch (AuthenticationException authenticationException) {
            LOGGER.error("Failed to verify authentication", (Throwable)authenticationException);
            return UserApiService.OFFLINE;
        }
    }

    public static ModStatus getModStatus() {
        return ModStatus.check("vanilla", ClientBrandRetriever::getClientModName, "Client", MinecraftClient.class);
    }

    private void handleResourceReloadException(Throwable throwable) {
        if (this.resourcePackManager.getEnabledNames().size() > 1) {
            this.onResourceReloadFailure(throwable, null);
        } else {
            Util.throwUnchecked(throwable);
        }
    }

    public void onResourceReloadFailure(Throwable exception, @Nullable Text resourceName) {
        LOGGER.info("Caught error loading resourcepacks, removing all selected resourcepacks", exception);
        this.resourceReloadLogger.recover(exception);
        this.resourcePackManager.setEnabledProfiles(Collections.emptyList());
        this.options.resourcePacks.clear();
        this.options.incompatibleResourcePacks.clear();
        this.options.write();
        this.reloadResources(true).thenRun(() -> {
            ToastManager toastManager = this.getToastManager();
            SystemToast.show(toastManager, SystemToast.Type.PACK_LOAD_FAILURE, new TranslatableText("resourcePack.load_fail"), resourceName);
        });
    }

    public void run() {
        this.thread = Thread.currentThread();
        if (Runtime.getRuntime().availableProcessors() > 4) {
            this.thread.setPriority(10);
        }
        try {
            boolean bl = false;
            while (this.running) {
                if (this.crashReportSupplier != null) {
                    MinecraftClient.printCrashReport(this.crashReportSupplier.get());
                    return;
                }
                try {
                    TickDurationMonitor tickDurationMonitor = TickDurationMonitor.create("Renderer");
                    boolean bl2 = this.shouldMonitorTickDuration();
                    this.profiler = this.startMonitor(bl2, tickDurationMonitor);
                    this.profiler.startTick();
                    this.recorder.startTick();
                    this.render(!bl);
                    this.recorder.endTick();
                    this.profiler.endTick();
                    this.endMonitor(bl2, tickDurationMonitor);
                }
                catch (OutOfMemoryError outOfMemoryError) {
                    if (bl) {
                        throw outOfMemoryError;
                    }
                    this.cleanUpAfterCrash();
                    this.setScreen(new OutOfMemoryScreen());
                    System.gc();
                    LOGGER.error(LogUtils.FATAL_MARKER, "Out of memory", (Throwable)outOfMemoryError);
                    bl = true;
                }
            }
        }
        catch (CrashException crashException) {
            this.addDetailsToCrashReport(crashException.getReport());
            this.cleanUpAfterCrash();
            LOGGER.error(LogUtils.FATAL_MARKER, "Reported exception thrown!", (Throwable)crashException);
            MinecraftClient.printCrashReport(crashException.getReport());
        }
        catch (Throwable throwable) {
            CrashReport crashReport = this.addDetailsToCrashReport(new CrashReport("Unexpected error", throwable));
            LOGGER.error(LogUtils.FATAL_MARKER, "Unreported exception thrown!", throwable);
            this.cleanUpAfterCrash();
            MinecraftClient.printCrashReport(crashReport);
        }
    }

    void initFont(boolean forcesUnicode) {
        this.fontManager.setIdOverrides((Map<Identifier, Identifier>)(forcesUnicode ? ImmutableMap.of((Object)DEFAULT_FONT_ID, (Object)UNICODE_FONT_ID) : ImmutableMap.of()));
    }

    private void initializeSearchableContainers() {
        TextSearchableContainer<ItemStack> textSearchableContainer = new TextSearchableContainer<ItemStack>(stack -> stack.getTooltip(null, TooltipContext.Default.NORMAL).stream().map(tooltip -> Formatting.strip(tooltip.getString()).trim()).filter(string -> !string.isEmpty()), stack -> Stream.of(Registry.ITEM.getId(stack.getItem())));
        IdentifierSearchableContainer<ItemStack> identifierSearchableContainer = new IdentifierSearchableContainer<ItemStack>(stack -> stack.streamTags().map(TagKey::id));
        DefaultedList<ItemStack> defaultedList = DefaultedList.of();
        for (Item item : Registry.ITEM) {
            item.appendStacks(ItemGroup.SEARCH, defaultedList);
        }
        defaultedList.forEach(stack -> {
            textSearchableContainer.add((ItemStack)stack);
            identifierSearchableContainer.add((ItemStack)stack);
        });
        TextSearchableContainer<RecipeResultCollection> textSearchableContainer2 = new TextSearchableContainer<RecipeResultCollection>(resultCollection -> resultCollection.getAllRecipes().stream().flatMap(recipe -> recipe.getOutput().getTooltip(null, TooltipContext.Default.NORMAL).stream()).map(tooltip -> Formatting.strip(tooltip.getString()).trim()).filter(string -> !string.isEmpty()), resultCollection -> resultCollection.getAllRecipes().stream().map(recipe -> Registry.ITEM.getId(recipe.getOutput().getItem())));
        this.searchManager.put(SearchManager.ITEM_TOOLTIP, textSearchableContainer);
        this.searchManager.put(SearchManager.ITEM_TAG, identifierSearchableContainer);
        this.searchManager.put(SearchManager.RECIPE_OUTPUT, textSearchableContainer2);
    }

    private void handleGlErrorByDisableVsync(int error, long description) {
        this.options.enableVsync = false;
        this.options.write();
    }

    private static boolean checkIs64Bit() {
        String[] strings;
        for (String string : strings = new String[]{"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"}) {
            String string2 = System.getProperty(string);
            if (string2 == null || !string2.contains("64")) continue;
            return true;
        }
        return false;
    }

    public Framebuffer getFramebuffer() {
        return this.framebuffer;
    }

    public String getGameVersion() {
        return this.gameVersion;
    }

    public String getVersionType() {
        return this.versionType;
    }

    public void setCrashReportSupplier(Supplier<CrashReport> crashReportSupplier) {
        this.crashReportSupplier = crashReportSupplier;
    }

    public static void printCrashReport(CrashReport report) {
        File file = new File(MinecraftClient.getInstance().runDirectory, "crash-reports");
        File file2 = new File(file, "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-client.txt");
        Bootstrap.println(report.asString());
        if (report.getFile() != null) {
            Bootstrap.println("#@!@# Game crashed! Crash report saved to: #@!@# " + report.getFile());
            System.exit(-1);
        } else if (report.writeToFile(file2)) {
            Bootstrap.println("#@!@# Game crashed! Crash report saved to: #@!@# " + file2.getAbsolutePath());
            System.exit(-1);
        } else {
            Bootstrap.println("#@?@# Game crashed! Crash report could not be saved. #@?@#");
            System.exit(-2);
        }
    }

    public boolean forcesUnicodeFont() {
        return this.options.forceUnicodeFont;
    }

    public CompletableFuture<Void> reloadResources() {
        return this.reloadResources(false);
    }

    private CompletableFuture<Void> reloadResources(boolean force) {
        if (this.resourceReloadFuture != null) {
            return this.resourceReloadFuture;
        }
        CompletableFuture<Void> completableFuture = new CompletableFuture<Void>();
        if (!force && this.overlay instanceof SplashOverlay) {
            this.resourceReloadFuture = completableFuture;
            return completableFuture;
        }
        this.resourcePackManager.scanPacks();
        List<ResourcePack> list = this.resourcePackManager.createResourcePacks();
        if (!force) {
            this.resourceReloadLogger.reload(ResourceReloadLogger.ReloadReason.MANUAL, list);
        }
        this.setOverlay(new SplashOverlay(this, this.resourceManager.reload(Util.getMainWorkerExecutor(), this, COMPLETED_UNIT_FUTURE, list), throwable -> Util.ifPresentOrElse(throwable, this::handleResourceReloadException, () -> {
            this.worldRenderer.reload();
            this.resourceReloadLogger.finish();
            completableFuture.complete(null);
        }), true));
        return completableFuture;
    }

    private void checkGameData() {
        boolean bl = false;
        BlockModels blockModels = this.getBlockRenderManager().getModels();
        BakedModel bakedModel = blockModels.getModelManager().getMissingModel();
        for (Block block : Registry.BLOCK) {
            for (BlockState blockState : block.getStateManager().getStates()) {
                BakedModel bakedModel2;
                if (blockState.getRenderType() != BlockRenderType.MODEL || (bakedModel2 = blockModels.getModel(blockState)) != bakedModel) continue;
                LOGGER.debug("Missing model for: {}", (Object)blockState);
                bl = true;
            }
        }
        Sprite sprite = bakedModel.getParticleSprite();
        for (Block block2 : Registry.BLOCK) {
            for (BlockState blockState2 : block2.getStateManager().getStates()) {
                Sprite sprite2 = blockModels.getModelParticleSprite(blockState2);
                if (blockState2.isAir() || sprite2 != sprite) continue;
                LOGGER.debug("Missing particle icon for: {}", (Object)blockState2);
                bl = true;
            }
        }
        DefaultedList<ItemStack> defaultedList = DefaultedList.of();
        for (Item item : Registry.ITEM) {
            defaultedList.clear();
            item.appendStacks(ItemGroup.SEARCH, defaultedList);
            for (ItemStack itemStack : defaultedList) {
                String string = itemStack.getTranslationKey();
                String string2 = new TranslatableText(string).getString();
                if (!string2.toLowerCase(Locale.ROOT).equals(item.getTranslationKey())) continue;
                LOGGER.debug("Missing translation for: {} {} {}", new Object[]{itemStack, string, itemStack.getItem()});
            }
        }
        bl |= HandledScreens.isMissingScreens();
        if (bl |= EntityRenderers.isMissingRendererFactories()) {
            throw new IllegalStateException("Your game data is foobar, fix the errors above!");
        }
    }

    public LevelStorage getLevelStorage() {
        return this.levelStorage;
    }

    private void openChatScreen(String text) {
        ChatRestriction chatRestriction = this.getChatRestriction();
        if (!chatRestriction.allowsChat(this.isInSingleplayer())) {
            this.inGameHud.setOverlayMessage(chatRestriction.getDescription(), false);
        } else {
            this.setScreen(new ChatScreen(text));
        }
    }

    public void setScreen(@Nullable Screen screen) {
        if (SharedConstants.isDevelopment && Thread.currentThread() != this.thread) {
            LOGGER.error("setScreen called from non-game thread");
        }
        if (this.currentScreen != null) {
            this.currentScreen.removed();
        }
        if (screen == null && this.world == null) {
            screen = new TitleScreen();
        } else if (screen == null && this.player.isDead()) {
            if (this.player.showsDeathScreen()) {
                screen = new DeathScreen(null, this.world.getLevelProperties().isHardcore());
            } else {
                this.player.requestRespawn();
            }
        }
        this.currentScreen = screen;
        BufferRenderer.unbindAll();
        if (screen != null) {
            this.mouse.unlockCursor();
            KeyBinding.unpressAll();
            screen.init(this, this.window.getScaledWidth(), this.window.getScaledHeight());
            this.skipGameRender = false;
        } else {
            this.soundManager.resumeAll();
            this.mouse.lockCursor();
        }
        this.updateWindowTitle();
    }

    public void setOverlay(@Nullable Overlay overlay) {
        this.overlay = overlay;
    }

    public void stop() {
        try {
            LOGGER.info("Stopping!");
            try {
                NarratorManager.INSTANCE.destroy();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            try {
                if (this.world != null) {
                    this.world.disconnect();
                }
                this.disconnect();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            if (this.currentScreen != null) {
                this.currentScreen.removed();
            }
            this.close();
        }
        finally {
            Util.nanoTimeSupplier = System::nanoTime;
            if (this.crashReportSupplier == null) {
                System.exit(0);
            }
        }
    }

    @Override
    public void close() {
        try {
            this.regionalComplianciesManager.close();
            this.bakedModelManager.close();
            this.fontManager.close();
            this.gameRenderer.close();
            this.worldRenderer.close();
            this.soundManager.close();
            this.resourcePackManager.close();
            this.particleManager.clearAtlas();
            this.statusEffectSpriteManager.close();
            this.paintingManager.close();
            this.textureManager.close();
            this.resourceManager.close();
            Util.shutdownExecutors();
        }
        catch (Throwable throwable) {
            LOGGER.error("Shutdown failure!", throwable);
            throw throwable;
        }
        finally {
            this.windowProvider.close();
            this.window.close();
        }
    }

    private void render(boolean tick) {
        boolean bl;
        int j;
        Runnable runnable;
        this.window.setPhase("Pre render");
        long l = Util.getMeasuringTimeNano();
        if (this.window.shouldClose()) {
            this.scheduleStop();
        }
        if (this.resourceReloadFuture != null && !(this.overlay instanceof SplashOverlay)) {
            CompletableFuture<Void> completableFuture = this.resourceReloadFuture;
            this.resourceReloadFuture = null;
            this.reloadResources().thenRun(() -> completableFuture.complete(null));
        }
        while ((runnable = this.renderTaskQueue.poll()) != null) {
            runnable.run();
        }
        if (tick) {
            int i = this.renderTickCounter.beginRenderTick(Util.getMeasuringTimeMs());
            this.profiler.push("scheduledExecutables");
            this.runTasks();
            this.profiler.pop();
            this.profiler.push("tick");
            for (j = 0; j < Math.min(10, i); ++j) {
                this.profiler.visit("clientTick");
                this.tick();
            }
            this.profiler.pop();
        }
        this.mouse.updateMouse();
        this.window.setPhase("Render");
        this.profiler.push("sound");
        this.soundManager.updateListenerPosition(this.gameRenderer.getCamera());
        this.profiler.pop();
        this.profiler.push("render");
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.clear(16640, IS_SYSTEM_MAC);
        this.framebuffer.beginWrite(true);
        BackgroundRenderer.clearFog();
        this.profiler.push("display");
        RenderSystem.enableTexture();
        RenderSystem.enableCull();
        this.profiler.pop();
        if (!this.skipGameRender) {
            this.profiler.swap("gameRenderer");
            this.gameRenderer.render(this.paused ? this.pausedTickDelta : this.renderTickCounter.tickDelta, l, tick);
            this.profiler.swap("toasts");
            this.toastManager.draw(new MatrixStack());
            this.profiler.pop();
        }
        if (this.tickProfilerResult != null) {
            this.profiler.push("fpsPie");
            this.drawProfilerResults(new MatrixStack(), this.tickProfilerResult);
            this.profiler.pop();
        }
        this.profiler.push("blit");
        this.framebuffer.endWrite();
        matrixStack.pop();
        matrixStack.push();
        RenderSystem.applyModelViewMatrix();
        this.framebuffer.draw(this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
        this.profiler.swap("updateDisplay");
        this.window.swapBuffers();
        j = this.getFramerateLimit();
        if ((double)j < Option.FRAMERATE_LIMIT.getMax()) {
            RenderSystem.limitDisplayFPS(j);
        }
        this.profiler.swap("yield");
        Thread.yield();
        this.profiler.pop();
        this.window.setPhase("Post render");
        ++this.fpsCounter;
        boolean bl2 = bl = this.isIntegratedServerRunning() && (this.currentScreen != null && this.currentScreen.shouldPause() || this.overlay != null && this.overlay.pausesGame()) && !this.server.isRemote();
        if (this.paused != bl) {
            if (this.paused) {
                this.pausedTickDelta = this.renderTickCounter.tickDelta;
            } else {
                this.renderTickCounter.tickDelta = this.pausedTickDelta;
            }
            this.paused = bl;
        }
        long m = Util.getMeasuringTimeNano();
        this.metricsData.pushSample(m - this.lastMetricsSampleTime);
        this.lastMetricsSampleTime = m;
        this.profiler.push("fpsUpdate");
        while (Util.getMeasuringTimeMs() >= this.nextDebugInfoUpdateTime + 1000L) {
            currentFps = this.fpsCounter;
            this.fpsDebugString = String.format("%d fps T: %s%s%s%s B: %d", new Object[]{currentFps, (double)this.options.maxFps == Option.FRAMERATE_LIMIT.getMax() ? "inf" : Integer.valueOf(this.options.maxFps), this.options.enableVsync ? " vsync" : "", this.options.graphicsMode, this.options.cloudRenderMode == CloudRenderMode.OFF ? "" : (this.options.cloudRenderMode == CloudRenderMode.FAST ? " fast-clouds" : " fancy-clouds"), this.options.biomeBlendRadius});
            this.nextDebugInfoUpdateTime += 1000L;
            this.fpsCounter = 0;
        }
        this.profiler.pop();
    }

    private boolean shouldMonitorTickDuration() {
        return this.options.debugEnabled && this.options.debugProfilerEnabled && !this.options.hudHidden;
    }

    private Profiler startMonitor(boolean active, @Nullable TickDurationMonitor monitor) {
        Profiler profiler;
        if (!active) {
            this.tickTimeTracker.disable();
            if (!this.recorder.isActive() && monitor == null) {
                return DummyProfiler.INSTANCE;
            }
        }
        if (active) {
            if (!this.tickTimeTracker.isActive()) {
                this.trackingTick = 0;
                this.tickTimeTracker.enable();
            }
            ++this.trackingTick;
            profiler = this.tickTimeTracker.getProfiler();
        } else {
            profiler = DummyProfiler.INSTANCE;
        }
        if (this.recorder.isActive()) {
            profiler = Profiler.union(profiler, this.recorder.getProfiler());
        }
        return TickDurationMonitor.tickProfiler(profiler, monitor);
    }

    private void endMonitor(boolean active, @Nullable TickDurationMonitor monitor) {
        if (monitor != null) {
            monitor.endTick();
        }
        this.tickProfilerResult = active ? this.tickTimeTracker.getResult() : null;
        this.profiler = this.tickTimeTracker.getProfiler();
    }

    @Override
    public void onResolutionChanged() {
        int i = this.window.calculateScaleFactor(this.options.guiScale, this.forcesUnicodeFont());
        this.window.setScaleFactor(i);
        if (this.currentScreen != null) {
            this.currentScreen.resize(this, this.window.getScaledWidth(), this.window.getScaledHeight());
        }
        Framebuffer framebuffer = this.getFramebuffer();
        framebuffer.resize(this.window.getFramebufferWidth(), this.window.getFramebufferHeight(), IS_SYSTEM_MAC);
        this.gameRenderer.onResized(this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
        this.mouse.onResolutionChanged();
    }

    @Override
    public void onCursorEnterChanged() {
        this.mouse.setResolutionChanged();
    }

    private int getFramerateLimit() {
        if (this.world == null && (this.currentScreen != null || this.overlay != null)) {
            return 60;
        }
        return this.window.getFramerateLimit();
    }

    public void cleanUpAfterCrash() {
        try {
            CrashMemoryReserve.releaseMemory();
            this.worldRenderer.cleanUp();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            System.gc();
            if (this.integratedServerRunning && this.server != null) {
                this.server.stop(true);
            }
            this.disconnect(new SaveLevelScreen(new TranslatableText("menu.savingLevel")));
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        System.gc();
    }

    public boolean toggleDebugProfiler(Consumer<TranslatableText> chatMessageSender) {
        Consumer<Path> consumer4;
        if (this.recorder.isActive()) {
            this.stopRecorder();
            return false;
        }
        Consumer<ProfileResult> consumer = result -> {
            int i = result.getTickSpan();
            double d = (double)result.getTimeSpan() / (double)TimeHelper.SECOND_IN_NANOS;
            this.execute(() -> chatMessageSender.accept(new TranslatableText("commands.debug.stopped", String.format(Locale.ROOT, "%.2f", d), i, String.format(Locale.ROOT, "%.2f", (double)i / d))));
        };
        Consumer<Path> consumer2 = path -> {
            MutableText text = new LiteralText(path.toString()).formatted(Formatting.UNDERLINE).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, path.toFile().getParent())));
            this.execute(() -> chatMessageSender.accept(new TranslatableText("debug.profiling.stop", text)));
        };
        SystemDetails systemDetails = MinecraftClient.addSystemDetailsToCrashReport(new SystemDetails(), this, this.languageManager, this.gameVersion, this.options);
        Consumer<List> consumer3 = files -> {
            Path path = this.saveProfilingResult(systemDetails, (List<Path>)files);
            consumer2.accept(path);
        };
        if (this.server == null) {
            consumer4 = path -> consumer3.accept((List)ImmutableList.of((Object)path));
        } else {
            this.server.addSystemDetails(systemDetails);
            CompletableFuture completableFuture = new CompletableFuture();
            CompletableFuture completableFuture2 = new CompletableFuture();
            CompletableFuture.allOf(completableFuture, completableFuture2).thenRunAsync(() -> consumer3.accept((List)ImmutableList.of((Object)((Path)completableFuture.join()), (Object)((Path)completableFuture2.join()))), Util.getIoWorkerExecutor());
            this.server.setupRecorder(result -> {}, completableFuture2::complete);
            consumer4 = completableFuture::complete;
        }
        this.recorder = DebugRecorder.of(new ClientSamplerSource(Util.nanoTimeSupplier, this.worldRenderer), Util.nanoTimeSupplier, Util.getIoWorkerExecutor(), new RecordDumper("client"), result -> {
            this.recorder = DummyRecorder.INSTANCE;
            consumer.accept((ProfileResult)result);
        }, consumer4);
        return true;
    }

    private void stopRecorder() {
        this.recorder.stop();
        if (this.server != null) {
            this.server.stopRecorder();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Path saveProfilingResult(SystemDetails details, List<Path> files) {
        Path path;
        String string = this.isInSingleplayer() ? this.getServer().getSaveProperties().getLevelName() : this.getCurrentServerEntry().name;
        try {
            String string2 = String.format("%s-%s-%s", new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()), string, SharedConstants.getGameVersion().getId());
            String string3 = FileNameUtil.getNextUniqueName(RecordDumper.DEBUG_PROFILING_DIRECTORY, string2, ".zip");
            path = RecordDumper.DEBUG_PROFILING_DIRECTORY.resolve(string3);
        }
        catch (IOException iOException) {
            throw new UncheckedIOException(iOException);
        }
        try (ZipCompressor zipCompressor = new ZipCompressor(path);){
            zipCompressor.write(Paths.get("system.txt", new String[0]), details.collect());
            zipCompressor.write(Paths.get("client", new String[0]).resolve(this.options.getOptionsFile().getName()), this.options.collectProfiledOptions());
            files.forEach(zipCompressor::copyAll);
        }
        finally {
            for (Path path2 : files) {
                try {
                    FileUtils.forceDelete((File)path2.toFile());
                }
                catch (IOException iOException2) {
                    LOGGER.warn("Failed to delete temporary profiling result {}", (Object)path2, (Object)iOException2);
                }
            }
        }
        return path;
    }

    public void handleProfilerKeyPress(int digit) {
        if (this.tickProfilerResult == null) {
            return;
        }
        List<ProfilerTiming> list = this.tickProfilerResult.getTimings(this.openProfilerSection);
        if (list.isEmpty()) {
            return;
        }
        ProfilerTiming profilerTiming = list.remove(0);
        if (digit == 0) {
            int i;
            if (!profilerTiming.name.isEmpty() && (i = this.openProfilerSection.lastIndexOf(30)) >= 0) {
                this.openProfilerSection = this.openProfilerSection.substring(0, i);
            }
        } else if (--digit < list.size() && !"unspecified".equals(list.get((int)digit).name)) {
            if (!this.openProfilerSection.isEmpty()) {
                this.openProfilerSection = this.openProfilerSection + "\u001e";
            }
            this.openProfilerSection = this.openProfilerSection + list.get((int)digit).name;
        }
    }

    private void drawProfilerResults(MatrixStack matrices, ProfileResult profileResult) {
        int m;
        List<ProfilerTiming> list = profileResult.getTimings(this.openProfilerSection);
        ProfilerTiming profilerTiming = list.remove(0);
        RenderSystem.clear(256, IS_SYSTEM_MAC);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Matrix4f matrix4f = Matrix4f.projectionMatrix(0.0f, this.window.getFramebufferWidth(), 0.0f, this.window.getFramebufferHeight(), 1000.0f, 3000.0f);
        RenderSystem.setProjectionMatrix(matrix4f);
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.loadIdentity();
        matrixStack.translate(0.0, 0.0, -2000.0);
        RenderSystem.applyModelViewMatrix();
        RenderSystem.lineWidth(1.0f);
        RenderSystem.disableTexture();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        int i = 160;
        int j = this.window.getFramebufferWidth() - 160 - 10;
        int k = this.window.getFramebufferHeight() - 320;
        RenderSystem.enableBlend();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex((float)j - 176.0f, (float)k - 96.0f - 16.0f, 0.0).color(200, 0, 0, 0).next();
        bufferBuilder.vertex((float)j - 176.0f, k + 320, 0.0).color(200, 0, 0, 0).next();
        bufferBuilder.vertex((float)j + 176.0f, k + 320, 0.0).color(200, 0, 0, 0).next();
        bufferBuilder.vertex((float)j + 176.0f, (float)k - 96.0f - 16.0f, 0.0).color(200, 0, 0, 0).next();
        tessellator.draw();
        RenderSystem.disableBlend();
        double d = 0.0;
        for (ProfilerTiming profilerTiming2 : list) {
            float h;
            float g;
            float f;
            int q;
            int l = MathHelper.floor(profilerTiming2.parentSectionUsagePercentage / 4.0) + 1;
            bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
            m = profilerTiming2.getColor();
            int n = m >> 16 & 0xFF;
            int o = m >> 8 & 0xFF;
            int p = m & 0xFF;
            bufferBuilder.vertex(j, k, 0.0).color(n, o, p, 255).next();
            for (q = l; q >= 0; --q) {
                f = (float)((d + profilerTiming2.parentSectionUsagePercentage * (double)q / (double)l) * 6.2831854820251465 / 100.0);
                g = MathHelper.sin(f) * 160.0f;
                h = MathHelper.cos(f) * 160.0f * 0.5f;
                bufferBuilder.vertex((float)j + g, (float)k - h, 0.0).color(n, o, p, 255).next();
            }
            tessellator.draw();
            bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
            for (q = l; q >= 0; --q) {
                f = (float)((d + profilerTiming2.parentSectionUsagePercentage * (double)q / (double)l) * 6.2831854820251465 / 100.0);
                g = MathHelper.sin(f) * 160.0f;
                h = MathHelper.cos(f) * 160.0f * 0.5f;
                if (h > 0.0f) continue;
                bufferBuilder.vertex((float)j + g, (float)k - h, 0.0).color(n >> 1, o >> 1, p >> 1, 255).next();
                bufferBuilder.vertex((float)j + g, (float)k - h + 10.0f, 0.0).color(n >> 1, o >> 1, p >> 1, 255).next();
            }
            tessellator.draw();
            d += profilerTiming2.parentSectionUsagePercentage;
        }
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
        decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
        RenderSystem.enableTexture();
        String string = ProfileResult.getHumanReadableName(profilerTiming.name);
        Object string2 = "";
        if (!"unspecified".equals(string)) {
            string2 = (String)string2 + "[0] ";
        }
        string2 = string.isEmpty() ? (String)string2 + "ROOT " : (String)string2 + string + " ";
        m = 0xFFFFFF;
        this.textRenderer.drawWithShadow(matrices, (String)string2, (float)(j - 160), (float)(k - 80 - 16), 0xFFFFFF);
        string2 = decimalFormat.format(profilerTiming.totalUsagePercentage) + "%";
        this.textRenderer.drawWithShadow(matrices, (String)string2, (float)(j + 160 - this.textRenderer.getWidth((String)string2)), (float)(k - 80 - 16), 0xFFFFFF);
        for (int r = 0; r < list.size(); ++r) {
            ProfilerTiming profilerTiming3 = list.get(r);
            StringBuilder stringBuilder = new StringBuilder();
            if ("unspecified".equals(profilerTiming3.name)) {
                stringBuilder.append("[?] ");
            } else {
                stringBuilder.append("[").append(r + 1).append("] ");
            }
            Object string3 = stringBuilder.append(profilerTiming3.name).toString();
            this.textRenderer.drawWithShadow(matrices, (String)string3, (float)(j - 160), (float)(k + 80 + r * 8 + 20), profilerTiming3.getColor());
            string3 = decimalFormat.format(profilerTiming3.parentSectionUsagePercentage) + "%";
            this.textRenderer.drawWithShadow(matrices, (String)string3, (float)(j + 160 - 50 - this.textRenderer.getWidth((String)string3)), (float)(k + 80 + r * 8 + 20), profilerTiming3.getColor());
            string3 = decimalFormat.format(profilerTiming3.totalUsagePercentage) + "%";
            this.textRenderer.drawWithShadow(matrices, (String)string3, (float)(j + 160 - this.textRenderer.getWidth((String)string3)), (float)(k + 80 + r * 8 + 20), profilerTiming3.getColor());
        }
    }

    public void scheduleStop() {
        this.running = false;
    }

    public boolean isRunning() {
        return this.running;
    }

    public void openPauseMenu(boolean pause) {
        boolean bl;
        if (this.currentScreen != null) {
            return;
        }
        boolean bl2 = bl = this.isIntegratedServerRunning() && !this.server.isRemote();
        if (bl) {
            this.setScreen(new GameMenuScreen(!pause));
            this.soundManager.pauseAll();
        } else {
            this.setScreen(new GameMenuScreen(true));
        }
    }

    private void handleBlockBreaking(boolean bl) {
        if (!bl) {
            this.attackCooldown = 0;
        }
        if (this.attackCooldown > 0 || this.player.isUsingItem()) {
            return;
        }
        if (bl && this.crosshairTarget != null && this.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            Direction direction;
            BlockHitResult blockHitResult = (BlockHitResult)this.crosshairTarget;
            BlockPos blockPos = blockHitResult.getBlockPos();
            if (!this.world.getBlockState(blockPos).isAir() && this.interactionManager.updateBlockBreakingProgress(blockPos, direction = blockHitResult.getSide())) {
                this.particleManager.addBlockBreakingParticles(blockPos, direction);
                this.player.swingHand(Hand.MAIN_HAND);
            }
            return;
        }
        this.interactionManager.cancelBlockBreaking();
    }

    private boolean doAttack() {
        if (this.attackCooldown > 0) {
            return false;
        }
        if (this.crosshairTarget == null) {
            LOGGER.error("Null returned as 'hitResult', this shouldn't happen!");
            if (this.interactionManager.hasLimitedAttackSpeed()) {
                this.attackCooldown = 10;
            }
            return false;
        }
        if (this.player.isRiding()) {
            return false;
        }
        boolean bl = false;
        switch (this.crosshairTarget.getType()) {
            case ENTITY: {
                this.interactionManager.attackEntity(this.player, ((EntityHitResult)this.crosshairTarget).getEntity());
                break;
            }
            case BLOCK: {
                BlockHitResult blockHitResult = (BlockHitResult)this.crosshairTarget;
                BlockPos blockPos = blockHitResult.getBlockPos();
                if (!this.world.getBlockState(blockPos).isAir()) {
                    this.interactionManager.attackBlock(blockPos, blockHitResult.getSide());
                    if (!this.world.getBlockState(blockPos).isAir()) break;
                    bl = true;
                    break;
                }
            }
            case MISS: {
                if (this.interactionManager.hasLimitedAttackSpeed()) {
                    this.attackCooldown = 10;
                }
                this.player.resetLastAttackedTicks();
            }
        }
        this.player.swingHand(Hand.MAIN_HAND);
        return bl;
    }

    private void doItemUse() {
        if (this.interactionManager.isBreakingBlock()) {
            return;
        }
        this.itemUseCooldown = 4;
        if (this.player.isRiding()) {
            return;
        }
        if (this.crosshairTarget == null) {
            LOGGER.warn("Null returned as 'hitResult', this shouldn't happen!");
        }
        for (Hand hand : Hand.values()) {
            ActionResult actionResult3;
            ItemStack itemStack = this.player.getStackInHand(hand);
            if (this.crosshairTarget != null) {
                switch (this.crosshairTarget.getType()) {
                    case ENTITY: {
                        EntityHitResult entityHitResult = (EntityHitResult)this.crosshairTarget;
                        Entity entity = entityHitResult.getEntity();
                        if (!this.world.getWorldBorder().contains(entity.getBlockPos())) {
                            return;
                        }
                        ActionResult actionResult = this.interactionManager.interactEntityAtLocation(this.player, entity, entityHitResult, hand);
                        if (!actionResult.isAccepted()) {
                            actionResult = this.interactionManager.interactEntity(this.player, entity, hand);
                        }
                        if (!actionResult.isAccepted()) break;
                        if (actionResult.shouldSwingHand()) {
                            this.player.swingHand(hand);
                        }
                        return;
                    }
                    case BLOCK: {
                        BlockHitResult blockHitResult = (BlockHitResult)this.crosshairTarget;
                        int i = itemStack.getCount();
                        ActionResult actionResult2 = this.interactionManager.interactBlock(this.player, this.world, hand, blockHitResult);
                        if (actionResult2.isAccepted()) {
                            if (actionResult2.shouldSwingHand()) {
                                this.player.swingHand(hand);
                                if (!itemStack.isEmpty() && (itemStack.getCount() != i || this.interactionManager.hasCreativeInventory())) {
                                    this.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
                                }
                            }
                            return;
                        }
                        if (actionResult2 != ActionResult.FAIL) break;
                        return;
                    }
                }
            }
            if (itemStack.isEmpty() || !(actionResult3 = this.interactionManager.interactItem(this.player, this.world, hand)).isAccepted()) continue;
            if (actionResult3.shouldSwingHand()) {
                this.player.swingHand(hand);
            }
            this.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
            return;
        }
    }

    public MusicTracker getMusicTracker() {
        return this.musicTracker;
    }

    public void tick() {
        if (this.itemUseCooldown > 0) {
            --this.itemUseCooldown;
        }
        this.profiler.push("gui");
        this.inGameHud.tick(this.paused);
        this.profiler.pop();
        this.gameRenderer.updateTargetedEntity(1.0f);
        this.tutorialManager.tick(this.world, this.crosshairTarget);
        this.profiler.push("gameMode");
        if (!this.paused && this.world != null) {
            this.interactionManager.tick();
        }
        this.profiler.swap("textures");
        if (this.world != null) {
            this.textureManager.tick();
        }
        if (this.currentScreen == null && this.player != null) {
            if (this.player.isDead() && !(this.currentScreen instanceof DeathScreen)) {
                this.setScreen(null);
            } else if (this.player.isSleeping() && this.world != null) {
                this.setScreen(new SleepingChatScreen());
            }
        } else {
            Screen screen = this.currentScreen;
            if (screen instanceof SleepingChatScreen) {
                SleepingChatScreen sleepingChatScreen = (SleepingChatScreen)screen;
                if (!this.player.isSleeping()) {
                    sleepingChatScreen.closeChatIfEmpty();
                }
            }
        }
        if (this.currentScreen != null) {
            this.attackCooldown = 10000;
        }
        if (this.currentScreen != null) {
            Screen.wrapScreenError(() -> this.currentScreen.tick(), "Ticking screen", this.currentScreen.getClass().getCanonicalName());
        }
        if (!this.options.debugEnabled) {
            this.inGameHud.resetDebugHudChunk();
        }
        if (this.overlay == null && (this.currentScreen == null || this.currentScreen.passEvents)) {
            this.profiler.swap("Keybindings");
            this.handleInputEvents();
            if (this.attackCooldown > 0) {
                --this.attackCooldown;
            }
        }
        if (this.world != null) {
            this.profiler.swap("gameRenderer");
            if (!this.paused) {
                this.gameRenderer.tick();
            }
            this.profiler.swap("levelRenderer");
            if (!this.paused) {
                this.worldRenderer.tick();
            }
            this.profiler.swap("level");
            if (!this.paused) {
                if (this.world.getLightningTicksLeft() > 0) {
                    this.world.setLightningTicksLeft(this.world.getLightningTicksLeft() - 1);
                }
                this.world.tickEntities();
            }
        } else if (this.gameRenderer.getShader() != null) {
            this.gameRenderer.disableShader();
        }
        if (!this.paused) {
            this.musicTracker.tick();
        }
        this.soundManager.tick(this.paused);
        if (this.world != null) {
            if (!this.paused) {
                if (!this.options.joinedFirstServer && this.isConnectedToServer()) {
                    TranslatableText text = new TranslatableText("tutorial.socialInteractions.title");
                    TranslatableText text2 = new TranslatableText("tutorial.socialInteractions.description", TutorialManager.keyToText("socialInteractions"));
                    this.socialInteractionsToast = new TutorialToast(TutorialToast.Type.SOCIAL_INTERACTIONS, text, text2, true);
                    this.tutorialManager.add(this.socialInteractionsToast, 160);
                    this.options.joinedFirstServer = true;
                    this.options.write();
                }
                this.tutorialManager.tick();
                try {
                    this.world.tick(() -> true);
                }
                catch (Throwable throwable) {
                    CrashReport crashReport = CrashReport.create(throwable, "Exception in world tick");
                    if (this.world == null) {
                        CrashReportSection crashReportSection = crashReport.addElement("Affected level");
                        crashReportSection.add("Problem", "Level is null!");
                    } else {
                        this.world.addDetailsToCrashReport(crashReport);
                    }
                    throw new CrashException(crashReport);
                }
            }
            this.profiler.swap("animateTick");
            if (!this.paused && this.world != null) {
                this.world.doRandomBlockDisplayTicks(this.player.getBlockX(), this.player.getBlockY(), this.player.getBlockZ());
            }
            this.profiler.swap("particles");
            if (!this.paused) {
                this.particleManager.tick();
            }
        } else if (this.integratedServerConnection != null) {
            this.profiler.swap("pendingConnection");
            this.integratedServerConnection.tick();
        }
        this.profiler.swap("keyboard");
        this.keyboard.pollDebugCrash();
        this.profiler.pop();
    }

    private boolean isConnectedToServer() {
        return !this.integratedServerRunning || this.server != null && this.server.isRemote();
    }

    private void handleInputEvents() {
        while (this.options.togglePerspectiveKey.wasPressed()) {
            Perspective perspective = this.options.getPerspective();
            this.options.setPerspective(this.options.getPerspective().next());
            if (perspective.isFirstPerson() != this.options.getPerspective().isFirstPerson()) {
                this.gameRenderer.onCameraEntitySet(this.options.getPerspective().isFirstPerson() ? this.getCameraEntity() : null);
            }
            this.worldRenderer.scheduleTerrainUpdate();
        }
        while (this.options.smoothCameraKey.wasPressed()) {
            this.options.smoothCameraEnabled = !this.options.smoothCameraEnabled;
        }
        for (int i = 0; i < 9; ++i) {
            boolean bl = this.options.saveToolbarActivatorKey.isPressed();
            boolean bl2 = this.options.loadToolbarActivatorKey.isPressed();
            if (!this.options.hotbarKeys[i].wasPressed()) continue;
            if (this.player.isSpectator()) {
                this.inGameHud.getSpectatorHud().selectSlot(i);
                continue;
            }
            if (this.player.isCreative() && this.currentScreen == null && (bl2 || bl)) {
                CreativeInventoryScreen.onHotbarKeyPress(this, i, bl2, bl);
                continue;
            }
            this.player.getInventory().selectedSlot = i;
        }
        while (this.options.socialInteractionsKey.wasPressed()) {
            if (!this.isConnectedToServer()) {
                this.player.sendMessage(SOCIAL_INTERACTIONS_NOT_AVAILABLE, true);
                NarratorManager.INSTANCE.narrate(SOCIAL_INTERACTIONS_NOT_AVAILABLE);
                continue;
            }
            if (this.socialInteractionsToast != null) {
                this.tutorialManager.remove(this.socialInteractionsToast);
                this.socialInteractionsToast = null;
            }
            this.setScreen(new SocialInteractionsScreen());
        }
        while (this.options.inventoryKey.wasPressed()) {
            if (this.interactionManager.hasRidingInventory()) {
                this.player.openRidingInventory();
                continue;
            }
            this.tutorialManager.onInventoryOpened();
            this.setScreen(new InventoryScreen(this.player));
        }
        while (this.options.advancementsKey.wasPressed()) {
            this.setScreen(new AdvancementsScreen(this.player.networkHandler.getAdvancementHandler()));
        }
        while (this.options.swapHandsKey.wasPressed()) {
            if (this.player.isSpectator()) continue;
            this.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN));
        }
        while (this.options.dropKey.wasPressed()) {
            if (this.player.isSpectator() || !this.player.dropSelectedItem(Screen.hasControlDown())) continue;
            this.player.swingHand(Hand.MAIN_HAND);
        }
        while (this.options.chatKey.wasPressed()) {
            this.openChatScreen("");
        }
        if (this.currentScreen == null && this.overlay == null && this.options.commandKey.wasPressed()) {
            this.openChatScreen("/");
        }
        boolean bl3 = false;
        if (this.player.isUsingItem()) {
            if (!this.options.useKey.isPressed()) {
                this.interactionManager.stopUsingItem(this.player);
            }
            while (this.options.attackKey.wasPressed()) {
            }
            while (this.options.useKey.wasPressed()) {
            }
            while (this.options.pickItemKey.wasPressed()) {
            }
        } else {
            while (this.options.attackKey.wasPressed()) {
                bl3 |= this.doAttack();
            }
            while (this.options.useKey.wasPressed()) {
                this.doItemUse();
            }
            while (this.options.pickItemKey.wasPressed()) {
                this.doItemPick();
            }
        }
        if (this.options.useKey.isPressed() && this.itemUseCooldown == 0 && !this.player.isUsingItem()) {
            this.doItemUse();
        }
        this.handleBlockBreaking(this.currentScreen == null && !bl3 && this.options.attackKey.isPressed() && this.mouse.isCursorLocked());
    }

    public TelemetrySender createTelemetrySender() {
        return new TelemetrySender(this, this.userApiService, this.session.getXuid(), this.session.getClientId(), this.deviceSessionId);
    }

    public void startIntegratedServer(String worldName) {
        this.startIntegratedServer(worldName, SaveLoader.DataPackSettingsSupplier::loadFromWorld, SaveLoader.SavePropertiesSupplier::loadFromWorld, false, WorldLoadAction.BACKUP);
    }

    public void createWorld(String worldName, LevelInfo levelInfo, DynamicRegistryManager dynamicRegistryManager, GeneratorOptions generatorOptions) {
        this.startIntegratedServer(worldName, session -> levelInfo::getDataPackSettings, session -> (resourceManager, dataPackSettings) -> {
            DynamicRegistryManager.Mutable mutable = DynamicRegistryManager.createAndLoad();
            RegistryOps dynamicOps = RegistryOps.of(JsonOps.INSTANCE, dynamicRegistryManager);
            RegistryOps dynamicOps2 = RegistryOps.ofLoaded(JsonOps.INSTANCE, mutable, resourceManager);
            DataResult dataResult = GeneratorOptions.CODEC.encodeStart(dynamicOps, (Object)generatorOptions).setLifecycle(Lifecycle.stable()).flatMap(json -> GeneratorOptions.CODEC.parse(dynamicOps2, json));
            GeneratorOptions generatorOptions2 = (GeneratorOptions)dataResult.getOrThrow(false, Util.addPrefix("Error reading worldgen settings after loading data packs: ", arg_0 -> ((Logger)LOGGER).error(arg_0)));
            return Pair.of((Object)new LevelProperties(levelInfo, generatorOptions2, dataResult.lifecycle()), (Object)mutable.toImmutable());
        }, false, WorldLoadAction.CREATE);
    }

    private void startIntegratedServer(String worldName, Function<LevelStorage.Session, SaveLoader.DataPackSettingsSupplier> dataPackSettingsSupplierGetter, Function<LevelStorage.Session, SaveLoader.SavePropertiesSupplier> savePropertiesSupplierGetter, boolean safeMode, WorldLoadAction worldLoadAction) {
        boolean bl2;
        SaveLoader saveLoader;
        LevelStorage.Session session;
        try {
            session = this.levelStorage.createSession(worldName);
        }
        catch (IOException iOException) {
            LOGGER.warn("Failed to read level {} data", (Object)worldName, (Object)iOException);
            SystemToast.addWorldAccessFailureToast(this, worldName);
            this.setScreen(null);
            return;
        }
        ResourcePackManager resourcePackManager = MinecraftClient.createServerDataManager(session);
        try {
            saveLoader = this.createSaveLoader(resourcePackManager, safeMode, dataPackSettingsSupplierGetter.apply(session), savePropertiesSupplierGetter.apply(session));
        }
        catch (Exception exception) {
            LOGGER.warn("Failed to load datapacks, can't proceed with server load", (Throwable)exception);
            this.setScreen(new DatapackFailureScreen(() -> this.startIntegratedServer(worldName, dataPackSettingsSupplierGetter, savePropertiesSupplierGetter, true, worldLoadAction)));
            try {
                resourcePackManager.close();
                session.close();
            }
            catch (IOException iOException2) {
                LOGGER.warn("Failed to unlock access to level {}", (Object)worldName, (Object)iOException2);
            }
            return;
        }
        SaveProperties saveProperties = saveLoader.saveProperties();
        boolean bl = saveProperties.getGeneratorOptions().isLegacyCustomizedType();
        boolean bl3 = bl2 = saveProperties.getLifecycle() != Lifecycle.stable();
        if (worldLoadAction != WorldLoadAction.NONE && (bl || bl2)) {
            this.showExperimentalWarning(worldLoadAction, worldName, bl, () -> this.startIntegratedServer(worldName, dataPackSettingsSupplierGetter, savePropertiesSupplierGetter, safeMode, WorldLoadAction.NONE));
            saveLoader.close();
            try {
                resourcePackManager.close();
                session.close();
            }
            catch (IOException iOException3) {
                LOGGER.warn("Failed to unlock access to level {}", (Object)worldName, (Object)iOException3);
            }
            return;
        }
        this.disconnect();
        this.worldGenProgressTracker.set(null);
        try {
            DynamicRegistryManager.Immutable immutable = saveLoader.dynamicRegistryManager();
            session.backupLevelDataFile(immutable, saveProperties);
            saveLoader.refresh();
            YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(this.networkProxy);
            MinecraftSessionService minecraftSessionService = yggdrasilAuthenticationService.createMinecraftSessionService();
            GameProfileRepository gameProfileRepository = yggdrasilAuthenticationService.createProfileRepository();
            UserCache userCache = new UserCache(gameProfileRepository, new File(this.runDirectory, MinecraftServer.USER_CACHE_FILE.getName()));
            userCache.setExecutor(this);
            SkullBlockEntity.setServices(userCache, minecraftSessionService, this);
            UserCache.setUseRemote(false);
            this.server = MinecraftServer.startServer(thread2 -> new IntegratedServer((Thread)thread2, this, session, resourcePackManager, saveLoader, minecraftSessionService, gameProfileRepository, userCache, spawnChunkRadius -> {
                WorldGenerationProgressTracker worldGenerationProgressTracker = new WorldGenerationProgressTracker(spawnChunkRadius + 0);
                this.worldGenProgressTracker.set(worldGenerationProgressTracker);
                return QueueingWorldGenerationProgressListener.create(worldGenerationProgressTracker, this.renderTaskQueue::add);
            }));
            this.integratedServerRunning = true;
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Starting integrated server");
            CrashReportSection crashReportSection = crashReport.addElement("Starting integrated server");
            crashReportSection.add("Level ID", worldName);
            crashReportSection.add("Level Name", saveProperties.getLevelName());
            throw new CrashException(crashReport);
        }
        while (this.worldGenProgressTracker.get() == null) {
            Thread.yield();
        }
        LevelLoadingScreen levelLoadingScreen = new LevelLoadingScreen(this.worldGenProgressTracker.get());
        this.setScreen(levelLoadingScreen);
        this.profiler.push("waitForServer");
        while (!this.server.isLoading()) {
            levelLoadingScreen.tick();
            this.render(false);
            try {
                Thread.sleep(16L);
            }
            catch (InterruptedException crashReport) {
                // empty catch block
            }
            if (this.crashReportSupplier == null) continue;
            MinecraftClient.printCrashReport(this.crashReportSupplier.get());
            return;
        }
        this.profiler.pop();
        SocketAddress socketAddress = this.server.getNetworkIo().bindLocal();
        ClientConnection clientConnection = ClientConnection.connectLocal(socketAddress);
        clientConnection.setPacketListener(new ClientLoginNetworkHandler(clientConnection, this, null, text -> {}));
        clientConnection.send(new HandshakeC2SPacket(socketAddress.toString(), 0, NetworkState.LOGIN));
        clientConnection.send(new LoginHelloC2SPacket(this.getSession().getProfile()));
        this.integratedServerConnection = clientConnection;
    }

    private void showExperimentalWarning(WorldLoadAction worldLoadAction, String levelName, boolean legacyCustomized, Runnable onConfirm) {
        if (worldLoadAction == WorldLoadAction.BACKUP) {
            TranslatableText text2;
            TranslatableText text;
            if (legacyCustomized) {
                text = new TranslatableText("selectWorld.backupQuestion.customized");
                text2 = new TranslatableText("selectWorld.backupWarning.customized");
            } else {
                text = new TranslatableText("selectWorld.backupQuestion.experimental");
                text2 = new TranslatableText("selectWorld.backupWarning.experimental");
            }
            this.setScreen(new BackupPromptScreen(null, (shouldBackup, eraseCache) -> {
                if (shouldBackup) {
                    EditWorldScreen.onBackupConfirm(this.levelStorage, levelName);
                }
                onConfirm.run();
            }, text, text2, false));
        } else {
            this.setScreen(new ConfirmScreen(confirmed -> {
                if (confirmed) {
                    onConfirm.run();
                } else {
                    this.setScreen(null);
                    try (LevelStorage.Session session = this.levelStorage.createSession(levelName);){
                        session.deleteSessionLock();
                    }
                    catch (IOException iOException) {
                        SystemToast.addWorldDeleteFailureToast(this, levelName);
                        LOGGER.error("Failed to delete world {}", (Object)levelName, (Object)iOException);
                    }
                }
            }, new TranslatableText("selectWorld.backupQuestion.experimental"), new TranslatableText("selectWorld.backupWarning.experimental"), ScreenTexts.PROCEED, ScreenTexts.CANCEL));
        }
    }

    public SaveLoader createSaveLoader(LevelStorage.Session session, boolean safeMode) throws ExecutionException, InterruptedException {
        ResourcePackManager resourcePackManager = MinecraftClient.createServerDataManager(session);
        return this.createSaveLoader(resourcePackManager, safeMode, SaveLoader.DataPackSettingsSupplier.loadFromWorld(session), SaveLoader.SavePropertiesSupplier.loadFromWorld(session));
    }

    public SaveLoader createSaveLoader(ResourcePackManager dataPackManager, boolean safeMode, SaveLoader.DataPackSettingsSupplier dataPackSettingsSupplier, SaveLoader.SavePropertiesSupplier savePropertiesSupplier) throws InterruptedException, ExecutionException {
        try {
            SaveLoader.FunctionLoaderConfig functionLoaderConfig = new SaveLoader.FunctionLoaderConfig(dataPackManager, CommandManager.RegistrationEnvironment.INTEGRATED, 2, safeMode);
            CompletableFuture<SaveLoader> completableFuture = SaveLoader.ofLoaded(functionLoaderConfig, dataPackSettingsSupplier, savePropertiesSupplier, Util.getMainWorkerExecutor(), this);
            this.runTasks(completableFuture::isDone);
            return completableFuture.get();
        }
        catch (InterruptedException | ExecutionException exception) {
            dataPackManager.close();
            throw exception;
        }
    }

    private static ResourcePackManager createServerDataManager(LevelStorage.Session session) {
        return new ResourcePackManager(ResourceType.SERVER_DATA, new VanillaDataPackProvider(), new FileResourcePackProvider(session.getDirectory(WorldSavePath.DATAPACKS).toFile(), ResourcePackSource.PACK_SOURCE_WORLD));
    }

    public void joinWorld(ClientWorld world) {
        ProgressScreen progressScreen = new ProgressScreen(true);
        progressScreen.setTitle(new TranslatableText("connect.joining"));
        this.reset(progressScreen);
        this.world = world;
        this.setWorld(world);
        if (!this.integratedServerRunning) {
            YggdrasilAuthenticationService authenticationService = new YggdrasilAuthenticationService(this.networkProxy);
            MinecraftSessionService minecraftSessionService = authenticationService.createMinecraftSessionService();
            GameProfileRepository gameProfileRepository = authenticationService.createProfileRepository();
            UserCache userCache = new UserCache(gameProfileRepository, new File(this.runDirectory, MinecraftServer.USER_CACHE_FILE.getName()));
            userCache.setExecutor(this);
            SkullBlockEntity.setServices(userCache, minecraftSessionService, this);
            UserCache.setUseRemote(false);
        }
    }

    public void disconnect() {
        this.disconnect(new ProgressScreen(true));
    }

    public void disconnect(Screen screen) {
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.getNetworkHandler();
        if (clientPlayNetworkHandler != null) {
            this.cancelTasks();
            clientPlayNetworkHandler.clearWorld();
        }
        this.socialInteractionsManager.unloadBlockList();
        IntegratedServer integratedServer = this.server;
        this.server = null;
        this.gameRenderer.reset();
        this.interactionManager = null;
        NarratorManager.INSTANCE.clear();
        this.reset(screen);
        if (this.world != null) {
            if (integratedServer != null) {
                this.profiler.push("waitForServer");
                while (!integratedServer.isStopping()) {
                    this.render(false);
                }
                this.profiler.pop();
            }
            this.builtinPackProvider.clear();
            this.inGameHud.clear();
            this.currentServerEntry = null;
            this.integratedServerRunning = false;
            this.game.onLeaveGameSession();
        }
        this.world = null;
        this.setWorld(null);
        this.player = null;
        SkullBlockEntity.clearServices();
    }

    private void reset(Screen screen) {
        this.profiler.push("forcedTick");
        this.soundManager.stopAll();
        this.cameraEntity = null;
        this.integratedServerConnection = null;
        this.setScreen(screen);
        this.render(false);
        this.profiler.pop();
    }

    public void setScreenAndRender(Screen screen) {
        this.profiler.push("forcedTick");
        this.setScreen(screen);
        this.render(false);
        this.profiler.pop();
    }

    private void setWorld(@Nullable ClientWorld world) {
        this.worldRenderer.setWorld(world);
        this.particleManager.setWorld(world);
        this.blockEntityRenderDispatcher.setWorld(world);
        this.updateWindowTitle();
    }

    public boolean isMultiplayerEnabled() {
        return this.multiplayerEnabled && this.userApiService.properties().flag(UserApiService.UserFlag.SERVERS_ALLOWED);
    }

    public boolean isRealmsEnabled() {
        return this.userApiService.properties().flag(UserApiService.UserFlag.REALMS_ALLOWED);
    }

    public boolean shouldBlockMessages(UUID sender) {
        if (!this.getChatRestriction().allowsChat(false)) {
            return (this.player == null || !sender.equals(this.player.getUuid())) && !sender.equals(Util.NIL_UUID);
        }
        return this.socialInteractionsManager.isPlayerMuted(sender);
    }

    public ChatRestriction getChatRestriction() {
        if (this.options.chatVisibility == ChatVisibility.HIDDEN) {
            return ChatRestriction.DISABLED_BY_OPTIONS;
        }
        if (!this.onlineChatEnabled) {
            return ChatRestriction.DISABLED_BY_LAUNCHER;
        }
        if (!this.userApiService.properties().flag(UserApiService.UserFlag.CHAT_ALLOWED)) {
            return ChatRestriction.DISABLED_BY_PROFILE;
        }
        return ChatRestriction.ENABLED;
    }

    public final boolean isDemo() {
        return this.isDemo;
    }

    @Nullable
    public ClientPlayNetworkHandler getNetworkHandler() {
        return this.player == null ? null : this.player.networkHandler;
    }

    public static boolean isHudEnabled() {
        return !MinecraftClient.instance.options.hudHidden;
    }

    public static boolean isFancyGraphicsOrBetter() {
        return MinecraftClient.instance.options.graphicsMode.getId() >= GraphicsMode.FANCY.getId();
    }

    public static boolean isFabulousGraphicsOrBetter() {
        return !MinecraftClient.instance.gameRenderer.isRenderingPanorama() && MinecraftClient.instance.options.graphicsMode.getId() >= GraphicsMode.FABULOUS.getId();
    }

    public static boolean isAmbientOcclusionEnabled() {
        return MinecraftClient.instance.options.ao != AoMode.OFF;
    }

    private void doItemPick() {
        ItemStack itemStack;
        if (this.crosshairTarget == null || this.crosshairTarget.getType() == HitResult.Type.MISS) {
            return;
        }
        boolean bl = this.player.getAbilities().creativeMode;
        BlockEntity blockEntity = null;
        HitResult.Type type = this.crosshairTarget.getType();
        if (type == HitResult.Type.BLOCK) {
            BlockPos blockPos = ((BlockHitResult)this.crosshairTarget).getBlockPos();
            BlockState blockState = this.world.getBlockState(blockPos);
            if (blockState.isAir()) {
                return;
            }
            Block block = blockState.getBlock();
            itemStack = block.getPickStack(this.world, blockPos, blockState);
            if (itemStack.isEmpty()) {
                return;
            }
            if (bl && Screen.hasControlDown() && blockState.hasBlockEntity()) {
                blockEntity = this.world.getBlockEntity(blockPos);
            }
        } else if (type == HitResult.Type.ENTITY && bl) {
            Entity entity = ((EntityHitResult)this.crosshairTarget).getEntity();
            itemStack = entity.getPickBlockStack();
            if (itemStack == null) {
                return;
            }
        } else {
            return;
        }
        if (itemStack.isEmpty()) {
            String string = "";
            if (type == HitResult.Type.BLOCK) {
                string = Registry.BLOCK.getId(this.world.getBlockState(((BlockHitResult)this.crosshairTarget).getBlockPos()).getBlock()).toString();
            } else if (type == HitResult.Type.ENTITY) {
                string = Registry.ENTITY_TYPE.getId(((EntityHitResult)this.crosshairTarget).getEntity().getType()).toString();
            }
            LOGGER.warn("Picking on: [{}] {} gave null item", (Object)type, (Object)string);
            return;
        }
        PlayerInventory playerInventory = this.player.getInventory();
        if (blockEntity != null) {
            this.addBlockEntityNbt(itemStack, blockEntity);
        }
        int i = playerInventory.getSlotWithStack(itemStack);
        if (bl) {
            playerInventory.addPickBlock(itemStack);
            this.interactionManager.clickCreativeStack(this.player.getStackInHand(Hand.MAIN_HAND), 36 + playerInventory.selectedSlot);
        } else if (i != -1) {
            if (PlayerInventory.isValidHotbarIndex(i)) {
                playerInventory.selectedSlot = i;
            } else {
                this.interactionManager.pickFromInventory(i);
            }
        }
    }

    private ItemStack addBlockEntityNbt(ItemStack stack, BlockEntity blockEntity) {
        NbtCompound nbtCompound = blockEntity.createNbtWithIdentifyingData();
        if (stack.getItem() instanceof SkullItem && nbtCompound.contains("SkullOwner")) {
            NbtCompound nbtCompound2 = nbtCompound.getCompound("SkullOwner");
            stack.getOrCreateNbt().put("SkullOwner", nbtCompound2);
            return stack;
        }
        BlockItem.setBlockEntityNbt(stack, blockEntity.getType(), nbtCompound);
        NbtCompound nbtCompound2 = new NbtCompound();
        NbtList nbtList = new NbtList();
        nbtList.add(NbtString.of("\"(+NBT)\""));
        nbtCompound2.put("Lore", nbtList);
        stack.setSubNbt("display", nbtCompound2);
        return stack;
    }

    public CrashReport addDetailsToCrashReport(CrashReport report) {
        SystemDetails systemDetails = report.getSystemDetailsSection();
        MinecraftClient.addSystemDetailsToCrashReport(systemDetails, this, this.languageManager, this.gameVersion, this.options);
        if (this.world != null) {
            this.world.addDetailsToCrashReport(report);
        }
        if (this.server != null) {
            this.server.addSystemDetails(systemDetails);
        }
        this.resourceReloadLogger.addReloadSection(report);
        return report;
    }

    public static void addSystemDetailsToCrashReport(@Nullable MinecraftClient client, @Nullable LanguageManager languageManager, String version, @Nullable GameOptions options, CrashReport report) {
        SystemDetails systemDetails = report.getSystemDetailsSection();
        MinecraftClient.addSystemDetailsToCrashReport(systemDetails, client, languageManager, version, options);
    }

    private static SystemDetails addSystemDetailsToCrashReport(SystemDetails systemDetails, @Nullable MinecraftClient client, @Nullable LanguageManager languageManager, String version, GameOptions options) {
        systemDetails.addSection("Launched Version", () -> version);
        systemDetails.addSection("Backend library", RenderSystem::getBackendDescription);
        systemDetails.addSection("Backend API", RenderSystem::getApiDescription);
        systemDetails.addSection("Window size", () -> client != null ? minecraftClient.window.getFramebufferWidth() + "x" + minecraftClient.window.getFramebufferHeight() : "<not initialized>");
        systemDetails.addSection("GL Caps", RenderSystem::getCapsString);
        systemDetails.addSection("GL debug messages", () -> GlDebug.isDebugMessageEnabled() ? String.join((CharSequence)"\n", GlDebug.collectDebugMessages()) : "<disabled>");
        systemDetails.addSection("Using VBOs", () -> "Yes");
        systemDetails.addSection("Is Modded", () -> MinecraftClient.getModStatus().getMessage());
        systemDetails.addSection("Type", "Client (map_client.txt)");
        if (options != null) {
            String string;
            if (instance != null && (string = instance.getVideoWarningManager().getWarningsAsString()) != null) {
                systemDetails.addSection("GPU Warnings", string);
            }
            systemDetails.addSection("Graphics mode", options.graphicsMode.toString());
            systemDetails.addSection("Resource Packs", () -> {
                StringBuilder stringBuilder = new StringBuilder();
                for (String string : gameOptions.resourcePacks) {
                    if (stringBuilder.length() > 0) {
                        stringBuilder.append(", ");
                    }
                    stringBuilder.append(string);
                    if (!gameOptions.incompatibleResourcePacks.contains(string)) continue;
                    stringBuilder.append(" (incompatible)");
                }
                return stringBuilder.toString();
            });
        }
        if (languageManager != null) {
            systemDetails.addSection("Current Language", () -> languageManager.getLanguage().toString());
        }
        systemDetails.addSection("CPU", GlDebugInfo::getCpuInfo);
        return systemDetails;
    }

    public static MinecraftClient getInstance() {
        return instance;
    }

    public CompletableFuture<Void> reloadResourcesConcurrently() {
        return this.submit(this::reloadResources).thenCompose(future -> future);
    }

    public void setCurrentServerEntry(@Nullable ServerInfo serverEntry) {
        this.currentServerEntry = serverEntry;
    }

    @Nullable
    public ServerInfo getCurrentServerEntry() {
        return this.currentServerEntry;
    }

    public boolean isInSingleplayer() {
        return this.integratedServerRunning;
    }

    public boolean isIntegratedServerRunning() {
        return this.integratedServerRunning && this.server != null;
    }

    @Nullable
    public IntegratedServer getServer() {
        return this.server;
    }

    public Session getSession() {
        return this.session;
    }

    public PropertyMap getSessionProperties() {
        if (this.sessionPropertyMap.isEmpty()) {
            GameProfile gameProfile = this.getSessionService().fillProfileProperties(this.session.getProfile(), false);
            this.sessionPropertyMap.putAll((Multimap)gameProfile.getProperties());
        }
        return this.sessionPropertyMap;
    }

    public Proxy getNetworkProxy() {
        return this.networkProxy;
    }

    public TextureManager getTextureManager() {
        return this.textureManager;
    }

    public ResourceManager getResourceManager() {
        return this.resourceManager;
    }

    public ResourcePackManager getResourcePackManager() {
        return this.resourcePackManager;
    }

    public ClientBuiltinResourcePackProvider getResourcePackProvider() {
        return this.builtinPackProvider;
    }

    public File getResourcePackDir() {
        return this.resourcePackDir;
    }

    public LanguageManager getLanguageManager() {
        return this.languageManager;
    }

    public Function<Identifier, Sprite> getSpriteAtlas(Identifier id) {
        return this.bakedModelManager.getAtlas(id)::getSprite;
    }

    public boolean is64Bit() {
        return this.is64Bit;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public VideoWarningManager getVideoWarningManager() {
        return this.videoWarningManager;
    }

    public SoundManager getSoundManager() {
        return this.soundManager;
    }

    public MusicSound getMusicType() {
        if (this.currentScreen instanceof CreditsScreen) {
            return MusicType.CREDITS;
        }
        if (this.player != null) {
            if (this.player.world.getRegistryKey() == World.END) {
                if (this.inGameHud.getBossBarHud().shouldPlayDragonMusic()) {
                    return MusicType.DRAGON;
                }
                return MusicType.END;
            }
            RegistryEntry<Biome> registryEntry = this.player.world.getBiome(this.player.getBlockPos());
            Biome.Category category = Biome.getCategory(registryEntry);
            if (this.musicTracker.isPlayingType(MusicType.UNDERWATER) || this.player.isSubmergedInWater() && (category == Biome.Category.OCEAN || category == Biome.Category.RIVER)) {
                return MusicType.UNDERWATER;
            }
            if (this.player.world.getRegistryKey() != World.NETHER && this.player.getAbilities().creativeMode && this.player.getAbilities().allowFlying) {
                return MusicType.CREATIVE;
            }
            return registryEntry.value().getMusic().orElse(MusicType.GAME);
        }
        return MusicType.MENU;
    }

    public MinecraftSessionService getSessionService() {
        return this.sessionService;
    }

    public PlayerSkinProvider getSkinProvider() {
        return this.skinProvider;
    }

    @Nullable
    public Entity getCameraEntity() {
        return this.cameraEntity;
    }

    public void setCameraEntity(Entity entity) {
        this.cameraEntity = entity;
        this.gameRenderer.onCameraEntitySet(entity);
    }

    public boolean hasOutline(Entity entity) {
        return entity.isGlowing() || this.player != null && this.player.isSpectator() && this.options.spectatorOutlinesKey.isPressed() && entity.getType() == EntityType.PLAYER;
    }

    @Override
    protected Thread getThread() {
        return this.thread;
    }

    @Override
    protected Runnable createTask(Runnable runnable) {
        return runnable;
    }

    @Override
    protected boolean canExecute(Runnable task) {
        return true;
    }

    public BlockRenderManager getBlockRenderManager() {
        return this.blockRenderManager;
    }

    public EntityRenderDispatcher getEntityRenderDispatcher() {
        return this.entityRenderDispatcher;
    }

    public BlockEntityRenderDispatcher getBlockEntityRenderDispatcher() {
        return this.blockEntityRenderDispatcher;
    }

    public ItemRenderer getItemRenderer() {
        return this.itemRenderer;
    }

    public HeldItemRenderer getHeldItemRenderer() {
        return this.heldItemRenderer;
    }

    public <T> SearchableContainer<T> getSearchableContainer(SearchManager.Key<T> key) {
        return this.searchManager.get(key);
    }

    public MetricsData getMetricsData() {
        return this.metricsData;
    }

    public boolean isConnectedToRealms() {
        return this.connectedToRealms;
    }

    public void setConnectedToRealms(boolean connectedToRealms) {
        this.connectedToRealms = connectedToRealms;
    }

    public DataFixer getDataFixer() {
        return this.dataFixer;
    }

    public float getTickDelta() {
        return this.renderTickCounter.tickDelta;
    }

    public float getLastFrameDuration() {
        return this.renderTickCounter.lastFrameDuration;
    }

    public BlockColors getBlockColors() {
        return this.blockColors;
    }

    public boolean hasReducedDebugInfo() {
        return this.player != null && this.player.hasReducedDebugInfo() || this.options.reducedDebugInfo;
    }

    public ToastManager getToastManager() {
        return this.toastManager;
    }

    public TutorialManager getTutorialManager() {
        return this.tutorialManager;
    }

    public boolean isWindowFocused() {
        return this.windowFocused;
    }

    public HotbarStorage getCreativeHotbarStorage() {
        return this.creativeHotbarStorage;
    }

    public BakedModelManager getBakedModelManager() {
        return this.bakedModelManager;
    }

    public PaintingManager getPaintingManager() {
        return this.paintingManager;
    }

    public StatusEffectSpriteManager getStatusEffectSpriteManager() {
        return this.statusEffectSpriteManager;
    }

    @Override
    public void onWindowFocusChanged(boolean focused) {
        this.windowFocused = focused;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Text takePanorama(File directory, int width, int height) {
        int i = this.window.getFramebufferWidth();
        int j = this.window.getFramebufferHeight();
        SimpleFramebuffer framebuffer = new SimpleFramebuffer(width, height, true, IS_SYSTEM_MAC);
        float f = this.player.getPitch();
        float g = this.player.getYaw();
        float h = this.player.prevPitch;
        float k = this.player.prevYaw;
        this.gameRenderer.setBlockOutlineEnabled(false);
        try {
            this.gameRenderer.setRenderingPanorama(true);
            this.worldRenderer.reloadTransparencyShader();
            this.window.setFramebufferWidth(width);
            this.window.setFramebufferHeight(height);
            for (int l = 0; l < 6; ++l) {
                switch (l) {
                    case 0: {
                        this.player.setYaw(g);
                        this.player.setPitch(0.0f);
                        break;
                    }
                    case 1: {
                        this.player.setYaw((g + 90.0f) % 360.0f);
                        this.player.setPitch(0.0f);
                        break;
                    }
                    case 2: {
                        this.player.setYaw((g + 180.0f) % 360.0f);
                        this.player.setPitch(0.0f);
                        break;
                    }
                    case 3: {
                        this.player.setYaw((g - 90.0f) % 360.0f);
                        this.player.setPitch(0.0f);
                        break;
                    }
                    case 4: {
                        this.player.setYaw(g);
                        this.player.setPitch(-90.0f);
                        break;
                    }
                    default: {
                        this.player.setYaw(g);
                        this.player.setPitch(90.0f);
                    }
                }
                this.player.prevYaw = this.player.getYaw();
                this.player.prevPitch = this.player.getPitch();
                framebuffer.beginWrite(true);
                this.gameRenderer.renderWorld(1.0f, 0L, new MatrixStack());
                try {
                    Thread.sleep(10L);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                ScreenshotRecorder.saveScreenshot(directory, "panorama_" + l + ".png", framebuffer, text -> {});
            }
            MutableText text2 = new LiteralText(directory.getName()).formatted(Formatting.UNDERLINE).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, directory.getAbsolutePath())));
            TranslatableText translatableText = new TranslatableText("screenshot.success", text2);
            return translatableText;
        }
        catch (Exception exception) {
            LOGGER.error("Couldn't save image", (Throwable)exception);
            TranslatableText translatableText = new TranslatableText("screenshot.failure", exception.getMessage());
            return translatableText;
        }
        finally {
            this.player.setPitch(f);
            this.player.setYaw(g);
            this.player.prevPitch = h;
            this.player.prevYaw = k;
            this.gameRenderer.setBlockOutlineEnabled(true);
            this.window.setFramebufferWidth(i);
            this.window.setFramebufferHeight(j);
            framebuffer.delete();
            this.gameRenderer.setRenderingPanorama(false);
            this.worldRenderer.reloadTransparencyShader();
            this.getFramebuffer().beginWrite(true);
        }
    }

    private Text takeHugeScreenshot(File gameDirectory, int unitWidth, int unitHeight, int width, int height) {
        try {
            ByteBuffer byteBuffer = GlDebugInfo.allocateMemory(unitWidth * unitHeight * 3);
            ScreenshotRecorder screenshotRecorder = new ScreenshotRecorder(gameDirectory, width, height, unitHeight);
            float f = (float)width / (float)unitWidth;
            float g = (float)height / (float)unitHeight;
            float h = f > g ? f : g;
            for (int i = (height - 1) / unitHeight * unitHeight; i >= 0; i -= unitHeight) {
                for (int j = 0; j < width; j += unitWidth) {
                    RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
                    float k = (float)(width - unitWidth) / 2.0f * 2.0f - (float)(j * 2);
                    float l = (float)(height - unitHeight) / 2.0f * 2.0f - (float)(i * 2);
                    this.gameRenderer.renderWithZoom(h, k /= (float)unitWidth, l /= (float)unitHeight);
                    byteBuffer.clear();
                    RenderSystem.pixelStore(3333, 1);
                    RenderSystem.pixelStore(3317, 1);
                    RenderSystem.readPixels(0, 0, unitWidth, unitHeight, 32992, 5121, byteBuffer);
                    screenshotRecorder.getIntoBuffer(byteBuffer, j, i, unitWidth, unitHeight);
                }
                screenshotRecorder.writeToStream();
            }
            File file = screenshotRecorder.finish();
            GlDebugInfo.freeMemory(byteBuffer);
            MutableText text = new LiteralText(file.getName()).formatted(Formatting.UNDERLINE).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file.getAbsolutePath())));
            return new TranslatableText("screenshot.success", text);
        }
        catch (Exception exception) {
            LOGGER.warn("Couldn't save screenshot", (Throwable)exception);
            return new TranslatableText("screenshot.failure", exception.getMessage());
        }
    }

    public Profiler getProfiler() {
        return this.profiler;
    }

    public MinecraftClientGame getGame() {
        return this.game;
    }

    @Nullable
    public WorldGenerationProgressTracker getWorldGenerationProgressTracker() {
        return this.worldGenProgressTracker.get();
    }

    public SplashTextResourceSupplier getSplashTextLoader() {
        return this.splashTextLoader;
    }

    @Nullable
    public Overlay getOverlay() {
        return this.overlay;
    }

    public SocialInteractionsManager getSocialInteractionsManager() {
        return this.socialInteractionsManager;
    }

    public boolean shouldRenderAsync() {
        return false;
    }

    public Window getWindow() {
        return this.window;
    }

    public BufferBuilderStorage getBufferBuilders() {
        return this.bufferBuilders;
    }

    private static ResourcePackProfile createResourcePackProfile(String name, Text displayName, boolean alwaysEnabled, Supplier<ResourcePack> packFactory, PackResourceMetadata metadata, ResourcePackProfile.InsertionPosition insertionPosition, ResourcePackSource source) {
        int i = metadata.getPackFormat();
        Supplier<ResourcePack> supplier = packFactory;
        if (i <= 3) {
            supplier = MinecraftClient.createV3ResourcePackFactory(supplier);
        }
        if (i <= 4) {
            supplier = MinecraftClient.createV4ResourcePackFactory(supplier);
        }
        return new ResourcePackProfile(name, displayName, alwaysEnabled, supplier, metadata, ResourceType.CLIENT_RESOURCES, insertionPosition, source);
    }

    private static Supplier<ResourcePack> createV3ResourcePackFactory(Supplier<ResourcePack> packFactory) {
        return () -> new Format3ResourcePack((ResourcePack)packFactory.get(), Format3ResourcePack.NEW_TO_OLD_MAP);
    }

    private static Supplier<ResourcePack> createV4ResourcePackFactory(Supplier<ResourcePack> packFactory) {
        return () -> new Format4ResourcePack((ResourcePack)packFactory.get());
    }

    public void setMipmapLevels(int mipmapLevels) {
        this.bakedModelManager.setMipmapLevels(mipmapLevels);
    }

    public EntityModelLoader getEntityModelLoader() {
        return this.entityModelLoader;
    }

    public boolean shouldFilterText() {
        return this.userApiService.properties().flag(UserApiService.UserFlag.PROFANITY_FILTER_ENABLED);
    }

    public void loadBlockList() {
        this.socialInteractionsManager.loadBlockList();
    }

    static {
        LOGGER = LogUtils.getLogger();
        IS_SYSTEM_MAC = Util.getOperatingSystem() == Util.OperatingSystem.OSX;
        DEFAULT_FONT_ID = new Identifier("default");
        UNICODE_FONT_ID = new Identifier("uniform");
        ALT_TEXT_RENDERER_ID = new Identifier("alt");
        REGIONAL_COMPLIANCIES_ID = new Identifier("regional_compliancies.json");
        COMPLETED_UNIT_FUTURE = CompletableFuture.completedFuture(Unit.INSTANCE);
        SOCIAL_INTERACTIONS_NOT_AVAILABLE = new TranslatableText("multiplayer.socialInteractions.not_available");
    }

    /*
     * Uses 'sealed' constructs - enablewith --sealed true
     */
    @Environment(value=EnvType.CLIENT)
    public static abstract class ChatRestriction
    extends Enum<ChatRestriction> {
        public static final /* enum */ ChatRestriction ENABLED = new ChatRestriction(LiteralText.EMPTY){

            @Override
            public boolean allowsChat(boolean singlePlayer) {
                return true;
            }
        };
        public static final /* enum */ ChatRestriction DISABLED_BY_OPTIONS = new ChatRestriction(new TranslatableText("chat.disabled.options").formatted(Formatting.RED)){

            @Override
            public boolean allowsChat(boolean singlePlayer) {
                return false;
            }
        };
        public static final /* enum */ ChatRestriction DISABLED_BY_LAUNCHER = new ChatRestriction(new TranslatableText("chat.disabled.launcher").formatted(Formatting.RED)){

            @Override
            public boolean allowsChat(boolean singlePlayer) {
                return singlePlayer;
            }
        };
        public static final /* enum */ ChatRestriction DISABLED_BY_PROFILE = new ChatRestriction(new TranslatableText("chat.disabled.profile").formatted(Formatting.RED)){

            @Override
            public boolean allowsChat(boolean singlePlayer) {
                return singlePlayer;
            }
        };
        private final Text description;
        private static final /* synthetic */ ChatRestriction[] field_28945;

        public static ChatRestriction[] values() {
            return (ChatRestriction[])field_28945.clone();
        }

        public static ChatRestriction valueOf(String string) {
            return Enum.valueOf(ChatRestriction.class, string);
        }

        ChatRestriction(Text description) {
            this.description = description;
        }

        public Text getDescription() {
            return this.description;
        }

        public abstract boolean allowsChat(boolean var1);

        private static /* synthetic */ ChatRestriction[] method_36862() {
            return new ChatRestriction[]{ENABLED, DISABLED_BY_OPTIONS, DISABLED_BY_LAUNCHER, DISABLED_BY_PROFILE};
        }

        static {
            field_28945 = ChatRestriction.method_36862();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class WorldLoadAction
    extends Enum<WorldLoadAction> {
        public static final /* enum */ WorldLoadAction NONE = new WorldLoadAction();
        public static final /* enum */ WorldLoadAction CREATE = new WorldLoadAction();
        public static final /* enum */ WorldLoadAction BACKUP = new WorldLoadAction();
        private static final /* synthetic */ WorldLoadAction[] field_25438;

        public static WorldLoadAction[] values() {
            return (WorldLoadAction[])field_25438.clone();
        }

        public static WorldLoadAction valueOf(String string) {
            return Enum.valueOf(WorldLoadAction.class, string);
        }

        private static /* synthetic */ WorldLoadAction[] method_36863() {
            return new WorldLoadAction[]{NONE, CREATE, BACKUP};
        }

        static {
            field_25438 = WorldLoadAction.method_36863();
        }
    }
}

