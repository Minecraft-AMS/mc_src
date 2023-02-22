/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.tutorial;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.tutorial.CraftPlanksTutorialStepHandler;
import net.minecraft.client.tutorial.FindTreeTutorialStepHandler;
import net.minecraft.client.tutorial.MovementTutorialStepHandler;
import net.minecraft.client.tutorial.NoneTutorialStepHandler;
import net.minecraft.client.tutorial.OpenInventoryTutorialStepHandler;
import net.minecraft.client.tutorial.PunchTreeTutorialStepHandler;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.client.tutorial.TutorialStepHandler;

@Environment(value=EnvType.CLIENT)
public final class TutorialStep
extends Enum<TutorialStep> {
    public static final /* enum */ TutorialStep MOVEMENT = new TutorialStep("movement", MovementTutorialStepHandler::new);
    public static final /* enum */ TutorialStep FIND_TREE = new TutorialStep("find_tree", FindTreeTutorialStepHandler::new);
    public static final /* enum */ TutorialStep PUNCH_TREE = new TutorialStep("punch_tree", PunchTreeTutorialStepHandler::new);
    public static final /* enum */ TutorialStep OPEN_INVENTORY = new TutorialStep("open_inventory", OpenInventoryTutorialStepHandler::new);
    public static final /* enum */ TutorialStep CRAFT_PLANKS = new TutorialStep("craft_planks", CraftPlanksTutorialStepHandler::new);
    public static final /* enum */ TutorialStep NONE = new TutorialStep("none", NoneTutorialStepHandler::new);
    private final String name;
    private final Function<TutorialManager, ? extends TutorialStepHandler> handlerFactory;
    private static final /* synthetic */ TutorialStep[] field_5654;

    public static TutorialStep[] values() {
        return (TutorialStep[])field_5654.clone();
    }

    public static TutorialStep valueOf(String string) {
        return Enum.valueOf(TutorialStep.class, string);
    }

    private <T extends TutorialStepHandler> TutorialStep(String name, Function<TutorialManager, T> factory) {
        this.name = name;
        this.handlerFactory = factory;
    }

    public TutorialStepHandler createHandler(TutorialManager manager) {
        return this.handlerFactory.apply(manager);
    }

    public String getName() {
        return this.name;
    }

    public static TutorialStep byName(String name) {
        for (TutorialStep tutorialStep : TutorialStep.values()) {
            if (!tutorialStep.name.equals(name)) continue;
            return tutorialStep;
        }
        return NONE;
    }

    private static /* synthetic */ TutorialStep[] method_36929() {
        return new TutorialStep[]{MOVEMENT, FIND_TREE, PUNCH_TREE, OPEN_INVENTORY, CRAFT_PLANKS, NONE};
    }

    static {
        field_5654 = TutorialStep.method_36929();
    }
}

