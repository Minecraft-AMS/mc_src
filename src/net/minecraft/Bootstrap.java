/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft;

import java.io.PrintStream;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.command.EntitySelectorOptions;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.tag.RequiredTagListRegistry;
import net.minecraft.util.Language;
import net.minecraft.util.logging.DebugLoggerPrintStream;
import net.minecraft.util.logging.LoggerPrintStream;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Bootstrap {
    public static final PrintStream SYSOUT = System.out;
    private static boolean initialized;
    private static final Logger LOGGER;

    public static void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        if (Registry.REGISTRIES.getIds().isEmpty()) {
            throw new IllegalStateException("Unable to load registries");
        }
        FireBlock.registerDefaultFlammables();
        ComposterBlock.registerDefaultCompostableItems();
        if (EntityType.getId(EntityType.PLAYER) == null) {
            throw new IllegalStateException("Failed loading EntityTypes");
        }
        BrewingRecipeRegistry.registerDefaults();
        EntitySelectorOptions.register();
        DispenserBehavior.registerDefaults();
        ArgumentTypes.register();
        RequiredTagListRegistry.validateRegistrations();
        Bootstrap.setOutputStreams();
    }

    private static <T> void collectMissingTranslations(Iterable<T> registry, Function<T, String> keyExtractor, Set<String> translationKeys) {
        Language language = Language.getInstance();
        registry.forEach(object -> {
            String string = (String)keyExtractor.apply(object);
            if (!language.hasTranslation(string)) {
                translationKeys.add(string);
            }
        });
    }

    private static void collectMissingGameRuleTranslations(final Set<String> translations) {
        final Language language = Language.getInstance();
        GameRules.accept(new GameRules.Visitor(){

            @Override
            public <T extends GameRules.Rule<T>> void visit(GameRules.Key<T> key, GameRules.Type<T> type) {
                if (!language.hasTranslation(key.getTranslationKey())) {
                    translations.add(key.getName());
                }
            }
        });
    }

    public static Set<String> getMissingTranslations() {
        TreeSet<String> set = new TreeSet<String>();
        Bootstrap.collectMissingTranslations(Registry.ATTRIBUTE, EntityAttribute::getTranslationKey, set);
        Bootstrap.collectMissingTranslations(Registry.ENTITY_TYPE, EntityType::getTranslationKey, set);
        Bootstrap.collectMissingTranslations(Registry.STATUS_EFFECT, StatusEffect::getTranslationKey, set);
        Bootstrap.collectMissingTranslations(Registry.ITEM, Item::getTranslationKey, set);
        Bootstrap.collectMissingTranslations(Registry.ENCHANTMENT, Enchantment::getTranslationKey, set);
        Bootstrap.collectMissingTranslations(Registry.BLOCK, Block::getTranslationKey, set);
        Bootstrap.collectMissingTranslations(Registry.CUSTOM_STAT, identifier -> "stat." + identifier.toString().replace(':', '.'), set);
        Bootstrap.collectMissingGameRuleTranslations(set);
        return set;
    }

    public static void logMissing() {
        if (!initialized) {
            throw new IllegalArgumentException("Not bootstrapped");
        }
        if (SharedConstants.isDevelopment) {
            Bootstrap.getMissingTranslations().forEach(string -> LOGGER.error("Missing translations: " + string));
            CommandManager.checkMissing();
        }
        DefaultAttributeRegistry.checkMissing();
    }

    private static void setOutputStreams() {
        if (LOGGER.isDebugEnabled()) {
            System.setErr(new DebugLoggerPrintStream("STDERR", System.err));
            System.setOut(new DebugLoggerPrintStream("STDOUT", SYSOUT));
        } else {
            System.setErr(new LoggerPrintStream("STDERR", System.err));
            System.setOut(new LoggerPrintStream("STDOUT", SYSOUT));
        }
    }

    public static void println(String str) {
        SYSOUT.println(str);
    }

    static {
        LOGGER = LogManager.getLogger();
    }
}

