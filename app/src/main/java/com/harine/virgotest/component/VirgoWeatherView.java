package com.harine.virgotest.component;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.harine.virgotest.Constants;
import com.harine.virgotest.R;
import com.harine.virgotest.bean.WeatherInfo;

import java.util.logging.LoggingMXBean;

/**
 * @author nepalese on 2021/3/11 17:59
 * @usage 天气显示
 * https://free-api.heweather.com/s6/weather/now?key=20ce3187f9664117b3236fdf72ac67cc&location=24.57287407,118.10086823
 *
 * {"HeWeather6":
 * [
 *        {
 * 	"basic":{"cid":"CN101230206","location":"集美","parent_city":"厦门",
 * 			"admin_area":"福建省","cnty":"中国","lat":"24.57287407",
 * 			"lon":"118.10086823","tz":"+8.00"},
 * 	"update":{"loc":"2021-03-12 11:47","utc":"2021-03-12 03:47"},
 * 	"status":"ok",
 * 	"now":{"cloud":"91","cond_code":"100","cond_txt":"晴","fl":"23",
 * 		"hum":"63","pcpn":"0.0","pres":"1001","tmp":"23","vis":"5",
 * 		"wind_deg":"270","wind_dir":"西风","wind_sc":"2","wind_spd":"9"}
 *    }
 * ]
 * }
 */
public class VirgoWeatherView extends RelativeLayout {
    private static final String TAG = "VirgoWeatherView";

    private Context context;
    private ImageView imgWeather;
    private TextView tvTemp;
    private TextView tvAddress;
    private TextView tvWeather;
    private TextView tvWind;

    private WeatherInfo weatherInfo;

    public VirgoWeatherView(Context context) {
        this(context, null);
    }

    public VirgoWeatherView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VirgoWeatherView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.layout_weather_view, this, true);
        init();
    }

    private void init() {
        tvTemp = findViewById(R.id.tvTemp);
        tvWeather = findViewById(R.id.tvWeather);
        tvWind = findViewById(R.id.tvAir);
        tvAddress = findViewById(R.id.tvAddress);
        imgWeather = findViewById(R.id.imgWeather);
    }

    public void setWeatherInfo(WeatherInfo weatherInfo) {
        this.weatherInfo = weatherInfo;
        updateInfo();
    }

    private void updateInfo() {
        if (weatherInfo!=null
                && weatherInfo.getHeWeather6()!=null
                &&weatherInfo.getHeWeather6().size()>0
                &&weatherInfo.getHeWeather6().get(0).getNow()!=null){

            String code = weatherInfo.getHeWeather6().get(0).getNow().getCond_code();
            String temp = weatherInfo.getHeWeather6().get(0).getNow().getTmp();
            String weather = weatherInfo.getHeWeather6().get(0).getNow().getCond_txt();
            String wind = weatherInfo.getHeWeather6().get(0).getNow().getWind_dir();
            String address = weatherInfo.getHeWeather6().get(0).getBasic().getLocation();

            setType(Integer.parseInt(code));
            updateTemp(temp);
            updateWeather(weather);
            updateWind(wind);
            updateAddress(address);
        }else {
            setDefaultValue();
        }
    }

    private void setDefaultValue() {
        setType(Constants.WEATHER_UNKNOWN_CODE);
        updateTemp("--");
        updateWeather("未知");
        updateWind("未知");
        updateAddress("未知");
    }

    private void updateTemp(String temp) {
        tvTemp.setText(context.getString(R.string.weather_temp, temp));
    }

    private void updateWeather(String weather) {
        tvWeather.setText(weather);
    }

    private void updateWind(String wind) {
        tvWind.setText(wind);
    }

    private void updateAddress(String address) {
        tvAddress.setText(address);
    }

    private void setType(int code) {
        switch (code){
            case Constants.WEATHER_SUNNY_CODE:
                imgWeather.setImageResource(R.mipmap.weather100);
                break;
            case Constants.WEATHER_CLOUDY_CODE:
                imgWeather.setImageResource(R.mipmap.weather101);
                break;
            case Constants.WEATHER_FEW_CLOUDS_CODE:
                imgWeather.setImageResource(R.mipmap.weather102);
                break;
            case Constants.WEATHER_PARTLY_CLOUDY_CODE:
                imgWeather.setImageResource(R.mipmap.weather103);
                break;
            case Constants.WEATHER_OVERCAST_CODE:
                imgWeather.setImageResource(R.mipmap.weather104);
                break;
            case Constants.WEATHER_WINDY_CODE:
                imgWeather.setImageResource(R.mipmap.weather200);
                break;
            case Constants.WEATHER_CALM_CODE:
                imgWeather.setImageResource(R.mipmap.weather201);
                break;
            case Constants.WEATHER_LIGHT_BREEZE_CODE:
                imgWeather.setImageResource(R.mipmap.weather200);
                break;
            case Constants.WEATHER_MODERATE_BREEZE_CODE:
                imgWeather.setImageResource(R.mipmap.weather200);
                break;
            case Constants.WEATHER_FRESH_BREEZE_CODE:
                imgWeather.setImageResource(R.mipmap.weather200);
                break;
            case Constants.WEATHER_STRONG_BREEZE_CODE:
                imgWeather.setImageResource(R.mipmap.weather205);
                break;
            case Constants.WEATHER_HIGH_WIND_CODE:
                imgWeather.setImageResource(R.mipmap.weather205);
                break;
            case Constants.WEATHER_GALE_CODE:
                imgWeather.setImageResource(R.mipmap.weather205);
                break;
            case Constants.WEATHER_STRONG_GALE_CODE:
                imgWeather.setImageResource(R.mipmap.weather208);
                break;
            case Constants.WEATHER_STORM_CODE:
                imgWeather.setImageResource(R.mipmap.weather208);
                break;
            case Constants.WEATHER_VIOLENT_STORM_CODE:
                imgWeather.setImageResource(R.mipmap.weather208);
                break;
            case Constants.WEATHER_HURRICANE_CODE:
                imgWeather.setImageResource(R.mipmap.weather101);
                break;
            case Constants.WEATHER_TORNADO_CODE:
                imgWeather.setImageResource(R.mipmap.weather208);
                break;
            case Constants.WEATHER_TROPICAL_STORM_CODE:
                imgWeather.setImageResource(R.mipmap.weather208);
                break;
            case Constants.WEATHER_SHOWER_RAIN_CODE:
                imgWeather.setImageResource(R.mipmap.weather300);
                break;
            case Constants.WEATHER_HEAVY_SHOWER_RAIN_CODE:
                imgWeather.setImageResource(R.mipmap.weather301);
                break;
            case Constants.WEATHER_THUNDERSHOWER_CODE:
                imgWeather.setImageResource(R.mipmap.weather302);
                break;
            case Constants.WEATHER_HEAVY_THUNDERSHOWER_CODE:
                imgWeather.setImageResource(R.mipmap.weather303);
                break;
            case Constants.WEATHER_THUNDERSHOWER_WITH_HAIL_CODE:
                imgWeather.setImageResource(R.mipmap.weather304);
                break;
            case Constants.WEATHER_LIGHT_RAIN_CODE:
                imgWeather.setImageResource(R.mipmap.weather305);
                break;
            case Constants.WEATHER_MODERATE_RAIN_CODE:
                imgWeather.setImageResource(R.mipmap.weather306);
                break;
            case Constants.WEATHER_HEAVY_RAIN_CODE:
                imgWeather.setImageResource(R.mipmap.weather307);
                break;
            case Constants.WEATHER_EXTREME_RAIN_CODE:
                imgWeather.setImageResource(R.mipmap.weather312);
                break;
            case Constants.WEATHER_DRIZZLE_RAIN_CODE:
                imgWeather.setImageResource(R.mipmap.weather309);
                break;
            case Constants.WEATHER_STORM_RAIN_CODE:
                imgWeather.setImageResource(R.mipmap.weather310);
                break;
            case Constants.WEATHER_HEAVY_STORM_CODE:
                imgWeather.setImageResource(R.mipmap.weather311);
                break;
            case Constants.WEATHER_SEVERE_STORM_CODE:
                imgWeather.setImageResource(R.mipmap.weather312);
                break;
            case Constants.WEATHER_FREEZING_RAIN_CODE:
                imgWeather.setImageResource(R.mipmap.weather313);
                break;
            case Constants.WEATHER_LIGHT_TO_MODERATE_RAIN_CODE:
                imgWeather.setImageResource(R.mipmap.weather314);
                break;
            case Constants.WEATHER_MODERATE_TO_HEAVY_RAIN_CODE:
                imgWeather.setImageResource(R.mipmap.weather307);
                break;
            case Constants.WEATHER_HEAVY_RAIN_TO_STORM_CODE:
                imgWeather.setImageResource(R.mipmap.weather310);
                break;
            case Constants.WEATHER_STORM_TO_HEAVY_STORM_CODE:
                imgWeather.setImageResource(R.mipmap.weather311);
                break;
            case Constants.WEATHER_HEAVY_TO_SEVERE_STORM_CODE:
                imgWeather.setImageResource(R.mipmap.weather312);
                break;
            case Constants.WEATHER_RAIN_CODE:
                imgWeather.setImageResource(R.mipmap.weather399);
                break;
            case Constants.WEATHER_LIGHT_SNOW_CODE:
                imgWeather.setImageResource(R.mipmap.weather400);
                break;
            case Constants.WEATHER_MODERATE_SNOW_CODE:
                imgWeather.setImageResource(R.mipmap.weather401);
                break;
            case Constants.WEATHER_HEAVY_SNOW_CODE:
                imgWeather.setImageResource(R.mipmap.weather402);
                break;
            case Constants.WEATHER_SNOWSTORM_CODE:
                imgWeather.setImageResource(R.mipmap.weather403);
                break;
            case Constants.WEATHER_SLEET_CODE:
                imgWeather.setImageResource(R.mipmap.weather404);
                break;
            case Constants.WEATHER_RAIN_AND_SNOW_CODE:
                imgWeather.setImageResource(R.mipmap.weather405);
                break;
            case Constants.WEATHER_SHOWER_SNOW_CODE:
                imgWeather.setImageResource(R.mipmap.weather406);
                break;
            case Constants.WEATHER_SNOW_FLURRY_CODE:
                imgWeather.setImageResource(R.mipmap.weather407);
                break;
            case Constants.WEATHER_LIGHT_TO_MODERATE_SNOW_CODE:
                imgWeather.setImageResource(R.mipmap.weather401);
                break;
            case Constants.WEATHER_MODERATE_TO_HEAVY_SNOW_CODE:
                imgWeather.setImageResource(R.mipmap.weather402);
                break;
            case Constants.WEATHER_HEAVY_SNOW_TO_SNOWSTORM_CODE:
                imgWeather.setImageResource(R.mipmap.weather403);
                break;
            case Constants.WEATHER_SNOW_CODE:
                imgWeather.setImageResource(R.mipmap.weather499);
                break;
            case Constants.WEATHER_MIST_CODE:
                imgWeather.setImageResource(R.mipmap.weather500);
                break;
            case Constants.WEATHER_FOGGY_CODE:
                imgWeather.setImageResource(R.mipmap.weather501);
                break;
            case Constants.WEATHER_HAZE_CODE:
                imgWeather.setImageResource(R.mipmap.weather502);
                break;
            case Constants.WEATHER_SAND_CODE:
                imgWeather.setImageResource(R.mipmap.weather503);
                break;
            case Constants.WEATHER_DUST_CODE:
                imgWeather.setImageResource(R.mipmap.weather504);
                break;
            case Constants.WEATHER_DUSTSTORM_CODE:
                imgWeather.setImageResource(R.mipmap.weather507);
                break;
            case Constants.WEATHER_SANDSTORM_CODE:
                imgWeather.setImageResource(R.mipmap.weather508);
                break;
            case Constants.WEATHER_DENSE_FOG_CODE:
                imgWeather.setImageResource(R.mipmap.weather509);
                break;
            case Constants.WEATHER_STRONG_FOG_CODE:
                imgWeather.setImageResource(R.mipmap.weather509);
                break;
            case Constants.WEATHER_MODERATE_HAZE_CODE:
                imgWeather.setImageResource(R.mipmap.weather511);
                break;
            case Constants.WEATHER_HEAVY_HAZE_CODE:
                imgWeather.setImageResource(R.mipmap.weather512);
                break;
            case Constants.WEATHER_SEVERE_HAZE_CODE:
                imgWeather.setImageResource(R.mipmap.weather513);
                break;
            case Constants.WEATHER_HEAVY_FOG_CODE:
                imgWeather.setImageResource(R.mipmap.weather509);
                break;
            case Constants.WEATHER_EXTRA_HEAVY_FOG_CODE:
                imgWeather.setImageResource(R.mipmap.weather509);
                break;
            case Constants.WEATHER_HOT_CODE:
                imgWeather.setImageResource(R.mipmap.weather900);
                break;
            case Constants.WEATHER_COLD_CODE:
                imgWeather.setImageResource(R.mipmap.weather901);
                break;
            default:
                imgWeather.setImageResource(R.mipmap.weather999);
                break;
        }
    }
}