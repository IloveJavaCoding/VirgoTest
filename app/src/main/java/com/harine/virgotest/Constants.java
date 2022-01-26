package com.harine.virgotest;

/**
 * @author nepalese on 2021/3/12 12:02
 * @usage
 */
public class Constants {

    public static final String WEATHER_API_URL = "https://free-api.heweather.com/s6/weather/now?key=20ce3187f9664117b3236fdf72ac67cc&location=";

    public static final String ACTION_UPDATE_TIME_LIST_IMAGE = "intent.action_update_time_list_image";
    public static final String ACTION_UPDATE_TIME_LIST_VIDEO = "intent.action_update_time_list_video";
    public static final String ACTION_UPDATE_TIME_LIST_PROGRAM = "intent.action_update_time_list_program";
    public static final String ACTION_UPDATE_TIME_LIST_TEXT = "intent.action_update_time_list_text";

    public static final int SCREEN_MIAN = 1;
    public static final int SCREEN_SECOND = 2;

    public static final int ALARM_REQUEST_CODE_IMAGE = 1;
    public static final int ALARM_REQUEST_CODE_VIDEO = 2;
    public static final int ALARM_REQUEST_CODE_PROGRAM = 3;
    public static final int ALARM_REQUEST_CODE_TEXT = 4;

    public static final int PROGRAM_TYPE_DIANPIAN = 0;
    public static final int PROGRAM_TYPE_CYCLE = 1;
    public static final int PROGRAM_TYPE_TIMING = 2;
    public static final int PROGRAM_TYPE_LOCATION = 3;
    public static final int PROGRAM_TYPE_URGENT = 4;
    public static final int PROGRAM_TYPE_MOST_URGENT = 5;

    //图片切换模式
    public static final int ANIM_TYPE_FADE = 0;//淡入淡出
    public static final int ANIM_TYPE_SCALE = 1;//中心缩放
    public static final int ANIM_TYPE_RL = 2;//右进左出
    public static final int ANIM_TYPE_LR = 3;//左进右出

    public static final String TYPE_BG = "Bg";
    public static final String TYPE_VIDEO = "Video";
    public static final String TYPE_VIDEO_IMAGE = "VideoImage";
    public static final String TYPE_STATIC_TEXT = "Text";
    public static final String TYPE_SCROLL_TEXT = "ScrollText";
    public static final String TYPE_IMP = "Imp";
    public static final String TYPE_DYNAMIC = "DynamicTable";
    public static final String TYPE_BUTTON = "Button";
    public static final String TYPE_WEB = "Web";
    public static final String TYPE_LOGO = "Logo";
    public static final String TYPE_TOUCH = "Touch";
    public static final String TYPE_TIME = "Time";
    public static final String TYPE_DATE = "Date";
    public static final String TYPE_MASK = "Mask";
    public static final String TYPE_WEATHER = "Weather";
    public static final String TYPE_DYNAMIC_IMAGE = "Image";
    public static final String TYPE_DYNAMIC_TABLE = "WForm";
    public static final String TYPE_STATION_LIST = "StationList";
    public static final String TYPE_STATION_ARRIVE_VIEW = "StationArriveView";
    public static final String TYPE_BOTTOM_VIEW = "BottomView";
    public static final String TYPE_LIVE = "Live";

    //服务器ip
    public static final String SERVER_IP = "http://192.168.0.101:8080/VirgoMusic_war/";
    public static final String SERVER_HEAD = "http://";
    public static final String SERVER_API = "/VirgoMusic_war/mobile/";
    public static final String DEFAULT_SERVER_IP = "192.168.2.151";
    public static final String DEFAULT_SERVER_PORT = "8080";

    //weather
    public static final int WEATHER_SUNNY_CODE = 100;
    public static final int WEATHER_CLOUDY_CODE = 101;
    public static final int WEATHER_FEW_CLOUDS_CODE = 102;
    public static final int WEATHER_PARTLY_CLOUDY_CODE = 103;
    public static final int WEATHER_OVERCAST_CODE = 104;
    public static final int WEATHER_WINDY_CODE = 200;
    public static final int WEATHER_CALM_CODE = 201;
    public static final int WEATHER_LIGHT_BREEZE_CODE = 202;
    public static final int WEATHER_MODERATE_BREEZE_CODE = 203;
    public static final int WEATHER_FRESH_BREEZE_CODE = 204;
    public static final int WEATHER_STRONG_BREEZE_CODE = 205;
    public static final int WEATHER_HIGH_WIND_CODE = 206;
    public static final int WEATHER_GALE_CODE = 207;
    public static final int WEATHER_STRONG_GALE_CODE = 208;
    public static final int WEATHER_STORM_CODE = 209;
    public static final int WEATHER_VIOLENT_STORM_CODE = 210;
    public static final int WEATHER_HURRICANE_CODE = 211;
    public static final int WEATHER_TORNADO_CODE = 212;
    public static final int WEATHER_TROPICAL_STORM_CODE = 213;
    public static final int WEATHER_SHOWER_RAIN_CODE = 300;
    public static final int WEATHER_HEAVY_SHOWER_RAIN_CODE = 301;
    public static final int WEATHER_THUNDERSHOWER_CODE = 302;
    public static final int WEATHER_HEAVY_THUNDERSHOWER_CODE = 303;
    public static final int WEATHER_THUNDERSHOWER_WITH_HAIL_CODE = 304;
    public static final int WEATHER_LIGHT_RAIN_CODE = 305;
    public static final int WEATHER_MODERATE_RAIN_CODE = 306;
    public static final int WEATHER_HEAVY_RAIN_CODE = 307;
    public static final int WEATHER_EXTREME_RAIN_CODE = 308;
    public static final int WEATHER_DRIZZLE_RAIN_CODE = 309;
    public static final int WEATHER_STORM_RAIN_CODE = 310;
    public static final int WEATHER_HEAVY_STORM_CODE = 311;
    public static final int WEATHER_SEVERE_STORM_CODE = 312;
    public static final int WEATHER_FREEZING_RAIN_CODE = 313;
    public static final int WEATHER_LIGHT_TO_MODERATE_RAIN_CODE = 314;
    public static final int WEATHER_MODERATE_TO_HEAVY_RAIN_CODE = 315;
    public static final int WEATHER_HEAVY_RAIN_TO_STORM_CODE = 316;
    public static final int WEATHER_STORM_TO_HEAVY_STORM_CODE = 317;
    public static final int WEATHER_HEAVY_TO_SEVERE_STORM_CODE = 318;
    public static final int WEATHER_RAIN_CODE = 399;
    public static final int WEATHER_LIGHT_SNOW_CODE = 400;
    public static final int WEATHER_MODERATE_SNOW_CODE = 401;
    public static final int WEATHER_HEAVY_SNOW_CODE = 402;
    public static final int WEATHER_SNOWSTORM_CODE = 403;
    public static final int WEATHER_SLEET_CODE = 404;
    public static final int WEATHER_RAIN_AND_SNOW_CODE = 405;
    public static final int WEATHER_SHOWER_SNOW_CODE = 406;
    public static final int WEATHER_SNOW_FLURRY_CODE = 407;
    public static final int WEATHER_LIGHT_TO_MODERATE_SNOW_CODE = 408;
    public static final int WEATHER_MODERATE_TO_HEAVY_SNOW_CODE = 409;
    public static final int WEATHER_HEAVY_SNOW_TO_SNOWSTORM_CODE = 410;
    public static final int WEATHER_SNOW_CODE = 499;
    public static final int WEATHER_MIST_CODE = 500;
    public static final int WEATHER_FOGGY_CODE = 501;
    public static final int WEATHER_HAZE_CODE = 502;
    public static final int WEATHER_SAND_CODE = 503;
    public static final int WEATHER_DUST_CODE = 504;
    public static final int WEATHER_DUSTSTORM_CODE = 507;
    public static final int WEATHER_SANDSTORM_CODE = 508;
    public static final int WEATHER_DENSE_FOG_CODE = 509;
    public static final int WEATHER_STRONG_FOG_CODE = 510;
    public static final int WEATHER_MODERATE_HAZE_CODE = 511;
    public static final int WEATHER_HEAVY_HAZE_CODE = 512;
    public static final int WEATHER_SEVERE_HAZE_CODE = 513;
    public static final int WEATHER_HEAVY_FOG_CODE = 514;
    public static final int WEATHER_EXTRA_HEAVY_FOG_CODE = 515;
    public static final int WEATHER_HOT_CODE = 900;
    public static final int WEATHER_COLD_CODE = 901;
    public static final int WEATHER_UNKNOWN_CODE = 999;
}
