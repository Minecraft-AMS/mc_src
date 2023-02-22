/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectList
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.network.message;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.network.message.LastSeenMessageList;
import org.jetbrains.annotations.Nullable;

public class AcknowledgmentValidator {
    private static final int UNKNOWN = Integer.MIN_VALUE;
    private LastSeenMessageList prevValidated = LastSeenMessageList.EMPTY;
    private final ObjectList<LastSeenMessageList.Entry> pending = new ObjectArrayList();

    public void addPending(LastSeenMessageList.Entry entry) {
        this.pending.add((Object)entry);
    }

    public int getPendingCount() {
        return this.pending.size();
    }

    private boolean hasDuplicateProfiles(LastSeenMessageList messages) {
        HashSet<UUID> set = new HashSet<UUID>(messages.entries().size());
        for (LastSeenMessageList.Entry entry : messages.entries()) {
            if (set.add(entry.profileId())) continue;
            return true;
        }
        return false;
    }

    private int order(List<LastSeenMessageList.Entry> lastSeen, int[] result, @Nullable LastSeenMessageList.Entry lastReceived) {
        int k;
        int j;
        Arrays.fill(result, Integer.MIN_VALUE);
        List<LastSeenMessageList.Entry> list = this.prevValidated.entries();
        int i = list.size();
        for (j = i - 1; j >= 0; --j) {
            k = lastSeen.indexOf(list.get(j));
            if (k == -1) continue;
            result[k] = -j - 1;
        }
        j = Integer.MIN_VALUE;
        k = this.pending.size();
        for (int l = 0; l < k; ++l) {
            LastSeenMessageList.Entry entry = (LastSeenMessageList.Entry)this.pending.get(l);
            int m = lastSeen.indexOf(entry);
            if (m != -1) {
                result[m] = l;
            }
            if (!entry.equals(lastReceived)) continue;
            j = l;
        }
        return j;
    }

    public Set<FailureReason> validate(LastSeenMessageList.Acknowledgment acknowledgment) {
        EnumSet<FailureReason> enumSet = EnumSet.noneOf(FailureReason.class);
        LastSeenMessageList lastSeenMessageList = acknowledgment.lastSeen();
        LastSeenMessageList.Entry entry = acknowledgment.lastReceived().orElse(null);
        List<LastSeenMessageList.Entry> list = lastSeenMessageList.entries();
        int i = this.prevValidated.entries().size();
        int j = Integer.MIN_VALUE;
        int k = list.size();
        if (k < i) {
            enumSet.add(FailureReason.REMOVED_MESSAGES);
        }
        int[] is = new int[k];
        int l = this.order(list, is, entry);
        for (int m = k - 1; m >= 0; --m) {
            int n = is[m];
            if (n != Integer.MIN_VALUE) {
                if (n < j) {
                    enumSet.add(FailureReason.OUT_OF_ORDER);
                    continue;
                }
                j = n;
                continue;
            }
            enumSet.add(FailureReason.UNKNOWN_MESSAGES);
        }
        if (entry != null) {
            if (l == Integer.MIN_VALUE || l < j) {
                enumSet.add(FailureReason.UNKNOWN_MESSAGES);
            } else {
                j = l;
            }
        }
        if (j >= 0) {
            this.pending.removeElements(0, j + 1);
        }
        if (this.hasDuplicateProfiles(lastSeenMessageList)) {
            enumSet.add(FailureReason.DUPLICATED_PROFILES);
        }
        this.prevValidated = lastSeenMessageList;
        return enumSet;
    }

    public static final class FailureReason
    extends Enum<FailureReason> {
        public static final /* enum */ FailureReason OUT_OF_ORDER = new FailureReason("messages received out of order");
        public static final /* enum */ FailureReason DUPLICATED_PROFILES = new FailureReason("multiple entries for single profile");
        public static final /* enum */ FailureReason UNKNOWN_MESSAGES = new FailureReason("unknown message");
        public static final /* enum */ FailureReason REMOVED_MESSAGES = new FailureReason("previously present messages removed from context");
        private final String description;
        private static final /* synthetic */ FailureReason[] field_39896;

        public static FailureReason[] values() {
            return (FailureReason[])field_39896.clone();
        }

        public static FailureReason valueOf(String string) {
            return Enum.valueOf(FailureReason.class, string);
        }

        private FailureReason(String description) {
            this.description = description;
        }

        public String getDescription() {
            return this.description;
        }

        private static /* synthetic */ FailureReason[] method_44993() {
            return new FailureReason[]{OUT_OF_ORDER, DUPLICATED_PROFILES, UNKNOWN_MESSAGES, REMOVED_MESSAGES};
        }

        static {
            field_39896 = FailureReason.method_44993();
        }
    }
}

