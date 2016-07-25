package adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import adifli.sql_json_v2.R;
import model.AlarmeItemBean;

/**
 * Created by Fabrice on 8/14/2015.
 */
public class AlarmeDialogAdapter extends BaseAdapter{

    private SparseArray<View> lmap = new SparseArray<>();
    private List<AlarmeItemBean> items;
    private Context context;

    public AlarmeDialogAdapter(Context context, List<AlarmeItemBean> alarmeItemBeans){
        this.items = alarmeItemBeans;
        this.context = context;

    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        if(lmap.get(position) == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.alarme_dialog,null);
            holder.text = (TextView) convertView.findViewById(R.id.items);
            holder.ll = convertView.findViewById(R.id.ll);
            lmap.put(position, convertView);
            convertView.setTag(holder);
        }else{
            convertView = lmap.get(position);
            holder = (Holder) convertView.getTag();
        }
        switch (items.get(position).getMessage()){
            case "POT 1":
                holder.text.setText("POT 1");
                holder.ll.setBackgroundResource(R.drawable.shape_layout_orange);
                break;
            case "ROE 2":
                holder.text.setText("ROE 2");
                holder.ll.setBackgroundResource(R.drawable.shape_layout_orange);
                break;
            case "NPR 3":
                holder.text.setText("NPR 3");
                holder.ll.setBackgroundResource(R.drawable.shape_layout_gris);
                break;
            case "NPE 4":
                holder.text.setText("NPE 4");
                holder.ll.setBackgroundResource(R.drawable.shape_layout_gris);
                break;
            case "NRX 5":
                holder.text.setText("NRX 5");
                holder.ll.setBackgroundResource(R.drawable.shape_layout_jaune);
                break;
            case "S/R 6":
                holder.text.setText("S/R 6");
                holder.ll.setBackgroundResource(R.drawable.shape_layout_jaune);
                break;
            case "BAT 7":
                holder.text.setText("BAT 7");
                holder.ll.setBackgroundResource(R.drawable.shape_layout_violet);
                break;
            case "RED 8":
                holder.text.setText("RED 8");
                holder.ll.setBackgroundResource(R.drawable.shape_layout_violet);
                break;
            case "PTA 9":
                holder.text.setText("PTA 9");
                holder.ll.setBackgroundResource(R.drawable.shape_layout_gris);
                break;
            case "SUP 10":
                holder.text.setText("SUP 10");
                holder.ll.setBackgroundResource(R.drawable.shape_layout_rouge);
                break;
            case "TEM 11":
                holder.text.setText("TEM 11");
                holder.ll.setBackgroundResource(R.drawable.shape_layout_gris);
                break;
            case "CNF 12":
                holder.text.setText("CNF 12");
                holder.ll.setBackgroundResource(R.drawable.shape_layout_gris);
                break;
            default :
                holder.text.setText("Null!!");
                holder.ll.setBackgroundResource(R.drawable.shape_layout_gris);
                break;
        }
        return convertView;

    }

    public class Holder{
        public TextView text;
        public View ll;
    }
}
