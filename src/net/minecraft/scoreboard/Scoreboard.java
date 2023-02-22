/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

public class Scoreboard {
    private final Map<String, ScoreboardObjective> objectives = Maps.newHashMap();
    private final Map<ScoreboardCriterion, List<ScoreboardObjective>> objectivesByCriterion = Maps.newHashMap();
    private final Map<String, Map<ScoreboardObjective, ScoreboardPlayerScore>> playerObjectives = Maps.newHashMap();
    private final ScoreboardObjective[] objectiveSlots = new ScoreboardObjective[19];
    private final Map<String, Team> teams = Maps.newHashMap();
    private final Map<String, Team> teamsByPlayer = Maps.newHashMap();
    private static String[] displaySlotNames;

    @Environment(value=EnvType.CLIENT)
    public boolean containsObjective(String name) {
        return this.objectives.containsKey(name);
    }

    public ScoreboardObjective getObjective(String name) {
        return this.objectives.get(name);
    }

    @Nullable
    public ScoreboardObjective getNullableObjective(@Nullable String name) {
        return this.objectives.get(name);
    }

    public ScoreboardObjective addObjective(String name, ScoreboardCriterion criterion2, Text displayName, ScoreboardCriterion.RenderType renderType) {
        if (name.length() > 16) {
            throw new IllegalArgumentException("The objective name '" + name + "' is too long!");
        }
        if (this.objectives.containsKey(name)) {
            throw new IllegalArgumentException("An objective with the name '" + name + "' already exists!");
        }
        ScoreboardObjective scoreboardObjective = new ScoreboardObjective(this, name, criterion2, displayName, renderType);
        this.objectivesByCriterion.computeIfAbsent(criterion2, criterion -> Lists.newArrayList()).add(scoreboardObjective);
        this.objectives.put(name, scoreboardObjective);
        this.updateObjective(scoreboardObjective);
        return scoreboardObjective;
    }

    public final void forEachScore(ScoreboardCriterion criterion, String player, Consumer<ScoreboardPlayerScore> action) {
        this.objectivesByCriterion.getOrDefault(criterion, Collections.emptyList()).forEach(objective -> action.accept(this.getPlayerScore(player, (ScoreboardObjective)objective)));
    }

    public boolean playerHasObjective(String playerName, ScoreboardObjective objective) {
        Map<ScoreboardObjective, ScoreboardPlayerScore> map = this.playerObjectives.get(playerName);
        if (map == null) {
            return false;
        }
        ScoreboardPlayerScore scoreboardPlayerScore = map.get(objective);
        return scoreboardPlayerScore != null;
    }

    public ScoreboardPlayerScore getPlayerScore(String player, ScoreboardObjective objective2) {
        if (player.length() > 40) {
            throw new IllegalArgumentException("The player name '" + player + "' is too long!");
        }
        Map map = this.playerObjectives.computeIfAbsent(player, string -> Maps.newHashMap());
        return map.computeIfAbsent(objective2, objective -> {
            ScoreboardPlayerScore scoreboardPlayerScore = new ScoreboardPlayerScore(this, (ScoreboardObjective)objective, player);
            scoreboardPlayerScore.setScore(0);
            return scoreboardPlayerScore;
        });
    }

    public Collection<ScoreboardPlayerScore> getAllPlayerScores(ScoreboardObjective objective) {
        ArrayList list = Lists.newArrayList();
        for (Map<ScoreboardObjective, ScoreboardPlayerScore> map : this.playerObjectives.values()) {
            ScoreboardPlayerScore scoreboardPlayerScore = map.get(objective);
            if (scoreboardPlayerScore == null) continue;
            list.add(scoreboardPlayerScore);
        }
        Collections.sort(list, ScoreboardPlayerScore.COMPARATOR);
        return list;
    }

    public Collection<ScoreboardObjective> getObjectives() {
        return this.objectives.values();
    }

    public Collection<String> getObjectiveNames() {
        return this.objectives.keySet();
    }

    public Collection<String> getKnownPlayers() {
        return Lists.newArrayList(this.playerObjectives.keySet());
    }

    public void resetPlayerScore(String playerName, @Nullable ScoreboardObjective objective) {
        if (objective == null) {
            Map<ScoreboardObjective, ScoreboardPlayerScore> map = this.playerObjectives.remove(playerName);
            if (map != null) {
                this.updatePlayerScore(playerName);
            }
        } else {
            Map<ScoreboardObjective, ScoreboardPlayerScore> map = this.playerObjectives.get(playerName);
            if (map != null) {
                ScoreboardPlayerScore scoreboardPlayerScore = map.remove(objective);
                if (map.size() < 1) {
                    Map<ScoreboardObjective, ScoreboardPlayerScore> map2 = this.playerObjectives.remove(playerName);
                    if (map2 != null) {
                        this.updatePlayerScore(playerName);
                    }
                } else if (scoreboardPlayerScore != null) {
                    this.updatePlayerScore(playerName, objective);
                }
            }
        }
    }

    public Map<ScoreboardObjective, ScoreboardPlayerScore> getPlayerObjectives(String string) {
        HashMap map = this.playerObjectives.get(string);
        if (map == null) {
            map = Maps.newHashMap();
        }
        return map;
    }

    public void removeObjective(ScoreboardObjective objective) {
        this.objectives.remove(objective.getName());
        for (int i = 0; i < 19; ++i) {
            if (this.getObjectiveForSlot(i) != objective) continue;
            this.setObjectiveSlot(i, null);
        }
        List<ScoreboardObjective> list = this.objectivesByCriterion.get(objective.getCriterion());
        if (list != null) {
            list.remove(objective);
        }
        for (Map<ScoreboardObjective, ScoreboardPlayerScore> map : this.playerObjectives.values()) {
            map.remove(objective);
        }
        this.updateRemovedObjective(objective);
    }

    public void setObjectiveSlot(int slot, @Nullable ScoreboardObjective objective) {
        this.objectiveSlots[slot] = objective;
    }

    @Nullable
    public ScoreboardObjective getObjectiveForSlot(int i) {
        return this.objectiveSlots[i];
    }

    public Team getTeam(String string) {
        return this.teams.get(string);
    }

    public Team addTeam(String string) {
        if (string.length() > 16) {
            throw new IllegalArgumentException("The team name '" + string + "' is too long!");
        }
        Team team = this.getTeam(string);
        if (team != null) {
            throw new IllegalArgumentException("A team with the name '" + string + "' already exists!");
        }
        team = new Team(this, string);
        this.teams.put(string, team);
        this.updateScoreboardTeamAndPlayers(team);
        return team;
    }

    public void removeTeam(Team team) {
        this.teams.remove(team.getName());
        for (String string : team.getPlayerList()) {
            this.teamsByPlayer.remove(string);
        }
        this.updateRemovedTeam(team);
    }

    public boolean addPlayerToTeam(String playerName, Team team) {
        if (playerName.length() > 40) {
            throw new IllegalArgumentException("The player name '" + playerName + "' is too long!");
        }
        if (this.getPlayerTeam(playerName) != null) {
            this.clearPlayerTeam(playerName);
        }
        this.teamsByPlayer.put(playerName, team);
        return team.getPlayerList().add(playerName);
    }

    public boolean clearPlayerTeam(String string) {
        Team team = this.getPlayerTeam(string);
        if (team != null) {
            this.removePlayerFromTeam(string, team);
            return true;
        }
        return false;
    }

    public void removePlayerFromTeam(String playerName, Team team) {
        if (this.getPlayerTeam(playerName) != team) {
            throw new IllegalStateException("Player is either on another team or not on any team. Cannot remove from team '" + team.getName() + "'.");
        }
        this.teamsByPlayer.remove(playerName);
        team.getPlayerList().remove(playerName);
    }

    public Collection<String> getTeamNames() {
        return this.teams.keySet();
    }

    public Collection<Team> getTeams() {
        return this.teams.values();
    }

    @Nullable
    public Team getPlayerTeam(String string) {
        return this.teamsByPlayer.get(string);
    }

    public void updateObjective(ScoreboardObjective objective) {
    }

    public void updateExistingObjective(ScoreboardObjective objective) {
    }

    public void updateRemovedObjective(ScoreboardObjective objective) {
    }

    public void updateScore(ScoreboardPlayerScore score) {
    }

    public void updatePlayerScore(String playerName) {
    }

    public void updatePlayerScore(String playerName, ScoreboardObjective objective) {
    }

    public void updateScoreboardTeamAndPlayers(Team team) {
    }

    public void updateScoreboardTeam(Team team) {
    }

    public void updateRemovedTeam(Team team) {
    }

    public static String getDisplaySlotName(int slotId) {
        Formatting formatting;
        switch (slotId) {
            case 0: {
                return "list";
            }
            case 1: {
                return "sidebar";
            }
            case 2: {
                return "belowName";
            }
        }
        if (slotId >= 3 && slotId <= 18 && (formatting = Formatting.byColorIndex(slotId - 3)) != null && formatting != Formatting.RESET) {
            return "sidebar.team." + formatting.getName();
        }
        return null;
    }

    public static int getDisplaySlotId(String slotName) {
        String string;
        Formatting formatting;
        if ("list".equalsIgnoreCase(slotName)) {
            return 0;
        }
        if ("sidebar".equalsIgnoreCase(slotName)) {
            return 1;
        }
        if ("belowName".equalsIgnoreCase(slotName)) {
            return 2;
        }
        if (slotName.startsWith("sidebar.team.") && (formatting = Formatting.byName(string = slotName.substring("sidebar.team.".length()))) != null && formatting.getColorIndex() >= 0) {
            return formatting.getColorIndex() + 3;
        }
        return -1;
    }

    public static String[] getDisplaySlotNames() {
        if (displaySlotNames == null) {
            displaySlotNames = new String[19];
            for (int i = 0; i < 19; ++i) {
                Scoreboard.displaySlotNames[i] = Scoreboard.getDisplaySlotName(i);
            }
        }
        return displaySlotNames;
    }

    public void resetEntityScore(Entity entity) {
        if (entity == null || entity instanceof PlayerEntity || entity.isAlive()) {
            return;
        }
        String string = entity.getUuidAsString();
        this.resetPlayerScore(string, null);
        this.clearPlayerTeam(string);
    }

    protected ListTag toTag() {
        ListTag listTag = new ListTag();
        this.playerObjectives.values().stream().map(Map::values).forEach(collection -> collection.stream().filter(score -> score.getObjective() != null).forEach(score -> {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putString("Name", score.getPlayerName());
            compoundTag.putString("Objective", score.getObjective().getName());
            compoundTag.putInt("Score", score.getScore());
            compoundTag.putBoolean("Locked", score.isLocked());
            listTag.add(compoundTag);
        }));
        return listTag;
    }

    protected void fromTag(ListTag listTag) {
        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompound(i);
            ScoreboardObjective scoreboardObjective = this.getObjective(compoundTag.getString("Objective"));
            String string = compoundTag.getString("Name");
            if (string.length() > 40) {
                string = string.substring(0, 40);
            }
            ScoreboardPlayerScore scoreboardPlayerScore = this.getPlayerScore(string, scoreboardObjective);
            scoreboardPlayerScore.setScore(compoundTag.getInt("Score"));
            if (!compoundTag.contains("Locked")) continue;
            scoreboardPlayerScore.setLocked(compoundTag.getBoolean("Locked"));
        }
    }
}
