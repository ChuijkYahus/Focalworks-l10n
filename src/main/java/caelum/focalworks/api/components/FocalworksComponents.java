package caelum.focalworks.api.components;

import dev.onyxstudios.cca.api.v3.block.BlockComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.block.BlockComponentInitializer;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;

import static caelum.focalworks.Focalworks.id;


public final class FocalworksComponents implements ItemComponentInitializer, EntityComponentInitializer, BlockComponentInitializer {
    public static final ComponentKey<HexComponentInterface> RIGGED_COMPONENT =
            ComponentRegistry.getOrCreate(id("rigged"), HexComponentInterface.class);

    @Override
    public void registerBlockComponentFactories(BlockComponentFactoryRegistry registry) {
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
    }

    @Override
    public void registerItemComponentFactories(ItemComponentFactoryRegistry registry) {
    }

}
