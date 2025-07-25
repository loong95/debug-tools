/*
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.future0923.debug.tools.base.hutool.core.net.multipart;

/**
 * 上传文件设定文件
 *
 * @author xiaoleilu
 *
 */
public class UploadSetting {

	/** 最大文件大小，默认无限制 */
	protected long maxFileSize = -1;
	/** 文件保存到内存的边界 */
	protected int memoryThreshold = 8192;
	/** 临时文件目录 */
	protected String tmpUploadPath;
	/** 文件扩展名限定 */
	protected String[] fileExts;
	/** 扩展名是允许列表还是禁止列表 */
	protected boolean isAllowFileExts = true;

	public UploadSetting() {
	}

	// ---------------------------------------------------------------------- Setters and Getters start
	/**
	 * @return 获得最大文件大小，-1表示无限制
	 */
	public long getMaxFileSize() {
		return maxFileSize;
	}

	/**
	 * 设定最大文件大小，-1表示无限制
	 *
	 * @param maxFileSize 最大文件大小
	 */
	public void setMaxFileSize(long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	/**
	 * @return 文件保存到内存的边界
	 */
	public int getMemoryThreshold() {
		return memoryThreshold;
	}

	/**
	 * 设定文件保存到内存的边界<br>
	 * 如果文件大小小于这个边界，将保存于内存中，否则保存至临时目录中
	 *
	 * @param memoryThreshold 文件保存到内存的边界
	 */
	public void setMemoryThreshold(int memoryThreshold) {
		this.memoryThreshold = memoryThreshold;
	}

	/**
	 * @return 上传文件的临时目录，若为空，使用系统目录
	 */
	public String getTmpUploadPath() {
		return tmpUploadPath;
	}

	/**
	 * 设定上传文件的临时目录，null表示使用系统临时目录
	 *
	 * @param tmpUploadPath 临时目录，绝对路径
	 */
	public void setTmpUploadPath(String tmpUploadPath) {
		this.tmpUploadPath = tmpUploadPath;
	}

	/**
	 * @return 文件扩展名限定列表
	 */
	public String[] getFileExts() {
		return fileExts;
	}

	/**
	 * 设定文件扩展名限定里列表<br>
	 * 禁止列表还是允许列表取决于isAllowFileExts
	 *
	 * @param fileExts 文件扩展名列表
	 */
	public void setFileExts(String[] fileExts) {
		this.fileExts = fileExts;
	}

	/**
	 * 是否允许文件扩展名<br>
	 *
	 * @return 若true表示只允许列表里的扩展名，否则是禁止列表里的扩展名
	 */
	public boolean isAllowFileExts() {
		return isAllowFileExts;
	}

	/**
	 * 设定是否允许扩展名
	 *
	 * @param isAllowFileExts 若true表示只允许列表里的扩展名，否则是禁止列表里的扩展名
	 */
	public void setAllowFileExts(boolean isAllowFileExts) {
		this.isAllowFileExts = isAllowFileExts;
	}
	// ---------------------------------------------------------------------- Setters and Getters end
}
