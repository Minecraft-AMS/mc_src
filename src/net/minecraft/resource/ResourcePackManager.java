/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Functions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.resource;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.featuretoggle.FeatureSet;
import org.jetbrains.annotations.Nullable;

public class ResourcePackManager {
    private final Set<ResourcePackProvider> providers;
    private Map<String, ResourcePackProfile> profiles = ImmutableMap.of();
    private List<ResourcePackProfile> enabled = ImmutableList.of();

    public ResourcePackManager(ResourcePackProvider ... providers) {
        this.providers = ImmutableSet.copyOf((Object[])providers);
    }

    public void scanPacks() {
        List list = (List)this.enabled.stream().map(ResourcePackProfile::getName).collect(ImmutableList.toImmutableList());
        this.profiles = this.providePackProfiles();
        this.enabled = this.buildEnabledProfiles(list);
    }

    private Map<String, ResourcePackProfile> providePackProfiles() {
        TreeMap map = Maps.newTreeMap();
        for (ResourcePackProvider resourcePackProvider : this.providers) {
            resourcePackProvider.register(profile -> map.put(profile.getName(), profile));
        }
        return ImmutableMap.copyOf((Map)map);
    }

    public void setEnabledProfiles(Collection<String> enabled) {
        this.enabled = this.buildEnabledProfiles(enabled);
    }

    private List<ResourcePackProfile> buildEnabledProfiles(Collection<String> enabledNames) {
        List list = this.streamProfilesByName(enabledNames).collect(Collectors.toList());
        for (ResourcePackProfile resourcePackProfile : this.profiles.values()) {
            if (!resourcePackProfile.isAlwaysEnabled() || list.contains(resourcePackProfile)) continue;
            resourcePackProfile.getInitialPosition().insert(list, resourcePackProfile, Functions.identity(), false);
        }
        return ImmutableList.copyOf(list);
    }

    private Stream<ResourcePackProfile> streamProfilesByName(Collection<String> names) {
        return names.stream().map(this.profiles::get).filter(Objects::nonNull);
    }

    public Collection<String> getNames() {
        return this.profiles.keySet();
    }

    public Collection<ResourcePackProfile> getProfiles() {
        return this.profiles.values();
    }

    public Collection<String> getEnabledNames() {
        return (Collection)this.enabled.stream().map(ResourcePackProfile::getName).collect(ImmutableSet.toImmutableSet());
    }

    public FeatureSet getRequestedFeatures() {
        return this.getEnabledProfiles().stream().map(ResourcePackProfile::getRequestedFeatures).reduce(FeatureSet::combine).orElse(FeatureSet.empty());
    }

    public Collection<ResourcePackProfile> getEnabledProfiles() {
        return this.enabled;
    }

    @Nullable
    public ResourcePackProfile getProfile(String name) {
        return this.profiles.get(name);
    }

    public boolean hasProfile(String name) {
        return this.profiles.containsKey(name);
    }

    public List<ResourcePack> createResourcePacks() {
        return (List)this.enabled.stream().map(ResourcePackProfile::createResourcePack).collect(ImmutableList.toImmutableList());
    }
}

