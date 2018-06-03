package com.example.danie.newsapp;

import android.text.TextUtils;
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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class NewsUtils {

    private static final String LOG_TAG = NewsUtils.class.getSimpleName();
    private static final int READ_TIMEOUT = 10000;
    private static final int CON_TIMEOUT = 15000;

    public static List<News> fetchNews(String requestUrl){
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            String httpFailReq = NewsApp.getResourcesStatic().getString(R.string.http_req_failed);
            Log.e(LOG_TAG, httpFailReq, e);
        }

        // Return the list of {@link News}s
        return extractFeatureFromJson(jsonResponse);
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            String urlCreateFail = NewsApp.getResourcesStatic().getString(R.string.url_create_failed);
            Log.e(LOG_TAG, urlCreateFail, e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT /* milliseconds */);
            urlConnection.setConnectTimeout(CON_TIMEOUT /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                String errorCode = NewsApp.getResourcesStatic().getString(R.string.error_code);
                Log.e(LOG_TAG, errorCode + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            String getJSONFailed  = NewsApp.getResourcesStatic().getString(R.string.get_json_failed);
            Log.e(LOG_TAG, getJSONFailed, e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<News> extractFeatureFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news articles to
        List<News> news = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(newsJSON);
            JSONObject responseObj = baseJsonResponse.getJSONObject("response");
            JSONArray resultArr = responseObj.getJSONArray("results");

            for (int i = 0; i < resultArr.length(); i++) {
                // Extract out the first feature (which is an article)
                JSONObject results = resultArr.getJSONObject(i);
                JSONArray tagsArr = results.getJSONArray("tags");

                // Extract out the title, section, author (if it exists), date and url
                String nTitle = results.getString("webTitle");
                String nSection = results.getString("sectionName");
                String nDate = results.getString("webPublicationDate");
                String nURL = results.getString("webUrl");
                String nAuthor = null;

                // if there is the JSONArray is not empty get the Author
                if (tagsArr.length() > 0) {
                    for (int j = 0; j < tagsArr.length(); j++) {
                        JSONObject tags = tagsArr.getJSONObject(j);
                        nAuthor = tags.getString("webTitle");
                    }
                // else, Author will not be defined
                } else {
                    nAuthor = NewsApp.getResourcesStatic().getString(R.string.author_not_defined);
                }

                // Create a new {@link News} object
                News currentNews = new News(nTitle, nSection, nAuthor, nDate, nURL);

                // add currentNews to the List of news
                news.add(currentNews);
            }
        } catch (JSONException e) {
            String errorParseJSON = NewsApp.getResourcesStatic().getString(R.string.parse_json_failed);
            Log.e(LOG_TAG, errorParseJSON, e);
        }
        return news;
    }
}
