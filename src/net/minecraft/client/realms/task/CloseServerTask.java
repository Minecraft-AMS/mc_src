/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.task;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.exception.RetryCallException;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class CloseServerTask
extends LongRunningTask {
    private final RealmsServer serverData;
    private final RealmsConfigureWorldScreen configureScreen;

    public CloseServerTask(RealmsServer realmsServer, RealmsConfigureWorldScreen configureWorldScreen) {
        this.serverData = realmsServer;
        this.configureScreen = configureWorldScreen;
    }

    @Override
    public void run() {
        this.setTitle(new TranslatableText("mco.configure.world.closing"));
        RealmsClient realmsClient = RealmsClient.createRealmsClient();
        for (int i = 0; i < 25; ++i) {
            if (this.aborted()) {
                return;
            }
            try {
                boolean bl = realmsClient.close(this.serverData.id);
                if (!bl) continue;
                this.configureScreen.stateChanged();
                this.serverData.state = RealmsServer.State.CLOSED;
                CloseServerTask.setScreen(this.configureScreen);
                break;
            }
            catch (RetryCallException retryCallException) {
                if (this.aborted()) {
                    return;
                }
                CloseServerTask.pause(retryCallException.delaySeconds);
                continue;
            }
            catch (Exception exception) {
                if (this.aborted()) {
                    return;
                }
                LOGGER.error("Failed to close server", (Throwable)exception);
                this.error("Failed to close the server");
            }
        }
    }
}

