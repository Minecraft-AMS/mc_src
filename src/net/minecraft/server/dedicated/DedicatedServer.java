/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.dedicated;

import net.minecraft.server.dedicated.ServerPropertiesHandler;

public interface DedicatedServer {
    public ServerPropertiesHandler getProperties();

    public String getHostname();

    public int getPort();

    public String getMotd();

    public String getVersion();

    public int getCurrentPlayerCount();

    public int getMaxPlayerCount();

    public String[] getPlayerNames();

    public String getLevelName();

    public String getPlugins();

    public String executeRconCommand(String var1);

    public boolean isDebuggingEnabled();

    public void info(String var1);

    public void warn(String var1);

    public void logError(String var1);

    public void log(String var1);
}

