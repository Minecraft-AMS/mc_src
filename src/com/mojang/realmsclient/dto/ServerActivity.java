/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class ServerActivity
extends ValueObject {
    public String profileUuid;
    public long joinTime;
    public long leaveTime;

    public static ServerActivity parse(JsonObject element) {
        ServerActivity serverActivity = new ServerActivity();
        try {
            serverActivity.profileUuid = JsonUtils.getStringOr("profileUuid", element, null);
            serverActivity.joinTime = JsonUtils.getLongOr("joinTime", element, Long.MIN_VALUE);
            serverActivity.leaveTime = JsonUtils.getLongOr("leaveTime", element, Long.MIN_VALUE);
        }
        catch (Exception exception) {
            // empty catch block
        }
        return serverActivity;
    }
}

