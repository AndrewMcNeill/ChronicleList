package ca.andrewmcneill.chroniclelist.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import ca.andrewmcneill.chroniclelist.fragments.detailBookFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<detailBookFragment> bookFragments = new ArrayList<>();

    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public void addFragmentsToViewPager(ArrayList<detailBookFragment> fragments) {
        bookFragments.clear();
        bookFragments.addAll(fragments);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public detailBookFragment getItem(int position) {
        return bookFragments.get(position);
    }

    @Override
    public int getCount() {
        return bookFragments.size();
    }
}
