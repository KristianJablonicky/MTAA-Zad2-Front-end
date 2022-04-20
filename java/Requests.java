package mtaa.java;

import android.content.DialogInterface;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class Requests {
    //static private String IP = "10.10.37.255";   //MATUS-IP
    static private String IP = "192.168.219.127"; //KRISTIAN-IP
    static private String PORT = ":8000";

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
    public int PDF_request(String type, String urlBody) throws SecurityException{

        //https://stackoverflow.com/questions/2469451/upload-files-from-java-client-to-a-http-server
        String boundary = Long.toHexString(System.currentTimeMillis());
        String charset = "UTF-8";
        String CRLF = "\r\n";

        //Path path = FileSystems.getDefault().getPath("sdcard/Download", "zivotopis.pdf");
        //"/sdcard/Download/zivotopis.pdf"

        Path path = Paths.get("/sdcard/Download/zivotopis.pdf");
        //Path path = Paths.get("/storage/emulated/0/Download/cv-1.pdf");
        File binaryFile = new File(String.valueOf(path));

        binaryFile.setReadable(true, false);

        boolean exists = binaryFile.exists();
        if (exists == true) {

            // Printing the permissions associated
            // with the file
            Log.i("Executable: ", String.valueOf(binaryFile.canExecute()));
            Log.i("Readable: ", String.valueOf(binaryFile.canRead()));
            Log.i("Writable: ", String.valueOf(binaryFile.canWrite()));
        }


        URLConnection connection = null;
        try {
            connection = new URL("http://" + IP + PORT + urlBody).openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        try (
                OutputStream output = connection.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);
        ) {writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"binaryFile\"; filename=\"" + binaryFile.getName() + "\"").append(CRLF);
            writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(binaryFile.getName())).append(CRLF);
            writer.append("Content-Transfer-Encoding: binary").append(CRLF);
            writer.append(CRLF).flush();
            Files.copy(binaryFile.toPath(), output);

            output.flush(); // Important before continuing with writer!
            writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.

            // End of multipart/form-data.
            writer.append("--" + boundary + "--").append(CRLF).flush();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            int responseCode = ((HttpURLConnection) connection).getResponseCode();
            return responseCode;
        } catch (IOException e) {
            e.printStackTrace();
            return 400;
        }


    }
}
