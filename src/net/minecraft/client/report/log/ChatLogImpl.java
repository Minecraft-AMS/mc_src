/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.report.log;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.report.log.ChatLog;
import net.minecraft.client.report.log.ChatLogEntry;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ChatLogImpl
implements ChatLog {
    private final ChatLogEntry[] entries;
    private int maxIndex = -1;
    private int minIndex = -1;

    public ChatLogImpl(int maxEntries) {
        this.entries = new ChatLogEntry[maxEntries];
    }

    @Override
    public void add(ChatLogEntry entry) {
        int i = this.incrementIndex();
        this.entries[this.wrapIndex((int)i)] = entry;
    }

    private int incrementIndex() {
        int i;
        this.minIndex = (i = ++this.maxIndex) >= this.entries.length ? ++this.minIndex : 0;
        return i;
    }

    @Override
    @Nullable
    public ChatLogEntry get(int index) {
        return this.contains(index) ? this.entries[this.wrapIndex(index)] : null;
    }

    private int wrapIndex(int index) {
        return index % this.entries.length;
    }

    @Override
    public boolean contains(int index) {
        return index >= this.minIndex && index <= this.maxIndex;
    }

    @Override
    public int getOffsetIndex(int index, int offset) {
        int i = index + offset;
        return this.contains(i) ? i : -1;
    }

    @Override
    public int getMaxIndex() {
        return this.maxIndex;
    }

    @Override
    public int getMinIndex() {
        return this.minIndex;
    }
}

