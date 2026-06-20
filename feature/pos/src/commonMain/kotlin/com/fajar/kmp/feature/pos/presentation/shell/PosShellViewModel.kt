package com.fajar.kmp.feature.pos.presentation.shell

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fajar.kmp.core.common.result.Try
import com.fajar.kmp.core.datastore.SessionPreferences
import com.fajar.kmp.feature.pos.data.api.AuthLoginRequest
import com.fajar.kmp.feature.pos.data.api.AuthRegisterRequest
import com.fajar.kmp.feature.pos.data.api.StoreRegisterRequest
import com.fajar.kmp.feature.pos.data.api.SyncRequest
import com.fajar.kmp.feature.pos.data.api.TransactionCreateRequest
import com.fajar.kmp.feature.pos.domain.repository.PosRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PosShellViewModel(
    private val repository: PosRepository,
    private val coroutineScope: CoroutineScope? = null,
    private val sessionPreferences: SessionPreferences? = null,
) : ViewModel() {
    private val _state = MutableStateFlow(PosShellState())
    val state: StateFlow<PosShellState> = _state.asStateFlow()

    init {
        hydrateSession()
    }

    fun login(email: String, password: String) {
        launchShellUpdate {
            _state.update { it.copy(isAuthLoading = true) }
            when (val result = repository.login(AuthLoginRequest(email, password))) {
                is Try.Success -> _state.update {
                    it.copy(isAuthenticated = true, authStatus = "Berhasil masuk", isAuthLoading = false)
                }
                is Try.Error -> _state.update {
                    it.copy(authStatus = result.result.toStatusMessage(), isAuthLoading = false)
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
                is Try.Success -> {
                    _state.update {
                        it.copy(isAuthenticated = true, authStatus = "Akun berhasil dibuat", isAuthLoading = false)
                    }
                }
                is Try.Error -> _state.update {
                    it.copy(authStatus = result.result.toStatusMessage(), isAuthLoading = false)
                }
            }
        }
    }

    fun registerStore(request: StoreRegisterRequest) {
        launchShellUpdate {
            _state.update { it.copy(isStoreLoading = true) }
            when (val result = repository.registerStore(request)) {
                is Try.Success -> _state.update {
                    it.copy(
                        hasActiveStore = true,
                        activeStoreId = result.result,
                        storeStatus = "Toko siap digunakan",
                        isStoreLoading = false,
                    )
                }
                is Try.Error -> _state.update {
                    it.copy(storeStatus = result.result.toStatusMessage(), isStoreLoading = false)
                }
            }
        }
    }

    fun loadCatalog(storeId: String) {
        launchShellUpdate {
            _state.update { it.copy(isCatalogLoading = true) }
            when (val result = repository.listProducts(storeId)) {
                is Try.Success -> _state.update {
                    it.copy(catalogStatus = if (result.result.isBlank()) "Belum ada produk" else "Produk berhasil dimuat", isCatalogLoading = false)
                }
                is Try.Error -> _state.update {
                    it.copy(catalogStatus = result.result.toStatusMessage(), isCatalogLoading = false)
                }
            }
        }
    }

    fun checkout(storeId: String, request: TransactionCreateRequest) {
        launchShellUpdate {
            _state.update { it.copy(isCheckoutLoading = true) }
            when (val result = repository.createTransaction(storeId, request)) {
                is Try.Success -> _state.update {
                    it.copy(checkoutStatus = "Transaksi berhasil disimpan", isCheckoutLoading = false)
                }
                is Try.Error -> _state.update {
                    it.copy(checkoutStatus = result.result.toStatusMessage(), isCheckoutLoading = false)
                }
            }
        }
    }

    fun sync(storeId: String, request: SyncRequest) {
        launchShellUpdate {
            _state.update { it.copy(isSyncLoading = true) }
            when (val result = repository.sync(storeId, request)) {
                is Try.Success -> _state.update {
                    it.copy(syncStatus = "Data toko sudah sinkron", isSyncLoading = false)
                }
                is Try.Error -> _state.update {
                    it.copy(syncStatus = result.result.toStatusMessage(), isSyncLoading = false)
                }
            }
        }
    }

    fun loadAdmin() {
        launchShellUpdate {
            _state.update { it.copy(isAdminLoading = true) }
            when (val result = repository.adminStats()) {
                is Try.Success -> _state.update {
                    it.copy(adminStatus = if (result.result.isBlank()) "Belum ada data admin" else "Ringkasan admin siap", isAdminLoading = false)
                }
                is Try.Error -> _state.update {
                    it.copy(adminStatus = result.result.toStatusMessage(), isAdminLoading = false)
                }
            }
        }
    }

    fun logout() {
        launchShellUpdate {
            sessionPreferences?.clearSession()
            _state.update { PosShellState(isSessionLoaded = true, authStatus = "Anda sudah keluar") }
        }
    }

    private fun hydrateSession() {
        val preferences = sessionPreferences
        if (preferences == null) {
            _state.update { it.copy(isSessionLoaded = true) }
            return
        }
        launchShellUpdate {
            val accessToken = preferences.getAccessToken()
            val activeStoreId = preferences.getActiveStoreId()
            _state.update {
                it.copy(
                    isSessionLoaded = true,
                    isAuthenticated = !accessToken.isNullOrBlank(),
                    hasActiveStore = !activeStoreId.isNullOrBlank(),
                    activeStoreId = activeStoreId,
                    authStatus = if (accessToken.isNullOrBlank()) "Belum masuk" else "Sesi aktif",
                    storeStatus = if (activeStoreId.isNullOrBlank()) "Belum ada toko aktif" else "Toko aktif",
                )
            }
        }
    }

    private fun launchShellUpdate(block: suspend CoroutineScope.() -> Unit) {
        (coroutineScope ?: viewModelScope).launch(block = block)
    }
}

private fun Throwable.toStatusMessage(): String = message ?: "Terjadi kesalahan jaringan"
