package com.raassh.dicodingstoryapp.views.stories

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.raassh.dicodingstoryapp.R
import com.raassh.dicodingstoryapp.data.api.ListStoryItem
import com.raassh.dicodingstoryapp.databinding.FragmentStoriesWithMapsBinding

class StoriesWithMapsFragment : Fragment() {
    private var token = ""

    private val viewModel by viewModels<StoriesViewModel> {
        StoriesViewModel.Factory(getString(R.string.auth, token), 1)
    }

    private var binding: FragmentStoriesWithMapsBinding? = null

    private val callback = OnMapReadyCallback { googleMap ->
        setMapStyle(googleMap)

        viewModel.stories.observe(viewLifecycleOwner) {
            googleMap.clear()

            it.forEach { story ->
                val latLng = LatLng(story.lat, story.lon)
                val marker = googleMap.addMarker(
                    MarkerOptions().position(latLng)
                        .title(getString(R.string.stories_content_description, story.name))
                        .snippet(getString(R.string.marker_snippet))
                        .icon(
                            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                        )
                )
                marker?.tag = story
            }

            googleMap.uiSettings.apply {
                isZoomControlsEnabled = true
                isCompassEnabled = true
            }

            googleMap.apply {
                setOnInfoWindowClickListener { marker ->
                    findNavController().navigate(
                        StoriesWithMapsFragmentDirections
                            .actionStoriesWithMapsFragmentToStoryDetailFragment(
                                marker.tag as ListStoryItem
                            )
                    )
                }

                val surabaya = LatLng(-7.250445, 112.768845)
                moveCamera(CameraUpdateFactory.newLatLngZoom(surabaya, 7f))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoriesWithMapsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        token = StoriesWithMapsFragmentArgs.fromBundle(arguments as Bundle).token

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun setMapStyle(map: GoogleMap) {
        try {
            context?.let {
                val success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        it,
                        R.raw.map_style
                    )
                )

                if (!success) {
                    Log.e(TAG, "Style parsing failed.")
                }
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    companion object {
        private const val TAG = "StoriesWithMapsFragment"
    }
}