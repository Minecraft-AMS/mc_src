/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.authlib.GameProfile
 */
package net.minecraft.server;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.server.ServerConfigEntry;

public class WhitelistEntry
extends ServerConfigEntry<GameProfile> {
    public WhitelistEntry(GameProfile gameProfile) {
        super(gameProfile);
    }

    public WhitelistEntry(JsonObject jsonObject) {
        super(WhitelistEntry.deserializeProfile(jsonObject), jsonObject);
    }

    @Override
    protected void serialize(JsonObject jsonObject) {
        if (this.getKey() == null) {
            return;
        }
        jsonObject.addProperty("uuid", ((GameProfile)this.getKey()).getId() == null ? "" : ((GameProfile)this.getKey()).getId().toString());
        jsonObject.addProperty("name", ((GameProfile)this.getKey()).getName());
        super.serialize(jsonObject);
    }

    private static GameProfile deserializeProfile(JsonObject json) {
        UUID uUID;
        if (!json.has("uuid") || !json.has("name")) {
            return null;
        }
        String string = json.get("uuid").getAsString();
        try {
            uUID = UUID.fromString(string);
        }
        catch (Throwable throwable) {
            return null;
        }
        return new GameProfile(uUID, json.get("name").getAsString());
    }
}

