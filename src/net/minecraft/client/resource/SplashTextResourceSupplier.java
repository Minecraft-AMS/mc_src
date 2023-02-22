/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.resource;

import com.google.common.collect.Lists;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.Session;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SplashTextResourceSupplier
extends SinglePreparationResourceReloadListener<List<String>> {
    private static final Identifier RESOURCE_ID = new Identifier("texts/splashes.txt");
    private static final Random RANDOM = new Random();
    private final List<String> splashTexts = Lists.newArrayList();
    private final Session field_18934;

    public SplashTextResourceSupplier(Session session) {
        this.field_18934 = session;
    }

    /*
     * Exception decompiling
     */
    @Override
    protected List<String> prepare(ResourceManager resourceManager, Profiler profiler) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    @Override
    protected void apply(List<String> list, ResourceManager resourceManager, Profiler profiler) {
        this.splashTexts.clear();
        this.splashTexts.addAll(list);
    }

    @Nullable
    public String get() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        if (calendar.get(2) + 1 == 12 && calendar.get(5) == 24) {
            return "Merry X-mas!";
        }
        if (calendar.get(2) + 1 == 1 && calendar.get(5) == 1) {
            return "Happy new year!";
        }
        if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31) {
            return "OOoooOOOoooo! Spooky!";
        }
        if (this.splashTexts.isEmpty()) {
            return null;
        }
        if (this.field_18934 != null && RANDOM.nextInt(this.splashTexts.size()) == 42) {
            return this.field_18934.getUsername().toUpperCase(Locale.ROOT) + " IS YOU";
        }
        return this.splashTexts.get(RANDOM.nextInt(this.splashTexts.size()));
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager manager, Profiler profiler) {
        return this.prepare(manager, profiler);
    }

    private static /* synthetic */ boolean method_18664(String string) {
        return string.hashCode() != 125780783;
    }
}

