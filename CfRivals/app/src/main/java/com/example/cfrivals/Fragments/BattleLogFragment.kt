package com.example.cfrivals.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cfrivals.Adapter.ProblemAdapter
import com.example.cfrivals.Api.RetrofitClient
import com.example.cfrivals.databinding.FragmentBattleLogBinding
import kotlinx.coroutines.launch

class BattleLogFragment : Fragment() {
    private var _binding: FragmentBattleLogBinding? = null
    private val binding get() = _binding!!

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
        binding.rvNotSolved.layoutManager = LinearLayoutManager(requireContext())
        fetchBattleData()
    }

    private fun fetchBattleData() {
        val prefs = requireActivity().getSharedPreferences("CF_PREFS", Context.MODE_PRIVATE)
        val myHandle = prefs.getString("my_handle", null)
        val rivalHandle = prefs.getString("rival_handle", null)

        if (myHandle != null && rivalHandle != null) {
            lifecycleScope.launch {
                try {
                    // 1. Get status for both
                    val myStatus = RetrofitClient.instance.getStatus(myHandle)
                    val rivalStatus = RetrofitClient.instance.getStatus(rivalHandle)

                    if (myStatus.isSuccessful && rivalStatus.isSuccessful) {
                        val mySolved = myStatus.body()?.result
                            ?.filter { it.verdict == "OK" }
                            ?.map { "${it.problem.contestId}${it.problem.index}" }
                            ?.toSet() ?: emptySet()

                        val rivalSolvedProblems = rivalStatus.body()?.result
                            ?.filter { it.verdict == "OK" }
                            ?.map { it.problem }
                            ?.distinctBy { "${it.contestId}${it.index}" } ?: emptyList()

                        // 2. Filter problems solved by rival but not by me
                        val toCatchUp = rivalSolvedProblems.filter {
                            "${it.contestId}${it.index}" !in mySolved
                        }

                        binding.rvNotSolved.adapter = ProblemAdapter(toCatchUp)
                    }
                } catch (e: Exception) {
                    // Handle error
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}