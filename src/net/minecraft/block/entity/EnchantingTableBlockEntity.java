/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.block.entity;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Nameable;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class EnchantingTableBlockEntity
extends BlockEntity
implements Nameable,
Tickable {
    public int ticks;
    public float nextPageAngle;
    public float pageAngle;
    public float field_11969;
    public float field_11967;
    public float nextPageTurningSpeed;
    public float pageTurningSpeed;
    public float field_11964;
    public float field_11963;
    public float field_11962;
    private static final Random RANDOM = new Random();
    private Text customName;

    public EnchantingTableBlockEntity() {
        super(BlockEntityType.ENCHANTING_TABLE);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (this.hasCustomName()) {
            nbt.putString("CustomName", Text.Serializer.toJson(this.customName));
        }
        return nbt;
    }

    @Override
    public void fromTag(BlockState state, NbtCompound tag) {
        super.fromTag(state, tag);
        if (tag.contains("CustomName", 8)) {
            this.customName = Text.Serializer.fromJson(tag.getString("CustomName"));
        }
    }

    @Override
    public void tick() {
        float g;
        this.pageTurningSpeed = this.nextPageTurningSpeed;
        this.field_11963 = this.field_11964;
        PlayerEntity playerEntity = this.world.getClosestPlayer((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5, 3.0, false);
        if (playerEntity != null) {
            double d = playerEntity.getX() - ((double)this.pos.getX() + 0.5);
            double e = playerEntity.getZ() - ((double)this.pos.getZ() + 0.5);
            this.field_11962 = (float)MathHelper.atan2(e, d);
            this.nextPageTurningSpeed += 0.1f;
            if (this.nextPageTurningSpeed < 0.5f || RANDOM.nextInt(40) == 0) {
                float f = this.field_11969;
                do {
                    this.field_11969 += (float)(RANDOM.nextInt(4) - RANDOM.nextInt(4));
                } while (f == this.field_11969);
            }
        } else {
            this.field_11962 += 0.02f;
            this.nextPageTurningSpeed -= 0.1f;
        }
        while (this.field_11964 >= (float)Math.PI) {
            this.field_11964 -= (float)Math.PI * 2;
        }
        while (this.field_11964 < (float)(-Math.PI)) {
            this.field_11964 += (float)Math.PI * 2;
        }
        while (this.field_11962 >= (float)Math.PI) {
            this.field_11962 -= (float)Math.PI * 2;
        }
        while (this.field_11962 < (float)(-Math.PI)) {
            this.field_11962 += (float)Math.PI * 2;
        }
        for (g = this.field_11962 - this.field_11964; g >= (float)Math.PI; g -= (float)Math.PI * 2) {
        }
        while (g < (float)(-Math.PI)) {
            g += (float)Math.PI * 2;
        }
        this.field_11964 += g * 0.4f;
        this.nextPageTurningSpeed = MathHelper.clamp(this.nextPageTurningSpeed, 0.0f, 1.0f);
        ++this.ticks;
        this.pageAngle = this.nextPageAngle;
        float h = (this.field_11969 - this.nextPageAngle) * 0.4f;
        float i = 0.2f;
        h = MathHelper.clamp(h, -0.2f, 0.2f);
        this.field_11967 += (h - this.field_11967) * 0.9f;
        this.nextPageAngle += this.field_11967;
    }

    @Override
    public Text getName() {
        if (this.customName != null) {
            return this.customName;
        }
        return new TranslatableText("container.enchant");
    }

    public void setCustomName(@Nullable Text value) {
        this.customName = value;
    }

    @Override
    @Nullable
    public Text getCustomName() {
        return this.customName;
    }
}

