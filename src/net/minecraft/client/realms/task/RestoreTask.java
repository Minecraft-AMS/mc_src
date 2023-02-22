/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.task;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.Backup;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.exception.RetryCallException;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen;
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.text.Text;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RestoreTask
extends LongRunningTask {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Backup backup;
    private final long worldId;
    private final RealmsConfigureWorldScreen lastScreen;

    public RestoreTask(Backup backup, long worldId, RealmsConfigureWorldScreen lastScreen) {
        this.backup = backup;
        this.worldId = worldId;
        this.lastScreen = lastScreen;
    }

    @Override
    public void run() {
        this.setTitle(Text.translatable("mco.backup.restoring"));
        RealmsClient realmsClient = RealmsClient.create();
        for (int i = 0; i < 25; ++i) {
            try {
                if (this.aborted()) {
                    return;
                }
                realmsClient.restoreWorld(this.worldId, this.backup.backupId);
                RestoreTask.pause(1L);
                if (this.aborted()) {
                    return;
                }
                RestoreTask.setScreen(this.lastScreen.getNewScreen());
                return;
            }
            catch (RetryCallException retryCallException) {
                if (this.aborted()) {
                    return;
                }
                RestoreTask.pause(retryCallException.delaySeconds);
                continue;
            }
            catch (RealmsServiceException realmsServiceException) {
                if (this.aborted()) {
                    return;
                }
                LOGGER.error("Couldn't restore backup", (Throwable)realmsServiceException);
                RestoreTask.setScreen(new RealmsGenericErrorScreen(realmsServiceException, (Screen)this.lastScreen));
                return;
            }
            catch (Exception exception) {
                if (this.aborted()) {
                    return;
                }
                LOGGER.error("Couldn't restore backup", (Throwable)exception);
                this.error(exception.getLocalizedMessage());
                return;
            }
        }
    }
}

