package com.fajar.kmp.pos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fajar.kmp.core.network.data.AuthLoginRequest
import com.fajar.kmp.core.network.data.AuthRegisterRequest
import com.fajar.kmp.core.network.data.StoreRegisterRequest
import com.fajar.kmp.core.network.data.SyncRequest
import com.fajar.kmp.core.network.data.TransactionCreateRequest
import com.fajar.kmp.feature.pos.domain.repository.PosError
import com.fajar.kmp.feature.pos.domain.repository.PosRepository
import com.fajar.kmp.feature.pos.domain.repository.PosResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PosShellViewModel(
    private val repository: PosRepository,
    private val coroutineScope: CoroutineScope? = null,
) : ViewModel() {
    private val _state = MutableStateFlow(PosShellState())
    val state: StateFlow<PosShellState> = _state.asStateFlow()

    fun login(email: String, password: String) {
        launchShellUpdate {
            _state.update { it.copy(isAuthLoading = true) }
            when (val result = repository.login(AuthLoginRequest(email, password))) {
                is PosResult.Success -> _state.update {
                    it.copy(authStatus = "Authenticated", isAuthLoading = false)
                }
                is PosResult.Failure -> _state.update {
                    it.copy(authStatus = result.error.toStatusMessage(), isAuthLoading = false)
                }
            }
        }
    }

    fun register(email: String, fullName: String, password: String, phone: String?, tenantId: String?) {
        launchShellUpdate {
            _state.update { it.copy(isAuthLoading = true) }
            val request = AuthRegisterRequest(
                email = email,
                fullName = fullName,
                password = password,
                phone = phone?.ifBlank { null },
                tenantId = tenantId?.ifBlank { null },
            )
            when (val result = repository.register(request)) {
                is PosResult.Success -> _state.update {
                    it.copy(authStatus = "Registered", isAuthLoading = false)
                }
                is PosResult.Failure -> _state.update {
                    it.copy(authStatus = result.error.toStatusMessage(), isAuthLoading = false)
                }
            }
        }
    }

    fun registerStore(request: StoreRegisterRequest) {
        launchShellUpdate {
            _state.update { it.copy(isStoreLoading = true) }
            when (val result = repository.registerStore(request)) {
                is PosResult.Success -> _state.update {
                    it.copy(storeStatus = "Store registered: ${result.value}", isStoreLoading = false)
                }
                is PosResult.Failure -> _state.update {
                    it.copy(storeStatus = result.error.toStatusMessage(), isStoreLoading = false)
                }
            }
        }
    }

    fun loadCatalog(storeId: String) {
        launchShellUpdate {
            _state.update { it.copy(isCatalogLoading = true) }
            when (val result = repository.listProducts(storeId)) {
                is PosResult.Success -> _state.update {
                    it.copy(catalogStatus = result.value.ifBlank { "Catalog empty" }, isCatalogLoading = false)
                }
                is PosResult.Failure -> _state.update {
                    it.copy(catalogStatus = result.error.toStatusMessage(), isCatalogLoading = false)
                }
            }
        }
    }

    fun checkout(storeId: String, request: TransactionCreateRequest) {
        launchShellUpdate {
            _state.update { it.copy(isCheckoutLoading = true) }
            when (val result = repository.createTransaction(storeId, request)) {
                is PosResult.Success -> _state.update {
                    it.copy(checkoutStatus = "Checkout complete: ${result.value}", isCheckoutLoading = false)
                }
                is PosResult.Failure -> _state.update {
                    it.copy(checkoutStatus = result.error.toStatusMessage(), isCheckoutLoading = false)
                }
            }
        }
    }

    fun sync(storeId: String, request: SyncRequest) {
        launchShellUpdate {
            _state.update { it.copy(isSyncLoading = true) }
            when (val result = repository.sync(storeId, request)) {
                is PosResult.Success -> _state.update {
                    it.copy(syncStatus = "Synced: ${result.value}", isSyncLoading = false)
                }
                is PosResult.Failure -> _state.update {
                    it.copy(syncStatus = result.error.toStatusMessage(), isSyncLoading = false)
                }
            }
        }
    }

    fun loadAdmin() {
        launchShellUpdate {
            _state.update { it.copy(isAdminLoading = true) }
            when (val result = repository.adminStats()) {
                is PosResult.Success -> _state.update {
                    it.copy(adminStatus = result.value, isAdminLoading = false)
                }
                is PosResult.Failure -> _state.update {
                    it.copy(adminStatus = result.error.toStatusMessage(), isAdminLoading = false)
                }
            }
        }
    }

    private fun launchShellUpdate(block: suspend CoroutineScope.() -> Unit) {
        (coroutineScope ?: viewModelScope).launch(block = block)
    }
}

private fun PosError.toStatusMessage(): String = when (this) {
    PosError.Unauthorized -> "Unauthorized"
    is PosError.MissingData -> message
    is PosError.Network -> message
}
