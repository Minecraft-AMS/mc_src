/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 */
package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.ItemSlotArgumentType;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ReplaceItemCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class LootCommand {
    public static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> {
        LootManager lootManager = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getLootManager();
        return CommandSource.suggestIdentifiers(lootManager.getTableIds(), suggestionsBuilder);
    };
    private static final DynamicCommandExceptionType NO_HELD_ITEMS_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.drop.no_held_items", object));
    private static final DynamicCommandExceptionType NO_LOOT_TABLE_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.drop.no_loot_table", object));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)LootCommand.addTargetArguments(CommandManager.literal("loot").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)), (argumentBuilder, target) -> argumentBuilder.then(CommandManager.literal("fish").then(CommandManager.argument("loot_table", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("pos", BlockPosArgumentType.blockPos()).executes(commandContext -> LootCommand.executeFish((CommandContext<ServerCommandSource>)commandContext, IdentifierArgumentType.getIdentifier((CommandContext<ServerCommandSource>)commandContext, "loot_table"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), ItemStack.EMPTY, target))).then(CommandManager.argument("tool", ItemStackArgumentType.itemStack()).executes(commandContext -> LootCommand.executeFish((CommandContext<ServerCommandSource>)commandContext, IdentifierArgumentType.getIdentifier((CommandContext<ServerCommandSource>)commandContext, "loot_table"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), ItemStackArgumentType.getItemStackArgument(commandContext, "tool").createStack(1, false), target)))).then(CommandManager.literal("mainhand").executes(commandContext -> LootCommand.executeFish((CommandContext<ServerCommandSource>)commandContext, IdentifierArgumentType.getIdentifier((CommandContext<ServerCommandSource>)commandContext, "loot_table"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), LootCommand.getHeldItem((ServerCommandSource)commandContext.getSource(), EquipmentSlot.MAINHAND), target)))).then(CommandManager.literal("offhand").executes(commandContext -> LootCommand.executeFish((CommandContext<ServerCommandSource>)commandContext, IdentifierArgumentType.getIdentifier((CommandContext<ServerCommandSource>)commandContext, "loot_table"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), LootCommand.getHeldItem((ServerCommandSource)commandContext.getSource(), EquipmentSlot.OFFHAND), target)))))).then(CommandManager.literal("loot").then(CommandManager.argument("loot_table", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(commandContext -> LootCommand.executeLoot((CommandContext<ServerCommandSource>)commandContext, IdentifierArgumentType.getIdentifier((CommandContext<ServerCommandSource>)commandContext, "loot_table"), target)))).then(CommandManager.literal("kill").then(CommandManager.argument("target", EntityArgumentType.entity()).executes(commandContext -> LootCommand.executeKill((CommandContext<ServerCommandSource>)commandContext, EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)commandContext, "target"), target)))).then(CommandManager.literal("mine").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("pos", BlockPosArgumentType.blockPos()).executes(commandContext -> LootCommand.executeMine((CommandContext<ServerCommandSource>)commandContext, BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), ItemStack.EMPTY, target))).then(CommandManager.argument("tool", ItemStackArgumentType.itemStack()).executes(commandContext -> LootCommand.executeMine((CommandContext<ServerCommandSource>)commandContext, BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), ItemStackArgumentType.getItemStackArgument(commandContext, "tool").createStack(1, false), target)))).then(CommandManager.literal("mainhand").executes(commandContext -> LootCommand.executeMine((CommandContext<ServerCommandSource>)commandContext, BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), LootCommand.getHeldItem((ServerCommandSource)commandContext.getSource(), EquipmentSlot.MAINHAND), target)))).then(CommandManager.literal("offhand").executes(commandContext -> LootCommand.executeMine((CommandContext<ServerCommandSource>)commandContext, BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), LootCommand.getHeldItem((ServerCommandSource)commandContext.getSource(), EquipmentSlot.OFFHAND), target)))))));
    }

    private static <T extends ArgumentBuilder<ServerCommandSource, T>> T addTargetArguments(T rootArgument, SourceConstructor sourceConstructor) {
        return (T)rootArgument.then(((LiteralArgumentBuilder)CommandManager.literal("replace").then(CommandManager.literal("entity").then(CommandManager.argument("entities", EntityArgumentType.entities()).then(sourceConstructor.construct((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("slot", ItemSlotArgumentType.itemSlot()), (commandContext, list, feedbackMessage) -> LootCommand.executeReplace(EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "entities"), ItemSlotArgumentType.getItemSlot((CommandContext<ServerCommandSource>)commandContext, "slot"), list.size(), list, feedbackMessage)).then(sourceConstructor.construct((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("count", IntegerArgumentType.integer((int)0)), (commandContext, list, feedbackMessage) -> LootCommand.executeReplace(EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "entities"), ItemSlotArgumentType.getItemSlot((CommandContext<ServerCommandSource>)commandContext, "slot"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"count"), list, feedbackMessage))))))).then(CommandManager.literal("block").then(CommandManager.argument("targetPos", BlockPosArgumentType.blockPos()).then(sourceConstructor.construct((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("slot", ItemSlotArgumentType.itemSlot()), (commandContext, list, feedbackMessage) -> LootCommand.executeBlock((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "targetPos"), ItemSlotArgumentType.getItemSlot((CommandContext<ServerCommandSource>)commandContext, "slot"), list.size(), list, feedbackMessage)).then(sourceConstructor.construct((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("count", IntegerArgumentType.integer((int)0)), (commandContext, list, feedbackMessage) -> LootCommand.executeBlock((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "targetPos"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"slot"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"count"), list, feedbackMessage))))))).then(CommandManager.literal("insert").then(sourceConstructor.construct((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targetPos", BlockPosArgumentType.blockPos()), (commandContext, list, feedbackMessage) -> LootCommand.executeInsert((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "targetPos"), list, feedbackMessage)))).then(CommandManager.literal("give").then(sourceConstructor.construct((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("players", EntityArgumentType.players()), (commandContext, list, feedbackMessage) -> LootCommand.executeGive(EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "players"), list, feedbackMessage)))).then(CommandManager.literal("spawn").then(sourceConstructor.construct((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targetPos", Vec3ArgumentType.vec3()), (commandContext, list, feedbackMessage) -> LootCommand.executeSpawn((ServerCommandSource)commandContext.getSource(), Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "targetPos"), list, feedbackMessage))));
    }

    private static Inventory getBlockInventory(ServerCommandSource source, BlockPos pos) throws CommandSyntaxException {
        BlockEntity blockEntity = source.getWorld().getBlockEntity(pos);
        if (!(blockEntity instanceof Inventory)) {
            throw ReplaceItemCommand.BLOCK_FAILED_EXCEPTION.create();
        }
        return (Inventory)((Object)blockEntity);
    }

    private static int executeInsert(ServerCommandSource source, BlockPos targetPos, List<ItemStack> stacks, FeedbackMessage messageSender) throws CommandSyntaxException {
        Inventory inventory = LootCommand.getBlockInventory(source, targetPos);
        ArrayList list = Lists.newArrayListWithCapacity((int)stacks.size());
        for (ItemStack itemStack : stacks) {
            if (!LootCommand.insert(inventory, itemStack.copy())) continue;
            inventory.markDirty();
            list.add(itemStack);
        }
        messageSender.accept(list);
        return list.size();
    }

    private static boolean insert(Inventory inventory, ItemStack stack) {
        boolean bl = false;
        for (int i = 0; i < inventory.size() && !stack.isEmpty(); ++i) {
            ItemStack itemStack = inventory.getStack(i);
            if (!inventory.isValid(i, stack)) continue;
            if (itemStack.isEmpty()) {
                inventory.setStack(i, stack);
                bl = true;
                break;
            }
            if (!LootCommand.itemsMatch(itemStack, stack)) continue;
            int j = stack.getMaxCount() - itemStack.getCount();
            int k = Math.min(stack.getCount(), j);
            stack.decrement(k);
            itemStack.increment(k);
            bl = true;
        }
        return bl;
    }

    private static int executeBlock(ServerCommandSource source, BlockPos targetPos, int slot, int stackCount, List<ItemStack> stacks, FeedbackMessage messageSender) throws CommandSyntaxException {
        Inventory inventory = LootCommand.getBlockInventory(source, targetPos);
        int i = inventory.size();
        if (slot < 0 || slot >= i) {
            throw ReplaceItemCommand.SLOT_INAPPLICABLE_EXCEPTION.create((Object)slot);
        }
        ArrayList list = Lists.newArrayListWithCapacity((int)stacks.size());
        for (int j = 0; j < stackCount; ++j) {
            ItemStack itemStack;
            int k = slot + j;
            ItemStack itemStack2 = itemStack = j < stacks.size() ? stacks.get(j) : ItemStack.EMPTY;
            if (!inventory.isValid(k, itemStack)) continue;
            inventory.setStack(k, itemStack);
            list.add(itemStack);
        }
        messageSender.accept(list);
        return list.size();
    }

    private static boolean itemsMatch(ItemStack first, ItemStack second) {
        return first.getItem() == second.getItem() && first.getDamage() == second.getDamage() && first.getCount() <= first.getMaxCount() && Objects.equals(first.getTag(), second.getTag());
    }

    private static int executeGive(Collection<ServerPlayerEntity> players, List<ItemStack> stacks, FeedbackMessage messageSender) throws CommandSyntaxException {
        ArrayList list = Lists.newArrayListWithCapacity((int)stacks.size());
        for (ItemStack itemStack : stacks) {
            for (ServerPlayerEntity serverPlayerEntity : players) {
                if (!serverPlayerEntity.inventory.insertStack(itemStack.copy())) continue;
                list.add(itemStack);
            }
        }
        messageSender.accept(list);
        return list.size();
    }

    private static void replace(Entity entity, List<ItemStack> stacks, int slot, int stackCount, List<ItemStack> addedStacks) {
        for (int i = 0; i < stackCount; ++i) {
            ItemStack itemStack;
            ItemStack itemStack2 = itemStack = i < stacks.size() ? stacks.get(i) : ItemStack.EMPTY;
            if (!entity.equip(slot + i, itemStack.copy())) continue;
            addedStacks.add(itemStack);
        }
    }

    private static int executeReplace(Collection<? extends Entity> targets, int slot, int stackCount, List<ItemStack> stacks, FeedbackMessage messageSender) throws CommandSyntaxException {
        ArrayList list = Lists.newArrayListWithCapacity((int)stacks.size());
        for (Entity entity : targets) {
            if (entity instanceof ServerPlayerEntity) {
                ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
                serverPlayerEntity.playerScreenHandler.sendContentUpdates();
                LootCommand.replace(entity, stacks, slot, stackCount, list);
                serverPlayerEntity.playerScreenHandler.sendContentUpdates();
                continue;
            }
            LootCommand.replace(entity, stacks, slot, stackCount, list);
        }
        messageSender.accept(list);
        return list.size();
    }

    private static int executeSpawn(ServerCommandSource source, Vec3d pos, List<ItemStack> stacks, FeedbackMessage messageSender) throws CommandSyntaxException {
        ServerWorld serverWorld = source.getWorld();
        stacks.forEach(itemStack -> {
            ItemEntity itemEntity = new ItemEntity(serverWorld, vec3d.x, vec3d.y, vec3d.z, itemStack.copy());
            itemEntity.setToDefaultPickupDelay();
            serverWorld.spawnEntity(itemEntity);
        });
        messageSender.accept(stacks);
        return stacks.size();
    }

    private static void sendDroppedFeedback(ServerCommandSource source, List<ItemStack> stacks) {
        if (stacks.size() == 1) {
            ItemStack itemStack = stacks.get(0);
            source.sendFeedback(new TranslatableText("commands.drop.success.single", itemStack.getCount(), itemStack.toHoverableText()), false);
        } else {
            source.sendFeedback(new TranslatableText("commands.drop.success.multiple", stacks.size()), false);
        }
    }

    private static void sendDroppedFeedback(ServerCommandSource source, List<ItemStack> stacks, Identifier lootTable) {
        if (stacks.size() == 1) {
            ItemStack itemStack = stacks.get(0);
            source.sendFeedback(new TranslatableText("commands.drop.success.single_with_table", itemStack.getCount(), itemStack.toHoverableText(), lootTable), false);
        } else {
            source.sendFeedback(new TranslatableText("commands.drop.success.multiple_with_table", stacks.size(), lootTable), false);
        }
    }

    private static ItemStack getHeldItem(ServerCommandSource source, EquipmentSlot slot) throws CommandSyntaxException {
        Entity entity = source.getEntityOrThrow();
        if (entity instanceof LivingEntity) {
            return ((LivingEntity)entity).getEquippedStack(slot);
        }
        throw NO_HELD_ITEMS_EXCEPTION.create((Object)entity.getDisplayName());
    }

    private static int executeMine(CommandContext<ServerCommandSource> context, BlockPos pos, ItemStack stack, Target constructor) throws CommandSyntaxException {
        ServerCommandSource serverCommandSource = (ServerCommandSource)context.getSource();
        ServerWorld serverWorld = serverCommandSource.getWorld();
        BlockState blockState = serverWorld.getBlockState(pos);
        BlockEntity blockEntity = serverWorld.getBlockEntity(pos);
        LootContext.Builder builder = new LootContext.Builder(serverWorld).parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos)).parameter(LootContextParameters.BLOCK_STATE, blockState).optionalParameter(LootContextParameters.BLOCK_ENTITY, blockEntity).optionalParameter(LootContextParameters.THIS_ENTITY, serverCommandSource.getEntity()).parameter(LootContextParameters.TOOL, stack);
        List<ItemStack> list2 = blockState.getDroppedStacks(builder);
        return constructor.accept(context, list2, list -> LootCommand.sendDroppedFeedback(serverCommandSource, list, blockState.getBlock().getLootTableId()));
    }

    private static int executeKill(CommandContext<ServerCommandSource> context, Entity entity, Target constructor) throws CommandSyntaxException {
        if (!(entity instanceof LivingEntity)) {
            throw NO_LOOT_TABLE_EXCEPTION.create((Object)entity.getDisplayName());
        }
        Identifier identifier = ((LivingEntity)entity).getLootTable();
        ServerCommandSource serverCommandSource = (ServerCommandSource)context.getSource();
        LootContext.Builder builder = new LootContext.Builder(serverCommandSource.getWorld());
        Entity entity2 = serverCommandSource.getEntity();
        if (entity2 instanceof PlayerEntity) {
            builder.parameter(LootContextParameters.LAST_DAMAGE_PLAYER, (PlayerEntity)entity2);
        }
        builder.parameter(LootContextParameters.DAMAGE_SOURCE, DamageSource.MAGIC);
        builder.optionalParameter(LootContextParameters.DIRECT_KILLER_ENTITY, entity2);
        builder.optionalParameter(LootContextParameters.KILLER_ENTITY, entity2);
        builder.parameter(LootContextParameters.THIS_ENTITY, entity);
        builder.parameter(LootContextParameters.ORIGIN, serverCommandSource.getPosition());
        LootTable lootTable = serverCommandSource.getMinecraftServer().getLootManager().getTable(identifier);
        List<ItemStack> list2 = lootTable.generateLoot(builder.build(LootContextTypes.ENTITY));
        return constructor.accept(context, list2, list -> LootCommand.sendDroppedFeedback(serverCommandSource, list, identifier));
    }

    private static int executeLoot(CommandContext<ServerCommandSource> context, Identifier lootTable, Target constructor) throws CommandSyntaxException {
        ServerCommandSource serverCommandSource = (ServerCommandSource)context.getSource();
        LootContext.Builder builder = new LootContext.Builder(serverCommandSource.getWorld()).optionalParameter(LootContextParameters.THIS_ENTITY, serverCommandSource.getEntity()).parameter(LootContextParameters.ORIGIN, serverCommandSource.getPosition());
        return LootCommand.getFeedbackMessageSingle(context, lootTable, builder.build(LootContextTypes.CHEST), constructor);
    }

    private static int executeFish(CommandContext<ServerCommandSource> context, Identifier lootTable, BlockPos pos, ItemStack stack, Target constructor) throws CommandSyntaxException {
        ServerCommandSource serverCommandSource = (ServerCommandSource)context.getSource();
        LootContext lootContext = new LootContext.Builder(serverCommandSource.getWorld()).parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos)).parameter(LootContextParameters.TOOL, stack).optionalParameter(LootContextParameters.THIS_ENTITY, serverCommandSource.getEntity()).build(LootContextTypes.FISHING);
        return LootCommand.getFeedbackMessageSingle(context, lootTable, lootContext, constructor);
    }

    private static int getFeedbackMessageSingle(CommandContext<ServerCommandSource> context, Identifier lootTable, LootContext lootContext, Target constructor) throws CommandSyntaxException {
        ServerCommandSource serverCommandSource = (ServerCommandSource)context.getSource();
        LootTable lootTable2 = serverCommandSource.getMinecraftServer().getLootManager().getTable(lootTable);
        List<ItemStack> list2 = lootTable2.generateLoot(lootContext);
        return constructor.accept(context, list2, list -> LootCommand.sendDroppedFeedback(serverCommandSource, list));
    }

    @FunctionalInterface
    static interface SourceConstructor {
        public ArgumentBuilder<ServerCommandSource, ?> construct(ArgumentBuilder<ServerCommandSource, ?> var1, Target var2);
    }

    @FunctionalInterface
    static interface Target {
        public int accept(CommandContext<ServerCommandSource> var1, List<ItemStack> var2, FeedbackMessage var3) throws CommandSyntaxException;
    }

    @FunctionalInterface
    static interface FeedbackMessage {
        public void accept(List<ItemStack> var1) throws CommandSyntaxException;
    }
}

