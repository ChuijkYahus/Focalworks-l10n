package caelum.focalworks.mixin.basic_hands;


import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.FrameEvaluate;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.iota.ListIota;
import at.petrak.hexcasting.common.casting.actions.rw.OpRead;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OpRead.class)
public class MixinOpRead{
    @Inject(method="operate",at=@At("RETURN"),remap=false,cancellable = true)
    private void Focalworks_OpRead(
            CastingEnvironment env,
            CastingImage image,
            SpellContinuation continuation,
            CallbackInfoReturnable<OperationResult> cir
    ) {

        CastingEnvironment.HeldItemInfo info = env.getHeldItemToOperateOn(stack ->  {
            ADIotaHolder dataHolder = IXplatAbstractions.INSTANCE.findDataHolder(stack);
            return dataHolder != null && (dataHolder.readIota(env.getWorld()) != null || dataHolder.emptyIota() != null)
                    && stack.hasTag() && stack.getOrCreateTag().contains("riggedread");});
        if (info != null) {
            ADIotaHolder datumHolder = IXplatAbstractions.INSTANCE.findDataHolder(info.stack());
            ListIota hex =  (ListIota)IotaType.deserialize((CompoundTag) info.stack().getTag().get("riggedread"),env.getWorld());
            FrameEvaluate frame = new FrameEvaluate(hex.getList(), true);
            cir.setReturnValue(new OperationResult(cir.getReturnValue().component1(),cir.getReturnValue().component2(),continuation.pushFrame(frame),cir.getReturnValue().component4()));
        }
    }

}
