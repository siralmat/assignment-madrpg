package siralmat.madrpg.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

import siralmat.madrpg.GameState
import siralmat.madrpg.model.Player

/**
 * Expose player status info to UI.
 */
class StatusViewModel: ViewModel() {
    lateinit var player: LiveData<Player>
    private lateinit var game: GameState
    private var initialized = false

    fun init(inGame: GameState) {
        if (!initialized) {
            game = inGame
            player = game.player
            initialized = true
        }
    }

    fun checkVictory(player: Player): Boolean {
        return (player.hasIceScraper &&
                player.hasJadeMonkey &&
                player.hasRoadmap)
    }

    /**
     * Handle a request to restart the game.
     */
    fun restart() {
        game.restartGame()
    }

}