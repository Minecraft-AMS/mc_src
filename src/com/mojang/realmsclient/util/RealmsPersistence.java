/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.io.FileUtils
 */
package com.mojang.realmsclient.util;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import org.apache.commons.io.FileUtils;

@Environment(value=EnvType.CLIENT)
public class RealmsPersistence {
    public static RealmsPersistenceData readFile() {
        File file = new File(Realms.getGameDirectoryPath(), "realms_persistence.json");
        Gson gson = new Gson();
        try {
            return (RealmsPersistenceData)gson.fromJson(FileUtils.readFileToString((File)file), RealmsPersistenceData.class);
        }
        catch (IOException iOException) {
            return new RealmsPersistenceData();
        }
    }

    public static void writeFile(RealmsPersistenceData data) {
        File file = new File(Realms.getGameDirectoryPath(), "realms_persistence.json");
        Gson gson = new Gson();
        String string = gson.toJson((Object)data);
        try {
            FileUtils.writeStringToFile((File)file, (String)string);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class RealmsPersistenceData {
        public String newsLink;
        public boolean hasUnreadNews;

        private RealmsPersistenceData() {
        }
    }
}

