package com.ps.myfilebrowser.zipviewer;

import java.util.ArrayList;
import java.util.TreeSet;

import com.ps.myfilebrowser.R;
import com.ps.myfilebrowser.data.MyFile;
import com.ps.myfilebrowser.util.BrowserUtil;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ZipFileListAdapter extends BaseAdapter {
	
	ZipViewer parent;
	ArrayList<String> data;
	LayoutInflater inflater;
	
	private static final int MARK_COLOR = Color.rgb(92,192,214);
	
	
//	ArrayList<Bitmap> icons;
	
	
	public ZipFileListAdapter(ZipViewer parent, ArrayList<String> data) {
		this.parent = parent;
		this.data = data;
		
		inflater = parent.getLayoutInflater();
//		icons = new ArrayList<Bitmap>(data.size());
		
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
				toRet = inflater.inflate(R.layout.zip_file_list_view, null);
			}
			
			TextView tv = (TextView)toRet.findViewById(R.id.zip_file_entry_name);
			tv.setText(data.get(arg0));
			
			if (parent.markedFiles.contains(data.get(arg0))) {
				tv.setBackgroundColor(MARK_COLOR);
			} else {
				tv.setBackgroundColor(Color.TRANSPARENT);
			}
			
		return toRet;
	}
}
