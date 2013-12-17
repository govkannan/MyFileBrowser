package com.ps.myfilebrowser.apkviewer;


import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.ActionBar;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;


public class APKViewTabListener <T extends Fragment> implements ActionBar.TabListener {

	private Class<T> fragmentClass;
	private APKViewer parent;
	private String tag ;
	private Fragment fragment;
	private String apkPath;
	
	public APKViewTabListener(APKViewer parent, Class<T> fragment, String tag) {
		this.fragmentClass = fragment;
		this.parent = parent;
		this.tag = tag;
	}
	
	
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
		if (fragment == null) {
			fragment = Fragment.instantiate(parent, fragmentClass.getName());
			ft.add(android.R.id.content, fragment, tag);
		} else {
			ft.attach(fragment);
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		if (fragment != null) {
			ft.detach(fragment);
		}
	}
}
