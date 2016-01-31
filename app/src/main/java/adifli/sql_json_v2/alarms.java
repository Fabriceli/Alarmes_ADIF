package adifli.sql_json_v2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import adapter.AlarmAdapter;
import model.Alarms;
import service.UpdatePosteService;
import utils.CheckInternet;
import utils.NetCallBack;
import utils.RequestUtils;


public class alarms extends Activity{

    private ListView lvAlarm;
    private AlarmAdapter adapter;
    private List<Alarms> alarmsList;
    private Button tvVilleId;
    public String tvVille;
    private ProgressDialog proDialog;
    private int dialogI = 0;
    protected String jsonDataActualise,jsonData,bandaId,bandaTotal,bandeName,oldData,bandaIdOld,bandaTotalOld,bandeNameOld;
    protected JSONArray postesOld,postes;
    protected String[] numeroOld,numero,couleurOld,couleur;
    private static int SUCCES = 1, ECHEC = 2,ACTUALISE = 3;
    private String password,username;
    private boolean loading = true,alarmsState = false;
    public static String url ;

    private Handler getAlarmHandler = new Handler() {
        public void handleMessage(android.os.Message msg){

            switch (msg.what){
                case 1 : //succes
                    jsonData = (String)msg.obj;
                    //System.out.println("jsonData:"+jsonData);
                    oldData = jsonData;
                    try {
                        JSONObject jsonObject = new JSONObject(jsonData);
                        JSONArray bands = jsonObject.getJSONArray("bands");
                        System.out.println("jsonData : "+jsonData);
                        //if()
                        for(int i=0;i<bands.length();i++){
                            JSONObject jsonObjectBands = bands.getJSONObject(i);
                            postesOld = jsonObjectBands.getJSONArray("pf");
                            bandaIdOld = jsonObjectBands.getString("bande");
                            bandaTotalOld = jsonObjectBands.getString("total");
                            bandeNameOld = jsonObjectBands.getString("bandeName");

                            //tableau de tableau
                            numeroOld=new String[postesOld.length()];
                            couleurOld=new String[postesOld.length()];
                            for(int j= 0;j<postesOld.length();j++){
                                numeroOld[j]= postesOld.getJSONObject(j).getString("number");
                                couleurOld[j]=postesOld.getJSONObject(j).getString("colour");
                                //System.out.println(numero[j]+couleur[j]);
                            }

                            alarmsList.add(new Alarms(bandaIdOld, bandaTotalOld, numeroOld, couleurOld,bandeNameOld));

                        }
                        //lvAlarm.setAdapter(adapter);

                        adapter.notifyDataSetChanged();//mis a jour les donnees
                        proDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    loading =false;
                    break;
                case 2 : //echec
                    if(alarmsState){
                        return;
                    }else {
                        proDialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(alarms.this);
                        builder.setTitle("Failure!!");
                        builder.setItems(new String[]{"Server ERROR !"}, null);
                        builder.setNegativeButton("OK", null);
                        builder.show();
                    }
                    break;
                case 3 : //actualise

                    jsonDataActualise = (String)msg.obj;
                    //Enregistrer la position actuelle
                    Parcelable listState = lvAlarm.onSaveInstanceState();
                    if(!(jsonDataActualise.equals(oldData))&&(!loading)){
                        try {
                            //System.out.println(jsonDataActualise);
                            alarmsList = new ArrayList<>();
                            adapter = new AlarmAdapter(alarms.this,alarmsList,tvVille,url,username,password);
                            lvAlarm.setAdapter(adapter);
                            JSONObject jsonObjectA = new JSONObject(jsonDataActualise);
                            JSONArray bandsA = jsonObjectA.getJSONArray("bands");
                            for(int i=0;i<bandsA.length();i++){
                                JSONObject jsonObjectBandsA = bandsA.getJSONObject(i);
                                postes = jsonObjectBandsA.getJSONArray("pf");
                                bandaId = jsonObjectBandsA.getString("bande");
                                bandaTotal = jsonObjectBandsA.getString("total");
                                bandeName = jsonObjectBandsA.getString("bandeName");
                                //tableau de tableau
                                numero=new String[postes.length()];
                                couleur=new String[postes.length()];
                                for(int j= 0;j<postes.length();j++){
                                    numero[j]= postes.getJSONObject(j).getString("number");
                                    couleur[j]=postes.getJSONObject(j).getString("colour");
                                    //System.out.println(numero[j]+couleur[j]);
                                }
                                alarmsList.add(new Alarms(bandaId, bandaTotal, numero, couleur, bandeName));
                                //adapter.updateView(i);

                            }
                            adapter.notifyDataSetChanged();//mis a jour les donnees
                            lvAlarm.onRestoreInstanceState(listState);//retour la position actuelle
                            oldData = jsonDataActualise;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else {
                        return;
                    }
                    break;

            }

        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarms);

        //Toast.makeText(this,"Loading Data ...",Toast.LENGTH_LONG).show();
        tvVilleId = (Button) findViewById(R.id.tvVilleId);
        lvAlarm = (ListView) findViewById(R.id.lvAlarm);

        Intent intent =getIntent();
        tvVille = intent.getStringExtra("ville");
        url = intent.getStringExtra("url");
        password = intent.getStringExtra("password");
        username = intent.getStringExtra("username");
        tvVilleId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadData();
            }
        });
        reloadData();
        //demarrer service
        Intent startIntent = new Intent(this,UpdatePosteService.class);
        startIntent.putExtra("ville",""+tvVille);
        startIntent.putExtra("url", ""+url);
        startIntent.putExtra("username", ""+username);
        startIntent.putExtra("password", ""+password);
        startService(startIntent);
        //lier service
        bindService(startIntent, conn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        alarmsState = true;
    }

    ServiceConnection conn = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            UpdatePosteService.Binder binder = (UpdatePosteService.Binder) service;
            binder.getService().setCallback(new UpdatePosteService.Callback() {
                @Override
                public void DataChange(String result) {
                    Message msg = new Message();
                    if(result.equals("failure")) {
                        dialogI++;
                        if(dialogI == 1){
                            msg.obj = result;
                            msg.what = ECHEC;//echec
                            getAlarmHandler.sendMessage(msg);
                        }
                    }else {
                        msg.obj = result;
                        msg.what = ACTUALISE;//actualise
                        getAlarmHandler.sendMessage(msg);
                        dialogI = 0;
                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void reloadData(){

        CheckInternet checkInternet = new CheckInternet();
        if(checkInternet.CheckInternet(alarms.this)){
            proDialog = ProgressDialog.show(alarms.this, "Please wait", "Connection server...", true, true);
            loading = true;
            alarmsList = new ArrayList<>();
            adapter = new AlarmAdapter(alarms.this,alarmsList,tvVille,url,username,password);
            tvVilleId.setText(tvVille);
            lvAlarm.setAdapter(adapter);
            // Ouvrez un fil pour connecter, si valider , il va entrer 2eme page
            Thread getDataThread = new Thread(new GetDataHandler());
            getDataThread.start();
        }
    }

    class GetDataHandler implements Runnable{

        @Override
        public void run() {
            final Message msg = new Message();
            RequestParams requestParams = new RequestParams();
            requestParams.put("tag","alarmBands");
            requestParams.put("username", username);
            requestParams.put("password", password);
            requestParams.put("city", tvVille);

            RequestUtils.ClientPost(url, requestParams, new NetCallBack() {
                @Override
                public void onMySuccss(String result) throws Exception {
                    //Log.d("result:",result);
                    msg.what = SUCCES;//succes
                    msg.obj = result;
                    getAlarmHandler.sendMessage(msg);

                }

                @Override
                public void onMyFailure(Throwable throwable) {
                    msg.what = ECHEC;//echec
                    msg.obj = "failure";
                    getAlarmHandler.sendMessage(msg);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        Intent startIntent = new Intent(this,UpdatePosteService.class);
        unbindService(conn);
        stopService(startIntent);
        alarmsState = true;
        super.onDestroy();
    }

}
