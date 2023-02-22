/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.report;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.report.log.ChatLog;
import net.minecraft.client.report.log.ReceivedMessage;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class GroupedMessagesCollector<T extends ReceivedMessage> {
    private final Function<ChatLog.IndexedEntry<T>, ReportType> reportTypeGetter;
    private final List<ChatLog.IndexedEntry<T>> messages = new ArrayList<ChatLog.IndexedEntry<T>>();
    @Nullable
    private ReportType reportType;

    public GroupedMessagesCollector(Function<ChatLog.IndexedEntry<T>, ReportType> reportTypeGetter) {
        this.reportTypeGetter = reportTypeGetter;
    }

    public boolean add(ChatLog.IndexedEntry<T> message) {
        ReportType reportType = this.reportTypeGetter.apply(message);
        if (this.reportType == null || reportType == this.reportType) {
            this.reportType = reportType;
            this.messages.add(message);
            return true;
        }
        return false;
    }

    @Nullable
    public GroupedMessages<T> collect() {
        if (!this.messages.isEmpty() && this.reportType != null) {
            return new GroupedMessages<T>(this.messages, this.reportType);
        }
        return null;
    }

    @Environment(value=EnvType.CLIENT)
    public static final class ReportType
    extends Enum<ReportType> {
        public static final /* enum */ ReportType REPORTABLE = new ReportType();
        public static final /* enum */ ReportType CONTEXT = new ReportType();
        private static final /* synthetic */ ReportType[] field_39554;

        public static ReportType[] values() {
            return (ReportType[])field_39554.clone();
        }

        public static ReportType valueOf(String string) {
            return Enum.valueOf(ReportType.class, string);
        }

        public boolean isContext() {
            return this == CONTEXT;
        }

        private static /* synthetic */ ReportType[] method_44455() {
            return new ReportType[]{REPORTABLE, CONTEXT};
        }

        static {
            field_39554 = ReportType.method_44455();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record GroupedMessages<T extends ReceivedMessage>(List<ChatLog.IndexedEntry<T>> messages, ReportType type) {
    }
}

