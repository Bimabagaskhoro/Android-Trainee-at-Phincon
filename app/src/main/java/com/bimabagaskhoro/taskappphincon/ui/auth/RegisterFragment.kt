package com.bimabagaskhoro.taskappphincon.ui.auth

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.utils.Resource
import com.bimabagaskhoro.taskappphincon.data.source.response.ResponseError
import com.bimabagaskhoro.taskappphincon.databinding.FragmentRegisterBinding
import com.bimabagaskhoro.taskappphincon.ui.camera.CameraActivity
import com.bimabagaskhoro.taskappphincon.utils.reduceFileImage
import com.bimabagaskhoro.taskappphincon.utils.rotateBitmap
import com.bimabagaskhoro.taskappphincon.utils.uriToFile
import com.bimabagaskhoro.taskappphincon.vm.AuthViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val TAG = RegisterFragment::class.java.simpleName
    private var getFile: File? = null
    private lateinit var result: Bitmap
    private val viewModel: AuthViewModel by viewModels()

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = myFile
            result = rotateBitmap(
                BitmapFactory.decodeFile(myFile.absolutePath),
                isBackCamera
            )

            binding.apply {
                floatingActionButton.isEnabled = true
                imgProfile.visibility = View.VISIBLE
                imgProfile.setImageBitmap(result)
            }
        }
    }
    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val uri = it.data?.data as Uri
            val file = uriToFile(uri, requireContext())
            getFile = file

            binding.apply {
                floatingActionButton.isEnabled = true
                imgProfile.visibility = View.VISIBLE
                imgProfile.setImageURI(uri)
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.apply {
            btnRegister.setOnClickListener { onButtonPressed() }
            btnLogin.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
            floatingActionButton.setOnClickListener{
                initDialog()
            }
        }
    }

    private fun onButtonPressed() {
        val name = binding.edtName.text.toString().trim()
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()
        val matchPassword = binding.edtConfirmPassword.text.toString().trim()
        val phone = binding.edtPhone.text.toString().trim()
        val gender = if (binding.rdFemale.isChecked) isGender(binding.rdFemale.isChecked) else isGender(binding.rdMale.isChecked)
        checkEmail()
        checkMatchPassword()
        when {
            email.isEmpty() -> {
                binding.edtEmail.error = "Masukan email terlebih dahulu"
                binding.edtEmail.requestFocus()
            }
            password.isEmpty() -> {
                binding.edtPassword.error = "Masukan password terlebih dahulu"
                binding.edtPassword.requestFocus()
            }
            matchPassword.isEmpty() -> {
                binding.edtConfirmPassword.error = "Masukan konfirmasi password terlebih dahulu"
                binding.edtConfirmPassword.requestFocus()
            }
            name.isEmpty() -> {
                binding.edtName.error = "Masukan nama terlebih dahulu"
                binding.edtName.requestFocus()
            }
            phone.isEmpty() -> {
                binding.edtPhone.error = "Masukan nomer terlebih dahulu"
                binding.edtPhone.requestFocus()
            }
            else -> {
                binding.edtEmail.error = null
                binding.edtPassword.error = null
                binding.edtName.error = null
                binding.edtConfirmPassword.error = null
                binding.edtPhone.error = null
                initData(name, email, password, phone, gender)
            }
        }
    }

    private fun initData(name: String, email: String, password: String, phone: String, gender: Int) {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val reqBodyImage = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val multipartImage: MultipartBody.Part = MultipartBody.Part.createFormData(
                "image",
                file.name,
                reqBodyImage
            )

            Log.d("getFile", "$getFile")

            val emailBody = email.toRequestBody("text/plain".toMediaType())
            val passwordBody = password.toRequestBody("text/plain".toMediaType())
            val nameBody = name.toRequestBody("text/plain".toMediaType())
            val phoneBody = phone.toRequestBody("text/plain".toMediaType())

            viewModel.register(multipartImage, emailBody, passwordBody, nameBody, phoneBody, gender).observe(viewLifecycleOwner) {
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
                        val dataMessages = it.data!!.success.message
                        AlertDialog.Builder(requireActivity())
                            .setTitle("Register Success")
                            .setMessage(dataMessages)
                            .setPositiveButton("Ok") { _, _ ->
                            }
                            .show()
                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                    }
                    is Resource.Error -> {
                        binding.progressbar.visibility = View.GONE
                        binding.cardProgressbar.visibility =View.GONE
                        binding.tvWaiting.visibility =View.GONE
                        val err = it.errorBody?.string()?.let { it1 -> JSONObject(it1).toString() }
                        val gson = Gson()
                        val jsonObject = gson.fromJson(err, JsonObject::class.java)
                        val errorResponse = gson.fromJson(jsonObject, ResponseError::class.java)
                        val messageErr = errorResponse.error.message
                        AlertDialog.Builder(requireActivity())
                            .setTitle("Gagal")
                            .setMessage(messageErr)
                            .setPositiveButton("Ok") { _, _ ->
                            }
                            .show()
                        val errCode = it.errorCode!!.toInt()
                        Log.d("errorCode", "$errCode")

                        //Toast.makeText(requireActivity(), errorResponse.error.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun initDialog() {
        val dialogBinding = layoutInflater.inflate(R.layout.fragment_dialog_camera, null)
        val mDialog = Dialog(requireActivity())
        mDialog.setContentView(dialogBinding)

        mDialog.setCancelable(true)
        mDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mDialog.show()

        val btnCam = dialogBinding.findViewById<TextView>(R.id.tv_camera)
        val btnGal = dialogBinding.findViewById<TextView>(R.id.tv_galery)
        btnCam.setOnClickListener { openCameraX() }
        btnGal.setOnClickListener { openGallery() }
    }

    private fun isGender(isChecked: Boolean): Int {
        val female = binding.rdFemale.isChecked
        return if (isChecked == female) {
            1
        } else {
            0
        }
    }

    private fun checkMatchPassword() {
        val password = binding.edtPassword.text.toString().trim()
        val matchPassword = binding.edtConfirmPassword.text.toString().trim()
        if (password != matchPassword) {
            Toast.makeText(context, "Password tidak sama", Toast.LENGTH_LONG).show()
        } else {
            Log.d(TAG, "password: success")
        }
    }

    private fun checkEmail() {
        val email = binding.edtEmail.text.toString().trim()
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) { // using EMAIL_ADREES matcher
            binding.tvCheckingEmail.visibility = View.INVISIBLE
        } else {
            binding.tvCheckingEmail.visibility = View.VISIBLE
        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        val chooser = Intent.createChooser(intent, "Pilih Gambar")
        launcherIntentGallery.launch(chooser)
    }

    private fun openCameraX() {
        val intent = Intent(requireActivity(), CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

}