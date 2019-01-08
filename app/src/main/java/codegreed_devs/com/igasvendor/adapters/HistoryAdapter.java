package codegreed_devs.com.igasvendor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import codegreed_devs.com.igasvendor.R;
import codegreed_devs.com.igasvendor.models.OrderModel;

public class HistoryAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<OrderModel> orders;

    public HistoryAdapter(Context context, ArrayList<OrderModel> orders) {
        this.context = context;
        this.orders = orders;
    }

    @Override
    public int getCount() {
        return orders.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = LayoutInflater.from(context).inflate(R.layout.history_item, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder();
        viewHolder.gasBrand = view.findViewById(R.id.gas_brand);
        viewHolder.gasDetails = view.findViewById(R.id.gas_details);

        view.setTag(viewHolder);

        String details = "";
        details += "Size : " + orders.get(i).getGasSize() + "\n";
        details += "Order type : " + orders.get(i).getGasType() + "\n";
        details += "No of cylinders : " + orders.get(i).getNumberOfCylinders() + "\n";
        details += "Order status : " + orders.get(i).getOrderStatus();

        viewHolder.gasBrand.setText(orders.get(i).getGasBrand());
        viewHolder.gasDetails.setText(details);

        return view;
    }

    private class ViewHolder{
         TextView gasBrand;
         TextView gasDetails;
    }

}
