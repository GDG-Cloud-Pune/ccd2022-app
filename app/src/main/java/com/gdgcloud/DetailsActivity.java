package com.gdgcloud;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gdgcloud.preferences.UserPreferences;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

public class DetailsActivity extends AppCompatActivity {

    static {
        System.loadLibrary("keys");
    }
    public native String GetApi();

    String key = new String(Base64.decode(GetApi(), Base64.DEFAULT));

    private FloatingActionButton fabScanner;
    private FirebaseAuth auth;
    private UserPreferences preferences;
    private TextView btnCheckIn,btnMorningTea,btnLunch,btnHighTea,btnSwags;
    private TextView txtCheckIn,txtMorningTea,txtLunch,txtHighTea,txtSwags,txtUserName,txtOrganizationName;
    private ImageView imageCheckIn,imageMorningTea,imageLunch,imageHighTea,imageSwags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        auth = FirebaseAuth.getInstance();
        preferences = new UserPreferences(this);
        txtOrganizationName = findViewById(R.id.txtOrganizationName);
        init();


        txtCheckIn.setVisibility(View.INVISIBLE);
        txtMorningTea.setVisibility(View.INVISIBLE);
        txtLunch.setVisibility(View.INVISIBLE);
        txtHighTea.setVisibility(View.INVISIBLE);
        txtSwags.setVisibility(View.INVISIBLE);

        imageCheckIn.setVisibility(View.INVISIBLE);
        imageMorningTea.setVisibility(View.INVISIBLE);
        imageLunch.setVisibility(View.INVISIBLE);
        imageHighTea.setVisibility(View.INVISIBLE);
        imageSwags.setVisibility(View.INVISIBLE);

        new GetData().execute("tickets",getIntent().getStringExtra("id"));

        fabScanner = findViewById(R.id.fabScanner);
        fabScanner.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        });
    }

    private void init(){
        txtUserName = findViewById(R.id.txtUserName);
        txtCheckIn = findViewById(R.id.txtCheck);
        txtMorningTea = findViewById(R.id.txtMorningTea);
        txtLunch = findViewById(R.id.txtLunch);
        txtHighTea = findViewById(R.id.txtHighTea);
        txtSwags = findViewById(R.id.txtSwag);

        imageCheckIn = findViewById(R.id.imageCheckedIn);
        imageMorningTea = findViewById(R.id.imageMorningTea);
        imageLunch = findViewById(R.id.imageLunch);
        imageHighTea = findViewById(R.id.imageHighTea);
        imageSwags = findViewById(R.id.imageSwags);

        btnCheckIn = findViewById(R.id.btnCheckIn);
        btnMorningTea = findViewById(R.id.btnMorningTea);
        btnLunch = findViewById(R.id.btnLunch);
        btnHighTea = findViewById(R.id.btnHighTea);
        btnSwags = findViewById(R.id.btnSwags);

    }

//    TODO ================= Sign Out Button =====================
    public void logOut(View v){
        preferences.logOut();
        auth.signOut();
        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        finish();

    }

//    TODO ================= AsyncTask Class get User Data =====================
    @SuppressLint("StaticFieldLeak")
    private class GetData extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            HttpsURLConnection urlConnection = null;
            String jsonData = "";
            try {
                URL url = new URL(key+"?collection="+strings[0] +
                        "&uid="+Long.parseLong(strings[1])+"&var=");

                urlConnection = (HttpsURLConnection) Objects.requireNonNull(url).openConnection();
                int data = urlConnection.getResponseCode();
                if (data != 200) {
                    throw new IOException("Invalid Response "+urlConnection.getResponseCode()+" \n "+url);
                }
                BufferedReader rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    Log.i("data", line);
                    jsonData = jsonData+line;
                }

            } catch (Exception e) {
                Log.e("Error", Objects.requireNonNull(e.getMessage()));
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return jsonData;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("response", s);
            try {
                JSONObject object = new JSONObject(s);
                String status = object.getString("status");
                int statusCode = object.getInt("statusCode");
                String ts = object.getString("ts");
                JSONObject data = object.getJSONObject("data");
                String id= data.getString("_id");
                String checkInStatus = data.getString("checkin");
                String organization = data.getString("organization");
                String name = data.getString("name");
                String morningTea = data.getString("foodmt");
                String lunch = data.getString("foodl");
                String highTea = data.getString("foodht");
                String swags = data.getString("swags");

                Log.d("onPostExecute: ", id);

                txtUserName.setText(name);
                txtOrganizationName.setText(organization);
                txtUserName.setVisibility(View.VISIBLE);
                txtOrganizationName.setVisibility(View.VISIBLE);
                txtOrganizationName.setAllCaps(true);

                if (checkInStatus.equals("true")){
                    txtCheckIn.setVisibility(View.VISIBLE);
                    imageCheckIn.setVisibility(View.VISIBLE);
                    btnCheckIn.setEnabled(false);
                    btnCheckIn.setBackgroundResource(R.drawable.btn_bg_drawable_cyan);
                    if (morningTea.equals("false")){
                        btnCheckIn.setEnabled(false);
                        btnCheckIn.setBackgroundResource(R.drawable.btn_bg_drawable_cyan);

                        btnMorningTea.setEnabled(true);
                        btnMorningTea.setBackgroundResource(R.drawable.btn_bg_drawable);
                        btnMorningTea.setOnClickListener(v -> {
                            new MorningTea().execute("tickets",id,"foodmt");
                        });
                    }else {
                        btnCheckIn.setEnabled(false);
                        btnCheckIn.setBackgroundResource(R.drawable.btn_bg_drawable_cyan);

                        btnMorningTea.setEnabled(false);
                        btnMorningTea.setBackgroundResource(R.drawable.btn_bg_drawable_cyan);

                        txtMorningTea.setVisibility(View.VISIBLE);
                        imageMorningTea.setVisibility(View.VISIBLE);

                        if (lunch.equals("false")){
                            btnCheckIn.setEnabled(false);
                            btnCheckIn.setBackgroundResource(R.drawable.btn_bg_drawable_cyan);

                            btnMorningTea.setEnabled(false);
                            btnMorningTea.setBackgroundResource(R.drawable.btn_bg_drawable_cyan);

                            btnLunch.setEnabled(true);
                            btnLunch.setBackgroundResource(R.drawable.btn_bg_drawable);

                            btnLunch.setOnClickListener(v -> {
                                new Lunch().execute("tickets", id, "foodl");
                            });
                        }else {
                            btnCheckIn.setEnabled(false);
                            btnCheckIn.setBackgroundResource(R.drawable.btn_bg_drawable_cyan);

                            btnMorningTea.setEnabled(false);
                            btnMorningTea.setBackgroundResource(R.drawable.btn_bg_drawable_cyan);

                            btnLunch.setEnabled(false);
                            btnLunch.setBackgroundResource(R.drawable.btn_bg_drawable_cyan);

                            txtLunch.setVisibility(View.VISIBLE);
                            imageLunch.setVisibility(View.VISIBLE);

                            if (highTea.equals("false")){
                                btnCheckIn.setEnabled(false);
                                btnCheckIn.setBackgroundResource(R.drawable.btn_bg_drawable_cyan);

                                btnMorningTea.setEnabled(false);
                                btnMorningTea.setBackgroundResource(R.drawable.btn_bg_drawable_cyan);

                                btnLunch.setEnabled(false);
                                btnLunch.setBackgroundResource(R.drawable.btn_bg_drawable_cyan);

                                btnHighTea.setEnabled(true);
                                btnHighTea.setBackgroundResource(R.drawable.btn_bg_drawable);

                                btnHighTea.setOnClickListener(v -> {
                                    new HighTea().execute("tickets", id, "foodht");
                                });

                            }else {
                                btnCheckIn.setEnabled(false);
                                btnCheckIn.setBackgroundResource(R.drawable.btn_bg_drawable_cyan);

                                btnMorningTea.setEnabled(false);
                                btnMorningTea.setBackgroundResource(R.drawable.btn_bg_drawable_cyan);

                                btnLunch.setEnabled(false);
                                btnLunch.setBackgroundResource(R.drawable.btn_bg_drawable_cyan);

                                btnHighTea.setEnabled(false);
                                btnHighTea.setBackgroundResource(R.drawable.btn_bg_drawable_cyan);

                                txtHighTea.setVisibility(View.VISIBLE);
                                txtHighTea.setVisibility(View.VISIBLE);

                                if (swags.equals("false")){
                                    btnCheckIn.setEnabled(false);
                                    btnCheckIn.setBackgroundResource(R.drawable.btn_bg_drawable_cyan);

                                    btnMorningTea.setEnabled(false);
                                    btnMorningTea.setBackgroundResource(R.drawable.btn_bg_drawable_cyan);

                                    btnLunch.setEnabled(false);
                                    btnLunch.setBackgroundResource(R.drawable.btn_bg_drawable_cyan);

                                    btnHighTea.setEnabled(false);
                                    btnHighTea.setBackgroundResource(R.drawable.btn_bg_drawable_cyan);

                                    btnSwags.setEnabled(true);
                                    btnSwags.setBackgroundResource(R.drawable.btn_bg_drawable);

                                    btnSwags.setOnClickListener(v -> new Swags().execute("tickets", id, "swags"));

                                }else {
                                    btnCheckIn.setEnabled(false);
                                    btnCheckIn.setBackgroundResource(R.drawable.btn_bg_drawable_cyan);

                                    btnMorningTea.setEnabled(false);
                                    btnMorningTea.setBackgroundResource(R.drawable.btn_bg_drawable_cyan);

                                    btnLunch.setEnabled(false);
                                    btnLunch.setBackgroundResource(R.drawable.btn_bg_drawable_cyan);

                                    btnHighTea.setEnabled(false);
                                    btnHighTea.setBackgroundResource(R.drawable.btn_bg_drawable_cyan);

                                    btnSwags.setEnabled(false);
                                    btnSwags.setBackgroundResource(R.drawable.btn_bg_drawable_cyan);

                                    txtSwags.setVisibility(View.VISIBLE);
                                    imageSwags.setVisibility(View.VISIBLE);

                                    Toast.makeText(DetailsActivity.this, "Completed", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                    }
                }else {
//                    btnCheckIn.setEnabled(true);
                    btnCheckIn.setBackgroundResource(R.drawable.btn_bg_drawable);
                    btnCheckIn.setOnClickListener(v -> {
                        if (!id.isEmpty()){
                            new CheckIn().execute("tickets",id,"checkin");
                        }else {
                            Toast.makeText(DetailsActivity.this, "Try again..!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }catch (JSONException e){
                Toast.makeText(DetailsActivity.this, "Try again", Toast.LENGTH_SHORT).show();
            }
        }
    }


//    TODO ================= AsyncTask Class User Check In =====================
    @SuppressLint("StaticFieldLeak")
    private class CheckIn extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            HttpsURLConnection urlConnection = null;
            String jsonData = "";
            try {
                URL url = new URL(key+"?collection="+strings[0] +
                        "&uid="+Long.parseLong(strings[1])+"&var="+strings[2]);

                urlConnection = (HttpsURLConnection) Objects.requireNonNull(url).openConnection();
                int data = urlConnection.getResponseCode();
                if (data != 200) {
                    throw new IOException("Invalid Response_check_in "+urlConnection.getResponseCode()+" \n "+url);
                }
                BufferedReader rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    Log.i("data_check_in", line);
                    jsonData = jsonData+line;
                }

            } catch (Exception e) {
                Log.e("Error_check_in", Objects.requireNonNull(e.getMessage()));
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return jsonData;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("response_check_in", s);
            try {
                JSONObject object = new JSONObject(s);
                String status = object.getString("status");
                int statusCode = object.getInt("statusCode");
                String ts = object.getString("ts");
                String data = object.getString("data");
                txtCheckIn.setVisibility(View.VISIBLE);
                imageCheckIn.setVisibility(View.VISIBLE);
                btnCheckIn.setBackgroundResource(R.drawable.btn_bg_drawable_cyan);

            }catch (JSONException e){
                Toast.makeText(DetailsActivity.this, "Try again", Toast.LENGTH_SHORT).show();
            }
        }
    }


//    TODO ================= AsyncTask Class User GET Morning Tea =====================
    @SuppressLint("StaticFieldLeak")
    private class MorningTea extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            HttpsURLConnection urlConnection = null;
            String jsonData = "";
            try {
                URL url = new URL(key+"?collection="+strings[0] +
                        "&uid="+Long.parseLong(strings[1])+"&var="+strings[2]);

                urlConnection = (HttpsURLConnection) Objects.requireNonNull(url).openConnection();
                int data = urlConnection.getResponseCode();
                if (data != 200) {
                    throw new IOException("Invalid Response "+urlConnection.getResponseCode()+" \n "+url);
                }
                BufferedReader rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    Log.i("data", line);
                    jsonData = jsonData+line;
                }

            } catch (Exception e) {
                Log.e("Error", Objects.requireNonNull(e.getMessage()));
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return jsonData;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("response", s);
            try {
                JSONObject object = new JSONObject(s);
                String status = object.getString("status");
                int statusCode = object.getInt("statusCode");
                String ts = object.getString("ts");
                String data = object.getString("data");

                Log.d("onPostExecute: ", status);

                txtMorningTea.setVisibility(View.VISIBLE);
                imageMorningTea.setVisibility(View.VISIBLE);

                btnMorningTea.setBackgroundResource(R.drawable.btn_bg_drawable_cyan);

            }catch (JSONException e){
                Toast.makeText(DetailsActivity.this, "Try again", Toast.LENGTH_SHORT).show();
            }
        }
    }


//    TODO ================= AsyncTask Class User GET Lunch =====================
    @SuppressLint("StaticFieldLeak")
    private class Lunch extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            HttpsURLConnection urlConnection = null;
            String jsonData = "";
            try {
                URL url = new URL(key+"?collection="+strings[0] +
                        "&uid="+Long.parseLong(strings[1])+"&var="+strings[2]);

                urlConnection = (HttpsURLConnection) Objects.requireNonNull(url).openConnection();
                int data = urlConnection.getResponseCode();
                if (data != 200) {
                    throw new IOException("Invalid Response "+urlConnection.getResponseCode()+" \n "+url);
                }
                BufferedReader rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    Log.i("data", line);
                    jsonData = jsonData+line;
                }

            } catch (Exception e) {
                Log.e("Error", Objects.requireNonNull(e.getMessage()));
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return jsonData;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("response", s);
            try {
                JSONObject object = new JSONObject(s);
                String status = object.getString("status");
                int statusCode = object.getInt("statusCode");
                String ts = object.getString("ts");
                String data = object.getString("data");

                Log.d("onPostExecute: ", status);

                txtLunch.setVisibility(View.VISIBLE);
                imageLunch.setVisibility(View.VISIBLE);

                btnLunch.setBackgroundResource(R.drawable.btn_bg_drawable_cyan);

            }catch (JSONException e){
                Toast.makeText(DetailsActivity.this, "Try again", Toast.LENGTH_SHORT).show();
            }
        }
    }


//    TODO ================= AsyncTask Class User GET High/Evening Tea =====================
    @SuppressLint("StaticFieldLeak")
    private class HighTea extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            HttpsURLConnection urlConnection = null;
            String jsonData = "";
            try {
                URL url = new URL(key+"?collection="+strings[0] +
                        "&uid="+Long.parseLong(strings[1])+"&var="+strings[2]);

                urlConnection = (HttpsURLConnection) Objects.requireNonNull(url).openConnection();
                int data = urlConnection.getResponseCode();
                if (data != 200) {
                    throw new IOException("Invalid Response "+urlConnection.getResponseCode()+" \n "+url);
                }
                BufferedReader rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    Log.i("data", line);
                    jsonData = jsonData+line;
                }

            } catch (Exception e) {
                Log.e("Error", Objects.requireNonNull(e.getMessage()));
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return jsonData;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("response", s);
            try {
                JSONObject object = new JSONObject(s);
                String status = object.getString("status");
                int statusCode = object.getInt("statusCode");
                String ts = object.getString("ts");
                String data = object.getString("data");

                Log.d("onPostExecute: ", status);

                txtHighTea.setVisibility(View.VISIBLE);
                imageHighTea.setVisibility(View.VISIBLE);

                btnHighTea.setBackgroundResource(R.drawable.btn_bg_drawable_cyan);

            }catch (JSONException e){
                Toast.makeText(DetailsActivity.this, "Try again", Toast.LENGTH_SHORT).show();
            }
        }
    }


//    TODO ================= AsyncTask Class User GET Swags/Gifts =====================
    @SuppressLint("StaticFieldLeak")
    private class Swags extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            HttpsURLConnection urlConnection = null;
            String jsonData = "";
            try {
                URL url = new URL(key+"?collection="+strings[0] +
                        "&uid="+Long.parseLong(strings[1])+"&var="+strings[2]);

                urlConnection = (HttpsURLConnection) Objects.requireNonNull(url).openConnection();
                int data = urlConnection.getResponseCode();
                if (data != 200) {
                    throw new IOException("Invalid Response "+urlConnection.getResponseCode()+" \n "+url);
                }
                BufferedReader rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    Log.i("data", line);
                    jsonData = jsonData+line;
                }

            } catch (Exception e) {
                Log.e("Error", Objects.requireNonNull(e.getMessage()));
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return jsonData;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("response", s);
            try {
                JSONObject object = new JSONObject(s);
                String status = object.getString("status");
                int statusCode = object.getInt("statusCode");
                String ts = object.getString("ts");
                String data = object.getString("data");

                Log.d("onPostExecute: ", status);

                txtSwags.setVisibility(View.VISIBLE);
                imageSwags.setVisibility(View.VISIBLE);

                btnSwags.setBackgroundResource(R.drawable.btn_bg_drawable_cyan);

            }catch (JSONException e){
                Toast.makeText(DetailsActivity.this, "Try again", Toast.LENGTH_SHORT).show();
            }
        }
    }


}