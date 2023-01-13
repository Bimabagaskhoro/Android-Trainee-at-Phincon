package com.bimabagaskhoro.taskappphincon.ui.user

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.data.pref.AuthPreference
import com.bimabagaskhoro.taskappphincon.data.source.Resource
import com.bimabagaskhoro.taskappphincon.data.source.response.ResponseLoginError
import com.bimabagaskhoro.taskappphincon.databinding.FragmentUserBinding
import com.bimabagaskhoro.taskappphincon.ui.activity.AuthActivity
import com.bimabagaskhoro.taskappphincon.ui.camera.CameraActivity
import com.bimabagaskhoro.taskappphincon.utils.rotateBitmap
import com.bimabagaskhoro.taskappphincon.utils.uriToFile
import com.bimabagaskhoro.taskappphincon.vm.AuthViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File

@AndroidEntryPoint
class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    private lateinit var authPreference: AuthPreference
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authPreference = AuthPreference(requireContext())
        //initDataStore()

        binding.apply {
            val spinner = binding.spinner
            floatingActionButton.setOnClickListener{
                initDialog()
                //initData()
            }
            btnLogout.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    //authPreference.clear()
                    startActivity(Intent(requireActivity(), AuthActivity::class.java))
                }
            }
        }
    }

    private fun initDataStore() {
//        val path = authPreference.userPath
//        val userName = authPreference.userName
//        val userEmail = authPreference.userEmail
//        Glide.with(requireActivity())
//            .load(path)
//            .into(binding.imgProfile)
//        userName.observe(viewLifecycleOwner) {
//            binding.tvUsername.text = it
//        }
//        userEmail.observe(viewLifecycleOwner) {
//            binding.tvEmail.text = it
//        }
    }

    private fun initData() {
        val userId = authPreference.userId
        userId.observe(viewLifecycleOwner) {
            if (getFile != null) {
                val file = getFile as File
                val reqBodyImage = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val multipartImage: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    reqBodyImage
                )
                viewModel.changeImage(it, multipartImage).observe(viewLifecycleOwner)  { result ->
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
                                .setTitle("Change Image Success")
                                .setMessage(dataMessages)
                                .setPositiveButton("Ok") { _, _ ->
                                }
                                .show()
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
                                .setTitle("Change Image Failed")
                                .setMessage(messageErr)
                                .setPositiveButton("Ok") { _, _ ->
                                }
                                .show()


                            val errCode = result.errorCode!!.toInt()
                            if (errCode == 401) {
                                val userIds = authPreference.userId.value!!.toInt()
                                val accessToken = authPreference.userToken.value!!.toString()
                                val refreshToken = authPreference.refreshToken.value!!.toString()
                                viewModel.refreshToken(userIds, accessToken, refreshToken).observe(viewLifecycleOwner) {errCode ->
                                    when(errCode) {
                                        is Resource.Loading -> {
                                            binding.progressbar.visibility = View.VISIBLE
                                            binding.cardProgressbar.visibility =View.VISIBLE
                                            binding.tvWaiting.visibility =View.VISIBLE
                                        }
                                        is Resource.Success -> {
                                            binding.progressbar.visibility = View.VISIBLE
                                            binding.cardProgressbar.visibility =View.VISIBLE
                                            binding.tvWaiting.visibility =View.VISIBLE
                                            val newAccessToken = errCode.data!!.success.accessToken
                                            val newRefreshToken = errCode.data.success.refreshToken
                                            Log.d("Token","$newAccessToken $newRefreshToken")
                                        }
                                        is Resource.Error -> {
                                            binding.progressbar.visibility = View.VISIBLE
                                            binding.cardProgressbar.visibility =View.VISIBLE
                                            binding.tvWaiting.visibility =View.VISIBLE
                                            val errE = errCode.errorBody?.string()?.let { it1 -> JSONObject(it1).toString() }
                                            val gsonE= Gson()
                                            val jsonObjectE = gsonE.fromJson(errE, JsonObject::class.java)
                                            val errorResponseE = gson.fromJson(jsonObjectE, ResponseLoginError::class.java)
                                            val messageErrE = errorResponseE.error.message
                                            Log.d("Token", messageErrE)
                                        }
                                    }
                                }
                            }
                        }
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