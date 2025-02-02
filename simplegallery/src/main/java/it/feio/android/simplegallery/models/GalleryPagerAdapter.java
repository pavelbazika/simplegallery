package it.feio.android.simplegallery.models;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import it.feio.android.simplegallery.GalleryPagerFragment;

public class GalleryPagerAdapter extends FragmentStateAdapter {
	private final List<GalleryItem> resources;

	public GalleryPagerAdapter(FragmentActivity activity, List<GalleryItem> resources) {
		super(activity);
		this.resources = resources;
	}

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return GalleryPagerFragment.create(position, resources.get(position));
    }

    @Override
    public int getItemCount() {
        return resources.size();
    }
}