/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.opengl.ARBTimerQuery
 *  org.lwjgl.opengl.GL
 *  org.lwjgl.opengl.GL32C
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.ARBTimerQuery;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL32C;

@Environment(value=EnvType.CLIENT)
public class GlTimer {
    private int queryId;

    public static Optional<GlTimer> getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public void beginProfile() {
        RenderSystem.assertOnRenderThread();
        if (this.queryId != 0) {
            throw new IllegalStateException("Current profile not ended");
        }
        this.queryId = GL32C.glGenQueries();
        GL32C.glBeginQuery((int)35007, (int)this.queryId);
    }

    public Query endProfile() {
        RenderSystem.assertOnRenderThread();
        if (this.queryId == 0) {
            throw new IllegalStateException("endProfile called before beginProfile");
        }
        GL32C.glEndQuery((int)35007);
        Query query = new Query(this.queryId);
        this.queryId = 0;
        return query;
    }

    @Environment(value=EnvType.CLIENT)
    static class InstanceHolder {
        static final Optional<GlTimer> INSTANCE = Optional.ofNullable(InstanceHolder.create());

        private InstanceHolder() {
        }

        @Nullable
        private static GlTimer create() {
            if (!GL.getCapabilities().GL_ARB_timer_query) {
                return null;
            }
            return new GlTimer();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Query {
        private static final long MISSING = 0L;
        private static final long CLOSED = -1L;
        private final int queryId;
        private long result;

        Query(int queryId) {
            this.queryId = queryId;
        }

        public void close() {
            RenderSystem.assertOnRenderThread();
            if (this.result != 0L) {
                return;
            }
            this.result = -1L;
            GL32C.glDeleteQueries((int)this.queryId);
        }

        public boolean isResultAvailable() {
            RenderSystem.assertOnRenderThread();
            if (this.result != 0L) {
                return true;
            }
            if (1 == GL32C.glGetQueryObjecti((int)this.queryId, (int)34919)) {
                this.result = ARBTimerQuery.glGetQueryObjecti64((int)this.queryId, (int)34918);
                GL32C.glDeleteQueries((int)this.queryId);
                return true;
            }
            return false;
        }

        public long queryResult() {
            RenderSystem.assertOnRenderThread();
            if (this.result == 0L) {
                this.result = ARBTimerQuery.glGetQueryObjecti64((int)this.queryId, (int)34918);
                GL32C.glDeleteQueries((int)this.queryId);
            }
            return this.result;
        }
    }
}

