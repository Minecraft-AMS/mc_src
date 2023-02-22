/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  org.slf4j.Logger
 */
package net.minecraft.world.entity;

import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.stream.Stream;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.collection.TypeFilterableList;
import net.minecraft.util.function.LazyIterationConsumer;
import net.minecraft.util.math.Box;
import net.minecraft.world.entity.EntityLike;
import net.minecraft.world.entity.EntityTrackingStatus;
import org.slf4j.Logger;

public class EntityTrackingSection<T extends EntityLike> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final TypeFilterableList<T> collection;
    private EntityTrackingStatus status;

    public EntityTrackingSection(Class<T> entityClass, EntityTrackingStatus status) {
        this.status = status;
        this.collection = new TypeFilterableList<T>(entityClass);
    }

    public void add(T entity) {
        this.collection.add(entity);
    }

    public boolean remove(T entity) {
        return this.collection.remove(entity);
    }

    public LazyIterationConsumer.NextIteration forEach(Box box, LazyIterationConsumer<T> consumer) {
        for (EntityLike entityLike : this.collection) {
            if (!entityLike.getBoundingBox().intersects(box) || !consumer.accept(entityLike).shouldAbort()) continue;
            return LazyIterationConsumer.NextIteration.ABORT;
        }
        return LazyIterationConsumer.NextIteration.CONTINUE;
    }

    public <U extends T> LazyIterationConsumer.NextIteration forEach(TypeFilter<T, U> type, Box box, LazyIterationConsumer<? super U> consumer) {
        Collection<T> collection = this.collection.getAllOfType(type.getBaseClass());
        if (collection.isEmpty()) {
            return LazyIterationConsumer.NextIteration.CONTINUE;
        }
        for (EntityLike entityLike : collection) {
            EntityLike entityLike2 = (EntityLike)type.downcast(entityLike);
            if (entityLike2 == null || !entityLike.getBoundingBox().intersects(box) || !consumer.accept(entityLike2).shouldAbort()) continue;
            return LazyIterationConsumer.NextIteration.ABORT;
        }
        return LazyIterationConsumer.NextIteration.CONTINUE;
    }

    public boolean isEmpty() {
        return this.collection.isEmpty();
    }

    public Stream<T> stream() {
        return this.collection.stream();
    }

    public EntityTrackingStatus getStatus() {
        return this.status;
    }

    public EntityTrackingStatus swapStatus(EntityTrackingStatus status) {
        EntityTrackingStatus entityTrackingStatus = this.status;
        this.status = status;
        return entityTrackingStatus;
    }

    @Debug
    public int size() {
        return this.collection.size();
    }
}

