package ca.andrewmcneill.chroniclelist.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ca.andrewmcneill.chroniclelist.ItemDetailActivity;
import ca.andrewmcneill.chroniclelist.ItemDetailFragment;
import ca.andrewmcneill.chroniclelist.ItemListActivity;
import ca.andrewmcneill.chroniclelist.R;
import ca.andrewmcneill.chroniclelist.beans.Book;
import ca.andrewmcneill.chroniclelist.dummy.DummyContent;

//Externalize Recycler View Adapter from fragment

public class bookAdapter extends RecyclerView.Adapter<bookAdapter.ViewHolder> {

    private final ItemListActivity mParentActivity;
    private final List<Book> books;
    private final boolean mTwoPane;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // this stuff is still very confusing, really need to figure this out
            Book book = (Book) view.getTag(); //cast the selected itemView into a book??????
            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putString(ItemDetailFragment.API_ID, book.getApiID());
                ItemDetailFragment fragment = new ItemDetailFragment();
                fragment.setArguments(arguments);
                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.item_detail_container, fragment)
                        .commit();
            } else {
                Context context = view.getContext();
                Intent intent = new Intent(context, ItemDetailActivity.class);
                intent.putExtra(ItemDetailFragment.API_ID, book.getApiID());

                context.startActivity(intent);
            }
        }
    };

    public bookAdapter(ItemListActivity parent,
                       List<Book> books,
                       boolean twoPane) {
        this.books = books;
        mParentActivity = parent;
        mTwoPane = twoPane;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_item_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.bookTitle.setText(books.get(position).getTitle());
        holder.bookAuthor.setText(books.get(position).getAuthor());
        holder.bookRating.setText(Double.toString(books.get(position).getRating()));
        holder.itemView.setTag(books.get(position));
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public void refresh(ArrayList<Book> newBooks)
    {
        books.clear();
        books.addAll(newBooks);
        notifyDataSetChanged();
        Log.d("Adapter", "I got new Data");
    }

    //TODO: Modify ViewHolder, Setup Picasso API to fetch and load book cover images
    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView bookTitle;
        final TextView bookAuthor;
        final TextView bookRating;
        final ImageView bookCoverImage;

        ViewHolder(View view) {
            super(view);
            bookTitle = view.findViewById(R.id.bookTitleText);
            bookAuthor = view.findViewById(R.id.bookAuthor);
            bookCoverImage = view.findViewById(R.id.bookCoverImage);
            bookRating = view.findViewById(R.id.bookRating);
        }
    }
}
