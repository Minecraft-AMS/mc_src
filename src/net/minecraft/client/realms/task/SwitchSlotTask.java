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
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.exception.RetryCallException;
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.text.TranslatableText;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class SwitchSlotTask
extends LongRunningTask {
    private static final Logger field_36361 = LogUtils.getLogger();
    private final long worldId;
    private final int slot;
    private final Runnable callback;

    public SwitchSlotTask(long worldId, int slot, Runnable callback) {
        this.worldId = worldId;
        this.slot = slot;
        this.callback = callback;
    }

    @Override
    public void run() {
        RealmsClient realmsClient = RealmsClient.createRealmsClient();
        this.setTitle(new TranslatableText("mco.minigame.world.slot.screen.title"));
        for (int i = 0; i < 25; ++i) {
            try {
                if (this.aborted()) {
                    return;
                }
                if (!realmsClient.switchSlot(this.worldId, this.slot)) continue;
                this.callback.run();
                break;
            }
            catch (RetryCallException retryCallException) {
                if (this.aborted()) {
                    return;
                }
                SwitchSlotTask.pause(retryCallException.delaySeconds);
                continue;
            }
            catch (Exception exception) {
                if (this.aborted()) {
                    return;
                }
                field_36361.error("Couldn't switch world!");
                this.error(exception.toString());
            }
        }
    }
}

