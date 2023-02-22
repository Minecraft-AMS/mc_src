/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world;

import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public final class GameMode
extends Enum<GameMode> {
    public static final /* enum */ GameMode SURVIVAL = new GameMode(0, "survival");
    public static final /* enum */ GameMode CREATIVE = new GameMode(1, "creative");
    public static final /* enum */ GameMode ADVENTURE = new GameMode(2, "adventure");
    public static final /* enum */ GameMode SPECTATOR = new GameMode(3, "spectator");
    public static final GameMode DEFAULT;
    private static final int field_30964 = -1;
    private final int id;
    private final String name;
    private final Text simpleTranslatableName;
    private final Text translatableName;
    private static final /* synthetic */ GameMode[] field_9222;

    public static GameMode[] values() {
        return (GameMode[])field_9222.clone();
    }

    public static GameMode valueOf(String string) {
        return Enum.valueOf(GameMode.class, string);
    }

    private GameMode(int id, String name) {
        this.id = id;
        this.name = name;
        this.simpleTranslatableName = Text.translatable("selectWorld.gameMode." + name);
        this.translatableName = Text.translatable("gameMode." + name);
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Text getTranslatableName() {
        return this.translatableName;
    }

    public Text getSimpleTranslatableName() {
        return this.simpleTranslatableName;
    }

    public void setAbilities(PlayerAbilities abilities) {
        if (this == CREATIVE) {
            abilities.allowFlying = true;
            abilities.creativeMode = true;
            abilities.invulnerable = true;
        } else if (this == SPECTATOR) {
            abilities.allowFlying = true;
            abilities.creativeMode = false;
            abilities.invulnerable = true;
            abilities.flying = true;
        } else {
            abilities.allowFlying = false;
            abilities.creativeMode = false;
            abilities.invulnerable = false;
            abilities.flying = false;
        }
        abilities.allowModifyWorld = !this.isBlockBreakingRestricted();
    }

    public boolean isBlockBreakingRestricted() {
        return this == ADVENTURE || this == SPECTATOR;
    }

    public boolean isCreative() {
        return this == CREATIVE;
    }

    public boolean isSurvivalLike() {
        return this == SURVIVAL || this == ADVENTURE;
    }

    public static GameMode byId(int id) {
        return GameMode.byId(id, DEFAULT);
    }

    public static GameMode byId(int id, GameMode defaultMode) {
        for (GameMode gameMode : GameMode.values()) {
            if (gameMode.id != id) continue;
            return gameMode;
        }
        return defaultMode;
    }

    public static GameMode byName(String name) {
        return GameMode.byName(name, SURVIVAL);
    }

    public static GameMode byName(String name, GameMode defaultMode) {
        for (GameMode gameMode : GameMode.values()) {
            if (!gameMode.name.equals(name)) continue;
            return gameMode;
        }
        return defaultMode;
    }

    public static int getId(@Nullable GameMode gameMode) {
        return gameMode != null ? gameMode.id : -1;
    }

    @Nullable
    public static GameMode getOrNull(int id) {
        if (id == -1) {
            return null;
        }
        return GameMode.byId(id);
    }

    private static /* synthetic */ GameMode[] method_36695() {
        return new GameMode[]{SURVIVAL, CREATIVE, ADVENTURE, SPECTATOR};
    }

    static {
        field_9222 = GameMode.method_36695();
        DEFAULT = SURVIVAL;
    }
}

