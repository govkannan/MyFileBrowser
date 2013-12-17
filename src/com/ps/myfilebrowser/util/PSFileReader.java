package com.ps.myfilebrowser.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.TreeMap;

import com.ps.myfilebrowser.data.MarkedFileList;
import com.ps.myfilebrowser.data.MyFile;
import com.ps.myfilebrowser.data.MyFileOperationListener;

import android.util.Log;

public class PSFileReader {
	
	public static boolean cancelCopyOperation = false;
	
	public static StringBuffer readFile(String path) {
		File file = new File(path);
		
		StringBuffer toRet = new StringBuffer();
		FileReader fis = null;
		BufferedReader dis = null;
		if (file.canRead()) {
			try {
				fis = new FileReader(file);
				 dis = new BufferedReader(fis);
				String tmp = null;
				while ((tmp =dis.readLine())!= null) {
					toRet.append(tmp);
					toRet.append("\n");
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				try {
					dis.close();
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
//		Log.i("FileReader ", toRet.toString());
		return toRet;
		
	}
	
	public static boolean canWrite(String path) {
		
		File file = new File(path);
		
		if (file.canWrite()) {
			return file.getParentFile().canWrite();
		}
		
		return false;
	}
	
	public static boolean writeFile(String path, String txt) {
		
		StringBuffer buffer = new StringBuffer(txt);
		
		
		File file = new File(path);
		if (file.canWrite()) {
			try {
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(txt.getBytes());
				
				fos.flush();
				fos.close();
				
				return true;
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return false;
	}
	
	public static int createFile(String path, String fileName, MyFileOperationListener listener) {
		
		File parent = new File(path);
		
		if (fileName== null || fileName.isEmpty()) {
			return -4;
		}
		if (parent.canWrite()) {
			File myFile = new File(path + File.separatorChar + fileName);
			
			if (myFile.exists()) {
				return -2;
			}
			try {
				myFile.createNewFile();
				 if (listener != null)
					 listener.fileCreated(myFile.getAbsolutePath());
				return 0;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return -3;
			}
			
		} else {
			return -1;
		}
	}
	
	public static int createFolder(String path, String folderName, MyFileOperationListener listener) {
		
		File parent = new File(path);

		if (folderName== null || folderName.isEmpty()) {
			return -4;
		}

		if (parent.canWrite()) {
			File myFile = new File(path + File.separatorChar + folderName);
			
			if (myFile.exists()) {
				return -2;
			}
			try {
				myFile.mkdir();
				 if (listener != null)
					 listener.fileCreated(myFile.getAbsolutePath());
				return 0;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return -3;
			}
			
		} else {
			return -1;
		}
	}
	
	public static boolean deleteFiles(MarkedFileList list, MyFileOperationListener listener) {
		
		ArrayList<MyFile> table = list.getFilePaths();
		
		MyFile myFile;
		File file;
		for (int i = 0; i < table.size(); i++ ) {
			 myFile = table.get(i);
			 file = myFile.getFileObject();
			 try {
				 if (file.isDirectory()) {
					 deleteDirectory(file);
				 } else {
					 file.delete();
				 }
				 if (listener != null)
					 listener.fileDeleted(myFile);
			 } catch (Exception ex) {
				 
			 }
		}
		return true;
	}
	
	
	public static boolean copyFiles(MarkedFileList list, String destination, MyFileOperationListener listener) {
		
		cancelCopyOperation = false;
		ArrayList<MyFile> table = list.getFilePaths();
		MyFile myFile;
		File file;
		
		for (int i = 0; i < table.size(); i++ ) {
			
			if (cancelCopyOperation) {
				return false;
			}
			 myFile = table.get(i);
			 file = myFile.getFileObject();
			 
			 try {
				 if (file.isDirectory()) {
					 copyFolder(file.getAbsolutePath(), destination, listener);
					 
					 if (list.isCut) {
						 deleteDirectory(file);
					 }
					 
				 } else {
					 createFile(destination, file.getName(), null);
					 copyFile(file.getAbsolutePath(), destination + File.separatorChar + file.getName(), listener);
					 
					 if (list.isCut) {
						 file.delete();
					 }
					 
					 
				 }
				 if (listener != null)
					 listener.fileCreated(destination + File.separatorChar + file.getName());
				 
			 } catch (Exception ex) {
				 
			 }
		}
//		listener.fileCopyFinished();
		return true;
	}
	
//	public static boolean moveFiles(MarkedFileList list, String destination, MyFileOperationListener listener ) {
//		
//		return true;
//	}
	
	private static boolean deleteDirectory(File file) {
		
		
			File[] files = file.listFiles();
			File tmp;
			for (int i=0; i<files.length; i++) {
				tmp = files[i];
				if (tmp.isDirectory()) {
					deleteDirectory(tmp);  // recursive call
				} else {
					try {
						Log.i("Delete File " , tmp.getName());
						tmp.delete();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
			try {
				Log.i("Delete Dir " , file.getName());
				file.delete();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			return true;
		}
	
	public static boolean copyFile(String src, String dst, MyFileOperationListener listener) {
		
		File srcFile = new File(src);
		File dstFile = new File(dst);
		
		if (cancelCopyOperation) {
			return false;
		}

		
		try {
			if (listener != null) {
				listener.updateProgress(src);
			}
			FileInputStream fis = new FileInputStream(srcFile);
			FileOutputStream fos = new FileOutputStream(dstFile);
			
			byte[] buffer = new byte[1024];
			int len;
			while ((len = fis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
				if (cancelCopyOperation) {
					break;
				}

			}
			
			fis.close();
			fos.close();
			
			if (cancelCopyOperation) {
				dstFile.delete();
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	public static boolean copyFolder(String src, String destination, MyFileOperationListener listener) {
		
		
		File srcFile = new File(src);
		
		File dstFile = new File(destination + File.separatorChar + srcFile.getName());
		

		if (!dstFile.exists()) {
			dstFile.mkdir();
		}
		
		File[] files = srcFile.listFiles();
		
		File tmpFile;
		for (int i = 0; i < files.length; i++) {

			if (cancelCopyOperation) {
				break;
			}

			tmpFile = files[i];
			if (tmpFile.isDirectory()) {
				copyFolder(files[i].getAbsolutePath(),dstFile.getAbsolutePath(), listener);
			} else {
				createFile(dstFile.getAbsolutePath(), tmpFile.getName(), null);
				copyFile(tmpFile.getAbsolutePath(), dstFile.getAbsolutePath() + File.separatorChar + tmpFile.getName(), listener);
			}
		}
		
		if (cancelCopyOperation) {
			return false;
		}
		return true;
	}
	
	public static File rename(File file, String text) {
		
		String ext = null;
		File renameFile;
		
		if (file.canRead() && file.canWrite()) {
			
		} else { // not possible to change name
			return null;
		}
		
		String sfName = file.getName();
		
		String parentFolder = file.getParent();
		
		if (file.isDirectory()) {
			
			if (file.getParentFile().canWrite()) {
				
				renameFile = new File(parentFolder + File.separatorChar + text.trim());
				file.renameTo(renameFile);
				
			} else {
				return null;
			}
			
			
		} else {
			if (sfName.lastIndexOf(".") != -1)
				ext = sfName.substring(sfName.lastIndexOf("."));
			if (text.lastIndexOf(".") == -1) {
				text = text.trim() + ext;
			}
			renameFile = new File(parentFolder, text);
			file.renameTo(renameFile);
		}
		return new File(renameFile.getAbsolutePath());
	}

}
