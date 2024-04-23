package com.shahbaz.letstalk.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shahbaz.letstalk.R
import com.shahbaz.letstalk.databinding.FragmentStoriesBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class StoriesFragment : Fragment() {

    private lateinit var binding:FragmentStoriesBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentStoriesBinding.inflate(inflater,container,false)
        return binding.root
    }

}