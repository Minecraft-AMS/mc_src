/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.FireworkEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FireworkChargeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FireworkItem
extends Item {
    public FireworkItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (!world.isClient) {
            ItemStack itemStack = context.getStack();
            Vec3d vec3d = context.getHitPos();
            FireworkEntity fireworkEntity = new FireworkEntity(world, vec3d.x, vec3d.y, vec3d.z, itemStack);
            world.spawnEntity(fireworkEntity);
            itemStack.decrement(1);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user.isFallFlying()) {
            ItemStack itemStack = user.getStackInHand(hand);
            if (!world.isClient) {
                world.spawnEntity(new FireworkEntity(world, itemStack, user));
                if (!user.abilities.creativeMode) {
                    itemStack.decrement(1);
                }
            }
            return new TypedActionResult<ItemStack>(ActionResult.SUCCESS, user.getStackInHand(hand));
        }
        return new TypedActionResult<ItemStack>(ActionResult.PASS, user.getStackInHand(hand));
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        ListTag listTag;
        CompoundTag compoundTag = stack.getSubTag("Fireworks");
        if (compoundTag == null) {
            return;
        }
        if (compoundTag.contains("Flight", 99)) {
            tooltip.add(new TranslatableText("item.minecraft.firework_rocket.flight", new Object[0]).append(" ").append(String.valueOf(compoundTag.getByte("Flight"))).formatted(Formatting.GRAY));
        }
        if (!(listTag = compoundTag.getList("Explosions", 10)).isEmpty()) {
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag compoundTag2 = listTag.getCompound(i);
                ArrayList list = Lists.newArrayList();
                FireworkChargeItem.appendFireworkTooltip(compoundTag2, list);
                if (list.isEmpty()) continue;
                for (int j = 1; j < list.size(); ++j) {
                    list.set(j, new LiteralText("  ").append((Text)list.get(j)).formatted(Formatting.GRAY));
                }
                tooltip.addAll(list);
            }
        }
    }

    public static enum Type {
        SMALL_BALL(0, "small_ball"),
        LARGE_BALL(1, "large_ball"),
        STAR(2, "star"),
        CREEPER(3, "creeper"),
        BURST(4, "burst");

        private static final Type[] TYPES;
        private final int id;
        private final String name;

        private Type(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return this.id;
        }

        @Environment(value=EnvType.CLIENT)
        public String getName() {
            return this.name;
        }

        @Environment(value=EnvType.CLIENT)
        public static Type byId(int id) {
            if (id < 0 || id >= TYPES.length) {
                return SMALL_BALL;
            }
            return TYPES[id];
        }

        static {
            TYPES = (Type[])Arrays.stream(Type.values()).sorted(Comparator.comparingInt(type -> type.id)).toArray(Type[]::new);
        }
    }
}

