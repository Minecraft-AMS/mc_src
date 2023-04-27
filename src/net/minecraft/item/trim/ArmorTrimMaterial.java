/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.item.trim;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryFixedCodec;
import net.minecraft.text.Text;
import net.minecraft.util.dynamic.Codecs;

public record ArmorTrimMaterial(String assetName, RegistryEntry<Item> ingredient, float itemModelIndex, Map<ArmorMaterials, String> overrideArmorMaterials, Text description) {
    public static final Codec<ArmorTrimMaterial> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.STRING.fieldOf("asset_name").forGetter(ArmorTrimMaterial::assetName), (App)RegistryFixedCodec.of(RegistryKeys.ITEM).fieldOf("ingredient").forGetter(ArmorTrimMaterial::ingredient), (App)Codec.FLOAT.fieldOf("item_model_index").forGetter(ArmorTrimMaterial::itemModelIndex), (App)Codec.unboundedMap(ArmorMaterials.CODEC, (Codec)Codec.STRING).optionalFieldOf("override_armor_materials", Map.of()).forGetter(ArmorTrimMaterial::overrideArmorMaterials), (App)Codecs.TEXT.fieldOf("description").forGetter(ArmorTrimMaterial::description)).apply((Applicative)instance, ArmorTrimMaterial::new));
    public static final Codec<RegistryEntry<ArmorTrimMaterial>> ENTRY_CODEC = RegistryElementCodec.of(RegistryKeys.TRIM_MATERIAL, CODEC);

    public static ArmorTrimMaterial of(String assetName, Item ingredient, float itemModelIndex, Text description, Map<ArmorMaterials, String> overrideArmorMaterials) {
        return new ArmorTrimMaterial(assetName, Registries.ITEM.getEntry(ingredient), itemModelIndex, overrideArmorMaterials, description);
    }
}

