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
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  com.google.gson.TypeAdapterFactory
 *  com.google.gson.stream.JsonReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.text;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.Message;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.text.KeybindText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.NbtText;
import net.minecraft.text.ScoreText;
import net.minecraft.text.SelectorText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.LowercaseEnumTypeAdapterFactory;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

public interface Text
extends Message,
Iterable<Text> {
    public Text setStyle(Style var1);

    public Style getStyle();

    default public Text append(String text) {
        return this.append(new LiteralText(text));
    }

    public Text append(Text var1);

    public String asString();

    default public String getString() {
        StringBuilder stringBuilder = new StringBuilder();
        this.stream().forEach(text -> stringBuilder.append(text.asString()));
        return stringBuilder.toString();
    }

    default public String asTruncatedString(int length) {
        int i;
        StringBuilder stringBuilder = new StringBuilder();
        Iterator iterator = this.stream().iterator();
        while (iterator.hasNext() && (i = length - stringBuilder.length()) > 0) {
            String string = ((Text)iterator.next()).asString();
            stringBuilder.append(string.length() <= i ? string : string.substring(0, i));
        }
        return stringBuilder.toString();
    }

    default public String asFormattedString() {
        StringBuilder stringBuilder = new StringBuilder();
        String string = "";
        Iterator iterator = this.stream().iterator();
        while (iterator.hasNext()) {
            Text text = (Text)iterator.next();
            String string2 = text.asString();
            if (string2.isEmpty()) continue;
            String string3 = text.getStyle().asString();
            if (!string3.equals(string)) {
                if (!string.isEmpty()) {
                    stringBuilder.append((Object)Formatting.RESET);
                }
                stringBuilder.append(string3);
                string = string3;
            }
            stringBuilder.append(string2);
        }
        if (!string.isEmpty()) {
            stringBuilder.append((Object)Formatting.RESET);
        }
        return stringBuilder.toString();
    }

    public List<Text> getSiblings();

    public Stream<Text> stream();

    default public Stream<Text> streamCopied() {
        return this.stream().map(Text::copyWithoutChildren);
    }

    @Override
    default public Iterator<Text> iterator() {
        return this.streamCopied().iterator();
    }

    public Text copy();

    default public Text deepCopy() {
        Text text = this.copy();
        text.setStyle(this.getStyle().deepCopy());
        for (Text text2 : this.getSiblings()) {
            text.append(text2.deepCopy());
        }
        return text;
    }

    default public Text styled(Consumer<Style> transformer) {
        transformer.accept(this.getStyle());
        return this;
    }

    default public Text formatted(Formatting ... formatting) {
        for (Formatting formatting2 : formatting) {
            this.formatted(formatting2);
        }
        return this;
    }

    default public Text formatted(Formatting formatting) {
        Style style = this.getStyle();
        if (formatting.isColor()) {
            style.setColor(formatting);
        }
        if (formatting.isModifier()) {
            switch (formatting) {
                case OBFUSCATED: {
                    style.setObfuscated(true);
                    break;
                }
                case BOLD: {
                    style.setBold(true);
                    break;
                }
                case STRIKETHROUGH: {
                    style.setStrikethrough(true);
                    break;
                }
                case UNDERLINE: {
                    style.setUnderline(true);
                    break;
                }
                case ITALIC: {
                    style.setItalic(true);
                    break;
                }
            }
        }
        return this;
    }

    public static Text copyWithoutChildren(Text text) {
        Text text2 = text.copy();
        text2.setStyle(text.getStyle().copy());
        return text2;
    }

    public static class Serializer
    implements JsonDeserializer<Text>,
    JsonSerializer<Text> {
        private static final Gson GSON = Util.make(() -> {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.disableHtmlEscaping();
            gsonBuilder.registerTypeHierarchyAdapter(Text.class, (Object)new Serializer());
            gsonBuilder.registerTypeHierarchyAdapter(Style.class, (Object)new Style.Serializer());
            gsonBuilder.registerTypeAdapterFactory((TypeAdapterFactory)new LowercaseEnumTypeAdapterFactory());
            return gsonBuilder.create();
        });
        private static final Field JSON_READER_POS = Util.make(() -> {
            try {
                new JsonReader((Reader)new StringReader(""));
                Field field = JsonReader.class.getDeclaredField("pos");
                field.setAccessible(true);
                return field;
            }
            catch (NoSuchFieldException noSuchFieldException) {
                throw new IllegalStateException("Couldn't get field 'pos' for JsonReader", noSuchFieldException);
            }
        });
        private static final Field JSON_READER_LINE_START = Util.make(() -> {
            try {
                new JsonReader((Reader)new StringReader(""));
                Field field = JsonReader.class.getDeclaredField("lineStart");
                field.setAccessible(true);
                return field;
            }
            catch (NoSuchFieldException noSuchFieldException) {
                throw new IllegalStateException("Couldn't get field 'lineStart' for JsonReader", noSuchFieldException);
            }
        });

        /*
         * WARNING - void declaration
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        public Text deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            void var5_18;
            if (jsonElement.isJsonPrimitive()) {
                return new LiteralText(jsonElement.getAsString());
            }
            if (jsonElement.isJsonObject()) {
                void var5_16;
                String string;
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                if (jsonObject.has("text")) {
                    LiteralText literalText = new LiteralText(JsonHelper.getString(jsonObject, "text"));
                } else if (jsonObject.has("translate")) {
                    string = JsonHelper.getString(jsonObject, "translate");
                    if (jsonObject.has("with")) {
                        JsonArray jsonArray = JsonHelper.getArray(jsonObject, "with");
                        Object[] objects = new Object[jsonArray.size()];
                        for (int i = 0; i < objects.length; ++i) {
                            LiteralText literalText;
                            objects[i] = this.deserialize(jsonArray.get(i), type, jsonDeserializationContext);
                            if (!(objects[i] instanceof LiteralText) || !(literalText = (LiteralText)objects[i]).getStyle().isEmpty() || !literalText.getSiblings().isEmpty()) continue;
                            objects[i] = literalText.getRawString();
                        }
                        TranslatableText translatableText = new TranslatableText(string, objects);
                    } else {
                        TranslatableText translatableText = new TranslatableText(string, new Object[0]);
                    }
                } else if (jsonObject.has("score")) {
                    JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "score");
                    if (!jsonObject2.has("name") || !jsonObject2.has("objective")) throw new JsonParseException("A score component needs a least a name and an objective");
                    ScoreText scoreText = new ScoreText(JsonHelper.getString(jsonObject2, "name"), JsonHelper.getString(jsonObject2, "objective"));
                    if (jsonObject2.has("value")) {
                        scoreText.setScore(JsonHelper.getString(jsonObject2, "value"));
                    }
                } else if (jsonObject.has("selector")) {
                    SelectorText selectorText = new SelectorText(JsonHelper.getString(jsonObject, "selector"));
                } else if (jsonObject.has("keybind")) {
                    KeybindText keybindText = new KeybindText(JsonHelper.getString(jsonObject, "keybind"));
                } else {
                    if (!jsonObject.has("nbt")) throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
                    string = JsonHelper.getString(jsonObject, "nbt");
                    boolean bl = JsonHelper.getBoolean(jsonObject, "interpret", false);
                    if (jsonObject.has("block")) {
                        NbtText.BlockNbtText blockNbtText = new NbtText.BlockNbtText(string, bl, JsonHelper.getString(jsonObject, "block"));
                    } else {
                        if (!jsonObject.has("entity")) throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
                        NbtText.EntityNbtText entityNbtText = new NbtText.EntityNbtText(string, bl, JsonHelper.getString(jsonObject, "entity"));
                    }
                }
                if (jsonObject.has("extra")) {
                    JsonArray jsonArray2 = JsonHelper.getArray(jsonObject, "extra");
                    if (jsonArray2.size() <= 0) throw new JsonParseException("Unexpected empty array of components");
                    for (int j = 0; j < jsonArray2.size(); ++j) {
                        var5_16.append(this.deserialize(jsonArray2.get(j), type, jsonDeserializationContext));
                    }
                }
                var5_16.setStyle((Style)jsonDeserializationContext.deserialize(jsonElement, Style.class));
                return var5_16;
            }
            if (!jsonElement.isJsonArray()) throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
            JsonArray jsonArray3 = jsonElement.getAsJsonArray();
            Object var5_17 = null;
            for (JsonElement jsonElement2 : jsonArray3) {
                Text text2 = this.deserialize(jsonElement2, jsonElement2.getClass(), jsonDeserializationContext);
                if (var5_18 == null) {
                    Text text = text2;
                    continue;
                }
                var5_18.append(text2);
            }
            return var5_18;
        }

        private void addStyle(Style style, JsonObject json, JsonSerializationContext context) {
            JsonElement jsonElement = context.serialize((Object)style);
            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = (JsonObject)jsonElement;
                for (Map.Entry entry : jsonObject.entrySet()) {
                    json.add((String)entry.getKey(), (JsonElement)entry.getValue());
                }
            }
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        public JsonElement serialize(Text text, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            if (!text.getStyle().isEmpty()) {
                this.addStyle(text.getStyle(), jsonObject, jsonSerializationContext);
            }
            if (!text.getSiblings().isEmpty()) {
                JsonArray jsonArray = new JsonArray();
                for (Text text2 : text.getSiblings()) {
                    jsonArray.add(this.serialize(text2, text2.getClass(), jsonSerializationContext));
                }
                jsonObject.add("extra", (JsonElement)jsonArray);
            }
            if (text instanceof LiteralText) {
                jsonObject.addProperty("text", ((LiteralText)text).getRawString());
                return jsonObject;
            } else if (text instanceof TranslatableText) {
                TranslatableText translatableText = (TranslatableText)text;
                jsonObject.addProperty("translate", translatableText.getKey());
                if (translatableText.getArgs() == null || translatableText.getArgs().length <= 0) return jsonObject;
                JsonArray jsonArray2 = new JsonArray();
                for (Object object : translatableText.getArgs()) {
                    if (object instanceof Text) {
                        jsonArray2.add(this.serialize((Text)object, object.getClass(), jsonSerializationContext));
                        continue;
                    }
                    jsonArray2.add((JsonElement)new JsonPrimitive(String.valueOf(object)));
                }
                jsonObject.add("with", (JsonElement)jsonArray2);
                return jsonObject;
            } else if (text instanceof ScoreText) {
                ScoreText scoreText = (ScoreText)text;
                JsonObject jsonObject2 = new JsonObject();
                jsonObject2.addProperty("name", scoreText.getName());
                jsonObject2.addProperty("objective", scoreText.getObjective());
                jsonObject2.addProperty("value", scoreText.asString());
                jsonObject.add("score", (JsonElement)jsonObject2);
                return jsonObject;
            } else if (text instanceof SelectorText) {
                SelectorText selectorText = (SelectorText)text;
                jsonObject.addProperty("selector", selectorText.getPattern());
                return jsonObject;
            } else if (text instanceof KeybindText) {
                KeybindText keybindText = (KeybindText)text;
                jsonObject.addProperty("keybind", keybindText.getKey());
                return jsonObject;
            } else {
                if (!(text instanceof NbtText)) throw new IllegalArgumentException("Don't know how to serialize " + text + " as a Component");
                NbtText nbtText = (NbtText)text;
                jsonObject.addProperty("nbt", nbtText.getPath());
                jsonObject.addProperty("interpret", Boolean.valueOf(nbtText.shouldInterpret()));
                if (text instanceof NbtText.BlockNbtText) {
                    NbtText.BlockNbtText blockNbtText = (NbtText.BlockNbtText)text;
                    jsonObject.addProperty("block", blockNbtText.getPos());
                    return jsonObject;
                } else {
                    if (!(text instanceof NbtText.EntityNbtText)) throw new IllegalArgumentException("Don't know how to serialize " + text + " as a Component");
                    NbtText.EntityNbtText entityNbtText = (NbtText.EntityNbtText)text;
                    jsonObject.addProperty("entity", entityNbtText.getSelector());
                }
            }
            return jsonObject;
        }

        public static String toJson(Text text) {
            return GSON.toJson((Object)text);
        }

        public static JsonElement toJsonTree(Text text) {
            return GSON.toJsonTree((Object)text);
        }

        @Nullable
        public static Text fromJson(String json) {
            return JsonHelper.deserialize(GSON, json, Text.class, false);
        }

        @Nullable
        public static Text fromJson(JsonElement json) {
            return (Text)GSON.fromJson(json, Text.class);
        }

        @Nullable
        public static Text fromLenientJson(String json) {
            return JsonHelper.deserialize(GSON, json, Text.class, true);
        }

        public static Text fromJson(com.mojang.brigadier.StringReader reader) {
            try {
                JsonReader jsonReader = new JsonReader((Reader)new StringReader(reader.getRemaining()));
                jsonReader.setLenient(false);
                Text text = (Text)GSON.getAdapter(Text.class).read(jsonReader);
                reader.setCursor(reader.getCursor() + Serializer.getPosition(jsonReader));
                return text;
            }
            catch (IOException iOException) {
                throw new JsonParseException((Throwable)iOException);
            }
        }

        private static int getPosition(JsonReader reader) {
            try {
                return JSON_READER_POS.getInt(reader) - JSON_READER_LINE_START.getInt(reader) + 1;
            }
            catch (IllegalAccessException illegalAccessException) {
                throw new IllegalStateException("Couldn't read position of JsonReader", illegalAccessException);
            }
        }

        public /* synthetic */ JsonElement serialize(Object text, Type type, JsonSerializationContext context) {
            return this.serialize((Text)text, type, context);
        }

        public /* synthetic */ Object deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            return this.deserialize(json, type, context);
        }
    }
}

