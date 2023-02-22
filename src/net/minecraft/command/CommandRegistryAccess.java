/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.featuretoggle.FeatureSet;

public interface CommandRegistryAccess {
    public <T> RegistryWrapper<T> createWrapper(RegistryKey<? extends Registry<T>> var1);

    public static CommandRegistryAccess of(final RegistryWrapper.WrapperLookup wrapperLookup, final FeatureSet enabledFeatures) {
        return new CommandRegistryAccess(){

            @Override
            public <T> RegistryWrapper<T> createWrapper(RegistryKey<? extends Registry<T>> registryRef) {
                return wrapperLookup.getWrapperOrThrow(registryRef).withFeatureFilter(enabledFeatures);
            }
        };
    }

    public static EntryListCreationPolicySettable of(final DynamicRegistryManager registryManager, final FeatureSet enabledFeatures) {
        return new EntryListCreationPolicySettable(){
            EntryListCreationPolicy entryListCreationPolicy = EntryListCreationPolicy.FAIL;

            @Override
            public void setEntryListCreationPolicy(EntryListCreationPolicy entryListCreationPolicy) {
                this.entryListCreationPolicy = entryListCreationPolicy;
            }

            @Override
            public <T> RegistryWrapper<T> createWrapper(RegistryKey<? extends Registry<T>> registryRef) {
                Registry registry = registryManager.get(registryRef);
                final RegistryWrapper.Impl impl = registry.getReadOnlyWrapper();
                final RegistryWrapper.Impl impl2 = registry.getTagCreatingWrapper();
                RegistryWrapper.Impl.Delegating impl3 = new RegistryWrapper.Impl.Delegating<T>(){

                    @Override
                    protected RegistryWrapper.Impl<T> getBase() {
                        return switch (entryListCreationPolicy) {
                            default -> throw new IncompatibleClassChangeError();
                            case EntryListCreationPolicy.FAIL -> impl;
                            case EntryListCreationPolicy.CREATE_NEW -> impl2;
                        };
                    }
                };
                return impl3.withFeatureFilter(enabledFeatures);
            }
        };
    }

    public static interface EntryListCreationPolicySettable
    extends CommandRegistryAccess {
        public void setEntryListCreationPolicy(EntryListCreationPolicy var1);
    }

    public static final class EntryListCreationPolicy
    extends Enum<EntryListCreationPolicy> {
        public static final /* enum */ EntryListCreationPolicy CREATE_NEW = new EntryListCreationPolicy();
        public static final /* enum */ EntryListCreationPolicy FAIL = new EntryListCreationPolicy();
        private static final /* synthetic */ EntryListCreationPolicy[] field_37827;

        public static EntryListCreationPolicy[] values() {
            return (EntryListCreationPolicy[])field_37827.clone();
        }

        public static EntryListCreationPolicy valueOf(String string) {
            return Enum.valueOf(EntryListCreationPolicy.class, string);
        }

        private static /* synthetic */ EntryListCreationPolicy[] method_41701() {
            return new EntryListCreationPolicy[]{CREATE_NEW, FAIL};
        }

        static {
            field_37827 = EntryListCreationPolicy.method_41701();
        }
    }
}

