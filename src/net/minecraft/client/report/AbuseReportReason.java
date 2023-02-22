/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.report;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public final class AbuseReportReason
extends Enum<AbuseReportReason> {
    public static final /* enum */ AbuseReportReason FALSE_REPORTING = new AbuseReportReason(2, "false_reporting", false);
    public static final /* enum */ AbuseReportReason HATE_SPEECH = new AbuseReportReason(5, "hate_speech", true);
    public static final /* enum */ AbuseReportReason TERRORISM_OR_VIOLENT_EXTREMISM = new AbuseReportReason(16, "terrorism_or_violent_extremism", true);
    public static final /* enum */ AbuseReportReason CHILD_SEXUAL_EXPLOITATION_OR_ABUSE = new AbuseReportReason(17, "child_sexual_exploitation_or_abuse", true);
    public static final /* enum */ AbuseReportReason IMMINENT_HARM = new AbuseReportReason(18, "imminent_harm", true);
    public static final /* enum */ AbuseReportReason NON_CONSENSUAL_INTIMATE_IMAGERY = new AbuseReportReason(19, "non_consensual_intimate_imagery", true);
    public static final /* enum */ AbuseReportReason HARASSMENT_OR_BULLYING = new AbuseReportReason(21, "harassment_or_bullying", true);
    public static final /* enum */ AbuseReportReason DEFAMATION_IMPERSONATION_FALSE_INFORMATION = new AbuseReportReason(27, "defamation_impersonation_false_information", true);
    public static final /* enum */ AbuseReportReason SELF_HARM_OR_SUICIDE = new AbuseReportReason(31, "self_harm_or_suicide", true);
    public static final /* enum */ AbuseReportReason ALCOHOL_TOBACCO_DRUGS = new AbuseReportReason(39, "alcohol_tobacco_drugs", true);
    private final int banReasonId;
    private final String id;
    private final boolean reportable;
    private final Text text;
    private final Text description;
    private static final /* synthetic */ AbuseReportReason[] field_39674;

    public static AbuseReportReason[] values() {
        return (AbuseReportReason[])field_39674.clone();
    }

    public static AbuseReportReason valueOf(String string) {
        return Enum.valueOf(AbuseReportReason.class, string);
    }

    private AbuseReportReason(int banReasonId, String id, boolean reportable) {
        this.banReasonId = banReasonId;
        this.id = id.toUpperCase(Locale.ROOT);
        this.reportable = reportable;
        String string2 = "gui.abuseReport.reason." + id;
        this.text = Text.translatable(string2);
        this.description = Text.translatable(string2 + ".description");
    }

    public String getId() {
        return this.id;
    }

    public Text getText() {
        return this.text;
    }

    public Text getDescription() {
        return this.description;
    }

    public boolean isReportable() {
        return this.reportable;
    }

    @Nullable
    public static Text getText(int banReasonId) {
        for (AbuseReportReason abuseReportReason : AbuseReportReason.values()) {
            if (abuseReportReason.banReasonId != banReasonId) continue;
            return abuseReportReason.text;
        }
        return null;
    }

    private static /* synthetic */ AbuseReportReason[] method_44597() {
        return new AbuseReportReason[]{FALSE_REPORTING, HATE_SPEECH, TERRORISM_OR_VIOLENT_EXTREMISM, CHILD_SEXUAL_EXPLOITATION_OR_ABUSE, IMMINENT_HARM, NON_CONSENSUAL_INTIMATE_IMAGERY, HARASSMENT_OR_BULLYING, DEFAMATION_IMPERSONATION_FALSE_INFORMATION, SELF_HARM_OR_SUICIDE, ALCOHOL_TOBACCO_DRUGS};
    }

    static {
        field_39674 = AbuseReportReason.method_44597();
    }
}

