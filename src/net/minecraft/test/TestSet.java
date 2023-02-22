/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.test;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.function.Consumer;
import net.minecraft.test.GameTestState;
import net.minecraft.test.TestListener;
import org.jetbrains.annotations.Nullable;

public class TestSet {
    private final Collection<GameTestState> tests = Lists.newArrayList();
    @Nullable
    private Collection<TestListener> field_25303 = Lists.newArrayList();

    public TestSet() {
    }

    public TestSet(Collection<GameTestState> tests) {
        this.tests.addAll(tests);
    }

    public void add(GameTestState test) {
        this.tests.add(test);
        this.field_25303.forEach(test::addListener);
    }

    public void addListener(TestListener listener) {
        this.field_25303.add(listener);
        this.tests.forEach(gameTestState -> gameTestState.addListener(listener));
    }

    public void method_29407(final Consumer<GameTestState> consumer) {
        this.addListener(new TestListener(){

            @Override
            public void onStarted(GameTestState test) {
            }

            @Override
            public void onFailed(GameTestState test) {
                consumer.accept(test);
            }
        });
    }

    public int getFailedRequiredTestCount() {
        return (int)this.tests.stream().filter(GameTestState::isFailed).filter(GameTestState::isRequired).count();
    }

    public int getFailedOptionalTestCount() {
        return (int)this.tests.stream().filter(GameTestState::isFailed).filter(GameTestState::isOptional).count();
    }

    public int getCompletedTestCount() {
        return (int)this.tests.stream().filter(GameTestState::isCompleted).count();
    }

    public boolean failed() {
        return this.getFailedRequiredTestCount() > 0;
    }

    public boolean hasFailedOptionalTests() {
        return this.getFailedOptionalTestCount() > 0;
    }

    public int getTestCount() {
        return this.tests.size();
    }

    public boolean isDone() {
        return this.getCompletedTestCount() == this.getTestCount();
    }

    public String getResultString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append('[');
        this.tests.forEach(gameTestState -> {
            if (!gameTestState.isStarted()) {
                stringBuffer.append(' ');
            } else if (gameTestState.isPassed()) {
                stringBuffer.append('+');
            } else if (gameTestState.isFailed()) {
                stringBuffer.append(gameTestState.isRequired() ? (char)'X' : (char)'x');
            } else {
                stringBuffer.append('_');
            }
        });
        stringBuffer.append(']');
        return stringBuffer.toString();
    }

    public String toString() {
        return this.getResultString();
    }
}

