package org.trp.shincolle

import com.mojang.logging.LogUtils
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import org.slf4j.Logger
import org.trp.shincolle.init.ModBootstrap

@Mod(Shincolle.Companion.MODID)
class Shincolle(modEventBus: IEventBus, modContainer: ModContainer) {
    init {
        ModBootstrap.initialize(modEventBus, modContainer)
    }

    companion object {
        const val MODID: String = "shincolle"
        @JvmField
        val LOGGER: Logger = LogUtils.getLogger()

        @JvmStatic
        fun debugLog(message: String?, vararg args: Any?) {
            if (!Config.debugLogging) {
                return
            }
            LOGGER.info("[ShinColleDebug] " + message, *args)
        }

        @JvmStatic
        fun diagnosticLog(message: String?, vararg args: Any?) {
            if (!Config.debugLogging) {
                return
            }
            LOGGER.info("[ShinColleDiag] " + message, *args)
        }

        @JvmStatic
        fun perfLog(message: String?, vararg args: Any?) {
            if (!Config.debugPerformanceLogging) {
                return
            }
            LOGGER.info("[ShinCollePerf] " + message, *args)
        }
    }
}
