/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.mojang.realmsclient.dto;

import com.mojang.realmsclient.dto.RegionPingResult;
import com.mojang.realmsclient.dto.ValueObject;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class PingResult
extends ValueObject {
    public List<RegionPingResult> pingResults = new ArrayList<RegionPingResult>();
    public List<Long> worldIds = new ArrayList<Long>();
}

