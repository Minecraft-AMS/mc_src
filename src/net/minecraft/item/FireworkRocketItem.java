/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.FireworkStarItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FireworkRocketItem
extends Item {
    public static final byte[] FLIGHT_VALUES = new byte[]{1, 2, 3};
    public static final String FIREWORKS_KEY = "Fireworks";
    public static final String EXPLOSION_KEY = "Explosion";
    public static final String EXPLOSIONS_KEY = "Explosions";
    public static final String FLIGHT_KEY = "Flight";
    public static final String TYPE_KEY = "Type";
    public static final String TRAIL_KEY = "Trail";
    public static final String FLICKER_KEY = "Flicker";
    public static final String COLORS_KEY = "Colors";
    public static final String FADE_COLORS_KEY = "FadeColors";
    public static final double OFFSET_POS_MULTIPLIER = 0.15;

    public FireworkRocketItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (!world.isClient) {
            ItemStack itemStack = context.getStack();
            Vec3d vec3d = context.getHitPos();
            Direction direction = context.getSide();
            FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity(world, context.getPlayer(), vec3d.x + (double)direction.getOffsetX() * 0.15, vec3d.y + (double)direction.getOffsetY() * 0.15, vec3d.z + (double)direction.getOffsetZ() * 0.15, itemStack);
            world.spawnEntity(fireworkRocketEntity);
            itemStack.decrement(1);
        }
        return ActionResult.success(world.isClient);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user.isFallFlying()) {
            ItemStack itemStack = user.getStackInHand(hand);
            if (!world.isClient) {
                FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity(world, itemStack, user);
                world.spawnEntity(fireworkRocketEntity);
                if (!user.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }
                user.incrementStat(Stats.USED.getOrCreateStat(this));
            }
            return TypedActionResult.success(user.getStackInHand(hand), world.isClient());
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        NbtList nbtList;
        NbtCompound nbtCompound = stack.getSubNbt(FIREWORKS_KEY);
        if (nbtCompound == null) {
            return;
        }
        if (nbtCompound.contains(FLIGHT_KEY, 99)) {
            tooltip.add(Text.translatable("item.minecraft.firework_rocket.flight").append(ScreenTexts.SPACE).append(String.valueOf(nbtCompound.getByte(FLIGHT_KEY))).formatted(Formatting.GRAY));
        }
        if (!(nbtList = nbtCompound.getList(EXPLOSIONS_KEY, 10)).isEmpty()) {
            for (int i = 0; i < nbtList.size(); ++i) {
                NbtCompound nbtCompound2 = nbtList.getCompound(i);
                ArrayList list = Lists.newArrayList();
                FireworkStarItem.appendFireworkTooltip(nbtCompound2, list);
                if (list.isEmpty()) continue;
                for (int j = 1; j < list.size(); ++j) {
                    list.set(j, Text.literal("  ").append((Text)list.get(j)).formatted(Formatting.GRAY));
                }
                tooltip.addAll(list);
            }
        }
    }

    public static void setFlight(ItemStack stack, byte flight) {
        stack.getOrCreateSubNbt(FIREWORKS_KEY).putByte(FLIGHT_KEY, flight);
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack itemStack = new ItemStack(this);
        FireworkRocketItem.setFlight(itemStack, (byte)1);
        return itemStack;
    }

    public static final class Type
    extends Enum<Type> {
        public static final /* enum */ Type SMALL_BALL = new Type(0, "small_ball");
        public static final /* enum */ Type LARGE_BALL = new Type(1, "large_ball");
        public static final /* enum */ Type STAR = new Type(2, "star");
        public static final /* enum */ Type CREEPER = new Type(3, "creeper");
        public static final /* enum */ Type BURST = new Type(4, "burst");
        private static final IntFunction<Type> BY_ID;
        private final int id;
        private final String name;
        private static final /* synthetic */ Type[] field_7978;

        public static Type[] values() {
            return (Type[])field_7978.clone();
        }

        public static Type valueOf(String string) {
            return Enum.valueOf(Type.class, string);
        }

        private Type(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public static Type byId(int id) {
            return BY_ID.apply(id);
        }

        private static /* synthetic */ Type[] method_36677() {
            return new Type[]{SMALL_BALL, LARGE_BALL, STAR, CREEPER, BURST};
        }

        static {
            field_7978 = Type.method_36677();
            BY_ID = ValueLists.createIdToValueFunction(Type::getId, Type.values(), ValueLists.OutOfBoundsHandling.ZERO);
        }
    }
}

