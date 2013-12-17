package com.ps.myfilebrowser.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.TreeMap;
import java.util.Vector;

import android.net.Uri;

public class MarkedFileList {
	
	TreeMap<Integer, MyFile> markedItems;
	
	public Vector<MyFile> deletedFiles;
	public Vector<MyFile> addedFiles;
	
	public boolean isCut = false;
	public boolean isCopy = false;
	
	int readOnlyCount = 0;
	
	public MarkedFileList() {
		markedItems = new TreeMap<Integer, MyFile>();
		deletedFiles = new Vector<MyFile>();
		addedFiles = new Vector<MyFile>();
	}
	
	public void markItem(int itemid, MyFile path) {
		if (markedItems.containsKey(itemid)) {
			unMarkItem(itemid);
		} else {
			markedItems.put(itemid, path);
			
			if (!path.canWrite()) {
				readOnlyCount++;
			}
		}
		
	}
	
	public void unMarkItem(int itemid) {
		if (markedItems.containsKey(itemid)) {
			if (!markedItems.get(itemid).canWrite()) {
				readOnlyCount--;
			}
			markedItems.remove(itemid);
			
		}		
	}
	
	public boolean isMarked(int itemId) {
		return markedItems.containsKey(itemId);
	}
	public boolean isMarked(MyFile file) {
		return markedItems.containsValue(file);
	}

	
	public void unMarkAll() {
		markedItems.clear();
		readOnlyCount = 0;
		isCut=false;
		isCopy=false;
	}
	
	public TreeMap getTable() {
		return  markedItems;
	}
	
	public int getSize() {
		return markedItems.size();
	}
	
	public ArrayList<MyFile> getFilePaths() {
		return new ArrayList<MyFile>(markedItems.values());
	}
	
	public boolean containsReadOnlyFiles() {
		return readOnlyCount>0;
	}
	
	private boolean hasReadOnlyFiles() {
		ArrayList<MyFile> tmp = new ArrayList<MyFile>(markedItems.values());
		
		for (int i=0; i< tmp.size(); i++) {
			if (!tmp.get(i).canWrite()) {
				return true;
			}
		}
		return false;
	}

	public void clearAddDeletedFiles() {
		deletedFiles.removeAllElements();
		addedFiles.removeAllElements();
	}
	
	public boolean containsDir(String fileName) {
	
		if (markedItems.isEmpty()) {
			return false;
		}
//		File tmpFile = new File(fileName);
		
		for (MyFile file:markedItems.values()) {
			if (file.getFileObject().isDirectory()) {
				if (fileName.startsWith(file.getFileObject().getAbsolutePath())) {
					return true;
				}
//				if (file.getFileObject().getAbsolutePath().equals(fileName)){
//					return true;
//				} else if(file.getFileObject().getAbsolutePath().equals(tmpFile.getParent())) {
//					return true;
//				}
			}
		}
		return false;
		
	}
	
	public ArrayList<Uri> toUriList() {
		ArrayList<MyFile> tmp = new ArrayList<MyFile>(markedItems.values());
		
		ArrayList<Uri> toRet = new ArrayList<Uri>();
		
		for (int i=0; i<tmp.size(); i++) {
			if (tmp.get(i).getFileObject().canRead()) {
				if (tmp.get(i).getFileObject().isDirectory()) {
					parseFolderToUri(tmp.get(i).getFileObject(), toRet);
				} else {
					toRet.add(Uri.fromFile(tmp.get(i).getFileObject()));
				}
			}
		}
		
		return toRet;
		
	}
	
	private void parseFolderToUri(File folder, ArrayList<Uri> list) {
		
		if (folder.isDirectory() && folder.canRead()) {
			
			File[] files = folder.listFiles();
			
			if (files != null) {
				
				for (int i=0; i<files.length; i++) {
					if(files[i].isDirectory()) {
						parseFolderToUri(files[i], list);
					} else {
						if (files[i].canRead()) {
							list.add(Uri.fromFile(files[i]));
						}
					}
				}
			}
		}
		
		
	}
}
