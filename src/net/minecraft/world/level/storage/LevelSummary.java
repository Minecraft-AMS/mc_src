/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.level.storage;

import java.nio.file.Path;
import net.minecraft.GameVersion;
import net.minecraft.SharedConstants;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringHelper;
import net.minecraft.world.GameMode;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.storage.SaveVersionInfo;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

public class LevelSummary
implements Comparable<LevelSummary> {
    private final LevelInfo levelInfo;
    private final SaveVersionInfo versionInfo;
    private final String name;
    private final boolean requiresConversion;
    private final boolean locked;
    private final Path iconPath;
    @Nullable
    private Text details;

    public LevelSummary(LevelInfo levelInfo, SaveVersionInfo versionInfo, String name, boolean requiresConversion, boolean locked, Path iconPath) {
        this.levelInfo = levelInfo;
        this.versionInfo = versionInfo;
        this.name = name;
        this.locked = locked;
        this.iconPath = iconPath;
        this.requiresConversion = requiresConversion;
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        return StringUtils.isEmpty((CharSequence)this.levelInfo.getLevelName()) ? this.name : this.levelInfo.getLevelName();
    }

    public Path getIconPath() {
        return this.iconPath;
    }

    public boolean requiresConversion() {
        return this.requiresConversion;
    }

    public long getLastPlayed() {
        return this.versionInfo.getLastPlayed();
    }

    @Override
    public int compareTo(LevelSummary levelSummary) {
        if (this.versionInfo.getLastPlayed() < levelSummary.versionInfo.getLastPlayed()) {
            return 1;
        }
        if (this.versionInfo.getLastPlayed() > levelSummary.versionInfo.getLastPlayed()) {
            return -1;
        }
        return this.name.compareTo(levelSummary.name);
    }

    public LevelInfo getLevelInfo() {
        return this.levelInfo;
    }

    public GameMode getGameMode() {
        return this.levelInfo.getGameMode();
    }

    public boolean isHardcore() {
        return this.levelInfo.isHardcore();
    }

    public boolean hasCheats() {
        return this.levelInfo.areCommandsAllowed();
    }

    public MutableText getVersion() {
        if (StringHelper.isEmpty(this.versionInfo.getVersionName())) {
            return Text.translatable("selectWorld.versionUnknown");
        }
        return Text.literal(this.versionInfo.getVersionName());
    }

    public SaveVersionInfo getVersionInfo() {
        return this.versionInfo;
    }

    public boolean isDifferentVersion() {
        return this.isFutureLevel() || !SharedConstants.getGameVersion().isStable() && !this.versionInfo.isStable() || this.getConversionWarning().promptsBackup();
    }

    public boolean isFutureLevel() {
        return this.versionInfo.getVersion().getId() > SharedConstants.getGameVersion().getSaveVersion().getId();
    }

    public ConversionWarning getConversionWarning() {
        GameVersion gameVersion = SharedConstants.getGameVersion();
        int i = gameVersion.getSaveVersion().getId();
        int j = this.versionInfo.getVersion().getId();
        if (!gameVersion.isStable() && j < i) {
            return ConversionWarning.UPGRADE_TO_SNAPSHOT;
        }
        if (j > i) {
            return ConversionWarning.DOWNGRADE;
        }
        return ConversionWarning.NONE;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public boolean isUnavailable() {
        if (this.isLocked() || this.requiresConversion()) {
            return true;
        }
        return !this.isVersionAvailable();
    }

    public boolean isVersionAvailable() {
        return SharedConstants.getGameVersion().getSaveVersion().isAvailableTo(this.versionInfo.getVersion());
    }

    public Text getDetails() {
        if (this.details == null) {
            this.details = this.createDetails();
        }
        return this.details;
    }

    private Text createDetails() {
        MutableText mutableText;
        if (this.isLocked()) {
            return Text.translatable("selectWorld.locked").formatted(Formatting.RED);
        }
        if (this.requiresConversion()) {
            return Text.translatable("selectWorld.conversion").formatted(Formatting.RED);
        }
        if (!this.isVersionAvailable()) {
            return Text.translatable("selectWorld.incompatible_series").formatted(Formatting.RED);
        }
        MutableText mutableText2 = mutableText = this.isHardcore() ? Text.empty().append(Text.translatable("gameMode.hardcore").formatted(Formatting.DARK_RED)) : Text.translatable("gameMode." + this.getGameMode().getName());
        if (this.hasCheats()) {
            mutableText.append(", ").append(Text.translatable("selectWorld.cheats"));
        }
        MutableText mutableText22 = this.getVersion();
        MutableText mutableText3 = Text.literal(", ").append(Text.translatable("selectWorld.version")).append(" ");
        if (this.isDifferentVersion()) {
            mutableText3.append(mutableText22.formatted(this.isFutureLevel() ? Formatting.RED : Formatting.ITALIC));
        } else {
            mutableText3.append(mutableText22);
        }
        mutableText.append(mutableText3);
        return mutableText;
    }

    @Override
    public /* synthetic */ int compareTo(Object other) {
        return this.compareTo((LevelSummary)other);
    }

    public static final class ConversionWarning
    extends Enum<ConversionWarning> {
        public static final /* enum */ ConversionWarning NONE = new ConversionWarning(false, false, "");
        public static final /* enum */ ConversionWarning DOWNGRADE = new ConversionWarning(true, true, "downgrade");
        public static final /* enum */ ConversionWarning UPGRADE_TO_SNAPSHOT = new ConversionWarning(true, false, "snapshot");
        private final boolean backup;
        private final boolean boldRedFormatting;
        private final String translationKeySuffix;
        private static final /* synthetic */ ConversionWarning[] field_28443;

        public static ConversionWarning[] values() {
            return (ConversionWarning[])field_28443.clone();
        }

        public static ConversionWarning valueOf(String string) {
            return Enum.valueOf(ConversionWarning.class, string);
        }

        private ConversionWarning(boolean backup, boolean boldRedFormatting, String translationKeySuffix) {
            this.backup = backup;
            this.boldRedFormatting = boldRedFormatting;
            this.translationKeySuffix = translationKeySuffix;
        }

        public boolean promptsBackup() {
            return this.backup;
        }

        public boolean needsBoldRedFormatting() {
            return this.boldRedFormatting;
        }

        public String getTranslationKeySuffix() {
            return this.translationKeySuffix;
        }

        private static /* synthetic */ ConversionWarning[] method_36792() {
            return new ConversionWarning[]{NONE, DOWNGRADE, UPGRADE_TO_SNAPSHOT};
        }

        static {
            field_28443 = ConversionWarning.method_36792();
        }
    }
}

