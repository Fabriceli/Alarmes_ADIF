package adifli.sql_json_v2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import utils.CheckInternet;
import utils.NetCallBack;
import utils.RequestUtils;

public class login extends Activity {
    private EditText editTextUserName;
    private EditText editTextPassword,editTextIP;
    private CheckBox rememberMe;
    private ProgressDialog proDialog;
    protected String login;
    String userName;
    String passWord;
    public static String url ;

    //tag of SharePreferences
    /** tag de le SharePreferences */
    private final String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";

    //save username with SharedPreferences
    /** enregistrer le username avec le SharedPreferences */
    private String SHARE_LOGIN_USERNAME = "MAP_LOGIN_USERNAME";

    /**  */
    //private String SHARE_LOGIN_PASSWORD = "MAP_LOGIN_PASSWORD";

    private String SHARE_URL = "MAP_URL";

    /**
     * met a jour le thread d'interface utilisateur,pour la connexion echoue
     */
    //refresh UI and prompt error
    Handler loginHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (proDialog != null) {
                proDialog.dismiss();
            }
            switch (msg.what){
                case 1://Username or password is empty
                    Toast.makeText(login.this, "Failure : Username or password is empty !",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 2://Username or password is incorrect
                    Toast.makeText(login.this, "Failure : Username or password is incorrect !",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 3://IP is empty
                    Toast.makeText(login.this, "Failure : IP is empty !",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 4://IP is incorrect or serveur has a probleme
                    Toast.makeText(login.this, "Failure : IP is incorrect or Serveur has a probleme !",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editTextUserName = (EditText) findViewById(R.id.editTextUserName);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextIP = (EditText) findViewById(R.id.editTextIP);
        rememberMe = (CheckBox) findViewById(R.id.loginRememberMeIPCheckBox);
        initView(false);
        rememberMe.setOnCheckedChangeListener(rememberMeListener);

    }

    @Override
    protected void onRestart() {

        super.onRestart();

        if(!isRememberMe()){
            clearShareUrl();
        }
    }

    public void invokeLogin(View view) {

        CheckInternet checkInternet = new CheckInternet();
        if(checkInternet.CheckInternet(login.this)){
            proDialog = ProgressDialog.show(login.this, "Please wait", "Connection server...", true, true);
            // Ouvrez un fil pour connecter, si valider , il va entrer 2eme page
            //open a new thread if login successful,and jump to second page
            Thread loginThread = new Thread(new LoginFailureHandler());
            loginThread.start();
        }

    }

    class LoginFailureHandler implements Runnable {
        @Override
        public void run() {
            userName = editTextUserName.getText().toString();
            passWord = editTextPassword.getText().toString();
            if ((userName.length()!=0) &&(passWord.length()!=0)){
                if(editTextIP.getText().toString().length() == 0){
                    Message message = new Message();
                    message.what = 3;//IP is empty
                    loginHandler.sendMessage(message);
                }else {
                    url = "http://"+editTextIP.getText().toString()+"/apptt/";
                    login = "login";
                    RequestParams requestParams = new RequestParams();
                    requestParams.put("tag",login);
                    requestParams.put("username", userName);
                    requestParams.put("password", passWord);

                    RequestUtils.ClientPost(url, requestParams, new NetCallBack() {
                        @Override
                        public void onMySuccss(String result) throws Exception {

                            JSONObject s = new JSONObject(result);
                            boolean error = s.getBoolean("error");

                            // valider
                            //successful
                            if (!error) {

                                if (isRememberMe()) {
                                    saveSharePreferences(true, true);
                                } else {
                                    saveSharePreferences(true, false);
                                }
                                if (!rememberMe.isChecked()) {
                                    clearShareUrl();
                                }
                                // 2eme page
                                Intent intent = new Intent();
                                intent.putExtra("password", "" + passWord);
                                intent.putExtra("url", "" + url);
                                intent.putExtra("username", userName);
                                intent.setClass(login.this, Bienvenue.class);
                                startActivity(intent);

                                proDialog.dismiss();
                                finish();
                            } else {
                                // Appeler le "handler" pour mis a jour le UI
                                Message message = new Message();
                                message.what = 2;//Username or password is incorrect
                                loginHandler.sendMessage(message);
                            }
                        }

                        @Override
                        public void onMyFailure(Throwable throwable) {
                            //Log.i("error", String.valueOf(throwable));
                            Message message = new Message();
                            message.what = 4;//IP is incorrect
                            loginHandler.sendMessage(message);

                        }
                    });
                }

            }else{
                Message message = new Message();
                message.what = 1;//Username or password is empty
                loginHandler.sendMessage(message);
            }

        }

    }

    private void initView(boolean isRememberMe) {
        SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
        String userName = share.getString(SHARE_LOGIN_USERNAME, "");
        String urlIp = share.getString(SHARE_URL,"");
        //Log.d(this.toString(), "userName=" + userName + " password=" + password);
        if (!"".equals(userName)) {
            editTextUserName.setText(userName);
        }
        if (!"".equals(urlIp)){
            editTextIP.setText(urlIp);
            rememberMe.setChecked(true);
        }
        share = null;
    }

    /**
     * si reussi,va eSharePreferences
     * */
    private void saveSharePreferences(boolean saveUserName, boolean saveUrl) {
        SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
        if (saveUserName) {
            share.edit().putString(SHARE_LOGIN_USERNAME, editTextUserName.getText().toString()).apply();
        }
        if (saveUrl) {
            share.edit().putString(SHARE_URL, editTextIP.getText().toString()).apply();
        }
        share = null;
    }

    /** crocher le carre ou pas */
    private boolean isRememberMe() {
        return rememberMe.isChecked();
    }

    /**
     * supprimer le mot de passe
     */
    private void clearShareUrl() {
        SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
        share.edit().putString(SHARE_URL, "").apply();
        share = null;
    }

    /**
     *  remember me checkBoxListener
     */
    private CompoundButton.OnCheckedChangeListener rememberMeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (rememberMe.isChecked()) {
                Toast.makeText(login.this, "if login successfully, automatically add the IP next time.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

}


