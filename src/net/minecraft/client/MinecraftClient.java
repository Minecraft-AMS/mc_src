/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Queues
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.GameProfileRepository
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.authlib.properties.PropertyMap
 *  com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
 *  com.mojang.datafixers.DataFixer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client;

import com.google.common.collect.Multimap;
import com.google.common.collect.Queues;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;
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
import net.minecraft.client.gui.WorldGenerationProgressTracker;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.LevelLoadingScreen;
import net.minecraft.client.gui.screen.OutOfMemoryScreen;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.gui.screen.SaveLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.Screens;
import net.minecraft.client.gui.screen.SleepingChatScreen;
import net.minecraft.client.gui.screen.SplashScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.options.AoOption;
import net.minecraft.client.options.ChatVisibility;
import net.minecraft.client.options.CloudRenderMode;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.HotbarStorage;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.options.Option;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.chunk.ChunkRenderer;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.resource.ClientBuiltinResourcePackProvider;
import net.minecraft.client.resource.ClientResourcePackProfile;
import net.minecraft.client.resource.FoliageColormapResourceSupplier;
import net.minecraft.client.resource.Format3ResourcePack;
import net.minecraft.client.resource.GrassColormapResourceSupplier;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.search.IdentifierSearchableContainer;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.search.SearchableContainer;
import net.minecraft.client.search.TextSearchableContainer;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.texture.PaintingManager;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.Session;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.WindowProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.datafixer.Schemas;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.LeadKnotEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SkullItem;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.QueueingWorldGenerationProgressListener;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.tag.ItemTags;
import net.minecraft.text.KeybindText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.MetricsData;
import net.minecraft.util.UncaughtExceptionLogger;
import net.minecraft.util.Unit;
import net.minecraft.util.UserCache;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.DisableableProfiler;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.ProfilerTiming;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.snooper.Snooper;
import net.minecraft.util.snooper.SnooperListener;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import net.minecraft.world.Difficulty;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.TheEndDimension;
import net.minecraft.world.dimension.TheNetherDimension;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class MinecraftClient
extends ReentrantThreadExecutor<Runnable>
implements SnooperListener,
WindowEventHandler,
AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final boolean IS_SYSTEM_MAC = Util.getOperatingSystem() == Util.OperatingSystem.OSX;
    public static final Identifier DEFAULT_TEXT_RENDERER_ID = new Identifier("default");
    public static final Identifier ALT_TEXT_RENDERER_ID = new Identifier("alt");
    private static final CompletableFuture<Unit> COMPLETED_UNIT_FUTURE = CompletableFuture.completedFuture(Unit.INSTANCE);
    public static byte[] memoryReservedForCrash = new byte[0xA00000];
    private static int cachedMaxTextureSize = -1;
    private final File resourcePackDir;
    private final PropertyMap sessionPropertyMap;
    private final WindowSettings windowSettings;
    private ServerInfo currentServerEntry;
    private TextureManager textureManager;
    private static MinecraftClient instance;
    private final DataFixer dataFixer;
    public ClientPlayerInteractionManager interactionManager;
    private WindowProvider windowProvider;
    public Window window;
    private boolean crashed;
    private CrashReport crashReport;
    private boolean connectedToRealms;
    private final RenderTickCounter renderTickCounter = new RenderTickCounter(20.0f, 0L);
    private final Snooper snooper = new Snooper("client", this, Util.getMeasuringTimeMs());
    public ClientWorld world;
    public WorldRenderer worldRenderer;
    private EntityRenderDispatcher entityRenderManager;
    private ItemRenderer itemRenderer;
    private HeldItemRenderer heldItemRenderer;
    public ClientPlayerEntity player;
    @Nullable
    public Entity cameraEntity;
    @Nullable
    public Entity targetedEntity;
    public ParticleManager particleManager;
    private final SearchManager searchManager = new SearchManager();
    private final Session session;
    private boolean paused;
    private float pausedTickDelta;
    public TextRenderer textRenderer;
    @Nullable
    public Screen currentScreen;
    @Nullable
    public Overlay overlay;
    public GameRenderer gameRenderer;
    public DebugRenderer debugRenderer;
    protected int attackCooldown;
    @Nullable
    private IntegratedServer server;
    private final AtomicReference<WorldGenerationProgressTracker> worldGenProgressTracker = new AtomicReference();
    public InGameHud inGameHud;
    public boolean skipGameRender;
    public HitResult crosshairTarget;
    public GameOptions options;
    private HotbarStorage creativeHotbarStorage;
    public Mouse mouse;
    public Keyboard keyboard;
    public final File runDirectory;
    private final File assetDirectory;
    private final String gameVersion;
    private final String versionType;
    private final Proxy netProxy;
    private LevelStorage levelStorage;
    private static int currentFps;
    private int itemUseCooldown;
    private String autoConnectServerIp;
    private int autoConnectServerPort;
    public final MetricsData metricsData = new MetricsData();
    private long lastMetricsSampleTime = Util.getMeasuringTimeNano();
    private final boolean is64Bit;
    private final boolean isDemo;
    @Nullable
    private ClientConnection connection;
    private boolean isIntegratedServerRunning;
    private final DisableableProfiler profiler = new DisableableProfiler(() -> this.renderTickCounter.ticksThisFrame);
    private ReloadableResourceManager resourceManager;
    private final ClientBuiltinResourcePackProvider builtinPackProvider;
    private final ResourcePackManager<ClientResourcePackProfile> resourcePackManager;
    private LanguageManager languageManager;
    private BlockColors blockColorMap;
    private ItemColors itemColorMap;
    private Framebuffer framebuffer;
    private SpriteAtlasTexture spriteAtlas;
    private SoundManager soundManager;
    private MusicTracker musicTracker;
    private FontManager fontManager;
    private SplashTextResourceSupplier splashTextLoader;
    private final MinecraftSessionService sessionService;
    private PlayerSkinProvider skinProvider;
    private final Thread thread = Thread.currentThread();
    private BakedModelManager bakedModelManager;
    private BlockRenderManager blockRenderManager;
    private PaintingManager paintingManager;
    private StatusEffectSpriteManager statusEffectSpriteManager;
    private final ToastManager toastManager;
    private final MinecraftClientGame game = new MinecraftClientGame(this);
    private volatile boolean running = true;
    public String fpsDebugString = "";
    public boolean field_1730 = true;
    private long nextDebugInfoUpdateTime;
    private int fpsCounter;
    private final TutorialManager tutorialManager;
    private boolean windowFocused;
    private final Queue<Runnable> renderTaskQueue = Queues.newConcurrentLinkedQueue();
    private CompletableFuture<Void> resourceReloadFuture;
    private String openProfilerSection = "root";

    public MinecraftClient(RunArgs runArgs) {
        super("Client");
        this.windowSettings = runArgs.windowSettings;
        instance = this;
        this.runDirectory = runArgs.directories.runDir;
        this.assetDirectory = runArgs.directories.assetDir;
        this.resourcePackDir = runArgs.directories.resourcePackDir;
        this.gameVersion = runArgs.game.version;
        this.versionType = runArgs.game.versionType;
        this.sessionPropertyMap = runArgs.network.profileProperties;
        this.builtinPackProvider = new ClientBuiltinResourcePackProvider(new File(this.runDirectory, "server-resource-packs"), runArgs.directories.getResourceIndex());
        this.resourcePackManager = new ResourcePackManager<ClientResourcePackProfile>((string, bl, supplier, resourcePack, packResourceMetadata, insertionPosition) -> {
            Supplier<ResourcePack> supplier2 = packResourceMetadata.getPackFormat() < SharedConstants.getGameVersion().getPackVersion() ? () -> MinecraftClient.method_1528((Supplier)supplier) : supplier;
            return new ClientResourcePackProfile(string, bl, supplier2, resourcePack, packResourceMetadata, insertionPosition);
        });
        this.resourcePackManager.registerProvider(this.builtinPackProvider);
        this.resourcePackManager.registerProvider(new FileResourcePackProvider(this.resourcePackDir));
        this.netProxy = runArgs.network.netProxy == null ? Proxy.NO_PROXY : runArgs.network.netProxy;
        this.sessionService = new YggdrasilAuthenticationService(this.netProxy, UUID.randomUUID().toString()).createMinecraftSessionService();
        this.session = runArgs.network.session;
        LOGGER.info("Setting user: {}", (Object)this.session.getUsername());
        LOGGER.debug("(Session ID is {})", (Object)this.session.getSessionId());
        this.isDemo = runArgs.game.demo;
        this.is64Bit = MinecraftClient.checkIs64Bit();
        this.server = null;
        if (runArgs.autoConnect.serverIP != null) {
            this.autoConnectServerIp = runArgs.autoConnect.serverIP;
            this.autoConnectServerPort = runArgs.autoConnect.serverPort;
        }
        Bootstrap.initialize();
        Bootstrap.logMissingTranslations();
        KeybindText.i18n = KeyBinding::getLocalizedName;
        this.dataFixer = Schemas.getFixer();
        this.toastManager = new ToastManager(this);
        this.tutorialManager = new TutorialManager(this);
    }

    public void run() {
        this.running = true;
        try {
            this.init();
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Initializing game");
            crashReport.addElement("Initialization");
            this.printCrashReport(this.addDetailsToCrashReport(crashReport));
            return;
        }
        try {
            boolean bl = false;
            while (this.running) {
                if (this.crashed && this.crashReport != null) {
                    this.printCrashReport(this.crashReport);
                    return;
                }
                try {
                    this.render(!bl);
                }
                catch (OutOfMemoryError outOfMemoryError) {
                    if (bl) {
                        throw outOfMemoryError;
                    }
                    this.cleanUpAfterCrash();
                    this.openScreen(new OutOfMemoryScreen());
                    System.gc();
                    LOGGER.fatal("Out of memory", (Throwable)outOfMemoryError);
                    bl = true;
                }
            }
        }
        catch (CrashException crashException) {
            this.addDetailsToCrashReport(crashException.getReport());
            this.cleanUpAfterCrash();
            LOGGER.fatal("Reported exception thrown!", (Throwable)crashException);
            this.printCrashReport(crashException.getReport());
        }
        catch (Throwable throwable) {
            CrashReport crashReport = this.addDetailsToCrashReport(new CrashReport("Unexpected error", throwable));
            LOGGER.fatal("Unreported exception thrown!", throwable);
            this.cleanUpAfterCrash();
            this.printCrashReport(crashReport);
        }
        finally {
            this.stop();
        }
    }

    private void init() {
        LongSupplier longSupplier;
        this.options = new GameOptions(this, this.runDirectory);
        this.creativeHotbarStorage = new HotbarStorage(this.runDirectory, this.dataFixer);
        this.startTimerHackThread();
        LOGGER.info("LWJGL Version: {}", (Object)GLX.getLWJGLVersion());
        WindowSettings windowSettings = this.windowSettings;
        if (this.options.overrideHeight > 0 && this.options.overrideWidth > 0) {
            windowSettings = new WindowSettings(this.options.overrideWidth, this.options.overrideHeight, windowSettings.fullscreenWidth, windowSettings.fullscreenHeight, windowSettings.fullscreen);
        }
        if ((longSupplier = GLX.initGlfw()) != null) {
            Util.nanoTimeSupplier = longSupplier;
        }
        this.windowProvider = new WindowProvider(this);
        this.window = this.windowProvider.createWindow(windowSettings, this.options.fullscreenResolution, "Minecraft " + SharedConstants.getGameVersion().getName());
        this.onWindowFocusChanged(true);
        try {
            InputStream inputStream = this.getResourcePackDownloader().getPack().open(ResourceType.CLIENT_RESOURCES, new Identifier("icons/icon_16x16.png"));
            InputStream inputStream2 = this.getResourcePackDownloader().getPack().open(ResourceType.CLIENT_RESOURCES, new Identifier("icons/icon_32x32.png"));
            this.window.setIcon(inputStream, inputStream2);
        }
        catch (IOException iOException) {
            LOGGER.error("Couldn't set icon", (Throwable)iOException);
        }
        this.window.setFramerateLimit(this.options.maxFps);
        this.mouse = new Mouse(this);
        this.mouse.setup(this.window.getHandle());
        this.keyboard = new Keyboard(this);
        this.keyboard.setup(this.window.getHandle());
        GLX.init();
        GlDebug.enableDebug(this.options.glDebugVerbosity, false);
        this.framebuffer = new Framebuffer(this.window.getFramebufferWidth(), this.window.getFramebufferHeight(), true, IS_SYSTEM_MAC);
        this.framebuffer.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        this.resourceManager = new ReloadableResourceManagerImpl(ResourceType.CLIENT_RESOURCES, this.thread);
        this.options.addResourcePackProfilesToManager(this.resourcePackManager);
        this.resourcePackManager.scanPacks();
        List<ResourcePack> list = this.resourcePackManager.getEnabledProfiles().stream().map(ResourcePackProfile::createResourcePack).collect(Collectors.toList());
        for (ResourcePack resourcePack : list) {
            this.resourceManager.addPack(resourcePack);
        }
        this.languageManager = new LanguageManager(this.options.language);
        this.resourceManager.registerListener(this.languageManager);
        this.languageManager.reloadResources(list);
        this.textureManager = new TextureManager(this.resourceManager);
        this.resourceManager.registerListener(this.textureManager);
        this.onResolutionChanged();
        this.skinProvider = new PlayerSkinProvider(this.textureManager, new File(this.assetDirectory, "skins"), this.sessionService);
        this.levelStorage = new LevelStorage(this.runDirectory.toPath().resolve("saves"), this.runDirectory.toPath().resolve("backups"), this.dataFixer);
        this.soundManager = new SoundManager(this.resourceManager, this.options);
        this.resourceManager.registerListener(this.soundManager);
        this.splashTextLoader = new SplashTextResourceSupplier(this.session);
        this.resourceManager.registerListener(this.splashTextLoader);
        this.musicTracker = new MusicTracker(this);
        this.fontManager = new FontManager(this.textureManager, this.forcesUnicodeFont());
        this.resourceManager.registerListener(this.fontManager.getResourceReloadListener());
        this.textRenderer = this.fontManager.getTextRenderer(DEFAULT_TEXT_RENDERER_ID);
        if (this.options.language != null) {
            this.textRenderer.setRightToLeft(this.languageManager.isRightToLeft());
        }
        this.resourceManager.registerListener(new GrassColormapResourceSupplier());
        this.resourceManager.registerListener(new FoliageColormapResourceSupplier());
        this.window.setPhase("Startup");
        GlStateManager.enableTexture();
        GlStateManager.shadeModel(7425);
        GlStateManager.clearDepth(1.0);
        GlStateManager.enableDepthTest();
        GlStateManager.depthFunc(515);
        GlStateManager.enableAlphaTest();
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.cullFace(GlStateManager.FaceSides.BACK);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        this.window.setPhase("Post startup");
        this.spriteAtlas = new SpriteAtlasTexture("textures");
        this.spriteAtlas.setMipLevel(this.options.mipmapLevels);
        this.textureManager.registerTextureUpdateable(SpriteAtlasTexture.BLOCK_ATLAS_TEX, this.spriteAtlas);
        this.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        this.spriteAtlas.setFilter(false, this.options.mipmapLevels > 0);
        this.blockColorMap = BlockColors.create();
        this.itemColorMap = ItemColors.create(this.blockColorMap);
        this.bakedModelManager = new BakedModelManager(this.spriteAtlas, this.blockColorMap);
        this.resourceManager.registerListener(this.bakedModelManager);
        this.itemRenderer = new ItemRenderer(this.textureManager, this.bakedModelManager, this.itemColorMap);
        this.entityRenderManager = new EntityRenderDispatcher(this.textureManager, this.itemRenderer, this.resourceManager);
        this.heldItemRenderer = new HeldItemRenderer(this);
        this.resourceManager.registerListener(this.itemRenderer);
        this.gameRenderer = new GameRenderer(this, this.resourceManager);
        this.resourceManager.registerListener(this.gameRenderer);
        this.blockRenderManager = new BlockRenderManager(this.bakedModelManager.getBlockModels(), this.blockColorMap);
        this.resourceManager.registerListener(this.blockRenderManager);
        this.worldRenderer = new WorldRenderer(this);
        this.resourceManager.registerListener(this.worldRenderer);
        this.initializeSearchableContainers();
        this.resourceManager.registerListener(this.searchManager);
        GlStateManager.viewport(0, 0, this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
        this.particleManager = new ParticleManager(this.world, this.textureManager);
        this.resourceManager.registerListener(this.particleManager);
        this.paintingManager = new PaintingManager(this.textureManager);
        this.resourceManager.registerListener(this.paintingManager);
        this.statusEffectSpriteManager = new StatusEffectSpriteManager(this.textureManager);
        this.resourceManager.registerListener(this.statusEffectSpriteManager);
        this.inGameHud = new InGameHud(this);
        this.debugRenderer = new DebugRenderer(this);
        GLX.setGlfwErrorCallback(this::handleGlErrorByDisableVsync);
        if (this.options.fullscreen && !this.window.isFullscreen()) {
            this.window.toggleFullscreen();
            this.options.fullscreen = this.window.isFullscreen();
        }
        this.window.setVsync(this.options.enableVsync);
        this.window.method_21668(this.options.field_20308);
        this.window.logOnGlError();
        if (this.autoConnectServerIp != null) {
            this.openScreen(new ConnectScreen(new TitleScreen(), this, this.autoConnectServerIp, this.autoConnectServerPort));
        } else {
            this.openScreen(new TitleScreen(true));
        }
        SplashScreen.method_18819(this);
        this.setOverlay(new SplashScreen(this, this.resourceManager.beginInitialMonitoredReload(Util.getServerWorkerExecutor(), this, COMPLETED_UNIT_FUTURE), () -> {
            if (SharedConstants.isDevelopment) {
                this.checkGameData();
            }
        }, false));
    }

    private void initializeSearchableContainers() {
        TextSearchableContainer<ItemStack> textSearchableContainer = new TextSearchableContainer<ItemStack>(itemStack -> itemStack.getTooltip(null, TooltipContext.Default.NORMAL).stream().map(text -> Formatting.strip(text.getString()).trim()).filter(string -> !string.isEmpty()), itemStack -> Stream.of(Registry.ITEM.getId(itemStack.getItem())));
        IdentifierSearchableContainer<ItemStack> identifierSearchableContainer = new IdentifierSearchableContainer<ItemStack>(itemStack -> ItemTags.getContainer().getTagsFor(itemStack.getItem()).stream());
        DefaultedList<ItemStack> defaultedList = DefaultedList.of();
        for (Item item : Registry.ITEM) {
            item.appendStacks(ItemGroup.SEARCH, defaultedList);
        }
        defaultedList.forEach(itemStack -> {
            textSearchableContainer.add((ItemStack)itemStack);
            identifierSearchableContainer.add((ItemStack)itemStack);
        });
        TextSearchableContainer<RecipeResultCollection> textSearchableContainer2 = new TextSearchableContainer<RecipeResultCollection>(recipeResultCollection -> recipeResultCollection.getAllRecipes().stream().flatMap(recipe -> recipe.getOutput().getTooltip(null, TooltipContext.Default.NORMAL).stream()).map(text -> Formatting.strip(text.getString()).trim()).filter(string -> !string.isEmpty()), recipeResultCollection -> recipeResultCollection.getAllRecipes().stream().map(recipe -> Registry.ITEM.getId(recipe.getOutput().getItem())));
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

    private void startTimerHackThread() {
        Thread thread = new Thread("Timer hack thread"){

            @Override
            public void run() {
                while (MinecraftClient.this.running) {
                    try {
                        Thread.sleep(Integer.MAX_VALUE);
                    }
                    catch (InterruptedException interruptedException) {}
                }
            }
        };
        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
        thread.start();
    }

    public void setCrashReport(CrashReport crashReport) {
        this.crashed = true;
        this.crashReport = crashReport;
    }

    public void printCrashReport(CrashReport crashReport) {
        File file = new File(MinecraftClient.getInstance().runDirectory, "crash-reports");
        File file2 = new File(file, "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-client.txt");
        Bootstrap.println(crashReport.asString());
        if (crashReport.getFile() != null) {
            Bootstrap.println("#@!@# Game crashed! Crash report saved to: #@!@# " + crashReport.getFile());
            System.exit(-1);
        } else if (crashReport.writeToFile(file2)) {
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
        if (this.resourceReloadFuture != null) {
            return this.resourceReloadFuture;
        }
        CompletableFuture<Void> completableFuture = new CompletableFuture<Void>();
        if (this.overlay instanceof SplashScreen) {
            this.resourceReloadFuture = completableFuture;
            return completableFuture;
        }
        this.resourcePackManager.scanPacks();
        List<ResourcePack> list = this.resourcePackManager.getEnabledProfiles().stream().map(ResourcePackProfile::createResourcePack).collect(Collectors.toList());
        this.setOverlay(new SplashScreen(this, this.resourceManager.beginMonitoredReload(Util.getServerWorkerExecutor(), this, COMPLETED_UNIT_FUTURE, list), () -> {
            this.languageManager.reloadResources(list);
            if (this.worldRenderer != null) {
                this.worldRenderer.reload();
            }
            completableFuture.complete(null);
        }, true));
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
        Sprite sprite = bakedModel.getSprite();
        for (Block block2 : Registry.BLOCK) {
            for (BlockState blockState2 : block2.getStateManager().getStates()) {
                Sprite sprite2 = blockModels.getSprite(blockState2);
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
                String string2 = new TranslatableText(string, new Object[0]).getString();
                if (!string2.toLowerCase(Locale.ROOT).equals(item.getTranslationKey())) continue;
                LOGGER.debug("Missing translation for: {} {} {}", (Object)itemStack, (Object)string, (Object)itemStack.getItem());
            }
        }
        if (bl |= Screens.validateScreens()) {
            throw new IllegalStateException("Your game data is foobar, fix the errors above!");
        }
    }

    public LevelStorage getLevelStorage() {
        return this.levelStorage;
    }

    public void openScreen(@Nullable Screen screen) {
        if (this.currentScreen != null) {
            this.currentScreen.removed();
        }
        if (screen == null && this.world == null) {
            screen = new TitleScreen();
        } else if (screen == null && this.player.getHealth() <= 0.0f) {
            screen = new DeathScreen(null, this.world.getLevelProperties().isHardcore());
        }
        if (screen instanceof TitleScreen || screen instanceof MultiplayerScreen) {
            this.options.debugEnabled = false;
            this.inGameHud.getChatHud().clear(true);
        }
        this.currentScreen = screen;
        if (screen != null) {
            this.mouse.unlockCursor();
            KeyBinding.unpressAll();
            screen.init(this, this.window.getScaledWidth(), this.window.getScaledHeight());
            this.skipGameRender = false;
            NarratorManager.INSTANCE.narrate(screen.getNarrationMessage());
        } else {
            this.soundManager.resumeAll();
            this.mouse.lockCursor();
        }
    }

    public void setOverlay(@Nullable Overlay overlay) {
        this.overlay = overlay;
    }

    public void stop() {
        try {
            LOGGER.info("Stopping!");
            NarratorManager.INSTANCE.destroy();
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
            if (!this.crashed) {
                System.exit(0);
            }
        }
    }

    @Override
    public void close() {
        try {
            this.spriteAtlas.clear();
            this.textRenderer.close();
            this.fontManager.close();
            this.gameRenderer.close();
            this.worldRenderer.close();
            this.soundManager.close();
            this.resourcePackManager.close();
            this.particleManager.clearAtlas();
            this.statusEffectSpriteManager.close();
            this.paintingManager.close();
            Util.shutdownServerWorkerExecutor();
        }
        finally {
            this.windowProvider.close();
            this.window.close();
        }
    }

    private void render(boolean tick) {
        boolean bl;
        Runnable runnable;
        this.window.setPhase("Pre render");
        long l = Util.getMeasuringTimeNano();
        this.profiler.startTick();
        if (GLX.shouldClose(this.window)) {
            this.scheduleStop();
        }
        if (this.resourceReloadFuture != null && !(this.overlay instanceof SplashScreen)) {
            CompletableFuture<Void> completableFuture = this.resourceReloadFuture;
            this.resourceReloadFuture = null;
            this.reloadResources().thenRun(() -> completableFuture.complete(null));
        }
        while ((runnable = this.renderTaskQueue.poll()) != null) {
            runnable.run();
        }
        if (tick) {
            this.renderTickCounter.beginRenderTick(Util.getMeasuringTimeMs());
            this.profiler.push("scheduledExecutables");
            this.runTasks();
            this.profiler.pop();
        }
        long m = Util.getMeasuringTimeNano();
        this.profiler.push("tick");
        if (tick) {
            for (int i = 0; i < Math.min(10, this.renderTickCounter.ticksThisFrame); ++i) {
                this.tick();
            }
        }
        this.mouse.updateMouse();
        this.window.setPhase("Render");
        GLX.pollEvents();
        long n = Util.getMeasuringTimeNano() - m;
        this.profiler.swap("sound");
        this.soundManager.updateListenerPosition(this.gameRenderer.getCamera());
        this.profiler.pop();
        this.profiler.push("render");
        GlStateManager.pushMatrix();
        GlStateManager.clear(16640, IS_SYSTEM_MAC);
        this.framebuffer.beginWrite(true);
        this.profiler.push("display");
        GlStateManager.enableTexture();
        this.profiler.pop();
        if (!this.skipGameRender) {
            this.profiler.swap("gameRenderer");
            this.gameRenderer.render(this.paused ? this.pausedTickDelta : this.renderTickCounter.tickDelta, l, tick);
            this.profiler.swap("toasts");
            this.toastManager.draw();
            this.profiler.pop();
        }
        this.profiler.endTick();
        if (this.options.debugEnabled && this.options.debugProfilerEnabled && !this.options.hudHidden) {
            this.profiler.getController().enable();
            this.drawProfilerResults();
        } else {
            this.profiler.getController().disable();
        }
        this.framebuffer.endWrite();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        this.framebuffer.draw(this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
        GlStateManager.popMatrix();
        this.profiler.startTick();
        this.updateDisplay(true);
        Thread.yield();
        this.window.setPhase("Post render");
        ++this.fpsCounter;
        boolean bl2 = bl = this.isIntegratedServerRunning() && (this.currentScreen != null && this.currentScreen.isPauseScreen() || this.overlay != null && this.overlay.pausesGame()) && !this.server.isRemote();
        if (this.paused != bl) {
            if (this.paused) {
                this.pausedTickDelta = this.renderTickCounter.tickDelta;
            } else {
                this.renderTickCounter.tickDelta = this.pausedTickDelta;
            }
            this.paused = bl;
        }
        long o = Util.getMeasuringTimeNano();
        this.metricsData.pushSample(o - this.lastMetricsSampleTime);
        this.lastMetricsSampleTime = o;
        while (Util.getMeasuringTimeMs() >= this.nextDebugInfoUpdateTime + 1000L) {
            currentFps = this.fpsCounter;
            Object[] objectArray = new Object[8];
            objectArray[0] = currentFps;
            objectArray[1] = ChunkRenderer.chunkUpdateCount;
            objectArray[2] = ChunkRenderer.chunkUpdateCount == 1 ? "" : "s";
            objectArray[3] = (double)this.options.maxFps == Option.FRAMERATE_LIMIT.getMax() ? "inf" : Integer.valueOf(this.options.maxFps);
            objectArray[4] = this.options.enableVsync ? " vsync" : "";
            Object object = objectArray[5] = this.options.fancyGraphics ? "" : " fast";
            objectArray[6] = this.options.cloudRenderMode == CloudRenderMode.OFF ? "" : (this.options.cloudRenderMode == CloudRenderMode.FAST ? " fast-clouds" : " fancy-clouds");
            objectArray[7] = GLX.useVbo() ? " vbo" : "";
            this.fpsDebugString = String.format("%d fps (%d chunk update%s) T: %s%s%s%s%s", objectArray);
            ChunkRenderer.chunkUpdateCount = 0;
            this.nextDebugInfoUpdateTime += 1000L;
            this.fpsCounter = 0;
            this.snooper.update();
            if (this.snooper.isActive()) continue;
            this.snooper.method_5482();
        }
        this.profiler.endTick();
    }

    @Override
    public void updateDisplay(boolean respectFramerateLimit) {
        this.profiler.push("display_update");
        this.window.setFullscreen(this.options.fullscreen);
        this.profiler.pop();
        if (respectFramerateLimit && this.isFramerateLimited()) {
            this.profiler.push("fpslimit_wait");
            this.window.waitForFramerateLimit();
            this.profiler.pop();
        }
    }

    @Override
    public void onResolutionChanged() {
        Framebuffer framebuffer;
        int i = this.window.calculateScaleFactor(this.options.guiScale, this.forcesUnicodeFont());
        this.window.setScaleFactor(i);
        if (this.currentScreen != null) {
            this.currentScreen.resize(this, this.window.getScaledWidth(), this.window.getScaledHeight());
        }
        if ((framebuffer = this.getFramebuffer()) != null) {
            framebuffer.resize(this.window.getFramebufferWidth(), this.window.getFramebufferHeight(), IS_SYSTEM_MAC);
        }
        if (this.gameRenderer != null) {
            this.gameRenderer.onResized(this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
        }
        if (this.mouse != null) {
            this.mouse.onResolutionChanged();
        }
    }

    private int getFramerateLimit() {
        if (this.world == null && (this.currentScreen != null || this.overlay != null)) {
            return 60;
        }
        return this.window.getFramerateLimit();
    }

    private boolean isFramerateLimited() {
        return (double)this.getFramerateLimit() < Option.FRAMERATE_LIMIT.getMax();
    }

    public void cleanUpAfterCrash() {
        try {
            memoryReservedForCrash = new byte[0];
            this.worldRenderer.method_3267();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            System.gc();
            if (this.isIntegratedServerRunning()) {
                this.server.stop(true);
            }
            this.disconnect(new SaveLevelScreen(new TranslatableText("menu.savingLevel", new Object[0])));
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        System.gc();
    }

    void handleProfilerKeyPress(int digit) {
        ProfileResult profileResult = this.profiler.getController().getResults();
        List<ProfilerTiming> list = profileResult.getTimings(this.openProfilerSection);
        if (list.isEmpty()) {
            return;
        }
        ProfilerTiming profilerTiming = list.remove(0);
        if (digit == 0) {
            int i;
            if (!profilerTiming.name.isEmpty() && (i = this.openProfilerSection.lastIndexOf(46)) >= 0) {
                this.openProfilerSection = this.openProfilerSection.substring(0, i);
            }
        } else if (--digit < list.size() && !"unspecified".equals(list.get((int)digit).name)) {
            if (!this.openProfilerSection.isEmpty()) {
                this.openProfilerSection = this.openProfilerSection + ".";
            }
            this.openProfilerSection = this.openProfilerSection + list.get((int)digit).name;
        }
    }

    private void drawProfilerResults() {
        int m;
        if (!this.profiler.getController().isEnabled()) {
            return;
        }
        ProfileResult profileResult = this.profiler.getController().getResults();
        List<ProfilerTiming> list = profileResult.getTimings(this.openProfilerSection);
        ProfilerTiming profilerTiming = list.remove(0);
        GlStateManager.clear(256, IS_SYSTEM_MAC);
        GlStateManager.matrixMode(5889);
        GlStateManager.enableColorMaterial();
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0, this.window.getFramebufferWidth(), this.window.getFramebufferHeight(), 0.0, 1000.0, 3000.0);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translatef(0.0f, 0.0f, -2000.0f);
        GlStateManager.lineWidth(1.0f);
        GlStateManager.disableTexture();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        int i = 160;
        int j = this.window.getFramebufferWidth() - 160 - 10;
        int k = this.window.getFramebufferHeight() - 320;
        GlStateManager.enableBlend();
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex((float)j - 176.0f, (float)k - 96.0f - 16.0f, 0.0).color(200, 0, 0, 0).next();
        bufferBuilder.vertex((float)j - 176.0f, k + 320, 0.0).color(200, 0, 0, 0).next();
        bufferBuilder.vertex((float)j + 176.0f, k + 320, 0.0).color(200, 0, 0, 0).next();
        bufferBuilder.vertex((float)j + 176.0f, (float)k - 96.0f - 16.0f, 0.0).color(200, 0, 0, 0).next();
        tessellator.draw();
        GlStateManager.disableBlend();
        double d = 0.0;
        for (int l = 0; l < list.size(); ++l) {
            float h;
            float g;
            float f;
            int r;
            ProfilerTiming profilerTiming2 = list.get(l);
            m = MathHelper.floor(profilerTiming2.parentSectionUsagePercentage / 4.0) + 1;
            bufferBuilder.begin(6, VertexFormats.POSITION_COLOR);
            int n = profilerTiming2.getColor();
            int o = n >> 16 & 0xFF;
            int p = n >> 8 & 0xFF;
            int q = n & 0xFF;
            bufferBuilder.vertex(j, k, 0.0).color(o, p, q, 255).next();
            for (r = m; r >= 0; --r) {
                f = (float)((d + profilerTiming2.parentSectionUsagePercentage * (double)r / (double)m) * 6.2831854820251465 / 100.0);
                g = MathHelper.sin(f) * 160.0f;
                h = MathHelper.cos(f) * 160.0f * 0.5f;
                bufferBuilder.vertex((float)j + g, (float)k - h, 0.0).color(o, p, q, 255).next();
            }
            tessellator.draw();
            bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
            for (r = m; r >= 0; --r) {
                f = (float)((d + profilerTiming2.parentSectionUsagePercentage * (double)r / (double)m) * 6.2831854820251465 / 100.0);
                g = MathHelper.sin(f) * 160.0f;
                h = MathHelper.cos(f) * 160.0f * 0.5f;
                bufferBuilder.vertex((float)j + g, (float)k - h, 0.0).color(o >> 1, p >> 1, q >> 1, 255).next();
                bufferBuilder.vertex((float)j + g, (float)k - h + 10.0f, 0.0).color(o >> 1, p >> 1, q >> 1, 255).next();
            }
            tessellator.draw();
            d += profilerTiming2.parentSectionUsagePercentage;
        }
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
        decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
        GlStateManager.enableTexture();
        String string = "";
        if (!"unspecified".equals(profilerTiming.name)) {
            string = string + "[0] ";
        }
        string = profilerTiming.name.isEmpty() ? string + "ROOT " : string + profilerTiming.name + ' ';
        m = 0xFFFFFF;
        this.textRenderer.drawWithShadow(string, j - 160, k - 80 - 16, 0xFFFFFF);
        string = decimalFormat.format(profilerTiming.totalUsagePercentage) + "%";
        this.textRenderer.drawWithShadow(string, j + 160 - this.textRenderer.getStringWidth(string), k - 80 - 16, 0xFFFFFF);
        for (int s = 0; s < list.size(); ++s) {
            ProfilerTiming profilerTiming3 = list.get(s);
            StringBuilder stringBuilder = new StringBuilder();
            if ("unspecified".equals(profilerTiming3.name)) {
                stringBuilder.append("[?] ");
            } else {
                stringBuilder.append("[").append(s + 1).append("] ");
            }
            String string2 = stringBuilder.append(profilerTiming3.name).toString();
            this.textRenderer.drawWithShadow(string2, j - 160, k + 80 + s * 8 + 20, profilerTiming3.getColor());
            string2 = decimalFormat.format(profilerTiming3.parentSectionUsagePercentage) + "%";
            this.textRenderer.drawWithShadow(string2, j + 160 - 50 - this.textRenderer.getStringWidth(string2), k + 80 + s * 8 + 20, profilerTiming3.getColor());
            string2 = decimalFormat.format(profilerTiming3.totalUsagePercentage) + "%";
            this.textRenderer.drawWithShadow(string2, j + 160 - this.textRenderer.getStringWidth(string2), k + 80 + s * 8 + 20, profilerTiming3.getColor());
        }
    }

    public void scheduleStop() {
        this.running = false;
    }

    public void openPauseMenu(boolean bl) {
        boolean bl2;
        if (this.currentScreen != null) {
            return;
        }
        boolean bl3 = bl2 = this.isIntegratedServerRunning() && !this.server.isRemote();
        if (bl2) {
            this.openScreen(new GameMenuScreen(!bl));
            this.soundManager.pauseAll();
        } else {
            this.openScreen(new GameMenuScreen(true));
        }
    }

    private void method_1590(boolean bl) {
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
            if (!this.world.getBlockState(blockPos).isAir() && this.interactionManager.method_2902(blockPos, direction = blockHitResult.getSide())) {
                this.particleManager.addBlockBreakingParticles(blockPos, direction);
                this.player.swingHand(Hand.MAIN_HAND);
            }
            return;
        }
        this.interactionManager.cancelBlockBreaking();
    }

    private void doAttack() {
        if (this.attackCooldown > 0) {
            return;
        }
        if (this.crosshairTarget == null) {
            LOGGER.error("Null returned as 'hitResult', this shouldn't happen!");
            if (this.interactionManager.hasLimitedAttackSpeed()) {
                this.attackCooldown = 10;
            }
            return;
        }
        if (this.player.isRiding()) {
            return;
        }
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
            ItemStack itemStack = this.player.getStackInHand(hand);
            if (this.crosshairTarget != null) {
                switch (this.crosshairTarget.getType()) {
                    case ENTITY: {
                        EntityHitResult entityHitResult = (EntityHitResult)this.crosshairTarget;
                        Entity entity = entityHitResult.getEntity();
                        if (this.interactionManager.interactEntityAtLocation(this.player, entity, entityHitResult, hand) == ActionResult.SUCCESS) {
                            return;
                        }
                        if (this.interactionManager.interactEntity(this.player, entity, hand) != ActionResult.SUCCESS) break;
                        return;
                    }
                    case BLOCK: {
                        BlockHitResult blockHitResult = (BlockHitResult)this.crosshairTarget;
                        int i = itemStack.getCount();
                        ActionResult actionResult = this.interactionManager.interactBlock(this.player, this.world, hand, blockHitResult);
                        if (actionResult == ActionResult.SUCCESS) {
                            this.player.swingHand(hand);
                            if (!itemStack.isEmpty() && (itemStack.getCount() != i || this.interactionManager.hasCreativeInventory())) {
                                this.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
                            }
                            return;
                        }
                        if (actionResult != ActionResult.FAIL) break;
                        return;
                    }
                }
            }
            if (itemStack.isEmpty() || this.interactionManager.interactItem(this.player, this.world, hand) != ActionResult.SUCCESS) continue;
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
        if (!this.paused) {
            this.inGameHud.tick();
        }
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
            if (this.player.getHealth() <= 0.0f && !(this.currentScreen instanceof DeathScreen)) {
                this.openScreen(null);
            } else if (this.player.isSleeping() && this.world != null) {
                this.openScreen(new SleepingChatScreen());
            }
        } else if (this.currentScreen != null && this.currentScreen instanceof SleepingChatScreen && !this.player.isSleeping()) {
            this.openScreen(null);
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
            this.profiler.swap("GLFW events");
            GLX.pollEvents();
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
                if (this.world.getTicksSinceLightning() > 0) {
                    this.world.setLightningTicksLeft(this.world.getTicksSinceLightning() - 1);
                }
                this.world.tickEntities();
            }
        } else if (this.gameRenderer.isShaderEnabled()) {
            this.gameRenderer.disableShader();
        }
        if (!this.paused) {
            this.musicTracker.tick();
        }
        this.soundManager.tick(this.paused);
        if (this.world != null) {
            if (!this.paused) {
                this.world.setMobSpawnOptions(this.world.getDifficulty() != Difficulty.PEACEFUL, true);
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
                this.world.doRandomBlockDisplayTicks(MathHelper.floor(this.player.x), MathHelper.floor(this.player.y), MathHelper.floor(this.player.z));
            }
            this.profiler.swap("particles");
            if (!this.paused) {
                this.particleManager.tick();
            }
        } else if (this.connection != null) {
            this.profiler.swap("pendingConnection");
            this.connection.tick();
        }
        this.profiler.swap("keyboard");
        this.keyboard.pollDebugCrash();
        this.profiler.pop();
    }

    private void handleInputEvents() {
        boolean bl3;
        while (this.options.keyTogglePerspective.wasPressed()) {
            ++this.options.perspective;
            if (this.options.perspective > 2) {
                this.options.perspective = 0;
            }
            if (this.options.perspective == 0) {
                this.gameRenderer.onCameraEntitySet(this.getCameraEntity());
            } else if (this.options.perspective == 1) {
                this.gameRenderer.onCameraEntitySet(null);
            }
            this.worldRenderer.scheduleTerrainUpdate();
        }
        while (this.options.keySmoothCamera.wasPressed()) {
            this.options.smoothCameraEnabled = !this.options.smoothCameraEnabled;
        }
        for (int i = 0; i < 9; ++i) {
            boolean bl = this.options.keySaveToolbarActivator.isPressed();
            boolean bl2 = this.options.keyLoadToolbarActivator.isPressed();
            if (!this.options.keysHotbar[i].wasPressed()) continue;
            if (this.player.isSpectator()) {
                this.inGameHud.getSpectatorHud().selectSlot(i);
                continue;
            }
            if (this.player.isCreative() && this.currentScreen == null && (bl2 || bl)) {
                CreativeInventoryScreen.onHotbarKeyPress(this, i, bl2, bl);
                continue;
            }
            this.player.inventory.selectedSlot = i;
        }
        while (this.options.keyInventory.wasPressed()) {
            if (this.interactionManager.hasRidingInventory()) {
                this.player.openRidingInventory();
                continue;
            }
            this.tutorialManager.onInventoryOpened();
            this.openScreen(new InventoryScreen(this.player));
        }
        while (this.options.keyAdvancements.wasPressed()) {
            this.openScreen(new AdvancementsScreen(this.player.networkHandler.getAdvancementHandler()));
        }
        while (this.options.keySwapHands.wasPressed()) {
            if (this.player.isSpectator()) continue;
            this.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_HELD_ITEMS, BlockPos.ORIGIN, Direction.DOWN));
        }
        while (this.options.keyDrop.wasPressed()) {
            if (this.player.isSpectator()) continue;
            this.player.dropSelectedItem(Screen.hasControlDown());
        }
        boolean bl = bl3 = this.options.chatVisibility != ChatVisibility.HIDDEN;
        if (bl3) {
            while (this.options.keyChat.wasPressed()) {
                this.openScreen(new ChatScreen(""));
            }
            if (this.currentScreen == null && this.overlay == null && this.options.keyCommand.wasPressed()) {
                this.openScreen(new ChatScreen("/"));
            }
        }
        if (this.player.isUsingItem()) {
            if (!this.options.keyUse.isPressed()) {
                this.interactionManager.stopUsingItem(this.player);
            }
            while (this.options.keyAttack.wasPressed()) {
            }
            while (this.options.keyUse.wasPressed()) {
            }
            while (this.options.keyPickItem.wasPressed()) {
            }
        } else {
            while (this.options.keyAttack.wasPressed()) {
                this.doAttack();
            }
            while (this.options.keyUse.wasPressed()) {
                this.doItemUse();
            }
            while (this.options.keyPickItem.wasPressed()) {
                this.doItemPick();
            }
        }
        if (this.options.keyUse.isPressed() && this.itemUseCooldown == 0 && !this.player.isUsingItem()) {
            this.doItemUse();
        }
        this.method_1590(this.currentScreen == null && this.options.keyAttack.isPressed() && this.mouse.isCursorLocked());
    }

    public void startIntegratedServer(String name, String displayName, @Nullable LevelInfo levelInfo) {
        this.disconnect();
        WorldSaveHandler worldSaveHandler = this.levelStorage.createSaveHandler(name, null);
        LevelProperties levelProperties = worldSaveHandler.readProperties();
        if (levelProperties == null && levelInfo != null) {
            levelProperties = new LevelProperties(levelInfo, name);
            worldSaveHandler.saveWorld(levelProperties);
        }
        if (levelInfo == null) {
            levelInfo = new LevelInfo(levelProperties);
        }
        this.worldGenProgressTracker.set(null);
        try {
            YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(this.netProxy, UUID.randomUUID().toString());
            MinecraftSessionService minecraftSessionService = yggdrasilAuthenticationService.createMinecraftSessionService();
            GameProfileRepository gameProfileRepository = yggdrasilAuthenticationService.createProfileRepository();
            UserCache userCache = new UserCache(gameProfileRepository, new File(this.runDirectory, MinecraftServer.USER_CACHE_FILE.getName()));
            SkullBlockEntity.setUserCache(userCache);
            SkullBlockEntity.setSessionService(minecraftSessionService);
            UserCache.setUseRemote(false);
            this.server = new IntegratedServer(this, name, displayName, levelInfo, yggdrasilAuthenticationService, minecraftSessionService, gameProfileRepository, userCache, i -> {
                WorldGenerationProgressTracker worldGenerationProgressTracker = new WorldGenerationProgressTracker(i + 0);
                worldGenerationProgressTracker.start();
                this.worldGenProgressTracker.set(worldGenerationProgressTracker);
                return new QueueingWorldGenerationProgressListener(worldGenerationProgressTracker, this.renderTaskQueue::add);
            });
            this.server.start();
            this.isIntegratedServerRunning = true;
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Starting integrated server");
            CrashReportSection crashReportSection = crashReport.addElement("Starting integrated server");
            crashReportSection.add("Level ID", name);
            crashReportSection.add("Level Name", displayName);
            throw new CrashException(crashReport);
        }
        while (this.worldGenProgressTracker.get() == null) {
            Thread.yield();
        }
        LevelLoadingScreen levelLoadingScreen = new LevelLoadingScreen(this.worldGenProgressTracker.get());
        this.openScreen(levelLoadingScreen);
        while (!this.server.isLoading()) {
            levelLoadingScreen.tick();
            this.render(false);
            try {
                Thread.sleep(16L);
            }
            catch (InterruptedException crashReport) {
                // empty catch block
            }
            if (!this.crashed || this.crashReport == null) continue;
            this.printCrashReport(this.crashReport);
            return;
        }
        SocketAddress socketAddress = this.server.getNetworkIo().bindLocal();
        ClientConnection clientConnection = ClientConnection.connectLocal(socketAddress);
        clientConnection.setPacketListener(new ClientLoginNetworkHandler(clientConnection, this, null, text -> {}));
        clientConnection.send(new HandshakeC2SPacket(socketAddress.toString(), 0, NetworkState.LOGIN));
        clientConnection.send(new LoginHelloC2SPacket(this.getSession().getProfile()));
        this.connection = clientConnection;
    }

    public void joinWorld(ClientWorld clientWorld) {
        ProgressScreen progressScreen = new ProgressScreen();
        progressScreen.method_15412(new TranslatableText("connect.joining", new Object[0]));
        this.reset(progressScreen);
        this.world = clientWorld;
        this.setWorld(clientWorld);
        if (!this.isIntegratedServerRunning) {
            YggdrasilAuthenticationService authenticationService = new YggdrasilAuthenticationService(this.netProxy, UUID.randomUUID().toString());
            MinecraftSessionService minecraftSessionService = authenticationService.createMinecraftSessionService();
            GameProfileRepository gameProfileRepository = authenticationService.createProfileRepository();
            UserCache userCache = new UserCache(gameProfileRepository, new File(this.runDirectory, MinecraftServer.USER_CACHE_FILE.getName()));
            SkullBlockEntity.setUserCache(userCache);
            SkullBlockEntity.setSessionService(minecraftSessionService);
            UserCache.setUseRemote(false);
        }
    }

    public void disconnect() {
        this.disconnect(new ProgressScreen());
    }

    public void disconnect(Screen screen) {
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.getNetworkHandler();
        if (clientPlayNetworkHandler != null) {
            this.cancelTasks();
            clientPlayNetworkHandler.clearWorld();
        }
        IntegratedServer integratedServer = this.server;
        this.server = null;
        this.gameRenderer.reset();
        this.interactionManager = null;
        NarratorManager.INSTANCE.clear();
        this.reset(screen);
        if (this.world != null) {
            if (integratedServer != null) {
                while (!integratedServer.isStopping()) {
                    this.render(false);
                }
            }
            this.builtinPackProvider.clear();
            this.inGameHud.clear();
            this.setCurrentServerEntry(null);
            this.isIntegratedServerRunning = false;
            this.game.onLeaveGameSession();
        }
        this.world = null;
        this.setWorld(null);
        this.player = null;
    }

    private void reset(Screen screen) {
        this.musicTracker.stop();
        this.soundManager.stopAll();
        this.cameraEntity = null;
        this.connection = null;
        this.openScreen(screen);
        this.render(false);
    }

    private void setWorld(@Nullable ClientWorld clientWorld) {
        if (this.worldRenderer != null) {
            this.worldRenderer.setWorld(clientWorld);
        }
        if (this.particleManager != null) {
            this.particleManager.setWorld(clientWorld);
        }
        BlockEntityRenderDispatcher.INSTANCE.setWorld(clientWorld);
    }

    public final boolean isDemo() {
        return this.isDemo;
    }

    @Nullable
    public ClientPlayNetworkHandler getNetworkHandler() {
        return this.player == null ? null : this.player.networkHandler;
    }

    public static boolean isHudEnabled() {
        return instance == null || !MinecraftClient.instance.options.hudHidden;
    }

    public static boolean isFancyGraphicsEnabled() {
        return instance != null && MinecraftClient.instance.options.fancyGraphics;
    }

    public static boolean isAmbientOcclusionEnabled() {
        return instance != null && MinecraftClient.instance.options.ao != AoOption.OFF;
    }

    private void doItemPick() {
        ItemStack itemStack;
        if (this.crosshairTarget == null || this.crosshairTarget.getType() == HitResult.Type.MISS) {
            return;
        }
        boolean bl = this.player.abilities.creativeMode;
        BlockEntity blockEntity = null;
        HitResult.Type type = this.crosshairTarget.getType();
        if (type == HitResult.Type.BLOCK) {
            BlockPos blockPos = ((BlockHitResult)this.crosshairTarget).getBlockPos();
            BlockState blockState = this.world.getBlockState(blockPos);
            Block block = blockState.getBlock();
            if (blockState.isAir()) {
                return;
            }
            itemStack = block.getPickStack(this.world, blockPos, blockState);
            if (itemStack.isEmpty()) {
                return;
            }
            if (bl && Screen.hasControlDown() && block.hasBlockEntity()) {
                blockEntity = this.world.getBlockEntity(blockPos);
            }
        } else if (type == HitResult.Type.ENTITY && bl) {
            Entity entity = ((EntityHitResult)this.crosshairTarget).getEntity();
            if (entity instanceof PaintingEntity) {
                itemStack = new ItemStack(Items.PAINTING);
            } else if (entity instanceof LeadKnotEntity) {
                itemStack = new ItemStack(Items.LEAD);
            } else if (entity instanceof ItemFrameEntity) {
                ItemFrameEntity itemFrameEntity = (ItemFrameEntity)entity;
                ItemStack itemStack2 = itemFrameEntity.getHeldItemStack();
                itemStack = itemStack2.isEmpty() ? new ItemStack(Items.ITEM_FRAME) : itemStack2.copy();
            } else if (entity instanceof AbstractMinecartEntity) {
                Item item;
                AbstractMinecartEntity abstractMinecartEntity = (AbstractMinecartEntity)entity;
                switch (abstractMinecartEntity.getMinecartType()) {
                    case FURNACE: {
                        item = Items.FURNACE_MINECART;
                        break;
                    }
                    case CHEST: {
                        item = Items.CHEST_MINECART;
                        break;
                    }
                    case TNT: {
                        item = Items.TNT_MINECART;
                        break;
                    }
                    case HOPPER: {
                        item = Items.HOPPER_MINECART;
                        break;
                    }
                    case COMMAND_BLOCK: {
                        item = Items.COMMAND_BLOCK_MINECART;
                        break;
                    }
                    default: {
                        item = Items.MINECART;
                    }
                }
                itemStack = new ItemStack(item);
            } else if (entity instanceof BoatEntity) {
                itemStack = new ItemStack(((BoatEntity)entity).asItem());
            } else if (entity instanceof ArmorStandEntity) {
                itemStack = new ItemStack(Items.ARMOR_STAND);
            } else if (entity instanceof EnderCrystalEntity) {
                itemStack = new ItemStack(Items.END_CRYSTAL);
            } else {
                SpawnEggItem spawnEggItem = SpawnEggItem.forEntity(entity.getType());
                if (spawnEggItem == null) {
                    return;
                }
                itemStack = new ItemStack(spawnEggItem);
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
        PlayerInventory playerInventory = this.player.inventory;
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
        CompoundTag compoundTag = blockEntity.toTag(new CompoundTag());
        if (stack.getItem() instanceof SkullItem && compoundTag.contains("Owner")) {
            CompoundTag compoundTag2 = compoundTag.getCompound("Owner");
            stack.getOrCreateTag().put("SkullOwner", compoundTag2);
            return stack;
        }
        stack.putSubTag("BlockEntityTag", compoundTag);
        CompoundTag compoundTag2 = new CompoundTag();
        ListTag listTag = new ListTag();
        listTag.add(new StringTag("\"(+NBT)\""));
        compoundTag2.put("Lore", listTag);
        stack.putSubTag("display", compoundTag2);
        return stack;
    }

    public CrashReport addDetailsToCrashReport(CrashReport report) {
        CrashReportSection crashReportSection = report.getSystemDetailsSection();
        crashReportSection.add("Launched Version", () -> this.gameVersion);
        crashReportSection.add("LWJGL", GLX::getLWJGLVersion);
        crashReportSection.add("OpenGL", GLX::getOpenGLVersionString);
        crashReportSection.add("GL Caps", GLX::getCapsString);
        crashReportSection.add("Using VBOs", () -> "Yes");
        crashReportSection.add("Is Modded", () -> {
            String string = ClientBrandRetriever.getClientModName();
            if (!"vanilla".equals(string)) {
                return "Definitely; Client brand changed to '" + string + "'";
            }
            if (MinecraftClient.class.getSigners() == null) {
                return "Very likely; Jar signature invalidated";
            }
            return "Probably not. Jar signature remains and client brand is untouched.";
        });
        crashReportSection.add("Type", "Client (map_client.txt)");
        crashReportSection.add("Resource Packs", () -> {
            StringBuilder stringBuilder = new StringBuilder();
            for (String string : this.options.resourcePacks) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(", ");
                }
                stringBuilder.append(string);
                if (!this.options.incompatibleResourcePacks.contains(string)) continue;
                stringBuilder.append(" (incompatible)");
            }
            return stringBuilder.toString();
        });
        crashReportSection.add("Current Language", () -> this.languageManager.getLanguage().toString());
        crashReportSection.add("CPU", GLX::getCpuInfo);
        if (this.world != null) {
            this.world.addDetailsToCrashReport(report);
        }
        return report;
    }

    public static MinecraftClient getInstance() {
        return instance;
    }

    public CompletableFuture<Void> reloadResourcesConcurrently() {
        return this.submit(this::reloadResources).thenCompose(completableFuture -> completableFuture);
    }

    @Override
    public void addSnooperInfo(Snooper snooper) {
        snooper.addInfo("fps", currentFps);
        snooper.addInfo("vsync_enabled", this.options.enableVsync);
        int i = GLX.getRefreshRate(this.window);
        snooper.addInfo("display_frequency", i);
        snooper.addInfo("display_type", this.window.isFullscreen() ? "fullscreen" : "windowed");
        snooper.addInfo("run_time", (Util.getMeasuringTimeMs() - snooper.getStartTime()) / 60L * 1000L);
        snooper.addInfo("current_action", this.getCurrentAction());
        snooper.addInfo("language", this.options.language == null ? "en_us" : this.options.language);
        String string = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? "little" : "big";
        snooper.addInfo("endianness", string);
        snooper.addInfo("subtitles", this.options.showSubtitles);
        snooper.addInfo("touch", this.options.touchscreen ? "touch" : "mouse");
        int j = 0;
        for (ClientResourcePackProfile clientResourcePackProfile : this.resourcePackManager.getEnabledProfiles()) {
            if (clientResourcePackProfile.isAlwaysEnabled() || clientResourcePackProfile.isPinned()) continue;
            snooper.addInfo("resource_pack[" + j++ + "]", clientResourcePackProfile.getName());
        }
        snooper.addInfo("resource_packs", j);
        if (this.server != null && this.server.getSnooper() != null) {
            snooper.addInfo("snooper_partner", this.server.getSnooper().getToken());
        }
    }

    private String getCurrentAction() {
        if (this.server != null) {
            if (this.server.isRemote()) {
                return "hosting_lan";
            }
            return "singleplayer";
        }
        if (this.currentServerEntry != null) {
            if (this.currentServerEntry.isLocal()) {
                return "playing_lan";
            }
            return "multiplayer";
        }
        return "out_of_game";
    }

    public static int getMaxTextureSize() {
        if (cachedMaxTextureSize == -1) {
            for (int i = 16384; i > 0; i >>= 1) {
                GlStateManager.texImage2D(32868, 0, 6408, i, i, 0, 6408, 5121, null);
                int j = GlStateManager.getTexLevelParameter(32868, 0, 4096);
                if (j == 0) continue;
                cachedMaxTextureSize = i;
                return i;
            }
            cachedMaxTextureSize = MathHelper.clamp(GlStateManager.getInteger(3379), 1024, 16384);
            LOGGER.info("Failed to determine maximum texture size by probing, trying GL_MAX_TEXTURE_SIZE = {}", (Object)cachedMaxTextureSize);
        }
        return cachedMaxTextureSize;
    }

    public void setCurrentServerEntry(ServerInfo serverInfo) {
        this.currentServerEntry = serverInfo;
    }

    @Nullable
    public ServerInfo getCurrentServerEntry() {
        return this.currentServerEntry;
    }

    public boolean isInSingleplayer() {
        return this.isIntegratedServerRunning;
    }

    public boolean isIntegratedServerRunning() {
        return this.isIntegratedServerRunning && this.server != null;
    }

    @Nullable
    public IntegratedServer getServer() {
        return this.server;
    }

    public Snooper getSnooper() {
        return this.snooper;
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
        return this.netProxy;
    }

    public TextureManager getTextureManager() {
        return this.textureManager;
    }

    public ResourceManager getResourceManager() {
        return this.resourceManager;
    }

    public ResourcePackManager<ClientResourcePackProfile> getResourcePackManager() {
        return this.resourcePackManager;
    }

    public ClientBuiltinResourcePackProvider getResourcePackDownloader() {
        return this.builtinPackProvider;
    }

    public File getResourcePackDir() {
        return this.resourcePackDir;
    }

    public LanguageManager getLanguageManager() {
        return this.languageManager;
    }

    public SpriteAtlasTexture getSpriteAtlas() {
        return this.spriteAtlas;
    }

    public boolean is64Bit() {
        return this.is64Bit;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public SoundManager getSoundManager() {
        return this.soundManager;
    }

    public MusicTracker.MusicType getMusicType() {
        if (this.currentScreen instanceof CreditsScreen) {
            return MusicTracker.MusicType.CREDITS;
        }
        if (this.player != null) {
            if (this.player.world.dimension instanceof TheNetherDimension) {
                return MusicTracker.MusicType.NETHER;
            }
            if (this.player.world.dimension instanceof TheEndDimension) {
                if (this.inGameHud.getBossBarHud().shouldPlayDragonMusic()) {
                    return MusicTracker.MusicType.END_BOSS;
                }
                return MusicTracker.MusicType.END;
            }
            Biome.Category category = this.player.world.getBiome(new BlockPos(this.player)).getCategory();
            if (this.musicTracker.isPlayingType(MusicTracker.MusicType.UNDER_WATER) || this.player.isSubmergedInWater() && !this.musicTracker.isPlayingType(MusicTracker.MusicType.GAME) && (category == Biome.Category.OCEAN || category == Biome.Category.RIVER)) {
                return MusicTracker.MusicType.UNDER_WATER;
            }
            if (this.player.abilities.creativeMode && this.player.abilities.allowFlying) {
                return MusicTracker.MusicType.CREATIVE;
            }
            return MusicTracker.MusicType.GAME;
        }
        return MusicTracker.MusicType.MENU;
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

    public EntityRenderDispatcher getEntityRenderManager() {
        return this.entityRenderManager;
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

    public static int getCurrentFps() {
        return currentFps;
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

    public BlockColors getBlockColorMap() {
        return this.blockColorMap;
    }

    public boolean hasReducedDebugInfo() {
        return this.player != null && this.player.getReducedDebugInfo() || this.options.reducedDebugInfo;
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

    public FontManager getFontManager() {
        return this.fontManager;
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

    public Profiler getProfiler() {
        return this.profiler;
    }

    public MinecraftClientGame getGame() {
        return this.game;
    }

    public SplashTextResourceSupplier getSplashTextLoader() {
        return this.splashTextLoader;
    }

    @Nullable
    public Overlay getOverlay() {
        return this.overlay;
    }

    private static /* synthetic */ ResourcePack method_1528(Supplier supplier) {
        return new Format3ResourcePack((ResourcePack)supplier.get(), Format3ResourcePack.NEW_TO_OLD_MAP);
    }
}
