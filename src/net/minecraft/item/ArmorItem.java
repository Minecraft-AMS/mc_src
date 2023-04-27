/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMultimap
 *  com.google.common.collect.ImmutableMultimap$Builder
 *  com.google.common.collect.Multimap
 */
package net.minecraft.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.EnumMap;
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
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class ArmorItem
extends Item
implements Equipment {
    private static final EnumMap<Type, UUID> MODIFIERS = Util.make(new EnumMap(Type.class), uuidMap -> {
        uuidMap.put(Type.BOOTS, UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"));
        uuidMap.put(Type.LEGGINGS, UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"));
        uuidMap.put(Type.CHESTPLATE, UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"));
        uuidMap.put(Type.HELMET, UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150"));
    });
    public static final DispenserBehavior DISPENSER_BEHAVIOR = new ItemDispenserBehavior(){

        @Override
        protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
            return ArmorItem.dispenseArmor(pointer, stack) ? stack : super.dispenseSilently(pointer, stack);
        }
    };
    protected final Type type;
    private final int protection;
    private final float toughness;
    protected final float knockbackResistance;
    protected final ArmorMaterial material;
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

    public ArmorItem(ArmorMaterial material, Type type, Item.Settings settings) {
        super(settings.maxDamageIfAbsent(material.getDurability(type)));
        this.material = material;
        this.type = type;
        this.protection = material.getProtection(type);
        this.toughness = material.getToughness();
        this.knockbackResistance = material.getKnockbackResistance();
        DispenserBlock.registerBehavior(this, DISPENSER_BEHAVIOR);
        ImmutableMultimap.Builder builder = ImmutableMultimap.builder();
        UUID uUID = MODIFIERS.get((Object)type);
        builder.put((Object)EntityAttributes.GENERIC_ARMOR, (Object)new EntityAttributeModifier(uUID, "Armor modifier", (double)this.protection, EntityAttributeModifier.Operation.ADDITION));
        builder.put((Object)EntityAttributes.GENERIC_ARMOR_TOUGHNESS, (Object)new EntityAttributeModifier(uUID, "Armor toughness", (double)this.toughness, EntityAttributeModifier.Operation.ADDITION));
        if (material == ArmorMaterials.NETHERITE) {
            builder.put((Object)EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, (Object)new EntityAttributeModifier(uUID, "Armor knockback resistance", (double)this.knockbackResistance, EntityAttributeModifier.Operation.ADDITION));
        }
        this.attributeModifiers = builder.build();
    }

    public Type getType() {
        return this.type;
    }

    @Override
    public int getEnchantability() {
        return this.material.getEnchantability();
    }

    public ArmorMaterial getMaterial() {
        return this.material;
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return this.material.getRepairIngredient().test(ingredient) || super.canRepair(stack, ingredient);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return this.equipAndSwap(this, world, user, hand);
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        if (slot == this.type.getEquipmentSlot()) {
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
    public EquipmentSlot getSlotType() {
        return this.type.getEquipmentSlot();
    }

    @Override
    public SoundEvent getEquipSound() {
        return this.getMaterial().getEquipSound();
    }

    public static final class Type
    extends Enum<Type> {
        public static final /* enum */ Type HELMET = new Type(EquipmentSlot.HEAD, "helmet");
        public static final /* enum */ Type CHESTPLATE = new Type(EquipmentSlot.CHEST, "chestplate");
        public static final /* enum */ Type LEGGINGS = new Type(EquipmentSlot.LEGS, "leggings");
        public static final /* enum */ Type BOOTS = new Type(EquipmentSlot.FEET, "boots");
        private final EquipmentSlot equipmentSlot;
        private final String name;
        private static final /* synthetic */ Type[] field_41940;

        public static Type[] values() {
            return (Type[])field_41940.clone();
        }

        public static Type valueOf(String string) {
            return Enum.valueOf(Type.class, string);
        }

        private Type(EquipmentSlot equipmentSlot, String name) {
            this.equipmentSlot = equipmentSlot;
            this.name = name;
        }

        public EquipmentSlot getEquipmentSlot() {
            return this.equipmentSlot;
        }

        public String getName() {
            return this.name;
        }

        private static /* synthetic */ Type[] method_48401() {
            return new Type[]{HELMET, CHESTPLATE, LEGGINGS, BOOTS};
        }

        static {
            field_41940 = Type.method_48401();
        }
    }
}

