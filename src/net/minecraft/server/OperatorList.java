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
import java.io.File;
import net.minecraft.server.OperatorEntry;
import net.minecraft.server.ServerConfigEntry;
import net.minecraft.server.ServerConfigList;

public class OperatorList
extends ServerConfigList<GameProfile, OperatorEntry> {
    public OperatorList(File file) {
        super(file);
    }

    @Override
    protected ServerConfigEntry<GameProfile> fromJson(JsonObject jsonObject) {
        return new OperatorEntry(jsonObject);
    }

    @Override
    public String[] getNames() {
        String[] strings = new String[this.values().size()];
        int i = 0;
        for (ServerConfigEntry serverConfigEntry : this.values()) {
            strings[i++] = ((GameProfile)serverConfigEntry.getKey()).getName();
        }
        return strings;
    }

    public boolean isOp(GameProfile gameProfile) {
        OperatorEntry operatorEntry = (OperatorEntry)this.get(gameProfile);
        if (operatorEntry != null) {
            return operatorEntry.canBypassPlayerLimit();
        }
        return false;
    }

    @Override
    protected String toString(GameProfile gameProfile) {
        return gameProfile.getId().toString();
    }

    @Override
    protected /* synthetic */ String toString(Object profile) {
        return this.toString((GameProfile)profile);
    }
}
