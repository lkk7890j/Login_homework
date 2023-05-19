package idv.tfp10104.login_homework;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import idv.tfp10104.login_homework.network.RemoteAccess;

public class MainActivity extends AppCompatActivity {

    private EditText edUsername, edPassword;
    private Button loginbt;
    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //獲取元件
        edUsername = findViewById(R.id.edUsername2);
        edPassword = findViewById(R.id.edPassword2);
        textView = findViewById(R.id.textView2);
        loginbt = findViewById(R.id.Loginbutton2);

        loginbt.setOnClickListener(v -> {
            //取得帳密              trim()：去除開頭和結尾的空格
            String user = edUsername.getText().toString().trim();
            String password = edPassword.getText().toString().trim();
            //帳密長度不能小於等於0
            if (user.length() <= 0 || password.length() <= 0) {
                textView.setText("帳號或密碼不能為空");
                return;
            }
            if (isUserValid(user, password)) {

                //如果帳密比對正確顯示ok
                textView.setText("Welcome "+user);
            } else {
                textView.setText("帳號或密碼錯誤");
            }
        });

    }


    private boolean isUserValid(String name, String password) {
        //確認有無連線傳入Context物件
        if (RemoteAccess.networkConnected(this)) {
            //欲連線之server完整地址（後面加欲連線之servlet)
            String url = RemoteAccess.URL + "LoginCheck";
            //欲傳送到後端的資料用JSON格式（用ＧSON去傳遞）
            JsonObject jsonObject = new JsonObject();
            //準備要送入server端的資料
            jsonObject.addProperty("name", name);
            jsonObject.addProperty("password", password);
            boolean isUserValid;
            //新開執行緒 傳入server網址 和 jsonObject物件
            String jsonIn = RemoteAccess.getRemoteDate(url, jsonObject.toString());
            jsonObject = new Gson().fromJson(jsonIn, JsonObject.class);
            isUserValid = jsonObject.get("isUserValid").getAsBoolean();
            return isUserValid;
        } else {
            Toast.makeText(this, "no network connection available", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}