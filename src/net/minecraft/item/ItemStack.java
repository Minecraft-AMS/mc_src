/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Multimap
 *  com.google.gson.JsonParseException
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.command.argument.BlockPredicateArgumentType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagManager;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public final class ItemStack {
    public static final Codec<ItemStack> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Registry.ITEM.fieldOf("id").forGetter(itemStack -> itemStack.item), (App)Codec.INT.fieldOf("Count").forGetter(itemStack -> itemStack.count), (App)NbtCompound.CODEC.optionalFieldOf("tag").forGetter(itemStack -> Optional.ofNullable(itemStack.tag))).apply((Applicative)instance, ItemStack::new));
    private static final Logger LOGGER = LogManager.getLogger();
    public static final ItemStack EMPTY = new ItemStack((ItemConvertible)null);
    public static final DecimalFormat MODIFIER_FORMAT = Util.make(new DecimalFormat("#.##"), decimalFormat -> decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT)));
    private static final Style LORE_STYLE = Style.EMPTY.withColor(Formatting.DARK_PURPLE).withItalic(true);
    private int count;
    private int cooldown;
    @Deprecated
    private final Item item;
    private NbtCompound tag;
    private boolean empty;
    private Entity holder;
    private CachedBlockPosition lastDestroyPos;
    private boolean lastDestroyResult;
    private CachedBlockPosition lastPlaceOnPos;
    private boolean lastPlaceOnResult;

    public ItemStack(ItemConvertible item) {
        this(item, 1);
    }

    private ItemStack(ItemConvertible item, int count, Optional<NbtCompound> tag) {
        this(item, count);
        tag.ifPresent(this::setTag);
    }

    public ItemStack(ItemConvertible item, int count) {
        this.item = item == null ? null : item.asItem();
        this.count = count;
        if (this.item != null && this.item.isDamageable()) {
            this.setDamage(this.getDamage());
        }
        this.updateEmptyState();
    }

    private void updateEmptyState() {
        this.empty = false;
        this.empty = this.isEmpty();
    }

    private ItemStack(NbtCompound tag) {
        this.item = Registry.ITEM.get(new Identifier(tag.getString("id")));
        this.count = tag.getByte("Count");
        if (tag.contains("tag", 10)) {
            this.tag = tag.getCompound("tag");
            this.getItem().postProcessNbt(tag);
        }
        if (this.getItem().isDamageable()) {
            this.setDamage(this.getDamage());
        }
        this.updateEmptyState();
    }

    public static ItemStack fromNbt(NbtCompound nbt) {
        try {
            return new ItemStack(nbt);
        }
        catch (RuntimeException runtimeException) {
            LOGGER.debug("Tried to load invalid item: {}", (Object)nbt, (Object)runtimeException);
            return EMPTY;
        }
    }

    public boolean isEmpty() {
        if (this == EMPTY) {
            return true;
        }
        if (this.getItem() == null || this.getItem() == Items.AIR) {
            return true;
        }
        return this.count <= 0;
    }

    public ItemStack split(int amount) {
        int i = Math.min(amount, this.count);
        ItemStack itemStack = this.copy();
        itemStack.setCount(i);
        this.decrement(i);
        return itemStack;
    }

    public Item getItem() {
        return this.empty ? Items.AIR : this.item;
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity playerEntity = context.getPlayer();
        BlockPos blockPos = context.getBlockPos();
        CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(context.getWorld(), blockPos, false);
        if (playerEntity != null && !playerEntity.abilities.allowModifyWorld && !this.canPlaceOn(context.getWorld().getTagManager(), cachedBlockPosition)) {
            return ActionResult.PASS;
        }
        Item item = this.getItem();
        ActionResult actionResult = item.useOnBlock(context);
        if (playerEntity != null && actionResult.isAccepted()) {
            playerEntity.incrementStat(Stats.USED.getOrCreateStat(item));
        }
        return actionResult;
    }

    public float getMiningSpeedMultiplier(BlockState state) {
        return this.getItem().getMiningSpeedMultiplier(this, state);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return this.getItem().use(world, user, hand);
    }

    public ItemStack finishUsing(World world, LivingEntity user) {
        return this.getItem().finishUsing(this, world, user);
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        Identifier identifier = Registry.ITEM.getId(this.getItem());
        nbt.putString("id", identifier == null ? "minecraft:air" : identifier.toString());
        nbt.putByte("Count", (byte)this.count);
        if (this.tag != null) {
            nbt.put("tag", this.tag.copy());
        }
        return nbt;
    }

    public int getMaxCount() {
        return this.getItem().getMaxCount();
    }

    public boolean isStackable() {
        return this.getMaxCount() > 1 && (!this.isDamageable() || !this.isDamaged());
    }

    public boolean isDamageable() {
        if (this.empty || this.getItem().getMaxDamage() <= 0) {
            return false;
        }
        NbtCompound nbtCompound = this.getTag();
        return nbtCompound == null || !nbtCompound.getBoolean("Unbreakable");
    }

    public boolean isDamaged() {
        return this.isDamageable() && this.getDamage() > 0;
    }

    public int getDamage() {
        return this.tag == null ? 0 : this.tag.getInt("Damage");
    }

    public void setDamage(int damage) {
        this.getOrCreateTag().putInt("Damage", Math.max(0, damage));
    }

    public int getMaxDamage() {
        return this.getItem().getMaxDamage();
    }

    public boolean damage(int amount, Random random, @Nullable ServerPlayerEntity player) {
        int i;
        if (!this.isDamageable()) {
            return false;
        }
        if (amount > 0) {
            i = EnchantmentHelper.getLevel(Enchantments.UNBREAKING, this);
            int j = 0;
            for (int k = 0; i > 0 && k < amount; ++k) {
                if (!UnbreakingEnchantment.shouldPreventDamage(this, i, random)) continue;
                ++j;
            }
            if ((amount -= j) <= 0) {
                return false;
            }
        }
        if (player != null && amount != 0) {
            Criteria.ITEM_DURABILITY_CHANGED.trigger(player, this, this.getDamage() + amount);
        }
        i = this.getDamage() + amount;
        this.setDamage(i);
        return i >= this.getMaxDamage();
    }

    public <T extends LivingEntity> void damage(int amount, T entity, Consumer<T> breakCallback) {
        if (entity.world.isClient || entity instanceof PlayerEntity && ((PlayerEntity)entity).abilities.creativeMode) {
            return;
        }
        if (!this.isDamageable()) {
            return;
        }
        if (this.damage(amount, entity.getRandom(), entity instanceof ServerPlayerEntity ? (ServerPlayerEntity)entity : null)) {
            breakCallback.accept(entity);
            Item item = this.getItem();
            this.decrement(1);
            if (entity instanceof PlayerEntity) {
                ((PlayerEntity)entity).incrementStat(Stats.BROKEN.getOrCreateStat(item));
            }
            this.setDamage(0);
        }
    }

    public void postHit(LivingEntity target, PlayerEntity attacker) {
        Item item = this.getItem();
        if (item.postHit(this, target, attacker)) {
            attacker.incrementStat(Stats.USED.getOrCreateStat(item));
        }
    }

    public void postMine(World world, BlockState state, BlockPos pos, PlayerEntity miner) {
        Item item = this.getItem();
        if (item.postMine(this, world, state, pos, miner)) {
            miner.incrementStat(Stats.USED.getOrCreateStat(item));
        }
    }

    public boolean isSuitableFor(BlockState state) {
        return this.getItem().isSuitableFor(state);
    }

    public ActionResult useOnEntity(PlayerEntity user, LivingEntity entity, Hand hand) {
        return this.getItem().useOnEntity(this, user, entity, hand);
    }

    public ItemStack copy() {
        if (this.isEmpty()) {
            return EMPTY;
        }
        ItemStack itemStack = new ItemStack(this.getItem(), this.count);
        itemStack.setCooldown(this.getCooldown());
        if (this.tag != null) {
            itemStack.tag = this.tag.copy();
        }
        return itemStack;
    }

    public static boolean areTagsEqual(ItemStack left, ItemStack right) {
        if (left.isEmpty() && right.isEmpty()) {
            return true;
        }
        if (left.isEmpty() || right.isEmpty()) {
            return false;
        }
        if (left.tag == null && right.tag != null) {
            return false;
        }
        return left.tag == null || left.tag.equals(right.tag);
    }

    public static boolean areEqual(ItemStack left, ItemStack right) {
        if (left.isEmpty() && right.isEmpty()) {
            return true;
        }
        if (left.isEmpty() || right.isEmpty()) {
            return false;
        }
        return left.isEqual(right);
    }

    private boolean isEqual(ItemStack stack) {
        if (this.count != stack.count) {
            return false;
        }
        if (this.getItem() != stack.getItem()) {
            return false;
        }
        if (this.tag == null && stack.tag != null) {
            return false;
        }
        return this.tag == null || this.tag.equals(stack.tag);
    }

    public static boolean areItemsEqualIgnoreDamage(ItemStack left, ItemStack right) {
        if (left == right) {
            return true;
        }
        if (!left.isEmpty() && !right.isEmpty()) {
            return left.isItemEqualIgnoreDamage(right);
        }
        return false;
    }

    public static boolean areItemsEqual(ItemStack left, ItemStack right) {
        if (left == right) {
            return true;
        }
        if (!left.isEmpty() && !right.isEmpty()) {
            return left.isItemEqual(right);
        }
        return false;
    }

    public boolean isItemEqualIgnoreDamage(ItemStack stack) {
        return !stack.isEmpty() && this.getItem() == stack.getItem();
    }

    public boolean isItemEqual(ItemStack stack) {
        if (this.isDamageable()) {
            return !stack.isEmpty() && this.getItem() == stack.getItem();
        }
        return this.isItemEqualIgnoreDamage(stack);
    }

    public String getTranslationKey() {
        return this.getItem().getTranslationKey(this);
    }

    public String toString() {
        return this.count + " " + this.getItem();
    }

    public void inventoryTick(World world, Entity entity, int slot, boolean selected) {
        if (this.cooldown > 0) {
            --this.cooldown;
        }
        if (this.getItem() != null) {
            this.getItem().inventoryTick(this, world, entity, slot, selected);
        }
    }

    public void onCraft(World world, PlayerEntity player, int amount) {
        player.increaseStat(Stats.CRAFTED.getOrCreateStat(this.getItem()), amount);
        this.getItem().onCraft(this, world, player);
    }

    public int getMaxUseTime() {
        return this.getItem().getMaxUseTime(this);
    }

    public UseAction getUseAction() {
        return this.getItem().getUseAction(this);
    }

    public void onStoppedUsing(World world, LivingEntity user, int remainingUseTicks) {
        this.getItem().onStoppedUsing(this, world, user, remainingUseTicks);
    }

    public boolean isUsedOnRelease() {
        return this.getItem().isUsedOnRelease(this);
    }

    public boolean hasTag() {
        return !this.empty && this.tag != null && !this.tag.isEmpty();
    }

    @Nullable
    public NbtCompound getTag() {
        return this.tag;
    }

    public NbtCompound getOrCreateTag() {
        if (this.tag == null) {
            this.setTag(new NbtCompound());
        }
        return this.tag;
    }

    public NbtCompound getOrCreateSubTag(String key) {
        if (this.tag == null || !this.tag.contains(key, 10)) {
            NbtCompound nbtCompound = new NbtCompound();
            this.putSubTag(key, nbtCompound);
            return nbtCompound;
        }
        return this.tag.getCompound(key);
    }

    @Nullable
    public NbtCompound getSubTag(String key) {
        if (this.tag == null || !this.tag.contains(key, 10)) {
            return null;
        }
        return this.tag.getCompound(key);
    }

    public void removeSubTag(String key) {
        if (this.tag != null && this.tag.contains(key)) {
            this.tag.remove(key);
            if (this.tag.isEmpty()) {
                this.tag = null;
            }
        }
    }

    public NbtList getEnchantments() {
        if (this.tag != null) {
            return this.tag.getList("Enchantments", 10);
        }
        return new NbtList();
    }

    public void setTag(@Nullable NbtCompound tag) {
        this.tag = tag;
        if (this.getItem().isDamageable()) {
            this.setDamage(this.getDamage());
        }
    }

    public Text getName() {
        NbtCompound nbtCompound = this.getSubTag("display");
        if (nbtCompound != null && nbtCompound.contains("Name", 8)) {
            try {
                MutableText text = Text.Serializer.fromJson(nbtCompound.getString("Name"));
                if (text != null) {
                    return text;
                }
                nbtCompound.remove("Name");
            }
            catch (JsonParseException jsonParseException) {
                nbtCompound.remove("Name");
            }
        }
        return this.getItem().getName(this);
    }

    public ItemStack setCustomName(@Nullable Text name) {
        NbtCompound nbtCompound = this.getOrCreateSubTag("display");
        if (name != null) {
            nbtCompound.putString("Name", Text.Serializer.toJson(name));
        } else {
            nbtCompound.remove("Name");
        }
        return this;
    }

    public void removeCustomName() {
        NbtCompound nbtCompound = this.getSubTag("display");
        if (nbtCompound != null) {
            nbtCompound.remove("Name");
            if (nbtCompound.isEmpty()) {
                this.removeSubTag("display");
            }
        }
        if (this.tag != null && this.tag.isEmpty()) {
            this.tag = null;
        }
    }

    public boolean hasCustomName() {
        NbtCompound nbtCompound = this.getSubTag("display");
        return nbtCompound != null && nbtCompound.contains("Name", 8);
    }

    @Environment(value=EnvType.CLIENT)
    public List<Text> getTooltip(@Nullable PlayerEntity player, TooltipContext context) {
        int i;
        ArrayList list = Lists.newArrayList();
        MutableText mutableText = new LiteralText("").append(this.getName()).formatted(this.getRarity().formatting);
        if (this.hasCustomName()) {
            mutableText.formatted(Formatting.ITALIC);
        }
        list.add(mutableText);
        if (!context.isAdvanced() && !this.hasCustomName() && this.getItem() == Items.FILLED_MAP) {
            list.add(new LiteralText("#" + FilledMapItem.getMapId(this)).formatted(Formatting.GRAY));
        }
        if (ItemStack.isSectionVisible(i = this.getHideFlags(), TooltipSection.ADDITIONAL)) {
            this.getItem().appendTooltip(this, player == null ? null : player.world, list, context);
        }
        if (this.hasTag()) {
            if (ItemStack.isSectionVisible(i, TooltipSection.ENCHANTMENTS)) {
                ItemStack.appendEnchantments(list, this.getEnchantments());
            }
            if (this.tag.contains("display", 10)) {
                NbtCompound nbtCompound = this.tag.getCompound("display");
                if (ItemStack.isSectionVisible(i, TooltipSection.DYE) && nbtCompound.contains("color", 99)) {
                    if (context.isAdvanced()) {
                        list.add(new TranslatableText("item.color", String.format("#%06X", nbtCompound.getInt("color"))).formatted(Formatting.GRAY));
                    } else {
                        list.add(new TranslatableText("item.dyed").formatted(Formatting.GRAY, Formatting.ITALIC));
                    }
                }
                if (nbtCompound.getType("Lore") == 9) {
                    NbtList nbtList = nbtCompound.getList("Lore", 8);
                    for (int j = 0; j < nbtList.size(); ++j) {
                        String string = nbtList.getString(j);
                        try {
                            MutableText mutableText2 = Text.Serializer.fromJson(string);
                            if (mutableText2 == null) continue;
                            list.add(Texts.setStyleIfAbsent(mutableText2, LORE_STYLE));
                            continue;
                        }
                        catch (JsonParseException jsonParseException) {
                            nbtCompound.remove("Lore");
                        }
                    }
                }
            }
        }
        if (ItemStack.isSectionVisible(i, TooltipSection.MODIFIERS)) {
            for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                Multimap<EntityAttribute, EntityAttributeModifier> multimap = this.getAttributeModifiers(equipmentSlot);
                if (multimap.isEmpty()) continue;
                list.add(LiteralText.EMPTY);
                list.add(new TranslatableText("item.modifiers." + equipmentSlot.getName()).formatted(Formatting.GRAY));
                for (Map.Entry entry : multimap.entries()) {
                    EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier)entry.getValue();
                    double d = entityAttributeModifier.getValue();
                    boolean bl = false;
                    if (player != null) {
                        if (entityAttributeModifier.getId() == Item.ATTACK_DAMAGE_MODIFIER_ID) {
                            d += player.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
                            d += (double)EnchantmentHelper.getAttackDamage(this, EntityGroup.DEFAULT);
                            bl = true;
                        } else if (entityAttributeModifier.getId() == Item.ATTACK_SPEED_MODIFIER_ID) {
                            d += player.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_SPEED);
                            bl = true;
                        }
                    }
                    double e = entityAttributeModifier.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_BASE || entityAttributeModifier.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_TOTAL ? d * 100.0 : (((EntityAttribute)entry.getKey()).equals(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE) ? d * 10.0 : d);
                    if (bl) {
                        list.add(new LiteralText(" ").append(new TranslatableText("attribute.modifier.equals." + entityAttributeModifier.getOperation().getId(), MODIFIER_FORMAT.format(e), new TranslatableText(((EntityAttribute)entry.getKey()).getTranslationKey()))).formatted(Formatting.DARK_GREEN));
                        continue;
                    }
                    if (d > 0.0) {
                        list.add(new TranslatableText("attribute.modifier.plus." + entityAttributeModifier.getOperation().getId(), MODIFIER_FORMAT.format(e), new TranslatableText(((EntityAttribute)entry.getKey()).getTranslationKey())).formatted(Formatting.BLUE));
                        continue;
                    }
                    if (!(d < 0.0)) continue;
                    list.add(new TranslatableText("attribute.modifier.take." + entityAttributeModifier.getOperation().getId(), MODIFIER_FORMAT.format(e *= -1.0), new TranslatableText(((EntityAttribute)entry.getKey()).getTranslationKey())).formatted(Formatting.RED));
                }
            }
        }
        if (this.hasTag()) {
            NbtList nbtList2;
            if (ItemStack.isSectionVisible(i, TooltipSection.UNBREAKABLE) && this.tag.getBoolean("Unbreakable")) {
                list.add(new TranslatableText("item.unbreakable").formatted(Formatting.BLUE));
            }
            if (ItemStack.isSectionVisible(i, TooltipSection.CAN_DESTROY) && this.tag.contains("CanDestroy", 9) && !(nbtList2 = this.tag.getList("CanDestroy", 8)).isEmpty()) {
                list.add(LiteralText.EMPTY);
                list.add(new TranslatableText("item.canBreak").formatted(Formatting.GRAY));
                for (int k = 0; k < nbtList2.size(); ++k) {
                    list.addAll(ItemStack.parseBlockTag(nbtList2.getString(k)));
                }
            }
            if (ItemStack.isSectionVisible(i, TooltipSection.CAN_PLACE) && this.tag.contains("CanPlaceOn", 9) && !(nbtList2 = this.tag.getList("CanPlaceOn", 8)).isEmpty()) {
                list.add(LiteralText.EMPTY);
                list.add(new TranslatableText("item.canPlace").formatted(Formatting.GRAY));
                for (int k = 0; k < nbtList2.size(); ++k) {
                    list.addAll(ItemStack.parseBlockTag(nbtList2.getString(k)));
                }
            }
        }
        if (context.isAdvanced()) {
            if (this.isDamaged()) {
                list.add(new TranslatableText("item.durability", this.getMaxDamage() - this.getDamage(), this.getMaxDamage()));
            }
            list.add(new LiteralText(Registry.ITEM.getId(this.getItem()).toString()).formatted(Formatting.DARK_GRAY));
            if (this.hasTag()) {
                list.add(new TranslatableText("item.nbt_tags", this.tag.getKeys().size()).formatted(Formatting.DARK_GRAY));
            }
        }
        return list;
    }

    @Environment(value=EnvType.CLIENT)
    private static boolean isSectionVisible(int flags, TooltipSection tooltipSection) {
        return (flags & tooltipSection.getFlag()) == 0;
    }

    @Environment(value=EnvType.CLIENT)
    private int getHideFlags() {
        if (this.hasTag() && this.tag.contains("HideFlags", 99)) {
            return this.tag.getInt("HideFlags");
        }
        return 0;
    }

    public void addHideFlag(TooltipSection tooltipSection) {
        NbtCompound nbtCompound = this.getOrCreateTag();
        nbtCompound.putInt("HideFlags", nbtCompound.getInt("HideFlags") | tooltipSection.getFlag());
    }

    @Environment(value=EnvType.CLIENT)
    public static void appendEnchantments(List<Text> tooltip, NbtList enchantments) {
        for (int i = 0; i < enchantments.size(); ++i) {
            NbtCompound nbtCompound = enchantments.getCompound(i);
            Registry.ENCHANTMENT.getOrEmpty(Identifier.tryParse(nbtCompound.getString("id"))).ifPresent(e -> tooltip.add(e.getName(nbtCompound.getInt("lvl"))));
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static Collection<Text> parseBlockTag(String tag) {
        try {
            boolean bl2;
            BlockArgumentParser blockArgumentParser = new BlockArgumentParser(new StringReader(tag), true).parse(true);
            BlockState blockState = blockArgumentParser.getBlockState();
            Identifier identifier = blockArgumentParser.getTagId();
            boolean bl = blockState != null;
            boolean bl3 = bl2 = identifier != null;
            if (bl || bl2) {
                List<Block> collection;
                if (bl) {
                    return Lists.newArrayList((Object[])new Text[]{blockState.getBlock().getName().formatted(Formatting.DARK_GRAY)});
                }
                Tag<Block> tag2 = BlockTags.getTagGroup().getTag(identifier);
                if (tag2 != null && !(collection = tag2.values()).isEmpty()) {
                    return collection.stream().map(Block::getName).map(text -> text.formatted(Formatting.DARK_GRAY)).collect(Collectors.toList());
                }
            }
        }
        catch (CommandSyntaxException commandSyntaxException) {
            // empty catch block
        }
        return Lists.newArrayList((Object[])new Text[]{new LiteralText("missingno").formatted(Formatting.DARK_GRAY)});
    }

    public boolean hasGlint() {
        return this.getItem().hasGlint(this);
    }

    public Rarity getRarity() {
        return this.getItem().getRarity(this);
    }

    public boolean isEnchantable() {
        if (!this.getItem().isEnchantable(this)) {
            return false;
        }
        return !this.hasEnchantments();
    }

    public void addEnchantment(Enchantment enchantment, int level) {
        this.getOrCreateTag();
        if (!this.tag.contains("Enchantments", 9)) {
            this.tag.put("Enchantments", new NbtList());
        }
        NbtList nbtList = this.tag.getList("Enchantments", 10);
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putString("id", String.valueOf(Registry.ENCHANTMENT.getId(enchantment)));
        nbtCompound.putShort("lvl", (byte)level);
        nbtList.add(nbtCompound);
    }

    public boolean hasEnchantments() {
        if (this.tag != null && this.tag.contains("Enchantments", 9)) {
            return !this.tag.getList("Enchantments", 10).isEmpty();
        }
        return false;
    }

    public void putSubTag(String key, NbtElement tag) {
        this.getOrCreateTag().put(key, tag);
    }

    public boolean isInFrame() {
        return this.holder instanceof ItemFrameEntity;
    }

    public void setHolder(@Nullable Entity holder) {
        this.holder = holder;
    }

    @Nullable
    public ItemFrameEntity getFrame() {
        return this.holder instanceof ItemFrameEntity ? (ItemFrameEntity)this.getHolder() : null;
    }

    @Nullable
    public Entity getHolder() {
        return !this.empty ? this.holder : null;
    }

    public int getRepairCost() {
        if (this.hasTag() && this.tag.contains("RepairCost", 3)) {
            return this.tag.getInt("RepairCost");
        }
        return 0;
    }

    public void setRepairCost(int repairCost) {
        this.getOrCreateTag().putInt("RepairCost", repairCost);
    }

    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        HashMultimap multimap;
        if (this.hasTag() && this.tag.contains("AttributeModifiers", 9)) {
            multimap = HashMultimap.create();
            NbtList nbtList = this.tag.getList("AttributeModifiers", 10);
            for (int i = 0; i < nbtList.size(); ++i) {
                EntityAttributeModifier entityAttributeModifier;
                Optional<EntityAttribute> optional;
                NbtCompound nbtCompound = nbtList.getCompound(i);
                if (nbtCompound.contains("Slot", 8) && !nbtCompound.getString("Slot").equals(slot.getName()) || !(optional = Registry.ATTRIBUTE.getOrEmpty(Identifier.tryParse(nbtCompound.getString("AttributeName")))).isPresent() || (entityAttributeModifier = EntityAttributeModifier.fromNbt(nbtCompound)) == null || entityAttributeModifier.getId().getLeastSignificantBits() == 0L || entityAttributeModifier.getId().getMostSignificantBits() == 0L) continue;
                multimap.put((Object)optional.get(), (Object)entityAttributeModifier);
            }
        } else {
            multimap = this.getItem().getAttributeModifiers(slot);
        }
        return multimap;
    }

    public void addAttributeModifier(EntityAttribute attribute, EntityAttributeModifier modifier, @Nullable EquipmentSlot slot) {
        this.getOrCreateTag();
        if (!this.tag.contains("AttributeModifiers", 9)) {
            this.tag.put("AttributeModifiers", new NbtList());
        }
        NbtList nbtList = this.tag.getList("AttributeModifiers", 10);
        NbtCompound nbtCompound = modifier.toNbt();
        nbtCompound.putString("AttributeName", Registry.ATTRIBUTE.getId(attribute).toString());
        if (slot != null) {
            nbtCompound.putString("Slot", slot.getName());
        }
        nbtList.add(nbtCompound);
    }

    public Text toHoverableText() {
        MutableText mutableText = new LiteralText("").append(this.getName());
        if (this.hasCustomName()) {
            mutableText.formatted(Formatting.ITALIC);
        }
        MutableText mutableText2 = Texts.bracketed(mutableText);
        if (!this.empty) {
            mutableText2.formatted(this.getRarity().formatting).styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackContent(this))));
        }
        return mutableText2;
    }

    private static boolean areBlocksEqual(CachedBlockPosition first, @Nullable CachedBlockPosition second) {
        if (second == null || first.getBlockState() != second.getBlockState()) {
            return false;
        }
        if (first.getBlockEntity() == null && second.getBlockEntity() == null) {
            return true;
        }
        if (first.getBlockEntity() == null || second.getBlockEntity() == null) {
            return false;
        }
        return Objects.equals(first.getBlockEntity().writeNbt(new NbtCompound()), second.getBlockEntity().writeNbt(new NbtCompound()));
    }

    public boolean canDestroy(TagManager tagManager, CachedBlockPosition pos) {
        if (ItemStack.areBlocksEqual(pos, this.lastDestroyPos)) {
            return this.lastDestroyResult;
        }
        this.lastDestroyPos = pos;
        if (this.hasTag() && this.tag.contains("CanDestroy", 9)) {
            NbtList nbtList = this.tag.getList("CanDestroy", 8);
            for (int i = 0; i < nbtList.size(); ++i) {
                String string = nbtList.getString(i);
                try {
                    Predicate<CachedBlockPosition> predicate = BlockPredicateArgumentType.blockPredicate().parse(new StringReader(string)).create(tagManager);
                    if (predicate.test(pos)) {
                        this.lastDestroyResult = true;
                        return true;
                    }
                    continue;
                }
                catch (CommandSyntaxException commandSyntaxException) {
                    // empty catch block
                }
            }
        }
        this.lastDestroyResult = false;
        return false;
    }

    public boolean canPlaceOn(TagManager tagManager, CachedBlockPosition pos) {
        if (ItemStack.areBlocksEqual(pos, this.lastPlaceOnPos)) {
            return this.lastPlaceOnResult;
        }
        this.lastPlaceOnPos = pos;
        if (this.hasTag() && this.tag.contains("CanPlaceOn", 9)) {
            NbtList nbtList = this.tag.getList("CanPlaceOn", 8);
            for (int i = 0; i < nbtList.size(); ++i) {
                String string = nbtList.getString(i);
                try {
                    Predicate<CachedBlockPosition> predicate = BlockPredicateArgumentType.blockPredicate().parse(new StringReader(string)).create(tagManager);
                    if (predicate.test(pos)) {
                        this.lastPlaceOnResult = true;
                        return true;
                    }
                    continue;
                }
                catch (CommandSyntaxException commandSyntaxException) {
                    // empty catch block
                }
            }
        }
        this.lastPlaceOnResult = false;
        return false;
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public int getCount() {
        return this.empty ? 0 : this.count;
    }

    public void setCount(int count) {
        this.count = count;
        this.updateEmptyState();
    }

    public void increment(int amount) {
        this.setCount(this.count + amount);
    }

    public void decrement(int amount) {
        this.increment(-amount);
    }

    public void usageTick(World world, LivingEntity user, int remainingUseTicks) {
        this.getItem().usageTick(world, user, this, remainingUseTicks);
    }

    public boolean isFood() {
        return this.getItem().isFood();
    }

    public SoundEvent getDrinkSound() {
        return this.getItem().getDrinkSound();
    }

    public SoundEvent getEatSound() {
        return this.getItem().getEatSound();
    }

    public static enum TooltipSection {
        ENCHANTMENTS,
        MODIFIERS,
        UNBREAKABLE,
        CAN_DESTROY,
        CAN_PLACE,
        ADDITIONAL,
        DYE;

        private int flag = 1 << this.ordinal();

        public int getFlag() {
            return this.flag;
        }
    }
}

