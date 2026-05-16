package gr.ionio.pijonmod;

import com.mojang.logging.LogUtils;
import gr.ionio.pijonmod.client.StinkOverlay;
import gr.ionio.pijonmod.init.ModEffects;
import gr.ionio.pijonmod.init.ModItems;
import gr.ionio.pijonmod.init.ModPotions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
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
            event.accept(gr.ionio.pijonmod.init.ModItems.PIJON_SPAWN_EGG);
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
        public static void registerRenderers(net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(gr.ionio.pijonmod.init.ModEntities.PIJON.get(), gr.ionio.pijonmod.client.renderer.PijonRenderer::new);
            event.registerEntityRenderer(gr.ionio.pijonmod.init.ModEntities.PIJON_POOP_PROJECTILE.get(), net.minecraft.client.renderer.entity.ThrownItemRenderer::new);
        }

        @SubscribeEvent
        public static void registerLayerDefinitions(net.minecraftforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(gr.ionio.pijonmod.client.model.PijonModel.LAYER_LOCATION, gr.ionio.pijonmod.client.model.PijonModel::createBodyLayer);
        }

        // (Διαγράφηκε από εδώ το λάθος onRenderTick)
    }

    // ==========================================
    // IN-GAME EVENTS (FORGE BUS)
    // ==========================================

    // 1. Γραφικά (Τρέχει ΜΟΝΟ στον Client για να μην κρασάρει ο Server)
    @Mod.EventBusSubscriber(modid = "pijonmod", bus = net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.FORGE, value = net.minecraftforge.api.distmarker.Dist.CLIENT)
    public static class ClientForgeEvents {
        @SubscribeEvent
        public static void onRenderTick(net.minecraftforge.event.TickEvent.RenderTickEvent event) {
            if (event.phase == net.minecraftforge.event.TickEvent.Phase.END) {
                gr.ionio.pijonmod.client.StinkOverlay.renderStink();
            }
        }
    }

    // 2. Λογική Παιχνιδιού (Τρέχει παντού, Client & Server)
    @net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid = "pijonmod", bus = net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.FORGE)
    public static class CommonForgeEvents {
        @net.minecraftforge.eventbus.api.SubscribeEvent
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