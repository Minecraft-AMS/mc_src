/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  org.apache.commons.lang3.mutable.MutableFloat
 *  org.apache.commons.lang3.mutable.MutableInt
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.SweepingEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.WeightedPicker;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

public class EnchantmentHelper {
    public static int getLevel(Enchantment enchantment, ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        Identifier identifier = Registry.ENCHANTMENT.getId(enchantment);
        NbtList nbtList = stack.getEnchantments();
        for (int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            Identifier identifier2 = Identifier.tryParse(nbtCompound.getString("id"));
            if (identifier2 == null || !identifier2.equals(identifier)) continue;
            return MathHelper.clamp(nbtCompound.getInt("lvl"), 0, 255);
        }
        return 0;
    }

    public static Map<Enchantment, Integer> get(ItemStack stack) {
        NbtList nbtList = stack.getItem() == Items.ENCHANTED_BOOK ? EnchantedBookItem.getEnchantmentNbt(stack) : stack.getEnchantments();
        return EnchantmentHelper.fromNbt(nbtList);
    }

    public static Map<Enchantment, Integer> fromNbt(NbtList list) {
        LinkedHashMap map = Maps.newLinkedHashMap();
        for (int i = 0; i < list.size(); ++i) {
            NbtCompound nbtCompound = list.getCompound(i);
            Registry.ENCHANTMENT.getOrEmpty(Identifier.tryParse(nbtCompound.getString("id"))).ifPresent(enchantment -> map.put(enchantment, nbtCompound.getInt("lvl")));
        }
        return map;
    }

    public static void set(Map<Enchantment, Integer> enchantments, ItemStack stack) {
        NbtList nbtList = new NbtList();
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            Enchantment enchantment = entry.getKey();
            if (enchantment == null) continue;
            int i = entry.getValue();
            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.putString("id", String.valueOf(Registry.ENCHANTMENT.getId(enchantment)));
            nbtCompound.putShort("lvl", (short)i);
            nbtList.add(nbtCompound);
            if (stack.getItem() != Items.ENCHANTED_BOOK) continue;
            EnchantedBookItem.addEnchantment(stack, new EnchantmentLevelEntry(enchantment, i));
        }
        if (nbtList.isEmpty()) {
            stack.removeSubTag("Enchantments");
        } else if (stack.getItem() != Items.ENCHANTED_BOOK) {
            stack.putSubTag("Enchantments", nbtList);
        }
    }

    private static void forEachEnchantment(Consumer consumer, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        NbtList nbtList = stack.getEnchantments();
        for (int i = 0; i < nbtList.size(); ++i) {
            String string = nbtList.getCompound(i).getString("id");
            int j = nbtList.getCompound(i).getInt("lvl");
            Registry.ENCHANTMENT.getOrEmpty(Identifier.tryParse(string)).ifPresent(enchantment -> consumer.accept((Enchantment)enchantment, j));
        }
    }

    private static void forEachEnchantment(Consumer consumer, Iterable<ItemStack> stacks) {
        for (ItemStack itemStack : stacks) {
            EnchantmentHelper.forEachEnchantment(consumer, itemStack);
        }
    }

    public static int getProtectionAmount(Iterable<ItemStack> equipment, DamageSource source) {
        MutableInt mutableInt = new MutableInt();
        EnchantmentHelper.forEachEnchantment((Enchantment enchantment, int level) -> mutableInt.add(enchantment.getProtectionAmount(level, source)), equipment);
        return mutableInt.intValue();
    }

    public static float getAttackDamage(ItemStack stack, EntityGroup group) {
        MutableFloat mutableFloat = new MutableFloat();
        EnchantmentHelper.forEachEnchantment((Enchantment enchantment, int level) -> mutableFloat.add(enchantment.getAttackDamage(level, group)), stack);
        return mutableFloat.floatValue();
    }

    public static float getSweepingMultiplier(LivingEntity entity) {
        int i = EnchantmentHelper.getEquipmentLevel(Enchantments.SWEEPING, entity);
        if (i > 0) {
            return SweepingEnchantment.getMultiplier(i);
        }
        return 0.0f;
    }

    public static void onUserDamaged(LivingEntity user, Entity attacker) {
        Consumer consumer = (enchantment, level) -> enchantment.onUserDamaged(user, attacker, level);
        if (user != null) {
            EnchantmentHelper.forEachEnchantment(consumer, user.getItemsEquipped());
        }
        if (attacker instanceof PlayerEntity) {
            EnchantmentHelper.forEachEnchantment(consumer, user.getMainHandStack());
        }
    }

    public static void onTargetDamaged(LivingEntity user, Entity target) {
        Consumer consumer = (enchantment, level) -> enchantment.onTargetDamaged(user, target, level);
        if (user != null) {
            EnchantmentHelper.forEachEnchantment(consumer, user.getItemsEquipped());
        }
        if (user instanceof PlayerEntity) {
            EnchantmentHelper.forEachEnchantment(consumer, user.getMainHandStack());
        }
    }

    public static int getEquipmentLevel(Enchantment enchantment, LivingEntity entity) {
        Collection<ItemStack> iterable = enchantment.getEquipment(entity).values();
        if (iterable == null) {
            return 0;
        }
        int i = 0;
        for (ItemStack itemStack : iterable) {
            int j = EnchantmentHelper.getLevel(enchantment, itemStack);
            if (j <= i) continue;
            i = j;
        }
        return i;
    }

    public static int getKnockback(LivingEntity entity) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.KNOCKBACK, entity);
    }

    public static int getFireAspect(LivingEntity entity) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.FIRE_ASPECT, entity);
    }

    public static int getRespiration(LivingEntity entity) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.RESPIRATION, entity);
    }

    public static int getDepthStrider(LivingEntity entity) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.DEPTH_STRIDER, entity);
    }

    public static int getEfficiency(LivingEntity entity) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.EFFICIENCY, entity);
    }

    public static int getLuckOfTheSea(ItemStack stack) {
        return EnchantmentHelper.getLevel(Enchantments.LUCK_OF_THE_SEA, stack);
    }

    public static int getLure(ItemStack stack) {
        return EnchantmentHelper.getLevel(Enchantments.LURE, stack);
    }

    public static int getLooting(LivingEntity entity) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.LOOTING, entity);
    }

    public static boolean hasAquaAffinity(LivingEntity entity) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.AQUA_AFFINITY, entity) > 0;
    }

    public static boolean hasFrostWalker(LivingEntity entity) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.FROST_WALKER, entity) > 0;
    }

    public static boolean hasSoulSpeed(LivingEntity entity) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.SOUL_SPEED, entity) > 0;
    }

    public static boolean hasBindingCurse(ItemStack stack) {
        return EnchantmentHelper.getLevel(Enchantments.BINDING_CURSE, stack) > 0;
    }

    public static boolean hasVanishingCurse(ItemStack stack) {
        return EnchantmentHelper.getLevel(Enchantments.VANISHING_CURSE, stack) > 0;
    }

    public static int getLoyalty(ItemStack stack) {
        return EnchantmentHelper.getLevel(Enchantments.LOYALTY, stack);
    }

    public static int getRiptide(ItemStack stack) {
        return EnchantmentHelper.getLevel(Enchantments.RIPTIDE, stack);
    }

    public static boolean hasChanneling(ItemStack stack) {
        return EnchantmentHelper.getLevel(Enchantments.CHANNELING, stack) > 0;
    }

    @Nullable
    public static Map.Entry<EquipmentSlot, ItemStack> chooseEquipmentWith(Enchantment enchantment, LivingEntity entity) {
        return EnchantmentHelper.chooseEquipmentWith(enchantment, entity, stack -> true);
    }

    @Nullable
    public static Map.Entry<EquipmentSlot, ItemStack> chooseEquipmentWith(Enchantment enchantment, LivingEntity entity, Predicate<ItemStack> condition) {
        Map<EquipmentSlot, ItemStack> map = enchantment.getEquipment(entity);
        if (map.isEmpty()) {
            return null;
        }
        ArrayList list = Lists.newArrayList();
        for (Map.Entry<EquipmentSlot, ItemStack> entry : map.entrySet()) {
            ItemStack itemStack = entry.getValue();
            if (itemStack.isEmpty() || EnchantmentHelper.getLevel(enchantment, itemStack) <= 0 || !condition.test(itemStack)) continue;
            list.add(entry);
        }
        return list.isEmpty() ? null : (Map.Entry)list.get(entity.getRandom().nextInt(list.size()));
    }

    public static int calculateRequiredExperienceLevel(Random random, int slotIndex, int bookshelfCount, ItemStack stack) {
        Item item = stack.getItem();
        int i = item.getEnchantability();
        if (i <= 0) {
            return 0;
        }
        if (bookshelfCount > 15) {
            bookshelfCount = 15;
        }
        int j = random.nextInt(8) + 1 + (bookshelfCount >> 1) + random.nextInt(bookshelfCount + 1);
        if (slotIndex == 0) {
            return Math.max(j / 3, 1);
        }
        if (slotIndex == 1) {
            return j * 2 / 3 + 1;
        }
        return Math.max(j, bookshelfCount * 2);
    }

    public static ItemStack enchant(Random random, ItemStack target, int level, boolean treasureAllowed) {
        boolean bl;
        List<EnchantmentLevelEntry> list = EnchantmentHelper.generateEnchantments(random, target, level, treasureAllowed);
        boolean bl2 = bl = target.getItem() == Items.BOOK;
        if (bl) {
            target = new ItemStack(Items.ENCHANTED_BOOK);
        }
        for (EnchantmentLevelEntry enchantmentLevelEntry : list) {
            if (bl) {
                EnchantedBookItem.addEnchantment(target, enchantmentLevelEntry);
                continue;
            }
            target.addEnchantment(enchantmentLevelEntry.enchantment, enchantmentLevelEntry.level);
        }
        return target;
    }

    public static List<EnchantmentLevelEntry> generateEnchantments(Random random, ItemStack stack, int level, boolean treasureAllowed) {
        ArrayList list = Lists.newArrayList();
        Item item = stack.getItem();
        int i = item.getEnchantability();
        if (i <= 0) {
            return list;
        }
        level += 1 + random.nextInt(i / 4 + 1) + random.nextInt(i / 4 + 1);
        float f = (random.nextFloat() + random.nextFloat() - 1.0f) * 0.15f;
        List<EnchantmentLevelEntry> list2 = EnchantmentHelper.getPossibleEntries(level = MathHelper.clamp(Math.round((float)level + (float)level * f), 1, Integer.MAX_VALUE), stack, treasureAllowed);
        if (!list2.isEmpty()) {
            list.add(WeightedPicker.getRandom(random, list2));
            while (random.nextInt(50) <= level) {
                EnchantmentHelper.removeConflicts(list2, (EnchantmentLevelEntry)Util.getLast(list));
                if (list2.isEmpty()) break;
                list.add(WeightedPicker.getRandom(random, list2));
                level /= 2;
            }
        }
        return list;
    }

    public static void removeConflicts(List<EnchantmentLevelEntry> possibleEntries, EnchantmentLevelEntry pickedEntry) {
        Iterator<EnchantmentLevelEntry> iterator = possibleEntries.iterator();
        while (iterator.hasNext()) {
            if (pickedEntry.enchantment.canCombine(iterator.next().enchantment)) continue;
            iterator.remove();
        }
    }

    public static boolean isCompatible(Collection<Enchantment> existing, Enchantment candidate) {
        for (Enchantment enchantment : existing) {
            if (enchantment.canCombine(candidate)) continue;
            return false;
        }
        return true;
    }

    public static List<EnchantmentLevelEntry> getPossibleEntries(int power, ItemStack stack, boolean treasureAllowed) {
        ArrayList list = Lists.newArrayList();
        Item item = stack.getItem();
        boolean bl = stack.getItem() == Items.BOOK;
        block0: for (Enchantment enchantment : Registry.ENCHANTMENT) {
            if (enchantment.isTreasure() && !treasureAllowed || !enchantment.isAvailableForRandomSelection() || !enchantment.type.isAcceptableItem(item) && !bl) continue;
            for (int i = enchantment.getMaxLevel(); i > enchantment.getMinLevel() - 1; --i) {
                if (power < enchantment.getMinPower(i) || power > enchantment.getMaxPower(i)) continue;
                list.add(new EnchantmentLevelEntry(enchantment, i));
                continue block0;
            }
        }
        return list;
    }

    @FunctionalInterface
    static interface Consumer {
        public void accept(Enchantment var1, int var2);
    }
}

