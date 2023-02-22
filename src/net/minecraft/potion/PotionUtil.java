/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.potion;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class PotionUtil {
    public static List<StatusEffectInstance> getPotionEffects(ItemStack stack) {
        return PotionUtil.getPotionEffects(stack.getTag());
    }

    public static List<StatusEffectInstance> getPotionEffects(Potion potion, Collection<StatusEffectInstance> custom) {
        ArrayList list = Lists.newArrayList();
        list.addAll(potion.getEffects());
        list.addAll(custom);
        return list;
    }

    public static List<StatusEffectInstance> getPotionEffects(@Nullable CompoundTag tag) {
        ArrayList list = Lists.newArrayList();
        list.addAll(PotionUtil.getPotion(tag).getEffects());
        PotionUtil.getCustomPotionEffects(tag, list);
        return list;
    }

    public static List<StatusEffectInstance> getCustomPotionEffects(ItemStack stack) {
        return PotionUtil.getCustomPotionEffects(stack.getTag());
    }

    public static List<StatusEffectInstance> getCustomPotionEffects(@Nullable CompoundTag tag) {
        ArrayList list = Lists.newArrayList();
        PotionUtil.getCustomPotionEffects(tag, list);
        return list;
    }

    public static void getCustomPotionEffects(@Nullable CompoundTag tag, List<StatusEffectInstance> list) {
        if (tag != null && tag.contains("CustomPotionEffects", 9)) {
            ListTag listTag = tag.getList("CustomPotionEffects", 10);
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag compoundTag = listTag.getCompound(i);
                StatusEffectInstance statusEffectInstance = StatusEffectInstance.fromTag(compoundTag);
                if (statusEffectInstance == null) continue;
                list.add(statusEffectInstance);
            }
        }
    }

    public static int getColor(ItemStack stack) {
        CompoundTag compoundTag = stack.getTag();
        if (compoundTag != null && compoundTag.contains("CustomPotionColor", 99)) {
            return compoundTag.getInt("CustomPotionColor");
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
        return PotionUtil.getPotion(stack.getTag());
    }

    public static Potion getPotion(@Nullable CompoundTag compound) {
        if (compound == null) {
            return Potions.EMPTY;
        }
        return Potion.byId(compound.getString("Potion"));
    }

    public static ItemStack setPotion(ItemStack stack, Potion potion) {
        Identifier identifier = Registry.POTION.getId(potion);
        if (potion == Potions.EMPTY) {
            stack.removeSubTag("Potion");
        } else {
            stack.getOrCreateTag().putString("Potion", identifier.toString());
        }
        return stack;
    }

    public static ItemStack setCustomPotionEffects(ItemStack stack, Collection<StatusEffectInstance> effects) {
        if (effects.isEmpty()) {
            return stack;
        }
        CompoundTag compoundTag = stack.getOrCreateTag();
        ListTag listTag = compoundTag.getList("CustomPotionEffects", 9);
        for (StatusEffectInstance statusEffectInstance : effects) {
            listTag.add(statusEffectInstance.toTag(new CompoundTag()));
        }
        compoundTag.put("CustomPotionEffects", listTag);
        return stack;
    }

    @Environment(value=EnvType.CLIENT)
    public static void buildTooltip(ItemStack stack, List<Text> list, float f) {
        List<StatusEffectInstance> list2 = PotionUtil.getPotionEffects(stack);
        ArrayList list3 = Lists.newArrayList();
        if (list2.isEmpty()) {
            list.add(new TranslatableText("effect.none", new Object[0]).formatted(Formatting.GRAY));
        } else {
            for (StatusEffectInstance statusEffectInstance : list2) {
                TranslatableText text = new TranslatableText(statusEffectInstance.getTranslationKey(), new Object[0]);
                StatusEffect statusEffect = statusEffectInstance.getEffectType();
                Map<EntityAttribute, EntityAttributeModifier> map = statusEffect.getAttributeModifiers();
                if (!map.isEmpty()) {
                    for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : map.entrySet()) {
                        EntityAttributeModifier entityAttributeModifier = entry.getValue();
                        EntityAttributeModifier entityAttributeModifier2 = new EntityAttributeModifier(entityAttributeModifier.getName(), statusEffect.adjustModifierAmount(statusEffectInstance.getAmplifier(), entityAttributeModifier), entityAttributeModifier.getOperation());
                        list3.add(new Pair<String, EntityAttributeModifier>(entry.getKey().getId(), entityAttributeModifier2));
                    }
                }
                if (statusEffectInstance.getAmplifier() > 0) {
                    text.append(" ").append(new TranslatableText("potion.potency." + statusEffectInstance.getAmplifier(), new Object[0]));
                }
                if (statusEffectInstance.getDuration() > 20) {
                    text.append(" (").append(StatusEffectUtil.durationToString(statusEffectInstance, f)).append(")");
                }
                list.add(text.formatted(statusEffect.getType().getFormatting()));
            }
        }
        if (!list3.isEmpty()) {
            list.add(new LiteralText(""));
            list.add(new TranslatableText("potion.whenDrank", new Object[0]).formatted(Formatting.DARK_PURPLE));
            for (Pair pair : list3) {
                EntityAttributeModifier entityAttributeModifier3 = (EntityAttributeModifier)pair.getRight();
                double d = entityAttributeModifier3.getAmount();
                double e = entityAttributeModifier3.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_BASE || entityAttributeModifier3.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_TOTAL ? entityAttributeModifier3.getAmount() * 100.0 : entityAttributeModifier3.getAmount();
                if (d > 0.0) {
                    list.add(new TranslatableText("attribute.modifier.plus." + entityAttributeModifier3.getOperation().getId(), ItemStack.MODIFIER_FORMAT.format(e), new TranslatableText("attribute.name." + (String)pair.getLeft(), new Object[0])).formatted(Formatting.BLUE));
                    continue;
                }
                if (!(d < 0.0)) continue;
                list.add(new TranslatableText("attribute.modifier.take." + entityAttributeModifier3.getOperation().getId(), ItemStack.MODIFIER_FORMAT.format(e *= -1.0), new TranslatableText("attribute.name." + (String)pair.getLeft(), new Object[0])).formatted(Formatting.RED));
            }
        }
    }
}

