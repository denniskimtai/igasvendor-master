package codegreed_devs.com.igasvendor.ui;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import codegreed_devs.com.igasvendor.R;
import codegreed_devs.com.igasvendor.adapters.OrdersAdapter;
import codegreed_devs.com.igasvendor.models.OrderModel;
import codegreed_devs.com.igasvendor.utils.Constants;
import codegreed_devs.com.igasvendor.utils.Utils;

public class Home extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private ArrayList<String> viableOrderIDs;
    private ArrayList<OrderModel> viableOrders;
    private OrdersAdapter ordersAdapter;
    private DatabaseReference rootDBRef;
    private GeoFire geoFire;
    private float[] results;
    GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initialize views
        ListView ordersList = findViewById(android.R.id.list);
        TextView emptyListView = findViewById(android.R.id.empty);

        //get shared preference values
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final float vendorLatitude = sharedPref.getFloat(Constants.SHARED_PREF_NAME_LOC_LAT, 0);
        final float vendorLongitude = sharedPref.getFloat(Constants.SHARED_PREF_NAME_LOC_LONG, 0);

        //initialize firebase variables
        rootDBRef = FirebaseDatabase.getInstance().getReference();
        geoFire = new GeoFire(rootDBRef.child("clientRequests"));

        //initialize other variables
        viableOrderIDs = new ArrayList<String>();
        viableOrders = new ArrayList<OrderModel>();
        ordersAdapter = new OrdersAdapter(getApplicationContext(), viableOrders);

        //method calls
        getViableOrders(vendorLatitude, vendorLongitude);
        getViableOrderDetails(viableOrderIDs);

        //update ui
        ordersList.setAdapter(ordersAdapter);
        ordersList.setEmptyView(emptyListView);

        //handle item clicks
        ordersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent viewOrder = new Intent(Home.this, ViewOrder.class);
                viewOrder.putExtra("order_id", viableOrders.get(position).getOrderId());
                viewOrder.putExtra("gas_brand", viableOrders.get(position).getGasBrand());
                startActivity(viewOrder);
            }
        });

        //get location of user
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }


    }

    private void getViableOrders(final float latitude, final float longitude) {
        rootDBRef.child("clientRequests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                viableOrderIDs.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    final String requestID = ds.getKey();
                    geoFire.getLocation(requestID, new LocationCallback() {
                        @Override
                        public void onLocationResult(String key, GeoLocation location) {

                            //add the order IDs to arraylist for later processing if
                            // the difference in distance is less than 15 kilometers
                            Location.distanceBetween(location.latitude, location.longitude, latitude, longitude, results);

                            if (results[0] <= 1.5) {
                                viableOrderIDs.add(requestID);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getViableOrderDetails(ArrayList<String> orderIDs) {
        for (String orderID : orderIDs) {
            //fetch all details in the db for the request
            rootDBRef.child("Order Details").child(orderID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    viableOrders.clear();

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String orderStatus = ds.child("orderStatus").getValue(String.class);
                        if (orderStatus != null && orderStatus.equals("pending")) {
                            viableOrders.add(new OrderModel(ds.child("orderId").getValue(String.class),
                                    ds.child("clientId").getValue(String.class),
                                    ds.getKey(),
                                    ds.child("gasBrand").getValue(String.class),
                                    ds.child("gasSize").getValue(String.class),
                                    ds.child("gasType").getValue(String.class),
                                    ds.child("price").getValue(String.class),
                                    ds.child("mnumberOfCylinders").getValue(String.class),
                                    ds.child("orderStatus").getValue(String.class)));
                        }
                    }

                    ordersAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.history) {
            startActivity(new Intent(getApplicationContext(), History.class));
        } else if (id == R.id.edit_profile) {
            startActivity(new Intent(getApplicationContext(), EditProfile.class));
        } else if (id == R.id.log_out) {
            logOut();
        } else if (id == R.id.exit) {
            finish();
        }

        return true;
    }

    //log out user
    private void logOut() {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Logout");
        alert.setMessage("Are you sure you want to log out...?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Utils.clearSP(getApplicationContext());
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //ignore and dismiss dialog
            }
        });
        alert.show();

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {

            //Save vendors location
            String vendorId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Available Vendors");

            //Save using geofire
            GeoFire geoFire = new GeoFire(ref);
            geoFire.setLocation(vendorId, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {

                }
            });

        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
