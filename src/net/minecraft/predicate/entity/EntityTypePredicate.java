/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSyntaxException
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.predicate.entity;

import com.google.common.base.Joiner;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import net.minecraft.entity.EntityType;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public abstract class EntityTypePredicate {
    public static final EntityTypePredicate ANY = new EntityTypePredicate(){

        @Override
        public boolean matches(EntityType<?> entityType) {
            return true;
        }

        @Override
        public JsonElement toJson() {
            return JsonNull.INSTANCE;
        }
    };
    private static final Joiner COMMA_JOINER = Joiner.on((String)", ");

    public abstract boolean matches(EntityType<?> var1);

    public abstract JsonElement toJson();

    public static EntityTypePredicate deserialize(@Nullable JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return ANY;
        }
        String string = JsonHelper.asString(element, "type");
        if (string.startsWith("#")) {
            Identifier identifier = new Identifier(string.substring(1));
            Tag<EntityType<?>> tag = EntityTypeTags.getContainer().getOrCreate(identifier);
            return new Tagged(tag);
        }
        Identifier identifier = new Identifier(string);
        EntityType entityType = (EntityType)Registry.ENTITY_TYPE.getOrEmpty(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown entity type '" + identifier + "', valid types are: " + COMMA_JOINER.join(Registry.ENTITY_TYPE.getIds())));
        return new Single(entityType);
    }

    public static EntityTypePredicate create(EntityType<?> type) {
        return new Single(type);
    }

    public static EntityTypePredicate create(Tag<EntityType<?>> tag) {
        return new Tagged(tag);
    }

    static class Tagged
    extends EntityTypePredicate {
        private final Tag<EntityType<?>> tag;

        public Tagged(Tag<EntityType<?>> tag) {
            this.tag = tag;
        }

        @Override
        public boolean matches(EntityType<?> entityType) {
            return this.tag.contains(entityType);
        }

        @Override
        public JsonElement toJson() {
            return new JsonPrimitive("#" + this.tag.getId().toString());
        }
    }

    static class Single
    extends EntityTypePredicate {
        private final EntityType<?> type;

        public Single(EntityType<?> entityType) {
            this.type = entityType;
        }

        @Override
        public boolean matches(EntityType<?> entityType) {
            return this.type == entityType;
        }

        @Override
        public JsonElement toJson() {
            return new JsonPrimitive(Registry.ENTITY_TYPE.getId(this.type).toString());
        }
    }
}

