/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.BanDetails
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screen;

import com.mojang.authlib.minecraft.BanDetails;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.LogoDrawer;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.option.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screen.option.CreditsAndAttributionScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.gui.screen.RealmsNotificationsScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelSummary;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class TitleScreen
extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String DEMO_WORLD_NAME = "Demo_World";
    public static final Text COPYRIGHT = Text.literal("Copyright Mojang AB. Do not distribute!");
    public static final CubeMapRenderer PANORAMA_CUBE_MAP = new CubeMapRenderer(new Identifier("textures/gui/title/background/panorama"));
    private static final Identifier PANORAMA_OVERLAY = new Identifier("textures/gui/title/background/panorama_overlay.png");
    @Nullable
    private SplashTextRenderer splashText;
    private ButtonWidget buttonResetDemo;
    @Nullable
    private RealmsNotificationsScreen realmsNotificationGui;
    private final RotatingCubeMapRenderer backgroundRenderer = new RotatingCubeMapRenderer(PANORAMA_CUBE_MAP);
    private final boolean doBackgroundFade;
    private long backgroundFadeStart;
    @Nullable
    private DeprecationNotice deprecationNotice;
    private final LogoDrawer logoDrawer;

    public TitleScreen() {
        this(false);
    }

    public TitleScreen(boolean doBackgroundFade) {
        this(doBackgroundFade, null);
    }

    public TitleScreen(boolean doBackgroundFade, @Nullable LogoDrawer logoDrawer) {
        super(Text.translatable("narrator.screen.title"));
        this.doBackgroundFade = doBackgroundFade;
        this.logoDrawer = Objects.requireNonNullElseGet(logoDrawer, () -> new LogoDrawer(false));
    }

    private boolean isRealmsNotificationsGuiDisplayed() {
        return this.realmsNotificationGui != null;
    }

    @Override
    public void tick() {
        if (this.isRealmsNotificationsGuiDisplayed()) {
            this.realmsNotificationGui.tick();
        }
        this.client.getRealms32BitWarningChecker().showWarningIfNeeded(this);
    }

    public static CompletableFuture<Void> loadTexturesAsync(TextureManager textureManager, Executor executor) {
        return CompletableFuture.allOf(textureManager.loadTextureAsync(LogoDrawer.LOGO_TEXTURE, executor), textureManager.loadTextureAsync(LogoDrawer.EDITION_TEXTURE, executor), textureManager.loadTextureAsync(PANORAMA_OVERLAY, executor), PANORAMA_CUBE_MAP.loadTexturesAsync(textureManager, executor));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    protected void init() {
        if (this.splashText == null) {
            this.splashText = this.client.getSplashTextLoader().get();
        }
        int i = this.textRenderer.getWidth(COPYRIGHT);
        int j = this.width - i - 2;
        int k = 24;
        int l = this.height / 4 + 48;
        if (this.client.isDemo()) {
            this.initWidgetsDemo(l, 24);
        } else {
            this.initWidgetsNormal(l, 24);
        }
        this.addDrawableChild(new TexturedButtonWidget(this.width / 2 - 124, l + 72 + 12, 20, 20, 0, 106, 20, ButtonWidget.WIDGETS_TEXTURE, 256, 256, button -> this.client.setScreen(new LanguageOptionsScreen((Screen)this, this.client.options, this.client.getLanguageManager())), Text.translatable("narrator.button.language")));
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.options"), button -> this.client.setScreen(new OptionsScreen(this, this.client.options))).dimensions(this.width / 2 - 100, l + 72 + 12, 98, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.quit"), button -> this.client.scheduleStop()).dimensions(this.width / 2 + 2, l + 72 + 12, 98, 20).build());
        this.addDrawableChild(new TexturedButtonWidget(this.width / 2 + 104, l + 72 + 12, 20, 20, 0, 0, 20, ButtonWidget.ACCESSIBILITY_TEXTURE, 32, 64, button -> this.client.setScreen(new AccessibilityOptionsScreen(this, this.client.options)), Text.translatable("narrator.button.accessibility")));
        this.addDrawableChild(new PressableTextWidget(j, this.height - 10, i, 10, COPYRIGHT, button -> this.client.setScreen(new CreditsAndAttributionScreen(this)), this.textRenderer));
        this.client.setConnectedToRealms(false);
        if (this.realmsNotificationGui == null) {
            this.realmsNotificationGui = new RealmsNotificationsScreen();
        }
        if (this.isRealmsNotificationsGuiDisplayed()) {
            this.realmsNotificationGui.init(this.client, this.width, this.height);
        }
        if (!this.client.is64Bit()) {
            this.deprecationNotice = new DeprecationNotice(this.textRenderer, MultilineText.create(this.textRenderer, (StringVisitable)Text.translatable("title.32bit.deprecation"), 350, 2), this.width / 2, l - 24);
        }
    }

    private void initWidgetsNormal(int y, int spacingY) {
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.singleplayer"), button -> this.client.setScreen(new SelectWorldScreen(this))).dimensions(this.width / 2 - 100, y, 200, 20).build());
        Text text = this.getMultiplayerDisabledText();
        boolean bl = text == null;
        Tooltip tooltip = text != null ? Tooltip.of(text) : null;
        this.addDrawableChild(ButtonWidget.builder((Text)Text.translatable((String)"menu.multiplayer"), (ButtonWidget.PressAction)(ButtonWidget.PressAction)LambdaMetafactory.metafactory(null, null, null, (Lnet/minecraft/client/gui/widget/ButtonWidget;)V, onMultiplayerButtonPressed(net.minecraft.client.gui.widget.ButtonWidget ), (Lnet/minecraft/client/gui/widget/ButtonWidget;)V)((TitleScreen)this)).dimensions((int)(this.width / 2 - 100), (int)(y + spacingY * 1), (int)200, (int)20).tooltip((Tooltip)tooltip).build()).active = bl;
        this.addDrawableChild(ButtonWidget.builder((Text)Text.translatable((String)"menu.online"), (ButtonWidget.PressAction)(ButtonWidget.PressAction)LambdaMetafactory.metafactory(null, null, null, (Lnet/minecraft/client/gui/widget/ButtonWidget;)V, onRealmsButtonPress(net.minecraft.client.gui.widget.ButtonWidget ), (Lnet/minecraft/client/gui/widget/ButtonWidget;)V)((TitleScreen)this)).dimensions((int)(this.width / 2 - 100), (int)(y + spacingY * 2), (int)200, (int)20).tooltip((Tooltip)tooltip).build()).active = bl;
    }

    @Nullable
    private Text getMultiplayerDisabledText() {
        if (this.client.isMultiplayerEnabled()) {
            return null;
        }
        BanDetails banDetails = this.client.getMultiplayerBanDetails();
        if (banDetails != null) {
            if (banDetails.expires() != null) {
                return Text.translatable("title.multiplayer.disabled.banned.temporary");
            }
            return Text.translatable("title.multiplayer.disabled.banned.permanent");
        }
        return Text.translatable("title.multiplayer.disabled");
    }

    private void initWidgetsDemo(int y, int spacingY) {
        boolean bl = this.canReadDemoWorldData();
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.playdemo"), button -> {
            if (bl) {
                this.client.createIntegratedServerLoader().start(this, DEMO_WORLD_NAME);
            } else {
                this.client.createIntegratedServerLoader().createAndStart(DEMO_WORLD_NAME, MinecraftServer.DEMO_LEVEL_INFO, GeneratorOptions.DEMO_OPTIONS, WorldPresets::createDemoOptions);
            }
        }).dimensions(this.width / 2 - 100, y, 200, 20).build());
        this.buttonResetDemo = this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.resetdemo"), button -> {
            LevelStorage levelStorage = this.client.getLevelStorage();
            try (LevelStorage.Session session = levelStorage.createSession(DEMO_WORLD_NAME);){
                LevelSummary levelSummary = session.getLevelSummary();
                if (levelSummary != null) {
                    this.client.setScreen(new ConfirmScreen(this::onDemoDeletionConfirmed, Text.translatable("selectWorld.deleteQuestion"), Text.translatable("selectWorld.deleteWarning", levelSummary.getDisplayName()), Text.translatable("selectWorld.deleteButton"), ScreenTexts.CANCEL));
                }
            }
            catch (IOException iOException) {
                SystemToast.addWorldAccessFailureToast(this.client, DEMO_WORLD_NAME);
                LOGGER.warn("Failed to access demo world", (Throwable)iOException);
            }
        }).dimensions(this.width / 2 - 100, y + spacingY * 1, 200, 20).build());
        this.buttonResetDemo.active = bl;
    }

    private boolean canReadDemoWorldData() {
        boolean bl;
        block8: {
            LevelStorage.Session session = this.client.getLevelStorage().createSession(DEMO_WORLD_NAME);
            try {
                boolean bl2 = bl = session.getLevelSummary() != null;
                if (session == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if (session != null) {
                        try {
                            session.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException iOException) {
                    SystemToast.addWorldAccessFailureToast(this.client, DEMO_WORLD_NAME);
                    LOGGER.warn("Failed to read demo world data", (Throwable)iOException);
                    return false;
                }
            }
            session.close();
        }
        return bl;
    }

    private void switchToRealms() {
        this.client.setScreen(new RealmsMainScreen(this));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.backgroundFadeStart == 0L && this.doBackgroundFade) {
            this.backgroundFadeStart = Util.getMeasuringTimeMs();
        }
        float f = this.doBackgroundFade ? (float)(Util.getMeasuringTimeMs() - this.backgroundFadeStart) / 1000.0f : 1.0f;
        this.backgroundRenderer.render(delta, MathHelper.clamp(f, 0.0f, 1.0f));
        RenderSystem.enableBlend();
        context.setShaderColor(1.0f, 1.0f, 1.0f, this.doBackgroundFade ? (float)MathHelper.ceil(MathHelper.clamp(f, 0.0f, 1.0f)) : 1.0f);
        context.drawTexture(PANORAMA_OVERLAY, 0, 0, this.width, this.height, 0.0f, 0.0f, 16, 128, 16, 128);
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        float g = this.doBackgroundFade ? MathHelper.clamp(f - 1.0f, 0.0f, 1.0f) : 1.0f;
        this.logoDrawer.draw(context, this.width, g);
        int i = MathHelper.ceil(g * 255.0f) << 24;
        if ((i & 0xFC000000) == 0) {
            return;
        }
        if (this.deprecationNotice != null) {
            this.deprecationNotice.render(context, i);
        }
        if (this.splashText != null) {
            this.splashText.render(context, this.width, this.textRenderer, i);
        }
        String string = "Minecraft " + SharedConstants.getGameVersion().getName();
        string = this.client.isDemo() ? string + " Demo" : string + (String)("release".equalsIgnoreCase(this.client.getVersionType()) ? "" : "/" + this.client.getVersionType());
        if (MinecraftClient.getModStatus().isModded()) {
            string = string + I18n.translate("menu.modded", new Object[0]);
        }
        context.drawTextWithShadow(this.textRenderer, string, 2, this.height - 10, 0xFFFFFF | i);
        for (Element element : this.children()) {
            if (!(element instanceof ClickableWidget)) continue;
            ((ClickableWidget)element).setAlpha(g);
        }
        super.render(context, mouseX, mouseY, delta);
        if (this.isRealmsNotificationsGuiDisplayed() && g >= 1.0f) {
            RenderSystem.enableDepthTest();
            this.realmsNotificationGui.render(context, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return this.isRealmsNotificationsGuiDisplayed() && this.realmsNotificationGui.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void removed() {
        if (this.realmsNotificationGui != null) {
            this.realmsNotificationGui.removed();
        }
    }

    @Override
    public void onDisplayed() {
        super.onDisplayed();
        if (this.realmsNotificationGui != null) {
            this.realmsNotificationGui.onDisplayed();
        }
    }

    private void onDemoDeletionConfirmed(boolean delete) {
        if (delete) {
            try (LevelStorage.Session session = this.client.getLevelStorage().createSession(DEMO_WORLD_NAME);){
                session.deleteSessionLock();
            }
            catch (IOException iOException) {
                SystemToast.addWorldDeleteFailureToast(this.client, DEMO_WORLD_NAME);
                LOGGER.warn("Failed to delete demo world", (Throwable)iOException);
            }
        }
        this.client.setScreen(this);
    }

    private /* synthetic */ void onRealmsButtonPress(ButtonWidget button) {
        this.switchToRealms();
    }

    private /* synthetic */ void onMultiplayerButtonPressed(ButtonWidget button) {
        Screen screen = this.client.options.skipMultiplayerWarning ? new MultiplayerScreen(this) : new MultiplayerWarningScreen(this);
        this.client.setScreen(screen);
    }

    @Environment(value=EnvType.CLIENT)
    record DeprecationNotice(TextRenderer textRenderer, MultilineText label, int x, int y) {
        public void render(DrawContext context, int color) {
            this.label.fillBackground(context, this.x, this.y, this.textRenderer.fontHeight, 2, 0x200000 | Math.min(color, 0x55000000));
            this.label.drawCenterWithShadow(context, this.x, this.y, this.textRenderer.fontHeight, 0xFFFFFF | color);
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{DeprecationNotice.class, "font;label;x;y", "textRenderer", "label", "x", "y"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{DeprecationNotice.class, "font;label;x;y", "textRenderer", "label", "x", "y"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{DeprecationNotice.class, "font;label;x;y", "textRenderer", "label", "x", "y"}, this, object);
        }
    }
}

