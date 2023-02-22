/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.properties.Property
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public final class NbtHelper {
    private static final Logger LOGGER = LogManager.getLogger();

    @Nullable
    public static GameProfile toGameProfile(CompoundTag tag) {
        String string = null;
        String string2 = null;
        if (tag.contains("Name", 8)) {
            string = tag.getString("Name");
        }
        if (tag.contains("Id", 8)) {
            string2 = tag.getString("Id");
        }
        try {
            UUID uUID;
            try {
                uUID = UUID.fromString(string2);
            }
            catch (Throwable throwable) {
                uUID = null;
            }
            GameProfile gameProfile = new GameProfile(uUID, string);
            if (tag.contains("Properties", 10)) {
                CompoundTag compoundTag = tag.getCompound("Properties");
                for (String string3 : compoundTag.getKeys()) {
                    ListTag listTag = compoundTag.getList(string3, 10);
                    for (int i = 0; i < listTag.size(); ++i) {
                        CompoundTag compoundTag2 = listTag.getCompound(i);
                        String string4 = compoundTag2.getString("Value");
                        if (compoundTag2.contains("Signature", 8)) {
                            gameProfile.getProperties().put((Object)string3, (Object)new com.mojang.authlib.properties.Property(string3, string4, compoundTag2.getString("Signature")));
                            continue;
                        }
                        gameProfile.getProperties().put((Object)string3, (Object)new com.mojang.authlib.properties.Property(string3, string4));
                    }
                }
            }
            return gameProfile;
        }
        catch (Throwable throwable) {
            return null;
        }
    }

    public static CompoundTag fromGameProfile(CompoundTag tag, GameProfile profile) {
        if (!ChatUtil.isEmpty(profile.getName())) {
            tag.putString("Name", profile.getName());
        }
        if (profile.getId() != null) {
            tag.putString("Id", profile.getId().toString());
        }
        if (!profile.getProperties().isEmpty()) {
            CompoundTag compoundTag = new CompoundTag();
            for (String string : profile.getProperties().keySet()) {
                ListTag listTag = new ListTag();
                for (com.mojang.authlib.properties.Property property : profile.getProperties().get((Object)string)) {
                    CompoundTag compoundTag2 = new CompoundTag();
                    compoundTag2.putString("Value", property.getValue());
                    if (property.hasSignature()) {
                        compoundTag2.putString("Signature", property.getSignature());
                    }
                    listTag.add(compoundTag2);
                }
                compoundTag.put(string, listTag);
            }
            tag.put("Properties", compoundTag);
        }
        return tag;
    }

    @VisibleForTesting
    public static boolean matches(@Nullable Tag standard, @Nullable Tag subject, boolean equalValue) {
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
        if (standard instanceof CompoundTag) {
            CompoundTag compoundTag = (CompoundTag)standard;
            CompoundTag compoundTag2 = (CompoundTag)subject;
            for (String string : compoundTag.getKeys()) {
                Tag tag = compoundTag.get(string);
                if (NbtHelper.matches(tag, compoundTag2.get(string), equalValue)) continue;
                return false;
            }
            return true;
        }
        if (standard instanceof ListTag && equalValue) {
            ListTag listTag = (ListTag)standard;
            ListTag listTag2 = (ListTag)subject;
            if (listTag.isEmpty()) {
                return listTag2.isEmpty();
            }
            for (int i = 0; i < listTag.size(); ++i) {
                Tag tag2 = listTag.get(i);
                boolean bl = false;
                for (int j = 0; j < listTag2.size(); ++j) {
                    if (!NbtHelper.matches(tag2, listTag2.get(j), equalValue)) continue;
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

    public static CompoundTag fromUuid(UUID uuid) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putLong("M", uuid.getMostSignificantBits());
        compoundTag.putLong("L", uuid.getLeastSignificantBits());
        return compoundTag;
    }

    public static UUID toUuid(CompoundTag tag) {
        return new UUID(tag.getLong("M"), tag.getLong("L"));
    }

    public static BlockPos toBlockPos(CompoundTag tag) {
        return new BlockPos(tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z"));
    }

    public static CompoundTag fromBlockPos(BlockPos pos) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putInt("X", pos.getX());
        compoundTag.putInt("Y", pos.getY());
        compoundTag.putInt("Z", pos.getZ());
        return compoundTag;
    }

    public static BlockState toBlockState(CompoundTag tag) {
        if (!tag.contains("Name", 8)) {
            return Blocks.AIR.getDefaultState();
        }
        Block block = Registry.BLOCK.get(new Identifier(tag.getString("Name")));
        BlockState blockState = block.getDefaultState();
        if (tag.contains("Properties", 10)) {
            CompoundTag compoundTag = tag.getCompound("Properties");
            StateManager<Block, BlockState> stateManager = block.getStateManager();
            for (String string : compoundTag.getKeys()) {
                Property<?> property = stateManager.getProperty(string);
                if (property == null) continue;
                blockState = NbtHelper.withProperty(blockState, property, string, compoundTag, tag);
            }
        }
        return blockState;
    }

    private static <S extends State<S>, T extends Comparable<T>> S withProperty(S state, Property<T> property, String key, CompoundTag propertiesTag, CompoundTag mainTag) {
        Optional<T> optional = property.parse(propertiesTag.getString(key));
        if (optional.isPresent()) {
            return (S)((State)state.with(property, (Comparable)((Comparable)optional.get())));
        }
        LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", (Object)key, (Object)propertiesTag.getString(key), (Object)mainTag.toString());
        return state;
    }

    public static CompoundTag fromBlockState(BlockState state) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("Name", Registry.BLOCK.getId(state.getBlock()).toString());
        ImmutableMap<Property<?>, Comparable<?>> immutableMap = state.getEntries();
        if (!immutableMap.isEmpty()) {
            CompoundTag compoundTag2 = new CompoundTag();
            for (Map.Entry entry : immutableMap.entrySet()) {
                Property property = (Property)entry.getKey();
                compoundTag2.putString(property.getName(), NbtHelper.nameValue(property, (Comparable)entry.getValue()));
            }
            compoundTag.put("Properties", compoundTag2);
        }
        return compoundTag;
    }

    private static <T extends Comparable<T>> String nameValue(Property<T> property, Comparable<?> value) {
        return property.name(value);
    }

    public static CompoundTag update(DataFixer fixer, DataFixTypes fixTypes, CompoundTag tag, int oldVersion) {
        return NbtHelper.update(fixer, fixTypes, tag, oldVersion, SharedConstants.getGameVersion().getWorldVersion());
    }

    public static CompoundTag update(DataFixer fixer, DataFixTypes fixTypes, CompoundTag tag, int oldVersion, int currentVersion) {
        return (CompoundTag)fixer.update(fixTypes.getTypeReference(), new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)tag), oldVersion, currentVersion).getValue();
    }
}

