package com.example.bot;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
        import android.widget.EditText;
        import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

        import androidx.appcompat.app.AppCompatActivity;
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

public class MainActivity extends AppCompatActivity {
    private RecyclerView chatsRV;
    private ImageView sendMsgIB;
    private EditText userMsgEdt;
    private final String USER_KEY = "user";
    private final String BOT_KEY = "bot";
    private RequestQueue mRequestQueue;
    private ArrayList<ModalData> messageModalArrayList;
    private MessageRVAdapter messageRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
try {
    chatsRV = findViewById(R.id.idRVChats);
    sendMsgIB = findViewById(R.id.send_to_bot);
    userMsgEdt = findViewById(R.id.sendmsg);
    getSupportActionBar().hide();

    mRequestQueue = Volley.newRequestQueue(MainActivity.this);
    mRequestQueue.getCache().clear();

    messageModalArrayList = new ArrayList<>();

    sendMsgIB.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (userMsgEdt.getText().toString().isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter your message..", Toast.LENGTH_SHORT).show();
                return;
            }
            //Toast.makeText(MainActivity.this, ""+userMsgEdt.getText().toString(), Toast.LENGTH_SHORT).show();
            sendMessage(userMsgEdt.getText().toString());
            userMsgEdt.setText("");
        }
    });
    messageRVAdapter = new MessageRVAdapter(messageModalArrayList, this);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);
    chatsRV.setLayoutManager(linearLayoutManager);
    chatsRV.setAdapter(messageRVAdapter);
    }catch (Exception e)
        {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private void sendMessage(String userMsg) {
        try {
            messageModalArrayList.add(new ModalData(USER_KEY,userMsg.trim()));
            messageRVAdapter.notifyDataSetChanged();
            String url = "http://api.brainshop.ai/get?bid=178058&key=dsIMphQgCEd8SArx&uid=[uid]&msg=" + userMsg.trim();
            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
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
}
