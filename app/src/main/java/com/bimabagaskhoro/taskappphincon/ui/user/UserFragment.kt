package com.bimabagaskhoro.taskappphincon.ui.user

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
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
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.utils.Resource
import com.bimabagaskhoro.taskappphincon.data.source.response.auth.ResponseError
import com.bimabagaskhoro.taskappphincon.data.source.response.auth.SuccessImage
import com.bimabagaskhoro.taskappphincon.databinding.FragmentUserBinding
import com.bimabagaskhoro.taskappphincon.ui.SplashScreen
import com.bimabagaskhoro.taskappphincon.ui.activity.AuthActivity
import com.bimabagaskhoro.taskappphincon.ui.adapter.CustomSpinnerAdapter
import com.bimabagaskhoro.taskappphincon.ui.camera.CameraActivity
import com.bimabagaskhoro.taskappphincon.utils.reduceFileImage
import com.bimabagaskhoro.taskappphincon.utils.rotateBitmap
import com.bimabagaskhoro.taskappphincon.utils.uriToFile
import com.bimabagaskhoro.taskappphincon.vm.AuthViewModel
import com.bimabagaskhoro.taskappphincon.vm.DataStoreViewModel
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.util.*

@Suppress("DEPRECATION")
@AndroidEntryPoint
class UserFragment : Fragment() {
    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    private var getFile: File? = null
    private lateinit var result: Bitmap
    private val viewModel: AuthViewModel by viewModels()
    private val dataStoreViewModel: DataStoreViewModel by viewModels()
    val dataLanguage = arrayOf("EN","ID")
    val dataImgLanguage = intArrayOf(R.drawable.unitedstates, R.drawable.indonesia)


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
        // Inflate the layout for this fragment
        _binding = FragmentUserBinding.inflate(inflater, container, false)
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
        initDataStore()
        initSpinner()

        binding.apply {
            card2.setOnClickListener{
                findNavController().navigate(R.id.action_navigation_user_to_passwordFragment)
            }
            floatingActionButton.setOnClickListener{
                initDialog()
                initChangeImage()
            }
            btnLogout.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    dataStoreViewModel.clear()
                    startActivity(Intent(requireActivity(), AuthActivity::class.java))
                }
            }
        }
    }

    private fun initSpinner() {
        binding.spinnerCustom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    setLocate("en")
                    Log.d("language", "setlocale")
                    AlertDialog.Builder(requireActivity())
                        .setTitle(R.string.change_language)
                        .setMessage(R.string.change_language)
                        .setPositiveButton(R.string.ok) { _, _ ->
                            val intent = Intent(requireActivity(), SplashScreen::class.java)
                            startActivity(intent)
                        }
                        .setNegativeButton(R.string.cancel) { _, _ ->
                        }
                        .show()
                    //set dialog

                } else if (position == 1) {
                    setLocate("in")
                    Log.d("language", "setlocale")
                    AlertDialog.Builder(requireActivity())
                        .setTitle(R.string.change_language)
                        .setMessage(R.string.change_language)
                        .setPositiveButton(R.string.ok) { _, _ ->
                            val intent = Intent(requireActivity(), SplashScreen::class.java)
                            startActivity(intent)
                        }
                        .setNegativeButton(R.string.cancel) { _, _ ->
                        }
                        .show()
                    //set dialog
                }
                //Toast.makeText(requireContext(),"you select: $position \n${dataLanguage[position]}", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        val customSpinnerAdapter = CustomSpinnerAdapter(requireContext(), dataImgLanguage, dataLanguage)
        binding.spinnerCustom.adapter = customSpinnerAdapter
    }

    private fun setLocate(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        requireActivity().resources.updateConfiguration(config, requireActivity().resources.displayMetrics)
    }


    private fun initDataStore() {
        dataStoreViewModel.apply {
            getUserName.observe(viewLifecycleOwner) {
                val username = it
                binding.tvUsername.text = username
            }

            getUserEmail.observe(viewLifecycleOwner) {
                val userEmail = it
                binding.tvEmail.text = userEmail
            }

            getUserPath.observe(viewLifecycleOwner) {
                val userPath = it
                Glide.with(requireActivity())
                    .load(userPath)
                    .into(binding.imgProfile)
            }
        }
    }

    private fun initChangeImage() {
        var userToken = ""
        var userId = 0
        dataStoreViewModel.apply {
            getToken.observe(viewLifecycleOwner) {
                userToken = it
            }
            getUserId.observe(viewLifecycleOwner) {
                userId = it
            }
        }
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val reqBodyImage = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val multipartImage: MultipartBody.Part = MultipartBody.Part.createFormData(
                "image",
                file.name,
                reqBodyImage
            )
            viewModel.changeImage(
               // userToken,
                userId, multipartImage)
                .observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is Resource.Loading -> {
                            binding.progressbar.visibility = View.VISIBLE
                            binding.cardProgressbar.visibility = View.VISIBLE
                            binding.tvWaiting.visibility = View.VISIBLE
                        }
                        is Resource.Success -> {
                            binding.progressbar.visibility = View.GONE
                            binding.cardProgressbar.visibility = View.GONE
                            binding.tvWaiting.visibility = View.GONE
                            saveUpdateImagePath(result.data!!.success)
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
                            binding.cardProgressbar.visibility = View.GONE
                            binding.tvWaiting.visibility = View.GONE
                            val err = result.errorBody?.string()
                                ?.let { it1 -> JSONObject(it1).toString() }
                            val gson = Gson()
                            val jsonObject = gson.fromJson(err, JsonObject::class.java)
                            val errorResponse = gson.fromJson(jsonObject, ResponseError::class.java)
                            val messageErr = errorResponse.error.message
                            AlertDialog.Builder(requireActivity())
                                .setTitle("Change Image Failed")
                                .setMessage(messageErr)
                                .setPositiveButton("Ok") { _, _ ->
                                }
                                .show()

                        }
                    }
                }
        }
    }

    private fun saveUpdateImagePath(data: SuccessImage) {
        val path = data.path
        dataStoreViewModel.apply {
            saveUserPath(path)
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