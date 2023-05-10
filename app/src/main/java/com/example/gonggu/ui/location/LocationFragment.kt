package com.example.gonggu.ui.location

import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import com.example.gonggu.R
import com.example.gonggu.MainActivity
import com.example.gonggu.databinding.FragmentLocationBinding
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView



class LocationFragment : Fragment(), MapView.MapViewEventListener {

    private var binding: FragmentLocationBinding? = null
    private lateinit var map: MapView
    private val marker = MapPOIItem()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLocationBinding.inflate(inflater, container, false)
        val root : View = binding!!.root
        map = MapView(context)
        //requestLocationUpdates()
        binding!!.mapView.addView(map)
        startTracking()
        map.setMapViewEventListener(this)
        // 줌인
        map.zoomIn(true)
        // 줌아웃
        map.zoomOut(true)
        return root
    }

    // 현재 사용자 위치 추적
    private fun startTracking() {
        map.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
        val lm = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val userNowLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        // 위도, 경도
        val uLatitude = userNowLocation?.latitude
        val uLongitude = userNowLocation?.longitude
        //val uNowPosition = MapPoint.mapPointWithGeoCoord(uLatitude!!, uLongitude!!)
        Log.d("activity2", "latitude : $uLatitude, longitude : $uLongitude")
        // 중심점 변경
        //map.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(uLatitude!!, uLongitude!!), true)
        val uNowPosition = MapPoint.mapPointWithGeoCoord(37.5828, 127.0106)
        map.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.5828, 127.0106), true)
        Log.d("location", "위도: $uLatitude 경도: $uLongitude")
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
    override fun onDestroy() {
        super.onDestroy()
        stopTasking()
    }

    private fun requestLocationUpdates() {
        val locationManager = activity?.getSystemService(LOCATION_SERVICE) as LocationManager

        val locationListener = LocationListener { location ->
            val latitude = location.latitude
            val longitude = location.longitude
            Log.d("activity", "latitude : $latitude, longitude : $longitude")
        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            1000L,
            10.0F,
            locationListener
        )
    }

    override fun onMapViewInitialized(p0: MapView?) {
       Log.d("map", "onMapViewInitialized")
    }

    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {
        Log.d("map", "onMapViewCenterPointMoved")
    }

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {
        Log.d("map", "onMapViewZoomLevelChanged")
    }

    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {
        Log.d("map", "onMapViewSingleTapped")
        map.removePOIItem(marker)
    }

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {
        Log.d("map", "onMapViewDoubleTapped")
    }

    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {
        Log.d("map", "onMapViewLongPressed")
    }

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {
        Log.d("map", "onMapViewDragStarted")
    }

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {
        Log.d("map", "onMapViewDragEnded")
    }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {
        Log.d("map", "onMapViewMoveFinished")
    }
}