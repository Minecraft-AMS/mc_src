/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.data.server;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.server.AbstractTagProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.util.registry.Registry;

public class EntityTypeTagProvider
extends AbstractTagProvider<EntityType<?>> {
    public EntityTypeTagProvider(DataGenerator root) {
        super(root, Registry.ENTITY_TYPE);
    }

    @Override
    protected void configure() {
        this.getOrCreateTagBuilder(EntityTypeTags.SKELETONS).add(EntityType.SKELETON, EntityType.STRAY, EntityType.WITHER_SKELETON);
        this.getOrCreateTagBuilder(EntityTypeTags.RAIDERS).add(EntityType.EVOKER, EntityType.PILLAGER, EntityType.RAVAGER, EntityType.VINDICATOR, EntityType.ILLUSIONER, EntityType.WITCH);
        this.getOrCreateTagBuilder(EntityTypeTags.BEEHIVE_INHABITORS).add(EntityType.BEE);
        this.getOrCreateTagBuilder(EntityTypeTags.ARROWS).add(EntityType.ARROW, EntityType.SPECTRAL_ARROW);
        this.getOrCreateTagBuilder(EntityTypeTags.IMPACT_PROJECTILES).addTag(EntityTypeTags.ARROWS).add(EntityType.SNOWBALL, EntityType.FIREBALL, EntityType.SMALL_FIREBALL, EntityType.EGG, EntityType.TRIDENT, EntityType.DRAGON_FIREBALL, EntityType.WITHER_SKULL);
        this.getOrCreateTagBuilder(EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS).add(EntityType.RABBIT, EntityType.ENDERMITE, EntityType.SILVERFISH, EntityType.FOX);
        this.getOrCreateTagBuilder(EntityTypeTags.AXOLOTL_HUNT_TARGETS).add(EntityType.TROPICAL_FISH, EntityType.PUFFERFISH, EntityType.SALMON, EntityType.COD, EntityType.SQUID, EntityType.GLOW_SQUID);
        this.getOrCreateTagBuilder(EntityTypeTags.AXOLOTL_ALWAYS_HOSTILES).add(EntityType.DROWNED, EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN);
        this.getOrCreateTagBuilder(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES).add(EntityType.STRAY, EntityType.POLAR_BEAR, EntityType.SNOW_GOLEM, EntityType.WITHER);
        this.getOrCreateTagBuilder(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES).add(EntityType.STRIDER, EntityType.BLAZE, EntityType.MAGMA_CUBE);
    }

    @Override
    public String getName() {
        return "Entity Type Tags";
    }
}

