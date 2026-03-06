package caelum.focalworks.registry;


import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.castables.Action;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import caelum.focalworks.casting.actions.ioticblocks.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import at.petrak.hexcasting.common.lib.hex.HexActions;
import caelum.focalworks.casting.actions.*;
import static caelum.focalworks.Focalworks.id;


public class FocalworksActions {
    public static void init() {
        register("rig_read","wwaqwqqqwqwqqwwqqeqqwwqq",HexDir.EAST,new OpRigRead());
        register("rig_write","wwdeweeeweweewweeqeewwee",HexDir.EAST,new OpRigWrite());
        register("crack_rig","wwaqwqqqwqedwewwewdawdwwwwdw",HexDir.EAST,new OpCrackRig());
        register("flip_spellbook","wwdeweeewewdqeeqdqeewwee",HexDir.EAST,new OpFlipSpellbook());
        register("read_spellbook_page","wwaqwqqqwqwqqwwqqeaeqqea",HexDir.EAST,new OpReadSpellbookPage());
        if (FabricLoader.getInstance().isModLoaded("ioticblocks")) {
            register("rig_write_index","wwdeweeeweqawqwawwwdwwdw",HexDir.EAST,new OpRigWriteIndex());
            register("rig_read_index","wwaqwqqqwqedwewwewewdwww",HexDir.EAST,new OpRigReadIndex());
        }
    }
    private static ActionRegistryEntry register(
            String name,
            String signature,
            HexDir startDir,
            Action action
    ) {
        return Registry.register(HexActions.REGISTRY, id(name), new ActionRegistryEntry(HexPattern.fromAngles(signature, startDir), action));
    }
}
