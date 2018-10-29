package id.tokped.bye.hello;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.tokped.bye.hello.adapter.ArticleAdapter;
import id.tokped.bye.hello.config.AppConfig;
import id.tokped.bye.hello.config.AppController;
import id.tokped.bye.hello.model.ArticleModel;

public class ArticleSearch extends AppCompatActivity {
    private static final String TAG = ArticleSearch.class.getSimpleName();

    ArticleAdapter article_adapter;
    List<ArticleModel> data_articles = new ArrayList<>();;
    RecyclerView article_recycler_view;
    LinearLayoutManager verticalLinearLayoutManager;
    RecyclerViewEmptyRetryGroup mRecyclerViewEmptyRetryGroup;
    ProgressBar progress_bar;

    EditText plain_text_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_search);

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

        Drawable drawable= getResources().getDrawable(R.drawable.ic_chevron_left_white);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 150, 150, true));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(newdrawable);

        mRecyclerViewEmptyRetryGroup = (RecyclerViewEmptyRetryGroup) findViewById(R.id.recyclerViewEmptyRetryGroup);

        LinearLayout mEmptyView = (LinearLayout) findViewById(R.id.layout_empty);
        mEmptyView.setVisibility(View.GONE);
        LinearLayout mRetryView = (LinearLayout) findViewById(R.id.layout_retry);
        mRetryView.setVisibility(View.GONE);

        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);

        article_recycler_view = (RecyclerView) findViewById(R.id.article_recycler_view);
        article_recycler_view.setNestedScrollingEnabled(false);

        SyncTaskGetArticles("bitcoin");

        article_adapter = new ArticleAdapter(this, data_articles);
        article_recycler_view.setAdapter(article_adapter);
        verticalLinearLayoutManager = new LinearLayoutManager(this);
        article_recycler_view.setLayoutManager(verticalLinearLayoutManager);

        plain_text_search = (EditText) findViewById(R.id.plain_text_search);
        ImageButton button_search = (ImageButton) findViewById(R.id.button_search);
        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data_articles.clear();
                hideSoftKeyboard();
                SyncTaskGetArticles(plain_text_search.getText().toString());
            }
        });


        mRecyclerViewEmptyRetryGroup.setOnRetryClick(new RecyclerViewEmptyRetryGroup.OnRetryClick() {
            @Override
            public void onRetry() {
                if(!plain_text_search.getText().toString().equals("")) {
                    SyncTaskGetArticles(plain_text_search.getText().toString());
                }
            }
        });


    }
    public void hideSoftKeyboard () {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }

    public void SyncTaskGetArticles(final String keyword){
        Log.d(TAG, "Get Articles");

        progress_bar.setVisibility(View.VISIBLE);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {

                        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.URL_EVERYTHING_KEYWORD(keyword,"1", "10"), new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG, "Response: " + response);
                                try {
                                    JSONObject jObj = new JSONObject(response);
                                    String status = jObj.getString("status");
                                    if(status.equals("ok")){
                                        parseDataArticles(jObj);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getBaseContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                loadDataFailed();
                                Log.d(TAG, "Error Get Article List : " + error.toString());
                            }

                        });

                        AppController.getInstance().addToRequestQueue(strReq, "Get Article List");

                    }
                }, 3000);

    }

    private void parseDataArticles(JSONObject response){

        try {

            JSONArray value_array = response.getJSONArray("articles");

            for (int i = 0; i < value_array.length(); i++) {
                JSONObject getObject = (JSONObject) value_array.get(i);

                if (!getObject.isNull("urlToImage") && !getObject.isNull("content") && !getObject.isNull("author") &&  !getObject.isNull("description")) {
                    ArticleModel article_item = new ArticleModel();
                    if(i == 0){
                        article_item.setCategory("searching");
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
                    article_item.setKeyword(plain_text_search.getText().toString());
                    data_articles.add(article_item);
                }

            }

            article_adapter = new ArticleAdapter(this, data_articles);
            loadDataSuccess();

        } catch (Exception e){
            loadDataFailed();
            e.printStackTrace();
        }

    }


    private void loadDataFailed() {
        mRecyclerViewEmptyRetryGroup.loading();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progress_bar.setVisibility(View.VISIBLE);
                mRecyclerViewEmptyRetryGroup.retry();
            }
        }, 3000);
    }

    private void loadDataSuccess() {

        mRecyclerViewEmptyRetryGroup.loading();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                article_recycler_view.setAdapter(article_adapter);

                verticalLinearLayoutManager = new LinearLayoutManager(getBaseContext());
                verticalLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

                progress_bar.setVisibility(View.GONE);
                mRecyclerViewEmptyRetryGroup.success();
                article_adapter.notifyDataSetChanged();
            }
        }, 3000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_for_search, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.stay, R.anim.slide_out_down);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        super.onBackPressed();
        overridePendingTransition(R.anim.stay, R.anim.slide_out_down);
    }
}