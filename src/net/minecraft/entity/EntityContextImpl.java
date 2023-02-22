/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

public class EntityContextImpl
implements EntityContext {
    protected static final EntityContext ABSENT = new EntityContextImpl(false, -1.7976931348623157E308, Items.AIR){

        @Override
        public boolean isAbove(VoxelShape shape, BlockPos pos, boolean defaultValue) {
            return defaultValue;
        }
    };
    private final boolean descending;
    private final double minY;
    private final Item heldItem;

    protected EntityContextImpl(boolean descending, double minY, Item heldItem) {
        this.descending = descending;
        this.minY = minY;
        this.heldItem = heldItem;
    }

    @Deprecated
    protected EntityContextImpl(Entity entity) {
        this(entity.isSneaking(), entity.getBoundingBox().y1, entity instanceof LivingEntity ? ((LivingEntity)entity).getMainHandStack().getItem() : Items.AIR);
    }

    @Override
    public boolean isHolding(Item item) {
        return this.heldItem == item;
    }

    @Override
    public boolean isDescending() {
        return this.descending;
    }

    @Override
    public boolean isAbove(VoxelShape shape, BlockPos pos, boolean defaultValue) {
        return this.minY > (double)pos.getY() + shape.getMaximum(Direction.Axis.Y) - (double)1.0E-5f;
    }
}
