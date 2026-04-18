package caelum.focalworks.mixin.hexcasting;

import at.petrak.hexcasting.api.casting.SpellList;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.FrameEvaluate;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.EntityIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.common.casting.actions.rw.OpTheCoolerRead;
import caelum.focalworks.api.OldRiggedHexFinder;
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
            if (!(((EntityIota) x).getEntity() instanceof ItemEntity)) {return;}
            Entity entity = ((EntityIota) x).getEntity();
            ItemStack item = ((ItemEntity)entity).getItem();
            SpellList hex = OldRiggedHexFinder.get_rig_item(item,env.getWorld(),"riggedread");
            if (hex != null ) {
                FrameEvaluate frame = new FrameEvaluate(hex, true);
                cir.setReturnValue(new OperationResult(cir.getReturnValue().component1(),cir.getReturnValue().component2(),continuation.pushFrame(frame),cir.getReturnValue().component4()));
            }
        }
    }

}
