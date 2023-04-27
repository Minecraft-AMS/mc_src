/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft;

import java.util.Date;
import net.minecraft.SaveVersion;
import net.minecraft.resource.ResourceType;

public interface GameVersion {
    public SaveVersion getSaveVersion();

    public String getId();

    public String getName();

    public int getProtocolVersion();

    public int getResourceVersion(ResourceType var1);

    public Date getBuildTime();

    public boolean isStable();
}

