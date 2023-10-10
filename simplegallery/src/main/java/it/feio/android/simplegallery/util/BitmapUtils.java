/*******************************************************************************
 * Copyright 2014 Federico Iosue (federico.iosue@gmail.com)

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 *   http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package it.feio.android.simplegallery.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import androidx.exifinterface.media.ExifInterface;
import it.feio.android.simplegallery.R;


public class BitmapUtils {

	/**
	 * Creates a thumbnail of requested size by doing a first sampled decoding of the bitmap to optimize memory
	 */
	private static Bitmap doGetThumbnail(Bitmap srcBmp, Context mContext, Uri uri, int reqWidth, int reqHeight) {
		// If picture is smaller than required thumbnail
		Bitmap dstBmp;
		if (srcBmp.getWidth() < reqWidth && srcBmp.getHeight() < reqHeight) {
			dstBmp = ThumbnailUtils.extractThumbnail(srcBmp, reqWidth, reqHeight);

			// Otherwise the ratio between measures is calculated to fit requested thumbnail's one
		} else {
			// Cropping
			int x = 0, y = 0, width = srcBmp.getWidth(), height = srcBmp.getHeight();
			float ratio = ((float) reqWidth / (float) reqHeight) * ((float) srcBmp.getHeight() / (float) srcBmp
					.getWidth());
			if (ratio < 1) {
				x = (int) (srcBmp.getWidth() - srcBmp.getWidth() * ratio) / 2;
				width = (int) (srcBmp.getWidth() * ratio);
			} else {
				y = (int) (srcBmp.getHeight() - srcBmp.getHeight() / ratio) / 2;
				height = (int) (srcBmp.getHeight() / ratio);
			}

			int rotation = neededRotation(mContext, uri);
			if (rotation != 0) {
				Matrix matrix = new Matrix();
				matrix.postRotate(rotation);
				dstBmp = Bitmap.createBitmap(srcBmp, x, y, width, height, matrix, true);
			} else {
				dstBmp = Bitmap.createBitmap(srcBmp, x, y, width, height);
			}

		}
		return dstBmp;
	}

    @SuppressWarnings("unused")
    public static Bitmap getThumbnail(Context mContext, Uri uri, int reqWidth, int reqHeight) {
        Bitmap srcBmp = BitmapUtils.decodeSampledFromUri(mContext, uri, reqWidth, reqHeight);
        return doGetThumbnail(srcBmp, mContext, uri, reqWidth, reqHeight);
    }

    public static Bitmap getThumbnailOrThrow(Context mContext, Uri uri, int reqWidth, int reqHeight) throws ELoadFailed {
        Bitmap srcBmp = BitmapUtils.decodeSampledFromUriOrThrow(mContext, uri, reqWidth, reqHeight);
        return doGetThumbnail(srcBmp, mContext, uri, reqWidth, reqHeight);
    }


    /**
	 * Checks using EXIF data image's orientation
	 */
	public static int neededRotation(Context mContext, Uri uri) {
		try {
			try (InputStream is = mContext.getContentResolver().openInputStream(uri)) {
                ExifInterface exif = new ExifInterface(is);
                int orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                    return 270;
                }
                if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                    return 180;
                }
                if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                    return 90;
                }
                return 0;
            }
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static class ELoadFailed extends Exception {
    }

	/**
	 * Decodifica ottimizzata per la memoria dei bitmap
	 *
	 * @param uri
	 *            URI bitmap
	 * @param reqWidth
	 *            Larghezza richiesta
	 * @param reqHeight
	 *            Altezza richiesta
	 */
	public static Bitmap decodeSampledFromUriOrThrow(Context mContext, Uri uri, int reqWidth, int reqHeight) throws ELoadFailed {

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		InputStream inputStream = null;
		InputStream inputStreamSampled = null;
		try {
			inputStream = mContext.getContentResolver().openInputStream(uri);
			BitmapFactory.decodeStream(inputStream, null, options);

			// Setting decode options
			options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
			options.inJustDecodeBounds = false;

			// Bitmap is now decoded for real using calculated inSampleSize
			inputStreamSampled = mContext.getContentResolver().openInputStream(uri);
			return BitmapFactory.decodeStream(inputStreamSampled, null, options);
		} catch (IOException e) {
			Log.e("BitmapUtils", "Error");
			throw new ELoadFailed();
		} finally {
			try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (inputStreamSampled != null) {
                    inputStreamSampled.close();
                }
			} catch (IOException | NullPointerException e) {
				Log.e("BitmapUtils", "Failed to close streams");
			}
		}
	}

    public static Bitmap decodeSampledFromUri(Context mContext, Uri uri, int reqWidth, int reqHeight) {
	    try {
	        return decodeSampledFromUriOrThrow(mContext, uri, reqWidth, reqHeight);
        }
        catch (ELoadFailed e) {
            return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.image_broken);
        }
    }


    /**
	 * Decoding with inJustDecodeBounds=true to check sampling index without breaking memory
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
			throws FileNotFoundException {

		// Calcolo dell'inSampleSize e delle nuove dimensioni proporzionate
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}

            while ( (halfHeight / inSampleSize) > reqHeight * 2 || (halfWidth / inSampleSize) > reqWidth * 2) {
				inSampleSize *= 2;
			}
		}
		return inSampleSize;
	}


    /**
	 * Scales a bitmap to fit required ratio
	 */
	@SuppressWarnings("unused")
	private static Bitmap scaleImage(Context mContext, Bitmap bitmap, int reqWidth, int reqHeight) {

		// Get current dimensions AND the desired bounding box
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int boundingX = dpToPx(mContext, reqWidth);
		int boundingY = dpToPx(mContext, reqHeight);

		// Determine how much to scale: the dimension requiring less scaling is
		// closer to the its side. This way the image always stays inside your
		// bounding box AND either x/y axis touches it.
		float xScale = ((float) boundingX) / width;
		float yScale = ((float) boundingY) / height;
		float scale = Math.max(xScale, yScale);

		// Create a matrix for the scaling and add the scaling data
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);

		// Create a new bitmap and convert it to a format understood by the
		// ImageView

        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
	}


    /**
	 * Checks if a bitmap is null and returns a placeholder in its place
	 */
	public static int dpToPx(Context mContext, int dp) {
		float density = mContext.getResources().getDisplayMetrics().density;
		return Math.round((float) dp * density);
	}


}
