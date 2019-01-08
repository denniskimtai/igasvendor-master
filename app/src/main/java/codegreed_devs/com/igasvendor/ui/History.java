package codegreed_devs.com.igasvendor.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import codegreed_devs.com.igasvendor.adapters.OrdersAdapter;
import codegreed_devs.com.igasvendor.R;
import codegreed_devs.com.igasvendor.models.OrderModel;
import codegreed_devs.com.igasvendor.utils.Constants;
import codegreed_devs.com.igasvendor.utils.Utils;

public class History extends AppCompatActivity {

    private OrdersAdapter ordersAdapter;
    private ArrayList<OrderModel> orders;
    private DatabaseReference rootRef;
    private ProgressDialog loadOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        //set up toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initialize views
        ListView orderList = (ListView)findViewById(android.R.id.list);
        loadOrders = new ProgressDialog(this);

        //initialize firebase variables
        rootRef = FirebaseDatabase.getInstance().getReference();

        //initialize other variables
        orders = new ArrayList<OrderModel>();
        ordersAdapter = new OrdersAdapter(getApplicationContext(), orders);

        //update ui
        orderList.setAdapter(ordersAdapter);
        orderList.setEmptyView(findViewById(android.R.id.empty));

        //method call
        getRecords();

        //handle item clicks
        orderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent viewOrder = new Intent(History.this, ViewOrder.class);
                viewOrder.putExtra("client_id", orders.get(i).getClientId());
                viewOrder.putExtra("order_id", orders.get(i).getOrderId());
                startActivity(viewOrder);
            }
        });

    }

    private void getRecords() {

        loadOrders.setMessage("Getting orders...");
        loadOrders.setCancelable(false);
        loadOrders.show();

        rootRef.child("Order Details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                orders.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    String vendorId = ds.child("vendorId").getValue(String.class);

                    if (vendorId != null && vendorId.equals(Utils.getPrefString(getApplicationContext(), Constants.SHARED_PREF_NAME_BUSINESS_ID)))
                    {
                        orders.add(new OrderModel(ds.child("orderId").getValue(String.class),
                                ds.child("clientId").getValue(String.class),
                                vendorId,
                                ds.child("gasBrand").getValue(String.class),
                                ds.child("gasSize").getValue(String.class),
                                ds.child("gasType").getValue(String.class),
                                ds.child("price").getValue(String.class),
                                ds.child("mnumberOfCylinders").getValue(String.class),
                                ds.child("orderStatus").getValue(String.class)));
                    }
                }

                loadOrders.dismiss();
                ordersAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadOrders.dismiss();
                Toast.makeText(History.this, "Couldn't fetch orders", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home)
        {
            finish();
        }

        return true;
    }
}
