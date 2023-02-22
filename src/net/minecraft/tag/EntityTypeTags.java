/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.tag;

import java.util.Collection;
import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;

public class EntityTypeTags {
    private static TagContainer<EntityType<?>> container = new TagContainer(identifier -> Optional.empty(), "", false, "");
    private static int latestVersion;
    public static final Tag<EntityType<?>> SKELETONS;
    public static final Tag<EntityType<?>> RAIDERS;

    public static void setContainer(TagContainer<EntityType<?>> container) {
        EntityTypeTags.container = container;
        ++latestVersion;
    }

    public static TagContainer<EntityType<?>> getContainer() {
        return container;
    }

    private static Tag<EntityType<?>> register(String id) {
        return new CachingTag(new Identifier(id));
    }

    static {
        SKELETONS = EntityTypeTags.register("skeletons");
        RAIDERS = EntityTypeTags.register("raiders");
    }

    public static class CachingTag
    extends Tag<EntityType<?>> {
        private int version = -1;
        private Tag<EntityType<?>> delegate;

        public CachingTag(Identifier identifier) {
            super(identifier);
        }

        @Override
        public boolean contains(EntityType<?> entityType) {
            if (this.version != latestVersion) {
                this.delegate = container.getOrCreate(this.getId());
                this.version = latestVersion;
            }
            return this.delegate.contains(entityType);
        }

        @Override
        public Collection<EntityType<?>> values() {
            if (this.version != latestVersion) {
                this.delegate = container.getOrCreate(this.getId());
                this.version = latestVersion;
            }
            return this.delegate.values();
        }

        @Override
        public Collection<Tag.Entry<EntityType<?>>> entries() {
            if (this.version != latestVersion) {
                this.delegate = container.getOrCreate(this.getId());
                this.version = latestVersion;
            }
            return this.delegate.entries();
        }
    }
}

