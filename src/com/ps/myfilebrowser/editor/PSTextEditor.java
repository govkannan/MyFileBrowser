package com.ps.myfilebrowser.editor;

import java.io.File;

import com.ps.myfilebrowser.R;
import com.ps.myfilebrowser.R.layout;
import com.ps.myfilebrowser.R.menu;
import com.ps.myfilebrowser.data.MyFile;
import com.ps.myfilebrowser.util.PSFileReader;

import android.R.color;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PSTextEditor extends Activity {

	EditText editor;
	TextView pathText;
	public static final String FILE_PATH = "com.ps.FILE_PATH" ;
	public static final double MAX_FILE_SIZE = 15240.0;
	String path;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_pstext_editor);
		
		editor = (EditText)findViewById(R.id.editor_text);
		
		Intent intent = getIntent();
		
		if (intent != null) {
			path = intent.getStringExtra(FILE_PATH);
			if (path== null) {
				path = intent.getData().getEncodedPath().toString();
			}
		}
		
//		Log.i("PSTextEditor", "path :" + path);
		
		refreshText();
	}
	
	private void refreshText() {
		
		
		File myFile = new File(path);
		if (myFile.length() > MAX_FILE_SIZE) {
			editor.setText("could not open this file due to large file size... will be implemented in later version");
			editor.setEnabled(false);
			editor.setTextColor(Color.WHITE);
			this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			return;
		}

		StringBuffer text = null;
		try {
			text = PSFileReader.readFile(path);
		} catch (Exception ex) {}

		if (!PSFileReader.canWrite(path)) {
			editor.setEnabled(false);
			editor.setTextColor(Color.WHITE);
			
			if (text == null) {
				editor.setText("Empty File");
			} else {
				editor.setText(text);
			}

			pathText = (TextView)findViewById(R.id.editor_file_path);
			pathText.setText(path);
			this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		} else {
//			editor.setText(PSFileReader.readFile(path));
			
			if (text == null ) {
				editor.setText("");
//				editor.setEnabled(false);
//				editor.setTextColor(Color.WHITE);
			} else {
				editor.setText(text);
			}
			
			pathText = (TextView)findViewById(R.id.editor_file_path);
			pathText.setText(path);
			
			if (myFile.length() != 0) {
				this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pstext_editor, menu);
		
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.editor_save:
	    	
	    	if (PSFileReader.writeFile(path, editor.getText().toString())) {
	    		Toast.makeText(this, "Saved ", Toast.LENGTH_SHORT).show();	
	    	} else {
	    		Toast.makeText(this, "Not Saved ", Toast.LENGTH_SHORT).show();
	    	}
	    	
	      break;
	    }
	    return super.onOptionsItemSelected(item);
	  }

}
