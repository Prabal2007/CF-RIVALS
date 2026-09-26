package com.example.cfrivals.Models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cfrivals.Api.RetrofitClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class BattleLogViewModel : ViewModel() {

    private val _problemsToCatchUp =
        MutableLiveData<List<Problem>>(emptyList())

    val problemsToCatchUp: LiveData<List<Problem>> =
        _problemsToCatchUp

    private val _isLoading =
        MutableLiveData(false)

    val isLoading: LiveData<Boolean> =
        _isLoading

    private val _error =
        MutableLiveData<String?>(null)

    val error: LiveData<String?> =
        _error

    fun fetchBattleData(
        myHandle: String,
        rivalHandle: String,
        forceRefresh: Boolean = false
    ) {
        if (_isLoading.value == true) return

        viewModelScope.launch {

            _isLoading.value = true
            _error.value = null

            try {

                /*
                 * Use cached submissions when available.
                 *
                 * This prevents Battle Log from making the same
                 * Codeforces API calls that Home already made.
                 */
                if (
                    !forceRefresh &&
                    CFSessionCache.isValidFor(
                        myHandle,
                        rivalHandle
                    )
                ) {

                    val mySubmissions =
                        CFSessionCache.mySubmissions!!

                    val rivalSubmissions =
                        CFSessionCache.rivalSubmissions!!

                    val problemsToCatchUp =
                        BattleLogCalculator.problemsToCatchUp(
                            mySubmissions = mySubmissions,
                            rivalSubmissions = rivalSubmissions
                        )

                    _problemsToCatchUp.value =
                        problemsToCatchUp

                    return@launch
                }

                /*
                 * Cache is unavailable or a fresh request
                 * was explicitly requested.
                 */

                val myResponse =
                    RetrofitClient.instance.getStatus(
                        handle = myHandle,
                        count = 10000
                    )

                if (!myResponse.isSuccessful) {
                    _error.value =
                        "Unable to fetch your Codeforces data."
                    return@launch
                }

                val myBody = myResponse.body()

                if (myBody?.status != "OK") {
                    _error.value =
                        myBody?.comment
                            ?: "Unable to fetch your Codeforces data."
                    return@launch
                }

                val rivalResponse =
                    RetrofitClient.instance.getStatus(
                        handle = rivalHandle,
                        count = 10000
                    )

                if (!rivalResponse.isSuccessful) {
                    _error.value =
                        "Unable to fetch rival Codeforces data."
                    return@launch
                }

                val rivalBody = rivalResponse.body()

                if (rivalBody?.status != "OK") {
                    _error.value =
                        rivalBody?.comment
                            ?: "Unable to fetch rival Codeforces data."
                    return@launch
                }

                val mySubmissions =
                    myBody.result ?: emptyList()

                val rivalSubmissions =
                    rivalBody.result ?: emptyList()

                /*
                 * Save fresh submission data so Home and
                 * Battle Log can reuse it later.
                 */
                CFSessionCache.mySubmissions =
                    mySubmissions

                CFSessionCache.rivalSubmissions =
                    rivalSubmissions

                CFSessionCache.cachedMyHandle =
                    myHandle

                CFSessionCache.cachedRivalHandle =
                    rivalHandle

                val problemsToCatchUp =
                    BattleLogCalculator.problemsToCatchUp(
                        mySubmissions = mySubmissions,
                        rivalSubmissions = rivalSubmissions
                    )

                _problemsToCatchUp.value =
                    problemsToCatchUp

            } catch (exception: CancellationException) {

                throw exception

            } catch (exception: Exception) {

                _error.value = when (exception) {

                    is java.net.UnknownHostException ->
                        "No internet connection. Check your network."

                    is java.net.SocketTimeoutException ->
                        "Connection timed out. Please try again."

                    else ->
                        "Network Error: Check internet connection."
                }

            } finally {

                _isLoading.value = false
            }
        }
    }
}