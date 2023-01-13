package com.bimabagaskhoro.taskappphincon.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.bimabagaskhoro.taskappphincon.data.source.response.ResponseLoginError
import com.bimabagaskhoro.taskappphincon.data.source.response.SuccessResponse
import com.bimabagaskhoro.taskappphincon.databinding.FragmentLoginBinding
import com.bimabagaskhoro.taskappphincon.ui.activity.MainActivity
import com.bimabagaskhoro.taskappphincon.vm.AuthViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONObject

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
                    binding.cardProgressbar.visibility =View.VISIBLE
                    binding.tvWaiting.visibility =View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressbar.visibility = View.GONE
                    binding.cardProgressbar.visibility =View.GONE
                    binding.tvWaiting.visibility =View.GONE
                    saveUserData(it.data!!.success)
                    val dataPref = it.data.success

                    Log.e("pref" , "$dataPref")
                    Intent(requireContext(), MainActivity::class.java).also { intent ->
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    //Toast.makeText(requireActivity(), it.data.success.dataUser.id, Toast.LENGTH_SHORT).show()
                    //Log.d("get id login", "$idPref")
                }
                is Resource.Error -> {
                    binding.progressbar.visibility = View.GONE
                    binding.cardProgressbar.visibility =View.GONE
                    binding.tvWaiting.visibility =View.GONE
                    val err = it.errorBody?.string()?.let { it1 -> JSONObject(it1).toString() }
                    val gson = Gson()
                    val jsonObject = gson.fromJson(err, JsonObject::class.java)
                    val errorResponse = gson.fromJson(jsonObject, ResponseLoginError::class.java)
                    val messageErr = errorResponse.error.message
                    Toast.makeText(requireActivity(), messageErr, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveUserData(success: SuccessResponse) {
        val accessToken = success.accessToken
        val refreshToken = success.refreshToken
        val id = success.dataUser.id
        val email = success.dataUser.email
        val name = success.dataUser.name
        val path = success.dataUser.path
        val gender = success.dataUser.gender
        val phone = success.dataUser.phone

        viewLifecycleOwner.lifecycleScope.launch {
            authPreference.apply {
                saveUserToken(accessToken)
                saveUserId(id)
                saveRefreshToken(refreshToken)
//                saveUserEmail(email)
//                saveUserPhone(phone)
//                saveUserGender(gender)
//                saveUserPath(path)
                saveIsLoggedIn(true)
            }
        }
    }
}