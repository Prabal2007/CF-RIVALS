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
                    val response1 = RetrofitClient.instance.getUsers("$myHandle")
                    val response2 = RetrofitClient.instance.getUsers("$rivalHandle")
                    if (response1.isSuccessful && response2.isSuccessful && response1.body()?.status == "OK" && response2.body()?.status == "OK") {
                        val user1 = response1.body()?.result ?: emptyList()
                        val user2 = response2.body()?.result ?: emptyList()
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

                            if (status1.isSuccessful && status2.isSuccessful) {
                                val solvedMe = status1.body()?.result?.count { it.verdict == "OK" } ?: 0
                                val solvedRival = status2.body()?.result?.count { it.verdict == "OK" } ?: 0

                                binding.txtMeSolved.text = solvedMe.toString()
                                binding.txtRivalSolved.text = solvedRival.toString()

                                val meScore = (me.rating * 0.7) + (solvedMe * 10)
                                val rivalScore = (rival.rating * 0.7) + (solvedRival * 10)

                                val totalScore = (meScore + rivalScore).toFloat()
                                if (totalScore > 0f) {
                                    val progress = ((meScore / totalScore) * 100).toInt()
                                    binding.dominanceBar.setProgress(progress, true)
                                }
                            } else {
                                val total = (me.rating + rival.rating).toFloat()
                                if (total > 0) {
                                    val progress = ((me.rating / total) * 100).toInt()
                                    binding.dominanceBar.setProgress(progress, true)
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("HomeFragment", "Error updating dominance", e)
                }
            }
        } else {
            binding.txtRatingGap.text = "Set handles in Settings"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}