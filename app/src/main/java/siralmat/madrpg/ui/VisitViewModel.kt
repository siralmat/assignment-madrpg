package siralmat.madrpg.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

import siralmat.madrpg.GameState
import siralmat.madrpg.model.Area
import siralmat.madrpg.model.Item
import siralmat.madrpg.model.Player

class VisitViewModel: ViewModel() {
    private lateinit var game: GameState
    var initialized = false

    lateinit var areaItems: LiveData<List<Item>>
    lateinit var playerItems: LiveData<List<Item>>
    lateinit var player: LiveData<Player>

    fun init(inGame: GameState) {
        if (!initialized) {
            game = inGame
            areaItems = game.areaItems
            playerItems = game.playerInventory
            player = game.player
            initialized = true
        }
    }

    fun getDrop(item: Item, player: Player, area: Area, list: Int): Boolean {
        return when(list) {
            ItemListAdapter.BUY -> buyItem(item, player)
            ItemListAdapter.SELL -> sellItem(item, player, area)
            ItemListAdapter.DROP -> dropItem(item, player, area)
            ItemListAdapter.PICKUP -> pickupItem(item, player)
            else -> false
        }
    }

    fun useItem(item: Item): String {
        Log.d(TAG, "Attempting to use item ${item.description}")
        game.destroyItem(item)
        var msg = "Nothing happened..."
        if (item.effectType == Item.E_HEALTH) {
            player.value?.let {
                it.updateHealth(item.effectAmount)
                game.updatePlayer(it)
                msg = "Health changed by ${item.effectAmount} points!"
            }
        } else if (item.description == Item.BEN_KENOBI) {
            takeAllItems()
            msg = "If you define yourself by your power to take life, the desire to dominate, to possess? Then you have nothing."
        } else if (item.description == Item.IMPROBABILITY_DRIVE) {
            game.regenerateMap()
            msg = "Map has been regenerated!"
        } else if (item.description == Item.SMELLOSCOPE) {
            msg = "Sniffing out the best bargains..."
        }
        return msg
    }

    private fun takeAllItems() {
        player.value?.let { p ->
            areaItems.value?.forEach { pickupItem(it, p) }
        }
    }

    fun canUse(item: Item): Boolean {
        return (item.owner != null &&
                (item.effectType == Item.E_SPECIAL || item.effectType == Item.E_HEALTH))
    }

    fun dropItem(item: Item, player: Player, area: Area): Boolean {
        return game.dropItem(item, player, area)
    }

    fun pickupItem(item: Item, player: Player): Boolean {
        Log.d("VisitViewModel", "Picking up item for player ${player.id}")
        return game.pickupItem(item, player)
    }

    fun buyItem(item: Item, player: Player): Boolean {
        return game.buyItem(item, player)
    }

    fun sellItem(item: Item, player: Player, area: Area): Boolean {
        return game.sellItem(item, player, area)
    }

    companion object {
        const val TAG = "VisitViewModel"
    }
}