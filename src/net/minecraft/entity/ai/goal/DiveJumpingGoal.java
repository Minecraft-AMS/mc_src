/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.ai.goal.Goal;

public abstract class DiveJumpingGoal
extends Goal {
    public DiveJumpingGoal() {
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.JUMP));
    }

    protected float updatePitch(float previousPitch, float f, float g) {
        float h;
        for (h = f - previousPitch; h < -180.0f; h += 360.0f) {
        }
        while (h >= 180.0f) {
            h -= 360.0f;
        }
        return previousPitch + g * h;
    }
}

