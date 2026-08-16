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

        fetchBattleData()
    }

    private fun setupRecyclerView() {
        binding.rvNotSolved.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            fetchBattleData()
        }
    }

    private fun observeViewModel() {

        viewModel.problemsToCatchUp.observe(viewLifecycleOwner) { problems ->
            binding.rvNotSolved.adapter = ProblemAdapter(problems)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                // Proper error UI will be added in a future issue.
            }
        }
    }

    private fun fetchBattleData() {

        val preferences = requireActivity()
            .getSharedPreferences("CF_PREFS", Context.MODE_PRIVATE)

        val myHandle = preferences.getString("my_handle", null)
        val rivalHandle = preferences.getString("rival_handle", null)

        if (myHandle == null || rivalHandle == null) {
            return
        }

        viewModel.fetchBattleData(
            myHandle = myHandle,
            rivalHandle = rivalHandle
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}