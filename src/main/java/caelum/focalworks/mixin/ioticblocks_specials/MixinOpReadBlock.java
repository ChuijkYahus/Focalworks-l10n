package caelum.focalworks.mixin.ioticblocks_specials;

import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.api.casting.SpellList;
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.FrameEvaluate;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock;
import at.petrak.hexcasting.common.casting.actions.rw.OpTheCoolerRead;
import caelum.focalworks.api.OldRiggedHexFinder;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import gay.object.ioticblocks.impl.IoticBlocksAPIImpl;
import gay.object.ioticblocks.utils.IoticBlocksUtils;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = OpTheCoolerRead.class,remap=false,priority=900)
public class MixinOpReadBlock {
    @Shadow
    public int getArgc() {return 0;}

    @Unique
    private SpellContinuation cont = null;

    @WrapOperation(method = "operate", at = @At(value = "INVOKE", target = "Lat/petrak/hexcasting/api/casting/castables/ConstMediaAction$DefaultImpls;operate(Lat/petrak/hexcasting/api/casting/castables/ConstMediaAction;Lat/petrak/hexcasting/api/casting/eval/CastingEnvironment;Lat/petrak/hexcasting/api/casting/eval/vm/CastingImage;Lat/petrak/hexcasting/api/casting/eval/vm/SpellContinuation;)Lat/petrak/hexcasting/api/casting/eval/OperationResult;"))
    private OperationResult operate(ConstMediaAction $this, CastingEnvironment env, CastingImage image, SpellContinuation continuation, Operation<OperationResult> original) {
        cont = continuation;
        OperationResult old = original.call($this, env, image, continuation);
        return old.copy(old.getNewImage(), old.getSideEffects(), cont, old.getSound());
    }

    @Inject(method="execute",at= @At("HEAD"), cancellable = true, remap = false)
    private void execute(List<? extends Iota> args, CastingEnvironment env, CallbackInfoReturnable<List<Iota>> cir) {
        IoticBlocksUtils.getEntityOrBlockPos(args, 0, getArgc()).ifRight(target -> {
            env.assertPosInRange(target);

            ADIotaHolder datumHolder = new IoticBlocksAPIImpl().findIotaHolder(env.getWorld(), target);
            if (datumHolder == null) {throw MishapBadBlock.of(target,"iota.read");}

            Iota datum = datumHolder.readIota(env.getWorld());
            if (datum == null) {
                datum = datumHolder.emptyIota();
            }
            if (datum == null) {
                throw MishapBadBlock.of(target,"iota.read");
            }
            SpellList hex = OldRiggedHexFinder.get_rig_vec(target,env.getWorld(),"riggedread");
            if (hex != null) {
                FrameEvaluate frame = new FrameEvaluate(hex, true);
                cont = cont.pushFrame(frame);
            }
            cir.setReturnValue(List.of(datum));
        });

    }
}
