/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMultimap
 *  com.google.common.collect.ImmutableMultimap$Builder
 *  com.google.common.collect.Multimap
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Wearable;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ArmorItem
extends Item
implements Wearable {
    private static final UUID[] MODIFIERS = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
    public static final DispenserBehavior DISPENSER_BEHAVIOR = new ItemDispenserBehavior(){

        @Override
        protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
            return ArmorItem.dispenseArmor(pointer, stack) ? stack : super.dispenseSilently(pointer, stack);
        }
    };
    protected final EquipmentSlot slot;
    private final int protection;
    private final float toughness;
    protected final float knockbackResistance;
    protected final ArmorMaterial type;
    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    public static boolean dispenseArmor(BlockPointer pointer, ItemStack armor) {
        BlockPos blockPos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
        List<Entity> list = pointer.getWorld().getEntitiesByClass(LivingEntity.class, new Box(blockPos), EntityPredicates.EXCEPT_SPECTATOR.and(new EntityPredicates.Equipable(armor)));
        if (list.isEmpty()) {
            return false;
        }
        LivingEntity livingEntity = (LivingEntity)list.get(0);
        EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(armor);
        ItemStack itemStack = armor.split(1);
        livingEntity.equipStack(equipmentSlot, itemStack);
        if (livingEntity instanceof MobEntity) {
            ((MobEntity)livingEntity).setEquipmentDropChance(equipmentSlot, 2.0f);
            ((MobEntity)livingEntity).setPersistent();
        }
        return true;
    }

    public ArmorItem(ArmorMaterial material, EquipmentSlot slot, Item.Settings settings) {
        super(settings.maxDamageIfAbsent(material.getDurability(slot)));
        this.type = material;
        this.slot = slot;
        this.protection = material.getProtectionAmount(slot);
        this.toughness = material.getToughness();
        this.knockbackResistance = material.getKnockbackResistance();
        DispenserBlock.registerBehavior(this, DISPENSER_BEHAVIOR);
        ImmutableMultimap.Builder builder = ImmutableMultimap.builder();
        UUID uUID = MODIFIERS[slot.getEntitySlotId()];
        builder.put((Object)EntityAttributes.GENERIC_ARMOR, (Object)new EntityAttributeModifier(uUID, "Armor modifier", (double)this.protection, EntityAttributeModifier.Operation.ADDITION));
        builder.put((Object)EntityAttributes.GENERIC_ARMOR_TOUGHNESS, (Object)new EntityAttributeModifier(uUID, "Armor toughness", (double)this.toughness, EntityAttributeModifier.Operation.ADDITION));
        if (material == ArmorMaterials.NETHERITE) {
            builder.put((Object)EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, (Object)new EntityAttributeModifier(uUID, "Armor knockback resistance", (double)this.knockbackResistance, EntityAttributeModifier.Operation.ADDITION));
        }
        this.attributeModifiers = builder.build();
    }

    public EquipmentSlot getSlotType() {
        return this.slot;
    }

    @Override
    public int getEnchantability() {
        return this.type.getEnchantability();
    }

    public ArmorMaterial getMaterial() {
        return this.type;
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return this.type.getRepairIngredient().test(ingredient) || super.canRepair(stack, ingredient);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(itemStack);
        ItemStack itemStack2 = user.getEquippedStack(equipmentSlot);
        if (itemStack2.isEmpty()) {
            user.equipStack(equipmentSlot, itemStack.copy());
            if (!world.isClient()) {
                user.incrementStat(Stats.USED.getOrCreateStat(this));
            }
            itemStack.setCount(0);
            return TypedActionResult.success(itemStack, world.isClient());
        }
        return TypedActionResult.fail(itemStack);
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        if (slot == this.slot) {
            return this.attributeModifiers;
        }
        return super.getAttributeModifiers(slot);
    }

    public int getProtection() {
        return this.protection;
    }

    public float getToughness() {
        return this.toughness;
    }

    @Override
    @Nullable
    public SoundEvent getEquipSound() {
        return this.getMaterial().getEquipSound();
    }
}

