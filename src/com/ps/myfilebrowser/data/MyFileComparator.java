package com.ps.myfilebrowser.data;

import java.util.Comparator;

public class MyFileComparator implements Comparator<MyFile> {

	@Override
	public int compare(MyFile arg0, MyFile arg1) {
		// TODO Auto-generated method stub
		
		// check for folder and directory 
		
		
		int toRet =  arg0.getFileObject().getName().compareToIgnoreCase(arg1.getFileObject().getName());
		
		if (toRet == 0) {
			
			if (arg0.getFileObject().isDirectory() && !arg1.getFileObject().isDirectory()) {
				return 1; 
			} else if (!arg0.getFileObject().isDirectory() && arg1.getFileObject().isDirectory()) {
				return -1;
			} else {
				return 0;  
			}
			
		} else {
			return toRet;
		}
		
	}

}
