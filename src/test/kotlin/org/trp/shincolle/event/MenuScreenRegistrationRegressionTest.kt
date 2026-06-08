package org.trp.shincolle.event

import org.junit.jupiter.api.Test

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.ArrayList
import java.util.regex.Matcher
import java.util.regex.Pattern

import org.junit.jupiter.api.Assertions.assertTrue

class MenuScreenRegistrationRegressionTest {
    private val MOD_MENUS_SOURCE: Path =
            Path.of("src/main/java/org/trp/shincolle/menu/ModMenus.kt")
    private val CLIENT_EVENT_SOURCE: Path =
            Path.of("src/main/java/org/trp/shincolle/event/ClientModEventBusEvents.kt")
    private val MENU_FIELD_PATTERN: Pattern = Pattern.compile(
            "public static final DeferredHolder<MenuType<\\?>, MenuType<[^>]+>>\\s+([A-Z0-9_]+)\\s*=\\s*MENUS\\.register",
            Pattern.MULTILINE)

    @Test
    fun registeredMenusShouldKeepClientScreenRegistrations() {
        val modMenus = Files.readString(MOD_MENUS_SOURCE)
        val clientEvents = Files.readString(CLIENT_EVENT_SOURCE)
        val missing = ArrayList<String>()

        val matcher = MENU_FIELD_PATTERN.matcher(modMenus)
        while (matcher.find()) {
            val fieldName = matcher.group(1)!!
            val registration = "event.register(ModMenus." + fieldName + ".get(),"
            if (!clientEvents.contains(registration)) {
                missing.add(fieldName)
            }
        }

        assertTrue(missing.isEmpty()) {
            "Every registered menu should keep a client screen registration: " +
                    missing.joinToString(", ")
        }
    }
}
