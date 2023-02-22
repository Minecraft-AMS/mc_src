/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.network.message;

import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class CachedDecoratorResult {
    @Nullable
    private Cache cachedResult;

    public void setCachedResult(String query, Text preview) {
        this.cachedResult = new Cache(query, preview);
    }

    @Nullable
    public Text tryConsume(String query) {
        Cache cache = this.cachedResult;
        if (cache != null && cache.queryEquals(query)) {
            this.cachedResult = null;
            return cache.preview();
        }
        return null;
    }

    record Cache(String query, Text preview) {
        public boolean queryEquals(String query) {
            return this.query.equals(query);
        }
    }
}

