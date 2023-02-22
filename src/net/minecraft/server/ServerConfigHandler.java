/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.io.Files
 *  com.mojang.authlib.Agent
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.ProfileLookupCallback
 *  com.mojang.authlib.yggdrasil.ProfileNotFoundException
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.yggdrasil.ProfileNotFoundException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.BanEntry;
import net.minecraft.server.BannedIpEntry;
import net.minecraft.server.BannedIpList;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.BannedPlayerList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.OperatorEntry;
import net.minecraft.server.OperatorList;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.Whitelist;
import net.minecraft.server.WhitelistEntry;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.util.ChatUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerConfigHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final File BANNED_IPS_FILE = new File("banned-ips.txt");
    public static final File BANNED_PLAYERS_FILE = new File("banned-players.txt");
    public static final File OPERATORS_FILE = new File("ops.txt");
    public static final File WHITE_LIST_FILE = new File("white-list.txt");

    static List<String> processSimpleListFile(File file, Map<String, String[]> valueMap) throws IOException {
        List list = Files.readLines((File)file, (Charset)StandardCharsets.UTF_8);
        for (String string : list) {
            if ((string = string.trim()).startsWith("#") || string.length() < 1) continue;
            String[] strings = string.split("\\|");
            valueMap.put(strings[0].toLowerCase(Locale.ROOT), strings);
        }
        return list;
    }

    private static void lookupProfile(MinecraftServer minecraftServer, Collection<String> bannedPlayers, ProfileLookupCallback callback) {
        String[] strings = (String[])bannedPlayers.stream().filter(string -> !ChatUtil.isEmpty(string)).toArray(String[]::new);
        if (minecraftServer.isOnlineMode()) {
            minecraftServer.getGameProfileRepo().findProfilesByNames(strings, Agent.MINECRAFT, callback);
        } else {
            for (String string2 : strings) {
                UUID uUID = PlayerEntity.getUuidFromProfile(new GameProfile(null, string2));
                GameProfile gameProfile = new GameProfile(uUID, string2);
                callback.onProfileLookupSucceeded(gameProfile);
            }
        }
    }

    public static boolean convertBannedPlayers(final MinecraftServer minecraftServer) {
        final BannedPlayerList bannedPlayerList = new BannedPlayerList(PlayerManager.BANNED_PLAYERS_FILE);
        if (BANNED_PLAYERS_FILE.exists() && BANNED_PLAYERS_FILE.isFile()) {
            if (bannedPlayerList.getFile().exists()) {
                try {
                    bannedPlayerList.load();
                }
                catch (FileNotFoundException fileNotFoundException) {
                    LOGGER.warn("Could not load existing file {}", (Object)bannedPlayerList.getFile().getName(), (Object)fileNotFoundException);
                }
            }
            try {
                final HashMap map = Maps.newHashMap();
                ServerConfigHandler.processSimpleListFile(BANNED_PLAYERS_FILE, map);
                ProfileLookupCallback profileLookupCallback = new ProfileLookupCallback(){

                    public void onProfileLookupSucceeded(GameProfile profile) {
                        minecraftServer.getUserCache().add(profile);
                        String[] strings = (String[])map.get(profile.getName().toLowerCase(Locale.ROOT));
                        if (strings == null) {
                            LOGGER.warn("Could not convert user banlist entry for {}", (Object)profile.getName());
                            throw new ServerConfigException("Profile not in the conversionlist");
                        }
                        Date date = strings.length > 1 ? ServerConfigHandler.stringToDate(strings[1], null) : null;
                        String string = strings.length > 2 ? strings[2] : null;
                        Date date2 = strings.length > 3 ? ServerConfigHandler.stringToDate(strings[3], null) : null;
                        String string2 = strings.length > 4 ? strings[4] : null;
                        bannedPlayerList.add(new BannedPlayerEntry(profile, date, string, date2, string2));
                    }

                    public void onProfileLookupFailed(GameProfile profile, Exception exception) {
                        LOGGER.warn("Could not lookup user banlist entry for {}", (Object)profile.getName(), (Object)exception);
                        if (!(exception instanceof ProfileNotFoundException)) {
                            throw new ServerConfigException("Could not request user " + profile.getName() + " from backend systems", exception);
                        }
                    }
                };
                ServerConfigHandler.lookupProfile(minecraftServer, map.keySet(), profileLookupCallback);
                bannedPlayerList.save();
                ServerConfigHandler.markFileConverted(BANNED_PLAYERS_FILE);
            }
            catch (IOException iOException) {
                LOGGER.warn("Could not read old user banlist to convert it!", (Throwable)iOException);
                return false;
            }
            catch (ServerConfigException serverConfigException) {
                LOGGER.error("Conversion failed, please try again later", (Throwable)serverConfigException);
                return false;
            }
            return true;
        }
        return true;
    }

    public static boolean convertBannedIps(MinecraftServer minecraftServer) {
        BannedIpList bannedIpList = new BannedIpList(PlayerManager.BANNED_IPS_FILE);
        if (BANNED_IPS_FILE.exists() && BANNED_IPS_FILE.isFile()) {
            if (bannedIpList.getFile().exists()) {
                try {
                    bannedIpList.load();
                }
                catch (FileNotFoundException fileNotFoundException) {
                    LOGGER.warn("Could not load existing file {}", (Object)bannedIpList.getFile().getName(), (Object)fileNotFoundException);
                }
            }
            try {
                HashMap map = Maps.newHashMap();
                ServerConfigHandler.processSimpleListFile(BANNED_IPS_FILE, map);
                for (String string : map.keySet()) {
                    String[] strings = (String[])map.get(string);
                    Date date = strings.length > 1 ? ServerConfigHandler.stringToDate(strings[1], null) : null;
                    String string2 = strings.length > 2 ? strings[2] : null;
                    Date date2 = strings.length > 3 ? ServerConfigHandler.stringToDate(strings[3], null) : null;
                    String string3 = strings.length > 4 ? strings[4] : null;
                    bannedIpList.add(new BannedIpEntry(string, date, string2, date2, string3));
                }
                bannedIpList.save();
                ServerConfigHandler.markFileConverted(BANNED_IPS_FILE);
            }
            catch (IOException iOException) {
                LOGGER.warn("Could not parse old ip banlist to convert it!", (Throwable)iOException);
                return false;
            }
            return true;
        }
        return true;
    }

    public static boolean convertOperators(final MinecraftServer minecraftServer) {
        final OperatorList operatorList = new OperatorList(PlayerManager.OPERATORS_FILE);
        if (OPERATORS_FILE.exists() && OPERATORS_FILE.isFile()) {
            if (operatorList.getFile().exists()) {
                try {
                    operatorList.load();
                }
                catch (FileNotFoundException fileNotFoundException) {
                    LOGGER.warn("Could not load existing file {}", (Object)operatorList.getFile().getName(), (Object)fileNotFoundException);
                }
            }
            try {
                List list = Files.readLines((File)OPERATORS_FILE, (Charset)StandardCharsets.UTF_8);
                ProfileLookupCallback profileLookupCallback = new ProfileLookupCallback(){

                    public void onProfileLookupSucceeded(GameProfile profile) {
                        minecraftServer.getUserCache().add(profile);
                        operatorList.add(new OperatorEntry(profile, minecraftServer.getOpPermissionLevel(), false));
                    }

                    public void onProfileLookupFailed(GameProfile profile, Exception exception) {
                        LOGGER.warn("Could not lookup oplist entry for {}", (Object)profile.getName(), (Object)exception);
                        if (!(exception instanceof ProfileNotFoundException)) {
                            throw new ServerConfigException("Could not request user " + profile.getName() + " from backend systems", exception);
                        }
                    }
                };
                ServerConfigHandler.lookupProfile(minecraftServer, list, profileLookupCallback);
                operatorList.save();
                ServerConfigHandler.markFileConverted(OPERATORS_FILE);
            }
            catch (IOException iOException) {
                LOGGER.warn("Could not read old oplist to convert it!", (Throwable)iOException);
                return false;
            }
            catch (ServerConfigException serverConfigException) {
                LOGGER.error("Conversion failed, please try again later", (Throwable)serverConfigException);
                return false;
            }
            return true;
        }
        return true;
    }

    public static boolean convertWhitelist(final MinecraftServer minecraftServer) {
        final Whitelist whitelist = new Whitelist(PlayerManager.WHITELIST_FILE);
        if (WHITE_LIST_FILE.exists() && WHITE_LIST_FILE.isFile()) {
            if (whitelist.getFile().exists()) {
                try {
                    whitelist.load();
                }
                catch (FileNotFoundException fileNotFoundException) {
                    LOGGER.warn("Could not load existing file {}", (Object)whitelist.getFile().getName(), (Object)fileNotFoundException);
                }
            }
            try {
                List list = Files.readLines((File)WHITE_LIST_FILE, (Charset)StandardCharsets.UTF_8);
                ProfileLookupCallback profileLookupCallback = new ProfileLookupCallback(){

                    public void onProfileLookupSucceeded(GameProfile profile) {
                        minecraftServer.getUserCache().add(profile);
                        whitelist.add(new WhitelistEntry(profile));
                    }

                    public void onProfileLookupFailed(GameProfile profile, Exception exception) {
                        LOGGER.warn("Could not lookup user whitelist entry for {}", (Object)profile.getName(), (Object)exception);
                        if (!(exception instanceof ProfileNotFoundException)) {
                            throw new ServerConfigException("Could not request user " + profile.getName() + " from backend systems", exception);
                        }
                    }
                };
                ServerConfigHandler.lookupProfile(minecraftServer, list, profileLookupCallback);
                whitelist.save();
                ServerConfigHandler.markFileConverted(WHITE_LIST_FILE);
            }
            catch (IOException iOException) {
                LOGGER.warn("Could not read old whitelist to convert it!", (Throwable)iOException);
                return false;
            }
            catch (ServerConfigException serverConfigException) {
                LOGGER.error("Conversion failed, please try again later", (Throwable)serverConfigException);
                return false;
            }
            return true;
        }
        return true;
    }

    public static String getPlayerUuidByName(final MinecraftServer minecraftServer, String name) {
        if (ChatUtil.isEmpty(name) || name.length() > 16) {
            return name;
        }
        GameProfile gameProfile = minecraftServer.getUserCache().findByName(name);
        if (gameProfile != null && gameProfile.getId() != null) {
            return gameProfile.getId().toString();
        }
        if (minecraftServer.isSinglePlayer() || !minecraftServer.isOnlineMode()) {
            return PlayerEntity.getUuidFromProfile(new GameProfile(null, name)).toString();
        }
        final ArrayList list = Lists.newArrayList();
        ProfileLookupCallback profileLookupCallback = new ProfileLookupCallback(){

            public void onProfileLookupSucceeded(GameProfile profile) {
                minecraftServer.getUserCache().add(profile);
                list.add(profile);
            }

            public void onProfileLookupFailed(GameProfile profile, Exception exception) {
                LOGGER.warn("Could not lookup user whitelist entry for {}", (Object)profile.getName(), (Object)exception);
            }
        };
        ServerConfigHandler.lookupProfile(minecraftServer, Lists.newArrayList((Object[])new String[]{name}), profileLookupCallback);
        if (!list.isEmpty() && ((GameProfile)list.get(0)).getId() != null) {
            return ((GameProfile)list.get(0)).getId().toString();
        }
        return "";
    }

    public static boolean convertPlayerFiles(final MinecraftDedicatedServer minecraftServer) {
        final File file = ServerConfigHandler.getLevelPlayersFolder(minecraftServer);
        final File file2 = new File(file.getParentFile(), "playerdata");
        final File file3 = new File(file.getParentFile(), "unknownplayers");
        if (!file.exists() || !file.isDirectory()) {
            return true;
        }
        File[] files = file.listFiles();
        ArrayList list = Lists.newArrayList();
        for (File file4 : files) {
            String string2;
            String string = file4.getName();
            if (!string.toLowerCase(Locale.ROOT).endsWith(".dat") || (string2 = string.substring(0, string.length() - ".dat".length())).isEmpty()) continue;
            list.add(string2);
        }
        try {
            Object[] strings = list.toArray(new String[list.size()]);
            ProfileLookupCallback profileLookupCallback = new ProfileLookupCallback((String[])strings){
                final /* synthetic */ String[] field_14340;
                {
                    this.field_14340 = strings;
                }

                public void onProfileLookupSucceeded(GameProfile profile) {
                    minecraftServer.getUserCache().add(profile);
                    UUID uUID = profile.getId();
                    if (uUID == null) {
                        throw new ServerConfigException("Missing UUID for user profile " + profile.getName());
                    }
                    this.convertPlayerFile(file2, this.getPlayerFileName(profile), uUID.toString());
                }

                public void onProfileLookupFailed(GameProfile profile, Exception exception) {
                    LOGGER.warn("Could not lookup user uuid for {}", (Object)profile.getName(), (Object)exception);
                    if (!(exception instanceof ProfileNotFoundException)) {
                        throw new ServerConfigException("Could not request user " + profile.getName() + " from backend systems", exception);
                    }
                    String string = this.getPlayerFileName(profile);
                    this.convertPlayerFile(file3, string, string);
                }

                private void convertPlayerFile(File playerDataFolder, String fileName, String uuid) {
                    File file4 = new File(file, fileName + ".dat");
                    File file22 = new File(playerDataFolder, uuid + ".dat");
                    ServerConfigHandler.createDirectory(playerDataFolder);
                    if (!file4.renameTo(file22)) {
                        throw new ServerConfigException("Could not convert file for " + fileName);
                    }
                }

                private String getPlayerFileName(GameProfile profile) {
                    String string = null;
                    for (String string2 : this.field_14340) {
                        if (string2 == null || !string2.equalsIgnoreCase(profile.getName())) continue;
                        string = string2;
                        break;
                    }
                    if (string == null) {
                        throw new ServerConfigException("Could not find the filename for " + profile.getName() + " anymore");
                    }
                    return string;
                }
            };
            ServerConfigHandler.lookupProfile(minecraftServer, Lists.newArrayList((Object[])strings), profileLookupCallback);
        }
        catch (ServerConfigException serverConfigException) {
            LOGGER.error("Conversion failed, please try again later", (Throwable)serverConfigException);
            return false;
        }
        return true;
    }

    private static void createDirectory(File directory) {
        if (directory.exists()) {
            if (directory.isDirectory()) {
                return;
            }
            throw new ServerConfigException("Can't create directory " + directory.getName() + " in world save directory.");
        }
        if (!directory.mkdirs()) {
            throw new ServerConfigException("Can't create directory " + directory.getName() + " in world save directory.");
        }
    }

    public static boolean checkSuccess(MinecraftServer minecraftServer) {
        boolean bl = ServerConfigHandler.checkListConversionSuccess();
        bl = bl && ServerConfigHandler.checkPlayerConversionSuccess(minecraftServer);
        return bl;
    }

    private static boolean checkListConversionSuccess() {
        boolean bl = false;
        if (BANNED_PLAYERS_FILE.exists() && BANNED_PLAYERS_FILE.isFile()) {
            bl = true;
        }
        boolean bl2 = false;
        if (BANNED_IPS_FILE.exists() && BANNED_IPS_FILE.isFile()) {
            bl2 = true;
        }
        boolean bl3 = false;
        if (OPERATORS_FILE.exists() && OPERATORS_FILE.isFile()) {
            bl3 = true;
        }
        boolean bl4 = false;
        if (WHITE_LIST_FILE.exists() && WHITE_LIST_FILE.isFile()) {
            bl4 = true;
        }
        if (bl || bl2 || bl3 || bl4) {
            LOGGER.warn("**** FAILED TO START THE SERVER AFTER ACCOUNT CONVERSION!");
            LOGGER.warn("** please remove the following files and restart the server:");
            if (bl) {
                LOGGER.warn("* {}", (Object)BANNED_PLAYERS_FILE.getName());
            }
            if (bl2) {
                LOGGER.warn("* {}", (Object)BANNED_IPS_FILE.getName());
            }
            if (bl3) {
                LOGGER.warn("* {}", (Object)OPERATORS_FILE.getName());
            }
            if (bl4) {
                LOGGER.warn("* {}", (Object)WHITE_LIST_FILE.getName());
            }
            return false;
        }
        return true;
    }

    private static boolean checkPlayerConversionSuccess(MinecraftServer minecraftServer) {
        File file = ServerConfigHandler.getLevelPlayersFolder(minecraftServer);
        if (file.exists() && file.isDirectory() && (file.list().length > 0 || !file.delete())) {
            LOGGER.warn("**** DETECTED OLD PLAYER DIRECTORY IN THE WORLD SAVE");
            LOGGER.warn("**** THIS USUALLY HAPPENS WHEN THE AUTOMATIC CONVERSION FAILED IN SOME WAY");
            LOGGER.warn("** please restart the server and if the problem persists, remove the directory '{}'", (Object)file.getPath());
            return false;
        }
        return true;
    }

    private static File getLevelPlayersFolder(MinecraftServer minecraftServer) {
        String string = minecraftServer.getLevelName();
        File file = new File(string);
        return new File(file, "players");
    }

    private static void markFileConverted(File file) {
        File file2 = new File(file.getName() + ".converted");
        file.renameTo(file2);
    }

    private static Date stringToDate(String string, Date fallback) {
        Date date;
        try {
            date = BanEntry.DATE_FORMAT.parse(string);
        }
        catch (ParseException parseException) {
            date = fallback;
        }
        return date;
    }

    static class ServerConfigException
    extends RuntimeException {
        private ServerConfigException(String title, Throwable other) {
            super(title, other);
        }

        private ServerConfigException(String title) {
            super(title);
        }
    }
}

