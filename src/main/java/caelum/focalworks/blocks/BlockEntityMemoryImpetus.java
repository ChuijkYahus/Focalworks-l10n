package caelum.focalworks.blocks;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.circles.BlockEntityAbstractImpetus;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.lib.HexBlockEntities;
import caelum.focalworks.registry.FocalworksBlockEntities;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityMemoryImpetus extends BlockEntityAbstractImpetus {

    public BlockEntityMemoryImpetus(BlockPos pWorldPosition, BlockState pBlockState) {
        super(FocalworksBlockEntities.IMPETUS_MEMORY_TILE, pWorldPosition, pBlockState);
    }

    public void applyScryingLensOverlay(List<Pair<ItemStack, Component>> lines,
                                        BlockState state, BlockPos pos, Player observer,
                                        Level world,
                                        Direction hitFace) {
        super.applyScryingLensOverlay(lines, state, pos, observer, world, hitFace);
    }

    @Override
    protected void saveModData(CompoundTag tag) {
        super.saveModData(tag);
    }

    @Override
    protected void loadModData(CompoundTag tag) {
        super.loadModData(tag);
    }
}
