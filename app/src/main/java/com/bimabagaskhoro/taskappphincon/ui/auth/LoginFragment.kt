package com.bimabagaskhoro.taskappphincon.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.data.pref.AuthPreference
import com.bimabagaskhoro.taskappphincon.data.source.Resource
import com.bimabagaskhoro.taskappphincon.data.source.response.SuccessResponse
import com.bimabagaskhoro.taskappphincon.databinding.FragmentLoginBinding
import com.bimabagaskhoro.taskappphincon.ui.activity.MainActivity
import com.bimabagaskhoro.taskappphincon.vm.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var authPreference: AuthPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authPreference = AuthPreference(requireContext())

        binding.apply {
            btnLogin.setOnClickListener { onButtonPressed() }
            btnSignup.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
        }
    }

    private fun onButtonPressed() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tvCheckingEmail.visibility = View.INVISIBLE
        } else {
            binding.tvCheckingEmail.visibility = View.VISIBLE
            binding.edtEmail.requestFocus()
        }
        when {
            email.isEmpty() -> {
                binding.edtEmail.error = "Masukan email terlebih dahulu"
                binding.edtEmail.requestFocus()
            }
            password.isEmpty() -> {
                binding.edtPassword.error = "Masukan password terlebih dahulu"
                binding.edtPassword.requestFocus()
            }
            else -> {

                binding.edtEmail.error = null
                binding.edtPassword.error = null
                initData(email, password)
            }
        }
    }

    private fun initData(email: String, password: String) {
        viewModel.login(email, password).observe(viewLifecycleOwner) {
            when(it) {
                is Resource.Loading -> {
                    binding.progressbar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressbar.visibility = View.GONE
                    saveUserData(it.data!!.success, email)
                    val intent = Intent(context , MainActivity::class.java)
                    startActivity(intent)

                }
                is Resource.Error -> {
                    binding.progressbar.visibility = View.GONE
                    val errors = it.message
                    Toast.makeText(context, "Email or password not match ! $errors" , Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun saveUserData(success: SuccessResponse, email: String) {
        val accessToken = success.accessToken
        val refreshToken = success.refreshToken

        viewLifecycleOwner.lifecycleScope.launch {
            authPreference.apply {
                saveUserName(refreshToken)
                saveUserEmail(email)
                saveUserToken(accessToken)
                saveIsLoggedIn(true)
            }
        }
    }
}