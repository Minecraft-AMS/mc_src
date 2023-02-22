/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.ScoreboardDisplayS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardPlayerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.ScoreboardState;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class ServerScoreboard
extends Scoreboard {
    private final MinecraftServer server;
    private final Set<ScoreboardObjective> objectives = Sets.newHashSet();
    private final List<Runnable> updateListeners = Lists.newArrayList();

    public ServerScoreboard(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public void updateScore(ScoreboardPlayerScore score) {
        super.updateScore(score);
        if (this.objectives.contains(score.getObjective())) {
            this.server.getPlayerManager().sendToAll(new ScoreboardPlayerUpdateS2CPacket(UpdateMode.CHANGE, score.getObjective().getName(), score.getPlayerName(), score.getScore()));
        }
        this.runUpdateListeners();
    }

    @Override
    public void updatePlayerScore(String playerName) {
        super.updatePlayerScore(playerName);
        this.server.getPlayerManager().sendToAll(new ScoreboardPlayerUpdateS2CPacket(UpdateMode.REMOVE, null, playerName, 0));
        this.runUpdateListeners();
    }

    @Override
    public void updatePlayerScore(String playerName, ScoreboardObjective objective) {
        super.updatePlayerScore(playerName, objective);
        if (this.objectives.contains(objective)) {
            this.server.getPlayerManager().sendToAll(new ScoreboardPlayerUpdateS2CPacket(UpdateMode.REMOVE, objective.getName(), playerName, 0));
        }
        this.runUpdateListeners();
    }

    @Override
    public void setObjectiveSlot(int slot, @Nullable ScoreboardObjective objective) {
        ScoreboardObjective scoreboardObjective = this.getObjectiveForSlot(slot);
        super.setObjectiveSlot(slot, objective);
        if (scoreboardObjective != objective && scoreboardObjective != null) {
            if (this.getSlot(scoreboardObjective) > 0) {
                this.server.getPlayerManager().sendToAll(new ScoreboardDisplayS2CPacket(slot, objective));
            } else {
                this.removeScoreboardObjective(scoreboardObjective);
            }
        }
        if (objective != null) {
            if (this.objectives.contains(objective)) {
                this.server.getPlayerManager().sendToAll(new ScoreboardDisplayS2CPacket(slot, objective));
            } else {
                this.addScoreboardObjective(objective);
            }
        }
        this.runUpdateListeners();
    }

    @Override
    public boolean addPlayerToTeam(String playerName, Team team) {
        if (super.addPlayerToTeam(playerName, team)) {
            this.server.getPlayerManager().sendToAll(TeamS2CPacket.changePlayerTeam(team, playerName, TeamS2CPacket.Operation.ADD));
            this.runUpdateListeners();
            return true;
        }
        return false;
    }

    @Override
    public void removePlayerFromTeam(String playerName, Team team) {
        super.removePlayerFromTeam(playerName, team);
        this.server.getPlayerManager().sendToAll(TeamS2CPacket.changePlayerTeam(team, playerName, TeamS2CPacket.Operation.REMOVE));
        this.runUpdateListeners();
    }

    @Override
    public void updateObjective(ScoreboardObjective objective) {
        super.updateObjective(objective);
        this.runUpdateListeners();
    }

    @Override
    public void updateExistingObjective(ScoreboardObjective objective) {
        super.updateExistingObjective(objective);
        if (this.objectives.contains(objective)) {
            this.server.getPlayerManager().sendToAll(new ScoreboardObjectiveUpdateS2CPacket(objective, 2));
        }
        this.runUpdateListeners();
    }

    @Override
    public void updateRemovedObjective(ScoreboardObjective objective) {
        super.updateRemovedObjective(objective);
        if (this.objectives.contains(objective)) {
            this.removeScoreboardObjective(objective);
        }
        this.runUpdateListeners();
    }

    @Override
    public void updateScoreboardTeamAndPlayers(Team team) {
        super.updateScoreboardTeamAndPlayers(team);
        this.server.getPlayerManager().sendToAll(TeamS2CPacket.updateTeam(team, true));
        this.runUpdateListeners();
    }

    @Override
    public void updateScoreboardTeam(Team team) {
        super.updateScoreboardTeam(team);
        this.server.getPlayerManager().sendToAll(TeamS2CPacket.updateTeam(team, false));
        this.runUpdateListeners();
    }

    @Override
    public void updateRemovedTeam(Team team) {
        super.updateRemovedTeam(team);
        this.server.getPlayerManager().sendToAll(TeamS2CPacket.updateRemovedTeam(team));
        this.runUpdateListeners();
    }

    public void addUpdateListener(Runnable listener) {
        this.updateListeners.add(listener);
    }

    protected void runUpdateListeners() {
        for (Runnable runnable : this.updateListeners) {
            runnable.run();
        }
    }

    public List<Packet<?>> createChangePackets(ScoreboardObjective objective) {
        ArrayList list = Lists.newArrayList();
        list.add(new ScoreboardObjectiveUpdateS2CPacket(objective, 0));
        for (int i = 0; i < 19; ++i) {
            if (this.getObjectiveForSlot(i) != objective) continue;
            list.add(new ScoreboardDisplayS2CPacket(i, objective));
        }
        for (ScoreboardPlayerScore scoreboardPlayerScore : this.getAllPlayerScores(objective)) {
            list.add(new ScoreboardPlayerUpdateS2CPacket(UpdateMode.CHANGE, scoreboardPlayerScore.getObjective().getName(), scoreboardPlayerScore.getPlayerName(), scoreboardPlayerScore.getScore()));
        }
        return list;
    }

    public void addScoreboardObjective(ScoreboardObjective objective) {
        List<Packet<?>> list = this.createChangePackets(objective);
        for (ServerPlayerEntity serverPlayerEntity : this.server.getPlayerManager().getPlayerList()) {
            for (Packet<?> packet : list) {
                serverPlayerEntity.networkHandler.sendPacket(packet);
            }
        }
        this.objectives.add(objective);
    }

    public List<Packet<?>> createRemovePackets(ScoreboardObjective objective) {
        ArrayList list = Lists.newArrayList();
        list.add(new ScoreboardObjectiveUpdateS2CPacket(objective, 1));
        for (int i = 0; i < 19; ++i) {
            if (this.getObjectiveForSlot(i) != objective) continue;
            list.add(new ScoreboardDisplayS2CPacket(i, objective));
        }
        return list;
    }

    public void removeScoreboardObjective(ScoreboardObjective objective) {
        List<Packet<?>> list = this.createRemovePackets(objective);
        for (ServerPlayerEntity serverPlayerEntity : this.server.getPlayerManager().getPlayerList()) {
            for (Packet<?> packet : list) {
                serverPlayerEntity.networkHandler.sendPacket(packet);
            }
        }
        this.objectives.remove(objective);
    }

    public int getSlot(ScoreboardObjective objective) {
        int i = 0;
        for (int j = 0; j < 19; ++j) {
            if (this.getObjectiveForSlot(j) != objective) continue;
            ++i;
        }
        return i;
    }

    public ScoreboardState createState() {
        ScoreboardState scoreboardState = new ScoreboardState(this);
        this.addUpdateListener(scoreboardState::markDirty);
        return scoreboardState;
    }

    public ScoreboardState stateFromNbt(NbtCompound nbt) {
        return this.createState().readNbt(nbt);
    }

    public static final class UpdateMode
    extends Enum<UpdateMode> {
        public static final /* enum */ UpdateMode CHANGE = new UpdateMode();
        public static final /* enum */ UpdateMode REMOVE = new UpdateMode();
        private static final /* synthetic */ UpdateMode[] field_13429;

        public static UpdateMode[] values() {
            return (UpdateMode[])field_13429.clone();
        }

        public static UpdateMode valueOf(String string) {
            return Enum.valueOf(UpdateMode.class, string);
        }

        private static /* synthetic */ UpdateMode[] method_36963() {
            return new UpdateMode[]{CHANGE, REMOVE};
        }

        static {
            field_13429 = UpdateMode.method_36963();
        }
    }
}

