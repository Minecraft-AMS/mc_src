/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface GameTest {
    public int tickLimit() default 100;

    public String batchId() default "defaultBatch";

    public int rotation() default 0;

    public boolean required() default true;

    public String structureName() default "";

    public long duration() default 0L;

    public int maxAttempts() default 1;

    public int requiredSuccesses() default 1;
}

