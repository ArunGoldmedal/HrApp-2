package com.goldmedal.hrapp.util

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class BaseGenericRecyclerViewAdapter<T> protected constructor(initialItems: List<T>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val items: ArrayList<T> = kotlin.collections.ArrayList(initialItems)

    abstract fun setViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder
    abstract fun getViewType(position: Int): Int
    abstract fun onBindData(holder: RecyclerView.ViewHolder?, data: T)

    open fun areItemsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem
    open fun areContentsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return setViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        onBindData(viewHolder, items[position])
    }

    override fun getItemViewType(position: Int): Int {
        return getViewType(position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun addItem(`object`: T) {
        items.add(`object`)
        notifyItemInserted(items.size - 1)
    }

    fun addItemAt(`object`: T, position: Int) {
        items.add(position, `object`)
        notifyItemInserted(position)
        notifyItemRangeChanged(position, items.size)
    }

    fun addAllItems(newItems: List<T>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize() = items.size
            override fun getNewListSize() = newItems.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return this@BaseGenericRecyclerViewAdapter.areItemsTheSame(
                    items[oldItemPosition],
                    newItems[newItemPosition]
                )
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return this@BaseGenericRecyclerViewAdapter.areContentsTheSame(
                    items[oldItemPosition],
                    newItems[newItemPosition]
                )
            }
        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun removeItemAt(position: Int): T {
        val item: T = items[position]
        items.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, items.size)
        return item
    }

    fun replaceItem(newItem: T, position: Int) {
        items[position] = newItem
        notifyItemChanged(position)
    }

    fun getItem(position: Int): T {
        return items[position]
    }
}
