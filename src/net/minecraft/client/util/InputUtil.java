/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWCharModsCallbackI
 *  org.lwjgl.glfw.GLFWCursorPosCallbackI
 *  org.lwjgl.glfw.GLFWKeyCallbackI
 *  org.lwjgl.glfw.GLFWMouseButtonCallbackI
 *  org.lwjgl.glfw.GLFWScrollCallbackI
 */
package net.minecraft.client.util;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Map;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharModsCallbackI;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWScrollCallbackI;

@Environment(value=EnvType.CLIENT)
public class InputUtil {
    @Nullable
    private static final MethodHandle field_20333;
    private static final int field_20334;
    public static final KeyCode UNKNOWN_KEYCODE;

    public static KeyCode getKeyCode(int i, int j) {
        if (i == -1) {
            return Type.SCANCODE.createFromCode(j);
        }
        return Type.KEYSYM.createFromCode(i);
    }

    public static KeyCode fromName(String s) {
        if (KeyCode.NAMES.containsKey(s)) {
            return (KeyCode)KeyCode.NAMES.get(s);
        }
        for (Type type : Type.values()) {
            if (!s.startsWith(type.name)) continue;
            String string = s.substring(type.name.length() + 1);
            return type.createFromCode(Integer.parseInt(string));
        }
        throw new IllegalArgumentException("Unknown key name: " + s);
    }

    public static boolean isKeyPressed(long handle, int i) {
        return GLFW.glfwGetKey((long)handle, (int)i) == 1;
    }

    public static void setKeyboardCallbacks(long handle, GLFWKeyCallbackI gLFWKeyCallbackI, GLFWCharModsCallbackI gLFWCharModsCallbackI) {
        GLFW.glfwSetKeyCallback((long)handle, (GLFWKeyCallbackI)gLFWKeyCallbackI);
        GLFW.glfwSetCharModsCallback((long)handle, (GLFWCharModsCallbackI)gLFWCharModsCallbackI);
    }

    public static void setMouseCallbacks(long handle, GLFWCursorPosCallbackI gLFWCursorPosCallbackI, GLFWMouseButtonCallbackI gLFWMouseButtonCallbackI, GLFWScrollCallbackI gLFWScrollCallbackI) {
        GLFW.glfwSetCursorPosCallback((long)handle, (GLFWCursorPosCallbackI)gLFWCursorPosCallbackI);
        GLFW.glfwSetMouseButtonCallback((long)handle, (GLFWMouseButtonCallbackI)gLFWMouseButtonCallbackI);
        GLFW.glfwSetScrollCallback((long)handle, (GLFWScrollCallbackI)gLFWScrollCallbackI);
    }

    public static void setCursorParameters(long l, int i, double d, double e) {
        GLFW.glfwSetCursorPos((long)l, (double)d, (double)e);
        GLFW.glfwSetInputMode((long)l, (int)208897, (int)i);
    }

    public static boolean method_21735() {
        try {
            return field_20333 != null && field_20333.invokeExact();
        }
        catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public static void method_21736(long l, boolean bl) {
        if (InputUtil.method_21735()) {
            GLFW.glfwSetInputMode((long)l, (int)field_20334, (int)(bl ? 1 : 0));
        }
    }

    @Nullable
    public static String getKeycodeName(int i) {
        return GLFW.glfwGetKeyName((int)i, (int)-1);
    }

    @Nullable
    public static String getScancodeName(int i) {
        return GLFW.glfwGetKeyName((int)-1, (int)i);
    }

    static {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodType methodType = MethodType.methodType(Boolean.TYPE);
        MethodHandle methodHandle = null;
        int i = 0;
        try {
            methodHandle = lookup.findStatic(GLFW.class, "glfwRawMouseMotionSupported", methodType);
            MethodHandle methodHandle2 = lookup.findStaticGetter(GLFW.class, "GLFW_RAW_MOUSE_MOTION", Integer.TYPE);
            i = methodHandle2.invokeExact();
        }
        catch (NoSuchFieldException | NoSuchMethodException methodHandle2) {
        }
        catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
        field_20333 = methodHandle;
        field_20334 = i;
        UNKNOWN_KEYCODE = Type.KEYSYM.createFromCode(-1);
    }

    @Environment(value=EnvType.CLIENT)
    public static final class KeyCode {
        private final String name;
        private final Type type;
        private final int keyCode;
        private static final Map<String, KeyCode> NAMES = Maps.newHashMap();

        private KeyCode(String keyName, Type type, int i) {
            this.name = keyName;
            this.type = type;
            this.keyCode = i;
            NAMES.put(keyName, this);
        }

        public Type getCategory() {
            return this.type;
        }

        public int getKeyCode() {
            return this.keyCode;
        }

        public String getName() {
            return this.name;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            KeyCode keyCode = (KeyCode)o;
            return this.keyCode == keyCode.keyCode && this.type == keyCode.type;
        }

        public int hashCode() {
            return Objects.hash(new Object[]{this.type, this.keyCode});
        }

        public String toString() {
            return this.name;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Type {
        KEYSYM("key.keyboard"),
        SCANCODE("scancode"),
        MOUSE("key.mouse");

        private static final String[] mouseButtons;
        private final Int2ObjectMap<KeyCode> map = new Int2ObjectOpenHashMap();
        private final String name;

        private static void mapKey(Type type, String name, int keyCode) {
            KeyCode keyCode2 = new KeyCode(name, type, keyCode);
            type.map.put(keyCode, (Object)keyCode2);
        }

        private Type(String string2) {
            this.name = string2;
        }

        public KeyCode createFromCode(int i) {
            if (this.map.containsKey(i)) {
                return (KeyCode)this.map.get(i);
            }
            String string = this == MOUSE ? (i <= 2 ? "." + mouseButtons[i] : "." + (i + 1)) : "." + i;
            KeyCode keyCode = new KeyCode(this.name + string, this, i);
            this.map.put(i, (Object)keyCode);
            return keyCode;
        }

        public String getName() {
            return this.name;
        }

        static {
            Type.mapKey(KEYSYM, "key.keyboard.unknown", -1);
            Type.mapKey(MOUSE, "key.mouse.left", 0);
            Type.mapKey(MOUSE, "key.mouse.right", 1);
            Type.mapKey(MOUSE, "key.mouse.middle", 2);
            Type.mapKey(MOUSE, "key.mouse.4", 3);
            Type.mapKey(MOUSE, "key.mouse.5", 4);
            Type.mapKey(MOUSE, "key.mouse.6", 5);
            Type.mapKey(MOUSE, "key.mouse.7", 6);
            Type.mapKey(MOUSE, "key.mouse.8", 7);
            Type.mapKey(KEYSYM, "key.keyboard.0", 48);
            Type.mapKey(KEYSYM, "key.keyboard.1", 49);
            Type.mapKey(KEYSYM, "key.keyboard.2", 50);
            Type.mapKey(KEYSYM, "key.keyboard.3", 51);
            Type.mapKey(KEYSYM, "key.keyboard.4", 52);
            Type.mapKey(KEYSYM, "key.keyboard.5", 53);
            Type.mapKey(KEYSYM, "key.keyboard.6", 54);
            Type.mapKey(KEYSYM, "key.keyboard.7", 55);
            Type.mapKey(KEYSYM, "key.keyboard.8", 56);
            Type.mapKey(KEYSYM, "key.keyboard.9", 57);
            Type.mapKey(KEYSYM, "key.keyboard.a", 65);
            Type.mapKey(KEYSYM, "key.keyboard.b", 66);
            Type.mapKey(KEYSYM, "key.keyboard.c", 67);
            Type.mapKey(KEYSYM, "key.keyboard.d", 68);
            Type.mapKey(KEYSYM, "key.keyboard.e", 69);
            Type.mapKey(KEYSYM, "key.keyboard.f", 70);
            Type.mapKey(KEYSYM, "key.keyboard.g", 71);
            Type.mapKey(KEYSYM, "key.keyboard.h", 72);
            Type.mapKey(KEYSYM, "key.keyboard.i", 73);
            Type.mapKey(KEYSYM, "key.keyboard.j", 74);
            Type.mapKey(KEYSYM, "key.keyboard.k", 75);
            Type.mapKey(KEYSYM, "key.keyboard.l", 76);
            Type.mapKey(KEYSYM, "key.keyboard.m", 77);
            Type.mapKey(KEYSYM, "key.keyboard.n", 78);
            Type.mapKey(KEYSYM, "key.keyboard.o", 79);
            Type.mapKey(KEYSYM, "key.keyboard.p", 80);
            Type.mapKey(KEYSYM, "key.keyboard.q", 81);
            Type.mapKey(KEYSYM, "key.keyboard.r", 82);
            Type.mapKey(KEYSYM, "key.keyboard.s", 83);
            Type.mapKey(KEYSYM, "key.keyboard.t", 84);
            Type.mapKey(KEYSYM, "key.keyboard.u", 85);
            Type.mapKey(KEYSYM, "key.keyboard.v", 86);
            Type.mapKey(KEYSYM, "key.keyboard.w", 87);
            Type.mapKey(KEYSYM, "key.keyboard.x", 88);
            Type.mapKey(KEYSYM, "key.keyboard.y", 89);
            Type.mapKey(KEYSYM, "key.keyboard.z", 90);
            Type.mapKey(KEYSYM, "key.keyboard.f1", 290);
            Type.mapKey(KEYSYM, "key.keyboard.f2", 291);
            Type.mapKey(KEYSYM, "key.keyboard.f3", 292);
            Type.mapKey(KEYSYM, "key.keyboard.f4", 293);
            Type.mapKey(KEYSYM, "key.keyboard.f5", 294);
            Type.mapKey(KEYSYM, "key.keyboard.f6", 295);
            Type.mapKey(KEYSYM, "key.keyboard.f7", 296);
            Type.mapKey(KEYSYM, "key.keyboard.f8", 297);
            Type.mapKey(KEYSYM, "key.keyboard.f9", 298);
            Type.mapKey(KEYSYM, "key.keyboard.f10", 299);
            Type.mapKey(KEYSYM, "key.keyboard.f11", 300);
            Type.mapKey(KEYSYM, "key.keyboard.f12", 301);
            Type.mapKey(KEYSYM, "key.keyboard.f13", 302);
            Type.mapKey(KEYSYM, "key.keyboard.f14", 303);
            Type.mapKey(KEYSYM, "key.keyboard.f15", 304);
            Type.mapKey(KEYSYM, "key.keyboard.f16", 305);
            Type.mapKey(KEYSYM, "key.keyboard.f17", 306);
            Type.mapKey(KEYSYM, "key.keyboard.f18", 307);
            Type.mapKey(KEYSYM, "key.keyboard.f19", 308);
            Type.mapKey(KEYSYM, "key.keyboard.f20", 309);
            Type.mapKey(KEYSYM, "key.keyboard.f21", 310);
            Type.mapKey(KEYSYM, "key.keyboard.f22", 311);
            Type.mapKey(KEYSYM, "key.keyboard.f23", 312);
            Type.mapKey(KEYSYM, "key.keyboard.f24", 313);
            Type.mapKey(KEYSYM, "key.keyboard.f25", 314);
            Type.mapKey(KEYSYM, "key.keyboard.num.lock", 282);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.0", 320);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.1", 321);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.2", 322);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.3", 323);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.4", 324);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.5", 325);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.6", 326);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.7", 327);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.8", 328);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.9", 329);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.add", 334);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.decimal", 330);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.enter", 335);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.equal", 336);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.multiply", 332);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.divide", 331);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.subtract", 333);
            Type.mapKey(KEYSYM, "key.keyboard.down", 264);
            Type.mapKey(KEYSYM, "key.keyboard.left", 263);
            Type.mapKey(KEYSYM, "key.keyboard.right", 262);
            Type.mapKey(KEYSYM, "key.keyboard.up", 265);
            Type.mapKey(KEYSYM, "key.keyboard.apostrophe", 39);
            Type.mapKey(KEYSYM, "key.keyboard.backslash", 92);
            Type.mapKey(KEYSYM, "key.keyboard.comma", 44);
            Type.mapKey(KEYSYM, "key.keyboard.equal", 61);
            Type.mapKey(KEYSYM, "key.keyboard.grave.accent", 96);
            Type.mapKey(KEYSYM, "key.keyboard.left.bracket", 91);
            Type.mapKey(KEYSYM, "key.keyboard.minus", 45);
            Type.mapKey(KEYSYM, "key.keyboard.period", 46);
            Type.mapKey(KEYSYM, "key.keyboard.right.bracket", 93);
            Type.mapKey(KEYSYM, "key.keyboard.semicolon", 59);
            Type.mapKey(KEYSYM, "key.keyboard.slash", 47);
            Type.mapKey(KEYSYM, "key.keyboard.space", 32);
            Type.mapKey(KEYSYM, "key.keyboard.tab", 258);
            Type.mapKey(KEYSYM, "key.keyboard.left.alt", 342);
            Type.mapKey(KEYSYM, "key.keyboard.left.control", 341);
            Type.mapKey(KEYSYM, "key.keyboard.left.shift", 340);
            Type.mapKey(KEYSYM, "key.keyboard.left.win", 343);
            Type.mapKey(KEYSYM, "key.keyboard.right.alt", 346);
            Type.mapKey(KEYSYM, "key.keyboard.right.control", 345);
            Type.mapKey(KEYSYM, "key.keyboard.right.shift", 344);
            Type.mapKey(KEYSYM, "key.keyboard.right.win", 347);
            Type.mapKey(KEYSYM, "key.keyboard.enter", 257);
            Type.mapKey(KEYSYM, "key.keyboard.escape", 256);
            Type.mapKey(KEYSYM, "key.keyboard.backspace", 259);
            Type.mapKey(KEYSYM, "key.keyboard.delete", 261);
            Type.mapKey(KEYSYM, "key.keyboard.end", 269);
            Type.mapKey(KEYSYM, "key.keyboard.home", 268);
            Type.mapKey(KEYSYM, "key.keyboard.insert", 260);
            Type.mapKey(KEYSYM, "key.keyboard.page.down", 267);
            Type.mapKey(KEYSYM, "key.keyboard.page.up", 266);
            Type.mapKey(KEYSYM, "key.keyboard.caps.lock", 280);
            Type.mapKey(KEYSYM, "key.keyboard.pause", 284);
            Type.mapKey(KEYSYM, "key.keyboard.scroll.lock", 281);
            Type.mapKey(KEYSYM, "key.keyboard.menu", 348);
            Type.mapKey(KEYSYM, "key.keyboard.print.screen", 283);
            Type.mapKey(KEYSYM, "key.keyboard.world.1", 161);
            Type.mapKey(KEYSYM, "key.keyboard.world.2", 162);
            mouseButtons = new String[]{"left", "middle", "right"};
        }
    }
}

