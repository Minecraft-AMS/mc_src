/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  org.slf4j.Logger
 */
package net.minecraft.server;

import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.loot.function.LootFunctionManager;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.SimpleResourceReload;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.function.FunctionLoader;
import net.minecraft.tag.TagKey;
import net.minecraft.tag.TagManagerLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.RegistryKey;
import org.slf4j.Logger;

public class DataPackContents {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final CompletableFuture<Unit> COMPLETED_UNIT = CompletableFuture.completedFuture(Unit.INSTANCE);
    private final CommandRegistryAccess commandRegistryAccess;
    private final CommandManager commandManager;
    private final RecipeManager recipeManager = new RecipeManager();
    private final TagManagerLoader registryTagManager;
    private final LootConditionManager lootConditionManager = new LootConditionManager();
    private final LootManager lootManager = new LootManager(this.lootConditionManager);
    private final LootFunctionManager lootFunctionManager = new LootFunctionManager(this.lootConditionManager, this.lootManager);
    private final ServerAdvancementLoader serverAdvancementLoader = new ServerAdvancementLoader(this.lootConditionManager);
    private final FunctionLoader functionLoader;

    public DataPackContents(DynamicRegistryManager.Immutable dynamicRegistryManager, CommandManager.RegistrationEnvironment commandEnvironment, int functionPermissionLevel) {
        this.registryTagManager = new TagManagerLoader(dynamicRegistryManager);
        this.commandRegistryAccess = new CommandRegistryAccess(dynamicRegistryManager);
        this.commandManager = new CommandManager(commandEnvironment, this.commandRegistryAccess);
        this.commandRegistryAccess.setEntryListCreationPolicy(CommandRegistryAccess.EntryListCreationPolicy.CREATE_NEW);
        this.functionLoader = new FunctionLoader(functionPermissionLevel, this.commandManager.getDispatcher());
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

    public LootFunctionManager getLootFunctionManager() {
        return this.lootFunctionManager;
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

    public List<ResourceReloader> getContents() {
        return List.of(this.registryTagManager, this.lootConditionManager, this.recipeManager, this.lootManager, this.lootFunctionManager, this.functionLoader, this.serverAdvancementLoader);
    }

    public static CompletableFuture<DataPackContents> reload(ResourceManager manager, DynamicRegistryManager.Immutable dynamicRegistryManager, CommandManager.RegistrationEnvironment commandEnvironment, int functionPermissionLevel, Executor prepareExecutor, Executor applyExecutor) {
        DataPackContents dataPackContents = new DataPackContents(dynamicRegistryManager, commandEnvironment, functionPermissionLevel);
        return ((CompletableFuture)SimpleResourceReload.start(manager, dataPackContents.getContents(), prepareExecutor, applyExecutor, COMPLETED_UNIT, LOGGER.isDebugEnabled()).whenComplete().whenComplete((void_, throwable) -> dataPackContents.commandRegistryAccess.setEntryListCreationPolicy(CommandRegistryAccess.EntryListCreationPolicy.FAIL))).thenApply(void_ -> dataPackContents);
    }

    public void refresh(DynamicRegistryManager dynamicRegistryManager) {
        this.registryTagManager.getRegistryTags().forEach(tags -> DataPackContents.repopulateTags(dynamicRegistryManager, tags));
        Blocks.refreshShapeCache();
    }

    private static <T> void repopulateTags(DynamicRegistryManager dynamicRegistryManager, TagManagerLoader.RegistryTags<T> tags) {
        RegistryKey registryKey = tags.key();
        Map map = tags.tags().entrySet().stream().collect(Collectors.toUnmodifiableMap(entry -> TagKey.of(registryKey, (Identifier)entry.getKey()), entry -> List.copyOf((Collection)entry.getValue())));
        dynamicRegistryManager.get(registryKey).populateTags(map);
    }
}

