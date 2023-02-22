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
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
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
    private final Supplier<ResourcePack> packFactory;
    private final Text displayName;
    private final Text description;
    private final ResourcePackCompatibility compatibility;
    private final InsertionPosition position;
    private final boolean alwaysEnabled;
    private final boolean pinned;
    private final ResourcePackSource source;

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    public static ResourcePackProfile of(String name, boolean alwaysEnabled, Supplier<ResourcePack> packFactory, Factory profileFactory, InsertionPosition insertionPosition, ResourcePackSource packSource) {
        try (ResourcePack resourcePack = packFactory.get();){
            PackResourceMetadata packResourceMetadata = resourcePack.parseMetadata(PackResourceMetadata.READER);
            if (packResourceMetadata != null) {
                ResourcePackProfile resourcePackProfile = profileFactory.create(name, Text.literal(resourcePack.getName()), alwaysEnabled, packFactory, packResourceMetadata, insertionPosition, packSource);
                return resourcePackProfile;
            }
            LOGGER.warn("Couldn't find pack meta for pack {}", (Object)name);
            return null;
        }
        catch (IOException iOException) {
            LOGGER.warn("Couldn't get pack info for: {}", (Object)iOException.toString());
        }
        return null;
    }

    public ResourcePackProfile(String name, boolean alwaysEnabled, Supplier<ResourcePack> packFactory, Text displayName, Text description, ResourcePackCompatibility compatibility, InsertionPosition direction, boolean pinned, ResourcePackSource source) {
        this.name = name;
        this.packFactory = packFactory;
        this.displayName = displayName;
        this.description = description;
        this.compatibility = compatibility;
        this.alwaysEnabled = alwaysEnabled;
        this.position = direction;
        this.pinned = pinned;
        this.source = source;
    }

    public ResourcePackProfile(String name, Text displayName, boolean alwaysEnabled, Supplier<ResourcePack> packFactory, PackResourceMetadata metadata, ResourceType type, InsertionPosition direction, ResourcePackSource source) {
        this(name, alwaysEnabled, packFactory, displayName, metadata.getDescription(), ResourcePackCompatibility.from(metadata, type), direction, false, source);
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

    public ResourcePack createResourcePack() {
        return this.packFactory.get();
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
    public static interface Factory {
        @Nullable
        public ResourcePackProfile create(String var1, Text var2, boolean var3, Supplier<ResourcePack> var4, PackResourceMetadata var5, InsertionPosition var6, ResourcePackSource var7);
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

