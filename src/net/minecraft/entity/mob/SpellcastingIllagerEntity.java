/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.mob;

import java.util.EnumSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class SpellcastingIllagerEntity
extends IllagerEntity {
    private static final TrackedData<Byte> SPELL = DataTracker.registerData(SpellcastingIllagerEntity.class, TrackedDataHandlerRegistry.BYTE);
    protected int spellTicks;
    private Spell spell = Spell.NONE;

    protected SpellcastingIllagerEntity(EntityType<? extends SpellcastingIllagerEntity> type, World world) {
        super((EntityType<? extends IllagerEntity>)type, world);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SPELL, (byte)0);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.spellTicks = tag.getInt("SpellTicks");
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("SpellTicks", this.spellTicks);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
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
        if (this.world.isClient) {
            return this.dataTracker.get(SPELL) > 0;
        }
        return this.spellTicks > 0;
    }

    public void setSpell(Spell spell) {
        this.spell = spell;
        this.dataTracker.set(SPELL, (byte)spell.id);
    }

    protected Spell getSpell() {
        if (!this.world.isClient) {
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
        if (this.world.isClient && this.isSpellcasting()) {
            Spell spell = this.getSpell();
            double d = spell.particleVelocity[0];
            double e = spell.particleVelocity[1];
            double f = spell.particleVelocity[2];
            float g = this.field_6283 * ((float)Math.PI / 180) + MathHelper.cos((float)this.age * 0.6662f) * 0.25f;
            float h = MathHelper.cos(g);
            float i = MathHelper.sin(g);
            this.world.addParticle(ParticleTypes.ENTITY_EFFECT, this.x + (double)h * 0.6, this.y + 1.8, this.z + (double)i * 0.6, d, e, f);
            this.world.addParticle(ParticleTypes.ENTITY_EFFECT, this.x - (double)h * 0.6, this.y + 1.8, this.z - (double)i * 0.6, d, e, f);
        }
    }

    protected int getSpellTicks() {
        return this.spellTicks;
    }

    protected abstract SoundEvent getCastSpellSound();

    public static enum Spell {
        NONE(0, 0.0, 0.0, 0.0),
        SUMMON_VEX(1, 0.7, 0.7, 0.8),
        FANGS(2, 0.4, 0.3, 0.35),
        WOLOLO(3, 0.7, 0.5, 0.2),
        DISAPPEAR(4, 0.3, 0.3, 0.8),
        BLINDNESS(5, 0.1, 0.1, 0.2);

        private final int id;
        private final double[] particleVelocity;

        private Spell(int id, double particleVelocityX, double particleVelocityY, double particleVelocityZ) {
            this.id = id;
            this.particleVelocity = new double[]{particleVelocityX, particleVelocityY, particleVelocityZ};
        }

        public static Spell byId(int id) {
            for (Spell spell : Spell.values()) {
                if (id != spell.id) continue;
                return spell;
            }
            return NONE;
        }
    }

    public abstract class CastSpellGoal
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
            this.spellCooldown = this.getInitialCooldown();
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

    public class LookAtTargetGoal
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
                SpellcastingIllagerEntity.this.getLookControl().lookAt(SpellcastingIllagerEntity.this.getTarget(), SpellcastingIllagerEntity.this.method_5986(), SpellcastingIllagerEntity.this.getLookPitchSpeed());
            }
        }
    }
}

