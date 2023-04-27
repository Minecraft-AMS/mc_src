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
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParser
 *  com.google.gson.reflect.TypeToken
 *  com.google.gson.stream.JsonReader
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
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
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.option.ChatVisibility;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.NarratorMode;
import net.minecraft.client.option.ParticlesMode;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.option.StickyKeyBinding;
import net.minecraft.client.render.ChunkBuilderMode;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.resource.VideoWarningManager;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Formatting;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class GameOptions {
    static final Logger LOGGER = LogUtils.getLogger();
    static final Gson GSON = new Gson();
    private static final TypeToken<List<String>> STRING_LIST_TYPE = new TypeToken<List<String>>(){};
    public static final int field_32149 = 2;
    public static final int field_32150 = 4;
    public static final int field_32152 = 8;
    public static final int field_32153 = 12;
    public static final int field_32154 = 16;
    public static final int field_32155 = 32;
    private static final Splitter COLON_SPLITTER = Splitter.on((char)':').limit(2);
    private static final float field_32151 = 1.0f;
    public static final String EMPTY_STRING = "";
    private static final Text DARK_MOJANG_STUDIOS_BACKGROUND_COLOR_TOOLTIP = Text.translatable("options.darkMojangStudiosBackgroundColor.tooltip");
    private final SimpleOption<Boolean> monochromeLogo = SimpleOption.ofBoolean("options.darkMojangStudiosBackgroundColor", SimpleOption.constantTooltip(DARK_MOJANG_STUDIOS_BACKGROUND_COLOR_TOOLTIP), false);
    private static final Text HIDE_LIGHTNING_FLASHES_TOOLTIP = Text.translatable("options.hideLightningFlashes.tooltip");
    private final SimpleOption<Boolean> hideLightningFlashes = SimpleOption.ofBoolean("options.hideLightningFlashes", SimpleOption.constantTooltip(HIDE_LIGHTNING_FLASHES_TOOLTIP), false);
    private final SimpleOption<Double> mouseSensitivity = new SimpleOption<Double>("options.sensitivity", SimpleOption.emptyTooltip(), (optionText, value) -> {
        if (value == 0.0) {
            return GameOptions.getGenericValueText(optionText, Text.translatable("options.sensitivity.min"));
        }
        if (value == 1.0) {
            return GameOptions.getGenericValueText(optionText, Text.translatable("options.sensitivity.max"));
        }
        return GameOptions.getPercentValueText(optionText, 2.0 * value);
    }, SimpleOption.DoubleSliderCallbacks.INSTANCE, 0.5, value -> {});
    private final SimpleOption<Integer> viewDistance;
    private final SimpleOption<Integer> simulationDistance;
    private int serverViewDistance = 0;
    private final SimpleOption<Double> entityDistanceScaling = new SimpleOption<Double>("options.entityDistanceScaling", SimpleOption.emptyTooltip(), GameOptions::getPercentValueText, new SimpleOption.ValidatingIntSliderCallbacks(2, 20).withModifier(sliderProgressValue -> (double)sliderProgressValue / 4.0, value -> (int)(value * 4.0)), Codec.doubleRange((double)0.5, (double)5.0), 1.0, value -> {});
    public static final int MAX_FRAMERATE = 260;
    private final SimpleOption<Integer> maxFps = new SimpleOption<Integer>("options.framerateLimit", SimpleOption.emptyTooltip(), (optionText, value) -> {
        if (value == 260) {
            return GameOptions.getGenericValueText(optionText, Text.translatable("options.framerateLimit.max"));
        }
        return GameOptions.getGenericValueText(optionText, Text.translatable("options.framerate", value));
    }, new SimpleOption.ValidatingIntSliderCallbacks(1, 26).withModifier(value -> value * 10, value -> value / 10), Codec.intRange((int)10, (int)260), 120, value -> MinecraftClient.getInstance().getWindow().setFramerateLimit((int)value));
    private final SimpleOption<CloudRenderMode> cloudRenderMode = new SimpleOption<CloudRenderMode>("options.renderClouds", SimpleOption.emptyTooltip(), SimpleOption.enumValueText(), new SimpleOption.PotentialValuesBasedCallbacks<CloudRenderMode>(Arrays.asList(CloudRenderMode.values()), Codec.either((Codec)Codec.BOOL, (Codec)Codec.STRING).xmap(either -> (CloudRenderMode)either.map(value -> value != false ? CloudRenderMode.FANCY : CloudRenderMode.OFF, cloudRenderMode -> switch (cloudRenderMode) {
        case "true" -> CloudRenderMode.FANCY;
        case "fast" -> CloudRenderMode.FAST;
        default -> CloudRenderMode.OFF;
    }), cloudRenderMode -> Either.right((Object)(switch (cloudRenderMode) {
        default -> throw new IncompatibleClassChangeError();
        case CloudRenderMode.FANCY -> "true";
        case CloudRenderMode.FAST -> "fast";
        case CloudRenderMode.OFF -> "false";
    })))), CloudRenderMode.FANCY, cloudRenderMode -> {
        Framebuffer framebuffer;
        if (MinecraftClient.isFabulousGraphicsOrBetter() && (framebuffer = MinecraftClient.getInstance().worldRenderer.getCloudsFramebuffer()) != null) {
            framebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
        }
    });
    private static final Text FAST_GRAPHICS_TOOLTIP = Text.translatable("options.graphics.fast.tooltip");
    private static final Text FABULOUS_GRAPHICS_TOOLTIP = Text.translatable("options.graphics.fabulous.tooltip", Text.translatable("options.graphics.fabulous").formatted(Formatting.ITALIC));
    private static final Text FANCY_GRAPHICS_TOOLTIP = Text.translatable("options.graphics.fancy.tooltip");
    private final SimpleOption<GraphicsMode> graphicsMode = new SimpleOption<GraphicsMode>("options.graphics", value -> switch (value) {
        default -> throw new IncompatibleClassChangeError();
        case GraphicsMode.FANCY -> Tooltip.of(FANCY_GRAPHICS_TOOLTIP);
        case GraphicsMode.FAST -> Tooltip.of(FAST_GRAPHICS_TOOLTIP);
        case GraphicsMode.FABULOUS -> Tooltip.of(FABULOUS_GRAPHICS_TOOLTIP);
    }, (optionText, value) -> {
        MutableText mutableText = Text.translatable(value.getTranslationKey());
        if (value == GraphicsMode.FABULOUS) {
            return mutableText.formatted(Formatting.ITALIC);
        }
        return mutableText;
    }, new SimpleOption.AlternateValuesSupportingCyclingCallbacks<GraphicsMode>(Arrays.asList(GraphicsMode.values()), Stream.of(GraphicsMode.values()).filter(graphicsMode -> graphicsMode != GraphicsMode.FABULOUS).collect(Collectors.toList()), () -> MinecraftClient.getInstance().isRunning() && MinecraftClient.getInstance().getVideoWarningManager().hasCancelledAfterWarning(), (option, graphicsMode) -> {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        VideoWarningManager videoWarningManager = minecraftClient.getVideoWarningManager();
        if (graphicsMode == GraphicsMode.FABULOUS && videoWarningManager.canWarn()) {
            videoWarningManager.scheduleWarning();
            return;
        }
        option.setValue(graphicsMode);
        minecraftClient.worldRenderer.reload();
    }, Codec.INT.xmap(GraphicsMode::byId, GraphicsMode::getId)), GraphicsMode.FANCY, value -> {});
    private final SimpleOption<Boolean> ao = SimpleOption.ofBoolean("options.ao", true, value -> MinecraftClient.getInstance().worldRenderer.reload());
    private static final Text NONE_CHUNK_BUILDER_MODE_TOOLTIP = Text.translatable("options.prioritizeChunkUpdates.none.tooltip");
    private static final Text BY_PLAYER_CHUNK_BUILDER_MODE_TOOLTIP = Text.translatable("options.prioritizeChunkUpdates.byPlayer.tooltip");
    private static final Text NEARBY_CHUNK_BUILDER_MODE_TOOLTIP = Text.translatable("options.prioritizeChunkUpdates.nearby.tooltip");
    private final SimpleOption<ChunkBuilderMode> chunkBuilderMode = new SimpleOption<ChunkBuilderMode>("options.prioritizeChunkUpdates", value -> switch (value) {
        default -> throw new IncompatibleClassChangeError();
        case ChunkBuilderMode.NONE -> Tooltip.of(NONE_CHUNK_BUILDER_MODE_TOOLTIP);
        case ChunkBuilderMode.PLAYER_AFFECTED -> Tooltip.of(BY_PLAYER_CHUNK_BUILDER_MODE_TOOLTIP);
        case ChunkBuilderMode.NEARBY -> Tooltip.of(NEARBY_CHUNK_BUILDER_MODE_TOOLTIP);
    }, SimpleOption.enumValueText(), new SimpleOption.PotentialValuesBasedCallbacks<ChunkBuilderMode>(Arrays.asList(ChunkBuilderMode.values()), Codec.INT.xmap(ChunkBuilderMode::get, ChunkBuilderMode::getId)), ChunkBuilderMode.NONE, value -> {});
    public List<String> resourcePacks = Lists.newArrayList();
    public List<String> incompatibleResourcePacks = Lists.newArrayList();
    private final SimpleOption<ChatVisibility> chatVisibility = new SimpleOption<ChatVisibility>("options.chat.visibility", SimpleOption.emptyTooltip(), SimpleOption.enumValueText(), new SimpleOption.PotentialValuesBasedCallbacks<ChatVisibility>(Arrays.asList(ChatVisibility.values()), Codec.INT.xmap(ChatVisibility::byId, ChatVisibility::getId)), ChatVisibility.FULL, value -> {});
    private final SimpleOption<Double> chatOpacity = new SimpleOption<Double>("options.chat.opacity", SimpleOption.emptyTooltip(), (optionText, value) -> GameOptions.getPercentValueText(optionText, value * 0.9 + 0.1), SimpleOption.DoubleSliderCallbacks.INSTANCE, 1.0, value -> MinecraftClient.getInstance().inGameHud.getChatHud().reset());
    private final SimpleOption<Double> chatLineSpacing = new SimpleOption<Double>("options.chat.line_spacing", SimpleOption.emptyTooltip(), GameOptions::getPercentValueText, SimpleOption.DoubleSliderCallbacks.INSTANCE, 0.0, value -> {});
    private final SimpleOption<Double> textBackgroundOpacity = new SimpleOption<Double>("options.accessibility.text_background_opacity", SimpleOption.emptyTooltip(), GameOptions::getPercentValueText, SimpleOption.DoubleSliderCallbacks.INSTANCE, 0.5, value -> MinecraftClient.getInstance().inGameHud.getChatHud().reset());
    private final SimpleOption<Double> panoramaSpeed = new SimpleOption<Double>("options.accessibility.panorama_speed", SimpleOption.emptyTooltip(), GameOptions::getPercentValueText, SimpleOption.DoubleSliderCallbacks.INSTANCE, 1.0, value -> {});
    private static final Text HIGH_CONTRAST_TOOLTIP = Text.translatable("options.accessibility.high_contrast.tooltip");
    private final SimpleOption<Boolean> highContrast = SimpleOption.ofBoolean("options.accessibility.high_contrast", SimpleOption.constantTooltip(HIGH_CONTRAST_TOOLTIP), false, value -> {
        ResourcePackManager resourcePackManager = MinecraftClient.getInstance().getResourcePackManager();
        boolean bl = resourcePackManager.getEnabledNames().contains("high_contrast");
        if (!bl && value.booleanValue()) {
            if (resourcePackManager.enable("high_contrast")) {
                this.refreshResourcePacks(resourcePackManager);
            }
        } else if (bl && !value.booleanValue() && resourcePackManager.disable("high_contrast")) {
            this.refreshResourcePacks(resourcePackManager);
        }
    });
    @Nullable
    public String fullscreenResolution;
    public boolean hideServerAddress;
    public boolean advancedItemTooltips;
    public boolean pauseOnLostFocus = true;
    private final Set<PlayerModelPart> enabledPlayerModelParts = EnumSet.allOf(PlayerModelPart.class);
    private final SimpleOption<Arm> mainArm = new SimpleOption<Arm>("options.mainHand", SimpleOption.emptyTooltip(), SimpleOption.enumValueText(), new SimpleOption.PotentialValuesBasedCallbacks<Arm>(Arrays.asList(Arm.values()), Codec.STRING.xmap(value -> "left".equals(value) ? Arm.LEFT : Arm.RIGHT, value -> value == Arm.LEFT ? "left" : "right")), Arm.RIGHT, value -> this.sendClientSettings());
    public int overrideWidth;
    public int overrideHeight;
    private final SimpleOption<Double> chatScale = new SimpleOption<Double>("options.chat.scale", SimpleOption.emptyTooltip(), (optionText, value) -> {
        if (value == 0.0) {
            return ScreenTexts.composeToggleText(optionText, false);
        }
        return GameOptions.getPercentValueText(optionText, value);
    }, SimpleOption.DoubleSliderCallbacks.INSTANCE, 1.0, value -> MinecraftClient.getInstance().inGameHud.getChatHud().reset());
    private final SimpleOption<Double> chatWidth = new SimpleOption<Double>("options.chat.width", SimpleOption.emptyTooltip(), (optionText, value) -> GameOptions.getPixelValueText(optionText, ChatHud.getWidth(value)), SimpleOption.DoubleSliderCallbacks.INSTANCE, 1.0, value -> MinecraftClient.getInstance().inGameHud.getChatHud().reset());
    private final SimpleOption<Double> chatHeightUnfocused = new SimpleOption<Double>("options.chat.height.unfocused", SimpleOption.emptyTooltip(), (optionText, value) -> GameOptions.getPixelValueText(optionText, ChatHud.getHeight(value)), SimpleOption.DoubleSliderCallbacks.INSTANCE, ChatHud.getDefaultUnfocusedHeight(), value -> MinecraftClient.getInstance().inGameHud.getChatHud().reset());
    private final SimpleOption<Double> chatHeightFocused = new SimpleOption<Double>("options.chat.height.focused", SimpleOption.emptyTooltip(), (optionText, value) -> GameOptions.getPixelValueText(optionText, ChatHud.getHeight(value)), SimpleOption.DoubleSliderCallbacks.INSTANCE, 1.0, value -> MinecraftClient.getInstance().inGameHud.getChatHud().reset());
    private final SimpleOption<Double> chatDelay = new SimpleOption<Double>("options.chat.delay_instant", SimpleOption.emptyTooltip(), (optionText, value) -> {
        if (value <= 0.0) {
            return Text.translatable("options.chat.delay_none");
        }
        return Text.translatable("options.chat.delay", String.format(Locale.ROOT, "%.1f", value));
    }, new SimpleOption.ValidatingIntSliderCallbacks(0, 60).withModifier(value -> (double)value / 10.0, value -> (int)(value * 10.0)), Codec.doubleRange((double)0.0, (double)6.0), 0.0, value -> MinecraftClient.getInstance().getMessageHandler().setChatDelay((double)value));
    private static final Text NOTIFICATION_DISPLAY_TIME_TOOLTIP = Text.translatable("options.notifications.display_time.tooltip");
    private final SimpleOption<Double> notificationDisplayTime = new SimpleOption<Double>("options.notifications.display_time", SimpleOption.constantTooltip(NOTIFICATION_DISPLAY_TIME_TOOLTIP), (optionText, value) -> GameOptions.getGenericValueText(optionText, Text.translatable("options.multiplier", value)), new SimpleOption.ValidatingIntSliderCallbacks(5, 100).withModifier(sliderProgressValue -> (double)sliderProgressValue / 10.0, value -> (int)(value * 10.0)), Codec.doubleRange((double)0.5, (double)10.0), 1.0, value -> {});
    private final SimpleOption<Integer> mipmapLevels = new SimpleOption<Integer>("options.mipmapLevels", SimpleOption.emptyTooltip(), (optionText, value) -> {
        if (value == 0) {
            return ScreenTexts.composeToggleText(optionText, false);
        }
        return GameOptions.getGenericValueText(optionText, value);
    }, new SimpleOption.ValidatingIntSliderCallbacks(0, 4), 4, value -> {});
    public boolean useNativeTransport = true;
    private final SimpleOption<AttackIndicator> attackIndicator = new SimpleOption<AttackIndicator>("options.attackIndicator", SimpleOption.emptyTooltip(), SimpleOption.enumValueText(), new SimpleOption.PotentialValuesBasedCallbacks<AttackIndicator>(Arrays.asList(AttackIndicator.values()), Codec.INT.xmap(AttackIndicator::byId, AttackIndicator::getId)), AttackIndicator.CROSSHAIR, value -> {});
    public TutorialStep tutorialStep = TutorialStep.MOVEMENT;
    public boolean joinedFirstServer = false;
    public boolean hideBundleTutorial = false;
    private final SimpleOption<Integer> biomeBlendRadius = new SimpleOption<Integer>("options.biomeBlendRadius", SimpleOption.emptyTooltip(), (optionText, value) -> {
        int i = value * 2 + 1;
        return GameOptions.getGenericValueText(optionText, Text.translatable("options.biomeBlendRadius." + i));
    }, new SimpleOption.ValidatingIntSliderCallbacks(0, 7), 2, value -> MinecraftClient.getInstance().worldRenderer.reload());
    private final SimpleOption<Double> mouseWheelSensitivity = new SimpleOption<Double>("options.mouseWheelSensitivity", SimpleOption.emptyTooltip(), (optionText, value) -> GameOptions.getGenericValueText(optionText, Text.literal(String.format(Locale.ROOT, "%.2f", value))), new SimpleOption.ValidatingIntSliderCallbacks(-200, 100).withModifier(GameOptions::toMouseWheelSensitivityValue, GameOptions::toMouseWheelSensitivitySliderProgressValue), Codec.doubleRange((double)GameOptions.toMouseWheelSensitivityValue(-200), (double)GameOptions.toMouseWheelSensitivityValue(100)), GameOptions.toMouseWheelSensitivityValue(0), value -> {});
    private final SimpleOption<Boolean> rawMouseInput = SimpleOption.ofBoolean("options.rawMouseInput", true, value -> {
        Window window = MinecraftClient.getInstance().getWindow();
        if (window != null) {
            window.setRawMouseMotion((boolean)value);
        }
    });
    public int glDebugVerbosity = 1;
    private final SimpleOption<Boolean> autoJump = SimpleOption.ofBoolean("options.autoJump", false);
    private final SimpleOption<Boolean> operatorItemsTab = SimpleOption.ofBoolean("options.operatorItemsTab", false);
    private final SimpleOption<Boolean> autoSuggestions = SimpleOption.ofBoolean("options.autoSuggestCommands", true);
    private final SimpleOption<Boolean> chatColors = SimpleOption.ofBoolean("options.chat.color", true);
    private final SimpleOption<Boolean> chatLinks = SimpleOption.ofBoolean("options.chat.links", true);
    private final SimpleOption<Boolean> chatLinksPrompt = SimpleOption.ofBoolean("options.chat.links.prompt", true);
    private final SimpleOption<Boolean> enableVsync = SimpleOption.ofBoolean("options.vsync", true, value -> {
        if (MinecraftClient.getInstance().getWindow() != null) {
            MinecraftClient.getInstance().getWindow().setVsync((boolean)value);
        }
    });
    private final SimpleOption<Boolean> entityShadows = SimpleOption.ofBoolean("options.entityShadows", true);
    private final SimpleOption<Boolean> forceUnicodeFont = SimpleOption.ofBoolean("options.forceUnicodeFont", false, value -> {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient.getWindow() != null) {
            minecraftClient.initFont((boolean)value);
            minecraftClient.onResolutionChanged();
        }
    });
    private final SimpleOption<Boolean> invertYMouse = SimpleOption.ofBoolean("options.invertMouse", false);
    private final SimpleOption<Boolean> discreteMouseScroll = SimpleOption.ofBoolean("options.discrete_mouse_scroll", false);
    private final SimpleOption<Boolean> realmsNotifications = SimpleOption.ofBoolean("options.realmsNotifications", true);
    private static final Text ALLOW_SERVER_LISTING_TOOLTIP = Text.translatable("options.allowServerListing.tooltip");
    private final SimpleOption<Boolean> allowServerListing = SimpleOption.ofBoolean("options.allowServerListing", SimpleOption.constantTooltip(ALLOW_SERVER_LISTING_TOOLTIP), true, value -> this.sendClientSettings());
    private final SimpleOption<Boolean> reducedDebugInfo = SimpleOption.ofBoolean("options.reducedDebugInfo", false);
    private final Map<SoundCategory, SimpleOption<Double>> soundVolumeLevels = Util.make(new EnumMap(SoundCategory.class), soundVolumeLevels -> {
        for (SoundCategory soundCategory : SoundCategory.values()) {
            soundVolumeLevels.put(soundCategory, this.createSoundVolumeOption("soundCategory." + soundCategory.getName(), soundCategory));
        }
    });
    private final SimpleOption<Boolean> showSubtitles = SimpleOption.ofBoolean("options.showSubtitles", false);
    private static final Text DIRECTIONAL_AUDIO_ON_TOOLTIP = Text.translatable("options.directionalAudio.on.tooltip");
    private static final Text DIRECTIONAL_AUDIO_OFF_TOOLTIP = Text.translatable("options.directionalAudio.off.tooltip");
    private final SimpleOption<Boolean> directionalAudio = SimpleOption.ofBoolean("options.directionalAudio", value -> value != false ? Tooltip.of(DIRECTIONAL_AUDIO_ON_TOOLTIP) : Tooltip.of(DIRECTIONAL_AUDIO_OFF_TOOLTIP), false, value -> {
        SoundManager soundManager = MinecraftClient.getInstance().getSoundManager();
        soundManager.reloadSounds();
        soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    });
    private final SimpleOption<Boolean> backgroundForChatOnly = new SimpleOption<Boolean>("options.accessibility.text_background", SimpleOption.emptyTooltip(), (optionText, value) -> value != false ? Text.translatable("options.accessibility.text_background.chat") : Text.translatable("options.accessibility.text_background.everywhere"), SimpleOption.BOOLEAN, true, value -> {});
    private final SimpleOption<Boolean> touchscreen = SimpleOption.ofBoolean("options.touchscreen", false);
    private final SimpleOption<Boolean> fullscreen = SimpleOption.ofBoolean("options.fullscreen", false, value -> {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient.getWindow() != null && minecraftClient.getWindow().isFullscreen() != value.booleanValue()) {
            minecraftClient.getWindow().toggleFullscreen();
            this.getFullscreen().setValue(minecraftClient.getWindow().isFullscreen());
        }
    });
    private final SimpleOption<Boolean> bobView = SimpleOption.ofBoolean("options.viewBobbing", true);
    private static final Text TOGGLE_KEY_TEXT = Text.translatable("options.key.toggle");
    private static final Text HOLD_KEY_TEXT = Text.translatable("options.key.hold");
    private final SimpleOption<Boolean> sneakToggled = new SimpleOption<Boolean>("key.sneak", SimpleOption.emptyTooltip(), (optionText, value) -> value != false ? TOGGLE_KEY_TEXT : HOLD_KEY_TEXT, SimpleOption.BOOLEAN, false, value -> {});
    private final SimpleOption<Boolean> sprintToggled = new SimpleOption<Boolean>("key.sprint", SimpleOption.emptyTooltip(), (optionText, value) -> value != false ? TOGGLE_KEY_TEXT : HOLD_KEY_TEXT, SimpleOption.BOOLEAN, false, value -> {});
    public boolean skipMultiplayerWarning;
    public boolean skipRealms32BitWarning;
    private static final Text HIDE_MATCHED_NAMES_TOOLTIP = Text.translatable("options.hideMatchedNames.tooltip");
    private final SimpleOption<Boolean> hideMatchedNames = SimpleOption.ofBoolean("options.hideMatchedNames", SimpleOption.constantTooltip(HIDE_MATCHED_NAMES_TOOLTIP), true);
    private final SimpleOption<Boolean> showAutosaveIndicator = SimpleOption.ofBoolean("options.autosaveIndicator", true);
    private static final Text ONLY_SHOW_SECURE_CHAT_TOOLTIP = Text.translatable("options.onlyShowSecureChat.tooltip");
    private final SimpleOption<Boolean> onlyShowSecureChat = SimpleOption.ofBoolean("options.onlyShowSecureChat", SimpleOption.constantTooltip(ONLY_SHOW_SECURE_CHAT_TOOLTIP), false);
    public final KeyBinding forwardKey = new KeyBinding("key.forward", 87, "key.categories.movement");
    public final KeyBinding leftKey = new KeyBinding("key.left", 65, "key.categories.movement");
    public final KeyBinding backKey = new KeyBinding("key.back", 83, "key.categories.movement");
    public final KeyBinding rightKey = new KeyBinding("key.right", 68, "key.categories.movement");
    public final KeyBinding jumpKey = new KeyBinding("key.jump", 32, "key.categories.movement");
    public final KeyBinding sneakKey = new StickyKeyBinding("key.sneak", 340, "key.categories.movement", this.sneakToggled::getValue);
    public final KeyBinding sprintKey = new StickyKeyBinding("key.sprint", 341, "key.categories.movement", this.sprintToggled::getValue);
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
    public boolean hudHidden;
    private Perspective perspective = Perspective.FIRST_PERSON;
    public boolean debugEnabled;
    public boolean debugProfilerEnabled;
    public boolean debugTpsEnabled;
    public String lastServer = "";
    public boolean smoothCameraEnabled;
    private final SimpleOption<Integer> fov = new SimpleOption<Integer>("options.fov", SimpleOption.emptyTooltip(), (optionText, value) -> switch (value) {
        case 70 -> GameOptions.getGenericValueText(optionText, Text.translatable("options.fov.min"));
        case 110 -> GameOptions.getGenericValueText(optionText, Text.translatable("options.fov.max"));
        default -> GameOptions.getGenericValueText(optionText, value);
    }, new SimpleOption.ValidatingIntSliderCallbacks(30, 110), Codec.DOUBLE.xmap(value -> (int)(value * 40.0 + 70.0), value -> ((double)value.intValue() - 70.0) / 40.0), 70, value -> MinecraftClient.getInstance().worldRenderer.scheduleTerrainUpdate());
    private static final Text TELEMETRY_TOOLTIP = Text.translatable("options.telemetry.button.tooltip", Text.translatable("options.telemetry.state.minimal"), Text.translatable("options.telemetry.state.all"));
    private final SimpleOption<Boolean> telemetryOptInExtra = SimpleOption.ofBoolean("options.telemetry.button", SimpleOption.constantTooltip(TELEMETRY_TOOLTIP), (optionText, value) -> {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (!minecraftClient.isTelemetryEnabledByApi()) {
            return Text.translatable("options.telemetry.state.none");
        }
        if (value.booleanValue() && minecraftClient.isOptionalTelemetryEnabledByApi()) {
            return Text.translatable("options.telemetry.state.all");
        }
        return Text.translatable("options.telemetry.state.minimal");
    }, false, value -> {});
    private static final Text SCREEN_EFFECT_SCALE_TOOLTIP = Text.translatable("options.screenEffectScale.tooltip");
    private final SimpleOption<Double> distortionEffectScale = new SimpleOption<Double>("options.screenEffectScale", SimpleOption.constantTooltip(SCREEN_EFFECT_SCALE_TOOLTIP), (optionText, value) -> {
        if (value == 0.0) {
            return GameOptions.getGenericValueText(optionText, ScreenTexts.OFF);
        }
        return GameOptions.getPercentValueText(optionText, value);
    }, SimpleOption.DoubleSliderCallbacks.INSTANCE, 1.0, value -> {});
    private static final Text FOV_EFFECT_SCALE_TOOLTIP = Text.translatable("options.fovEffectScale.tooltip");
    private final SimpleOption<Double> fovEffectScale = new SimpleOption<Double>("options.fovEffectScale", SimpleOption.constantTooltip(FOV_EFFECT_SCALE_TOOLTIP), (optionText, value) -> {
        if (value == 0.0) {
            return GameOptions.getGenericValueText(optionText, ScreenTexts.OFF);
        }
        return GameOptions.getPercentValueText(optionText, value);
    }, SimpleOption.DoubleSliderCallbacks.INSTANCE.withModifier(MathHelper::square, Math::sqrt), Codec.doubleRange((double)0.0, (double)1.0), 1.0, value -> {});
    private static final Text DARKNESS_EFFECT_SCALE_TOOLTIP = Text.translatable("options.darknessEffectScale.tooltip");
    private final SimpleOption<Double> darknessEffectScale = new SimpleOption<Double>("options.darknessEffectScale", SimpleOption.constantTooltip(DARKNESS_EFFECT_SCALE_TOOLTIP), (optionText, value) -> {
        if (value == 0.0) {
            return GameOptions.getGenericValueText(optionText, ScreenTexts.OFF);
        }
        return GameOptions.getPercentValueText(optionText, value);
    }, SimpleOption.DoubleSliderCallbacks.INSTANCE.withModifier(MathHelper::square, Math::sqrt), 1.0, value -> {});
    private static final Text GLINT_SPEED_TOOLTIP = Text.translatable("options.glintSpeed.tooltip");
    private final SimpleOption<Double> glintSpeed = new SimpleOption<Double>("options.glintSpeed", SimpleOption.constantTooltip(GLINT_SPEED_TOOLTIP), (optionText, value) -> {
        if (value == 0.0) {
            return GameOptions.getGenericValueText(optionText, ScreenTexts.OFF);
        }
        return GameOptions.getPercentValueText(optionText, value);
    }, SimpleOption.DoubleSliderCallbacks.INSTANCE, 0.5, value -> {});
    private static final Text GLINT_STRENGTH_TOOLTIP = Text.translatable("options.glintStrength.tooltip");
    private final SimpleOption<Double> glintStrength = new SimpleOption<Double>("options.glintStrength", SimpleOption.constantTooltip(GLINT_STRENGTH_TOOLTIP), (optionText, value) -> {
        if (value == 0.0) {
            return GameOptions.getGenericValueText(optionText, ScreenTexts.OFF);
        }
        return GameOptions.getPercentValueText(optionText, value);
    }, SimpleOption.DoubleSliderCallbacks.INSTANCE, 0.75, RenderSystem::setShaderGlintAlpha);
    private static final Text DAMAGE_TILT_STRENGTH_TOOLTIP = Text.translatable("options.damageTiltStrength.tooltip");
    private final SimpleOption<Double> damageTiltStrength = new SimpleOption<Double>("options.damageTiltStrength", SimpleOption.constantTooltip(DAMAGE_TILT_STRENGTH_TOOLTIP), (optionText, value) -> {
        if (value == 0.0) {
            return GameOptions.getGenericValueText(optionText, ScreenTexts.OFF);
        }
        return GameOptions.getPercentValueText(optionText, value);
    }, SimpleOption.DoubleSliderCallbacks.INSTANCE, 1.0, value -> {});
    private final SimpleOption<Double> gamma = new SimpleOption<Double>("options.gamma", SimpleOption.emptyTooltip(), (optionText, value) -> {
        int i = (int)(value * 100.0);
        if (i == 0) {
            return GameOptions.getGenericValueText(optionText, Text.translatable("options.gamma.min"));
        }
        if (i == 50) {
            return GameOptions.getGenericValueText(optionText, Text.translatable("options.gamma.default"));
        }
        if (i == 100) {
            return GameOptions.getGenericValueText(optionText, Text.translatable("options.gamma.max"));
        }
        return GameOptions.getGenericValueText(optionText, i);
    }, SimpleOption.DoubleSliderCallbacks.INSTANCE, 0.5, value -> {});
    public static final int field_43405 = 0;
    private static final int MAX_SERIALIZABLE_GUI_SCALE = 0x7FFFFFFE;
    private final SimpleOption<Integer> guiScale = new SimpleOption<Integer>("options.guiScale", SimpleOption.emptyTooltip(), (optionText, value) -> value == 0 ? Text.translatable("options.guiScale.auto") : Text.literal(Integer.toString(value)), new SimpleOption.MaxSuppliableIntCallbacks(0, () -> {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (!minecraftClient.isRunning()) {
            return 0x7FFFFFFE;
        }
        return minecraftClient.getWindow().calculateScaleFactor(0, minecraftClient.forcesUnicodeFont());
    }, 0x7FFFFFFE), 0, value -> {});
    private final SimpleOption<ParticlesMode> particles = new SimpleOption<ParticlesMode>("options.particles", SimpleOption.emptyTooltip(), SimpleOption.enumValueText(), new SimpleOption.PotentialValuesBasedCallbacks<ParticlesMode>(Arrays.asList(ParticlesMode.values()), Codec.INT.xmap(ParticlesMode::byId, ParticlesMode::getId)), ParticlesMode.ALL, value -> {});
    private final SimpleOption<NarratorMode> narrator = new SimpleOption<NarratorMode>("options.narrator", SimpleOption.emptyTooltip(), (optionText, value) -> {
        if (this.client.getNarratorManager().isActive()) {
            return value.getName();
        }
        return Text.translatable("options.narrator.notavailable");
    }, new SimpleOption.PotentialValuesBasedCallbacks<NarratorMode>(Arrays.asList(NarratorMode.values()), Codec.INT.xmap(NarratorMode::byId, NarratorMode::getId)), NarratorMode.OFF, value -> this.client.getNarratorManager().onModeChange((NarratorMode)((Object)value)));
    public String language = "en_us";
    private final SimpleOption<String> soundDevice = new SimpleOption<String>("options.audioDevice", SimpleOption.emptyTooltip(), (optionText, value) -> {
        if (EMPTY_STRING.equals(value)) {
            return Text.translatable("options.audioDevice.default");
        }
        if (value.startsWith("OpenAL Soft on ")) {
            return Text.literal(value.substring(SoundSystem.OPENAL_SOFT_ON_LENGTH));
        }
        return Text.literal(value);
    }, new SimpleOption.LazyCyclingCallbacks<String>(() -> Stream.concat(Stream.of(EMPTY_STRING), MinecraftClient.getInstance().getSoundManager().getSoundDevices().stream()).toList(), (Function<String, Optional<String>>)((Function<String, Optional>)value -> {
        if (!MinecraftClient.getInstance().isRunning() || value == EMPTY_STRING || MinecraftClient.getInstance().getSoundManager().getSoundDevices().contains(value)) {
            return Optional.of(value);
        }
        return Optional.empty();
    }), (Codec<String>)Codec.STRING), "", value -> {
        SoundManager soundManager = MinecraftClient.getInstance().getSoundManager();
        soundManager.reloadSounds();
        soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    });
    public boolean onboardAccessibility = true;
    public boolean syncChunkWrites;

    public SimpleOption<Boolean> getMonochromeLogo() {
        return this.monochromeLogo;
    }

    public SimpleOption<Boolean> getHideLightningFlashes() {
        return this.hideLightningFlashes;
    }

    public SimpleOption<Double> getMouseSensitivity() {
        return this.mouseSensitivity;
    }

    public SimpleOption<Integer> getViewDistance() {
        return this.viewDistance;
    }

    public SimpleOption<Integer> getSimulationDistance() {
        return this.simulationDistance;
    }

    public SimpleOption<Double> getEntityDistanceScaling() {
        return this.entityDistanceScaling;
    }

    public SimpleOption<Integer> getMaxFps() {
        return this.maxFps;
    }

    public SimpleOption<CloudRenderMode> getCloudRenderMode() {
        return this.cloudRenderMode;
    }

    public SimpleOption<GraphicsMode> getGraphicsMode() {
        return this.graphicsMode;
    }

    public SimpleOption<Boolean> getAo() {
        return this.ao;
    }

    public SimpleOption<ChunkBuilderMode> getChunkBuilderMode() {
        return this.chunkBuilderMode;
    }

    public void refreshResourcePacks(ResourcePackManager resourcePackManager) {
        ImmutableList list = ImmutableList.copyOf(this.resourcePacks);
        this.resourcePacks.clear();
        this.incompatibleResourcePacks.clear();
        for (ResourcePackProfile resourcePackProfile : resourcePackManager.getEnabledProfiles()) {
            if (resourcePackProfile.isPinned()) continue;
            this.resourcePacks.add(resourcePackProfile.getName());
            if (resourcePackProfile.getCompatibility().isCompatible()) continue;
            this.incompatibleResourcePacks.add(resourcePackProfile.getName());
        }
        this.write();
        ImmutableList list2 = ImmutableList.copyOf(this.resourcePacks);
        if (!list2.equals(list)) {
            this.client.reloadResources();
        }
    }

    public SimpleOption<ChatVisibility> getChatVisibility() {
        return this.chatVisibility;
    }

    public SimpleOption<Double> getChatOpacity() {
        return this.chatOpacity;
    }

    public SimpleOption<Double> getChatLineSpacing() {
        return this.chatLineSpacing;
    }

    public SimpleOption<Double> getTextBackgroundOpacity() {
        return this.textBackgroundOpacity;
    }

    public SimpleOption<Double> getPanoramaSpeed() {
        return this.panoramaSpeed;
    }

    public SimpleOption<Boolean> getHighContrast() {
        return this.highContrast;
    }

    public SimpleOption<Arm> getMainArm() {
        return this.mainArm;
    }

    public SimpleOption<Double> getChatScale() {
        return this.chatScale;
    }

    public SimpleOption<Double> getChatWidth() {
        return this.chatWidth;
    }

    public SimpleOption<Double> getChatHeightUnfocused() {
        return this.chatHeightUnfocused;
    }

    public SimpleOption<Double> getChatHeightFocused() {
        return this.chatHeightFocused;
    }

    public SimpleOption<Double> getChatDelay() {
        return this.chatDelay;
    }

    public SimpleOption<Double> getNotificationDisplayTime() {
        return this.notificationDisplayTime;
    }

    public SimpleOption<Integer> getMipmapLevels() {
        return this.mipmapLevels;
    }

    public SimpleOption<AttackIndicator> getAttackIndicator() {
        return this.attackIndicator;
    }

    public SimpleOption<Integer> getBiomeBlendRadius() {
        return this.biomeBlendRadius;
    }

    private static double toMouseWheelSensitivityValue(int value) {
        return Math.pow(10.0, (double)value / 100.0);
    }

    private static int toMouseWheelSensitivitySliderProgressValue(double value) {
        return MathHelper.floor(Math.log10(value) * 100.0);
    }

    public SimpleOption<Double> getMouseWheelSensitivity() {
        return this.mouseWheelSensitivity;
    }

    public SimpleOption<Boolean> getRawMouseInput() {
        return this.rawMouseInput;
    }

    public SimpleOption<Boolean> getAutoJump() {
        return this.autoJump;
    }

    public SimpleOption<Boolean> getOperatorItemsTab() {
        return this.operatorItemsTab;
    }

    public SimpleOption<Boolean> getAutoSuggestions() {
        return this.autoSuggestions;
    }

    public SimpleOption<Boolean> getChatColors() {
        return this.chatColors;
    }

    public SimpleOption<Boolean> getChatLinks() {
        return this.chatLinks;
    }

    public SimpleOption<Boolean> getChatLinksPrompt() {
        return this.chatLinksPrompt;
    }

    public SimpleOption<Boolean> getEnableVsync() {
        return this.enableVsync;
    }

    public SimpleOption<Boolean> getEntityShadows() {
        return this.entityShadows;
    }

    public SimpleOption<Boolean> getForceUnicodeFont() {
        return this.forceUnicodeFont;
    }

    public SimpleOption<Boolean> getInvertYMouse() {
        return this.invertYMouse;
    }

    public SimpleOption<Boolean> getDiscreteMouseScroll() {
        return this.discreteMouseScroll;
    }

    public SimpleOption<Boolean> getRealmsNotifications() {
        return this.realmsNotifications;
    }

    public SimpleOption<Boolean> getAllowServerListing() {
        return this.allowServerListing;
    }

    public SimpleOption<Boolean> getReducedDebugInfo() {
        return this.reducedDebugInfo;
    }

    public final float getSoundVolume(SoundCategory category) {
        return this.getSoundVolumeOption(category).getValue().floatValue();
    }

    public final SimpleOption<Double> getSoundVolumeOption(SoundCategory category) {
        return Objects.requireNonNull(this.soundVolumeLevels.get((Object)category));
    }

    private SimpleOption<Double> createSoundVolumeOption(String key, SoundCategory category) {
        return new SimpleOption<Double>(key, SimpleOption.emptyTooltip(), (prefix, value) -> {
            if (value == 0.0) {
                return GameOptions.getGenericValueText(prefix, ScreenTexts.OFF);
            }
            return GameOptions.getPercentValueText(prefix, value);
        }, SimpleOption.DoubleSliderCallbacks.INSTANCE, 1.0, value -> MinecraftClient.getInstance().getSoundManager().updateSoundVolume(category, value.floatValue()));
    }

    public SimpleOption<Boolean> getShowSubtitles() {
        return this.showSubtitles;
    }

    public SimpleOption<Boolean> getDirectionalAudio() {
        return this.directionalAudio;
    }

    public SimpleOption<Boolean> getBackgroundForChatOnly() {
        return this.backgroundForChatOnly;
    }

    public SimpleOption<Boolean> getTouchscreen() {
        return this.touchscreen;
    }

    public SimpleOption<Boolean> getFullscreen() {
        return this.fullscreen;
    }

    public SimpleOption<Boolean> getBobView() {
        return this.bobView;
    }

    public SimpleOption<Boolean> getSneakToggled() {
        return this.sneakToggled;
    }

    public SimpleOption<Boolean> getSprintToggled() {
        return this.sprintToggled;
    }

    public SimpleOption<Boolean> getHideMatchedNames() {
        return this.hideMatchedNames;
    }

    public SimpleOption<Boolean> getShowAutosaveIndicator() {
        return this.showAutosaveIndicator;
    }

    public SimpleOption<Boolean> getOnlyShowSecureChat() {
        return this.onlyShowSecureChat;
    }

    public SimpleOption<Integer> getFov() {
        return this.fov;
    }

    public SimpleOption<Boolean> getTelemetryOptInExtra() {
        return this.telemetryOptInExtra;
    }

    public SimpleOption<Double> getDistortionEffectScale() {
        return this.distortionEffectScale;
    }

    public SimpleOption<Double> getFovEffectScale() {
        return this.fovEffectScale;
    }

    public SimpleOption<Double> getDarknessEffectScale() {
        return this.darknessEffectScale;
    }

    public SimpleOption<Double> getGlintSpeed() {
        return this.glintSpeed;
    }

    public SimpleOption<Double> getGlintStrength() {
        return this.glintStrength;
    }

    public SimpleOption<Double> getDamageTiltStrength() {
        return this.damageTiltStrength;
    }

    public SimpleOption<Double> getGamma() {
        return this.gamma;
    }

    public SimpleOption<Integer> getGuiScale() {
        return this.guiScale;
    }

    public SimpleOption<ParticlesMode> getParticles() {
        return this.particles;
    }

    public SimpleOption<NarratorMode> getNarrator() {
        return this.narrator;
    }

    public SimpleOption<String> getSoundDevice() {
        return this.soundDevice;
    }

    public GameOptions(MinecraftClient client, File optionsFile) {
        this.client = client;
        this.optionsFile = new File(optionsFile, "options.txt");
        boolean bl = client.is64Bit();
        boolean bl2 = bl && Runtime.getRuntime().maxMemory() >= 1000000000L;
        this.viewDistance = new SimpleOption<Integer>("options.renderDistance", SimpleOption.emptyTooltip(), (optionText, value) -> GameOptions.getGenericValueText(optionText, Text.translatable("options.chunks", value)), new SimpleOption.ValidatingIntSliderCallbacks(2, bl2 ? 32 : 16), bl ? 12 : 8, value -> MinecraftClient.getInstance().worldRenderer.scheduleTerrainUpdate());
        this.simulationDistance = new SimpleOption<Integer>("options.simulationDistance", SimpleOption.emptyTooltip(), (optionText, value) -> GameOptions.getGenericValueText(optionText, Text.translatable("options.chunks", value)), new SimpleOption.ValidatingIntSliderCallbacks(5, bl2 ? 32 : 16), bl ? 12 : 8, value -> {});
        this.syncChunkWrites = Util.getOperatingSystem() == Util.OperatingSystem.WINDOWS;
        this.load();
    }

    public float getTextBackgroundOpacity(float fallback) {
        return this.backgroundForChatOnly.getValue() != false ? fallback : this.getTextBackgroundOpacity().getValue().floatValue();
    }

    public int getTextBackgroundColor(float fallbackOpacity) {
        return (int)(this.getTextBackgroundOpacity(fallbackOpacity) * 255.0f) << 24 & 0xFF000000;
    }

    public int getTextBackgroundColor(int fallbackColor) {
        return this.backgroundForChatOnly.getValue() != false ? fallbackColor : (int)(this.textBackgroundOpacity.getValue() * 255.0) << 24 & 0xFF000000;
    }

    public void setKeyCode(KeyBinding key, InputUtil.Key code) {
        key.setBoundKey(code);
        this.write();
    }

    private void accept(Visitor visitor) {
        visitor.accept("autoJump", this.autoJump);
        visitor.accept("operatorItemsTab", this.operatorItemsTab);
        visitor.accept("autoSuggestions", this.autoSuggestions);
        visitor.accept("chatColors", this.chatColors);
        visitor.accept("chatLinks", this.chatLinks);
        visitor.accept("chatLinksPrompt", this.chatLinksPrompt);
        visitor.accept("enableVsync", this.enableVsync);
        visitor.accept("entityShadows", this.entityShadows);
        visitor.accept("forceUnicodeFont", this.forceUnicodeFont);
        visitor.accept("discrete_mouse_scroll", this.discreteMouseScroll);
        visitor.accept("invertYMouse", this.invertYMouse);
        visitor.accept("realmsNotifications", this.realmsNotifications);
        visitor.accept("reducedDebugInfo", this.reducedDebugInfo);
        visitor.accept("showSubtitles", this.showSubtitles);
        visitor.accept("directionalAudio", this.directionalAudio);
        visitor.accept("touchscreen", this.touchscreen);
        visitor.accept("fullscreen", this.fullscreen);
        visitor.accept("bobView", this.bobView);
        visitor.accept("toggleCrouch", this.sneakToggled);
        visitor.accept("toggleSprint", this.sprintToggled);
        visitor.accept("darkMojangStudiosBackground", this.monochromeLogo);
        visitor.accept("hideLightningFlashes", this.hideLightningFlashes);
        visitor.accept("mouseSensitivity", this.mouseSensitivity);
        visitor.accept("fov", this.fov);
        visitor.accept("screenEffectScale", this.distortionEffectScale);
        visitor.accept("fovEffectScale", this.fovEffectScale);
        visitor.accept("darknessEffectScale", this.darknessEffectScale);
        visitor.accept("glintSpeed", this.glintSpeed);
        visitor.accept("glintStrength", this.glintStrength);
        visitor.accept("damageTiltStrength", this.damageTiltStrength);
        visitor.accept("highContrast", this.highContrast);
        visitor.accept("gamma", this.gamma);
        visitor.accept("renderDistance", this.viewDistance);
        visitor.accept("simulationDistance", this.simulationDistance);
        visitor.accept("entityDistanceScaling", this.entityDistanceScaling);
        visitor.accept("guiScale", this.guiScale);
        visitor.accept("particles", this.particles);
        visitor.accept("maxFps", this.maxFps);
        visitor.accept("graphicsMode", this.graphicsMode);
        visitor.accept("ao", this.ao);
        visitor.accept("prioritizeChunkUpdates", this.chunkBuilderMode);
        visitor.accept("biomeBlendRadius", this.biomeBlendRadius);
        visitor.accept("renderClouds", this.cloudRenderMode);
        this.resourcePacks = visitor.visitObject("resourcePacks", this.resourcePacks, GameOptions::parseList, arg_0 -> ((Gson)GSON).toJson(arg_0));
        this.incompatibleResourcePacks = visitor.visitObject("incompatibleResourcePacks", this.incompatibleResourcePacks, GameOptions::parseList, arg_0 -> ((Gson)GSON).toJson(arg_0));
        this.lastServer = visitor.visitString("lastServer", this.lastServer);
        this.language = visitor.visitString("lang", this.language);
        visitor.accept("soundDevice", this.soundDevice);
        visitor.accept("chatVisibility", this.chatVisibility);
        visitor.accept("chatOpacity", this.chatOpacity);
        visitor.accept("chatLineSpacing", this.chatLineSpacing);
        visitor.accept("textBackgroundOpacity", this.textBackgroundOpacity);
        visitor.accept("backgroundForChatOnly", this.backgroundForChatOnly);
        this.hideServerAddress = visitor.visitBoolean("hideServerAddress", this.hideServerAddress);
        this.advancedItemTooltips = visitor.visitBoolean("advancedItemTooltips", this.advancedItemTooltips);
        this.pauseOnLostFocus = visitor.visitBoolean("pauseOnLostFocus", this.pauseOnLostFocus);
        this.overrideWidth = visitor.visitInt("overrideWidth", this.overrideWidth);
        this.overrideHeight = visitor.visitInt("overrideHeight", this.overrideHeight);
        visitor.accept("chatHeightFocused", this.chatHeightFocused);
        visitor.accept("chatDelay", this.chatDelay);
        visitor.accept("chatHeightUnfocused", this.chatHeightUnfocused);
        visitor.accept("chatScale", this.chatScale);
        visitor.accept("chatWidth", this.chatWidth);
        visitor.accept("notificationDisplayTime", this.notificationDisplayTime);
        visitor.accept("mipmapLevels", this.mipmapLevels);
        this.useNativeTransport = visitor.visitBoolean("useNativeTransport", this.useNativeTransport);
        visitor.accept("mainHand", this.mainArm);
        visitor.accept("attackIndicator", this.attackIndicator);
        visitor.accept("narrator", this.narrator);
        this.tutorialStep = visitor.visitObject("tutorialStep", this.tutorialStep, TutorialStep::byName, TutorialStep::getName);
        visitor.accept("mouseWheelSensitivity", this.mouseWheelSensitivity);
        visitor.accept("rawMouseInput", this.rawMouseInput);
        this.glDebugVerbosity = visitor.visitInt("glDebugVerbosity", this.glDebugVerbosity);
        this.skipMultiplayerWarning = visitor.visitBoolean("skipMultiplayerWarning", this.skipMultiplayerWarning);
        this.skipRealms32BitWarning = visitor.visitBoolean("skipRealms32bitWarning", this.skipRealms32BitWarning);
        visitor.accept("hideMatchedNames", this.hideMatchedNames);
        this.joinedFirstServer = visitor.visitBoolean("joinedFirstServer", this.joinedFirstServer);
        this.hideBundleTutorial = visitor.visitBoolean("hideBundleTutorial", this.hideBundleTutorial);
        this.syncChunkWrites = visitor.visitBoolean("syncChunkWrites", this.syncChunkWrites);
        visitor.accept("showAutosaveIndicator", this.showAutosaveIndicator);
        visitor.accept("allowServerListing", this.allowServerListing);
        visitor.accept("onlyShowSecureChat", this.onlyShowSecureChat);
        visitor.accept("panoramaScrollSpeed", this.panoramaSpeed);
        visitor.accept("telemetryOptInExtra", this.telemetryOptInExtra);
        this.onboardAccessibility = visitor.visitBoolean("onboardAccessibility", this.onboardAccessibility);
        for (KeyBinding keyBinding : this.allKeys) {
            String string2;
            String string = keyBinding.getBoundKeyTranslationKey();
            if (string.equals(string2 = visitor.visitString("key_" + keyBinding.getTranslationKey(), string))) continue;
            keyBinding.setBoundKey(InputUtil.fromTranslationKey(string2));
        }
        for (SoundCategory soundCategory : SoundCategory.values()) {
            visitor.accept("soundCategory_" + soundCategory.getName(), this.soundVolumeLevels.get((Object)soundCategory));
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
                if (GameOptions.isTrue(nbtCompound2.getString("fancyGraphics"))) {
                    this.graphicsMode.setValue(GraphicsMode.FANCY);
                } else {
                    this.graphicsMode.setValue(GraphicsMode.FAST);
                }
            }
            this.accept(new Visitor(){

                @Nullable
                private String find(String key) {
                    return nbtCompound2.contains(key) ? nbtCompound2.getString(key) : null;
                }

                @Override
                public <T> void accept(String key, SimpleOption<T> option) {
                    String string = this.find(key);
                    if (string != null) {
                        JsonReader jsonReader = new JsonReader((Reader)new StringReader(string.isEmpty() ? "\"\"" : string));
                        JsonElement jsonElement = JsonParser.parseReader((JsonReader)jsonReader);
                        DataResult dataResult = option.getCodec().parse((DynamicOps)JsonOps.INSTANCE, (Object)jsonElement);
                        dataResult.error().ifPresent(partialResult -> LOGGER.error("Error parsing option value " + string + " for option " + option + ": " + partialResult.message()));
                        dataResult.result().ifPresent(option::setValue);
                    }
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
            });
            if (nbtCompound2.contains("fullscreenResolution")) {
                this.fullscreenResolution = nbtCompound2.getString("fullscreenResolution");
            }
            if (this.client.getWindow() != null) {
                this.client.getWindow().setFramerateLimit(this.maxFps.getValue());
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
        return DataFixTypes.OPTIONS.update(this.client.getDataFixer(), nbt, i);
    }

    public void write() {
        try (final PrintWriter printWriter = new PrintWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(this.optionsFile), StandardCharsets.UTF_8));){
            printWriter.println("version:" + SharedConstants.getGameVersion().getSaveVersion().getId());
            this.accept(new Visitor(){

                public void print(String key) {
                    printWriter.print(key);
                    printWriter.print(':');
                }

                @Override
                public <T> void accept(String key, SimpleOption<T> option) {
                    DataResult dataResult = option.getCodec().encodeStart((DynamicOps)JsonOps.INSTANCE, option.getValue());
                    dataResult.error().ifPresent(partialResult -> LOGGER.error("Error saving option " + option + ": " + partialResult));
                    dataResult.result().ifPresent(json -> {
                        this.print(key);
                        printWriter.println(GSON.toJson(json));
                    });
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

    public void sendClientSettings() {
        if (this.client.player != null) {
            int i = 0;
            for (PlayerModelPart playerModelPart : this.enabledPlayerModelParts) {
                i |= playerModelPart.getBitFlag();
            }
            this.client.player.networkHandler.sendPacket(new ClientSettingsC2SPacket(this.language, this.viewDistance.getValue(), this.chatVisibility.getValue(), this.chatColors.getValue(), i, this.mainArm.getValue(), this.client.shouldFilterText(), this.allowServerListing.getValue()));
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

    public CloudRenderMode getCloudRenderModeValue() {
        if (this.getClampedViewDistance() >= 4) {
            return this.cloudRenderMode.getValue();
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

    public File getOptionsFile() {
        return this.optionsFile;
    }

    public String collectProfiledOptions() {
        Stream<Pair> stream = Stream.builder().add(Pair.of((Object)"ao", (Object)this.ao.getValue())).add(Pair.of((Object)"biomeBlendRadius", (Object)this.biomeBlendRadius.getValue())).add(Pair.of((Object)"enableVsync", (Object)this.enableVsync.getValue())).add(Pair.of((Object)"entityDistanceScaling", (Object)this.entityDistanceScaling.getValue())).add(Pair.of((Object)"entityShadows", (Object)this.entityShadows.getValue())).add(Pair.of((Object)"forceUnicodeFont", (Object)this.forceUnicodeFont.getValue())).add(Pair.of((Object)"fov", (Object)this.fov.getValue())).add(Pair.of((Object)"fovEffectScale", (Object)this.fovEffectScale.getValue())).add(Pair.of((Object)"darknessEffectScale", (Object)this.darknessEffectScale.getValue())).add(Pair.of((Object)"glintSpeed", (Object)this.glintSpeed.getValue())).add(Pair.of((Object)"glintStrength", (Object)this.glintStrength.getValue())).add(Pair.of((Object)"prioritizeChunkUpdates", (Object)this.chunkBuilderMode.getValue())).add(Pair.of((Object)"fullscreen", (Object)this.fullscreen.getValue())).add(Pair.of((Object)"fullscreenResolution", (Object)String.valueOf(this.fullscreenResolution))).add(Pair.of((Object)"gamma", (Object)this.gamma.getValue())).add(Pair.of((Object)"glDebugVerbosity", (Object)this.glDebugVerbosity)).add(Pair.of((Object)"graphicsMode", (Object)this.graphicsMode.getValue())).add(Pair.of((Object)"guiScale", (Object)this.guiScale.getValue())).add(Pair.of((Object)"maxFps", (Object)this.maxFps.getValue())).add(Pair.of((Object)"mipmapLevels", (Object)this.mipmapLevels.getValue())).add(Pair.of((Object)"narrator", (Object)((Object)this.narrator.getValue()))).add(Pair.of((Object)"overrideHeight", (Object)this.overrideHeight)).add(Pair.of((Object)"overrideWidth", (Object)this.overrideWidth)).add(Pair.of((Object)"particles", (Object)this.particles.getValue())).add(Pair.of((Object)"reducedDebugInfo", (Object)this.reducedDebugInfo.getValue())).add(Pair.of((Object)"renderClouds", (Object)this.cloudRenderMode.getValue())).add(Pair.of((Object)"renderDistance", (Object)this.viewDistance.getValue())).add(Pair.of((Object)"simulationDistance", (Object)this.simulationDistance.getValue())).add(Pair.of((Object)"resourcePacks", this.resourcePacks)).add(Pair.of((Object)"screenEffectScale", (Object)this.distortionEffectScale.getValue())).add(Pair.of((Object)"syncChunkWrites", (Object)this.syncChunkWrites)).add(Pair.of((Object)"useNativeTransport", (Object)this.useNativeTransport)).add(Pair.of((Object)"soundDevice", (Object)this.soundDevice.getValue())).build();
        return stream.map(option -> (String)option.getFirst() + ": " + option.getSecond()).collect(Collectors.joining(System.lineSeparator()));
    }

    public void setServerViewDistance(int serverViewDistance) {
        this.serverViewDistance = serverViewDistance;
    }

    public int getClampedViewDistance() {
        return this.serverViewDistance > 0 ? Math.min(this.viewDistance.getValue(), this.serverViewDistance) : this.viewDistance.getValue();
    }

    private static Text getPixelValueText(Text prefix, int value) {
        return Text.translatable("options.pixel_value", prefix, value);
    }

    private static Text getPercentValueText(Text prefix, double value) {
        return Text.translatable("options.percent_value", prefix, (int)(value * 100.0));
    }

    public static Text getGenericValueText(Text prefix, Text value) {
        return Text.translatable("options.generic_value", prefix, value);
    }

    public static Text getGenericValueText(Text prefix, int value) {
        return GameOptions.getGenericValueText(prefix, Text.literal(Integer.toString(value)));
    }

    @Environment(value=EnvType.CLIENT)
    static interface Visitor {
        public <T> void accept(String var1, SimpleOption<T> var2);

        public int visitInt(String var1, int var2);

        public boolean visitBoolean(String var1, boolean var2);

        public String visitString(String var1, String var2);

        public float visitFloat(String var1, float var2);

        public <T> T visitObject(String var1, T var2, Function<String, T> var3, Function<T, String> var4);
    }
}

