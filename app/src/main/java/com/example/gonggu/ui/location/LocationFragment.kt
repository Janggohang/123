package com.example.gonggu.ui.location

import android.Manifest
import android.app.AlertDialog
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationManager.NETWORK_PROVIDER
import android.util.Log
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import com.example.gonggu.R
import com.example.gonggu.databinding.FragmentLocationBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import java.io.IOException
import kotlin.properties.Delegates

@Suppress("DEPRECATION")
class LocationFragment : Fragment() {

    private var binding: FragmentLocationBinding? = null
    private lateinit var map: MapView
    private var uLatitude by Delegates.notNull<Double>() // 위도
    private var uLongitude by Delegates.notNull<Double>() // 경도
    private val marker = MapPOIItem()
    private val db = Firebase.database
    private val mAuth = Firebase.auth
    val usersRef = db.getReference("user")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLocationBinding.inflate(inflater, container, false)
        val lm = activity?.getSystemService(LOCATION_SERVICE) as LocationManager
        val view = inflater.inflate(R.layout.fragment_location, container, false)
        val context = view.context
        val root : View = binding!!.root

        val setBtn = binding!!.setBtn // 위치 설정 버튼

        map = MapView(context)

        binding!!.mapView.addView(map)
        setBtn.setOnClickListener {
            setLocation()
        }
        // 줌인
        map.zoomIn(true)
        // 줌아웃
        map.zoomOut(true)

        when {
            PermissionChecker.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PermissionChecker.PERMISSION_GRANTED
            -> {
                val userNowLocation = (lm.getLastKnownLocation(GPS_PROVIDER) ?: lm.getLastKnownLocation(
                    NETWORK_PROVIDER)) as Location
                println(userNowLocation)
                uLatitude = userNowLocation?.latitude!!
                uLongitude = userNowLocation?.longitude!!
                startTracking()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                showPermissionContextPopup()
            }
            else -> {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1001
                )
            }
        }
        return root
    }

    // 현재 사용자 위치 추적
    private fun startTracking() {
        map.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading

        val uNowPosition = MapPoint.mapPointWithGeoCoord(uLatitude, uLongitude)
        // 중심점 변경
        map.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(uLatitude, uLongitude), true)
        Log.d("myLocation", "위도: $uLatitude 경도: $uLongitude")
        // 현위치에 마커 찍기
        marker.itemName = "현 위치"
        marker.tag = 0
        marker.mapPoint = uNowPosition
        marker.markerType = MapPOIItem.MarkerType.BluePin
        marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
        map.addPOIItem(marker)

    }

    // 위치 추적 종료
    private fun stopTasking() {
        map.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
    }

    private fun addMap() {

    }

    // 내 위치 설정
    private fun setLocation() {
        val geoCoder = Geocoder(context)

        try {
            val addresses = geoCoder.getFromLocation(uLatitude, uLongitude, 1)
            if (addresses.isNotEmpty()){
                val address = addresses[0]
                val addressText = address.getAddressLine(0)
                val addressString = addressText.drop(5)
                // 내 위치 설정
                usersRef.child(mAuth.currentUser?.uid!!).child("address").setValue(addressString)
            }
        }catch(e: IOException){
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTasking()
    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(context)
            .setTitle("권한이 필요합니다.")
            .setMessage("지도 사용을 위해 필요합니다.")
            .setPositiveButton("동의") { _, _ ->
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1010)
            }
            .create()
            .show()
    }
}