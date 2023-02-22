/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.projectile;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class ArrowEntity
extends ProjectileEntity {
    private static final TrackedData<Integer> COLOR = DataTracker.registerData(ArrowEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private Potion potion = Potions.EMPTY;
    private final Set<StatusEffectInstance> effects = Sets.newHashSet();
    private boolean colorSet;

    public ArrowEntity(EntityType<? extends ArrowEntity> entityType, World world) {
        super((EntityType<? extends ProjectileEntity>)entityType, world);
    }

    public ArrowEntity(World world, double x, double y, double z) {
        super(EntityType.ARROW, x, y, z, world);
    }

    public ArrowEntity(World world, LivingEntity owner) {
        super(EntityType.ARROW, owner, world);
    }

    public void initFromStack(ItemStack stack) {
        if (stack.getItem() == Items.TIPPED_ARROW) {
            int i;
            this.potion = PotionUtil.getPotion(stack);
            List<StatusEffectInstance> collection = PotionUtil.getCustomPotionEffects(stack);
            if (!collection.isEmpty()) {
                for (StatusEffectInstance statusEffectInstance : collection) {
                    this.effects.add(new StatusEffectInstance(statusEffectInstance));
                }
            }
            if ((i = ArrowEntity.getCustomPotionColor(stack)) == -1) {
                this.initColor();
            } else {
                this.setColor(i);
            }
        } else if (stack.getItem() == Items.ARROW) {
            this.potion = Potions.EMPTY;
            this.effects.clear();
            this.dataTracker.set(COLOR, -1);
        }
    }

    public static int getCustomPotionColor(ItemStack stack) {
        CompoundTag compoundTag = stack.getTag();
        if (compoundTag != null && compoundTag.contains("CustomPotionColor", 99)) {
            return compoundTag.getInt("CustomPotionColor");
        }
        return -1;
    }

    private void initColor() {
        this.colorSet = false;
        this.dataTracker.set(COLOR, PotionUtil.getColor(PotionUtil.getPotionEffects(this.potion, this.effects)));
    }

    public void addEffect(StatusEffectInstance effect) {
        this.effects.add(effect);
        this.getDataTracker().set(COLOR, PotionUtil.getColor(PotionUtil.getPotionEffects(this.potion, this.effects)));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(COLOR, -1);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.world.isClient) {
            if (this.inGround) {
                if (this.inGroundTime % 5 == 0) {
                    this.spawnParticles(1);
                }
            } else {
                this.spawnParticles(2);
            }
        } else if (this.inGround && this.inGroundTime != 0 && !this.effects.isEmpty() && this.inGroundTime >= 600) {
            this.world.sendEntityStatus(this, (byte)0);
            this.potion = Potions.EMPTY;
            this.effects.clear();
            this.dataTracker.set(COLOR, -1);
        }
    }

    private void spawnParticles(int i) {
        int j = this.getColor();
        if (j == -1 || i <= 0) {
            return;
        }
        double d = (double)(j >> 16 & 0xFF) / 255.0;
        double e = (double)(j >> 8 & 0xFF) / 255.0;
        double f = (double)(j >> 0 & 0xFF) / 255.0;
        for (int k = 0; k < i; ++k) {
            this.world.addParticle(ParticleTypes.ENTITY_EFFECT, this.x + (this.random.nextDouble() - 0.5) * (double)this.getWidth(), this.y + this.random.nextDouble() * (double)this.getHeight(), this.z + (this.random.nextDouble() - 0.5) * (double)this.getWidth(), d, e, f);
        }
    }

    public int getColor() {
        return this.dataTracker.get(COLOR);
    }

    private void setColor(int color) {
        this.colorSet = true;
        this.dataTracker.set(COLOR, color);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        if (this.potion != Potions.EMPTY && this.potion != null) {
            tag.putString("Potion", Registry.POTION.getId(this.potion).toString());
        }
        if (this.colorSet) {
            tag.putInt("Color", this.getColor());
        }
        if (!this.effects.isEmpty()) {
            ListTag listTag = new ListTag();
            for (StatusEffectInstance statusEffectInstance : this.effects) {
                listTag.add(statusEffectInstance.toTag(new CompoundTag()));
            }
            tag.put("CustomPotionEffects", listTag);
        }
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (tag.contains("Potion", 8)) {
            this.potion = PotionUtil.getPotion(tag);
        }
        for (StatusEffectInstance statusEffectInstance : PotionUtil.getCustomPotionEffects(tag)) {
            this.addEffect(statusEffectInstance);
        }
        if (tag.contains("Color", 99)) {
            this.setColor(tag.getInt("Color"));
        } else {
            this.initColor();
        }
    }

    @Override
    protected void onHit(LivingEntity target) {
        super.onHit(target);
        for (StatusEffectInstance statusEffectInstance : this.potion.getEffects()) {
            target.addStatusEffect(new StatusEffectInstance(statusEffectInstance.getEffectType(), Math.max(statusEffectInstance.getDuration() / 8, 1), statusEffectInstance.getAmplifier(), statusEffectInstance.isAmbient(), statusEffectInstance.shouldShowParticles()));
        }
        if (!this.effects.isEmpty()) {
            for (StatusEffectInstance statusEffectInstance : this.effects) {
                target.addStatusEffect(statusEffectInstance);
            }
        }
    }

    @Override
    protected ItemStack asItemStack() {
        if (this.effects.isEmpty() && this.potion == Potions.EMPTY) {
            return new ItemStack(Items.ARROW);
        }
        ItemStack itemStack = new ItemStack(Items.TIPPED_ARROW);
        PotionUtil.setPotion(itemStack, this.potion);
        PotionUtil.setCustomPotionEffects(itemStack, this.effects);
        if (this.colorSet) {
            itemStack.getOrCreateTag().putInt("CustomPotionColor", this.getColor());
        }
        return itemStack;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte status) {
        if (status == 0) {
            int i = this.getColor();
            if (i != -1) {
                double d = (double)(i >> 16 & 0xFF) / 255.0;
                double e = (double)(i >> 8 & 0xFF) / 255.0;
                double f = (double)(i >> 0 & 0xFF) / 255.0;
                for (int j = 0; j < 20; ++j) {
                    this.world.addParticle(ParticleTypes.ENTITY_EFFECT, this.x + (this.random.nextDouble() - 0.5) * (double)this.getWidth(), this.y + this.random.nextDouble() * (double)this.getHeight(), this.z + (this.random.nextDouble() - 0.5) * (double)this.getWidth(), d, e, f);
                }
            }
        } else {
            super.handleStatus(status);
        }
    }
}
