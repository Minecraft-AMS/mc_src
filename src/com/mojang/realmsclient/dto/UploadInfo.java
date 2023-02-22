/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.google.gson.annotations.Expose
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class UploadInfo
extends ValueObject {
    private static final Logger LOGGER = LogManager.getLogger();
    @Expose
    private boolean worldClosed;
    @Expose
    private String token = "";
    @Expose
    private String uploadEndpoint = "";
    private int port;

    public static UploadInfo parse(String json) {
        UploadInfo uploadInfo = new UploadInfo();
        try {
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
            uploadInfo.worldClosed = JsonUtils.getBooleanOr("worldClosed", jsonObject, false);
            uploadInfo.token = JsonUtils.getStringOr("token", jsonObject, null);
            uploadInfo.uploadEndpoint = JsonUtils.getStringOr("uploadEndpoint", jsonObject, null);
            uploadInfo.port = JsonUtils.getIntOr("port", jsonObject, 8080);
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse UploadInfo: " + exception.getMessage());
        }
        return uploadInfo;
    }

    public String getToken() {
        return this.token;
    }

    public String getUploadEndpoint() {
        return this.uploadEndpoint;
    }

    public boolean isWorldClosed() {
        return this.worldClosed;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getPort() {
        return this.port;
    }
}
