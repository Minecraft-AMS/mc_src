/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.LiteralCommandNode
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.command.arguments.MessageArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class TeammsgCommand {
    private static final SimpleCommandExceptionType NO_TEAM_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.teammsg.failed.noteam", new Object[0]));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode literalCommandNode = dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("teammsg").then(CommandManager.argument("message", MessageArgumentType.message()).executes(commandContext -> TeammsgCommand.execute((ServerCommandSource)commandContext.getSource(), MessageArgumentType.getMessage((CommandContext<ServerCommandSource>)commandContext, "message")))));
        dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("tm").redirect((CommandNode)literalCommandNode));
    }

    private static int execute(ServerCommandSource source, Text message) throws CommandSyntaxException {
        Entity entity = source.getEntityOrThrow();
        Team team = (Team)entity.getScoreboardTeam();
        if (team == null) {
            throw NO_TEAM_EXCEPTION.create();
        }
        Consumer<Style> consumer = style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText("chat.type.team.hover", new Object[0]))).setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/teammsg "));
        Text text = team.getFormattedName().styled(consumer);
        for (Text text2 : text.getSiblings()) {
            text2.styled(consumer);
        }
        List<ServerPlayerEntity> list = source.getMinecraftServer().getPlayerManager().getPlayerList();
        for (ServerPlayerEntity serverPlayerEntity : list) {
            if (serverPlayerEntity == entity) {
                serverPlayerEntity.sendMessage(new TranslatableText("chat.type.team.sent", text, source.getDisplayName(), message.deepCopy()));
                continue;
            }
            if (serverPlayerEntity.getScoreboardTeam() != team) continue;
            serverPlayerEntity.sendMessage(new TranslatableText("chat.type.team.text", text, source.getDisplayName(), message.deepCopy()));
        }
        return list.size();
    }
}

