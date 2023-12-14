package com.example.bot;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView chatsRV;
    private DrawerLayout drawerLayout;
    private ImageView sendMsgIB,mic;
    TextToSpeech text = null;
    private final int Request_code = 100;
    private EditText userMsgEdt;
    private final String USER_KEY = "user";
    private final String BOT_KEY = "bot";
    private ImageView openDrawerButton;
    private TextView logout,scan_nav,chat_nav;
    private RequestQueue mRequestQueue;
    private ArrayList<ModalData> messageModalArrayList;
    private MessageRVAdapter messageRVAdapter;
    private String msg="chat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawer_layout1);
        openDrawerButton = findViewById(R.id.navigation_drawer);
        chatsRV = findViewById(R.id.idRVChats);
        sendMsgIB = findViewById(R.id.send_to_bot);
        userMsgEdt = findViewById(R.id.sendmsg);
        mic = findViewById(R.id.imageView);
        logout=findViewById(R.id.Logout);
        scan_nav=findViewById(R.id.Scan_navigation);
        chat_nav=findViewById(R.id.chat_option_navigation);
try {
    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    Objects.requireNonNull(getSupportActionBar()).hide();

    mRequestQueue = Volley.newRequestQueue(MainActivity.this);
    mRequestQueue.getCache().clear();
    messageModalArrayList = new ArrayList<>();
    text=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int i) {
            if(i != TextToSpeech.ERROR)
            {
                text.setLanguage(Locale.ENGLISH);
            }
        }
    });
    openDrawerButton.setOnClickListener(this);
    mic.setOnClickListener(this);
    sendMsgIB.setOnClickListener(this);
    logout.setOnClickListener(this);
    chat_nav.setOnClickListener(this);
    scan_nav.setOnClickListener(this);
    messageRVAdapter = new MessageRVAdapter(messageModalArrayList, this);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);
    chatsRV.setLayoutManager(linearLayoutManager);
    chatsRV.setAdapter(messageRVAdapter);
    }catch (Exception e)
        {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void speech_to_text() {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");
        try {
                startActivityForResult(i, Request_code);
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "Sorry! your device does not support speech input.", Toast.LENGTH_LONG).show();
            }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Request_code: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> res = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if(userMsgEdt.getText().toString()!="")
                    {
                        String s= userMsgEdt.getText().toString();
                        s+=res.get(0);
                        userMsgEdt.setText(res.get(0));
                    }
                    else{
                        userMsgEdt.setText(res.get(0));
                    }
                  //  Toast.makeText(getApplicationContext(), res.get(0), Toast.LENGTH_SHORT).show();
                  //  text.speak(res.get(0), TextToSpeech.QUEUE_FLUSH, null);
                }
                break;
            }
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private void sendMessage(String userMsg) {
        try {
            messageModalArrayList.add(new ModalData(USER_KEY,userMsg.trim()));
            messageRVAdapter.notifyDataSetChanged();
            String url = "http://api.brainshop.ai/get?bid=178058&key=dsIMphQgCEd8SArx&uid=[uid]&msg=" + userMsg.trim();
            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

//              API_ENDPOINT = "https://api-inference.huggingface.co/models/OpenAssistant/oasst-sft-4-pythia-12b-epoch-3.5";
//            StringRequest request = new StringRequest(Request.Method.POST, "https://us-central1-language.googleapis.com/v1/projects/clgbot-404610/locations/us-central1/models/bard:predict", new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response)
//                {
//                    Log.d("Volley", "Response: " + response);
//                    try {
//                        String botResponse = response;
//                        messageModalArrayList.add(new ModalData(BOT_KEY,botResponse));
//                        messageRVAdapter.notifyDataSetChanged();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        messageModalArrayList.add(new ModalData(BOT_KEY,"No response"));
//                        messageRVAdapter.notifyDataSetChanged();
//                    }
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Log.e("Volley", "Error: " + error.getMessage());
//                }
//            }){
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    Map<String, String> headers = new HashMap<>();
//                    headers.put("Authorization", "Bearer " + "APoG2W9sNkpwHsGgfNLMrJcJDgTL-3gT4a0n3IgIAMmI-mHbQ_zwZDT8Asff9HK1wtJNXJPx8A");
//                    headers.put("Content-Type", "application/json");
//                    return headers;
//                }
//                @Override
//                public byte[] getBody() throws AuthFailureError {
//                    JSONObject requestBody = new JSONObject();
//                    try {
//                        requestBody.put("inputs", new JSONArray().put(new JSONObject().put("text", "Write a poem about the beauty of nature.")));
//                    } catch (JSONException e) {
//                        throw new RuntimeException(e);
//                    }
//                    return requestBody.toString().getBytes();
//                }
//            };
//            queue.add(request);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String botResponse = response.getString("cnt");
                        messageModalArrayList.add(new ModalData(BOT_KEY,botResponse));
                        messageRVAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();

                        messageModalArrayList.add(new ModalData(BOT_KEY,"No response"));
                        messageRVAdapter.notifyDataSetChanged();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    messageModalArrayList.add(new ModalData(BOT_KEY,"Sorry no response found"));
                    //Toast.makeText(MainActivity.this, "No response from the bot..", Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("msg",error.getMessage());
                }
            });
            queue.add(jsonObjectRequest);
        }catch (Exception e)
        {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
    }
    @Override
    protected void onDestroy() {
        text.stop();
        text.shutdown();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if(view==openDrawerButton)
        {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        else if(view == mic)
        {
            speech_to_text();
        }
        else if(view==sendMsgIB)
        {
            if (userMsgEdt.getText().toString().isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter your message..", Toast.LENGTH_SHORT).show();
                return;
            }
            //Toast.makeText(MainActivity.this, ""+userMsgEdt.getText().toString(), Toast.LENGTH_SHORT).show();
            sendMessage(userMsgEdt.getText().toString());
            userMsgEdt.setText("");
        }
        else if(view == logout)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to Logout?");
            builder.setTitle("Alert !");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                finish();
               // overridePendingTransition(R.anim.swipe_in_left, R.anim.swipe_out_right);
            });
            builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                dialog.cancel();
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        else if(view == scan_nav)
        {
            Intent i = new Intent(getApplicationContext(),ScannerActivity.class);
            startActivity(i);
        }
    }
}