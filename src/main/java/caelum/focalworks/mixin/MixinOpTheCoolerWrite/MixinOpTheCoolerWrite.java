package caelum.focalworks.mixin.MixinOpTheCoolerWrite;

import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.iota.BooleanIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import at.petrak.hexcasting.common.casting.actions.rw.OpTheCoolerWrite;
import caelum.focalworks.Focalworks;
import caelum.focalworks.api.RiggedHexFinder;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.List;

@Mixin(OpTheCoolerWrite.class)
public class MixinOpTheCoolerWrite {
    @Unique
    private static Boolean isNotCancelled = true;

    //@ModifyArg(method = "execute", at = @At(value = "INVOKE", target = "Lat/petrak/hexcasting/common/casting/actions/rw/OpWrite$Spell;<init>(Lat/petrak/hexcasting/api/casting/iota/Iota;Lat/petrak/hexcasting/api/addldata/ADIotaHolder;)V"))
    @ModifyVariable(method = "execute", at = @At(value = "INVOKE_ASSIGN", target = "at/petrak/hexcasting/common/casting/actions/rw/OpTheCoolerWrite$Spell.<init> (Lat/petrak/hexcasting/api/casting/iota/Iota;Lat/petrak/hexcasting/api/addldata/ADIotaHolder;)V"),name="datum")
    private Iota focalworks_execute(Iota datum, @Local(name = "target") Entity target, @Local(name="env") CastingEnvironment env) {
        HashMap<String,Object> map = Focalworks.CONTEXT.get();
        if(target instanceof ItemEntity) {
            CastingVM vm = (CastingVM) map.get("vm");
            RiggedHexFinder.cast_rigged_hex(vm, RiggedHexFinder.get_rig_write_item(((ItemEntity) target).getItem(), env.getWorld()));
            CastingImage image = vm.getImage();
            List<Iota> stack = image.getStack();
            Iota top = stack.remove(stack.size() - 1);

            vm.setImage(image.copy(
                    stack,
                    image.getParenCount(),
                    image.getParenthesized(),
                    image.getEscapeNext(),
                    image.getOpsConsumed(),
                    image.getUserData()
            ));
            if (top instanceof BooleanIota) {
                isNotCancelled = ((BooleanIota) top).getBool();
                if (isNotCancelled) {
                    return stack.remove(stack.size() - 1);
                } else {/* I don't really need this, but it's just to make sure no errors pop up */ return new NullIota();}
            } else {
                return top;
            }
        }
        return datum;
    }
    @Inject(method="execute",at=@At(value="TAIL"),cancellable = true)
    private void focalworks_canceller(List<Iota> args, CastingEnvironment env, CallbackInfoReturnable<SpellAction.Result> cir) {
        if (!isNotCancelled) {
            cir.setReturnValue(new SpellAction.Result(
                    Focalworks.emptyRenderedSpell,
                    0L,
                    List.of(),
                    1L
            ));
        }
    }
}
