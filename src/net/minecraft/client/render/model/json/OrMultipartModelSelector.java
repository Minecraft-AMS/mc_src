/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Streams
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model.json;

import com.google.common.collect.Streams;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.state.StateManager;

@Environment(value=EnvType.CLIENT)
public class OrMultipartModelSelector
implements MultipartModelSelector {
    public static final String KEY = "OR";
    private final Iterable<? extends MultipartModelSelector> selectors;

    public OrMultipartModelSelector(Iterable<? extends MultipartModelSelector> selectors) {
        this.selectors = selectors;
    }

    @Override
    public Predicate<BlockState> getPredicate(StateManager<Block, BlockState> stateManager) {
        List list = Streams.stream(this.selectors).map(multipartModelSelector -> multipartModelSelector.getPredicate(stateManager)).collect(Collectors.toList());
        return blockState -> list.stream().anyMatch(predicate -> predicate.test(blockState));
    }
}

