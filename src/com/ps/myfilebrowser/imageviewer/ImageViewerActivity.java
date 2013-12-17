package com.ps.myfilebrowser.imageviewer;

import java.io.File;
import java.util.ArrayList;

import com.ps.myfilebrowser.R;
import com.ps.myfilebrowser.R.layout;
import com.ps.myfilebrowser.R.menu;
import com.ps.myfilebrowser.data.MyFile;
import com.ps.myfilebrowser.data.MyFileOptions;
import com.ps.myfilebrowser.util.BrowserUtil;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

public class ImageViewerActivity extends Activity {
	
	private int currentIndex;
	public static String IMG_INDEX="IMG_INDEX";
	public static String IMG_NAME_LIST = "IMG_NAME_LIST";
	
	private ArrayList<String> imageNameList;
	
	private boolean isPortrait;
	
	private ViewPager pager;
	private MyViewPagerAdapter pagerAdapter;
	
	private MyFileOptions options = new MyFileOptions();
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
	                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
	
		isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
		
		if (savedInstanceState != null) {
			currentIndex = savedInstanceState.getInt(IMG_INDEX);
			imageNameList = savedInstanceState.getStringArrayList(IMG_NAME_LIST);
		} else {
				parseIntent();
		}
		

		setContentView(R.layout.activity_image_viewer);
		
		pager = (ViewPager)findViewById(R.id.image_view);
		
		pagerAdapter = new MyViewPagerAdapter(this, imageNameList, isPortrait);
		pager.setAdapter(pagerAdapter);
		pager.setCurrentItem(currentIndex);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image_viewer, menu);
		return true;
	}

	@Override
	public void onSaveInstanceState(Bundle args) {
		args.putInt(IMG_INDEX, currentIndex);
		args.putStringArrayList(IMG_NAME_LIST, imageNameList);
	}
	
	private void parseIntent() {
		Intent myIntent = getIntent();
		currentIndex = myIntent.getIntExtra(IMG_INDEX, 0);
		imageNameList = myIntent.getStringArrayListExtra(IMG_NAME_LIST);
		
		if (imageNameList == null) {
			Uri imageURI = myIntent.getData();
			String currentPath = imageURI.getPath();
			File file = new File(currentPath);
			options.type = MyFile.MYFILE_TYPE_IMAGE;
			imageNameList = BrowserUtil.getTypeArrayList(BrowserUtil.getFiles(file.getParent(), options), 0);
			if (imageNameList.contains(currentPath))
				currentIndex = imageNameList.indexOf(currentPath);
			else {
				imageNameList.add(currentPath);
				currentIndex = imageNameList.indexOf(currentPath);
			}
		}
		
	}
}
