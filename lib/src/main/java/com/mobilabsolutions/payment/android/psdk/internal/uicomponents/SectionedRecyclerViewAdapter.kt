package com.mobilabsolutions.payment.android.psdk.internal.uicomponents

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class SectionedRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val groups: List<AlphabetGroup> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            0 -> TextViewHolder(LayoutInflater.from(parent.context).inflate(0, parent, false))
            else -> TextViewHolder(LayoutInflater.from(parent.context).inflate(0, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return groups
            .sumBy { it.length }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemViewType(position: Int): Int {
        var sum = 0
        return groups
            .map { it.length }
            .mapIndexed { _, i ->
                sum += i; sum - i //Start with 0, Skip the last
            }
            .contains(position)
            .toInt()
    }

    private fun Boolean.toInt(): Int {
        return if (this) {
            1
        } else {
            0
        }
    }
}


class TextViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val rootView = view.rootView
}

class AlphabetGroup(
    private val title: String,
    private val list: List<String>

) {
    private var filteredList: List<String> = ArrayList()

    val length: Int
        get() {
            return list.size + 1 // Including Title
        }
}