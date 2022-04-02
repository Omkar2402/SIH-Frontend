package com.example.sihfrontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    private EditText email;
    private Button verify;
    private String otp = null;

    float v=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        try {
            email = findViewById(R.id.etemailAddress);
            verify = findViewById(R.id.btnVerifyEmail);

            email.setTranslationX(800);
            verify.setTranslationX(800);

            email.setAlpha(v);
            verify.setAlpha(v);

            email.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(300).start();
            verify.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(600).start();


            verify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(),"Email sent please check",Toast.LENGTH_SHORT).show();
                    if(email.getText().toString().isEmpty()){
                        Toast.makeText(MainActivity.this,"Please enter your email",Toast.LENGTH_SHORT).show();
                    }else{
                        //Intent verifyIntent = new Intent(MainActivity.this, VerifyEmail.class);
                        try {
                            //Map<String,String> map = new HashMap<>();
                            //map.put("email",email.getText().toString());

                            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(500000, TimeUnit.MILLISECONDS).build();
                            RequestBody formBody = new FormBody.Builder()
                                    .add("email", email.getText().toString())
                                    .build();

                            Request request = new Request.Builder()
                                    .url(getString(R.string.api)+"/verify-email")
                                    .post(formBody)
                                    .build();




                            Log.d("Before Response",request.toString());

                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    e.printStackTrace();

                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    if(response.isSuccessful()){
                                        try {
                                            JSONObject jsonObject = new JSONObject(response.body().string());
                                            String message = jsonObject.getString("message");
                                            otp = jsonObject.getString("otp");

                                            Log.d("message",message);
                                            Log.d("otp",otp);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                            Log.d("In catch",""+e.getMessage());
                                        }
                                        MainActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (otp.equals("null")) {
                                                    Toast.makeText(getApplicationContext(), "This email id has been blocked due to fake booking of tickets...", Toast.LENGTH_SHORT).show();
                                                }
                                                else if(otp!=null){
                                                    Intent verifyIntent = new Intent(getApplicationContext(), OTPVerification.class);
                                                    verifyIntent.putExtra("email",email.getText().toString());
                                                    verifyIntent.putExtra("otp",otp);
                                                    startActivity(verifyIntent);

                                                }

                                                else{
                                                    Toast.makeText(MainActivity.this,"OTP not sent",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    }
                                }
                            });


                        }catch (Exception e){
                            e.printStackTrace();
                            Log.d("In catch:",""+e.getMessage());
                        }

                    }
                }
            });

        }catch (Exception e) {
            e.printStackTrace();
        }




    }
}