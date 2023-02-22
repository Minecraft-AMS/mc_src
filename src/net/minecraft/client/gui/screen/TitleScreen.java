/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.util.concurrent.Runnables
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen;

import com.google.common.util.concurrent.Runnables;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.ConfirmChatLinkScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.options.AccessibilityScreen;
import net.minecraft.client.gui.screen.options.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class TitleScreen
extends Screen {
    public static final CubeMapRenderer PANORAMA_CUBE_MAP = new CubeMapRenderer(new Identifier("textures/gui/title/background/panorama"));
    private static final Identifier PANORAMA_OVERLAY = new Identifier("textures/gui/title/background/panorama_overlay.png");
    private static final Identifier ACCESSIBILITY_ICON_TEXTURE = new Identifier("textures/gui/accessibility.png");
    private final boolean field_17776;
    @Nullable
    private String splashText;
    private ButtonWidget buttonResetDemo;
    @Nullable
    private Warning warning;
    private static final Identifier MINECRAFT_TITLE_TEXTURE = new Identifier("textures/gui/title/minecraft.png");
    private static final Identifier EDITION_TITLE_TEXTURE = new Identifier("textures/gui/title/edition.png");
    private boolean realmsNotificationsInitialized;
    private Screen realmsNotificationGui;
    private int copyrightTextWidth;
    private int copyrightTextX;
    private final RotatingCubeMapRenderer backgroundRenderer = new RotatingCubeMapRenderer(PANORAMA_CUBE_MAP);
    private final boolean doBackgroundFade;
    private long backgroundFadeStart;

    public TitleScreen() {
        this(false);
    }

    public TitleScreen(boolean doBackgroundFade) {
        super(new TranslatableText("narrator.screen.title", new Object[0]));
        this.doBackgroundFade = doBackgroundFade;
        boolean bl = this.field_17776 = (double)new Random().nextFloat() < 1.0E-4;
        if (!GLX.supportsOpenGL2() && !GLX.isNextGen()) {
            this.warning = new Warning(new TranslatableText("title.oldgl.eol.line1", new Object[0]).formatted(Formatting.RED).formatted(Formatting.BOLD), new TranslatableText("title.oldgl.eol.line2", new Object[0]).formatted(Formatting.RED).formatted(Formatting.BOLD), "https://help.mojang.com/customer/portal/articles/325948?ref=game");
        }
    }

    private boolean areRealmsNotificationsEnabled() {
        return this.minecraft.options.realmsNotifications && this.realmsNotificationGui != null;
    }

    @Override
    public void tick() {
        if (this.areRealmsNotificationsEnabled()) {
            this.realmsNotificationGui.tick();
        }
    }

    public static CompletableFuture<Void> loadTexturesAsync(TextureManager textureManager, Executor executor) {
        return CompletableFuture.allOf(textureManager.loadTextureAsync(MINECRAFT_TITLE_TEXTURE, executor), textureManager.loadTextureAsync(EDITION_TITLE_TEXTURE, executor), textureManager.loadTextureAsync(PANORAMA_OVERLAY, executor), PANORAMA_CUBE_MAP.loadTexturesAsync(textureManager, executor));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    protected void init() {
        if (this.splashText == null) {
            this.splashText = this.minecraft.getSplashTextLoader().get();
        }
        this.copyrightTextWidth = this.font.getStringWidth("Copyright Mojang AB. Do not distribute!");
        this.copyrightTextX = this.width - this.copyrightTextWidth - 2;
        int i = 24;
        int j = this.height / 4 + 48;
        if (this.minecraft.isDemo()) {
            this.initWidgetsDemo(j, 24);
        } else {
            this.initWidgetsNormal(j, 24);
        }
        this.addButton(new TexturedButtonWidget(this.width / 2 - 124, j + 72 + 12, 20, 20, 0, 106, 20, ButtonWidget.WIDGETS_LOCATION, 256, 256, buttonWidget -> this.minecraft.openScreen(new LanguageOptionsScreen(this, this.minecraft.options, this.minecraft.getLanguageManager())), I18n.translate("narrator.button.language", new Object[0])));
        this.addButton(new ButtonWidget(this.width / 2 - 100, j + 72 + 12, 98, 20, I18n.translate("menu.options", new Object[0]), buttonWidget -> this.minecraft.openScreen(new SettingsScreen(this, this.minecraft.options))));
        this.addButton(new ButtonWidget(this.width / 2 + 2, j + 72 + 12, 98, 20, I18n.translate("menu.quit", new Object[0]), buttonWidget -> this.minecraft.scheduleStop()));
        this.addButton(new TexturedButtonWidget(this.width / 2 + 104, j + 72 + 12, 20, 20, 0, 0, 20, ACCESSIBILITY_ICON_TEXTURE, 32, 64, buttonWidget -> this.minecraft.openScreen(new AccessibilityScreen(this, this.minecraft.options)), I18n.translate("narrator.button.accessibility", new Object[0])));
        if (this.warning != null) {
            this.warning.init(j);
        }
        this.minecraft.setConnectedToRealms(false);
        if (this.minecraft.options.realmsNotifications && !this.realmsNotificationsInitialized) {
            RealmsBridge realmsBridge = new RealmsBridge();
            this.realmsNotificationGui = realmsBridge.getNotificationScreen(this);
            this.realmsNotificationsInitialized = true;
        }
        if (this.areRealmsNotificationsEnabled()) {
            this.realmsNotificationGui.init(this.minecraft, this.width, this.height);
        }
    }

    private void initWidgetsNormal(int y, int spacingY) {
        this.addButton(new ButtonWidget(this.width / 2 - 100, y, 200, 20, I18n.translate("menu.singleplayer", new Object[0]), buttonWidget -> this.minecraft.openScreen(new SelectWorldScreen(this))));
        this.addButton(new ButtonWidget(this.width / 2 - 100, y + spacingY * 1, 200, 20, I18n.translate("menu.multiplayer", new Object[0]), buttonWidget -> this.minecraft.openScreen(new MultiplayerScreen(this))));
        this.addButton(new ButtonWidget(this.width / 2 - 100, y + spacingY * 2, 200, 20, I18n.translate("menu.online", new Object[0]), buttonWidget -> this.switchToRealms()));
    }

    private void initWidgetsDemo(int y, int spacingY) {
        this.addButton(new ButtonWidget(this.width / 2 - 100, y, 200, 20, I18n.translate("menu.playdemo", new Object[0]), buttonWidget -> this.minecraft.startIntegratedServer("Demo_World", "Demo_World", MinecraftServer.DEMO_LEVEL_INFO)));
        this.buttonResetDemo = this.addButton(new ButtonWidget(this.width / 2 - 100, y + spacingY * 1, 200, 20, I18n.translate("menu.resetdemo", new Object[0]), buttonWidget -> {
            LevelStorage levelStorage = this.minecraft.getLevelStorage();
            LevelProperties levelProperties = levelStorage.getLevelProperties("Demo_World");
            if (levelProperties != null) {
                this.minecraft.openScreen(new ConfirmScreen(this::method_20375, new TranslatableText("selectWorld.deleteQuestion", new Object[0]), new TranslatableText("selectWorld.deleteWarning", levelProperties.getLevelName()), I18n.translate("selectWorld.deleteButton", new Object[0]), I18n.translate("gui.cancel", new Object[0])));
            }
        }));
        LevelStorage levelStorage = this.minecraft.getLevelStorage();
        LevelProperties levelProperties = levelStorage.getLevelProperties("Demo_World");
        if (levelProperties == null) {
            this.buttonResetDemo.active = false;
        }
    }

    private void switchToRealms() {
        RealmsBridge realmsBridge = new RealmsBridge();
        realmsBridge.switchToRealms(this);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        if (this.backgroundFadeStart == 0L && this.doBackgroundFade) {
            this.backgroundFadeStart = Util.getMeasuringTimeMs();
        }
        float f = this.doBackgroundFade ? (float)(Util.getMeasuringTimeMs() - this.backgroundFadeStart) / 1000.0f : 1.0f;
        TitleScreen.fill(0, 0, this.width, this.height, -1);
        this.backgroundRenderer.render(delta, MathHelper.clamp(f, 0.0f, 1.0f));
        int i = 274;
        int j = this.width / 2 - 137;
        int k = 30;
        this.minecraft.getTextureManager().bindTexture(PANORAMA_OVERLAY);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, this.doBackgroundFade ? (float)MathHelper.ceil(MathHelper.clamp(f, 0.0f, 1.0f)) : 1.0f);
        TitleScreen.blit(0, 0, this.width, this.height, 0.0f, 0.0f, 16, 128, 16, 128);
        float g = this.doBackgroundFade ? MathHelper.clamp(f - 1.0f, 0.0f, 1.0f) : 1.0f;
        int l = MathHelper.ceil(g * 255.0f) << 24;
        if ((l & 0xFC000000) == 0) {
            return;
        }
        this.minecraft.getTextureManager().bindTexture(MINECRAFT_TITLE_TEXTURE);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, g);
        if (this.field_17776) {
            this.blit(j + 0, 30, 0, 0, 99, 44);
            this.blit(j + 99, 30, 129, 0, 27, 44);
            this.blit(j + 99 + 26, 30, 126, 0, 3, 44);
            this.blit(j + 99 + 26 + 3, 30, 99, 0, 26, 44);
            this.blit(j + 155, 30, 0, 45, 155, 44);
        } else {
            this.blit(j + 0, 30, 0, 0, 155, 44);
            this.blit(j + 155, 30, 0, 45, 155, 44);
        }
        this.minecraft.getTextureManager().bindTexture(EDITION_TITLE_TEXTURE);
        TitleScreen.blit(j + 88, 67, 0.0f, 0.0f, 98, 14, 128, 16);
        if (this.splashText != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translatef(this.width / 2 + 90, 70.0f, 0.0f);
            GlStateManager.rotatef(-20.0f, 0.0f, 0.0f, 1.0f);
            float h = 1.8f - MathHelper.abs(MathHelper.sin((float)(Util.getMeasuringTimeMs() % 1000L) / 1000.0f * ((float)Math.PI * 2)) * 0.1f);
            h = h * 100.0f / (float)(this.font.getStringWidth(this.splashText) + 32);
            GlStateManager.scalef(h, h, h);
            this.drawCenteredString(this.font, this.splashText, 0, -8, 0xFFFF00 | l);
            GlStateManager.popMatrix();
        }
        String string = "Minecraft " + SharedConstants.getGameVersion().getName();
        string = this.minecraft.isDemo() ? string + " Demo" : string + ("release".equalsIgnoreCase(this.minecraft.getVersionType()) ? "" : "/" + this.minecraft.getVersionType());
        this.drawString(this.font, string, 2, this.height - 10, 0xFFFFFF | l);
        this.drawString(this.font, "Copyright Mojang AB. Do not distribute!", this.copyrightTextX, this.height - 10, 0xFFFFFF | l);
        if (mouseX > this.copyrightTextX && mouseX < this.copyrightTextX + this.copyrightTextWidth && mouseY > this.height - 10 && mouseY < this.height) {
            TitleScreen.fill(this.copyrightTextX, this.height - 1, this.copyrightTextX + this.copyrightTextWidth, this.height, 0xFFFFFF | l);
        }
        if (this.warning != null) {
            this.warning.render(l);
        }
        for (AbstractButtonWidget abstractButtonWidget : this.buttons) {
            abstractButtonWidget.setAlpha(g);
        }
        super.render(mouseX, mouseY, delta);
        if (this.areRealmsNotificationsEnabled() && g >= 1.0f) {
            this.realmsNotificationGui.render(mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (this.warning != null && this.warning.onClick(mouseX, mouseY)) {
            return true;
        }
        if (this.areRealmsNotificationsEnabled() && this.realmsNotificationGui.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (mouseX > (double)this.copyrightTextX && mouseX < (double)(this.copyrightTextX + this.copyrightTextWidth) && mouseY > (double)(this.height - 10) && mouseY < (double)this.height) {
            this.minecraft.openScreen(new CreditsScreen(false, Runnables.doNothing()));
        }
        return false;
    }

    @Override
    public void removed() {
        if (this.realmsNotificationGui != null) {
            this.realmsNotificationGui.removed();
        }
    }

    private void method_20375(boolean bl) {
        if (bl) {
            LevelStorage levelStorage = this.minecraft.getLevelStorage();
            levelStorage.deleteLevel("Demo_World");
        }
        this.minecraft.openScreen(this);
    }

    @Environment(value=EnvType.CLIENT)
    class Warning {
        private int line2Width;
        private int startX;
        private int startY;
        private int endX;
        private int endY;
        private final Text line1;
        private final Text line2;
        private final String helpUrl;

        public Warning(Text line1, Text line2, String helpUrl) {
            this.line1 = line1;
            this.line2 = line2;
            this.helpUrl = helpUrl;
        }

        public void init(int bottomY) {
            int i = TitleScreen.this.font.getStringWidth(this.line1.getString());
            this.line2Width = TitleScreen.this.font.getStringWidth(this.line2.getString());
            int j = Math.max(i, this.line2Width);
            this.startX = (TitleScreen.this.width - j) / 2;
            this.startY = bottomY - 24;
            this.endX = this.startX + j;
            this.endY = this.startY + 24;
        }

        public void render(int aplphaChannel) {
            DrawableHelper.fill(this.startX - 2, this.startY - 2, this.endX + 2, this.endY - 1, 0x55200000);
            TitleScreen.this.drawString(TitleScreen.this.font, this.line1.asFormattedString(), this.startX, this.startY, 0xFFFFFF | aplphaChannel);
            TitleScreen.this.drawString(TitleScreen.this.font, this.line2.asFormattedString(), (TitleScreen.this.width - this.line2Width) / 2, this.startY + 12, 0xFFFFFF | aplphaChannel);
        }

        public boolean onClick(double mouseX, double mouseY) {
            if (!ChatUtil.isEmpty(this.helpUrl) && mouseX >= (double)this.startX && mouseX <= (double)this.endX && mouseY >= (double)this.startY && mouseY <= (double)this.endY) {
                TitleScreen.this.minecraft.openScreen(new ConfirmChatLinkScreen(bl -> {
                    if (bl) {
                        Util.getOperatingSystem().open(this.helpUrl);
                    }
                    TitleScreen.this.minecraft.openScreen(TitleScreen.this);
                }, this.helpUrl, true));
                return true;
            }
            return false;
        }
    }
}
