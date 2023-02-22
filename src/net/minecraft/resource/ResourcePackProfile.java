/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.logging.LogUtils
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.resource;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.function.Function;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.resource.metadata.PackFeatureSetMetadata;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ResourcePackProfile {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final String name;
    private final PackFactory packFactory;
    private final Text displayName;
    private final Text description;
    private final ResourcePackCompatibility compatibility;
    private final FeatureSet requestedFeatures;
    private final InsertionPosition position;
    private final boolean alwaysEnabled;
    private final boolean pinned;
    private final ResourcePackSource source;

    @Nullable
    public static ResourcePackProfile create(String name, Text displayName, boolean alwaysEnabled, PackFactory packFactory, ResourceType type, InsertionPosition position, ResourcePackSource source) {
        Metadata metadata = ResourcePackProfile.loadMetadata(name, packFactory);
        return metadata != null ? ResourcePackProfile.of(name, displayName, alwaysEnabled, packFactory, metadata, type, position, false, source) : null;
    }

    public static ResourcePackProfile of(String name, Text displayName, boolean alwaysEnabled, PackFactory packFactory, Metadata metadata, ResourceType type, InsertionPosition position, boolean pinned, ResourcePackSource source) {
        return new ResourcePackProfile(name, alwaysEnabled, packFactory, displayName, metadata, metadata.getCompatibility(type), position, pinned, source);
    }

    private ResourcePackProfile(String name, boolean alwaysEnabled, PackFactory packFactory, Text displayName, Metadata metadata, ResourcePackCompatibility compatibility, InsertionPosition position, boolean pinned, ResourcePackSource source) {
        this.name = name;
        this.packFactory = packFactory;
        this.displayName = displayName;
        this.description = metadata.description();
        this.compatibility = compatibility;
        this.requestedFeatures = metadata.requestedFeatures();
        this.alwaysEnabled = alwaysEnabled;
        this.position = position;
        this.pinned = pinned;
        this.source = source;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    public static Metadata loadMetadata(String name, PackFactory packFactory) {
        try (ResourcePack resourcePack = packFactory.open(name);){
            PackResourceMetadata packResourceMetadata = resourcePack.parseMetadata(PackResourceMetadata.SERIALIZER);
            if (packResourceMetadata == null) {
                LOGGER.warn("Missing metadata in pack {}", (Object)name);
                Metadata metadata = null;
                return metadata;
            }
            PackFeatureSetMetadata packFeatureSetMetadata = resourcePack.parseMetadata(PackFeatureSetMetadata.SERIALIZER);
            FeatureSet featureSet = packFeatureSetMetadata != null ? packFeatureSetMetadata.flags() : FeatureSet.empty();
            Metadata metadata = new Metadata(packResourceMetadata.getDescription(), packResourceMetadata.getPackFormat(), featureSet);
            return metadata;
        }
        catch (Exception exception) {
            LOGGER.warn("Failed to read pack metadata", (Throwable)exception);
            return null;
        }
    }

    public Text getDisplayName() {
        return this.displayName;
    }

    public Text getDescription() {
        return this.description;
    }

    public Text getInformationText(boolean enabled) {
        return Texts.bracketed(this.source.decorate(Text.literal(this.name))).styled(style -> style.withColor(enabled ? Formatting.GREEN : Formatting.RED).withInsertion(StringArgumentType.escapeIfRequired((String)this.name)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.empty().append(this.displayName).append("\n").append(this.description))));
    }

    public ResourcePackCompatibility getCompatibility() {
        return this.compatibility;
    }

    public FeatureSet getRequestedFeatures() {
        return this.requestedFeatures;
    }

    public ResourcePack createResourcePack() {
        return this.packFactory.open(this.name);
    }

    public String getName() {
        return this.name;
    }

    public boolean isAlwaysEnabled() {
        return this.alwaysEnabled;
    }

    public boolean isPinned() {
        return this.pinned;
    }

    public InsertionPosition getInitialPosition() {
        return this.position;
    }

    public ResourcePackSource getSource() {
        return this.source;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResourcePackProfile)) {
            return false;
        }
        ResourcePackProfile resourcePackProfile = (ResourcePackProfile)o;
        return this.name.equals(resourcePackProfile.name);
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    @FunctionalInterface
    public static interface PackFactory {
        public ResourcePack open(String var1);
    }

    public record Metadata(Text description, int format, FeatureSet requestedFeatures) {
        public ResourcePackCompatibility getCompatibility(ResourceType type) {
            return ResourcePackCompatibility.from(this.format, type);
        }
    }

    public static final class InsertionPosition
    extends Enum<InsertionPosition> {
        public static final /* enum */ InsertionPosition TOP = new InsertionPosition();
        public static final /* enum */ InsertionPosition BOTTOM = new InsertionPosition();
        private static final /* synthetic */ InsertionPosition[] field_14282;

        public static InsertionPosition[] values() {
            return (InsertionPosition[])field_14282.clone();
        }

        public static InsertionPosition valueOf(String string) {
            return Enum.valueOf(InsertionPosition.class, string);
        }

        public <T> int insert(List<T> items, T item, Function<T, ResourcePackProfile> profileGetter, boolean listInverted) {
            ResourcePackProfile resourcePackProfile;
            int i;
            InsertionPosition insertionPosition;
            InsertionPosition insertionPosition2 = insertionPosition = listInverted ? this.inverse() : this;
            if (insertionPosition == BOTTOM) {
                ResourcePackProfile resourcePackProfile2;
                int i2;
                for (i2 = 0; i2 < items.size() && (resourcePackProfile2 = profileGetter.apply(items.get(i2))).isPinned() && resourcePackProfile2.getInitialPosition() == this; ++i2) {
                }
                items.add(i2, item);
                return i2;
            }
            for (i = items.size() - 1; i >= 0 && (resourcePackProfile = profileGetter.apply(items.get(i))).isPinned() && resourcePackProfile.getInitialPosition() == this; --i) {
            }
            items.add(i + 1, item);
            return i + 1;
        }

        public InsertionPosition inverse() {
            return this == TOP ? BOTTOM : TOP;
        }

        private static /* synthetic */ InsertionPosition[] method_36583() {
            return new InsertionPosition[]{TOP, BOTTOM};
        }

        static {
            field_14282 = InsertionPosition.method_36583();
        }
    }
}

