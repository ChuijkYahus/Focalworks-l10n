package caelum.focalworks.mixin.ioticblocks_specials.OpReadIndex;


import at.petrak.hexcasting.api.casting.SpellList;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.vm.FrameEvaluate;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import caelum.focalworks.Focalworks;
import caelum.focalworks.api.OldRiggedHexFinder;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Either;
import gay.object.ioticblocks.casting.actions.OpReadIndex;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.List;


// intellij is wrong, this is actually okay
// I don't know why it's not finding my expression, but launching works fine
@Mixin(value = OpReadIndex.class,remap = true)
public class MixinOpReadIndex {


    @Definition(id = "deserialize", method = "Lat/petrak/hexcasting/api/casting/iota/IotaType;deserialize(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/server/level/ServerLevel;)Lat/petrak/hexcasting/api/casting/iota/Iota;")
    @Definition(id = "env", local = @Local(type = CastingEnvironment.class, argsOnly = true))
    @Definition(id = "getWorld", method = "Lat/petrak/hexcasting/api/casting/eval/CastingEnvironment;getWorld()Lnet/minecraft/server/level/ServerLevel;")
    @Definition(id = "listOf", method = "Lkotlin/collections/CollectionsKt;listOf(Ljava/lang/Object;)Ljava/util/List;")
    @Definition(id = "datum", local = @Local(type = Iota.class, index=10))
    @Expression("return listOf(datum)")
    @Inject(method = "execute", at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.BEFORE))
    private void focalworks_execute(
            List<? extends Iota> args, CastingEnvironment env, CallbackInfoReturnable<List<Iota>> cir, @Local Either<Entity, BlockPos> target) {
        SpellList hex = null;
        if (target.right().isPresent()) {
            hex = OldRiggedHexFinder.get_rig_vec(target.right().get(),env.getWorld(),"riggedreadindex");
        } else {
            Entity entity = target.left().get();
            if (entity instanceof ItemEntity) {
                hex = OldRiggedHexFinder.get_rig_item(((ItemEntity) entity).getItem(),env.getWorld(),"riggedreadindex");
            }
        }
        if (hex != null) {
            HashMap<String, Object> map = Focalworks.CONTEXT.get();
            SpellContinuation cont = (SpellContinuation) map.get("cont");
            FrameEvaluate frame = new FrameEvaluate(hex,false);
            map.put("cont",cont.pushFrame(frame));
        }
    }
}
