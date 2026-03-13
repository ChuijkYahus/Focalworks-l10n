package caelum.focalworks.registry;

import at.petrak.hexcasting.xplat.IXplatAbstractions;
import caelum.focalworks.Focalworks;
import caelum.focalworks.blocks.BlockEntityMemoryImpetus;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class FocalworksBlockEntities {
    private static final Map<ResourceLocation, BlockEntityType<?>> BLOCK_ENTITIES = new LinkedHashMap<>();

    public static void registerTiles(BiConsumer<BlockEntityType<?>, ResourceLocation> r) {
        for (var e : BLOCK_ENTITIES.entrySet()) {
            r.accept(e.getValue(), e.getKey());
        }
    }

    public static final BlockEntityType<BlockEntityMemoryImpetus> IMPETUS_MEMORY_TILE = register(
            "impetus/memory",
            BlockEntityMemoryImpetus::new, FocalworksBlocks.IMPETUS_MEMORY);

    private static <T extends BlockEntity> BlockEntityType<T> register(String id,
                                                                       BiFunction<BlockPos, BlockState, T> func, Block... blocks) {
        var ret = IXplatAbstractions.INSTANCE.createBlockEntityType(func, blocks);
        var old = BLOCK_ENTITIES.put(Focalworks.id(id), ret);
        if (old != null) {
            throw new IllegalArgumentException("Duplicate id " + id);
        }
        return ret;
    }
}
