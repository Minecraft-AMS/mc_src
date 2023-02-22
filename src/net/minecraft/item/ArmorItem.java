/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multimap
 */
package net.minecraft.item;

import com.google.common.collect.Multimap;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class ArmorItem
extends Item {
    private static final UUID[] MODIFIERS = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
    public static final DispenserBehavior DISPENSER_BEHAVIOR = new ItemDispenserBehavior(){

        @Override
        protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
            ItemStack itemStack = ArmorItem.dispenseArmor(pointer, stack);
            return itemStack.isEmpty() ? super.dispenseSilently(pointer, stack) : itemStack;
        }
    };
    protected final EquipmentSlot slot;
    protected final int protection;
    protected final float toughness;
    protected final ArmorMaterial type;

    public static ItemStack dispenseArmor(BlockPointer pointer, ItemStack armor) {
        BlockPos blockPos = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
        List<Entity> list = pointer.getWorld().getEntities(LivingEntity.class, new Box(blockPos), EntityPredicates.EXCEPT_SPECTATOR.and(new EntityPredicates.CanPickup(armor)));
        if (list.isEmpty()) {
            return ItemStack.EMPTY;
        }
        LivingEntity livingEntity = (LivingEntity)list.get(0);
        EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(armor);
        ItemStack itemStack = armor.split(1);
        livingEntity.equipStack(equipmentSlot, itemStack);
        if (livingEntity instanceof MobEntity) {
            ((MobEntity)livingEntity).setEquipmentDropChance(equipmentSlot, 2.0f);
            ((MobEntity)livingEntity).setPersistent();
        }
        return armor;
    }

    public ArmorItem(ArmorMaterial material, EquipmentSlot slot, Item.Settings settings) {
        super(settings.maxDamageIfAbsent(material.getDurability(slot)));
        this.type = material;
        this.slot = slot;
        this.protection = material.getProtectionAmount(slot);
        this.toughness = material.getToughness();
        DispenserBlock.registerBehavior(this, DISPENSER_BEHAVIOR);
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
            itemStack.setCount(0);
            return new TypedActionResult<ItemStack>(ActionResult.SUCCESS, itemStack);
        }
        return new TypedActionResult<ItemStack>(ActionResult.FAIL, itemStack);
    }

    @Override
    public Multimap<String, EntityAttributeModifier> getModifiers(EquipmentSlot slot) {
        Multimap<String, EntityAttributeModifier> multimap = super.getModifiers(slot);
        if (slot == this.slot) {
            multimap.put((Object)EntityAttributes.ARMOR.getId(), (Object)new EntityAttributeModifier(MODIFIERS[slot.getEntitySlotId()], "Armor modifier", (double)this.protection, EntityAttributeModifier.Operation.ADDITION));
            multimap.put((Object)EntityAttributes.ARMOR_TOUGHNESS.getId(), (Object)new EntityAttributeModifier(MODIFIERS[slot.getEntitySlotId()], "Armor toughness", (double)this.toughness, EntityAttributeModifier.Operation.ADDITION));
        }
        return multimap;
    }

    public int getProtection() {
        return this.protection;
    }
}

