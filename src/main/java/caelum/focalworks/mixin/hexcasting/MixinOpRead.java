package caelum.focalworks.mixin.hexcasting;


import at.petrak.hexcasting.api.casting.SpellList;
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.FrameEvaluate;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.common.casting.actions.rw.OpRead;
import caelum.focalworks.api.OldRiggedHexFinder;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(OpRead.class)
public class MixinOpRead {

    @Unique
    private SpellContinuation cont = null;

    @WrapOperation(method = "operate", at = @At(value = "INVOKE", target = "Lat/petrak/hexcasting/api/casting/castables/ConstMediaAction$DefaultImpls;operate(Lat/petrak/hexcasting/api/casting/castables/ConstMediaAction;Lat/petrak/hexcasting/api/casting/eval/CastingEnvironment;Lat/petrak/hexcasting/api/casting/eval/vm/CastingImage;Lat/petrak/hexcasting/api/casting/eval/vm/SpellContinuation;)Lat/petrak/hexcasting/api/casting/eval/OperationResult;"))
    private OperationResult operate(ConstMediaAction $this, CastingEnvironment env, CastingImage image, SpellContinuation continuation, Operation<OperationResult> original) {
        cont = continuation;
        OperationResult old = original.call($this, env, image, continuation);
        return old.copy(old.getNewImage(), old.getSideEffects(), cont, old.getSound());
    }
    @Expression("return ?")
    @Inject(method = "execute", at = @At("MIXINEXTRAS:EXPRESSION"), remap = false)
    private void execute(List<? extends Iota> args, CastingEnvironment env, CallbackInfoReturnable<List<Iota>> cir,
                         @Local(name="handStack") ItemStack handStack) {
        SpellList hex = OldRiggedHexFinder.get_rig_item(handStack, env.getWorld(), "riggedread");
        cont.pushFrame(new FrameEvaluate(hex, true));
    }
}
