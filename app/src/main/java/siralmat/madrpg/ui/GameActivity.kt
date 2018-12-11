package siralmat.madrpg.ui

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import siralmat.madrpg.R

/** Shared interface to enable fragment communication with activities.
 * https://developer.android.com/training/basics/fragments/communicating
 **/
interface GameActivity {
    fun onRestartEvent()

    fun onGameOverEvent()

    fun showToast(message: String, c: Context) {
        val t = Toast.makeText(c, message, Toast.LENGTH_SHORT)
        t.setGravity(Gravity.TOP, 0, 100)
        t.view.setBackgroundColor(t.view.resources.getColor(R.color.colorAccent))
        t.show()
    }
}