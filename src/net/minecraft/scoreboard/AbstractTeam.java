/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.scoreboard;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractTeam {
    public boolean isEqual(@Nullable AbstractTeam team) {
        if (team == null) {
            return false;
        }
        return this == team;
    }

    public abstract String getName();

    public abstract MutableText decorateName(Text var1);

    public abstract boolean shouldShowFriendlyInvisibles();

    public abstract boolean isFriendlyFireAllowed();

    public abstract VisibilityRule getNameTagVisibilityRule();

    public abstract Formatting getColor();

    public abstract Collection<String> getPlayerList();

    public abstract VisibilityRule getDeathMessageVisibilityRule();

    public abstract CollisionRule getCollisionRule();

    public static final class CollisionRule
    extends Enum<CollisionRule> {
        public static final /* enum */ CollisionRule ALWAYS = new CollisionRule("always", 0);
        public static final /* enum */ CollisionRule NEVER = new CollisionRule("never", 1);
        public static final /* enum */ CollisionRule PUSH_OTHER_TEAMS = new CollisionRule("pushOtherTeams", 2);
        public static final /* enum */ CollisionRule PUSH_OWN_TEAM = new CollisionRule("pushOwnTeam", 3);
        private static final Map<String, CollisionRule> COLLISION_RULES;
        public final String name;
        public final int value;
        private static final /* synthetic */ CollisionRule[] field_1439;

        public static CollisionRule[] values() {
            return (CollisionRule[])field_1439.clone();
        }

        public static CollisionRule valueOf(String string) {
            return Enum.valueOf(CollisionRule.class, string);
        }

        @Nullable
        public static CollisionRule getRule(String name) {
            return COLLISION_RULES.get(name);
        }

        private CollisionRule(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public Text getTranslationKey() {
            return new TranslatableText("team.collision." + this.name);
        }

        private static /* synthetic */ CollisionRule[] method_36797() {
            return new CollisionRule[]{ALWAYS, NEVER, PUSH_OTHER_TEAMS, PUSH_OWN_TEAM};
        }

        static {
            field_1439 = CollisionRule.method_36797();
            COLLISION_RULES = Arrays.stream(CollisionRule.values()).collect(Collectors.toMap(collisionRule -> collisionRule.name, collisionRule -> collisionRule));
        }
    }

    public static final class VisibilityRule
    extends Enum<VisibilityRule> {
        public static final /* enum */ VisibilityRule ALWAYS = new VisibilityRule("always", 0);
        public static final /* enum */ VisibilityRule NEVER = new VisibilityRule("never", 1);
        public static final /* enum */ VisibilityRule HIDE_FOR_OTHER_TEAMS = new VisibilityRule("hideForOtherTeams", 2);
        public static final /* enum */ VisibilityRule HIDE_FOR_OWN_TEAM = new VisibilityRule("hideForOwnTeam", 3);
        private static final Map<String, VisibilityRule> VISIBILITY_RULES;
        public final String name;
        public final int value;
        private static final /* synthetic */ VisibilityRule[] field_1448;

        public static VisibilityRule[] values() {
            return (VisibilityRule[])field_1448.clone();
        }

        public static VisibilityRule valueOf(String string) {
            return Enum.valueOf(VisibilityRule.class, string);
        }

        public static String[] method_35595() {
            return VISIBILITY_RULES.keySet().toArray(new String[VISIBILITY_RULES.size()]);
        }

        @Nullable
        public static VisibilityRule getRule(String name) {
            return VISIBILITY_RULES.get(name);
        }

        private VisibilityRule(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public Text getTranslationKey() {
            return new TranslatableText("team.visibility." + this.name);
        }

        private static /* synthetic */ VisibilityRule[] method_36798() {
            return new VisibilityRule[]{ALWAYS, NEVER, HIDE_FOR_OTHER_TEAMS, HIDE_FOR_OWN_TEAM};
        }

        static {
            field_1448 = VisibilityRule.method_36798();
            VISIBILITY_RULES = Arrays.stream(VisibilityRule.values()).collect(Collectors.toMap(visibilityRule -> visibilityRule.name, visibilityRule -> visibilityRule));
        }
    }
}

