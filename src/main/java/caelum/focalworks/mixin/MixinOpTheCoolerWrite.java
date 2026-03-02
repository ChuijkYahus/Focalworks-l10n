package caelum.focalworks.mixin;

import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.ExecutionClientView;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType;
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.*;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import at.petrak.hexcasting.common.casting.actions.rw.OpTheCoolerWrite;
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import caelum.focalworks.casting.mishaps.MishapInternalHex;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;

// gosh, I think I'm going to explode on the spot
// practically reimplementing execute and parts of operate, but I don't know a better way - make an issue if you
// have a better idea
@Mixin(value = OpTheCoolerWrite.class, remap = false)
public class MixinOpTheCoolerWrite {
    @WrapMethod(method="operate")
    public OperationResult focalworks_operate(
            CastingEnvironment env, CastingImage image, SpellContinuation continuation, Operation<OperationResult> original
    ) {
        // please don't sue me, I know it's shit :sob:
        CastingImage img = image;
        SpellContinuation cont = continuation;
        List<Iota> stack = image.getStack();
        Iota x = stack.get(stack.size()-2);
        Iota datum = stack.get(stack.size()-1);
        if ((x instanceof EntityIota)) {
            stack.remove(stack.size()-2);
            Entity entity = ((EntityIota) x).getEntity();
            ItemStack item = ((ItemEntity)entity).getItem();

            CastingImage image2 = image.copy(stack,image.getParenCount(),image.getParenthesized(),image.getEscapeNext(),image.getOpsConsumed(),image.getUserData());
            CastingVM vm = new CastingVM(image2,env);
            ADIotaHolder datumHolder = IXplatAbstractions.INSTANCE.findDataHolder(entity);
            if (datumHolder == null) {throw MishapBadEntity.of(entity, "iota.write");}

            if (!datumHolder.writeIota(datum, true)){
                throw MishapBadEntity.of(entity, "iota.write");}
            ListIota hex =  (ListIota) IotaType.deserialize((CompoundTag) item.getTag().get("riggedwrite"),env.getWorld());
            List<Iota> temp = new ArrayList<Iota>();
            hex.getList().forEach(temp::add);
            ExecutionClientView result = vm.queueExecuteAndWrapIotas(temp,env.getWorld());
            if (result.getResolutionType() == ResolvedPatternType.ERRORED) {
                throw new MishapInternalHex();
            }
            img = vm.getImage();
            List<Iota> stack2 = img.getStack();
            Iota top = stack2.remove(stack2.size()-1);
            stack2.add(stack2.size(),x);
            stack2.add(stack2.size(),top);
            img = img.copy(stack2,img.getParenCount(),img.getParenthesized(),img.getEscapeNext(),img.getOpsConsumed(),img.getUserData());
            cont = continuation;

            if (top instanceof BooleanIota) {
                stack.remove(stack.size()-1);

                img = img.copy(stack,img.getParenCount(),img.getParenthesized(),img.getEscapeNext(),img.getOpsConsumed(),img.getUserData());
                if (!((BooleanIota)top).getBool()) {
                    List<OperatorSideEffect> sideEffects = new ArrayList<OperatorSideEffect>();
                    return new OperationResult(img,sideEffects,cont, HexEvalSounds.MUTE);
                }
            }
        }
        return original.call(env,img,cont);
    }
}

