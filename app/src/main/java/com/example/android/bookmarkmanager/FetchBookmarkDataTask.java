package com.example.android.bookmarkmanager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.format.Time;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by vpetrosyan on 20.05.2015.
 */
public class FetchBookmarkDataTask extends AsyncTask<Void,Void,Void> {

    //TODO change logic into recursive

    private final String TAG = FetchBookmarkDataTask.class.getSimpleName();

    private final String BASE_URL = "http://bkmanager.webege.com/file.json";

    private DatabaseHandler dbHandler;

    private CategoryListAdapter catAdapter_;

    private ProgressDialog progressDialog_;

    private Activity loaderActivity_;

    @Override
    protected Void doInBackground(Void... params) {

        if(dbHandler == null)
        {
            Log.e(TAG,"DATABASE ERROR");
            return null;
        }

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast

            Uri builtURI = Uri.parse(BASE_URL).buildUpon().build();

            String myUrl = builtURI.toString();

            URL url = new URL(myUrl);

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            forecastJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }

        Log.v(TAG,forecastJsonStr);

      try {
            return getBookMarkDataFromJson(forecastJsonStr);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */

    private Void getBookMarkDataFromJson(String forecastJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_CHILDREN = "children";
        final String OHM_MODIFIED_TIME = "dateGroupModified";

        final String OWM_TITLE = "title";
        final String OWM_URL = "url";
        final String OWM_ID = "id";
        final String OWM_parID = "parentId";
        final String OWM_index = "index";
        final String OWM_ADDED_TIME = "dateAdded";

        JSONArray bookmarkJson = new JSONArray(forecastJsonStr);

        ArrayList<SimpleBookmarkCategory> bookMarkData = new ArrayList<>();

        for(int i=0 ; i < bookmarkJson.length(); i++)
        {
            JSONObject currentItem = bookmarkJson.getJSONObject(i);

            JSONArray folders = currentItem.getJSONArray(OWM_CHILDREN);

            for(int j=0; j < folders.length(); j++)
            {
                JSONObject currentFolder = folders.getJSONObject(j);

                String folderTitle = currentFolder.getString(OWM_TITLE);

                long folderCreationTime = currentFolder.getLong(OWM_ADDED_TIME);
                long folderModifiedTime = currentFolder.getLong(OHM_MODIFIED_TIME);

                int folderID = Integer.parseInt(currentFolder.getString(OWM_ID));
                int folderIndex = currentFolder.getInt(OWM_index);
                int folderParent = Integer.parseInt(currentFolder.getString(OWM_parID));

                SimpleBookmarkCategory catItem = new SimpleBookmarkCategory(folderTitle,
                                                                            folderCreationTime,
                                                                            folderModifiedTime,
                                                                            folderID,
                                                                            folderIndex,
                                                                            folderParent);

                JSONArray itemsInFolder = currentFolder.getJSONArray(OWM_CHILDREN);

                for(int h=0; h < itemsInFolder.length(); h++)
                {
                    JSONObject bkItem = itemsInFolder.getJSONObject(h);

                    String bkTitle = bkItem.getString(OWM_TITLE);

                    String bkUrl = bkItem.getString(OWM_URL);

                    long bkCreationTime = bkItem.getLong(OWM_ADDED_TIME);

                    int bkID = Integer.parseInt(bkItem.getString(OWM_ID));
                    int bkIndex = bkItem.getInt(OWM_index);
                    int bkParent = Integer.parseInt(bkItem.getString(OWM_parID));

                    SimpleBookmarkEntry bkEntry = new SimpleBookmarkEntry(bkUrl,bkTitle,bkCreationTime,bkID,bkIndex,bkParent);

                    bkEntry.setIsScheduled_(true);

                    catItem.addSimpleBookmark(bkEntry);

                    dbHandler.addBookmark(bkEntry);
                }

                dbHandler.addCategory(catItem);
                bookMarkData.add(catItem);
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(loaderActivity_ != null) {
            progressDialog_ = new ProgressDialog(loaderActivity_);
            progressDialog_.setIcon(R.mipmap.ic_launcher);
            progressDialog_.setTitle("Updating");
            progressDialog_.show();
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressDialog_.dismiss();
        if (!isCancelled()) {
            catAdapter_.setNewData(dbHandler.getAllCategories());
        }
    }

    public void setDatabaseHandler(DatabaseHandler dbHandler) {
        if(dbHandler != null) {

            if(dbHandler.getCategoriesCount() != 0)
            {
                dbHandler.dropDb();
            }

            this.dbHandler = dbHandler;
        }
        else
        {
            Log.e(TAG,"DATABASE ERROR");
        }

    }

    public void setCatAdapter(CategoryListAdapter catAdapter_) {
        this.catAdapter_ = catAdapter_;
    }

    public void setLoaderActivity(Activity loaderActivity_) {
        this.loaderActivity_ = loaderActivity_;
    }
}