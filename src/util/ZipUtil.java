/**
 * Copyright(c) Guangzhou JiaxinCloud Science & Technology Ltd. 
 */
package util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

/**
 * <pre>
 * 解压缩工具类。
 * </pre>
 * 
 * @author 王文辉 wangwenhui@jiaxincloud.com
 * @version 1.00.00
 * @date 2017年8月24日
 * 
 *       <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 *       </pre>
 */
public final class ZipUtil {
	/**
	 * 将filePaths 文件集合打包到zipPath 这个zip文件中
	 * 
	 * @param zipPath
	 *            压缩后的文件路径
	 * @param filePaths
	 *            需要压缩的文件路径列表
	 * 
	 */
	public static void zip(String zipPath, String originFolder) {
		File f = new File(originFolder);
		if (!f.exists() || !f.isDirectory()) {
			return;
		}
		File[] fileList = f.listFiles();
		String[] filePaths = new String[6];
		int index = 0;
		for (File fl : fileList) {
			if (fl.getName().endsWith("xmind")) {
				continue;
			}
			filePaths[index] = fl.getPath();
			index++;
		}

		File target = new File(zipPath);
		// 压缩文件名=源文件名.zip
		if (target.exists()) {
			target.delete(); // 删除旧的文件
		}
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		try {
			fos = new FileOutputStream(target);
			zos = new ZipOutputStream(new BufferedOutputStream(fos));
			// 添加对应的文件Entry
			addEntry(filePaths, zos);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			IOUtil.closeQuietly(zos, fos);
		}
	}

	/**
	 * 扫描添加文件Entry
	 * 
	 * @param base
	 *            基路径
	 * 
	 * @param source
	 *            源文件
	 * @param zos
	 *            Zip文件输出流
	 * @throws IOException
	 */
	private static void addEntry(String[] filePaths, ZipOutputStream zos) throws IOException {
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		File tempFile = null;
		try {

			for (String filePath : filePaths) {
				if (filePath == null) {
					continue;
				}
				tempFile = new File(filePath);
				if (tempFile.isDirectory()) {
					ZIPDIR(tempFile.getPath(), zos, tempFile.getName() + File.separator);
				} else {
					fis = new FileInputStream(tempFile);
					byte[] buffer = new byte[1024 * 10];
					bis = new BufferedInputStream(fis, buffer.length);
					int read = 0;
					zos.putNextEntry(new ZipEntry(tempFile.getName()));
					while ((read = bis.read(buffer, 0, buffer.length)) != -1) {
						zos.write(buffer, 0, read);
					}
				}
				zos.closeEntry();
			}
		} finally {
			IOUtil.closeQuietly(bis, fis);
		}
	}

	/**
	 * 压缩目录。除非有特殊需要，否则请调用ZIP方法来压缩文件！
	 * 
	 * @param sourceDir
	 *            需要压缩的目录位置
	 * @param zos
	 *            压缩到的zip文件
	 * @param tager
	 *            压缩到的目标位置
	 * @throws IOException
	 *             压缩文件的过程中可能会抛出IO异常，请自行处理该异常。
	 */
	public static void ZIPDIR(String sourceDir, ZipOutputStream zos, String tager) throws IOException {
		// System.out.println(tager);
		ZipEntry ze = new ZipEntry(tager);
		zos.putNextEntry(ze);
		// 提取要压缩的文件夹中的所有文件
		File f = new File(sourceDir);
		File[] flist = f.listFiles();
		if (flist != null) {
			// 如果该文件夹下有文件则提取所有的文件进行压缩
			for (File fsub : flist) {
				if (fsub.isDirectory()) {
					// 如果是目录则进行目录压缩
					ZIPDIR(fsub.getPath(), zos, tager + fsub.getName() + "/");
				} else {
					// 如果是文件，则进行文件压缩
					ZIPFile(fsub.getPath(), zos, tager + fsub.getName());
				}
			}
		}
	}

	public static void ZIPFile(String sourceFileName, ZipOutputStream zos, String tager) throws IOException {
		// System.out.println(tager);
		ZipEntry ze = new ZipEntry(tager);
		zos.putNextEntry(ze);

		// 读取要压缩文件并将其添加到压缩文件中
		FileInputStream fis = new FileInputStream(new File(sourceFileName));
		byte[] bf = new byte[2048];
		int location = 0;
		while ((location = fis.read(bf)) != -1) {
			zos.write(bf, 0, location);
		}
		fis.close();
	}

	/**
	 * 解压文件 -- 这个fun没有按照咱们的需要定制
	 * 
	 * @param filePath
	 *            压缩文件路径
	 * @throws Exception
	 */
	public static void unzip(String filePath) throws Exception {

		File source = new File(filePath);
		ZipFile zipFile = new ZipFile(source, "gb2312");
		if (source.exists()) {

			BufferedOutputStream bos = null;
			InputStream inputStream = null;
			try {
				ZipEntry entry = null;
				Enumeration<ZipEntry> en = zipFile.getEntries();
				while (en.hasMoreElements()) {
					entry = en.nextElement();
					if (entry.isDirectory()) {

						continue;
					}
					File target = new File(source.getParent(), entry.getName());
					if (!target.getParentFile().exists()) {
						// 创建文件父目录
						target.getParentFile().mkdirs();
					}
					// 写入文件
					bos = new BufferedOutputStream(new FileOutputStream(target));
					int read = 0;
					inputStream = zipFile.getInputStream(entry);

					byte[] buffer = new byte[1024 * 10];
					while ((read = inputStream.read(buffer, 0, buffer.length)) != -1) {
						bos.write(buffer, 0, read);
					}
					bos.flush();
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				IOUtil.closeQuietly(zipFile, bos, inputStream);
			}
		}
	}

	public static void main(String[] args) {

		// zip("d:/1.zip",new String[]{"d:/QQ图片20160112144921.jpg","d:/1.doc"});
	}
}

/**
 * 用于关闭流对象
 * 
 * @author wanglei
 * @version [版本号, 2016年6月1日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
class IOUtil {
	/**
	 * 关闭一个或多个流对象
	 * 
	 * @param closeables
	 *            可关闭的流对象列表
	 * @throws IOException
	 */
	public static void close(Closeable... closeables) throws IOException {
		if (closeables != null) {
			for (Closeable closeable : closeables) {
				if (closeable != null) {
					closeable.close();
				}
			}
		}
	}

	/**
	 * 关闭一个或多个流对象
	 * 
	 * @param closeables
	 *            可关闭的流对象列表
	 */
	public static void closeQuietly(Closeable... closeables) {
		try {
			close(closeables);
		} catch (IOException e) {
			// do nothing
		}
	}

}
