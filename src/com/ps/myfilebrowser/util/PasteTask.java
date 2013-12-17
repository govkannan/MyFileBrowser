package com.ps.myfilebrowser.util;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.WindowManager;

import com.ps.myfilebrowser.BrowserActivity;
import com.ps.myfilebrowser.data.MarkedFileList;

public class PasteTask extends AsyncTask<Void, String, Void> {

	BrowserActivity parent;
	MarkedFileList markedList;
	String currentDir;
	boolean isDelete;
	
	ProgressDialog progress;

	
	public PasteTask(BrowserActivity parent, 
			MarkedFileList markedList, String currentDir, boolean isDelete) {
		this.parent = parent;
		this.markedList = markedList;
		this.currentDir = currentDir;
		this.isDelete = isDelete;
	}
	
	protected void onPreExecute() {
		
		if (isDelete) {
			
			progress = ProgressDialog.show(parent, "Delete",
				    "Deleting files", true); 
				
		} else {
//			progress = ProgressDialog.show(parent, "Copy",
//					"Copying files", true);
			progress = new ProgressDialog(parent);
			progress.setTitle("Copy");
			progress.setMessage("Copying files");
			progress.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			    	PSFileReader.cancelCopyOperation = true;
			    }
			});
			progress.show();
			
		}
		
		progress.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }
	@Override
	protected Void doInBackground(Void... arg0) {
		// TODO Auto-generated method stub
		if (isDelete) {
			PSFileReader.deleteFiles(markedList, parent);
		}else {
			PSFileReader.copyFiles(markedList, currentDir, parent);
		}
		
//		publishProgress();
			
		return null;
		
	}
	
	@Override
	protected void onProgressUpdate(String... fileNames) {
//        parent.notifyDataSetChanged();
		if (isDelete) {
			progress.setMessage("Deleting... " + fileNames[0]);
		} else {
			progress.setMessage("Copying... " + fileNames[0]);
		}
		
    }

	@Override
	protected void onPostExecute(Void result) {
//    	parent.notifyDataSetChanged();
		progress.dismiss();
		parent.completed();
//		BrowserUtil.keepScreenOn();
    }
	
	public void updateProgress(String fileName) {
		publishProgress(fileName);
	}
}