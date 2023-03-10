package com.bimabagaskhoro.phincon.features.auth.auth

import android.Manifest
import android.annotation.SuppressLint
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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bimabagaskhoro.phincon.core.data.source.remote.response.ResponseError
import com.bimabagaskhoro.phincon.core.utils.Constant.Companion.CAMERA_X_RESULT
import com.bimabagaskhoro.phincon.core.utils.Constant.Companion.REQUEST_CODE_PERMISSIONS
import com.bimabagaskhoro.phincon.core.vm.FGAViewModel
import com.bimabagaskhoro.phincon.core.vm.RemoteViewModel
import com.bimabagaskhoro.phincon.features.auth.CameraActivity
import com.bimabagaskhoro.phincon.features.auth.R
import com.bimabagaskhoro.phincon.features.auth.databinding.FragmentRegisterBinding
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

@Suppress("DEPRECATION")
@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding
    private var getFile: File? = null
    private lateinit var result: Bitmap
    private val viewModel: RemoteViewModel by viewModels()
    private val analyticViewModel: FGAViewModel by viewModels()
    private var isCamera = false

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = myFile
            result = com.bimabagaskhoro.phincon.core.utils.rotateBitmap(
                BitmapFactory.decodeFile(myFile.absolutePath),
                isBackCamera
            )

            binding?.apply {
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
            val file = com.bimabagaskhoro.phincon.core.utils.uriToFile(uri, requireContext())
            getFile = file

            binding?.apply {
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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding?.root
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

        binding?.apply {
            btnRegisterRegisterFragment.setOnClickListener { onButtonPressed() }
            btnLoginOnRegisterFragment.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                analyticViewModel.onClickButtonRegisterToLogin()
            }
            floatingActionButton.setOnClickListener {
                initDialog()
                analyticViewModel.onClickCameraIcon()
            }
            edtEmailRegisterFragment.doOnTextChanged { _, _, _, _ ->
                onTextChange()
            }
        }
    }

    private fun onTextChange() {
        val email = binding?.edtEmailRegisterFragment?.text.toString()
        if(Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding?.tvCheckingEmail?.visibility = View.INVISIBLE
        }else{
            binding?.tvCheckingEmail?.visibility = View.VISIBLE
            binding?.edtEmailRegisterFragment?.requestFocus()
        }
    }

    private fun onButtonPressed() {
        val name = binding?.edtNameRegisterFragment?.text.toString().trim()
        val email = binding?.edtEmailRegisterFragment?.text.toString().trim()
        val password = binding?.edtPasswordRegisterFragment?.text.toString().trim()
        val matchPassword = binding?.edtConfirmPasswordRegisterFragment?.text.toString().trim()
        val phone = binding?.edtPhoneRegisterFragment?.text.toString().trim()
        val gender =
            if (binding?.rdFemaleRegisterFragment?.isChecked == true) isGender(binding?.rdFemaleRegisterFragment?.isChecked!!) else isGender(
                binding?.rdMaleRegisterFragment?.isChecked == true
            )

        val emailPattern = binding?.edtEmailRegisterFragment?.text.toString().trim()
        if (Patterns.EMAIL_ADDRESS.matcher(emailPattern).matches()) { // using EMAIL_ADREES matcher
            binding?.tvCheckingEmail?.visibility = View.INVISIBLE
            val passwordCheck = binding?.edtPasswordRegisterFragment?.text.toString().trim()
            val matchPasswordCheck = binding?.edtConfirmPasswordRegisterFragment?.text.toString().trim()
            if (passwordCheck != matchPasswordCheck) {
                Toast.makeText(context, "Password tidak sama", Toast.LENGTH_LONG).show()
            } else {
                Log.d("checkMatchPassword", "password: success")
                if (getFile == null) {
                    Toast.makeText(context, "Upload Profile Required", Toast.LENGTH_LONG).show()
                } else {
                    when {
                        email.isEmpty() -> {
                            binding?.edtEmailRegisterFragment?.error = "Masukan email terlebih dahulu"
                            binding?.edtEmailRegisterFragment?.requestFocus()
                        }
                        password.isEmpty() -> {
                            binding?.edtPasswordRegisterFragment?.error = "Masukan password terlebih dahulu"
                            binding?.edtPasswordRegisterFragment?.requestFocus()
                        }
                        matchPassword.isEmpty() -> {
                            binding?.edtConfirmPasswordRegisterFragment?.error =
                                "Masukan konfirmasi password terlebih dahulu"
                            binding?.edtConfirmPasswordRegisterFragment?.requestFocus()
                        }
                        name.isEmpty() -> {
                            binding?.edtNameRegisterFragment?.error = "Masukan nama terlebih dahulu"
                            binding?.edtNameRegisterFragment?.requestFocus()
                        }
                        phone.isEmpty() -> {
                            binding?.edtPhoneRegisterFragment?.error = "Masukan nomer terlebih dahulu"
                            binding?.edtPhoneRegisterFragment?.requestFocus()
                        }
                        else -> {
                            binding?.edtEmailRegisterFragment?.error = null
                            binding?.edtPasswordRegisterFragment?.error = null
                            binding?.edtNameRegisterFragment?.error = null
                            binding?.edtConfirmPasswordRegisterFragment?.error = null
                            binding?.edtPhoneRegisterFragment?.error = null
                            initData(name, email, password, phone, gender)
                        }
                    }
                }
            }
        } else {
            binding?.tvCheckingEmail?.visibility = View.VISIBLE
        }
    }

    private fun initData(
        name: String,
        email: String,
        password: String,
        phone: String,
        gender: Int
    ) {
        if (getFile != null) {
            val file = com.bimabagaskhoro.phincon.core.utils.reduceFileImage(getFile as File)
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

            viewModel.register(multipartImage, emailBody, passwordBody, nameBody, phoneBody, gender)
                .observe(viewLifecycleOwner) {
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
                            val dataMessages = it.data?.success?.message
                            AlertDialog.Builder(requireActivity())
                                .setTitle("Register Success")
                                .setMessage(dataMessages)
                                .setPositiveButton("Ok") { _, _ ->
                                }
                                .show()
                            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                            val isfrom = if (isCamera) CAMERA else GALLERY
                            analyticViewModel.onClickButtonRegister(
                                isfrom,
                                email,
                                name,
                                phone,
                                gender.toString()
                            )
                        }
                        is com.bimabagaskhoro.phincon.core.utils.Resource.Error -> {
                            try {
                                binding?.progressbar?.visibility = View.GONE
                                binding?.cardProgressbar?.visibility = View.GONE
                                binding?.tvWaiting?.visibility = View.GONE
                                val err =
                                    it.errorBody?.string()
                                        ?.let { it1 -> JSONObject(it1).toString() }
                                val gson = Gson()
                                val jsonObject = gson.fromJson(err, JsonObject::class.java)
                                val errorResponse =
                                    gson.fromJson(jsonObject, ResponseError::class.java)
                                val messageErr = errorResponse.error.message
                                AlertDialog.Builder(requireActivity())
                                    .setTitle("Gagal")
                                    .setMessage(messageErr)
                                    .setPositiveButton("Ok") { _, _ ->
                                    }
                                    .show()
                                val errCode = it.errorCode
                                Log.d("errorCode", "$errCode")
                            } catch (t: Throwable) {
                                Toast.makeText(
                                    requireActivity(),
                                    t.localizedMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        is com.bimabagaskhoro.phincon.core.utils.Resource.Empty -> {
                            Log.d("Empty Data", "Empty")
                        }
                    }
                }
        }
    }

    @SuppressLint("InflateParams")
    private fun initDialog() {
        val dialogBinding = layoutInflater.inflate(R.layout.custom_dialog_camera, null)
        val mDialog = Dialog(requireActivity())
        mDialog.setContentView(dialogBinding)

        mDialog.setCancelable(true)
        mDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mDialog.show()

        val btnCam = dialogBinding.findViewById<TextView>(R.id.tv_camera)
        val btnGal = dialogBinding.findViewById<TextView>(R.id.tv_galery)

        btnCam.setOnClickListener {
            isCamera = true
            openCameraX()
            mDialog.dismiss()
            analyticViewModel.onChangeImage(CAMERA)
        }
        btnGal.setOnClickListener {
            openGallery()
            mDialog.dismiss()
            analyticViewModel.onChangeImage(GALLERY)
        }
    }

    private fun isGender(isChecked: Boolean): Int {
        val female = binding?.rdFemaleRegisterFragment?.isChecked
        return if (isChecked == female) {
            1
        } else {
            0
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
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val CAMERA = "camera"
        private const val GALLERY = "gallery"
    }

    override fun onResume() {
        super.onResume()
        val nameScreen = this.javaClass.simpleName
        analyticViewModel.onLoadScreenRegister(nameScreen)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}

//