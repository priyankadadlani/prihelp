package com.example.purva.helpmeout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.purva.helpmeout.gpstracking.GPSTracker;
import com.example.purva.helpmeout.gpstracking.contGPS;

import org.json.JSONException;
import org.json.JSONObject;

//import com.example.purva.helpmeout.AsyncResponse;
public class MainActivity extends ActionBarActivity {

    public static String result1;
    private Button b1;
    private EditText t1;
    private LocationManager locationManager;
    public int flag;
    private GPSTracker gps;
    //int tempuid=123456;
    private boolean gps_enabled = false;
    private boolean network_enabled = false;
    private MenuItem item;
    private connectNetwork con;
    private MainActivity mainActivity;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    private contGPS contGps;

   @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String MyPREF = "sharedPrefs" ;
        gps = new GPSTracker(this);

       //int data = getIntent().getExtras().getInt("type");

        mainActivity = this;
        flag=0;
        //sharedpreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
       sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedpreferences.edit();
        if(sharedpreferences.contains("uid")==false)
        {
//            Toast.makeText(getApplicationContext(),"Please login first!!! Redirecting you to login page", Toast.LENGTH_LONG).show();
            System.out.println("******************************8entering to login page********************");
            Intent i1 = new Intent(MainActivity.this,Login.class);
            startActivity(i1);
        }
       System.out.println("\n\ncrossed it.. enterinf to gpg on 1");
        contGps = new contGPS(this,mainActivity);
        b1 = (Button) findViewById(R.id.help_button);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"tap for longer time", Toast.LENGTH_LONG).show();
            }
        });
        b1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override

            public boolean onLongClick(View v) {
                if(flag == 0)
                {
                    flag=1;
                    b1.setText("STOP");
                    if (gps.canGetLocation())
                    {

                        double latitude = gps.getLatitude();
                        double longitude = gps.getLongitude();

                        Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                        JSONObject locationHelp = new JSONObject();
                        try
                        {
                            System.out.print("*********INSIDE MAIN BUTTON ON LONG HELP\n\n");
                            locationHelp.put("uid",sharedpreferences.getLong("uid",0));

                            locationHelp.put("type", "help");
                            locationHelp.put("latitude",latitude);
                            locationHelp.put("longitude",longitude);
                            locationHelp.put("id",6);
                            editor.putString("latitude",latitude+"");
                            editor.putString("longitude",longitude+"");

                            con = new connectNetwork(mainActivity);
                            con.execute(locationHelp.toString());
                            Toast.makeText(getApplicationContext(),result1, Toast.LENGTH_LONG).show();
                            Thread s = new SoundModule();
                            s.start();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }


                    }
                    else
                    {
                        gps.showSettingsAlert();
                    }
                    //flag=1;
                    return true;
                }
                else
                {
                    flag=0;
                    b1.setText("HELP");
                    JSONObject stop = new JSONObject();
                    try
                    {
                        stop.put("uid", sharedpreferences.getLong("uid",0));
                        stop.put("type", "stop");
                        stop.put("latitude",sharedpreferences.getString("latitude","0.0"));
                        stop.put("longitude",sharedpreferences.getString("longitude","0.0"));
                        stop.put("id",sharedpreferences.getInt("id",-1));
                        new connectNetwork(mainActivity).execute(stop.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return true;

            }
        });

        t1= (EditText) findViewById(R.id.ackNum);
//       if(data==5)
//       {
//           t1.setVisibility(View.INVISIBLE);
//       }
        t1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Boolean handled = false;

                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    String ack= t1.getText().toString();
                    if(ack.length()!=6)
                    {
                        Toast.makeText(getApplicationContext(),"Acknowledgement no. must contain 6 digits",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        int ackNo=Integer.parseInt(ack);
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("uid", sharedpreferences.getLong("uid",0));
                            obj.put("ack", ackNo);
                            new connectNetwork(mainActivity).execute(obj.toString());
                        }
                        catch(JSONException e)
                        {
                            e.printStackTrace();
                        }
                        handled = true;

                    }
                    return handled;

                }
                return true;
            }
        });
    }
    void print(String ack)
    {
        Toast.makeText(getApplicationContext(),ack,Toast.LENGTH_SHORT).show();
        try {
            JSONObject ackUID = new JSONObject(ack);
            System.out.println("\n\n\n print fn   *****"+"\n\n\n\n******");
            t1.setText(ackUID.getLong("ack")+"");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"error in receiving json object",Toast.LENGTH_SHORT).show();
        }

    }
    public void sendData(double latitude, double longitude)
    {
        System.out.print("fgdfgfd entered into send data \n\n7777777777777777777777777777");
            JSONObject location = new JSONObject();
            try
            {
                location.put("uid",sharedpreferences.getLong("uid",0));
                location.put("id",sharedpreferences.getInt("id",-1));
                location.put("type", "location");
                location.put("latitude",latitude);
                location.put("longitude",longitude);
                con = new connectNetwork();
                System.out.println("\n\n\n\n*********SENT BY CONTGPS******\n\n\n\n\n");
                con.execute(location.toString());
                Toast.makeText(getApplicationContext(), "Sending location: latitude:" + latitude + ", longitude" + longitude , Toast.LENGTH_LONG).show();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_temp, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public double mLat() {
        return gps.getLatitude();
    }

    public double mLong() {
        return gps.getLongitude();
    }
}
