/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world;

import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public enum GameMode {
    NOT_SET(-1, ""),
    SURVIVAL(0, "survival"),
    CREATIVE(1, "creative"),
    ADVENTURE(2, "adventure"),
    SPECTATOR(3, "spectator");

    private final int id;
    private final String name;

    private GameMode(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Text getTranslatableName() {
        return new TranslatableText("gameMode." + this.name, new Object[0]);
    }

    public void setAbilitites(PlayerAbilities abilities) {
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
        abilities.allowModifyWorld = !this.shouldLimitWorldModification();
    }

    public boolean shouldLimitWorldModification() {
        return this == ADVENTURE || this == SPECTATOR;
    }

    public boolean isCreative() {
        return this == CREATIVE;
    }

    public boolean isSurvivalLike() {
        return this == SURVIVAL || this == ADVENTURE;
    }

    public static GameMode byId(int id) {
        return GameMode.byId(id, SURVIVAL);
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
}

