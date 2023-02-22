/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource;

import java.io.IOException;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

public interface ResourceFactory {
    public Resource getResource(Identifier var1) throws IOException;
}

