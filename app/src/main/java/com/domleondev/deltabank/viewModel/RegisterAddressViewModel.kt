package com.domleondev.deltabank.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domleondev.deltabank.repository.addressrepository.AddressRepository
import com.domleondev.deltabank.repository.response.CityResponse
import com.domleondev.deltabank.repository.response.StateResponse
import com.domleondev.deltabank.repository.response.ViaCepResponse
import kotlinx.coroutines.launch

class RegisterAddressViewModel : ViewModel() {

    private val repository = AddressRepository()

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _allValid = MutableLiveData<Boolean>()
    val allValid: LiveData<Boolean> = _allValid

    private val _viaCepData = MutableLiveData<ViaCepResponse?>()
    val viaCepData: LiveData<ViaCepResponse?> = _viaCepData

    private val _estados = MutableLiveData<List<StateResponse>>()
    val estados: LiveData<List<StateResponse>> = _estados

    private val _cidades = MutableLiveData<List<CityResponse>>()
    val cidades: LiveData<List<CityResponse>> = _cidades

    val listError = mutableListOf<String>()

    fun buscarCep(cep: String) {
        _loading.postValue(true)
        viewModelScope.launch {
            try {
                val result = repository.buscarCep(cep)
                if (result != null && result.cep != null) {
                    _viaCepData.postValue(result)
                } else {
                    _error.postValue("CEP não encontrado.")
                }
            } catch (e: Exception) {
                _error.postValue("Erro ao buscar CEP.")
            } finally {
                _loading.postValue(false)
            }
        }
    }

    fun buscarEstados() {
        viewModelScope.launch {
            try {
                val result = repository.buscarEstados()
                if (!result.isNullOrEmpty()) {
                    _estados.postValue(result)
                } else {
                    _error.postValue("Erro ao carregar estados.")
                }
            } catch (e: Exception) {
                _error.postValue("Falha na conexão com o IBGE.")
            }
        }
    }

    fun buscarCidades(uf: String) {
        viewModelScope.launch {
            try {
                val result = repository.buscarCidades(uf)
                if (!result.isNullOrEmpty()) {
                    _cidades.postValue(result)
                } else {
                    _error.postValue("Nenhuma cidade encontrada para $uf.")
                }
            } catch (e: Exception) {
                _error.postValue("Erro ao carregar cidades.")
            }
        }
    }

    fun validarCampos(
        cep: String?,
        endereco: String?,
        numero: String?,
        cidade: String?,
        estado: String?
    ) {
        listError.clear()

        if (cep.isNullOrEmpty()) listError.add("CEP é obrigatório.")
        if (endereco.isNullOrEmpty()) listError.add("Endereço é obrigatório.")
        if (numero.isNullOrEmpty()) listError.add("Número é obrigatório.")
        if (cidade.isNullOrEmpty()) listError.add("Cidade é obrigatória.")
        if (estado.isNullOrEmpty()) listError.add("Estado é obrigatório.")

        if (listError.isEmpty()) {
            _allValid.postValue(true)
        } else {
            var mensagem = ""
            for (erro in listError) mensagem += "$erro\n"
            _error.postValue(mensagem.trim())
        }
    }
}
