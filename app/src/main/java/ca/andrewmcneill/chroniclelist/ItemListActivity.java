package ca.andrewmcneill.chroniclelist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ca.andrewmcneill.chroniclelist.Helpers.DBHelper;
import ca.andrewmcneill.chroniclelist.adapters.BookAdapter;
import ca.andrewmcneill.chroniclelist.beans.Book;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;

import java.util.ArrayList;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener  {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private EditText searchBar;
    public static BookAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.hide();

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        customAdapter = new BookAdapter(this, new ArrayList<Book>(), mTwoPane, this);
        setupRecyclerView((RecyclerView) recyclerView, customAdapter);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                navSelected(item);
                return true;
            }
        });

        searchBar = findViewById(R.id.searchBar);
        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {
                if (actionID == EditorInfo.IME_ACTION_DONE || actionID == EditorInfo.IME_ACTION_SEARCH) {
                    searchDone();
                    return true;
                }
                return false;
            }
        });
        storedSelected();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);


        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("start_hot", false)) {
            bottomNavigationView.setSelectedItemId(R.id.hot);
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Use this when you actually want to grab a setting anywhere
        // PreferenceManager.getDefaultSharedPreferences(this).getString("signature", "default")

        if (key.equals("sync")) {
            Log.d("Pref", "Toggled sync: " + sharedPreferences.getBoolean("sync", false));
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;
            case R.id.credits:
                Intent j = new Intent(this, CreditsActivity.class);
                startActivity(j);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void navSelected(MenuItem item) {
        Log.d("BottomNavigation", "onNavigationItemSelected: " + item.toString());
        switch (item.getItemId()) {
            case R.id.stored:
                storedSelected();
                break;
            case R.id.hot:
                hotSelected();
                break;
            case R.id.search:
                searchSelected();
                break;
        }
    }

    private void hotSelected() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.goodreads.com/review/recent_reviews.xml?&key=";
        String key = "z1Gl9wmQ9FBFQiLqMSlxA";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+key,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Convert XML response into JSON, because XML is a cancer
                        XmlToJson xmlToJson = new XmlToJson.Builder(response).build();
                        JSONObject jsonObject = xmlToJson.toJson();
                        try {
                            JSONArray reviews = jsonObject.getJSONObject("GoodreadsResponse").getJSONObject("reviews").getJSONArray("review");
                            ArrayList<Book> books = new ArrayList<>();
                            for (int i = 0; i < reviews.length(); i++) {
                                JSONObject jsonBook = reviews.getJSONObject(i).getJSONObject("book");
                                Log.d("Hot", jsonBook.toString());
                                Log.d("Hot", jsonBook.getJSONObject("authors").toString());
                                Book book = new Book(
                                        jsonBook.getJSONObject("id").getString("content"),
                                        jsonBook.getString("title"),
                                        jsonBook.getJSONObject("authors").getJSONObject("author").getString("name"),
                                        jsonBook.getDouble("average_rating"),
                                        jsonBook.getString("image_url")
                                );
                                books.add(book);
                                Log.d("Hot", book.toString());
                            }

                            customAdapter.refresh(books, false); // refresh data in view
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // Convert XML response into JSON, because XML is a cancer

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Hot", "onErrorResponse: " + "That didn't work!");
            }
        });

    // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, RecyclerView.Adapter adapter) {
        recyclerView.setAdapter(adapter);
    }

    private void searchSelected() {
        if (searchBar.getVisibility() == View.GONE) {
            searchBar.setVisibility(View.VISIBLE);
            searchBar.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT);
        }
        else
            searchDone();
    }

    private void storedSelected() {
        DBHelper db = new DBHelper(this);
        ArrayList<Book> storedBooks = db.getAllBooks();
        customAdapter.refresh(storedBooks, true);
    }

    private void searchDone() {
        searchBar.setVisibility(View.GONE);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchBar.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        Log.d("Search", searchBar.getText().toString());
        String searchText = "";
        try {
            searchText = URLEncoder.encode(searchBar.getText().toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "https://www.goodreads.com/search/index.xml?q=";
        String key = "&key=z1Gl9wmQ9FBFQiLqMSlxA";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+searchText+key,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Convert XML response into JSON, because XML is a cancer
                        XmlToJson xmlToJson = new XmlToJson.Builder(response).build();
                        JSONObject jsonObject = xmlToJson.toJson();
                        try {
                            JSONObject results = jsonObject.getJSONObject("GoodreadsResponse").getJSONObject("search").getJSONObject("results");
                            Object worksObject = results.get("work");
                            ArrayList<Book> books = new ArrayList<>();
                            if (worksObject instanceof JSONArray) { // Multiple results
                                JSONArray works = (JSONArray) worksObject;
                                for (int i = 0; i < works.length(); i++) {
                                    JSONObject work = works.getJSONObject(i);
                                    JSONObject jsonBook = work.getJSONObject("best_book");
                                    Book book = new Book(
                                            jsonBook.getJSONObject("id").getString("content"),
                                            jsonBook.getString("title"),
                                            jsonBook.getJSONObject("author").getString("name"),
                                            work.getDouble("average_rating"),
                                            jsonBook.getString("image_url")
                                    );
                                    Log.d("Search", book.getTitle());
                                    books.add(book);
                                }
                            } else { // Only one result
                                JSONObject work = (JSONObject) worksObject;
                                JSONObject jsonBook = work.getJSONObject("best_book");
                                Book book = new Book(
                                        jsonBook.getJSONObject("id").getString("content"),
                                        jsonBook.getString("title"),
                                        jsonBook.getJSONObject("author").getString("name"),
                                        work.getDouble("average_rating"),
                                        jsonBook.getString("image_url")
                                );
                                Log.d("Search", book.getTitle());
                                books.add(book);
                            }

                            customAdapter.refresh(books,false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // Convert XML response into JSON, because XML is a cancer

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Search", "onErrorResponse: " + "That didn't work!");
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

}
