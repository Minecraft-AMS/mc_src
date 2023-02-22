/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.dto.RealmsServerAddress;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.util.RealmsTasks;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsResourcePackScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private final RealmsScreen lastScreen;
    private final RealmsServerAddress serverAddress;
    private final ReentrantLock connectLock;

    public RealmsResourcePackScreen(RealmsScreen lastScreen, RealmsServerAddress serverAddress, ReentrantLock connectLock) {
        this.lastScreen = lastScreen;
        this.serverAddress = serverAddress;
        this.connectLock = connectLock;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void confirmResult(boolean result, int id) {
        try {
            if (!result) {
                Realms.setScreen(this.lastScreen);
            } else {
                try {
                    ((CompletableFuture)Realms.downloadResourcePack(this.serverAddress.resourcePackUrl, this.serverAddress.resourcePackHash).thenRun(() -> {
                        RealmsLongRunningMcoTaskScreen realmsLongRunningMcoTaskScreen = new RealmsLongRunningMcoTaskScreen(this.lastScreen, new RealmsTasks.RealmsConnectTask(this.lastScreen, this.serverAddress));
                        realmsLongRunningMcoTaskScreen.start();
                        Realms.setScreen(realmsLongRunningMcoTaskScreen);
                    })).exceptionally(throwable -> {
                        Realms.clearResourcePack();
                        LOGGER.error(throwable);
                        Realms.setScreen(new RealmsGenericErrorScreen("Failed to download resource pack!", this.lastScreen));
                        return null;
                    });
                }
                catch (Exception exception) {
                    Realms.clearResourcePack();
                    LOGGER.error((Object)exception);
                    Realms.setScreen(new RealmsGenericErrorScreen("Failed to download resource pack!", this.lastScreen));
                }
            }
        }
        finally {
            if (this.connectLock != null && this.connectLock.isHeldByCurrentThread()) {
                this.connectLock.unlock();
            }
        }
    }
}

