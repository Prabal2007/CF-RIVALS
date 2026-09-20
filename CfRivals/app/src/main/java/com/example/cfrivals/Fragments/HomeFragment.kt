package com.example.cfrivals.Fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.example.cfrivals.Api.RetrofitClient
import com.example.cfrivals.Models.ComparisonWinner
import com.example.cfrivals.Models.RivalComparisonCalculator
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

        binding.btnRetry.setOnClickListener {
            fetchData()
        }

        fetchData()
    }

    private fun fetchData() {

        val prefs = requireActivity()
            .getSharedPreferences("CF_PREFS", Context.MODE_PRIVATE)

        val myHandle = prefs.getString("my_handle", null)
        val rivalHandle = prefs.getString("rival_handle", null)

        if (myHandle != null && rivalHandle != null) {

            binding.progressBar.visibility = View.VISIBLE
            binding.btnRetry.visibility = View.GONE

            binding.txtUserHandle.text = myHandle
            binding.txtRivalHandle.text = rivalHandle

            lifecycleScope.launch {

                val b = _binding ?: return@launch

                try {

                    // -------------------------------------------------
                    // Fetch user information
                    // -------------------------------------------------

                    val response1 =
                        RetrofitClient.instance.getUsers(myHandle)

                    val response2 =
                        RetrofitClient.instance.getUsers(rivalHandle)

                    if (_binding == null) return@launch

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

                    // -------------------------------------------------
                    // Find users
                    // -------------------------------------------------

                    val user1 = body1.result ?: emptyList()
                    val user2 = body2.result ?: emptyList()

                    val me = user1.find {
                        it.handle.equals(
                            myHandle,
                            ignoreCase = true
                        )
                    }

                    val rival = user2.find {
                        it.handle.equals(
                            rivalHandle,
                            ignoreCase = true
                        )
                    }

                    // -------------------------------------------------
                    // Load profile images
                    // -------------------------------------------------

                    me?.let {

                        b.imgMe.load(it.titlePhoto) {

                            crossfade(true)

                            placeholder(R.drawable.avatar)

                            error(R.drawable.avatar)
                        }
                    }

                    rival?.let {

                        b.imgRival.load(it.titlePhoto) {

                            crossfade(true)

                            placeholder(R.drawable.avatar)

                            error(R.drawable.avatar)
                        }
                    }

                    if (me == null || rival == null) {
                        showError("Unable to find Codeforces user data")
                        return@launch
                    }

                    // -------------------------------------------------
                    // Display ratings
                    // -------------------------------------------------

                    b.txtMeRating.text = me.rating.toString()
                    b.txtRivalRating.text = rival.rating.toString()

                    // -------------------------------------------------
                    // Fetch submissions
                    // -------------------------------------------------

                    val status1 =
                        RetrofitClient.instance.getStatus(
                            myHandle,
                            count = 10000
                        )

                    val status2 =
                        RetrofitClient.instance.getStatus(
                            rivalHandle,
                            count = 10000
                        )

                    if (_binding == null) return@launch

                    if (!status1.isSuccessful || !status2.isSuccessful) {
                        showError("Unable to fetch submission data")
                        return@launch
                    }

                    val statusBody1 = status1.body()
                    val statusBody2 = status2.body()

                    if (statusBody1 == null || statusBody2 == null) {
                        showError(
                            "Empty submission response from Codeforces"
                        )
                        return@launch
                    }

                    if (
                        statusBody1.status != "OK" ||
                        statusBody2.status != "OK"
                    ) {

                        showError(
                            statusBody1.comment
                                ?: statusBody2.comment
                                ?: "Codeforces returned an error"
                        )

                        return@launch
                    }

                    // -------------------------------------------------
                    // Calculate solved problems
                    // -------------------------------------------------

                    val solvedMe =
                        SolvedProblemCalculator.countUniqueSolvedProblems(
                            statusBody1.result ?: emptyList()
                        )

                    val solvedRival =
                        SolvedProblemCalculator.countUniqueSolvedProblems(
                            statusBody2.result ?: emptyList()
                        )

                    b.txtMeSolved.text = solvedMe.toString()
                    b.txtRivalSolved.text = solvedRival.toString()

                    // -------------------------------------------------
                    // Compare user and rival
                    // -------------------------------------------------

                    val comparison =
                        RivalComparisonCalculator.compare(
                            myRating = me.rating,
                            rivalRating = rival.rating,
                            mySolvedProblems = solvedMe,
                            rivalSolvedProblems = solvedRival
                        )

                    // -------------------------------------------------
                    // Solved problem difference
                    // -------------------------------------------------

                    b.txtSolvedGap.text =
                        when (comparison.solvedWinner) {

                            ComparisonWinner.YOU ->
                                "You solved ${comparison.solvedDifference} more problems"

                            ComparisonWinner.RIVAL ->
                                "Rival solved ${-comparison.solvedDifference} more problems"

                            ComparisonWinner.EQUAL ->
                                "You both solved the same number of problems"
                        }

                    // -------------------------------------------------
                    // Rating difference
                    // -------------------------------------------------

                    b.txtRatingGap.text =
                        when (comparison.ratingWinner) {

                            ComparisonWinner.YOU ->
                                "You are ${comparison.ratingDifference} rating ahead"

                            ComparisonWinner.RIVAL ->
                                "You are ${-comparison.ratingDifference} rating behind"

                            ComparisonWinner.EQUAL ->
                                "You both have equal rating"
                        }

                    // -------------------------------------------------
                    // Rating difference color
                    // -------------------------------------------------

                    val ratingColor =
                        when (comparison.ratingWinner) {

                            ComparisonWinner.YOU ->
                                Color.GREEN

                            ComparisonWinner.RIVAL ->
                                Color.RED

                            ComparisonWinner.EQUAL ->
                                Color.BLUE
                        }

                    b.txtRatingGap.setTextColor(ratingColor)

                    // -------------------------------------------------
                    // Dominance bar
                    // -------------------------------------------------

                    val myPercentage =
                        comparison.myDominancePercentage

                    val rivalPercentage =
                        100 - myPercentage

                    val youParams =
                        b.dominanceYou.layoutParams
                                as LinearLayout.LayoutParams

                    youParams.weight =
                        myPercentage.toFloat()

                    b.dominanceYou.layoutParams = youParams

                    val rivalParams =
                        b.dominanceRival.layoutParams
                                as LinearLayout.LayoutParams

                    rivalParams.weight =
                        rivalPercentage.toFloat()

                    b.dominanceRival.layoutParams = rivalParams

                    // -------------------------------------------------
                    // Dominance labels
                    // -------------------------------------------------

                    b.txtYouDominance.text =
                        "YOU $myPercentage%"

                    b.txtRivalDominance.text =
                        "RIVAL $rivalPercentage%"

                } catch (e: Exception) {

                    Log.e(
                        "HomeFragment",
                        "Error updating comparison",
                        e
                    )

                    showError(
                        "Network Error: Check internet connection."
                    )

                } finally {

                    _binding?.progressBar?.visibility =
                        View.GONE
                }
            }

        } else {

            showEmptyState("Set Handles in Settings")
        }
    }

    // -------------------------------------------------------------
    // Reset dominance bar
    // -------------------------------------------------------------

    private fun resetDominanceBar() {

        val youParams =
            binding.dominanceYou.layoutParams
                    as LinearLayout.LayoutParams

        youParams.weight = 0f

        binding.dominanceYou.layoutParams = youParams

        val rivalParams =
            binding.dominanceRival.layoutParams
                    as LinearLayout.LayoutParams

        rivalParams.weight = 0f

        binding.dominanceRival.layoutParams = rivalParams

        binding.txtYouDominance.text = "YOU"
        binding.txtRivalDominance.text = "RIVAL"
    }

    // -------------------------------------------------------------
    // Error state
    // -------------------------------------------------------------

    private fun showError(message: String) {

        binding.txtRatingGap.text = message
        binding.txtRatingGap.setTextColor(Color.RED)

        binding.txtSolvedGap.text = "--"

        binding.txtMeRating.text = "--"
        binding.txtRivalRating.text = "--"

        binding.txtMeSolved.text = "--"
        binding.txtRivalSolved.text = "--"

        binding.imgMe.setImageResource(R.drawable.avatar)
        binding.imgRival.setImageResource(R.drawable.avatar)

        resetDominanceBar()

        binding.btnRetry.visibility = View.VISIBLE
    }

    // -------------------------------------------------------------
    // Empty state
    // -------------------------------------------------------------

    private fun showEmptyState(message: String) {

        binding.txtRatingGap.text = message
        binding.txtRatingGap.setTextColor(Color.GRAY)

        binding.txtSolvedGap.text = "--"

        binding.txtMeRating.text = "--"
        binding.txtRivalRating.text = "--"

        binding.txtMeSolved.text = "--"
        binding.txtRivalSolved.text = "--"

        binding.imgMe.setImageResource(R.drawable.avatar)
        binding.imgRival.setImageResource(R.drawable.avatar)

        resetDominanceBar()

        binding.btnRetry.visibility = View.GONE
    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}