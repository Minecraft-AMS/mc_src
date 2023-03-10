/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.resource.language;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.client.resource.metadata.LanguageResourceMetadata;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.Language;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class LanguageManager
implements SynchronousResourceReloader {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String DEFAULT_LANGUAGE_CODE = "en_us";
    private static final LanguageDefinition ENGLISH_US = new LanguageDefinition("en_us", "US", "English", false);
    private Map<String, LanguageDefinition> languageDefs = ImmutableMap.of((Object)"en_us", (Object)ENGLISH_US);
    private String currentLanguageCode;
    private LanguageDefinition language = ENGLISH_US;

    public LanguageManager(String languageCode) {
        this.currentLanguageCode = languageCode;
    }

    private static Map<String, LanguageDefinition> loadAvailableLanguages(Stream<ResourcePack> packs) {
        HashMap map = Maps.newHashMap();
        packs.forEach(pack -> {
            try {
                LanguageResourceMetadata languageResourceMetadata = pack.parseMetadata(LanguageResourceMetadata.READER);
                if (languageResourceMetadata != null) {
                    for (LanguageDefinition languageDefinition : languageResourceMetadata.getLanguageDefinitions()) {
                        map.putIfAbsent(languageDefinition.getCode(), languageDefinition);
                    }
                }
            }
            catch (IOException | RuntimeException exception) {
                LOGGER.warn("Unable to parse language metadata section of resourcepack: {}", (Object)pack.getName(), (Object)exception);
            }
        });
        return ImmutableMap.copyOf((Map)map);
    }

    @Override
    public void reload(ResourceManager manager) {
        this.languageDefs = LanguageManager.loadAvailableLanguages(manager.streamResourcePacks());
        LanguageDefinition languageDefinition = this.languageDefs.getOrDefault(DEFAULT_LANGUAGE_CODE, ENGLISH_US);
        this.language = this.languageDefs.getOrDefault(this.currentLanguageCode, languageDefinition);
        ArrayList list = Lists.newArrayList((Object[])new LanguageDefinition[]{languageDefinition});
        if (this.language != languageDefinition) {
            list.add(this.language);
        }
        TranslationStorage translationStorage = TranslationStorage.load(manager, list);
        I18n.setLanguage(translationStorage);
        Language.setInstance(translationStorage);
    }

    public void setLanguage(LanguageDefinition language) {
        this.currentLanguageCode = language.getCode();
        this.language = language;
    }

    public LanguageDefinition getLanguage() {
        return this.language;
    }

    public SortedSet<LanguageDefinition> getAllLanguages() {
        return Sets.newTreeSet(this.languageDefs.values());
    }

    public LanguageDefinition getLanguage(String code) {
        return this.languageDefs.get(code);
    }
}

