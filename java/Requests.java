package mtaa.java;


import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class Requests {

    //static private String IP = "10.10.37.255";   //MATUS-IP
    static private String IP = "192.168.219.127"; //KRISTIAN-IP
    static private String PORT = ":8000";

    //static private String filePath = "sdcard/Download/"; // emulator
    static private String filePath = "storage/emulated/0/Download/"; // android zariadenie


    //https://stackoverflow.com/questions/4308554/simplest-way-to-read-json-from-a-url-in-java
    private static String readAll(InputStream s) throws IOException {
        BufferedReader rd = new BufferedReader(new InputStreamReader(s, Charset.forName("UTF-8")));
        StringBuilder sb = new StringBuilder();
        int cp;

        while ((cp = rd.read()) != -1) sb.append((char) cp);

        rd.close();
        return sb.toString();
    }

    public static JSONObject GET_request(String urlBody)
    {
        //necessary for performing network operations
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        HttpURLConnection con;
        JSONObject result = null;

        urlBody = urlBody.replaceAll(" ","%20");
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
                    String jsonString = readAll(istream);

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


    public static JSONArray GET_requestARRAY(String urlBody)
    {
        //necessary for performing network operations
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        HttpURLConnection con;
        JSONArray result = null;

        urlBody = urlBody.replaceAll(" ","%20");
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
                    String vysledok = readAll(istream);

                    result = new JSONArray(vysledok);

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

    public static String OTHER_request(String type, String urlBody)
    {
        String code = null;

        //necessary for performing network operations
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        if (type == "POST" || type == "PUT" || type == "DELETE")
        {
            HttpURLConnection con;

            urlBody = urlBody.replaceAll(" ","%20");
            urlBody = "http://" + IP + PORT + urlBody;

            try
            {
                URL url = new URL( urlBody);
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod(type);

                con.setDoInput(true);
                con.setDoOutput(true);
                con.setConnectTimeout(5000);

                code = String.valueOf(con.getResponseCode());

                con.disconnect();
            }
            catch (Exception e) {
                e.printStackTrace();
                return "408";
            }
        }
        else
        {
            code = "XXX";
            Log.e("Request","Wrong type of request: " + type);
        }

        return code;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int PDF_POST_request(String pdfMeno, String url) throws SecurityException{


        //https://stackoverflow.com/questions/2469451/upload-files-from-java-client-to-a-http-server

        File binaryFile = new File(pdfMeno.split(":")[1]);

        try {
            Path cesta =  Paths.get(pdfMeno.split(":")[1]);
            binaryFile.setReadable(true);

            byte[] fileContent = FileUtils.readFileToByteArray(new File(String.valueOf(cesta)));
            String encodedString = Base64.getEncoder().encodeToString(fileContent);

            createPostRequest(encodedString, "http://" + IP + PORT + url);
            return 200;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 500;

    }
    public boolean PDF_GET_request(String url, String meno) {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("http://" + IP + PORT + url).build();
        Response response = null;

        //https://www.geeksforgeeks.org/how-to-generate-a-pdf-file-in-android-app/
        //cast kodu je prekopirovana aj do MainActivity



        try {
            response = client.newCall(request).execute();

            if (response.body().contentLength() != -1){ // pouzivatel nema zverejneny zivotopis
                response.close();
                return false;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                PdfDocument pdfDocument = new PdfDocument();
                File file = new File(Environment.getExternalStorageDirectory(), "Životopis " + meno + ".pdf");
                file.setWritable(true);
                FileOutputStream fos = new FileOutputStream(file);
                pdfDocument.writeTo(new FileOutputStream(file));

                fos.write(Base64.getDecoder().decode(response.body().bytes()));
                fos.close();
                pdfDocument.close();

                response.close();
                return true;
            }
            else{
                Log.i("Android Verzia", "Verzia Androidu nepodporuje tuto funkciu.");
                response.close();
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!response.isSuccessful()) {
            try {
                throw new IOException("Failed to download file: " + response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        response.close();
        return false;
    }
    private void createPostRequest(String byteArray, String url) throws IOException, JSONException {
        JSONObject jsonData = new JSONObject();
        jsonData.put("PDFbytes", byteArray);

        final MediaType CONTENT_TYPE = MediaType.parse("application/json");

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.writeTimeout(10, TimeUnit.SECONDS);

        OkHttpClient client = builder.build();
        final Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(jsonData.toString(), CONTENT_TYPE))
                .build();

        client.newCall(request).execute();
    }
}
