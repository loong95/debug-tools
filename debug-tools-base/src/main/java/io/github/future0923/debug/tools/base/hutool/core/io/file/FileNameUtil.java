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
package io.github.future0923.debug.tools.base.hutool.core.io.file;

import io.github.future0923.debug.tools.base.hutool.core.util.CharUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.ReUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;

import java.io.File;
import java.util.regex.Pattern;

/**
 * 文件名相关工具类
 *
 * @author looly
 * @since 5.4.1
 */
public class FileNameUtil {

	/**
	 * .java文件扩展名
	 */
	public static final String EXT_JAVA = ".java";
	/**
	 * .class文件扩展名
	 */
	public static final String EXT_CLASS = ".class";
	/**
	 * .jar文件扩展名
	 */
	public static final String EXT_JAR = ".jar";

	/**
	 * 类Unix路径分隔符
	 */
	public static final char UNIX_SEPARATOR = CharUtil.SLASH;
	/**
	 * Windows路径分隔符
	 */
	public static final char WINDOWS_SEPARATOR = CharUtil.BACKSLASH;

	/**
	 * Windows下文件名中的无效字符
	 */
	private static final Pattern FILE_NAME_INVALID_PATTERN_WIN = Pattern.compile("[\\\\/:*?\"<>|\r\n]");

	/**
	 * 特殊后缀
	 */
	private static final CharSequence[] SPECIAL_SUFFIX = {"tar.bz2", "tar.Z", "tar.gz", "tar.xz"};


	// -------------------------------------------------------------------------------------------- name start

	/**
	 * 返回文件名
	 *
	 * @param file 文件
	 * @return 文件名
	 * @since 4.1.13
	 */
	public static String getName(File file) {
		return (null != file) ? file.getName() : null;
	}

	/**
	 * 返回文件名<br>
	 * <pre>
	 * "d:/test/aaa" 返回 "aaa"
	 * "/test/aaa.jpg" 返回 "aaa.jpg"
	 * </pre>
	 *
	 * @param filePath 文件
	 * @return 文件名
	 * @since 4.1.13
	 */
	public static String getName(String filePath) {
		if (null == filePath) {
			return null;
		}
		int len = filePath.length();
		if (0 == len) {
			return filePath;
		}
		if (CharUtil.isFileSeparator(filePath.charAt(len - 1))) {
			// 以分隔符结尾的去掉结尾分隔符
			len--;
		}

		int begin = 0;
		char c;
		for (int i = len - 1; i > -1; i--) {
			c = filePath.charAt(i);
			if (CharUtil.isFileSeparator(c)) {
				// 查找最后一个路径分隔符（/或者\）
				begin = i + 1;
				break;
			}
		}

		return filePath.substring(begin, len);
	}

	/**
	 * 获取文件后缀名，扩展名不带“.”
	 *
	 * @param file 文件
	 * @return 扩展名
	 * @see #extName(File)
	 * @since 5.3.8
	 */
	public static String getSuffix(File file) {
		return extName(file);
	}

	/**
	 * 获得文件后缀名，扩展名不带“.”
	 *
	 * @param fileName 文件名
	 * @return 扩展名
	 * @see #extName(String)
	 * @since 5.3.8
	 */
	public static String getSuffix(String fileName) {
		return extName(fileName);
	}

	/**
	 * 返回主文件名
	 *
	 * @param file 文件
	 * @return 主文件名
	 * @see #mainName(File)
	 * @since 5.3.8
	 */
	public static String getPrefix(File file) {
		return mainName(file);
	}

	/**
	 * 返回主文件名
	 *
	 * @param fileName 完整文件名
	 * @return 主文件名
	 * @see #mainName(String)
	 * @since 5.3.8
	 */
	public static String getPrefix(String fileName) {
		return mainName(fileName);
	}

	/**
	 * 返回主文件名
	 *
	 * @param file 文件
	 * @return 主文件名
	 */
	public static String mainName(File file) {
		if (file.isDirectory()) {
			return file.getName();
		}
		return mainName(file.getName());
	}

	/**
	 * 返回主文件名
	 *
	 * @param fileName 完整文件名
	 * @return 主文件名
	 */
	public static String mainName(String fileName) {
		if (null == fileName) {
			return null;
		}
		int len = fileName.length();
		if (0 == len) {
			return fileName;
		}

		//issue#2642，多级扩展名的主文件名
		for (final CharSequence specialSuffix : SPECIAL_SUFFIX) {
			if(StrUtil.endWith(fileName, "." + specialSuffix)){
				return StrUtil.subPre(fileName, len - specialSuffix.length() - 1);
			}
		}

		if (CharUtil.isFileSeparator(fileName.charAt(len - 1))) {
			len--;
		}

		int begin = 0;
		int end = len;
		char c;
		for (int i = len - 1; i >= 0; i--) {
			c = fileName.charAt(i);
			if (len == end && CharUtil.DOT == c) {
				// 查找最后一个文件名和扩展名的分隔符：.
				end = i;
			}
			// 查找最后一个路径分隔符（/或者\），如果这个分隔符在.之后，则继续查找，否则结束
			if (CharUtil.isFileSeparator(c)) {
				begin = i + 1;
				break;
			}
		}

		return fileName.substring(begin, end);
	}

	/**
	 * 获取文件扩展名（后缀名），扩展名不带“.”
	 *
	 * @param file 文件
	 * @return 扩展名
	 */
	public static String extName(File file) {
		if (null == file) {
			return null;
		}
		if (file.isDirectory()) {
			return null;
		}
		return extName(file.getName());
	}

	/**
	 * 获得文件的扩展名（后缀名），扩展名不带“.”
	 *
	 * @param fileName 文件名
	 * @return 扩展名
	 */
	public static String extName(String fileName) {
		if (fileName == null) {
			return null;
		}
		final int index = fileName.lastIndexOf(StrUtil.DOT);
		if (index == -1) {
			return StrUtil.EMPTY;
		} else {
			// issue#I4W5FS@Gitee
			final int secondToLastIndex = fileName.substring(0, index).lastIndexOf(StrUtil.DOT);
			final String substr = fileName.substring(secondToLastIndex == -1 ? index : secondToLastIndex + 1);
			if (StrUtil.containsAny(substr, SPECIAL_SUFFIX)) {
				return substr;
			}

			final String ext = fileName.substring(index + 1);
			// 扩展名中不能包含路径相关的符号
			return StrUtil.containsAny(ext, UNIX_SEPARATOR, WINDOWS_SEPARATOR) ? StrUtil.EMPTY : ext;
		}
	}

	/**
	 * 清除文件名中的在Windows下不支持的非法字符，包括： \ / : * ? " &lt; &gt; |
	 *
	 * @param fileName 文件名（必须不包括路径，否则路径符将被替换）
	 * @return 清理后的文件名
	 * @since 3.3.1
	 */
	public static String cleanInvalid(String fileName) {
		return StrUtil.isBlank(fileName) ? fileName : ReUtil.delAll(FILE_NAME_INVALID_PATTERN_WIN, fileName);
	}

	/**
	 * 文件名中是否包含在Windows下不支持的非法字符，包括： \ / : * ? " &lt; &gt; |
	 *
	 * @param fileName 文件名（必须不包括路径，否则路径符将被替换）
	 * @return 是否包含非法字符
	 * @since 3.3.1
	 */
	public static boolean containsInvalid(String fileName) {
		return (false == StrUtil.isBlank(fileName)) && ReUtil.contains(FILE_NAME_INVALID_PATTERN_WIN, fileName);
	}

	/**
	 * 根据文件名检查文件类型，忽略大小写
	 *
	 * @param fileName 文件名，例如hutool.png
	 * @param extNames 被检查的扩展名数组，同一文件类型可能有多种扩展名，扩展名不带“.”
	 * @return 是否是指定扩展名的类型
	 * @since 5.5.2
	 */
	public static boolean isType(String fileName, String... extNames) {
		return StrUtil.equalsAnyIgnoreCase(extName(fileName), extNames);
	}
	// -------------------------------------------------------------------------------------------- name end
}
