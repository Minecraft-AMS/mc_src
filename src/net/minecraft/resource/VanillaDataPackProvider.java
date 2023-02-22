/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource;

import java.util.Map;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;

public class VanillaDataPackProvider
implements ResourcePackProvider {
    private final DefaultResourcePack pack = new DefaultResourcePack("minecraft");

    @Override
    public <T extends ResourcePackProfile> void register(Map<String, T> registry, ResourcePackProfile.Factory<T> factory) {
        T resourcePackProfile = ResourcePackProfile.of("vanilla", false, () -> this.pack, factory, ResourcePackProfile.InsertionPosition.BOTTOM);
        if (resourcePackProfile != null) {
            registry.put("vanilla", resourcePackProfile);
        }
    }
}

