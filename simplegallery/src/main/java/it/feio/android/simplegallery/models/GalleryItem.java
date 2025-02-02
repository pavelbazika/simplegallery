package it.feio.android.simplegallery.models;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GalleryItem {
    public GalleryItem(@NonNull Uri _uri, boolean _primary) {
        mUri = _uri;
        mPrimary = _primary;
    }

    @NonNull
    public Uri getUri() {
        return mUri;
    }

    public boolean isPrimary() {
        return mPrimary;
    }

    @NonNull
    private final Uri mUri;
    private final boolean mPrimary;
}
