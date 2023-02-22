/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.boss;

import java.util.UUID;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public abstract class BossBar {
    private final UUID uuid;
    protected Text name;
    protected float percent;
    protected Color color;
    protected Style style;
    protected boolean darkenSky;
    protected boolean dragonMusic;
    protected boolean thickenFog;

    public BossBar(UUID uuid, Text name, Color color, Style style) {
        this.uuid = uuid;
        this.name = name;
        this.color = color;
        this.style = style;
        this.percent = 1.0f;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public Text getName() {
        return this.name;
    }

    public void setName(Text name) {
        this.name = name;
    }

    public float getPercent() {
        return this.percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Style getStyle() {
        return this.style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public boolean shouldDarkenSky() {
        return this.darkenSky;
    }

    public BossBar setDarkenSky(boolean darkenSky) {
        this.darkenSky = darkenSky;
        return this;
    }

    public boolean hasDragonMusic() {
        return this.dragonMusic;
    }

    public BossBar setDragonMusic(boolean dragonMusic) {
        this.dragonMusic = dragonMusic;
        return this;
    }

    public BossBar setThickenFog(boolean thickenFog) {
        this.thickenFog = thickenFog;
        return this;
    }

    public boolean shouldThickenFog() {
        return this.thickenFog;
    }

    public static final class Color
    extends Enum<Color> {
        public static final /* enum */ Color PINK = new Color("pink", Formatting.RED);
        public static final /* enum */ Color BLUE = new Color("blue", Formatting.BLUE);
        public static final /* enum */ Color RED = new Color("red", Formatting.DARK_RED);
        public static final /* enum */ Color GREEN = new Color("green", Formatting.GREEN);
        public static final /* enum */ Color YELLOW = new Color("yellow", Formatting.YELLOW);
        public static final /* enum */ Color PURPLE = new Color("purple", Formatting.DARK_BLUE);
        public static final /* enum */ Color WHITE = new Color("white", Formatting.WHITE);
        private final String name;
        private final Formatting format;
        private static final /* synthetic */ Color[] field_5789;

        public static Color[] values() {
            return (Color[])field_5789.clone();
        }

        public static Color valueOf(String string) {
            return Enum.valueOf(Color.class, string);
        }

        private Color(String name, Formatting format) {
            this.name = name;
            this.format = format;
        }

        public Formatting getTextFormat() {
            return this.format;
        }

        public String getName() {
            return this.name;
        }

        public static Color byName(String name) {
            for (Color color : Color.values()) {
                if (!color.name.equals(name)) continue;
                return color;
            }
            return WHITE;
        }

        private static /* synthetic */ Color[] method_36595() {
            return new Color[]{PINK, BLUE, RED, GREEN, YELLOW, PURPLE, WHITE};
        }

        static {
            field_5789 = Color.method_36595();
        }
    }

    public static final class Style
    extends Enum<Style> {
        public static final /* enum */ Style PROGRESS = new Style("progress");
        public static final /* enum */ Style NOTCHED_6 = new Style("notched_6");
        public static final /* enum */ Style NOTCHED_10 = new Style("notched_10");
        public static final /* enum */ Style NOTCHED_12 = new Style("notched_12");
        public static final /* enum */ Style NOTCHED_20 = new Style("notched_20");
        private final String name;
        private static final /* synthetic */ Style[] field_5792;

        public static Style[] values() {
            return (Style[])field_5792.clone();
        }

        public static Style valueOf(String string) {
            return Enum.valueOf(Style.class, string);
        }

        private Style(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public static Style byName(String name) {
            for (Style style : Style.values()) {
                if (!style.name.equals(name)) continue;
                return style;
            }
            return PROGRESS;
        }

        private static /* synthetic */ Style[] method_36596() {
            return new Style[]{PROGRESS, NOTCHED_6, NOTCHED_10, NOTCHED_12, NOTCHED_20};
        }

        static {
            field_5792 = Style.method_36596();
        }
    }
}

