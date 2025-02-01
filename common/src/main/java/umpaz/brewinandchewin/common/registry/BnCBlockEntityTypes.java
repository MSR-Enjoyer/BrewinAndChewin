package umpaz.brewinandchewin.common.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.common.block.entity.CoasterBlockEntity;
import umpaz.brewinandchewin.common.block.entity.KegBlockEntity;

public class BnCBlockEntityTypes {
    public static final BlockEntityType<KegBlockEntity> KEG = BlockEntityType.Builder.of(KegBlockEntity::new, BnCBlocks.KEG).build(null);
    public static final BlockEntityType<CoasterBlockEntity> COASTER = BlockEntityType.Builder.of(CoasterBlockEntity::new, BnCBlocks.COASTER).build(null);

    public static void registerAll() {
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, BrewinAndChewin.asResource("keg"), KEG);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, BrewinAndChewin.asResource("coaster"), COASTER);
    }
}
