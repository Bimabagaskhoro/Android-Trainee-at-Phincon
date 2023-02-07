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
import androidx.navigation.fragment.findNavController
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.utils.Resource
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.ResponseError
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.auth.SuccessLogin
import com.bimabagaskhoro.taskappphincon.databinding.FragmentLoginBinding
import com.bimabagaskhoro.taskappphincon.ui.activity.MainActivity
import com.bimabagaskhoro.taskappphincon.vm.AuthViewModel
import com.bimabagaskhoro.taskappphincon.vm.DataStoreViewModel
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()
    private val dataStoreViewModel: DataStoreViewModel by viewModels()

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

        binding.apply {
            btnLogin.setOnClickListener {
                onButtonPressed()
                getTokenFcm()
            }
            btnSignup.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
        }


    }

    private fun getTokenFcm() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            val token = task.result
            Log.d("tokenFcm", "$token")
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
            when (it) {
                is Resource.Loading -> {
                    binding.progressbar.visibility = View.VISIBLE
                    binding.cardProgressbar.visibility = View.VISIBLE
                    binding.tvWaiting.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressbar.visibility = View.GONE
                    binding.cardProgressbar.visibility = View.GONE
                    binding.tvWaiting.visibility = View.GONE
                    saveUserData(it.data!!.success)
                    val dataLog = it.data!!.success
                    Log.d("datas", "$dataLog")
                    Intent(requireContext(), MainActivity::class.java).also { intent ->
                        startActivity(intent)
                        requireActivity().finish()
                    }
                }
                is Resource.Error -> {
                    binding.progressbar.visibility = View.GONE
                    binding.cardProgressbar.visibility = View.GONE
                    binding.tvWaiting.visibility = View.GONE
                    val err = it.errorBody?.string()?.let { it1 -> JSONObject(it1).toString() }
                    val gson = Gson()
                    val jsonObject = gson.fromJson(err, JsonObject::class.java)
                    val errorResponse = gson.fromJson(jsonObject, ResponseError::class.java)
                    val messageErr = errorResponse.error.message
                    Toast.makeText(requireActivity(), messageErr, Toast.LENGTH_SHORT).show()
                }
                is Resource.Empty -> {
                    Log.d("Empty Data", "Empty")
                }
            }
        }
    }

    private fun saveUserData(data: SuccessLogin) {
        val isLoggedIn = true
        val accessToken = data.access_token
        val refreshToken = data.refresh_token
        val userId = data.data_user.id
        val userEmail = data.data_user.email
        val username = data.data_user.name
        val gender = data.data_user.gender
        val phone = data.data_user.phone
        val path = data.data_user.path
        val image = data.data_user.image

        dataStoreViewModel.apply {
            isLogin(isLoggedIn)
            saveToken(accessToken)
            saveRefreshToken(refreshToken)
            saveUserId(userId)
            saveUserEmail(userEmail)
            saveUserName(username)
            saveUserGender(gender)
            saveUserPhone(phone)
            saveUserPath(path)
            saveUserPhoto(image)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}