package it.feio.android.simplegallery.models;
import android.net.Uri;
import it.feio.android.simplegallery.GalleryPagerFragment;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class GalleryPagerAdapter extends FragmentStatePagerAdapter {

	private List<Uri> resources;


	public GalleryPagerAdapter(FragmentActivity activity, List<Uri> resources) {
		super(activity.getSupportFragmentManager());
		this.resources = resources;
	}

	@Override
	public Fragment getItem(int position) {
		try {
			return GalleryPagerFragment.create(position, resources.get(position));
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public int getCount() {
		return resources.size();
	}
}