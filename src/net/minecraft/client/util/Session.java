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
import java.util.Optional;
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
    private final Optional<String> xuid;
    private final Optional<String> clientId;
    private final AccountType accountType;

    public Session(String username, String uuid, String accessToken, Optional<String> xuid, Optional<String> clientId, AccountType accountType) {
        this.username = username;
        this.uuid = uuid;
        this.accessToken = accessToken;
        this.xuid = xuid;
        this.clientId = clientId;
        this.accountType = accountType;
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

    public Optional<String> getClientId() {
        return this.clientId;
    }

    public Optional<String> getXuid() {
        return this.xuid;
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

    public AccountType getAccountType() {
        return this.accountType;
    }

    @Environment(value=EnvType.CLIENT)
    public static final class AccountType
    extends Enum<AccountType> {
        public static final /* enum */ AccountType LEGACY = new AccountType("legacy");
        public static final /* enum */ AccountType MOJANG = new AccountType("mojang");
        public static final /* enum */ AccountType MSA = new AccountType("msa");
        private static final Map<String, AccountType> BY_NAME;
        private final String name;
        private static final /* synthetic */ AccountType[] field_1987;

        public static AccountType[] values() {
            return (AccountType[])field_1987.clone();
        }

        public static AccountType valueOf(String string) {
            return Enum.valueOf(AccountType.class, string);
        }

        private AccountType(String name) {
            this.name = name;
        }

        @Nullable
        public static AccountType byName(String name) {
            return BY_NAME.get(name.toLowerCase(Locale.ROOT));
        }

        public String getName() {
            return this.name;
        }

        private static /* synthetic */ AccountType[] method_36868() {
            return new AccountType[]{LEGACY, MOJANG, MSA};
        }

        static {
            field_1987 = AccountType.method_36868();
            BY_NAME = Arrays.stream(AccountType.values()).collect(Collectors.toMap(type -> type.name, Function.identity()));
        }
    }
}

