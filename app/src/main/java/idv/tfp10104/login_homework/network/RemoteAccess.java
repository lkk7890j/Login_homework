package idv.tfp10104.login_homework.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import java.util.Locale;
import java.util.concurrent.FutureTask;

public class RemoteAccess {
    //方便log查找
    private static final String TAG = "RemoteAccess";

    // 實機或模擬器
    // public final static String URL = "http://192.168.196.157:8080/eclipse要連的專案名稱/";

    //官方模擬器連線
    public static final String URL = "http://10.0.2.2:8080/login_homwork/";

    //把要連的網址跟目的地傳進來 開執行緒
    public static String getRemoteDate(String url, String outStr) {
        //建立callable物件(新開執行緒）
        JsonCallable callable = new JsonCallable(url, outStr);
        //FutureTask實作Runnable介面,藉由FutureTask把callable轉成Runnable
        FutureTask<String> task = new FutureTask<>(callable);
        //Runnable物件藉由Thread物件來開啟執行緒,因只有Thread類有start()方法
        Thread thread = new Thread(task);
        //會呼叫call方法
        thread.start();
        try {
            //等新執行緒拿到資料回傳（主執行緒呼叫task.get()就要等）
            return task.get();
        } catch (Exception e) {
            task.cancel(true);
            return "";
        }
    }


    //確認網路是否線
    public static boolean networkConnected(Context context) {
//取得系統資源去做檢查
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
//比對Android版次:新版
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //API 23 支援getActiveNetwork();取得現在正在連線的網路
                Network network = connectivityManager.getActiveNetwork();
                //API 21 支援getNetworkCapabilities();取得可以連線的資訊，判斷連線方式
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                if (networkCapabilities != null) {
                    String msg = String.format(Locale.getDefault(),
                            "TRANSPORT_WIFI: %b%nTRANSPORT_CELLULAR: %b%nTRANSPORT_ETHERNET: %b%n",
                            //檢查連線方式（WIFI）
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI),
                            //檢查連線方式（手機基地台）
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR),
                            //檢查連線方式（有線網路）
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
                    //用log確認值有無正確
                    Log.d(TAG, msg);
                    //不等於空值回傳其中一種 ture/false
                    return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
                }

            } else {
                // API 29將NetworkInfo列為deprecated:舊版
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected();
            }
        }
        //都沒連上回傳false
        return false;
    }


}