package com.mobilabsolutions.payment.android.psdk.internal.uicomponents

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.mobilabsolutions.payment.android.R
import kotlinx.android.synthetic.main.country_chooser_name.view.textView

class CountryGroupAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    internal val groups: MutableList<CountryGroup> = ArrayList()
    private var lastSelected: View? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> TextViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.country_chooser_group, parent, false))
            else -> TextViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.country_chooser_name, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return getFilteredGroups()
            .sumBy { it.length }
    }

    private fun getFilteredGroups(): List<CountryGroup> {
        return groups.filter { it.filteredList.isNotEmpty() }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var lastPosition = 0
        for (group in getFilteredGroups()) {
            val length = group.length
            if (position in lastPosition until lastPosition + length) {
                val viewHolder = holder as TextViewHolder
                when (position) {
                    lastPosition -> viewHolder.textView.text = group.title
                    else -> {
                        val index = position - lastPosition - 1
                        viewHolder.textView.text = group.filteredList[index].displayName
                        viewHolder.rootView.setOnClickListener {
                            updateSelection(it)
                            onCountryClicked(group.filteredList[index])

                            // TODO: Remove
                            with(group.filteredList[index]) {
                                Snackbar.make(it, "${this.displayName} [${this.alpha2Code}][${this.alpha3Code}]", com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
            lastPosition += length
        }
    }

    private fun onCountryClicked(country: Country) {
        //TODO: Pass the selected country back
    }

    override fun getItemViewType(position: Int): Int {
        var lastPosition = 0
        return getFilteredGroups()
            .map { it.length }
            .mapIndexed { _, i ->
                lastPosition += i; lastPosition - i // Start with 0, Skip the last
            }
            .contains(position)
            .toInt()
    }

    fun addGroup(group: CountryGroup) {
        groups.add(group)
    }

    private fun Boolean.toInt(): Int {
        return when (this) {
            true -> 1
            else -> 0
        }
    }

    private fun updateSelection(view: View?) {
        lastSelected?.isSelected = false
        view?.isSelected = true
        lastSelected = view
    }

    fun update() {
        // Old selected item may invisible as query grows,
        // but selection remains on index.
        // Clear selection
        updateSelection(null)
        notifyDataSetChanged()
    }
}

class TextViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val rootView = view.rootView
    val textView: TextView = view.textView
}

data class Country(
    val displayName: String,
    val alpha2Code: String,
    val alpha3Code: String
)

class CountryGroup(
    internal val title: String,
    private val list: List<Country>
) {
    internal var filteredList: List<Country> = ArrayList(list)

    fun filter(query: String) {
        if (TextUtils.isEmpty(query)) {
            filteredList = ArrayList(list)
        } else {
            filteredList = list.filter { it.displayName.toLowerCase().contains(query.toLowerCase()) }
        }
    }

    val length: Int
        get() {
            return filteredList.size + 1 // Including Title
        }
}