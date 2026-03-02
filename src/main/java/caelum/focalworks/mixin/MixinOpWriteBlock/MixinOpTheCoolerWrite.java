package caelum.focalworks.mixin.MixinOpWriteBlock;

import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.SpellList;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.ExecutionClientView;
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType;
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.iota.BooleanIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.Vec3Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock;
import at.petrak.hexcasting.common.casting.actions.rw.OpTheCoolerWrite;
import caelum.focalworks.Focalworks;
import caelum.focalworks.api.RiggedHexFinder;
import caelum.focalworks.casting.mishaps.MishapInternalHex;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import gay.object.ioticblocks.impl.IoticBlocksAPIImpl;
import gay.object.ioticblocks.utils.IoticBlocksUtils;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(OpTheCoolerWrite.class)
public class MixinOpTheCoolerWrite {
    @Shadow
    @Final
    private static int argc;

    @WrapMethod(method="execute")
    private SpellAction.Result execute(List<Iota> args, CastingEnvironment env, Operation<SpellAction.Result> original)
    {
        // it's really nice to be able to WrapMethod execute instead of operate
        // thank you pool & justarandomdude
        AtomicReference<SpellAction.Result> optional_result = new AtomicReference<>();
        AtomicReference<List<Iota>> args2 = new AtomicReference<>(args);
        IoticBlocksUtils.getEntityOrBlockPos(args,0,argc).ifRight(target -> {
            args.remove(0);
            HashMap<String, Object> map = Focalworks.CONTEXT.get();
            CastingVM vm = (CastingVM) map.get("vm");
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
            SpellList hex = RiggedHexFinder.get_rig_write_vec(target,env.getWorld());
            if (hex != null) {
                RiggedHexFinder.cast_rigged_hex(vm,hex);
                CastingImage image = vm.getImage();
                List<Iota> stack2 = image.getStack();
                Iota top = stack2.remove(stack2.size()-1);;
                if (top instanceof BooleanIota) {
                    stack2.remove(stack2.size()-1);
                    if (!((BooleanIota)top).getBool()) {
                        List<OperatorSideEffect> sideEffects = new ArrayList<OperatorSideEffect>();
                        optional_result.set(new SpellAction.Result(Focalworks.emptyRenderedSpell(),
                                0L,
                                List.of(new ParticleSpray(target.getCenter(), new Vec3(0, 0, 0), 0, 0, 0)),
                                1L
                        ));
                    }
                }
                vm.setImage(image.copy(stack2,image.getParenCount(),image.getParenthesized(),image.getEscapeNext(),image.getOpsConsumed(),image.getUserData()));
                args2.set(List.of(new Vec3Iota(target.getCenter()),top));
            }
        });
        if (optional_result.get() != null) {
            return optional_result.get();
        }
        return original.call(args2.get(),env);
    }
}
