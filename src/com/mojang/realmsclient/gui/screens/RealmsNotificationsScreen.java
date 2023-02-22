/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsScreen;

@Environment(value=EnvType.CLIENT)
public class RealmsNotificationsScreen
extends RealmsScreen {
    private static final RealmsDataFetcher realmsDataFetcher = new RealmsDataFetcher();
    private volatile int numberOfPendingInvites;
    private static boolean checkedMcoAvailability;
    private static boolean trialAvailable;
    private static boolean validClient;
    private static boolean hasUnreadNews;
    private static final List<RealmsDataFetcher.Task> tasks;

    public RealmsNotificationsScreen(RealmsScreen lastScreen) {
    }

    @Override
    public void init() {
        this.checkIfMcoEnabled();
        this.setKeyboardHandlerSendRepeatsToGui(true);
    }

    @Override
    public void tick() {
        if (!(Realms.getRealmsNotificationsEnabled() && Realms.inTitleScreen() && validClient || realmsDataFetcher.isStopped())) {
            realmsDataFetcher.stop();
            return;
        }
        if (!validClient || !Realms.getRealmsNotificationsEnabled()) {
            return;
        }
        realmsDataFetcher.initWithSpecificTaskList(tasks);
        if (realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.PENDING_INVITE)) {
            this.numberOfPendingInvites = realmsDataFetcher.getPendingInvitesCount();
        }
        if (realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.TRIAL_AVAILABLE)) {
            trialAvailable = realmsDataFetcher.isTrialAvailable();
        }
        if (realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.UNREAD_NEWS)) {
            hasUnreadNews = realmsDataFetcher.hasUnreadNews();
        }
        realmsDataFetcher.markClean();
    }

    private void checkIfMcoEnabled() {
        if (!checkedMcoAvailability) {
            checkedMcoAvailability = true;
            new Thread("Realms Notification Availability checker #1"){

                @Override
                public void run() {
                    RealmsClient realmsClient = RealmsClient.createRealmsClient();
                    try {
                        RealmsClient.CompatibleVersionResponse compatibleVersionResponse = realmsClient.clientCompatible();
                        if (!compatibleVersionResponse.equals((Object)RealmsClient.CompatibleVersionResponse.COMPATIBLE)) {
                            return;
                        }
                    }
                    catch (RealmsServiceException realmsServiceException) {
                        if (realmsServiceException.httpResultCode != 401) {
                            checkedMcoAvailability = false;
                        }
                        return;
                    }
                    catch (IOException iOException) {
                        checkedMcoAvailability = false;
                        return;
                    }
                    validClient = true;
                }
            }.start();
        }
    }

    @Override
    public void render(int xm, int ym, float a) {
        if (validClient) {
            this.drawIcons(xm, ym);
        }
        super.render(xm, ym, a);
    }

    @Override
    public boolean mouseClicked(double xm, double ym, int button) {
        return super.mouseClicked(xm, ym, button);
    }

    private void drawIcons(int xm, int ym) {
        int i = this.numberOfPendingInvites;
        int j = 24;
        int k = this.height() / 4 + 48;
        int l = this.width() / 2 + 80;
        int m = k + 48 + 2;
        int n = 0;
        if (hasUnreadNews) {
            RealmsScreen.bind("realms:textures/gui/realms/news_notification_mainscreen.png");
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.4f, 0.4f, 0.4f);
            RealmsScreen.blit((int)((double)(l + 2 - n) * 2.5), (int)((double)m * 2.5), 0.0f, 0.0f, 40, 40, 40, 40);
            GlStateManager.popMatrix();
            n += 14;
        }
        if (i != 0) {
            RealmsScreen.bind("realms:textures/gui/realms/invite_icon.png");
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.pushMatrix();
            RealmsScreen.blit(l - n, m - 6, 0.0f, 0.0f, 15, 25, 31, 25);
            GlStateManager.popMatrix();
            n += 16;
        }
        if (trialAvailable) {
            RealmsScreen.bind("realms:textures/gui/realms/trial_icon.png");
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.pushMatrix();
            int o = 0;
            if ((System.currentTimeMillis() / 800L & 1L) == 1L) {
                o = 8;
            }
            RealmsScreen.blit(l + 4 - n, m + 4, 0.0f, o, 8, 8, 8, 16);
            GlStateManager.popMatrix();
        }
    }

    @Override
    public void removed() {
        realmsDataFetcher.stop();
    }

    static {
        tasks = Arrays.asList(RealmsDataFetcher.Task.PENDING_INVITE, RealmsDataFetcher.Task.TRIAL_AVAILABLE, RealmsDataFetcher.Task.UNREAD_NEWS);
    }
}

