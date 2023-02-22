/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.UserApiService
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.report;

import com.mojang.authlib.minecraft.UserApiService;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.report.AbuseReportSender;
import net.minecraft.client.report.ReporterEnvironment;
import net.minecraft.client.report.log.ChatLog;
import net.minecraft.client.report.log.ChatLogImpl;

@Environment(value=EnvType.CLIENT)
public record AbuseReportContext(AbuseReportSender sender, ReporterEnvironment environment, ChatLog chatLog) {
    private static final int MAX_LOGS = 1024;

    public static AbuseReportContext create(ReporterEnvironment environment, UserApiService userApiService) {
        ChatLogImpl chatLogImpl = new ChatLogImpl(1024);
        AbuseReportSender abuseReportSender = AbuseReportSender.create(environment, userApiService);
        return new AbuseReportContext(abuseReportSender, environment, chatLogImpl);
    }

    public boolean environmentEquals(ReporterEnvironment environment) {
        return Objects.equals(this.environment, environment);
    }
}

