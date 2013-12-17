package com.ps.myfilebrowser.zipviewer;

import java.util.ArrayList;
import java.util.TreeSet;

import com.ps.myfilebrowser.R;
import com.ps.myfilebrowser.R.layout;
import com.ps.myfilebrowser.R.menu;
import com.ps.myfilebrowser.data.MyFile;
import com.ps.myfilebrowser.util.BrowserUtil;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class ZipViewer extends Activity implements OnItemClickListener {

	ZIPParser parser;
	ArrayList<String> zipFileEntries;
	
	ArrayList<String> markedFiles;
	
	ZipFileListAdapter zipFileListAdapter;
	
	ProgressDialog progress;
	ListView lv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zip_viewer);
//		parseIntent();
		
		markedFiles = new ArrayList<String>();
		
		lv = (ListView)findViewById(R.id.list_view);
		lv.setOnItemClickListener(this);
		
//		View footer = getLayoutInflater().inflate(R.layout.zip_view_list_footer, null);
////		lv.addFooterView(footer);
//		lv.addHeaderView(footer);
		
		ZipAsychTask task = new ZipAsychTask(this, null);
		task.execute(markedFiles);
		
	}
	
	public void initScreen() {
		
		parseIntent();
		zipFileListAdapter = new ZipFileListAdapter(this, zipFileEntries);
		
		runOnUiThread(new Runnable() {
			public void run() {
				lv.setAdapter(zipFileListAdapter);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.zip_viewer, menu);
		return true;
	}
	
	private void parseIntent() {
		
//		ZipAsychTask task = new ZipAsychTask(this, null);
//		task.execute(markedFiles);
		
		Intent myIntent = getIntent();
		Uri path = myIntent.getData();
		parser = new ZIPParser(path.getPath());
		zipFileEntries = parser.parseEntries();
		
//		task.readComplete();
	}
	
	public void onExtractAll(View view) {
		
		ZipAsychTask task = new ZipAsychTask(this, parser);
		task.execute(markedFiles);
		
//		parser.extractAll();
//		sendResult();
	}
	public void onExtractSelected(View view) {
		
		if (markedFiles.size()==0) {
			Toast.makeText(this, "Please select one or more files to extrtact", Toast.LENGTH_SHORT).show();
			return;
		}
		
		ZipAsychTask task = new ZipAsychTask(this, parser);
		task.execute(markedFiles);

//		parser.extractFiles(markedFiles);
//		sendResult();
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
		
		if (markedFiles.contains(zipFileEntries.get(pos))) {
			markedFiles.remove(zipFileEntries.get(pos));
		} else {
			markedFiles.add(zipFileEntries.get(pos));
		}
		zipFileListAdapter.notifyDataSetChanged();
	}

	private void sendResult() {
		Intent toRet = new Intent();
		toRet.putExtra("CURR_DIR", parser.getLastExtractedFolder());
		
		setResult(RESULT_OK, toRet);
		finish();
	}
	
	public void extractionCompleted() {
		markedFiles.clear();
		runOnUiThread(new Runnable() {
			public void run() {
				sendResult();
			}
		});


	}
	
}
