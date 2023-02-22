/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.util.JsonUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsError {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final String errorMessage;
    private final int errorCode;

    private RealmsError(String errorMessage, int errorCode) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    @Nullable
    public static RealmsError create(String error) {
        if (Strings.isNullOrEmpty((String)error)) {
            return null;
        }
        try {
            JsonObject jsonObject = JsonParser.parseString((String)error).getAsJsonObject();
            String string = JsonUtils.getStringOr("errorMsg", jsonObject, "");
            int i = JsonUtils.getIntOr("errorCode", jsonObject, -1);
            return new RealmsError(string, i);
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse RealmsError: {}", (Object)exception.getMessage());
            LOGGER.error("The error was: {}", (Object)error);
            return null;
        }
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public int getErrorCode() {
        return this.errorCode;
    }
}

