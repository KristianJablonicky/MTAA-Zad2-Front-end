package mtaa.java;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.StrictMode;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import mtaa.java.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.util.Log;
import android.widget.EditText;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;


    public void popupMessage(String sprava, String nadpis){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(sprava);
        alertDialogBuilder.setTitle(nadpis);
        alertDialogBuilder.setNegativeButton("ok", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(nadpis,"Ok button stlaceny");
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        Button button = (Button) findViewById(R.id.button_login);
        EditText menoInput = (EditText)findViewById(R.id.textInput_meno);
        EditText hesloInput = (EditText)findViewById(R.id.textInput_heslo);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String meno = menoInput.getText().toString();
                String heslo = hesloInput.getText().toString();
                URL url = null;



                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy); // habadura zarucujuca funkcnost

                try {
                    url = new URL("http://192.168.219.127:8000/getUser/" + meno + "/" + heslo + "/");

                } catch (MalformedURLException e) {
                    Log.i("URL error", "new URL hodil exception");
                    e.printStackTrace();
                }

                HttpURLConnection con = null;
                BufferedReader vysledok = null;
                try {
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");

                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    //skopirovane z netu
                    con.setDoInput(true);
                    //con.setDoOutput(true);
                    InputStreamReader input = new InputStreamReader(con.getInputStream()); //tuna to crashne
                    vysledok = new BufferedReader(input);
                    Log.i("InputStreamReader", "ok");
                    Log.i("InputStreamReader", vysledok.toString());

                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } finally {
                    con.disconnect();
                }

                /*
                try {
                    con = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    Log.i("con error", "URL connection hodil exception");
                    e.printStackTrace();
                }
                */

                /*
                try {
                    con.setRequestMethod("GET");
                } catch (ProtocolException e) {
                    Log.i("HTTP GET ERROR", "GET volanie hodil exception");
                    e.printStackTrace();
                }*/
                con.setConnectTimeout(5000);

                try {
                    InputStream in = null;
                    try {
                        in = new BufferedInputStream(con.getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.i("in", in.toString());
                } finally {
                    con.disconnect();
                }

                if (vysledok != null) {
                    try {
                        Log.i("OK", vysledok.toString() + "\n" + con.getResponseCode());
                        if(con.getResponseCode() > 200){
                            popupMessage("Nespravne meno alebo heslo", "Pozor:");
                        }
                        else{
                            Log.i("Login", "Spravne meno aj heslo");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.i("Error pri URL: ", url.toString());
                }

            }
        });

        //activityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        //setSupportActionBar(binding.toolbar);



/*
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

 */
/*
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

 */

        }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }*/
/*
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
*/
    /*
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
    */

}
