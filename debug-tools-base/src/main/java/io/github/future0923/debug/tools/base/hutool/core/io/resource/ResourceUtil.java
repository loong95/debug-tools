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
package io.github.future0923.debug.tools.base.hutool.core.io.resource;

import io.github.future0923.debug.tools.base.hutool.core.collection.EnumerationIter;
import io.github.future0923.debug.tools.base.hutool.core.collection.IterUtil;
import io.github.future0923.debug.tools.base.hutool.core.io.FileUtil;
import io.github.future0923.debug.tools.base.hutool.core.io.IORuntimeException;
import io.github.future0923.debug.tools.base.hutool.core.lang.Filter;
import io.github.future0923.debug.tools.base.hutool.core.util.CharsetUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.ClassLoaderUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.ObjUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.URLUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.List;

/**
 * Resource资源工具类
 *
 * @author Looly
 */
public class ResourceUtil {

	/**
	 * 读取Classpath下的资源为字符串，使用UTF-8编码
	 *
	 * @param resource 资源路径，使用相对ClassPath的路径
	 * @return 资源内容
	 * @since 3.1.1
	 */
	public static String readUtf8Str(String resource) {
		return getResourceObj(resource).readUtf8Str();
	}

	/**
	 * 读取Classpath下的资源为字符串
	 *
	 * @param resource 可以是绝对路径，也可以是相对路径（相对ClassPath）
	 * @param charset  编码
	 * @return 资源内容
	 * @since 3.1.1
	 */
	public static String readStr(String resource, Charset charset) {
		return getResourceObj(resource).readStr(charset);
	}

	/**
	 * 读取Classpath下的资源为byte[]
	 *
	 * @param resource 可以是绝对路径，也可以是相对路径（相对ClassPath）
	 * @return 资源内容
	 * @since 4.5.19
	 */
	public static byte[] readBytes(String resource) {
		return getResourceObj(resource).readBytes();
	}

	/**
	 * 从ClassPath资源中获取{@link InputStream}
	 *
	 * @param resource ClassPath资源
	 * @return {@link InputStream}
	 * @throws NoResourceException 资源不存在异常
	 * @since 3.1.2
	 */
	public static InputStream getStream(String resource) throws NoResourceException {
		return getResourceObj(resource).getStream();
	}

	/**
	 * 从ClassPath资源中获取{@link InputStream}，当资源不存在时返回null
	 *
	 * @param resource ClassPath资源
	 * @return {@link InputStream}
	 * @since 4.0.3
	 */
	public static InputStream getStreamSafe(String resource) {
		try {
			return getResourceObj(resource).getStream();
		} catch (NoResourceException e) {
			// ignore
		}
		return null;
	}

	/**
	 * 从ClassPath资源中获取{@link BufferedReader}
	 *
	 * @param resource ClassPath资源
	 * @return {@link InputStream}
	 * @since 5.3.6
	 */
	public static BufferedReader getUtf8Reader(String resource) {
		return getReader(resource, CharsetUtil.CHARSET_UTF_8);
	}

	/**
	 * 从ClassPath资源中获取{@link BufferedReader}
	 *
	 * @param resource ClassPath资源
	 * @param charset  编码
	 * @return {@link InputStream}
	 * @since 3.1.2
	 */
	public static BufferedReader getReader(String resource, Charset charset) {
		return getResourceObj(resource).getReader(charset);
	}

	/**
	 * 获得资源的URL<br>
	 * 路径用/分隔，例如:
	 *
	 * <pre>
	 * config/a/db.config
	 * spring/xml/test.xml
	 * </pre>
	 *
	 * @param resource 资源（相对Classpath的路径）
	 * @return 资源URL
	 */
	public static URL getResource(String resource) throws IORuntimeException {
		return getResource(resource, null);
	}

	/**
	 * 获取指定路径下的资源列表<br>
	 * 路径格式必须为目录格式,用/分隔，例如:
	 *
	 * <pre>
	 * config/a
	 * spring/xml
	 * </pre>
	 *
	 * @param resource 资源路径
	 * @return 资源列表
	 */
	public static List<URL> getResources(String resource) {
		return getResources(resource, null);
	}

	/**
	 * 获取指定路径下的资源列表<br>
	 * 路径格式必须为目录格式,用/分隔，例如:
	 *
	 * <pre>
	 * config/a
	 * spring/xml
	 * </pre>
	 *
	 * @param resource 资源路径
	 * @param filter   过滤器，用于过滤不需要的资源，{@code null}表示不过滤，保留所有元素
	 * @return 资源列表
	 */
	public static List<URL> getResources(String resource, Filter<URL> filter) {
		return IterUtil.filterToList(getResourceIter(resource), filter);
	}

	/**
	 * 获取指定路径下的资源Iterator<br>
	 * 路径格式必须为目录格式,用/分隔，例如:
	 *
	 * <pre>
	 * config/a
	 * spring/xml
	 * </pre>
	 *
	 * @param resource 资源路径
	 * @return 资源列表
	 * @since 4.1.5
	 */
	public static EnumerationIter<URL> getResourceIter(String resource) {
		return getResourceIter(resource, null);
	}

	/**
	 * 获取指定路径下的资源Iterator<br>
	 * 路径格式必须为目录格式,用/分隔，例如:
	 *
	 * <pre>
	 * config/a
	 * spring/xml
	 * </pre>
	 *
	 * @param resource 资源路径
	 * @param classLoader {@link ClassLoader}
	 * @return 资源列表
	 * @since 4.1.5
	 */
	public static EnumerationIter<URL> getResourceIter(String resource, ClassLoader classLoader) {
		final Enumeration<URL> resources;
		try {
			resources = ObjUtil.defaultIfNull(classLoader, ClassLoaderUtil::getClassLoader).getResources(resource);
		} catch (final IOException e) {
			throw new IORuntimeException(e);
		}
		return new EnumerationIter<>(resources);
	}

	/**
	 * 获得资源相对路径对应的URL
	 *
	 * @param resource  资源相对路径，{@code null}和""都表示classpath根路径
	 * @param baseClass 基准Class，获得的相对路径相对于此Class所在路径，如果为{@code null}则相对ClassPath
	 * @return {@link URL}
	 */
	public static URL getResource(String resource, Class<?> baseClass) {
		resource = StrUtil.nullToEmpty(resource);
		return (null != baseClass) ? baseClass.getResource(resource) : ClassLoaderUtil.getClassLoader().getResource(resource);
	}

	/**
	 * 获取{@link Resource} 资源对象<br>
	 * 如果提供路径为绝对路径或路径以file:开头，返回{@link FileResource}，否则返回{@link ClassPathResource}
	 *
	 * @param path 路径，可以是绝对路径，也可以是相对路径（相对ClassPath）
	 * @return {@link Resource} 资源对象
	 * @since 3.2.1
	 */
	public static Resource getResourceObj(String path) {
		if (StrUtil.isNotBlank(path)) {
			if (path.startsWith(URLUtil.FILE_URL_PREFIX) || FileUtil.isAbsolutePath(path)) {
				return new FileResource(path);
			}
		}
		return new ClassPathResource(path);
	}
}
