/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2LongMap
 *  it.unimi.dsi.fastutil.objects.Object2LongMaps
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.ObjectUtils
 *  org.slf4j.Logger
 */
package net.minecraft.util.profiler;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import java.io.BufferedWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import net.minecraft.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.ProfileLocationInfo;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.ProfilerTiming;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;

public class ProfileResultImpl
implements ProfileResult {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ProfileLocationInfo EMPTY_INFO = new ProfileLocationInfo(){

        @Override
        public long getTotalTime() {
            return 0L;
        }

        @Override
        public long getMaxTime() {
            return 0L;
        }

        @Override
        public long getVisitCount() {
            return 0L;
        }

        @Override
        public Object2LongMap<String> getCounts() {
            return Object2LongMaps.emptyMap();
        }
    };
    private static final Splitter SPLITTER = Splitter.on((char)'\u001e');
    private static final Comparator<Map.Entry<String, CounterInfo>> COMPARATOR = Map.Entry.comparingByValue(Comparator.comparingLong(counterInfo -> counterInfo.totalTime)).reversed();
    private final Map<String, ? extends ProfileLocationInfo> locationInfos;
    private final long startTime;
    private final int startTick;
    private final long endTime;
    private final int endTick;
    private final int tickDuration;

    public ProfileResultImpl(Map<String, ? extends ProfileLocationInfo> locationInfos, long startTime, int startTick, long endTime, int endTick) {
        this.locationInfos = locationInfos;
        this.startTime = startTime;
        this.startTick = startTick;
        this.endTime = endTime;
        this.endTick = endTick;
        this.tickDuration = endTick - startTick;
    }

    private ProfileLocationInfo getInfo(String path) {
        ProfileLocationInfo profileLocationInfo = this.locationInfos.get(path);
        return profileLocationInfo != null ? profileLocationInfo : EMPTY_INFO;
    }

    @Override
    public List<ProfilerTiming> getTimings(String parentPath) {
        String string = parentPath;
        ProfileLocationInfo profileLocationInfo = this.getInfo("root");
        long l = profileLocationInfo.getTotalTime();
        ProfileLocationInfo profileLocationInfo2 = this.getInfo((String)parentPath);
        long m = profileLocationInfo2.getTotalTime();
        long n = profileLocationInfo2.getVisitCount();
        ArrayList list = Lists.newArrayList();
        if (!((String)parentPath).isEmpty()) {
            parentPath = (String)parentPath + "\u001e";
        }
        long o = 0L;
        for (String string2 : this.locationInfos.keySet()) {
            if (!ProfileResultImpl.isSubpath((String)parentPath, string2)) continue;
            o += this.getInfo(string2).getTotalTime();
        }
        float f = o;
        if (o < m) {
            o = m;
        }
        if (l < o) {
            l = o;
        }
        for (String string3 : this.locationInfos.keySet()) {
            if (!ProfileResultImpl.isSubpath((String)parentPath, string3)) continue;
            ProfileLocationInfo profileLocationInfo3 = this.getInfo(string3);
            long p = profileLocationInfo3.getTotalTime();
            double d = (double)p * 100.0 / (double)o;
            double e = (double)p * 100.0 / (double)l;
            String string4 = string3.substring(((String)parentPath).length());
            list.add(new ProfilerTiming(string4, d, e, profileLocationInfo3.getVisitCount()));
        }
        if ((float)o > f) {
            list.add(new ProfilerTiming("unspecified", (double)((float)o - f) * 100.0 / (double)o, (double)((float)o - f) * 100.0 / (double)l, n));
        }
        Collections.sort(list);
        list.add(0, new ProfilerTiming(string, 100.0, (double)o * 100.0 / (double)l, n));
        return list;
    }

    private static boolean isSubpath(String parent, String path) {
        return path.length() > parent.length() && path.startsWith(parent) && path.indexOf(30, parent.length() + 1) < 0;
    }

    private Map<String, CounterInfo> setupCounters() {
        TreeMap map = Maps.newTreeMap();
        this.locationInfos.forEach((location, info) -> {
            Object2LongMap<String> object2LongMap = info.getCounts();
            if (!object2LongMap.isEmpty()) {
                List list = SPLITTER.splitToList((CharSequence)location);
                object2LongMap.forEach((marker, count) -> map.computeIfAbsent(marker, k -> new CounterInfo()).add(list.iterator(), (long)count));
            }
        });
        return map;
    }

    @Override
    public long getStartTime() {
        return this.startTime;
    }

    @Override
    public int getStartTick() {
        return this.startTick;
    }

    @Override
    public long getEndTime() {
        return this.endTime;
    }

    @Override
    public int getEndTick() {
        return this.endTick;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean save(Path path) {
        boolean bl;
        BufferedWriter writer = null;
        try {
            Files.createDirectories(path.getParent(), new FileAttribute[0]);
            writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, new OpenOption[0]);
            writer.write(this.asString(this.getTimeSpan(), this.getTickSpan()));
            bl = true;
        }
        catch (Throwable throwable) {
            boolean bl2;
            try {
                LOGGER.error("Could not save profiler results to {}", (Object)path, (Object)throwable);
                bl2 = false;
            }
            catch (Throwable throwable2) {
                IOUtils.closeQuietly(writer);
                throw throwable2;
            }
            IOUtils.closeQuietly((Writer)writer);
            return bl2;
        }
        IOUtils.closeQuietly((Writer)writer);
        return bl;
    }

    protected String asString(long timeSpan, int tickSpan) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("---- Minecraft Profiler Results ----\n");
        stringBuilder.append("// ");
        stringBuilder.append(ProfileResultImpl.generateWittyComment());
        stringBuilder.append("\n\n");
        stringBuilder.append("Version: ").append(SharedConstants.getGameVersion().getId()).append('\n');
        stringBuilder.append("Time span: ").append(timeSpan / 1000000L).append(" ms\n");
        stringBuilder.append("Tick span: ").append(tickSpan).append(" ticks\n");
        stringBuilder.append("// This is approximately ").append(String.format(Locale.ROOT, "%.2f", Float.valueOf((float)tickSpan / ((float)timeSpan / 1.0E9f)))).append(" ticks per second. It should be ").append(20).append(" ticks per second\n\n");
        stringBuilder.append("--- BEGIN PROFILE DUMP ---\n\n");
        this.appendTiming(0, "root", stringBuilder);
        stringBuilder.append("--- END PROFILE DUMP ---\n\n");
        Map<String, CounterInfo> map = this.setupCounters();
        if (!map.isEmpty()) {
            stringBuilder.append("--- BEGIN COUNTER DUMP ---\n\n");
            this.appendCounterDump(map, stringBuilder, tickSpan);
            stringBuilder.append("--- END COUNTER DUMP ---\n\n");
        }
        return stringBuilder.toString();
    }

    @Override
    public String getRootTimings() {
        StringBuilder stringBuilder = new StringBuilder();
        this.appendTiming(0, "root", stringBuilder);
        return stringBuilder.toString();
    }

    private static StringBuilder indent(StringBuilder sb, int size) {
        sb.append(String.format(Locale.ROOT, "[%02d] ", size));
        for (int i = 0; i < size; ++i) {
            sb.append("|   ");
        }
        return sb;
    }

    private void appendTiming(int level, String name, StringBuilder sb) {
        List<ProfilerTiming> list = this.getTimings(name);
        Object2LongMap<String> object2LongMap = ((ProfileLocationInfo)ObjectUtils.firstNonNull((Object[])new ProfileLocationInfo[]{this.locationInfos.get(name), EMPTY_INFO})).getCounts();
        object2LongMap.forEach((marker, count) -> ProfileResultImpl.indent(sb, level).append('#').append((String)marker).append(' ').append(count).append('/').append(count / (long)this.tickDuration).append('\n'));
        if (list.size() < 3) {
            return;
        }
        for (int i = 1; i < list.size(); ++i) {
            ProfilerTiming profilerTiming = list.get(i);
            ProfileResultImpl.indent(sb, level).append(profilerTiming.name).append('(').append(profilerTiming.visitCount).append('/').append(String.format(Locale.ROOT, "%.0f", Float.valueOf((float)profilerTiming.visitCount / (float)this.tickDuration))).append(')').append(" - ").append(String.format(Locale.ROOT, "%.2f", profilerTiming.parentSectionUsagePercentage)).append("%/").append(String.format(Locale.ROOT, "%.2f", profilerTiming.totalUsagePercentage)).append("%\n");
            if ("unspecified".equals(profilerTiming.name)) continue;
            try {
                this.appendTiming(level + 1, name + "\u001e" + profilerTiming.name, sb);
                continue;
            }
            catch (Exception exception) {
                sb.append("[[ EXCEPTION ").append(exception).append(" ]]");
            }
        }
    }

    private void appendCounter(int depth, String name, CounterInfo info, int tickSpan, StringBuilder sb) {
        ProfileResultImpl.indent(sb, depth).append(name).append(" total:").append(info.selfTime).append('/').append(info.totalTime).append(" average: ").append(info.selfTime / (long)tickSpan).append('/').append(info.totalTime / (long)tickSpan).append('\n');
        info.subCounters.entrySet().stream().sorted(COMPARATOR).forEach(entry -> this.appendCounter(depth + 1, (String)entry.getKey(), (CounterInfo)entry.getValue(), tickSpan, sb));
    }

    private void appendCounterDump(Map<String, CounterInfo> counters, StringBuilder sb, int tickSpan) {
        counters.forEach((name, info) -> {
            sb.append("-- Counter: ").append((String)name).append(" --\n");
            this.appendCounter(0, "root", info.subCounters.get("root"), tickSpan, sb);
            sb.append("\n\n");
        });
    }

    private static String generateWittyComment() {
        String[] strings = new String[]{"I'd Rather Be Surfing", "Shiny numbers!", "Am I not running fast enough? :(", "I'm working as hard as I can!", "Will I ever be good enough for you? :(", "Speedy. Zoooooom!", "Hello world", "40% better than a crash report.", "Now with extra numbers", "Now with less numbers", "Now with the same numbers", "You should add flames to things, it makes them go faster!", "Do you feel the need for... optimization?", "*cracks redstone whip*", "Maybe if you treated it better then it'll have more motivation to work faster! Poor server."};
        try {
            return strings[(int)(Util.getMeasuringTimeNano() % (long)strings.length)];
        }
        catch (Throwable throwable) {
            return "Witty comment unavailable :(";
        }
    }

    @Override
    public int getTickSpan() {
        return this.tickDuration;
    }

    static class CounterInfo {
        long selfTime;
        long totalTime;
        final Map<String, CounterInfo> subCounters = Maps.newHashMap();

        CounterInfo() {
        }

        public void add(Iterator<String> pathIterator, long time) {
            this.totalTime += time;
            if (!pathIterator.hasNext()) {
                this.selfTime += time;
            } else {
                this.subCounters.computeIfAbsent(pathIterator.next(), k -> new CounterInfo()).add(pathIterator, time);
            }
        }
    }
}

