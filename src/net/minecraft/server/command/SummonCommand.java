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
import net.minecraft.command.arguments.EntitySummonArgumentType;
import net.minecraft.command.arguments.NbtCompoundTagArgumentType;
import net.minecraft.command.arguments.Vec3ArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class SummonCommand {
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.summon.failed", new Object[0]));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("summon").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(((RequiredArgumentBuilder)CommandManager.argument("entity", EntitySummonArgumentType.entitySummon()).suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes(commandContext -> SummonCommand.execute((ServerCommandSource)commandContext.getSource(), EntitySummonArgumentType.getEntitySummon((CommandContext<ServerCommandSource>)commandContext, "entity"), ((ServerCommandSource)commandContext.getSource()).getPosition(), new CompoundTag(), true))).then(((RequiredArgumentBuilder)CommandManager.argument("pos", Vec3ArgumentType.vec3()).executes(commandContext -> SummonCommand.execute((ServerCommandSource)commandContext.getSource(), EntitySummonArgumentType.getEntitySummon((CommandContext<ServerCommandSource>)commandContext, "entity"), Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "pos"), new CompoundTag(), true))).then(CommandManager.argument("nbt", NbtCompoundTagArgumentType.nbtCompound()).executes(commandContext -> SummonCommand.execute((ServerCommandSource)commandContext.getSource(), EntitySummonArgumentType.getEntitySummon((CommandContext<ServerCommandSource>)commandContext, "entity"), Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "pos"), NbtCompoundTagArgumentType.getCompoundTag(commandContext, "nbt"), false))))));
    }

    private static int execute(ServerCommandSource source, Identifier entity2, Vec3d pos, CompoundTag nbt, boolean initialize) throws CommandSyntaxException {
        CompoundTag compoundTag = nbt.copy();
        compoundTag.putString("id", entity2.toString());
        if (EntityType.getId(EntityType.LIGHTNING_BOLT).equals(entity2)) {
            LightningEntity lightningEntity = new LightningEntity(source.getWorld(), pos.x, pos.y, pos.z, false);
            source.getWorld().addLightning(lightningEntity);
            source.sendFeedback(new TranslatableText("commands.summon.success", lightningEntity.getDisplayName()), true);
            return 1;
        }
        ServerWorld serverWorld = source.getWorld();
        Entity entity22 = EntityType.loadEntityWithPassengers(compoundTag, serverWorld, entity -> {
            entity.refreshPositionAndAngles(vec3d.x, vec3d.y, vec3d.z, entity.yaw, entity.pitch);
            if (!serverWorld.method_18768((Entity)entity)) {
                return null;
            }
            return entity;
        });
        if (entity22 == null) {
            throw FAILED_EXCEPTION.create();
        }
        if (initialize && entity22 instanceof MobEntity) {
            ((MobEntity)entity22).initialize(source.getWorld(), source.getWorld().getLocalDifficulty(new BlockPos(entity22)), SpawnType.COMMAND, null, null);
        }
        source.sendFeedback(new TranslatableText("commands.summon.success", entity22.getDisplayName()), true);
        return 1;
    }
}

