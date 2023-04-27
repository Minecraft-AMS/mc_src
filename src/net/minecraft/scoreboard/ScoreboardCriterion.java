/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 */
package net.minecraft.scoreboard;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.registry.Registries;
import net.minecraft.stat.StatType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;

public class ScoreboardCriterion {
    private static final Map<String, ScoreboardCriterion> SIMPLE_CRITERIA = Maps.newHashMap();
    private static final Map<String, ScoreboardCriterion> CRITERIA = Maps.newHashMap();
    public static final ScoreboardCriterion DUMMY = ScoreboardCriterion.create("dummy");
    public static final ScoreboardCriterion TRIGGER = ScoreboardCriterion.create("trigger");
    public static final ScoreboardCriterion DEATH_COUNT = ScoreboardCriterion.create("deathCount");
    public static final ScoreboardCriterion PLAYER_KILL_COUNT = ScoreboardCriterion.create("playerKillCount");
    public static final ScoreboardCriterion TOTAL_KILL_COUNT = ScoreboardCriterion.create("totalKillCount");
    public static final ScoreboardCriterion HEALTH = ScoreboardCriterion.create("health", true, RenderType.HEARTS);
    public static final ScoreboardCriterion FOOD = ScoreboardCriterion.create("food", true, RenderType.INTEGER);
    public static final ScoreboardCriterion AIR = ScoreboardCriterion.create("air", true, RenderType.INTEGER);
    public static final ScoreboardCriterion ARMOR = ScoreboardCriterion.create("armor", true, RenderType.INTEGER);
    public static final ScoreboardCriterion XP = ScoreboardCriterion.create("xp", true, RenderType.INTEGER);
    public static final ScoreboardCriterion LEVEL = ScoreboardCriterion.create("level", true, RenderType.INTEGER);
    public static final ScoreboardCriterion[] TEAM_KILLS = new ScoreboardCriterion[]{ScoreboardCriterion.create("teamkill." + Formatting.BLACK.getName()), ScoreboardCriterion.create("teamkill." + Formatting.DARK_BLUE.getName()), ScoreboardCriterion.create("teamkill." + Formatting.DARK_GREEN.getName()), ScoreboardCriterion.create("teamkill." + Formatting.DARK_AQUA.getName()), ScoreboardCriterion.create("teamkill." + Formatting.DARK_RED.getName()), ScoreboardCriterion.create("teamkill." + Formatting.DARK_PURPLE.getName()), ScoreboardCriterion.create("teamkill." + Formatting.GOLD.getName()), ScoreboardCriterion.create("teamkill." + Formatting.GRAY.getName()), ScoreboardCriterion.create("teamkill." + Formatting.DARK_GRAY.getName()), ScoreboardCriterion.create("teamkill." + Formatting.BLUE.getName()), ScoreboardCriterion.create("teamkill." + Formatting.GREEN.getName()), ScoreboardCriterion.create("teamkill." + Formatting.AQUA.getName()), ScoreboardCriterion.create("teamkill." + Formatting.RED.getName()), ScoreboardCriterion.create("teamkill." + Formatting.LIGHT_PURPLE.getName()), ScoreboardCriterion.create("teamkill." + Formatting.YELLOW.getName()), ScoreboardCriterion.create("teamkill." + Formatting.WHITE.getName())};
    public static final ScoreboardCriterion[] KILLED_BY_TEAMS = new ScoreboardCriterion[]{ScoreboardCriterion.create("killedByTeam." + Formatting.BLACK.getName()), ScoreboardCriterion.create("killedByTeam." + Formatting.DARK_BLUE.getName()), ScoreboardCriterion.create("killedByTeam." + Formatting.DARK_GREEN.getName()), ScoreboardCriterion.create("killedByTeam." + Formatting.DARK_AQUA.getName()), ScoreboardCriterion.create("killedByTeam." + Formatting.DARK_RED.getName()), ScoreboardCriterion.create("killedByTeam." + Formatting.DARK_PURPLE.getName()), ScoreboardCriterion.create("killedByTeam." + Formatting.GOLD.getName()), ScoreboardCriterion.create("killedByTeam." + Formatting.GRAY.getName()), ScoreboardCriterion.create("killedByTeam." + Formatting.DARK_GRAY.getName()), ScoreboardCriterion.create("killedByTeam." + Formatting.BLUE.getName()), ScoreboardCriterion.create("killedByTeam." + Formatting.GREEN.getName()), ScoreboardCriterion.create("killedByTeam." + Formatting.AQUA.getName()), ScoreboardCriterion.create("killedByTeam." + Formatting.RED.getName()), ScoreboardCriterion.create("killedByTeam." + Formatting.LIGHT_PURPLE.getName()), ScoreboardCriterion.create("killedByTeam." + Formatting.YELLOW.getName()), ScoreboardCriterion.create("killedByTeam." + Formatting.WHITE.getName())};
    private final String name;
    private final boolean readOnly;
    private final RenderType defaultRenderType;

    public static ScoreboardCriterion create(String name, boolean readOnly, RenderType defaultRenderType) {
        ScoreboardCriterion scoreboardCriterion = new ScoreboardCriterion(name, readOnly, defaultRenderType);
        SIMPLE_CRITERIA.put(name, scoreboardCriterion);
        return scoreboardCriterion;
    }

    public static ScoreboardCriterion create(String name) {
        return ScoreboardCriterion.create(name, false, RenderType.INTEGER);
    }

    protected ScoreboardCriterion(String name) {
        this(name, false, RenderType.INTEGER);
    }

    protected ScoreboardCriterion(String name, boolean readOnly, RenderType defaultRenderType) {
        this.name = name;
        this.readOnly = readOnly;
        this.defaultRenderType = defaultRenderType;
        CRITERIA.put(name, this);
    }

    public static Set<String> getAllSimpleCriteria() {
        return ImmutableSet.copyOf(SIMPLE_CRITERIA.keySet());
    }

    public static Optional<ScoreboardCriterion> getOrCreateStatCriterion(String name) {
        ScoreboardCriterion scoreboardCriterion = CRITERIA.get(name);
        if (scoreboardCriterion != null) {
            return Optional.of(scoreboardCriterion);
        }
        int i = name.indexOf(58);
        if (i < 0) {
            return Optional.empty();
        }
        return Registries.STAT_TYPE.getOrEmpty(Identifier.splitOn(name.substring(0, i), '.')).flatMap(type -> ScoreboardCriterion.getOrCreateStatCriterion(type, Identifier.splitOn(name.substring(i + 1), '.')));
    }

    private static <T> Optional<ScoreboardCriterion> getOrCreateStatCriterion(StatType<T> statType, Identifier id) {
        return statType.getRegistry().getOrEmpty(id).map(statType::getOrCreateStat);
    }

    public String getName() {
        return this.name;
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public RenderType getDefaultRenderType() {
        return this.defaultRenderType;
    }

    public static final class RenderType
    extends Enum<RenderType>
    implements StringIdentifiable {
        public static final /* enum */ RenderType INTEGER = new RenderType("integer");
        public static final /* enum */ RenderType HEARTS = new RenderType("hearts");
        private final String name;
        public static final StringIdentifiable.Codec<RenderType> CODEC;
        private static final /* synthetic */ RenderType[] field_1473;

        public static RenderType[] values() {
            return (RenderType[])field_1473.clone();
        }

        public static RenderType valueOf(String string) {
            return Enum.valueOf(RenderType.class, string);
        }

        private RenderType(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        public static RenderType getType(String name) {
            return CODEC.byId(name, INTEGER);
        }

        private static /* synthetic */ RenderType[] method_36799() {
            return new RenderType[]{INTEGER, HEARTS};
        }

        static {
            field_1473 = RenderType.method_36799();
            CODEC = StringIdentifiable.createCodec(RenderType::values);
        }
    }
}

