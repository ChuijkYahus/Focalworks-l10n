package caelum.focalworks.mixin.hexcasting;

import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.api.casting.SpellList;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.*;
import at.petrak.hexcasting.api.casting.iota.BooleanIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.iota.ListIota;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.casting.actions.rw.OpWrite;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import caelum.focalworks.Focalworks;
import caelum.focalworks.api.RiggedHexFinder;
import caelum.focalworks.casting.frames.FrameWrite;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.core.jmx.Server;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.List;
// yayayayayayayay
@Mixin(value = OpWrite.class, remap = false)
public class MixinOpWrite {
    @Unique
    private List<Iota> stack = null;
    @Unique
    @Expression("return ?")
    //@ModifyArg(method = "execute", at = @At(value = "INVOKE", target = "Lat/petrak/hexcasting/common/casting/actions/rw/OpWrite$Spell;<init>(Lat/petrak/hexcasting/api/casting/iota/Iota;Lat/petrak/hexcasting/api/addldata/ADIotaHolder;)V"))
    @Inject(method = "execute", at = @At(value = "MIXINEXTRAS:EXPRESSION"), cancellable = true)
    private void focalworks_execute(List<? extends Iota> args, CastingEnvironment env, CallbackInfoReturnable<SpellAction.Result> cir, @Local(name = "datum") Iota datum, @Local(name = "handStack") ItemStack handStack) {
        SpellList hex = RiggedHexFinder.get_rig_item(handStack, env.getWorld(), "riggedwrite");
        if (hex != null) {
            HashMap<String, Object> map = Focalworks.CONTEXT.get();
            SpellContinuation continuation = (SpellContinuation) map.get("continuation");
            continuation = continuation
                    .pushFrame(new FrameWrite(null, "basic_hand_write","basic_hand_write"))
                    .pushFrame(new FrameEvaluate(hex, false));
            map.put("continuation",continuation);
            stack = List.of(datum);
            cir.setReturnValue(new SpellAction.Result(
                    Focalworks.emptyRenderedSpell,
                    0L,
                    List.of(),
                    1L
            ));
        }
    }

    @WrapOperation(method= "operate", at = @At(value = "INVOKE", target = "Lat/petrak/hexcasting/api/casting/castables/SpellAction$DefaultImpls;operate(Lat/petrak/hexcasting/api/casting/castables/SpellAction;Lat/petrak/hexcasting/api/casting/eval/CastingEnvironment;Lat/petrak/hexcasting/api/casting/eval/vm/CastingImage;Lat/petrak/hexcasting/api/casting/eval/vm/SpellContinuation;)Lat/petrak/hexcasting/api/casting/eval/OperationResult;"))
    private OperationResult focalworks_operate(SpellAction spell, CastingEnvironment env, CastingImage image, SpellContinuation continuation, Operation<OperationResult> original) {

        OperationResult old = original.call(spell, env, image, continuation);
        if (stack == null) {return old;}
        CastingImage oldImage = old.component1();
        List<Iota> old_stack = oldImage.getStack();
        old_stack.addAll(stack);
        CastingImage newImage = oldImage.copy(
                old_stack,
                oldImage.getParenCount(),
                oldImage.getParenthesized(),
                oldImage.getEscapeNext(),
                oldImage.getOpsConsumed(),
                oldImage.getUserData()
        );
        return old.copy(newImage, old.getSideEffects(), old.getNewContinuation(), old.getSound());
    }

    static {
        Focalworks.WRITE_TARGET_SERIALIZER_HASHMAP.put("basic_hand_write", new Focalworks.WriteTargetSerializer<ItemStack>() {
            @Override
            public ItemStack read(CompoundTag tag, ServerLevel world) {
                return null;
            }

            @Override
            public void write(CompoundTag tag, ItemStack target) {
                return;
            }
        });
        Focalworks.WRITE_CONSUMER_HASHMAP.put("basic_hand_write", new Focalworks.WriteConsumer<ItemStack>() {
            @Override
            public void consume(SpellContinuation continuation, ServerLevel world, CastingVM vm, ItemStack target) {
                CastingEnvironment env = vm.getEnv();
                CastingImage image = vm.getImage();
                List<Iota> stack = image.getStack();
                int stackSize = stack.size();
                if (stack.isEmpty()) {
                    return;
                }
                Iota datum;
                Iota top = stack.remove(stackSize - 1);
                if (top instanceof BooleanIota && stackSize >= 2) {
                    if (((BooleanIota) top).getBool()) {
                        datum = stack.remove(stackSize - 2);
                    } else {return;}
                } else {datum = top;}
                CastingEnvironment.HeldItemInfo datumHolder = env.getHeldItemToOperateOn((itemStack) -> {
                    final ADIotaHolder holder = IXplatAbstractions.INSTANCE.findDataHolder(itemStack);
                    if (holder == null) return false;
                    return holder.writeIota(datum, true);
                });
                if (datumHolder != null && datumHolder.stack() != null && IXplatAbstractions.INSTANCE.findDataHolder(datumHolder.stack()) != null)  {
                    IXplatAbstractions.INSTANCE.findDataHolder(datumHolder.stack()).writeIota(datum, false);
                }
            }
        });
    }
}