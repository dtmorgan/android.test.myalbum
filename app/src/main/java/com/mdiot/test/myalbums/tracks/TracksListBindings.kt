package com.mdiot.test.myalbums.tracks

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mdiot.test.myalbums.data.Track

/**
 * [BindingAdapter]s for the [Track]s list.
 */
@BindingAdapter("app:items")
fun setItems(listView: RecyclerView, items: List<Track>?) {
    items?.let {
        (listView.adapter as TracksAdapter).submitList(items)
    }
}