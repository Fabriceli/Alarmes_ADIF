package utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Fabrice on 8/27/2015.
 */
public class CheckReseau {
    Context context;
    public boolean CheckReseau(Context context){
    this.context = context;
    ConnectivityManager manger = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo info = manger.getActiveNetworkInfo();
    if(info!=null&&info.isConnected()){
        return true;
    }else{
        return false;
    }
    }
}
