/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.boss.dragon;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathMinHeap;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.boss.dragon.phase.Phase;
import net.minecraft.entity.boss.dragon.phase.PhaseManager;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.dimension.TheEndDimension;
import net.minecraft.world.gen.feature.EndPortalFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class EnderDragonEntity
extends MobEntity
implements Monster {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final TrackedData<Integer> PHASE_TYPE = DataTracker.registerData(EnderDragonEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TargetPredicate CLOSE_PLAYER_PREDICATE = new TargetPredicate().setBaseMaxDistance(64.0);
    public final double[][] field_7026 = new double[64][3];
    public int field_7010 = -1;
    public final EnderDragonPart[] parts;
    public final EnderDragonPart partHead;
    public final EnderDragonPart partNeck;
    public final EnderDragonPart partBody;
    public final EnderDragonPart partTail1;
    public final EnderDragonPart partTail2;
    public final EnderDragonPart partTail3;
    public final EnderDragonPart partWingRight;
    public final EnderDragonPart partWingLeft;
    public float field_7019;
    public float field_7030;
    public boolean field_7027;
    public int field_7031;
    public EnderCrystalEntity connectedCrystal;
    private final EnderDragonFight fight;
    private final PhaseManager phaseManager;
    private int field_7018 = 100;
    private int field_7029;
    private final PathNode[] field_7012 = new PathNode[24];
    private final int[] field_7025 = new int[24];
    private final PathMinHeap field_7008 = new PathMinHeap();

    public EnderDragonEntity(EntityType<? extends EnderDragonEntity> entityType, World world) {
        super((EntityType<? extends MobEntity>)EntityType.ENDER_DRAGON, world);
        this.partHead = new EnderDragonPart(this, "head", 1.0f, 1.0f);
        this.partNeck = new EnderDragonPart(this, "neck", 3.0f, 3.0f);
        this.partBody = new EnderDragonPart(this, "body", 5.0f, 3.0f);
        this.partTail1 = new EnderDragonPart(this, "tail", 2.0f, 2.0f);
        this.partTail2 = new EnderDragonPart(this, "tail", 2.0f, 2.0f);
        this.partTail3 = new EnderDragonPart(this, "tail", 2.0f, 2.0f);
        this.partWingRight = new EnderDragonPart(this, "wing", 4.0f, 2.0f);
        this.partWingLeft = new EnderDragonPart(this, "wing", 4.0f, 2.0f);
        this.parts = new EnderDragonPart[]{this.partHead, this.partNeck, this.partBody, this.partTail1, this.partTail2, this.partTail3, this.partWingRight, this.partWingLeft};
        this.setHealth(this.getMaximumHealth());
        this.noClip = true;
        this.ignoreCameraFrustum = true;
        this.fight = !world.isClient && world.dimension instanceof TheEndDimension ? ((TheEndDimension)world.dimension).method_12513() : null;
        this.phaseManager = new PhaseManager(this);
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(200.0);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.getDataTracker().startTracking(PHASE_TYPE, PhaseType.HOVER.getTypeId());
    }

    public double[] method_6817(int i, float f) {
        if (this.getHealth() <= 0.0f) {
            f = 0.0f;
        }
        f = 1.0f - f;
        int j = this.field_7010 - i & 0x3F;
        int k = this.field_7010 - i - 1 & 0x3F;
        double[] ds = new double[3];
        double d = this.field_7026[j][0];
        double e = MathHelper.wrapDegrees(this.field_7026[k][0] - d);
        ds[0] = d + e * (double)f;
        d = this.field_7026[j][1];
        e = this.field_7026[k][1] - d;
        ds[1] = d + e * (double)f;
        ds[2] = MathHelper.lerp((double)f, this.field_7026[j][2], this.field_7026[k][2]);
        return ds;
    }

    @Override
    public void tickMovement() {
        int ac;
        float m;
        double k;
        double j;
        double e;
        float g;
        float f;
        if (this.world.isClient) {
            this.setHealth(this.getHealth());
            if (!this.isSilent()) {
                f = MathHelper.cos(this.field_7030 * ((float)Math.PI * 2));
                g = MathHelper.cos(this.field_7019 * ((float)Math.PI * 2));
                if (g <= -0.3f && f >= -0.3f) {
                    this.world.playSound(this.x, this.y, this.z, SoundEvents.ENTITY_ENDER_DRAGON_FLAP, this.getSoundCategory(), 5.0f, 0.8f + this.random.nextFloat() * 0.3f, false);
                }
                if (!this.phaseManager.getCurrent().method_6848() && --this.field_7018 < 0) {
                    this.world.playSound(this.x, this.y, this.z, SoundEvents.ENTITY_ENDER_DRAGON_GROWL, this.getSoundCategory(), 2.5f, 0.8f + this.random.nextFloat() * 0.3f, false);
                    this.field_7018 = 200 + this.random.nextInt(200);
                }
            }
        }
        this.field_7019 = this.field_7030;
        if (this.getHealth() <= 0.0f) {
            f = (this.random.nextFloat() - 0.5f) * 8.0f;
            g = (this.random.nextFloat() - 0.5f) * 4.0f;
            float h = (this.random.nextFloat() - 0.5f) * 8.0f;
            this.world.addParticle(ParticleTypes.EXPLOSION, this.x + (double)f, this.y + 2.0 + (double)g, this.z + (double)h, 0.0, 0.0, 0.0);
            return;
        }
        this.method_6830();
        Vec3d vec3d = this.getVelocity();
        g = 0.2f / (MathHelper.sqrt(EnderDragonEntity.squaredHorizontalLength(vec3d)) * 10.0f + 1.0f);
        this.field_7030 = this.phaseManager.getCurrent().method_6848() ? (this.field_7030 += 0.1f) : (this.field_7027 ? (this.field_7030 += g * 0.5f) : (this.field_7030 += (g *= (float)Math.pow(2.0, vec3d.y))));
        this.yaw = MathHelper.wrapDegrees(this.yaw);
        if (this.isAiDisabled()) {
            this.field_7030 = 0.5f;
            return;
        }
        if (this.field_7010 < 0) {
            for (int i = 0; i < this.field_7026.length; ++i) {
                this.field_7026[i][0] = this.yaw;
                this.field_7026[i][1] = this.y;
            }
        }
        if (++this.field_7010 == this.field_7026.length) {
            this.field_7010 = 0;
        }
        this.field_7026[this.field_7010][0] = this.yaw;
        this.field_7026[this.field_7010][1] = this.y;
        if (this.world.isClient) {
            if (this.field_6210 > 0) {
                double d = this.x + (this.field_6224 - this.x) / (double)this.field_6210;
                e = this.y + (this.field_6245 - this.y) / (double)this.field_6210;
                j = this.z + (this.field_6263 - this.z) / (double)this.field_6210;
                k = MathHelper.wrapDegrees(this.field_6284 - (double)this.yaw);
                this.yaw = (float)((double)this.yaw + k / (double)this.field_6210);
                this.pitch = (float)((double)this.pitch + (this.field_6221 - (double)this.pitch) / (double)this.field_6210);
                --this.field_6210;
                this.updatePosition(d, e, j);
                this.setRotation(this.yaw, this.pitch);
            }
            this.phaseManager.getCurrent().clientTick();
        } else {
            Vec3d vec3d2;
            Phase phase = this.phaseManager.getCurrent();
            phase.serverTick();
            if (this.phaseManager.getCurrent() != phase) {
                phase = this.phaseManager.getCurrent();
                phase.serverTick();
            }
            if ((vec3d2 = phase.getTarget()) != null) {
                e = vec3d2.x - this.x;
                j = vec3d2.y - this.y;
                k = vec3d2.z - this.z;
                double l = e * e + j * j + k * k;
                m = phase.method_6846();
                double n = MathHelper.sqrt(e * e + k * k);
                if (n > 0.0) {
                    j = MathHelper.clamp(j / n, (double)(-m), (double)m);
                }
                this.setVelocity(this.getVelocity().add(0.0, j * 0.01, 0.0));
                this.yaw = MathHelper.wrapDegrees(this.yaw);
                double o = MathHelper.clamp(MathHelper.wrapDegrees(180.0 - MathHelper.atan2(e, k) * 57.2957763671875 - (double)this.yaw), -50.0, 50.0);
                Vec3d vec3d3 = vec3d2.subtract(this.x, this.y, this.z).normalize();
                Vec3d vec3d4 = new Vec3d(MathHelper.sin(this.yaw * ((float)Math.PI / 180)), this.getVelocity().y, -MathHelper.cos(this.yaw * ((float)Math.PI / 180))).normalize();
                float p = Math.max(((float)vec3d4.dotProduct(vec3d3) + 0.5f) / 1.5f, 0.0f);
                this.field_6267 *= 0.8f;
                this.field_6267 = (float)((double)this.field_6267 + o * (double)phase.method_6847());
                this.yaw += this.field_6267 * 0.1f;
                float q = (float)(2.0 / (l + 1.0));
                float r = 0.06f;
                this.updateVelocity(0.06f * (p * q + (1.0f - q)), new Vec3d(0.0, 0.0, -1.0));
                if (this.field_7027) {
                    this.move(MovementType.SELF, this.getVelocity().multiply(0.8f));
                } else {
                    this.move(MovementType.SELF, this.getVelocity());
                }
                Vec3d vec3d5 = this.getVelocity().normalize();
                double s = 0.8 + 0.15 * (vec3d5.dotProduct(vec3d4) + 1.0) / 2.0;
                this.setVelocity(this.getVelocity().multiply(s, 0.91f, s));
            }
        }
        this.field_6283 = this.yaw;
        Vec3d[] vec3ds = new Vec3d[this.parts.length];
        for (int t = 0; t < this.parts.length; ++t) {
            vec3ds[t] = new Vec3d(this.parts[t].x, this.parts[t].y, this.parts[t].z);
        }
        float u = (float)(this.method_6817(5, 1.0f)[1] - this.method_6817(10, 1.0f)[1]) * 10.0f * ((float)Math.PI / 180);
        float v = MathHelper.cos(u);
        float w = MathHelper.sin(u);
        float x = this.yaw * ((float)Math.PI / 180);
        float y = MathHelper.sin(x);
        float z = MathHelper.cos(x);
        this.partBody.tick();
        this.partBody.refreshPositionAndAngles(this.x + (double)(y * 0.5f), this.y, this.z - (double)(z * 0.5f), 0.0f, 0.0f);
        this.partWingRight.tick();
        this.partWingRight.refreshPositionAndAngles(this.x + (double)(z * 4.5f), this.y + 2.0, this.z + (double)(y * 4.5f), 0.0f, 0.0f);
        this.partWingLeft.tick();
        this.partWingLeft.refreshPositionAndAngles(this.x - (double)(z * 4.5f), this.y + 2.0, this.z - (double)(y * 4.5f), 0.0f, 0.0f);
        if (!this.world.isClient && this.hurtTime == 0) {
            this.method_6825(this.world.getEntities(this, this.partWingRight.getBoundingBox().expand(4.0, 2.0, 4.0).offset(0.0, -2.0, 0.0), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR));
            this.method_6825(this.world.getEntities(this, this.partWingLeft.getBoundingBox().expand(4.0, 2.0, 4.0).offset(0.0, -2.0, 0.0), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR));
            this.method_6827(this.world.getEntities(this, this.partHead.getBoundingBox().expand(1.0), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR));
            this.method_6827(this.world.getEntities(this, this.partNeck.getBoundingBox().expand(1.0), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR));
        }
        double[] ds = this.method_6817(5, 1.0f);
        float aa = MathHelper.sin(this.yaw * ((float)Math.PI / 180) - this.field_6267 * 0.01f);
        float ab = MathHelper.cos(this.yaw * ((float)Math.PI / 180) - this.field_6267 * 0.01f);
        this.partHead.tick();
        this.partNeck.tick();
        m = this.method_6820(1.0f);
        this.partHead.refreshPositionAndAngles(this.x + (double)(aa * 6.5f * v), this.y + (double)m + (double)(w * 6.5f), this.z - (double)(ab * 6.5f * v), 0.0f, 0.0f);
        this.partNeck.refreshPositionAndAngles(this.x + (double)(aa * 5.5f * v), this.y + (double)m + (double)(w * 5.5f), this.z - (double)(ab * 5.5f * v), 0.0f, 0.0f);
        for (ac = 0; ac < 3; ++ac) {
            EnderDragonPart enderDragonPart = null;
            if (ac == 0) {
                enderDragonPart = this.partTail1;
            }
            if (ac == 1) {
                enderDragonPart = this.partTail2;
            }
            if (ac == 2) {
                enderDragonPart = this.partTail3;
            }
            double[] es = this.method_6817(12 + ac * 2, 1.0f);
            float ad = this.yaw * ((float)Math.PI / 180) + this.method_6832(es[0] - ds[0]) * ((float)Math.PI / 180);
            float ae = MathHelper.sin(ad);
            float af = MathHelper.cos(ad);
            float ag = 1.5f;
            float ah = (float)(ac + 1) * 2.0f;
            enderDragonPart.tick();
            enderDragonPart.refreshPositionAndAngles(this.x - (double)((y * 1.5f + ae * ah) * v), this.y + (es[1] - ds[1]) - (double)((ah + 1.5f) * w) + 1.5, this.z + (double)((z * 1.5f + af * ah) * v), 0.0f, 0.0f);
        }
        if (!this.world.isClient) {
            this.field_7027 = this.method_6821(this.partHead.getBoundingBox()) | this.method_6821(this.partNeck.getBoundingBox()) | this.method_6821(this.partBody.getBoundingBox());
            if (this.fight != null) {
                this.fight.updateFight(this);
            }
        }
        for (ac = 0; ac < this.parts.length; ++ac) {
            this.parts[ac].prevX = vec3ds[ac].x;
            this.parts[ac].prevY = vec3ds[ac].y;
            this.parts[ac].prevZ = vec3ds[ac].z;
        }
    }

    private float method_6820(float f) {
        double d;
        if (this.phaseManager.getCurrent().method_6848()) {
            d = -1.0;
        } else {
            double[] ds = this.method_6817(5, 1.0f);
            double[] es = this.method_6817(0, 1.0f);
            d = ds[1] - es[1];
        }
        return (float)d;
    }

    private void method_6830() {
        if (this.connectedCrystal != null) {
            if (this.connectedCrystal.removed) {
                this.connectedCrystal = null;
            } else if (this.age % 10 == 0 && this.getHealth() < this.getMaximumHealth()) {
                this.setHealth(this.getHealth() + 1.0f);
            }
        }
        if (this.random.nextInt(10) == 0) {
            List<EnderCrystalEntity> list = this.world.getNonSpectatingEntities(EnderCrystalEntity.class, this.getBoundingBox().expand(32.0));
            EnderCrystalEntity enderCrystalEntity = null;
            double d = Double.MAX_VALUE;
            for (EnderCrystalEntity enderCrystalEntity2 : list) {
                double e = enderCrystalEntity2.squaredDistanceTo(this);
                if (!(e < d)) continue;
                d = e;
                enderCrystalEntity = enderCrystalEntity2;
            }
            this.connectedCrystal = enderCrystalEntity;
        }
    }

    private void method_6825(List<Entity> list) {
        double d = (this.partBody.getBoundingBox().x1 + this.partBody.getBoundingBox().x2) / 2.0;
        double e = (this.partBody.getBoundingBox().z1 + this.partBody.getBoundingBox().z2) / 2.0;
        for (Entity entity : list) {
            if (!(entity instanceof LivingEntity)) continue;
            double f = entity.x - d;
            double g = entity.z - e;
            double h = f * f + g * g;
            entity.addVelocity(f / h * 4.0, 0.2f, g / h * 4.0);
            if (this.phaseManager.getCurrent().method_6848() || ((LivingEntity)entity).getLastAttackedTime() >= entity.age - 2) continue;
            entity.damage(DamageSource.mob(this), 5.0f);
            this.dealDamage(this, entity);
        }
    }

    private void method_6827(List<Entity> list) {
        for (int i = 0; i < list.size(); ++i) {
            Entity entity = list.get(i);
            if (!(entity instanceof LivingEntity)) continue;
            entity.damage(DamageSource.mob(this), 10.0f);
            this.dealDamage(this, entity);
        }
    }

    private float method_6832(double d) {
        return (float)MathHelper.wrapDegrees(d);
    }

    private boolean method_6821(Box box) {
        int i = MathHelper.floor(box.x1);
        int j = MathHelper.floor(box.y1);
        int k = MathHelper.floor(box.z1);
        int l = MathHelper.floor(box.x2);
        int m = MathHelper.floor(box.y2);
        int n = MathHelper.floor(box.z2);
        boolean bl = false;
        boolean bl2 = false;
        for (int o = i; o <= l; ++o) {
            for (int p = j; p <= m; ++p) {
                for (int q = k; q <= n; ++q) {
                    BlockPos blockPos = new BlockPos(o, p, q);
                    BlockState blockState = this.world.getBlockState(blockPos);
                    Block block = blockState.getBlock();
                    if (blockState.isAir() || blockState.getMaterial() == Material.FIRE) continue;
                    if (!this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING) || BlockTags.DRAGON_IMMUNE.contains(block)) {
                        bl = true;
                        continue;
                    }
                    bl2 = this.world.removeBlock(blockPos, false) || bl2;
                }
            }
        }
        if (bl2) {
            BlockPos blockPos2 = new BlockPos(i + this.random.nextInt(l - i + 1), j + this.random.nextInt(m - j + 1), k + this.random.nextInt(n - k + 1));
            this.world.playLevelEvent(2008, blockPos2, 0);
        }
        return bl;
    }

    public boolean damagePart(EnderDragonPart enderDragonPart, DamageSource damageSource, float f) {
        f = this.phaseManager.getCurrent().modifyDamageTaken(damageSource, f);
        if (enderDragonPart != this.partHead) {
            f = f / 4.0f + Math.min(f, 1.0f);
        }
        if (f < 0.01f) {
            return false;
        }
        if (damageSource.getAttacker() instanceof PlayerEntity || damageSource.isExplosive()) {
            float g = this.getHealth();
            this.method_6819(damageSource, f);
            if (this.getHealth() <= 0.0f && !this.phaseManager.getCurrent().method_6848()) {
                this.setHealth(1.0f);
                this.phaseManager.setPhase(PhaseType.DYING);
            }
            if (this.phaseManager.getCurrent().method_6848()) {
                this.field_7029 = (int)((float)this.field_7029 + (g - this.getHealth()));
                if ((float)this.field_7029 > 0.25f * this.getMaximumHealth()) {
                    this.field_7029 = 0;
                    this.phaseManager.setPhase(PhaseType.TAKEOFF);
                }
            }
        }
        return true;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source instanceof EntityDamageSource && ((EntityDamageSource)source).method_5549()) {
            this.damagePart(this.partBody, source, amount);
        }
        return false;
    }

    protected boolean method_6819(DamageSource damageSource, float f) {
        return super.damage(damageSource, f);
    }

    @Override
    public void kill() {
        this.remove();
        if (this.fight != null) {
            this.fight.updateFight(this);
            this.fight.dragonKilled(this);
        }
    }

    @Override
    protected void updatePostDeath() {
        if (this.fight != null) {
            this.fight.updateFight(this);
        }
        ++this.field_7031;
        if (this.field_7031 >= 180 && this.field_7031 <= 200) {
            float f = (this.random.nextFloat() - 0.5f) * 8.0f;
            float g = (this.random.nextFloat() - 0.5f) * 4.0f;
            float h = (this.random.nextFloat() - 0.5f) * 8.0f;
            this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.x + (double)f, this.y + 2.0 + (double)g, this.z + (double)h, 0.0, 0.0, 0.0);
        }
        boolean bl = this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT);
        int i = 500;
        if (this.fight != null && !this.fight.hasPreviouslyKilled()) {
            i = 12000;
        }
        if (!this.world.isClient) {
            if (this.field_7031 > 150 && this.field_7031 % 5 == 0 && bl) {
                this.method_6824(MathHelper.floor((float)i * 0.08f));
            }
            if (this.field_7031 == 1) {
                this.world.playGlobalEvent(1028, new BlockPos(this), 0);
            }
        }
        this.move(MovementType.SELF, new Vec3d(0.0, 0.1f, 0.0));
        this.yaw += 20.0f;
        this.field_6283 = this.yaw;
        if (this.field_7031 == 200 && !this.world.isClient) {
            if (bl) {
                this.method_6824(MathHelper.floor((float)i * 0.2f));
            }
            if (this.fight != null) {
                this.fight.dragonKilled(this);
            }
            this.remove();
        }
    }

    private void method_6824(int i) {
        while (i > 0) {
            int j = ExperienceOrbEntity.roundToOrbSize(i);
            i -= j;
            this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.x, this.y, this.z, j));
        }
    }

    public int method_6818() {
        if (this.field_7012[0] == null) {
            for (int i = 0; i < 24; ++i) {
                int m;
                int l;
                int j = 5;
                int k = i;
                if (i < 12) {
                    l = MathHelper.floor(60.0f * MathHelper.cos(2.0f * ((float)(-Math.PI) + 0.2617994f * (float)k)));
                    m = MathHelper.floor(60.0f * MathHelper.sin(2.0f * ((float)(-Math.PI) + 0.2617994f * (float)k)));
                } else if (i < 20) {
                    l = MathHelper.floor(40.0f * MathHelper.cos(2.0f * ((float)(-Math.PI) + 0.3926991f * (float)(k -= 12))));
                    m = MathHelper.floor(40.0f * MathHelper.sin(2.0f * ((float)(-Math.PI) + 0.3926991f * (float)k)));
                    j += 10;
                } else {
                    l = MathHelper.floor(20.0f * MathHelper.cos(2.0f * ((float)(-Math.PI) + 0.7853982f * (float)(k -= 20))));
                    m = MathHelper.floor(20.0f * MathHelper.sin(2.0f * ((float)(-Math.PI) + 0.7853982f * (float)k)));
                }
                int n = Math.max(this.world.getSeaLevel() + 10, this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(l, 0, m)).getY() + j);
                this.field_7012[i] = new PathNode(l, n, m);
            }
            this.field_7025[0] = 6146;
            this.field_7025[1] = 8197;
            this.field_7025[2] = 8202;
            this.field_7025[3] = 16404;
            this.field_7025[4] = 32808;
            this.field_7025[5] = 32848;
            this.field_7025[6] = 65696;
            this.field_7025[7] = 131392;
            this.field_7025[8] = 131712;
            this.field_7025[9] = 263424;
            this.field_7025[10] = 526848;
            this.field_7025[11] = 525313;
            this.field_7025[12] = 1581057;
            this.field_7025[13] = 3166214;
            this.field_7025[14] = 2138120;
            this.field_7025[15] = 6373424;
            this.field_7025[16] = 4358208;
            this.field_7025[17] = 12910976;
            this.field_7025[18] = 9044480;
            this.field_7025[19] = 9706496;
            this.field_7025[20] = 15216640;
            this.field_7025[21] = 0xD0E000;
            this.field_7025[22] = 11763712;
            this.field_7025[23] = 0x7E0000;
        }
        return this.method_6822(this.x, this.y, this.z);
    }

    public int method_6822(double d, double e, double f) {
        float g = 10000.0f;
        int i = 0;
        PathNode pathNode = new PathNode(MathHelper.floor(d), MathHelper.floor(e), MathHelper.floor(f));
        int j = 0;
        if (this.fight == null || this.fight.getAliveEndCrystals() == 0) {
            j = 12;
        }
        for (int k = j; k < 24; ++k) {
            float h;
            if (this.field_7012[k] == null || !((h = this.field_7012[k].getSquaredDistance(pathNode)) < g)) continue;
            g = h;
            i = k;
        }
        return i;
    }

    @Nullable
    public Path method_6833(int i, int j, @Nullable PathNode pathNode) {
        PathNode pathNode2;
        for (int k = 0; k < 24; ++k) {
            pathNode2 = this.field_7012[k];
            pathNode2.field_42 = false;
            pathNode2.heapWeight = 0.0f;
            pathNode2.field_36 = 0.0f;
            pathNode2.field_34 = 0.0f;
            pathNode2.field_35 = null;
            pathNode2.heapIndex = -1;
        }
        PathNode pathNode3 = this.field_7012[i];
        pathNode2 = this.field_7012[j];
        pathNode3.field_36 = 0.0f;
        pathNode3.heapWeight = pathNode3.field_34 = pathNode3.getDistance(pathNode2);
        this.field_7008.clear();
        this.field_7008.push(pathNode3);
        PathNode pathNode4 = pathNode3;
        int l = 0;
        if (this.fight == null || this.fight.getAliveEndCrystals() == 0) {
            l = 12;
        }
        while (!this.field_7008.isEmpty()) {
            int n;
            PathNode pathNode5 = this.field_7008.pop();
            if (pathNode5.equals(pathNode2)) {
                if (pathNode != null) {
                    pathNode.field_35 = pathNode2;
                    pathNode2 = pathNode;
                }
                return this.method_6826(pathNode3, pathNode2);
            }
            if (pathNode5.getDistance(pathNode2) < pathNode4.getDistance(pathNode2)) {
                pathNode4 = pathNode5;
            }
            pathNode5.field_42 = true;
            int m = 0;
            for (n = 0; n < 24; ++n) {
                if (this.field_7012[n] != pathNode5) continue;
                m = n;
                break;
            }
            for (n = l; n < 24; ++n) {
                if ((this.field_7025[m] & 1 << n) <= 0) continue;
                PathNode pathNode6 = this.field_7012[n];
                if (pathNode6.field_42) continue;
                float f = pathNode5.field_36 + pathNode5.getDistance(pathNode6);
                if (pathNode6.isInHeap() && !(f < pathNode6.field_36)) continue;
                pathNode6.field_35 = pathNode5;
                pathNode6.field_36 = f;
                pathNode6.field_34 = pathNode6.getDistance(pathNode2);
                if (pathNode6.isInHeap()) {
                    this.field_7008.setNodeWeight(pathNode6, pathNode6.field_36 + pathNode6.field_34);
                    continue;
                }
                pathNode6.heapWeight = pathNode6.field_36 + pathNode6.field_34;
                this.field_7008.push(pathNode6);
            }
        }
        if (pathNode4 == pathNode3) {
            return null;
        }
        LOGGER.debug("Failed to find path from {} to {}", (Object)i, (Object)j);
        if (pathNode != null) {
            pathNode.field_35 = pathNode4;
            pathNode4 = pathNode;
        }
        return this.method_6826(pathNode3, pathNode4);
    }

    private Path method_6826(PathNode pathNode, PathNode pathNode2) {
        ArrayList list = Lists.newArrayList();
        PathNode pathNode3 = pathNode2;
        list.add(0, pathNode3);
        while (pathNode3.field_35 != null) {
            pathNode3 = pathNode3.field_35;
            list.add(0, pathNode3);
        }
        return new Path(list, new BlockPos(pathNode2.x, pathNode2.y, pathNode2.z), true);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("DragonPhase", this.phaseManager.getCurrent().getType().getTypeId());
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (tag.contains("DragonPhase")) {
            this.phaseManager.setPhase(PhaseType.getFromId(tag.getInt("DragonPhase")));
        }
    }

    @Override
    protected void checkDespawn() {
    }

    public EnderDragonPart[] method_5690() {
        return this.parts;
    }

    @Override
    public boolean collides() {
        return false;
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ENDER_DRAGON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_ENDER_DRAGON_HURT;
    }

    @Override
    protected float getSoundVolume() {
        return 5.0f;
    }

    @Environment(value=EnvType.CLIENT)
    public float method_6823(int i, double[] ds, double[] es) {
        double d;
        Phase phase = this.phaseManager.getCurrent();
        PhaseType<? extends Phase> phaseType = phase.getType();
        if (phaseType == PhaseType.LANDING || phaseType == PhaseType.TAKEOFF) {
            BlockPos blockPos = this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPortalFeature.ORIGIN);
            float f = Math.max(MathHelper.sqrt(blockPos.getSquaredDistance(this.getPos(), true)) / 4.0f, 1.0f);
            d = (float)i / f;
        } else {
            d = phase.method_6848() ? (double)i : (i == 6 ? 0.0 : es[1] - ds[1]);
        }
        return (float)d;
    }

    public Vec3d method_6834(float f) {
        Vec3d vec3d;
        Phase phase = this.phaseManager.getCurrent();
        PhaseType<? extends Phase> phaseType = phase.getType();
        if (phaseType == PhaseType.LANDING || phaseType == PhaseType.TAKEOFF) {
            BlockPos blockPos = this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPortalFeature.ORIGIN);
            float g = Math.max(MathHelper.sqrt(blockPos.getSquaredDistance(this.getPos(), true)) / 4.0f, 1.0f);
            float h = 6.0f / g;
            float i = this.pitch;
            float j = 1.5f;
            this.pitch = -h * 1.5f * 5.0f;
            vec3d = this.getRotationVec(f);
            this.pitch = i;
        } else if (phase.method_6848()) {
            float k = this.pitch;
            float g = 1.5f;
            this.pitch = -45.0f;
            vec3d = this.getRotationVec(f);
            this.pitch = k;
        } else {
            vec3d = this.getRotationVec(f);
        }
        return vec3d;
    }

    public void crystalDestroyed(EnderCrystalEntity crystal, BlockPos pos, DamageSource source) {
        PlayerEntity playerEntity = source.getAttacker() instanceof PlayerEntity ? (PlayerEntity)source.getAttacker() : this.world.getClosestPlayer(CLOSE_PLAYER_PREDICATE, pos.getX(), pos.getY(), pos.getZ());
        if (crystal == this.connectedCrystal) {
            this.damagePart(this.partHead, DamageSource.explosion(playerEntity), 10.0f);
        }
        this.phaseManager.getCurrent().crystalDestroyed(crystal, pos, source, playerEntity);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (PHASE_TYPE.equals(data) && this.world.isClient) {
            this.phaseManager.setPhase(PhaseType.getFromId(this.getDataTracker().get(PHASE_TYPE)));
        }
        super.onTrackedDataSet(data);
    }

    public PhaseManager getPhaseManager() {
        return this.phaseManager;
    }

    @Nullable
    public EnderDragonFight getFight() {
        return this.fight;
    }

    @Override
    public boolean addStatusEffect(StatusEffectInstance effect) {
        return false;
    }

    @Override
    protected boolean canStartRiding(Entity entity) {
        return false;
    }

    @Override
    public boolean canUsePortals() {
        return false;
    }
}

