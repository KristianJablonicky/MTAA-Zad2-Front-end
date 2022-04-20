package mtaa.java;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterCompanyActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_register_company);

        Button registerTlacidlo = (Button) findViewById(R.id.button_createCompany);
        registerTlacidlo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String urlString = "/postCompany/";

                EditText menoInput = (EditText) findViewById(R.id.textInput_menoCompany);
                String meno = menoInput.getText().toString();
                if(meno.length() < 1){
                    popupMessage("Chyba!", "Údaj Názov spoločnosti musí byť vlpnený");
                    return;
                }
                else{
                    urlString += meno + "/";
                }

                EditText mailInput = (EditText) findViewById(R.id.textInput_emailCompany);
                String mail = mailInput.getText().toString();
                if(mail.length() < 1){
                    popupMessage("Chyba!", "Údaj e-mail musí byť vlpnený");
                    return;
                }
                else{
                    urlString += mail + "/";
                }

                EditText phoneInput = (EditText) findViewById(R.id.textInput_phoneCompany);
                String phone = phoneInput.getText().toString();
                if(phone.length() < 1){
                    popupMessage("Chyba!", "Údaj tel. číslo musí byť vlpnený");
                    return;
                }
                else{
                    urlString += phone + "/";
                }

                EditText webInput = (EditText) findViewById(R.id.textInput_web);
                String web = webInput.getText().toString();
                if(web.length() > 0){
                    urlString += "web=" + web + "/";
                }

                EditText detailInput = (EditText) findViewById(R.id.textInput_detail);
                String detail = detailInput.getText().toString();
                if(detail.length() > 0){
                    urlString += "detail=" + detail + "/";
                }

                //inputy nacitane, ide sa poslat request

                Requests objekt = new Requests();
                String response = objekt.OTHER_request("POST", urlString);

                Log.i("response", String.valueOf(response));
                int responseCode = Integer.parseInt(response);
                if (responseCode >= 400){
                    popupMessage("Chyba!", "Spoločnosť s menom " + meno + " je už zaevidovaná v databáze," +
                            "\nalebo ste zadali nesprávne údaje");
                }
                else {
                    startActivity(new Intent(RegisterCompanyActivity.this, RegisterActivity.class));
                }
            }
        });
    }
}
