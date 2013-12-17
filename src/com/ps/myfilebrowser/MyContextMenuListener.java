package com.ps.myfilebrowser;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class MyContextMenuListener implements OnItemClickListener {
	
	private BrowserActivity parent;
	
	public MyContextMenuListener(BrowserActivity parent) {
		this.parent = parent;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		parent.menuSelected(arg2);
	}
	
	

}
