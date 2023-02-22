/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.VillagerResourceMetadataReader;

@Environment(value=EnvType.CLIENT)
public class VillagerResourceMetadata {
    public static final VillagerResourceMetadataReader READER = new VillagerResourceMetadataReader();
    public static final String KEY = "villager";
    private final HatType hatType;

    public VillagerResourceMetadata(HatType hatType) {
        this.hatType = hatType;
    }

    public HatType getHatType() {
        return this.hatType;
    }

    @Environment(value=EnvType.CLIENT)
    public static final class HatType
    extends Enum<HatType> {
        public static final /* enum */ HatType NONE = new HatType("none");
        public static final /* enum */ HatType PARTIAL = new HatType("partial");
        public static final /* enum */ HatType FULL = new HatType("full");
        private static final Map<String, HatType> BY_NAME;
        private final String name;
        private static final /* synthetic */ HatType[] field_17165;

        public static HatType[] values() {
            return (HatType[])field_17165.clone();
        }

        public static HatType valueOf(String string) {
            return Enum.valueOf(HatType.class, string);
        }

        private HatType(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public static HatType from(String name) {
            return BY_NAME.getOrDefault(name, NONE);
        }

        private static /* synthetic */ HatType[] method_36924() {
            return new HatType[]{NONE, PARTIAL, FULL};
        }

        static {
            field_17165 = HatType.method_36924();
            BY_NAME = Arrays.stream(HatType.values()).collect(Collectors.toMap(HatType::getName, hatType -> hatType));
        }
    }
}

