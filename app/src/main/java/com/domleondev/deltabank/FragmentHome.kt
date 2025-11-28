package com.domleondev.deltabank

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class FragmentHome: Fragment() {
    companion object{
        fun newInstance(): Fragment{
            val fragmentHome = FragmentHome()
            val args = Bundle()
            fragmentHome.arguments = args
            return fragmentHome
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
}