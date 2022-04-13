package com.raassh.dicodingstoryapp.views.newstory

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.*
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.raassh.dicodingstoryapp.R
import com.raassh.dicodingstoryapp.customviews.EditTextWithValidation
import com.raassh.dicodingstoryapp.databinding.NewStoryFragmentBinding
import com.raassh.dicodingstoryapp.misc.*
import com.raassh.dicodingstoryapp.views.cameraview.CameraFragment
import java.io.File

class NewStoryFragment : Fragment() {
    private val viewModel by viewModels<NewStoryViewModel>()

    private var _binding: NewStoryFragmentBinding? = null
    private val binding get() = _binding!!

    private var imgFile: File? = null
    private var token = ""

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

        showLoading(false)
        token = NewStoryFragmentArgs.fromBundle(arguments as Bundle).token

        if (!allPermissionGranted()) {
            launcherPermissionRequest.launch(REQUIRED_PERMISSIONS)
        }

        setFragmentResultListener(CameraFragment.CAMERA_RESULT) { _, bundle ->
            showSnackbar(binding.root, getString(R.string.take_picture_success))
            val uri = bundle.getParcelable<Uri>("picture") as Uri
            val isBackCamera = bundle.get("isBackCamera") as Boolean

            imgFile = uriToFile(uri, context as Context)
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

                viewModel.addNewStory(imgFile as File, descriptionInput.text.toString(), token)
            }
        }

        viewModel.apply {
            isLoading.observe(viewLifecycleOwner) {
                showLoading(it)
            }

            isSuccess.observe(viewLifecycleOwner) {
                it.getContentIfNotHandled()?.let { success ->
                    if (success) {
                        setFragmentResult(ADD_RESULT, bundleOf(
                            Pair("isSuccess", true)
                        ))

                        view.findNavController().navigateUp()
                    }
                }
            }

            error.observe(viewLifecycleOwner) {
                it.getContentIfNotHandled()?.let { message ->
                    showSnackbar(binding.root, message)
                }
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

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            uploadGroup.visibility = visibility(!isLoading)
            uploadLoadingGroup.visibility = visibility(isLoading)
        }
    }

    companion object {
        private val REQUIRED_PERMISSIONS = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else {
            arrayOf(Manifest.permission.CAMERA)
        }

        const val ADD_RESULT = "add_result"
    }
}