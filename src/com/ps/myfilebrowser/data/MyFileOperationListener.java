package com.ps.myfilebrowser.data;

public interface MyFileOperationListener {
	
	void fileDeleted(MyFile file);
	void fileCreated(String path);
	void fileCopyFinished();
	void updateProgress(String fileName);

}
