package com.ps.myfilebrowser.zipviewer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.os.Environment;

public class ZIPParser {
	
	public static final int FILE_DOESNOT_EXIST = -1;
	public static final int FILE_NO_READ = -2;
	public static final int FILE_VALID = 0;
	private String fileName;
	private File fileObject;
	
	private String lastExtractedFolder;
	
	private ExtractUpdateListener extractStatusListener;
	
	
	public ZIPParser(String fileName) {
		this.fileName = fileName;
		isValidFile();
	}
	
	public int isValidFile() {
		
		fileObject = new File(fileName);
		if (fileObject.exists()) {
			return FILE_DOESNOT_EXIST;
		}
		if (!fileObject.canRead()) {
			return FILE_NO_READ;
		}
		return FILE_VALID;
		
	}
	
	public void setExtractUpdateListener(ExtractUpdateListener listener) {
		this.extractStatusListener = listener;
	}
	
	public ArrayList<String> parseEntries() {
		
		ZipInputStream zipInputStream;
		TreeSet<String> zipFileEntries = new TreeSet<String>();
        FileInputStream fis;
		try {
			fis = new FileInputStream(fileName);
			zipInputStream = new ZipInputStream (new BufferedInputStream(fis));
			
	        ZipEntry entry ;
	        
	        try {
				while ((entry = zipInputStream.getNextEntry()) != null) {
					if (entry.isDirectory()) {
						
					} else {  // add only files
						zipFileEntries.add(entry.getName());
					}
				}
				zipInputStream.close();
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
	        
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return new ArrayList<String>(zipFileEntries);
	}

	public void extractAll() {
		
		if (lastExtractedFolder == null) {
			updateLastExtractedFolder();
		}
		
		extractFiles(null, lastExtractedFolder);
	}
	
	public void extractFiles(ArrayList<String> files) {
		
		if (lastExtractedFolder == null) {
			updateLastExtractedFolder();
		}
		extractFiles(files, lastExtractedFolder);
	}
	
	private void updateLastExtractedFolder() {
		
		String fileName = fileObject.getName();
		
		if(fileName.contains(".")) {
			lastExtractedFolder = fileName.substring(0, fileName.lastIndexOf("."));
//			lastExtractedFolder = lastExtractedFolder + "_extract";
		} else {
			lastExtractedFolder = fileName + "_extract";
		}
		
		if (fileObject.getParentFile().canWrite()) {
			lastExtractedFolder = fileObject.getParent() + File.separatorChar + lastExtractedFolder;
		} else {
			lastExtractedFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separatorChar + lastExtractedFolder;
		}
	}
	
	public String getLastExtractedFolder() {
		return lastExtractedFolder;
	}
	
//	public void extractAll(String destinationFolder) {
//		
//        FileInputStream fis;
//        FileOutputStream fos;
//        BufferedOutputStream bos;
//		try {
//			fis = new FileInputStream(fileName);
//			
//			File tmp;
//			int MAX_LEN = 2048;
//			byte[] buffer = new byte[MAX_LEN];
//			int count;
//			
//			ZipInputStream zipInputStream = new ZipInputStream (new BufferedInputStream(fis));
//	        ZipEntry entry ;
//	        
//	        tmp = new File(destinationFolder);
//	        if (!tmp.exists()) {
//	        	tmp.mkdir();
//	        }
//	        
//	        try {
//				while ((entry = zipInputStream.getNextEntry()) != null) {
//					
//					if (entry.isDirectory()) {
//						tmp = new File(destinationFolder + File.separatorChar + entry.getName());
//						if (!tmp.exists()) {
//							tmp.mkdirs();
//						}
//					} else {
//						tmp = new File(destinationFolder + File.separatorChar + entry.getName());
//						if (!tmp.exists()) {
//							tmp.createNewFile();
//						}
//						
//						bos = new BufferedOutputStream(new FileOutputStream(tmp), MAX_LEN);
//						while ((count = zipInputStream.read(buffer, 0, MAX_LEN)) != -1) {
//			                bos.write(buffer, 0, count);
//						}
//						bos.flush();
//						bos.close();
//					}
//				}
//				zipInputStream.close();
//				fis.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	public void extractFiles(ArrayList<String> files, String destinationFolder) {
		boolean extractAll = false;
		if (files == null || files.size() <=0 ) {
			extractAll = true;
		}
		
        FileInputStream fis;
        FileOutputStream fos;
        BufferedOutputStream bos;
		try {
			fis = new FileInputStream(fileName);
			
			File tmp;
			int MAX_LEN = 2048;
			byte[] buffer = new byte[MAX_LEN];
			int count;
			
			ZipInputStream zipInputStream = new ZipInputStream (new BufferedInputStream(fis));
	        ZipEntry entry ;
	        
	        tmp = new File(destinationFolder);
	        if (!tmp.exists()) {
	        	tmp.mkdir();
	        }
	        
	        try {
				while ((entry = zipInputStream.getNextEntry()) != null) {
					
					if (extractAll || files.contains(entry.getName())) {
					
						if (extractStatusListener != null) {
							extractStatusListener.updateProgress(entry.getName());
						}
						
						if (entry.isDirectory()) {
							tmp = new File(destinationFolder + File.separatorChar + entry.getName());
							if (!tmp.exists()) {
								tmp.mkdirs();
							}
						} else {
							tmp = new File(destinationFolder + File.separatorChar + entry.getName());
							if (!tmp.exists()) {
								// check if folders are present
								File pDir = tmp.getParentFile();
								if (!pDir.exists()) {
									pDir.mkdirs();
								}
								tmp.createNewFile();
							}
						
							bos = new BufferedOutputStream(new FileOutputStream(tmp), MAX_LEN);
							while ((count = zipInputStream.read(buffer, 0, MAX_LEN)) != -1) {
								bos.write(buffer, 0, count);
							}
							bos.flush();
							bos.close();
						}
					}
				}
				zipInputStream.close();
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	} catch (FileNotFoundException e) {
	    		// TODO Auto-generated catch block
	    		e.printStackTrace();
	    	}
	}
}
