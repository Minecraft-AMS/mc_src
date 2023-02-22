/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.TextureStitcherCannotFitException;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class TextureStitcher {
    private static final Comparator<Holder> comparator = Comparator.comparing(holder -> -holder.height).thenComparing(holder -> -holder.width).thenComparing(holder -> holder.sprite.getId());
    private final int mipLevel;
    private final Set<Holder> holders = Sets.newHashSetWithExpectedSize((int)256);
    private final List<Slot> slots = Lists.newArrayListWithCapacity((int)256);
    private int width;
    private int height;
    private final int maxWidth;
    private final int maxHeight;

    public TextureStitcher(int maxWidth, int maxHeight, int mipLevel) {
        this.mipLevel = mipLevel;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void add(Sprite sprite) {
        Holder holder = new Holder(sprite, this.mipLevel);
        this.holders.add(holder);
    }

    public void stitch() {
        ArrayList list = Lists.newArrayList(this.holders);
        list.sort(comparator);
        for (Holder holder2 : list) {
            if (this.fit(holder2)) continue;
            throw new TextureStitcherCannotFitException(holder2.sprite, (Collection)list.stream().map(holder -> holder.sprite).collect(ImmutableList.toImmutableList()));
        }
        this.width = MathHelper.smallestEncompassingPowerOfTwo(this.width);
        this.height = MathHelper.smallestEncompassingPowerOfTwo(this.height);
    }

    public List<Sprite> getStitchedSprites() {
        ArrayList list = Lists.newArrayList();
        for (Slot slot2 : this.slots) {
            slot2.addAllFilledSlots(slot -> {
                Holder holder = slot.getTexture();
                Sprite sprite = holder.sprite;
                sprite.init(this.width, this.height, slot.getX(), slot.getY());
                list.add(sprite);
            });
        }
        return list;
    }

    private static int applyMipLevel(int size, int mipLevel) {
        return (size >> mipLevel) + ((size & (1 << mipLevel) - 1) == 0 ? 0 : 1) << mipLevel;
    }

    private boolean fit(Holder holder) {
        for (Slot slot : this.slots) {
            if (!slot.tryFit(holder)) continue;
            return true;
        }
        return this.method_4552(holder);
    }

    private boolean method_4552(Holder holder) {
        Slot slot;
        boolean bl5;
        boolean bl4;
        boolean bl2;
        int i = MathHelper.smallestEncompassingPowerOfTwo(this.width);
        int j = MathHelper.smallestEncompassingPowerOfTwo(this.height);
        int k = MathHelper.smallestEncompassingPowerOfTwo(this.width + holder.width);
        int l = MathHelper.smallestEncompassingPowerOfTwo(this.height + holder.height);
        boolean bl = k <= this.maxWidth;
        boolean bl3 = bl2 = l <= this.maxHeight;
        if (!bl && !bl2) {
            return false;
        }
        boolean bl32 = bl && i != k;
        boolean bl6 = bl4 = bl2 && j != l;
        if (bl32 ^ bl4) {
            bl5 = bl32;
        } else {
            boolean bl7 = bl5 = bl && i <= j;
        }
        if (bl5) {
            if (this.height == 0) {
                this.height = holder.height;
            }
            slot = new Slot(this.width, 0, holder.width, this.height);
            this.width += holder.width;
        } else {
            slot = new Slot(0, this.height, this.width, holder.height);
            this.height += holder.height;
        }
        slot.tryFit(holder);
        this.slots.add(slot);
        return true;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Slot {
        private final int x;
        private final int y;
        private final int width;
        private final int height;
        private List<Slot> subSlots;
        private Holder texture;

        public Slot(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public Holder getTexture() {
            return this.texture;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public boolean tryFit(Holder holder) {
            if (this.texture != null) {
                return false;
            }
            int i = holder.width;
            int j = holder.height;
            if (i > this.width || j > this.height) {
                return false;
            }
            if (i == this.width && j == this.height) {
                this.texture = holder;
                return true;
            }
            if (this.subSlots == null) {
                this.subSlots = Lists.newArrayListWithCapacity((int)1);
                this.subSlots.add(new Slot(this.x, this.y, i, j));
                int k = this.width - i;
                int l = this.height - j;
                if (l > 0 && k > 0) {
                    int n;
                    int m = Math.max(this.height, k);
                    if (m >= (n = Math.max(this.width, l))) {
                        this.subSlots.add(new Slot(this.x, this.y + j, i, l));
                        this.subSlots.add(new Slot(this.x + i, this.y, k, this.height));
                    } else {
                        this.subSlots.add(new Slot(this.x + i, this.y, k, j));
                        this.subSlots.add(new Slot(this.x, this.y + j, this.width, l));
                    }
                } else if (k == 0) {
                    this.subSlots.add(new Slot(this.x, this.y + j, i, l));
                } else if (l == 0) {
                    this.subSlots.add(new Slot(this.x + i, this.y, k, j));
                }
            }
            for (Slot slot : this.subSlots) {
                if (!slot.tryFit(holder)) continue;
                return true;
            }
            return false;
        }

        public void addAllFilledSlots(Consumer<Slot> consumer) {
            if (this.texture != null) {
                consumer.accept(this);
            } else if (this.subSlots != null) {
                for (Slot slot : this.subSlots) {
                    slot.addAllFilledSlots(consumer);
                }
            }
        }

        public String toString() {
            return "Slot{originX=" + this.x + ", originY=" + this.y + ", width=" + this.width + ", height=" + this.height + ", texture=" + this.texture + ", subSlots=" + this.subSlots + '}';
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Holder {
        public final Sprite sprite;
        public final int width;
        public final int height;

        public Holder(Sprite sprite, int mipLevel) {
            this.sprite = sprite;
            this.width = TextureStitcher.applyMipLevel(sprite.getWidth(), mipLevel);
            this.height = TextureStitcher.applyMipLevel(sprite.getHeight(), mipLevel);
        }

        public String toString() {
            return "Holder{width=" + this.width + ", height=" + this.height + '}';
        }
    }
}
