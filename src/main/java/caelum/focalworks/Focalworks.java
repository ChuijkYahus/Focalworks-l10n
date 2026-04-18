package caelum.focalworks;

import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.SpellList;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import caelum.focalworks.registry.FocalworksActions;
import caelum.focalworks.registry.FocalworksBlockEntities;
import caelum.focalworks.registry.FocalworksBlocks;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.function.BiConsumer;

public class Focalworks implements ModInitializer {

    public interface WriteConsumer<T> {
        void consume(SpellContinuation continuation, ServerLevel level, CastingVM vm, T Target);
    }

    public interface WriteTargetSerializer<T> {
        T read(CompoundTag tag, ServerLevel world);
        void write(CompoundTag tag, T target);
    }

    public static final ThreadLocal<HashMap<String,Object>> CONTEXT = ThreadLocal.withInitial(HashMap::new);
    public static final HashMap<String, WriteConsumer<?>> WRITE_CONSUMER_HASHMAP = new HashMap<>();
    public static final HashMap<String, WriteTargetSerializer<?>> WRITE_TARGET_SERIALIZER_HASHMAP = new HashMap<>();



	public static final String MOD_ID = "focalworks";
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID,path);
    }

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
        FocalworksActions.init();
        FocalworksBlocks.registerBlocks(bind(BuiltInRegistries.BLOCK));
        FocalworksBlockEntities.registerTiles(bind(BuiltInRegistries.BLOCK_ENTITY_TYPE));
		LOGGER.info("I slipped and hit the init button!!!!");
	}
    public static int clamp(int val, int min, int max) {
        return Math.min(max,Math.max(val, min));
    }
    public static RenderedSpell emptyRenderedSpell = new RenderedSpell() {
        @Override
        public void cast(@NotNull CastingEnvironment castingEnvironment) {

        }

        @Override
        public @Nullable CastingImage cast(@NotNull CastingEnvironment castingEnvironment, @NotNull CastingImage castingImage) {
            return null;
        }
    };
    private static <T> BiConsumer<T, ResourceLocation> bind(Registry<T> registry) {
        return new BiConsumer<T, ResourceLocation>() {
            @Override
            public void accept(T t, ResourceLocation resourceLocation) {
                Registry.register(registry, resourceLocation, t);
            }
        };
    }
    public static SpellList modifySpellListAt(SpellList list, int index, Iota iota) {
        SpellList newList = list.modifyAt(index, new Function1() {
            @NotNull
            public final SpellList invoke(@NotNull SpellList it) {
                Intrinsics.checkNotNullParameter(it, "it");
                return (SpellList)(new SpellList.LPair(iota, it.getCdr()));
            }

            // $FF: synthetic method
            // $FF: bridge method
            public Object invoke(Object p1) {
                return this.invoke((SpellList)p1);
            }
        });
        return newList;
    }
}

