/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.Realms;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsWorldOptions;
import net.minecraft.client.realms.dto.WorldDownload;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.RealmsWorldSlotButton;
import net.minecraft.client.realms.gui.screen.RealmsDownloadLatestWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen;
import net.minecraft.client.realms.gui.screen.RealmsLongConfirmationScreen;
import net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.gui.screen.RealmsResetWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.realms.task.OpenServerTask;
import net.minecraft.client.realms.task.SwitchSlotTask;
import net.minecraft.client.realms.util.RealmsTextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsBrokenWorldScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Screen parent;
    private final RealmsMainScreen mainScreen;
    private RealmsServer field_20492;
    private final long serverId;
    private final Text field_24204;
    private final Text[] message = new Text[]{new TranslatableText("mco.brokenworld.message.line1"), new TranslatableText("mco.brokenworld.message.line2")};
    private int left_x;
    private int right_x;
    private final List<Integer> slotsThatHasBeenDownloaded = Lists.newArrayList();
    private int animTick;

    public RealmsBrokenWorldScreen(Screen parent, RealmsMainScreen mainScreen, long serverId, boolean bl) {
        this.parent = parent;
        this.mainScreen = mainScreen;
        this.serverId = serverId;
        this.field_24204 = bl ? new TranslatableText("mco.brokenworld.minigame.title") : new TranslatableText("mco.brokenworld.title");
    }

    @Override
    public void init() {
        this.left_x = this.width / 2 - 150;
        this.right_x = this.width / 2 + 190;
        this.addButton(new ButtonWidget(this.right_x - 80 + 8, RealmsBrokenWorldScreen.row(13) - 5, 70, 20, ScreenTexts.BACK, buttonWidget -> this.backButtonClicked()));
        if (this.field_20492 == null) {
            this.fetchServerData(this.serverId);
        } else {
            this.addButtons();
        }
        this.client.keyboard.setRepeatEvents(true);
        Realms.narrateNow(Stream.concat(Stream.of(this.field_24204), Stream.of(this.message)).map(Text::getString).collect(Collectors.joining(" ")));
    }

    private void addButtons() {
        for (Map.Entry<Integer, RealmsWorldOptions> entry : this.field_20492.slots.entrySet()) {
            int i = entry.getKey();
            boolean bl = i != this.field_20492.activeSlot || this.field_20492.worldType == RealmsServer.WorldType.MINIGAME;
            ButtonWidget buttonWidget2 = bl ? new ButtonWidget(this.getFramePositionX(i), RealmsBrokenWorldScreen.row(8), 80, 20, new TranslatableText("mco.brokenworld.play"), buttonWidget -> {
                if (this.field_20492.slots.get((Object)Integer.valueOf((int)i)).empty) {
                    RealmsResetWorldScreen realmsResetWorldScreen = new RealmsResetWorldScreen(this, this.field_20492, new TranslatableText("mco.configure.world.switch.slot"), new TranslatableText("mco.configure.world.switch.slot.subtitle"), 0xA0A0A0, ScreenTexts.CANCEL, this::method_25123, () -> {
                        this.client.openScreen(this);
                        this.method_25123();
                    });
                    realmsResetWorldScreen.setSlot(i);
                    realmsResetWorldScreen.setResetTitle(new TranslatableText("mco.create.world.reset.title"));
                    this.client.openScreen(realmsResetWorldScreen);
                } else {
                    this.client.openScreen(new RealmsLongRunningMcoTaskScreen(this.parent, new SwitchSlotTask(this.field_20492.id, i, this::method_25123)));
                }
            }) : new ButtonWidget(this.getFramePositionX(i), RealmsBrokenWorldScreen.row(8), 80, 20, new TranslatableText("mco.brokenworld.download"), buttonWidget -> {
                TranslatableText text = new TranslatableText("mco.configure.world.restore.download.question.line1");
                TranslatableText text2 = new TranslatableText("mco.configure.world.restore.download.question.line2");
                this.client.openScreen(new RealmsLongConfirmationScreen(bl -> {
                    if (bl) {
                        this.downloadWorld(i);
                    } else {
                        this.client.openScreen(this);
                    }
                }, RealmsLongConfirmationScreen.Type.Info, text, text2, true));
            });
            if (this.slotsThatHasBeenDownloaded.contains(i)) {
                buttonWidget2.active = false;
                buttonWidget2.setMessage(new TranslatableText("mco.brokenworld.downloaded"));
            }
            this.addButton(buttonWidget2);
            this.addButton(new ButtonWidget(this.getFramePositionX(i), RealmsBrokenWorldScreen.row(10), 80, 20, new TranslatableText("mco.brokenworld.reset"), buttonWidget -> {
                RealmsResetWorldScreen realmsResetWorldScreen = new RealmsResetWorldScreen(this, this.field_20492, this::method_25123, () -> {
                    this.client.openScreen(this);
                    this.method_25123();
                });
                if (i != this.field_20492.activeSlot || this.field_20492.worldType == RealmsServer.WorldType.MINIGAME) {
                    realmsResetWorldScreen.setSlot(i);
                }
                this.client.openScreen(realmsResetWorldScreen);
            }));
        }
    }

    @Override
    public void tick() {
        ++this.animTick;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        RealmsBrokenWorldScreen.drawCenteredText(matrices, this.textRenderer, this.field_24204, this.width / 2, 17, 0xFFFFFF);
        for (int i = 0; i < this.message.length; ++i) {
            RealmsBrokenWorldScreen.drawCenteredText(matrices, this.textRenderer, this.message[i], this.width / 2, RealmsBrokenWorldScreen.row(-1) + 3 + i * 12, 0xA0A0A0);
        }
        if (this.field_20492 == null) {
            return;
        }
        for (Map.Entry<Integer, RealmsWorldOptions> entry : this.field_20492.slots.entrySet()) {
            if (entry.getValue().templateImage != null && entry.getValue().templateId != -1L) {
                this.drawSlotFrame(matrices, this.getFramePositionX(entry.getKey()), RealmsBrokenWorldScreen.row(1) + 5, mouseX, mouseY, this.field_20492.activeSlot == entry.getKey() && !this.isMinigame(), entry.getValue().getSlotName(entry.getKey()), entry.getKey(), entry.getValue().templateId, entry.getValue().templateImage, entry.getValue().empty);
                continue;
            }
            this.drawSlotFrame(matrices, this.getFramePositionX(entry.getKey()), RealmsBrokenWorldScreen.row(1) + 5, mouseX, mouseY, this.field_20492.activeSlot == entry.getKey() && !this.isMinigame(), entry.getValue().getSlotName(entry.getKey()), entry.getKey(), -1L, null, entry.getValue().empty);
        }
    }

    private int getFramePositionX(int i) {
        return this.left_x + (i - 1) * 110;
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
        this.client.openScreen(this.parent);
    }

    private void fetchServerData(long worldId) {
        new Thread(() -> {
            RealmsClient realmsClient = RealmsClient.createRealmsClient();
            try {
                this.field_20492 = realmsClient.getOwnWorld(worldId);
                this.addButtons();
            }
            catch (RealmsServiceException realmsServiceException) {
                LOGGER.error("Couldn't get own world");
                this.client.openScreen(new RealmsGenericErrorScreen(Text.of(realmsServiceException.getMessage()), this.parent));
            }
        }).start();
    }

    public void method_25123() {
        new Thread(() -> {
            RealmsClient realmsClient = RealmsClient.createRealmsClient();
            if (this.field_20492.state == RealmsServer.State.CLOSED) {
                this.client.execute(() -> this.client.openScreen(new RealmsLongRunningMcoTaskScreen(this, new OpenServerTask(this.field_20492, this, this.mainScreen, true))));
            } else {
                try {
                    this.mainScreen.newScreen().play(realmsClient.getOwnWorld(this.serverId), this);
                }
                catch (RealmsServiceException realmsServiceException) {
                    LOGGER.error("Couldn't get own world");
                    this.client.execute(() -> this.client.openScreen(this.parent));
                }
            }
        }).start();
    }

    private void downloadWorld(int slotId) {
        RealmsClient realmsClient = RealmsClient.createRealmsClient();
        try {
            WorldDownload worldDownload = realmsClient.download(this.field_20492.id, slotId);
            RealmsDownloadLatestWorldScreen realmsDownloadLatestWorldScreen = new RealmsDownloadLatestWorldScreen(this, worldDownload, this.field_20492.getWorldName(slotId), bl -> {
                if (bl) {
                    this.slotsThatHasBeenDownloaded.add(slotId);
                    this.children.clear();
                    this.addButtons();
                } else {
                    this.client.openScreen(this);
                }
            });
            this.client.openScreen(realmsDownloadLatestWorldScreen);
        }
        catch (RealmsServiceException realmsServiceException) {
            LOGGER.error("Couldn't download world data");
            this.client.openScreen(new RealmsGenericErrorScreen(realmsServiceException, (Screen)this));
        }
    }

    private boolean isMinigame() {
        return this.field_20492 != null && this.field_20492.worldType == RealmsServer.WorldType.MINIGAME;
    }

    private void drawSlotFrame(MatrixStack matrices, int y, int xm, int ym, int i, boolean bl, String string, int j, long l, String string2, boolean bl2) {
        if (bl2) {
            this.client.getTextureManager().bindTexture(RealmsWorldSlotButton.EMPTY_FRAME);
        } else if (string2 != null && l != -1L) {
            RealmsTextureManager.bindWorldTemplate(String.valueOf(l), string2);
        } else if (j == 1) {
            this.client.getTextureManager().bindTexture(RealmsWorldSlotButton.PANORAMA_0);
        } else if (j == 2) {
            this.client.getTextureManager().bindTexture(RealmsWorldSlotButton.PANORAMA_2);
        } else if (j == 3) {
            this.client.getTextureManager().bindTexture(RealmsWorldSlotButton.PANORAMA_3);
        } else {
            RealmsTextureManager.bindWorldTemplate(String.valueOf(this.field_20492.minigameId), this.field_20492.minigameImage);
        }
        if (!bl) {
            RenderSystem.color4f(0.56f, 0.56f, 0.56f, 1.0f);
        } else if (bl) {
            float f = 0.9f + 0.1f * MathHelper.cos((float)this.animTick * 0.2f);
            RenderSystem.color4f(f, f, f, 1.0f);
        }
        DrawableHelper.drawTexture(matrices, y + 3, xm + 3, 0.0f, 0.0f, 74, 74, 74, 74);
        this.client.getTextureManager().bindTexture(RealmsWorldSlotButton.SLOT_FRAME);
        if (bl) {
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        } else {
            RenderSystem.color4f(0.56f, 0.56f, 0.56f, 1.0f);
        }
        DrawableHelper.drawTexture(matrices, y, xm, 0.0f, 0.0f, 80, 80, 80, 80);
        RealmsBrokenWorldScreen.drawCenteredText(matrices, this.textRenderer, string, y + 40, xm + 66, 0xFFFFFF);
    }
}

