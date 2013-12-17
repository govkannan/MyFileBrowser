package com.ps.myfilebrowser.util;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeSet;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.ps.myfilebrowser.data.MyFile;
import com.ps.myfilebrowser.data.MyFileComparator;
import com.ps.myfilebrowser.data.MyFileOptions;

public class BrowserUtil {
	
	public static final int ICON_SIZE=40;

	private static Bitmap DIR_ICON;
	private static Bitmap TXT_ICON;
	private static Bitmap AUDIO_ICON;
	private static Bitmap IMAGE_ICON;
	private static Bitmap VIDEO_ICON;
	private static Bitmap PDF_ICON;
	private static Bitmap HTML_ICON;
	private static Bitmap APK_ICON;
	private static Bitmap UNKNOWN_ICON;
	private static Bitmap BACK_FOLDER_ICON;
	private static Bitmap XML_ICON;
	private static Bitmap VCF_ICON;
	private static Bitmap ZIP_ICON;
	private static Bitmap DOC_ICON;
	private static Bitmap XLS_ICON;
	
	
	private static Bitmap PLAY_VIDEO_SMALL_ICON;
	
	
	private static MemoryInfo mi = new MemoryInfo();
	private static ActivityManager activityManager;
	
	public static Activity activity;
	
	 protected static PowerManager.WakeLock mWakeLock;
	
	
//	public static ArrayList<MyFile>  getAllFiles(String path, int options) {
//		
//		File file = new File(path);
//		
//		ArrayList<MyFile> toRet = new ArrayList<MyFile>();
//		
//		if (file.isDirectory() && file.canRead()) {
//			
////			if (!path.equals(MyFile.ROOT)) {
////				toRet.add(new MyFile("..", new File(file.getParent())));
////			}
//			
//			File[] files = file.listFiles();
//			for (File f : files) {
//				toRet.add(new MyFile(f));
//			}
//		}
//		return toRet;
//	}
//	
	
	public static ArrayList<MyFile> getFiles(String path, MyFileOptions options) {
		
		File file = new File(path);
		
		ArrayList<MyFile> toRetEnd = new ArrayList<MyFile>();
		
		TreeSet<MyFile> toRet = new TreeSet<MyFile>(new MyFileComparator()); 
		
		if (file.isDirectory() && file.canRead()) {
			
//			if (!path.equals(MyFile.ROOT)) {
//				toRet.add(new MyFile("..", new File(file.getParent())));
//			}
			
			File[] files = file.listFiles();
			if (files == null) {
				return toRetEnd;
			}
			
			MyFile tmp;
			for (File f : files) {
				if (f.canRead() || f.canWrite()) {
					tmp = new MyFile(f);
					
					if (tmp.isMatch(options)) {
							toRet.add(new MyFile(f));
					}
					
				}
			}
		}
		toRetEnd = new ArrayList<MyFile>(toRet);
		return toRetEnd;
	}
	
	public static Bitmap createIcon(String uri) {
		try {
			
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
		
			BitmapFactory.decodeFile(uri, options);
		
			options.inSampleSize = options.outHeight / ICON_SIZE;
		
			options.inJustDecodeBounds = false;
		
			return BitmapFactory.decodeFile(uri, options);
		} catch(Exception ex) {
			return null;
		}
	}
	
	public static Bitmap getIconFromResource(int resId) {
		return BitmapFactory.decodeResource(activity.getResources(), resId);
	}
	
	
	public static Bitmap getIcon(int type) {
		
		switch(type) {
		case MyFile.MYFILE_TYPE_DIR :
			if (DIR_ICON == null) {
				DIR_ICON = BitmapFactory.decodeResource(activity.getResources(),
						com.ps.myfilebrowser.R.drawable.live_folder_notes);
						
			}
			return DIR_ICON;
			
		case MyFile.MYFILE_TYPE_TXT:;
			if (TXT_ICON == null) {
				TXT_ICON = BitmapFactory.decodeResource(activity.getResources(),
					com.ps.myfilebrowser.R.drawable.app_notes);
				
			}
			return TXT_ICON;
		
		case MyFile.MYFILE_TYPE_IMAGE:
			if (IMAGE_ICON == null) {
				IMAGE_ICON = BitmapFactory.decodeResource(activity.getResources(),
					com.ps.myfilebrowser.R.drawable.default_image_icon);
			}
		return IMAGE_ICON;
		
		case MyFile.MYFILE_TYPE_AUDIO:
			if (AUDIO_ICON == null) {
				AUDIO_ICON = BitmapFactory.decodeResource(activity.getResources(),
					com.ps.myfilebrowser.R.drawable.ic_stat_playing);
			}
			return AUDIO_ICON;
			
		case MyFile.MYFILE_TYPE_VIDEO:
			if (VIDEO_ICON == null) {
				VIDEO_ICON = BitmapFactory.decodeResource(activity.getResources(),
					com.ps.myfilebrowser.R.drawable.default_video_icon);
			}
			return VIDEO_ICON;
			
		case MyFile.MYFILE_TYPE_APK:
			if (APK_ICON == null) {
				APK_ICON = BitmapFactory.decodeResource(activity.getResources(),
					com.ps.myfilebrowser.R.drawable.default_icon_apk);
			}
			return APK_ICON;
			
		case MyFile.MYFILE_TYPE_PDF:
			if (PDF_ICON == null) {
				PDF_ICON = BitmapFactory.decodeResource(activity.getResources(),
					com.ps.myfilebrowser.R.drawable.pdf_icon);
			}
			return PDF_ICON;
			
		case MyFile.MYFILE_TYPE_HTML:
			if (HTML_ICON == null) {
				HTML_ICON = BitmapFactory.decodeResource(activity.getResources(),
					com.ps.myfilebrowser.R.drawable.html2);
			}
			return HTML_ICON;

		case MyFile.MYFILE_TYPE_XML:
			if (XML_ICON == null) {
				XML_ICON = BitmapFactory.decodeResource(activity.getResources(),
					com.ps.myfilebrowser.R.drawable.xml_icon);
			}
			return XML_ICON;
			
		case MyFile.MYFILE_TYPE_VCF:
			if (VCF_ICON == null) {
				VCF_ICON = BitmapFactory.decodeResource(activity.getResources(),
					com.ps.myfilebrowser.R.drawable.vcard_icon);
			}
			return VCF_ICON;

		case MyFile.MYFILE_TYPE_ZIP:
			if (ZIP_ICON == null) {
				ZIP_ICON = BitmapFactory.decodeResource(activity.getResources(),
					com.ps.myfilebrowser.R.drawable.zip_icon);
			}
			return ZIP_ICON;
		case MyFile.MYFILE_TYPE_DOC:
			if (DOC_ICON == null) {
				DOC_ICON = BitmapFactory.decodeResource(activity.getResources(),
					com.ps.myfilebrowser.R.drawable.doc_icon);
			}
			return DOC_ICON;
		case MyFile.MYFILE_TYPE_XLS:
			if (XLS_ICON == null) {
				XLS_ICON = BitmapFactory.decodeResource(activity.getResources(),
					com.ps.myfilebrowser.R.drawable.xls_icon);
			}
			return XLS_ICON;

		default:
			if (UNKNOWN_ICON == null) {
				UNKNOWN_ICON = BitmapFactory.decodeResource(activity.getResources(),
					com.ps.myfilebrowser.R.drawable.default_unknow_icon);
			}
			return UNKNOWN_ICON;
		}
	}
	
	public static Bitmap getBackFolderIcon() {
		if (BACK_FOLDER_ICON == null) {
			BACK_FOLDER_ICON = BitmapFactory.decodeResource(activity.getResources(),
					com.ps.myfilebrowser.R.drawable.ic_menu_revert);
		}
		
		return BACK_FOLDER_ICON;
		
	}
	
	public static Bitmap createIconForVideo(String url) {
		try {
			
			Bitmap toRet =ThumbnailUtils.createVideoThumbnail(url, android.provider.MediaStore.Video.Thumbnails.MICRO_KIND);
			if (PLAY_VIDEO_SMALL_ICON == null) {
				PLAY_VIDEO_SMALL_ICON =	BitmapFactory.decodeResource(activity.getResources(),
								com.ps.myfilebrowser.R.drawable.play_icon_19_19);
			}
			
			Canvas c = new Canvas(toRet);
			c.drawBitmap(PLAY_VIDEO_SMALL_ICON, new Rect(0,0,PLAY_VIDEO_SMALL_ICON.getWidth(), PLAY_VIDEO_SMALL_ICON.getHeight()),
					new Rect(
							toRet.getWidth() -PLAY_VIDEO_SMALL_ICON.getWidth(),
							toRet.getHeight() -PLAY_VIDEO_SMALL_ICON.getHeight(),
							toRet.getWidth(),
							toRet.getHeight()
							), null);
			
			
			return toRet; 
		} catch (Exception ex) {
			return null;
		}
	}
	
	
	public static Bitmap loadImage(String uri, Activity parent, boolean isPortrait) {
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		
		DisplayMetrics metrics = parent.getResources().getDisplayMetrics();
		
		int width, height;
		
		options.inJustDecodeBounds = true;
		
		BitmapFactory.decodeFile(uri, options);
		
		height = metrics.heightPixels > options.outHeight ? options.outHeight : metrics.heightPixels;
		width = metrics.widthPixels > options.outWidth ? options.outWidth : metrics.widthPixels;
		
//		if (isPortrait) {
//			options.inSampleSize = options.outHeight / 250;
//		} else {
//			options.inSampleSize = options.outWidth / metrics.widthPixels;
//		}
		
//		if (options.outWidth > metrics.widthPixels) {
			options.inSampleSize = options.outWidth / width;
//		}
		
		options.inJustDecodeBounds = false;
		
		return BitmapFactory.decodeFile(uri, options);
	}
	
	public static ArrayList<String> getTypeArrayList(ArrayList<MyFile> fileList, int type) {
		ArrayList<String> toRet = new ArrayList<String>();
		
		for (MyFile file: fileList) {
			if (type == 0) { // all file types
				toRet.add(file.getFileObject().getAbsolutePath());
			} else if (file.getType() == type) {
				toRet.add(file.getFileObject().getAbsolutePath());
			}
		}
		return toRet;
	}
	
	private static void keepScreenOn() {
		if (mWakeLock == null) {
			final PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
		    mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "wack_lock");
		}
		 mWakeLock.acquire();	
	}
	
	private static void keepScreenOff() {
		if (mWakeLock != null) { 
			mWakeLock.release();
		} 
	}
	
	public static long getAvailableRAM() {
		if (activityManager == null) {
			activityManager = (ActivityManager) activity.getSystemService(activity.ACTIVITY_SERVICE);
		}
		activityManager.getMemoryInfo(mi);
//		long availableMegs = mi.availMem / 1048576L;
		return mi.availMem/1024L;

	}
	
	public static boolean hasEnoughMemory() {
		return getAvailableRAM() > 2000;  // if more than 2 MB
	}
	
	public static boolean isSDCardAvailable() {
		
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    // We can read and write the media
		    mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		    // We can only read the media
		    mExternalStorageAvailable = true;
		    mExternalStorageWriteable = false;
		} else {
		    // Something else is wrong. It may be one of many other states, but all we need
		    //  to know is we can neither read nor write
		    mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		
		return mExternalStorageAvailable;
	}
	
	public static String getSDCardDir() {
//		return activity.getExternalFilesDir(null).getAbsolutePath();
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}
	public static String getCameraFolder() {
		return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
	}
	public static String getMusicFolder() {
		return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath();
	}
	public static String getVideoFolder() {
		return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath();
	}
	public static String getDownloadFolder() {
		return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
	}
	
}
