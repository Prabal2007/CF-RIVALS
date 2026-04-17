package com.example.cfrivals.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.cfrivals.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        loadSavedHandles()
        binding.btnSave.setOnClickListener {
            saveHandles()
        }
        binding.btnClear.setOnClickListener {
            binding.editUserHandle.text?.clear()
            binding.editRivalHandle.text?.clear()
            val prefs = requireActivity().getSharedPreferences("CF_PREFS", Context.MODE_PRIVATE)
            prefs.edit().clear().apply()

            Toast.makeText(context, "All handles cleared!", Toast.LENGTH_SHORT).show()
        }
        return binding.root
    }

    private fun loadSavedHandles() {
        val prefs = requireActivity().getSharedPreferences("CF_PREFS", Context.MODE_PRIVATE)
        binding.editUserHandle.setText(prefs.getString("my_handle", ""))
        binding.editRivalHandle.setText(prefs.getString("rival_handle", ""))
    }

    private fun saveHandles() {
        val myHandle = binding.editUserHandle.text.toString().trim()
        val rivalHandle = binding.editRivalHandle.text.toString().trim()
        var isEmpty = false
        if (myHandle.isEmpty()) {
            binding.editUserHandle.error = "Handle cannot be empty"
            isEmpty = true
        } else {
            binding.editUserHandle.error = null
        }

        if(rivalHandle.isEmpty()) {
            binding.editRivalHandle.error = "Handle cannot be empty"
            isEmpty = true
        } else {
            binding.editRivalHandle.error = null
        }

        if(isEmpty) return

        val prefs = requireActivity().getSharedPreferences("CF_PREFS", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("my_handle", myHandle)
            putString("rival_handle", rivalHandle)
            apply()
        }
        Toast.makeText(context, "Handles Updated! Go to Home to see changes.", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}