package com.raassh.dicodingstoryapp.views.stories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.raassh.dicodingstoryapp.R
import com.raassh.dicodingstoryapp.databinding.FragmentStoriesWithMapsBinding

class StoriesWithMapsFragment : Fragment() {
    private var token = ""

    private val viewModel by viewModels<StoriesViewModel> {
        StoriesViewModel.Factory(getString(R.string.auth, token), 1)
    }

    private var binding: FragmentStoriesWithMapsBinding? = null

    private val callback = OnMapReadyCallback { googleMap ->
//        val sydney = LatLng(-34.0, 151.0)
//        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        viewModel.stories.observe(viewLifecycleOwner) {
            googleMap.clear()

            it.forEach { story ->
                val latLng = LatLng(story.lat, story.lon)
                googleMap.addMarker(
                    MarkerOptions().position(latLng)
                        .title(getString(R.string.stories_content_description, story.name))
                )
            }

            val indonesia = LatLng(6.1750, 106.8283)
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(indonesia))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.apply {
            show()
            setDisplayHomeAsUpEnabled(false)
        }
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

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.map).isVisible = false
    }
}