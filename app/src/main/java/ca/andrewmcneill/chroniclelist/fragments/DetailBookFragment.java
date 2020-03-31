package ca.andrewmcneill.chroniclelist.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ca.andrewmcneill.chroniclelist.R;
import ca.andrewmcneill.chroniclelist.beans.Book;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailBookFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailBookFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String BOOK_ID = "arg_id";
    private static final String BOOK_TITLE = "arg_title";
    private static final String BOOK_AUTHOR = "arg_author";
    private static final String BOOK_DESC = "arg_desc";
    private static final String BOOK_RATING = "arg_rating";
    private static final String BOOK_COVER = "arg_cover";
    private static final String TWO_PANE = "arg_twoPane";

    // TODO: Rename and change types of parameters
    private String id;
    private String title;
    private String author;
    private String desc;
    private Double rating;
    private String cover;
    private boolean twoPane;

    private TextView tAuthor;
    private TextView tTitle;
    private TextView tDesc;
    private TextView tRating;
    private ImageView tCover;



    public DetailBookFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method for creating a book detail fragment
     *
     * @param bookID the book object in which you want to create the details fragment
     * @param twoPane boolean value which determines the layout used (tablet / phone)
     * @return A new instance of fragment DetailBookFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailBookFragment newInstance(String bookID, boolean twoPane) {
        DetailBookFragment fragment = new DetailBookFragment();
        Bundle args = new Bundle();
//        args.putString(BOOK_TITLE, book.getTitle());
//        args.putString(BOOK_AUTHOR, book.getAuthor());
//        args.putString(BOOK_DESC, book.getDescription());
//        args.putDouble(BOOK_RATING, book.getRating());
//        args.putString(BOOK_COVER, book.getCoverUrl());
        args.putString(BOOK_ID, bookID);
        args.putBoolean(TWO_PANE, twoPane);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            title = getArguments().getString(BOOK_TITLE);
//            author = getArguments().getString(BOOK_AUTHOR);
//            desc = getArguments().getString(BOOK_DESC);
//            rating = getArguments().getDouble(BOOK_RATING);
//            cover = getArguments().getString(BOOK_COVER);
            id = getArguments().getString(BOOK_ID);
            twoPane = getArguments().getBoolean(TWO_PANE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // determine if user is in tablet or phone layout
        int layout = twoPane ? R.layout.fragment_detail_book_tablet : R.layout.fragment_detail_book_phone;
        View view = inflater.inflate(layout, container, false);
        tAuthor = view.findViewById(R.id.book_author);
        tTitle = view.findViewById(R.id.book_title);
        tDesc = view.findViewById(R.id.book_desc);
        tRating =  view.findViewById(R.id.book_rating);
        tCover = view.findViewById(R.id.book_detail_image);

        Log.d("Pager", "Test");
        getBook();

        return view;
    }

    private void getBook() {
        RequestQueue queue = Volley.newRequestQueue(this.getContext());

        final String url = "https://www.goodreads.com/book/show/"+id+".xml?key=";
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
                            inflateBook(book);
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

    private void inflateBook(Book book) {

        tTitle.setText(book.getTitle());
        tAuthor.setText(book.getAuthor());
        tDesc.setText(book.getDescription());
        tRating.setText(Double.toString(book.getRating()));
        Picasso.get().load(book.getCoverUrl()).into(tCover);
    }
}
