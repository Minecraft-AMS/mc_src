/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.datafixer.fix;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.lang.reflect.Type;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.ChoiceFix;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.StringUtils;

public class BlockEntitySignTextStrictJsonFix
extends ChoiceFix {
    public static final Gson GSON = new GsonBuilder().registerTypeAdapter(Text.class, (Object)new JsonDeserializer<Text>(){

        public Text deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (jsonElement.isJsonPrimitive()) {
                return new LiteralText(jsonElement.getAsString());
            }
            if (jsonElement.isJsonArray()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                Text text = null;
                for (JsonElement jsonElement2 : jsonArray) {
                    Text text2 = this.deserialize(jsonElement2, jsonElement2.getClass(), jsonDeserializationContext);
                    if (text == null) {
                        text = text2;
                        continue;
                    }
                    text.append(text2);
                }
                return text;
            }
            throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
        }

        public /* synthetic */ Object deserialize(JsonElement functionJson, Type unused, JsonDeserializationContext context) throws JsonParseException {
            return this.deserialize(functionJson, unused, context);
        }
    }).create();

    public BlockEntitySignTextStrictJsonFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType, "BlockEntitySignTextStrictJsonFix", TypeReferences.BLOCK_ENTITY, "Sign");
    }

    private Dynamic<?> fix(Dynamic<?> tag, String lineName) {
        String string = tag.get(lineName).asString("");
        Text text = null;
        if ("null".equals(string) || StringUtils.isEmpty((CharSequence)string)) {
            text = new LiteralText("");
        } else if (string.charAt(0) == '\"' && string.charAt(string.length() - 1) == '\"' || string.charAt(0) == '{' && string.charAt(string.length() - 1) == '}') {
            try {
                text = JsonHelper.deserialize(GSON, string, Text.class, true);
                if (text == null) {
                    text = new LiteralText("");
                }
            }
            catch (JsonParseException jsonParseException) {
                // empty catch block
            }
            if (text == null) {
                try {
                    text = Text.Serializer.fromJson(string);
                }
                catch (JsonParseException jsonParseException) {
                    // empty catch block
                }
            }
            if (text == null) {
                try {
                    text = Text.Serializer.fromLenientJson(string);
                }
                catch (JsonParseException jsonParseException) {
                    // empty catch block
                }
            }
            if (text == null) {
                text = new LiteralText(string);
            }
        } else {
            text = new LiteralText(string);
        }
        return tag.set(lineName, tag.createString(Text.Serializer.toJson(text)));
    }

    @Override
    protected Typed<?> transform(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), dynamic -> {
            dynamic = this.fix((Dynamic<?>)dynamic, "Text1");
            dynamic = this.fix((Dynamic<?>)dynamic, "Text2");
            dynamic = this.fix((Dynamic<?>)dynamic, "Text3");
            dynamic = this.fix((Dynamic<?>)dynamic, "Text4");
            return dynamic;
        });
    }
}

