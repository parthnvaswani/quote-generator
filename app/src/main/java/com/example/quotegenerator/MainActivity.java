package com.example.quotegenerator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    TextView quote,author;
    Button generate,toggle,download;
    CardView quotecon;
    String quoteTxtEn="",quoteTxtSr="",currTxt="en";
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queue = Volley.newRequestQueue(this);
        quote=findViewById(R.id.quote);
        author=findViewById(R.id.author);
        generate=findViewById(R.id.generate);
        toggle=findViewById(R.id.toggle);
        download=findViewById(R.id.download);
        quotecon=findViewById(R.id.quotecon);

        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getQuote();
            }
        });

        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    toggleLanguage();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

//        download.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                download();
//            }
//        });

        getQuote();
    }

    public void  getQuote(){
        String url = "https://programming-quotes-api.herokuapp.com/Quotes/random";

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            quote.setText("\""+response.get("en").toString()+"\"");
                            author.setText("- "+response.get("author").toString());
                            quoteTxtEn = response.get("en").toString();
                            quoteTxtSr = "";
                            currTxt = "en";
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        queue.add(stringRequest);
    }

    public void toggleLanguage() throws JSONException {
        if (currTxt != "en") {
            quote.setText("\""+quoteTxtEn+"\"");
            currTxt = "en";
            return;
        }
        if (quoteTxtSr != "") {
            quote.setText("\""+quoteTxtSr+"\"");
            currTxt = "sr";
            return;
        }

        JSONObject obj=new JSONObject();
        obj.put("Text",quoteTxtEn);
        JSONArray arr=new JSONArray();
        arr.put(obj);

        JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.POST, "https://microsoft-translator-text.p.rapidapi.com/translate?to%5B0%5D=sr&api-version=3.0&profanityAction=NoAction&textType=plain", arr,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            String translated=response.getJSONObject(0).getJSONArray("translations").getJSONObject(0).get("text").toString();
                            quoteTxtSr = translated;
                            quote.setText("\""+quoteTxtSr+"\"");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        currTxt = "sr";
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        })
        {

            /** Passing some request headers* */
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-RapidAPI-Key", "100e891407msha115ea8e9402e94p1edb54jsnab3266b8b56e");
                headers.put("X-RapidAPI-Host", "microsoft-translator-text.p.rapidapi.com");
                return headers;
            }
        };

        queue.add(stringRequest);
    }

//    public void download(){
//        Bitmap b=getBitmapFromView(quotecon);
//        saveImage(b);
//    }

//    public static Bitmap getBitmapFromView(View v) {
//        Bitmap b = Bitmap.createBitmap(v.getWidth() , v.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas c = new Canvas(b);
//        v.draw(c);
//        return b;
//    }

//    private void saveImage(Bitmap finalBitmap) {
//        String root = Environment.getExternalStorageDirectory().toString();
//        File myDir = new File(root + "/saved_images");
//        myDir.mkdirs();
//
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String fname = "Shutta_"+ timeStamp +".jpg";
//
//        File file = new File(myDir, fname);
//        if (file.exists()) file.delete();
//        try {
//            FileOutputStream out = new FileOutputStream(file);
//            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//            out.flush();
//            out.close();
//        } catch (Exception e) {
//        }
//    }

}