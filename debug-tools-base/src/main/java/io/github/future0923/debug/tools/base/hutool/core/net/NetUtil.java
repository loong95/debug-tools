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
package io.github.future0923.debug.tools.base.hutool.core.net;

import io.github.future0923.debug.tools.base.hutool.core.collection.CollUtil;
import io.github.future0923.debug.tools.base.hutool.core.collection.EnumerationIter;
import io.github.future0923.debug.tools.base.hutool.core.exceptions.UtilException;
import io.github.future0923.debug.tools.base.hutool.core.io.IORuntimeException;
import io.github.future0923.debug.tools.base.hutool.core.io.IoUtil;
import io.github.future0923.debug.tools.base.hutool.core.lang.Filter;
import io.github.future0923.debug.tools.base.hutool.core.util.JNDIUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.RandomUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Authenticator;
import java.net.DatagramSocket;
import java.net.HttpCookie;
import java.net.IDN;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * 网络相关工具
 *
 * @author looly
 */
public class NetUtil {

	/**
	 * 本地IPv4地址
	 */
	public final static String LOCAL_IP = Ipv4Util.LOCAL_IP;

	/**
	 * 本地主机名称
	 */
	public static String localhostName;

	/**
	 * 默认最小端口，1024
	 */
	public static final int PORT_RANGE_MIN = 1024;
	/**
	 * 默认最大端口，65535
	 */
	public static final int PORT_RANGE_MAX = 0xFFFF;

	/**
	 * 根据long值获取ip v4地址
	 *
	 * @param longIP IP的long表示形式
	 * @return IP V4 地址
	 * @see Ipv4Util#longToIpv4(long)
	 */
	public static String longToIpv4(long longIP) {
		return Ipv4Util.longToIpv4(longIP);
	}

	/**
	 * 根据ip地址计算出long型的数据
	 *
	 * @param strIP IP V4 地址
	 * @return long值
	 * @see Ipv4Util#ipv4ToLong(String)
	 */
	public static long ipv4ToLong(String strIP) {
		return Ipv4Util.ipv4ToLong(strIP);
	}

	/**
	 * 将IPv6地址字符串转为大整数
	 *
	 * @param ipv6Str 字符串
	 * @return 大整数, 如发生异常返回 null
	 * @since 5.5.7
	 * @deprecated 拼写错误，请使用{@link #ipv6ToBigInteger(String)}
	 */
	@Deprecated
	public static BigInteger ipv6ToBitInteger(String ipv6Str) {
		return ipv6ToBigInteger(ipv6Str);
	}

	/**
	 * 将IPv6地址字符串转为大整数
	 *
	 * @param ipv6Str 字符串
	 * @return 大整数, 如发生异常返回 null
	 * @since 5.5.7
	 */
	public static BigInteger ipv6ToBigInteger(String ipv6Str) {
		try {
			InetAddress address = InetAddress.getByName(ipv6Str);
			if (address instanceof Inet6Address) {
				return new BigInteger(1, address.getAddress());
			}
		} catch (UnknownHostException ignore) {
		}
		return null;
	}

	/**
	 * 将大整数转换成ipv6字符串
	 *
	 * @param bigInteger 大整数
	 * @return IPv6字符串, 如发生异常返回 null
	 * @since 5.5.7
	 */
	public static String bigIntegerToIPv6(BigInteger bigInteger) {
		// 确保 BigInteger 在 IPv6 地址范围内
		if (bigInteger.compareTo(BigInteger.ZERO) < 0 || bigInteger.compareTo(new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16)) > 0) {
			throw new IllegalArgumentException("BigInteger value is out of IPv6 range");
		}

		// 将 BigInteger 转换为 16 字节的字节数组
		byte[] bytes = bigInteger.toByteArray();
		if (bytes.length > 16) {
			// 如果字节数组长度大于 16，去掉前导零
			int offset = bytes[0] == 0 ? 1 : 0;
			final byte[] newBytes = new byte[16];
			System.arraycopy(bytes, offset, newBytes, 0, 16);
			bytes = newBytes;
		} else if (bytes.length < 16) {
			// 如果字节数组长度小于 16，前面补零
			byte[] paddedBytes = new byte[16];
			System.arraycopy(bytes, 0, paddedBytes, 16 - bytes.length, bytes.length);
			bytes = paddedBytes;
		}

		// 将字节数组转换为 IPv6 地址字符串
		try {
			return Inet6Address.getByAddress(bytes).getHostAddress();
		} catch (UnknownHostException e) {
			return null;
		}
	}

	/**
	 * 检测本地端口可用性<br>
	 * 来自org.springframework.util.SocketUtils
	 *
	 * @param port 被检测的端口
	 * @return 是否可用
	 */
	public static boolean isUsableLocalPort(int port) {
		if (false == isValidPort(port)) {
			// 给定的IP未在指定端口范围中
			return false;
		}

		// issue#765@Github, 某些绑定非127.0.0.1的端口无法被检测到
		try (ServerSocket ss = new ServerSocket(port)) {
			ss.setReuseAddress(true);
		} catch (IOException ignored) {
			return false;
		}

		try (DatagramSocket ds = new DatagramSocket(port)) {
			ds.setReuseAddress(true);
		} catch (IOException ignored) {
			return false;
		}

		return true;
	}

	/**
	 * 是否为有效的端口<br>
	 * 此方法并不检查端口是否被占用
	 *
	 * @param port 端口号
	 * @return 是否有效
	 */
	public static boolean isValidPort(int port) {
		// 有效端口是0～65535
		return port >= 0 && port <= PORT_RANGE_MAX;
	}

	/**
	 * 查找1024~65535范围内的可用端口<br>
	 * 此方法只检测给定范围内的随机一个端口，检测65535-1024次<br>
	 * 来自org.springframework.util.SocketUtils
	 *
	 * @return 可用的端口
	 * @since 4.5.4
	 */
	public static int getUsableLocalPort() {
		return getUsableLocalPort(PORT_RANGE_MIN);
	}

	/**
	 * 查找指定范围内的可用端口，最大值为65535<br>
	 * 此方法只检测给定范围内的随机一个端口，检测65535-minPort次<br>
	 * 来自org.springframework.util.SocketUtils
	 *
	 * @param minPort 端口最小值（包含）
	 * @return 可用的端口
	 * @since 4.5.4
	 */
	public static int getUsableLocalPort(int minPort) {
		return getUsableLocalPort(minPort, PORT_RANGE_MAX);
	}

	/**
	 * 查找指定范围内的可用端口<br>
	 * 此方法只检测给定范围内的随机一个端口，检测maxPort-minPort次<br>
	 * 来自org.springframework.util.SocketUtils
	 *
	 * @param minPort 端口最小值（包含）
	 * @param maxPort 端口最大值（包含）
	 * @return 可用的端口
	 * @since 4.5.4
	 */
	public static int getUsableLocalPort(int minPort, int maxPort) {
		final int maxPortExclude = maxPort + 1;
		int randomPort;
		for (int i = minPort; i < maxPortExclude; i++) {
			randomPort = RandomUtil.randomInt(minPort, maxPortExclude);
			if (isUsableLocalPort(randomPort)) {
				return randomPort;
			}
		}

		throw new UtilException("Could not find an available port in the range [{}, {}] after {} attempts", minPort, maxPort, maxPort - minPort);
	}

	/**
	 * 获取多个本地可用端口<br>
	 * 来自org.springframework.util.SocketUtils
	 *
	 * @param numRequested 尝试次数
	 * @param minPort      端口最小值（包含）
	 * @param maxPort      端口最大值（包含）
	 * @return 可用的端口
	 * @since 4.5.4
	 */
	public static TreeSet<Integer> getUsableLocalPorts(int numRequested, int minPort, int maxPort) {
		final TreeSet<Integer> availablePorts = new TreeSet<>();
		int attemptCount = 0;
		while ((++attemptCount <= numRequested + 100) && availablePorts.size() < numRequested) {
			availablePorts.add(getUsableLocalPort(minPort, maxPort));
		}

		if (availablePorts.size() != numRequested) {
			throw new UtilException("Could not find {} available  ports in the range [{}, {}]", numRequested, minPort, maxPort);
		}

		return availablePorts;
	}

	/**
	 * 判定是否为内网IPv4<br>
	 * 私有IP：
	 * <pre>
	 * A类 10.0.0.0-10.255.255.255
	 * B类 172.16.0.0-172.31.255.255
	 * C类 192.168.0.0-192.168.255.255
	 * </pre>
	 * 当然，还有127这个网段是环回地址
	 *
	 * @param ipAddress IP地址
	 * @return 是否为内网IP
	 * @see Ipv4Util#isInnerIP(String)
	 */
	public static boolean isInnerIP(String ipAddress) {
		return Ipv4Util.isInnerIP(ipAddress);
	}

	/**
	 * 相对URL转换为绝对URL
	 *
	 * @param absoluteBasePath 基准路径，绝对
	 * @param relativePath     相对路径
	 * @return 绝对URL
	 */
	public static String toAbsoluteUrl(String absoluteBasePath, String relativePath) {
		try {
			URL absoluteUrl = new URL(absoluteBasePath);
			return new URL(absoluteUrl, relativePath).toString();
		} catch (Exception e) {
			throw new UtilException(e, "To absolute url [{}] base [{}] error!", relativePath, absoluteBasePath);
		}
	}

	/**
	 * 隐藏掉IP地址的最后一部分为 * 代替
	 *
	 * @param ip IP地址
	 * @return 隐藏部分后的IP
	 */
	public static String hideIpPart(String ip) {
		return StrUtil.builder(ip.length()).append(ip, 0, ip.lastIndexOf(".") + 1).append("*").toString();
	}

	/**
	 * 隐藏掉IP地址的最后一部分为 * 代替
	 *
	 * @param ip IP地址
	 * @return 隐藏部分后的IP
	 */
	public static String hideIpPart(long ip) {
		return hideIpPart(longToIpv4(ip));
	}

	/**
	 * 构建InetSocketAddress<br>
	 * 当host中包含端口时（用“：”隔开），使用host中的端口，否则使用默认端口<br>
	 * 给定host为空时使用本地host（127.0.0.1）
	 *
	 * @param host        Host
	 * @param defaultPort 默认端口
	 * @return InetSocketAddress
	 */
	public static InetSocketAddress buildInetSocketAddress(String host, int defaultPort) {
		if (StrUtil.isBlank(host)) {
			host = LOCAL_IP;
		}

		String destHost;
		int port;
		int index = host.indexOf(":");
		if (index != -1) {
			// host:port形式
			destHost = host.substring(0, index);
			port = Integer.parseInt(host.substring(index + 1));
		} else {
			destHost = host;
			port = defaultPort;
		}

		return new InetSocketAddress(destHost, port);
	}

	/**
	 * 通过域名得到IP
	 *
	 * @param hostName HOST
	 * @return ip address or hostName if UnknownHostException
	 */
	public static String getIpByHost(String hostName) {
		try {
			return InetAddress.getByName(hostName).getHostAddress();
		} catch (UnknownHostException e) {
			return hostName;
		}
	}

	/**
	 * 获取指定名称的网卡信息
	 *
	 * @param name 网络接口名，例如Linux下默认是eth0
	 * @return 网卡，未找到返回{@code null}
	 * @since 5.0.7
	 */
	public static NetworkInterface getNetworkInterface(String name) {
		Enumeration<NetworkInterface> networkInterfaces;
		try {
			networkInterfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			return null;
		}

		NetworkInterface netInterface;
		while (networkInterfaces.hasMoreElements()) {
			netInterface = networkInterfaces.nextElement();
			if (null != netInterface && name.equals(netInterface.getName())) {
				return netInterface;
			}
		}

		return null;
	}

	/**
	 * 获取本机所有网卡
	 *
	 * @return 所有网卡，异常返回{@code null}
	 * @since 3.0.1
	 */
	public static Collection<NetworkInterface> getNetworkInterfaces() {
		Enumeration<NetworkInterface> networkInterfaces;
		try {
			networkInterfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			return null;
		}

		return CollUtil.addAll(new ArrayList<>(), networkInterfaces);
	}

	/**
	 * 获得本机的IPv4地址列表<br>
	 * 返回的IP列表有序，按照系统设备顺序
	 *
	 * @return IP地址列表 {@link LinkedHashSet}
	 */
	public static LinkedHashSet<String> localIpv4s() {
		final LinkedHashSet<InetAddress> localAddressList = localAddressList(t -> t instanceof Inet4Address);

		return toIpList(localAddressList);
	}

	/**
	 * 获得本机的IPv6地址列表<br>
	 * 返回的IP列表有序，按照系统设备顺序
	 *
	 * @return IP地址列表 {@link LinkedHashSet}
	 * @since 4.5.17
	 */
	public static LinkedHashSet<String> localIpv6s() {
		final LinkedHashSet<InetAddress> localAddressList = localAddressList(t -> t instanceof Inet6Address);

		return toIpList(localAddressList);
	}

	/**
	 * 地址列表转换为IP地址列表
	 *
	 * @param addressList 地址{@link Inet4Address} 列表
	 * @return IP地址字符串列表
	 * @since 4.5.17
	 */
	public static LinkedHashSet<String> toIpList(Set<InetAddress> addressList) {
		final LinkedHashSet<String> ipSet = new LinkedHashSet<>();
		for (InetAddress address : addressList) {
			ipSet.add(address.getHostAddress());
		}

		return ipSet;
	}

	/**
	 * 获得本机的IP地址列表（包括Ipv4和Ipv6）<br>
	 * 返回的IP列表有序，按照系统设备顺序
	 *
	 * @return IP地址列表 {@link LinkedHashSet}
	 */
	public static LinkedHashSet<String> localIps() {
		final LinkedHashSet<InetAddress> localAddressList = localAddressList(null);
		return toIpList(localAddressList);
	}

	/**
	 * 获取所有满足过滤条件的本地IP地址对象
	 *
	 * @param addressFilter 过滤器，null表示不过滤，获取所有地址
	 * @return 过滤后的地址对象列表
	 * @since 4.5.17
	 */
	public static LinkedHashSet<InetAddress> localAddressList(Filter<InetAddress> addressFilter) {
		return localAddressList(null, addressFilter);
	}

	/**
	 * 获取所有满足过滤条件的本地IP地址对象
	 *
	 * @param addressFilter          过滤器，null表示不过滤，获取所有地址
	 * @param networkInterfaceFilter 过滤器，null表示不过滤，获取所有网卡
	 * @return 过滤后的地址对象列表
	 */
	public static LinkedHashSet<InetAddress> localAddressList(Filter<NetworkInterface> networkInterfaceFilter, Filter<InetAddress> addressFilter) {
		Enumeration<NetworkInterface> networkInterfaces;
		try {
			networkInterfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			throw new UtilException(e);
		}

		if (networkInterfaces == null) {
			throw new UtilException("Get network interface error!");
		}

		final LinkedHashSet<InetAddress> ipSet = new LinkedHashSet<>();

		while (networkInterfaces.hasMoreElements()) {
			final NetworkInterface networkInterface = networkInterfaces.nextElement();
			if (networkInterfaceFilter != null && false == networkInterfaceFilter.accept(networkInterface)) {
				continue;
			}
			final Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
			while (inetAddresses.hasMoreElements()) {
				final InetAddress inetAddress = inetAddresses.nextElement();
				if (inetAddress != null && (null == addressFilter || addressFilter.accept(inetAddress))) {
					ipSet.add(inetAddress);
				}
			}
		}

		return ipSet;
	}

	/**
	 * 获取本机网卡IP地址，这个地址为所有网卡中非回路地址的第一个<br>
	 * 如果获取失败调用 {@link InetAddress#getLocalHost()}方法获取。<br>
	 * 此方法不会抛出异常，获取失败将返回{@code null}<br>
	 * <p>
	 * 参考：http://stackoverflow.com/questions/9481865/getting-the-ip-address-of-the-current-machine-using-java
	 *
	 * @return 本机网卡IP地址，获取失败返回{@code null}
	 * @since 3.0.7
	 */
	public static String getLocalhostStr() {
		InetAddress localhost = getLocalhost();
		if (null != localhost) {
			return localhost.getHostAddress();
		}
		return null;
	}

	/**
	 * 获取本机网卡IP地址，规则如下：
	 *
	 * <pre>
	 * 1. 查找所有网卡地址，必须非回路（loopback）地址、非局域网地址（siteLocal）、IPv4地址
	 * 2. 如果无满足要求的地址，调用 {@link InetAddress#getLocalHost()} 获取地址
	 * </pre>
	 * <p>
	 * 此方法不会抛出异常，获取失败将返回{@code null}<br>
	 * <p>
	 * 见：https://github.com/chinabugotech/hutool/issues/428
	 *
	 * @return 本机网卡IP地址，获取失败返回{@code null}
	 * @since 3.0.1
	 */
	public static InetAddress getLocalhost() {
		final LinkedHashSet<InetAddress> localAddressList = localAddressList(address -> {
			// 非loopback地址，指127.*.*.*的地址
			return false == address.isLoopbackAddress()
				// 需为IPV4地址
				&& address instanceof Inet4Address;
		});

		if (CollUtil.isNotEmpty(localAddressList)) {
			InetAddress address2 = null;
			for (InetAddress inetAddress : localAddressList) {
				if (false == inetAddress.isSiteLocalAddress()) {
					// 非地区本地地址，指10.0.0.0 ~ 10.255.255.255、172.16.0.0 ~ 172.31.255.255、192.168.0.0 ~ 192.168.255.255
					return inetAddress;
				} else if (null == address2) {
					address2 = inetAddress;
				}
			}

			if (null != address2) {
				return address2;
			}
		}

		try {
			return InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// ignore
		}

		return null;
	}

	/**
	 * 获得本机MAC地址
	 *
	 * @return 本机MAC地址
	 */
	public static String getLocalMacAddress() {
		return getMacAddress(getLocalhost());
	}

	/**
	 * 获得指定地址信息中的MAC地址，使用分隔符“-”
	 *
	 * @param inetAddress {@link InetAddress}
	 * @return MAC地址，用-分隔
	 */
	public static String getMacAddress(InetAddress inetAddress) {
		return getMacAddress(inetAddress, "-");
	}

	/**
	 * 获得指定地址信息中的MAC地址
	 *
	 * @param inetAddress {@link InetAddress}
	 * @param separator   分隔符，推荐使用“-”或者“:”
	 * @return MAC地址，用-分隔
	 */
	public static String getMacAddress(InetAddress inetAddress, String separator) {
		if (null == inetAddress) {
			return null;
		}

		final byte[] mac = getHardwareAddress(inetAddress);
		if (null != mac) {
			final StringBuilder sb = new StringBuilder();
			String s;
			for (int i = 0; i < mac.length; i++) {
				if (i != 0) {
					sb.append(separator);
				}
				// 字节转换为整数
				s = Integer.toHexString(mac[i] & 0xFF);
				sb.append(s.length() == 1 ? 0 + s : s);
			}
			return sb.toString();
		}

		return null;
	}

	/**
	 * 获得指定地址信息中的硬件地址
	 *
	 * @param inetAddress {@link InetAddress}
	 * @return 硬件地址
	 * @since 5.7.3
	 */
	public static byte[] getHardwareAddress(InetAddress inetAddress) {
		if (null == inetAddress) {
			return null;
		}

		try {
			final NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inetAddress);
			if (null != networkInterface) {
				return networkInterface.getHardwareAddress();
			}
		} catch (SocketException e) {
			throw new UtilException(e);
		}
		return null;
	}

	/**
	 * 获得本机物理地址
	 *
	 * @return 本机物理地址
	 * @since 5.7.3
	 */
	public static byte[] getLocalHardwareAddress() {
		return getHardwareAddress(getLocalhost());
	}

	/**
	 * 获取主机名称，一次获取会缓存名称<br>
	 * 注意此方法会触发反向DNS解析，导致阻塞，阻塞时间取决于网络！
	 *
	 * @return 主机名称
	 * @since 5.4.4
	 */
	public static String getLocalHostName() {
		if (StrUtil.isNotBlank(localhostName)) {
			return localhostName;
		}

		final InetAddress localhost = getLocalhost();

		if (null != localhost) {
			String name = localhost.getHostName();
			if (StrUtil.isEmpty(name)) {
				name = localhost.getHostAddress();
			}
			localhostName = name;
		}

		return localhostName;
	}

	/**
	 * 创建 {@link InetSocketAddress}
	 *
	 * @param host 域名或IP地址，空表示任意地址
	 * @param port 端口，0表示系统分配临时端口
	 * @return {@link InetSocketAddress}
	 * @since 3.3.0
	 */
	public static InetSocketAddress createAddress(String host, int port) {
		if (StrUtil.isBlank(host)) {
			return new InetSocketAddress(port);
		}
		return new InetSocketAddress(host, port);
	}

	/**
	 * 简易的使用Socket发送数据
	 *
	 * @param host    Server主机
	 * @param port    Server端口
	 * @param isBlock 是否阻塞方式
	 * @param data    需要发送的数据
	 * @throws IORuntimeException IO异常
	 * @since 3.3.0
	 */
	public static void netCat(String host, int port, boolean isBlock, ByteBuffer data) throws IORuntimeException {
		try (SocketChannel channel = SocketChannel.open(createAddress(host, port))) {
			channel.configureBlocking(isBlock);
			channel.write(data);
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}

	/**
	 * 使用普通Socket发送数据
	 *
	 * @param host Server主机
	 * @param port Server端口
	 * @param data 数据
	 * @throws IORuntimeException IO异常
	 * @since 3.3.0
	 */
	public static void netCat(String host, int port, byte[] data) throws IORuntimeException {
		OutputStream out = null;
		try (Socket socket = new Socket(host, port)) {
			out = socket.getOutputStream();
			out.write(data);
			out.flush();
		} catch (IOException e) {
			throw new IORuntimeException(e);
		} finally {
			IoUtil.close(out);
		}
	}

	/**
	 * 是否在CIDR规则配置范围内<br>
	 * 方法来自：【成都】小邓
	 *
	 * @param ip   需要验证的IP
	 * @param cidr CIDR规则
	 * @return 是否在范围内
	 * @since 4.0.6
	 */
	public static boolean isInRange(String ip, String cidr) {
		final int maskSplitMarkIndex = cidr.lastIndexOf(Ipv4Util.IP_MASK_SPLIT_MARK);
		if (maskSplitMarkIndex < 0) {
			throw new IllegalArgumentException("Invalid cidr: " + cidr);
		}

		final long mask = (-1L << 32 - Integer.parseInt(cidr.substring(maskSplitMarkIndex + 1)));
		long cidrIpAddr = ipv4ToLong(cidr.substring(0, maskSplitMarkIndex));

		return (ipv4ToLong(ip) & mask) == (cidrIpAddr & mask);
	}

	/**
	 * Unicode域名转puny code
	 *
	 * @param unicode Unicode域名
	 * @return puny code
	 * @since 4.1.22
	 */
	public static String idnToASCII(String unicode) {
		return IDN.toASCII(unicode);
	}

	/**
	 * 从多级反向代理中获得第一个非unknown IP地址
	 *
	 * @param ip 获得的IP地址
	 * @return 第一个非unknown IP地址
	 * @since 4.4.1
	 */
	public static String getMultistageReverseProxyIp(String ip) {
		// 多级反向代理检测
		if (ip != null && StrUtil.indexOf(ip, ',') > 0) {
			final List<String> ips = StrUtil.splitTrim(ip, ',');
			for (final String subIp : ips) {
				if (false == isUnknown(subIp)) {
					ip = subIp;
					break;
				}
			}
		}
		return ip;
	}

	/**
	 * 检测给定字符串是否为未知，多用于检测HTTP请求相关<br>
	 *
	 * @param checkString 被检测的字符串
	 * @return 是否未知
	 * @since 5.2.6
	 */
	public static boolean isUnknown(String checkString) {
		return StrUtil.isBlank(checkString) || "unknown".equalsIgnoreCase(checkString);
	}

	/**
	 * 检测IP地址是否能ping通
	 *
	 * @param ip IP地址
	 * @return 返回是否ping通
	 */
	public static boolean ping(String ip) {
		return ping(ip, 200);
	}

	/**
	 * 检测IP地址是否能ping通
	 *
	 * @param ip      IP地址
	 * @param timeout 检测超时（毫秒）
	 * @return 是否ping通
	 */
	public static boolean ping(String ip, int timeout) {
		try {
			return InetAddress.getByName(ip).isReachable(timeout); // 当返回值是true时，说明host是可用的，false则不可。
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * 解析Cookie信息
	 *
	 * @param cookieStr Cookie字符串
	 * @return cookie字符串
	 * @since 5.2.6
	 */
	public static List<HttpCookie> parseCookies(String cookieStr) {
		if (StrUtil.isBlank(cookieStr)) {
			return Collections.emptyList();
		}
		return HttpCookie.parse(cookieStr);
	}

	/**
	 * 检查远程端口是否开启
	 *
	 * @param address 远程地址
	 * @param timeout 检测超时
	 * @return 远程端口是否开启
	 * @since 5.3.2
	 */
	public static boolean isOpen(InetSocketAddress address, int timeout) {
		try (Socket sc = new Socket()) {
			sc.connect(address, timeout);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 设置全局验证
	 *
	 * @param user 用户名
	 * @param pass 密码，考虑安全，此处不使用String
	 * @since 5.7.2
	 */
	public static void setGlobalAuthenticator(String user, char[] pass) {
		setGlobalAuthenticator(new UserPassAuthenticator(user, pass));
	}

	/**
	 * 设置全局验证
	 *
	 * @param authenticator 验证器
	 * @since 5.7.2
	 */
	public static void setGlobalAuthenticator(Authenticator authenticator) {
		Authenticator.setDefault(authenticator);
	}

	/**
	 * 获取DNS信息，如TXT信息：
	 *
	 * <pre class="code">
	 *     NetUtil.attrNames("hutool.cn", "TXT")
	 * </pre>
	 *
	 * @param hostName  主机域名
	 * @param attrNames 属性
	 * @return DNS信息
	 * @since 5.7.7
	 */
	public static List<String> getDnsInfo(String hostName, String... attrNames) {
		final String uri = StrUtil.addPrefixIfNot(hostName, "dns:");
		final Attributes attributes = JNDIUtil.getAttributes(uri, attrNames);

		final List<String> infos = new ArrayList<>();
		for (Attribute attribute : new EnumerationIter<>(attributes.getAll())) {
			try {
				infos.add((String) attribute.get());
			} catch (NamingException ignore) {
				//ignore
			}
		}
		return infos;
	}

	// ----------------------------------------------------------------------------------------- Private method start

	// ----------------------------------------------------------------------------------------- Private method end
}
