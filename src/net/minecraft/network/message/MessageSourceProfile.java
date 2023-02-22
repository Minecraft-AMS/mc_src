/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.network.message;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.UUID;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

public record MessageSourceProfile(UUID profileId, @Nullable PlayerPublicKey playerPublicKey) {
    public static final MessageSourceProfile NONE = new MessageSourceProfile(Util.NIL_UUID, null);

    public boolean lacksProfileId() {
        return NONE.equals(this);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{MessageSourceProfile.class, "profileId;profilePublicKey", "profileId", "playerPublicKey"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{MessageSourceProfile.class, "profileId;profilePublicKey", "profileId", "playerPublicKey"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{MessageSourceProfile.class, "profileId;profilePublicKey", "profileId", "playerPublicKey"}, this, object);
    }
}

