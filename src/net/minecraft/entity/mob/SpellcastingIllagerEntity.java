/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.mob;

import java.util.EnumSet;
import java.util.function.IntFunction;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class SpellcastingIllagerEntity
extends IllagerEntity {
    private static final TrackedData<Byte> SPELL = DataTracker.registerData(SpellcastingIllagerEntity.class, TrackedDataHandlerRegistry.BYTE);
    protected int spellTicks;
    private Spell spell = Spell.NONE;

    protected SpellcastingIllagerEntity(EntityType<? extends SpellcastingIllagerEntity> entityType, World world) {
        super((EntityType<? extends IllagerEntity>)entityType, world);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SPELL, (byte)0);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.spellTicks = nbt.getInt("SpellTicks");
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("SpellTicks", this.spellTicks);
    }

    @Override
    public IllagerEntity.State getState() {
        if (this.isSpellcasting()) {
            return IllagerEntity.State.SPELLCASTING;
        }
        if (this.isCelebrating()) {
            return IllagerEntity.State.CELEBRATING;
        }
        return IllagerEntity.State.CROSSED;
    }

    public boolean isSpellcasting() {
        if (this.getWorld().isClient) {
            return this.dataTracker.get(SPELL) > 0;
        }
        return this.spellTicks > 0;
    }

    public void setSpell(Spell spell) {
        this.spell = spell;
        this.dataTracker.set(SPELL, (byte)spell.id);
    }

    protected Spell getSpell() {
        if (!this.getWorld().isClient) {
            return this.spell;
        }
        return Spell.byId(this.dataTracker.get(SPELL).byteValue());
    }

    @Override
    protected void mobTick() {
        super.mobTick();
        if (this.spellTicks > 0) {
            --this.spellTicks;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient && this.isSpellcasting()) {
            Spell spell = this.getSpell();
            double d = spell.particleVelocity[0];
            double e = spell.particleVelocity[1];
            double f = spell.particleVelocity[2];
            float g = this.bodyYaw * ((float)Math.PI / 180) + MathHelper.cos((float)this.age * 0.6662f) * 0.25f;
            float h = MathHelper.cos(g);
            float i = MathHelper.sin(g);
            this.getWorld().addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() + (double)h * 0.6, this.getY() + 1.8, this.getZ() + (double)i * 0.6, d, e, f);
            this.getWorld().addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() - (double)h * 0.6, this.getY() + 1.8, this.getZ() - (double)i * 0.6, d, e, f);
        }
    }

    protected int getSpellTicks() {
        return this.spellTicks;
    }

    protected abstract SoundEvent getCastSpellSound();

    protected static final class Spell
    extends Enum<Spell> {
        public static final /* enum */ Spell NONE = new Spell(0, 0.0, 0.0, 0.0);
        public static final /* enum */ Spell SUMMON_VEX = new Spell(1, 0.7, 0.7, 0.8);
        public static final /* enum */ Spell FANGS = new Spell(2, 0.4, 0.3, 0.35);
        public static final /* enum */ Spell WOLOLO = new Spell(3, 0.7, 0.5, 0.2);
        public static final /* enum */ Spell DISAPPEAR = new Spell(4, 0.3, 0.3, 0.8);
        public static final /* enum */ Spell BLINDNESS = new Spell(5, 0.1, 0.1, 0.2);
        private static final IntFunction<Spell> BY_ID;
        final int id;
        final double[] particleVelocity;
        private static final /* synthetic */ Spell[] field_7376;

        public static Spell[] values() {
            return (Spell[])field_7376.clone();
        }

        public static Spell valueOf(String string) {
            return Enum.valueOf(Spell.class, string);
        }

        private Spell(int id, double particleVelocityX, double particleVelocityY, double particleVelocityZ) {
            this.id = id;
            this.particleVelocity = new double[]{particleVelocityX, particleVelocityY, particleVelocityZ};
        }

        public static Spell byId(int id) {
            return BY_ID.apply(id);
        }

        private static /* synthetic */ Spell[] method_36658() {
            return new Spell[]{NONE, SUMMON_VEX, FANGS, WOLOLO, DISAPPEAR, BLINDNESS};
        }

        static {
            field_7376 = Spell.method_36658();
            BY_ID = ValueLists.createIdToValueFunction(spell -> spell.id, Spell.values(), ValueLists.OutOfBoundsHandling.ZERO);
        }
    }

    protected abstract class CastSpellGoal
    extends Goal {
        protected int spellCooldown;
        protected int startTime;

        protected CastSpellGoal() {
        }

        @Override
        public boolean canStart() {
            LivingEntity livingEntity = SpellcastingIllagerEntity.this.getTarget();
            if (livingEntity == null || !livingEntity.isAlive()) {
                return false;
            }
            if (SpellcastingIllagerEntity.this.isSpellcasting()) {
                return false;
            }
            return SpellcastingIllagerEntity.this.age >= this.startTime;
        }

        @Override
        public boolean shouldContinue() {
            LivingEntity livingEntity = SpellcastingIllagerEntity.this.getTarget();
            return livingEntity != null && livingEntity.isAlive() && this.spellCooldown > 0;
        }

        @Override
        public void start() {
            this.spellCooldown = this.getTickCount(this.getInitialCooldown());
            SpellcastingIllagerEntity.this.spellTicks = this.getSpellTicks();
            this.startTime = SpellcastingIllagerEntity.this.age + this.startTimeDelay();
            SoundEvent soundEvent = this.getSoundPrepare();
            if (soundEvent != null) {
                SpellcastingIllagerEntity.this.playSound(soundEvent, 1.0f, 1.0f);
            }
            SpellcastingIllagerEntity.this.setSpell(this.getSpell());
        }

        @Override
        public void tick() {
            --this.spellCooldown;
            if (this.spellCooldown == 0) {
                this.castSpell();
                SpellcastingIllagerEntity.this.playSound(SpellcastingIllagerEntity.this.getCastSpellSound(), 1.0f, 1.0f);
            }
        }

        protected abstract void castSpell();

        protected int getInitialCooldown() {
            return 20;
        }

        protected abstract int getSpellTicks();

        protected abstract int startTimeDelay();

        @Nullable
        protected abstract SoundEvent getSoundPrepare();

        protected abstract Spell getSpell();
    }

    protected class LookAtTargetGoal
    extends Goal {
        public LookAtTargetGoal() {
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            return SpellcastingIllagerEntity.this.getSpellTicks() > 0;
        }

        @Override
        public void start() {
            super.start();
            SpellcastingIllagerEntity.this.navigation.stop();
        }

        @Override
        public void stop() {
            super.stop();
            SpellcastingIllagerEntity.this.setSpell(Spell.NONE);
        }

        @Override
        public void tick() {
            if (SpellcastingIllagerEntity.this.getTarget() != null) {
                SpellcastingIllagerEntity.this.getLookControl().lookAt(SpellcastingIllagerEntity.this.getTarget(), SpellcastingIllagerEntity.this.getMaxHeadRotation(), SpellcastingIllagerEntity.this.getMaxLookPitchChange());
            }
        }
    }
}

