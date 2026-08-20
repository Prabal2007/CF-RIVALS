package com.example.cfrivals.Fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.example.cfrivals.Api.RetrofitClient
import com.example.cfrivals.Models.SolvedProblemCalculator
import com.example.cfrivals.R
import com.example.cfrivals.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchData()
    }

    private fun fetchData() {
        val prefs = requireActivity().getSharedPreferences("CF_PREFS", Context.MODE_PRIVATE)
        val myHandle = prefs.getString("my_handle", null)
        val rivalHandle = prefs.getString("rival_handle", null)

        if (myHandle != null && rivalHandle != null) {
            binding.txtUserHandle.text = myHandle
            binding.txtRivalHandle.text = rivalHandle

            lifecycleScope.launch {
                try {
                    val response1 = RetrofitClient.instance.getUsers(myHandle)
                    val response2 = RetrofitClient.instance.getUsers(rivalHandle)

                    if (!response1.isSuccessful || !response2.isSuccessful) {
                        showError("Unable to connect to Codeforces")
                        return@launch
                    }

                    val body1 = response1.body()
                    val body2 = response2.body()

                    if (body1 == null || body2 == null) {
                        showError("Empty response from Codeforces")
                        return@launch
                    }

                    if (body1.status != "OK" || body2.status != "OK") {
                        showError(
                            body1.comment
                                ?: body2.comment
                                ?: "Codeforces returned an error"
                        )
                        return@launch
                    }

                    val user1 = body1.result ?: emptyList()
                    val user2 = body2.result ?: emptyList()
                    val me = user1.find { it.handle.lowercase() == myHandle.lowercase() }
                    val rival = user2.find { it.handle.lowercase() == rivalHandle.lowercase() }

                    me?.let {
                        binding.imgMe.load(it.titlePhoto) {
                            crossfade(true)
                            placeholder(R.drawable.avatar)
                            error(R.drawable.avatar)
                        }
                    }

                    rival?.let {
                        binding.imgRival.load(it.titlePhoto) {
                            crossfade(true)
                            placeholder(R.drawable.avatar)
                            error(R.drawable.avatar)
                        }
                    }

                    if (me != null && rival != null) {
                        val gap = me.rating - rival.rating
                        binding.txtRatingGap.text = when {
                            gap > 0 -> "You are $gap rating ahead"
                            gap < 0 -> "You are ${-gap} rating behind"
                            else -> "You both have equal rating"
                        }
                        val color = when {
                            gap > 0 -> Color.GREEN
                            gap < 0 -> Color.RED
                            else -> Color.BLUE
                        }
                        binding.txtRatingGap.setTextColor(color)

                        binding.txtMeRating.text = me.rating.toString()
                        binding.txtRivalRating.text = rival.rating.toString()

                        val status1 = RetrofitClient.instance.getStatus(myHandle, count = 10000)
                        val status2 = RetrofitClient.instance.getStatus(rivalHandle, count = 10000)

                        if (!status1.isSuccessful || !status2.isSuccessful) {
                            showError("Unable to fetch submission data")
                            return@launch
                        }

                        val statusBody1 = status1.body()
                        val statusBody2 = status2.body()

                        if (statusBody1 == null || statusBody2 == null) {
                            showError("Empty submission response from Codeforces")
                            return@launch
                        }

                        if (statusBody1.status != "OK" || statusBody2.status != "OK") {
                            showError(
                                statusBody1.comment
                                    ?: statusBody2.comment
                                    ?: "Codeforces returned an error"
                            )
                            return@launch
                        }

                        val solvedMe = SolvedProblemCalculator.countUniqueSolvedProblems(
                            statusBody1.result ?: emptyList()
                        )

                        val solvedRival = SolvedProblemCalculator.countUniqueSolvedProblems(
                            statusBody2.result ?: emptyList()
                        )

                        binding.txtMeSolved.text = solvedMe.toString()
                        binding.txtRivalSolved.text = solvedRival.toString()

                        val meScore = (me.rating * 0.7) + (solvedMe * 10)
                        val rivalScore = (rival.rating * 0.7) + (solvedRival * 10)

                        val totalScore = meScore + rivalScore

                        if (totalScore > 0f) {
                            val progress = ((meScore / totalScore) * 100).toInt()
                            binding.dominanceBar.setProgress(progress, true)
                        }
                    }

                } catch (e: Exception) {
                    Log.e("HomeFragment", "Error updating dominance", e)
                }
            }
        } else {
            showError("Set handles in Settings")
        }
    }

    private fun showError(message: String) {
        binding.txtRatingGap.text = message
        binding.txtRatingGap.setTextColor(Color.RED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}