/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.stream.Stream;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.BlockStateFlattening;

public class SavedDataVillageCropFix
extends DataFix {
    public SavedDataVillageCropFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    public TypeRewriteRule makeRule() {
        return this.writeFixAndRead("SavedDataVillageCropFix", this.getInputSchema().getType(TypeReferences.STRUCTURE_FEATURE), this.getOutputSchema().getType(TypeReferences.STRUCTURE_FEATURE), this::fix);
    }

    private <T> Dynamic<T> fix(Dynamic<T> dynamic) {
        return dynamic.update("Children", SavedDataVillageCropFix::fixVillageChildren);
    }

    private static <T> Dynamic<T> fixVillageChildren(Dynamic<T> dynamic) {
        return dynamic.asStreamOpt().map(SavedDataVillageCropFix::fixVillageChildren).map(arg_0 -> dynamic.createList(arg_0)).result().orElse(dynamic);
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

    private static <T> Dynamic<T> fixSmallPlotCropIds(Dynamic<T> dynamic) {
        dynamic = SavedDataVillageCropFix.fixCropId(dynamic, "CA");
        return SavedDataVillageCropFix.fixCropId(dynamic, "CB");
    }

    private static <T> Dynamic<T> fixLargePlotCropIds(Dynamic<T> dynamic) {
        dynamic = SavedDataVillageCropFix.fixCropId(dynamic, "CA");
        dynamic = SavedDataVillageCropFix.fixCropId(dynamic, "CB");
        dynamic = SavedDataVillageCropFix.fixCropId(dynamic, "CC");
        return SavedDataVillageCropFix.fixCropId(dynamic, "CD");
    }

    private static <T> Dynamic<T> fixCropId(Dynamic<T> dynamic, String cropId) {
        if (dynamic.get(cropId).asNumber().result().isPresent()) {
            return dynamic.set(cropId, BlockStateFlattening.lookupState(dynamic.get(cropId).asInt(0) << 4));
        }
        return dynamic;
    }
}

