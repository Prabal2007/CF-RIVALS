package com.example.cfrivals.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cfrivals.Adapter.ProblemAdapter
import com.example.cfrivals.Models.BattleLogViewModel
import com.example.cfrivals.databinding.FragmentBattleLogBinding

class BattleLogFragment : Fragment() {

    private var _binding: FragmentBattleLogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BattleLogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBattleLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupRefresh()
        observeViewModel()

        /*
         * Retry is an explicit request for fresh data.
         */
        binding.btnRetry.setOnClickListener {
            fetchBattleData(forceRefresh = true)
        }

        /*
         * Normal screen opening:
         * use cached data when available.
         */
        fetchBattleData()
    }

    private fun setupRecyclerView() {
        binding.rvNotSolved.layoutManager =
            LinearLayoutManager(requireContext())
    }

    private fun setupRefresh() {

        binding.swipeRefresh.setOnRefreshListener {

            /*
             * Pull-to-refresh explicitly bypasses
             * the session cache.
             */
            fetchBattleData(forceRefresh = true)
        }
    }

    private fun observeViewModel() {

        viewModel.problemsToCatchUp.observe(viewLifecycleOwner) { problems ->

            val b = _binding ?: return@observe

            b.rvNotSolved.adapter =
                ProblemAdapter(problems)

            if (
                problems.isEmpty() &&
                viewModel.isLoading.value != true
            ) {
                b.rvNotSolved.visibility = View.GONE
                b.txtEmptyState.visibility = View.VISIBLE
            } else {
                b.rvNotSolved.visibility = View.VISIBLE
                b.txtEmptyState.visibility = View.GONE
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->

            val b = _binding ?: return@observe

            b.swipeRefresh.isRefreshing = isLoading

            b.progressBar.visibility =
                if (isLoading) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

            if (isLoading) {
                b.txtErrorState.visibility = View.GONE
                b.btnRetry.visibility = View.GONE
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->

            val b = _binding ?: return@observe

            if (error != null) {

                b.rvNotSolved.visibility = View.GONE
                b.txtEmptyState.visibility = View.GONE

                b.txtErrorState.text = error
                b.txtErrorState.visibility = View.VISIBLE
                b.btnRetry.visibility = View.VISIBLE

            } else {

                b.txtErrorState.visibility = View.GONE
                b.btnRetry.visibility = View.GONE
            }
        }
    }

    private fun fetchBattleData(
        forceRefresh: Boolean = false
    ) {

        val preferences = requireActivity()
            .getSharedPreferences(
                "CF_PREFS",
                Context.MODE_PRIVATE
            )

        val myHandle =
            preferences.getString(
                "my_handle",
                null
            )

        val rivalHandle =
            preferences.getString(
                "rival_handle",
                null
            )

        if (myHandle == null || rivalHandle == null) {

            binding.txtEmptyState.text =
                "Set your handles in Settings to view Battle Log"

            binding.rvNotSolved.visibility =
                View.GONE

            binding.txtEmptyState.visibility =
                View.VISIBLE

            binding.btnRetry.visibility =
                View.GONE

            return
        }

        viewModel.fetchBattleData(
            myHandle = myHandle,
            rivalHandle = rivalHandle,
            forceRefresh = forceRefresh
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}