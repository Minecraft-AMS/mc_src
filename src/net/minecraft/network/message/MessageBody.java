/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.hash.HashCode
 *  com.google.common.hash.Hashing
 *  com.google.common.hash.HashingOutputStream
 */
package net.minecraft.network.message;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.message.DecoratedContents;
import net.minecraft.network.message.LastSeenMessageList;
import net.minecraft.text.Text;

public record MessageBody(DecoratedContents content, Instant timestamp, long salt, LastSeenMessageList lastSeenMessages) {
    public static final byte LAST_SEEN_SEPARATOR = 70;

    public MessageBody(PacketByteBuf buf) {
        this(DecoratedContents.read(buf), buf.readInstant(), buf.readLong(), new LastSeenMessageList(buf));
    }

    public void write(PacketByteBuf buf) {
        DecoratedContents.write(buf, this.content);
        buf.writeInstant(this.timestamp);
        buf.writeLong(this.salt);
        this.lastSeenMessages.write(buf);
    }

    public HashCode digest() {
        HashingOutputStream hashingOutputStream = new HashingOutputStream(Hashing.sha256(), OutputStream.nullOutputStream());
        try {
            DataOutputStream dataOutputStream = new DataOutputStream((OutputStream)hashingOutputStream);
            dataOutputStream.writeLong(this.salt);
            dataOutputStream.writeLong(this.timestamp.getEpochSecond());
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter((OutputStream)dataOutputStream, StandardCharsets.UTF_8);
            outputStreamWriter.write(this.content.plain());
            outputStreamWriter.flush();
            dataOutputStream.write(70);
            if (this.content.isDecorated()) {
                outputStreamWriter.write(Text.Serializer.toSortedJsonString(this.content.decorated()));
                outputStreamWriter.flush();
            }
            this.lastSeenMessages.write(dataOutputStream);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return hashingOutputStream.hash();
    }

    public MessageBody withContent(DecoratedContents content) {
        return new MessageBody(content, this.timestamp, this.salt, this.lastSeenMessages);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{MessageBody.class, "content;timeStamp;salt;lastSeen", "content", "timestamp", "salt", "lastSeenMessages"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{MessageBody.class, "content;timeStamp;salt;lastSeen", "content", "timestamp", "salt", "lastSeenMessages"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{MessageBody.class, "content;timeStamp;salt;lastSeen", "content", "timestamp", "salt", "lastSeenMessages"}, this, object);
    }
}

