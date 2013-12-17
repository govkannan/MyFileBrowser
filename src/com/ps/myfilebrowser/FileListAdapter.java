package com.ps.myfilebrowser;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import com.ps.myfilebrowser.data.MyFile;
import com.ps.myfilebrowser.util.BrowserUtil;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FileListAdapter extends BaseAdapter {
	
	BrowserActivity parent;
	ArrayList<MyFile> data;
	LayoutInflater inflater;
	
	IconLoaderAsyncTask iconLoader;
	
	private static final int COLOR_READ_ONLY = Color.YELLOW; 
	private static final int COLOR_NO_READ = Color.RED; 
	private static final int COLOR_READ_WRITE = Color.WHITE;
	
	private static final int MARK_COLOR = Color.rgb(92,192,214);
	
	
//	ArrayList<Bitmap> icons;
	
	
	public FileListAdapter(BrowserActivity parent, ArrayList<MyFile> data) {
		this.parent = parent;
		this.data = data;
		
		inflater = parent.getLayoutInflater();
//		icons = new ArrayList<Bitmap>(data.size());
		
		iconLoader = new IconLoaderAsyncTask(this, data);
		iconLoader.execute();
	}
	
	public void destroy() {
		if (iconLoader != null) {
			iconLoader.cancel(true);
		}
		iconLoader = null;
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		
		if (data == null || data.size() < arg0 ) {
			return null;
		}
			View toRet = arg1;
			
			if (toRet == null) {
				if (parent.isGridView) {
					toRet = inflater.inflate(R.layout.file_grid_view, null);
				}else {
					toRet = inflater.inflate(R.layout.file_list_view, null);
				}
			}
			
			ImageView iView = (ImageView) toRet.findViewById(R.id.file_icon);
			
			
			
			Bitmap im = data.get(arg0).getIcon(); 
			if (im != null) {
//				Log.i("inside adapter : image not null",data.get(arg0).getName());
				iView.setImageBitmap(im);
			} else {
				iView.setImageBitmap(BrowserUtil.getIcon(MyFile.MYFILE_TYPE_IMAGE));
//				Log.i("inside adapter : image null",data.get(arg0).getName());
//				loadBitmap(data.get(arg0), iView);
			}
			
			TextView tv = (TextView)toRet.findViewById(R.id.file_name);
			tv.setText(data.get(arg0).getName());
			
			String rwText = "";
			
			if (data.get(arg0).getFileObject().canWrite() && data.get(arg0).getFileObject().canRead() ) {
				tv.setTextColor(COLOR_READ_WRITE);
			} else if (data.get(arg0).getFileObject().canRead() && !(data.get(arg0).getFileObject().canWrite()) ) {
				tv.setTextColor(COLOR_READ_ONLY);
				rwText = parent.getResources().getString(R.string.read_only_txt);
			} else if (!data.get(arg0).getFileObject().canRead()) {
				tv.setTextColor(COLOR_NO_READ);
				rwText = parent.getResources().getString(R.string.no_read_txt);
			}
			
//			TextView fileSize = (TextView) toRet.findViewById(R.id.file_size);
//			if (fileSize != null) {
//				fileSize.setText(data.get(arg0).getFormatedSize());
//			}
			
//			TextView fileDate = (TextView) toRet.findViewById(R.id.file_date);
//			if (fileDate != null) {
//				fileDate.setText(data.get(arg0).getCreationDate());
//			}
//			
			if (parent.isGridView)  {
				
			} else {
				TextView subFiles = (TextView) toRet.findViewById(R.id.file_size_txt);
			
				if (subFiles != null) {
					if (data.get(arg0).getFileObject().isDirectory()) {
						subFiles.setText(data.get(arg0).getNoOfFolders() 
								+ " " + parent.getResources().getString(R.string.items_txt)
								+ "   " + data.get(arg0).getCreationDate() + "   " + rwText);
					} else {
						subFiles.setText(data.get(arg0).getFormatedSize() 
								+ "   " + data.get(arg0).getCreationDate() +  "   " + rwText);
					}
				}
			}
			
		if (parent.editModeManager.isMarkSeveral) {
			if (parent.markedList.isCopy || parent.markedList.isCut) {
				toRet.setBackgroundColor(Color.TRANSPARENT);
				
				if (parent.markedList.isMarked(data.get(arg0))) {
					toRet.setBackgroundColor(Color.DKGRAY);
				}
			} else {
				if (parent.markedList.isMarked(arg0)) {
					toRet.setBackgroundColor(MARK_COLOR);
//					toRet.setBackgroundResource(R.drawable.mirror_bg);
				} else {
				toRet.setBackgroundColor(Color.TRANSPARENT);
				}
			}
		} else {
			toRet.setBackgroundColor(Color.TRANSPARENT);
		}
			
		return toRet;
	}
	
	
//	public void loadBitmap(MyFile resId, ImageView imageView) {
//		Log.i("inside loadBitmap : ",resId.getName());
//		
//	    if (cancelPotentialWork(resId, imageView)) {
//	    	Log.i("inside loadBitmap cancel is true: ",resId.getName());
//	        final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
//	        final AsyncDrawable asyncDrawable =
//	                new AsyncDrawable(parent.getResources(), 
//	                		BrowserUtil.getIcon(MyFile.MYFILE_TYPE_IMAGE), task);
//	        imageView.setImageDrawable(asyncDrawable);
//	        task.execute(resId);
//	    }
//	}
//	
//	public static boolean cancelPotentialWork(MyFile data, ImageView imageView) {
//	    final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
//
//	    if (bitmapWorkerTask != null) {
//	        final MyFile bitmapData = bitmapWorkerTask.data;
//	        
//	        if (bitmapData != data) {
//	            // Cancel previous task
//	            bitmapWorkerTask.cancel(true);
//	        } else {
//	            // The same work is already in progress
//	            return false;
//	        }
//	    }
//	    // No task associated with the ImageView, or an existing task was cancelled
//	    return true;
//	}
//	
//	
//	private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
//		   if (imageView != null) {
//		       final Drawable drawable = imageView.getDrawable();
//		       if (drawable instanceof AsyncDrawable) {
//		           final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
//		           return asyncDrawable.getBitmapWorkerTask();
//		       }
//		    }
//		    return null;
//	}
//
//	
//	static class AsyncDrawable extends BitmapDrawable {
//	    private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;
//
//	    public AsyncDrawable(Resources res, Bitmap bitmap,
//	            BitmapWorkerTask bitmapWorkerTask) {
//	        super(res, bitmap);
//	        bitmapWorkerTaskReference =
//	            new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
//	    }
//
//	    public BitmapWorkerTask getBitmapWorkerTask() {
//	        return bitmapWorkerTaskReference.get();
//	    }
//	} // AsyncDrawable
//	
}


//class BitmapWorkerTask extends AsyncTask<MyFile, Void, Bitmap> {
//    private final WeakReference<ImageView> imageViewReference;
//    MyFile data = null;
//
//    public BitmapWorkerTask(ImageView imageView) {
//        // Use a WeakReference to ensure the ImageView can be garbage collected
//        imageViewReference = new WeakReference<ImageView>(imageView);
//    }
//
//    // Decode image in background.
//    @Override
//    protected Bitmap doInBackground(MyFile... params) {
//    	data  = params[0];
//    	Log.i("inside doInBackground: ",data.getName());
//		switch (data.getType()) {
//		case MyFile.MYFILE_TYPE_IMAGE:
//			return (BrowserUtil.createIcon(data.getFileObject().getAbsolutePath()));
//		case MyFile.MYFILE_TYPE_VIDEO:
//			return (BrowserUtil.createIconForVideo(data.getFileObject().getAbsolutePath()));
//			
//		}
//		return null;
//    }
//
//    // Once complete, see if ImageView is still around and set bitmap.
//    @Override
//    protected void onPostExecute(Bitmap bitmap) {
//        if (imageViewReference != null && bitmap != null) {
//            final ImageView imageView = imageViewReference.get();
//            if (imageView != null) {
//                imageView.setImageBitmap(bitmap);
//                Log.i("inside Task : set",data.getName());
//            }
//        }
//    }
//    
//}


class IconLoaderAsyncTask extends AsyncTask<Void, Void, Void> {

	FileListAdapter parent ;
	ArrayList<MyFile> fileData;
	ArrayList<Bitmap> fileIcons;
	public IconLoaderAsyncTask(FileListAdapter parent, 
			ArrayList<MyFile> fileData) {
		this.parent = parent;
		this.fileData = fileData;
	}
	
	@Override
	protected Void doInBackground(Void... arg0) {
		// TODO Auto-generated method stub
		
		for (MyFile file : fileData) {
			if (file.getIcon() == null) {
				// do not load icon if not enough memory
				if (!BrowserUtil.hasEnoughMemory()) {
					break;
				}
				switch (file.getType()) {
					case MyFile.MYFILE_TYPE_IMAGE:
						file.setIcon(BrowserUtil.createIcon(file.getFileObject().getAbsolutePath()));
						break;
					case MyFile.MYFILE_TYPE_VIDEO:
						file.setIcon(BrowserUtil.createIconForVideo(file.getFileObject().getAbsolutePath()));
						break;
				}
			}
			publishProgress();
			if (isCancelled()) {
				break;
			}
		}
		
		return null;
		
	}
	
	@Override
	protected void onProgressUpdate(Void... progress) {
        parent.notifyDataSetChanged();
    }

	@Override
	protected void onPostExecute(Void result) {
    	parent.notifyDataSetChanged();
    }
	
}
