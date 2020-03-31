package ca.andrewmcneill.chroniclelist;

import android.app.Activity;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ca.andrewmcneill.chroniclelist.adapters.SeriesPagerAdapter;
import ca.andrewmcneill.chroniclelist.beans.Book;
import ca.andrewmcneill.chroniclelist.fragments.DetailBookFragment;
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

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        View rootView = inflater.inflate(R.layout.book_detail, container, false);

        /** TODO: DO NOT KEEP
         *  TODO: DO THE API CALL HERE, GRAB ALL BOOKS (INCLUDING THOSE CONTAINED IN SERIES, USE THEM IN CREATING FRAGMENTS)
         */

        getClickedBook();

        Book dummyBook2 = new Book("TEST_ID_2", "TEST_TITLE_2","TEST_AUTHOR_2",2.0,"https://i.redd.it/r0ux7x1q2fo41.jpg", "Desc 2");
        Book dummyBook1 = new Book("TEST_ID_1", "EYEYEYEY","TEST_AUTHOR", 1.0,"https://i.redd.it/r0ux7x1q2fo41.jpg", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\n" +
                "\n" +
                "Curabitur pretium tincidunt lacus. Nulla gravida orci a odio. Nullam varius, turpis et commodo pharetra, est eros bibendum elit, nec luctus magna felis sollicitudin mauris. Integer in mauris eu nibh euismod gravida. Duis ac tellus et risus vulputate vehicula. Donec lobortis risus a elit. Etiam tempor. Ut ullamcorper, ligula eu tempor congue, eros est euismod turpis, id tincidunt sapien risus a quam. Maecenas fermentum consequat mi. Donec fermentum. Pellentesque malesuada nulla a mi. Duis sapien sem, aliquet nec, commodo eget, consequat quis, neque. Aliquam faucibus, elit ut dictum aliquet, felis nisl adipiscing sapien, sed malesuada diam lacus eget erat. Cras mollis scelerisque nunc. Nullam arcu. Aliquam consequat. Curabitur augue lorem, dapibus quis, laoreet et, pretium ac, nisi. Aenean magna nisl, mollis quis, molestie eu, feugiat in, orci. In hac habitasse platea dictumst.");

        // Create a fragment for each book, pass in twoPane argument to determine which layout to use (phone / tablet)
        DetailBookFragment dummyFragment1 = DetailBookFragment.newInstance(dummyBook1, twoPane);
        DetailBookFragment dummyFragment2 = DetailBookFragment.newInstance(dummyBook2, twoPane);
        // Place all the fragments into an array list
        ArrayList<DetailBookFragment> dummyList = new ArrayList<>();
        dummyList.add(dummyFragment2);
        dummyList.add(dummyFragment1);
        /** DO NOT KEEP **/

        // This can stay, only thing to modify from this is replace dummyList with an actual list of detail book fragments
        ViewPager detailViewPager = rootView.findViewById(R.id.detailViewPager);
        SeriesPagerAdapter seriesPagerAdapter = new SeriesPagerAdapter(getChildFragmentManager());
        detailViewPager.setAdapter(seriesPagerAdapter);
        seriesPagerAdapter.addFragmentsToViewPager(dummyList);
        return rootView;
    }

    private void getClickedBook() {

        RequestQueue queue = Volley.newRequestQueue(this.getContext());

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
                            String author = "Shit, this didn't work.";
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

        RequestQueue queue = Volley.newRequestQueue(this.getContext());

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
                            jsonObject.getJSONObject("GoodreadsResponse");
                        } catch (JSONException e) {

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
}
