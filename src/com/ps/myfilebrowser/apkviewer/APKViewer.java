package com.ps.myfilebrowser.apkviewer;

import java.io.File;

import com.ps.myfilebrowser.R;
import com.ps.myfilebrowser.R.layout;
import com.ps.myfilebrowser.R.menu;
import android.net.Uri;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class APKViewer extends Activity implements OnFragmentInteractionListener {

	public static  String apkPath;
	public static boolean install_status = false;
	public static String packageName= null;
	
	private static final String APK_PATH = "APK_PATH";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		setContentView(R.layout.activity_apkviewer);
		
		if (savedInstanceState != null) {
			
		} else { 
			parseIntent();
		}
		
		ActionBar actionBar = this.getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);
	
		Tab viewTab = actionBar.newTab();
		viewTab.setText(R.string.title_apkviewer_tab_view);
		viewTab.setTabListener(
			new APKViewTabListener<APKViewFragment>(this, APKViewFragment.class, getResources().getString(R.string.title_apkviewer_tab_view)));
	
	
		Tab manifestTab = actionBar.newTab();
		manifestTab.setText(R.string.title_apkviewer_tab_menifest);
		manifestTab.setTabListener(
			new APKViewTabListener<ManifestFragment>(this, ManifestFragment.class, getResources().getString(R.string.title_apkviewer_tab_menifest)));
	
		actionBar.addTab(viewTab);
		actionBar.addTab(manifestTab);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.apkviewer, menu);
		return true;
	}
	
	private void parseIntent() {
		Intent intent = getIntent();
		Uri apkUri = intent.getData();
		apkPath = apkUri.getPath();
	}
	
	public void onSaveInstanceState(Bundle args) {
		args.putString(APK_PATH, apkPath);
	}

	@Override
	public void onFragmentInteraction(Uri uri) {
		// TODO Auto-generated method stub
		
	}
	
	public void onInstall(View v) {
		
		if (install_status) {
			Intent intent = new Intent(Intent.ACTION_DELETE, 
					Uri.fromParts("package",packageName, null));
			startActivity(intent);
		} else {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(new File(apkPath)), "application/vnd.android.package-archive");
			startActivity(intent);
		}
		finish();
		
	}
	

	public void onRun(View v) {
		try {
			Intent targetIntent = getPackageManager().getLaunchIntentForPackage(packageName);
			
			if (targetIntent != null)
				targetIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
//				targetIntent.addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
				startActivity(targetIntent);
		} catch (Exception ex) {
			
		}
	} 

}
