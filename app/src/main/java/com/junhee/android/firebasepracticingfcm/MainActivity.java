package com.junhee.android.firebasepracticingfcm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.junhee.android.firebasepracticingfcm.domain.Uid;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class MainActivity extends AppCompatActivity implements CustomAdapter.IParent {


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference uidRef = database.getReference("uid");

    RecyclerView recyclerView;
    TextView textReceiver;
    EditText editMessage;
    CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Log.e("Main", "FirebaseInstanceId === " + FirebaseInstanceId.getInstance().getToken());
//        FirebaseInstanceId.getInstance().getToken();
        setWidget();
        setRecyclerView();
        setFirebaseListener();
    }

    private void setWidget() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        textReceiver = (TextView) findViewById(R.id.textReceiver);
        editMessage = (EditText) findViewById(R.id.editMsg);
    }

    private void setRecyclerView() {
        adapter = new CustomAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // 파이어베이스 DB에 있는 data들을 불러오기 위해 함수를 만들었다.
    private void setFirebaseListener() {

        uidRef.addValueEventListener(new ValueEventListener() {
            // DataSanpshot 객체는 파이어베이스 내부에 존재하는 데이터를 언제든 읽을 수 있도록 설계된 instance이다.
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Uid> list = new ArrayList<Uid>();
                // dataSnapshot.getChildren()의 경우 배열에서 꺼내는 것처럼 하나씩 꺼내서
                // Uid 객체를 만들고, 만들어진 Uid 객체를 list 배열에 저장한다.
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    Uid uid = item.getValue(Uid.class);
                    list.add(uid);
                }
                // 그리고 아래 만든 refreshData(list); 함수를 통해 adapter에 데이터 최신화를 시켜준다.
                refreshData(list);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });

    }

    private void refreshData(List<Uid> data) {
        adapter.setData(data);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setToken(String token) {
        textReceiver.setText("메시지 수신자의 토큰 = " + token);

    }

    public interface FcmService {
        public static final String SERVER_URL = "http://172.30.1.48:8080/";

        @POST("send_notification")
        Call<Result> sendFcm(@Body Msg msg);
    }

    public class Msg {
        String token;
        String msg;
    }

    public class Result {
        String result_status;
    }

    public void sendMsg(View view) {
        String token = textReceiver.getText().toString();
        String msg = editMessage.getText().toString();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FcmService.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FcmService fcmService = retrofit.create(FcmService.class);

        Msg data = new Msg();
        data.token = token;
        data.msg = msg;

        Call<Result> result = fcmService.sendFcm(data);
        result.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.isSuccessful()) {
                    Result data = response.body();
                    Log.e("Result", "Result == " + data.result_status);
                } else {
                    Log.e("Result", "Error == " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

            }
        });


    }
}
