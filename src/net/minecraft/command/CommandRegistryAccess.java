/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command;

import java.util.Optional;
import net.minecraft.command.CommandRegistryWrapper;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.util.registry.RegistryKey;

public final class CommandRegistryAccess {
    private final DynamicRegistryManager dynamicRegistryManager;
    EntryListCreationPolicy entryListCreationPolicy = EntryListCreationPolicy.FAIL;

    public CommandRegistryAccess(DynamicRegistryManager dynamicRegistryManager) {
        this.dynamicRegistryManager = dynamicRegistryManager;
    }

    public void setEntryListCreationPolicy(EntryListCreationPolicy entryListCreationPolicy) {
        this.entryListCreationPolicy = entryListCreationPolicy;
    }

    public <T> CommandRegistryWrapper<T> createWrapper(RegistryKey<? extends Registry<T>> registryRef) {
        return new CommandRegistryWrapper.Impl<T>(this.dynamicRegistryManager.get(registryRef)){

            @Override
            public Optional<? extends RegistryEntryList<T>> getEntryList(TagKey<T> tag) {
                return switch (CommandRegistryAccess.this.entryListCreationPolicy) {
                    default -> throw new IncompatibleClassChangeError();
                    case EntryListCreationPolicy.FAIL -> this.registry.getEntryList(tag);
                    case EntryListCreationPolicy.CREATE_NEW -> Optional.of(this.registry.getOrCreateEntryList(tag));
                    case EntryListCreationPolicy.RETURN_EMPTY -> {
                        Optional optional = this.registry.getEntryList(tag);
                        yield Optional.of(optional.isPresent() ? (RegistryEntryList.Direct)((Object)optional.get()) : RegistryEntryList.of(new RegistryEntry[0]));
                    }
                };
            }
        };
    }

    public static final class EntryListCreationPolicy
    extends Enum<EntryListCreationPolicy> {
        public static final /* enum */ EntryListCreationPolicy CREATE_NEW = new EntryListCreationPolicy();
        public static final /* enum */ EntryListCreationPolicy RETURN_EMPTY = new EntryListCreationPolicy();
        public static final /* enum */ EntryListCreationPolicy FAIL = new EntryListCreationPolicy();
        private static final /* synthetic */ EntryListCreationPolicy[] field_37827;

        public static EntryListCreationPolicy[] values() {
            return (EntryListCreationPolicy[])field_37827.clone();
        }

        public static EntryListCreationPolicy valueOf(String string) {
            return Enum.valueOf(EntryListCreationPolicy.class, string);
        }

        private static /* synthetic */ EntryListCreationPolicy[] method_41701() {
            return new EntryListCreationPolicy[]{CREATE_NEW, RETURN_EMPTY, FAIL};
        }

        static {
            field_37827 = EntryListCreationPolicy.method_41701();
        }
    }
}

