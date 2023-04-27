/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.report;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public final class AbuseReportReason
extends Enum<AbuseReportReason> {
    public static final /* enum */ AbuseReportReason HATE_SPEECH = new AbuseReportReason("hate_speech");
    public static final /* enum */ AbuseReportReason TERRORISM_OR_VIOLENT_EXTREMISM = new AbuseReportReason("terrorism_or_violent_extremism");
    public static final /* enum */ AbuseReportReason CHILD_SEXUAL_EXPLOITATION_OR_ABUSE = new AbuseReportReason("child_sexual_exploitation_or_abuse");
    public static final /* enum */ AbuseReportReason IMMINENT_HARM = new AbuseReportReason("imminent_harm");
    public static final /* enum */ AbuseReportReason NON_CONSENSUAL_INTIMATE_IMAGERY = new AbuseReportReason("non_consensual_intimate_imagery");
    public static final /* enum */ AbuseReportReason HARASSMENT_OR_BULLYING = new AbuseReportReason("harassment_or_bullying");
    public static final /* enum */ AbuseReportReason DEFAMATION_IMPERSONATION_FALSE_INFORMATION = new AbuseReportReason("defamation_impersonation_false_information");
    public static final /* enum */ AbuseReportReason SELF_HARM_OR_SUICIDE = new AbuseReportReason("self_harm_or_suicide");
    public static final /* enum */ AbuseReportReason ALCOHOL_TOBACCO_DRUGS = new AbuseReportReason("alcohol_tobacco_drugs");
    private final String id;
    private final Text text;
    private final Text description;
    private static final /* synthetic */ AbuseReportReason[] field_39674;

    public static AbuseReportReason[] values() {
        return (AbuseReportReason[])field_39674.clone();
    }

    public static AbuseReportReason valueOf(String string) {
        return Enum.valueOf(AbuseReportReason.class, string);
    }

    private AbuseReportReason(String id) {
        this.id = id.toUpperCase(Locale.ROOT);
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

    private static /* synthetic */ AbuseReportReason[] method_44597() {
        return new AbuseReportReason[]{HATE_SPEECH, TERRORISM_OR_VIOLENT_EXTREMISM, CHILD_SEXUAL_EXPLOITATION_OR_ABUSE, IMMINENT_HARM, NON_CONSENSUAL_INTIMATE_IMAGERY, HARASSMENT_OR_BULLYING, DEFAMATION_IMPERSONATION_FALSE_INFORMATION, SELF_HARM_OR_SUICIDE, ALCOHOL_TOBACCO_DRUGS};
    }

    static {
        field_39674 = AbuseReportReason.method_44597();
    }
}

