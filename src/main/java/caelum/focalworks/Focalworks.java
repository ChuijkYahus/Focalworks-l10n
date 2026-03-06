package caelum.focalworks;

import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import caelum.focalworks.registry.FocalworksActions;

import java.util.HashMap;
import java.util.List;

public class Focalworks implements ModInitializer {
    public static final ThreadLocal<HashMap<String,Object>> CONTEXT = ThreadLocal.withInitial(HashMap::new);
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
}

