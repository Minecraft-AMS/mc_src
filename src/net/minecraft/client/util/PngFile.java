/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.stb.STBIEOFCallback
 *  org.lwjgl.stb.STBIEOFCallbackI
 *  org.lwjgl.stb.STBIIOCallbacks
 *  org.lwjgl.stb.STBIReadCallback
 *  org.lwjgl.stb.STBIReadCallbackI
 *  org.lwjgl.stb.STBISkipCallback
 *  org.lwjgl.stb.STBISkipCallbackI
 *  org.lwjgl.stb.STBImage
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 */
package net.minecraft.client.util;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.stb.STBIEOFCallback;
import org.lwjgl.stb.STBIEOFCallbackI;
import org.lwjgl.stb.STBIIOCallbacks;
import org.lwjgl.stb.STBIReadCallback;
import org.lwjgl.stb.STBIReadCallbackI;
import org.lwjgl.stb.STBISkipCallback;
import org.lwjgl.stb.STBISkipCallbackI;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
public class PngFile {
    public final int width;
    public final int height;

    public PngFile(String name, InputStream in) throws IOException {
        try (MemoryStack memoryStack = MemoryStack.stackPush();
             Reader reader = PngFile.createReader(in);
             STBIReadCallback sTBIReadCallback = STBIReadCallback.create(reader::read);
             STBISkipCallback sTBISkipCallback = STBISkipCallback.create(reader::skip);
             STBIEOFCallback sTBIEOFCallback = STBIEOFCallback.create(reader::eof);){
            STBIIOCallbacks sTBIIOCallbacks = STBIIOCallbacks.mallocStack((MemoryStack)memoryStack);
            sTBIIOCallbacks.read((STBIReadCallbackI)sTBIReadCallback);
            sTBIIOCallbacks.skip((STBISkipCallbackI)sTBISkipCallback);
            sTBIIOCallbacks.eof((STBIEOFCallbackI)sTBIEOFCallback);
            IntBuffer intBuffer = memoryStack.mallocInt(1);
            IntBuffer intBuffer2 = memoryStack.mallocInt(1);
            IntBuffer intBuffer3 = memoryStack.mallocInt(1);
            if (!STBImage.stbi_info_from_callbacks((STBIIOCallbacks)sTBIIOCallbacks, (long)0L, (IntBuffer)intBuffer, (IntBuffer)intBuffer2, (IntBuffer)intBuffer3)) {
                throw new IOException("Could not read info from the PNG file " + name + " " + STBImage.stbi_failure_reason());
            }
            this.width = intBuffer.get(0);
            this.height = intBuffer2.get(0);
        }
    }

    private static Reader createReader(InputStream is) {
        if (is instanceof FileInputStream) {
            return new SeekableChannelReader(((FileInputStream)is).getChannel());
        }
        return new ChannelReader(Channels.newChannel(is));
    }

    @Environment(value=EnvType.CLIENT)
    static abstract class Reader
    implements AutoCloseable {
        protected boolean errored;

        Reader() {
        }

        int read(long user, long data, int size) {
            try {
                return this.read(data, size);
            }
            catch (IOException iOException) {
                this.errored = true;
                return 0;
            }
        }

        void skip(long user, int n) {
            try {
                this.skip(n);
            }
            catch (IOException iOException) {
                this.errored = true;
            }
        }

        int eof(long user) {
            return this.errored ? 1 : 0;
        }

        protected abstract int read(long var1, int var3) throws IOException;

        protected abstract void skip(int var1) throws IOException;

        @Override
        public abstract void close() throws IOException;
    }

    @Environment(value=EnvType.CLIENT)
    static class SeekableChannelReader
    extends Reader {
        private final SeekableByteChannel channel;

        SeekableChannelReader(SeekableByteChannel channel) {
            this.channel = channel;
        }

        @Override
        public int read(long data, int size) throws IOException {
            ByteBuffer byteBuffer = MemoryUtil.memByteBuffer((long)data, (int)size);
            return this.channel.read(byteBuffer);
        }

        @Override
        public void skip(int n) throws IOException {
            this.channel.position(this.channel.position() + (long)n);
        }

        @Override
        public int eof(long user) {
            return super.eof(user) != 0 && this.channel.isOpen() ? 1 : 0;
        }

        @Override
        public void close() throws IOException {
            this.channel.close();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class ChannelReader
    extends Reader {
        private static final int BUFFER_SIZE = 128;
        private final ReadableByteChannel channel;
        private long buffer = MemoryUtil.nmemAlloc((long)128L);
        private int bufferSize = 128;
        private int bufferPosition;
        private int readPosition;

        ChannelReader(ReadableByteChannel channel) {
            this.channel = channel;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void readToBuffer(int size) throws IOException {
            ByteBuffer byteBuffer = MemoryUtil.memByteBuffer((long)this.buffer, (int)this.bufferSize);
            if (size + this.readPosition > this.bufferSize) {
                this.bufferSize = size + this.readPosition;
                byteBuffer = MemoryUtil.memRealloc((ByteBuffer)byteBuffer, (int)this.bufferSize);
                this.buffer = MemoryUtil.memAddress((ByteBuffer)byteBuffer);
            }
            byteBuffer.position(this.bufferPosition);
            while (size + this.readPosition > this.bufferPosition) {
                try {
                    int i = this.channel.read(byteBuffer);
                    if (i != -1) continue;
                    break;
                }
                finally {
                    this.bufferPosition = byteBuffer.position();
                }
            }
        }

        @Override
        public int read(long data, int size) throws IOException {
            this.readToBuffer(size);
            if (size + this.readPosition > this.bufferPosition) {
                size = this.bufferPosition - this.readPosition;
            }
            MemoryUtil.memCopy((long)(this.buffer + (long)this.readPosition), (long)data, (long)size);
            this.readPosition += size;
            return size;
        }

        @Override
        public void skip(int n) throws IOException {
            if (n > 0) {
                this.readToBuffer(n);
                if (n + this.readPosition > this.bufferPosition) {
                    throw new EOFException("Can't skip past the EOF.");
                }
            }
            if (this.readPosition + n < 0) {
                throw new IOException("Can't seek before the beginning: " + (this.readPosition + n));
            }
            this.readPosition += n;
        }

        @Override
        public void close() throws IOException {
            MemoryUtil.nmemFree((long)this.buffer);
            this.channel.close();
        }
    }
}

