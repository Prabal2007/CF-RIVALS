package com.example.cfrivals.Models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cfrivals.Api.RetrofitClient
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
                val responses = listOf(
                    async {
                        RetrofitClient.instance.getStatus(
                            handle = myHandle,
                            count = 10000
                        )
                    },
                    async {
                        RetrofitClient.instance.getStatus(
                            handle = rivalHandle,
                            count = 10000
                        )
                    }
                ).awaitAll()

                val myResponse = responses[0]
                val rivalResponse = responses[1]

                if (!myResponse.isSuccessful || !rivalResponse.isSuccessful) {
                    _error.value = "Unable to fetch battle data."
                    return@launch
                }

                val myBody = myResponse.body()
                val rivalBody = rivalResponse.body()

                if (myBody?.status != "OK" || rivalBody?.status != "OK") {
                    _error.value = "Codeforces returned an invalid response."
                    return@launch
                }

                val mySolvedProblems =
                    SolvedProblemCalculator.uniqueSolvedProblems(myBody.result)


                val problemsToCatchUp = rivalBody.result
                    .asSequence()
                    .filter { it.verdict == "OK" }
                    .filter {
                        val problemKey =
                            "${it.problem.contestId}:${it.problem.index}"

                        problemKey !in mySolvedProblems
                    }
                    .map { it.problem }
                    .distinctBy {
                        "${it.contestId}:${it.index}"
                    }
                    .toList()

                _problemsToCatchUp.value = problemsToCatchUp

            } catch (exception: Exception) {
                _error.value =
                    exception.message ?: "Something went wrong."
            } finally {
                _isLoading.value = false
            }
        }
    }
}