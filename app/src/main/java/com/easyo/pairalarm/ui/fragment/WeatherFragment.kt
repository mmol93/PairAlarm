package com.easyo.pairalarm.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import com.easyo.pairalarm.R
import com.easyo.pairalarm.databinding.FragmentWeatherBinding
import com.easyo.pairalarm.util.isGrantedAllLocationPermission
import com.easyo.pairalarm.util.isInternetOnline
import com.easyo.pairalarm.util.makeToast
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class WeatherFragment : Fragment() {
    private lateinit var binding: FragmentWeatherBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    @SuppressLint("MissingPermission") // isGrantedAllLocationPermission 에서 권한 확인함
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWeatherBinding.bind(view)

        // Check internet connection
        if (!isInternetOnline(requireContext())) {
            makeToast(requireContext(), getString(R.string.toast_error_internet_connection))
            return
        }

        // Check location(GPS) permission
        if (!isGrantedAllLocationPermission()) {
            requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                1
            )
            makeToast(requireContext(), getString(R.string.toast_error_gps_location))
            return
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location == null) {
                Log.e("WeatherFragment", "location get fail")
            } else {
                Log.d("WeatherFragment", "${location.latitude} , ${location.longitude}")
                binding.loadingProgress.visibility = View.GONE
                binding.loadingTextView.visibility = View.GONE
                binding.loadingErrorTextView.visibility = View.GONE
                binding.weatherContainer.visibility = View.VISIBLE
            }
        }
            .addOnFailureListener {
                Log.e("WeatherFragment", "location error is ${it.message}")
                makeToast(requireContext(), getString(R.string.toast_error_please_refresh))
                it.printStackTrace()
            }
    }
}