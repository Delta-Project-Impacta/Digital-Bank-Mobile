package com.domleondev.deltabank.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.domleondev.deltabank.R
import com.domleondev.deltabank.presentation.activities.HomeActivity

class FragmentProfile: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentProfileButtonBack =
            view.findViewById<ImageView>(R.id.fragment_Profile_Button_Back)

        fragmentProfileButtonBack.setOnClickListener {
            val intent = Intent(requireContext(), HomeActivity::class.java)
            startActivity(intent)
        }
    }
}