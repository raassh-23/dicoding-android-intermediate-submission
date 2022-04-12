package com.raassh.dicodingstoryapp.views.newstory

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.raassh.dicodingstoryapp.R
import com.raassh.dicodingstoryapp.customviews.EditTextWithValidation
import com.raassh.dicodingstoryapp.databinding.NewStoryFragmentBinding
import com.raassh.dicodingstoryapp.misc.hideSoftKeyboard
import com.raassh.dicodingstoryapp.misc.rotateBitmap
import com.raassh.dicodingstoryapp.misc.showSnackbar
import com.raassh.dicodingstoryapp.misc.uriToFile
import com.raassh.dicodingstoryapp.views.cameraview.CameraFragment
import java.io.File

class NewStoryFragment : Fragment() {
    private val viewModel by viewModels<NewStoryViewModel>()

    private var _binding: NewStoryFragmentBinding? = null
    private val binding get() = _binding!!

    private var imgFile: File? = null

    private val launcherPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (!allPermissionGranted()) {
            showSnackbar(binding.root, getString(R.string.permission_denied))
            findNavController().navigateUp()
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImage = it.data?.data as Uri
            imgFile = uriToFile(selectedImage, context as Context)
            binding.previewImage.setImageURI(selectedImage)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.apply {
            show()
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NewStoryFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!allPermissionGranted()) {
            launcherPermissionRequest.launch(REQUIRED_PERMISSIONS)
        }

        setFragmentResultListener(CameraFragment.CAMERA_RESULT) { _, bundle ->
            Log.d("TAG", "onViewCreated: $bundle")
            imgFile = bundle.get("picture") as File
            val isBackCamera = bundle.get("isBackCamera") as Boolean

            val result = rotateBitmap(
                BitmapFactory.decodeFile(imgFile?.path),
                isBackCamera
            )

            binding.previewImage.setImageBitmap(result)
        }

        binding.apply {
            descriptionInput.setValidationCallback(object : EditTextWithValidation.InputValidation {
                override val errorMessage: String
                    get() = getString(R.string.desc_validation_message)

                override fun validate(input: String) = !TextUtils.isEmpty(input)
            })

            cameraButton.setOnClickListener {
                findNavController().navigate(R.id.action_newStoryFragment_to_cameraFragment)
            }

            galleryButton.setOnClickListener {
                val intent = Intent().apply {
                    action = Intent.ACTION_GET_CONTENT
                    type = "image/*"
                }

                val chooser = Intent.createChooser(intent, getString(R.string.chooser_title))
                launcherIntentGallery.launch(chooser)
            }

            addButton.setOnClickListener {
                hideSoftKeyboard(activity as FragmentActivity)

                if (!descriptionInput.validateInput() || imgFile == null) {
                    showSnackbar(binding.root, getString(R.string.validation_error))
                    return@setOnClickListener
                }

//                viewModel.addNewStory()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            activity?.baseContext as Context,
            it
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}