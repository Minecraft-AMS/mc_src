/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.util.Pair
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.potion;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class PotionUtil {
    public static final String CUSTOM_POTION_EFFECTS_KEY = "CustomPotionEffects";
    public static final String CUSTOM_POTION_COLOR_KEY = "CustomPotionColor";
    public static final String POTION_KEY = "Potion";
    private static final int DEFAULT_COLOR = 0xF800F8;
    private static final Text NONE_TEXT = Text.translatable("effect.none").formatted(Formatting.GRAY);

    public static List<StatusEffectInstance> getPotionEffects(ItemStack stack) {
        return PotionUtil.getPotionEffects(stack.getNbt());
    }

    public static List<StatusEffectInstance> getPotionEffects(Potion potion, Collection<StatusEffectInstance> custom) {
        ArrayList list = Lists.newArrayList();
        list.addAll(potion.getEffects());
        list.addAll(custom);
        return list;
    }

    public static List<StatusEffectInstance> getPotionEffects(@Nullable NbtCompound nbt) {
        ArrayList list = Lists.newArrayList();
        list.addAll(PotionUtil.getPotion(nbt).getEffects());
        PotionUtil.getCustomPotionEffects(nbt, list);
        return list;
    }

    public static List<StatusEffectInstance> getCustomPotionEffects(ItemStack stack) {
        return PotionUtil.getCustomPotionEffects(stack.getNbt());
    }

    public static List<StatusEffectInstance> getCustomPotionEffects(@Nullable NbtCompound nbt) {
        ArrayList list = Lists.newArrayList();
        PotionUtil.getCustomPotionEffects(nbt, list);
        return list;
    }

    public static void getCustomPotionEffects(@Nullable NbtCompound nbt, List<StatusEffectInstance> list) {
        if (nbt != null && nbt.contains(CUSTOM_POTION_EFFECTS_KEY, 9)) {
            NbtList nbtList = nbt.getList(CUSTOM_POTION_EFFECTS_KEY, 10);
            for (int i = 0; i < nbtList.size(); ++i) {
                NbtCompound nbtCompound = nbtList.getCompound(i);
                StatusEffectInstance statusEffectInstance = StatusEffectInstance.fromNbt(nbtCompound);
                if (statusEffectInstance == null) continue;
                list.add(statusEffectInstance);
            }
        }
    }

    public static int getColor(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        if (nbtCompound != null && nbtCompound.contains(CUSTOM_POTION_COLOR_KEY, 99)) {
            return nbtCompound.getInt(CUSTOM_POTION_COLOR_KEY);
        }
        return PotionUtil.getPotion(stack) == Potions.EMPTY ? 0xF800F8 : PotionUtil.getColor(PotionUtil.getPotionEffects(stack));
    }

    public static int getColor(Potion potion) {
        return potion == Potions.EMPTY ? 0xF800F8 : PotionUtil.getColor(potion.getEffects());
    }

    public static int getColor(Collection<StatusEffectInstance> effects) {
        int i = 3694022;
        if (effects.isEmpty()) {
            return 3694022;
        }
        float f = 0.0f;
        float g = 0.0f;
        float h = 0.0f;
        int j = 0;
        for (StatusEffectInstance statusEffectInstance : effects) {
            if (!statusEffectInstance.shouldShowParticles()) continue;
            int k = statusEffectInstance.getEffectType().getColor();
            int l = statusEffectInstance.getAmplifier() + 1;
            f += (float)(l * (k >> 16 & 0xFF)) / 255.0f;
            g += (float)(l * (k >> 8 & 0xFF)) / 255.0f;
            h += (float)(l * (k >> 0 & 0xFF)) / 255.0f;
            j += l;
        }
        if (j == 0) {
            return 0;
        }
        f = f / (float)j * 255.0f;
        g = g / (float)j * 255.0f;
        h = h / (float)j * 255.0f;
        return (int)f << 16 | (int)g << 8 | (int)h;
    }

    public static Potion getPotion(ItemStack stack) {
        return PotionUtil.getPotion(stack.getNbt());
    }

    public static Potion getPotion(@Nullable NbtCompound compound) {
        if (compound == null) {
            return Potions.EMPTY;
        }
        return Potion.byId(compound.getString(POTION_KEY));
    }

    public static ItemStack setPotion(ItemStack stack, Potion potion) {
        Identifier identifier = Registries.POTION.getId(potion);
        if (potion == Potions.EMPTY) {
            stack.removeSubNbt(POTION_KEY);
        } else {
            stack.getOrCreateNbt().putString(POTION_KEY, identifier.toString());
        }
        return stack;
    }

    public static ItemStack setCustomPotionEffects(ItemStack stack, Collection<StatusEffectInstance> effects) {
        if (effects.isEmpty()) {
            return stack;
        }
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        NbtList nbtList = nbtCompound.getList(CUSTOM_POTION_EFFECTS_KEY, 9);
        for (StatusEffectInstance statusEffectInstance : effects) {
            nbtList.add(statusEffectInstance.writeNbt(new NbtCompound()));
        }
        nbtCompound.put(CUSTOM_POTION_EFFECTS_KEY, nbtList);
        return stack;
    }

    public static void buildTooltip(ItemStack stack, List<Text> list, float durationMultiplier) {
        PotionUtil.buildTooltip(PotionUtil.getPotionEffects(stack), list, durationMultiplier);
    }

    public static void buildTooltip(List<StatusEffectInstance> statusEffects, List<Text> list, float durationMultiplier) {
        ArrayList list2 = Lists.newArrayList();
        if (statusEffects.isEmpty()) {
            list.add(NONE_TEXT);
        } else {
            for (StatusEffectInstance statusEffectInstance : statusEffects) {
                MutableText mutableText = Text.translatable(statusEffectInstance.getTranslationKey());
                StatusEffect statusEffect = statusEffectInstance.getEffectType();
                Map<EntityAttribute, EntityAttributeModifier> map = statusEffect.getAttributeModifiers();
                if (!map.isEmpty()) {
                    for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : map.entrySet()) {
                        EntityAttributeModifier entityAttributeModifier = entry.getValue();
                        EntityAttributeModifier entityAttributeModifier2 = new EntityAttributeModifier(entityAttributeModifier.getName(), statusEffect.adjustModifierAmount(statusEffectInstance.getAmplifier(), entityAttributeModifier), entityAttributeModifier.getOperation());
                        list2.add(new Pair((Object)entry.getKey(), (Object)entityAttributeModifier2));
                    }
                }
                if (statusEffectInstance.getAmplifier() > 0) {
                    mutableText = Text.translatable("potion.withAmplifier", mutableText, Text.translatable("potion.potency." + statusEffectInstance.getAmplifier()));
                }
                if (!statusEffectInstance.isDurationBelow(20)) {
                    mutableText = Text.translatable("potion.withDuration", mutableText, StatusEffectUtil.durationToString(statusEffectInstance, durationMultiplier));
                }
                list.add(mutableText.formatted(statusEffect.getCategory().getFormatting()));
            }
        }
        if (!list2.isEmpty()) {
            list.add(ScreenTexts.EMPTY);
            list.add(Text.translatable("potion.whenDrank").formatted(Formatting.DARK_PURPLE));
            for (Pair pair : list2) {
                EntityAttributeModifier entityAttributeModifier3 = (EntityAttributeModifier)pair.getSecond();
                double d = entityAttributeModifier3.getValue();
                double e = entityAttributeModifier3.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_BASE || entityAttributeModifier3.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_TOTAL ? entityAttributeModifier3.getValue() * 100.0 : entityAttributeModifier3.getValue();
                if (d > 0.0) {
                    list.add(Text.translatable("attribute.modifier.plus." + entityAttributeModifier3.getOperation().getId(), ItemStack.MODIFIER_FORMAT.format(e), Text.translatable(((EntityAttribute)pair.getFirst()).getTranslationKey())).formatted(Formatting.BLUE));
                    continue;
                }
                if (!(d < 0.0)) continue;
                list.add(Text.translatable("attribute.modifier.take." + entityAttributeModifier3.getOperation().getId(), ItemStack.MODIFIER_FORMAT.format(e *= -1.0), Text.translatable(((EntityAttribute)pair.getFirst()).getTranslationKey())).formatted(Formatting.RED));
            }
        }
    }
}

