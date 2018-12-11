package siralmat.madrpg.ui

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import kotlinx.android.synthetic.main.fragment_area_info.*
import kotlinx.android.synthetic.main.fragment_area_info.view.*

import siralmat.madrpg.GameState
import siralmat.madrpg.R
import siralmat.madrpg.model.Area


/**
 * Shows detailed info about an area - type, coordinates, description, & starred
 * status. Description and starred can be updated by the user.
 * Updates are sent/received through an AreaViewModel scoped to the containing
 * activity, so the activity should initialize the VM to show the desired area.
 */
class AreaInfoFragment : androidx.fragment.app.Fragment() {
    private lateinit var model: AreaViewModel

    // Declare the description and star watchers here so they it can be unset
    // when the field is updated manually
    private val descriptionWatcher = object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            // todo: currently sends updates for every character
            s?.toString()?.let { txt ->
                model.area.value?.let { a -> model.updateDescription(a.id, txt) } } }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private val checkboxWatcher =
            CompoundButton.OnCheckedChangeListener { _, isChecked ->
                model.area.value?.let { a -> model.updateStarred(a.id, isChecked) } }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        model = activity?.run {
            ViewModelProviders.of(this).get(AreaViewModel::class.java)
        } ?: throw Exception("Invalid activity")
        // If VM not initialized, set to default (player location).
        model.init(GameState.getInstance(context.applicationContext), null)

        model.area.observe(this, Observer {
            it?.let { updateDisplay(it) }})
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_area_info, container, false)

        view.starIcon.setOnCheckedChangeListener(checkboxWatcher)
        view.txtDescription.addTextChangedListener(descriptionWatcher)

        return view
    }


    private fun updateDisplay(a: Area) {
        areaType.text = if (a.town) resources.getString(R.string.town) else
            resources.getString(R.string.wilderness)
        txtXCoord.text = a.x.toString()
        txtYCoord.text = a.y.toString()

        txtDescription.removeTextChangedListener(descriptionWatcher)
        txtDescription.setText(a.description)
        txtDescription.addTextChangedListener(descriptionWatcher)

        starIcon.setOnCheckedChangeListener(null)
        starIcon.isChecked = a.starred
        starIcon.setOnCheckedChangeListener(checkboxWatcher)
    }

    companion object {
        const val TAG = "AreaInfoFragment"
    }
}
