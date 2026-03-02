package caelum.focalworks.mixin;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.FrameEvaluate;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.EntityIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.iota.ListIota;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import at.petrak.hexcasting.common.casting.actions.rw.OpTheCoolerRead;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(OpTheCoolerRead.class)
public class MixinOpTheCoolerRead {
    @Inject(method="operate",at=@At("RETURN"),remap=false,cancellable = true)
    private void Focalworks_OpTheCoolerRead(
            CastingEnvironment env,
            CastingImage image,
            SpellContinuation continuation,
            CallbackInfoReturnable<OperationResult> cir
    ) {
        // I keep having to do this, but I guess it'll work
        // you know, now that I think about this, this method is really cursed
        List<Iota> stack = image.getStack();
        Iota x = stack.get(stack.size()-1);
        if ((x instanceof EntityIota)) {
            Entity entity = ((EntityIota) x).getEntity();
            ItemStack item = ((ItemEntity)entity).getItem();
            if (item.getOrCreateTag().contains("riggedwrite") && item.hasTag()) {
                ListIota hex = (ListIota)IotaType.deserialize((CompoundTag) item.getTag().get("riggedwrite"),env.getWorld());
                FrameEvaluate frame = new FrameEvaluate(hex.getList(), true);
                cir.setReturnValue(new OperationResult(cir.getReturnValue().component1(),cir.getReturnValue().component2(),continuation.pushFrame(frame),cir.getReturnValue().component4()));
            }
        }
    }

}
