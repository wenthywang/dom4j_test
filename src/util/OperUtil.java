/**
 * Copyright(c) Guangzhou JiaxinCloud Science & Technology Ltd. 
 */
package util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.filechooser.FileSystemView;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import parseMain.Dom4JTest;

/**
 * <pre>
 * 常用操作util。
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
public class OperUtil {
	// 桌面路径
	public static String DESKTOP_PATH = "";

	// xml文件名称 这个固定 不改
	public static String XML_FILE_NAME = "content.xml";

	static {
		// 获取桌面路径
		FileSystemView fsv = FileSystemView.getFileSystemView();
		File com = fsv.getHomeDirectory();
		DESKTOP_PATH = com.getPath();
	}

	/**
	 * 添加日志打印
	 */
	private static final Logger logger = LoggerFactory.getLogger(OperUtil.class);

	/**
	 * 输出修改后的xml文档
	 * 
	 * @param originXmlFile
	 *            原来的xml文档
	 * @param doc
	 *            新的xml文档
	 */
	public static void outPutXml(String originXmlFilePath, Document doc) {
		logger.info("output xml begin（输出xml文件开始） ....");
		// 创建输出格式(OutputFormat对象)
		OutputFormat format = OutputFormat.createPrettyPrint();

		/// 设置输出文件的编码
		format.setEncoding("UTF-8");
		// format.setTrimText(true);
		try {
			// 创建XMLWriter对象
			XMLWriter writer = new XMLWriter(new FileOutputStream(originXmlFilePath), format);

			// 设置不自动进行转义
			writer.setEscapeText(false);

			// 生成XML文件
			writer.write(doc);

			// 关闭XMLWriter对象
			writer.close();
		} catch (IOException e) {
			logger.error("outPutXml exception", e);
		}
		logger.info("output xml end（输出xml文件结束） ....");
	}

	/**
	 * 删除 文件 夹
	 * 
	 * @throws IOException
	 */
	public static void deleteXmlFolder() {
		// 回收资源 删除文件
		System.gc();
		logger.info("delete xml folder begin （删除桌面上xml文件夹开始）....");
		String xmlFolderPath = DESKTOP_PATH + File.separator + Dom4JTest.FOLDER_NAME;
		try {
			FileUtils.deleteDirectory(new File(xmlFolderPath));
		} catch (IOException e) {
			logger.info("delete xml folder exception!", e);
		}
		logger.info("delete xml folder end（删除桌面上xml文件夹结束） ....");
	}

	/**
	 * 解压xmind
	 * 
	 * @throws Exception
	 */
	public static boolean unZip() throws Exception {
		logger.info("unZip  zip  begin（解压文件开始） ....");
		// 解压
		String zipPath = DESKTOP_PATH + File.separator + Dom4JTest.FOLDER_NAME + File.separator
				+ Dom4JTest.ZIP_FILE_NAME;
		File zipFile = new File(zipPath);
		if (!zipFile.exists()) {
			// 复制压缩文件到xml文件夹中
			String srcFilePath = DESKTOP_PATH + File.separator + Dom4JTest.ZIP_FILE_NAME;
			String destDir = DESKTOP_PATH + File.separator + Dom4JTest.FOLDER_NAME;
			File srcFile = new File(srcFilePath);
			if (!srcFile.exists()) {
				logger.error("桌面上不存在" + Dom4JTest.ZIP_FILE_NAME + "文件，请检查！");
				return false;
			}
			FileUtils.moveFileToDirectory(srcFile, new File(destDir), true);
		}
		// 解压文件
		ZipUtil.unzip(zipPath);

		logger.info("unZip zip  end （解压文件结束） ....");
		return true;
	}

	/**
	 * 压缩
	 */
	public static void Zip() {
		logger.info("zip file begin （压缩文件开始）....");
		// 压缩
		String originFolder = DESKTOP_PATH + File.separator + Dom4JTest.FOLDER_NAME;
		String targetFilePath = DESKTOP_PATH + File.separator + Dom4JTest.TARGET_ZIP_FILE_NAME;
		ZipUtil.zip(targetFilePath, originFolder);
		logger.info("zip file end（压缩文件结束） ....");
	}

	/**
	 * 获取xml文件
	 * 
	 * @param folderName
	 *            文件夹名称
	 * @param fileName
	 *            文件名称
	 * @return 文件
	 */
	public static File getFile() {
		// 获取桌面路径
		String xmlPath = DESKTOP_PATH + File.separator + Dom4JTest.FOLDER_NAME + File.separator + XML_FILE_NAME;
		File f = new File(xmlPath);
		if (f.exists()) {
			return f;
		} else {
			return null;
		}
	}
}
