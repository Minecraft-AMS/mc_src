/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.escape.Escaper
 *  com.google.common.escape.Escapers
 *  com.mojang.logging.LogUtils
 *  org.slf4j.Logger
 */
package net.minecraft.test;

import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import com.mojang.logging.LogUtils;
import net.minecraft.test.GameTestState;
import net.minecraft.test.TestCompletionListener;
import net.minecraft.util.Util;
import org.slf4j.Logger;

public class TeamCityTestCompletionListener
implements TestCompletionListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Escaper ESCAPER = Escapers.builder().addEscape('\'', "|'").addEscape('\n', "|n").addEscape('\r', "|r").addEscape('|', "||").addEscape('[', "|[").addEscape(']', "|]").build();

    @Override
    public void onTestFailed(GameTestState test) {
        String string = ESCAPER.escape(test.getTemplatePath());
        String string2 = ESCAPER.escape(test.getThrowable().getMessage());
        String string3 = ESCAPER.escape(Util.getInnermostMessage(test.getThrowable()));
        LOGGER.info("##teamcity[testStarted name='{}']", (Object)string);
        if (test.isRequired()) {
            LOGGER.info("##teamcity[testFailed name='{}' message='{}' details='{}']", new Object[]{string, string2, string3});
        } else {
            LOGGER.info("##teamcity[testIgnored name='{}' message='{}' details='{}']", new Object[]{string, string2, string3});
        }
        LOGGER.info("##teamcity[testFinished name='{}' duration='{}']", (Object)string, (Object)test.getElapsedMilliseconds());
    }

    @Override
    public void onTestPassed(GameTestState test) {
        String string = ESCAPER.escape(test.getTemplatePath());
        LOGGER.info("##teamcity[testStarted name='{}']", (Object)string);
        LOGGER.info("##teamcity[testFinished name='{}' duration='{}']", (Object)string, (Object)test.getElapsedMilliseconds());
    }
}

