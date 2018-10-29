package id.tokped.bye.hello;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import id.tokped.bye.hello.adapter.ArticleAdapter;
import id.tokped.bye.hello.config.AppConfig;
import id.tokped.bye.hello.config.AppController;
import id.tokped.bye.hello.config.Reptyr;
import id.tokped.bye.hello.config.Rocket;
import id.tokped.bye.hello.model.ArticleModel;

public class PortalDetails extends AppCompatActivity{
    public static final String TAG = Rocket.class.getSimpleName();

    List<ArticleModel> data_articles = new ArrayList<>();;
    RecyclerView recycler_view;
    ArticleAdapter article_adapter;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.portal_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String portal_init = "";
        String portal_image = "";
        String portal_name = "";
        if(bundle != null){
            portal_init = bundle.getString("portal_init");
            portal_image = bundle.getString("portal_image");
            portal_name = bundle.getString("portal_name");
        }

        try {
            getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>"+portal_name+"</font>"));
            Bitmap bitmap = ((BitmapDrawable) Reptyr.drawableFromUrl(portal_image)).getBitmap();
            Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 200, 200, true));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(newdrawable);
        } catch (IOException e){
            e.printStackTrace();
        }

        SyncGetArticles(this, portal_init);

        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        recycler_view.setNestedScrollingEnabled(false);

        article_adapter = new ArticleAdapter(this, data_articles);

        linearLayoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(linearLayoutManager);
        recycler_view.setAdapter(article_adapter);

    }

    public void SyncGetArticles(Context context, final String portal_init){
        Log.d(TAG, "Get Articles");

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.show();
        progressDialog.setContentView(R.layout.my_progress);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {

                        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.URL_TOP_HEADLINES_SOURCES(portal_init), new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG, "Response: " + response);
                                try {
                                    JSONObject jObj = new JSONObject(response);
                                    String status = jObj.getString("status");
                                    if(status.equals("ok")){
                                        parseDataArticles(jObj, progressDialog);
                                    }

                                } catch (JSONException e) {
                                    progressDialog.dismiss();
                                    e.printStackTrace();
                                    Toast.makeText(getBaseContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();
                                Log.d(TAG, "Error Get Article List : " + error.toString());
                            }

                        });

                        // Adding request to request queue
                        AppController.getInstance().addToRequestQueue(strReq, "Get Article List");

                    }
                }, 3000);
    }

    private void parseDataArticles(JSONObject response, ProgressDialog progressDialog){

        try {

            JSONArray value_array = response.getJSONArray("articles");

            for (int i = 0; i < value_array.length(); i++) {
                JSONObject getObject = (JSONObject) value_array.get(i);

                if (!getObject.isNull("urlToImage") && !getObject.isNull("content") && !getObject.isNull("author") &&  !getObject.isNull("description")) {
                    ArticleModel article_item = new ArticleModel();
                    article_item.setAuthor(getObject.getString("author"));
                    article_item.setTitle(getObject.getString("title"));
                    article_item.setDescription(getObject.getString("description"));
                    article_item.setUrl(getObject.getString("url"));
                    article_item.setUrlToImage(getObject.getString("urlToImage").replace("\\",""));
                    article_item.setPublishedAt(getObject.getString("publishedAt"));
                    article_item.setContent(getObject.getString("content"));
                    System.out.print(getObject.getString("urlToImage"));
                    article_item.setCategory("");
                    data_articles.add(article_item);
                }

            }

            recycler_view.setLayoutManager(linearLayoutManager);
            recycler_view.setAdapter(article_adapter);
            article_adapter.notifyDataSetChanged();

            progressDialog.dismiss();

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        super.onBackPressed();
        overridePendingTransition(R.anim.stay, R.anim.slide_out_down);
    }

}
