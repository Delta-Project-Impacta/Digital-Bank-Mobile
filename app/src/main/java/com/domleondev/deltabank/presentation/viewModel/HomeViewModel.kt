package com.domleondev.deltabank.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domleondev.deltabank.presentation.usecase.GetUserNameUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getUserNameUseCase: GetUserNameUseCase
) : ViewModel() {

    private val _userName = MutableStateFlow<String?>(null)
    val userName = _userName.asStateFlow()

    fun loadUserName() {
        viewModelScope.launch {
            val name = getUserNameUseCase()
            _userName.value = name
        }
    }
}