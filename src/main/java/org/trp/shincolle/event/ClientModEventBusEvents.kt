package org.trp.shincolle.event

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.MenuScreens.ScreenConstructor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.ParticleEngine.SpriteParticleRegistration
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.client.renderer.item.ItemPropertyFunction
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
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
import net.neoforged.neoforge.client.event.EntityRenderersEvent.*
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
import org.trp.shincolle.client.renderer.RendererSimpleMob
import org.trp.shincolle.client.renderer.block.RenderDesk
import org.trp.shincolle.client.renderer.layer.ShipHeldItemLayer
import org.trp.shincolle.client.screen.*
import org.trp.shincolle.entity.*
import org.trp.shincolle.entity.projectile.EntityAbyssMissile
import org.trp.shincolle.entity.projectile.EntityProjectileBeam
import org.trp.shincolle.init.ModBlockEntities
import org.trp.shincolle.init.ModEntities
import org.trp.shincolle.init.ModItems
import org.trp.shincolle.init.ModParticles
import org.trp.shincolle.item.*
import org.trp.shincolle.item.AbyssNuggetItem.getModelVariant
import org.trp.shincolle.item.CombatRationItem.getModelVariant
import org.trp.shincolle.item.GrudgeItem.getModelVariant
import org.trp.shincolle.item.LegacyEquipItem.getModelVariant
import org.trp.shincolle.item.PointerItem.getModelVariant
import org.trp.shincolle.item.ShipTankItem.getModelVariant
import org.trp.shincolle.menu.*
import java.util.function.Function
import java.util.function.Supplier

@EventBusSubscriber(modid = Shincolle.MODID, value = [Dist.CLIENT])
object ClientModEventBusEvents {
    private const val DEFAULT_MODEL_SCALE = 0.34f
    private val LEGACY_VARIANT_MODEL_PROPERTY: ResourceLocation =
        ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "legacy_variant")

    private fun entityTexture(name: String?): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/" + name + ".png")
    }

    @SubscribeEvent
    fun onClientSetup(event: FMLClientSetupEvent) {
        event.enqueueWork(Runnable {
            val mc = ModList.get().getModContainerById(Shincolle.MODID)
            mc.ifPresent { c: ModContainer? ->
                c!!.registerExtensionPoint<IConfigScreenFactory?>(
                    IConfigScreenFactory::class.java,
                    IConfigScreenFactory { modContainer: ModContainer?, parentScreen: Screen? ->
                        ShincolleConfigScreen.tryCreate(
                            parentScreen
                        )
                    }
                )
            }
            ClientModEventBusEvents.registerLegacyVariantProperty(ModItems.EQUIP_AIRPLANE.get())
            ClientModEventBusEvents.registerLegacyVariantProperty(ModItems.EQUIP_CANNON.get())
            ClientModEventBusEvents.registerLegacyVariantProperty(ModItems.EQUIP_DRUM.get())
            registerLegacyVariantProperty(ModItems.SHIP_TANK.get())
            registerLegacyVariantProperty(ModItems.COMBAT_RATION.get())
            registerLegacyVariantProperty(ModItems.POINTER_ITEM.get())
            registerLegacyVariantProperty(ModItems.GRUDGE.get())
            registerLegacyVariantProperty(ModItems.ABYSS_NUGGET.get())
        })
    }

    private fun registerLegacyVariantProperty(item: Item) {
        ItemProperties.register(
            item,
            LEGACY_VARIANT_MODEL_PROPERTY,
            ItemPropertyFunction { stack: ItemStack?, level: ClientLevel?, entity: LivingEntity?, seed: Int ->
                if (stack!!.getItem() is LegacyEquipItem) {
                    return@register legacyEquipItem.getModelVariant(stack)
                }
                if (stack.getItem() is ShipTankItem) {
                    return@register shipTankItem.getModelVariant(stack)
                }
                if (stack.getItem() is CombatRationItem) {
                    return@register combatRationItem.getModelVariant(stack)
                }
                if (stack.getItem() is PointerItem) {
                    return@register pointerItem.getModelVariant(stack)
                }
                if (stack.getItem() is GrudgeItem) {
                    return@register grudgeItem.getModelVariant(stack)
                }
                if (stack.getItem() is AbyssNuggetItem) {
                    return@register abyssNuggetItem.getModelVariant(stack)
                }
                0.0f
            })
    }

    @SubscribeEvent
    fun registerRenderers(event: RegisterRenderers) {
        event.registerEntityRenderer<EntityDestroyerI?>(
            ModEntities.DESTROYER_I.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererDestroyerI(context) })
        event.registerEntityRenderer<EntityDestroyerRo?>(
            ModEntities.DESTROYER_RO.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererDestroyerRo(context) })
        event.registerEntityRenderer<EntityDestroyerHa?>(
            ModEntities.DESTROYER_HA.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererDestroyerHa(context) })
        event.registerEntityRenderer<EntityDestroyerNi?>(
            ModEntities.DESTROYER_NI.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererDestroyerNi(context) })
        event.registerEntityRenderer<EntityHeavyCruiserRi?>(
            ModEntities.HEAVY_CRUISER_RI.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererHeavyCruiserRi(context) })
        event.registerEntityRenderer<EntityHeavyCruiserNe?>(
            ModEntities.HEAVY_CRUISER_NE.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererHeavyCruiserNe(context) })
        event.registerEntityRenderer<EntityCarrierWo?>(
            ModEntities.CARRIER_WO.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererCarrierWo(context) })
        event.registerEntityRenderer<EntityBattleshipRu?>(
            ModEntities.BATTLESHIP_RU.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererBattleshipRu(context) })
        event.registerEntityRenderer<EntityBattleshipTa?>(
            ModEntities.BATTLESHIP_TA.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererBattleshipTa(context) })
        event.registerEntityRenderer<EntityBattleshipRe?>(
            ModEntities.BATTLESHIP_RE.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererBattleshipRe(context) })
        event.registerEntityRenderer<EntityTransportWa?>(
            ModEntities.TRANSPORT_WA.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererTransportWa(context) })
        event.registerEntityRenderer<EntitySubmKa?>(
            ModEntities.SUBM_KA.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererSubmKa(context) })
        event.registerEntityRenderer<EntitySubmYo?>(
            ModEntities.SUBM_YO.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererSubmYo(context) })
        event.registerEntityRenderer<EntitySubmSo?>(
            ModEntities.SUBM_SO.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererSubmSo(context) })
        event.registerEntityRenderer<EntityDestroyerHime?>(
            ModEntities.DESTROYER_HIME.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererDestroyerHime(context) })
        event.registerEntityRenderer<EntityCAHime?>(
            ModEntities.CA_HIME.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererCAHime(context) })
        event.registerEntityRenderer<EntityAirfieldHime?>(
            ModEntities.AIRFIELD_HIME.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererAirfieldHime(context) })
        event.registerEntityRenderer<EntityBattleshipHime?>(
            ModEntities.BATTLESHIP_HIME.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererBattleshipHime(context) })
        event.registerEntityRenderer<EntityCarrierHime?>(
            ModEntities.CARRIER_HIME.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererCarrierHime(context) })
        event.registerEntityRenderer<EntityHarbourHime?>(
            ModEntities.HARBOUR_HIME.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererHarbourHime(context) })
        event.registerEntityRenderer<EntityIsolatedHime?>(
            ModEntities.ISOLATED_HIME.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererIsolatedHime(context) })
        event.registerEntityRenderer<EntityMidwayHime?>(
            ModEntities.MIDWAY_HIME.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererMidwayHime(context) })
        event.registerEntityRenderer<EntityNorthernHime?>(
            ModEntities.NORTHERN_HIME.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererNorthernHime(context) })
        event.registerEntityRenderer<EntitySubmHime?>(
            ModEntities.SUBM_HIME.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererSubmHime(context) })
        event.registerEntityRenderer<EntitySSNH?>(
            ModEntities.SSNH.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererSSNH(context) })
        event.registerEntityRenderer<EntityDestroyerAkatsuki?>(
            ModEntities.DESTROYER_AKATSUKI.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererDestroyerAkatsuki(context) })
        event.registerEntityRenderer<EntityDestroyerHibiki?>(
            ModEntities.DESTROYER_HIBIKI.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererDestroyerHibiki(context) })
        event.registerEntityRenderer<EntityDestroyerIkazuchi?>(
            ModEntities.DESTROYER_IKAZUCHI.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererDestroyerIkazuchi(context) })
        event.registerEntityRenderer<EntityDestroyerInazuma?>(
            ModEntities.DESTROYER_INAZUMA.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererDestroyerInazuma(context) })
        event.registerEntityRenderer<EntityDestroyerShimakaze?>(
            ModEntities.DESTROYER_SHIMAKAZE.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererDestroyerShimakaze(context) })
        event.registerEntityRenderer<EntityCarrierWDemon?>(
            ModEntities.CARRIER_W_DEMON.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererCarrierWDemon(context) })
        event.registerEntityRenderer<EntityCruiserTenryuu?>(
            ModEntities.CRUISER_TENRYUU.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererCruiserTenryuu(context) })
        event.registerEntityRenderer<EntityCruiserTatsuta?>(
            ModEntities.CRUISER_TATSUTA.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererCruiserTatsuta(context) })
        event.registerEntityRenderer<EntityCruiserTakao?>(
            ModEntities.CRUISER_TAKAO.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererCruiserTakao(context) })
        event.registerEntityRenderer<EntityCruiserAtago?>(
            ModEntities.CRUISER_ATAGO.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererCruiserAtago(context) })
        event.registerEntityRenderer<EntityCarrierKaga?>(
            ModEntities.CARRIER_KAGA.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererCarrierKaga(context) })
        event.registerEntityRenderer<EntityCarrierAkagi?>(
            ModEntities.CARRIER_AKAGI.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererCarrierAkagi(context) })
        event.registerEntityRenderer<EntityBBKongou?>(
            ModEntities.BB_KONGOU.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererBBKongou(context) })
        event.registerEntityRenderer<EntityBBHiei?>(
            ModEntities.BB_HIEI.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererBBHiei(context) })
        event.registerEntityRenderer<EntityBBHaruna?>(
            ModEntities.BB_HARUNA.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererBBHaruna(context) })
        event.registerEntityRenderer<EntityBBKirishima?>(
            ModEntities.BB_KIRISHIMA.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererBBKirishima(context) })
        event.registerEntityRenderer<EntityBattleshipNagato?>(
            ModEntities.BATTLESHIP_NAGATO.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererBattleshipNagato(context) })
        event.registerEntityRenderer<EntityBattleshipYamato?>(
            ModEntities.BATTLESHIP_YAMATO.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererBattleshipYamato(context) })
        event.registerEntityRenderer<EntitySubmU511?>(
            ModEntities.SUBM_U511.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererSubmU511(context) })
        event.registerEntityRenderer<EntitySubmRo500?>(
            ModEntities.SUBM_RO500.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererSubmRo500(context) })

        event.registerEntityRenderer<EntityAirplane?>(
            ModEntities.AIRPLANE.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? ->
                RendererSimpleMob<EntityAirplane?, ModelAirplane<EntityAirplane?>?>(
                    context,
                    ModelAirplane<EntityAirplane?>(context!!.bakeLayer(ModelAirplane.LAYER_LOCATION)),
                    0.5f,
                    DEFAULT_MODEL_SCALE,
                    entityTexture("airplane")
                )
            })
        event.registerEntityRenderer<EntityAirplaneT?>(
            ModEntities.AIRPLANE_T.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? ->
                RendererSimpleMob<EntityAirplaneT?, ModelAirplaneT<EntityAirplaneT?>?>(
                    context,
                    ModelAirplaneT<EntityAirplaneT?>(context!!.bakeLayer(ModelAirplaneT.LAYER_LOCATION)),
                    0.5f,
                    DEFAULT_MODEL_SCALE,
                    entityTexture("airplane_t")
                )
            })
        event.registerEntityRenderer<EntityAirplaneZero?>(
            ModEntities.AIRPLANE_ZERO.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? ->
                RendererSimpleMob<EntityAirplaneZero?, ModelAirplaneZero<EntityAirplaneZero?>?>(
                    context,
                    ModelAirplaneZero<EntityAirplaneZero?>(context!!.bakeLayer(ModelAirplaneZero.LAYER_LOCATION)),
                    0.5f,
                    DEFAULT_MODEL_SCALE,
                    entityTexture("airplane_zero")
                )
            })
        event.registerEntityRenderer<EntityMountSuH?>(
            ModEntities.MOUNT_SU_H.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererMountSuH(context) })
        event.registerEntityRenderer<EntityMountMiH?>(
            ModEntities.MOUNT_MI_H.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererMountMiH(context) })
        event.registerEntityRenderer<EntityMountIsH?>(
            ModEntities.MOUNT_IS_H.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererMountIsH(context) })
        event.registerEntityRenderer<EntityMountHbH?>(
            ModEntities.MOUNT_HB_H.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererMountHbH(context) })
        event.registerEntityRenderer<EntityMountCaWD?>(
            ModEntities.MOUNT_CA_WD.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererMountCaWD(context) })
        event.registerEntityRenderer<EntityMountAfH?>(
            ModEntities.MOUNT_AF_H.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererMountAfH(context) })
        event.registerEntityRenderer<EntityMountBaH?>(
            ModEntities.MOUNT_BA_H.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererMountBaH(context) })
        event.registerEntityRenderer<EntityMountCaH?>(
            ModEntities.MOUNT_CA_H.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererMountCaH(context) })
        event.registerEntityRenderer<EntityRensouhou?>(
            ModEntities.RENSOUHOU.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? ->
                RendererSimpleMob<EntityRensouhou?, ModelRensouhou<EntityRensouhou?>?>(
                    context,
                    ModelRensouhou<EntityRensouhou?>(context!!.bakeLayer(ModelRensouhou.LAYER_LOCATION)),
                    0.5f,
                    DEFAULT_MODEL_SCALE,
                    entityTexture("rensouhou")
                )
            })
        event.registerEntityRenderer<EntityRensouhouS?>(
            ModEntities.RENSOUHOU_S.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererRensouhouS(context) })
        event.registerEntityRenderer<EntityTakoyaki?>(
            ModEntities.TAKOYAKI.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererTakoyaki(context) })
        event.registerEntityRenderer<EntityAbyssMissile?>(
            ModEntities.ABYSS_MISSILE.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererAbyssMissile(context) })
        event.registerEntityRenderer<EntityProjectileBeam?>(
            ModEntities.PROJECTILE_BEAM.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererProjectileBeam(context) })
        event.registerEntityRenderer<EntityShipGrudge?>(
            ModEntities.SHIP_GRUDGE.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererShipGrudge(context) })
        event.registerEntityRenderer<EntityShipFishingHook?>(
            ModEntities.SHIP_FISHING_HOOK.get(),
            EntityRendererProvider { context: EntityRendererProvider.Context? -> RendererShipFishingHook(context) })

        event.registerBlockEntityRenderer<SmallShipyardBlockEntity?>(
            ModBlockEntities.SMALL_SHIPYARD.get(),
            BlockEntityRendererProvider { context: BlockEntityRendererProvider.Context? -> RenderSmallShipyard(context) })
        event.registerBlockEntityRenderer<LargeShipyardBlockEntity?>(
            ModBlockEntities.LARGE_SHIPYARD.get(),
            BlockEntityRendererProvider { context: BlockEntityRendererProvider.Context? -> RenderLargeShipyard(context) })
        event.registerBlockEntityRenderer<DeskBlockEntity?>(
            ModBlockEntities.DESK.get(),
            BlockEntityRendererProvider { context: BlockEntityRendererProvider.Context? -> RenderDesk(context) })
    }

    @SubscribeEvent
    fun addRenderLayers(event: AddLayers) {
        for (entry in ModEntities.ENTITY_TYPES.getEntries()) {
            val type: EntityType<*> = entry.get()
            val renderer: EntityRenderer<*>? = event.getRenderer(type)
            if (renderer is LivingEntityRenderer<*, *>
                && renderer.getModel() is ShipModelBaseAdv<*>
            ) {
                addHeldItemLayerUnchecked(renderer)
            }
        }
    }

    private fun addHeldItemLayerUnchecked(renderer: LivingEntityRenderer<*, *>) {
        renderer.addLayer(ShipHeldItemLayer<Any?, Any?>(renderer))
    }

    @SubscribeEvent
    fun registerLayerDefinitions(event: RegisterLayerDefinitions) {
        event.registerLayerDefinition(ModelDestroyerI.LAYER_LOCATION, Supplier { ModelDestroyerI.createBodyLayer() })
        event.registerLayerDefinition(ModelDestroyerRo.LAYER_LOCATION, Supplier { ModelDestroyerRo.createBodyLayer() })
        event.registerLayerDefinition(ModelDestroyerHa.LAYER_LOCATION, Supplier { ModelDestroyerHa.createBodyLayer() })
        event.registerLayerDefinition(ModelDestroyerNi.LAYER_LOCATION, Supplier { ModelDestroyerNi.createBodyLayer() })
        event.registerLayerDefinition(
            ModelHeavyCruiserRi.LAYER_LOCATION,
            Supplier { ModelHeavyCruiserRi.createBodyLayer() })
        event.registerLayerDefinition(
            ModelHeavyCruiserNe.LAYER_LOCATION,
            Supplier { ModelHeavyCruiserNe.createBodyLayer() })
        event.registerLayerDefinition(ModelCarrierWo.LAYER_LOCATION, Supplier { ModelCarrierWo.createBodyLayer() })
        event.registerLayerDefinition(
            ModelBattleshipRu.LAYER_LOCATION,
            Supplier { ModelBattleshipRu.createBodyLayer() })
        event.registerLayerDefinition(
            ModelBattleshipTa.LAYER_LOCATION,
            Supplier { ModelBattleshipTa.createBodyLayer() })
        event.registerLayerDefinition(
            ModelBattleshipRe.LAYER_LOCATION,
            Supplier { ModelBattleshipRe.createBodyLayer() })
        event.registerLayerDefinition(
            ModelTransportWa.LAYER_LOCATION,
            Supplier { obj: ModelTransportWa<*>? -> ModelTransportWa.createBodyLayer() })
        event.registerLayerDefinition(ModelSubmKa.LAYER_LOCATION, Supplier { ModelSubmKa.createBodyLayer() })
        event.registerLayerDefinition(
            ModelSubmYo.LAYER_LOCATION,
            Supplier { obj: ModelSubmYo<*>? -> ModelSubmYo.createBodyLayer() })
        event.registerLayerDefinition(ModelSubmSo.LAYER_LOCATION, Supplier { ModelSubmSo.createBodyLayer() })
        event.registerLayerDefinition(
            ModelDestroyerHime.LAYER_LOCATION,
            Supplier { ModelDestroyerHime.createBodyLayer() })
        event.registerLayerDefinition(ModelCAHime.LAYER_LOCATION, Supplier { ModelCAHime.createBodyLayer() })
        event.registerLayerDefinition(
            ModelAirfieldHime.LAYER_LOCATION,
            Supplier { obj: ModelAirfieldHime<*>? -> ModelAirfieldHime.createBodyLayer() })
        event.registerLayerDefinition(
            ModelBattleshipHime.LAYER_LOCATION,
            Supplier { obj: ModelBattleshipHime<*>? -> ModelBattleshipHime.createBodyLayer() })
        event.registerLayerDefinition(ModelCarrierHime.LAYER_LOCATION, Supplier { ModelCarrierHime.createBodyLayer() })
        event.registerLayerDefinition(ModelHarbourHime.LAYER_LOCATION, Supplier { ModelHarbourHime.createBodyLayer() })
        event.registerLayerDefinition(
            ModelIsolatedHime.LAYER_LOCATION,
            Supplier { ModelIsolatedHime.createBodyLayer() })
        event.registerLayerDefinition(ModelMidwayHime.LAYER_LOCATION, Supplier { ModelMidwayHime.createBodyLayer() })
        event.registerLayerDefinition(
            ModelNorthernHime.LAYER_LOCATION,
            Supplier { ModelNorthernHime.createBodyLayer() })
        event.registerLayerDefinition(ModelSubmHime.LAYER_LOCATION, Supplier { ModelSubmHime.createBodyLayer() })
        event.registerLayerDefinition(ModelSSNH.LAYER_LOCATION, Supplier { ModelSSNH.createBodyLayer() })
        event.registerLayerDefinition(
            ModelCarrierWDemon.LAYER_LOCATION,
            Supplier { ModelCarrierWDemon.createBodyLayer() })
        event.registerLayerDefinition(
            ModelDestroyerAkatsuki.LAYER_LOCATION,
            Supplier { ModelDestroyerAkatsuki.createBodyLayer() })
        event.registerLayerDefinition(
            ModelDestroyerHibiki.LAYER_LOCATION,
            Supplier { ModelDestroyerHibiki.createBodyLayer() })
        event.registerLayerDefinition(
            ModelDestroyerIkazuchi.LAYER_LOCATION,
            Supplier { ModelDestroyerIkazuchi.createBodyLayer() })
        event.registerLayerDefinition(
            ModelDestroyerInazuma.LAYER_LOCATION,
            Supplier { ModelDestroyerInazuma.createBodyLayer() })
        event.registerLayerDefinition(
            ModelDestroyerShimakaze.LAYER_LOCATION,
            Supplier { ModelDestroyerShimakaze.createBodyLayer() })
        event.registerLayerDefinition(
            ModelCruiserTenryuu.LAYER_LOCATION,
            Supplier { ModelCruiserTenryuu.createBodyLayer() })
        event.registerLayerDefinition(
            ModelCruiserTatsuta.LAYER_LOCATION,
            Supplier { ModelCruiserTatsuta.createBodyLayer() })
        event.registerLayerDefinition(
            ModelCruiserTakao.LAYER_LOCATION,
            Supplier { ModelCruiserTakao.createBodyLayer() })
        event.registerLayerDefinition(
            ModelCruiserAtago.LAYER_LOCATION,
            Supplier { ModelCruiserAtago.createBodyLayer() })
        event.registerLayerDefinition(ModelCarrierKaga.LAYER_LOCATION, Supplier { ModelCarrierKaga.createBodyLayer() })
        event.registerLayerDefinition(
            ModelCarrierAkagi.LAYER_LOCATION,
            Supplier { ModelCarrierAkagi.createBodyLayer() })
        event.registerLayerDefinition(ModelBBKongou.LAYER_LOCATION, Supplier { ModelBBKongou.createBodyLayer() })
        event.registerLayerDefinition(ModelBBHiei.LAYER_LOCATION, Supplier { ModelBBHiei.createBodyLayer() })
        event.registerLayerDefinition(ModelBBHaruna.LAYER_LOCATION, Supplier { ModelBBHaruna.createBodyLayer() })
        event.registerLayerDefinition(ModelBBKirishima.LAYER_LOCATION, Supplier { ModelBBKirishima.createBodyLayer() })
        event.registerLayerDefinition(
            ModelBattleshipNagato.LAYER_LOCATION,
            Supplier { ModelBattleshipNagato.createBodyLayer() })
        event.registerLayerDefinition(
            ModelBattleshipYamato.LAYER_LOCATION,
            Supplier { ModelBattleshipYamato.createBodyLayer() })
        event.registerLayerDefinition(
            ModelSubmU511.LAYER_LOCATION,
            Supplier { obj: ModelSubmU511<*>? -> ModelSubmU511.createBodyLayer() })
        event.registerLayerDefinition(ModelSubmRo500.LAYER_LOCATION, Supplier { ModelSubmRo500.createBodyLayer() })

        event.registerLayerDefinition(
            ModelAirplane.LAYER_LOCATION,
            Supplier { obj: ModelAirplane<*>? -> ModelAirplane.createBodyLayer() })
        event.registerLayerDefinition(
            ModelAirplaneT.LAYER_LOCATION,
            Supplier { obj: ModelAirplaneT<*>? -> ModelAirplaneT.createBodyLayer() })
        event.registerLayerDefinition(
            ModelAirplaneZero.LAYER_LOCATION,
            Supplier { obj: ModelAirplaneZero<*>? -> ModelAirplaneZero.createBodyLayer() })
        event.registerLayerDefinition(ModelMountAfH.LAYER_LOCATION, Supplier { ModelMountAfH.createBodyLayer() })
        event.registerLayerDefinition(ModelMountBaH.LAYER_LOCATION, Supplier { ModelMountBaH.createBodyLayer() })
        event.registerLayerDefinition(ModelMountCaH.LAYER_LOCATION, Supplier { ModelMountCaH.createBodyLayer() })
        event.registerLayerDefinition(ModelMountCaWD.LAYER_LOCATION, Supplier { ModelMountCaWD.createBodyLayer() })
        event.registerLayerDefinition(ModelMountHbH.LAYER_LOCATION, Supplier { ModelMountHbH.createBodyLayer() })
        event.registerLayerDefinition(ModelMountIsH.LAYER_LOCATION, Supplier { ModelMountIsH.createBodyLayer() })
        event.registerLayerDefinition(ModelMountMiH.LAYER_LOCATION, Supplier { ModelMountMiH.createBodyLayer() })
        event.registerLayerDefinition(ModelMountSuH.LAYER_LOCATION, Supplier { ModelMountSuH.createBodyLayer() })
        event.registerLayerDefinition(ModelRensouhou.LAYER_LOCATION, Supplier { ModelRensouhou.createBodyLayer() })
        event.registerLayerDefinition(ModelRensouhouS.LAYER_LOCATION, Supplier { ModelRensouhouS.createBodyLayer() })
        event.registerLayerDefinition(
            ModelTakoyaki.LAYER_LOCATION,
            Supplier { obj: ModelTakoyaki<*>? -> ModelTakoyaki.createBodyLayer() })
        event.registerLayerDefinition(
            ModelAbyssMissile.LAYER_LOCATION,
            Supplier { obj: ModelAbyssMissile<*>? -> ModelAbyssMissile.createBodyLayer() })
        event.registerLayerDefinition(ModelShipGrudge.LAYER_LOCATION, Supplier { ModelShipGrudge.createBodyLayer() })
        event.registerLayerDefinition(
            ModelSmallShipyard.LAYER_LOCATION,
            Supplier { ModelSmallShipyard.createBodyLayer() })
        event.registerLayerDefinition(
            ModelLargeShipyard.LAYER_LOCATION,
            Supplier { ModelLargeShipyard.createBodyLayer() })
        event.registerLayerDefinition(
            ModelVortex.LAYER_LOCATION,
            Supplier { obj: ModelVortex? -> ModelVortex.createBodyLayer() })
        event.registerLayerDefinition(ModelBlockDesk.LAYER_LOCATION, Supplier { ModelBlockDesk.createBodyLayer() })
        event.registerLayerDefinition(
            ModelBlockDeskLarge.LAYER_LOCATION,
            Supplier { ModelBlockDeskLarge.createBodyLayer() })
    }

    @SubscribeEvent
    fun registerScreens(event: RegisterMenuScreensEvent) {
        event.register<ShipContainerMenu?, ShipInventoryScreen?>(
            ModMenus.SHIP_MENU.get(),
            ScreenConstructor { menu: Component?, playerInv: MenuType<ShipContainerMenu?>?, title: Minecraft? ->
                ShipInventoryScreen(
                    menu,
                    playerInv,
                    title
                )
            })
        event.register<SmallShipyardMenu?, SmallShipyardScreen?>(
            ModMenus.SMALL_SHIPYARD_MENU.get(),
            ScreenConstructor { menu: Component?, playerInventory: MenuType<SmallShipyardMenu?>?, title: Minecraft? ->
                SmallShipyardScreen(
                    menu,
                    playerInventory,
                    title
                )
            })
        event.register<LargeShipyardMenu?, LargeShipyardScreen?>(
            ModMenus.LARGE_SHIPYARD_MENU.get(),
            ScreenConstructor { menu: Component?, playerInventory: MenuType<LargeShipyardMenu?>?, title: Minecraft? ->
                LargeShipyardScreen(
                    menu,
                    playerInventory,
                    title
                )
            })
        event.register<DeskMenu?, DeskScreen?>(
            ModMenus.DESK_MENU.get(),
            ScreenConstructor { menu: Component?, inventory: MenuType<DeskMenu?>?, title: Minecraft? ->
                DeskScreen(
                    menu,
                    inventory,
                    title
                )
            })
        event.register<VolCoreMenu?, VolCoreScreen?>(
            ModMenus.VOL_CORE_MENU.get(),
            ScreenConstructor { menu: Component?, inventory: MenuType<VolCoreMenu?>?, title: Minecraft? ->
                VolCoreScreen(
                    menu,
                    inventory,
                    title
                )
            })
        event.register<CraneMenu?, CraneScreen?>(
            ModMenus.CRANE_MENU.get(),
            ScreenConstructor { menu: Component?, playerInventory: MenuType<CraneMenu?>?, title: Minecraft? ->
                CraneScreen(
                    menu,
                    playerInventory,
                    title
                )
            })
        event.register<FormationMenu?, FormationScreen?>(
            ModMenus.FORMATION.get(),
            ScreenConstructor { menu: Component?, playerInventory: MenuType<FormationMenu?>?, title: Minecraft? ->
                FormationScreen(
                    menu,
                    playerInventory,
                    title
                )
            })
        event.register<RecipePaperMenu?, RecipePaperScreen?>(
            ModMenus.RECIPE_PAPER_MENU.get(),
            ScreenConstructor { menu: Component?, inventory: MenuType<RecipePaperMenu?>?, title: Minecraft? ->
                RecipePaperScreen(
                    menu,
                    inventory,
                    title
                )
            })
    }

    @SubscribeEvent
    fun registerParticles(event: RegisterParticleProvidersEvent) {
        event.registerSpriteSet<SimpleParticleType?>(
            ModParticles.PARTICLE_EMOTION.get(),
            SpriteParticleRegistration { sprites: SpriteSet? -> ParticleEmotion.Provider(sprites) })
        event.registerSpriteSet<SimpleParticleType?>(
            ModParticles.PARTICLE_HEAL_SPARKLE.get(),
            SpriteParticleRegistration { sprites: SpriteSet? -> ParticleHealSparkle.Provider(sprites) })
        event.registerSpriteSet<SimpleParticleType?>(
            ModParticles.PARTICLE_GODDESS.get(),
            SpriteParticleRegistration { sprites: SpriteSet? -> ParticleGoddess.Provider(sprites) })
        event.registerSpriteSet<SimpleParticleType?>(
            ModParticles.PARTICLE_TEXTS.get(),
            SpriteParticleRegistration { sprites: SpriteSet? -> ParticleTexts.Provider(sprites) })
        event.registerSpriteSet<SimpleParticleType?>(
            ModParticles.PARTICLE_LIGHTNING.get(),
            SpriteParticleRegistration { sprites: SpriteSet? -> ParticleLightning.Provider(sprites) })
        event.registerSpriteSet<SimpleParticleType?>(
            ModParticles.PARTICLE_SPRAY_RED.get(),
            SpriteParticleRegistration { sprites: SpriteSet? -> ParticleSprayRed.Provider(sprites) })
        event.registerSpriteSet<SimpleParticleType?>(
            ModParticles.PARTICLE_SPRAY.get(),
            SpriteParticleRegistration { sprites: SpriteSet? -> ParticleSpray.Provider(sprites) })
        event.registerSpriteSet<SimpleParticleType?>(
            ModParticles.PARTICLE_CRANING.get(),
            SpriteParticleRegistration { sprites: SpriteSet? -> ParticleCraning.Provider(sprites) })
        event.registerSpriteSet<SimpleParticleType?>(
            ModParticles.PARTICLE_TEAM.get(),
            SpriteParticleRegistration { sprites: SpriteSet? -> ParticleTeam.Provider(sprites) })
        event.registerSpriteSet<SimpleParticleType?>(
            ModParticles.PARTICLE_TEAM_SELECTED.get(),
            SpriteParticleRegistration { sprites: SpriteSet? ->
                ParticleTeam.Provider(
                    sprites,
                    RenderStyle.DEFAULT_BLUE
                )
            })
        event.registerSpriteSet<SimpleParticleType?>(
            ModParticles.PARTICLE_TEAM_SELECTED_RED.get(),
            SpriteParticleRegistration { sprites: SpriteSet? ->
                ParticleTeam.Provider(
                    sprites,
                    RenderStyle.SELECTED_RED
                )
            })
        event.registerSpriteSet<SimpleParticleType?>(
            ModParticles.PARTICLE_TEAM_SELECTED_YELLOW.get(),
            SpriteParticleRegistration { sprites: SpriteSet? ->
                ParticleTeam.Provider(
                    sprites,
                    RenderStyle.SELECTED_YELLOW
                )
            })
        event.registerSpriteSet<SimpleParticleType?>(
            ModParticles.PARTICLE_TEAM_TARGET.get(),
            SpriteParticleRegistration { sprites: SpriteSet? ->
                ParticleTeam.Provider(
                    sprites,
                    RenderStyle.TARGET_WHITE
                )
            })
        event.registerSpriteSet<SimpleParticleType?>(
            ModParticles.PARTICLE_TEAM_TARGET_ENTITY.get(),
            SpriteParticleRegistration { sprites: SpriteSet? ->
                ParticleTeam.Provider(
                    sprites,
                    RenderStyle.TARGET_RED
                )
            })
        event.registerSpriteSet<SimpleParticleType?>(
            ModParticles.PARTICLE_SPARKLE.get(),
            SpriteParticleRegistration { sprites: SpriteSet? -> ParticleSparkle.Provider(sprites) })
        event.registerSpriteSet<SimpleParticleType?>(
            ModParticles.PARTICLE_WAYPOINT.get(),
            SpriteParticleRegistration { sprites: SpriteSet? -> ParticleWaypoint.Provider(sprites) })
        event.registerSpecial<SimpleParticleType?>(
            ModParticles.PARTICLE_WAYPOINT_LINE.get(),
            ParticlePointerLine.Provider(0, null)
        )
        event.registerSpecial<SimpleParticleType?>(
            ModParticles.PARTICLE_WAYPOINT_LINE_PURPLE.get(),
            ParticlePointerLine.Provider(1, null)
        )
        event.registerSpecial<SimpleParticleType?>(
            ModParticles.PARTICLE_WAYPOINT_LINE_RED.get(),
            ParticlePointerLine.Provider(2, null)
        )
        event.registerSpriteSet<SimpleParticleType?>(
            ModParticles.PARTICLE_CHI.get(),
            SpriteParticleRegistration { sprites: SpriteSet? -> ParticleChi.Provider(sprites) })
        event.registerSpriteSet<SimpleParticleType?>(
            ModParticles.PARTICLE_91TYPE.get(),
            SpriteParticleRegistration { sprites: SpriteSet? -> Particle91Type.Provider(sprites) })
        event.registerSpriteSet<SimpleParticleType?>(
            ModParticles.PARTICLE_CUBE.get(),
            SpriteParticleRegistration { sprites: SpriteSet? -> ParticleCube.Provider(sprites) })
        event.registerSpecial<SimpleParticleType?>(ModParticles.PARTICLE_BEAM.get(), ParticleBeam.Provider())
    }

    @SubscribeEvent
    fun registerTooltipComponents(event: RegisterClientTooltipComponentFactoriesEvent) {
        event.register<ScaledTextTooltipData?>(
            ScaledTextTooltipData::class.java,
            Function { data: ScaledTextTooltipData? -> ScaledTextClientTooltip(data) })
    }
}
