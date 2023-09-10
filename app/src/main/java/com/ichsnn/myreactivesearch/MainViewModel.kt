package com.ichsnn.myreactivesearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ichsnn.myreactivesearch.model.UiState
import com.ichsnn.myreactivesearch.network.ApiConfig
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapLatest

@FlowPreview
@ExperimentalCoroutinesApi
class MainViewModel : ViewModel() {
    private val accessToken = BuildConfig.MAPBOX_API_TOKEN
    val queryChannel = MutableStateFlow("")

    val searchResult = queryChannel
        .debounce(300)
        .distinctUntilChanged()
        .filter { it.trim().isNotEmpty() }
        .mapLatest {
            try {
                val result = ApiConfig.provideApiService().getCountry(it, accessToken).features
                UiState.Success(result)
            } catch (e: Exception) {
                UiState.Error(e.message.toString())
            }
        }
        .asLiveData()
}