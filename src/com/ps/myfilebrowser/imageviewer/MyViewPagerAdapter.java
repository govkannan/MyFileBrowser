package com.ps.myfilebrowser.imageviewer;

import java.util.ArrayList;

import com.ps.myfilebrowser.util.BrowserUtil;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MyViewPagerAdapter extends PagerAdapter {

		ArrayList<String> imageNameList;
		Activity parent;
		private boolean isPortrait;
		
		
		MyViewPagerAdapter(Activity parent, ArrayList<String> imageNameList, boolean isPortrait) {
			this.parent = parent;
			this.imageNameList = imageNameList;
			this.isPortrait= isPortrait;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return imageNameList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == (View)arg1;
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			
			LayoutInflater inflater = (LayoutInflater)parent.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			View toRet = inflater.inflate(com.ps.myfilebrowser.R.layout.activity_my_photo_view, container, false);
			ImageView iv = (ImageView)toRet.findViewById(com.ps.myfilebrowser.R.id.myphoto);
			
			iv.setImageBitmap(BrowserUtil.loadImage(imageNameList.get(position), parent, isPortrait));
			
			((ViewPager)container).addView(toRet);
			
			return toRet;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View)object);
		}
		
	}
