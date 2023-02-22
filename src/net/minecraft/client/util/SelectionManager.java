/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.util;

import java.util.function.Consumer;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SelectionManager {
    private final MinecraftClient client;
    private final TextRenderer fontRenderer;
    private final Supplier<String> stringGetter;
    private final Consumer<String> stringSetter;
    private final int maxLength;
    private int selectionStart;
    private int selectionEnd;

    public SelectionManager(MinecraftClient client, Supplier<String> getter, Consumer<String> setter, int maxLength) {
        this.client = client;
        this.fontRenderer = client.textRenderer;
        this.stringGetter = getter;
        this.stringSetter = setter;
        this.maxLength = maxLength;
        this.moveCaretToEnd();
    }

    public boolean insert(char c) {
        if (SharedConstants.isValidChar(c)) {
            this.insert(Character.toString(c));
        }
        return true;
    }

    private void insert(String string) {
        if (this.selectionEnd != this.selectionStart) {
            this.deleteSelectedText();
        }
        String string2 = this.stringGetter.get();
        this.selectionStart = MathHelper.clamp(this.selectionStart, 0, string2.length());
        String string3 = new StringBuilder(string2).insert(this.selectionStart, string).toString();
        if (this.fontRenderer.getStringWidth(string3) <= this.maxLength) {
            this.stringSetter.accept(string3);
            this.selectionEnd = this.selectionStart = Math.min(string3.length(), this.selectionStart + string.length());
        }
    }

    public boolean handleSpecialKey(int keyCode) {
        String string = this.stringGetter.get();
        if (Screen.isSelectAll(keyCode)) {
            this.selectionEnd = 0;
            this.selectionStart = string.length();
            return true;
        }
        if (Screen.isCopy(keyCode)) {
            this.client.keyboard.setClipboard(this.getSelectedText());
            return true;
        }
        if (Screen.isPaste(keyCode)) {
            this.insert(SharedConstants.stripInvalidChars(Formatting.strip(this.client.keyboard.getClipboard().replaceAll("\\r", ""))));
            this.selectionEnd = this.selectionStart;
            return true;
        }
        if (Screen.isCut(keyCode)) {
            this.client.keyboard.setClipboard(this.getSelectedText());
            this.deleteSelectedText();
            return true;
        }
        if (keyCode == 259) {
            if (!string.isEmpty()) {
                if (this.selectionEnd != this.selectionStart) {
                    this.deleteSelectedText();
                } else if (this.selectionStart > 0) {
                    string = new StringBuilder(string).deleteCharAt(Math.max(0, this.selectionStart - 1)).toString();
                    this.selectionEnd = this.selectionStart = Math.max(0, this.selectionStart - 1);
                    this.stringSetter.accept(string);
                }
            }
            return true;
        }
        if (keyCode == 261) {
            if (!string.isEmpty()) {
                if (this.selectionEnd != this.selectionStart) {
                    this.deleteSelectedText();
                } else if (this.selectionStart < string.length()) {
                    string = new StringBuilder(string).deleteCharAt(Math.max(0, this.selectionStart)).toString();
                    this.stringSetter.accept(string);
                }
            }
            return true;
        }
        if (keyCode == 263) {
            int i = this.fontRenderer.isRightToLeft() ? 1 : -1;
            this.selectionStart = Screen.hasControlDown() ? this.fontRenderer.findWordEdge(string, i, this.selectionStart, true) : Math.max(0, Math.min(string.length(), this.selectionStart + i));
            if (!Screen.hasShiftDown()) {
                this.selectionEnd = this.selectionStart;
            }
            return true;
        }
        if (keyCode == 262) {
            int i = this.fontRenderer.isRightToLeft() ? -1 : 1;
            this.selectionStart = Screen.hasControlDown() ? this.fontRenderer.findWordEdge(string, i, this.selectionStart, true) : Math.max(0, Math.min(string.length(), this.selectionStart + i));
            if (!Screen.hasShiftDown()) {
                this.selectionEnd = this.selectionStart;
            }
            return true;
        }
        if (keyCode == 268) {
            this.selectionStart = 0;
            if (!Screen.hasShiftDown()) {
                this.selectionEnd = this.selectionStart;
            }
            return true;
        }
        if (keyCode == 269) {
            this.selectionStart = this.stringGetter.get().length();
            if (!Screen.hasShiftDown()) {
                this.selectionEnd = this.selectionStart;
            }
            return true;
        }
        return false;
    }

    private String getSelectedText() {
        String string = this.stringGetter.get();
        int i = Math.min(this.selectionStart, this.selectionEnd);
        int j = Math.max(this.selectionStart, this.selectionEnd);
        return string.substring(i, j);
    }

    private void deleteSelectedText() {
        if (this.selectionEnd == this.selectionStart) {
            return;
        }
        String string = this.stringGetter.get();
        int i = Math.min(this.selectionStart, this.selectionEnd);
        int j = Math.max(this.selectionStart, this.selectionEnd);
        String string2 = string.substring(0, i) + string.substring(j);
        this.selectionEnd = this.selectionStart = i;
        this.stringSetter.accept(string2);
    }

    public void moveCaretToEnd() {
        this.selectionEnd = this.selectionStart = this.stringGetter.get().length();
    }

    public int getSelectionStart() {
        return this.selectionStart;
    }

    public int getSelectionEnd() {
        return this.selectionEnd;
    }
}

