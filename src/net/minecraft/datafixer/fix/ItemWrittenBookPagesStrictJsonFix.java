/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.serialization.Dynamic
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.BlockEntitySignTextStrictJsonFix;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.StringUtils;

public class ItemWrittenBookPagesStrictJsonFix
extends DataFix {
    public ItemWrittenBookPagesStrictJsonFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    public Dynamic<?> fixBookPages(Dynamic<?> dynamic) {
        return dynamic.update("pages", dynamic2 -> (Dynamic)DataFixUtils.orElse((Optional)dynamic2.asStreamOpt().map(stream -> stream.map(dynamic -> {
            if (!dynamic.asString().result().isPresent()) {
                return dynamic;
            }
            String string = dynamic.asString("");
            Text text = null;
            if ("null".equals(string) || StringUtils.isEmpty((CharSequence)string)) {
                text = ScreenTexts.EMPTY;
            } else if (string.charAt(0) == '\"' && string.charAt(string.length() - 1) == '\"' || string.charAt(0) == '{' && string.charAt(string.length() - 1) == '}') {
                try {
                    text = JsonHelper.deserializeNullable(BlockEntitySignTextStrictJsonFix.GSON, string, Text.class, true);
                    if (text == null) {
                        text = ScreenTexts.EMPTY;
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
                if (text == null) {
                    try {
                        text = Text.Serializer.fromJson(string);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
                if (text == null) {
                    try {
                        text = Text.Serializer.fromLenientJson(string);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
                if (text == null) {
                    text = Text.literal(string);
                }
            } else {
                text = Text.literal(string);
            }
            return dynamic.createString(Text.Serializer.toJson(text));
        })).map(arg_0 -> ((Dynamic)dynamic).createList(arg_0)).result(), (Object)dynamic.emptyList()));
    }

    public TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
        OpticFinder opticFinder = type.findField("tag");
        return this.fixTypeEverywhereTyped("ItemWrittenBookPagesStrictJsonFix", type, typed2 -> typed2.updateTyped(opticFinder, typed -> typed.update(DSL.remainderFinder(), this::fixBookPages)));
    }
}

