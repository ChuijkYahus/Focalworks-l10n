package caelum.focalworks.registry;

import at.petrak.hexcasting.api.block.circle.BlockAbstractImpetus;
import at.petrak.hexcasting.common.lib.HexBlocks;
import at.petrak.hexcasting.common.lib.HexCreativeTabs;
import at.petrak.hexcasting.common.lib.HexItems;
import caelum.focalworks.Focalworks;
import caelum.focalworks.blocks.BlockEntityMemoryImpetus;
import caelum.focalworks.blocks.BlockMemoryImpetus;
import kotlin.Pair;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class FocalworksBlocks {
    private static final Map<ResourceLocation, Block> BLOCKS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, Pair<Block, Item.Properties>> BLOCK_ITEMS = new LinkedHashMap<>();
    private static final Map<CreativeModeTab, List<Block>> BLOCK_TABS = new LinkedHashMap<>();


    public static final Block IMPETUS_MEMORY = blockItem("impetus/memory",
            new BlockMemoryImpetus(slateish()
                    .pushReaction(PushReaction.BLOCK)
                    .lightLevel(bs -> bs.getValue(BlockAbstractImpetus.ENERGIZED) ? 15 : 0)),
            HexItems.props().rarity(Rarity.UNCOMMON));

    public static void registerBlocks(BiConsumer<Block, ResourceLocation> r) {
        for (var e : BLOCKS.entrySet()) {
            r.accept(e.getValue(), e.getKey());
        }
    }

    public static void init() {

    }

    private static BlockBehaviour.Properties slateish() {
        return BlockBehaviour.Properties
                .copy(Blocks.DEEPSLATE_TILES)
                .strength(4f, 4f);
    }

    private static <T extends Block> T blockItem(String name, T block, Item.Properties props, @Nullable CreativeModeTab tab) {
        blockNoItem(name, block);
        var old = BLOCK_ITEMS.put(Focalworks.id(name), new Pair<>(block, props));
        if (old != null) {
            throw new IllegalArgumentException("Typo? Duplicate id " + name);
        }
        if (tab != null) {
            BLOCK_TABS.computeIfAbsent(tab, t -> new ArrayList<>()).add(block);
        }
        return block;
    }
    private static <T extends Block> T blockNoItem(String name, T block) {
        var old = BLOCKS.put(Focalworks.id(name), block);
        if (old != null) {
            throw new IllegalArgumentException("Typo? Duplicate id " + name);
        }
        return block;
    }

    private static <T extends Block> T blockItem(String name, T block, Item.Properties props) {
        return blockItem(name, block, props, HexCreativeTabs.HEX);
    }

    private static void registerBlockAndItem(String name, Block block, BlockEntity blockEntity) {

    }
}
