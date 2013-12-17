package com.ps.myfilebrowser.data;

public class MyFileOptions {
	
	public int type = 0;  // 0 indicates all files
	public boolean all_file_options = true;
	public boolean read_or_write = true;
	public boolean read_only_files = false; 
	public boolean writable_files_only = false;
	
	public void reset() {
		type = 0;
		all_file_options = true;
		read_or_write = false;
		writable_files_only = false;
		read_only_files = false;
	}
	

}
