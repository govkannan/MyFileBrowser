package com.ps.myfilebrowser;

import java.io.File;

import com.ps.myfilebrowser.util.BrowserUtil;
import com.ps.myfilebrowser.util.PSFileReader;
import com.ps.myfilebrowser.util.PasteTask;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class EditModeIconManager implements OnClickListener {
	
	private BrowserActivity parent;
	LinearLayout editModeLayout;
	
	Bitmap unmarkIcon, markIcon;
	
	private PasteTask pasteTask;
	
	
	public boolean isMarkSeveral;
	
	private Button  newFolder, markSeveral, markAll, copy, cut, paste, delete, share;
	private Button newFile;
	
	public  EditModeIconManager(BrowserActivity parent, LinearLayout editModeLayout) {
		this.parent = parent;
		this.editModeLayout = editModeLayout;
		
		parse();
	}
	
	private void parse() {
		newFile = (Button)editModeLayout.findViewById(R.id.new_file_button);
		newFolder = (Button)editModeLayout.findViewById(R.id.new_folder_button);
		
		markSeveral = (Button)editModeLayout.findViewById(R.id.mark_several_button);
		markAll = (Button)editModeLayout.findViewById(R.id.select_all_button);
		
		copy = (Button)editModeLayout.findViewById(R.id.copy_button);
		cut = (Button)editModeLayout.findViewById(R.id.cut_button);
		paste = (Button)editModeLayout.findViewById(R.id.paste_button);
		delete = (Button)editModeLayout.findViewById(R.id.delete_button);
		share = (Button)editModeLayout.findViewById(R.id.share_button);
		
		newFile.setOnClickListener(this);
		newFolder.setOnClickListener(this);
		markSeveral.setOnClickListener(this);
		markAll.setOnClickListener(this);
		copy.setOnClickListener(this);
		cut.setOnClickListener(this);
//		paste.setOnClickListener(this);
//		delete.setOnClickListener(this);
		
		if (unmarkIcon == null) {
			unmarkIcon = BrowserUtil.getIconFromResource(R.drawable.mark_several_off_icon);
			markIcon = BrowserUtil.getIconFromResource(R.drawable.select_icon);
		}
	}
	
	public void init(String dir) {
		
		File file = new File(dir);
		
		if (file.canWrite()) {
			newFile.setVisibility(View.VISIBLE);
			newFolder.setVisibility(View.VISIBLE);
		
		} else {
			newFile.setVisibility(View.GONE);
			newFolder.setVisibility(View.GONE);
		}
		
		if (isMarkSeveral) {
			
			if (parent.markedList.isCopy || parent.markedList.isCut) {
				
				copy.setVisibility(View.GONE);
				cut.setVisibility(View.GONE);
				markAll.setVisibility(View.GONE);
				
				if (file.canWrite()) {
					paste.setVisibility(View.VISIBLE);
				} else {
					paste.setVisibility(View.GONE);
				}
				
			} else {
				markAll.setVisibility(View.VISIBLE);
				copy.setVisibility(View.VISIBLE);
				cut.setVisibility(View.VISIBLE);
				
			}
			
		} else {
			markAll.setVisibility(View.GONE);
			copy.setVisibility(View.GONE);
			cut.setVisibility(View.GONE);
			paste.setVisibility(View.GONE);
			delete.setVisibility(View.GONE);
			share.setVisibility(View.GONE);
		}
	}
	
	public void onMarkSeveral() {
		
		isMarkSeveral = !isMarkSeveral;
		
		if (isMarkSeveral) {
			
			Toast.makeText(parent, R.string.help_msg_on_mark_several, Toast.LENGTH_LONG).show();
//			markSeveral.setImageBitmap(unmarkIcon);
//			markSeveral.setBackgroundResource(R.drawable.unselect_button_animation);
//			markSeveral.setBackgroundResource(R.drawable.mark_several_off_icon);
			markSeveral.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.mark_several_off_icon, 0, 0);

			markAll.setVisibility(View.VISIBLE);
			copy.setVisibility(View.VISIBLE);
			cut.setVisibility(View.VISIBLE);
			paste.setVisibility(View.GONE);
			delete.setVisibility(View.VISIBLE);
			share.setVisibility(View.VISIBLE);
			parent.spinner.setEnabled(false);
			
			
		} else {
			
//			markSeveral.setBackgroundResource(R.drawable.select_icon);
			
			markSeveral.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.select_icon, 0, 0);

//			markSeveral.setBackgroundResource(R.drawable.select_button_animation);
			
			markAll.setVisibility(View.GONE);
			copy.setVisibility(View.GONE);
			cut.setVisibility(View.GONE);
			paste.setVisibility(View.GONE);
			delete.setVisibility(View.GONE);
			share.setVisibility(View.GONE);
			parent.spinner.setEnabled(true);
			
			parent.markedList.unMarkAll();
//			parent.myAdapter.notifyDataSetChanged();
			parent.updateCurrentPage();

		}
	}
	
	public void onCopyOrCut() {
		
		Toast.makeText(parent, R.string.help_msg_on_copy_cut, Toast.LENGTH_SHORT).show();
		markAll.setVisibility(View.GONE);
		copy.setVisibility(View.GONE);
		cut.setVisibility(View.GONE);
		paste.setVisibility(View.VISIBLE);
		delete.setVisibility(View.GONE);
		share.setVisibility(View.GONE);
		parent.spinner.setEnabled(true);
	}
	
	public void onPaste() {
		
		markAll.setVisibility(View.VISIBLE);
		copy.setVisibility(View.VISIBLE);
		cut.setVisibility(View.VISIBLE);
		paste.setVisibility(View.GONE);
		delete.setVisibility(View.VISIBLE);
		share.setVisibility(View.GONE);
	}
	
	public void markedListUpdated() {
		if (parent.markedList.containsReadOnlyFiles()) {
			cut.setVisibility(View.GONE);
			delete.setVisibility(View.GONE);
		} else {
			cut.setVisibility(View.VISIBLE);
			delete.setVisibility(View.VISIBLE);
		}
	}
	

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
		if (arg0 == newFile) {
			askFileNameAndCreate();
			
		} else if (arg0 == newFolder) {
			askFolderNameAndCreate();
			
		} else if (arg0 == markSeveral) {
			
			onMarkSeveral();
			
		} else if (arg0 == markAll) {
			
			for (int i=0; i < parent.fileLists.size(); i++) {
				if ( parent.fileLists.get(i).canRead()) {
					if (!parent.markedList.isMarked(i)) {
						parent.markedList.markItem(i, parent.fileLists.get(i));
					}
				}
			}
			parent.updateCurrentPage();
			
			
		} else if (arg0 == copy) {
			
			if (parent.markedList.getSize() > 0) {
				parent.markedList.isCopy = true;
				Toast.makeText(parent, parent.markedList.getSize() + " items copied", Toast.LENGTH_SHORT).show();
				parent.spinner.setEnabled(true);
			} else {
				Toast.makeText(parent, R.string.help_msg_on_copy, Toast.LENGTH_SHORT).show();
			}

		} else if (arg0 == cut) {
			
			if (parent.markedList.getSize() > 0) {
				parent.markedList.isCut = true;
				Toast.makeText(parent, parent.markedList.getSize() +" items marked for cut", Toast.LENGTH_SHORT).show();
				parent.spinner.setEnabled(true);
			} else {
				Toast.makeText(parent,R.string.help_msg_on_cut, Toast.LENGTH_SHORT).show();
				
			}
			
		} 
		
	}
	
	
	public void askFileNameAndCreate() {
		
		AlertDialog.Builder alert = new AlertDialog.Builder(parent);

		alert.setTitle(R.string.new_file_title_txt);
		alert.setMessage(R.string.new_file_message_txt);

		// Set an EditText view to get user input 
		final EditText input = new EditText(parent);
		alert.setView(input);

		alert.setPositiveButton(R.string.new_ok_txt, new DialogInterface.OnClickListener() {
			
		public void onClick(DialogInterface dialog, int whichButton) {
			
			String value = input.getText().toString();
			
			int ret = PSFileReader.createFile(parent.currentDir, value, parent);
			
			if (ret != 0) {
				showErrorMessage(ret);
			}else {
				parent.updateCurrentPage();				
//				parent.myAdapter.notifyDataSetChanged();
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
	
	private void askFolderNameAndCreate() {
		
		AlertDialog.Builder alert = new AlertDialog.Builder(parent);

		alert.setTitle(R.string.new_folder_title_txt);
		alert.setMessage(R.string.new_folder_message_txt);

		// Set an EditText view to get user input 
		final EditText input = new EditText(parent);
		alert.setView(input);

		alert.setPositiveButton(R.string.new_ok_txt, new DialogInterface.OnClickListener() {
			
		public void onClick(DialogInterface dialog, int whichButton) {
			
			String value = input.getText().toString();
			
			int ret = PSFileReader.createFolder(parent.currentDir, value, parent);
			
			if (ret != 0) {
				showErrorMessage(ret);
			} else {
//				parent.myAdapter.notifyDataSetChanged();
				parent.updateCurrentPage();
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

	private void showErrorMessage(int errCode) {
		
		AlertDialog.Builder alert = new AlertDialog.Builder(parent);

		alert.setTitle("Error");
		
		if (errCode == -1) {
			alert.setMessage("Access Denied");
		} else if (errCode == -2) {
			alert.setMessage("File/Folder exists already");
		} else if (errCode == -3) {
			alert.setMessage("UnKnown Error");
		}  else if (errCode == -4) {
			alert.setMessage("File/Folder name can't be empty");
		}
		
		alert.setNeutralButton(R.string.new_ok_txt, new DialogInterface.OnClickListener() {
			
		public void onClick(DialogInterface dialog, int whichButton) {
			}	
			});
		
		alert.show();
	}

}
