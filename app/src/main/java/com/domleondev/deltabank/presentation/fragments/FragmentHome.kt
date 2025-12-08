package com.domleondev.deltabank.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.domleondev.deltabank.R
import com.domleondev.deltabank.presentation.activities.CardsActivity
import com.domleondev.deltabank.presentation.activities.PaymentHomeActivity
import com.domleondev.deltabank.presentation.activities.TransferHomeActivity
import com.google.android.material.card.MaterialCardView

class FragmentHome: Fragment() {

    private var isBalanceVisible = false

    private lateinit var balanceTextView : TextView
    private lateinit var toggleIcon : ImageView

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

        val fragmentHomePixTransfer = view.findViewById<MaterialCardView>(R.id.fragment_Home_Pix_Transfer)

        fragmentHomePixTransfer.setOnClickListener {
             val intent = Intent(requireContext(), TransferHomeActivity::class.java)
            startActivity(intent)
        }

        val fragmentHomePayTransfer = view.findViewById<MaterialCardView>(R.id.fragment_Home_Pay_Transfer)

        fragmentHomePayTransfer.setOnClickListener {
            val intent = Intent(requireContext(), PaymentHomeActivity::class.java)
            startActivity(intent)
        }

        val fragmentHomeCards = view.findViewById<MaterialCardView>(R.id.fragment_Home_Cards)

        fragmentHomeCards.setOnClickListener {
            val intent = Intent(requireContext(), CardsActivity::class.java)
            startActivity(intent)
        }

        balanceTextView = view.findViewById(R.id.fragment_Home_Balance_Amount_Text)
        toggleIcon = view.findViewById(R.id.fragment_Home_Balance_Toggle_Icon)

        val realBalance = getString(R.string.home_balance_amount)
        val maskedBalance = "●●●●●●●"

        balanceTextView.text = maskedBalance
        toggleIcon.setImageResource(R.drawable.ic_eye_off)

        toggleIcon.setOnClickListener {

            isBalanceVisible = !isBalanceVisible

            if (isBalanceVisible) {
                balanceTextView.text = realBalance
                toggleIcon.setImageResource(R.drawable.ic_eye)
            } else {
                balanceTextView.text = maskedBalance
                toggleIcon.setImageResource(R.drawable.ic_eye_off)
            }
        }

    }
}
