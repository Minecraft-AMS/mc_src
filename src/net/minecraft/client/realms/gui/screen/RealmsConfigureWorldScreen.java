/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.realms.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsWorldOptions;
import net.minecraft.client.realms.dto.WorldTemplate;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.RealmsWorldSlotButton;
import net.minecraft.client.realms.gui.screen.RealmsBackupScreen;
import net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen;
import net.minecraft.client.realms.gui.screen.RealmsLongConfirmationScreen;
import net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.gui.screen.RealmsPlayerScreen;
import net.minecraft.client.realms.gui.screen.RealmsResetWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreenWithCallback;
import net.minecraft.client.realms.gui.screen.RealmsSelectWorldTemplateScreen;
import net.minecraft.client.realms.gui.screen.RealmsSettingsScreen;
import net.minecraft.client.realms.gui.screen.RealmsSlotOptionsScreen;
import net.minecraft.client.realms.gui.screen.RealmsSubscriptionInfoScreen;
import net.minecraft.client.realms.task.CloseServerTask;
import net.minecraft.client.realms.task.OpenServerTask;
import net.minecraft.client.realms.task.SwitchMinigameTask;
import net.minecraft.client.realms.task.SwitchSlotTask;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RealmsConfigureWorldScreen
extends RealmsScreenWithCallback {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Identifier ON_ICON = new Identifier("realms", "textures/gui/realms/on_icon.png");
    private static final Identifier OFF_ICON = new Identifier("realms", "textures/gui/realms/off_icon.png");
    private static final Identifier EXPIRED_ICON = new Identifier("realms", "textures/gui/realms/expired_icon.png");
    private static final Identifier EXPIRES_SOON_ICON = new Identifier("realms", "textures/gui/realms/expires_soon_icon.png");
    private static final Text field_26479 = new TranslatableText("mco.configure.worlds.title");
    private static final Text field_26480 = new TranslatableText("mco.configure.world.title");
    private static final Text field_26481 = new TranslatableText("mco.configure.current.minigame").append(": ");
    private static final Text field_26482 = new TranslatableText("mco.selectServer.expired");
    private static final Text field_26483 = new TranslatableText("mco.selectServer.expires.soon");
    private static final Text field_26484 = new TranslatableText("mco.selectServer.expires.day");
    private static final Text field_26485 = new TranslatableText("mco.selectServer.open");
    private static final Text field_26486 = new TranslatableText("mco.selectServer.closed");
    @Nullable
    private Text toolTip;
    private final RealmsMainScreen parent;
    @Nullable
    private RealmsServer server;
    private final long serverId;
    private int left_x;
    private int right_x;
    private ButtonWidget playersButton;
    private ButtonWidget settingsButton;
    private ButtonWidget subscriptionButton;
    private ButtonWidget optionsButton;
    private ButtonWidget backupButton;
    private ButtonWidget resetWorldButton;
    private ButtonWidget switchMinigameButton;
    private boolean stateChanged;
    private int animTick;
    private int clicks;

    public RealmsConfigureWorldScreen(RealmsMainScreen parent, long serverId) {
        this.parent = parent;
        this.serverId = serverId;
    }

    @Override
    public void init() {
        if (this.server == null) {
            this.fetchServerData(this.serverId);
        }
        this.left_x = this.width / 2 - 187;
        this.right_x = this.width / 2 + 190;
        this.client.keyboard.setRepeatEvents(true);
        this.playersButton = this.addButton(new ButtonWidget(this.buttonCenter(0, 3), RealmsConfigureWorldScreen.row(0), 100, 20, new TranslatableText("mco.configure.world.buttons.players"), buttonWidget -> this.client.openScreen(new RealmsPlayerScreen(this, this.server))));
        this.settingsButton = this.addButton(new ButtonWidget(this.buttonCenter(1, 3), RealmsConfigureWorldScreen.row(0), 100, 20, new TranslatableText("mco.configure.world.buttons.settings"), buttonWidget -> this.client.openScreen(new RealmsSettingsScreen(this, this.server.clone()))));
        this.subscriptionButton = this.addButton(new ButtonWidget(this.buttonCenter(2, 3), RealmsConfigureWorldScreen.row(0), 100, 20, new TranslatableText("mco.configure.world.buttons.subscription"), buttonWidget -> this.client.openScreen(new RealmsSubscriptionInfoScreen(this, this.server.clone(), this.parent))));
        for (int i = 1; i < 5; ++i) {
            this.addSlotButton(i);
        }
        this.switchMinigameButton = this.addButton(new ButtonWidget(this.buttonLeft(0), RealmsConfigureWorldScreen.row(13) - 5, 100, 20, new TranslatableText("mco.configure.world.buttons.switchminigame"), buttonWidget -> {
            RealmsSelectWorldTemplateScreen realmsSelectWorldTemplateScreen = new RealmsSelectWorldTemplateScreen(this, RealmsServer.WorldType.MINIGAME);
            realmsSelectWorldTemplateScreen.setTitle(new TranslatableText("mco.template.title.minigame"));
            this.client.openScreen(realmsSelectWorldTemplateScreen);
        }));
        this.optionsButton = this.addButton(new ButtonWidget(this.buttonLeft(0), RealmsConfigureWorldScreen.row(13) - 5, 90, 20, new TranslatableText("mco.configure.world.buttons.options"), buttonWidget -> this.client.openScreen(new RealmsSlotOptionsScreen(this, this.server.slots.get(this.server.activeSlot).clone(), this.server.worldType, this.server.activeSlot))));
        this.backupButton = this.addButton(new ButtonWidget(this.buttonLeft(1), RealmsConfigureWorldScreen.row(13) - 5, 90, 20, new TranslatableText("mco.configure.world.backup"), buttonWidget -> this.client.openScreen(new RealmsBackupScreen(this, this.server.clone(), this.server.activeSlot))));
        this.resetWorldButton = this.addButton(new ButtonWidget(this.buttonLeft(2), RealmsConfigureWorldScreen.row(13) - 5, 90, 20, new TranslatableText("mco.configure.world.buttons.resetworld"), buttonWidget -> this.client.openScreen(new RealmsResetWorldScreen(this, this.server.clone(), () -> this.client.openScreen(this.getNewScreen()), () -> this.client.openScreen(this.getNewScreen())))));
        this.addButton(new ButtonWidget(this.right_x - 80 + 8, RealmsConfigureWorldScreen.row(13) - 5, 70, 20, ScreenTexts.BACK, buttonWidget -> this.backButtonClicked()));
        this.backupButton.active = true;
        if (this.server == null) {
            this.hideMinigameButtons();
            this.hideRegularButtons();
            this.playersButton.active = false;
            this.settingsButton.active = false;
            this.subscriptionButton.active = false;
        } else {
            this.disableButtons();
            if (this.isMinigame()) {
                this.hideRegularButtons();
            } else {
                this.hideMinigameButtons();
            }
        }
    }

    private void addSlotButton(int slotIndex) {
        int i = this.frame(slotIndex);
        int j = RealmsConfigureWorldScreen.row(5) + 5;
        RealmsWorldSlotButton realmsWorldSlotButton = new RealmsWorldSlotButton(i, j, 80, 80, () -> this.server, text -> {
            this.toolTip = text;
        }, slotIndex, buttonWidget -> {
            RealmsWorldSlotButton.State state = ((RealmsWorldSlotButton)buttonWidget).getState();
            if (state != null) {
                switch (state.action) {
                    case NOTHING: {
                        break;
                    }
                    case JOIN: {
                        this.joinRealm(this.server);
                        break;
                    }
                    case SWITCH_SLOT: {
                        if (state.minigame) {
                            this.switchToMinigame();
                            break;
                        }
                        if (state.empty) {
                            this.switchToEmptySlot(slotIndex, this.server);
                            break;
                        }
                        this.switchToFullSlot(slotIndex, this.server);
                        break;
                    }
                    default: {
                        throw new IllegalStateException("Unknown action " + (Object)((Object)state.action));
                    }
                }
            }
        });
        this.addButton(realmsWorldSlotButton);
    }

    private int buttonLeft(int i) {
        return this.left_x + i * 95;
    }

    private int buttonCenter(int i, int total) {
        return this.width / 2 - (total * 105 - 5) / 2 + i * 105;
    }

    @Override
    public void tick() {
        super.tick();
        ++this.animTick;
        --this.clicks;
        if (this.clicks < 0) {
            this.clicks = 0;
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.toolTip = null;
        this.renderBackground(matrices);
        RealmsConfigureWorldScreen.drawCenteredText(matrices, this.textRenderer, field_26479, this.width / 2, RealmsConfigureWorldScreen.row(4), 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
        if (this.server == null) {
            RealmsConfigureWorldScreen.drawCenteredText(matrices, this.textRenderer, field_26480, this.width / 2, 17, 0xFFFFFF);
            return;
        }
        String string = this.server.getName();
        int i = this.textRenderer.getWidth(string);
        int j = this.server.state == RealmsServer.State.CLOSED ? 0xA0A0A0 : 0x7FFF7F;
        int k = this.textRenderer.getWidth(field_26480);
        RealmsConfigureWorldScreen.drawCenteredText(matrices, this.textRenderer, field_26480, this.width / 2, 12, 0xFFFFFF);
        RealmsConfigureWorldScreen.drawCenteredText(matrices, this.textRenderer, string, this.width / 2, 24, j);
        int l = Math.min(this.buttonCenter(2, 3) + 80 - 11, this.width / 2 + i / 2 + k / 2 + 10);
        this.drawServerStatus(matrices, l, 7, mouseX, mouseY);
        if (this.isMinigame()) {
            this.textRenderer.draw(matrices, field_26481.shallowCopy().append(this.server.getMinigameName()), (float)(this.left_x + 80 + 20 + 10), (float)RealmsConfigureWorldScreen.row(13), 0xFFFFFF);
        }
        if (this.toolTip != null) {
            this.renderMousehoverTooltip(matrices, this.toolTip, mouseX, mouseY);
        }
    }

    private int frame(int ordinal) {
        return this.left_x + (ordinal - 1) * 98;
    }

    @Override
    public void removed() {
        this.client.keyboard.setRepeatEvents(false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.backButtonClicked();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void backButtonClicked() {
        if (this.stateChanged) {
            this.parent.removeSelection();
        }
        this.client.openScreen(this.parent);
    }

    private void fetchServerData(long worldId) {
        new Thread(() -> {
            RealmsClient realmsClient = RealmsClient.createRealmsClient();
            try {
                this.server = realmsClient.getOwnWorld(worldId);
                this.disableButtons();
                if (this.isMinigame()) {
                    this.addButton(this.switchMinigameButton);
                } else {
                    this.addButton(this.optionsButton);
                    this.addButton(this.backupButton);
                    this.addButton(this.resetWorldButton);
                }
            }
            catch (RealmsServiceException realmsServiceException) {
                LOGGER.error("Couldn't get own world");
                this.client.execute(() -> this.client.openScreen(new RealmsGenericErrorScreen(Text.of(realmsServiceException.getMessage()), (Screen)this.parent)));
            }
        }).start();
    }

    private void disableButtons() {
        this.playersButton.active = !this.server.expired;
        this.settingsButton.active = !this.server.expired;
        this.subscriptionButton.active = true;
        this.switchMinigameButton.active = !this.server.expired;
        this.optionsButton.active = !this.server.expired;
        this.resetWorldButton.active = !this.server.expired;
    }

    private void joinRealm(RealmsServer serverData) {
        if (this.server.state == RealmsServer.State.OPEN) {
            this.parent.play(serverData, new RealmsConfigureWorldScreen(this.parent.newScreen(), this.serverId));
        } else {
            this.openTheWorld(true, new RealmsConfigureWorldScreen(this.parent.newScreen(), this.serverId));
        }
    }

    private void switchToMinigame() {
        RealmsSelectWorldTemplateScreen realmsSelectWorldTemplateScreen = new RealmsSelectWorldTemplateScreen(this, RealmsServer.WorldType.MINIGAME);
        realmsSelectWorldTemplateScreen.setTitle(new TranslatableText("mco.template.title.minigame"));
        realmsSelectWorldTemplateScreen.setWarning(new TranslatableText("mco.minigame.world.info.line1"), new TranslatableText("mco.minigame.world.info.line2"));
        this.client.openScreen(realmsSelectWorldTemplateScreen);
    }

    private void switchToFullSlot(int selectedSlot, RealmsServer serverData) {
        TranslatableText text = new TranslatableText("mco.configure.world.slot.switch.question.line1");
        TranslatableText text2 = new TranslatableText("mco.configure.world.slot.switch.question.line2");
        this.client.openScreen(new RealmsLongConfirmationScreen(bl -> {
            if (bl) {
                this.client.openScreen(new RealmsLongRunningMcoTaskScreen(this.parent, new SwitchSlotTask(realmsServer.id, selectedSlot, () -> this.client.openScreen(this.getNewScreen()))));
            } else {
                this.client.openScreen(this);
            }
        }, RealmsLongConfirmationScreen.Type.Info, text, text2, true));
    }

    private void switchToEmptySlot(int selectedSlot, RealmsServer serverData) {
        TranslatableText text = new TranslatableText("mco.configure.world.slot.switch.question.line1");
        TranslatableText text2 = new TranslatableText("mco.configure.world.slot.switch.question.line2");
        this.client.openScreen(new RealmsLongConfirmationScreen(bl -> {
            if (bl) {
                RealmsResetWorldScreen realmsResetWorldScreen = new RealmsResetWorldScreen(this, serverData, new TranslatableText("mco.configure.world.switch.slot"), new TranslatableText("mco.configure.world.switch.slot.subtitle"), 0xA0A0A0, ScreenTexts.CANCEL, () -> this.client.openScreen(this.getNewScreen()), () -> this.client.openScreen(this.getNewScreen()));
                realmsResetWorldScreen.setSlot(selectedSlot);
                realmsResetWorldScreen.setResetTitle(new TranslatableText("mco.create.world.reset.title"));
                this.client.openScreen(realmsResetWorldScreen);
            } else {
                this.client.openScreen(this);
            }
        }, RealmsLongConfirmationScreen.Type.Info, text, text2, true));
    }

    protected void renderMousehoverTooltip(MatrixStack matrices, @Nullable Text text, int i, int j) {
        int k = i + 12;
        int l = j - 12;
        int m = this.textRenderer.getWidth(text);
        if (k + m + 3 > this.right_x) {
            k = k - m - 20;
        }
        this.fillGradient(matrices, k - 3, l - 3, k + m + 3, l + 8 + 3, -1073741824, -1073741824);
        this.textRenderer.drawWithShadow(matrices, text, (float)k, (float)l, 0xFFFFFF);
    }

    private void drawServerStatus(MatrixStack matrices, int i, int j, int k, int l) {
        if (this.server.expired) {
            this.drawExpired(matrices, i, j, k, l);
        } else if (this.server.state == RealmsServer.State.CLOSED) {
            this.drawClosed(matrices, i, j, k, l);
        } else if (this.server.state == RealmsServer.State.OPEN) {
            if (this.server.daysLeft < 7) {
                this.drawExpiring(matrices, i, j, k, l, this.server.daysLeft);
            } else {
                this.drawOpen(matrices, i, j, k, l);
            }
        }
    }

    private void drawExpired(MatrixStack matrices, int i, int j, int k, int l) {
        this.client.getTextureManager().bindTexture(EXPIRED_ICON);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        DrawableHelper.drawTexture(matrices, i, j, 0.0f, 0.0f, 10, 28, 10, 28);
        if (k >= i && k <= i + 9 && l >= j && l <= j + 27) {
            this.toolTip = field_26482;
        }
    }

    private void drawExpiring(MatrixStack matrices, int i, int j, int k, int l, int m) {
        this.client.getTextureManager().bindTexture(EXPIRES_SOON_ICON);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (this.animTick % 20 < 10) {
            DrawableHelper.drawTexture(matrices, i, j, 0.0f, 0.0f, 10, 28, 20, 28);
        } else {
            DrawableHelper.drawTexture(matrices, i, j, 10.0f, 0.0f, 10, 28, 20, 28);
        }
        if (k >= i && k <= i + 9 && l >= j && l <= j + 27) {
            this.toolTip = m <= 0 ? field_26483 : (m == 1 ? field_26484 : new TranslatableText("mco.selectServer.expires.days", m));
        }
    }

    private void drawOpen(MatrixStack matrices, int i, int j, int k, int l) {
        this.client.getTextureManager().bindTexture(ON_ICON);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        DrawableHelper.drawTexture(matrices, i, j, 0.0f, 0.0f, 10, 28, 10, 28);
        if (k >= i && k <= i + 9 && l >= j && l <= j + 27) {
            this.toolTip = field_26485;
        }
    }

    private void drawClosed(MatrixStack matrices, int i, int j, int k, int l) {
        this.client.getTextureManager().bindTexture(OFF_ICON);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        DrawableHelper.drawTexture(matrices, i, j, 0.0f, 0.0f, 10, 28, 10, 28);
        if (k >= i && k <= i + 9 && l >= j && l <= j + 27) {
            this.toolTip = field_26486;
        }
    }

    private boolean isMinigame() {
        return this.server != null && this.server.worldType == RealmsServer.WorldType.MINIGAME;
    }

    private void hideRegularButtons() {
        this.removeButton(this.optionsButton);
        this.removeButton(this.backupButton);
        this.removeButton(this.resetWorldButton);
    }

    private void removeButton(ButtonWidget button) {
        button.visible = false;
        this.children.remove(button);
        this.buttons.remove(button);
    }

    private void addButton(ButtonWidget button) {
        button.visible = true;
        this.addButton(button);
    }

    private void hideMinigameButtons() {
        this.removeButton(this.switchMinigameButton);
    }

    public void saveSlotSettings(RealmsWorldOptions options) {
        RealmsWorldOptions realmsWorldOptions = this.server.slots.get(this.server.activeSlot);
        options.templateId = realmsWorldOptions.templateId;
        options.templateImage = realmsWorldOptions.templateImage;
        RealmsClient realmsClient = RealmsClient.createRealmsClient();
        try {
            realmsClient.updateSlot(this.server.id, this.server.activeSlot, options);
            this.server.slots.put(this.server.activeSlot, options);
        }
        catch (RealmsServiceException realmsServiceException) {
            LOGGER.error("Couldn't save slot settings");
            this.client.openScreen(new RealmsGenericErrorScreen(realmsServiceException, (Screen)this));
            return;
        }
        this.client.openScreen(this);
    }

    public void saveSettings(String name, String desc) {
        String string = desc.trim().isEmpty() ? null : desc;
        RealmsClient realmsClient = RealmsClient.createRealmsClient();
        try {
            realmsClient.update(this.server.id, name, string);
            this.server.setName(name);
            this.server.setDescription(string);
        }
        catch (RealmsServiceException realmsServiceException) {
            LOGGER.error("Couldn't save settings");
            this.client.openScreen(new RealmsGenericErrorScreen(realmsServiceException, (Screen)this));
            return;
        }
        this.client.openScreen(this);
    }

    public void openTheWorld(boolean join, Screen screen) {
        this.client.openScreen(new RealmsLongRunningMcoTaskScreen(screen, new OpenServerTask(this.server, this, this.parent, join)));
    }

    public void closeTheWorld(Screen screen) {
        this.client.openScreen(new RealmsLongRunningMcoTaskScreen(screen, new CloseServerTask(this.server, this)));
    }

    public void stateChanged() {
        this.stateChanged = true;
    }

    @Override
    protected void callback(@Nullable WorldTemplate template) {
        if (template == null) {
            return;
        }
        if (WorldTemplate.WorldTemplateType.MINIGAME == template.type) {
            this.client.openScreen(new RealmsLongRunningMcoTaskScreen(this.parent, new SwitchMinigameTask(this.server.id, template, this.getNewScreen())));
        }
    }

    public RealmsConfigureWorldScreen getNewScreen() {
        return new RealmsConfigureWorldScreen(this.parent, this.serverId);
    }
}

