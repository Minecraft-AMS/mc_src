/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.ints.Int2IntMap
 *  it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntAVLTreeSet
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntCollection
 *  it.unimi.dsi.fastutil.ints.IntIterator
 *  it.unimi.dsi.fastutil.ints.IntList
 *  it.unimi.dsi.fastutil.ints.IntListIterator
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.recipe;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.BitSet;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class RecipeMatcher {
    public final Int2IntMap inputs = new Int2IntOpenHashMap();

    public void addUnenchantedInput(ItemStack stack) {
        if (!(stack.isDamaged() || stack.hasEnchantments() || stack.hasCustomName())) {
            this.addInput(stack);
        }
    }

    public void addInput(ItemStack stack) {
        this.method_20478(stack, 64);
    }

    public void method_20478(ItemStack itemStack, int i) {
        if (!itemStack.isEmpty()) {
            int j = RecipeMatcher.getItemId(itemStack);
            int k = Math.min(i, itemStack.getCount());
            this.addInput(j, k);
        }
    }

    public static int getItemId(ItemStack stack) {
        return Registry.ITEM.getRawId(stack.getItem());
    }

    private boolean contains(int itemId) {
        return this.inputs.get(itemId) > 0;
    }

    private int consume(int itemId, int count) {
        int i = this.inputs.get(itemId);
        if (i >= count) {
            this.inputs.put(itemId, i - count);
            return itemId;
        }
        return 0;
    }

    private void addInput(int itemId, int count) {
        this.inputs.put(itemId, this.inputs.get(itemId) + count);
    }

    public boolean match(Recipe<?> recipe, @Nullable IntList output) {
        return this.match(recipe, output, 1);
    }

    public boolean match(Recipe<?> recipe, @Nullable IntList output, int multiplier) {
        return new Filter(recipe).find(multiplier, output);
    }

    public int countCrafts(Recipe<?> recipe, @Nullable IntList output) {
        return this.countCrafts(recipe, Integer.MAX_VALUE, output);
    }

    public int countCrafts(Recipe<?> recipe, int limit, @Nullable IntList output) {
        return new Filter(recipe).countCrafts(limit, output);
    }

    public static ItemStack getStackFromId(int itemId) {
        if (itemId == 0) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(Item.byRawId(itemId));
    }

    public void clear() {
        this.inputs.clear();
    }

    class Filter {
        private final Recipe<?> recipe;
        private final List<Ingredient> ingredients = Lists.newArrayList();
        private final int ingredientCount;
        private final int[] field_7551;
        private final int field_7553;
        private final BitSet field_7558;
        private final IntList field_7557 = new IntArrayList();

        public Filter(Recipe<?> recipe2) {
            this.recipe = recipe2;
            this.ingredients.addAll(recipe2.getIngredients());
            this.ingredients.removeIf(Ingredient::isEmpty);
            this.ingredientCount = this.ingredients.size();
            this.field_7551 = this.method_7422();
            this.field_7553 = this.field_7551.length;
            this.field_7558 = new BitSet(this.ingredientCount + this.field_7553 + this.ingredientCount + this.ingredientCount * this.field_7553);
            for (int i = 0; i < this.ingredients.size(); ++i) {
                IntList intList = this.ingredients.get(i).getMatchingItemIds();
                for (int j = 0; j < this.field_7553; ++j) {
                    if (!intList.contains(this.field_7551[j])) continue;
                    this.field_7558.set(this.method_7420(true, j, i));
                }
            }
        }

        public boolean find(int amount, @Nullable IntList outMatchingInputIds) {
            boolean bl2;
            if (amount <= 0) {
                return true;
            }
            int i = 0;
            while (this.method_7423(amount)) {
                RecipeMatcher.this.consume(this.field_7551[this.field_7557.getInt(0)], amount);
                int j = this.field_7557.size() - 1;
                this.method_7421(this.field_7557.getInt(j));
                for (int k = 0; k < j; ++k) {
                    this.method_7414((k & 1) == 0, this.field_7557.get(k), this.field_7557.get(k + 1));
                }
                this.field_7557.clear();
                this.field_7558.clear(0, this.ingredientCount + this.field_7553);
                ++i;
            }
            boolean bl = i == this.ingredientCount;
            boolean bl3 = bl2 = bl && outMatchingInputIds != null;
            if (bl2) {
                outMatchingInputIds.clear();
            }
            this.field_7558.clear(0, this.ingredientCount + this.field_7553 + this.ingredientCount);
            int l = 0;
            DefaultedList<Ingredient> list = this.recipe.getIngredients();
            for (int m = 0; m < list.size(); ++m) {
                if (bl2 && ((Ingredient)list.get(m)).isEmpty()) {
                    outMatchingInputIds.add(0);
                    continue;
                }
                for (int n = 0; n < this.field_7553; ++n) {
                    if (!this.method_7425(false, l, n)) continue;
                    this.method_7414(true, n, l);
                    RecipeMatcher.this.addInput(this.field_7551[n], amount);
                    if (!bl2) continue;
                    outMatchingInputIds.add(this.field_7551[n]);
                }
                ++l;
            }
            return bl;
        }

        private int[] method_7422() {
            IntAVLTreeSet intCollection = new IntAVLTreeSet();
            for (Ingredient ingredient : this.ingredients) {
                intCollection.addAll((IntCollection)ingredient.getMatchingItemIds());
            }
            IntIterator intIterator = intCollection.iterator();
            while (intIterator.hasNext()) {
                if (RecipeMatcher.this.contains(intIterator.nextInt())) continue;
                intIterator.remove();
            }
            return intCollection.toIntArray();
        }

        private boolean method_7423(int i) {
            int j = this.field_7553;
            for (int k = 0; k < j; ++k) {
                if (RecipeMatcher.this.inputs.get(this.field_7551[k]) < i) continue;
                this.method_7413(false, k);
                while (!this.field_7557.isEmpty()) {
                    int o;
                    int l = this.field_7557.size();
                    boolean bl = (l & 1) == 1;
                    int m = this.field_7557.getInt(l - 1);
                    if (!bl && !this.method_7416(m)) break;
                    int n = bl ? this.ingredientCount : j;
                    for (o = 0; o < n; ++o) {
                        if (this.method_7426(bl, o) || !this.method_7418(bl, m, o) || !this.method_7425(bl, m, o)) continue;
                        this.method_7413(bl, o);
                        break;
                    }
                    if ((o = this.field_7557.size()) != l) continue;
                    this.field_7557.removeInt(o - 1);
                }
                if (this.field_7557.isEmpty()) continue;
                return true;
            }
            return false;
        }

        private boolean method_7416(int i) {
            return this.field_7558.get(this.method_7419(i));
        }

        private void method_7421(int i) {
            this.field_7558.set(this.method_7419(i));
        }

        private int method_7419(int i) {
            return this.ingredientCount + this.field_7553 + i;
        }

        private boolean method_7418(boolean bl, int i, int j) {
            return this.field_7558.get(this.method_7420(bl, i, j));
        }

        private boolean method_7425(boolean bl, int i, int j) {
            return bl != this.field_7558.get(1 + this.method_7420(bl, i, j));
        }

        private void method_7414(boolean bl, int i, int j) {
            this.field_7558.flip(1 + this.method_7420(bl, i, j));
        }

        private int method_7420(boolean bl, int i, int j) {
            int k = bl ? i * this.ingredientCount + j : j * this.ingredientCount + i;
            return this.ingredientCount + this.field_7553 + this.ingredientCount + 2 * k;
        }

        private void method_7413(boolean bl, int i) {
            this.field_7558.set(this.method_7424(bl, i));
            this.field_7557.add(i);
        }

        private boolean method_7426(boolean bl, int i) {
            return this.field_7558.get(this.method_7424(bl, i));
        }

        private int method_7424(boolean bl, int i) {
            return (bl ? 0 : this.ingredientCount) + i;
        }

        public int countCrafts(int limit, @Nullable IntList outMatchingInputIds) {
            int k;
            int i = 0;
            int j = Math.min(limit, this.method_7415()) + 1;
            while (true) {
                if (this.find(k = (i + j) / 2, null)) {
                    if (j - i <= 1) break;
                    i = k;
                    continue;
                }
                j = k;
            }
            if (k > 0) {
                this.find(k, outMatchingInputIds);
            }
            return k;
        }

        private int method_7415() {
            int i = Integer.MAX_VALUE;
            for (Ingredient ingredient : this.ingredients) {
                int j = 0;
                IntListIterator intListIterator = ingredient.getMatchingItemIds().iterator();
                while (intListIterator.hasNext()) {
                    int k = (Integer)intListIterator.next();
                    j = Math.max(j, RecipeMatcher.this.inputs.get(k));
                }
                if (i <= 0) continue;
                i = Math.min(i, j);
            }
            return i;
        }
    }
}

