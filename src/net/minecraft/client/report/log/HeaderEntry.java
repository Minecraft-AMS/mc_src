/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.report.log;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.report.log.ChatLogEntry;
import net.minecraft.network.message.MessageHeader;
import net.minecraft.network.message.MessageSignatureData;

@Environment(value=EnvType.CLIENT)
public interface HeaderEntry
extends ChatLogEntry {
    public static Impl of(MessageHeader header, MessageSignatureData headerSignature, byte[] bodyDigest) {
        return new Impl(header, headerSignature, bodyDigest);
    }

    public MessageHeader header();

    public MessageSignatureData headerSignature();

    public byte[] bodyDigest();

    @Environment(value=EnvType.CLIENT)
    public record Impl(MessageHeader header, MessageSignatureData headerSignature, byte[] bodyDigest) implements HeaderEntry
    {
    }
}

