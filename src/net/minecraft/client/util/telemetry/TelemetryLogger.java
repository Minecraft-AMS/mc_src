/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.util.telemetry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.telemetry.SentTelemetryEvent;

@Environment(value=EnvType.CLIENT)
public interface TelemetryLogger {
    public void log(SentTelemetryEvent var1);
}

