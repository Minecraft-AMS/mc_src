/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.border;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.border.WorldBorderListener;
import net.minecraft.world.border.WorldBorderStage;
import net.minecraft.world.level.LevelProperties;

public class WorldBorder {
    private final List<WorldBorderListener> listeners = Lists.newArrayList();
    private double damagePerBlock = 0.2;
    private double buffer = 5.0;
    private int warningTime = 15;
    private int warningBlocks = 5;
    private double centerX;
    private double centerZ;
    private int maxWorldBorderRadius = 29999984;
    private Area area = new StaticArea(6.0E7);

    public boolean contains(BlockPos pos) {
        return (double)(pos.getX() + 1) > this.getBoundWest() && (double)pos.getX() < this.getBoundEast() && (double)(pos.getZ() + 1) > this.getBoundNorth() && (double)pos.getZ() < this.getBoundSouth();
    }

    public boolean contains(ChunkPos pos) {
        return (double)pos.getEndX() > this.getBoundWest() && (double)pos.getStartX() < this.getBoundEast() && (double)pos.getEndZ() > this.getBoundNorth() && (double)pos.getStartZ() < this.getBoundSouth();
    }

    public boolean contains(Box box) {
        return box.x2 > this.getBoundWest() && box.x1 < this.getBoundEast() && box.z2 > this.getBoundNorth() && box.z1 < this.getBoundSouth();
    }

    public double getDistanceInsideBorder(Entity entity) {
        return this.getDistanceInsideBorder(entity.x, entity.z);
    }

    public VoxelShape asVoxelShape() {
        return this.area.method_17906();
    }

    public double getDistanceInsideBorder(double x, double z) {
        double d = z - this.getBoundNorth();
        double e = this.getBoundSouth() - z;
        double f = x - this.getBoundWest();
        double g = this.getBoundEast() - x;
        double h = Math.min(f, g);
        h = Math.min(h, d);
        return Math.min(h, e);
    }

    @Environment(value=EnvType.CLIENT)
    public WorldBorderStage getStage() {
        return this.area.getStage();
    }

    public double getBoundWest() {
        return this.area.getBoundWest();
    }

    public double getBoundNorth() {
        return this.area.getBoundNorth();
    }

    public double getBoundEast() {
        return this.area.getBoundEast();
    }

    public double getBoundSouth() {
        return this.area.getBoundSouth();
    }

    public double getCenterX() {
        return this.centerX;
    }

    public double getCenterZ() {
        return this.centerZ;
    }

    public void setCenter(double x, double z) {
        this.centerX = x;
        this.centerZ = z;
        this.area.onCenterChanged();
        for (WorldBorderListener worldBorderListener : this.getListeners()) {
            worldBorderListener.onCenterChanged(this, x, z);
        }
    }

    public double getSize() {
        return this.area.getSize();
    }

    public long getTargetRemainingTime() {
        return this.area.getTargetRemainingTime();
    }

    public double getTargetSize() {
        return this.area.getTargetSize();
    }

    public void setSize(double size) {
        this.area = new StaticArea(size);
        for (WorldBorderListener worldBorderListener : this.getListeners()) {
            worldBorderListener.onSizeChange(this, size);
        }
    }

    public void interpolateSize(double fromSize, double toSize, long time) {
        this.area = fromSize == toSize ? new StaticArea(toSize) : new MovingArea(fromSize, toSize, time);
        for (WorldBorderListener worldBorderListener : this.getListeners()) {
            worldBorderListener.onInterpolateSize(this, fromSize, toSize, time);
        }
    }

    protected List<WorldBorderListener> getListeners() {
        return Lists.newArrayList(this.listeners);
    }

    public void addListener(WorldBorderListener listener) {
        this.listeners.add(listener);
    }

    public void setMaxWorldBorderRadius(int i) {
        this.maxWorldBorderRadius = i;
        this.area.onMaxWorldBorderRadiusChanged();
    }

    public int getMaxWorldBorderRadius() {
        return this.maxWorldBorderRadius;
    }

    public double getBuffer() {
        return this.buffer;
    }

    public void setBuffer(double buffer) {
        this.buffer = buffer;
        for (WorldBorderListener worldBorderListener : this.getListeners()) {
            worldBorderListener.onSafeZoneChanged(this, buffer);
        }
    }

    public double getDamagePerBlock() {
        return this.damagePerBlock;
    }

    public void setDamagePerBlock(double damagePerBlock) {
        this.damagePerBlock = damagePerBlock;
        for (WorldBorderListener worldBorderListener : this.getListeners()) {
            worldBorderListener.onDamagePerBlockChanged(this, damagePerBlock);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public double getShrinkingSpeed() {
        return this.area.getShrinkingSpeed();
    }

    public int getWarningTime() {
        return this.warningTime;
    }

    public void setWarningTime(int warningTime) {
        this.warningTime = warningTime;
        for (WorldBorderListener worldBorderListener : this.getListeners()) {
            worldBorderListener.onWarningTimeChanged(this, warningTime);
        }
    }

    public int getWarningBlocks() {
        return this.warningBlocks;
    }

    public void setWarningBlocks(int warningBlocks) {
        this.warningBlocks = warningBlocks;
        for (WorldBorderListener worldBorderListener : this.getListeners()) {
            worldBorderListener.onWarningBlocksChanged(this, warningBlocks);
        }
    }

    public void tick() {
        this.area = this.area.getAreaInstance();
    }

    public void save(LevelProperties levelProperties) {
        levelProperties.setBorderSize(this.getSize());
        levelProperties.setBorderCenterX(this.getCenterX());
        levelProperties.borderCenterZ(this.getCenterZ());
        levelProperties.setBorderSafeZone(this.getBuffer());
        levelProperties.setBorderDamagePerBlock(this.getDamagePerBlock());
        levelProperties.setBorderWarningBlocks(this.getWarningBlocks());
        levelProperties.setBorderWarningTime(this.getWarningTime());
        levelProperties.setBorderSizeLerpTarget(this.getTargetSize());
        levelProperties.setBorderSizeLerpTime(this.getTargetRemainingTime());
    }

    public void load(LevelProperties levelProperties) {
        this.setCenter(levelProperties.getBorderCenterX(), levelProperties.getBorderCenterZ());
        this.setDamagePerBlock(levelProperties.getBorderDamagePerBlock());
        this.setBuffer(levelProperties.getBorderSafeZone());
        this.setWarningBlocks(levelProperties.getBorderWarningBlocks());
        this.setWarningTime(levelProperties.getBorderWarningTime());
        if (levelProperties.getBorderSizeLerpTime() > 0L) {
            this.interpolateSize(levelProperties.getBorderSize(), levelProperties.getBorderSizeLerpTarget(), levelProperties.getBorderSizeLerpTime());
        } else {
            this.setSize(levelProperties.getBorderSize());
        }
    }

    class StaticArea
    implements Area {
        private final double size;
        private double boundWest;
        private double boundNorth;
        private double boundEast;
        private double boundSouth;
        private VoxelShape field_17653;

        public StaticArea(double d) {
            this.size = d;
            this.recalculateBounds();
        }

        @Override
        public double getBoundWest() {
            return this.boundWest;
        }

        @Override
        public double getBoundEast() {
            return this.boundEast;
        }

        @Override
        public double getBoundNorth() {
            return this.boundNorth;
        }

        @Override
        public double getBoundSouth() {
            return this.boundSouth;
        }

        @Override
        public double getSize() {
            return this.size;
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public WorldBorderStage getStage() {
            return WorldBorderStage.STATIONARY;
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public double getShrinkingSpeed() {
            return 0.0;
        }

        @Override
        public long getTargetRemainingTime() {
            return 0L;
        }

        @Override
        public double getTargetSize() {
            return this.size;
        }

        private void recalculateBounds() {
            this.boundWest = Math.max(WorldBorder.this.getCenterX() - this.size / 2.0, (double)(-WorldBorder.this.maxWorldBorderRadius));
            this.boundNorth = Math.max(WorldBorder.this.getCenterZ() - this.size / 2.0, (double)(-WorldBorder.this.maxWorldBorderRadius));
            this.boundEast = Math.min(WorldBorder.this.getCenterX() + this.size / 2.0, (double)WorldBorder.this.maxWorldBorderRadius);
            this.boundSouth = Math.min(WorldBorder.this.getCenterZ() + this.size / 2.0, (double)WorldBorder.this.maxWorldBorderRadius);
            this.field_17653 = VoxelShapes.combineAndSimplify(VoxelShapes.UNBOUNDED, VoxelShapes.cuboid(Math.floor(this.getBoundWest()), Double.NEGATIVE_INFINITY, Math.floor(this.getBoundNorth()), Math.ceil(this.getBoundEast()), Double.POSITIVE_INFINITY, Math.ceil(this.getBoundSouth())), BooleanBiFunction.ONLY_FIRST);
        }

        @Override
        public void onMaxWorldBorderRadiusChanged() {
            this.recalculateBounds();
        }

        @Override
        public void onCenterChanged() {
            this.recalculateBounds();
        }

        @Override
        public Area getAreaInstance() {
            return this;
        }

        @Override
        public VoxelShape method_17906() {
            return this.field_17653;
        }
    }

    class MovingArea
    implements Area {
        private final double oldSize;
        private final double newSize;
        private final long timeEnd;
        private final long timeStart;
        private final double timeDuration;

        private MovingArea(double d, double e, long l) {
            this.oldSize = d;
            this.newSize = e;
            this.timeDuration = l;
            this.timeStart = Util.getMeasuringTimeMs();
            this.timeEnd = this.timeStart + l;
        }

        @Override
        public double getBoundWest() {
            return Math.max(WorldBorder.this.getCenterX() - this.getSize() / 2.0, (double)(-WorldBorder.this.maxWorldBorderRadius));
        }

        @Override
        public double getBoundNorth() {
            return Math.max(WorldBorder.this.getCenterZ() - this.getSize() / 2.0, (double)(-WorldBorder.this.maxWorldBorderRadius));
        }

        @Override
        public double getBoundEast() {
            return Math.min(WorldBorder.this.getCenterX() + this.getSize() / 2.0, (double)WorldBorder.this.maxWorldBorderRadius);
        }

        @Override
        public double getBoundSouth() {
            return Math.min(WorldBorder.this.getCenterZ() + this.getSize() / 2.0, (double)WorldBorder.this.maxWorldBorderRadius);
        }

        @Override
        public double getSize() {
            double d = (double)(Util.getMeasuringTimeMs() - this.timeStart) / this.timeDuration;
            return d < 1.0 ? MathHelper.lerp(d, this.oldSize, this.newSize) : this.newSize;
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public double getShrinkingSpeed() {
            return Math.abs(this.oldSize - this.newSize) / (double)(this.timeEnd - this.timeStart);
        }

        @Override
        public long getTargetRemainingTime() {
            return this.timeEnd - Util.getMeasuringTimeMs();
        }

        @Override
        public double getTargetSize() {
            return this.newSize;
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public WorldBorderStage getStage() {
            return this.newSize < this.oldSize ? WorldBorderStage.SHRINKING : WorldBorderStage.GROWING;
        }

        @Override
        public void onCenterChanged() {
        }

        @Override
        public void onMaxWorldBorderRadiusChanged() {
        }

        @Override
        public Area getAreaInstance() {
            if (this.getTargetRemainingTime() <= 0L) {
                return new StaticArea(this.newSize);
            }
            return this;
        }

        @Override
        public VoxelShape method_17906() {
            return VoxelShapes.combineAndSimplify(VoxelShapes.UNBOUNDED, VoxelShapes.cuboid(Math.floor(this.getBoundWest()), Double.NEGATIVE_INFINITY, Math.floor(this.getBoundNorth()), Math.ceil(this.getBoundEast()), Double.POSITIVE_INFINITY, Math.ceil(this.getBoundSouth())), BooleanBiFunction.ONLY_FIRST);
        }
    }

    static interface Area {
        public double getBoundWest();

        public double getBoundEast();

        public double getBoundNorth();

        public double getBoundSouth();

        public double getSize();

        @Environment(value=EnvType.CLIENT)
        public double getShrinkingSpeed();

        public long getTargetRemainingTime();

        public double getTargetSize();

        @Environment(value=EnvType.CLIENT)
        public WorldBorderStage getStage();

        public void onMaxWorldBorderRadiusChanged();

        public void onCenterChanged();

        public Area getAreaInstance();

        public VoxelShape method_17906();
    }
}
