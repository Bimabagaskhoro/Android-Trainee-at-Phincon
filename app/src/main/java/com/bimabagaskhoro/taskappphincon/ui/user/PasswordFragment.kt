package com.bimabagaskhoro.taskappphincon.ui.user

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.data.pref.AuthPreference
import com.bimabagaskhoro.taskappphincon.data.source.Resource
import com.bimabagaskhoro.taskappphincon.data.source.response.ResponseLoginError
import com.bimabagaskhoro.taskappphincon.databinding.FragmentPasswordBinding
import com.bimabagaskhoro.taskappphincon.vm.AuthViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject

class PasswordFragment : Fragment() {
    private var _binding: FragmentPasswordBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var authPreference: AuthPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authPreference = AuthPreference(requireContext())

        binding.apply {
            btnChangePassword.setOnClickListener { onButtonPressed() }
//            btnSignup.setOnClickListener {
//                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
//            }
        }
    }

    private fun onButtonPressed() {
        val oldPassword = binding.edtOldPassword.text.toString().trim()
        val newPassword = binding.edtNewPassword.text.toString().trim()
        val confirmPassword = binding.edtConfirmNewPassword.text.toString().trim()

        if (oldPassword == newPassword) {
            Toast.makeText(context, "Password baru harus unik", Toast.LENGTH_LONG).show()
        } else if (newPassword != confirmPassword) {
            binding.tvCheckingPassword.visibility = View.VISIBLE
            binding.tvCheckingPassword2.visibility = View.VISIBLE
        } else {
            binding.tvCheckingPassword.visibility = View.INVISIBLE
            binding.tvCheckingPassword2.visibility = View.INVISIBLE
            when {
                oldPassword.isEmpty() -> {
                    binding.edtOldPassword.error = "Masukan password terlebih dahulu"
                    binding.edtOldPassword.requestFocus()
                }
                newPassword.isEmpty() -> {
                    binding.edtNewPassword.error = "Masukan password terlebih dahulu"
                    binding.edtNewPassword.requestFocus()
                }
                confirmPassword.isEmpty() -> {
                    binding.edtConfirmNewPassword.error = "Masukan password terlebih dahulu"
                    binding.edtConfirmNewPassword.requestFocus()
                }
                else -> {
                    binding.edtOldPassword.error = null
                    binding.edtNewPassword.error = null
                    binding.edtConfirmNewPassword.error = null
                    initData(oldPassword, newPassword, confirmPassword)
                }
            }
        }
    }

    private fun initData(oldPassword: String, newPassword: String, confirmPassword: String) {
        val userId = authPreference.userId
        userId.observe(viewLifecycleOwner) {
            viewModel.changePassword(it, oldPassword, newPassword,confirmPassword).observe(viewLifecycleOwner) { result->
                when(result) {
                    is Resource.Loading -> {
                        binding.progressbar.visibility = View.VISIBLE
                        binding.cardProgressbar.visibility =View.VISIBLE
                        binding.tvWaiting.visibility =View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbar.visibility = View.GONE
                        binding.cardProgressbar.visibility =View.GONE
                        binding.tvWaiting.visibility =View.GONE
                        val dataMessages = result.data!!.success.message
                        AlertDialog.Builder(requireActivity())
                            .setTitle("Change Password Success")
                            .setMessage(dataMessages)
                            .setPositiveButton("Ok") { _, _ ->
                            }
                            .show()
                        findNavController().navigate(R.id.action_passwordFragment_to_navigation_user)
                    }
                    is Resource.Error -> {
                        binding.progressbar.visibility = View.GONE
                        binding.cardProgressbar.visibility =View.GONE
                        binding.tvWaiting.visibility =View.GONE
                        val err = result.errorBody?.string()?.let { it1 -> JSONObject(it1).toString() }
                        val gson = Gson()
                        val jsonObject = gson.fromJson(err, JsonObject::class.java)
                        val errorResponse = gson.fromJson(jsonObject, ResponseLoginError::class.java)
                        val messageErr = errorResponse.error.message
                        AlertDialog.Builder(requireActivity())
                            .setTitle("Change Password Failed")
                            .setMessage(messageErr)
                            .setPositiveButton("Ok") { _, _ ->
                            }
                            .show()
                    }
                }
            }
        }
    }
}