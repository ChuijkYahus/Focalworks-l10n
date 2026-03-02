package caelum.focalworks.mixin.MixinOpWriteBlock;

import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.common.casting.actions.rw.OpTheCoolerWrite;
import caelum.focalworks.Focalworks;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;

@Mixin(SpellAction.DefaultImpls.class)
public final class MixinSpellActionDefaultImpls {
    @Inject(method="operate",at= @At(value = "INVOKE", target = "Lat/petrak/hexcasting/api/casting/castables/SpellAction;executeWithUserdata(Ljava/util/List;Lat/petrak/hexcasting/api/casting/eval/CastingEnvironment;Lnet/minecraft/nbt/CompoundTag;)Lat/petrak/hexcasting/api/casting/castables/SpellAction$Result;"))
    private static void operate0(SpellAction $this, CastingEnvironment env, CastingImage image, SpellContinuation continuation, CallbackInfoReturnable<OperationResult> cir) {
        if ($this instanceof OpTheCoolerWrite) {
            HashMap<String,Object> map = Focalworks.CONTEXT.get();
            map.put("vm",new CastingVM(image,env));
        }
    }
    @Definition(id = "executeWithUserdata", method = "Lat/petrak/hexcasting/api/casting/castables/SpellAction;executeWithUserdata(Ljava/util/List;Lat/petrak/hexcasting/api/casting/eval/CastingEnvironment;Lnet/minecraft/nbt/CompoundTag;)Lat/petrak/hexcasting/api/casting/castables/SpellAction$Result;")
    @Expression("? = ?.executeWithUserdata(?, ?, ?)")
    @Inject(method="operate",at= @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER))
    private static void operate1(SpellAction $this, CastingEnvironment environment, CastingImage castingImage, SpellContinuation spellContinuation, CallbackInfoReturnable<OperationResult> cir, @Local LocalRef<CastingEnvironment> env, @Local LocalRef<CastingImage> image, @Local LocalRef<SpellContinuation> continuation) {
        if ($this instanceof OpTheCoolerWrite) {
            HashMap<String,Object> map = Focalworks.CONTEXT.get();
            CastingVM vm = (CastingVM) map.get("vm");
            env.set(vm.getEnv());
            image.set(vm.getImage());
        }
    }
}
