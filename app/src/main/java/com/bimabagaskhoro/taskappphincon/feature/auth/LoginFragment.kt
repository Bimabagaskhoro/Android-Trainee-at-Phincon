package com.bimabagaskhoro.taskappphincon.feature.auth

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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bimabagaskhoro.phincon.core.data.source.remote.response.ResponseError
import com.bimabagaskhoro.phincon.core.data.source.remote.response.auth.SuccessLogin
import com.bimabagaskhoro.phincon.core.vm.DataStoreViewModel
import com.bimabagaskhoro.phincon.core.vm.FGAViewModel
import com.bimabagaskhoro.phincon.core.vm.RemoteViewModel
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.databinding.FragmentLoginBinding
import com.bimabagaskhoro.taskappphincon.feature.activity.MainActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import java.io.IOException

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding
    private val viewModel: RemoteViewModel by viewModels()
    private val dataStoreViewModel: DataStoreViewModel by viewModels()
    private val analyticViewModel: FGAViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            btnLogin.setOnClickListener {
                onButtonPressed()
            }
            btnSignup.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
                // login page slide 5
                analyticViewModel.onClickButtonLoginToRegister()
            }
        }


    }

    private fun onButtonPressed() {
        val email = binding?.edtEmail?.text.toString().trim()
        val password = binding?.edtPassword?.text.toString().trim()
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding?.tvCheckingEmail?.visibility = View.INVISIBLE
            when {
                email.isEmpty() -> {
                    binding?.edtEmail?.error = "Masukan email terlebih dahulu"
                    binding?.edtEmail?.requestFocus()
                }
                password.isEmpty() -> {
                    binding?.edtPassword?.error = "Masukan password terlebih dahulu"
                    binding?.edtPassword?.requestFocus()
                }
                else -> {
                    binding?.edtEmail?.error = null
                    binding?.edtPassword?.error = null

                    lifecycleScope.launch {
                        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            try {
                                val firebaseToken = FirebaseMessaging.getInstance().token
                                    .addOnCompleteListener { task ->
                                        task.toString()
                                    }
                                    .addOnFailureListener {
                                        when (it) {
                                            is FirebaseNetworkException -> {
                                                Toast.makeText(
                                                    requireActivity(),
                                                    "Check your internet connection.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            is FirebaseTooManyRequestsException -> {
                                                Toast.makeText(
                                                    requireActivity(),
                                                    "Too many request.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            is FirebaseException -> {
                                                Toast.makeText(
                                                    requireActivity(),
                                                    "An unknown error occurred.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }.await()
                                initData(email, password, firebaseToken)
                            } catch (io: IOException) {
                                Log.d("IOException", "No Internet")
                                Toast.makeText(
                                    requireActivity(),
                                    "No Internet Connection",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: Exception) {
                                Log.d("Exception", "Test")
                            }
                        }
                    }
                }
            }
        } else {
            binding?.tvCheckingEmail?.visibility = View.VISIBLE
            binding?.edtEmail?.requestFocus()
        }
    }

    private fun initData(email: String, password: String, tokenFcm: String) {
        viewModel.login(email, password, tokenFcm).observe(viewLifecycleOwner) {
            when (it) {
                is com.bimabagaskhoro.phincon.core.utils.Resource.Loading -> {
                    binding?.progressbar?.visibility = View.VISIBLE
                    binding?.cardProgressbar?.visibility = View.VISIBLE
                    binding?.tvWaiting?.visibility = View.VISIBLE
                }
                is com.bimabagaskhoro.phincon.core.utils.Resource.Success -> {
                    binding?.progressbar?.visibility = View.GONE
                    binding?.cardProgressbar?.visibility = View.GONE
                    binding?.tvWaiting?.visibility = View.GONE
                    it.data?.success?.let { it1 -> saveUserData(it1) }
                    val dataLog = it.data?.success
                    Log.d("datas", "$dataLog")
                    Intent(requireContext(), MainActivity::class.java).also { intent ->
                        startActivity(intent)
                        requireActivity().finish()
                        // login page slide 5
                        analyticViewModel.onClickButtonLogin(email)
                    }
                }
                is com.bimabagaskhoro.phincon.core.utils.Resource.Error -> {
                    try {
                        binding?.progressbar?.visibility = View.GONE
                        binding?.cardProgressbar?.visibility = View.GONE
                        binding?.tvWaiting?.visibility = View.GONE
                        val err = it.errorBody?.string()?.let { it1 -> JSONObject(it1).toString() }
                        val gson = Gson()
                        val jsonObject = gson.fromJson(err, JsonObject::class.java)
                        val errorResponse = gson.fromJson(jsonObject, ResponseError::class.java)
                        val messageErr = errorResponse?.error?.message
                        Toast.makeText(requireActivity(), messageErr, Toast.LENGTH_SHORT).show()
//
                    } catch (t: Throwable) {
                        Toast.makeText(requireActivity(),  t.localizedMessage, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                is com.bimabagaskhoro.phincon.core.utils.Resource.Empty -> {
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
            accessToken?.let { saveToken(it) }
            refreshToken?.let { saveRefreshToken(it) }
            userId?.let { saveUserId(it) }
            userEmail?.let { saveUserEmail(it) }
            username?.let { saveUserName(it) }
            gender?.let { saveUserGender(it) }
            phone?.let { saveUserPhone(it) }
            path?.let { saveUserPath(it) }
            image?.let { saveUserPhoto(it) }
        }
    }

    override fun onResume() {
        super.onResume()
        val nameScreen = this.javaClass.simpleName
        // login page slide 5
        analyticViewModel.onLoadScreenLogin(nameScreen)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}