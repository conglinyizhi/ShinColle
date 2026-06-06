package org.trp.shincolle.api

import org.slf4j.LoggerFactory

/**
 * 第三方 API 回调的安全调用包装器。
 *
 * 所有 `org.trp.shincolle.api` 包下对外暴露的接口回调，
 * 在调用时均应通过本对象包裹，防止第三方实现中的异常向上传播，
 * 导致舰娘 tick 中断或游戏崩溃。
 */
object ApiCallSafety {

    private val LOGGER = LoggerFactory.getLogger(ApiCallSafety::class.java)
    private const val LOG_PREFIX = "[ShipApiSafety]"

    /**
     * 安全执行无返回值回调。异常被捕获后记录日志，不向外抛出。
     *
     * @param context 用于日志定位的上下文描述，如 "ShipEquipSpecialEffect.tick"
     * @param action 要执行的回调
     */
    fun run(context: String, action: () -> Unit) {
        try {
            action()
        } catch (e: Throwable) {
            LOGGER.error("{} 第三方回调在 {} 中抛出异常，已拦截", LOG_PREFIX, context, e)
        }
    }

    /**
     * 安全执行有返回值回调。异常被捕获后返回 [default]，不向外抛出。
     *
     * @param context 用于日志定位的上下文描述
     * @param default 异常时的默认返回值
     * @param action 要执行的回调
     */
    fun <T> runWithDefault(context: String, default: T, action: () -> T): T {
        return try {
            action()
        } catch (e: Throwable) {
            LOGGER.error("{} 第三方回调在 {} 中抛出异常，已返回默认值", LOG_PREFIX, context, e)
            default
        }
    }

    /**
     * 安全执行有返回值回调。异常被捕获后返回 null，不向外抛出。
     *
     * @param context 用于日志定位的上下文描述
     * @param action 要执行的回调
     */
    fun <T> runNullable(context: String, action: () -> T?): T? {
        return try {
            action()
        } catch (e: Throwable) {
            LOGGER.error("{} 第三方回调在 {} 中抛出异常，已返回 null", LOG_PREFIX, context, e)
            null
        }
    }
}
