/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.InputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.EffectGlShader;
import net.minecraft.client.gl.GLImportProcessor;
import net.minecraft.client.gl.GlShader;
import net.minecraft.client.gl.Program;

@Environment(value=EnvType.CLIENT)
public class EffectProgram
extends Program {
    private static final GLImportProcessor LOADER = new GLImportProcessor(){

        @Override
        public String loadImport(boolean inline, String name) {
            return "#error Import statement not supported";
        }
    };
    private int refCount;

    private EffectProgram(Program.Type type, int shaderRef, String name) {
        super(type, shaderRef, name);
    }

    public void attachTo(EffectGlShader program) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        ++this.refCount;
        this.attachTo((GlShader)program);
    }

    @Override
    public void release() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        --this.refCount;
        if (this.refCount <= 0) {
            super.release();
        }
    }

    public static EffectProgram createFromResource(Program.Type type, String name, InputStream stream, String domain) throws IOException {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        int i = EffectProgram.loadProgram(type, name, stream, domain, LOADER);
        EffectProgram effectProgram = new EffectProgram(type, i, name);
        type.getProgramCache().put(name, effectProgram);
        return effectProgram;
    }
}

