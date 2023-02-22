/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;

@Environment(value=EnvType.CLIENT)
public class RealmsSharedConstants {
    public static final int TICKS_PER_SECOND = 20;
    public static final char[] ILLEGAL_FILE_CHARACTERS = SharedConstants.INVALID_CHARS_LEVEL_NAME;
}

