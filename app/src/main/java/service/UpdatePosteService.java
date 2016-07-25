package service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.loopj.android.http.RequestParams;

import utils.CheckReseau;
import utils.NetCallBack;
import utils.RequestUtils;

public class UpdatePosteService extends Service {
    private boolean running = false;
    private Callback callback = null;
    private String ville;
    private String password,username;
    private String url;
    private CheckReseau checkReseau;
    public UpdatePosteService() {
    }
    @Override
    public void onCreate() {
        running = true;
        super.onCreate();
        new Thread(){
            @Override
            public void run() {
                checkReseau = new CheckReseau();
                RequestParams requestParams = new RequestParams();
                super.run();
                while (running){
                    if(checkReseau.CheckReseau(getApplicationContext())){
                        if(ville!=null && password!=null && username!=null){
                            requestParams.put("tag","alarmBands");
                            requestParams.put("username", username);
                            requestParams.put("password", password);
                            requestParams.put("city", ville);
                            RequestUtils.ClientPost(url, requestParams, new NetCallBack() {
                                @Override
                                public void onMySuccss(String result) throws Exception {
                                    //Log.e("RESULT: ", result);
                                    if (callback != null) {
                                        callback.DataChange(result);
                                    }
                                }

                                @Override
                                public void onMyFailure(Throwable throwable) {
                                    callback.DataChange("failure");
                                }
                            });

                        }else {
                            return;
                        }
                    }else{
                        return;
                    }
                    try {
                        sleep(10*1000);//
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ville = intent.getStringExtra("ville");
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");
        url = intent.getStringExtra("url");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        running = false;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    public class Binder extends android.os.Binder{
        public UpdatePosteService getService(){
            return UpdatePosteService.this;
        }
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }


    public static interface Callback{
        void DataChange(String result);
    }

}
