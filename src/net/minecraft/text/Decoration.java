/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.text;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import net.minecraft.network.message.MessageType;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Nullable;

public record Decoration(String translationKey, List<Parameter> parameters, Style style) {
    public static final Codec<Decoration> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.STRING.fieldOf("translation_key").forGetter(Decoration::translationKey), (App)Parameter.CODEC.listOf().fieldOf("parameters").forGetter(Decoration::parameters), (App)Style.CODEC.optionalFieldOf("style", (Object)Style.EMPTY).forGetter(Decoration::style)).apply((Applicative)instance, Decoration::new));

    public static Decoration ofChat(String translationKey) {
        return new Decoration(translationKey, List.of(Parameter.SENDER, Parameter.CONTENT), Style.EMPTY);
    }

    public static Decoration ofIncomingMessage(String translationKey) {
        Style style = Style.EMPTY.withColor(Formatting.GRAY).withItalic(true);
        return new Decoration(translationKey, List.of(Parameter.SENDER, Parameter.CONTENT), style);
    }

    public static Decoration ofOutgoingMessage(String translationKey) {
        Style style = Style.EMPTY.withColor(Formatting.GRAY).withItalic(true);
        return new Decoration(translationKey, List.of(Parameter.TARGET, Parameter.CONTENT), style);
    }

    public static Decoration ofTeamMessage(String translationKey) {
        return new Decoration(translationKey, List.of(Parameter.TARGET, Parameter.SENDER, Parameter.CONTENT), Style.EMPTY);
    }

    public Text apply(Text content, MessageType.Parameters params) {
        Object[] objects = this.collectArguments(content, params);
        return Text.translatable(this.translationKey, objects).fillStyle(this.style);
    }

    private Text[] collectArguments(Text content, MessageType.Parameters params) {
        Text[] texts = new Text[this.parameters.size()];
        for (int i = 0; i < texts.length; ++i) {
            Parameter parameter = this.parameters.get(i);
            texts[i] = parameter.apply(content, params);
        }
        return texts;
    }

    public static final class Parameter
    extends Enum<Parameter>
    implements StringIdentifiable {
        public static final /* enum */ Parameter SENDER = new Parameter("sender", (content, params) -> params.name());
        public static final /* enum */ Parameter TARGET = new Parameter("target", (content, params) -> params.targetName());
        public static final /* enum */ Parameter CONTENT = new Parameter("content", (content, params) -> content);
        public static final Codec<Parameter> CODEC;
        private final String name;
        private final Selector selector;
        private static final /* synthetic */ Parameter[] field_39226;

        public static Parameter[] values() {
            return (Parameter[])field_39226.clone();
        }

        public static Parameter valueOf(String string) {
            return Enum.valueOf(Parameter.class, string);
        }

        private Parameter(String name, Selector selector) {
            this.name = name;
            this.selector = selector;
        }

        public Text apply(Text content, MessageType.Parameters params) {
            Text text = this.selector.select(content, params);
            return Objects.requireNonNullElse(text, ScreenTexts.EMPTY);
        }

        @Override
        public String asString() {
            return this.name;
        }

        private static /* synthetic */ Parameter[] method_43836() {
            return new Parameter[]{SENDER, TARGET, CONTENT};
        }

        static {
            field_39226 = Parameter.method_43836();
            CODEC = StringIdentifiable.createCodec(Parameter::values);
        }

        public static interface Selector {
            @Nullable
            public Text select(Text var1, MessageType.Parameters var2);
        }
    }
}

