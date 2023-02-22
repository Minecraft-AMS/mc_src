/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.report.AbuseReport
 *  com.mojang.authlib.minecraft.report.AbuseReportLimits
 *  com.mojang.authlib.minecraft.report.ReportChatMessage
 *  com.mojang.authlib.minecraft.report.ReportChatMessageBody
 *  com.mojang.authlib.minecraft.report.ReportChatMessageBody$LastSeenSignature
 *  com.mojang.authlib.minecraft.report.ReportChatMessageContent
 *  com.mojang.authlib.minecraft.report.ReportChatMessageHeader
 *  com.mojang.authlib.minecraft.report.ReportEvidence
 *  com.mojang.authlib.minecraft.report.ReportedEntity
 *  com.mojang.datafixers.util.Either
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMaps
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectRBTreeMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntArrayPriorityQueue
 *  it.unimi.dsi.fastutil.ints.IntCollection
 *  it.unimi.dsi.fastutil.ints.IntComparators
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.report;

import com.mojang.authlib.minecraft.report.AbuseReport;
import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.authlib.minecraft.report.ReportChatMessage;
import com.mojang.authlib.minecraft.report.ReportChatMessageBody;
import com.mojang.authlib.minecraft.report.ReportChatMessageContent;
import com.mojang.authlib.minecraft.report.ReportChatMessageHeader;
import com.mojang.authlib.minecraft.report.ReportEvidence;
import com.mojang.authlib.minecraft.report.ReportedEntity;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayPriorityQueue;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.report.AbuseReportContext;
import net.minecraft.client.report.AbuseReportReason;
import net.minecraft.client.report.log.ChatLog;
import net.minecraft.client.report.log.ChatLogEntry;
import net.minecraft.client.report.log.HeaderEntry;
import net.minecraft.client.report.log.ReceivedMessage;
import net.minecraft.network.message.LastSeenMessageList;
import net.minecraft.network.message.MessageBody;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ChatAbuseReport {
    private final UUID id;
    private final Instant timestamp;
    private final UUID reportedPlayerUuid;
    private final AbuseReportLimits limits;
    private final IntSet selections = new IntOpenHashSet();
    private String opinionComments = "";
    @Nullable
    private AbuseReportReason reason;

    private ChatAbuseReport(UUID id, Instant timestamp, UUID reportedPlayerUuid, AbuseReportLimits limits) {
        this.id = id;
        this.timestamp = timestamp;
        this.reportedPlayerUuid = reportedPlayerUuid;
        this.limits = limits;
    }

    public ChatAbuseReport(UUID reportedPlayerUuid, AbuseReportLimits limits) {
        this(UUID.randomUUID(), Instant.now(), reportedPlayerUuid, limits);
    }

    public void setOpinionComments(String opinionComments) {
        this.opinionComments = opinionComments;
    }

    public void setReason(AbuseReportReason reason) {
        this.reason = reason;
    }

    public void toggleMessageSelection(int index) {
        if (this.selections.contains(index)) {
            this.selections.remove(index);
        } else if (this.selections.size() < this.limits.maxReportedMessageCount()) {
            this.selections.add(index);
        }
    }

    public UUID getReportedPlayerUuid() {
        return this.reportedPlayerUuid;
    }

    public IntSet getSelections() {
        return this.selections;
    }

    public String getOpinionComments() {
        return this.opinionComments;
    }

    @Nullable
    public AbuseReportReason getReason() {
        return this.reason;
    }

    public boolean hasSelectedMessage(int index) {
        return this.selections.contains(index);
    }

    @Nullable
    public ValidationError validate() {
        if (this.selections.isEmpty()) {
            return ValidationError.NO_REPORTED_MESSAGES;
        }
        if (this.selections.size() > this.limits.maxReportedMessageCount()) {
            return ValidationError.TOO_MANY_MESSAGES;
        }
        if (this.reason == null) {
            return ValidationError.NO_REASON;
        }
        if (this.opinionComments.length() > this.limits.maxOpinionCommentsLength()) {
            return ValidationError.COMMENTS_TOO_LONG;
        }
        return null;
    }

    public Either<ReportWithId, ValidationError> finalizeReport(AbuseReportContext reporter) {
        ValidationError validationError = this.validate();
        if (validationError != null) {
            return Either.right((Object)validationError);
        }
        String string = Objects.requireNonNull(this.reason).getId();
        ReportEvidence reportEvidence = this.collectEvidence(reporter.chatLog());
        ReportedEntity reportedEntity = new ReportedEntity(this.reportedPlayerUuid);
        AbuseReport abuseReport = new AbuseReport(this.opinionComments, string, reportEvidence, reportedEntity, this.timestamp);
        return Either.left((Object)new ReportWithId(this.id, abuseReport));
    }

    private ReportEvidence collectEvidence(ChatLog log) {
        Int2ObjectRBTreeMap int2ObjectSortedMap = new Int2ObjectRBTreeMap();
        this.selections.forEach(arg_0 -> this.method_44962(log, (Int2ObjectSortedMap)int2ObjectSortedMap, arg_0));
        return new ReportEvidence(new ArrayList(int2ObjectSortedMap.values()));
    }

    private Stream<ChatLog.IndexedEntry<HeaderEntry>> streamHeadersFrom(ChatLog log, Int2ObjectMap<ReceivedMessage.ChatMessage> evidences, UUID senderUuid) {
        int i = Integer.MAX_VALUE;
        int j = Integer.MIN_VALUE;
        for (Int2ObjectMap.Entry entry : Int2ObjectMaps.fastIterable(evidences)) {
            ReceivedMessage.ChatMessage chatMessage = (ReceivedMessage.ChatMessage)entry.getValue();
            if (!chatMessage.getSenderUuid().equals(senderUuid)) continue;
            int k = entry.getIntKey();
            i = Math.min(i, k);
            j = Math.max(j, k);
        }
        return log.streamForward(i, j).streamIndexedEntries().map(indexedEntry -> indexedEntry.cast(HeaderEntry.class)).filter(Objects::nonNull).filter(headerEntry -> ((HeaderEntry)headerEntry.entry()).header().sender().equals(senderUuid));
    }

    private static Int2ObjectMap<ReceivedMessage.ChatMessage> collectEvidences(ChatLog log, int selectedIndex, AbuseReportLimits abuseReportLimits) {
        int i = abuseReportLimits.leadingContextMessageCount() + 1;
        Int2ObjectOpenHashMap int2ObjectMap = new Int2ObjectOpenHashMap();
        ChatAbuseReport.collectPrecedingMessages(log, selectedIndex, (arg_0, arg_1) -> ChatAbuseReport.method_44964((Int2ObjectMap)int2ObjectMap, i, arg_0, arg_1));
        ChatAbuseReport.streamSucceedingMessages(log, selectedIndex, abuseReportLimits.trailingContextMessageCount()).forEach(arg_0 -> ChatAbuseReport.method_44965((Int2ObjectMap)int2ObjectMap, arg_0));
        return int2ObjectMap;
    }

    private static Stream<ChatLog.IndexedEntry<ReceivedMessage.ChatMessage>> streamSucceedingMessages(ChatLog log, int selectedIndex, int maxCount) {
        return log.streamForward(log.getNextIndex(selectedIndex)).streamIndexedEntries().map(indexedEntry -> indexedEntry.cast(ReceivedMessage.ChatMessage.class)).filter(Objects::nonNull).limit(maxCount);
    }

    private static void collectPrecedingMessages(ChatLog log, int selectedIndex, IndexedMessageConsumer consumer) {
        IntArrayPriorityQueue intPriorityQueue = new IntArrayPriorityQueue(IntComparators.OPPOSITE_COMPARATOR);
        intPriorityQueue.enqueue(selectedIndex);
        IntOpenHashSet intSet = new IntOpenHashSet();
        intSet.add(selectedIndex);
        while (!intPriorityQueue.isEmpty()) {
            int i = intPriorityQueue.dequeueInt();
            ChatLogEntry chatLogEntry = log.get(i);
            if (!(chatLogEntry instanceof ReceivedMessage.ChatMessage)) continue;
            ReceivedMessage.ChatMessage chatMessage = (ReceivedMessage.ChatMessage)chatLogEntry;
            if (!consumer.accept(i, chatMessage)) break;
            chatLogEntry = ChatAbuseReport.collectIndicesUntilLastSeen(log, i, chatMessage.message()).iterator();
            while (chatLogEntry.hasNext()) {
                int j = (Integer)chatLogEntry.next();
                if (!intSet.add(j)) continue;
                intPriorityQueue.enqueue(j);
            }
        }
    }

    private static IntCollection collectIndicesUntilLastSeen(ChatLog log, int selectedIndex, SignedMessage message) {
        Set set = (Set)message.signedBody().lastSeenMessages().entries().stream().map(LastSeenMessageList.Entry::lastSignature).collect(Collectors.toCollection(ObjectOpenHashSet::new));
        MessageSignatureData messageSignatureData = message.signedHeader().precedingSignature();
        if (messageSignatureData != null) {
            set.add(messageSignatureData);
        }
        IntArrayList intList = new IntArrayList();
        Iterator iterator = log.streamBackward(selectedIndex).streamIndexedEntries().iterator();
        while (iterator.hasNext() && !set.isEmpty()) {
            ReceivedMessage.ChatMessage chatMessage;
            ChatLog.IndexedEntry indexedEntry = (ChatLog.IndexedEntry)iterator.next();
            Object t = indexedEntry.entry();
            if (!(t instanceof ReceivedMessage.ChatMessage) || !set.remove((chatMessage = (ReceivedMessage.ChatMessage)t).headerSignature())) continue;
            intList.add(indexedEntry.index());
        }
        return intList;
    }

    private ReportChatMessage toReportChatMessage(int index, ReceivedMessage.ChatMessage message) {
        SignedMessage signedMessage = message.message();
        MessageBody messageBody = signedMessage.signedBody();
        Instant instant = signedMessage.getTimestamp();
        long l = signedMessage.getSalt();
        ByteBuffer byteBuffer = signedMessage.headerSignature().toByteBuffer();
        ByteBuffer byteBuffer2 = Util.map(signedMessage.signedHeader().precedingSignature(), MessageSignatureData::toByteBuffer);
        ByteBuffer byteBuffer3 = ByteBuffer.wrap(messageBody.digest().asBytes());
        ReportChatMessageContent reportChatMessageContent = new ReportChatMessageContent(signedMessage.getSignedContent().plain(), signedMessage.getSignedContent().isDecorated() ? ChatAbuseReport.serializeContent(signedMessage.getSignedContent().decorated()) : null);
        String string = signedMessage.unsignedContent().map(ChatAbuseReport::serializeContent).orElse(null);
        List<ReportChatMessageBody.LastSeenSignature> list = messageBody.lastSeenMessages().entries().stream().map(entry -> new ReportChatMessageBody.LastSeenSignature(entry.profileId(), entry.lastSignature().toByteBuffer())).toList();
        return new ReportChatMessage(new ReportChatMessageHeader(byteBuffer2, message.getSenderUuid(), byteBuffer3, byteBuffer), new ReportChatMessageBody(instant, l, list, reportChatMessageContent), string, this.hasSelectedMessage(index));
    }

    private ReportChatMessage toReportChatMessage(HeaderEntry headerEntry) {
        ByteBuffer byteBuffer = headerEntry.headerSignature().toByteBuffer();
        ByteBuffer byteBuffer2 = Util.map(headerEntry.header().precedingSignature(), MessageSignatureData::toByteBuffer);
        return new ReportChatMessage(new ReportChatMessageHeader(byteBuffer2, headerEntry.header().sender(), ByteBuffer.wrap(headerEntry.bodyDigest()), byteBuffer), null, null, false);
    }

    private static String serializeContent(Text content) {
        return Text.Serializer.toSortedJsonString(content);
    }

    public ChatAbuseReport copy() {
        ChatAbuseReport chatAbuseReport = new ChatAbuseReport(this.id, this.timestamp, this.reportedPlayerUuid, this.limits);
        chatAbuseReport.selections.addAll((IntCollection)this.selections);
        chatAbuseReport.opinionComments = this.opinionComments;
        chatAbuseReport.reason = this.reason;
        return chatAbuseReport;
    }

    private static /* synthetic */ void method_44965(Int2ObjectMap message, ChatLog.IndexedEntry indexedEntry) {
        message.put(indexedEntry.index(), (Object)((ReceivedMessage.ChatMessage)indexedEntry.entry()));
    }

    private static /* synthetic */ boolean method_44964(Int2ObjectMap index, int message, int i, ReceivedMessage.ChatMessage chatMessage) {
        index.put(i, (Object)chatMessage);
        return index.size() < message;
    }

    private /* synthetic */ void method_44962(ChatLog selection, Int2ObjectSortedMap int2ObjectSortedMap, int i) {
        Int2ObjectMap<ReceivedMessage.ChatMessage> int2ObjectMap = ChatAbuseReport.collectEvidences(selection, i, this.limits);
        ObjectOpenHashSet set = new ObjectOpenHashSet();
        for (Int2ObjectMap.Entry entry2 : Int2ObjectMaps.fastIterable(int2ObjectMap)) {
            int j = entry2.getIntKey();
            ReceivedMessage.ChatMessage chatMessage = (ReceivedMessage.ChatMessage)entry2.getValue();
            int2ObjectSortedMap.put(j, (Object)this.toReportChatMessage(j, chatMessage));
            set.add(chatMessage.getSenderUuid());
        }
        for (UUID uUID : set) {
            this.streamHeadersFrom(selection, int2ObjectMap, uUID).forEach(entry -> {
                HeaderEntry headerEntry = (HeaderEntry)entry.entry();
                if (headerEntry instanceof ReceivedMessage.ChatMessage) {
                    ReceivedMessage.ChatMessage chatMessage = (ReceivedMessage.ChatMessage)headerEntry;
                    int2ObjectSortedMap.putIfAbsent(entry.index(), (Object)this.toReportChatMessage(entry.index(), chatMessage));
                } else {
                    int2ObjectSortedMap.putIfAbsent(entry.index(), (Object)this.toReportChatMessage(headerEntry));
                }
            });
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record ValidationError(Text message) {
        public static final ValidationError NO_REASON = new ValidationError(Text.translatable("gui.chatReport.send.no_reason"));
        public static final ValidationError NO_REPORTED_MESSAGES = new ValidationError(Text.translatable("gui.chatReport.send.no_reported_messages"));
        public static final ValidationError TOO_MANY_MESSAGES = new ValidationError(Text.translatable("gui.chatReport.send.too_many_messages"));
        public static final ValidationError COMMENTS_TOO_LONG = new ValidationError(Text.translatable("gui.chatReport.send.comments_too_long"));
    }

    @Environment(value=EnvType.CLIENT)
    public record ReportWithId(UUID id, AbuseReport report) {
    }

    @Environment(value=EnvType.CLIENT)
    static interface IndexedMessageConsumer {
        public boolean accept(int var1, ReceivedMessage.ChatMessage var2);
    }
}

