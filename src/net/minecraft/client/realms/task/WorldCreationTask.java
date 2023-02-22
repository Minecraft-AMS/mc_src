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
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.text.Text;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class WorldCreationTask
extends LongRunningTask {
    private static final Logger field_36362 = LogUtils.getLogger();
    private final String name;
    private final String motd;
    private final long worldId;
    private final Screen lastScreen;

    public WorldCreationTask(long worldId, String name, String motd, Screen lastScreen) {
        this.worldId = worldId;
        this.name = name;
        this.motd = motd;
        this.lastScreen = lastScreen;
    }

    @Override
    public void run() {
        this.setTitle(Text.translatable("mco.create.world.wait"));
        RealmsClient realmsClient = RealmsClient.create();
        try {
            realmsClient.initializeWorld(this.worldId, this.name, this.motd);
            WorldCreationTask.setScreen(this.lastScreen);
        }
        catch (RealmsServiceException realmsServiceException) {
            field_36362.error("Couldn't create world");
            this.error(realmsServiceException.toString());
        }
        catch (Exception exception) {
            field_36362.error("Could not create world");
            this.error(exception.getLocalizedMessage());
        }
    }
}

