/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.message;

import java.util.Arrays;
import net.minecraft.network.message.LastSeenMessageList;

public class LastSeenMessagesCollector {
    private final LastSeenMessageList.Entry[] entries;
    private int size;
    private LastSeenMessageList lastSeenMessages = LastSeenMessageList.EMPTY;

    public LastSeenMessagesCollector(int size) {
        this.entries = new LastSeenMessageList.Entry[size];
    }

    public void add(LastSeenMessageList.Entry entry) {
        LastSeenMessageList.Entry entry2 = entry;
        for (int i = 0; i < this.size; ++i) {
            LastSeenMessageList.Entry entry3 = this.entries[i];
            this.entries[i] = entry2;
            entry2 = entry3;
            if (!entry3.profileId().equals(entry.profileId())) continue;
            entry2 = null;
            break;
        }
        if (entry2 != null && this.size < this.entries.length) {
            this.entries[this.size++] = entry2;
        }
        this.lastSeenMessages = new LastSeenMessageList(Arrays.asList(Arrays.copyOf(this.entries, this.size)));
    }

    public LastSeenMessageList getLastSeenMessages() {
        return this.lastSeenMessages;
    }
}

