package it.feio.android.simplegallery.models;

import android.net.Uri;

public class GalleryItem {
    public GalleryItem(Uri _uri, boolean _primary) {
        mUri = _uri;
        mPrimary = _primary;
    }

    private Uri mUri;
    private boolean mPrimary;
}
