package com.ps.myfilebrowser.data;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TreeSet;

import com.ps.myfilebrowser.util.BrowserUtil;
import com.ps.myfilebrowser.util.MemoryStatusCheck;

import android.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class MyFile {
	
	public static final int MYFILE_TYPE_DIR = 1;  // 0
	public static final int MYFILE_TYPE_TXT = 2; // 1
	public static final int MYFILE_TYPE_IMAGE = 4; // 2
	public static final int MYFILE_TYPE_AUDIO = 16; // 3
	public static final int MYFILE_TYPE_VIDEO = 32; // 4
	public static final int MYFILE_TYPE_PDF = 64; // 5
	public static final int MYFILE_TYPE_APK = 128; // 6
	public static final int MYFILE_TYPE_HTML = 256; // 7
	public static final int MYFILE_TYPE_XML = 512; // 8
	public static final int MYFILE_TYPE_VCF = 1024; // 9
	public static final int MYFILE_TYPE_ZIP = 2048; // 10
	public static final int MYFILE_TYPE_DOC = 4096; // 11
	public static final int MYFILE_TYPE_XLS = 8192; // 12
	
	
	public static final int MYFILE_TYPE_UNKNOWN = 32768; // 15
	
	public static final String ROOT = "/";
	
	public final static TreeSet<String>  IMAGE_TYPES =  
			new TreeSet<String>( Arrays.asList(
					new String[]{"jpg", "png", "jpeg", "raw" , "bmp", "gif"}));
	public static final TreeSet<String>  VIDEO_TYPES = 
		new TreeSet<String> (Arrays.asList(new String[] {"mp4", "3gp", "m4v" }));

	public static final TreeSet<String>  AUDIO_TYPES = 
			new TreeSet<String> (Arrays.asList(new String[] {"mp3","flac", "mid", "imy", "ota", "ogg", "m4a" , "wav", "m3u"}));
	
	public static final TreeSet<String>  HTML_TYPES = 
			new TreeSet<String> (Arrays.asList(new String[] {"html", "htm"}));
	
	public static final TreeSet<String>  TXT_TYPES = 
			new TreeSet<String> (Arrays.asList(new String[] {"txt"}));
	
	public static final TreeSet<String>  PDF_TYPES = 
			new TreeSet<String> (Arrays.asList(new String[] {"pdf"}));
	
	public static final TreeSet<String>  APK_TYPES = 
			new TreeSet<String> (Arrays.asList(new String[] {"apk"}));
	
	public static final TreeSet<String>  XML_TYPES = 
			new TreeSet<String> (Arrays.asList(new String[] {"xml"}));

	public static final TreeSet<String>  VCF_TYPES = 
			new TreeSet<String> (Arrays.asList(new String[] {"vcf"}));

	public static final TreeSet<String>  ZIP_TYPES = 
			new TreeSet<String> (Arrays.asList(new String[] {"zip"}));

	public static final TreeSet<String>  DOC_TYPES = 
			new TreeSet<String> (Arrays.asList(new String[] {"doc", "docx"}));
	
	public static final TreeSet<String>  XLS_TYPES = 
			new TreeSet<String> (Arrays.asList(new String[] {"xls"}));

	
	private String name;
	private String parent;
	private String ext;
	private int type;
	private Bitmap icon;
	private long size;
	private String formatedSize;
	private String creationDate;
	
	private int subfilesCount;
	
	private SimpleDateFormat dateFormater = new SimpleDateFormat("dd MMM yy");
	
	
	private File file;
	
	public MyFile(File file) {
		
		this.file = file;
		parseFile();
	}
	
//	public MyFile(String displayName, File file) {
//		
//		this.file = file;
////		parseFile();
//		name = displayName;
//		type = MYFILE_TYPE_DIR;
//		ext="";
//		icon = BrowserUtil.getBackFolderIcon();
//		
//	}
	
	private void parseFile() {
		
		String fileName = file.getName();
		parent = file.getParent();
		
		
		
		
		creationDate = dateFormater.format(new Date(file.lastModified()));
		
		
		if (file.isDirectory()) {
			
			type = MYFILE_TYPE_DIR;
			name = fileName;
			ext="";
//			Log.i("Kannan:MyFileBrowser in Directory", "name :" + name + " ext: "+ ext);
			
			icon = BrowserUtil.getIcon(type);
			
			size = file.getTotalSpace();
			
			formatedSize = MemoryStatusCheck.formatMemorySize(size);
			
			if (file.canRead()) {
				String[] subfolder = file.list();
				subfilesCount = subfolder==null?0:subfolder.length;
				
			}
			
			
		} else {
			subfilesCount = 0;
			size = file.length();
			formatedSize = MemoryStatusCheck.formatMemorySize(size);
			if (fileName.contains(".")) {
				name = fileName.substring(0, fileName.lastIndexOf("."));
				ext = fileName.substring(fileName.lastIndexOf(".")+1);
				
				if (name == null || name.isEmpty()) {
					name = ext;
				}
				if (ext != null) {
					ext = ext.toLowerCase();
				}
				
			} else {
				name = fileName;
				ext = "";
			}

//			Log.i("Kannan:MyFileBrowser", "name :" + name + " ext: "+ ext);
			
			if (ext == "") {
				// does nothing
				type = MYFILE_TYPE_UNKNOWN;
				icon = BrowserUtil.getIcon(type);
				
			} else if (IMAGE_TYPES.contains(ext)) {
				type = MYFILE_TYPE_IMAGE;
			} else if (VIDEO_TYPES.contains(ext)) {
				type = MYFILE_TYPE_VIDEO;
			}else if (AUDIO_TYPES.contains(ext)) {
				type = MYFILE_TYPE_AUDIO;
				icon = BrowserUtil.getIcon(type);
			}else if (PDF_TYPES.contains(ext)) {
				type = MYFILE_TYPE_PDF;
				icon = BrowserUtil.getIcon(type);
				
			}else if (HTML_TYPES.contains(ext)) {
				type = MYFILE_TYPE_HTML;
				icon = BrowserUtil.getIcon(type);
				
			} else if (APK_TYPES.contains(ext)) {
				type = MYFILE_TYPE_APK;
				icon = BrowserUtil.getIcon(type);
			} else if (TXT_TYPES.contains(ext)) {
				type = MYFILE_TYPE_TXT;
				icon = BrowserUtil.getIcon(type);
			} else if (XML_TYPES.contains(ext)) {
				type = MYFILE_TYPE_XML;
				icon = BrowserUtil.getIcon(type);
			} else if (VCF_TYPES.contains(ext)) {
				type = MYFILE_TYPE_VCF;
				icon = BrowserUtil.getIcon(type);
			} else if (ZIP_TYPES.contains(ext)) {
				type = MYFILE_TYPE_ZIP;
				icon = BrowserUtil.getIcon(type);
			} else if (DOC_TYPES.contains(ext)) {
				type = MYFILE_TYPE_DOC;
				icon = BrowserUtil.getIcon(type);
			} else if (XLS_TYPES.contains(ext)) {
				type = MYFILE_TYPE_XLS;
				icon = BrowserUtil.getIcon(type);
			} else {
				type = MYFILE_TYPE_UNKNOWN;
				icon = BrowserUtil.getIcon(type);
			}
		}
		
		
	}
	
	public boolean canRead() {
		return file.canRead();
	}
	
	public boolean canWrite() {
		return file.canWrite();
	}
	
	public int getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}
	public String getExt() {
		return ext;
	}
	
	public String getParent() {
		return parent;
	}
	public File getFileObject() {
		return file;
	}
	public void setIcon(Bitmap icon) {
		if (icon == null) {
			icon = BrowserUtil.getIcon(type);
		} else {
			this.icon = icon;
		}
	}
	
	public Bitmap getIcon() {
		return icon;
		
	}
	
	public long getSize() {
		return size;
	}
	public int getNoOfFolders() {
		return subfilesCount;
		
	}
	public String getCreationDate() {
		return creationDate;
	}
	public String getFormatedSize() {
		return formatedSize;
	}
	
	public boolean isMatch(MyFileOptions options) {
		if (options.type == 0) {
			return isReadOptionMatch(options);
		} else {
			if ((options.type & type) > 0) { 
				return isReadOptionMatch(options);
			} else {
				return false;
			}
		}
	}
	
	public boolean isReadOptionMatch(MyFileOptions options) {
		if (options.all_file_options) {
			return true;
		} else if (options.writable_files_only) {
			return file.canWrite();
		} else if (options.read_or_write) {
			return (file.canWrite() || file.canRead());
		} else if (options.read_only_files) {
			return  (file.canRead() && !file.canWrite());
		} else {
			return false;
		}
		
	}
	
	@Override
	public boolean equals(Object myFile) {
		
		
		if (file.getAbsolutePath().equals(((MyFile)myFile).getFileObject().getAbsolutePath())) {
			
			Log.i("MyFile ", "path is equal " + file.getAbsolutePath());
			if (file.exists()) {
				
				if (file.isDirectory() && ((MyFile)myFile).getFileObject().isDirectory()) {
					Log.i("MyFile ", "path is equal both are directory" + file.getAbsolutePath());
					return true;
				} else if (!file.isDirectory() && !((MyFile)myFile).getFileObject().isDirectory()) {
					Log.i("MyFile ", "path is equal both are not directory" + file.getAbsolutePath());
					return true;
				}
			} else {
				 if (!((MyFile)myFile).getFileObject().exists()) {
					 Log.i("MyFile ", "path is equal both does not exists" + file.getAbsolutePath());
					 return true;
				 }
			}
			
		}
		return false;
	}
	
}
