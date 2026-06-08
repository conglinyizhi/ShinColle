package org.trp.shincolle.entity

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue

class ProjectileStaticSynchedDataRegressionTest {
    private val PROJECTILE_STATIC: Path =
            Path.of("src/main/java/org/trp/shincolle/entity/EntityProjectileStatic.kt")

    @Test
    fun shouldDefineThreeSynchedDataFields() {
        val source = Files.readString(PROJECTILE_STATIC)

        assertTrue(source.contains("private val DATA_LIFE_LENGTH: EntityDataAccessor<Int?>")) {
            "Should define synched data for life length (Int)"
        }
        assertTrue(source.contains("private val DATA_PULL_STRENGTH: EntityDataAccessor<Float?>")) {
            "Should define synched data for pull strength (Float)"
        }
        assertTrue(source.contains("private val DATA_RANGE: EntityDataAccessor<Float?>")) {
            "Should define synched data for range (Float)"
        }
    }

    @Test
    fun synchedDataShouldBeRegisteredInDefineSynchedData() {
        val source = Files.readString(PROJECTILE_STATIC)

        assertTrue(source.contains("builder.define<Int?>(DATA_LIFE_LENGTH, 100)")) {
            "Life length should be registered with default 100"
        }
        assertTrue(source.contains("builder.define<Float?>(DATA_PULL_STRENGTH, 0.08f)")) {
            "Pull strength should be registered with default 0.08f"
        }
        assertTrue(source.contains("builder.define<Float?>(DATA_RANGE, 6.0f)")) {
            "Range should be registered with default 6.0f"
        }
    }

    @Test
    fun shouldSerializeAllThreeFieldsToNbt() {
        val source = Files.readString(PROJECTILE_STATIC)

        assertTrue(source.contains("compound.putInt(\"LifeLength\", this.lifeLength)")) {
            "Life length should be serialized to NBT"
        }
        assertTrue(source.contains("compound.putFloat(\"PullStrength\", this.pullStrength)")) {
            "Pull strength should be serialized to NBT"
        }
        assertTrue(source.contains("compound.putFloat(\"Range\", this.range)")) {
            "Range should be serialized to NBT"
        }
        assertTrue(source.contains("compound.putInt(\"Age\", this.age)")) {
            "Age should be serialized to NBT"
        }
    }

    @Test
    fun shouldDeserializeAllThreeFieldsFromNbt() {
        val source = Files.readString(PROJECTILE_STATIC)

        assertTrue(source.contains("this.lifeLength = compound.getInt(\"LifeLength\")")) {
            "Life length should be deserialized from NBT"
        }
        assertTrue(source.contains("this.pullStrength = compound.getFloat(\"PullStrength\")")) {
            "Pull strength should be deserialized from NBT"
        }
        assertTrue(source.contains("this.range = compound.getFloat(\"Range\")")) {
            "Range should be deserialized from NBT"
        }
        assertTrue(source.contains("this.age = compound.getInt(\"Age\")")) {
            "Age should be deserialized from NBT"
        }
    }

    @Test
    fun shouldHaveDefaultValuesForAllProperties() {
        val source = Files.readString(PROJECTILE_STATIC)

        assertTrue(source.contains("get() = this.entityData.get<Int?>(DATA_LIFE_LENGTH) ?: 100")) {
            "Life length getter should fallback to 100"
        }
        assertTrue(source.contains("get() = this.entityData.get<Float?>(DATA_PULL_STRENGTH) ?: 0.08f")) {
            "Pull strength getter should fallback to 0.08f"
        }
        assertTrue(source.contains("get() = this.entityData.get<Float?>(DATA_RANGE) ?: 6.0f")) {
            "Range getter should fallback to 6.0f"
        }
    }

    @Test
    fun entityShouldExpireWhenAgeExceedsLifeLength() {
        val source = Files.readString(PROJECTILE_STATIC)

        assertTrue(source.contains("if (this.age >= this.lifeLength) {")) {
            "Entity should discard itself when age reaches life length"
        }
    }

    @Test
    fun entityShouldPullEntitiesEveryFourTicks() {
        val source = Files.readString(PROJECTILE_STATIC)

        assertTrue(source.contains("if (this.age % 4 == 0) {")) {
            "Entity should only pull entities every 4 ticks"
        }
    }

    @Test
    fun pullShouldUseDistanceBasedStrength() {
        val source = Files.readString(PROJECTILE_STATIC)

        assertTrue(source.contains("val strength = this.pullStrength * (1.0 - distSqr / ((range + 1) * (range + 1)))")) {
            "Pull strength should decrease with distance squared from center"
        }
    }

    @Test
    fun entityShouldHaveNoPhysicsAndNoGravity() {
        val source = Files.readString(PROJECTILE_STATIC)

        assertTrue(source.contains("this.noPhysics = true")) {
            "Projectile should have no physics (no collision)"
        }
        assertTrue(source.contains("this.setNoGravity(true)")) {
            "Projectile should have no gravity"
        }
    }
}
