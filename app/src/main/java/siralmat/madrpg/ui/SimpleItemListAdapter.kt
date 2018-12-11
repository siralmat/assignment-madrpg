package siralmat.madrpg.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_simple.view.*

import siralmat.madrpg.R
import siralmat.madrpg.model.SimpleItem

class SimpleItemListAdapter
: ListAdapter<SimpleItem, SimpleItemHolder>(SimpleItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleItemHolder {
        return SimpleItemHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_simple, parent, false))
    }

    override fun onBindViewHolder(holder: SimpleItemHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}


class SimpleItemHolder(val view: View): RecyclerView.ViewHolder(view) {
    lateinit var item: SimpleItem

    fun bind(newItem: SimpleItem) {
        item = newItem
        view.xCoord.text = item.x.toString()
        view.yCoord.text = item.y.toString()
        view.itemName.text = item.description
        view.itemIcon.text = view.context.resources.getString(item.effectType)
    }
}


class SimpleItemDiffCallback: DiffUtil.ItemCallback<SimpleItem>() {
    override fun areItemsTheSame(oldItem: SimpleItem, newItem: SimpleItem): Boolean {
        return oldItem == newItem
    }
    override fun areContentsTheSame(oldItem: SimpleItem, newItem: SimpleItem): Boolean {
        return oldItem == newItem
    }
}

