package caelum.focalworks.mixin.basic_hands.MixinOpWrite;


import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.common.casting.actions.rw.OpWrite;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

// gosh, I think I'm going to explode on the spot
// practically reimplementing execute and parts of operate, but I don't know a better way - make an issue if you
// have a better idea
@Mixin(value = OpWrite.class, remap = false)
public class MixinOpWrite {
    @Inject(method = "execute", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/List;get(I)Ljava/lang/Object;",ordinal=0))
    public void focalworks_execute(List<? extends Iota> args, CastingEnvironment env, CallbackInfoReturnable<SpellAction.Result> cir, @Local LocalRef<Iota> datum) {
    }
}

