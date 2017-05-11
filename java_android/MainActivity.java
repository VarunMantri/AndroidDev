
/*
    author : Varun Rajiv Mantri
    references: https://developer.android.com
 */

package example.vroom.com.mytestpp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    //Variable declaration
    //--------------------------------------
    GoogleApiClient mGoogleApiClient = null;                                                    //used to store instance of GoogleApiClient
    Geocoder geocoder;                                                                          //Used for decoding lat and long to add
    Location mLastLocation;                                                                     //used to store retrieved location
    TextView latitude;                                                                          //TextView to display latitude
    TextView longitude;                                                                         //TextView to display longitude
    TextView addr;                                                                              //TextView to display address
    double lat;
    double lon;
    StringBuilder sb;
    Button distress;
    //--------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();                                                                           //Method that binds xml with java
        makeConnection();                                                                       //creating an instance of GoogleApiClient
        distress=(Button) findViewById(R.id.help);
        distress.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v)
            {
                distressCall();
                return false;
            }
        });
    }

    protected void makeConnection() {
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    public void initialize() {
        //initializing
        latitude = (TextView) findViewById(R.id.latitude);
        longitude = (TextView) findViewById(R.id.longitude);
        addr = (TextView) findViewById(R.id.address);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            onStop();                                                                           //permission denied by user
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);    //used to get last location
        if (mLastLocation != null)
        {
            lat=mLastLocation.getLatitude();                                             //extracting latitude
            lon=mLastLocation.getLongitude();                                            //extracting longitude
            latitude.setText(String.valueOf(lat));
            longitude.setText(String.valueOf(lon));
            geocoder=new Geocoder(this,Locale.getDefault());                                    //Class capable of geocoding and reverse geocoding
            try
            {
                List<android.location.Address> l=geocoder.getFromLocation(lat,lon,1);           //Returns a list of the best possible match as the last parameter entered is 1
                int counter=0;
                sb=new StringBuilder();
                while(counter<l.size())
                {
                    android.location.Address address=l.get(counter);
                    try
                    {
                        sb.append(address.getAddressLine(0) + " ");                             //retrives first line of address
                        sb.append(address.getAddressLine(1) + " ");                             //retrives second line of address
                    }
                    catch(Exception e)
                    {
                        Toast.makeText(this,"Unable to retrive complete address",Toast.LENGTH_LONG).show();
                    }
                    counter=counter+1;
                }
                addr.setText(sb.toString());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Toast.makeText(this,"Location unavailable",Toast.LENGTH_SHORT).show();               //This indicates location is not available
        }
    }
    protected void onStop()                                                                      //Last stage of activity life cycle
    {
        mGoogleApiClient.disconnect();                                                           //disconnecting from location server
        super.onStop();
    }
    @Override
    public void onConnectionSuspended(int i)
    {
        //future use
    }
    public void butClick(View view)                                                              //Method called when button is pressed
    {
        mGoogleApiClient.connect();                                                              //Establishing connection
        Toast.makeText(this,"Connection established",Toast.LENGTH_SHORT).show();
    }
    public void distressCall()
    {
        Intent intent=new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT,"URGENT:DISTRESS CALL");
        intent.putExtra(Intent.EXTRA_TEXT,sb.toString());
        intent.setData(Uri.parse("mailto:mantri.varun16@gmail.com"));
        startActivity(intent);
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }
}
