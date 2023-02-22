/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Streams
 */
package net.minecraft.text;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public abstract class BaseText
implements Text {
    protected final List<Text> siblings = Lists.newArrayList();
    private Style style;

    @Override
    public Text append(Text text) {
        text.getStyle().setParent(this.getStyle());
        this.siblings.add(text);
        return this;
    }

    @Override
    public List<Text> getSiblings() {
        return this.siblings;
    }

    @Override
    public Text setStyle(Style style) {
        this.style = style;
        for (Text text : this.siblings) {
            text.getStyle().setParent(this.getStyle());
        }
        return this;
    }

    @Override
    public Style getStyle() {
        if (this.style == null) {
            this.style = new Style();
            for (Text text : this.siblings) {
                text.getStyle().setParent(this.style);
            }
        }
        return this.style;
    }

    @Override
    public Stream<Text> stream() {
        return Streams.concat((Stream[])new Stream[]{Stream.of(this), this.siblings.stream().flatMap(Text::stream)});
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof BaseText) {
            BaseText baseText = (BaseText)obj;
            return this.siblings.equals(baseText.siblings) && this.getStyle().equals(baseText.getStyle());
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.getStyle(), this.siblings);
    }

    public String toString() {
        return "BaseComponent{style=" + this.style + ", siblings=" + this.siblings + '}';
    }
}
