package com.domleondev.deltabank.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.domleondev.deltabank.domain.usecase.GetUserNameUseCase


class HomeViewModelFactory(
    private val getUserNameUseCase: GetUserNameUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(getUserNameUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}