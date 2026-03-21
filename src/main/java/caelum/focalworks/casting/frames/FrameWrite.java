package caelum.focalworks.casting.frames;

import at.petrak.hexcasting.api.casting.eval.CastResult;
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.eval.vm.ContinuationFrame;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds;
import caelum.focalworks.Focalworks;
import kotlin.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class FrameWrite implements ContinuationFrame {

    private final Object target;
    private final String consumer;
    private final String serializer;

    public FrameWrite(Object target, String consumerId, String targetSerializer) {
        this.target = target;
        this.consumer = consumerId;
        this.serializer = targetSerializer;
    }


    @Override
    public @NotNull CastResult evaluate(@NotNull SpellContinuation continuation, @NotNull ServerLevel world, @NotNull CastingVM vm) {

        Focalworks.WriteConsumer consumer1 = Focalworks.WRITE_CONSUMER_HASHMAP.get(consumer);
        consumer1.consume(continuation, world, vm, target);

        return new CastResult(
                new NullIota(),
                continuation,
                null,
                List.of(),
                ResolvedPatternType.EVALUATED,
                HexEvalSounds.NOTHING
        );
    }

    @Override
    public @NotNull Pair<Boolean, List<Iota>> breakDownwards(@NotNull List<? extends Iota> stack) {
        return new Pair<Boolean, List<Iota>>(true, (List<Iota>) stack);
    }

    @Override
    public @NotNull CompoundTag serializeToNBT() {
        final CompoundTag tag = new CompoundTag();
        final Focalworks.WriteTargetSerializer TargetSerializer = Focalworks.WRITE_TARGET_SERIALIZER_HASHMAP.get(serializer);
        TargetSerializer.write(tag,target);
        NBTHelper.putString(tag,"serializer",serializer);
        NBTHelper.putString(tag,"consumer",consumer);
        return tag;
    }

    @Override
    public int size() {
        return 0;
    }

    public static final Type<FrameWrite> TYPE = new Type<>() {
        @Override
        public @Nullable FrameWrite deserializeFromNBT(@NotNull CompoundTag tag, @NotNull ServerLevel world) {
            String serializer = NBTHelper.getString(tag, "serializer");
            final Focalworks.WriteTargetSerializer<?> targetDeserializer = Focalworks.WRITE_TARGET_SERIALIZER_HASHMAP.get(serializer);
            Object target = targetDeserializer.read(tag, world);
            String consumer = NBTHelper.getString(tag, "consumer");
            return new FrameWrite(target, consumer, serializer);
        }
    };
    @Override
    public @NotNull Type<FrameWrite> getType() {return TYPE;}
}
