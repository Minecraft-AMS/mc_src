/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.realms.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsPeriodicCheckers;
import net.minecraft.client.realms.dto.RealmsNews;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.realms.util.PeriodicRunnerFactory;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RealmsNotificationsScreen
extends RealmsScreen {
    private static final Identifier INVITE_ICON = new Identifier("realms", "textures/gui/realms/invite_icon.png");
    private static final Identifier TRIAL_ICON = new Identifier("realms", "textures/gui/realms/trial_icon.png");
    private static final Identifier NEWS_NOTIFICATION = new Identifier("realms", "textures/gui/realms/news_notification_mainscreen.png");
    @Nullable
    private PeriodicRunnerFactory.RunnersManager periodicRunnersManager;
    private volatile int pendingInvitesCount;
    static boolean checkedMcoAvailability;
    private static boolean trialAvailable;
    static boolean validClient;
    private static boolean hasUnreadNews;

    public RealmsNotificationsScreen() {
        super(NarratorManager.EMPTY);
    }

    @Override
    public void init() {
        this.checkIfMcoEnabled();
        this.client.keyboard.setRepeatEvents(true);
        if (this.periodicRunnersManager != null) {
            this.periodicRunnersManager.forceRunListeners();
        }
    }

    @Override
    public void tick() {
        boolean bl;
        boolean bl2 = bl = this.shouldShowNotifications() && this.isTitleScreen() && validClient;
        if (this.periodicRunnersManager == null && bl) {
            this.periodicRunnersManager = this.createPeriodicRunnersManager(this.client.getRealmsPeriodicCheckers());
        } else if (this.periodicRunnersManager != null && !bl) {
            this.periodicRunnersManager = null;
        }
        if (this.periodicRunnersManager != null) {
            this.periodicRunnersManager.runAll();
        }
    }

    private PeriodicRunnerFactory.RunnersManager createPeriodicRunnersManager(RealmsPeriodicCheckers periodicCheckers) {
        PeriodicRunnerFactory.RunnersManager runnersManager = periodicCheckers.runnerFactory.create();
        runnersManager.add(periodicCheckers.pendingInvitesCount, pendingInvitesCount -> {
            this.pendingInvitesCount = pendingInvitesCount;
        });
        runnersManager.add(periodicCheckers.trialAvailability, trialAvailable -> {
            RealmsNotificationsScreen.trialAvailable = trialAvailable;
        });
        runnersManager.add(periodicCheckers.news, news -> {
            realmsPeriodicCheckers.newsUpdater.updateNews((RealmsNews)news);
            hasUnreadNews = realmsPeriodicCheckers.newsUpdater.hasUnreadNews();
        });
        return runnersManager;
    }

    private boolean shouldShowNotifications() {
        return this.client.options.getRealmsNotifications().getValue();
    }

    private boolean isTitleScreen() {
        return this.client.currentScreen instanceof TitleScreen;
    }

    private void checkIfMcoEnabled() {
        if (!checkedMcoAvailability) {
            checkedMcoAvailability = true;
            new Thread("Realms Notification Availability checker #1"){

                @Override
                public void run() {
                    RealmsClient realmsClient = RealmsClient.create();
                    try {
                        RealmsClient.CompatibleVersionResponse compatibleVersionResponse = realmsClient.clientCompatible();
                        if (compatibleVersionResponse != RealmsClient.CompatibleVersionResponse.COMPATIBLE) {
                            return;
                        }
                    }
                    catch (RealmsServiceException realmsServiceException) {
                        if (realmsServiceException.httpResultCode != 401) {
                            checkedMcoAvailability = false;
                        }
                        return;
                    }
                    validClient = true;
                }
            }.start();
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (validClient) {
            this.drawIcons(matrices, mouseX, mouseY);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    private void drawIcons(MatrixStack matrices, int mouseX, int mouseY) {
        int i = this.pendingInvitesCount;
        int j = 24;
        int k = this.height / 4 + 48;
        int l = this.width / 2 + 80;
        int m = k + 48 + 2;
        int n = 0;
        if (hasUnreadNews) {
            RenderSystem.setShaderTexture(0, NEWS_NOTIFICATION);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            matrices.push();
            matrices.scale(0.4f, 0.4f, 0.4f);
            DrawableHelper.drawTexture(matrices, (int)((double)(l + 2 - n) * 2.5), (int)((double)m * 2.5), 0.0f, 0.0f, 40, 40, 40, 40);
            matrices.pop();
            n += 14;
        }
        if (i != 0) {
            RenderSystem.setShaderTexture(0, INVITE_ICON);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            DrawableHelper.drawTexture(matrices, l - n, m - 6, 0.0f, 0.0f, 15, 25, 31, 25);
            n += 16;
        }
        if (trialAvailable) {
            RenderSystem.setShaderTexture(0, TRIAL_ICON);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            int o = 0;
            if ((Util.getMeasuringTimeMs() / 800L & 1L) == 1L) {
                o = 8;
            }
            DrawableHelper.drawTexture(matrices, l + 4 - n, m + 4, 0.0f, o, 8, 8, 8, 16);
        }
    }
}

