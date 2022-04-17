package mtaa.java;

import android.content.DialogInterface;
import android.os.StrictMode;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;


class volania extends AppCompatActivity {

    String IP = "192.168.219.127:8000";


    private static String readAll(Reader rd) throws IOException { //https://stackoverflow.com/questions/4308554/simplest-way-to-read-json-from-a-url-in-java
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public JSONObject getUser(String urlString){

        URL url = null;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy); // habadura zarucujuca funkcnost

        HttpURLConnection con = null;

        JSONObject vysledok = new JSONObject();

        try {
            url = new URL(urlString);

            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            //skopirovane z netu
            con.setDoInput(true);

            con.setConnectTimeout(5000);

            InputStream in = null;
            //in = new BufferedInputStream(con.getInputStream());
            in = con.getURL().openStream();
            if(con.getResponseCode() == 200) {
                try {
                    BufferedReader rd = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
                    String jsonText = readAll(rd);

                    Log.i("json", jsonText);

                    if (jsonText != null)
                        jsonText = "{" + jsonText.substring(16, jsonText.length() - 8) + "}";
                    // odstranenie zbytocnych medzier a ozatvorkovanie

                    JSONObject docastny = new JSONObject(jsonText);
                    vysledok = docastny;
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    in.close();
                }

                if (vysledok != null) {

                    Log.i("Login", "Spravne meno aj heslo " + vysledok);

                } else {
                    Log.i("Error pri URL: ", url.toString());
                }
            }
            else{
                return null;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            con.disconnect();
        }
        Log.i("JSON", String.valueOf(vysledok));
        return vysledok;
    }

    public void postWorker(String urlString){

        try {
            Log.i("url", "http://" + IP + urlString);
            URL url = new URL("http://" + IP + urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            //https://stackoverflow.com/questions/4205980/java-sending-http-parameters-via-post-method-easily
            con.setDoOutput( true );
            con.setInstanceFollowRedirects( false );
            con.setRequestMethod("POST");

            //-----------NEPOSIELA ZIADNE REQUESTY, A TEXTOVE POLIA SA ESTE NENACITAVAJU--------------

            Log.i("ok?","ok");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
