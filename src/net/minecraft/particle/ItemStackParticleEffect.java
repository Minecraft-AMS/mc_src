/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.command.arguments.ItemStackArgument;
import net.minecraft.command.arguments.ItemStringReader;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class ItemStackParticleEffect
implements ParticleEffect {
    public static final ParticleEffect.Factory<ItemStackParticleEffect> PARAMETERS_FACTORY = new ParticleEffect.Factory<ItemStackParticleEffect>(){

        @Override
        public ItemStackParticleEffect read(ParticleType<ItemStackParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
            stringReader.expect(' ');
            ItemStringReader itemStringReader = new ItemStringReader(stringReader, false).consume();
            ItemStack itemStack = new ItemStackArgument(itemStringReader.getItem(), itemStringReader.getTag()).createStack(1, false);
            return new ItemStackParticleEffect(particleType, itemStack);
        }

        @Override
        public ItemStackParticleEffect read(ParticleType<ItemStackParticleEffect> particleType, PacketByteBuf packetByteBuf) {
            return new ItemStackParticleEffect(particleType, packetByteBuf.readItemStack());
        }

        @Override
        public /* synthetic */ ParticleEffect read(ParticleType type, PacketByteBuf buf) {
            return this.read(type, buf);
        }

        @Override
        public /* synthetic */ ParticleEffect read(ParticleType type, StringReader reader) throws CommandSyntaxException {
            return this.read(type, reader);
        }
    };
    private final ParticleType<ItemStackParticleEffect> type;
    private final ItemStack stack;

    public ItemStackParticleEffect(ParticleType<ItemStackParticleEffect> type, ItemStack stack) {
        this.type = type;
        this.stack = stack;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeItemStack(this.stack);
    }

    @Override
    public String asString() {
        return Registry.PARTICLE_TYPE.getId(this.getType()) + " " + new ItemStackArgument(this.stack.getItem(), this.stack.getTag()).asString();
    }

    public ParticleType<ItemStackParticleEffect> getType() {
        return this.type;
    }

    @Environment(value=EnvType.CLIENT)
    public ItemStack getItemStack() {
        return this.stack;
    }
}

