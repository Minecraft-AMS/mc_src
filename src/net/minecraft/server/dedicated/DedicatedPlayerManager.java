/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.logging.LogUtils
 *  org.slf4j.Logger
 */
package net.minecraft.server.dedicated;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.WorldSaveHandler;
import org.slf4j.Logger;

public class DedicatedPlayerManager
extends PlayerManager {
    private static final Logger LOGGER = LogUtils.getLogger();

    public DedicatedPlayerManager(MinecraftDedicatedServer server, DynamicRegistryManager.Immutable tracker, WorldSaveHandler saveHandler) {
        super(server, tracker, saveHandler, server.getProperties().maxPlayers);
        ServerPropertiesHandler serverPropertiesHandler = server.getProperties();
        this.setViewDistance(serverPropertiesHandler.viewDistance);
        this.setSimulationDistance(serverPropertiesHandler.simulationDistance);
        super.setWhitelistEnabled(serverPropertiesHandler.whiteList.get());
        this.loadUserBanList();
        this.saveUserBanList();
        this.loadIpBanList();
        this.saveIpBanList();
        this.loadOpList();
        this.loadWhitelist();
        this.saveOpList();
        if (!this.getWhitelist().getFile().exists()) {
            this.saveWhitelist();
        }
    }

    @Override
    public void setWhitelistEnabled(boolean whitelistEnabled) {
        super.setWhitelistEnabled(whitelistEnabled);
        this.getServer().setUseWhitelist(whitelistEnabled);
    }

    @Override
    public void addToOperators(GameProfile profile) {
        super.addToOperators(profile);
        this.saveOpList();
    }

    @Override
    public void removeFromOperators(GameProfile profile) {
        super.removeFromOperators(profile);
        this.saveOpList();
    }

    @Override
    public void reloadWhitelist() {
        this.loadWhitelist();
    }

    private void saveIpBanList() {
        try {
            this.getIpBanList().save();
        }
        catch (IOException iOException) {
            LOGGER.warn("Failed to save ip banlist: ", (Throwable)iOException);
        }
    }

    private void saveUserBanList() {
        try {
            this.getUserBanList().save();
        }
        catch (IOException iOException) {
            LOGGER.warn("Failed to save user banlist: ", (Throwable)iOException);
        }
    }

    private void loadIpBanList() {
        try {
            this.getIpBanList().load();
        }
        catch (IOException iOException) {
            LOGGER.warn("Failed to load ip banlist: ", (Throwable)iOException);
        }
    }

    private void loadUserBanList() {
        try {
            this.getUserBanList().load();
        }
        catch (IOException iOException) {
            LOGGER.warn("Failed to load user banlist: ", (Throwable)iOException);
        }
    }

    private void loadOpList() {
        try {
            this.getOpList().load();
        }
        catch (Exception exception) {
            LOGGER.warn("Failed to load operators list: ", (Throwable)exception);
        }
    }

    private void saveOpList() {
        try {
            this.getOpList().save();
        }
        catch (Exception exception) {
            LOGGER.warn("Failed to save operators list: ", (Throwable)exception);
        }
    }

    private void loadWhitelist() {
        try {
            this.getWhitelist().load();
        }
        catch (Exception exception) {
            LOGGER.warn("Failed to load white-list: ", (Throwable)exception);
        }
    }

    private void saveWhitelist() {
        try {
            this.getWhitelist().save();
        }
        catch (Exception exception) {
            LOGGER.warn("Failed to save white-list: ", (Throwable)exception);
        }
    }

    @Override
    public boolean isWhitelisted(GameProfile profile) {
        return !this.isWhitelistEnabled() || this.isOperator(profile) || this.getWhitelist().isAllowed(profile);
    }

    @Override
    public MinecraftDedicatedServer getServer() {
        return (MinecraftDedicatedServer)super.getServer();
    }

    @Override
    public boolean canBypassPlayerLimit(GameProfile profile) {
        return this.getOpList().canBypassPlayerLimit(profile);
    }

    @Override
    public /* synthetic */ MinecraftServer getServer() {
        return this.getServer();
    }
}

