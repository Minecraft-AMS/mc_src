/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.realms.task;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.realms.util.Errable;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public abstract class LongRunningTask
implements Errable,
Runnable {
    protected static final int MAX_RETRIES = 25;
    public static final Logger LOGGER = LogManager.getLogger();
    protected RealmsLongRunningMcoTaskScreen longRunningMcoTaskScreen;

    protected static void pause(long seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        }
        catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            LOGGER.error("", (Throwable)interruptedException);
        }
    }

    public static void setScreen(Screen screen) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        minecraftClient.execute(() -> minecraftClient.setScreen(screen));
    }

    public void setScreen(RealmsLongRunningMcoTaskScreen longRunningMcoTaskScreen) {
        this.longRunningMcoTaskScreen = longRunningMcoTaskScreen;
    }

    @Override
    public void error(Text errorMessage) {
        this.longRunningMcoTaskScreen.error(errorMessage);
    }

    public void setTitle(Text title) {
        this.longRunningMcoTaskScreen.setTitle(title);
    }

    public boolean aborted() {
        return this.longRunningMcoTaskScreen.aborted();
    }

    public void tick() {
    }

    public void init() {
    }

    public void abortTask() {
    }
}

