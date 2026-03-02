package caelum.focalworks.api;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.SpellList;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.ExecutionClientView;
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType;
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.eval.vm.FrameEvaluate;
import at.petrak.hexcasting.api.casting.iota.BooleanIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.iota.ListIota;
import at.petrak.hexcasting.api.utils.NBTHelper;
import caelum.focalworks.Focalworks;
import caelum.focalworks.casting.mishaps.MishapInternalHex;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class RiggedHexFinder {
    public static SpellList get_rig_read_vec(BlockPos pos, ServerLevel world) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        assert blockEntity != null;
        CompoundTag tag = blockEntity.saveWithoutMetadata();
        if (NBTHelper.contains(tag,"riggedread")) {
            return ((ListIota) IotaType.deserialize((CompoundTag) NBTHelper.get(tag, "riggedread"), world)).getList();
        }
        if (blockEntity instanceof Container) {
            return get_rig_read_item(((Container)blockEntity).getItem(0),world);
        }

        return null;
    }
    public static SpellList get_rig_write_vec(BlockPos pos, ServerLevel world) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null) {return null;}
        CompoundTag tag = blockEntity.saveWithoutMetadata();
        if (NBTHelper.contains(tag,"riggedwrite")) {
            return ((ListIota) IotaType.deserialize((CompoundTag) NBTHelper.get(tag, "riggedwrite"), world)).getList();
        }
        if (blockEntity instanceof Container) {
            return get_rig_write_item(((Container)blockEntity).getItem(0),world);
        }

        return null;
    }
    public static SpellList get_rig_write_item(ItemStack item, ServerLevel world) {
        if (item != null && world != null) {
            CompoundTag tag = item.getOrCreateTag();
            if (NBTHelper.contains(tag, "riggedwrite")) {
                ListIota hex = (ListIota) IotaType.deserialize((CompoundTag) NBTHelper.get(tag, "riggedwrite"), world);
                return hex.getList();
            }
        }
        return null;
    }
    public static SpellList get_rig_read_item(ItemStack item, ServerLevel world) {
        if (item != null && world != null) {
            CompoundTag tag = item.getOrCreateTag();
            if (NBTHelper.contains(tag, "riggedread")) {
                ListIota hex = (ListIota) IotaType.deserialize((CompoundTag) NBTHelper.get(tag, "riggedread"), world);
                return hex.getList();
            }
        }
        return null;
    }
    public static void cast_rigged_hex(CastingVM vm, SpellList hex) {
        List<Iota> temp = new ArrayList<Iota>();
        hex.forEach(temp::add);
        ExecutionClientView result = vm.queueExecuteAndWrapIotas(temp,vm.getEnv().getWorld());
        if (result.getResolutionType() == ResolvedPatternType.ERRORED) {
            throw new MishapInternalHex();
        }
    }

}
