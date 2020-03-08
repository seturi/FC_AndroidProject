package weather.fastcampus.firebase

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_open_weather.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OpenWeatherActivity : AppCompatActivity(), LocationListener{

    private val PERMISSION_REQUEST_CODE = 2000
    private val APP_ID = "9fe2ff7cadd1568739a9aa0b4e0d15c4"
    private val UNITS = "metric"
    private val LANGUAGE = "kr"
    private lateinit var backPressHolder: OnBackPressHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_weather)

        backPressHolder = OnBackPressHolder(this@OpenWeatherActivity)
        getLocationInfo()

        setting.setOnClickListener{
            startActivity(Intent(this, AccountSettingActivity::class.java))

        }
    }

    override fun onBackPressed() {
        backPressHolder.onBackPressed()
    }

    private fun drawCurrentWeather(currentWeather : TotalWeather) {
        with(currentWeather) {
            this.weatherList?.getOrNull(0)?.let{
                it.icon?.let{
                    val glide = Glide.with(this@OpenWeatherActivity)
                    glide
                        .load(Uri.parse("https://openweathermap.org/img/w/" + it + ".png"))
                        .into(current_icon)
                }
                it.main?.let { current_main.text = it }
                it.description?.let{ current_description.text = it }
            }
            this.main?.temp?.let{ current_now.text = String.format("%.1f", it) }
            this.main?.tempMax?.let{ current_max.text = String.format("%.1f", it) }
            this.main?.tempMin?.let{ current_min.text = String.format("%.1f", it) }

            loading_view.visibility = View.GONE
            weather_view.visibility = View.VISIBLE
        }
    }



    private fun getLocationInfo() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(
                this@OpenWeatherActivity,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(
                this@OpenWeatherActivity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_CODE
            )
        } else {
            val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if(location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                Log.d("testt", "1 latitude : " + latitude)
                requestWeatherInfoOfLocation(latitude = latitude, longitude = longitude)
            } else {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    3000L,
                    0F,
                    this
                )
                locationManager.removeUpdates(this)
            }
        }
    }

    private fun requestWeatherInfoOfLocation(latitude : Double, longitude : Double) {
        (application as WeatherApplication)
            .requestService()
            ?.getWeatherInfoOfCoordinates(
                latitude = latitude,
                longitude = longitude,
                appID = APP_ID,
                units = UNITS,
                language = LANGUAGE

            )
            ?.enqueue(object :Callback<TotalWeather>{
                override fun onFailure(call: Call<TotalWeather>, t: Throwable) {
                    loading_text.text = "로딩 실패"
                }

                override fun onResponse(
                    call: Call<TotalWeather>,
                    response: Response<TotalWeather>
                ) {
                    if (response.isSuccessful) {
                        val totalWeather = response.body()
                        totalWeather?.let {
                            drawCurrentWeather(it)
                        }
                    } else loading_text.text = "로딩 실패"

                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PERMISSION_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) getLocationInfo()
        }
    }

    override fun onLocationChanged(location: Location?) {
        val latitude = location?.latitude
        val longitude = location?.longitude
        if(latitude != null && longitude != null) {
            Log.d("testt", "2 longitude: " + longitude)
            requestWeatherInfoOfLocation(latitude = latitude, longitude = longitude)
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String?) {
    }

    override fun onProviderDisabled(provider: String?) {
    }

    inner class OnBackPressHolder(val activity: Activity) {
        private var backPressHolder : Long = 0

        fun onBackPressed() {
            if(System.currentTimeMillis() > backPressHolder + 2000) {
                backPressHolder = System.currentTimeMillis()
                showBackTest()
                return
            }
            if(System.currentTimeMillis() <= backPressHolder + 2000) {
                finishAffinity()
            }
        }

        fun showBackTest() {
            Toast.makeText(this@OpenWeatherActivity, "한번 더 누르시면 종료합니다.", Toast.LENGTH_SHORT).show()
        }
    }

}
