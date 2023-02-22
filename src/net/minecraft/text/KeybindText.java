/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.text;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.text.BaseText;
import net.minecraft.text.Text;

public class KeybindText
extends BaseText {
    public static Function<String, Supplier<String>> i18n = key -> () -> key;
    private final String key;
    private Supplier<String> name;

    public KeybindText(String key) {
        this.key = key;
    }

    @Override
    public String asString() {
        if (this.name == null) {
            this.name = i18n.apply(this.key);
        }
        return this.name.get();
    }

    @Override
    public KeybindText copy() {
        return new KeybindText(this.key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof KeybindText) {
            KeybindText keybindText = (KeybindText)o;
            return this.key.equals(keybindText.key) && super.equals(o);
        }
        return false;
    }

    @Override
    public String toString() {
        return "KeybindComponent{keybind='" + this.key + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
    }

    public String getKey() {
        return this.key;
    }

    @Override
    public /* synthetic */ Text copy() {
        return this.copy();
    }
}

