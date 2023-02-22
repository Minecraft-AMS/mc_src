/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.RegistryKeyArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class PlaceFeatureCommand {
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.placefeature.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("placefeature").requires(source -> source.hasPermissionLevel(2))).then(((RequiredArgumentBuilder)CommandManager.argument("feature", RegistryKeyArgumentType.registryKey(Registry.CONFIGURED_FEATURE_KEY)).executes(context -> PlaceFeatureCommand.execute((ServerCommandSource)context.getSource(), RegistryKeyArgumentType.getConfiguredFeatureEntry((CommandContext<ServerCommandSource>)context, "feature"), new BlockPos(((ServerCommandSource)context.getSource()).getPosition())))).then(CommandManager.argument("pos", BlockPosArgumentType.blockPos()).executes(context -> PlaceFeatureCommand.execute((ServerCommandSource)context.getSource(), RegistryKeyArgumentType.getConfiguredFeatureEntry((CommandContext<ServerCommandSource>)context, "feature"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)context, "pos"))))));
    }

    public static int execute(ServerCommandSource source, RegistryEntry<ConfiguredFeature<?, ?>> feature, BlockPos pos) throws CommandSyntaxException {
        ServerWorld serverWorld = source.getWorld();
        ConfiguredFeature<?, ?> configuredFeature = feature.value();
        if (!configuredFeature.generate(serverWorld, serverWorld.getChunkManager().getChunkGenerator(), serverWorld.getRandom(), pos)) {
            throw FAILED_EXCEPTION.create();
        }
        String string = feature.getKey().map(key -> key.getValue().toString()).orElse("[unregistered]");
        source.sendFeedback(new TranslatableText("commands.placefeature.success", string, pos.getX(), pos.getY(), pos.getZ()), true);
        return 1;
    }
}

