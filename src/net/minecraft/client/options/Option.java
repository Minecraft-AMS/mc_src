/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.options;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.options.AoOption;
import net.minecraft.client.options.AttackIndicator;
import net.minecraft.client.options.BooleanOption;
import net.minecraft.client.options.ChatVisibility;
import net.minecraft.client.options.CloudRenderMode;
import net.minecraft.client.options.CyclingOption;
import net.minecraft.client.options.DoubleOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.LogarithmicOption;
import net.minecraft.client.options.NarratorOption;
import net.minecraft.client.options.ParticlesOption;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.Window;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public abstract class Option {
    public static final DoubleOption BIOME_BLEND_RADIUS = new DoubleOption("options.biomeBlendRadius", 0.0, 7.0, 1.0f, gameOptions -> gameOptions.biomeBlendRadius, (gameOptions, double_) -> {
        gameOptions.biomeBlendRadius = MathHelper.clamp((int)double_.doubleValue(), 0, 7);
        MinecraftClient.getInstance().worldRenderer.reload();
    }, (gameOptions, doubleOption) -> {
        double d = doubleOption.get((GameOptions)gameOptions);
        String string = doubleOption.getDisplayPrefix();
        if (d == 0.0) {
            return string + I18n.translate("options.off", new Object[0]);
        }
        int i = (int)d * 2 + 1;
        return string + i + "x" + i;
    });
    public static final DoubleOption CHAT_HEIGHT_FOCUSED = new DoubleOption("options.chat.height.focused", 0.0, 1.0, 0.0f, gameOptions -> gameOptions.chatHeightFocused, (gameOptions, double_) -> {
        gameOptions.chatHeightFocused = double_;
        MinecraftClient.getInstance().inGameHud.getChatHud().reset();
    }, (gameOptions, doubleOption) -> {
        double d = doubleOption.method_18611(doubleOption.get((GameOptions)gameOptions));
        return doubleOption.getDisplayPrefix() + ChatHud.getHeight(d) + "px";
    });
    public static final DoubleOption SATURATION = new DoubleOption("options.chat.height.unfocused", 0.0, 1.0, 0.0f, gameOptions -> gameOptions.chatHeightUnfocused, (gameOptions, double_) -> {
        gameOptions.chatHeightUnfocused = double_;
        MinecraftClient.getInstance().inGameHud.getChatHud().reset();
    }, (gameOptions, doubleOption) -> {
        double d = doubleOption.method_18611(doubleOption.get((GameOptions)gameOptions));
        return doubleOption.getDisplayPrefix() + ChatHud.getHeight(d) + "px";
    });
    public static final DoubleOption CHAT_OPACITY = new DoubleOption("options.chat.opacity", 0.0, 1.0, 0.0f, gameOptions -> gameOptions.chatOpacity, (gameOptions, double_) -> {
        gameOptions.chatOpacity = double_;
        MinecraftClient.getInstance().inGameHud.getChatHud().reset();
    }, (gameOptions, doubleOption) -> {
        double d = doubleOption.method_18611(doubleOption.get((GameOptions)gameOptions));
        return doubleOption.getDisplayPrefix() + (int)(d * 90.0 + 10.0) + "%";
    });
    public static final DoubleOption CHAT_SCALE = new DoubleOption("options.chat.scale", 0.0, 1.0, 0.0f, gameOptions -> gameOptions.chatScale, (gameOptions, double_) -> {
        gameOptions.chatScale = double_;
        MinecraftClient.getInstance().inGameHud.getChatHud().reset();
    }, (gameOptions, doubleOption) -> {
        double d = doubleOption.method_18611(doubleOption.get((GameOptions)gameOptions));
        String string = doubleOption.getDisplayPrefix();
        if (d == 0.0) {
            return string + I18n.translate("options.off", new Object[0]);
        }
        return string + (int)(d * 100.0) + "%";
    });
    public static final DoubleOption CHAT_WIDTH = new DoubleOption("options.chat.width", 0.0, 1.0, 0.0f, gameOptions -> gameOptions.chatWidth, (gameOptions, double_) -> {
        gameOptions.chatWidth = double_;
        MinecraftClient.getInstance().inGameHud.getChatHud().reset();
    }, (gameOptions, doubleOption) -> {
        double d = doubleOption.method_18611(doubleOption.get((GameOptions)gameOptions));
        return doubleOption.getDisplayPrefix() + ChatHud.getWidth(d) + "px";
    });
    public static final DoubleOption FOV = new DoubleOption("options.fov", 30.0, 110.0, 1.0f, gameOptions -> gameOptions.fov, (gameOptions, double_) -> {
        gameOptions.fov = double_;
    }, (gameOptions, doubleOption) -> {
        double d = doubleOption.get((GameOptions)gameOptions);
        String string = doubleOption.getDisplayPrefix();
        if (d == 70.0) {
            return string + I18n.translate("options.fov.min", new Object[0]);
        }
        if (d == doubleOption.getMax()) {
            return string + I18n.translate("options.fov.max", new Object[0]);
        }
        return string + (int)d;
    });
    public static final DoubleOption FRAMERATE_LIMIT = new DoubleOption("options.framerateLimit", 10.0, 260.0, 10.0f, gameOptions -> gameOptions.maxFps, (gameOptions, double_) -> {
        gameOptions.maxFps = (int)double_.doubleValue();
        MinecraftClient.getInstance().window.setFramerateLimit(gameOptions.maxFps);
    }, (gameOptions, doubleOption) -> {
        double d = doubleOption.get((GameOptions)gameOptions);
        String string = doubleOption.getDisplayPrefix();
        if (d == doubleOption.getMax()) {
            return string + I18n.translate("options.framerateLimit.max", new Object[0]);
        }
        return string + I18n.translate("options.framerate", (int)d);
    });
    public static final DoubleOption GAMMA = new DoubleOption("options.gamma", 0.0, 1.0, 0.0f, gameOptions -> gameOptions.gamma, (gameOptions, double_) -> {
        gameOptions.gamma = double_;
    }, (gameOptions, doubleOption) -> {
        double d = doubleOption.method_18611(doubleOption.get((GameOptions)gameOptions));
        String string = doubleOption.getDisplayPrefix();
        if (d == 0.0) {
            return string + I18n.translate("options.gamma.min", new Object[0]);
        }
        if (d == 1.0) {
            return string + I18n.translate("options.gamma.max", new Object[0]);
        }
        return string + "+" + (int)(d * 100.0) + "%";
    });
    public static final DoubleOption MIPMAP_LEVELS = new DoubleOption("options.mipmapLevels", 0.0, 4.0, 1.0f, gameOptions -> gameOptions.mipmapLevels, (gameOptions, double_) -> {
        gameOptions.mipmapLevels = (int)double_.doubleValue();
    }, (gameOptions, doubleOption) -> {
        double d = doubleOption.get((GameOptions)gameOptions);
        String string = doubleOption.getDisplayPrefix();
        if (d == 0.0) {
            return string + I18n.translate("options.off", new Object[0]);
        }
        return string + (int)d;
    });
    public static final DoubleOption MOUSE_WHEEL_SENSITIVITY = new LogarithmicOption("options.mouseWheelSensitivity", 0.01, 10.0, 0.01f, gameOptions -> gameOptions.mouseWheelSensitivity, (gameOptions, double_) -> {
        gameOptions.mouseWheelSensitivity = double_;
    }, (gameOptions, doubleOption) -> {
        double d = doubleOption.method_18611(doubleOption.get((GameOptions)gameOptions));
        return doubleOption.getDisplayPrefix() + String.format("%.2f", doubleOption.method_18616(d));
    });
    public static final BooleanOption RAW_MOUSE_INPUT = new BooleanOption("options.rawMouseInput", gameOptions -> gameOptions.field_20308, (gameOptions, boolean_) -> {
        gameOptions.field_20308 = boolean_;
        Window window = MinecraftClient.getInstance().window;
        if (window != null) {
            window.method_21668((boolean)boolean_);
        }
    });
    public static final DoubleOption RENDER_DISTANCE = new DoubleOption("options.renderDistance", 2.0, 16.0, 1.0f, gameOptions -> gameOptions.viewDistance, (gameOptions, double_) -> {
        gameOptions.viewDistance = (int)double_.doubleValue();
        MinecraftClient.getInstance().worldRenderer.scheduleTerrainUpdate();
    }, (gameOptions, doubleOption) -> {
        double d = doubleOption.get((GameOptions)gameOptions);
        return doubleOption.getDisplayPrefix() + I18n.translate("options.chunks", (int)d);
    });
    public static final DoubleOption SENSITIVITY = new DoubleOption("options.sensitivity", 0.0, 1.0, 0.0f, gameOptions -> gameOptions.mouseSensitivity, (gameOptions, double_) -> {
        gameOptions.mouseSensitivity = double_;
    }, (gameOptions, doubleOption) -> {
        double d = doubleOption.method_18611(doubleOption.get((GameOptions)gameOptions));
        String string = doubleOption.getDisplayPrefix();
        if (d == 0.0) {
            return string + I18n.translate("options.sensitivity.min", new Object[0]);
        }
        if (d == 1.0) {
            return string + I18n.translate("options.sensitivity.max", new Object[0]);
        }
        return string + (int)(d * 200.0) + "%";
    });
    public static final DoubleOption TEXT_BACKGROUND_OPACITY = new DoubleOption("options.accessibility.text_background_opacity", 0.0, 1.0, 0.0f, gameOptions -> gameOptions.textBackgroundOpacity, (gameOptions, double_) -> {
        gameOptions.textBackgroundOpacity = double_;
        MinecraftClient.getInstance().inGameHud.getChatHud().reset();
    }, (gameOptions, doubleOption) -> doubleOption.getDisplayPrefix() + (int)(doubleOption.method_18611(doubleOption.get((GameOptions)gameOptions)) * 100.0) + "%");
    public static final CyclingOption AO = new CyclingOption("options.ao", (gameOptions, integer) -> {
        gameOptions.ao = AoOption.getOption(gameOptions.ao.getValue() + integer);
        MinecraftClient.getInstance().worldRenderer.reload();
    }, (gameOptions, cyclingOption) -> cyclingOption.getDisplayPrefix() + I18n.translate(gameOptions.ao.getTranslationKey(), new Object[0]));
    public static final CyclingOption ATTACK_INDICATOR = new CyclingOption("options.attackIndicator", (gameOptions, integer) -> {
        gameOptions.attackIndicator = AttackIndicator.byId(gameOptions.attackIndicator.getId() + integer);
    }, (gameOptions, cyclingOption) -> cyclingOption.getDisplayPrefix() + I18n.translate(gameOptions.attackIndicator.getTranslationKey(), new Object[0]));
    public static final CyclingOption VISIBILITY = new CyclingOption("options.chat.visibility", (gameOptions, integer) -> {
        gameOptions.chatVisibility = ChatVisibility.byId((gameOptions.chatVisibility.getId() + integer) % 3);
    }, (gameOptions, cyclingOption) -> cyclingOption.getDisplayPrefix() + I18n.translate(gameOptions.chatVisibility.getTranslationKey(), new Object[0]));
    public static final CyclingOption GRAPHICS = new CyclingOption("options.graphics", (gameOptions, integer) -> {
        gameOptions.fancyGraphics = !gameOptions.fancyGraphics;
        MinecraftClient.getInstance().worldRenderer.reload();
    }, (gameOptions, cyclingOption) -> {
        if (gameOptions.fancyGraphics) {
            return cyclingOption.getDisplayPrefix() + I18n.translate("options.graphics.fancy", new Object[0]);
        }
        return cyclingOption.getDisplayPrefix() + I18n.translate("options.graphics.fast", new Object[0]);
    });
    public static final CyclingOption GUI_SCALE = new CyclingOption("options.guiScale", (gameOptions, integer) -> {
        gameOptions.guiScale = Integer.remainderUnsigned(gameOptions.guiScale + integer, MinecraftClient.getInstance().window.calculateScaleFactor(0, MinecraftClient.getInstance().forcesUnicodeFont()) + 1);
    }, (gameOptions, cyclingOption) -> cyclingOption.getDisplayPrefix() + (gameOptions.guiScale == 0 ? I18n.translate("options.guiScale.auto", new Object[0]) : Integer.valueOf(gameOptions.guiScale)));
    public static final CyclingOption MAIN_HAND = new CyclingOption("options.mainHand", (gameOptions, integer) -> {
        gameOptions.mainArm = gameOptions.mainArm.getOpposite();
    }, (gameOptions, cyclingOption) -> cyclingOption.getDisplayPrefix() + (Object)((Object)gameOptions.mainArm));
    public static final CyclingOption NARRATOR = new CyclingOption("options.narrator", (gameOptions, integer) -> {
        gameOptions.narrator = NarratorManager.INSTANCE.isActive() ? NarratorOption.byId(gameOptions.narrator.getId() + integer) : NarratorOption.OFF;
        NarratorManager.INSTANCE.addToast(gameOptions.narrator);
    }, (gameOptions, cyclingOption) -> {
        if (NarratorManager.INSTANCE.isActive()) {
            return cyclingOption.getDisplayPrefix() + I18n.translate(gameOptions.narrator.getTranslationKey(), new Object[0]);
        }
        return cyclingOption.getDisplayPrefix() + I18n.translate("options.narrator.notavailable", new Object[0]);
    });
    public static final CyclingOption PARTICLES = new CyclingOption("options.particles", (gameOptions, integer) -> {
        gameOptions.particles = ParticlesOption.byId(gameOptions.particles.getId() + integer);
    }, (gameOptions, cyclingOption) -> cyclingOption.getDisplayPrefix() + I18n.translate(gameOptions.particles.getTranslationKey(), new Object[0]));
    public static final CyclingOption CLOUDS = new CyclingOption("options.renderClouds", (gameOptions, integer) -> {
        gameOptions.cloudRenderMode = CloudRenderMode.getOption(gameOptions.cloudRenderMode.getValue() + integer);
    }, (gameOptions, cyclingOption) -> cyclingOption.getDisplayPrefix() + I18n.translate(gameOptions.cloudRenderMode.getTranslationKey(), new Object[0]));
    public static final CyclingOption TEXT_BACKGROUND = new CyclingOption("options.accessibility.text_background", (gameOptions, integer) -> {
        gameOptions.backgroundForChatOnly = !gameOptions.backgroundForChatOnly;
    }, (gameOptions, cyclingOption) -> cyclingOption.getDisplayPrefix() + I18n.translate(gameOptions.backgroundForChatOnly ? "options.accessibility.text_background.chat" : "options.accessibility.text_background.everywhere", new Object[0]));
    public static final BooleanOption AUTO_JUMP = new BooleanOption("options.autoJump", gameOptions -> gameOptions.autoJump, (gameOptions, boolean_) -> {
        gameOptions.autoJump = boolean_;
    });
    public static final BooleanOption AUTO_SUGGESTIONS = new BooleanOption("options.autoSuggestCommands", gameOptions -> gameOptions.autoSuggestions, (gameOptions, boolean_) -> {
        gameOptions.autoSuggestions = boolean_;
    });
    public static final BooleanOption CHAT_COLOR = new BooleanOption("options.chat.color", gameOptions -> gameOptions.chatColors, (gameOptions, boolean_) -> {
        gameOptions.chatColors = boolean_;
    });
    public static final BooleanOption CHAT_LINKS = new BooleanOption("options.chat.links", gameOptions -> gameOptions.chatLinks, (gameOptions, boolean_) -> {
        gameOptions.chatLinks = boolean_;
    });
    public static final BooleanOption CHAT_LINKS_PROMPT = new BooleanOption("options.chat.links.prompt", gameOptions -> gameOptions.chatLinksPrompt, (gameOptions, boolean_) -> {
        gameOptions.chatLinksPrompt = boolean_;
    });
    public static final BooleanOption DISCRETE_MOUSE_SCROLL = new BooleanOption("options.discrete_mouse_scroll", gameOptions -> gameOptions.discreteMouseScroll, (gameOptions, boolean_) -> {
        gameOptions.discreteMouseScroll = boolean_;
    });
    public static final BooleanOption VSYNC = new BooleanOption("options.vsync", gameOptions -> gameOptions.enableVsync, (gameOptions, boolean_) -> {
        gameOptions.enableVsync = boolean_;
        if (MinecraftClient.getInstance().window != null) {
            MinecraftClient.getInstance().window.setVsync(gameOptions.enableVsync);
        }
    });
    public static final BooleanOption ENTITY_SHADOWS = new BooleanOption("options.entityShadows", gameOptions -> gameOptions.entityShadows, (gameOptions, boolean_) -> {
        gameOptions.entityShadows = boolean_;
    });
    public static final BooleanOption FORCE_UNICODE_FONT = new BooleanOption("options.forceUnicodeFont", gameOptions -> gameOptions.forceUnicodeFont, (gameOptions, boolean_) -> {
        gameOptions.forceUnicodeFont = boolean_;
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient.getFontManager() != null) {
            minecraftClient.getFontManager().setForceUnicodeFont(gameOptions.forceUnicodeFont, Util.getServerWorkerExecutor(), minecraftClient);
        }
    });
    public static final BooleanOption INVERT_MOUSE = new BooleanOption("options.invertMouse", gameOptions -> gameOptions.invertYMouse, (gameOptions, boolean_) -> {
        gameOptions.invertYMouse = boolean_;
    });
    public static final BooleanOption REALMS_NOTIFICATIONS = new BooleanOption("options.realmsNotifications", gameOptions -> gameOptions.realmsNotifications, (gameOptions, boolean_) -> {
        gameOptions.realmsNotifications = boolean_;
    });
    public static final BooleanOption REDUCED_DEBUG_INFO = new BooleanOption("options.reducedDebugInfo", gameOptions -> gameOptions.reducedDebugInfo, (gameOptions, boolean_) -> {
        gameOptions.reducedDebugInfo = boolean_;
    });
    public static final BooleanOption SUBTITLES = new BooleanOption("options.showSubtitles", gameOptions -> gameOptions.showSubtitles, (gameOptions, boolean_) -> {
        gameOptions.showSubtitles = boolean_;
    });
    public static final BooleanOption SNOOPER = new BooleanOption("options.snooper", gameOptions -> {
        if (gameOptions.snooperEnabled) {
            // empty if block
        }
        return false;
    }, (gameOptions, boolean_) -> {
        gameOptions.snooperEnabled = boolean_;
    });
    public static final BooleanOption TOUCHSCREEN = new BooleanOption("options.touchscreen", gameOptions -> gameOptions.touchscreen, (gameOptions, boolean_) -> {
        gameOptions.touchscreen = boolean_;
    });
    public static final BooleanOption FULLSCREEN = new BooleanOption("options.fullscreen", gameOptions -> gameOptions.fullscreen, (gameOptions, boolean_) -> {
        gameOptions.fullscreen = boolean_;
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient.window != null && minecraftClient.window.isFullscreen() != gameOptions.fullscreen) {
            minecraftClient.window.toggleFullscreen();
            gameOptions.fullscreen = minecraftClient.window.isFullscreen();
        }
    });
    public static final BooleanOption VIEW_BOBBING = new BooleanOption("options.viewBobbing", gameOptions -> gameOptions.bobView, (gameOptions, boolean_) -> {
        gameOptions.bobView = boolean_;
    });
    private final String key;

    public Option(String key) {
        this.key = key;
    }

    public abstract AbstractButtonWidget createButton(GameOptions var1, int var2, int var3, int var4);

    public String getDisplayPrefix() {
        return I18n.translate(this.key, new Object[0]) + ": ";
    }
}
