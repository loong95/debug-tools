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

import io.github.future0923.debug.tools.base.hutool.core.lang.Assert;
import io.github.future0923.debug.tools.base.hutool.core.util.ClassLoaderUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.ReflectUtil;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;

/**
 * VFS资源封装<br>
 * 支持VFS 3.x on JBoss AS 6+，JBoss AS 7 and WildFly 8+<br>
 * 参考：org.springframework.core.io.VfsUtils
 *
 * @author looly, Spring
 * @since 5.7.21
 */
public class VfsResource implements Resource {
	private static final String VFS3_PKG = "org.jboss.vfs.";

	private static final Method VIRTUAL_FILE_METHOD_EXISTS;
	private static final Method VIRTUAL_FILE_METHOD_GET_INPUT_STREAM;
	private static final Method VIRTUAL_FILE_METHOD_GET_SIZE;
	private static final Method VIRTUAL_FILE_METHOD_GET_LAST_MODIFIED;
	private static final Method VIRTUAL_FILE_METHOD_TO_URL;
	private static final Method VIRTUAL_FILE_METHOD_GET_NAME;

	static {
		Class<?> virtualFile = ClassLoaderUtil.loadClass(VFS3_PKG + "VirtualFile");
		try {
			VIRTUAL_FILE_METHOD_EXISTS = virtualFile.getMethod("exists");
			VIRTUAL_FILE_METHOD_GET_INPUT_STREAM = virtualFile.getMethod("openStream");
			VIRTUAL_FILE_METHOD_GET_SIZE = virtualFile.getMethod("getSize");
			VIRTUAL_FILE_METHOD_GET_LAST_MODIFIED = virtualFile.getMethod("getLastModified");
			VIRTUAL_FILE_METHOD_TO_URL = virtualFile.getMethod("toURL");
			VIRTUAL_FILE_METHOD_GET_NAME = virtualFile.getMethod("getName");
		} catch (NoSuchMethodException ex) {
			throw new IllegalStateException("Could not detect JBoss VFS infrastructure", ex);
		}
	}

	/**
	 * org.jboss.vfs.VirtualFile实例对象
	 */
	private final Object virtualFile;
	private final long lastModified;

	/**
	 * 构造
	 *
	 * @param resource org.jboss.vfs.VirtualFile实例对象
	 */
	public VfsResource(Object resource) {
		Assert.notNull(resource, "VirtualFile must not be null");
		this.virtualFile = resource;
		this.lastModified = getLastModified();
	}

	/**
	 * VFS文件是否存在
	 *
	 * @return 文件是否存在
	 */
	public boolean exists() {
		return ReflectUtil.invoke(virtualFile, VIRTUAL_FILE_METHOD_EXISTS);
	}

	@Override
	public String getName() {
		return ReflectUtil.invoke(virtualFile, VIRTUAL_FILE_METHOD_GET_NAME);
	}

	@Override
	public URL getUrl() {
		return ReflectUtil.invoke(virtualFile, VIRTUAL_FILE_METHOD_TO_URL);
	}

	@Override
	public InputStream getStream() {
		return ReflectUtil.invoke(virtualFile, VIRTUAL_FILE_METHOD_GET_INPUT_STREAM);
	}

	@Override
	public boolean isModified() {
		return this.lastModified != getLastModified();
	}

	/**
	 * 获得VFS文件最后修改时间
	 *
	 * @return 最后修改时间
	 */
	public long getLastModified() {
		return ReflectUtil.invoke(virtualFile, VIRTUAL_FILE_METHOD_GET_LAST_MODIFIED);
	}

	/**
	 * 获取VFS文件大小
	 *
	 * @return VFS文件大小
	 */
	public long size() {
		return ReflectUtil.invoke(virtualFile, VIRTUAL_FILE_METHOD_GET_SIZE);
	}

}
