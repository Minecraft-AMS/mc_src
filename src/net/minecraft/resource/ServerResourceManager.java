/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.function.FunctionLoader;
import net.minecraft.tag.TagManager;
import net.minecraft.tag.TagManagerLoader;
import net.minecraft.util.Unit;

public class ServerResourceManager
implements AutoCloseable {
    private static final CompletableFuture<Unit> field_25334 = CompletableFuture.completedFuture(Unit.INSTANCE);
    private final ReloadableResourceManager resourceManager = new ReloadableResourceManagerImpl(ResourceType.SERVER_DATA);
    private final CommandManager commandManager;
    private final RecipeManager recipeManager = new RecipeManager();
    private final TagManagerLoader registryTagManager = new TagManagerLoader();
    private final LootConditionManager lootConditionManager = new LootConditionManager();
    private final LootManager lootManager = new LootManager(this.lootConditionManager);
    private final ServerAdvancementLoader serverAdvancementLoader = new ServerAdvancementLoader(this.lootConditionManager);
    private final FunctionLoader functionLoader;

    public ServerResourceManager(CommandManager.RegistrationEnvironment registrationEnvironment, int i) {
        this.commandManager = new CommandManager(registrationEnvironment);
        this.functionLoader = new FunctionLoader(i, this.commandManager.getDispatcher());
        this.resourceManager.registerReloader(this.registryTagManager);
        this.resourceManager.registerReloader(this.lootConditionManager);
        this.resourceManager.registerReloader(this.recipeManager);
        this.resourceManager.registerReloader(this.lootManager);
        this.resourceManager.registerReloader(this.functionLoader);
        this.resourceManager.registerReloader(this.serverAdvancementLoader);
    }

    public FunctionLoader getFunctionLoader() {
        return this.functionLoader;
    }

    public LootConditionManager getLootConditionManager() {
        return this.lootConditionManager;
    }

    public LootManager getLootManager() {
        return this.lootManager;
    }

    public TagManager getRegistryTagManager() {
        return this.registryTagManager.getTagManager();
    }

    public RecipeManager getRecipeManager() {
        return this.recipeManager;
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    public ServerAdvancementLoader getServerAdvancementLoader() {
        return this.serverAdvancementLoader;
    }

    public ResourceManager getResourceManager() {
        return this.resourceManager;
    }

    public static CompletableFuture<ServerResourceManager> reload(List<ResourcePack> list, CommandManager.RegistrationEnvironment registrationEnvironment, int i, Executor executor, Executor executor2) {
        ServerResourceManager serverResourceManager = new ServerResourceManager(registrationEnvironment, i);
        CompletableFuture<Unit> completableFuture = serverResourceManager.resourceManager.reload(executor, executor2, list, field_25334);
        return ((CompletableFuture)completableFuture.whenComplete((unit, throwable) -> {
            if (throwable != null) {
                serverResourceManager.close();
            }
        })).thenApply(unit -> serverResourceManager);
    }

    public void loadRegistryTags() {
        this.registryTagManager.getTagManager().apply();
    }

    @Override
    public void close() {
        this.resourceManager.close();
    }
}
