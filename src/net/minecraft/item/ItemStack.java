/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Streams
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Streams;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.BlockPredicatesChecker;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.tag.TagKey;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public final class ItemStack {
    public static final Codec<ItemStack> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Registry.ITEM.getCodec().fieldOf("id").forGetter(stack -> stack.item), (App)Codec.INT.fieldOf("Count").forGetter(stack -> stack.count), (App)NbtCompound.CODEC.optionalFieldOf("tag").forGetter(stack -> Optional.ofNullable(stack.nbt))).apply((Applicative)instance, ItemStack::new));
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final ItemStack EMPTY = new ItemStack((ItemConvertible)null);
    public static final DecimalFormat MODIFIER_FORMAT = Util.make(new DecimalFormat("#.##"), decimalFormat -> decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT)));
    public static final String ENCHANTMENTS_KEY = "Enchantments";
    public static final String DISPLAY_KEY = "display";
    public static final String NAME_KEY = "Name";
    public static final String LORE_KEY = "Lore";
    public static final String DAMAGE_KEY = "Damage";
    public static final String COLOR_KEY = "color";
    private static final String UNBREAKABLE_KEY = "Unbreakable";
    private static final String REPAIR_COST_KEY = "RepairCost";
    private static final String CAN_DESTROY_KEY = "CanDestroy";
    private static final String CAN_PLACE_ON_KEY = "CanPlaceOn";
    private static final String HIDE_FLAGS_KEY = "HideFlags";
    private static final int field_30903 = 0;
    private static final Style LORE_STYLE = Style.EMPTY.withColor(Formatting.DARK_PURPLE).withItalic(true);
    private int count;
    private int bobbingAnimationTime;
    @Deprecated
    private final Item item;
    @Nullable
    private NbtCompound nbt;
    private boolean empty;
    @Nullable
    private Entity holder;
    @Nullable
    private BlockPredicatesChecker destroyChecker;
    @Nullable
    private BlockPredicatesChecker placeChecker;

    public Optional<TooltipData> getTooltipData() {
        return this.getItem().getTooltipData(this);
    }

    public ItemStack(ItemConvertible item) {
        this(item, 1);
    }

    public ItemStack(RegistryEntry<Item> entry) {
        this(entry.value(), 1);
    }

    private ItemStack(ItemConvertible item, int count, Optional<NbtCompound> nbt) {
        this(item, count);
        nbt.ifPresent(this::setNbt);
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

    private ItemStack(NbtCompound nbt) {
        this.item = Registry.ITEM.get(new Identifier(nbt.getString("id")));
        this.count = nbt.getByte("Count");
        if (nbt.contains("tag", 10)) {
            this.nbt = nbt.getCompound("tag");
            this.getItem().postProcessNbt(this.nbt);
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
        if (this.getItem() == null || this.isOf(Items.AIR)) {
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

    public boolean isIn(TagKey<Item> tag) {
        return this.getItem().getRegistryEntry().isIn(tag);
    }

    public boolean isOf(Item item) {
        return this.getItem() == item;
    }

    public Stream<TagKey<Item>> streamTags() {
        return this.getItem().getRegistryEntry().streamTags();
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity playerEntity = context.getPlayer();
        BlockPos blockPos = context.getBlockPos();
        CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(context.getWorld(), blockPos, false);
        if (playerEntity != null && !playerEntity.getAbilities().allowModifyWorld && !this.canPlaceOn(context.getWorld().getRegistryManager().get(Registry.BLOCK_KEY), cachedBlockPosition)) {
            return ActionResult.PASS;
        }
        Item item = this.getItem();
        ActionResult actionResult = item.useOnBlock(context);
        if (playerEntity != null && actionResult.shouldIncrementStat()) {
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
        if (this.nbt != null) {
            nbt.put("tag", this.nbt.copy());
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
        NbtCompound nbtCompound = this.getNbt();
        return nbtCompound == null || !nbtCompound.getBoolean(UNBREAKABLE_KEY);
    }

    public boolean isDamaged() {
        return this.isDamageable() && this.getDamage() > 0;
    }

    public int getDamage() {
        return this.nbt == null ? 0 : this.nbt.getInt(DAMAGE_KEY);
    }

    public void setDamage(int damage) {
        this.getOrCreateNbt().putInt(DAMAGE_KEY, Math.max(0, damage));
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
        if (entity.world.isClient || entity instanceof PlayerEntity && ((PlayerEntity)entity).getAbilities().creativeMode) {
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

    public boolean isItemBarVisible() {
        return this.item.isItemBarVisible(this);
    }

    public int getItemBarStep() {
        return this.item.getItemBarStep(this);
    }

    public int getItemBarColor() {
        return this.item.getItemBarColor(this);
    }

    public boolean onStackClicked(Slot slot, ClickType clickType, PlayerEntity player) {
        return this.getItem().onStackClicked(this, slot, clickType, player);
    }

    public boolean onClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        return this.getItem().onClicked(this, stack, slot, clickType, player, cursorStackReference);
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
        itemStack.setBobbingAnimationTime(this.getBobbingAnimationTime());
        if (this.nbt != null) {
            itemStack.nbt = this.nbt.copy();
        }
        return itemStack;
    }

    public static boolean areNbtEqual(ItemStack left, ItemStack right) {
        if (left.isEmpty() && right.isEmpty()) {
            return true;
        }
        if (left.isEmpty() || right.isEmpty()) {
            return false;
        }
        if (left.nbt == null && right.nbt != null) {
            return false;
        }
        return left.nbt == null || left.nbt.equals(right.nbt);
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
        if (!this.isOf(stack.getItem())) {
            return false;
        }
        if (this.nbt == null && stack.nbt != null) {
            return false;
        }
        return this.nbt == null || this.nbt.equals(stack.nbt);
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
        return !stack.isEmpty() && this.isOf(stack.getItem());
    }

    public boolean isItemEqual(ItemStack stack) {
        if (this.isDamageable()) {
            return !stack.isEmpty() && this.isOf(stack.getItem());
        }
        return this.isItemEqualIgnoreDamage(stack);
    }

    public static boolean canCombine(ItemStack stack, ItemStack otherStack) {
        return stack.isOf(otherStack.getItem()) && ItemStack.areNbtEqual(stack, otherStack);
    }

    public String getTranslationKey() {
        return this.getItem().getTranslationKey(this);
    }

    public String toString() {
        return this.count + " " + this.getItem();
    }

    public void inventoryTick(World world, Entity entity, int slot, boolean selected) {
        if (this.bobbingAnimationTime > 0) {
            --this.bobbingAnimationTime;
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

    public boolean hasNbt() {
        return !this.empty && this.nbt != null && !this.nbt.isEmpty();
    }

    @Nullable
    public NbtCompound getNbt() {
        return this.nbt;
    }

    public NbtCompound getOrCreateNbt() {
        if (this.nbt == null) {
            this.setNbt(new NbtCompound());
        }
        return this.nbt;
    }

    public NbtCompound getOrCreateSubNbt(String key) {
        if (this.nbt == null || !this.nbt.contains(key, 10)) {
            NbtCompound nbtCompound = new NbtCompound();
            this.setSubNbt(key, nbtCompound);
            return nbtCompound;
        }
        return this.nbt.getCompound(key);
    }

    @Nullable
    public NbtCompound getSubNbt(String key) {
        if (this.nbt == null || !this.nbt.contains(key, 10)) {
            return null;
        }
        return this.nbt.getCompound(key);
    }

    public void removeSubNbt(String key) {
        if (this.nbt != null && this.nbt.contains(key)) {
            this.nbt.remove(key);
            if (this.nbt.isEmpty()) {
                this.nbt = null;
            }
        }
    }

    public NbtList getEnchantments() {
        if (this.nbt != null) {
            return this.nbt.getList(ENCHANTMENTS_KEY, 10);
        }
        return new NbtList();
    }

    public void setNbt(@Nullable NbtCompound nbt) {
        this.nbt = nbt;
        if (this.getItem().isDamageable()) {
            this.setDamage(this.getDamage());
        }
        if (nbt != null) {
            this.getItem().postProcessNbt(nbt);
        }
    }

    public Text getName() {
        NbtCompound nbtCompound = this.getSubNbt(DISPLAY_KEY);
        if (nbtCompound != null && nbtCompound.contains(NAME_KEY, 8)) {
            try {
                MutableText text = Text.Serializer.fromJson(nbtCompound.getString(NAME_KEY));
                if (text != null) {
                    return text;
                }
                nbtCompound.remove(NAME_KEY);
            }
            catch (Exception exception) {
                nbtCompound.remove(NAME_KEY);
            }
        }
        return this.getItem().getName(this);
    }

    public ItemStack setCustomName(@Nullable Text name) {
        NbtCompound nbtCompound = this.getOrCreateSubNbt(DISPLAY_KEY);
        if (name != null) {
            nbtCompound.putString(NAME_KEY, Text.Serializer.toJson(name));
        } else {
            nbtCompound.remove(NAME_KEY);
        }
        return this;
    }

    public void removeCustomName() {
        NbtCompound nbtCompound = this.getSubNbt(DISPLAY_KEY);
        if (nbtCompound != null) {
            nbtCompound.remove(NAME_KEY);
            if (nbtCompound.isEmpty()) {
                this.removeSubNbt(DISPLAY_KEY);
            }
        }
        if (this.nbt != null && this.nbt.isEmpty()) {
            this.nbt = null;
        }
    }

    public boolean hasCustomName() {
        NbtCompound nbtCompound = this.getSubNbt(DISPLAY_KEY);
        return nbtCompound != null && nbtCompound.contains(NAME_KEY, 8);
    }

    public List<Text> getTooltip(@Nullable PlayerEntity player, TooltipContext context) {
        int i;
        Integer integer;
        ArrayList list = Lists.newArrayList();
        MutableText mutableText = new LiteralText("").append(this.getName()).formatted(this.getRarity().formatting);
        if (this.hasCustomName()) {
            mutableText.formatted(Formatting.ITALIC);
        }
        list.add(mutableText);
        if (!context.isAdvanced() && !this.hasCustomName() && this.isOf(Items.FILLED_MAP) && (integer = FilledMapItem.getMapId(this)) != null) {
            list.add(new LiteralText("#" + integer).formatted(Formatting.GRAY));
        }
        if (ItemStack.isSectionVisible(i = this.getHideFlags(), TooltipSection.ADDITIONAL)) {
            this.getItem().appendTooltip(this, player == null ? null : player.world, list, context);
        }
        if (this.hasNbt()) {
            if (ItemStack.isSectionVisible(i, TooltipSection.ENCHANTMENTS)) {
                ItemStack.appendEnchantments(list, this.getEnchantments());
            }
            if (this.nbt.contains(DISPLAY_KEY, 10)) {
                NbtCompound nbtCompound = this.nbt.getCompound(DISPLAY_KEY);
                if (ItemStack.isSectionVisible(i, TooltipSection.DYE) && nbtCompound.contains(COLOR_KEY, 99)) {
                    if (context.isAdvanced()) {
                        list.add(new TranslatableText("item.color", String.format("#%06X", nbtCompound.getInt(COLOR_KEY))).formatted(Formatting.GRAY));
                    } else {
                        list.add(new TranslatableText("item.dyed").formatted(Formatting.GRAY, Formatting.ITALIC));
                    }
                }
                if (nbtCompound.getType(LORE_KEY) == 9) {
                    NbtList nbtList = nbtCompound.getList(LORE_KEY, 8);
                    for (int j = 0; j < nbtList.size(); ++j) {
                        String string = nbtList.getString(j);
                        try {
                            MutableText mutableText2 = Text.Serializer.fromJson(string);
                            if (mutableText2 == null) continue;
                            list.add(Texts.setStyleIfAbsent(mutableText2, LORE_STYLE));
                            continue;
                        }
                        catch (Exception exception) {
                            nbtCompound.remove(LORE_KEY);
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
        if (this.hasNbt()) {
            NbtList nbtList2;
            if (ItemStack.isSectionVisible(i, TooltipSection.UNBREAKABLE) && this.nbt.getBoolean(UNBREAKABLE_KEY)) {
                list.add(new TranslatableText("item.unbreakable").formatted(Formatting.BLUE));
            }
            if (ItemStack.isSectionVisible(i, TooltipSection.CAN_DESTROY) && this.nbt.contains(CAN_DESTROY_KEY, 9) && !(nbtList2 = this.nbt.getList(CAN_DESTROY_KEY, 8)).isEmpty()) {
                list.add(LiteralText.EMPTY);
                list.add(new TranslatableText("item.canBreak").formatted(Formatting.GRAY));
                for (int k = 0; k < nbtList2.size(); ++k) {
                    list.addAll(ItemStack.parseBlockTag(nbtList2.getString(k)));
                }
            }
            if (ItemStack.isSectionVisible(i, TooltipSection.CAN_PLACE) && this.nbt.contains(CAN_PLACE_ON_KEY, 9) && !(nbtList2 = this.nbt.getList(CAN_PLACE_ON_KEY, 8)).isEmpty()) {
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
            if (this.hasNbt()) {
                list.add(new TranslatableText("item.nbt_tags", this.nbt.getKeys().size()).formatted(Formatting.DARK_GRAY));
            }
        }
        return list;
    }

    private static boolean isSectionVisible(int flags, TooltipSection tooltipSection) {
        return (flags & tooltipSection.getFlag()) == 0;
    }

    private int getHideFlags() {
        if (this.hasNbt() && this.nbt.contains(HIDE_FLAGS_KEY, 99)) {
            return this.nbt.getInt(HIDE_FLAGS_KEY);
        }
        return 0;
    }

    public void addHideFlag(TooltipSection tooltipSection) {
        NbtCompound nbtCompound = this.getOrCreateNbt();
        nbtCompound.putInt(HIDE_FLAGS_KEY, nbtCompound.getInt(HIDE_FLAGS_KEY) | tooltipSection.getFlag());
    }

    public static void appendEnchantments(List<Text> tooltip, NbtList enchantments) {
        for (int i = 0; i < enchantments.size(); ++i) {
            NbtCompound nbtCompound = enchantments.getCompound(i);
            Registry.ENCHANTMENT.getOrEmpty(EnchantmentHelper.getIdFromNbt(nbtCompound)).ifPresent(e -> tooltip.add(e.getName(EnchantmentHelper.getLevelFromNbt(nbtCompound))));
        }
    }

    private static Collection<Text> parseBlockTag(String tag) {
        try {
            List<Text> list;
            boolean bl2;
            BlockArgumentParser blockArgumentParser = new BlockArgumentParser(new StringReader(tag), true).parse(true);
            BlockState blockState = blockArgumentParser.getBlockState();
            TagKey<Block> tagKey = blockArgumentParser.getTagId();
            boolean bl = blockState != null;
            boolean bl3 = bl2 = tagKey != null;
            if (bl) {
                return Lists.newArrayList((Object[])new Text[]{blockState.getBlock().getName().formatted(Formatting.DARK_GRAY)});
            }
            if (bl2 && !(list = Streams.stream(Registry.BLOCK.iterateEntries(tagKey)).map(entry -> ((Block)entry.value()).getName()).map(text -> text.formatted(Formatting.DARK_GRAY)).collect(Collectors.toList())).isEmpty()) {
                return list;
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
        this.getOrCreateNbt();
        if (!this.nbt.contains(ENCHANTMENTS_KEY, 9)) {
            this.nbt.put(ENCHANTMENTS_KEY, new NbtList());
        }
        NbtList nbtList = this.nbt.getList(ENCHANTMENTS_KEY, 10);
        nbtList.add(EnchantmentHelper.createNbt(EnchantmentHelper.getEnchantmentId(enchantment), (byte)level));
    }

    public boolean hasEnchantments() {
        if (this.nbt != null && this.nbt.contains(ENCHANTMENTS_KEY, 9)) {
            return !this.nbt.getList(ENCHANTMENTS_KEY, 10).isEmpty();
        }
        return false;
    }

    public void setSubNbt(String key, NbtElement element) {
        this.getOrCreateNbt().put(key, element);
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
        if (this.hasNbt() && this.nbt.contains(REPAIR_COST_KEY, 3)) {
            return this.nbt.getInt(REPAIR_COST_KEY);
        }
        return 0;
    }

    public void setRepairCost(int repairCost) {
        this.getOrCreateNbt().putInt(REPAIR_COST_KEY, repairCost);
    }

    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        HashMultimap multimap;
        if (this.hasNbt() && this.nbt.contains("AttributeModifiers", 9)) {
            multimap = HashMultimap.create();
            NbtList nbtList = this.nbt.getList("AttributeModifiers", 10);
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
        this.getOrCreateNbt();
        if (!this.nbt.contains("AttributeModifiers", 9)) {
            this.nbt.put("AttributeModifiers", new NbtList());
        }
        NbtList nbtList = this.nbt.getList("AttributeModifiers", 10);
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

    public boolean canPlaceOn(Registry<Block> blockRegistry, CachedBlockPosition pos) {
        if (this.placeChecker == null) {
            this.placeChecker = new BlockPredicatesChecker(CAN_PLACE_ON_KEY);
        }
        return this.placeChecker.check(this, blockRegistry, pos);
    }

    public boolean canDestroy(Registry<Block> blockRegistry, CachedBlockPosition pos) {
        if (this.destroyChecker == null) {
            this.destroyChecker = new BlockPredicatesChecker(CAN_DESTROY_KEY);
        }
        return this.destroyChecker.check(this, blockRegistry, pos);
    }

    public int getBobbingAnimationTime() {
        return this.bobbingAnimationTime;
    }

    public void setBobbingAnimationTime(int bobbingAnimationTime) {
        this.bobbingAnimationTime = bobbingAnimationTime;
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

    public void onItemEntityDestroyed(ItemEntity entity) {
        this.getItem().onItemEntityDestroyed(entity);
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

    @Nullable
    public SoundEvent getEquipSound() {
        return this.getItem().getEquipSound();
    }

    public static final class TooltipSection
    extends Enum<TooltipSection> {
        public static final /* enum */ TooltipSection ENCHANTMENTS = new TooltipSection();
        public static final /* enum */ TooltipSection MODIFIERS = new TooltipSection();
        public static final /* enum */ TooltipSection UNBREAKABLE = new TooltipSection();
        public static final /* enum */ TooltipSection CAN_DESTROY = new TooltipSection();
        public static final /* enum */ TooltipSection CAN_PLACE = new TooltipSection();
        public static final /* enum */ TooltipSection ADDITIONAL = new TooltipSection();
        public static final /* enum */ TooltipSection DYE = new TooltipSection();
        private final int flag = 1 << this.ordinal();
        private static final /* synthetic */ TooltipSection[] field_25776;

        public static TooltipSection[] values() {
            return (TooltipSection[])field_25776.clone();
        }

        public static TooltipSection valueOf(String string) {
            return Enum.valueOf(TooltipSection.class, string);
        }

        public int getFlag() {
            return this.flag;
        }

        private static /* synthetic */ TooltipSection[] method_36678() {
            return new TooltipSection[]{ENCHANTMENTS, MODIFIERS, UNBREAKABLE, CAN_DESTROY, CAN_PLACE, ADDITIONAL, DYE};
        }

        static {
            field_25776 = TooltipSection.method_36678();
        }
    }
}

