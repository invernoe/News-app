package com.example.android.newsapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public final class QueryUtils {
    // Class name to enter in log messages.
    private final static String LOG_TAG = QueryUtils.class.getName();

    // Empty constructor because all methods are static methods and we should not create an instance of the class.
    private QueryUtils(){
    }

    public static List<News> fetchNewsdata(String stringUrl){
        // Make the url from the string input argument
        URL url = makeUrl(stringUrl);

        // make the Json response from the Http request using the url
        String jsonResponse = makeJsonResponse(url);

        // return list of news after relevant elements have been parsed
        return parseJson(jsonResponse);
    }

    /**
     * code responsible for getting the json from the given url
     * @param url is the url of our news query
     * @return Json string to be parsed
     */
    private static String makeJsonResponse(URL url){

        String jsonResponse = "";

        // If the URL is null we get out of the method as there is nothing to make a json response out of
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            // Set read and connect timeout times so we can throw an exception if the data doesn't load after a while
            // or if we just can't connect.
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else{
                Log.e(LOG_TAG, "Error Response Code:" + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the JSON results.", e);
        }
        finally {
            if (urlConnection != null)
                urlConnection.disconnect();
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error with closing stream", e);
                }
            }
        }
        return jsonResponse;
    }

    /**
     * method for reading the raw binary code being sent from the server and turning it to readable characters
     * @param inputStream input stream of binary information from the server
     * @return json string to be parsed.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        // Due to us getting information over time the string gets mutated frequently so we put
        // a string builder instead of a normal string.
        StringBuilder output = new StringBuilder();

        if (inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null){
                output.append(line);
                line = bufferedReader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL makeUrl(String stringUrl){

        URL url = null;

        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem with creating url.", e);
        }
        return url;
    }

    private static List<News> parseJson(String jsonResponse){
        // Create an empty ArrayList that we can start adding earthquakes to
        List<News> news = new ArrayList<>();

        try {
            JSONObject baseObject = new JSONObject(jsonResponse);
            JSONObject response = baseObject.getJSONObject("response");
            JSONArray results = response.getJSONArray("results");

            for (int i =0; i < results.length(); i++){
                // get the JSON object that has the results and parse the needed info for our news array list.
                JSONObject result = results.getJSONObject(i);
                String title = result.getString("webTitle");
                String url = result.getString("webUrl");
                String section = result.getString("sectionName");
                String date = result.getString("webPublicationDate");

                // get the tags array and get the first tag as it is the object with the author's name.
                JSONArray tags = result.getJSONArray("tags");
                JSONObject firstTag = tags.getJSONObject(0);
                String author = firstTag.getString("webTitle");

                // Add all the parsed information to a News object.
                news.add(new News(title,url,section,date,author));
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the JSON response.", e);
        }

        return news;
    }
}
