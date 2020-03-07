package weather.fastcampus.firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_open_weather.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OpenWeatherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_weather)

        setting.setOnClickListener{
            startActivity(Intent(this, AccountSettingActivity::class.java))
            requestCurrentWeather()
        }
    }

    private fun requestCurrentWeather() {
        (application as WeatherApplication)
            .requestService()
            ?.getWeatherInfoOfLocation("London", "9fe2ff7cadd1568739a9aa0b4e0d15c4")
            ?.enqueue(object : Callback<TotalWeather> {
                override fun onFailure(call: Call<TotalWeather>, t: Throwable) {

                }

                override fun onResponse(call: Call<TotalWeather>, response: Response<TotalWeather>) {
                    var totalWeather = response.body()
                    Log.d("testt", "main: " + totalWeather?.main?.temp)
                }
            })
    }
}
