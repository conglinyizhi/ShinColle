package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LegacyVariantModelPropertyRegistrationRegressionTest {
    private static final Path ITEM_MODEL_ROOT =
            Path.of("src/main/resources/assets/shincolle/models/item");
    private static final Path CLIENT_EVENT_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/event/ClientModEventBusEvents.kt");
    private static final Path ITEM_REGISTRY_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/init/ModItems.kt");
    private static final Pattern LEGACY_VARIANT_REGISTRATION_PATTERN =
            Pattern.compile("registerLegacyVariantProperty\\(ModItems\\.([A-Z0-9_]+)\\.get\\(\\)\\);");
    private static final Pattern ITEM_REGISTRATION_PATTERN =
            Pattern.compile("public static final DeferredItem<Item>\\s+([A-Z0-9_]+)\\s*=\\s*ITEMS\\.register\\(\"([a-z0-9_]+)\"");

    @Test
    void legacyVariantItemModelsShouldKeepMatchingClientPropertyRegistrations() throws IOException {
        Set<String> expectedFields = findLegacyVariantBackedItemFields();
        String clientEvents = Files.readString(CLIENT_EVENT_SOURCE);

        List<String> registeredFields = new ArrayList<>();
        Matcher registrationMatcher = LEGACY_VARIANT_REGISTRATION_PATTERN.matcher(clientEvents);
        while (registrationMatcher.find()) {
            registeredFields.add(registrationMatcher.group(1));
        }

        assertEquals(expectedFields.size(), new LinkedHashSet<>(registeredFields).size(),
                "Legacy variant model property registrations should not contain duplicates");
        assertEquals(expectedFields, new LinkedHashSet<>(registeredFields),
                "Every item model using shincolle:legacy_variant should keep a matching client property registration");
        assertTrue(clientEvents.contains("ItemProperties.register(item, LEGACY_VARIANT_MODEL_PROPERTY,"),
                "Client setup should keep registering the shared legacy variant model property");
    }

    private static Set<String> findLegacyVariantBackedItemFields() throws IOException {
        Set<String> itemFields = new LinkedHashSet<>();
        Map<String, String> itemRegistryNamesToFields = readItemRegistryNamesToFields();

        try (var stream = Files.walk(ITEM_MODEL_ROOT)) {
            for (Path file : (Iterable<Path>) stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))::iterator) {
                String source = Files.readString(file);
                if (!source.contains("\"shincolle:legacy_variant\"")) {
                    continue;
                }

                String baseName = stripJsonExtension(file.getFileName().toString());
                itemFields.add(resolveModItemsFieldName(baseName, itemRegistryNamesToFields));
            }
        }

        return itemFields;
    }

    private static Map<String, String> readItemRegistryNamesToFields() throws IOException {
        Map<String, String> registryNamesToFields = new HashMap<>();
        String itemRegistry = Files.readString(ITEM_REGISTRY_SOURCE);
        Matcher matcher = ITEM_REGISTRATION_PATTERN.matcher(itemRegistry);
        while (matcher.find()) {
            registryNamesToFields.put(matcher.group(2), matcher.group(1));
        }
        return registryNamesToFields;
    }

    private static String stripJsonExtension(String fileName) {
        return fileName.substring(0, fileName.length() - ".json".length());
    }

    private static String resolveModItemsFieldName(String itemModelName, Map<String, String> itemRegistryNamesToFields) {
        String fieldName = itemRegistryNamesToFields.get(itemModelName);
        assertTrue(fieldName != null,
                () -> "Expected item model " + itemModelName + " to map to a registered ModItems field");
        return fieldName;
    }
}
