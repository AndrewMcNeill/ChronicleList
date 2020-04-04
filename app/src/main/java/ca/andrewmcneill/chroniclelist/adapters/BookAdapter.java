package ca.andrewmcneill.chroniclelist.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ca.andrewmcneill.chroniclelist.Helpers.DBHelper;
import ca.andrewmcneill.chroniclelist.ItemDetailActivity;
import ca.andrewmcneill.chroniclelist.ItemDetailFragment;
import ca.andrewmcneill.chroniclelist.ItemListActivity;
import ca.andrewmcneill.chroniclelist.R;
import ca.andrewmcneill.chroniclelist.beans.Book;

//Externalize Recycler View Adapter from fragment

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    private final ItemListActivity mParentActivity;
    private final List<Book> books;
    private final boolean mTwoPane;
    private Context context;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Book book = (Book) view.getTag(); // get the book obj from whatever is selected, pass apiID to detail view
            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putString(ItemDetailFragment.API_ID, book.getApiID());
                arguments.putBoolean(ItemDetailFragment.TWO_PANE, mTwoPane);
                ItemDetailFragment fragment = new ItemDetailFragment();
                fragment.setArguments(arguments);
                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.item_detail_container, fragment)
                        .commit();
            } else {
                Context context = view.getContext();
                Intent intent = new Intent(context, ItemDetailActivity.class);
                intent.putExtra(ItemDetailFragment.API_ID, book.getApiID());
                intent.putExtra(ItemDetailFragment.TWO_PANE, mTwoPane);
                context.startActivity(intent);
            }
        }
    };

    public BookAdapter(ItemListActivity parent,
                       List<Book> books,
                       boolean twoPane, Context context) {
        this.books = books;
        mParentActivity = parent;
        mTwoPane = twoPane;
        this.context = context;
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
        Picasso.get().load(books.get(position).getCoverUrl()).into(holder.bookCoverImage);
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
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        final TextView bookTitle;
        final TextView bookAuthor;
        final TextView bookRating;
        final ImageView bookCoverImage;

        ViewHolder(View view) {
            super(view);
            view.setOnLongClickListener(this);
            bookTitle = view.findViewById(R.id.bookTitleText);
            bookAuthor = view.findViewById(R.id.bookAuthor);
            bookCoverImage = view.findViewById(R.id.bookCoverImage);
            bookRating = view.findViewById(R.id.bookRating);
        }

        @Override
        public boolean onLongClick(final View view) {
            final String bookName = books.get(getLayoutPosition()).getTitle();
            new AlertDialog.Builder(context)
                    .setTitle("Remove Book")
                    .setMessage("Do you want to remove " + bookName + "?")
                    .setIcon(R.drawable.ic_delete_forever_black_24dp)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DBHelper db = new DBHelper(context);
                            db.deleteBook(books.get(getLayoutPosition()).getApiID());
                            books.remove(getAdapterPosition());
                            notifyItemRemoved(getAdapterPosition());
                            db.close();
                            Snackbar.make(view, bookName + " was removed!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    })
                    .setNegativeButton("No", null).show();
            return true;
        }
    }
}
