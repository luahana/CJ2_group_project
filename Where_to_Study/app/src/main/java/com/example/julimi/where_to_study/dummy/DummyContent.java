package com.example.julimi.where_to_study.dummy;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.File;
import android.os.Environment;
import 	java.io.FileOutputStream;
import 	java.io.OutputStreamWriter;
import android.content.Context;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    /**
     * write string to a file
     * @param data: the string to output
     * @param filename: file name
     */
    public static void writeToFile(String data,String filename)
    {


        // Get the directory for the user's public pictures directory.
        final File path =
                Environment.getExternalStoragePublicDirectory
                        (
                                //Environment.DIRECTORY_PICTURES
                                /*Environment.DIRECTORY_DCIM +*/ "/buildings/"
                        );


        // Make sure the path directory exists.
        if(!path.exists())
        {
            // Make it, if it doesn't exit
            path.mkdirs();
        }

        final File file = new File(path, filename);

        // Save your stream, don't forget to flush() it before closing it.

        try
        {


            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);

            myOutWriter.close();

            fOut.flush();
            fOut.close();

        }
        catch (IOException e)
        {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private static int COUNT = 0;
    private static final String GET_URL_PRE = "https://api.uwaterloo.ca/v2/buildings/MC/";
    private static final String GET_URL_POST = "/courses.json?key=2d5402f20d57e1dd104101f9fa7dae27";
    private static final String USER_AGENT = "Marshmallow/6.0";
    private static final String GET_FILE = "MC.txt";
    private static String[] DoW = {
            "",
            "",
            "M",
            "T",
            "W",
            "Th",
            "F"
    };
    private static int Len = 0;

    public static JSONObject jsonObject = new JSONObject();
    public static StringBuilder responseStrBuilder;

    private static boolean helpToGetFile(String today, String cday) {
        // M,W,F
        for (int i = 0; i < cday.length(); i++) {
            if (today.length() == 1) {
                if (today.charAt(0) == cday.charAt(i) && cday.charAt(i+1) != 'h') return true;
            } else {
                if (cday.charAt(i) == 'h') return true;

            }
        }
        return false;
    }
    public static void fileGet() throws IOException {
        File local = Environment.getExternalStoragePublicDirectory("/buildings/");
        File file = new File(local,GET_FILE);

        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        System.out.println("Today: " + dayOfWeek);

        try {
            //InputStream in = new BufferedInputStream(file.get);
            BufferedReader streamReader = new BufferedReader(new FileReader(file));
            responseStrBuilder = new StringBuilder();

            String inStr;
            while ((inStr = streamReader.readLine()) != null) responseStrBuilder.append(inStr);

            jsonObject = new JSONObject(responseStrBuilder.toString());
             Log.d("","JSON value: " + jsonObject.getJSONObject("building").getJSONObject("MC").length());
            COUNT = jsonObject.getJSONObject("building").getJSONObject("MC").length();

            //for (int i = 0; i < jsonObject.getJSONObject("building").getJSONObject("MC").getJSONArray("2034").length(); i++) {
            //    if (helpToGetFile(DoW[dayOfWeek], jsonObject.getJSONObject("building").getJSONObject("MC").getJSONArray("2034").getJSONObject(i).getString("weekdays")))
            //        System.out.println("Today: " + DoW[dayOfWeek] + " " + jsonObject.getJSONObject("building").getJSONObject("MC").getJSONArray("2034").getJSONObject(i).getString("start_time"));
            //}

            System.out.println(jsonObject.getJSONObject("building").getJSONObject("MC").getJSONArray("2034").length());
            Iterator keysToCopyIterator = jsonObject.getJSONObject("building").getJSONObject("MC").getJSONArray("2034").getJSONObject(0).keys();
            List<String> keysList = new ArrayList<String>();
            while(keysToCopyIterator.hasNext()) {
                String key = (String) keysToCopyIterator.next();
                keysList.add(key);
                System.out.println(key);
            }
            //in.close();

        } catch (JSONException e) {
            e.printStackTrace();
            System.err.println("Fail to convert to JSONObject by File!");
        }
    }

    public static void sendGet(String GET_URL) throws IOException {
        URL obj = new URL(GET_URL);

        // obtain a new connection
        HttpURLConnection urlConnection = (HttpURLConnection) obj.openConnection();

        // prepare the request
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("User_Agent", USER_AGENT);
        int response = urlConnection.getResponseCode();
        //System.out.println("Connection Response Code: " + response);

        // read the request
        try {
            urlConnection.connect();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            responseStrBuilder = new StringBuilder();

            String inStr;
            while ((inStr = streamReader.readLine()) != null) responseStrBuilder.append(inStr);

            jsonObject = new JSONObject(responseStrBuilder.toString());
            // Log.d("","JSON value: " + jsonObject.getJSONArray("data").length());
            Len = jsonObject.getJSONArray("data").length();
            in.close();

        } catch (JSONException e) {
            e.printStackTrace();
            System.err.println("Fail to convert to JSONObject!");
        } finally {
            urlConnection.disconnect();
        }
    }

    private static class BackgroundTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                String GET_URL;
                String content="{\"building\":{\"MC\":{";
                int idx = 0;

                boolean firstclass = true;
                //int noc = 0;
                for (int i = 2034; i < 2060; i++) {

                    GET_URL = GET_URL_PRE + Integer.toString(i) + GET_URL_POST;

                    boolean first_line = true;
                    DummyContent.sendGet(GET_URL);
                    if (Len == 0) continue;

                    if(!firstclass) content += ",";
                    firstclass = false;
                    content += "\""+i+"\":[";
                    while (idx < Len) {
                        System.out.println(i);
                        if(!first_line) content += ",";
                        first_line = false;
                        content = content+ "{\"subject\":\"";
                        content += jsonObject.getJSONArray("data").getJSONObject(idx).getString("subject");
                        //jsonObject.getJSONArray().
                        content += "\",\"catalog_number\":\"";
                        content += jsonObject.getJSONArray("data").getJSONObject(idx).getString("catalog_number");
                        content = content+ "\",\"weekdays\":\"";
                        content += jsonObject.getJSONArray("data").getJSONObject(idx).getString("weekdays");
                        content = content+ "\",\"start_time\":\"";
                        content += jsonObject.getJSONArray("data").getJSONObject(idx).getString("start_time");
                        content += "\"";
                        content = content+ ",\"end_time\":\"";
                        content += jsonObject.getJSONArray("data").getJSONObject(idx).getString("end_time");
                        content += "\"";
                        content = content+ ",\"room\":\"";
                        content += jsonObject.getJSONArray("data").getJSONObject(idx).getString("room");
                        content = content+ "\"}";
                        ++idx;
                        //write
                    }
                    idx = 0;
                    content += "]";
                }
                content += "}}}";
                writeToFile(content,"MC.txt");
                System.out.println("1111111111111");

            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Fail to do sendGet() in background");

            } catch (JSONException e) {
                e.printStackTrace();
                System.err.println("Fail to write!");
            }
            return responseStrBuilder.toString();
        }
    }

    public static void getResponse() {
        new BackgroundTask().execute();
    }



    static {
        getResponse();
        // Add some sample items.
        try {
            fileGet();
            Iterator it = jsonObject.getJSONObject("building").getJSONObject("MC").keys();

            for (int i = 1; i <= COUNT && it.hasNext(); i++) {
                String key = (String) it.next();
                addItem(createDummyItem(i, key));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static DummyItem createDummyItem(int position, String item) {
        return new DummyItem(String.valueOf(position), "MC" + item, "");
    }

    /*private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }*/

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public final String id;
        public final String content;
        public final String details;

        public DummyItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
