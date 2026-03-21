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
import at.petrak.hexcasting.api.casting.iota.BooleanIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.ListIota;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import caelum.focalworks.Focalworks;
import caelum.focalworks.api.RiggedHexFinder;
import caelum.focalworks.casting.frames.FrameWrite;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import gay.object.ioticblocks.api.IoticBlocksAPI;
import gay.object.ioticblocks.casting.actions.OpWriteIndex;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.UUID;

@Mixin(value = OpWriteIndex.class,remap = false, priority=99)
public class MixinOpWriteIndex {
    @Unique
    private List<Iota> stack = null;
    @Unique
    private SpellContinuation cont = null;

    @Unique
    @Expression("return ?")
    //@ModifyArg(method = "execute", at = @At(value = "INVOKE", target = "Lat/petrak/hexcasting/common/casting/actions/rw/OpWrite$Spell;<init>(Lat/petrak/hexcasting/api/casting/iota/Iota;Lat/petrak/hexcasting/api/addldata/ADIotaHolder;)V"))
    @Inject(method = "execute", at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.BEFORE), cancellable = true)
    private void focalworks_execute(List<? extends Iota> args, CastingEnvironment env, CallbackInfoReturnable<SpellAction.Result> cir, @Local(name = "target") Either<Entity, BlockPos> target, @Local(name = "iota") Iota datum, @Local(name= "index") int index) {
        SpellList hex;
        if (target.left().isPresent()) {
            final Entity temp = target.left().get();
            if (!(temp instanceof ItemEntity)) {return;}
            hex = RiggedHexFinder.get_rig_item(((ItemEntity) temp).getItem(), env.getWorld(), "riggedwriteindex");
        } else {
            final BlockPos pos = target.right().get();
            hex = RiggedHexFinder.get_rig_vec(pos,env.getWorld(),"riggedwriteindex");
        }
        if (hex != null) {
            cont = cont
                    .pushFrame(new FrameWrite(Pair.of(target, index), "write_index","write_index"))
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
        Focalworks.WRITE_TARGET_SERIALIZER_HASHMAP.put("write_index", new Focalworks.WriteTargetSerializer<Pair<Either<Entity, BlockPos>, Integer>>() {
            @Override
            public Pair<Either<Entity, BlockPos>, Integer> read(CompoundTag tag, ServerLevel world) {
                CompoundTag target = NBTHelper.getCompound(tag, "target");
                final int targetIndex = NBTHelper.getInt(target, "index");
                if (NBTHelper.contains(target, "uuid")) {
                    UUID uuid = NBTHelper.getUUID(target, "uuid");
                    Entity entity = world.getEntity(uuid);
                    return new Pair<>(Either.left(entity), targetIndex);
                }
                final BlockPos targetPos = new BlockPos(NBTHelper.getInt(target, "x"), NBTHelper.getInt(target,"y"), NBTHelper.getInt(target, "z"));
                return new Pair<>(Either.right(targetPos), targetIndex);
            }

            @Override
            public void write(CompoundTag tag, Pair<Either<Entity, BlockPos>, Integer> target) {
                final Either<Entity, BlockPos> target_ = target.getFirst();
                CompoundTag temp = new CompoundTag();
                if (target_.right().isPresent()) {
                    BlockPos targetPos = target_.right().get();
                    NBTHelper.putInt(temp, "x", targetPos.getX());
                    NBTHelper.putInt(temp, "y", targetPos.getY());
                    NBTHelper.putInt(temp, "z", targetPos.getZ());

                } else if (target_.left().isPresent()) {
                    UUID targetUUID = target_.left().get().getUUID();
                    NBTHelper.putUUID(temp, "uuid", targetUUID);
                }
                NBTHelper.putInt(temp, "index", target.getSecond());

                NBTHelper.putCompound(tag, "target", temp);
            }
        });
        Focalworks.WRITE_CONSUMER_HASHMAP.put("write_index", new Focalworks.WriteConsumer<Pair<Either<Entity, BlockPos>, Integer>>() {
            @Override
            public void consume(SpellContinuation continuation, ServerLevel world, CastingVM vm, Pair<Either<Entity, BlockPos>, Integer> target_) {
                final Either<Entity, BlockPos> target = target_.getFirst();
                final int index = target_.getSecond();

                CastingEnvironment env = vm.getEnv();
                ADIotaHolder datumHolder;
                if (target.right().isPresent()) {
                    BlockPos targetPos = target.right().get();
                    if (!(env.isVecInAmbit(targetPos.getCenter()))) {
                        return;
                    }
                    datumHolder = IoticBlocksAPI.INSTANCE.findIotaHolder(world, targetPos);
                } else if (target.left().isPresent()) {
                    Entity targetEntity = target.left().get();
                    if (!env.isEntityInRange(targetEntity) || !(targetEntity instanceof ItemEntity)) {
                        return;
                    }
                    datumHolder = IXplatAbstractions.INSTANCE.findDataHolder(((ItemEntity) targetEntity).getItem());
                } else {
                    return;
                }
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

                if (datumHolder == null) {return;}
                Iota temp = datumHolder.readIota(world);
                if (!(temp instanceof ListIota list)) {return;}
                ListIota newListIota = new ListIota(Focalworks.modifySpellListAt(list.getList(), index, datum));

                datumHolder.writeIota(newListIota, false);
            }
        });
    }
}
