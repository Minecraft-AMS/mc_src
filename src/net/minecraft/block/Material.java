/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;

public final class Material {
    public static final Material AIR = Builder.method_15808(new Builder(MapColor.CLEAR).allowsMovement()).notSolid().replaceable().build();
    public static final Material STRUCTURE_VOID = Builder.method_15808(new Builder(MapColor.CLEAR).allowsMovement()).notSolid().replaceable().build();
    public static final Material PORTAL = Builder.method_15808(new Builder(MapColor.CLEAR).allowsMovement()).notSolid().blocksPistons().build();
    public static final Material CARPET = Builder.method_15808(new Builder(MapColor.WHITE_GRAY).allowsMovement()).notSolid().burnable().build();
    public static final Material PLANT = Builder.method_15808(new Builder(MapColor.DARK_GREEN).allowsMovement()).notSolid().destroyedByPiston().build();
    public static final Material UNDERWATER_PLANT = Builder.method_15808(new Builder(MapColor.WATER_BLUE).allowsMovement()).notSolid().destroyedByPiston().build();
    public static final Material REPLACEABLE_PLANT = Builder.method_15808(new Builder(MapColor.DARK_GREEN).allowsMovement()).notSolid().destroyedByPiston().replaceable().burnable().build();
    public static final Material NETHER_SHOOTS = Builder.method_15808(new Builder(MapColor.DARK_GREEN).allowsMovement()).notSolid().destroyedByPiston().replaceable().build();
    public static final Material REPLACEABLE_UNDERWATER_PLANT = Builder.method_15808(new Builder(MapColor.WATER_BLUE).allowsMovement()).notSolid().destroyedByPiston().replaceable().build();
    public static final Material WATER = Builder.method_15808(new Builder(MapColor.WATER_BLUE).allowsMovement()).notSolid().destroyedByPiston().replaceable().liquid().build();
    public static final Material BUBBLE_COLUMN = Builder.method_15808(new Builder(MapColor.WATER_BLUE).allowsMovement()).notSolid().destroyedByPiston().replaceable().liquid().build();
    public static final Material LAVA = Builder.method_15808(new Builder(MapColor.BRIGHT_RED).allowsMovement()).notSolid().destroyedByPiston().replaceable().liquid().build();
    public static final Material SNOW_LAYER = Builder.method_15808(new Builder(MapColor.WHITE).allowsMovement()).notSolid().destroyedByPiston().replaceable().build();
    public static final Material FIRE = Builder.method_15808(new Builder(MapColor.CLEAR).allowsMovement()).notSolid().destroyedByPiston().replaceable().build();
    public static final Material DECORATION = Builder.method_15808(new Builder(MapColor.CLEAR).allowsMovement()).notSolid().destroyedByPiston().build();
    public static final Material COBWEB = Builder.method_15808(new Builder(MapColor.WHITE_GRAY).allowsMovement()).destroyedByPiston().build();
    public static final Material REDSTONE_LAMP = new Builder(MapColor.CLEAR).build();
    public static final Material ORGANIC_PRODUCT = new Builder(MapColor.LIGHT_BLUE_GRAY).build();
    public static final Material SOIL = new Builder(MapColor.DIRT_BROWN).build();
    public static final Material SOLID_ORGANIC = new Builder(MapColor.PALE_GREEN).build();
    public static final Material DENSE_ICE = new Builder(MapColor.PALE_PURPLE).build();
    public static final Material AGGREGATE = new Builder(MapColor.PALE_YELLOW).build();
    public static final Material SPONGE = new Builder(MapColor.YELLOW).build();
    public static final Material SHULKER_BOX = new Builder(MapColor.PURPLE).build();
    public static final Material WOOD = new Builder(MapColor.OAK_TAN).burnable().build();
    public static final Material NETHER_WOOD = new Builder(MapColor.OAK_TAN).build();
    public static final Material BAMBOO_SAPLING = new Builder(MapColor.OAK_TAN).burnable().destroyedByPiston().allowsMovement().build();
    public static final Material BAMBOO = new Builder(MapColor.OAK_TAN).burnable().destroyedByPiston().build();
    public static final Material WOOL = new Builder(MapColor.WHITE_GRAY).burnable().build();
    public static final Material TNT = Builder.method_15808(new Builder(MapColor.BRIGHT_RED).burnable()).build();
    public static final Material LEAVES = Builder.method_15808(new Builder(MapColor.DARK_GREEN).burnable()).destroyedByPiston().build();
    public static final Material GLASS = Builder.method_15808(new Builder(MapColor.CLEAR)).build();
    public static final Material ICE = Builder.method_15808(new Builder(MapColor.PALE_PURPLE)).build();
    public static final Material CACTUS = Builder.method_15808(new Builder(MapColor.DARK_GREEN)).destroyedByPiston().build();
    public static final Material STONE = new Builder(MapColor.STONE_GRAY).build();
    public static final Material METAL = new Builder(MapColor.IRON_GRAY).build();
    public static final Material SNOW_BLOCK = new Builder(MapColor.WHITE).build();
    public static final Material REPAIR_STATION = new Builder(MapColor.IRON_GRAY).blocksPistons().build();
    public static final Material BARRIER = new Builder(MapColor.CLEAR).blocksPistons().build();
    public static final Material PISTON = new Builder(MapColor.STONE_GRAY).blocksPistons().build();
    public static final Material MOSS_BLOCK = new Builder(MapColor.DARK_GREEN).destroyedByPiston().build();
    public static final Material GOURD = new Builder(MapColor.DARK_GREEN).destroyedByPiston().build();
    public static final Material EGG = new Builder(MapColor.DARK_GREEN).destroyedByPiston().build();
    public static final Material CAKE = new Builder(MapColor.CLEAR).destroyedByPiston().build();
    private final MapColor color;
    private final PistonBehavior pistonBehavior;
    private final boolean blocksMovement;
    private final boolean burnable;
    private final boolean liquid;
    private final boolean blocksLight;
    private final boolean replaceable;
    private final boolean solid;

    public Material(MapColor color, boolean liquid, boolean solid, boolean blocksMovement, boolean blocksLight, boolean breakByHand, boolean burnable, PistonBehavior pistonBehavior) {
        this.color = color;
        this.liquid = liquid;
        this.solid = solid;
        this.blocksMovement = blocksMovement;
        this.blocksLight = blocksLight;
        this.burnable = breakByHand;
        this.replaceable = burnable;
        this.pistonBehavior = pistonBehavior;
    }

    public boolean isLiquid() {
        return this.liquid;
    }

    public boolean isSolid() {
        return this.solid;
    }

    public boolean blocksMovement() {
        return this.blocksMovement;
    }

    public boolean isBurnable() {
        return this.burnable;
    }

    public boolean isReplaceable() {
        return this.replaceable;
    }

    public boolean blocksLight() {
        return this.blocksLight;
    }

    public PistonBehavior getPistonBehavior() {
        return this.pistonBehavior;
    }

    public MapColor getColor() {
        return this.color;
    }

    public static class Builder {
        private PistonBehavior pistonBehavior = PistonBehavior.NORMAL;
        private boolean blocksMovement = true;
        private boolean burnable;
        private boolean liquid;
        private boolean replaceable;
        private boolean solid = true;
        private final MapColor color;
        private boolean blocksLight = true;

        public Builder(MapColor color) {
            this.color = color;
        }

        public Builder liquid() {
            this.liquid = true;
            return this;
        }

        public Builder notSolid() {
            this.solid = false;
            return this;
        }

        public Builder allowsMovement() {
            this.blocksMovement = false;
            return this;
        }

        private Builder lightPassesThrough() {
            this.blocksLight = false;
            return this;
        }

        protected Builder burnable() {
            this.burnable = true;
            return this;
        }

        public Builder replaceable() {
            this.replaceable = true;
            return this;
        }

        protected Builder destroyedByPiston() {
            this.pistonBehavior = PistonBehavior.DESTROY;
            return this;
        }

        protected Builder blocksPistons() {
            this.pistonBehavior = PistonBehavior.BLOCK;
            return this;
        }

        public Material build() {
            return new Material(this.color, this.liquid, this.solid, this.blocksMovement, this.blocksLight, this.burnable, this.replaceable, this.pistonBehavior);
        }

        static /* synthetic */ Builder method_15808(Builder builder) {
            return builder.lightPassesThrough();
        }
    }
}

