package com.ironfurnace;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Mod "Iron Furnace": uma fornalha de ferro que derrete itens/minérios mais rápido.
 */
@Mod(IronFurnaceMod.MODID)
public class IronFurnaceMod {
    public static final String MODID = "ironfurnace";

    /** Quantas vezes mais rápido que a fornalha vanilla. Mude aqui (2 = dobro, 3 = triplo...). */
    public static final int SPEED_MULTIPLIER = 2;

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }

    public static final RegistryObject<Block> IRON_FURNACE = BLOCKS.register("iron_furnace",
            () -> new IronFurnaceBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, id("iron_furnace")))
                    .mapColor(MapColor.METAL)
                    .strength(3.5F)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> state.getValue(BlockStateProperties.LIT) ? 13 : 0)));

    public static final RegistryObject<Item> IRON_FURNACE_ITEM = ITEMS.register("iron_furnace",
            () -> new BlockItem(IRON_FURNACE.get(), new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, id("iron_furnace")))
                    .useBlockDescriptionPrefix()));

    public static final RegistryObject<BlockEntityType<IronFurnaceBlockEntity>> IRON_FURNACE_BE =
            BLOCK_ENTITIES.register("iron_furnace",
                    () -> new BlockEntityType<>(IronFurnaceBlockEntity::new, IRON_FURNACE.get()));

    public IronFurnaceMod(FMLJavaModLoadingContext context) {
        BusGroup modBus = context.getModBusGroup();
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        BLOCK_ENTITIES.register(modBus);
        BuildCreativeModeTabContentsEvent.BUS.addListener(IronFurnaceMod::addToCreativeTab);
    }

    private static void addToCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(IRON_FURNACE_ITEM.get());
        }
    }
}
