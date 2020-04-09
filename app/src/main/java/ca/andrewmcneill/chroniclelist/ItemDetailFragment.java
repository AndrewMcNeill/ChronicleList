package ca.andrewmcneill.chroniclelist;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ca.andrewmcneill.chroniclelist.adapters.SeriesPagerAdapter;
import ca.andrewmcneill.chroniclelist.beans.Book;
import ca.andrewmcneill.chroniclelist.fragments.DetailBookFragment;
import ca.andrewmcneill.chroniclelist.transformers.ZoomOutPageTransformer;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {

    public static final String API_ID = "api_id";
    public static final String TWO_PANE = "two_pane";

    private String apiID;
    private boolean twoPane;
    private int clickedBook = 0;
    private ViewPager detailViewPager;

    private Context context;
    private Context activityContext;
    private SeriesPagerAdapter seriesPagerAdapter;
    private View rootView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        activityContext = getActivity();

        seriesPagerAdapter = new SeriesPagerAdapter(getChildFragmentManager());

        assert getArguments() != null;
        if (getArguments().containsKey(API_ID)) {
            apiID = getArguments().getString(API_ID);
            twoPane = getArguments().getBoolean(TWO_PANE);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitleEnabled(true);
                appBarLayout.setTitle(apiID);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.book_detail, container, false);
        detailViewPager = rootView.findViewById(R.id.detailViewPager);
        getClickedBook();
        return rootView;
    }

    private void getClickedBook() {

        RequestQueue queue = Volley.newRequestQueue(context);

        final String url = "https://www.goodreads.com/book/show/"+apiID+".xml?key=";
        final String key = "z1Gl9wmQ9FBFQiLqMSlxA";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+key,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        XmlToJson xmlToJson = new XmlToJson.Builder(response).build();
                        JSONObject jsonObject = xmlToJson.toJson();
                        try {
                            JSONObject jsonBook = jsonObject.getJSONObject("GoodreadsResponse").getJSONObject("book");
                            String author = "";
                            Object authorObject = jsonBook.getJSONObject("authors").get("author");
                            if (authorObject instanceof JSONObject)
                                author = ((JSONObject) authorObject).getString("name");
                            else if (authorObject instanceof JSONArray)
                                author = ((JSONArray) authorObject).getJSONObject(0).getString("name");
                            String description ="No description available.";
                            try {
                                description = jsonBook.getString("description");
                            } catch (JSONException e) {
                                Log.d("Book", "No description for " + jsonBook.getString("title"));
                            }
                            Book book = new Book(
                                    jsonBook.getString("id"),
                                    jsonBook.getString("title"),
                                    author,
                                    jsonBook.getDouble("average_rating"),
                                    jsonBook.getString("image_url"),
                                    description
                            );
                            Log.d("Book", "Description: " + book.getDescription());


                            if (!PreferenceManager.getDefaultSharedPreferences(activityContext).getBoolean("enable_series", true)) {
                                ArrayList<String> bookIDs = new ArrayList<>();
                                bookIDs.add(apiID);
                                populateViewPager(bookIDs);
                                return;
                            }

                            try {
                                JSONObject seriesObject = jsonBook.getJSONObject("series_works");
                                String seriesID="";
                                try {
                                    JSONArray seriesArray = seriesObject.getJSONArray("series_work");
                                    seriesID = seriesArray.getJSONObject(0).getJSONObject("series").getString("id");
                                } catch (JSONException e) {
                                    Log.d("Book", "Book is in only one series.");
                                    seriesID = seriesObject.getJSONObject("series_work").getJSONObject("series").getString("id");
                                }
                                getSeries(seriesID);
                            } catch (JSONException e) {
                                Log.d("Book", "Book is not in a series.");
                                ArrayList<String> bookIDs = new ArrayList<>();
                                bookIDs.add(apiID);
                                populateViewPager(bookIDs);
                            }
                        } catch (JSONException e) {
                            Log.d("Book", e.toString());
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Book", error.toString());
                Log.d("Book", url+key);
                Log.d("Book", "onErrorResponse: " + "getClickedBook didn't work!");
            }
        });

        queue.add(stringRequest);
    }

    private void getSeries(String seriesID) {

        RequestQueue queue = Volley.newRequestQueue(context);

        final String url = "https://www.goodreads.com/series/show/" + seriesID + ".xml?key=";
        final String key = "z1Gl9wmQ9FBFQiLqMSlxA";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + key,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        XmlToJson xmlToJson = new XmlToJson.Builder(response).build();
                        JSONObject jsonObject = xmlToJson.toJson();
                        try {
                            Log.d("Series", jsonObject.toString());
                            JSONObject series_works = jsonObject.getJSONObject("GoodreadsResponse").getJSONObject("series").getJSONObject("series_works");
                            JSONArray works = series_works.getJSONArray("series_work");
                            ArrayList<String> bookIDs = new ArrayList<>();
                            for (int i = 0; i < works.length(); i++) {
                                JSONObject work = works.getJSONObject(i).getJSONObject("work");
                                JSONObject jsonBook = work.getJSONObject("best_book");
                                String id = jsonBook.getString("id");
                                if (id.equals(apiID))
                                    clickedBook = i;
                                Log.d("Series", id + " " + apiID + " " + clickedBook);
                                bookIDs.add(id);
                            }
                            populateViewPager(bookIDs);
                        } catch (JSONException e) {
                            Log.d("Series", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Series", error.toString());
                Log.d("Series", url + key);
                Log.d("Series", "onErrorResponse: " + "getSeries didn't work!");
            }
        });

        queue.add(stringRequest);
    }

    private void populateViewPager(ArrayList<String> ids) {
        ArrayList<DetailBookFragment> bookFragments = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {

            DetailBookFragment bookFragment = DetailBookFragment.newInstance(ids.get(i), twoPane);
            bookFragments.add(bookFragment);
        }
        Log.d("Pager", "Setting up viewpager " + clickedBook);
        detailViewPager.setAdapter(seriesPagerAdapter);
        detailViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        seriesPagerAdapter.addFragmentsToViewPager(bookFragments);
        detailViewPager.setOffscreenPageLimit(ids.size());
        detailViewPager.setCurrentItem(clickedBook);
        if (bookFragments.size() > 1) {
            final SharedPreferences sharedPreferences = activityContext.getSharedPreferences("tutorial", Context.MODE_PRIVATE);
            if (!sharedPreferences.getBoolean("dismissedSeries", false)) {
                Snackbar.make(rootView, "Swipe Left or Right to view other books in the series!", Snackbar.LENGTH_INDEFINITE)
                        .setAction("DISMISS", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                sharedPreferences.edit().putBoolean("dismissedSeries", true).apply();
                            }
                        }).show();
            }
        }
    }
}
