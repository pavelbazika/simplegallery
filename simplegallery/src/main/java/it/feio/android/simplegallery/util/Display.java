package it.feio.android.simplegallery.util;


/*
public class Display {

	@SuppressLint({"NewApi", "ObsoleteSdkInt"})
	public static Point getUsableSize(Context mContext) {
		Point displaySize = new Point();
		try {
			WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
			if (manager != null) {
				android.view.Display display = manager.getDefaultDisplay();
				if (display != null) {
					if (android.os.Build.VERSION.SDK_INT < 13) {
                        //noinspection deprecation
                        displaySize.set(display.getWidth(), display.getHeight());
					} else {
						display.getSize(displaySize);
					}
				}
			}
		} catch (Exception e) {
			Log.e("checkDisplaySize", "Error checking display sizes", e);
		}
		return displaySize;
	}

}
*/
