package mtaa.java;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterWorkerActivity extends AppCompatActivity {

    public void popupMessage(String nadpis, String sprava){
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_worker);

        Button registerTlacidlo = (Button)findViewById(R.id.button_createWorker);
        registerTlacidlo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String urlString = "/postWorker/W/";

                EditText menoInput = (EditText) findViewById(R.id.textInput_menoWorker);
                String meno = menoInput.getText().toString();
                if(meno.length() < 1){
                    popupMessage("Chyba!", "Údaj Meno musí byť vlpnený");
                }
                else{
                    urlString += "name=" + meno + "/";
                }

                EditText hesloInput = (EditText) findViewById(R.id.textInput_hesloWorker);
                String heslo = hesloInput.getText().toString();
                if(heslo.length() < 1){
                    popupMessage("Chyba!", "Údaj Meno musí byť vlpnený");
                }
                else{
                    urlString += "password=" + heslo + "/";
                }

                Requests objekt = new Requests();
                objekt.postWorker(urlString);

                Log.i("ok",meno);

            }
        });
    }
}
