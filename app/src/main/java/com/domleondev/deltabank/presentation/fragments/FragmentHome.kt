package com.domleondev.deltabank.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.domleondev.deltabank.R
import com.domleondev.deltabank.presentation.activities.TransferHomeActivity
import com.google.android.material.card.MaterialCardView

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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentHomePixTrasfer = view.findViewById<MaterialCardView>(R.id.fragment_Home_Pix_Trasfer)

        fragmentHomePixTrasfer.setOnClickListener {
             val intent = Intent(requireContext(), TransferHomeActivity::class.java)
            startActivity(intent)
        }
    }
}
