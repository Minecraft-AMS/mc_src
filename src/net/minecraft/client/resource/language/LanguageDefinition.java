/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.bridge.game.Language
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.resource.language;

import com.mojang.bridge.game.Language;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class LanguageDefinition
implements Language,
Comparable<LanguageDefinition> {
    private final String code;
    private final String name;
    private final String region;
    private final boolean rightToLeft;

    public LanguageDefinition(String code, String name, String region, boolean bl) {
        this.code = code;
        this.name = name;
        this.region = region;
        this.rightToLeft = bl;
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.region;
    }

    public String getRegion() {
        return this.name;
    }

    public boolean isRightToLeft() {
        return this.rightToLeft;
    }

    public String toString() {
        return String.format("%s (%s)", this.region, this.name);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LanguageDefinition)) {
            return false;
        }
        return this.code.equals(((LanguageDefinition)o).code);
    }

    public int hashCode() {
        return this.code.hashCode();
    }

    @Override
    public int compareTo(LanguageDefinition languageDefinition) {
        return this.code.compareTo(languageDefinition.code);
    }

    @Override
    public /* synthetic */ int compareTo(Object object) {
        return this.compareTo((LanguageDefinition)object);
    }
}

