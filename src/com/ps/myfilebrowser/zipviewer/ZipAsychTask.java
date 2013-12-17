package com.ps.myfilebrowser.zipviewer;


import java.util.ArrayList;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.WindowManager;

import com.ps.myfilebrowser.BrowserActivity;
import com.ps.myfilebrowser.data.MarkedFileList;

public class ZipAsychTask extends AsyncTask<ArrayList<String>, String, Void> implements ExtractUpdateListener{

	ZipViewer parent;
	ZIPParser parser;
	
	ProgressDialog progress;

	
	public ZipAsychTask(ZipViewer parent, ZIPParser parser) {
		this.parent = parent;
		this.parser = parser;
	}
	
	protected void onPreExecute() {
		
		if (parser == null) {
			
			progress = ProgressDialog.show(parent, "Reading",
				    "Reading zip file", true); 
			progress.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		} else {
			parser.setExtractUpdateListener(this);
			progress = ProgressDialog.show(parent, "Extract",
				    "Extracting files", true); 
			progress.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}

    }
	
	@Override
	protected Void doInBackground(ArrayList<String>... arg0) {
		// TODO Auto-generated method stub
		if (parser != null) {
			parser.extractFiles(arg0[0]);
		} else {
			parent.initScreen();
		}
		return null;
		
	}
	
	@Override
	protected void onProgressUpdate(String... fileNames) {
		if (parser != null) {
			progress.setMessage("Extracting... " + fileNames[0]);
		}
    }

	@Override
	protected void onPostExecute(Void result) {
		if (parser != null) {
			progress.dismiss();
			parent.extractionCompleted();
		} else {
			progress.dismiss();
		}
    }
	
	public void updateProgress(String fileName) {
		publishProgress(fileName);
	}
	
}