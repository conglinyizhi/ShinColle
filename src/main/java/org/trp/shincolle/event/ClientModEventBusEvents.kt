package org.trp.shincolle.event

import net.minecraft.client.gui.screens.MenuScreens.ScreenConstructor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.ParticleEngine.SpriteParticleRegistration
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.client.renderer.item.ItemPropertyFunction
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModContainer
import net.neoforged.fml.ModList
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.EntityRenderersEvent.AddLayers
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent
import net.neoforged.neoforge.client.gui.IConfigScreenFactory
import org.trp.shincolle.Shincolle
import org.trp.shincolle.block.entity.DeskBlockEntity
import org.trp.shincolle.block.entity.LargeShipyardBlockEntity
import org.trp.shincolle.block.entity.SmallShipyardBlockEntity
import org.trp.shincolle.client.gui.ShincolleConfigScreen
import org.trp.shincolle.client.model.*
import org.trp.shincolle.client.particle.*
import org.trp.shincolle.client.particle.ParticleTeam.RenderStyle
import org.trp.shincolle.client.renderer.*
import org.trp.shincolle.client.renderer.RendererSimpleMob
import org.trp.shincolle.client.renderer.block.RenderDesk
import org.trp.shincolle.client.renderer.layer.ShipHeldItemLayer
import org.trp.shincolle.client.screen.*
import org.trp.shincolle.client.tooltip.ScaledTextClientTooltip
import org.trp.shincolle.entity.*
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.entity.projectile.EntityAbyssMissile
import org.trp.shincolle.entity.projectile.EntityProjectileBeam
import org.trp.shincolle.init.ModBlockEntities
import org.trp.shincolle.init.ModEntities
import org.trp.shincolle.init.ModItems
import org.trp.shincolle.menu.ModMenus
import org.trp.shincolle.init.ModParticles
import org.trp.shincolle.item.*
import org.trp.shincolle.menu.*
import java.util.function.Function
import java.util.function.Supplier

@EventBusSubscriber(modid = Shincolle.MODID, value = [Dist.CLIENT])
object ClientModEventBusEvents {
    private const val DEFAULT_MODEL_SCALE = 0.34f
    private val LEGACY_VARIANT_MODEL_PROPERTY: ResourceLocation =
        ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "legacy_variant")

    private fun entityTexture(name: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/$name.png")
    }

    @SubscribeEvent
    fun onClientSetup(event: FMLClientSetupEvent) {
        event.enqueueWork {
            ModList.get().getModContainerById(Shincolle.MODID).ifPresent { c: ModContainer ->
                c.registerExtensionPoint(
                    IConfigScreenFactory::class.java,
                    IConfigScreenFactory { _: ModContainer, parentScreen: Screen? ->
                        ShincolleConfigScreen.tryCreate(parentScreen)
                    }
                )
            }
            registerLegacyVariantProperty(ModItems.EQUIP_AIRPLANE.get() ?: return@enqueueWork)
            registerLegacyVariantProperty(ModItems.EQUIP_CANNON.get() ?: return@enqueueWork)
            registerLegacyVariantProperty(ModItems.EQUIP_DRUM.get() ?: return@enqueueWork)
            registerLegacyVariantProperty(ModItems.SHIP_TANK.get() ?: return@enqueueWork)
            registerLegacyVariantProperty(ModItems.COMBAT_RATION.get() ?: return@enqueueWork)
            registerLegacyVariantProperty(ModItems.POINTER_ITEM.get() ?: return@enqueueWork)
            registerLegacyVariantProperty(ModItems.GRUDGE.get() ?: return@enqueueWork)
            registerLegacyVariantProperty(ModItems.ABYSS_NUGGET.get() ?: return@enqueueWork)
        }
    }

    private fun registerLegacyVariantProperty(item: Item) {
        ItemProperties.register(
            item,
            LEGACY_VARIANT_MODEL_PROPERTY,
            ItemPropertyFunction { stack: ItemStack, _: ClientLevel?, _: LivingEntity?, _: Int ->
                val stackItem = stack.item
                when (stackItem) {
                    is LegacyEquipItem -> stackItem.getModelVariant(stack).toFloat()
                    is ShipTankItem -> stackItem.getModelVariant(stack).toFloat()
                    is CombatRationItem -> stackItem.getModelVariant(stack).toFloat()
                    is PointerItem -> stackItem.getModelVariant(stack).toFloat()
                    is GrudgeItem -> stackItem.getModelVariant(stack).toFloat()
                    is AbyssNuggetItem -> stackItem.getModelVariant(stack).toFloat()
                    else -> 0.0f
                }
            }
        )
    }

    @SubscribeEvent
    fun registerRenderers(event: RegisterRenderers) {
        event.registerEntityRenderer(ModEntities.DESTROYER_I.get()!!) { context -> RendererDestroyerI(context) }
        event.registerEntityRenderer(ModEntities.DESTROYER_RO.get()!!) { context -> RendererDestroyerRo(context) }
        event.registerEntityRenderer(ModEntities.DESTROYER_HA.get()!!) { context -> RendererDestroyerHa(context) }
        event.registerEntityRenderer(ModEntities.DESTROYER_NI.get()!!) { context -> RendererDestroyerNi(context) }
        event.registerEntityRenderer(ModEntities.HEAVY_CRUISER_RI.get()!!) { context -> RendererHeavyCruiserRi(context) }
        event.registerEntityRenderer(ModEntities.HEAVY_CRUISER_NE.get()!!) { context -> RendererHeavyCruiserNe(context) }
        event.registerEntityRenderer(ModEntities.CARRIER_WO.get()!!) { context -> RendererCarrierWo(context) }
        event.registerEntityRenderer(ModEntities.BATTLESHIP_RU.get()!!) { context -> RendererBattleshipRu(context) }
        event.registerEntityRenderer(ModEntities.BATTLESHIP_TA.get()!!) { context -> RendererBattleshipTa(context) }
        event.registerEntityRenderer(ModEntities.BATTLESHIP_RE.get()!!) { context -> RendererBattleshipRe(context) }
        event.registerEntityRenderer(ModEntities.TRANSPORT_WA.get()!!) { context -> RendererTransportWa(context) }
        event.registerEntityRenderer(ModEntities.SUBM_KA.get()!!) { context -> RendererSubmKa(context) }
        event.registerEntityRenderer(ModEntities.SUBM_YO.get()!!) { context -> RendererSubmYo(context) }
        event.registerEntityRenderer(ModEntities.SUBM_SO.get()!!) { context -> RendererSubmSo(context) }
        event.registerEntityRenderer(ModEntities.DESTROYER_HIME.get()!!) { context -> RendererDestroyerHime(context) }
        event.registerEntityRenderer(ModEntities.CA_HIME.get()!!) { context -> RendererCAHime(context) }
        event.registerEntityRenderer(ModEntities.AIRFIELD_HIME.get()!!) { context -> RendererAirfieldHime(context) }
        event.registerEntityRenderer(ModEntities.BATTLESHIP_HIME.get()!!) { context -> RendererBattleshipHime(context) }
        event.registerEntityRenderer(ModEntities.CARRIER_HIME.get()!!) { context -> RendererCarrierHime(context) }
        event.registerEntityRenderer(ModEntities.HARBOUR_HIME.get()!!) { context -> RendererHarbourHime(context) }
        event.registerEntityRenderer(ModEntities.ISOLATED_HIME.get()!!) { context -> RendererIsolatedHime(context) }
        event.registerEntityRenderer(ModEntities.MIDWAY_HIME.get()!!) { context -> RendererMidwayHime(context) }
        event.registerEntityRenderer(ModEntities.NORTHERN_HIME.get()!!) { context -> RendererNorthernHime(context) }
        event.registerEntityRenderer(ModEntities.SUBM_HIME.get()!!) { context -> RendererSubmHime(context) }
        event.registerEntityRenderer(ModEntities.SSNH.get()!!) { context -> RendererSSNH(context) }
        event.registerEntityRenderer(ModEntities.DESTROYER_AKATSUKI.get()!!) { context -> RendererDestroyerAkatsuki(context) }
        event.registerEntityRenderer(ModEntities.DESTROYER_HIBIKI.get()!!) { context -> RendererDestroyerHibiki(context) }
        event.registerEntityRenderer(ModEntities.DESTROYER_IKAZUCHI.get()!!) { context -> RendererDestroyerIkazuchi(context) }
        event.registerEntityRenderer(ModEntities.DESTROYER_INAZUMA.get()!!) { context -> RendererDestroyerInazuma(context) }
        event.registerEntityRenderer(ModEntities.DESTROYER_SHIMAKAZE.get()!!) { context -> RendererDestroyerShimakaze(context) }
        event.registerEntityRenderer(ModEntities.CARRIER_W_DEMON.get()!!) { context -> RendererCarrierWDemon(context) }
        event.registerEntityRenderer(ModEntities.CRUISER_TENRYUU.get()!!) { context -> RendererCruiserTenryuu(context) }
        event.registerEntityRenderer(ModEntities.CRUISER_TATSUTA.get()!!) { context -> RendererCruiserTatsuta(context) }
        event.registerEntityRenderer(ModEntities.CRUISER_TAKAO.get()!!) { context -> RendererCruiserTakao(context) }
        event.registerEntityRenderer(ModEntities.CRUISER_ATAGO.get()!!) { context -> RendererCruiserAtago(context) }
        event.registerEntityRenderer(ModEntities.CARRIER_KAGA.get()!!) { context -> RendererCarrierKaga(context) }
        event.registerEntityRenderer(ModEntities.CARRIER_AKAGI.get()!!) { context -> RendererCarrierAkagi(context) }
        event.registerEntityRenderer(ModEntities.BB_KONGOU.get()!!) { context -> RendererBBKongou(context) }
        event.registerEntityRenderer(ModEntities.BB_HIEI.get()!!) { context -> RendererBBHiei(context) }
        event.registerEntityRenderer(ModEntities.BB_HARUNA.get()!!) { context -> RendererBBHaruna(context) }
        event.registerEntityRenderer(ModEntities.BB_KIRISHIMA.get()!!) { context -> RendererBBKirishima(context) }
        event.registerEntityRenderer(ModEntities.BATTLESHIP_NAGATO.get()!!) { context -> RendererBattleshipNagato(context) }
        event.registerEntityRenderer(ModEntities.BATTLESHIP_YAMATO.get()!!) { context -> RendererBattleshipYamato(context) }
        event.registerEntityRenderer(ModEntities.SUBM_U511.get()!!) { context -> RendererSubmU511(context) }
        event.registerEntityRenderer(ModEntities.SUBM_RO500.get()!!) { context -> RendererSubmRo500(context) }

        event.registerEntityRenderer(ModEntities.AIRPLANE.get()!!) { context ->
            @Suppress("UNCHECKED_CAST")
            RendererSimpleMob(
                context,
                ModelAirplane(context.bakeLayer(ModelAirplane.LAYER_LOCATION)),
                0.5f,
                DEFAULT_MODEL_SCALE,
                entityTexture("airplane")
            ) as net.minecraft.client.renderer.entity.EntityRenderer<net.minecraft.world.entity.Entity>
        }
        event.registerEntityRenderer(ModEntities.AIRPLANE_T.get()!!) { context ->
            @Suppress("UNCHECKED_CAST")
            RendererSimpleMob(
                context,
                ModelAirplaneT(context.bakeLayer(ModelAirplaneT.LAYER_LOCATION)),
                0.5f,
                DEFAULT_MODEL_SCALE,
                entityTexture("airplane_t")
            ) as net.minecraft.client.renderer.entity.EntityRenderer<net.minecraft.world.entity.Entity>
        }
        event.registerEntityRenderer(ModEntities.AIRPLANE_ZERO.get()!!) { context ->
            @Suppress("UNCHECKED_CAST")
            RendererSimpleMob(
                context,
                ModelAirplaneZero(context.bakeLayer(ModelAirplaneZero.LAYER_LOCATION)),
                0.5f,
                DEFAULT_MODEL_SCALE,
                entityTexture("airplane_zero")
            ) as net.minecraft.client.renderer.entity.EntityRenderer<net.minecraft.world.entity.Entity>
        }
        event.registerEntityRenderer(ModEntities.MOUNT_SU_H.get()!!) { context: EntityRendererProvider.Context -> RendererMountSuH<EntityMountSuH>(context) }
        event.registerEntityRenderer(ModEntities.MOUNT_MI_H.get()!!) { context: EntityRendererProvider.Context -> RendererMountMiH<EntityMountMiH>(context) }
        event.registerEntityRenderer(ModEntities.MOUNT_IS_H.get()!!) { context: EntityRendererProvider.Context -> RendererMountIsH<EntityMountIsH>(context) }
        event.registerEntityRenderer(ModEntities.MOUNT_HB_H.get()!!) { context: EntityRendererProvider.Context -> RendererMountHbH<EntityMountHbH>(context) }
        event.registerEntityRenderer(ModEntities.MOUNT_CA_WD.get()!!) { context: EntityRendererProvider.Context -> RendererMountCaWD<EntityMountCaWD>(context) }
        event.registerEntityRenderer(ModEntities.MOUNT_AF_H.get()!!) { context: EntityRendererProvider.Context -> RendererMountAfH<EntityMountAfH>(context) }
        event.registerEntityRenderer(ModEntities.MOUNT_BA_H.get()!!) { context: EntityRendererProvider.Context -> RendererMountBaH<EntityMountBaH>(context) }
        event.registerEntityRenderer(ModEntities.MOUNT_CA_H.get()!!) { context: EntityRendererProvider.Context -> RendererMountCaH<EntityMountCaH>(context) }
        event.registerEntityRenderer(ModEntities.RENSOUHOU.get()!!) { context ->
            @Suppress("UNCHECKED_CAST")
            RendererSimpleMob(
                context,
                ModelRensouhou(context.bakeLayer(ModelRensouhou.LAYER_LOCATION)),
                0.5f,
                DEFAULT_MODEL_SCALE,
                entityTexture("rensouhou")
            ) as net.minecraft.client.renderer.entity.EntityRenderer<net.minecraft.world.entity.Entity>
        }
        event.registerEntityRenderer(ModEntities.RENSOUHOU_S.get()!!) { context: EntityRendererProvider.Context -> RendererRensouhouS<EntityRensouhouS>(context) }
        event.registerEntityRenderer(ModEntities.TAKOYAKI.get()!!) { context: EntityRendererProvider.Context -> RendererTakoyaki<EntityTakoyaki>(context) }
        event.registerEntityRenderer(ModEntities.ABYSS_MISSILE.get()!!) { context -> RendererAbyssMissile(context) }
        event.registerEntityRenderer(ModEntities.PROJECTILE_BEAM.get()!!) { context -> RendererProjectileBeam(context) }
        event.registerEntityRenderer(ModEntities.SHIP_GRUDGE.get()!!) { context -> RendererShipGrudge(context) }
        event.registerEntityRenderer(ModEntities.SHIP_FISHING_HOOK.get()!!) { context -> RendererShipFishingHook(context) }

        event.registerBlockEntityRenderer(ModBlockEntities.SMALL_SHIPYARD.get()!!) { context: BlockEntityRendererProvider.Context -> org.trp.shincolle.client.renderer.block.RenderSmallShipyard(context) }
        event.registerBlockEntityRenderer(ModBlockEntities.LARGE_SHIPYARD.get()!!) { context: BlockEntityRendererProvider.Context -> org.trp.shincolle.client.renderer.block.RenderLargeShipyard(context) }
        event.registerBlockEntityRenderer(ModBlockEntities.DESK.get()!!) { context: BlockEntityRendererProvider.Context -> RenderDesk(context) }
    }

    @SubscribeEvent
    fun addRenderLayers(event: AddLayers) {
        for (entry in ModEntities.ENTITY_TYPES.entries) {
            val type: EntityType<*> = entry.get() ?: continue
            val renderer: EntityRenderer<*>? = event.getRenderer(type)
            if (renderer is LivingEntityRenderer<*, *>
                && renderer.model is ShipModelBaseAdv<*>
            ) {
                @Suppress("UNCHECKED_CAST")
                addHeldItemLayerUnchecked(renderer as LivingEntityRenderer<out LivingEntity, out net.minecraft.client.model.EntityModel<out LivingEntity>>)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun addHeldItemLayerUnchecked(
        renderer: LivingEntityRenderer<out LivingEntity, out net.minecraft.client.model.EntityModel<out LivingEntity>>
    ) {
        val castRenderer = renderer as LivingEntityRenderer<EntityShipBase, net.minecraft.client.model.EntityModel<EntityShipBase>>
        castRenderer.addLayer(ShipHeldItemLayer(castRenderer))
    }

    @SubscribeEvent
    fun registerLayerDefinitions(event: RegisterLayerDefinitions) {
        event.registerLayerDefinition(ModelDestroyerI.LAYER_LOCATION) { ModelDestroyerI.createBodyLayer() }
        event.registerLayerDefinition(ModelDestroyerRo.LAYER_LOCATION) { ModelDestroyerRo.createBodyLayer() }
        event.registerLayerDefinition(ModelDestroyerHa.LAYER_LOCATION) { ModelDestroyerHa.createBodyLayer() }
        event.registerLayerDefinition(ModelDestroyerNi.LAYER_LOCATION) { ModelDestroyerNi.createBodyLayer() }
        event.registerLayerDefinition(ModelHeavyCruiserRi.LAYER_LOCATION) { ModelHeavyCruiserRi.createBodyLayer() }
        event.registerLayerDefinition(ModelHeavyCruiserNe.LAYER_LOCATION) { ModelHeavyCruiserNe.createBodyLayer() }
        event.registerLayerDefinition(ModelCarrierWo.LAYER_LOCATION) { ModelCarrierWo.createBodyLayer() }
        event.registerLayerDefinition(ModelBattleshipRu.LAYER_LOCATION) { ModelBattleshipRu.createBodyLayer() }
        event.registerLayerDefinition(ModelBattleshipTa.LAYER_LOCATION) { ModelBattleshipTa.createBodyLayer() }
        event.registerLayerDefinition(ModelBattleshipRe.LAYER_LOCATION) { ModelBattleshipRe.createBodyLayer() }
        event.registerLayerDefinition(ModelTransportWa.LAYER_LOCATION) { ModelTransportWa.createBodyLayer() }
        event.registerLayerDefinition(ModelSubmKa.LAYER_LOCATION) { ModelSubmKa.createBodyLayer() }
        event.registerLayerDefinition(ModelSubmYo.LAYER_LOCATION) { ModelSubmYo.createBodyLayer() }
        event.registerLayerDefinition(ModelSubmSo.LAYER_LOCATION) { ModelSubmSo.createBodyLayer() }
        event.registerLayerDefinition(ModelDestroyerHime.LAYER_LOCATION) { ModelDestroyerHime.createBodyLayer() }
        event.registerLayerDefinition(ModelCAHime.LAYER_LOCATION) { ModelCAHime.createBodyLayer() }
        event.registerLayerDefinition(ModelAirfieldHime.LAYER_LOCATION) { ModelAirfieldHime.createBodyLayer() }
        event.registerLayerDefinition(ModelBattleshipHime.LAYER_LOCATION) { ModelBattleshipHime.createBodyLayer() }
        event.registerLayerDefinition(ModelCarrierHime.LAYER_LOCATION) { ModelCarrierHime.createBodyLayer() }
        event.registerLayerDefinition(ModelHarbourHime.LAYER_LOCATION) { ModelHarbourHime.createBodyLayer() }
        event.registerLayerDefinition(ModelIsolatedHime.LAYER_LOCATION) { ModelIsolatedHime.createBodyLayer() }
        event.registerLayerDefinition(ModelMidwayHime.LAYER_LOCATION) { ModelMidwayHime.createBodyLayer() }
        event.registerLayerDefinition(ModelNorthernHime.LAYER_LOCATION) { ModelNorthernHime.createBodyLayer() }
        event.registerLayerDefinition(ModelSubmHime.LAYER_LOCATION) { ModelSubmHime.createBodyLayer() }
        event.registerLayerDefinition(ModelSSNH.LAYER_LOCATION) { ModelSSNH.createBodyLayer() }
        event.registerLayerDefinition(ModelCarrierWDemon.LAYER_LOCATION) { ModelCarrierWDemon.createBodyLayer() }
        event.registerLayerDefinition(ModelDestroyerAkatsuki.LAYER_LOCATION) { ModelDestroyerAkatsuki.createBodyLayer() }
        event.registerLayerDefinition(ModelDestroyerHibiki.LAYER_LOCATION) { ModelDestroyerHibiki.createBodyLayer() }
        event.registerLayerDefinition(ModelDestroyerIkazuchi.LAYER_LOCATION) { ModelDestroyerIkazuchi.createBodyLayer() }
        event.registerLayerDefinition(ModelDestroyerInazuma.LAYER_LOCATION) { ModelDestroyerInazuma.createBodyLayer() }
        event.registerLayerDefinition(ModelDestroyerShimakaze.LAYER_LOCATION) { ModelDestroyerShimakaze.createBodyLayer() }
        event.registerLayerDefinition(ModelCruiserTenryuu.LAYER_LOCATION) { ModelCruiserTenryuu.createBodyLayer() }
        event.registerLayerDefinition(ModelCruiserTatsuta.LAYER_LOCATION) { ModelCruiserTatsuta.createBodyLayer() }
        event.registerLayerDefinition(ModelCruiserTakao.LAYER_LOCATION) { ModelCruiserTakao.createBodyLayer() }
        event.registerLayerDefinition(ModelCruiserAtago.LAYER_LOCATION) { ModelCruiserAtago.createBodyLayer() }
        event.registerLayerDefinition(ModelCarrierKaga.LAYER_LOCATION) { ModelCarrierKaga.createBodyLayer() }
        event.registerLayerDefinition(ModelCarrierAkagi.LAYER_LOCATION) { ModelCarrierAkagi.createBodyLayer() }
        event.registerLayerDefinition(ModelBBKongou.LAYER_LOCATION) { ModelBBKongou.createBodyLayer() }
        event.registerLayerDefinition(ModelBBHiei.LAYER_LOCATION) { ModelBBHiei.createBodyLayer() }
        event.registerLayerDefinition(ModelBBHaruna.LAYER_LOCATION) { ModelBBHaruna.createBodyLayer() }
        event.registerLayerDefinition(ModelBBKirishima.LAYER_LOCATION) { ModelBBKirishima.createBodyLayer() }
        event.registerLayerDefinition(ModelBattleshipNagato.LAYER_LOCATION) { ModelBattleshipNagato.createBodyLayer() }
        event.registerLayerDefinition(ModelBattleshipYamato.LAYER_LOCATION) { ModelBattleshipYamato.createBodyLayer() }
        event.registerLayerDefinition(ModelSubmU511.LAYER_LOCATION) { ModelSubmU511.createBodyLayer() }
        event.registerLayerDefinition(ModelSubmRo500.LAYER_LOCATION) { ModelSubmRo500.createBodyLayer() }

        event.registerLayerDefinition(ModelAirplane.LAYER_LOCATION) { ModelAirplane.createBodyLayer() }
        event.registerLayerDefinition(ModelAirplaneT.LAYER_LOCATION) { ModelAirplaneT.createBodyLayer() }
        event.registerLayerDefinition(ModelAirplaneZero.LAYER_LOCATION) { ModelAirplaneZero.createBodyLayer() }
        event.registerLayerDefinition(ModelMountAfH.LAYER_LOCATION) { ModelMountAfH.createBodyLayer() }
        event.registerLayerDefinition(ModelMountBaH.LAYER_LOCATION) { ModelMountBaH.createBodyLayer() }
        event.registerLayerDefinition(ModelMountCaH.LAYER_LOCATION) { ModelMountCaH.createBodyLayer() }
        event.registerLayerDefinition(ModelMountCaWD.LAYER_LOCATION) { ModelMountCaWD.createBodyLayer() }
        event.registerLayerDefinition(ModelMountHbH.LAYER_LOCATION) { ModelMountHbH.createBodyLayer() }
        event.registerLayerDefinition(ModelMountIsH.LAYER_LOCATION) { ModelMountIsH.createBodyLayer() }
        event.registerLayerDefinition(ModelMountMiH.LAYER_LOCATION) { ModelMountMiH.createBodyLayer() }
        event.registerLayerDefinition(ModelMountSuH.LAYER_LOCATION) { ModelMountSuH.createBodyLayer() }
        event.registerLayerDefinition(ModelRensouhou.LAYER_LOCATION) { ModelRensouhou.createBodyLayer() }
        event.registerLayerDefinition(ModelRensouhouS.LAYER_LOCATION) { ModelRensouhouS.createBodyLayer() }
        event.registerLayerDefinition(ModelTakoyaki.LAYER_LOCATION) { ModelTakoyaki.createBodyLayer() }
        event.registerLayerDefinition(ModelAbyssMissile.LAYER_LOCATION) { ModelAbyssMissile.createBodyLayer() }
        event.registerLayerDefinition(ModelShipGrudge.LAYER_LOCATION) { ModelShipGrudge.createBodyLayer() }
        event.registerLayerDefinition(ModelSmallShipyard.LAYER_LOCATION) { ModelSmallShipyard.createBodyLayer() }
        event.registerLayerDefinition(ModelLargeShipyard.LAYER_LOCATION) { ModelLargeShipyard.createBodyLayer() }
        event.registerLayerDefinition(ModelVortex.LAYER_LOCATION) { ModelVortex.createBodyLayer() }
        event.registerLayerDefinition(ModelBlockDesk.LAYER_LOCATION) { ModelBlockDesk.createBodyLayer() }
        event.registerLayerDefinition(ModelBlockDeskLarge.LAYER_LOCATION) { ModelBlockDeskLarge.createBodyLayer() }
    }

    @SubscribeEvent
    fun registerScreens(event: RegisterMenuScreensEvent) {
        event.register(ModMenus.SHIP_MENU.get()!!, ScreenConstructor { menu, playerInv, title ->
            ShipInventoryScreen(menu!!, playerInv, title)
        })
        event.register(ModMenus.SMALL_SHIPYARD_MENU.get()!!, ScreenConstructor { menu, playerInventory, title ->
            SmallShipyardScreen(menu!!, playerInventory, title)
        })
        event.register(ModMenus.LARGE_SHIPYARD_MENU.get()!!, ScreenConstructor { menu, playerInventory, title ->
            LargeShipyardScreen(menu!!, playerInventory, title)
        })
        event.register(ModMenus.DESK_MENU.get()!!, ScreenConstructor { menu, inventory, title ->
            DeskScreen(menu!!, inventory, title)
        })
        event.register(ModMenus.VOL_CORE_MENU.get()!!, ScreenConstructor { menu, inventory, title ->
            VolCoreScreen(menu!!, inventory, title)
        })
        event.register(ModMenus.CRANE_MENU.get()!!, ScreenConstructor { menu, playerInventory, title ->
            CraneScreen(menu!!, playerInventory, title)
        })
        event.register(ModMenus.FORMATION.get()!!, ScreenConstructor { menu, playerInventory, title ->
            FormationScreen(menu!!, playerInventory, title)
        })
        event.register(ModMenus.RECIPE_PAPER_MENU.get()!!, ScreenConstructor { menu, inventory, title ->
            RecipePaperScreen(menu!!, inventory, title)
        })
    }

    @SubscribeEvent
    fun registerParticles(event: RegisterParticleProvidersEvent) {
        event.registerSpriteSet(ModParticles.PARTICLE_EMOTION.get()!!) { sprites -> ParticleEmotion.Provider(sprites) }
        event.registerSpriteSet(ModParticles.PARTICLE_HEAL_SPARKLE.get()!!) { sprites -> ParticleHealSparkle.Provider(sprites) }
        event.registerSpriteSet(ModParticles.PARTICLE_GODDESS.get()!!) { sprites -> ParticleGoddess.Provider(sprites) }
        event.registerSpriteSet(ModParticles.PARTICLE_TEXTS.get()!!) { sprites -> ParticleTexts.Provider(sprites) }
        event.registerSpriteSet(ModParticles.PARTICLE_LIGHTNING.get()!!) { sprites -> ParticleLightning.Provider(sprites) }
        event.registerSpriteSet(ModParticles.PARTICLE_SPRAY_RED.get()!!) { sprites -> ParticleSprayRed.Provider(sprites) }
        event.registerSpriteSet(ModParticles.PARTICLE_SPRAY.get()!!) { sprites -> ParticleSpray.Provider(sprites) }
        event.registerSpriteSet(ModParticles.PARTICLE_CRANING.get()!!) { sprites -> ParticleCraning.Provider(sprites) }
        event.registerSpriteSet(ModParticles.PARTICLE_TEAM.get()!!) { sprites -> ParticleTeam.Provider(sprites) }
        event.registerSpriteSet(ModParticles.PARTICLE_TEAM_SELECTED.get()!!) { sprites ->
            ParticleTeam.Provider(sprites, RenderStyle.DEFAULT_BLUE)
        }
        event.registerSpriteSet(ModParticles.PARTICLE_TEAM_SELECTED_RED.get()!!) { sprites ->
            ParticleTeam.Provider(sprites, RenderStyle.SELECTED_RED)
        }
        event.registerSpriteSet(ModParticles.PARTICLE_TEAM_SELECTED_YELLOW.get()!!) { sprites ->
            ParticleTeam.Provider(sprites, RenderStyle.SELECTED_YELLOW)
        }
        event.registerSpriteSet(ModParticles.PARTICLE_TEAM_TARGET.get()!!) { sprites ->
            ParticleTeam.Provider(sprites, RenderStyle.TARGET_WHITE)
        }
        event.registerSpriteSet(ModParticles.PARTICLE_TEAM_TARGET_ENTITY.get()!!) { sprites ->
            ParticleTeam.Provider(sprites, RenderStyle.TARGET_RED)
        }
        event.registerSpriteSet(ModParticles.PARTICLE_SPARKLE.get()!!) { sprites -> ParticleSparkle.Provider(sprites) }
        event.registerSpriteSet(ModParticles.PARTICLE_WAYPOINT.get()!!) { sprites -> ParticleWaypoint.Provider(sprites) }
        event.registerSpecial(ModParticles.PARTICLE_WAYPOINT_LINE.get()!!, ParticlePointerLine.Provider(0, null))
        event.registerSpecial(ModParticles.PARTICLE_WAYPOINT_LINE_PURPLE.get()!!, ParticlePointerLine.Provider(1, null))
        event.registerSpecial(ModParticles.PARTICLE_WAYPOINT_LINE_RED.get()!!, ParticlePointerLine.Provider(2, null))
        event.registerSpriteSet(ModParticles.PARTICLE_CHI.get()!!) { sprites -> ParticleChi.Provider(sprites) }
        event.registerSpriteSet(ModParticles.PARTICLE_91TYPE.get()!!) { sprites -> Particle91Type.Provider(sprites) }
        event.registerSpriteSet(ModParticles.PARTICLE_CUBE.get()!!) { sprites -> ParticleCube.Provider(sprites) }
        event.registerSpecial(ModParticles.PARTICLE_BEAM.get()!!, ParticleBeam.Provider())
    }

    @SubscribeEvent
    fun registerTooltipComponents(event: RegisterClientTooltipComponentFactoriesEvent) {
        event.register(ScaledTextTooltipData::class.java) { data -> ScaledTextClientTooltip(data) }
    }
}
