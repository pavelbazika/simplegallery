package it.feio.android.simplegallery.util;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AndroidHelper {
    @SuppressLint("ObsoleteSdkInt")
    @Nullable
    public static <T extends Parcelable> T getParcelable(@NonNull Bundle _bundle, @Nullable String _key,
                                                         @NonNull Class<T> _clazz) {
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            return _bundle.getParcelable(_key, _clazz);
        }
        else {
            return getParcelableLegacy(_bundle, _key);
        }
    }

    @SuppressWarnings("deprecation")
    private static <T extends Parcelable> T getParcelableLegacy(@NonNull Bundle _bundle, @Nullable String _key) {
        Parcelable p = _bundle.getParcelable(_key);
        if (p == null) {
            return null;
        }
        else {
            try {
                //noinspection unchecked
                return (T)p;
            }
            catch (ClassCastException ignore) {
                return  null;
            }
        }
    }

    private AndroidHelper() {}
}
