/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.predicate.entity;

import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.TypeSpecificPredicate;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class LightningBoltPredicate
implements TypeSpecificPredicate {
    private static final String BLOCKS_SET_ON_FIRE_KEY = "blocks_set_on_fire";
    private static final String ENTITY_STRUCK_KEY = "entity_struck";
    private final NumberRange.IntRange blocksSetOnFire;
    private final EntityPredicate entityStruck;

    private LightningBoltPredicate(NumberRange.IntRange blocksSetOnFire, EntityPredicate entityStruck) {
        this.blocksSetOnFire = blocksSetOnFire;
        this.entityStruck = entityStruck;
    }

    public static LightningBoltPredicate of(NumberRange.IntRange blocksSetOnFire) {
        return new LightningBoltPredicate(blocksSetOnFire, EntityPredicate.ANY);
    }

    public static LightningBoltPredicate fromJson(JsonObject json) {
        return new LightningBoltPredicate(NumberRange.IntRange.fromJson(json.get(BLOCKS_SET_ON_FIRE_KEY)), EntityPredicate.fromJson(json.get(ENTITY_STRUCK_KEY)));
    }

    @Override
    public JsonObject typeSpecificToJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(BLOCKS_SET_ON_FIRE_KEY, this.blocksSetOnFire.toJson());
        jsonObject.add(ENTITY_STRUCK_KEY, this.entityStruck.toJson());
        return jsonObject;
    }

    @Override
    public TypeSpecificPredicate.Deserializer getDeserializer() {
        return TypeSpecificPredicate.Deserializers.LIGHTNING;
    }

    @Override
    public boolean test(Entity entity, ServerWorld world, @Nullable Vec3d pos) {
        if (!(entity instanceof LightningEntity)) {
            return false;
        }
        LightningEntity lightningEntity = (LightningEntity)entity;
        return this.blocksSetOnFire.test(lightningEntity.getBlocksSetOnFire()) && (this.entityStruck == EntityPredicate.ANY || lightningEntity.getStruckEntities().anyMatch(struckEntity -> this.entityStruck.test(world, pos, (Entity)struckEntity)));
    }
}

