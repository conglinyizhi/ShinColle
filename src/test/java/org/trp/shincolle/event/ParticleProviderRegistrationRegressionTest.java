package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ParticleProviderRegistrationRegressionTest {
    private static final Path MOD_PARTICLES_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/init/ModParticles.kt");
    private static final Path CLIENT_EVENT_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/event/ClientModEventBusEvents.kt");
    private static final Pattern PARTICLE_FIELD_PATTERN = Pattern.compile(
            "public static final DeferredHolder<ParticleType<\\?>, SimpleParticleType>\\s+([A-Z0-9_]+)\\s*=\\s*PARTICLES\\.register",
            Pattern.MULTILINE);

    @Test
    void registeredParticlesShouldKeepClientProviderRegistrations() throws IOException {
        String modParticles = Files.readString(MOD_PARTICLES_SOURCE);
        String clientEvents = Files.readString(CLIENT_EVENT_SOURCE);
        List<String> missing = new ArrayList<>();

        Matcher matcher = PARTICLE_FIELD_PATTERN.matcher(modParticles);
        while (matcher.find()) {
            String fieldName = matcher.group(1);
            String spriteSetRegistration = "event.registerSpriteSet(ModParticles." + fieldName + ".get(),";
            String specialRegistration = "event.registerSpecial(ModParticles." + fieldName + ".get(),";
            if (!clientEvents.contains(spriteSetRegistration) && !clientEvents.contains(specialRegistration)) {
                missing.add(fieldName);
            }
        }

        assertTrue(missing.isEmpty(),
                () -> "Every registered particle should keep a client provider registration: "
                        + String.join(", ", missing));
    }
}
