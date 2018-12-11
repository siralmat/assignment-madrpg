package siralmat.madrpg.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/** Data class for player character. Stores info about current state:
 *  - Position (X/Y coordinates)
 *  - Health
 *  - Available cash
 *  - Equipment mass
 * Implementation notes:
 * - Player *could* have a FK referencing an area, but this gets unpleasant when
 * areas are regenerated. Instead, the player's location is stored as its
 * raw coordinates, which can be related to a specific area instance later.
 * - Player has an ID field to simplify database communications. Only one
 * player exists at a time so any Player is assumed to have the ID `PLAYER_ID`.
 * - The equipment list is not stored with the player. Unfortunately
 * LiveData makes this unfeasible.
 * - The victory conditions are stored as separate booleans in the player. While
 * this is not particularly scalable or flexible, it is by far the most efficient
 * method of storing the victory state.
 **/
@Parcelize
@Entity(tableName = "player")
class Player (
        var cash: Int = 0,
        var health: Double = MAX_HEALTH,
        var equipmentMass: Double = 0.0,
        @PrimaryKey var id: String = PLAYER_ID, // only var because weird compiler error otherwise
        var currentX: Int = 0,
        var currentY: Int = 0,
        var hasJadeMonkey: Boolean = false,
        var hasRoadmap: Boolean = false,
        var hasIceScraper: Boolean = false
): Parcelable {

    /** Update player's current location & health **/
    fun move(newX: Int, newY: Int): Double {
        currentX = newX
        currentY = newY
        updateHealth(-5.0 - (equipmentMass/2.0))
        return health
    }


    /** Modify player's health by the specified amount, without going below
     * minimum or above maximum valid health. **/
    fun updateHealth(change: Double) {
        health = (health + change).coerceIn(MIN_HEALTH, MAX_HEALTH)
    }


    fun pickup(item: Item) {
        if (item.effectType == Item.E_VICTORY) {
            updateVictoryState(item.description, true)
        }
        equipmentMass += item.mass
    }


    /** Attempt to deduct an amount of money from player's available cash.
     * Returns true if deduction was successful. **/
    fun buy(cost: Int): Boolean {
        if (cost <= cash) {
            cash -= cost
            return true
        }
        return false
    }


    fun sell(salePrice: Int) {
        cash += salePrice
    }


    fun drop(item: Item) {
        if (item.effectType == Item.E_VICTORY) {
            updateVictoryState(item.description, false)
        }
        equipmentMass -= item.mass
    }

    fun updateVictoryState(itemType: String, itemStatus: Boolean) {
        when (itemType) {
            Item.ROADMAP -> hasRoadmap = itemStatus
            Item.JADE_MONKEY -> hasJadeMonkey = itemStatus
            Item.ICE_SCRAPER -> hasIceScraper = itemStatus
        }

    }

    companion object {
        const val MAX_HEALTH = 100.0
        const val MIN_HEALTH = 0.0
        const val PLAYER_ID = "PlayerID"
    }

}

