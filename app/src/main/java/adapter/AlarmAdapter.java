package adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import adifli.sql_json_v2.R;
import model.AlarmeItemBean;
import model.Alarms;
import utils.CheckInternet;
import utils.NetCallBack;
import utils.RequestUtils;

/**
 * Created by Fabrice on 7/30/2015.
 */
    public class AlarmAdapter extends BaseAdapter{

    private Context context;
    private List<Alarms> alarmsList;
    private ProgressDialog proDialog;
    private CheckInternet checkInternet;
    private String ville;
    private String url,password,username;
    private SparseArray<View> lmap = new SparseArray<>();
    //private LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(90,90);

    public AlarmAdapter(Context context,List<Alarms> alarmsList,String ville,String url,String username,String password){
        this.context = context;
        this.alarmsList = alarmsList;
        this.ville = ville;
        this.url = url;
        this.username = username;
        this.password = password;
    }
    private void asynchttpPost(final View v,String url,String ville,String banda,int pf){
        RequestParams params = new RequestParams();

        params.put("tag","alarmPf");
        params.put("username",username);
        params.put("password",password);
        params.put("city",ville);
        params.put("bande",banda);
        params.put("pf", String.valueOf(pf));
        RequestUtils.ClientPost(url, params, new NetCallBack() {
            @Override
            public void onMySuccss(String result) throws Exception {
                //System.out.println(result);
                JSONObject jsonObject = new JSONObject(result);
                System.out.println(jsonObject);
                JSONArray alarmes = jsonObject.getJSONArray("alarmes");
                String banda = jsonObject.getString("bande");

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                //builder.setView(LayoutInflater.from(v.getContext()).inflate(R.layout.alarme_dialog_items,null));

                builder.setTitle("Banda" + banda + "  PF" + v.getTag());

                final List<AlarmeItemBean> items = new ArrayList<>();
                for (int j = 0; j < alarmes.length(); j++) {
                    items.add(new AlarmeItemBean(alarmes.getString(j)));
                }

                final AlarmeDialogAdapter alarmeDialogAdapter = new AlarmeDialogAdapter(v.getContext(),items);
                builder.setAdapter(alarmeDialogAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(v.getContext(), "You clicked: " + items.get(which).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setCancelable(true);
                builder.setNegativeButton("OK", null);
                proDialog.dismiss();
                AlertDialog dialog=builder.create();
                dialog.show();


            }

            @Override
            public void onMyFailure(Throwable throwable) {
                proDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Failure!!");
                builder.setItems(new String[]{"Server ERROR ! !"}, null);
                builder.setNegativeButton("OK", null);
                builder.show();
            }
        });
    }

    @Override
    public int getCount() {
        return alarmsList.size();
    }

    @Override
    public Alarms getItem(int position) {
        return alarmsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        //Log.e("position :", position+" : " + lmap.get(position));
        ViewHolder holder = null;
        final Button[] btn = new Button[0];
        //lmap.get(position) == null
        if (lmap.get(position) == null) {
            holder = new ViewHolder(btn);
            convertView = LayoutInflater.from(context).inflate(R.layout.alarms_items, null);
            holder.tvBandaId = (TextView) convertView.findViewById(R.id.tvBandaId);
            holder.tvBandaTotal = (TextView) convertView.findViewById(R.id.tvBabdaTotal);
            holder.tvBanda = (TextView) convertView.findViewById(R.id.tvBanda);
            holder.linearLayout = (LinearLayout) convertView.findViewById(R.id.llNumeroAlarms);

            holder.alarms = alarmsList.get(position);
            final String id = holder.alarms.getBandaId();
            final String[] numero = holder.alarms.getNumero();
            final String[] colour = holder.alarms.getCouleur(); //les couleurs des buttons
            holder.btn = new Button[numero.length];
            //add btn
            for (int i = 0; i < numero.length; i++) {
                holder.btn[i] = new Button(context);
                holder.espace = new TextView(context);
                holder.espace.setText("  ");//l'espace des buttons
                //holder.btn[i].setFocusable(false);
                holder.btn[i].setText(numero[i]);

                if (numero[i].contains("-")) {
                    holder.btn[i].setTag(10);
                } else {
                    holder.btn[i].setTag(Integer.parseInt(numero[i]));
                }
                //holder.btn[i].setLayoutParams(params);
                switch (colour[i]) {
                    case "r":
                        holder.btn[i].setBackgroundResource(R.drawable.shape_button_alarme_rouge);
                        break;
                    case "p":
                        holder.btn[i].setBackgroundResource(R.drawable.shape_button_alarme_violet);
                        break;
                    case "w":
                        holder.btn[i].setBackgroundResource(R.drawable.shape_button_alarme_gris);
                        break;
                    case "y":
                        holder.btn[i].setBackgroundResource(R.drawable.shape_button_alarme_jaune);
                        break;
                    case "o":
                        holder.btn[i].setBackgroundResource(R.drawable.shape_button_alarme_jaune_fonce);
                        break;
                    default:
                        holder.btn[i].setBackgroundResource(R.drawable.shape_button);
                        break;
                }

                //afficher les alarmes

                if (numero[i].contains("-")) {

                    holder.btn[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkInternet = new CheckInternet();
                            if (checkInternet.CheckInternet(context)) {
                                proDialog = ProgressDialog.show(context, "Please wait", "Connection server...", true, true);
                                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                //builder.setView(LayoutInflater.from(v.getContext()).inflate(R.layout.alarme_dialog_items,null));
                                builder.setTitle("Banda" + id + "  PF" + v.getTag());

                                final List<AlarmeItemBean> items = new ArrayList<>();

                                items.add(new AlarmeItemBean("SUP 10"));

                                final AlarmeDialogAdapter alarmeDialogAdapter = new AlarmeDialogAdapter(v.getContext(), items);
                                builder.setAdapter(alarmeDialogAdapter, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(context, "You clicked: " + items.get(which).getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                builder.setCancelable(true);
                                builder.setNegativeButton("OK", null);
                                proDialog.dismiss();
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    });

                } else {
                    holder.btn[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkInternet = new CheckInternet();
                            if (checkInternet.CheckInternet(context)) {
                                proDialog = ProgressDialog.show(context, "Please wait", "Connection server...", true, true);
                                asynchttpPost(v, url, ville, id, (Integer) v.getTag());
                            }
                        }
                    });
                }

                holder.linearLayout.addView(holder.espace);
                holder.linearLayout.addView(holder.btn[i]);

            }
            lmap.put(position, convertView);
            convertView.setTag(holder);

        }else{
            convertView = lmap.get(position);
            holder = (ViewHolder) convertView.getTag();

        }

        holder.tvBandaId.setText(holder.alarms.getBandaId());
        holder.tvBanda.setText(holder.alarms.getBandeName());
        holder.tvBandaTotal.setText(holder.alarms.getBandaTotal());
        return convertView;
    }


    public class ViewHolder {
        public TextView tvBandaId;
        public TextView tvBanda;
        public TextView tvBandaTotal;
        public TextView espace;
        public Alarms alarms;
        public Button[] btn;
        public LinearLayout linearLayout;

        public ViewHolder(Button[] btn) {
            this.btn = btn;
        }
    }

}
