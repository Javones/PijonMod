package gr.ionio.pijonmod;

import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod("pijonmod")
public class PijonMod {

    private static final Logger LOGGER = LogUtils.getLogger();

    public PijonMod(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        gr.ionio.pijonmod.init.ModEntities.register(modEventBus);
        gr.ionio.pijonmod.init.ModItems.register(modEventBus);

        modEventBus.addListener(this::registerAttributes);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Pijon Mod is loading...");
    }

    private void registerAttributes(net.minecraftforge.event.entity.EntityAttributeCreationEvent event) {
        event.put(gr.ionio.pijonmod.init.ModEntities.PIJON.get(), gr.ionio.pijonmod.entity.Pijon.createAttributes().build());
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(gr.ionio.pijonmod.init.ModItems.PIJON_SPAWN_EGG);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Pijon Mod successfully loaded!");
    }

    @Mod.EventBusSubscriber(modid = "pijonmod", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void registerRenderers(net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(gr.ionio.pijonmod.init.ModEntities.PIJON.get(), gr.ionio.pijonmod.client.renderer.PijonRenderer::new);
        }

        @SubscribeEvent
        public static void registerLayerDefinitions(net.minecraftforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(gr.ionio.pijonmod.client.model.PijonModel.LAYER_LOCATION, gr.ionio.pijonmod.client.model.PijonModel::createBodyLayer);
        }
    }
}