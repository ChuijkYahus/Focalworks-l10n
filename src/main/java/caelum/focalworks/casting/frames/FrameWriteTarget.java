package caelum.focalworks.casting.frames;

import net.minecraft.nbt.CompoundTag;

public interface FrameWriteTarget {
    public void serialize(CompoundTag tag);

    public static Object deserialize(CompoundTag tag) {
        return null;
    }
}
