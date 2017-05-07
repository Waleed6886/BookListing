package com.example.waleed.booklistingapplecation;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.graphics.BitmapCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    Button button;

    /** Tag for the log messages */
    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    //?q=android&maxResults=1
    private static final String GOOGLE_BOOKS_URL ="https://www.googleapis.com/books/v1/volumes";
    BooksAdapter booksAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.search_bar);
        button = (Button) findViewById(R.id.search_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookAsyncTask bookAsyncTask = new BookAsyncTask();
                bookAsyncTask.execute(editText.getText().toString());
            }
        });
        //ArrayList<Books> booksArrayList = QueryUtils.extractBooks();
        booksAdapter = new BooksAdapter(this);
//         Get a reference to the ListView, and attach the adapter to the listView.
        ListView listView = (ListView) findViewById(R.id.listview_books);
        listView.setAdapter(booksAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("books",booksAdapter.getBooks());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        booksAdapter.update(savedInstanceState.<Books>getParcelableArrayList("books"));
    }

    private class BookAsyncTask extends AsyncTask<String, Void ,ArrayList<Books>>{
        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage(getString(R.string.loading));
            pd.show();
        }

        @Override
        protected ArrayList<Books> doInBackground(String... params) {
            Uri uri = Uri.parse(GOOGLE_BOOKS_URL);
            Uri.Builder uriBuilder = uri.buildUpon();

            uriBuilder.appendQueryParameter("q", params[0]);
            uriBuilder.appendQueryParameter("maxResults", "10");
            uri = uriBuilder.build();

            Log.d("uri",uri.toString());

            // Create URL object
            //URL url = createUrl(GOOGLE_BOOKS_URL);

            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";

            try {
                jsonResponse = makeHttpRequest(new URL(uri.toString()));
                Log.d("json",jsonResponse);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Extract relevant fields from the JSON response and create an {@link Books} object

            return extractBooks(jsonResponse);
        }
        /**
         * Update the screen with the given Books (which was the result of the
         * {@link BookAsyncTask}).
         */
        @Override
        protected void onPostExecute(ArrayList<Books> books) {
            pd.dismiss();
            if (isOnline() == false){
                Toast.makeText(MainActivity.this,
                        "No internet available", Toast.LENGTH_LONG).show();
            }
            if (books == null && isOnline() == true) {
                Toast.makeText(MainActivity.this,
                        "No Book Available", Toast.LENGTH_LONG).show();
                return;
            }
            booksAdapter.update(books);
        }

        /**
         * Returns new URL object from the given string URL.
         */
        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;
            }
            return url;
        }

        /**
         * Make an HTTP request to the given URL and return a String as the response.
         */
        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";

            if (url == null){
                Toast.makeText(MainActivity.this,
                        "Enter Book name", Toast.LENGTH_LONG).show();
                return jsonResponse;
            }
            HttpsURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                // setup URL connection
                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                // actual connection
                urlConnection.connect();
                //receiving the response
                Log.d("responseCode",urlConnection.getResponseCode()+"");
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                }

            } catch (IOException e) {
                // TODO: Handle the exception
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    // function must handle java.io.IOException here
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        public boolean isOnline() {
            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if(netInfo != null && netInfo.isConnectedOrConnecting())
               return true;
            else
                return false;

        }
        /**
         * Convert the {@link InputStream} into a String which contains the
         * whole JSON response from the server.
         */
        private String readFromStream(InputStream inputStream) throws IOException {
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
        public  ArrayList<Books> extractBooks(String booksJSON) {
            if (TextUtils.isEmpty(booksJSON)) {
                return null;
            }
            // Create an empty ArrayList that we can start adding earthquakes to
            ArrayList<Books> booksArrayList = new ArrayList<>();

            // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
            // is formatted, a JSONException exception object will be thrown.
            // Catch the exception so the app doesn't crash, and print the error message to the logs.
            try {

                JSONObject root = new JSONObject(booksJSON);
                JSONArray itemsArray = root.getJSONArray("items");

                for (int i =0; i<itemsArray.length(); i++) {

                    JSONObject currentBook = itemsArray.getJSONObject(i);
                    JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                    String title = volumeInfo.getString("title");
                    JSONArray authorJsonArray = volumeInfo.getJSONArray("authors");
                    JSONObject jsonObject = volumeInfo.getJSONObject("imageLinks");
                    String imageUrl = jsonObject.getString("smallThumbnail");
                    URL url = new URL(imageUrl);
                    Bitmap img = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                    booksArrayList.add(new Books(authorJsonArray , title,img));
                }

            } catch (JSONException e) {
                // If an error is thrown when executing any of the above statements in the "try" block,
                // catch the exception here, so the app doesn't crash. Print a log message
                // with the message from the exception.
                Log.e("QueryUtils", "Problem parsing the Books JSON results", e);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return booksArrayList;
        }

    }
}
