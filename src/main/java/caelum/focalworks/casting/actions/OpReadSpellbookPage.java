package caelum.focalworks.casting.actions;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.items.storage.ItemSpellbook;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static at.petrak.hexcasting.api.casting.OperatorUtils.getEntity;

public class OpReadSpellbookPage implements ConstMediaAction {
    private static final Integer argc = 1;
    @Override
    public int getArgc() {
        return argc;
    }
    @Override
    public long getMediaCost() {
        return 20 * MediaConstants.DUST_UNIT;
    }

    @Override
    public @NotNull List<Iota> execute(@NotNull List<? extends Iota> args, @NotNull CastingEnvironment env) throws Mishap {
        Entity target = getEntity(args, 0, argc);

        if (!(target instanceof ItemEntity || target instanceof ItemFrame)) {throw new MishapBadEntity(target, Component.translatable("focalworks.spellbook_not_empty"));}

        ItemStack stack = target instanceof ItemFrame frame ? frame.getItem() : ((ItemEntity)target).getItem();
        if (!(stack.getItem() instanceof ItemSpellbook)) {
            throw new MishapBadEntity(target, Component.translatable("focalworks.spellbook_not_empty"));
        }

        return List.of(new DoubleIota(NBTHelper.getInt(stack,"page_idx")));
    }

    @Override
    public @NotNull CostMediaActionResult executeWithOpCount(@NotNull List<? extends Iota> args, @NotNull CastingEnvironment env) throws Mishap {
        return DefaultImpls.executeWithOpCount(this,args,env);
    }

    @Override
    public @NotNull OperationResult operate(@NotNull CastingEnvironment env, @NotNull CastingImage image, @NotNull SpellContinuation continuation) {
        return DefaultImpls.operate(this,env,image,continuation);
    }
}
