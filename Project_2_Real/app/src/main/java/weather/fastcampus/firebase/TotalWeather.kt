package weather.fastcampus.firebase

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class TotalWeather(
    var main: Main? = null,
    var weatherList: ArrayList<Weather>? = null
): Serializable

class Main(
    var humidity: Int? = null,
    var pressure: Int? = null,
    var temp: Float? = null,
    @SerializedName("temp_max")
    var tempMax: Float? = null,
    @SerializedName("temp_min")
    var tempMin: Float? = null
): Serializable

class Weather(
    var description: String? = null,
    var icon: String? = null,
    var main: String? = null
): Serializable