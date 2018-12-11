package siralmat.madrpg.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.grid_area_detail.view.*

import siralmat.madrpg.R
import siralmat.madrpg.model.Area


class MapGridAdapter(private val onClick: (Area) -> Unit)
    : ListAdapter<Area, MapGridAdapter.MapViewHolder>(MapDiffCallback())
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.grid_area_detail, parent, false)
        return MapViewHolder(view)
    }

    override fun onBindViewHolder(holder: MapViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.view.setOnClickListener { onClick(item) }
    }

    class MapViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        fun bind(a: Area) {
            if (a.explored) {
                val icon = if (a.town) R.string.town else R.string.wilderness
                view.areaIcon.text = view.resources.getString(icon)
                view.setBackgroundColor(Color.TRANSPARENT)
            } else {
                view.setBackgroundColor(Color.BLACK)
                view.areaIcon.text = view.resources.getString(R.string.unexplored)
            }

            if (a.starred) {
                view.starIcon.visibility = View.VISIBLE
            } else {
                view.starIcon.visibility = View.INVISIBLE
            }
        }
    }
}


class MapDiffCallback: DiffUtil.ItemCallback<Area>() {
    override fun areItemsTheSame(oldItem: Area, newItem: Area): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Area, newItem: Area): Boolean {
        return oldItem == newItem
    }
}