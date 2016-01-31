package adifli.sql_json_v2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import service.UpdateVilleService;
import utils.CheckInternet;
import utils.NetCallBack;
import utils.RequestUtils;


public class Bienvenue extends Activity implements View.OnClickListener {
    public static String url ;
    private TextView pBar,pBil,pCha,pCor,pGra,pLe,pMa,pMan,pMi,pMur,pOr,pOv,pSan,pSe,pVa,pZa;
    private View r1_1,r1_2,r1_3,r2_1,r2_2,r2_3,r3_1,r3_2,r3_3,r3_4,r4_1,r4_2,r5_1,r5_2,r5_3,r5_4;
    private Dialog loadingDialog;
    private JSONObject pourcentbar;
    private String username,password;


    private Handler showVilleHandler = new Handler() {
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case 1://succes
                    try {
                        pourcentbar =new JSONObject((String) msg.obj);
                        pBar.setText(String.format("%s", pourcentbar.getString("barcelona")) + "%");
                        pBil.setText(String.format("%s", pourcentbar.getString("bilbao")) + "%");
                        pCha.setText(String.format("%s", pourcentbar.getString("chamartin")) + "%");
                        pCor.setText(String.format("%s", pourcentbar.getString("cordoba")) + "%");
                        pGra.setText(String.format("%s", pourcentbar.getString("granada")) + "%");
                        pLe.setText(String.format("%s", pourcentbar.getString("leon")) + "%");
                        pMa.setText(String.format("%s", pourcentbar.getString("malaga")) + "%");
                        pMan.setText(String.format("%s", pourcentbar.getString("manzanares")) + "%");
                        pMi.setText(String.format("%s", pourcentbar.getString("miranda")) + "%");
                        pMur.setText(String.format("%s", pourcentbar.getString("murcia")) + "%");
                        pOr.setText(String.format("%s", pourcentbar.getString("orense")) + "%");
                        pOv.setText(String.format("%s", pourcentbar.getString("oviedo")) + "%");
                        pSan.setText(String.format("%s", pourcentbar.getString("santander")) + "%");
                        pSe.setText(String.format("%s", pourcentbar.getString("sevilla")) + "%");
                        pVa.setText(String.format("%s", pourcentbar.getString("valencia")) + "%");
                        pZa.setText(String.format("%s", pourcentbar.getString("zaragoza")) + "%");
                        loadingDialog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2://echec
                    pBar.setText("failure");
                    pBil.setText("failure");
                    pCha.setText("failure");
                    pCor.setText("failure");
                    pGra.setText("failure");
                    pLe.setText("failure");
                    pMa.setText("failure");
                    pMan.setText("failure");
                    pMi.setText("failure");
                    pMur.setText("failure");
                    pOr.setText("failure");
                    pOv.setText("failure");
                    pSan.setText("failure");
                    pSe.setText("failure");
                    pVa.setText("failure");
                    pZa.setText("failure");
                    break;
                case 3://actualist
                    try {
                        JSONObject villeArray = new JSONObject((String) msg.obj);
                        //juger de avoir besoin de changer les donnees ou pas
                        //--------输出第一次的json数据--------
                        //System.out.println(pourcentbar);
                        if (villeArray.equals(pourcentbar)){
                            return;
                        }else {
                            pBar.setText(String.format("%s", villeArray.getString("barcelona")) + "%");
                            pBil.setText(String.format("%s", villeArray.getString("bilbao")) + "%");
                            pCha.setText(String.format("%s", villeArray.getString("chamartin")) + "%");
                            pCor.setText(String.format("%s", villeArray.getString("cordoba")) + "%");
                            pGra.setText(String.format("%s", villeArray.getString("granada")) + "%");
                            pLe.setText(String.format("%s", villeArray.getString("leon")) + "%");
                            pMa.setText(String.format("%s", villeArray.getString("malaga")) + "%");
                            pMan.setText(String.format("%s", villeArray.getString("manzanares")) + "%");
                            pMi.setText(String.format("%s", villeArray.getString("miranda")) + "%");
                            pMur.setText(String.format("%s", villeArray.getString("murcia")) + "%");
                            pOr.setText(String.format("%s", villeArray.getString("orense")) + "%");
                            pOv.setText(String.format("%s", villeArray.getString("oviedo")) + "%");
                            pSan.setText(String.format("%s", villeArray.getString("santander")) + "%");
                            pSe.setText(String.format("%s", villeArray.getString("sevilla")) + "%");
                            pVa.setText(String.format("%s", villeArray.getString("valencia")) + "%");
                            pZa.setText(String.format("%s", villeArray.getString("zaragoza")) + "%");
                        }
                        //System.out.println(villeArray);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenue);

        Intent intent =getIntent();
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");
        url = intent.getStringExtra("url");

        r1_1 = findViewById(R.id.r1_1_bil);
        r1_2 = findViewById(R.id.r1_2_mi);
        r1_3 = findViewById(R.id.r1_3_san);

        r2_1 = findViewById(R.id.r2_1_le);
        r2_2 = findViewById(R.id.r2_2_or);
        r2_3 = findViewById(R.id.r2_3_ov);

        r3_1 = findViewById(R.id.r3_1_bar);
        r3_2 = findViewById(R.id.r3_2_mur);
        r3_3 = findViewById(R.id.r3_3_za);
        r3_4 = findViewById(R.id.r3_4_va);

        r4_1 = findViewById(R.id.r4_1_cha);
        r4_2 = findViewById(R.id.r4_2_man);

        r5_1 = findViewById(R.id.r5_1_cor);
        r5_2 = findViewById(R.id.r5_2_gra);
        r5_3 = findViewById(R.id.r5_3_ma);
        r5_4 = findViewById(R.id.r5_4_se);

        pBar = (TextView) findViewById(R.id.tv_pBar);
        pBil = (TextView) findViewById(R.id.tv_pBil);
        pCha = (TextView) findViewById(R.id.tv_pCha);

        pCor = (TextView) findViewById(R.id.tv_pCor);
        pGra = (TextView) findViewById(R.id.tv_pGra);
        pLe = (TextView) findViewById(R.id.tv_pLe);

        pMa = (TextView) findViewById(R.id.tv_pMa);
        pMan = (TextView) findViewById(R.id.tv_pMan);
        pMi = (TextView) findViewById(R.id.tv_pMi);

        pMur = (TextView) findViewById(R.id.tv_pMur);
        pOr = (TextView) findViewById(R.id.tv_pOr);
        pOv = (TextView) findViewById(R.id.tv_pOv);

        pSan = (TextView) findViewById(R.id.tv_pSan);
        pSe = (TextView) findViewById(R.id.tv_pSe);
        pVa = (TextView) findViewById(R.id.tv_pVa);
        pZa = (TextView) findViewById(R.id.tv_pZa);


        r1_1.setOnClickListener(this);
        r1_1.setTag(1);
        r1_2.setOnClickListener(this);
        r1_2.setTag(2);
        r1_3.setOnClickListener(this);
        r1_3.setTag(3);

        r2_1.setOnClickListener(this);
        r2_1.setTag(4);
        r2_2.setOnClickListener(this);
        r2_2.setTag(5);
        r2_3.setOnClickListener(this);
        r2_3.setTag(6);

        r3_1.setOnClickListener(this);
        r3_1.setTag(7);
        r3_2.setOnClickListener(this);
        r3_2.setTag(8);
        r3_3.setOnClickListener(this);
        r3_3.setTag(9);
        r3_4.setOnClickListener(this);
        r3_4.setTag(10);

        r4_1.setOnClickListener(this);
        r4_1.setTag(11);
        r4_2.setOnClickListener(this);
        r4_2.setTag(12);

        r5_1.setOnClickListener(this);
        r5_1.setTag(13);
        r5_2.setOnClickListener(this);
        r5_2.setTag(14);
        r5_3.setOnClickListener(this);
        r5_3.setTag(15);
        r5_4.setOnClickListener(this);
        r5_4.setTag(16);

        findViewById(R.id.btn_reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadData();
            }
        });

        reloadData();//au debut, il faut executer la fonction reloadeData


        //ouvre une service
        Intent startIntent = new Intent(Bienvenue.this, UpdateVilleService.class);
        startIntent.putExtra("url", ""+url);
        startIntent.putExtra("username",""+username);
        startIntent.putExtra("password",""+password);
        startService(startIntent);
        //binding la service
        bindService(startIntent, conn, Context.BIND_AUTO_CREATE);
    }

    ServiceConnection conn = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            UpdateVilleService.Binder binder = (UpdateVilleService.Binder) service;
            binder.getService().setCallback(new UpdateVilleService.Callback() {
                @Override
                public void DataChange(String result) {
                    //Log.e("UpdateVilleService : ", result);
                    Message msg = new Message();
                    if(result.equals("failure")){
                        System.out.println(result);
                        msg.what = 2;//echec
                        msg.obj = result;
                        showVilleHandler.sendMessage(msg);
                    }else {
                        msg.what = 3;//actualiste
                        msg.obj = result;
                        showVilleHandler.sendMessage(msg);
                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public void onClick(View v){

        int tag = (Integer) v.getTag();
        String ville;
        switch(tag){
            case 1:
                ville = "bilbao";
                Goto(ville);
                break;
            case 2:
                ville = "miranda";
                Goto(ville);
                break;
            case 3:
                ville = "santander";
                Goto(ville);
                break;
            case 4:
                ville = "leon";
                Goto(ville);
                break;
            case 5:
                ville = "orense";
                Goto(ville);
                break;
            case 6:
                ville = "oviedo";
                Goto(ville);
                break;
            case 7:
                ville = "barcelona";
                Goto(ville);
                break;
            case 8:
                ville = "murcia";
                Goto(ville);
                break;
            case 9:
                ville = "zaragoza";
                Goto(ville);
                break;
            case 10:
                ville = "valencia";
                Goto(ville);
                break;
            case 11:
                ville = "chamartin";
                Goto(ville);
                break;
            case 12:
                ville = "manzanares";
                Goto(ville);
                break;
            case 13:
                ville = "cordoba";
                Goto(ville);
                break;
            case 14:
                ville = "granada";
                Goto(ville);
                break;
            case 15:
                ville = "malaga";
                Goto(ville);
                break;
            case 16:
                ville = "sevilla";
                Goto(ville);
                break;
            default :
                break;
        }

    }

    public void Goto(String ville){
        Intent intent = new Intent(Bienvenue.this,alarms.class);
        intent.putExtra("ville",""+ville);
        intent.putExtra("url", ""+url);
        intent.putExtra("username", ""+username);
        intent.putExtra("password", ""+password);
        startActivity(intent);

    }

    private void reloadData() {
        CheckInternet checkInternet = new CheckInternet();
        if (checkInternet.CheckInternet(Bienvenue.this)) {
            loadingDialog = ProgressDialog.show(Bienvenue.this, "Please wait", "Loading...");
            loadingDialog.setCanceledOnTouchOutside(true);
            RequestParams requestParams = new RequestParams();
            requestParams.put("tag","alarmCities");
            requestParams.put("username", username);
            requestParams.put("password", password);
            RequestUtils.ClientPost(url, requestParams, new NetCallBack() {
                @Override
                public void onMySuccss(String result) throws Exception {
                    Message msg = new Message();
                    msg.what = 1;//succes
                    msg.obj = result;
                    showVilleHandler.sendMessage(msg);
                }
                @Override
                public void onMyFailure(Throwable throwable) {
                    Message msg = new Message();
                    msg.what = 2;//echec
                    msg.obj = "failure";
                    showVilleHandler.sendMessage(msg);
                    AlertDialog.Builder builder = new AlertDialog.Builder(Bienvenue.this);
                    builder.setTitle("Failure!!");
                    builder.setItems(new String[]{"Server ERROR!"}, null);
                    builder.setNegativeButton("OK", null);
                    builder.show();
                    loadingDialog.dismiss();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        Intent startIntent = new Intent(Bienvenue.this, UpdateVilleService.class);
        unbindService(conn);
        stopService(startIntent);
        super.onDestroy();
    }
}
