package com.bimabagaskhoro.phincon.features.changepassword

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.bimabagaskhoro.phincon.core.data.source.remote.response.ResponseError
import com.bimabagaskhoro.phincon.core.vm.DataStoreViewModel
import com.bimabagaskhoro.phincon.core.vm.FGAViewModel
import com.bimabagaskhoro.phincon.core.vm.RemoteViewModel
import com.bimabagaskhoro.phincon.features.changepassword.databinding.ActivityPasswordBinding
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class PasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPasswordBinding
    private val viewModel: RemoteViewModel by viewModels()
    private val dataStoreViewModel: DataStoreViewModel by viewModels()
    private val analyticViewModel: FGAViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val reqActivity = this@PasswordActivity
        reqActivity.title = getString(R.string.change_language)
//        reqActivity.binding.btnBack
        binding.btnBack.setOnClickListener {
            finish()
            analyticViewModel.onClickBackPassword()
        }
        reqActivity.setSupportActionBar(binding.toolbar)

        binding.apply {
            btnChangePassword.setOnClickListener { onButtonPressed() }
        }
    }

    private fun onButtonPressed() {
        val oldPassword = binding.edtOldPassword.text.toString().trim()
        val newPassword = binding.edtNewPassword.text.toString().trim()
        val confirmPassword = binding.edtConfirmNewPassword.text.toString().trim()
        if (oldPassword == newPassword) {
            Toast.makeText(this, "Password baru harus unik", Toast.LENGTH_LONG).show()
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
        var userToken = ""
        var userId = 0

        dataStoreViewModel.apply {
            getToken.observe(this@PasswordActivity) {
                userToken = it
            }
            getUserId.observe(this@PasswordActivity) {
                userId = it
            }
        }

        viewModel.changePassword(
            //userToken,
            userId,
            oldPassword,
            newPassword,
            confirmPassword
        ).observe(this) { result ->
            when (result) {
                is com.bimabagaskhoro.phincon.core.utils.Resource.Loading -> {
                    binding.cardProgressbar.visibility = View.VISIBLE
                }
                is com.bimabagaskhoro.phincon.core.utils.Resource.Success -> {
                    binding.cardProgressbar.visibility = View.GONE
                    val dataMessages = result.data!!.success.message
                    AlertDialog.Builder(this)
                        .setTitle("Change Password Success")
                        .setMessage(dataMessages)
                        .setPositiveButton("Ok") { _, _ ->
                            onBackPressed()
                            analyticViewModel.onClickSavePassword()
                        }
                        .show()
                }
                is com.bimabagaskhoro.phincon.core.utils.Resource.Error -> {
                    try {
                        binding.cardProgressbar.visibility = View.GONE
                        val err =
                            result.errorBody?.string()
                                ?.let { it1 -> JSONObject(it1).toString() }
                        val gson = Gson()
                        val jsonObject = gson.fromJson(err, JsonObject::class.java)
                        val errorResponse =
                            gson.fromJson(jsonObject, ResponseError::class.java)
                        val messageErr = errorResponse.error.message
                        AlertDialog.Builder(this).setTitle("Failed")
                            .setMessage(messageErr).setPositiveButton("Ok") { _, _ ->
                            }.show()
                    } catch (t: Throwable) {
                        Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
                    }
                }
                is com.bimabagaskhoro.phincon.core.utils.Resource.Empty -> {
                    binding.cardProgressbar.visibility = View.GONE
                    Log.d("Empty Data", "Empty")
                }

            }
        }
    }

    override fun onResume() {
        super.onResume()
        val nameScreen = this.javaClass.simpleName
        analyticViewModel.onLoadScreenPassword(nameScreen)
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}