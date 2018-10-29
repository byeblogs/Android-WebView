package id.tokped.bye.hello;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import id.tokped.bye.hello.adapter.ArticleAdapter;
import id.tokped.bye.hello.adapter.HorizontalImageRecyclerViewAdapter;
import id.tokped.bye.hello.config.AppConfig;
import id.tokped.bye.hello.config.AppController;
import id.tokped.bye.hello.model.ArticleModel;
import id.tokped.bye.hello.model.PortalModel;

public class Portals extends AppCompatActivity{
    private static final String TAG = Portals.class.getSimpleName();

    RecyclerView horizontal_recycler_view;
    List<PortalModel> data_portal = new ArrayList<>();;
    HorizontalImageRecyclerViewAdapter horizontal_listview_adapter;

    ArticleAdapter article_adapter;
    List<ArticleModel> data_articles = new ArrayList<>();;
    RecyclerView article_recycler_view;
    LinearLayoutManager verticalLinearLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.portals_grid);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        if (toolbar == null) {
            throw new Error("Can't find tool bar, did you forget to add it in Activity layout file?");
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        Drawable drawable= getResources().getDrawable(R.mipmap.ic_menu_launcher);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 200, 200, true));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(newdrawable);


        article_recycler_view = (RecyclerView) findViewById(R.id.article_recycler_view);
        article_recycler_view.setNestedScrollingEnabled(false);

        SyncTaskGetArticles(this);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {

                        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.URL_PORTALS, new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONArray result_list =  new JSONObject(response).getJSONArray("result");
                                    try {
                                        for(int i = 0; i<result_list.length(); i++){
                                            JSONObject getObject = (JSONObject) result_list.get(i);

                                            PortalModel portal_item = new PortalModel();
                                            portal_item.setPortal_name(getObject.getString("portal_name"));
                                            portal_item.setPortal_init(getObject.getString("portal_init"));
                                            portal_item.setPortal_image(getObject.getString("portal_image"));
                                            data_portal.add(portal_item);
                                        }

                                        horizontal_recycler_view = (RecyclerView) findViewById(R.id.horizontal_recycler_view);
                                        horizontal_listview_adapter = new HorizontalImageRecyclerViewAdapter(Portals.this, data_portal);
                                        LinearLayoutManager horizontalLinearLayoutManager = new LinearLayoutManager(getBaseContext());
                                        horizontalLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);                                        horizontal_recycler_view.setLayoutManager(horizontalLinearLayoutManager);
                                        horizontal_recycler_view.setAdapter(horizontal_listview_adapter);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getBaseContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Get Portals List : " + error.toString());
                            }

                        });

                        // Adding request to request queue
                        AppController.getInstance().addToRequestQueue(strReq, "Get Portals List");

                    }
                }, 500);

        }


        article_adapter = new ArticleAdapter(this, data_articles);
        verticalLinearLayoutManager = new LinearLayoutManager(this);
        article_recycler_view.setLayoutManager(verticalLinearLayoutManager);
        article_recycler_view.setAdapter(article_adapter);

    }

    public void SyncTaskGetArticles(Context context){
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

                        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.URL_TOP_HEADLINES_CATEGORY("general", "us", "1", "20"), new Response.Listener<String>() {

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
                    if(i == 0){
                        article_item.setCategory("headlines");
                    } else {
                        article_item.setCategory("");
                    }
                    article_item.setAuthor(getObject.getString("author"));
                    article_item.setTitle(getObject.getString("title"));
                    article_item.setDescription(getObject.getString("description"));
                    article_item.setUrl(getObject.getString("url"));
                    article_item.setUrlToImage(getObject.getString("urlToImage").replace("\\",""));
                    article_item.setPublishedAt(getObject.getString("publishedAt"));
                    article_item.setContent(getObject.getString("content"));
                    data_articles.add(article_item);
                }

            }

            article_recycler_view.setLayoutManager(verticalLinearLayoutManager);
            article_recycler_view.setAdapter(article_adapter);
            article_adapter.notifyDataSetChanged();

            progressDialog.dismiss();

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_for_all, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent i = new Intent(this, ArticleSearch.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

	@Override
	public void onBackPressed() {
		// disable going back to the PortalDetails
		super.onBackPressed();
		overridePendingTransition(R.anim.stay, R.anim.slide_out_down);
	}
}
