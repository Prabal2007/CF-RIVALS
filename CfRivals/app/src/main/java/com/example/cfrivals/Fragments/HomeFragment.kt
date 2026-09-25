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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.UnknownHostException

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

        if (myHandle == null || rivalHandle == null) {
            showEmptyState("Set Handles in Settings")
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.btnRetry.visibility = View.GONE

        binding.txtUserHandle.text = myHandle
        binding.txtRivalHandle.text = rivalHandle

        /*
         * Use the Fragment VIEW lifecycle.
         *
         * When the Home view is destroyed, this coroutine is
         * automatically cancelled.
         */
        viewLifecycleOwner.lifecycleScope.launch {

            try {

                // -------------------------------------------------
                // Fetch user information
                // -------------------------------------------------

                val response1 =
                    RetrofitClient.instance.getUsers(myHandle)

                val response2 =
                    RetrofitClient.instance.getUsers(rivalHandle)

                val b = _binding ?: return@launch

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

                if (me == null || rival == null) {
                    showError("Unable to find Codeforces user data")
                    return@launch
                }

                // -------------------------------------------------
                // Load profile images
                // -------------------------------------------------
                Log.d("HomeFragment", "My image URL: ${me.titlePhoto}")
                Log.d("HomeFragment", "Rival image URL: ${rival.titlePhoto}")

                b.imgMe.load(me.titlePhoto) {
                    crossfade(true)
                    placeholder(R.drawable.avatar)
                    error(R.drawable.avatar)

                    listener(
                        onSuccess = { _, _ ->
                            Log.d("HomeFragment", "My image loaded successfully")
                        },
                        onError = { _, result ->
                            Log.e(
                                "HomeFragment",
                                "My image failed: ${result.throwable}"
                            )
                        }
                    )
                }

                b.imgRival.load(rival.titlePhoto) {
                    crossfade(true)
                    placeholder(R.drawable.avatar)
                    error(R.drawable.avatar)

                    listener(
                        onSuccess = { _, _ ->
                            Log.d("HomeFragment", "Rival image loaded successfully")
                        },
                        onError = { _, result ->
                            Log.e(
                                "HomeFragment",
                                "Rival image failed: ${result.throwable}"
                            )
                        }
                    )
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

                val currentBinding = _binding ?: return@launch

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

                currentBinding.txtMeSolved.text =
                    solvedMe.toString()

                currentBinding.txtRivalSolved.text =
                    solvedRival.toString()

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

                currentBinding.txtSolvedGap.text =
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

                currentBinding.txtRatingGap.text =
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

                currentBinding.txtRatingGap.setTextColor(ratingColor)

                // -------------------------------------------------
                // Dominance bar
                // -------------------------------------------------

                val myPercentage =
                    comparison.myDominancePercentage

                val rivalPercentage =
                    100 - myPercentage

                val youParams =
                    currentBinding.dominanceYou.layoutParams
                            as LinearLayout.LayoutParams

                youParams.weight =
                    myPercentage.toFloat()

                currentBinding.dominanceYou.layoutParams =
                    youParams

                val rivalParams =
                    currentBinding.dominanceRival.layoutParams
                            as LinearLayout.LayoutParams

                rivalParams.weight =
                    rivalPercentage.toFloat()

                currentBinding.dominanceRival.layoutParams =
                    rivalParams

                // -------------------------------------------------
                // Dominance labels
                // -------------------------------------------------

                currentBinding.txtYouDominance.text =
                    "YOU $myPercentage%"

                currentBinding.txtRivalDominance.text =
                    "RIVAL $rivalPercentage%"

            } catch (e: CancellationException) {

                /*
                 * IMPORTANT:
                 *
                 * Cancellation is normal when the Fragment view
                 * is destroyed. It must NOT be treated as an error.
                 */
                throw e

            } catch (e: Exception) {

                Log.e(
                    "HomeFragment",
                    "Error updating comparison",
                    e
                )

                val message = when (e) {

                    is UnknownHostException ->
                        "No internet connection. Check your network."

                    is SocketTimeoutException ->
                        "Connection timed out. Please try again."

                    else ->
                        "Network Error: Check internet connection."
                }

                showError(message)

            } finally {

                /*
                 * The view may already be destroyed here, so
                 * never access binding directly.
                 */
                _binding?.progressBar?.visibility =
                    View.GONE
            }
        }
    }

    // -------------------------------------------------------------
    // Reset dominance bar
    // -------------------------------------------------------------

    private fun resetDominanceBar() {

        val b = _binding ?: return

        val youParams =
            b.dominanceYou.layoutParams
                    as LinearLayout.LayoutParams

        youParams.weight = 0f

        b.dominanceYou.layoutParams =
            youParams

        val rivalParams =
            b.dominanceRival.layoutParams
                    as LinearLayout.LayoutParams

        rivalParams.weight = 0f

        b.dominanceRival.layoutParams =
            rivalParams

        b.txtYouDominance.text = "YOU"
        b.txtRivalDominance.text = "RIVAL"
    }

    // -------------------------------------------------------------
    // Error state
    // -------------------------------------------------------------

    private fun showError(message: String) {

        val b = _binding ?: return

        b.txtRatingGap.text = message
        b.txtRatingGap.setTextColor(Color.RED)

        b.txtSolvedGap.text = "--"

        b.txtMeRating.text = "--"
        b.txtRivalRating.text = "--"

        b.txtMeSolved.text = "--"
        b.txtRivalSolved.text = "--"

        b.imgMe.setImageResource(R.drawable.avatar)
        b.imgRival.setImageResource(R.drawable.avatar)

        resetDominanceBar()

        b.btnRetry.visibility = View.VISIBLE
    }

    // -------------------------------------------------------------
    // Empty state
    // -------------------------------------------------------------

    private fun showEmptyState(message: String) {

        val b = _binding ?: return

        b.txtRatingGap.text = message
        b.txtRatingGap.setTextColor(Color.GRAY)

        b.txtSolvedGap.text = "--"

        b.txtMeRating.text = "--"
        b.txtRivalRating.text = "--"

        b.txtMeSolved.text = "--"
        b.txtRivalSolved.text = "--"

        b.imgMe.setImageResource(R.drawable.avatar)
        b.imgRival.setImageResource(R.drawable.avatar)

        resetDominanceBar()

        b.btnRetry.visibility = View.GONE
    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}