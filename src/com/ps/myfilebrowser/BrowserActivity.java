package com.ps.myfilebrowser;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.ps.myfilebrowser.data.MarkedFileList;
import com.ps.myfilebrowser.data.MyFile;
import com.ps.myfilebrowser.data.MyFileOperationListener;
import com.ps.myfilebrowser.data.MyFileOptions;
import com.ps.myfilebrowser.editor.PSTextEditor;
import com.ps.myfilebrowser.util.BrowserUtil;
import com.ps.myfilebrowser.util.MemoryStatusCheck;
import com.ps.myfilebrowser.util.PSFileReader;
import com.ps.myfilebrowser.util.PasteTask;

import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class BrowserActivity extends Activity implements OnItemClickListener, 
MyFileOperationListener, OnItemSelectedListener , OnItemLongClickListener {

	
	private static final int ZIP_ACTIVITY_REQUEST = 1;
	ArrayList<MyFile> fileLists;
	private FileListAdapter myAdapter;
	ListView lv;
	GridView gv;
	
	LinearLayout headerLayout;
	
	LinearLayout editModeLayout, viewModeLayout, genericLayout;
	
	public static MarkedFileList markedList; 
	
	
	public String currentDir;
	private ImageView backImage;
	private TextView totalItems, folderSize;
	private TextView iMemory, eMemory;
	
//	public boolean editMode = false;
//	public boolean isCopy = false;
//	public boolean isCut = false;
	public boolean isGridView = false;
	private int currentChoosenFileIndex;
	private static final String CURR_DIR = "CURR_DIR";
	
	private TextView folderPath;
	PasteTask pasteTask;
	
	public Spinner spinner;
	private  ArrayAdapter<CharSequence> spinnerAdapter;
	
	private ArrayList<String> spinnerPaths; 
	
	private MyFileOptions options = new MyFileOptions();
	
	
	public  EditModeIconManager editModeManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		 
		
		BrowserUtil.activity = this;
		setContentView(R.layout.activity_browser);
		
		
		
		folderPath= (TextView)findViewById(R.id.header_folder_path);
		spinner = (Spinner)findViewById(R.id.goto_folder);
		
		intiSpinner();
		
		// load layout
		genericLayout = (LinearLayout)findViewById(R.id.generic_view_layout);
		
		lv = (ListView)getLayoutInflater().inflate(R.layout.file_list_layout, null);
		lv.setOnItemLongClickListener(this);
		gv = (GridView)getLayoutInflater().inflate(R.layout.file_grid_layout, null);
		gv.setOnItemLongClickListener(this);
		
		isGridView = false;
		genericLayout.addView(lv);
		
		
		markedList = new MarkedFileList();
		
		headerLayout = (LinearLayout) findViewById(R.id.browser_mode_header);
		viewModeLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.browser_view_mode_header, null);
		editModeLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.browser_edit_mode_header, null);
		editModeManager = new EditModeIconManager(this, editModeLayout);
		
		
		modeChanged();
	
		
		lv.setOnItemClickListener(this);
		gv.setOnItemClickListener(this);
		
		
		if (savedInstanceState != null && savedInstanceState.getString(CURR_DIR) != null) {
			onDirectorySelected(savedInstanceState.getString(CURR_DIR));
		} else {
			onDirectorySelected(MyFile.ROOT);
		}
		
		
	}
	
	private void intiSpinner() {
		
		ArrayList<String> spinnerList = new ArrayList<String>();
		spinnerPaths = new ArrayList<String>();
		
		spinnerList.add("ROOT");
		spinnerPaths.add(MyFile.ROOT);
		
		if (BrowserUtil.isSDCardAvailable()) {
			spinnerList.add("SD Card");
			spinnerPaths.add(BrowserUtil.getSDCardDir());
		}
		
		String path = BrowserUtil.getCameraFolder();
		File tmpFile = new File(path);
		if (tmpFile.exists()) {
			spinnerList.add("Camera");
			spinnerPaths.add(path);
		}

		path = BrowserUtil.getMusicFolder();
		tmpFile = new File(path);
		
		if (tmpFile.exists()) {
			spinnerList.add("Music");
			spinnerPaths.add(path);
		}
		
		path = BrowserUtil.getVideoFolder();
		tmpFile = new File(path);
		
		if (tmpFile.exists()) {
			spinnerList.add("Video");
			spinnerPaths.add(path);
		}

		path = BrowserUtil.getDownloadFolder();
		tmpFile = new File(path);
		
		if (tmpFile.exists()) {
			spinnerList.add("Downloads");
			spinnerPaths.add(path);
		}
		
		spinnerAdapter = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item,
				spinnerList);
		// Specify the layout to use when the list of choices appears
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(spinnerAdapter);
		
		spinner.setOnItemSelectedListener(this);
	}
	
	private boolean isSpinnerChangeByProgramm = false;
	
	public void setSpinnerItemForDirectoyChange() {
		
		int currentSpinnerId = spinner.getSelectedItemPosition();
		int selectedPath = 0;
		for (int i=0; i < spinnerPaths.size(); i++) {
			
			if (currentDir.startsWith(spinnerPaths.get(i))) {
				selectedPath = i;
			}
		}
		
//		isSpinnerChangeByProgramm = true;
		if (currentSpinnerId != selectedPath) {
			spinner.setSelection(selectedPath);
			isSpinnerChangeByProgramm = true;
		}
	}
	
	
	private void modeChanged() {
		
		headerLayout.addView(editModeLayout);
		markedList.unMarkAll();

//		if (editMode) {
//			headerLayout.removeView(viewModeLayout);
//			headerLayout.addView(editModeLayout);
//			markedList.unMarkAll();
//		} else {
//			headerLayout.removeView(editModeLayout);
//			headerLayout.addView(viewModeLayout);
//			markedList.unMarkAll();
//			
//			backImage = (ImageView)viewModeLayout.findViewById(R.id.back_button);
//			totalItems = (TextView)viewModeLayout.findViewById(R.id.header_total_files);
//			folderSize = (TextView)viewModeLayout.findViewById(R.id.header_folder_size);
//			
//			iMemory = (TextView)viewModeLayout.findViewById(R.id.header_internal_txt);
//			eMemory = (TextView)viewModeLayout.findViewById(R.id.header_external_txt);
//			if (myAdapter != null)
//				myAdapter.notifyDataSetChanged();
//		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.browser, menu);
		return true;
	}
	
	private void onDirectorySelected(String file) {
		
		currentDir = file;
		fileLists = BrowserUtil.getFiles(file, options);
		
		setSpinnerItemForDirectoyChange();
		
		editModeManager.init(file);
		
		checkAndUpdateHeader();
		
		if (myAdapter != null) {
			myAdapter.destroy();
		}
		myAdapter = new FileListAdapter(this, fileLists);
		
		if (isGridView) {
			gv.setAdapter(myAdapter);
			
		} else {
//			myAdapter = new FileListAdapter(this, fileLists);
			lv.setAdapter(myAdapter);
		}
	}
	
	private void layoutChanged() {
		
		if (myAdapter != null) {
			myAdapter.destroy();
		}
		myAdapter = new FileListAdapter(this, fileLists);
		
		
		
		if (isGridView) {
			genericLayout.removeView(lv);
			gv.setAdapter(myAdapter);
			genericLayout.addView(gv);
			
		} else {
			genericLayout.removeView(gv);
			lv.setAdapter(myAdapter);
			genericLayout.addView(lv);
		}
	}
	


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {

		int fileType = fileLists.get(pos).getType();
		
		if (!fileLists.get(pos).canRead()) {
			Toast.makeText(this, "No permission to read :" + fileLists.get(pos).getName()  , Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (editModeManager.isMarkSeveral) {
			
			if (markedList.isCopy || markedList.isCut) {  // allow choosing different directory
				if (fileType == MyFile.MYFILE_TYPE_DIR) {
					if (!markedList.isMarked(fileLists.get(pos))) { // do not allow copying on any marked  folder
						onDirectorySelected(fileLists.get(pos).getFileObject().getAbsolutePath());
					}
				}
			} else {
				markedList.markItem(pos, fileLists.get(pos));
				editModeManager.markedListUpdated();
				myAdapter.notifyDataSetChanged();
			}
		} else {
			// TODO Auto-generated method stub
			Intent targetIntent;
		
			switch(fileType) {
				case MyFile.MYFILE_TYPE_DIR:
					onDirectorySelected(fileLists.get(pos).getFileObject().getAbsolutePath());
					break;
			case MyFile.MYFILE_TYPE_AUDIO:
				targetIntent = new Intent();
				targetIntent.setAction(android.content.Intent.ACTION_VIEW);
				targetIntent.setDataAndType(Uri.fromFile(fileLists.get(pos).getFileObject()),"audio/*");
				startActivity(targetIntent);
				break;
			case MyFile.MYFILE_TYPE_VIDEO:
				targetIntent = new Intent();
				targetIntent.setAction(android.content.Intent.ACTION_VIEW);
				targetIntent.setDataAndType(Uri.fromFile(fileLists.get(pos).getFileObject()),"video/*");
				startActivity(targetIntent);
				break;
			case MyFile.MYFILE_TYPE_HTML:
				targetIntent = new Intent();
				targetIntent.setAction(android.content.Intent.ACTION_VIEW);
				targetIntent.setDataAndType(Uri.fromFile(fileLists.get(pos).getFileObject()),"text/html");
				targetIntent.addCategory(Intent.CATEGORY_BROWSABLE);
				targetIntent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
				startActivity(targetIntent);
				break;
			case MyFile.MYFILE_TYPE_IMAGE:
				targetIntent = new Intent();
				targetIntent.setAction(android.content.Intent.ACTION_VIEW);
//				targetIntent.setDataAndType(Uri.fromFile(fileLists.get(pos).getFileObject()),"image/*");
				targetIntent.setDataAndType(Uri.fromFile(fileLists.get(pos).getFileObject()),"image/*");
//				ArrayList<String> filePaths = BrowserUtil.getTypeArrayList(fileLists, MyFile.MYFILE_TYPE_IMAGE);
//				targetIntent.putStringArrayListExtra("IMG_NAME_LIST", filePaths);
//				
//				targetIntent.putExtra("IMG_INDEX", filePaths.indexOf(fileLists.get(pos).getFileObject().getAbsolutePath()));
//				targetIntent.setClassName("com.ps.myfilebrowser", "com.ps.myfilebrowser.imageviewer.ImageViewerActivity");
				startActivity(targetIntent);
				break;
			case MyFile.MYFILE_TYPE_PDF:
				targetIntent = new Intent();
				targetIntent.setAction(android.content.Intent.ACTION_VIEW);
				targetIntent.setDataAndType(Uri.fromFile(fileLists.get(pos).getFileObject()),"application/pdf");
				
				if(targetIntent.resolveActivity(getPackageManager()) != null) {
					startActivity(targetIntent);
				} else {
					Toast.makeText(this, "No activity found to open this file", Toast.LENGTH_LONG).show();
				}
				break;
			case MyFile.MYFILE_TYPE_XML:				
			case MyFile.MYFILE_TYPE_TXT:
			
				if (fileLists.get(pos).getFileObject().length() < PSTextEditor.MAX_FILE_SIZE) {
					targetIntent = new Intent();
					targetIntent.putExtra(PSTextEditor.FILE_PATH, fileLists.get(pos).getFileObject().getAbsolutePath());
					targetIntent.setClassName("com.ps.myfilebrowser", "com.ps.myfilebrowser.editor.PSTextEditor");
					startActivity(targetIntent);
				} else {
					targetIntent = new Intent();
					targetIntent.setAction(android.content.Intent.ACTION_VIEW);
					targetIntent.setDataAndType(Uri.fromFile(fileLists.get(currentChoosenFileIndex).getFileObject()),"text/*");
					startActivity(targetIntent);
				}
				break;
				
			case MyFile.MYFILE_TYPE_XLS:
			case MyFile.MYFILE_TYPE_DOC:
				targetIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.fromFile(fileLists.get(pos).getFileObject()));
				
				if(targetIntent.resolveActivity(getPackageManager()) != null) {
					startActivity(targetIntent);
				} else {
					Toast.makeText(this, "No activity found to open this file", Toast.LENGTH_LONG).show();
				}
				break;
			case MyFile.MYFILE_TYPE_APK:
				targetIntent = new Intent();
				targetIntent.setDataAndType(Uri.fromFile(fileLists.get(pos).getFileObject()),"application/apk");
				targetIntent.setClassName("com.ps.myfilebrowser", "com.ps.myfilebrowser.apkviewer.APKViewer");
				startActivity(targetIntent);
				break;
				
			case MyFile.MYFILE_TYPE_VCF:
				targetIntent = new Intent();
				targetIntent.setAction(android.content.Intent.ACTION_VIEW);
				targetIntent.setDataAndType(Uri.fromFile(fileLists.get(pos).getFileObject()),"text/x-vcard");
				startActivity(targetIntent);
				break;
				
			case MyFile.MYFILE_TYPE_ZIP:
				targetIntent = new Intent();
				targetIntent.setAction(android.content.Intent.ACTION_VIEW);
				targetIntent.setDataAndType(Uri.fromFile(fileLists.get(pos).getFileObject()),"archive/zip");
				startActivityForResult(targetIntent, ZIP_ACTIVITY_REQUEST);
				break;
			case MyFile.MYFILE_TYPE_UNKNOWN:   // open in Text file
				
				currentChoosenFileIndex = pos;
				showPopupDialog();
				
				
//				targetIntent = new Intent();
//				targetIntent.putExtra(PSTextEditor.FILE_PATH, fileLists.get(pos).getFileObject().getAbsolutePath());
//				targetIntent.setClassName("com.ps.myfilebrowser", "com.ps.myfilebrowser.editor.PSTextEditor");
//				startActivity(targetIntent);
				
				
//				targetIntent = new Intent();
//				targetIntent.setAction(android.content.Intent.ACTION_VIEW);
//				targetIntent.putExtra(PSTextEditor.FILE_PATH, fileLists.get(pos).getFileObject().getAbsolutePath());
//				
////				targetIntent.setDataAndType(Uri.fromFile(fileLists.get(pos).getFileObject()),"*/*");
//				targetIntent.setData(Uri.fromFile(fileLists.get(pos).getFileObject()));
//				targetIntent.setType("*/*");
//				startActivity(Intent.createChooser(targetIntent, "Open with.."));
				
				break;
			}
		}
	}
	
	public void openUknownFileAs(int id) {
		
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
		Intent targetIntent = new Intent();
		targetIntent.setAction(android.content.Intent.ACTION_VIEW);

		switch(id) {
		case 0: // TEXT
//			targetIntent.putExtra(PSTextEditor.FILE_PATH, fileLists.get(currentChoosenFileIndex).getFileObject().getAbsolutePath());
			targetIntent.setDataAndType(Uri.fromFile(fileLists.get(currentChoosenFileIndex).getFileObject()),"text/*");
			startActivity(targetIntent);
			break;
		case 1: // Image 
			targetIntent.setDataAndType(Uri.fromFile(fileLists.get(currentChoosenFileIndex).getFileObject()),"image/*");
			startActivity(targetIntent);
			break;
		case 2: // Music
			targetIntent.setDataAndType(Uri.fromFile(fileLists.get(currentChoosenFileIndex).getFileObject()),"audio/*");
			startActivity(targetIntent);
			break;
			
		case 3: // Video
			targetIntent.setDataAndType(Uri.fromFile(fileLists.get(currentChoosenFileIndex).getFileObject()),"video/*");
			startActivity(targetIntent);
			break;
		}
		
	}
	
	public void onBackSelect(View v) {
			onBack();
	}
	
	public void onBack() {
		if (editModeManager.isMarkSeveral) {
			if (markedList.isCopy || markedList.isCut) {
				
			} else { 
				return;
			}
		}
		if (currentDir.equals(MyFile.ROOT)) {
			// does nothing..
		} else {
			File file = new File(currentDir);
			onDirectorySelected(file.getParent());
		}
		
	}
	
	public  void clearMarkSeveral() {
		editModeManager.onMarkSeveral();
		editModeManager.init(currentDir);
	}
	
	private void checkAndUpdateHeader() {
		
		if (folderPath != null) {
			folderPath.setText(currentDir);
		}
//		if (editMode) {
//			
//		} else {
//			if (currentDir.equals(MyFile.ROOT)) {
//				backImage.setImageBitmap(null);
//			} else {
//				backImage.setImageBitmap(BrowserUtil.getBackFolderIcon());
//			}
//		
//			totalItems.setText("" + fileLists.size() + " " + getResources().getText(R.string.header_total_txt).toString());
//			folderSize.setText(getResources().getText(R.string.header_folder_size_txt).toString() + ": " + MemoryStatusCheck.getFormatedUsedSpace(currentDir));
//		
//			iMemory.setText("I: " + MemoryStatusCheck.getInternalMemoryStatus());
//			eMemory.setText("E: " + MemoryStatusCheck.getExternalMemoryStatus());
//		}
	}
	
	@Override
	public void onStop() {
		super.onStop();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (myAdapter != null) {
			myAdapter.destroy();
		}
		
	}
	
	public void onSaveInstanceState(Bundle args) {
		args.putString(CURR_DIR, currentDir);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
				
				if (editModeManager.isMarkSeveral) {
					if (markedList.isCopy || markedList.isCut) {
						
					} else {
						clearMarkSeveral();
						return true;
					}
					
				}
				if (currentDir.equals(MyFile.ROOT)) {
					return super.onKeyDown(keyCode, event);	
				} else {
					onBack();
					return true;
				}
		}
		return super.onKeyDown(keyCode, event);	
		
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.browser_layout:
	    	if (isGridView) {
	    		isGridView = false;
	    		item.setIcon(R.drawable.grid_blue);
	    		
	    	} else {
	    		isGridView = true;
	    		item.setIcon(R.drawable.list_blue);
	    	}
	    	layoutChanged();
	    	break;
	    case R.id.action_feedback:
	    	startMailIntent();
	    	break;
	    }
	    return super.onOptionsItemSelected(item);
	  }
	
	
	public void onPaste(View v) {
		
		pasteTask = new PasteTask(this, markedList, currentDir, false);
		pasteTask.execute();
		
	}
	
	public void onDelete(View v) {
		if (markedList.getSize() > 0) {
			
			askDeleteConfirmation();
//			pasteTask = new PasteTask(this, markedList, currentDir, true);
//			pasteTask.execute();
		} else {
			Toast.makeText(this, "Please select items to be deleted", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void onShare(View v) {
		
		if (markedList.getSize() > 0) {
			
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
			shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, markedList.toUriList());
			shareIntent.setType("*/*");
			startActivity(Intent.createChooser(shareIntent, "Share to.."));
			
			editModeManager.onMarkSeveral();
			
		} else {
			Toast.makeText(this, "Please select items to be shared", Toast.LENGTH_SHORT).show();
		}
		
	}

	@Override
	public void fileDeleted(MyFile file) {
		// TODO Auto-generated method stub
//		fileLists.remove(file);
		markedList.deletedFiles.add(file);
//		myAdapter.notifyDataSetChanged();
		updateCurrentPageForModify();
	}
	

	@Override
	public void fileCreated(String path) {
		Log.i("BrowserActivity", path);
		MyFile myFile = new MyFile(new File(path));
		if (!fileLists.contains(myFile)) {
			Log.i("BrowserActivity", "Does not contain " + path);
			markedList.addedFiles.add(myFile);
			updateCurrentPageForModify();
			
		}else {
			Log.i("BrowserActivity", "Contains already " + path);
		}
		
	}
	
	public void fileCopyFinished() {
//		if (progress != null) 
//			progress.dismiss();
	}

	
	public void completed() {
		editModeManager.onMarkSeveral(); // markSeveral completed
		updateCurrentPageForModify();
	}


	@Override
	public void updateProgress(String fileName) {
		pasteTask.updateProgress(fileName);
	}
	
	public void updateCurrentPage() {
		runOnUiThread(new Runnable() {
			public void run() {
				myAdapter.notifyDataSetChanged();
			}
		});
	}

	public void updateCurrentPageForModify() {
		runOnUiThread(new Runnable() {
			public void run() {
				checkForFileItemUpdates();
				myAdapter.notifyDataSetChanged();
			}
		});
	}

	private void checkForFileItemUpdates() {
		for (int i=0; i<markedList.addedFiles.size(); i++) {
			fileLists.add(markedList.addedFiles.elementAt(i));
		}
		for (int i=0; i<markedList.deletedFiles.size(); i++) {
			fileLists.remove(markedList.deletedFiles.elementAt(i));
		}
		
		
		markedList.clearAddDeletedFiles();
		
		
	}
	
	protected void onActivityResult(int reqCode, int resCode, Intent data) {
		if (reqCode == ZIP_ACTIVITY_REQUEST) {
			if (resCode == RESULT_OK) {
				String path = data.getStringExtra(CURR_DIR);
				onDirectorySelected(path);
			}
		}
	}
	
	private void askDeleteConfirmation() {
		
		AlertDialog.Builder deleteConfirm = new AlertDialog.Builder(this);

		deleteConfirm.setTitle(R.string.delete_confirm_title);
		deleteConfirm.setMessage(R.string.delete_confirm_txt);

		deleteConfirm.setPositiveButton(R.string.yes_txt, new DialogInterface.OnClickListener() {
			
		public void onClick(DialogInterface dialog, int whichButton) {
			
			pasteTask = new PasteTask(BrowserActivity.this, markedList, currentDir, true);
			pasteTask.execute();
			// Do something with value!
		 	}
			});

		deleteConfirm.setNegativeButton(R.string.no_txt, new DialogInterface.OnClickListener() {
		 public void onClick(DialogInterface dialog, int whichButton) {
		     // Canceled.
		}
		});

		deleteConfirm.show();
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		if (isSpinnerChangeByProgramm) {
			// ignore this request.. triggered while updating the spinner item by prgramatically
		} else {
			if (markedList.containsDir(spinnerPaths.get(arg2))) {
				Toast.makeText(this, "Not allowed to copy this folder", Toast.LENGTH_SHORT).show();
			} else {
				onDirectorySelected(spinnerPaths.get(arg2));
			}
		}
		isSpinnerChangeByProgramm = false;
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	Dialog dialog;
	public void showPopupDialog() {
		
		String[] options = {"Text", "Image", "Music", "Video"};
		dialog = new Dialog(this);
		dialog.setTitle(R.string.unknown_file_open_as);
		ListView optionList = new ListView(this);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		          android.R.layout.simple_list_item_1, android.R.id.text1, options);
		optionList.setAdapter(adapter);
		
		optionList.setOnItemClickListener(new UnknownFileOpenListener(this));
        dialog.setContentView(optionList);
        dialog.show();
         
	}
	

	public void showContextPopupDialog() {
		String[] options ={""};
		if (fileLists.get(longPressItemSelected).getType() == MyFile.MYFILE_TYPE_IMAGE) {
			options = new String[] {"Rename", "Set as Wallpaper"};
		} else if (fileLists.get(longPressItemSelected).getType() == MyFile.MYFILE_TYPE_AUDIO) {
			options = new String[] {"Rename", "Set as Ringtone"};
		}
		
		dialog = new Dialog(this);
		dialog.setTitle("Do you want to ");
//		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		ListView optionList = new ListView(this);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		          android.R.layout.simple_list_item_1, android.R.id.text1, options);
		optionList.setAdapter(adapter);
		
		optionList.setOnItemClickListener(new MyContextMenuListener(this));
        dialog.setContentView(optionList);
        dialog.show();
         
	}
	
	public void menuSelected(int pos) {
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
		if (pos == 0) {
			askNewFile(longPressItemSelected);
			
		} else if (pos == 1) { /// set as wallpaper or ringtone
			if (fileLists.get(longPressItemSelected).getType() == MyFile.MYFILE_TYPE_AUDIO) {
				// setting ringtone
				
				File ringtoneFile = fileLists.get(longPressItemSelected).getFileObject();
				
				try {
					
					try { // check if the audio is found already in ringtone db
						Uri uri = MediaStore.Audio.Media.getContentUriForPath(ringtoneFile.getAbsolutePath());
						getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + ringtoneFile.getAbsolutePath() + "\"", null);
					} catch (Exception ex1) {
						ex1.printStackTrace();
						
					}
					
					ContentValues values = new ContentValues();
					values.put(MediaStore.MediaColumns.DATA, ringtoneFile.getAbsolutePath());
					values.put(MediaStore.MediaColumns.TITLE, ringtoneFile.getName());
					values.put(MediaStore.MediaColumns.SIZE, ringtoneFile.length());
					values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
					values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
					values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
					values.put(MediaStore.Audio.Media.IS_ALARM, false);
					values.put(MediaStore.Audio.Media.IS_MUSIC, false);

					//Insert it into the database
					Uri uri = MediaStore.Audio.Media.getContentUriForPath(ringtoneFile.getAbsolutePath());
					Uri newUri = getContentResolver().insert(uri, values);

					RingtoneManager.setActualDefaultRingtoneUri(
							this,  RingtoneManager.TYPE_RINGTONE, newUri);
					
					Toast.makeText(this, "Ringtone is Set ", Toast.LENGTH_SHORT).show();
					
				} catch (Exception ex) {
					ex.printStackTrace();
					Toast.makeText(this, "Ringtone is not Set ", Toast.LENGTH_SHORT).show();
				}
				
				
				
			} else if (fileLists.get(longPressItemSelected).getType() == MyFile.MYFILE_TYPE_IMAGE) {
				try {
					WallpaperManager wpm = WallpaperManager.getInstance(this);
					
				    Bitmap myBitmap = BrowserUtil.loadImage(fileLists.get(longPressItemSelected).getFileObject().getAbsolutePath(), this, true);
				    if (myBitmap != null) {  
				        	wpm.setBitmap(myBitmap);
				        	Toast.makeText(this, "WallPaper is set", Toast.LENGTH_SHORT).show();
				    } else {
				    	Toast.makeText(this, "WallPaper is not set", Toast.LENGTH_SHORT).show();
				    }
				} catch (Exception ex) {
//					ex.printStackTrace();
					Toast.makeText(this, "WallPaper is not set", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	
	public void startMailIntent() {
		
		Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
	            "mailto","nid.kannan@gmail.com", null));
		
		String versionName = "0.0.4";
		int versionCode = 4;
		try {
			PackageInfo pm = getPackageManager().getPackageInfo(getPackageName(), 0);
			versionName = pm.versionName;
			versionCode = pm.versionCode;
		} catch (Exception ex) {}
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback : NiD File Viewer Ver " + versionCode);
		
		StringBuffer bodyText = new StringBuffer();
		
		bodyText.append("Your feedback please\n");
		bodyText.append("App Name: NiD File Viewer \n");
		bodyText.append("Version Name :" + versionName +"\n");
		bodyText.append("Version Code :" + versionCode + "\n");
		
		bodyText.append("Phone Details \n");
		
		bodyText.append(Build.MANUFACTURER + " " + Build.MODEL +  " "+ Build.PRODUCT +"\n");
		bodyText.append("OS :" + Build.VERSION.RELEASE);
		bodyText.append("\n\n\n");
		
		emailIntent.putExtra(Intent.EXTRA_TEXT, bodyText.toString());
		startActivity(Intent.createChooser(emailIntent, "Send email..."));
	}

	private int longPressItemSelected = 0;
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos,
			long arg3) {
		// TODO Auto-generated method stub
		longPressItemSelected = pos;
		
		if (fileLists.get(pos).getFileObject().canRead() && 
				fileLists.get(pos).getFileObject().canWrite() &&
				fileLists.get(pos).getFileObject().getParentFile().canWrite() &&
				!markedList.isMarked(fileLists.get(pos))) {
			if (fileLists.get(pos).getType() == MyFile.MYFILE_TYPE_AUDIO || 
					fileLists.get(pos).getType() == MyFile.MYFILE_TYPE_IMAGE) {
				showContextPopupDialog();	
			} else {
				askNewFile(pos);
			}
			
			
		} else {
			Toast.makeText(this, R.string.rename_not_allowed_txt , Toast.LENGTH_SHORT).show();
		}
		return true;
	}
	
	
	private void askNewFile(final int index) {
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle(R.string.rename_title_txt);
		alert.setMessage(R.string.rename_msg_txt);
		
		final int position = index;

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);
		input.setText(fileLists.get(index).getFileObject().getName());

		alert.setPositiveButton(R.string.new_ok_txt, new DialogInterface.OnClickListener() {
			
		public void onClick(DialogInterface dialog, int whichButton) {
			
			String value = input.getText().toString();
			
			if (value.trim().isEmpty()) {
				showErrorMessage(R.string.rename_file_name_empty_txt);
			} else {
				
				File file = PSFileReader.rename(fileLists.get(index).getFileObject(), value);
			
				if (file != null) {
					fileLists.set(index, new MyFile(file));
					myAdapter.notifyDataSetChanged();
				} else {
					showErrorMessage(R.string.rename_not_allowed_txt);
					//				parent.myAdapter.notifyDataSetChanged();
					//				parent.updateCurrentPage();
				}
			}
		
			// Do something with value!
		 	}
			});

		alert.setNegativeButton(R.string.new_cancel_txt, new DialogInterface.OnClickListener() {
		 public void onClick(DialogInterface dialog, int whichButton) {
		     // Canceled.
		}
		});

		alert.show();
		
	}
	
	private void showErrorMessage(int message) {
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle(R.string.error_title_txt);
		
		alert.setMessage(message);
		
		alert.setNeutralButton(R.string.new_ok_txt, new DialogInterface.OnClickListener() {
			
		public void onClick(DialogInterface dialog, int whichButton) {
			}	
			});
		
		alert.show();
	}

}
