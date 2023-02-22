/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.collect.ComparisonChain
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.dto;

import com.google.common.base.Joiner;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.realms.dto.PlayerInfo;
import net.minecraft.client.realms.dto.RealmsServerPing;
import net.minecraft.client.realms.dto.RealmsServerPlayerList;
import net.minecraft.client.realms.dto.RealmsWorldOptions;
import net.minecraft.client.realms.dto.ValueObject;
import net.minecraft.client.realms.util.JsonUtils;
import net.minecraft.client.realms.util.RealmsUtil;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsServer
extends ValueObject {
    private static final Logger LOGGER = LogUtils.getLogger();
    public long id;
    public String remoteSubscriptionId;
    public String name;
    public String description;
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
        return this.description;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public void updateServerPing(RealmsServerPlayerList serverPlayerList) {
        ArrayList list = Lists.newArrayList();
        int i = 0;
        for (String string : serverPlayerList.players) {
            if (string.equals(MinecraftClient.getInstance().getSession().getUuid())) continue;
            String string2 = "";
            try {
                string2 = RealmsUtil.uuidToName(string);
            }
            catch (Exception exception) {
                LOGGER.error("Could not get name for {}", (Object)string, (Object)exception);
                continue;
            }
            list.add(string2);
            ++i;
        }
        this.serverPing.nrOfPlayers = String.valueOf(i);
        this.serverPing.playerList = Joiner.on((char)'\n').join((Iterable)list);
    }

    public static RealmsServer parse(JsonObject node) {
        RealmsServer realmsServer = new RealmsServer();
        try {
            realmsServer.id = JsonUtils.getLongOr("id", node, -1L);
            realmsServer.remoteSubscriptionId = JsonUtils.getStringOr("remoteSubscriptionId", node, null);
            realmsServer.name = JsonUtils.getStringOr("name", node, null);
            realmsServer.description = JsonUtils.getStringOr("motd", node, null);
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
            LOGGER.error("Could not parse McoServer: {}", (Object)exception.getMessage());
        }
        return realmsServer;
    }

    private static void sortInvited(RealmsServer server) {
        server.players.sort((a, b) -> ComparisonChain.start().compareFalseFirst(b.isAccepted(), a.isAccepted()).compare((Comparable)((Object)a.getName().toLowerCase(Locale.ROOT)), (Comparable)((Object)b.getName().toLowerCase(Locale.ROOT))).result());
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

    private static Map<Integer, RealmsWorldOptions> parseSlots(JsonArray json) {
        HashMap map = Maps.newHashMap();
        for (JsonElement jsonElement : json) {
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
        try {
            return RealmsServer.parse(new JsonParser().parse(json).getAsJsonObject());
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse McoServer: {}", (Object)exception.getMessage());
            return new RealmsServer();
        }
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
        return Objects.hash(new Object[]{this.id, this.name, this.description, this.state, this.owner, this.expired});
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        RealmsServer realmsServer = (RealmsServer)o;
        return new EqualsBuilder().append(this.id, realmsServer.id).append((Object)this.name, (Object)realmsServer.name).append((Object)this.description, (Object)realmsServer.description).append((Object)this.state, (Object)realmsServer.state).append((Object)this.owner, (Object)realmsServer.owner).append(this.expired, realmsServer.expired).append((Object)this.worldType, (Object)this.worldType).isEquals();
    }

    public RealmsServer clone() {
        RealmsServer realmsServer = new RealmsServer();
        realmsServer.id = this.id;
        realmsServer.remoteSubscriptionId = this.remoteSubscriptionId;
        realmsServer.name = this.name;
        realmsServer.description = this.description;
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

    public String getWorldName(int slotId) {
        return this.name + " (" + this.slots.get(slotId).getSlotName(slotId) + ")";
    }

    public ServerInfo createServerInfo(String address) {
        return new ServerInfo(this.name, address, false);
    }

    public /* synthetic */ Object clone() throws CloneNotSupportedException {
        return this.clone();
    }

    @Environment(value=EnvType.CLIENT)
    public static final class State
    extends Enum<State> {
        public static final /* enum */ State CLOSED = new State();
        public static final /* enum */ State OPEN = new State();
        public static final /* enum */ State UNINITIALIZED = new State();
        private static final /* synthetic */ State[] field_19436;

        public static State[] values() {
            return (State[])field_19436.clone();
        }

        public static State valueOf(String name) {
            return Enum.valueOf(State.class, name);
        }

        private static /* synthetic */ State[] method_36848() {
            return new State[]{CLOSED, OPEN, UNINITIALIZED};
        }

        static {
            field_19436 = State.method_36848();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class WorldType
    extends Enum<WorldType> {
        public static final /* enum */ WorldType NORMAL = new WorldType();
        public static final /* enum */ WorldType MINIGAME = new WorldType();
        public static final /* enum */ WorldType ADVENTUREMAP = new WorldType();
        public static final /* enum */ WorldType EXPERIENCE = new WorldType();
        public static final /* enum */ WorldType INSPIRATION = new WorldType();
        private static final /* synthetic */ WorldType[] field_19442;

        public static WorldType[] values() {
            return (WorldType[])field_19442.clone();
        }

        public static WorldType valueOf(String name) {
            return Enum.valueOf(WorldType.class, name);
        }

        private static /* synthetic */ WorldType[] method_36849() {
            return new WorldType[]{NORMAL, MINIGAME, ADVENTUREMAP, EXPERIENCE, INSPIRATION};
        }

        static {
            field_19442 = WorldType.method_36849();
        }
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
            return ComparisonChain.start().compareTrueFirst(realmsServer.state == State.UNINITIALIZED, realmsServer2.state == State.UNINITIALIZED).compareTrueFirst(realmsServer.expiredTrial, realmsServer2.expiredTrial).compareTrueFirst(realmsServer.owner.equals(this.refOwner), realmsServer2.owner.equals(this.refOwner)).compareFalseFirst(realmsServer.expired, realmsServer2.expired).compareTrueFirst(realmsServer.state == State.OPEN, realmsServer2.state == State.OPEN).compare(realmsServer.id, realmsServer2.id).result();
        }

        @Override
        public /* synthetic */ int compare(Object one, Object two) {
            return this.compare((RealmsServer)one, (RealmsServer)two);
        }
    }
}

