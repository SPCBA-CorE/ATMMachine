package com.example.sergedelossantos.atmmachine;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sergedelossantos.atmmachine.services.ATMService;
import com.example.sergedelossantos.atmmachine.services.ServiceManager;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private RequestQueue queue;
    private ProgressDialog pDialog;
    private TextView txtMessage;
    private Button btnYes, btnNo;
    private String cardid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queue = Volley.newRequestQueue(this);
        //Start service

        txtMessage = (TextView)findViewById(R.id.txtMessage);
        btnYes = (Button)findViewById(R.id.btnYes);
        btnNo = (Button)findViewById(R.id.btnNo);

        txtMessage.setVisibility(View.INVISIBLE);
        btnYes.setVisibility(View.INVISIBLE);
        btnNo.setVisibility(View.INVISIBLE);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Processing request...");
        pDialog.setCancelable(false);
        Intent intent = new Intent(this, ATMService.class);

        startService(intent);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    public void onReceive(Context context, Intent intent) {
                        boolean isconfirmed = intent.getBooleanExtra("isconfirmed", false);
                        cardid = intent.getStringExtra("cardid");

                        if(isconfirmed){
                            txtMessage.setVisibility(View.VISIBLE);
                            btnYes.setVisibility(View.VISIBLE);
                            btnNo.setVisibility(View.VISIBLE);

                            btnYes.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ClaimMoney(cardid);
                                    txtMessage.setVisibility(View.INVISIBLE);
                                    btnYes.setVisibility(View.INVISIBLE);
                                    btnNo.setVisibility(View.INVISIBLE);
                                }
                            });

                            btnNo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    CancelWithdrawal(cardid);
                                    txtMessage.setVisibility(View.INVISIBLE);
                                    btnYes.setVisibility(View.INVISIBLE);
                                    btnNo.setVisibility(View.INVISIBLE);
                                }
                            });
                        }

                    }
                }, new IntentFilter(ATMService.ACTION_ATM_BROADCAST)
        );
    }

    private void ClaimMoney(final String cardid){

        showpDialog();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                "http://unionbankphils.gear.host/api/Bank/ClaimMoney?cardid="+cardid,
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Parsing json object response
                            // response will be a json object
                            boolean IsSuccess = response.getBoolean("ResultBoolean");

                            if(IsSuccess){
                                Toast.makeText(getApplicationContext(),
                                        "Thank you for banking with us",
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                        hidepDialog();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                hidepDialog();
            }
        });

        // Adding request to request queue
        queue.add(jsonObjReq);
    }

    private void CancelWithdrawal(String cardid){
        showpDialog();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                "http://unionbankphils.gear.host/api/Bank/CancelWithdrawal?cardid="+cardid,
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Parsing json object response
                            // response will be a json object
                            boolean IsSuccess = response.getBoolean("ResultBoolean");

                            if(IsSuccess){
                                Toast.makeText(getApplicationContext(),
                                        "Transaction cancelled",
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                        hidepDialog();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                hidepDialog();
            }
        });

        // Adding request to request queue
        queue.add(jsonObjReq);
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
