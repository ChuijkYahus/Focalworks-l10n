package caelum.focalworks.mixin.ioticblocks_specials;

import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.api.casting.SpellList;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.eval.vm.FrameEvaluate;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.*;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.casting.actions.rw.OpTheCoolerWrite;
import caelum.focalworks.Focalworks;
import caelum.focalworks.api.OldRiggedHexFinder;
import caelum.focalworks.casting.frames.FrameWrite;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.util.Either;
import gay.object.ioticblocks.api.IoticBlocksAPI;
import gay.object.ioticblocks.utils.IoticBlocksUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.List;

@Mixin(value = OpTheCoolerWrite.class,remap = false, priority=99)
public class MixinOpWriteBlock {
    @Unique
    private List<Iota> stack = null;
    @Unique
    private SpellContinuation cont;
    @Unique
    //@ModifyArg(method = "execute", at = @At(value = "INVOKE", target = "Lat/petrak/hexcasting/common/casting/actions/rw/OpWrite$Spell;<init>(Lat/petrak/hexcasting/api/casting/iota/Iota;Lat/petrak/hexcasting/api/addldata/ADIotaHolder;)V"))
    @Inject(method = "execute", at = @At(value = "HEAD"), cancellable = true)
    private void focalworks_execute(List<? extends Iota> args, CastingEnvironment env, CallbackInfoReturnable<SpellAction.Result> cir) {
        Either<Entity, BlockPos> target_ = IoticBlocksUtils.getEntityOrBlockPos(args, 0, 2);
        Iota datum = args.get(1);
        if (target_.left().isPresent()) {return;}
        BlockPos target = target_.right().get();
        SpellList hex = OldRiggedHexFinder.get_rig_vec(target,env.getWorld(),"riggedwrite");
        if (hex != null) {
            HashMap<String, Object> map = Focalworks.CONTEXT.get();
            cont = cont
                    .pushFrame(new FrameWrite(target, "block_write","block_write"))
                    .pushFrame(new FrameEvaluate(hex, false));
            stack = List.of(datum);
            cir.setReturnValue(new SpellAction.Result(
                    Focalworks.emptyRenderedSpell,
                    0L,
                    List.of(),
                    1L
            ));
        }
    }

    @WrapOperation(method= "operate", at = @At(value = "INVOKE", target = "Lat/petrak/hexcasting/api/casting/castables/SpellAction$DefaultImpls;operate(Lat/petrak/hexcasting/api/casting/castables/SpellAction;Lat/petrak/hexcasting/api/casting/eval/CastingEnvironment;Lat/petrak/hexcasting/api/casting/eval/vm/CastingImage;Lat/petrak/hexcasting/api/casting/eval/vm/SpellContinuation;)Lat/petrak/hexcasting/api/casting/eval/OperationResult;"))
    private OperationResult focalworks_operate(SpellAction spell, CastingEnvironment env, CastingImage image, SpellContinuation continuation, Operation<OperationResult> original) {
        cont = continuation;
        stack = null;
        OperationResult old = original.call(spell, env, image, continuation);
        if (stack == null) {return old;}
        CastingImage oldImage = old.component1();
        List<Iota> old_stack = oldImage.getStack();
        old_stack.addAll(stack);
        CastingImage newImage = oldImage.copy(
                old_stack,
                oldImage.getParenCount(),
                oldImage.getParenthesized(),
                oldImage.getEscapeNext(),
                oldImage.getOpsConsumed(),
                oldImage.getUserData()
        );
        return old.copy(newImage, old.getSideEffects(), cont, old.getSound());
    }

    static {
        Focalworks.WRITE_TARGET_SERIALIZER_HASHMAP.put("block_write", new Focalworks.WriteTargetSerializer<BlockPos>() {
            @Override
            public BlockPos read(CompoundTag tag, ServerLevel world) {
                CompoundTag target = NBTHelper.getCompound(tag, "target");
                return new BlockPos(NBTHelper.getInt(target, "x"), NBTHelper.getInt(target,"y"), NBTHelper.getInt(target, "z"));
            }

            @Override
            public void write(CompoundTag tag, BlockPos target) {
                CompoundTag temp = new CompoundTag();
                NBTHelper.putInt(temp, "x", target.getX());
                NBTHelper.putInt(temp, "y", target.getY());
                NBTHelper.putInt(temp, "z", target.getZ());
                NBTHelper.putCompound(tag, "target", temp);
            }
        });
        Focalworks.WRITE_CONSUMER_HASHMAP.put("block_write", new Focalworks.WriteConsumer<BlockPos>() {
            @Override
            public void consume(SpellContinuation continuation, ServerLevel world, CastingVM vm, BlockPos target) {
                CastingEnvironment env = vm.getEnv();
                if (!(env.isVecInAmbit(target.getCenter()))) {return;}
                CastingImage image = vm.getImage();
                List<Iota> stack = image.getStack();
                int stackSize = stack.size();
                if (stack.isEmpty()) {
                    return;
                }
                Iota datum;
                Iota top = stack.remove(stackSize - 1);
                if (top instanceof BooleanIota && stackSize >= 2) {
                    if (((BooleanIota) top).getBool()) {
                        datum = stack.remove(stackSize - 2);
                    } else {
                        return;
                    }
                } else {
                    datum = top;
                }

                ADIotaHolder datumHolder = IoticBlocksAPI.INSTANCE.findIotaHolder(world, target);
                if (datumHolder != null) {
                    datumHolder.writeIota(datum, false);
                }
            }
        });
    }
}
