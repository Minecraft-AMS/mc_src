/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Splitter
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.properties.Property
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLongArray;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.NbtTypes;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.nbt.visitor.NbtOrderedStringFormatter;
import net.minecraft.nbt.visitor.NbtTextFormatter;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public final class NbtHelper {
    private static final Comparator<NbtList> BLOCK_POS_COMPARATOR = Comparator.comparingInt(nbt -> nbt.getInt(1)).thenComparingInt(nbt -> nbt.getInt(0)).thenComparingInt(nbt -> nbt.getInt(2));
    private static final Comparator<NbtList> ENTITY_POS_COMPARATOR = Comparator.comparingDouble(nbt -> nbt.getDouble(1)).thenComparingDouble(nbt -> nbt.getDouble(0)).thenComparingDouble(nbt -> nbt.getDouble(2));
    public static final String DATA_KEY = "data";
    private static final char LEFT_CURLY_BRACKET = '{';
    private static final char RIGHT_CURLY_BRACKET = '}';
    private static final String COMMA = ",";
    private static final char COLON = ':';
    private static final Splitter COMMA_SPLITTER = Splitter.on((String)",");
    private static final Splitter COLON_SPLITTER = Splitter.on((char)':').limit(2);
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int field_33229 = 2;
    private static final int field_33230 = -1;

    private NbtHelper() {
    }

    @Nullable
    public static GameProfile toGameProfile(NbtCompound nbt) {
        String string = null;
        UUID uUID = null;
        if (nbt.contains("Name", 8)) {
            string = nbt.getString("Name");
        }
        if (nbt.containsUuid("Id")) {
            uUID = nbt.getUuid("Id");
        }
        try {
            GameProfile gameProfile = new GameProfile(uUID, string);
            if (nbt.contains("Properties", 10)) {
                NbtCompound nbtCompound = nbt.getCompound("Properties");
                for (String string2 : nbtCompound.getKeys()) {
                    NbtList nbtList = nbtCompound.getList(string2, 10);
                    for (int i = 0; i < nbtList.size(); ++i) {
                        NbtCompound nbtCompound2 = nbtList.getCompound(i);
                        String string3 = nbtCompound2.getString("Value");
                        if (nbtCompound2.contains("Signature", 8)) {
                            gameProfile.getProperties().put((Object)string2, (Object)new Property(string2, string3, nbtCompound2.getString("Signature")));
                            continue;
                        }
                        gameProfile.getProperties().put((Object)string2, (Object)new Property(string2, string3));
                    }
                }
            }
            return gameProfile;
        }
        catch (Throwable throwable) {
            return null;
        }
    }

    public static NbtCompound writeGameProfile(NbtCompound nbt, GameProfile profile) {
        if (!StringHelper.isEmpty(profile.getName())) {
            nbt.putString("Name", profile.getName());
        }
        if (profile.getId() != null) {
            nbt.putUuid("Id", profile.getId());
        }
        if (!profile.getProperties().isEmpty()) {
            NbtCompound nbtCompound = new NbtCompound();
            for (String string : profile.getProperties().keySet()) {
                NbtList nbtList = new NbtList();
                for (Property property : profile.getProperties().get((Object)string)) {
                    NbtCompound nbtCompound2 = new NbtCompound();
                    nbtCompound2.putString("Value", property.getValue());
                    if (property.hasSignature()) {
                        nbtCompound2.putString("Signature", property.getSignature());
                    }
                    nbtList.add(nbtCompound2);
                }
                nbtCompound.put(string, nbtList);
            }
            nbt.put("Properties", nbtCompound);
        }
        return nbt;
    }

    @VisibleForTesting
    public static boolean matches(@Nullable NbtElement standard, @Nullable NbtElement subject, boolean ignoreListOrder) {
        if (standard == subject) {
            return true;
        }
        if (standard == null) {
            return true;
        }
        if (subject == null) {
            return false;
        }
        if (!standard.getClass().equals(subject.getClass())) {
            return false;
        }
        if (standard instanceof NbtCompound) {
            NbtCompound nbtCompound = (NbtCompound)standard;
            NbtCompound nbtCompound2 = (NbtCompound)subject;
            for (String string : nbtCompound.getKeys()) {
                NbtElement nbtElement = nbtCompound.get(string);
                if (NbtHelper.matches(nbtElement, nbtCompound2.get(string), ignoreListOrder)) continue;
                return false;
            }
            return true;
        }
        if (standard instanceof NbtList && ignoreListOrder) {
            NbtList nbtList = (NbtList)standard;
            NbtList nbtList2 = (NbtList)subject;
            if (nbtList.isEmpty()) {
                return nbtList2.isEmpty();
            }
            for (int i = 0; i < nbtList.size(); ++i) {
                NbtElement nbtElement2 = nbtList.get(i);
                boolean bl = false;
                for (int j = 0; j < nbtList2.size(); ++j) {
                    if (!NbtHelper.matches(nbtElement2, nbtList2.get(j), ignoreListOrder)) continue;
                    bl = true;
                    break;
                }
                if (bl) continue;
                return false;
            }
            return true;
        }
        return standard.equals(subject);
    }

    public static NbtIntArray fromUuid(UUID uuid) {
        return new NbtIntArray(Uuids.toIntArray(uuid));
    }

    public static UUID toUuid(NbtElement element) {
        if (element.getNbtType() != NbtIntArray.TYPE) {
            throw new IllegalArgumentException("Expected UUID-Tag to be of type " + NbtIntArray.TYPE.getCrashReportName() + ", but found " + element.getNbtType().getCrashReportName() + ".");
        }
        int[] is = ((NbtIntArray)element).getIntArray();
        if (is.length != 4) {
            throw new IllegalArgumentException("Expected UUID-Array to be of length 4, but found " + is.length + ".");
        }
        return Uuids.toUuid(is);
    }

    public static BlockPos toBlockPos(NbtCompound nbt) {
        return new BlockPos(nbt.getInt("X"), nbt.getInt("Y"), nbt.getInt("Z"));
    }

    public static NbtCompound fromBlockPos(BlockPos pos) {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putInt("X", pos.getX());
        nbtCompound.putInt("Y", pos.getY());
        nbtCompound.putInt("Z", pos.getZ());
        return nbtCompound;
    }

    public static BlockState toBlockState(RegistryEntryLookup<Block> blockLookup, NbtCompound nbt) {
        if (!nbt.contains("Name", 8)) {
            return Blocks.AIR.getDefaultState();
        }
        Identifier identifier = new Identifier(nbt.getString("Name"));
        Optional<RegistryEntry.Reference<Block>> optional = blockLookup.getOptional(RegistryKey.of(RegistryKeys.BLOCK, identifier));
        if (optional.isEmpty()) {
            return Blocks.AIR.getDefaultState();
        }
        Block block = (Block)((RegistryEntry)optional.get()).value();
        BlockState blockState = block.getDefaultState();
        if (nbt.contains("Properties", 10)) {
            NbtCompound nbtCompound = nbt.getCompound("Properties");
            StateManager<Block, BlockState> stateManager = block.getStateManager();
            for (String string : nbtCompound.getKeys()) {
                net.minecraft.state.property.Property<?> property = stateManager.getProperty(string);
                if (property == null) continue;
                blockState = NbtHelper.withProperty(blockState, property, string, nbtCompound, nbt);
            }
        }
        return blockState;
    }

    private static <S extends State<?, S>, T extends Comparable<T>> S withProperty(S state, net.minecraft.state.property.Property<T> property, String key, NbtCompound properties, NbtCompound root) {
        Optional<T> optional = property.parse(properties.getString(key));
        if (optional.isPresent()) {
            return (S)((State)state.with(property, (Comparable)((Comparable)optional.get())));
        }
        LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", new Object[]{key, properties.getString(key), root.toString()});
        return state;
    }

    public static NbtCompound fromBlockState(BlockState state) {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putString("Name", Registries.BLOCK.getId(state.getBlock()).toString());
        ImmutableMap<net.minecraft.state.property.Property<?>, Comparable<?>> immutableMap = state.getEntries();
        if (!immutableMap.isEmpty()) {
            NbtCompound nbtCompound2 = new NbtCompound();
            for (Map.Entry entry : immutableMap.entrySet()) {
                net.minecraft.state.property.Property property = (net.minecraft.state.property.Property)entry.getKey();
                nbtCompound2.putString(property.getName(), NbtHelper.nameValue(property, (Comparable)entry.getValue()));
            }
            nbtCompound.put("Properties", nbtCompound2);
        }
        return nbtCompound;
    }

    public static NbtCompound fromFluidState(FluidState state) {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putString("Name", Registries.FLUID.getId(state.getFluid()).toString());
        ImmutableMap<net.minecraft.state.property.Property<?>, Comparable<?>> immutableMap = state.getEntries();
        if (!immutableMap.isEmpty()) {
            NbtCompound nbtCompound2 = new NbtCompound();
            for (Map.Entry entry : immutableMap.entrySet()) {
                net.minecraft.state.property.Property property = (net.minecraft.state.property.Property)entry.getKey();
                nbtCompound2.putString(property.getName(), NbtHelper.nameValue(property, (Comparable)entry.getValue()));
            }
            nbtCompound.put("Properties", nbtCompound2);
        }
        return nbtCompound;
    }

    private static <T extends Comparable<T>> String nameValue(net.minecraft.state.property.Property<T> property, Comparable<?> value) {
        return property.name(value);
    }

    public static String toFormattedString(NbtElement nbt) {
        return NbtHelper.toFormattedString(nbt, false);
    }

    public static String toFormattedString(NbtElement nbt, boolean withArrayContents) {
        return NbtHelper.appendFormattedString(new StringBuilder(), nbt, 0, withArrayContents).toString();
    }

    public static StringBuilder appendFormattedString(StringBuilder stringBuilder, NbtElement nbt, int depth, boolean withArrayContents) {
        switch (nbt.getType()) {
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 8: {
                stringBuilder.append(nbt);
                break;
            }
            case 0: {
                break;
            }
            case 7: {
                NbtByteArray nbtByteArray = (NbtByteArray)nbt;
                byte[] bs = nbtByteArray.getByteArray();
                int i = bs.length;
                NbtHelper.appendIndent(depth, stringBuilder).append("byte[").append(i).append("] {\n");
                if (withArrayContents) {
                    NbtHelper.appendIndent(depth + 1, stringBuilder);
                    for (int j = 0; j < bs.length; ++j) {
                        if (j != 0) {
                            stringBuilder.append(',');
                        }
                        if (j % 16 == 0 && j / 16 > 0) {
                            stringBuilder.append('\n');
                            if (j < bs.length) {
                                NbtHelper.appendIndent(depth + 1, stringBuilder);
                            }
                        } else if (j != 0) {
                            stringBuilder.append(' ');
                        }
                        stringBuilder.append(String.format(Locale.ROOT, "0x%02X", bs[j] & 0xFF));
                    }
                } else {
                    NbtHelper.appendIndent(depth + 1, stringBuilder).append(" // Skipped, supply withBinaryBlobs true");
                }
                stringBuilder.append('\n');
                NbtHelper.appendIndent(depth, stringBuilder).append('}');
                break;
            }
            case 9: {
                NbtList nbtList = (NbtList)nbt;
                int k = nbtList.size();
                byte i = nbtList.getHeldType();
                String string = i == 0 ? "undefined" : NbtTypes.byId(i).getCommandFeedbackName();
                NbtHelper.appendIndent(depth, stringBuilder).append("list<").append(string).append(">[").append(k).append("] [");
                if (k != 0) {
                    stringBuilder.append('\n');
                }
                for (int l = 0; l < k; ++l) {
                    if (l != 0) {
                        stringBuilder.append(",\n");
                    }
                    NbtHelper.appendIndent(depth + 1, stringBuilder);
                    NbtHelper.appendFormattedString(stringBuilder, nbtList.get(l), depth + 1, withArrayContents);
                }
                if (k != 0) {
                    stringBuilder.append('\n');
                }
                NbtHelper.appendIndent(depth, stringBuilder).append(']');
                break;
            }
            case 11: {
                NbtIntArray nbtIntArray = (NbtIntArray)nbt;
                int[] is = nbtIntArray.getIntArray();
                int i = 0;
                int[] string = is;
                int l = string.length;
                for (int j = 0; j < l; ++j) {
                    int m = string[j];
                    i = Math.max(i, String.format(Locale.ROOT, "%X", m).length());
                }
                int j = is.length;
                NbtHelper.appendIndent(depth, stringBuilder).append("int[").append(j).append("] {\n");
                if (withArrayContents) {
                    NbtHelper.appendIndent(depth + 1, stringBuilder);
                    for (l = 0; l < is.length; ++l) {
                        if (l != 0) {
                            stringBuilder.append(',');
                        }
                        if (l % 16 == 0 && l / 16 > 0) {
                            stringBuilder.append('\n');
                            if (l < is.length) {
                                NbtHelper.appendIndent(depth + 1, stringBuilder);
                            }
                        } else if (l != 0) {
                            stringBuilder.append(' ');
                        }
                        stringBuilder.append(String.format(Locale.ROOT, "0x%0" + i + "X", is[l]));
                    }
                } else {
                    NbtHelper.appendIndent(depth + 1, stringBuilder).append(" // Skipped, supply withBinaryBlobs true");
                }
                stringBuilder.append('\n');
                NbtHelper.appendIndent(depth, stringBuilder).append('}');
                break;
            }
            case 10: {
                NbtCompound nbtCompound = (NbtCompound)nbt;
                ArrayList list = Lists.newArrayList(nbtCompound.getKeys());
                Collections.sort(list);
                NbtHelper.appendIndent(depth, stringBuilder).append('{');
                if (stringBuilder.length() - stringBuilder.lastIndexOf("\n") > 2 * (depth + 1)) {
                    stringBuilder.append('\n');
                    NbtHelper.appendIndent(depth + 1, stringBuilder);
                }
                int i = list.stream().mapToInt(String::length).max().orElse(0);
                String string = Strings.repeat((String)" ", (int)i);
                for (int l = 0; l < list.size(); ++l) {
                    if (l != 0) {
                        stringBuilder.append(",\n");
                    }
                    String string2 = (String)list.get(l);
                    NbtHelper.appendIndent(depth + 1, stringBuilder).append('\"').append(string2).append('\"').append(string, 0, string.length() - string2.length()).append(": ");
                    NbtHelper.appendFormattedString(stringBuilder, nbtCompound.get(string2), depth + 1, withArrayContents);
                }
                if (!list.isEmpty()) {
                    stringBuilder.append('\n');
                }
                NbtHelper.appendIndent(depth, stringBuilder).append('}');
                break;
            }
            case 12: {
                int m;
                NbtLongArray nbtLongArray = (NbtLongArray)nbt;
                long[] ls = nbtLongArray.getLongArray();
                long n = 0L;
                long[] l = ls;
                int n2 = l.length;
                for (m = 0; m < n2; ++m) {
                    long o = l[m];
                    n = Math.max(n, (long)String.format(Locale.ROOT, "%X", o).length());
                }
                long p = ls.length;
                NbtHelper.appendIndent(depth, stringBuilder).append("long[").append(p).append("] {\n");
                if (withArrayContents) {
                    NbtHelper.appendIndent(depth + 1, stringBuilder);
                    for (m = 0; m < ls.length; ++m) {
                        if (m != 0) {
                            stringBuilder.append(',');
                        }
                        if (m % 16 == 0 && m / 16 > 0) {
                            stringBuilder.append('\n');
                            if (m < ls.length) {
                                NbtHelper.appendIndent(depth + 1, stringBuilder);
                            }
                        } else if (m != 0) {
                            stringBuilder.append(' ');
                        }
                        stringBuilder.append(String.format(Locale.ROOT, "0x%0" + n + "X", ls[m]));
                    }
                } else {
                    NbtHelper.appendIndent(depth + 1, stringBuilder).append(" // Skipped, supply withBinaryBlobs true");
                }
                stringBuilder.append('\n');
                NbtHelper.appendIndent(depth, stringBuilder).append('}');
                break;
            }
            default: {
                stringBuilder.append("<UNKNOWN :(>");
            }
        }
        return stringBuilder;
    }

    private static StringBuilder appendIndent(int depth, StringBuilder stringBuilder) {
        int i = stringBuilder.lastIndexOf("\n") + 1;
        int j = stringBuilder.length() - i;
        for (int k = 0; k < 2 * depth - j; ++k) {
            stringBuilder.append(' ');
        }
        return stringBuilder;
    }

    public static Text toPrettyPrintedText(NbtElement element) {
        return new NbtTextFormatter("", 0).apply(element);
    }

    public static String toNbtProviderString(NbtCompound compound) {
        return new NbtOrderedStringFormatter().apply(NbtHelper.toNbtProviderFormat(compound));
    }

    public static NbtCompound fromNbtProviderString(String string) throws CommandSyntaxException {
        return NbtHelper.fromNbtProviderFormat(StringNbtReader.parse(string));
    }

    @VisibleForTesting
    static NbtCompound toNbtProviderFormat(NbtCompound compound) {
        NbtList nbtList4;
        NbtList nbtList3;
        boolean bl = compound.contains("palettes", 9);
        NbtList nbtList = bl ? compound.getList("palettes", 9).getList(0) : compound.getList("palette", 10);
        NbtList nbtList2 = nbtList.stream().map(NbtCompound.class::cast).map(NbtHelper::toNbtProviderFormattedPalette).map(NbtString::of).collect(Collectors.toCollection(NbtList::new));
        compound.put("palette", nbtList2);
        if (bl) {
            nbtList3 = new NbtList();
            nbtList4 = compound.getList("palettes", 9);
            nbtList4.stream().map(NbtList.class::cast).forEach(nbt -> {
                NbtCompound nbtCompound = new NbtCompound();
                for (int i = 0; i < nbt.size(); ++i) {
                    nbtCompound.putString(nbtList2.getString(i), NbtHelper.toNbtProviderFormattedPalette(nbt.getCompound(i)));
                }
                nbtList3.add(nbtCompound);
            });
            compound.put("palettes", nbtList3);
        }
        if (compound.contains("entities", 9)) {
            nbtList3 = compound.getList("entities", 10);
            nbtList4 = nbtList3.stream().map(NbtCompound.class::cast).sorted(Comparator.comparing(nbt -> nbt.getList("pos", 6), ENTITY_POS_COMPARATOR)).collect(Collectors.toCollection(NbtList::new));
            compound.put("entities", nbtList4);
        }
        nbtList3 = compound.getList("blocks", 10).stream().map(NbtCompound.class::cast).sorted(Comparator.comparing(nbt -> nbt.getList("pos", 3), BLOCK_POS_COMPARATOR)).peek(nbt -> nbt.putString("state", nbtList2.getString(nbt.getInt("state")))).collect(Collectors.toCollection(NbtList::new));
        compound.put(DATA_KEY, nbtList3);
        compound.remove("blocks");
        return compound;
    }

    @VisibleForTesting
    static NbtCompound fromNbtProviderFormat(NbtCompound compound) {
        NbtList nbtList = compound.getList("palette", 8);
        Map map = (Map)nbtList.stream().map(NbtString.class::cast).map(NbtString::asString).collect(ImmutableMap.toImmutableMap(Function.identity(), NbtHelper::fromNbtProviderFormattedPalette));
        if (compound.contains("palettes", 9)) {
            compound.put("palettes", compound.getList("palettes", 10).stream().map(NbtCompound.class::cast).map(nbt -> map.keySet().stream().map(nbt::getString).map(NbtHelper::fromNbtProviderFormattedPalette).collect(Collectors.toCollection(NbtList::new))).collect(Collectors.toCollection(NbtList::new)));
            compound.remove("palette");
        } else {
            compound.put("palette", map.values().stream().collect(Collectors.toCollection(NbtList::new)));
        }
        if (compound.contains(DATA_KEY, 9)) {
            Object2IntOpenHashMap object2IntMap = new Object2IntOpenHashMap();
            object2IntMap.defaultReturnValue(-1);
            for (int i = 0; i < nbtList.size(); ++i) {
                object2IntMap.put((Object)nbtList.getString(i), i);
            }
            NbtList nbtList2 = compound.getList(DATA_KEY, 10);
            for (int j = 0; j < nbtList2.size(); ++j) {
                NbtCompound nbtCompound = nbtList2.getCompound(j);
                String string = nbtCompound.getString("state");
                int k = object2IntMap.getInt((Object)string);
                if (k == -1) {
                    throw new IllegalStateException("Entry " + string + " missing from palette");
                }
                nbtCompound.putInt("state", k);
            }
            compound.put("blocks", nbtList2);
            compound.remove(DATA_KEY);
        }
        return compound;
    }

    @VisibleForTesting
    static String toNbtProviderFormattedPalette(NbtCompound compound) {
        StringBuilder stringBuilder = new StringBuilder(compound.getString("Name"));
        if (compound.contains("Properties", 10)) {
            NbtCompound nbtCompound = compound.getCompound("Properties");
            String string = nbtCompound.getKeys().stream().sorted().map(key -> key + ":" + nbtCompound.get((String)key).asString()).collect(Collectors.joining(COMMA));
            stringBuilder.append('{').append(string).append('}');
        }
        return stringBuilder.toString();
    }

    @VisibleForTesting
    static NbtCompound fromNbtProviderFormattedPalette(String string) {
        String string2;
        NbtCompound nbtCompound = new NbtCompound();
        int i = string.indexOf(123);
        if (i >= 0) {
            string2 = string.substring(0, i);
            NbtCompound nbtCompound2 = new NbtCompound();
            if (i + 2 <= string.length()) {
                String string3 = string.substring(i + 1, string.indexOf(125, i));
                COMMA_SPLITTER.split((CharSequence)string3).forEach(property -> {
                    List list = COLON_SPLITTER.splitToList((CharSequence)property);
                    if (list.size() == 2) {
                        nbtCompound2.putString((String)list.get(0), (String)list.get(1));
                    } else {
                        LOGGER.error("Something went wrong parsing: '{}' -- incorrect gamedata!", (Object)string);
                    }
                });
                nbtCompound.put("Properties", nbtCompound2);
            }
        } else {
            string2 = string;
        }
        nbtCompound.putString("Name", string2);
        return nbtCompound;
    }

    public static NbtCompound putDataVersion(NbtCompound nbt) {
        int i = SharedConstants.getGameVersion().getSaveVersion().getId();
        return NbtHelper.putDataVersion(nbt, i);
    }

    public static NbtCompound putDataVersion(NbtCompound nbt, int dataVersion) {
        nbt.putInt("DataVersion", dataVersion);
        return nbt;
    }

    public static int getDataVersion(NbtCompound nbt, int fallback) {
        return nbt.contains("DataVersion", 99) ? nbt.getInt("DataVersion") : fallback;
    }
}

