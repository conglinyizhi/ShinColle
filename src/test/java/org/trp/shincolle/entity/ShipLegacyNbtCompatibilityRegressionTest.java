package org.trp.shincolle.entity;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipLegacyNbtCompatibilityRegressionTest {
    private static final Path SERIALIZATION_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBaseSerialization.kt");

    @Test
    void legacyShipExtPropsAndInventoryShouldStillLoad() throws IOException {
        String source = Files.readString(SERIALIZATION_SOURCE);

        assertTrue(source.contains("private static final String LEGACY_EXT_PROPS = \"ShipExtProps\";"),
                "Ship serialization should keep the legacy ShipExtProps tag name for old save compatibility");
        assertTrue(source.contains("private static final String LEGACY_EXT_PROPS_BACKUP = \"LegacyShipExtPropsBackup\";"),
                "Ship serialization should preserve a backup copy of legacy ShipExtProps for still-unmapped fields");
        assertTrue(source.contains("private static final String LEGACY_INV_TAG = \"ShipInv\";"),
                "Ship serialization should keep the legacy ShipInv inventory tag name for old save compatibility");
        assertTrue(source.contains("} else if (compound.contains(LEGACY_INV_TAG)) {\n            this.ship.getInventory().deserializeNBT(this.ship.registryAccess(), compound.getCompound(LEGACY_INV_TAG));"),
                "Ship serialization should still load legacy inventory NBT when modern ShipInventory is absent");
        assertTrue(source.contains("} else if (compound.contains(LEGACY_EXT_PROPS)) {\n            loadLegacyShipExtProps(compound.getCompound(LEGACY_EXT_PROPS));"),
                "Ship serialization should fall back to legacy ShipExtProps when modern tags are absent");
        assertTrue(source.contains("if (compound.contains(LEGACY_EXT_PROPS_BACKUP)) {\n            this.ship.setLegacyShipExtPropsBackupInternal(compound.getCompound(LEGACY_EXT_PROPS_BACKUP));"),
                "Ship serialization should reload the preserved legacy ShipExtProps backup when modern saves carry it forward");
        assertTrue(source.contains("private void loadLegacyShipExtProps(CompoundTag legacyExt) {"),
                "Legacy ShipExtProps loading should be centralized in one compatibility helper");
        assertTrue(source.contains("this.ship.setLegacyShipExtPropsBackupInternal(legacyExt.copy());"),
                "Loading legacy ShipExtProps should preserve the original compound so still-unmapped fields survive future saves");
        assertTrue(source.contains("this.ship.setFuel(minor.getInt(\"NumGrudge\"));"),
                "Legacy grudge fuel should still load from ShipExtProps into the modern fuel field");
        assertTrue(source.contains("this.ship.setFormationTeam(minor.getInt(\"FType\"));"),
                "Legacy formation team id should still load from ShipExtProps");
        assertTrue(source.contains("this.ship.setFormationSlot(minor.getInt(\"FPos\"));"),
                "Legacy formation slot should still load from ShipExtProps");
        assertTrue(source.contains("this.ship.setStateMinor(EntityShipBase.STATE_MINOR_CRANING, minor.getInt(\"Crane\"));"),
                "Legacy crane state should still load from ShipExtProps into the modern craning minor state");
        assertTrue(source.contains("this.ship.setStateMinor(org.trp.shincolle.menu.ShipContainerMenu.STATE_MINOR_TASK_ID, minor.getInt(\"Task\"));"),
                "Legacy task id should still load from ShipExtProps");
        assertTrue(source.contains("this.ship.setStateMinor(org.trp.shincolle.menu.ShipContainerMenu.STATE_MINOR_TASK_SIDE, minor.getInt(\"Side\"));"),
                "Legacy task side bits should still load from ShipExtProps");
        assertTrue(source.contains("this.ship.setStateMinor(org.trp.shincolle.menu.ShipContainerMenu.STATE_MINOR_RATION_MORALE, minor.getInt(\"AutoCR\"));"),
                "Legacy auto-combat-ration threshold should still load from ShipExtProps");
        assertTrue(source.contains("this.ship.setStateMinor(org.trp.shincolle.menu.ShipContainerMenu.STATE_MINOR_WP_STAY, minor.getInt(\"WpStay\"));"),
                "Legacy waypoint stay settings should still load from ShipExtProps");
        assertTrue(source.contains("this.ship.setStateEmotion(0, display.getInt(\"State\"), false);"),
                "Legacy display state should still load from ShipExtProps");
        assertTrue(source.contains("this.ship.applyLegacyBonusTag(legacyBonus);"),
                "Legacy bonus point data should still load from ShipExtProps");
        assertTrue(source.contains("this.ship.setStateMarried(flags.getBoolean(\"IsMarried\"));"),
                "Legacy married state should still load from ShipExtProps");
        assertTrue(source.contains("this.ship.setLegacyDeathDropInternal(flags.getBoolean(\"CanDrop\"));"),
                "Legacy CanDrop death-drop gating should still load from ShipExtProps");
        assertTrue(source.contains("this.ship.setStateFlag(org.trp.shincolle.menu.ShipContainerMenu.STATE_FLAG_AUTO_PUMP, flags.getBoolean(\"AutoPump\"));"),
                "Legacy auto-pump state should still load from ShipExtProps");
        assertTrue(source.contains("this.ship.setStateAppearance(flags.getBoolean(\"HeldItem\"));"),
                "Legacy held-item visibility should still load from ShipExtProps");
        assertTrue(source.contains("this.ship.setStateTimer(LEGACY_TIMER_CRANE, timer.getInt(\"Crane\"));"),
                "Legacy crane timer should still load from ShipExtProps");
    }

    @Test
    void legacyShipExtPropsBackupShouldBeSavedForward() throws IOException {
        String source = Files.readString(SERIALIZATION_SOURCE);
        String entitySource = Files.readString(Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBase.kt"));

        assertTrue(entitySource.contains("private CompoundTag legacyShipExtPropsBackup = new CompoundTag();"),
                "Ship entities should keep an internal backup of legacy ShipExtProps data");
        assertTrue(entitySource.contains("CompoundTag getLegacyShipExtPropsBackupInternal() {"),
                "Ship entities should expose the preserved legacy ShipExtProps backup to serialization");
        assertTrue(entitySource.contains("void setLegacyShipExtPropsBackupInternal(CompoundTag backup) {"),
                "Ship entities should accept restored legacy ShipExtProps backups from serialization");
        assertTrue(source.contains("CompoundTag legacyShipExtPropsBackup = this.ship.getLegacyShipExtPropsBackupInternal();"),
                "Ship serialization should fetch the preserved ShipExtProps backup when saving");
        assertTrue(source.contains("if (!legacyShipExtPropsBackup.isEmpty()) {\n            compound.put(LEGACY_EXT_PROPS_BACKUP, legacyShipExtPropsBackup);"),
                "Ship serialization should write the preserved ShipExtProps backup back into modern saves");
    }

    @Test
    void legacyCanDropShouldStillGateDeathSpawnEggDrop() throws IOException {
        String entitySource = Files.readString(Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBase.kt"));

        assertTrue(entitySource.contains("if (!this.level().isClientSide && this.shipDeathTicks == SHIP_DEATH_MAX_TICKS && this.hostileCanDrop) {"),
                "Ship death handling should still respect the legacy CanDrop gate before spawning the preserved spawn egg");
        assertTrue(entitySource.contains("this.hostileCanDrop = false;\n            spawnShipGrudge();"),
                "Ship death handling should still clear the legacy death-drop gate after the preserved spawn egg is emitted once");
    }
}
