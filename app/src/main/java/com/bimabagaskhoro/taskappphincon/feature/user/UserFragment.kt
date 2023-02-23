package com.bimabagaskhoro.taskappphincon.feature.user

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bimabagaskhoro.phincon.core.data.source.remote.response.ResponseError
import com.bimabagaskhoro.phincon.core.data.source.remote.response.auth.SuccessImage
import com.bimabagaskhoro.phincon.core.utils.Constant.Companion.CAMERA_X_RESULT
import com.bimabagaskhoro.phincon.core.utils.Constant.Companion.REQUEST_CODE_PERMISSIONS
import com.bimabagaskhoro.phincon.core.vm.DataStoreViewModel
import com.bimabagaskhoro.phincon.core.vm.FGAViewModel
import com.bimabagaskhoro.phincon.core.vm.RemoteViewModel
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.databinding.FragmentUserBinding
import com.bimabagaskhoro.taskappphincon.feature.activity.AuthActivity
import com.bimabagaskhoro.taskappphincon.feature.activity.PasswordActivity
import com.bimabagaskhoro.taskappphincon.feature.adapter.CustomSpinnerAdapter
import com.bimabagaskhoro.taskappphincon.feature.camera.CameraActivity
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
    private val binding get() = _binding

    private var getFile: File? = null
    private lateinit var result: Bitmap
    private val viewModel: RemoteViewModel by viewModels()
    private val dataStoreViewModel: DataStoreViewModel by viewModels()
    private val dataLanguage = arrayOf("en", "id")
    private val dataImgLanguage = intArrayOf(R.drawable.unitedstates, R.drawable.indonesia)
    private val analyticViewModel: FGAViewModel by viewModels()

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
                initChangeImage()
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
                initChangeImage()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUserBinding.inflate(inflater, container, false)
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
        initDataStore()
        initSpinner()

        binding?.apply {
            card2.setOnClickListener {
                val intent = Intent(requireActivity(), PasswordActivity::class.java)
                startActivity(intent)
                analyticViewModel.onClickChangePasswordProfile()
            }
            floatingActionButton.setOnClickListener {
                initDialog()
                analyticViewModel.onClickCameraProfile()
            }
            btnLogout.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    dataStoreViewModel.clear()
                    startActivity(Intent(requireActivity(), AuthActivity::class.java))
                    requireActivity().finish()
                    analyticViewModel.onClickLogoutProfile()
                }
            }
        }
    }

    // Start Spinner
    @SuppressLint("ClickableViewAccessibility")
    private fun initSpinner() {
        var isSpinnerTouched = false
        binding?.apply {
            spinnerCustom.setOnTouchListener { _, _ ->
                isSpinnerTouched = true
                false
            }
            spinnerCustom.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        if (isSpinnerTouched) {
                            when (position) {
                                0 -> {
                                    setLocate("en")
                                    setDialogChangeLanguage()
                                    analyticViewModel.onChangeLanguageProfile("EN")
                                }
                                1 -> {
                                    setLocate("in")
                                    setDialogChangeLanguage()
                                    analyticViewModel.onChangeLanguageProfile("ID")
                                }
                            }
                        } else {
                            isSpinnerTouched = false
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
//            dataStoreViewModel.getUserLanguage.observe(viewLifecycleOwner) {
//                val pos = it
//                if (pos != null) {
//                    spinnerCustom.setSelection(pos)
//                } else {
//                    spinnerCustom.setSelection(0)
//                }
//            }
            val customSpinnerAdapter =
                CustomSpinnerAdapter(requireContext(), dataImgLanguage, dataLanguage)
            spinnerCustom.adapter = customSpinnerAdapter
        }
    }

    private fun setDialogChangeLanguage() {
        AlertDialog.Builder(requireActivity())
            .setTitle(R.string.change_language)
            .setMessage(R.string.change_language)
            .setPositiveButton(R.string.ok) { _, _ ->
                activity?.recreate()
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
            }
            .show()
    }

    private fun setLocate(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.locale = locale
        res.updateConfiguration(conf, dm)


        dataStoreViewModel.saveLanguage(lang)
    }

    // End Spinner

    private fun initDataStore() {
        dataStoreViewModel.apply {
            getUserName.observe(viewLifecycleOwner) {
                val username = it
                binding?.tvUsername?.text = username
            }

            getUserEmail.observe(viewLifecycleOwner) {
                val userEmail = it
                binding?.tvEmail?.text = userEmail
            }

            getUserPath.observe(viewLifecycleOwner) {
                val userPath = it
                binding?.imgProfile?.let { it1 ->
                    Glide.with(requireActivity())
                        .load(userPath)
                        .into(it1)
                }
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
            val file = com.bimabagaskhoro.phincon.core.utils.reduceFileImage(getFile as File)
            val reqBodyImage = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val multipartImage: MultipartBody.Part = MultipartBody.Part.createFormData(
                "image",
                file.name,
                reqBodyImage
            )
            viewModel.changeImage(
                // userToken,
                userId, multipartImage
            )
                .observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is com.bimabagaskhoro.phincon.core.utils.Resource.Loading -> {
                            binding?.apply {
                                progressbar.visibility = View.VISIBLE
                                cardProgressbar.visibility = View.VISIBLE
                                tvWaiting.visibility = View.VISIBLE
                            }
                        }
                        is com.bimabagaskhoro.phincon.core.utils.Resource.Success -> {
                            binding?.apply {
                                progressbar.visibility = View.GONE
                                cardProgressbar.visibility = View.GONE
                                tvWaiting.visibility = View.GONE
                                result.data?.success?.let { saveUpdateImagePath(it) }
                                val dataMessages = result.data?.success?.message
                                AlertDialog.Builder(requireActivity())
                                    .setTitle("Change Image Success")
                                    .setMessage(dataMessages)
                                    .setPositiveButton("Ok") { _, _ ->
                                    }
                                    .show()
                            }
                        }
                        is com.bimabagaskhoro.phincon.core.utils.Resource.Error -> {
                            try {
                                binding?.apply {
                                    progressbar.visibility = View.GONE
                                    cardProgressbar.visibility = View.GONE
                                    tvWaiting.visibility = View.GONE
                                }
                                val err = result.errorBody?.string()
                                    ?.let { it1 -> JSONObject(it1).toString() }
                                val gson = Gson()
                                val jsonObject = gson.fromJson(err, JsonObject::class.java)
                                val errorResponse =
                                    gson.fromJson(jsonObject, ResponseError::class.java)
                                val messageErr = errorResponse.error.message
                                AlertDialog.Builder(requireActivity())
                                    .setTitle("Change Image Failed")
                                    .setMessage(messageErr)
                                    .setPositiveButton("Ok") { _, _ ->
                                    }
                                    .show()
                            } catch (t: Throwable) {
                                Toast.makeText(requireActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show()
                            }

                        }
                        is com.bimabagaskhoro.phincon.core.utils.Resource.Empty -> {
                            binding?.apply {
                                progressbar.visibility = View.GONE
                                cardProgressbar.visibility = View.GONE
                                tvWaiting.visibility = View.GONE
                                Log.d("Empty Data", "Empty")
                            }
                        }
                    }
                }
        }
    }

    private fun saveUpdateImagePath(data: SuccessImage) {
        val path = data.path
        dataStoreViewModel.apply {
            path?.let { saveUserPath(it) }
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
            openCameraX()
            mDialog.dismiss()
            analyticViewModel.onChangeImageProfile("camera")
        }
        btnGal.setOnClickListener {
            openGallery()
            mDialog.dismiss()
            analyticViewModel.onChangeImageProfile("gallery")
        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        val chooser = Intent.createChooser(intent, "Choose Image")
        launcherIntentGallery.launch(chooser)
    }

    private fun openCameraX() {
        val intent = Intent(requireActivity(), CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        val nameScreen = this.javaClass.simpleName
        analyticViewModel.onLoadScreenProfile(nameScreen)
    }

}