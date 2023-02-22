/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.server;

import com.google.gson.JsonObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.minecraft.server.ServerConfigEntry;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public abstract class BanEntry<T>
extends ServerConfigEntry<T> {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    protected final Date creationDate;
    protected final String source;
    protected final Date expiryDate;
    protected final String reason;

    public BanEntry(T object, @Nullable Date creationDate, @Nullable String source, @Nullable Date expiryDate, @Nullable String reason) {
        super(object);
        this.creationDate = creationDate == null ? new Date() : creationDate;
        this.source = source == null ? "(Unknown)" : source;
        this.expiryDate = expiryDate;
        this.reason = reason == null ? "Banned by an operator." : reason;
    }

    protected BanEntry(T object, JsonObject jsonObject) {
        super(object, jsonObject);
        Date date2;
        Date date;
        try {
            date = jsonObject.has("created") ? DATE_FORMAT.parse(jsonObject.get("created").getAsString()) : new Date();
        }
        catch (ParseException parseException) {
            date = new Date();
        }
        this.creationDate = date;
        this.source = jsonObject.has("source") ? jsonObject.get("source").getAsString() : "(Unknown)";
        try {
            date2 = jsonObject.has("expires") ? DATE_FORMAT.parse(jsonObject.get("expires").getAsString()) : null;
        }
        catch (ParseException parseException2) {
            date2 = null;
        }
        this.expiryDate = date2;
        this.reason = jsonObject.has("reason") ? jsonObject.get("reason").getAsString() : "Banned by an operator.";
    }

    public String getSource() {
        return this.source;
    }

    public Date getExpiryDate() {
        return this.expiryDate;
    }

    public String getReason() {
        return this.reason;
    }

    public abstract Text toText();

    @Override
    boolean isInvalid() {
        if (this.expiryDate == null) {
            return false;
        }
        return this.expiryDate.before(new Date());
    }

    @Override
    protected void serialize(JsonObject jsonObject) {
        jsonObject.addProperty("created", DATE_FORMAT.format(this.creationDate));
        jsonObject.addProperty("source", this.source);
        jsonObject.addProperty("expires", this.expiryDate == null ? "forever" : DATE_FORMAT.format(this.expiryDate));
        jsonObject.addProperty("reason", this.reason);
    }
}
