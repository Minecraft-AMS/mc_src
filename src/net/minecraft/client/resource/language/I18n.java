/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.resource.language;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.TranslationStorage;

@Environment(value=EnvType.CLIENT)
public class I18n {
    private static TranslationStorage storage;

    static void setLanguage(TranslationStorage storage) {
        I18n.storage = storage;
    }

    public static String translate(String key, Object ... args) {
        return storage.translate(key, args);
    }

    public static boolean hasTranslation(String key) {
        return storage.containsKey(key);
    }
}

