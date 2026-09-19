package com.example.cfrivals.Models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cfrivals.Api.RetrofitClient
import kotlinx.coroutines.launch

class BattleLogViewModel : ViewModel() {

    private val _problemsToCatchUp = MutableLiveData<List<Problem>>(emptyList())
    val problemsToCatchUp: LiveData<List<Problem>> = _problemsToCatchUp

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    fun fetchBattleData(
        myHandle: String,
        rivalHandle: String
    ) {
        if (_isLoading.value == true) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Fetch my submissions
                val myResponse = RetrofitClient.instance.getStatus(
                    handle = myHandle,
                    count = 10000
                )

                if (!myResponse.isSuccessful) {
                    _error.value = "Unable to fetch your Codeforces data."
                    return@launch
                }

                val myBody = myResponse.body()

                if (myBody?.status != "OK") {
                    _error.value =
                        myBody?.comment ?: "Unable to fetch your Codeforces data."
                    return@launch
                }

                // Fetch rival submissions
                val rivalResponse = RetrofitClient.instance.getStatus(
                    handle = rivalHandle,
                    count = 10000
                )

                if (!rivalResponse.isSuccessful) {
                    _error.value = "Unable to fetch rival Codeforces data."
                    return@launch
                }

                val rivalBody = rivalResponse.body()

                if (rivalBody?.status != "OK") {
                    _error.value =
                        rivalBody?.comment ?: "Unable to fetch rival Codeforces data."
                    return@launch
                }

                // Find all problems solved by me
                val mySolvedProblems =
                    SolvedProblemCalculator.uniqueSolvedProblems(
                        myBody.result ?: emptyList()
                    )

                // Find problems solved by rival but not by me
                val problemsToCatchUp = (rivalBody.result ?: emptyList())
                    .asSequence()
                    .filter { it.verdict == "OK" }
                    .map { it.problem }
                    .filter { problem ->
                        val problemKey =
                            "${problem.contestId}:${problem.index}"

                        problemKey !in mySolvedProblems
                    }
                    .distinctBy { problem ->
                        "${problem.contestId}:${problem.index}"
                    }
                    .toList()

                _problemsToCatchUp.value = problemsToCatchUp

            } catch (exception: Exception) {

                _error.value = when {
                    exception is java.net.UnknownHostException ->
                        "No internet connection. Check your network."

                    exception is java.net.SocketTimeoutException ->
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