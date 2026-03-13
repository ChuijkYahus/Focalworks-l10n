package caelum.focalworks.mixin.hexcasting;

import at.petrak.hexcasting.api.casting.eval.CastResult;
import at.petrak.hexcasting.api.casting.eval.vm.*;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CastingVM.class)
public class MixinCastingVM {
    @Shadow
    private CastingImage image;

    @Definition(id = "next", local = @Local(type = ContinuationFrame.class, name = "next"))
    @Definition(id = "evaluate", method = "Lat/petrak/hexcasting/api/casting/eval/vm/ContinuationFrame;evaluate(Lat/petrak/hexcasting/api/casting/eval/vm/SpellContinuation;Lnet/minecraft/server/level/ServerLevel;Lat/petrak/hexcasting/api/casting/eval/vm/CastingVM;)Lat/petrak/hexcasting/api/casting/eval/CastResult;")
    @Expression("? = ?.evaluate(?,?,?)")
    @WrapOperation(method = "queueExecuteAndWrapIotas", at = @At(value = "INVOKE", target = "Lat/petrak/hexcasting/api/casting/eval/vm/ContinuationFrame;evaluate(Lat/petrak/hexcasting/api/casting/eval/vm/SpellContinuation;Lnet/minecraft/server/level/ServerLevel;Lat/petrak/hexcasting/api/casting/eval/vm/CastingVM;)Lat/petrak/hexcasting/api/casting/eval/CastResult;"))
    private static CastResult queueExecuteAndWrapIotasMixin(ContinuationFrame instance, SpellContinuation continuation, ServerLevel serverLevel, CastingVM castingVM, Operation<CastResult> original, @Local(name = "next") ContinuationFrame a) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment() && a instanceof FrameEvaluate next) {
            if (next.isMetacasting()) {

            }
        }
        return original.call(instance, continuation, serverLevel, castingVM);
    }
}
