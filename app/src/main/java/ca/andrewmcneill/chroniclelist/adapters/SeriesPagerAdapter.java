package ca.andrewmcneill.chroniclelist.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import ca.andrewmcneill.chroniclelist.fragments.DetailBookFragment;

public class SeriesPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<DetailBookFragment> bookFragments = new ArrayList<>();

    public SeriesPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public void addFragmentsToViewPager(ArrayList<DetailBookFragment> fragments) {
        bookFragments.clear();
        bookFragments.addAll(fragments);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DetailBookFragment getItem(int position) {
        return bookFragments.get(position);
    }

    @Override
    public int getCount() {
        return bookFragments.size();
    }
}
