package mtaa.java;

import android.content.DialogInterface;
import android.os.StrictMode;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;

class Requests {
    static private String IP = "192.168.1.5";   //MATUS-IP
    //static private String IP = "192.168.219.127"; //KRISTIAN-IP
    static private String PORT = ":8000";

    //https://stackoverflow.com/questions/4308554/simplest-way-to-read-json-from-a-url-in-java
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject GET_request(String urlBody)
    {
        //necessary for performing network operations
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        HttpURLConnection con;
        JSONObject result = null;

        urlBody = "http://" + IP + PORT + urlBody;

        try
        {
            URL url = new URL( urlBody);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            con.setDoInput(true);
            con.setConnectTimeout(5000);

            if (con.getResponseCode() == 200)
            {
                try
                {
                    InputStream istream = con.getURL().openStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(istream, Charset.forName("UTF-8")));
                    String jsonString = readAll(rd);

                    // odstranenie zbytocnych medzier a ozatvorkovanie
                    if (jsonString != null)
                    {
                        jsonString = "{" + jsonString.substring(16, jsonString.length() - 8) + "}";
                        result = new JSONObject(jsonString);
                    }

                    istream.close();
                }
                catch (Exception e)
                {
                    Log.e("IOSTREAM", "Reading JSON response from HTTP");
                    throw e;
                }

            }
            else
            {
                Log.i("WARNING" ,"HTTP request: '" + urlBody + "' with response code = " + con.getResponseCode());
            }

            con.disconnect();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Log.i("JSON", String.valueOf(result));
        return result;
    }

    public static String [] OTHER_request(String type, String urlBody)
    {
        String code = null;
        String response = null;

        //necessary for performing network operations
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (type == "POST" || type == "PUT" || type == "DELETE")
        {
            HttpURLConnection con;
            urlBody = "http://" + IP + PORT + urlBody;

            try
            {
                URL url = new URL( urlBody);
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod(type);

                con.setDoInput(true);
                con.setConnectTimeout(5000);

                code = String.valueOf(con.getResponseCode());

                try
                {
                    InputStream istream = con.getURL().openStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(istream, Charset.forName("UTF-8")));
                    response = readAll(rd);

                    istream.close();
                }
                catch (Exception e)
                {
                    Log.e("IOSTREAM", "Reading string response from HTTP");
                    throw e;
                }

                con.disconnect();
            }
            catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
        else
        {
            code = "XXX";
            response = "Wrong type of request!!!";
            Log.e("Request","Wrong type of request: " + type);
        }

        return new String [] {code, response};
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
