/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.stream.Stream;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.BlockStateFlattening;

public class SavedDataVillageCropFix
extends DataFix {
    public SavedDataVillageCropFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    public TypeRewriteRule makeRule() {
        return this.writeFixAndRead("SavedDataVillageCropFix", this.getInputSchema().getType(TypeReferences.STRUCTURE_FEATURE), this.getOutputSchema().getType(TypeReferences.STRUCTURE_FEATURE), this::method_5152);
    }

    private <T> Dynamic<T> method_5152(Dynamic<T> tag) {
        return tag.update("Children", SavedDataVillageCropFix::method_5157);
    }

    private static <T> Dynamic<T> method_5157(Dynamic<T> tag) {
        return tag.asStreamOpt().map(SavedDataVillageCropFix::fixVillageChildren).map(arg_0 -> tag.createList(arg_0)).orElse(tag);
    }

    private static Stream<? extends Dynamic<?>> fixVillageChildren(Stream<? extends Dynamic<?>> villageChildren) {
        return villageChildren.map(dynamic -> {
            String string = dynamic.get("id").asString("");
            if ("ViF".equals(string)) {
                return SavedDataVillageCropFix.fixSmallPlotCropIds(dynamic);
            }
            if ("ViDF".equals(string)) {
                return SavedDataVillageCropFix.fixLargePlotCropIds(dynamic);
            }
            return dynamic;
        });
    }

    private static <T> Dynamic<T> fixSmallPlotCropIds(Dynamic<T> tag) {
        tag = SavedDataVillageCropFix.fixCropId(tag, "CA");
        return SavedDataVillageCropFix.fixCropId(tag, "CB");
    }

    private static <T> Dynamic<T> fixLargePlotCropIds(Dynamic<T> tag) {
        tag = SavedDataVillageCropFix.fixCropId(tag, "CA");
        tag = SavedDataVillageCropFix.fixCropId(tag, "CB");
        tag = SavedDataVillageCropFix.fixCropId(tag, "CC");
        return SavedDataVillageCropFix.fixCropId(tag, "CD");
    }

    private static <T> Dynamic<T> fixCropId(Dynamic<T> tag, String cropId) {
        if (tag.get(cropId).asNumber().isPresent()) {
            return tag.set(cropId, BlockStateFlattening.lookupState(tag.get(cropId).asInt(0) << 4));
        }
        return tag;
    }
}

