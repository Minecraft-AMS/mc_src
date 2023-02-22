/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.input;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.input.Input;
import net.minecraft.client.option.GameOptions;

@Environment(value=EnvType.CLIENT)
public class KeyboardInput
extends Input {
    private final GameOptions settings;
    private static final float field_32670 = 0.3f;

    public KeyboardInput(GameOptions settings) {
        this.settings = settings;
    }

    private static float getMovementMultiplier(boolean positive, boolean negative) {
        if (positive == negative) {
            return 0.0f;
        }
        return positive ? 1.0f : -1.0f;
    }

    @Override
    public void tick(boolean slowDown) {
        this.pressingForward = this.settings.forwardKey.isPressed();
        this.pressingBack = this.settings.backKey.isPressed();
        this.pressingLeft = this.settings.leftKey.isPressed();
        this.pressingRight = this.settings.rightKey.isPressed();
        this.movementForward = KeyboardInput.getMovementMultiplier(this.pressingForward, this.pressingBack);
        this.movementSideways = KeyboardInput.getMovementMultiplier(this.pressingLeft, this.pressingRight);
        this.jumping = this.settings.jumpKey.isPressed();
        this.sneaking = this.settings.sneakKey.isPressed();
        if (slowDown) {
            this.movementSideways *= 0.3f;
            this.movementForward *= 0.3f;
        }
    }
}

