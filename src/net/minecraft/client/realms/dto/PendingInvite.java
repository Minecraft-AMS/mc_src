/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.realms.dto;

import com.google.gson.JsonObject;
import java.util.Date;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.ValueObject;
import net.minecraft.client.realms.util.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class PendingInvite
extends ValueObject {
    private static final Logger LOGGER = LogManager.getLogger();
    public String invitationId;
    public String worldName;
    public String worldOwnerName;
    public String worldOwnerUuid;
    public Date date;

    public static PendingInvite parse(JsonObject json) {
        PendingInvite pendingInvite = new PendingInvite();
        try {
            pendingInvite.invitationId = JsonUtils.getStringOr("invitationId", json, "");
            pendingInvite.worldName = JsonUtils.getStringOr("worldName", json, "");
            pendingInvite.worldOwnerName = JsonUtils.getStringOr("worldOwnerName", json, "");
            pendingInvite.worldOwnerUuid = JsonUtils.getStringOr("worldOwnerUuid", json, "");
            pendingInvite.date = JsonUtils.getDateOr("date", json);
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse PendingInvite: " + exception.getMessage());
        }
        return pendingInvite;
    }
}

