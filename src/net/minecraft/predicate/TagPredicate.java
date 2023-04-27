/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.predicate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

public class TagPredicate<T> {
    private final TagKey<T> tag;
    private final boolean expected;

    public TagPredicate(TagKey<T> tag, boolean expected) {
        this.tag = tag;
        this.expected = expected;
    }

    public static <T> TagPredicate<T> expected(TagKey<T> tag) {
        return new TagPredicate<T>(tag, true);
    }

    public static <T> TagPredicate<T> unexpected(TagKey<T> tag) {
        return new TagPredicate<T>(tag, false);
    }

    public boolean test(RegistryEntry<T> registryEntry) {
        return registryEntry.isIn(this.tag) == this.expected;
    }

    public JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", this.tag.id().toString());
        jsonObject.addProperty("expected", Boolean.valueOf(this.expected));
        return jsonObject;
    }

    public static <T> TagPredicate<T> fromJson(@Nullable JsonElement json, RegistryKey<? extends Registry<T>> registry) {
        if (json == null) {
            throw new JsonParseException("Expected a tag predicate");
        }
        JsonObject jsonObject = JsonHelper.asObject(json, "Tag Predicate");
        Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "id"));
        boolean bl = JsonHelper.getBoolean(jsonObject, "expected");
        return new TagPredicate(TagKey.of(registry, identifier), bl);
    }
}

