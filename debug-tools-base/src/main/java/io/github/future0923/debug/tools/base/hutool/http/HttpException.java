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
package io.github.future0923.debug.tools.base.hutool.http;

import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;

/**
 * HTTP异常
 *
 * @author xiaoleilu
 */
public class HttpException extends RuntimeException {
	private static final long serialVersionUID = 8247610319171014183L;

	public HttpException(Throwable e) {
		super(e.getMessage(), e);
	}

	public HttpException(String message) {
		super(message);
	}

	public HttpException(String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params));
	}

	public HttpException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public HttpException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
		super(message, throwable, enableSuppression, writableStackTrace);
	}

	public HttpException(Throwable throwable, String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params), throwable);
	}
}
