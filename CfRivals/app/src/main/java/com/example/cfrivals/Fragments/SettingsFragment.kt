package com.example.cfrivals.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.cfrivals.Api.RetrofitClient
import com.example.cfrivals.Models.CFSessionCache
import com.example.cfrivals.databinding.FragmentSettingsBinding
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentSettingsBinding.inflate(
                inflater,
                container,
                false
            )

        loadSavedHandles()

        binding.btnSave.setOnClickListener {
            saveHandles()
        }

        binding.btnClear.setOnClickListener {
            clearHandles()
        }

        return binding.root
    }

    private fun loadSavedHandles() {

        val prefs =
            requireActivity().getSharedPreferences(
                "CF_PREFS",
                Context.MODE_PRIVATE
            )

        binding.editUserHandle.setText(
            prefs.getString("my_handle", "")
        )

        binding.editRivalHandle.setText(
            prefs.getString("rival_handle", "")
        )
    }

    private fun saveHandles() {

        val myHandle =
            binding.editUserHandle
                .text
                .toString()
                .trim()

        val rivalHandle =
            binding.editRivalHandle
                .text
                .toString()
                .trim()

        var hasError = false

        if (myHandle.isEmpty()) {

            binding.editUserHandle.error =
                "Handle cannot be empty"

            hasError = true
        }

        if (rivalHandle.isEmpty()) {

            binding.editRivalHandle.error =
                "Handle cannot be empty"

            hasError = true
        }

        if (hasError) return

        if (
            myHandle.equals(
                rivalHandle,
                ignoreCase = true
            )
        ) {

            binding.editUserHandle.error =
                "Rival handle must be different from your handle"

            return
        }

        binding.btnSave.isEnabled = false

        Toast.makeText(
            requireContext(),
            "Validating Codeforces handles...",
            Toast.LENGTH_SHORT
        ).show()

        viewLifecycleOwner.lifecycleScope.launch {

            try {

                val myResponse =
                    RetrofitClient.instance
                        .getUsers(myHandle)

                if (!myResponse.isSuccessful) {

                    binding.editUserHandle.error =
                        "Unable to validate handle"

                    return@launch
                }

                val myBody =
                    myResponse.body()

                if (myBody == null) {

                    binding.editUserHandle.error =
                        "Empty response from Codeforces"

                    return@launch
                }

                if (myBody.status != "OK") {

                    binding.editUserHandle.error =
                        "Codeforces handle not found"

                    return@launch
                }

                val rivalResponse =
                    RetrofitClient.instance
                        .getUsers(rivalHandle)

                if (!rivalResponse.isSuccessful) {

                    binding.editRivalHandle.error =
                        "Unable to validate handle"

                    return@launch
                }

                val rivalBody =
                    rivalResponse.body()

                if (rivalBody == null) {

                    binding.editRivalHandle.error =
                        "Empty response from Codeforces"

                    return@launch
                }

                if (rivalBody.status != "OK") {

                    binding.editRivalHandle.error =
                        "Codeforces handle not found"

                    return@launch
                }

                val prefs =
                    requireActivity()
                        .getSharedPreferences(
                            "CF_PREFS",
                            Context.MODE_PRIVATE
                        )

                prefs.edit {
                    putString(
                        "my_handle",
                        myHandle
                    )

                    putString(
                        "rival_handle",
                        rivalHandle
                    )
                }

                /*
                 * The handles changed successfully.
                 * Any cached data belongs to the previous
                 * handle pair, so it must be discarded.
                 */
                CFSessionCache.clear()

                Toast.makeText(
                    requireContext(),
                    "Handles Updated! Go to Home to see changes.",
                    Toast.LENGTH_SHORT
                ).show()

            } catch (exception: Exception) {

                Toast.makeText(
                    requireContext(),
                    "Unable to validate handles. Check your internet connection.",
                    Toast.LENGTH_SHORT
                ).show()

            } finally {

                _binding?.btnSave?.isEnabled = true
            }
        }
    }

    private fun clearHandles() {

        binding.editUserHandle.text?.clear()
        binding.editRivalHandle.text?.clear()

        binding.editUserHandle.error = null
        binding.editRivalHandle.error = null

        val prefs =
            requireActivity()
                .getSharedPreferences(
                    "CF_PREFS",
                    Context.MODE_PRIVATE
                )

        prefs.edit {
            clear()
        }

        /*
         * Handles are gone, so cached Codeforces data
         * must also be discarded.
         */
        CFSessionCache.clear()

        Toast.makeText(
            requireContext(),
            "All handles cleared!",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}