/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.advancement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class AdvancementObtainedStatus
extends Enum<AdvancementObtainedStatus> {
    public static final /* enum */ AdvancementObtainedStatus OBTAINED = new AdvancementObtainedStatus(0);
    public static final /* enum */ AdvancementObtainedStatus UNOBTAINED = new AdvancementObtainedStatus(1);
    private final int spriteIndex;
    private static final /* synthetic */ AdvancementObtainedStatus[] field_2698;

    public static AdvancementObtainedStatus[] values() {
        return (AdvancementObtainedStatus[])field_2698.clone();
    }

    public static AdvancementObtainedStatus valueOf(String string) {
        return Enum.valueOf(AdvancementObtainedStatus.class, string);
    }

    private AdvancementObtainedStatus(int spriteIndex) {
        this.spriteIndex = spriteIndex;
    }

    public int getSpriteIndex() {
        return this.spriteIndex;
    }

    private static /* synthetic */ AdvancementObtainedStatus[] method_36884() {
        return new AdvancementObtainedStatus[]{OBTAINED, UNOBTAINED};
    }

    static {
        field_2698 = AdvancementObtainedStatus.method_36884();
    }
}

