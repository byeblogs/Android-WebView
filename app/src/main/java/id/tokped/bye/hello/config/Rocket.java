package id.tokped.bye.hello.config;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import id.tokped.bye.hello.R;
import id.tokped.bye.hello.helper.RoundedImageView;

public class Rocket extends AppCompatActivity {

	public static final String TAG = Rocket.class.getSimpleName();

	public static class AsyncGetArticles extends AsyncTask<String, String, Reptyr.AsyncTaskResult<JSONObject>> {

		public static final String REQUEST_METHOD = "POST";
		public static final int READ_TIMEOUT = 15000;
		public static final int CONNECTION_TIMEOUT = 15000;

		Callback listener;
		Context context;

		public AsyncGetArticles(Callback listener, Context context) {  // can take other params if needed
			this.listener = listener;
			this.context = context;
		}

		@Override
		protected Reptyr.AsyncTaskResult<JSONObject> doInBackground(String... params) {

			try {


				JSONObject result_object = null;

				try {
                    String url = params[0];

					//Create a URL object holding our url
					URL myUrl = new URL(url);

					JSONObject postDataParams = new JSONObject();

					Log.d(TAG, "Params AsyncTaskJobsList" + postDataParams.toString());

					//Create a connection
					HttpURLConnection connection =(HttpURLConnection) myUrl.openConnection();

					//Set methods and timeouts
					connection.setRequestMethod(REQUEST_METHOD);
					connection.setReadTimeout(READ_TIMEOUT);
					connection.setConnectTimeout(CONNECTION_TIMEOUT);

					OutputStream os = connection.getOutputStream();
					BufferedWriter writer = new BufferedWriter(
							new OutputStreamWriter(os, "UTF-8"));
					writer.write(Reptyr.getPostDataString(postDataParams));

					writer.flush();
					writer.close();
					os.close();

					int responseCode = connection.getResponseCode();

					if (responseCode == HttpsURLConnection.HTTP_OK) {

						BufferedReader in=new BufferedReader(new InputStreamReader(connection.getInputStream()));

						StringBuffer sb = new StringBuffer("");
						String line="";

						while((line = in.readLine()) != null) {

							sb.append(line);
							break;
						}

						in.close();
						result_object = new JSONObject(sb.toString());

					}
					else {
						result_object = null;
					}

				}
				catch(IOException | JSONException e){
					e.printStackTrace();
				}

				// put your JSONObject here after fetch from the server
				return new Reptyr.AsyncTaskResult<JSONObject>(result_object);

			} catch ( Exception anyError) {
				return new Reptyr.AsyncTaskResult<JSONObject>(anyError);
			}
		}

		@Override
		protected void onPreExecute() {
		}

		protected void onPostExecute(Reptyr.AsyncTaskResult<JSONObject> result) {

			if ( result.getError() != null ) {
				// error handling here
				Log.d(TAG, "errorIn" + this.getClass().getSimpleName());
			} else if ( isCancelled()) {
				// cancel handling here
				Log.d(TAG, "cancelledIn" + this.getClass().getSimpleName());
			} else if(result.getResult()!=null){
				listener.onResultReceived(result.getResult());
			} else {
				// null handling here
				Log.d(TAG, "nullHandling" + this.getClass().getSimpleName());
			}
		};

		@Override
		protected void onProgressUpdate(String... text) {
			// Current Updated
		}

		public interface Callback {
			void onResultReceived(JSONObject result);
		}

	};

    public static class AsyncTaskLoadImage extends AsyncTask<String, String, Bitmap> {

        private ImageView imageView;
        public AsyncTaskLoadImage(ImageView imageView) {
            this.imageView = imageView;
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                URL url = new URL(params[0]);
                bitmap = BitmapFactory.decodeStream((InputStream)url.getContent());
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
            return bitmap;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }

    public static class AsyncTaskLoadImageCustomView extends AsyncTask<String, String, Bitmap> {

        private RoundedImageView imageView;
        public AsyncTaskLoadImageCustomView(RoundedImageView imageView) {
            this.imageView = imageView;
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                URL url = new URL(params[0]);
                bitmap = BitmapFactory.decodeStream((InputStream)url.getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }

    public static class AsyncTaskLoadImagePortalView extends AsyncTask<String, String, Bitmap> {

        private ImageView imageView;
        public AsyncTaskLoadImagePortalView(ImageView imageView) {
            this.imageView = imageView;
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                URL url_params = new URL(params[0]);
                Log.d(TAG, params[0]);
                if(!url_params.equals("null") && url_params!=null) {
                    bitmap = Reptyr.getCircleBitmap(BitmapFactory.decodeStream((InputStream) url_params.getContent()));
                } else {
                    bitmap = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.my_face);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "Processing...");
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            imageView.setImageBitmap(bitmap);

        }
    }


}
