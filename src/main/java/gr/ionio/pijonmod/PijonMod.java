package gr.ionio.pijonmod;

import com.mojang.logging.LogUtils;
import gr.ionio.pijonmod.client.StinkOverlay;
import gr.ionio.pijonmod.client.renderer.PijonRenderer;
import gr.ionio.pijonmod.init.ModEffects;
import gr.ionio.pijonmod.init.ModItems;
import gr.ionio.pijonmod.init.ModPotions;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.brewing.BrewingRecipeRegisterEvent;
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
        gr.ionio.pijonmod.init.ModPotions.register(modEventBus);
        gr.ionio.pijonmod.init.ModEffects.register(modEventBus);

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
            event.accept(ModItems.PIJON_SPAWN_EGG);
        }

        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.PIJON_POOP);
        }

        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(ModItems.PIJON_POOP);
        }

        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.GREY_FEATHER);
            event.accept(ModItems.BROWN_FEATHER);
            event.accept(ModItems.BROWN_GREY_FEATHER);
            event.accept(ModItems.WHITE_FEATHER);
            event.accept(ModItems.PURPLE_FEATHER);
            event.accept(ModItems.DOTTED_FEATHER);
            event.accept(ModItems.RED_FEATHER);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Pijon Mod successfully loaded!");
    }

    @SubscribeEvent
    public void onBrewingRecipes(BrewingRecipeRegisterEvent event) {
        event.getBuilder().addMix(
                Potions.AWKWARD,
                ModItems.PIJON_POOP.get(),
                ModPotions.STINK_POTION.getHolder().get()
        );
    }

    @Mod.EventBusSubscriber(modid = "pijonmod", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(gr.ionio.pijonmod.init.ModEntities.PIJON.get(), PijonRenderer::new);
            event.registerEntityRenderer(gr.ionio.pijonmod.init.ModEntities.PIJON_POOP_PROJECTILE.get(), ThrownItemRenderer::new);
        }

        @SubscribeEvent
        public static void registerLayerDefinitions(net.minecraftforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(gr.ionio.pijonmod.client.model.PijonModel.LAYER_LOCATION, gr.ionio.pijonmod.client.model.PijonModel::createBodyLayer);
        }
    }

    @Mod.EventBusSubscriber(modid = "pijonmod", bus = net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.FORGE, value = net.minecraftforge.api.distmarker.Dist.CLIENT)
    public static class ClientForgeEvents {
        @SubscribeEvent
        public static void onRenderGui(net.minecraftforge.client.event.CustomizeGuiOverlayEvent event) {
            gr.ionio.pijonmod.client.StinkOverlay.renderStink(event.getGuiGraphics());
        }
    }

    @Mod.EventBusSubscriber(modid = "pijonmod", bus = net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.FORGE)
    public static class CommonForgeEvents {
        @SubscribeEvent
        public static void onPlayerTick(net.minecraftforge.event.TickEvent.PlayerTickEvent event) {
            if (event.phase == net.minecraftforge.event.TickEvent.Phase.END) {
                net.minecraft.world.entity.player.Player player = event.player;
                if (player != null && player.hasEffect(gr.ionio.pijonmod.init.ModEffects.STINK.getHolder().get())) {
                    if (player.isInWater()) {
                        player.removeEffect(gr.ionio.pijonmod.init.ModEffects.STINK.getHolder().get());
                    }
                }
            }
        }
    }
}