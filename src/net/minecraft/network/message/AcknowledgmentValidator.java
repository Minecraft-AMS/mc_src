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
import java.util.List;
import java.util.Optional;
import net.minecraft.network.message.AcknowledgedMessage;
import net.minecraft.network.message.LastSeenMessageList;
import net.minecraft.network.message.MessageSignatureData;
import org.jetbrains.annotations.Nullable;

public class AcknowledgmentValidator {
    private final int size;
    private final ObjectList<AcknowledgedMessage> messages = new ObjectArrayList();
    @Nullable
    private MessageSignatureData lastSignature;

    public AcknowledgmentValidator(int size) {
        this.size = size;
        for (int i = 0; i < size; ++i) {
            this.messages.add(null);
        }
    }

    public void addPending(MessageSignatureData signature) {
        if (!signature.equals(this.lastSignature)) {
            this.messages.add((Object)new AcknowledgedMessage(signature, true));
            this.lastSignature = signature;
        }
    }

    public int getMessageCount() {
        return this.messages.size();
    }

    public boolean removeUntil(int index) {
        int i = this.messages.size() - this.size;
        if (index >= 0 && index <= i) {
            this.messages.removeElements(0, index);
            return true;
        }
        return false;
    }

    public Optional<LastSeenMessageList> validate(LastSeenMessageList.Acknowledgment acknowledgment) {
        if (!this.removeUntil(acknowledgment.offset())) {
            return Optional.empty();
        }
        ObjectArrayList objectList = new ObjectArrayList(acknowledgment.acknowledged().cardinality());
        if (acknowledgment.acknowledged().length() > this.size) {
            return Optional.empty();
        }
        for (int i = 0; i < this.size; ++i) {
            boolean bl = acknowledgment.acknowledged().get(i);
            AcknowledgedMessage acknowledgedMessage = (AcknowledgedMessage)this.messages.get(i);
            if (bl) {
                if (acknowledgedMessage == null) {
                    return Optional.empty();
                }
                this.messages.set(i, (Object)acknowledgedMessage.unmarkAsPending());
                objectList.add((Object)acknowledgedMessage.signature());
                continue;
            }
            if (acknowledgedMessage != null && !acknowledgedMessage.pending()) {
                return Optional.empty();
            }
            this.messages.set(i, null);
        }
        return Optional.of(new LastSeenMessageList((List<MessageSignatureData>)objectList));
    }
}

