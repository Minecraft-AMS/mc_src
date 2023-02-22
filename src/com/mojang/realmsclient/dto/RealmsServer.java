/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ComparisonChain
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.realmsclient.dto;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.RealmsServerPing;
import com.mojang.realmsclient.dto.RealmsServerPlayerList;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsServer
extends ValueObject {
    private static final Logger LOGGER = LogManager.getLogger();
    public long id;
    public String remoteSubscriptionId;
    public String name;
    public String motd;
    public State state;
    public String owner;
    public String ownerUUID;
    public List<PlayerInfo> players;
    public Map<Integer, RealmsWorldOptions> slots;
    public boolean expired;
    public boolean expiredTrial;
    public int daysLeft;
    public WorldType worldType;
    public int activeSlot;
    public String minigameName;
    public int minigameId;
    public String minigameImage;
    public RealmsServerPing serverPing = new RealmsServerPing();

    public String getDescription() {
        return this.motd;
    }

    public String getName() {
        return this.name;
    }

    public String getMinigameName() {
        return this.minigameName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String motd) {
        this.motd = motd;
    }

    public void updateServerPing(RealmsServerPlayerList serverPlayerList) {
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        for (String string : serverPlayerList.players) {
            if (string.equals(Realms.getUUID())) continue;
            String string2 = "";
            try {
                string2 = RealmsUtil.uuidToName(string);
            }
            catch (Exception exception) {
                LOGGER.error("Could not get name for " + string, (Throwable)exception);
                continue;
            }
            if (stringBuilder.length() > 0) {
                stringBuilder.append("\n");
            }
            stringBuilder.append(string2);
            ++i;
        }
        this.serverPing.nrOfPlayers = String.valueOf(i);
        this.serverPing.playerList = stringBuilder.toString();
    }

    public static RealmsServer parse(JsonObject node) {
        RealmsServer realmsServer = new RealmsServer();
        try {
            realmsServer.id = JsonUtils.getLongOr("id", node, -1L);
            realmsServer.remoteSubscriptionId = JsonUtils.getStringOr("remoteSubscriptionId", node, null);
            realmsServer.name = JsonUtils.getStringOr("name", node, null);
            realmsServer.motd = JsonUtils.getStringOr("motd", node, null);
            realmsServer.state = RealmsServer.getState(JsonUtils.getStringOr("state", node, State.CLOSED.name()));
            realmsServer.owner = JsonUtils.getStringOr("owner", node, null);
            if (node.get("players") != null && node.get("players").isJsonArray()) {
                realmsServer.players = RealmsServer.parseInvited(node.get("players").getAsJsonArray());
                RealmsServer.sortInvited(realmsServer);
            } else {
                realmsServer.players = Lists.newArrayList();
            }
            realmsServer.daysLeft = JsonUtils.getIntOr("daysLeft", node, 0);
            realmsServer.expired = JsonUtils.getBooleanOr("expired", node, false);
            realmsServer.expiredTrial = JsonUtils.getBooleanOr("expiredTrial", node, false);
            realmsServer.worldType = RealmsServer.getWorldType(JsonUtils.getStringOr("worldType", node, WorldType.NORMAL.name()));
            realmsServer.ownerUUID = JsonUtils.getStringOr("ownerUUID", node, "");
            realmsServer.slots = node.get("slots") != null && node.get("slots").isJsonArray() ? RealmsServer.parseSlots(node.get("slots").getAsJsonArray()) : RealmsServer.getEmptySlots();
            realmsServer.minigameName = JsonUtils.getStringOr("minigameName", node, null);
            realmsServer.activeSlot = JsonUtils.getIntOr("activeSlot", node, -1);
            realmsServer.minigameId = JsonUtils.getIntOr("minigameId", node, -1);
            realmsServer.minigameImage = JsonUtils.getStringOr("minigameImage", node, null);
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse McoServer: " + exception.getMessage());
        }
        return realmsServer;
    }

    private static void sortInvited(RealmsServer server) {
        server.players.sort((playerInfo, playerInfo2) -> ComparisonChain.start().compareFalseFirst(playerInfo2.getAccepted(), playerInfo.getAccepted()).compare((Comparable)((Object)playerInfo.getName().toLowerCase(Locale.ROOT)), (Comparable)((Object)playerInfo2.getName().toLowerCase(Locale.ROOT))).result());
    }

    private static List<PlayerInfo> parseInvited(JsonArray jsonArray) {
        ArrayList list = Lists.newArrayList();
        for (JsonElement jsonElement : jsonArray) {
            try {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                PlayerInfo playerInfo = new PlayerInfo();
                playerInfo.setName(JsonUtils.getStringOr("name", jsonObject, null));
                playerInfo.setUuid(JsonUtils.getStringOr("uuid", jsonObject, null));
                playerInfo.setOperator(JsonUtils.getBooleanOr("operator", jsonObject, false));
                playerInfo.setAccepted(JsonUtils.getBooleanOr("accepted", jsonObject, false));
                playerInfo.setOnline(JsonUtils.getBooleanOr("online", jsonObject, false));
                list.add(playerInfo);
            }
            catch (Exception exception) {}
        }
        return list;
    }

    private static Map<Integer, RealmsWorldOptions> parseSlots(JsonArray jsonArray) {
        HashMap map = Maps.newHashMap();
        for (JsonElement jsonElement : jsonArray) {
            try {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                JsonParser jsonParser = new JsonParser();
                JsonElement jsonElement2 = jsonParser.parse(jsonObject.get("options").getAsString());
                RealmsWorldOptions realmsWorldOptions = jsonElement2 == null ? RealmsWorldOptions.getDefaults() : RealmsWorldOptions.parse(jsonElement2.getAsJsonObject());
                int i = JsonUtils.getIntOr("slotId", jsonObject, -1);
                map.put(i, realmsWorldOptions);
            }
            catch (Exception exception) {}
        }
        for (int j = 1; j <= 3; ++j) {
            if (map.containsKey(j)) continue;
            map.put(j, RealmsWorldOptions.getEmptyDefaults());
        }
        return map;
    }

    private static Map<Integer, RealmsWorldOptions> getEmptySlots() {
        HashMap map = Maps.newHashMap();
        map.put(1, RealmsWorldOptions.getEmptyDefaults());
        map.put(2, RealmsWorldOptions.getEmptyDefaults());
        map.put(3, RealmsWorldOptions.getEmptyDefaults());
        return map;
    }

    public static RealmsServer parse(String json) {
        RealmsServer realmsServer = new RealmsServer();
        try {
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
            realmsServer = RealmsServer.parse(jsonObject);
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse McoServer: " + exception.getMessage());
        }
        return realmsServer;
    }

    private static State getState(String state) {
        try {
            return State.valueOf(state);
        }
        catch (Exception exception) {
            return State.CLOSED;
        }
    }

    private static WorldType getWorldType(String state) {
        try {
            return WorldType.valueOf(state);
        }
        catch (Exception exception) {
            return WorldType.NORMAL;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(this.id).append((Object)this.name).append((Object)this.motd).append((Object)this.state).append((Object)this.owner).append(this.expired).toHashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        RealmsServer realmsServer = (RealmsServer)obj;
        return new EqualsBuilder().append(this.id, realmsServer.id).append((Object)this.name, (Object)realmsServer.name).append((Object)this.motd, (Object)realmsServer.motd).append((Object)this.state, (Object)realmsServer.state).append((Object)this.owner, (Object)realmsServer.owner).append(this.expired, realmsServer.expired).append((Object)this.worldType, (Object)this.worldType).isEquals();
    }

    public RealmsServer clone() {
        RealmsServer realmsServer = new RealmsServer();
        realmsServer.id = this.id;
        realmsServer.remoteSubscriptionId = this.remoteSubscriptionId;
        realmsServer.name = this.name;
        realmsServer.motd = this.motd;
        realmsServer.state = this.state;
        realmsServer.owner = this.owner;
        realmsServer.players = this.players;
        realmsServer.slots = this.cloneSlots(this.slots);
        realmsServer.expired = this.expired;
        realmsServer.expiredTrial = this.expiredTrial;
        realmsServer.daysLeft = this.daysLeft;
        realmsServer.serverPing = new RealmsServerPing();
        realmsServer.serverPing.nrOfPlayers = this.serverPing.nrOfPlayers;
        realmsServer.serverPing.playerList = this.serverPing.playerList;
        realmsServer.worldType = this.worldType;
        realmsServer.ownerUUID = this.ownerUUID;
        realmsServer.minigameName = this.minigameName;
        realmsServer.activeSlot = this.activeSlot;
        realmsServer.minigameId = this.minigameId;
        realmsServer.minigameImage = this.minigameImage;
        return realmsServer;
    }

    public Map<Integer, RealmsWorldOptions> cloneSlots(Map<Integer, RealmsWorldOptions> slots) {
        HashMap map = Maps.newHashMap();
        for (Map.Entry<Integer, RealmsWorldOptions> entry : slots.entrySet()) {
            map.put(entry.getKey(), entry.getValue().clone());
        }
        return map;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum WorldType {
        NORMAL,
        MINIGAME,
        ADVENTUREMAP,
        EXPERIENCE,
        INSPIRATION;

    }

    @Environment(value=EnvType.CLIENT)
    public static enum State {
        CLOSED,
        OPEN,
        UNINITIALIZED;

    }

    @Environment(value=EnvType.CLIENT)
    public static class McoServerComparator
    implements Comparator<RealmsServer> {
        private final String refOwner;

        public McoServerComparator(String owner) {
            this.refOwner = owner;
        }

        @Override
        public int compare(RealmsServer realmsServer, RealmsServer realmsServer2) {
            return ComparisonChain.start().compareTrueFirst(realmsServer.state.equals((Object)State.UNINITIALIZED), realmsServer2.state.equals((Object)State.UNINITIALIZED)).compareTrueFirst(realmsServer.expiredTrial, realmsServer2.expiredTrial).compareTrueFirst(realmsServer.owner.equals(this.refOwner), realmsServer2.owner.equals(this.refOwner)).compareFalseFirst(realmsServer.expired, realmsServer2.expired).compareTrueFirst(realmsServer.state.equals((Object)State.OPEN), realmsServer2.state.equals((Object)State.OPEN)).compare(realmsServer.id, realmsServer2.id).result();
        }

        @Override
        public /* synthetic */ int compare(Object object, Object object2) {
            return this.compare((RealmsServer)object, (RealmsServer)object2);
        }
    }
}

