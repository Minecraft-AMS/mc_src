/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.entity;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BellBlockEntity
extends BlockEntity
implements Tickable {
    private long field_19155;
    public int ringTicks;
    public boolean isRinging;
    public Direction lastSideHit;
    private List<LivingEntity> field_19156;
    private boolean field_19157;
    private int field_19158;

    public BellBlockEntity() {
        super(BlockEntityType.BELL);
    }

    @Override
    public boolean onBlockAction(int i, int j) {
        if (i == 1) {
            this.method_20219();
            this.field_19158 = 0;
            this.lastSideHit = Direction.byId(j);
            this.ringTicks = 0;
            this.isRinging = true;
            return true;
        }
        return super.onBlockAction(i, j);
    }

    @Override
    public void tick() {
        if (this.isRinging) {
            ++this.ringTicks;
        }
        if (this.ringTicks >= 50) {
            this.isRinging = false;
            this.ringTicks = 0;
        }
        if (this.ringTicks >= 5 && this.field_19158 == 0 && this.method_20523()) {
            this.field_19157 = true;
            this.playResonateSound();
        }
        if (this.field_19157) {
            if (this.field_19158 < 40) {
                ++this.field_19158;
            } else {
                this.method_20521(this.world);
                this.method_20218(this.world);
                this.field_19157 = false;
            }
        }
    }

    private void playResonateSound() {
        this.world.playSound(null, this.getPos(), SoundEvents.BLOCK_BELL_RESONATE, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    public void activate(Direction direction) {
        BlockPos blockPos = this.getPos();
        this.lastSideHit = direction;
        if (this.isRinging) {
            this.ringTicks = 0;
        } else {
            this.isRinging = true;
        }
        this.world.addBlockAction(blockPos, this.getCachedState().getBlock(), 1, direction.getId());
    }

    private void method_20219() {
        BlockPos blockPos = this.getPos();
        if (this.world.getTime() > this.field_19155 + 60L || this.field_19156 == null) {
            this.field_19155 = this.world.getTime();
            Box box = new Box(blockPos).expand(48.0);
            this.field_19156 = this.world.getNonSpectatingEntities(LivingEntity.class, box);
        }
        if (!this.world.isClient) {
            for (LivingEntity livingEntity : this.field_19156) {
                if (!livingEntity.isAlive() || livingEntity.removed || !blockPos.isWithinDistance(livingEntity.getPos(), 32.0)) continue;
                livingEntity.getBrain().putMemory(MemoryModuleType.HEARD_BELL_TIME, this.world.getTime());
            }
        }
    }

    private boolean method_20523() {
        BlockPos blockPos = this.getPos();
        for (LivingEntity livingEntity : this.field_19156) {
            if (!livingEntity.isAlive() || livingEntity.removed || !blockPos.isWithinDistance(livingEntity.getPos(), 32.0) || !livingEntity.getType().isTaggedWith(EntityTypeTags.RAIDERS)) continue;
            return true;
        }
        return false;
    }

    private void method_20521(World world) {
        if (world.isClient) {
            return;
        }
        this.field_19156.stream().filter(this::method_20518).forEach(this::method_20520);
    }

    private void method_20218(World world) {
        if (!world.isClient) {
            return;
        }
        BlockPos blockPos = this.getPos();
        AtomicInteger atomicInteger = new AtomicInteger(16700985);
        int i = (int)this.field_19156.stream().filter(livingEntity -> blockPos.isWithinDistance(livingEntity.getPos(), 48.0)).count();
        this.field_19156.stream().filter(this::method_20518).forEach(livingEntity -> {
            float f = 1.0f;
            float g = MathHelper.sqrt((livingEntity.getX() - (double)blockPos.getX()) * (livingEntity.getX() - (double)blockPos.getX()) + (livingEntity.getZ() - (double)blockPos.getZ()) * (livingEntity.getZ() - (double)blockPos.getZ()));
            double d = (double)((float)blockPos.getX() + 0.5f) + (double)(1.0f / g) * (livingEntity.getX() - (double)blockPos.getX());
            double e = (double)((float)blockPos.getZ() + 0.5f) + (double)(1.0f / g) * (livingEntity.getZ() - (double)blockPos.getZ());
            int j = MathHelper.clamp((i - 21) / -2, 3, 15);
            for (int k = 0; k < j; ++k) {
                atomicInteger.addAndGet(5);
                double h = (double)(atomicInteger.get() >> 16 & 0xFF) / 255.0;
                double l = (double)(atomicInteger.get() >> 8 & 0xFF) / 255.0;
                double m = (double)(atomicInteger.get() & 0xFF) / 255.0;
                world.addParticle(ParticleTypes.ENTITY_EFFECT, d, (float)blockPos.getY() + 0.5f, e, h, l, m);
            }
        });
    }

    private boolean method_20518(LivingEntity livingEntity) {
        return livingEntity.isAlive() && !livingEntity.removed && this.getPos().isWithinDistance(livingEntity.getPos(), 48.0) && livingEntity.getType().isTaggedWith(EntityTypeTags.RAIDERS);
    }

    private void method_20520(LivingEntity livingEntity) {
        livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 60));
    }
}

