package com.example.openweathermapex2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    Button weatherButton;
    EditText cityText;
    TextView tempText, pressText, windText;

    private final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    private final String API_KEY = "817ad648c66c558ed428c26096a5bc62";
    OkHttpClient weatherClient;
    Request weatherRequest;
    Response weatherResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherButton   = findViewById(R.id.weatherButton);
        cityText        = findViewById(R.id.city);
        tempText        = findViewById(R.id.temper);
        pressText       = findViewById(R.id.press);
        windText        = findViewById(R.id.wind);

        weatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = cityText.getText().toString();
                //запрос должен быть отправлен в параллельном потомке!!!
                //1 способ - AsyncTask и метод для отправки запроса execute()
                // 2 способ - использование метода отправки enqueue()
                WeatherTask weatherTask = new WeatherTask();
                //if (weatherTask.getStatus() == AsyncTask.Status.FINISHED ||
                // weatherTask == null){
                weatherTask.execute(city);
                //}
            }
        });
    }

    private class WeatherTask extends AsyncTask<String, Void, Response> {

        @Override
        protected Response doInBackground(String... strings) {
            //подготовка запроса
            weatherClient = new OkHttpClient();
            //подготовка запроса с параментрами
            HttpUrl.Builder hub = HttpUrl.parse(WEATHER_URL).newBuilder();
            hub.addQueryParameter("q", strings[0]);
            hub.addQueryParameter("appid", API_KEY);
            hub.addQueryParameter("units", "metric");
            String url = hub.toString();
            weatherRequest = new Request.Builder().url(url).build();

            //отправка запроса
            try {
                weatherResponse = weatherClient.newCall(weatherRequest).execute();
                return weatherResponse;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            //try {
            if (response != null) {
                String resp = weatherResponse.headers().toString();
                //String resp = weatherResponse.body().string();
                try {
                    JSONObject mainJson = new JSONObject(response.body().string());
                    JSONObject main = mainJson.getJSONObject("main");
                    tempText.setText(Double.toString(main.getDouble("temp")));
                    pressText.setText(Integer.toString(main.getInt("pressure")));
                    JSONObject wind = main.getJSONObject("wind");
                    windText.setText(wind.toString());
                    /* достать из массива
                    JSONArray weather = mainJson.getJSONArray("weather");
                    JSONObject descr = weather.getJSONObject(0);*/
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e){
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(getApplicationContext(), "Ответ не порлучен", Toast.LENGTH_SHORT).show();
            }
            //} catch (IOException e) {
            //    e.printStackTrace();
            //}
        }
    }
}
