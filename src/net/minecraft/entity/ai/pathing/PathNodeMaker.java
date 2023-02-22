/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 */
package net.minecraft.entity.ai.pathing;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.ai.pathing.TargetPathNode;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;

public abstract class PathNodeMaker {
    protected CollisionView blockView;
    protected MobEntity entity;
    protected final Int2ObjectMap<PathNode> pathNodeCache = new Int2ObjectOpenHashMap();
    protected int field_31;
    protected int field_30;
    protected int field_28;
    protected boolean canEnterOpenDoors;
    protected boolean canOpenDoors;
    protected boolean canSwim;

    public void init(CollisionView collisionView, MobEntity mobEntity) {
        this.blockView = collisionView;
        this.entity = mobEntity;
        this.pathNodeCache.clear();
        this.field_31 = MathHelper.floor(mobEntity.getWidth() + 1.0f);
        this.field_30 = MathHelper.floor(mobEntity.getHeight() + 1.0f);
        this.field_28 = MathHelper.floor(mobEntity.getWidth() + 1.0f);
    }

    public void clear() {
        this.blockView = null;
        this.entity = null;
    }

    protected PathNode getNode(int x, int y, int z) {
        return (PathNode)this.pathNodeCache.computeIfAbsent(PathNode.hash(x, y, z), l -> new PathNode(x, y, z));
    }

    public abstract PathNode getStart();

    public abstract TargetPathNode getNode(double var1, double var3, double var5);

    public abstract int getSuccessors(PathNode[] var1, PathNode var2);

    public abstract PathNodeType getNodeType(BlockView var1, int var2, int var3, int var4, MobEntity var5, int var6, int var7, int var8, boolean var9, boolean var10);

    public abstract PathNodeType getNodeType(BlockView var1, int var2, int var3, int var4);

    public void setCanEnterOpenDoors(boolean canEnterOpenDoors) {
        this.canEnterOpenDoors = canEnterOpenDoors;
    }

    public void setCanOpenDoors(boolean canOpenDoors) {
        this.canOpenDoors = canOpenDoors;
    }

    public void setCanSwim(boolean canSwim) {
        this.canSwim = canSwim;
    }

    public boolean canEnterOpenDoors() {
        return this.canEnterOpenDoors;
    }

    public boolean canOpenDoors() {
        return this.canOpenDoors;
    }

    public boolean canSwim() {
        return this.canSwim;
    }
}
