/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.damage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class EntityDamageSource
extends DamageSource {
    @Nullable
    protected final Entity source;
    private boolean field_5880;

    public EntityDamageSource(String name, @Nullable Entity entity) {
        super(name);
        this.source = entity;
    }

    public EntityDamageSource method_5550() {
        this.field_5880 = true;
        return this;
    }

    public boolean method_5549() {
        return this.field_5880;
    }

    @Override
    @Nullable
    public Entity getAttacker() {
        return this.source;
    }

    @Override
    public Text getDeathMessage(LivingEntity livingEntity) {
        ItemStack itemStack = this.source instanceof LivingEntity ? ((LivingEntity)this.source).getMainHandStack() : ItemStack.EMPTY;
        String string = "death.attack." + this.name;
        if (!itemStack.isEmpty() && itemStack.hasCustomName()) {
            return new TranslatableText(string + ".item", livingEntity.getDisplayName(), this.source.getDisplayName(), itemStack.toHoverableText());
        }
        return new TranslatableText(string, livingEntity.getDisplayName(), this.source.getDisplayName());
    }

    @Override
    public boolean isScaledWithDifficulty() {
        return this.source != null && this.source instanceof LivingEntity && !(this.source instanceof PlayerEntity);
    }

    @Override
    @Nullable
    public Vec3d method_5510() {
        return new Vec3d(this.source.x, this.source.y, this.source.z);
    }
}

