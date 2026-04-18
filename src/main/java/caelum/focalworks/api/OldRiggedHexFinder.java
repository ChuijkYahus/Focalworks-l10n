package caelum.focalworks.api;

import at.petrak.hexcasting.api.casting.SpellList;
import at.petrak.hexcasting.api.casting.eval.ExecutionClientView;
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.iota.*;
import at.petrak.hexcasting.api.utils.NBTHelper;
import caelum.focalworks.casting.mishaps.MishapInternalHex;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;

public class OldRiggedHexFinder {
    public static SpellList get_rig_vec(BlockPos pos, ServerLevel world, String key) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        assert blockEntity != null;
        CompoundTag tag = blockEntity.saveWithoutMetadata();
        if (NBTHelper.contains(tag,key)) {
            return ((ListIota) IotaType.deserialize((CompoundTag) NBTHelper.get(tag, key), world)).getList();
        }
        if (blockEntity instanceof Container) {
            return get_rig_item(((Container)blockEntity).getItem(0),world,key);
        }
        return null;
    }
    public static SpellList get_rig_item(ItemStack item, ServerLevel world, String key) {
        if (item != null && world != null) {
            CompoundTag tag = item.getOrCreateTag();
            if (NBTHelper.contains(tag, key)) {
                ListIota hex = (ListIota) IotaType.deserialize((CompoundTag) NBTHelper.get(tag, key), world);
                return hex.getList();
            }
        }
        return null;
    }
    public static void cast_rigged_hex(CastingVM vm, SpellList hex) {
        if(hex != null) {
            List<Iota> temp = new ArrayList<Iota>();
            hex.forEach(temp::add);
            ExecutionClientView result = vm.queueExecuteAndWrapIotas(temp, vm.getEnv().getWorld());
            if (result.getResolutionType() == ResolvedPatternType.ERRORED) {
                throw new MishapInternalHex();
            }
        }
    }
    public static CastingImage set_image_stack(CastingImage image, List<Iota> stack) {
        return image.copy(stack,image.getParenCount(),image.getParenthesized(),image.getEscapeNext(), image.getOpsConsumed(), image.getUserData());
    }
    public static Pair<Iota, Boolean> cast_rigged_write_with_cancel(CastingVM vm, SpellList hex) {
        boolean isNotCancelled = true;
        OldRiggedHexFinder.cast_rigged_hex(vm,hex);
        CastingImage image = vm.getImage();
        List<Iota> stack = image.getStack();
        Iota top = stack.remove(stack.size() - 1);

        vm.setImage(set_image_stack(image,stack));

        if (top instanceof BooleanIota) {
            isNotCancelled = ((BooleanIota) top).getBool();
            if (isNotCancelled) {
                return new Pair<Iota, Boolean>(stack.remove(stack.size() - 1),true);
            } else {/* I don't really need this, but it's just to make sure no errors pop up */ return new Pair<Iota, Boolean>(new NullIota(),false);}
        } else {
            return new Pair<Iota, Boolean>(top,true);
        }
    }
    public static Iota cast_rigged_read(CastingVM vm, SpellList hex) {
        cast_rigged_hex(vm,hex);
        CastingImage image = vm.getImage();
        List<Iota> stack = image.getStack();
        Iota top = stack.remove(stack.size() - 1);

        vm.setImage(set_image_stack(image,stack));
        return top;
    }
}
