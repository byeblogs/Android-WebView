package id.tokped.bye.hello;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class SplashScreen extends Activity {

    private static int splashInterval = 1000;
    public static final int RequestPermissionCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.splash_screen);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                Intent i = new Intent(SplashScreen.this, Portals.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
                this.finish();

            }

            private void finish() {
                // TODO Auto-generated method stub

            }
        }, splashInterval);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}