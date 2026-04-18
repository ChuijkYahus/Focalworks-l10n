package caelum.focalworks.api.components;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.utils.NBTHelper;
import dev.onyxstudios.cca.api.v3.component.ComponentFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class WriteComponent implements HexComponentInterface, ComponentFactory<BlockEntity, WriteComponent> {

    Iota hex;
    ServerLevel level;

    WriteComponent(Iota hex, ServerLevel level) {
        this.hex = hex;
        this.level = level;
    }

    @Override
    public Iota getHex() {
        return hex;
    }

    @Override
    public void setHex(Iota toSet) {
        hex = toSet;
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        hex = IotaType.deserialize(tag.getCompound("riggedwrite").copy(),level);
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        NBTHelper.putCompound(tag, "riggedwrite", IotaType.serialize(hex));
    }

    @Override
    public @NotNull WriteComponent createComponent(BlockEntity blockEntity) {
        return new WriteComponent(null, (ServerLevel) blockEntity.getLevel());
    }
}
