/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.search;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.search.SearchProvider;

@Environment(value=EnvType.CLIENT)
public interface ReloadableSearchProvider<T>
extends SearchProvider<T> {
    public static <T> ReloadableSearchProvider<T> empty() {
        return text -> List.of();
    }

    default public void reload() {
    }
}

