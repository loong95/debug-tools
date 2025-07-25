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
package io.github.future0923.debug.tools.base.hutool.core.bean;

import io.github.future0923.debug.tools.base.hutool.core.lang.func.Func0;
import io.github.future0923.debug.tools.base.hutool.core.map.WeakConcurrentMap;

/**
 * Bean属性缓存<br>
 * 缓存用于防止多次反射造成的性能问题
 *
 * @author Looly
 */
public enum BeanDescCache {
	INSTANCE;

	private final WeakConcurrentMap<Class<?>, BeanDesc> bdCache = new WeakConcurrentMap<>();

	/**
	 * 获得属性名和{@link BeanDesc}Map映射
	 *
	 * @param beanClass Bean的类
	 * @param supplier  对象不存在时创建对象的函数
	 * @return 属性名和{@link BeanDesc}映射
	 * @since 5.4.2
	 */
	public BeanDesc getBeanDesc(Class<?> beanClass, Func0<BeanDesc> supplier) {
		return bdCache.computeIfAbsent(beanClass, (key)->supplier.callWithRuntimeException());
	}

	/**
	 * 清空全局的Bean属性缓存
	 *
	 * @since 5.7.21
	 */
	public void clear() {
		this.bdCache.clear();
	}
}
