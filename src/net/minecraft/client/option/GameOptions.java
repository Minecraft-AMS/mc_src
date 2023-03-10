/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Splitter
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.google.common.io.Files
 *  com.google.gson.Gson
 *  com.google.gson.reflect.TypeToken
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2FloatMap
 *  it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.ArrayUtils
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.option;

import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.AoMode;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.option.ChatVisibility;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.NarratorMode;
import net.minecraft.client.option.Option;
import net.minecraft.client.option.ParticlesMode;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.option.StickyKeyBinding;
import net.minecraft.client.render.ChunkBuilderMode;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.client.util.InputUtil;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Arm;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class GameOptions {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new Gson();
    private static final TypeToken<List<String>> STRING_LIST_TYPE = new TypeToken<List<String>>(){};
    public static final int field_32149 = 2;
    public static final int field_32150 = 4;
    public static final int field_32152 = 8;
    public static final int field_32153 = 12;
    public static final int field_32154 = 16;
    public static final int field_32155 = 32;
    private static final Splitter COLON_SPLITTER = Splitter.on((char)':').limit(2);
    private static final float field_32151 = 1.0f;
    public static final String field_34785 = "";
    public boolean monochromeLogo;
    public boolean hideLightningFlashes;
    public double mouseSensitivity = 0.5;
    public int viewDistance;
    public int simulationDistance;
    private int serverViewDistance = 0;
    public float entityDistanceScaling = 1.0f;
    public int maxFps = 120;
    public CloudRenderMode cloudRenderMode = CloudRenderMode.FANCY;
    public GraphicsMode graphicsMode = GraphicsMode.FANCY;
    public AoMode ao = AoMode.MAX;
    public ChunkBuilderMode chunkBuilderMode = ChunkBuilderMode.NONE;
    public List<String> resourcePacks = Lists.newArrayList();
    public List<String> incompatibleResourcePacks = Lists.newArrayList();
    public ChatVisibility chatVisibility = ChatVisibility.FULL;
    public double chatOpacity = 1.0;
    public double chatLineSpacing;
    public double textBackgroundOpacity = 0.5;
    @Nullable
    public String fullscreenResolution;
    public boolean hideServerAddress;
    public boolean advancedItemTooltips;
    public boolean pauseOnLostFocus = true;
    private final Set<PlayerModelPart> enabledPlayerModelParts = EnumSet.allOf(PlayerModelPart.class);
    public Arm mainArm = Arm.RIGHT;
    public int overrideWidth;
    public int overrideHeight;
    public boolean heldItemTooltips = true;
    public double chatScale = 1.0;
    public double chatWidth = 1.0;
    public double chatHeightUnfocused = 0.44366195797920227;
    public double chatHeightFocused = 1.0;
    public double chatDelay;
    public int mipmapLevels = 4;
    private final Object2FloatMap<SoundCategory> soundVolumeLevels = (Object2FloatMap)Util.make(new Object2FloatOpenHashMap(), object2FloatOpenHashMap -> object2FloatOpenHashMap.defaultReturnValue(1.0f));
    public boolean useNativeTransport = true;
    public AttackIndicator attackIndicator = AttackIndicator.CROSSHAIR;
    public TutorialStep tutorialStep = TutorialStep.MOVEMENT;
    public boolean joinedFirstServer = false;
    public boolean hideBundleTutorial = false;
    public int biomeBlendRadius = 2;
    public double mouseWheelSensitivity = 1.0;
    public boolean rawMouseInput = true;
    public int glDebugVerbosity = 1;
    public boolean autoJump = true;
    public boolean autoSuggestions = true;
    public boolean chatColors = true;
    public boolean chatLinks = true;
    public boolean chatLinksPrompt = true;
    public boolean enableVsync = true;
    public boolean entityShadows = true;
    public boolean forceUnicodeFont;
    public boolean invertYMouse;
    public boolean discreteMouseScroll;
    public boolean realmsNotifications = true;
    public boolean allowServerListing = true;
    public boolean reducedDebugInfo;
    public boolean showSubtitles;
    public boolean backgroundForChatOnly = true;
    public boolean touchscreen;
    public boolean fullscreen;
    public boolean bobView = true;
    public boolean sneakToggled;
    public boolean sprintToggled;
    public boolean skipMultiplayerWarning;
    public boolean skipRealms32BitWarning;
    public boolean hideMatchedNames = true;
    public boolean showAutosaveIndicator = true;
    public final KeyBinding forwardKey = new KeyBinding("key.forward", 87, "key.categories.movement");
    public final KeyBinding leftKey = new KeyBinding("key.left", 65, "key.categories.movement");
    public final KeyBinding backKey = new KeyBinding("key.back", 83, "key.categories.movement");
    public final KeyBinding rightKey = new KeyBinding("key.right", 68, "key.categories.movement");
    public final KeyBinding jumpKey = new KeyBinding("key.jump", 32, "key.categories.movement");
    public final KeyBinding sneakKey = new StickyKeyBinding("key.sneak", 340, "key.categories.movement", () -> this.sneakToggled);
    public final KeyBinding sprintKey = new StickyKeyBinding("key.sprint", 341, "key.categories.movement", () -> this.sprintToggled);
    public final KeyBinding inventoryKey = new KeyBinding("key.inventory", 69, "key.categories.inventory");
    public final KeyBinding swapHandsKey = new KeyBinding("key.swapOffhand", 70, "key.categories.inventory");
    public final KeyBinding dropKey = new KeyBinding("key.drop", 81, "key.categories.inventory");
    public final KeyBinding useKey = new KeyBinding("key.use", InputUtil.Type.MOUSE, 1, "key.categories.gameplay");
    public final KeyBinding attackKey = new KeyBinding("key.attack", InputUtil.Type.MOUSE, 0, "key.categories.gameplay");
    public final KeyBinding pickItemKey = new KeyBinding("key.pickItem", InputUtil.Type.MOUSE, 2, "key.categories.gameplay");
    public final KeyBinding chatKey = new KeyBinding("key.chat", 84, "key.categories.multiplayer");
    public final KeyBinding playerListKey = new KeyBinding("key.playerlist", 258, "key.categories.multiplayer");
    public final KeyBinding commandKey = new KeyBinding("key.command", 47, "key.categories.multiplayer");
    public final KeyBinding socialInteractionsKey = new KeyBinding("key.socialInteractions", 80, "key.categories.multiplayer");
    public final KeyBinding screenshotKey = new KeyBinding("key.screenshot", 291, "key.categories.misc");
    public final KeyBinding togglePerspectiveKey = new KeyBinding("key.togglePerspective", 294, "key.categories.misc");
    public final KeyBinding smoothCameraKey = new KeyBinding("key.smoothCamera", InputUtil.UNKNOWN_KEY.getCode(), "key.categories.misc");
    public final KeyBinding fullscreenKey = new KeyBinding("key.fullscreen", 300, "key.categories.misc");
    public final KeyBinding spectatorOutlinesKey = new KeyBinding("key.spectatorOutlines", InputUtil.UNKNOWN_KEY.getCode(), "key.categories.misc");
    public final KeyBinding advancementsKey = new KeyBinding("key.advancements", 76, "key.categories.misc");
    public final KeyBinding[] hotbarKeys = new KeyBinding[]{new KeyBinding("key.hotbar.1", 49, "key.categories.inventory"), new KeyBinding("key.hotbar.2", 50, "key.categories.inventory"), new KeyBinding("key.hotbar.3", 51, "key.categories.inventory"), new KeyBinding("key.hotbar.4", 52, "key.categories.inventory"), new KeyBinding("key.hotbar.5", 53, "key.categories.inventory"), new KeyBinding("key.hotbar.6", 54, "key.categories.inventory"), new KeyBinding("key.hotbar.7", 55, "key.categories.inventory"), new KeyBinding("key.hotbar.8", 56, "key.categories.inventory"), new KeyBinding("key.hotbar.9", 57, "key.categories.inventory")};
    public final KeyBinding saveToolbarActivatorKey = new KeyBinding("key.saveToolbarActivator", 67, "key.categories.creative");
    public final KeyBinding loadToolbarActivatorKey = new KeyBinding("key.loadToolbarActivator", 88, "key.categories.creative");
    public final KeyBinding[] allKeys = (KeyBinding[])ArrayUtils.addAll((Object[])new KeyBinding[]{this.attackKey, this.useKey, this.forwardKey, this.leftKey, this.backKey, this.rightKey, this.jumpKey, this.sneakKey, this.sprintKey, this.dropKey, this.inventoryKey, this.chatKey, this.playerListKey, this.pickItemKey, this.commandKey, this.socialInteractionsKey, this.screenshotKey, this.togglePerspectiveKey, this.smoothCameraKey, this.fullscreenKey, this.spectatorOutlinesKey, this.swapHandsKey, this.saveToolbarActivatorKey, this.loadToolbarActivatorKey, this.advancementsKey}, (Object[])this.hotbarKeys);
    protected MinecraftClient client;
    private final File optionsFile;
    public Difficulty difficulty = Difficulty.NORMAL;
    public boolean hudHidden;
    private Perspective perspective = Perspective.FIRST_PERSON;
    public boolean debugEnabled;
    public boolean debugProfilerEnabled;
    public boolean debugTpsEnabled;
    public String lastServer = "";
    public boolean smoothCameraEnabled;
    public double fov = 70.0;
    public float distortionEffectScale = 1.0f;
    public float fovEffectScale = 1.0f;
    public double gamma;
    public int guiScale;
    public ParticlesMode particles = ParticlesMode.ALL;
    public NarratorMode narrator = NarratorMode.OFF;
    public String language = "en_us";
    public String soundDevice = "";
    public boolean syncChunkWrites;

    public GameOptions(MinecraftClient client, File optionsFile) {
        this.client = client;
        this.optionsFile = new File(optionsFile, "options.txt");
        if (client.is64Bit() && Runtime.getRuntime().maxMemory() >= 1000000000L) {
            Option.RENDER_DISTANCE.setMax(32.0f);
            Option.SIMULATION_DISTANCE.setMax(32.0f);
        } else {
            Option.RENDER_DISTANCE.setMax(16.0f);
            Option.SIMULATION_DISTANCE.setMax(16.0f);
        }
        this.viewDistance = client.is64Bit() ? 12 : 8;
        this.simulationDistance = client.is64Bit() ? 12 : 8;
        this.gamma = 0.5;
        this.syncChunkWrites = Util.getOperatingSystem() == Util.OperatingSystem.WINDOWS;
        this.load();
    }

    public float getTextBackgroundOpacity(float fallback) {
        return this.backgroundForChatOnly ? fallback : (float)this.textBackgroundOpacity;
    }

    public int getTextBackgroundColor(float fallbackOpacity) {
        return (int)(this.getTextBackgroundOpacity(fallbackOpacity) * 255.0f) << 24 & 0xFF000000;
    }

    public int getTextBackgroundColor(int fallbackColor) {
        return this.backgroundForChatOnly ? fallbackColor : (int)(this.textBackgroundOpacity * 255.0) << 24 & 0xFF000000;
    }

    public void setKeyCode(KeyBinding key, InputUtil.Key code) {
        key.setBoundKey(code);
        this.write();
    }

    private void accept(Visitor visitor) {
        this.autoJump = visitor.visitBoolean("autoJump", this.autoJump);
        this.autoSuggestions = visitor.visitBoolean("autoSuggestions", this.autoSuggestions);
        this.chatColors = visitor.visitBoolean("chatColors", this.chatColors);
        this.chatLinks = visitor.visitBoolean("chatLinks", this.chatLinks);
        this.chatLinksPrompt = visitor.visitBoolean("chatLinksPrompt", this.chatLinksPrompt);
        this.enableVsync = visitor.visitBoolean("enableVsync", this.enableVsync);
        this.entityShadows = visitor.visitBoolean("entityShadows", this.entityShadows);
        this.forceUnicodeFont = visitor.visitBoolean("forceUnicodeFont", this.forceUnicodeFont);
        this.discreteMouseScroll = visitor.visitBoolean("discrete_mouse_scroll", this.discreteMouseScroll);
        this.invertYMouse = visitor.visitBoolean("invertYMouse", this.invertYMouse);
        this.realmsNotifications = visitor.visitBoolean("realmsNotifications", this.realmsNotifications);
        this.reducedDebugInfo = visitor.visitBoolean("reducedDebugInfo", this.reducedDebugInfo);
        this.showSubtitles = visitor.visitBoolean("showSubtitles", this.showSubtitles);
        this.touchscreen = visitor.visitBoolean("touchscreen", this.touchscreen);
        this.fullscreen = visitor.visitBoolean("fullscreen", this.fullscreen);
        this.bobView = visitor.visitBoolean("bobView", this.bobView);
        this.sneakToggled = visitor.visitBoolean("toggleCrouch", this.sneakToggled);
        this.sprintToggled = visitor.visitBoolean("toggleSprint", this.sprintToggled);
        this.monochromeLogo = visitor.visitBoolean("darkMojangStudiosBackground", this.monochromeLogo);
        this.hideLightningFlashes = visitor.visitBoolean("hideLightningFlashes", this.hideLightningFlashes);
        this.mouseSensitivity = visitor.visitDouble("mouseSensitivity", this.mouseSensitivity);
        this.fov = visitor.visitDouble("fov", (this.fov - 70.0) / 40.0) * 40.0 + 70.0;
        this.distortionEffectScale = visitor.visitFloat("screenEffectScale", this.distortionEffectScale);
        this.fovEffectScale = visitor.visitFloat("fovEffectScale", this.fovEffectScale);
        this.gamma = visitor.visitDouble("gamma", this.gamma);
        this.viewDistance = (int)MathHelper.clamp((double)visitor.visitInt("renderDistance", this.viewDistance), Option.RENDER_DISTANCE.getMin(), Option.RENDER_DISTANCE.getMax());
        this.simulationDistance = (int)MathHelper.clamp((double)visitor.visitInt("simulationDistance", this.simulationDistance), Option.SIMULATION_DISTANCE.getMin(), Option.SIMULATION_DISTANCE.getMax());
        this.entityDistanceScaling = visitor.visitFloat("entityDistanceScaling", this.entityDistanceScaling);
        this.guiScale = visitor.visitInt("guiScale", this.guiScale);
        this.particles = visitor.visitObject("particles", this.particles, ParticlesMode::byId, ParticlesMode::getId);
        this.maxFps = visitor.visitInt("maxFps", this.maxFps);
        this.difficulty = visitor.visitObject("difficulty", this.difficulty, Difficulty::byOrdinal, Difficulty::getId);
        this.graphicsMode = visitor.visitObject("graphicsMode", this.graphicsMode, GraphicsMode::byId, GraphicsMode::getId);
        this.ao = visitor.visitObject("ao", this.ao, GameOptions::loadAo, ao -> Integer.toString(ao.getId()));
        this.chunkBuilderMode = visitor.visitObject("prioritizeChunkUpdates", this.chunkBuilderMode, ChunkBuilderMode::get, ChunkBuilderMode::getId);
        this.biomeBlendRadius = visitor.visitInt("biomeBlendRadius", this.biomeBlendRadius);
        this.cloudRenderMode = visitor.visitObject("renderClouds", this.cloudRenderMode, GameOptions::loadCloudRenderMode, GameOptions::saveCloudRenderMode);
        this.resourcePacks = visitor.visitObject("resourcePacks", this.resourcePacks, GameOptions::parseList, arg_0 -> ((Gson)GSON).toJson(arg_0));
        this.incompatibleResourcePacks = visitor.visitObject("incompatibleResourcePacks", this.incompatibleResourcePacks, GameOptions::parseList, arg_0 -> ((Gson)GSON).toJson(arg_0));
        this.lastServer = visitor.visitString("lastServer", this.lastServer);
        this.language = visitor.visitString("lang", this.language);
        this.soundDevice = visitor.visitString("soundDevice", this.soundDevice);
        this.chatVisibility = visitor.visitObject("chatVisibility", this.chatVisibility, ChatVisibility::byId, ChatVisibility::getId);
        this.chatOpacity = visitor.visitDouble("chatOpacity", this.chatOpacity);
        this.chatLineSpacing = visitor.visitDouble("chatLineSpacing", this.chatLineSpacing);
        this.textBackgroundOpacity = visitor.visitDouble("textBackgroundOpacity", this.textBackgroundOpacity);
        this.backgroundForChatOnly = visitor.visitBoolean("backgroundForChatOnly", this.backgroundForChatOnly);
        this.hideServerAddress = visitor.visitBoolean("hideServerAddress", this.hideServerAddress);
        this.advancedItemTooltips = visitor.visitBoolean("advancedItemTooltips", this.advancedItemTooltips);
        this.pauseOnLostFocus = visitor.visitBoolean("pauseOnLostFocus", this.pauseOnLostFocus);
        this.overrideWidth = visitor.visitInt("overrideWidth", this.overrideWidth);
        this.overrideHeight = visitor.visitInt("overrideHeight", this.overrideHeight);
        this.heldItemTooltips = visitor.visitBoolean("heldItemTooltips", this.heldItemTooltips);
        this.chatHeightFocused = visitor.visitDouble("chatHeightFocused", this.chatHeightFocused);
        this.chatDelay = visitor.visitDouble("chatDelay", this.chatDelay);
        this.chatHeightUnfocused = visitor.visitDouble("chatHeightUnfocused", this.chatHeightUnfocused);
        this.chatScale = visitor.visitDouble("chatScale", this.chatScale);
        this.chatWidth = visitor.visitDouble("chatWidth", this.chatWidth);
        this.mipmapLevels = visitor.visitInt("mipmapLevels", this.mipmapLevels);
        this.useNativeTransport = visitor.visitBoolean("useNativeTransport", this.useNativeTransport);
        this.mainArm = visitor.visitObject("mainHand", this.mainArm, GameOptions::loadArm, GameOptions::saveArm);
        this.attackIndicator = visitor.visitObject("attackIndicator", this.attackIndicator, AttackIndicator::byId, AttackIndicator::getId);
        this.narrator = visitor.visitObject("narrator", this.narrator, NarratorMode::byId, NarratorMode::getId);
        this.tutorialStep = visitor.visitObject("tutorialStep", this.tutorialStep, TutorialStep::byName, TutorialStep::getName);
        this.mouseWheelSensitivity = visitor.visitDouble("mouseWheelSensitivity", this.mouseWheelSensitivity);
        this.rawMouseInput = visitor.visitBoolean("rawMouseInput", this.rawMouseInput);
        this.glDebugVerbosity = visitor.visitInt("glDebugVerbosity", this.glDebugVerbosity);
        this.skipMultiplayerWarning = visitor.visitBoolean("skipMultiplayerWarning", this.skipMultiplayerWarning);
        this.skipRealms32BitWarning = visitor.visitBoolean("skipRealms32bitWarning", this.skipRealms32BitWarning);
        this.hideMatchedNames = visitor.visitBoolean("hideMatchedNames", this.hideMatchedNames);
        this.joinedFirstServer = visitor.visitBoolean("joinedFirstServer", this.joinedFirstServer);
        this.hideBundleTutorial = visitor.visitBoolean("hideBundleTutorial", this.hideBundleTutorial);
        this.syncChunkWrites = visitor.visitBoolean("syncChunkWrites", this.syncChunkWrites);
        this.showAutosaveIndicator = visitor.visitBoolean("showAutosaveIndicator", this.showAutosaveIndicator);
        this.allowServerListing = visitor.visitBoolean("allowServerListing", this.allowServerListing);
        for (KeyBinding keyBinding : this.allKeys) {
            String string2;
            String string = keyBinding.getBoundKeyTranslationKey();
            if (string.equals(string2 = visitor.visitString("key_" + keyBinding.getTranslationKey(), string))) continue;
            keyBinding.setBoundKey(InputUtil.fromTranslationKey(string2));
        }
        for (SoundCategory soundCategory : SoundCategory.values()) {
            this.soundVolumeLevels.computeFloat((Object)soundCategory, (category, currentLevel) -> Float.valueOf(visitor.visitFloat("soundCategory_" + category.getName(), currentLevel != null ? currentLevel.floatValue() : 1.0f)));
        }
        for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
            boolean bl = this.enabledPlayerModelParts.contains((Object)playerModelPart);
            boolean bl2 = visitor.visitBoolean("modelPart_" + playerModelPart.getName(), bl);
            if (bl2 == bl) continue;
            this.setPlayerModelPart(playerModelPart, bl2);
        }
    }

    public void load() {
        try {
            if (!this.optionsFile.exists()) {
                return;
            }
            this.soundVolumeLevels.clear();
            NbtCompound nbtCompound = new NbtCompound();
            try (BufferedReader bufferedReader = Files.newReader((File)this.optionsFile, (Charset)Charsets.UTF_8);){
                bufferedReader.lines().forEach(line -> {
                    try {
                        Iterator iterator = COLON_SPLITTER.split((CharSequence)line).iterator();
                        nbtCompound.putString((String)iterator.next(), (String)iterator.next());
                    }
                    catch (Exception exception) {
                        LOGGER.warn("Skipping bad option: {}", line);
                    }
                });
            }
            final NbtCompound nbtCompound2 = this.update(nbtCompound);
            if (!nbtCompound2.contains("graphicsMode") && nbtCompound2.contains("fancyGraphics")) {
                this.graphicsMode = GameOptions.isTrue(nbtCompound2.getString("fancyGraphics")) ? GraphicsMode.FANCY : GraphicsMode.FAST;
            }
            this.accept(new Visitor(){

                @Nullable
                private String find(String key) {
                    return nbtCompound2.contains(key) ? nbtCompound2.getString(key) : null;
                }

                @Override
                public int visitInt(String key, int current) {
                    String string = this.find(key);
                    if (string != null) {
                        try {
                            return Integer.parseInt(string);
                        }
                        catch (NumberFormatException numberFormatException) {
                            LOGGER.warn("Invalid integer value for option {} = {}", new Object[]{key, string, numberFormatException});
                        }
                    }
                    return current;
                }

                @Override
                public boolean visitBoolean(String key, boolean current) {
                    String string = this.find(key);
                    return string != null ? GameOptions.isTrue(string) : current;
                }

                @Override
                public String visitString(String key, String current) {
                    return (String)MoreObjects.firstNonNull((Object)this.find(key), (Object)current);
                }

                @Override
                public double visitDouble(String key, double current) {
                    String string = this.find(key);
                    if (string != null) {
                        if (GameOptions.isTrue(string)) {
                            return 1.0;
                        }
                        if (GameOptions.isFalse(string)) {
                            return 0.0;
                        }
                        try {
                            return Double.parseDouble(string);
                        }
                        catch (NumberFormatException numberFormatException) {
                            LOGGER.warn("Invalid floating point value for option {} = {}", new Object[]{key, string, numberFormatException});
                        }
                    }
                    return current;
                }

                @Override
                public float visitFloat(String key, float current) {
                    String string = this.find(key);
                    if (string != null) {
                        if (GameOptions.isTrue(string)) {
                            return 1.0f;
                        }
                        if (GameOptions.isFalse(string)) {
                            return 0.0f;
                        }
                        try {
                            return Float.parseFloat(string);
                        }
                        catch (NumberFormatException numberFormatException) {
                            LOGGER.warn("Invalid floating point value for option {} = {}", new Object[]{key, string, numberFormatException});
                        }
                    }
                    return current;
                }

                @Override
                public <T> T visitObject(String key, T current, Function<String, T> decoder, Function<T, String> encoder) {
                    String string = this.find(key);
                    return string == null ? current : decoder.apply(string);
                }

                @Override
                public <T> T visitObject(String key, T current, IntFunction<T> decoder, ToIntFunction<T> encoder) {
                    String string = this.find(key);
                    if (string != null) {
                        try {
                            return decoder.apply(Integer.parseInt(string));
                        }
                        catch (Exception exception) {
                            LOGGER.warn("Invalid integer value for option {} = {}", new Object[]{key, string, exception});
                        }
                    }
                    return current;
                }
            });
            if (nbtCompound2.contains("fullscreenResolution")) {
                this.fullscreenResolution = nbtCompound2.getString("fullscreenResolution");
            }
            if (this.client.getWindow() != null) {
                this.client.getWindow().setFramerateLimit(this.maxFps);
            }
            KeyBinding.updateKeysByCode();
        }
        catch (Exception exception) {
            LOGGER.error("Failed to load options", (Throwable)exception);
        }
    }

    static boolean isTrue(String value) {
        return "true".equals(value);
    }

    static boolean isFalse(String value) {
        return "false".equals(value);
    }

    private NbtCompound update(NbtCompound nbt) {
        int i = 0;
        try {
            i = Integer.parseInt(nbt.getString("version"));
        }
        catch (RuntimeException runtimeException) {
            // empty catch block
        }
        return NbtHelper.update(this.client.getDataFixer(), DataFixTypes.OPTIONS, nbt, i);
    }

    public void write() {
        try (final PrintWriter printWriter = new PrintWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(this.optionsFile), StandardCharsets.UTF_8));){
            printWriter.println("version:" + SharedConstants.getGameVersion().getWorldVersion());
            this.accept(new Visitor(){

                public void print(String key) {
                    printWriter.print(key);
                    printWriter.print(':');
                }

                @Override
                public int visitInt(String key, int current) {
                    this.print(key);
                    printWriter.println(current);
                    return current;
                }

                @Override
                public boolean visitBoolean(String key, boolean current) {
                    this.print(key);
                    printWriter.println(current);
                    return current;
                }

                @Override
                public String visitString(String key, String current) {
                    this.print(key);
                    printWriter.println(current);
                    return current;
                }

                @Override
                public double visitDouble(String key, double current) {
                    this.print(key);
                    printWriter.println(current);
                    return current;
                }

                @Override
                public float visitFloat(String key, float current) {
                    this.print(key);
                    printWriter.println(current);
                    return current;
                }

                @Override
                public <T> T visitObject(String key, T current, Function<String, T> decoder, Function<T, String> encoder) {
                    this.print(key);
                    printWriter.println(encoder.apply(current));
                    return current;
                }

                @Override
                public <T> T visitObject(String key, T current, IntFunction<T> decoder, ToIntFunction<T> encoder) {
                    this.print(key);
                    printWriter.println(encoder.applyAsInt(current));
                    return current;
                }
            });
            if (this.client.getWindow().getVideoMode().isPresent()) {
                printWriter.println("fullscreenResolution:" + this.client.getWindow().getVideoMode().get().asString());
            }
        }
        catch (Exception exception) {
            LOGGER.error("Failed to save options", (Throwable)exception);
        }
        this.sendClientSettings();
    }

    public float getSoundVolume(SoundCategory category) {
        return this.soundVolumeLevels.getFloat((Object)category);
    }

    public void setSoundVolume(SoundCategory category, float volume) {
        this.soundVolumeLevels.put((Object)category, volume);
        this.client.getSoundManager().updateSoundVolume(category, volume);
    }

    public void sendClientSettings() {
        if (this.client.player != null) {
            int i = 0;
            for (PlayerModelPart playerModelPart : this.enabledPlayerModelParts) {
                i |= playerModelPart.getBitFlag();
            }
            this.client.player.networkHandler.sendPacket(new ClientSettingsC2SPacket(this.language, this.viewDistance, this.chatVisibility, this.chatColors, i, this.mainArm, this.client.shouldFilterText(), this.allowServerListing));
        }
    }

    private void setPlayerModelPart(PlayerModelPart part, boolean enabled) {
        if (enabled) {
            this.enabledPlayerModelParts.add(part);
        } else {
            this.enabledPlayerModelParts.remove((Object)part);
        }
    }

    public boolean isPlayerModelPartEnabled(PlayerModelPart part) {
        return this.enabledPlayerModelParts.contains((Object)part);
    }

    public void togglePlayerModelPart(PlayerModelPart part, boolean enabled) {
        this.setPlayerModelPart(part, enabled);
        this.sendClientSettings();
    }

    public CloudRenderMode getCloudRenderMode() {
        if (this.getViewDistance() >= 4) {
            return this.cloudRenderMode;
        }
        return CloudRenderMode.OFF;
    }

    public boolean shouldUseNativeTransport() {
        return this.useNativeTransport;
    }

    public void addResourcePackProfilesToManager(ResourcePackManager manager) {
        LinkedHashSet set = Sets.newLinkedHashSet();
        Iterator<String> iterator = this.resourcePacks.iterator();
        while (iterator.hasNext()) {
            String string = iterator.next();
            ResourcePackProfile resourcePackProfile = manager.getProfile(string);
            if (resourcePackProfile == null && !string.startsWith("file/")) {
                resourcePackProfile = manager.getProfile("file/" + string);
            }
            if (resourcePackProfile == null) {
                LOGGER.warn("Removed resource pack {} from options because it doesn't seem to exist anymore", (Object)string);
                iterator.remove();
                continue;
            }
            if (!resourcePackProfile.getCompatibility().isCompatible() && !this.incompatibleResourcePacks.contains(string)) {
                LOGGER.warn("Removed resource pack {} from options because it is no longer compatible", (Object)string);
                iterator.remove();
                continue;
            }
            if (resourcePackProfile.getCompatibility().isCompatible() && this.incompatibleResourcePacks.contains(string)) {
                LOGGER.info("Removed resource pack {} from incompatibility list because it's now compatible", (Object)string);
                this.incompatibleResourcePacks.remove(string);
                continue;
            }
            set.add(resourcePackProfile.getName());
        }
        manager.setEnabledProfiles(set);
    }

    public Perspective getPerspective() {
        return this.perspective;
    }

    public void setPerspective(Perspective perspective) {
        this.perspective = perspective;
    }

    private static List<String> parseList(String content) {
        ArrayList list = JsonHelper.deserialize(GSON, content, STRING_LIST_TYPE);
        return list != null ? list : Lists.newArrayList();
    }

    private static CloudRenderMode loadCloudRenderMode(String literal) {
        switch (literal) {
            case "true": {
                return CloudRenderMode.FANCY;
            }
            case "fast": {
                return CloudRenderMode.FAST;
            }
        }
        return CloudRenderMode.OFF;
    }

    private static String saveCloudRenderMode(CloudRenderMode mode) {
        switch (mode) {
            case FANCY: {
                return "true";
            }
            case FAST: {
                return "fast";
            }
        }
        return "false";
    }

    private static AoMode loadAo(String value) {
        if (GameOptions.isTrue(value)) {
            return AoMode.MAX;
        }
        if (GameOptions.isFalse(value)) {
            return AoMode.OFF;
        }
        return AoMode.byId(Integer.parseInt(value));
    }

    private static Arm loadArm(String arm) {
        return "left".equals(arm) ? Arm.LEFT : Arm.RIGHT;
    }

    private static String saveArm(Arm arm) {
        return arm == Arm.LEFT ? "left" : "right";
    }

    public File getOptionsFile() {
        return this.optionsFile;
    }

    public String collectProfiledOptions() {
        ImmutableList immutableList = ImmutableList.builder().add((Object)Pair.of((Object)"ao", (Object)String.valueOf((Object)this.ao))).add((Object)Pair.of((Object)"biomeBlendRadius", (Object)String.valueOf(this.biomeBlendRadius))).add((Object)Pair.of((Object)"enableVsync", (Object)String.valueOf(this.enableVsync))).add((Object)Pair.of((Object)"entityDistanceScaling", (Object)String.valueOf(this.entityDistanceScaling))).add((Object)Pair.of((Object)"entityShadows", (Object)String.valueOf(this.entityShadows))).add((Object)Pair.of((Object)"forceUnicodeFont", (Object)String.valueOf(this.forceUnicodeFont))).add((Object)Pair.of((Object)"fov", (Object)String.valueOf(this.fov))).add((Object)Pair.of((Object)"fovEffectScale", (Object)String.valueOf(this.fovEffectScale))).add((Object)Pair.of((Object)"prioritizeChunkUpdates", (Object)String.valueOf((Object)this.chunkBuilderMode))).add((Object)Pair.of((Object)"fullscreen", (Object)String.valueOf(this.fullscreen))).add((Object)Pair.of((Object)"fullscreenResolution", (Object)String.valueOf(this.fullscreenResolution))).add((Object)Pair.of((Object)"gamma", (Object)String.valueOf(this.gamma))).add((Object)Pair.of((Object)"glDebugVerbosity", (Object)String.valueOf(this.glDebugVerbosity))).add((Object)Pair.of((Object)"graphicsMode", (Object)String.valueOf((Object)this.graphicsMode))).add((Object)Pair.of((Object)"guiScale", (Object)String.valueOf(this.guiScale))).add((Object)Pair.of((Object)"maxFps", (Object)String.valueOf(this.maxFps))).add((Object)Pair.of((Object)"mipmapLevels", (Object)String.valueOf(this.mipmapLevels))).add((Object)Pair.of((Object)"narrator", (Object)String.valueOf((Object)this.narrator))).add((Object)Pair.of((Object)"overrideHeight", (Object)String.valueOf(this.overrideHeight))).add((Object)Pair.of((Object)"overrideWidth", (Object)String.valueOf(this.overrideWidth))).add((Object)Pair.of((Object)"particles", (Object)String.valueOf((Object)this.particles))).add((Object)Pair.of((Object)"reducedDebugInfo", (Object)String.valueOf(this.reducedDebugInfo))).add((Object)Pair.of((Object)"renderClouds", (Object)String.valueOf((Object)this.cloudRenderMode))).add((Object)Pair.of((Object)"renderDistance", (Object)String.valueOf(this.viewDistance))).add((Object)Pair.of((Object)"simulationDistance", (Object)String.valueOf(this.simulationDistance))).add((Object)Pair.of((Object)"resourcePacks", (Object)String.valueOf(this.resourcePacks))).add((Object)Pair.of((Object)"screenEffectScale", (Object)String.valueOf(this.distortionEffectScale))).add((Object)Pair.of((Object)"syncChunkWrites", (Object)String.valueOf(this.syncChunkWrites))).add((Object)Pair.of((Object)"useNativeTransport", (Object)String.valueOf(this.useNativeTransport))).add((Object)Pair.of((Object)"soundDevice", (Object)String.valueOf(this.soundDevice))).build();
        return immutableList.stream().map(option -> (String)option.getFirst() + ": " + (String)option.getSecond()).collect(Collectors.joining(System.lineSeparator()));
    }

    public void setServerViewDistance(int serverViewDistance) {
        this.serverViewDistance = serverViewDistance;
    }

    public int getViewDistance() {
        return this.serverViewDistance > 0 ? Math.min(this.viewDistance, this.serverViewDistance) : this.viewDistance;
    }

    @Environment(value=EnvType.CLIENT)
    static interface Visitor {
        public int visitInt(String var1, int var2);

        public boolean visitBoolean(String var1, boolean var2);

        public String visitString(String var1, String var2);

        public double visitDouble(String var1, double var2);

        public float visitFloat(String var1, float var2);

        public <T> T visitObject(String var1, T var2, Function<String, T> var3, Function<T, String> var4);

        public <T> T visitObject(String var1, T var2, IntFunction<T> var3, ToIntFunction<T> var4);
    }
}

