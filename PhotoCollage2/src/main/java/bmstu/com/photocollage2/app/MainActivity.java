package bmstu.com.photocollage2.app;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.EditText;

import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends Activity implements OnClickListener {
    public static final String APIURL = "https://api.instagram.com/v1";

    public String client_id = "8b0252d91a364af39bf0e3c29dd7acb1";
//    public String client_secret = "77573e46dd57453083dbb7b258c8dbf4";
    private String username;
//    private String token="1438216001.8b0252d.0390a888a6b94881ac1364f90241d0d9";

    public ArrayList<String> imageUrls = new ArrayList<String>();
    public Intent intent;

    private ProgressDialog pDialog;

    public void getURL() throws IOException, JSONException {
        String urlString = "https://api.instagram.com/v1/users/search?q="+username+"&client_id="+client_id;
        String userId;

        // поиск пользователя
        InputStream inputStream = downloadJSON(urlString);
        String responseJSON = streamToString(inputStream);
        JSONObject jsonObject = (JSONObject) new JSONTokener(responseJSON).nextValue();
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        userId = jsonArray.getJSONObject(0).getString("id");

        //загрузка url
        String idString = APIURL + "/users/" + userId+ "/media/recent/?client_id="+client_id;
        InputStream inputStream2 = downloadJSON(idString);
        String responseJSON2 = streamToString(inputStream2);
        JSONObject jsonObject2 = (JSONObject) new JSONTokener(responseJSON2).nextValue();
        JSONArray jsonArray2 = jsonObject2.getJSONArray("data");
        JSONObject imageJsonObject;

        for (int i = 0; i < jsonArray2.length(); i++) {
            imageJsonObject = jsonArray2.getJSONObject(i).getJSONObject("images").getJSONObject("standard_resolution");
            imageUrls.add(imageJsonObject.getString("url"));
        }
    }


    class downloadURL extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading images. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
                try {
                    getURL();
                    intent = new Intent(MainActivity.this, PhotoSelectActivity.class);
                    intent.putExtra("imageUrls", imageUrls);
                    startActivity(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pDialog.dismiss();
        }
    }

    public boolean getInternetConnectionState(){
        ConnectivityManager  connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            return true;
        } else {
            return false;
        }
    }

    private InputStream downloadJSON(String stringUrl) throws IOException {
        URL url = new URL(stringUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(20000);
        connection.setConnectTimeout(20000);
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.connect();
        return connection.getInputStream();
    }

    private String streamToString(InputStream inputStream) throws IOException {
        String output = "";
        if(inputStream != null){
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            try{
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                while( (line = bufferedReader.readLine()) != null){
                    stringBuilder.append(line);
                }
                bufferedReader.close();
            }finally{
                inputStream.close();
            }
            output = stringBuilder.toString();
        }
        return output;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        //getInternetConnectionState();
    }

    @Override
    public void onClick(View arg0) {
        if ((username = ((EditText)findViewById(R.id.name)).getText().toString()).equals("")){
                Toast.makeText(this, "Введите ник", Toast.LENGTH_SHORT).show();
            } else {
            downloadURL downloadURL = new downloadURL();
            downloadURL.execute();

        }
    }
}