package com.mobilabsolutions.payment.android.psdk.internal.uicomponents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobilabsolutions.payment.android.R
import kotlinx.android.synthetic.main.country_chooser_fragment.back
import java.util.Locale
import kotlin.collections.ArrayList

/**
 * @author [Ugljesa Jovanovic](ugi@mobilabsolutions.com)
 */
class CountryChooserFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.country_chooser_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = prepareCountryGroupAdapter()
        val recyclerView = view.findViewById<RecyclerView>(R.id.countryChooserRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        val searchView = view.findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                for (group in adapter.groups) {
                    group.filter(newText)
                }
                adapter.update()
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
        })

        back.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun prepareCountryGroupAdapter(): CountryGroupAdapter {
        val adapter = CountryGroupAdapter()

        // TODO: Default Location, make it optional to enable and set
        val locale = Locale.getDefault()
        adapter.addGroup(CountryGroup("Current location", listOf(Country(locale.displayCountry, locale.country, locale.isO3Country))))

        getCountryList()
            .groupByTo(mutableMapOf()) { it.displayName[0] }
            .forEach {
                adapter.addGroup(CountryGroup(it.key.toString(), it.value))
            }

        return adapter
    }

    private fun getCountryList(): List<Country> {
        val countryList = arrayListOf<Country>()

        for (code in Locale.getISOCountries()) {
            val locale = Locale("", code)
            val displayName = locale.displayCountry
            if (displayName.isNotBlank()) {
                try {
                    countryList.add(Country(displayName, locale.country, locale.isO3Country))
                } catch (ignore: Exception) {
                }
            }
        }

        return ArrayList(countryList.sortedWith(compareBy { it.displayName }))
    }
}
