package ca.andrewmcneill.chroniclelist.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ca.andrewmcneill.chroniclelist.R;
import ca.andrewmcneill.chroniclelist.beans.Book;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailBookFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailBookFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String BOOK_TITLE = "arg_title";
    private static final String BOOK_AUTHOR = "arg_author";
    private static final String BOOK_DESC = "arg_desc";
    private static final String BOOK_RATING = "arg_rating";
    private static final String BOOK_COVER = "arg_cover";
    private static final String TWO_PANE = "arg_twoPane";

    // TODO: Rename and change types of parameters
    private String title;
    private String author;
    private String desc;
    private Double rating;
    private String cover;
    private boolean twoPane;


    public DetailBookFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method for creating a book detail fragment
     *
     * @param book the book object in which you want to create the details fragment
     * @param twoPane boolean value which determines the layout used (tablet / phone)
     * @return A new instance of fragment DetailBookFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailBookFragment newInstance(Book book, boolean twoPane) {
        DetailBookFragment fragment = new DetailBookFragment();
        Bundle args = new Bundle();
        args.putString(BOOK_TITLE, book.getTitle());
        args.putString(BOOK_AUTHOR, book.getAuthor());
        args.putString(BOOK_DESC, book.getDescription());
        args.putDouble(BOOK_RATING, book.getRating());
        args.putString(BOOK_COVER, book.getCoverUrl());
        args.putBoolean(TWO_PANE, twoPane);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(BOOK_TITLE);
            author = getArguments().getString(BOOK_AUTHOR);
            desc = getArguments().getString(BOOK_DESC);
            rating = getArguments().getDouble(BOOK_RATING);
            cover = getArguments().getString(BOOK_COVER);
            twoPane = getArguments().getBoolean(TWO_PANE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // determine if user is in tablet or phone layout
        int layout = twoPane ? R.layout.fragment_detail_book_tablet : R.layout.fragment_detail_book_phone;
        View view = inflater.inflate(layout, container, false);
        TextView tAuthor = view.findViewById(R.id.book_author);
        TextView tTitle = view.findViewById(R.id.book_title);
        TextView tDesc = view.findViewById(R.id.book_desc);
        TextView tRating =  view.findViewById(R.id.book_rating);
        ImageView tCover = view.findViewById(R.id.book_detail_image);

        tTitle.setText(title);
        tAuthor.setText(author);
        tDesc.setText(desc);
        tRating.setText(Double.toString(rating));
        Picasso.get().load(cover).into(tCover);

        return view;
    }

}
