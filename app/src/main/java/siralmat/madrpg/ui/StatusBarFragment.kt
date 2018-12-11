package siralmat.madrpg.ui

import android.app.AlertDialog
import android.content.Context
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_status_bar.*
import kotlinx.android.synthetic.main.fragment_status_bar.view.*

import siralmat.madrpg.GameState
import siralmat.madrpg.R
import siralmat.madrpg.model.Player


class StatusBarFragment: androidx.fragment.app.Fragment() {
    private lateinit var model: StatusViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        model = activity?.run {
            ViewModelProviders.of(this).get(StatusViewModel::class.java)
        } ?: throw Exception("Invalid activity")
        model.init(GameState.getInstance(context.applicationContext))

        model.player.observe(this, Observer { it?.let { p-> updateInfo(p) }})
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_status_bar, container, false)
        v.btnRestart.setOnClickListener {
            restartDialog(getString(R.string.title_restart), getString(R.string.msg_restart)) }
        return v
    }

    private fun updateInfo(player: Player) {
        Log.d(TAG, "Updating player status.")
        playerCash.text = player.cash.toString()
        playerHealth.text = "%.0f".format(player.health)
        playerMass.text = "%.2f".format(player.equipmentMass)
        iconJadeMonkey.visibility = if (player.hasJadeMonkey) View.VISIBLE else View.INVISIBLE
        iconRoadmap.visibility = if (player.hasRoadmap) View.VISIBLE else View.INVISIBLE
        iconIceScraper.visibility = if (player.hasIceScraper) View.VISIBLE else View.INVISIBLE
        if (model.checkVictory(player)) {
            restartDialog(getString(R.string.title_victory), getString(R.string.msg_victory))
        } else if (player.health <= 0) {
            restartDialog(getString(R.string.title_gameover), getString(R.string.msg_gameover))
            (activity as GameActivity).onGameOverEvent()
        }
    }



    private fun restartDialog(title: String, message: String) {
        AlertDialog.Builder(this.context).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("Yes") { _, _ ->
                Toast.makeText(this.context, "Restarting game", Toast.LENGTH_SHORT).show()
                model.restart()
                (activity as GameActivity).onRestartEvent() }
            setNegativeButton("Cancel") { _, _ -> }
            show()
        }
    }

    companion object {
        const val TAG = "StatusBarFragment"
    }
}
