/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.util.UUIDTypeAdapter
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.util;

import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class Session {
    private final String username;
    private final String uuid;
    private final String accessToken;
    private final AccountType accountType;

    public Session(String username, String uuid, String accessToken, String accountType) {
        this.username = username;
        this.uuid = uuid;
        this.accessToken = accessToken;
        this.accountType = AccountType.byName(accountType);
    }

    public String getSessionId() {
        return "token:" + this.accessToken + ":" + this.uuid;
    }

    public String getUuid() {
        return this.uuid;
    }

    public String getUsername() {
        return this.username;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public GameProfile getProfile() {
        try {
            UUID uUID = UUIDTypeAdapter.fromString((String)this.getUuid());
            return new GameProfile(uUID, this.getUsername());
        }
        catch (IllegalArgumentException illegalArgumentException) {
            return new GameProfile(null, this.getUsername());
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum AccountType {
        LEGACY("legacy"),
        MOJANG("mojang");

        private static final Map<String, AccountType> field_1989;
        private final String field_1986;

        private AccountType(String string2) {
            this.field_1986 = string2;
        }

        @Nullable
        public static AccountType byName(String string) {
            return field_1989.get(string.toLowerCase(Locale.ROOT));
        }

        static {
            field_1989 = Arrays.stream(AccountType.values()).collect(Collectors.toMap(accountType -> accountType.field_1986, Function.identity()));
        }
    }
}

