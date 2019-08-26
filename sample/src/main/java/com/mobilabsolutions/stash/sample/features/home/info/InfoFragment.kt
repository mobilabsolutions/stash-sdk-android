package com.mobilabsolutions.stash.sample.features.home.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mobilabsolutions.stash.sample.databinding.FragmentInfoBinding
import dagger.android.support.DaggerFragment
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-08-2019.
 */
class InfoFragment : DaggerFragment() {
    @Inject
    lateinit var infoTextCreator: InfoTextCreator

    private lateinit var binding: FragmentInfoBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentInfoBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.textCreator = infoTextCreator
        return binding.root
    }
}