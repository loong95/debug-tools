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
package io.github.future0923.debug.tools.base.hutool.core.img;

import io.github.future0923.debug.tools.base.hutool.core.util.ObjectUtil;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * {@link Graphics}相关工具类
 *
 * @author looly
 * @since 4.5.2
 */
public class GraphicsUtil {

	/**
	 * 创建{@link Graphics2D}
	 *
	 * @param image {@link BufferedImage}
	 * @param color {@link Color}背景颜色以及当前画笔颜色，{@code null}表示不设置背景色
	 * @return {@link Graphics2D}
	 * @since 4.5.2
	 */
	public static Graphics2D createGraphics(BufferedImage image, Color color) {
		final Graphics2D g = image.createGraphics();

		if (null != color) {
			// 填充背景
			g.setColor(color);
			g.fillRect(0, 0, image.getWidth(), image.getHeight());
		}

		return g;
	}

	/**
	 * 获取文字居中高度的Y坐标（距离上边距距离）<br>
	 * 此方法依赖FontMetrics，如果获取失败，默认为背景高度的1/3
	 *
	 * @param g                {@link Graphics2D}画笔
	 * @param backgroundHeight 背景高度
	 * @return 最小高度，-1表示无法获取
	 * @since 4.5.17
	 */
	public static int getCenterY(Graphics g, int backgroundHeight) {
		// 获取允许文字最小高度
		FontMetrics metrics = null;
		try {
			metrics = g.getFontMetrics();
		} catch (Exception e) {
			// 此处报告bug某些情况下会抛出IndexOutOfBoundsException，在此做容错处理
		}
		int y;
		if (null != metrics) {
			y = (backgroundHeight - metrics.getHeight()) / 2 + metrics.getAscent();
		} else {
			y = backgroundHeight / 3;
		}
		return y;
	}

	/**
	 * 绘制字符串，使用随机颜色，默认抗锯齿
	 *
	 * @param g      {@link Graphics}画笔
	 * @param str    字符串
	 * @param font   字体
	 * @param width  字符串总宽度
	 * @param height 字符串背景高度
	 * @return 画笔对象
	 * @since 4.5.10
	 */
	public static Graphics drawStringColourful(Graphics g, String str, Font font, int width, int height) {
		return drawString(g, str, font, null, width, height, null, 0);
	}

	/**
	 * 绘制字符串，使用随机颜色，默认抗锯齿
	 *
	 * @param g      {@link Graphics}画笔
	 * @param str    字符串
	 * @param font   字体
	 * @param width  字符串总宽度
	 * @param height 字符串背景高度
	 * @param compareColor 用于比对的颜色
	 * @param minColorDistance 随机生成的颜色与对比颜色的最小色差，小于此值则重新生成颜色
	 * @return 画笔对象
	 * @since 5.8.30
	 */
	public static Graphics drawStringColourful(Graphics g, String str, Font font, int width, int height, Color compareColor, int minColorDistance) {
		return drawString(g, str, font, null, width, height, compareColor, minColorDistance);
	}

	/**
	 * 绘制字符串，使用随机颜色，并且与背景颜色保持一定色差，默认抗锯齿
	 *
	 * @param g      {@link Graphics}画笔
	 * @param str    字符串
	 * @param font   字体
	 * @param width  字符串总宽度
	 * @param height 字符串背景高度
	 * @param backgroundColor 背景颜色
	 * @return 画笔对象
	 * @since 4.5.10
	 */
	public static Graphics drawStringColourful(Graphics g, String str, Font font, int width, int height, Color backgroundColor) {
		// 默认色差为最大色差的1/2
		return drawString(g, str, font, null, width, height, backgroundColor, ColorUtil.maxDistance(backgroundColor) / 2);
	}

	/**
	 * 绘制字符串，默认抗锯齿
	 *
	 * @param g      {@link Graphics}画笔
	 * @param str    字符串
	 * @param font   字体
	 * @param color  字体颜色，{@code null} 表示使用随机颜色（每个字符单独随机）
	 * @param width  字符串背景的宽度
	 * @param height 字符串背景的高度
	 * @return 画笔对象
	 * @since 4.5.10
	 */
	public static Graphics drawString(Graphics g, String str, Font font, Color color, int width, int height) {
		return drawString(g, str, font, color, width, height, null, 0);
	}

	/**
	 * 绘制字符串，默认抗锯齿
	 *
	 * @param g      {@link Graphics}画笔
	 * @param str    字符串
	 * @param font   字体
	 * @param color  字体颜色，{@code null} 表示使用随机颜色（每个字符单独随机）
	 * @param width  字符串背景的宽度
	 * @param height 字符串背景的高度
	 * @param compareColor 用于比对的颜色
	 * @param minColorDistance 随机生成的颜色与对比颜色的最小色差，小于此值则重新生成颜色
	 * @return 画笔对象
	 * @since 5.8.30
	 */
	public static Graphics drawString(Graphics g, String str, Font font, Color color, int width, int height, Color compareColor, int minColorDistance) {
		// 抗锯齿
		if (g instanceof Graphics2D) {
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		// 创建字体
		g.setFont(font);

		// 文字高度（必须在设置字体后调用）
		int midY = getCenterY(g, height);
		if (null != color) {
			g.setColor(color);
		}

		final int len = str.length();
		int charWidth = width / len;
		for (int i = 0; i < len; i++) {
			if (null == color) {
				// 产生随机的颜色值，让输出的每个字符的颜色值都将不同。
				if (null != compareColor && minColorDistance > 0) {
					g.setColor(ImgUtil.randomColor(compareColor,minColorDistance));
				}else {
					g.setColor(ImgUtil.randomColor());
				}
			}
			g.drawString(String.valueOf(str.charAt(i)), i * charWidth, midY);
		}
		return g;
	}

	/**
	 * 绘制字符串，默认抗锯齿。<br>
	 * 此方法定义一个矩形区域和坐标，文字基于这个区域中间偏移x,y绘制。
	 *
	 * @param g         {@link Graphics}画笔
	 * @param str       字符串
	 * @param font      字体，字体大小决定了在背景中绘制的大小
	 * @param color     字体颜色，{@code null} 表示使用黑色
	 * @param rectangle 字符串绘制坐标和大小，此对象定义了绘制字符串的区域大小和偏移位置
	 * @return 画笔对象
	 * @since 4.5.10
	 */
	public static Graphics drawString(Graphics g, String str, Font font, Color color, Rectangle rectangle) {
		// 背景长宽
		final int backgroundWidth = rectangle.width;
		final int backgroundHeight = rectangle.height;

		//获取字符串本身的长宽
		Dimension dimension;
		try {
			dimension = FontUtil.getDimension(g.getFontMetrics(font), str);
		} catch (Exception e) {
			// 此处报告bug某些情况下会抛出IndexOutOfBoundsException，在此做容错处理
			dimension = new Dimension(backgroundWidth / 3, backgroundHeight / 3);
		}

		rectangle.setSize(dimension.width, dimension.height);
		final Point point = ImgUtil.getPointBaseCentre(rectangle, backgroundWidth, backgroundHeight);

		return drawString(g, str, font, color, point);
	}

	/**
	 * 绘制字符串，默认抗锯齿
	 *
	 * @param g     {@link Graphics}画笔
	 * @param str   字符串
	 * @param font  字体，字体大小决定了在背景中绘制的大小
	 * @param color 字体颜色，{@code null} 表示使用黑色
	 * @param point 绘制字符串的位置坐标
	 * @return 画笔对象
	 * @since 5.3.6
	 */
	public static Graphics drawString(Graphics g, String str, Font font, Color color, Point point) {
		// 抗锯齿
		if (g instanceof Graphics2D) {
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}

		g.setFont(font);
		g.setColor(ObjectUtil.defaultIfNull(color, Color.BLACK));
		g.drawString(str, point.x, point.y);

		return g;
	}

	/**
	 * 绘制图片
	 *
	 * @param g     画笔
	 * @param img   要绘制的图片
	 * @param point 绘制的位置，基于左上角
	 * @return 画笔对象
	 */
	public static Graphics drawImg(Graphics g, Image img, Point point) {
		return drawImg(g, img,
				new Rectangle(point.x, point.y, img.getWidth(null), img.getHeight(null)));
	}

	/**
	 * 绘制图片
	 *
	 * @param g         画笔
	 * @param img       要绘制的图片
	 * @param rectangle 矩形对象，表示矩形区域的x，y，width，height,，基于左上角
	 * @return 画笔对象
	 */
	public static Graphics drawImg(Graphics g, Image img, Rectangle rectangle) {
		g.drawImage(img, rectangle.x, rectangle.y, rectangle.width, rectangle.height, null); // 绘制切割后的图
		return g;
	}

	/**
	 * 设置画笔透明度
	 *
	 * @param g     画笔
	 * @param alpha 透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
	 * @return 画笔
	 */
	public static Graphics2D setAlpha(Graphics2D g, float alpha) {
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
		return g;
	}
}
