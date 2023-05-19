package idv.tfp10104.login_homework.network;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

//參數為前端拿到的資料
public class JsonCallable implements Callable<String> {
    private final static String TAG = "TAG_JsonCallable";
    private final String url;
    private final String outStr;

    //建構子
    public JsonCallable(String url, String outStr) {
        this.url = url;
        this.outStr = outStr;
    }

    @Override
    public String call() throws Exception {
        return getData();
    }

    //跟後端連線,取得資料,回傳前端
    private String getData() {
        //跟server建立連線
        HttpURLConnection connection = null;
        StringBuilder inStr = new StringBuilder();

        try {
            // 建立與url連線
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true);//此連線是否允許接收資料
            connection.setDoOutput(true);//此連線是否允許送出資料

            // 不知道請求內容大小時可以呼叫此方法將請求內容分段傳輸，設定0代表使用預設大小
            connection.setChunkedStreamingMode(0);
            connection.setUseCaches(false); // do not use a cached copy（快取）
            connection.setRequestMethod("POST");//POST請求
            connection.setRequestProperty("content-type", "application/json");//資料內容：Json
            connection.setRequestProperty("charset", "UTF-8");//文字編碼
            //送出資料至後端                                                      //傳出前端的資料串流
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))) {
                bw.write(outStr);
                Log.d(TAG, "output" + outStr);
            }
            int responseCode = connection.getResponseCode();
            //代表成功收到server回傳資料
            if (responseCode == 200) {
                try (BufferedReader br = new BufferedReader(
                                                         //收到後端回傳的資料串流
                        new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    //一行一行讀
                    while ((line = br.readLine()) != null) {
                        inStr.append(line);
                    }
                }
            } else {
                Log.d(TAG, "response code: " + responseCode);
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        } finally {
            if (connection != null) {
                //關閉連線
                connection.disconnect();
            }
        }
        Log.d(TAG, "input: " + inStr);
        return inStr.toString();
    }
}
