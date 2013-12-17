package com.ps.myfilebrowser.util;

import java.io.File;
import java.text.DecimalFormat;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.os.StatFs;

public class MemoryStatusCheck {
	
	private static DecimalFormat dFormater = new DecimalFormat("@##");
	
	public static boolean hasExternalMemory() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
	
	public static long getTotalInternalMemory() {
		return getTotalMemory(Environment.getDataDirectory().getPath());
	}

	public static long getTotalExternalMemory() {
		if (hasExternalMemory()) {
			return getTotalMemory(Environment.getExternalStorageDirectory().getPath()); 
		} else {
			return 0;
		}
		
	}
	
	public static long getAvailableInternalMemory() {
		return getAvailableMemory(Environment.getDataDirectory().getPath());
	}
	
	public static long getAvailableExternalMemory() {
		if (hasExternalMemory()) {
			return getAvailableMemory(Environment.getExternalStorageDirectory().getPath()); 
		} else {
			return 0;
		}
	}
	
	@SuppressLint("NewApi")
	public static long getAvailableMemory(String path) {
		long size, blockSize;
		StatFs  fileStatus = new StatFs(path);
		
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
			blockSize = fileStatus.getBlockSizeLong();
			size = fileStatus.getAvailableBlocksLong();
		} else {
				blockSize = fileStatus.getBlockSize();
				size = fileStatus.getAvailableBlocks();
		}
		
		size *= blockSize;
		return size;
	}
	
	@SuppressLint("NewApi")
	public static long getTotalMemory(String path) {
		long size, blockSize;
		StatFs  fileStatus = new StatFs(path);
		
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
			blockSize = fileStatus.getBlockSizeLong();
			size = fileStatus.getBlockCountLong();
		} else {
				blockSize = fileStatus.getBlockSize();
				size = fileStatus.getBlockCount();
		}
		size *= blockSize;
		return size;
		
	}
	
	public static String formatMemorySize(long size1) {
		
		String toRet;
		String suffix = "b";
		double dSize = size1;
		if (dSize >= 1024.0) {
			dSize /= 1024.0;
			suffix = "KB";
					
					if (dSize >= 1024.0) {
						dSize /= 1024.0;
						suffix = "MB";
						
						if (dSize >= 1024.0) {
							dSize /= 1024.0;
							suffix = "GB";
						}
					}
		}
		toRet = dFormater.format(dSize) + " " + suffix;
		return toRet;
	}
	
	
	public static String getInternalMemoryStatus() {
		String toRet = formatMemorySize(getAvailableInternalMemory()) + "/" + 
				formatMemorySize(getTotalInternalMemory());
		
		return toRet;
	}
	
	public static String getExternalMemoryStatus() {
		String toRet = formatMemorySize(getAvailableExternalMemory()) + "/" + 
				formatMemorySize(getTotalExternalMemory());
		return toRet;
	}

	public static long getUsedSpace(String path) {
		long size = getTotalMemory(path) - getAvailableMemory(path);
		return size;
	}
	
	public static String getFormatedUsedSpace(String path) {
		return formatMemorySize(getUsedSpace(path));
	}

}
