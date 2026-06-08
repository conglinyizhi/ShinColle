package org.trp.shincolle.event

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.ArrayList
import java.util.regex.Pattern

class ParticleProviderRegistrationRegressionTest {
    private val MOD_PARTICLES_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/init/ModParticles.kt")
    private val CLIENT_EVENT_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/event/ClientModEventBusEvents.kt")
    private val PARTICLE_FIELD_PATTERN: Pattern = Pattern.compile(
        "public static final DeferredHolder<ParticleType<\\?>, SimpleParticleType>\\s+([A-Z0-9_]+)\\s*=\\s*PARTICLES\\.register",
        Pattern.MULTILINE
    )

    @Test
    fun registeredParticlesShouldKeepClientProviderRegistrations() {
        val modParticles = Files.readString(MOD_PARTICLES_SOURCE)
        val clientEvents = Files.readString(CLIENT_EVENT_SOURCE)
        val missing = ArrayList<String>()

        val matcher = PARTICLE_FIELD_PATTERN.matcher(modParticles)
        while (matcher.find()) {
            val fieldName = matcher.group(1)!!
            val spriteSetRegistration = "event.registerSpriteSet(ModParticles." + fieldName + ".get(),"
            val specialRegistration = "event.registerSpecial(ModParticles." + fieldName + ".get(),"
            if (!clientEvents.contains(spriteSetRegistration) && !clientEvents.contains(specialRegistration)) {
                missing.add(fieldName)
            }
        }

        assertTrue(missing.isEmpty()) {
            "Every registered particle should keep a client provider registration: " + missing.joinToString(", ")
        }
    }
}
