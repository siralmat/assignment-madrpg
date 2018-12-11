package siralmat.madrpg.model

import java.util.*
import androidx.room.*
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

import siralmat.madrpg.R


@Parcelize
@Entity(
    tableName = "items",
    foreignKeys = [
        ForeignKey(entity = Area::class, parentColumns = ["id"],
            childColumns = ["area"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(entity = Player::class, parentColumns = ["id"],
            childColumns = ["owner"], onDelete = ForeignKey.SET_NULL)],
    indices = [Index("area"), Index("owner")])
data class Item(
        @PrimaryKey val iid: String = UUID.randomUUID().toString(),
        val value: Int = 0,
        val description: String = "Nondescript item",
        val mass: Double = 0.0,
        val canEquip: Boolean = false,
        val effectType: Int = E_NONE,
        val effectAmount: Double = 0.0,
        var area: String? = null,
        var owner: String? = null
): Parcelable {

    fun salePrice(): Int {
        return (value * 0.75).toInt()
    }

    companion object {
        // Effect types
        const val E_NONE = R.string.equipment
        const val E_HEALTH = R.string.food
        const val E_SPECIAL = R.string.special
        const val E_VICTORY = R.string.victory

        // Named items
        const val IMPROBABILITY_DRIVE = "Improbability Drive"
        const val BEN_KENOBI = "Ben Kenobi"
        const val SMELLOSCOPE = "Smell-O-Scope"
        const val JADE_MONKEY = "Jade monkey"
        const val ICE_SCRAPER = "Ice scraper"
        const val ROADMAP = "Road map"
        // todo: refactor
    }
}

/**
 * Subset of item data for UI display.
 */
data class SimpleItem(
        val description: String,
        val x: Int,
        val y: Int,
        val effectType: Int)