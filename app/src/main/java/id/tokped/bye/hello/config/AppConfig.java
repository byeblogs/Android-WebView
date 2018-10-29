package id.tokped.bye.hello.config;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class AppConfig extends AppCompatActivity {
    private static final String TAG = AppConfig.class.getSimpleName();

    public static String API_KEY = "fc2d93f79005478f8d68ec5d7e9e68ad";

    public static String URL_BASE = "https://newsapi.org/v2/";

    public static String URL_PORTALS = "https://gist.githubusercontent.com/bye-webster/6d6e0d856f9d7c74ae1cd549f35ddb17/raw/67893c2095b681617a011d2783b6cafcef158fe3/data_news.json";

    public static String URL_TOP_HEADLINES_SOURCES(String portal_init){
        return URL_BASE+"top-headlines?sources="+portal_init+"&apiKey="+API_KEY;
    }

    public static String URL_EVERYTHING_KEYWORD(String keyword, String page, String pageSize){
        return URL_BASE+"everything?q="+keyword+"&apiKey="+API_KEY;
    }

    public static String URL_TOP_HEADLINES_CATEGORY(String category, String country, String page, String pageSize){
        return URL_BASE+"top-headlines?category="+category+"&page="+page+"&pageSize="+pageSize+"&country="+country+"&apiKey="+API_KEY;
    }

}
