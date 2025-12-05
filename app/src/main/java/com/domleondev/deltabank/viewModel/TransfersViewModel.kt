package com.domleondev.deltabank.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domleondev.deltabank.repository.tranfersrepository.TransfersRepository
import kotlinx.coroutines.launch

class TransfersViewModel: ViewModel() {

    private val repository = TransfersRepository()

    private val _banks = MutableLiveData<List<BankItem>>()
    val banks: LiveData<List<BankItem>> get() = _banks

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _errorsList = MutableLiveData<List<ValidationError>>()
    val errorsList: LiveData<List<ValidationError>> get() = _errorsList

    private val _isButtonEnabled = MutableLiveData(true)
    val isButtonEnabled: LiveData<Boolean> get() = _isButtonEnabled

    fun validateFields(bank: String?, agency: String?, account: String?, accountType: String?) {

        // CRIA A LISTA DE ERROS E REVERTE A LÓGICA DO WHEN PARA IF
        val currentErrors = mutableListOf<ValidationError>()

        // Validação 1: Banco
        if (bank.isNullOrEmpty()) {
            currentErrors.add(ValidationError.EmptyBank)

        }

        // Validação 2: Agência
        if (agency.isNullOrEmpty() || agency.length < 4 || agency == "0000") {
            currentErrors.add(ValidationError.EmptyAgency)
        }

        // Validação 3: Conta (Vazio e Tamanho Mínimo)
        if (account.isNullOrEmpty() || account.length < 4) {
            currentErrors.add(ValidationError.EmptyAccount)
        } else if (!isValidAccount(account)) { // Validação 4: Formato da conta
            currentErrors.add(ValidationError.InvalidAccount)
        }

        // Validação 5: Tipo de Conta
        if (accountType.isNullOrEmpty()) {
            currentErrors.add(ValidationError.EmptyAccountType)
        }


        // 3. Verifica o resultado e atualiza os LiveDatas
        if (currentErrors.isEmpty()) {
            _allValid.postValue(true)
        } else {
            _errorsList.postValue(currentErrors) // Envia a lista completa de erros
            _isButtonEnabled.postValue(false)  //  habilita/desabilita
            _allValid.postValue(false) // Garante que a navegação não ocorra
        }
    }

    private fun isValidAccount(account: String): Boolean {
        // Permite de 1 a 8 dígitos, seguido opcionalmente por um hífen e um dígito (ou X)
        val regex = Regex("^\\d{1,8}(-[\\dXx])?$")
        return regex.matches(account)
    }

    private val _allValid = MutableLiveData<Boolean>()
    val allValid: LiveData<Boolean> get() = _allValid

    data class BankItem(
        val code: Int,
        val name: String,
        val displayName: String
    )

    fun seekBanks() {
        viewModelScope.launch {
            _loading.postValue(true)
            try {
                val result = repository.seekBanks()
                if (!result.isNullOrEmpty()) {
                    val mapped = result.map { bank ->
                        BankItem(
                            code = bank.code,
                            name = bank.fullName,
                            displayName = "${bank.code} - ${bank.fullName}"
                        )
                    }
                    _banks.postValue(mapped)
                }
            } finally {
                _loading.postValue(false)
            }
        }
    }

    sealed class ValidationError {
        object EmptyBank : ValidationError()
        object EmptyAgency : ValidationError()
        object EmptyAccount : ValidationError()
        object InvalidAccount : ValidationError()
        object EmptyAccountType : ValidationError()
    }
}