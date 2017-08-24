/**
 * Copyright(c) Guangzhou JiaxinCloud Science & Technology Ltd. 
 */
package parse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.filechooser.FileSystemView;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

/**
 * <pre>
 * Dom4J 解析xml。
 * </pre>
 * 
 * @author 王文辉 wangwenhui@jiaxincloud.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 *          </pre>
 */
public class Dom4JTest {
	/**
	 * 添加日志打印
	 */
	private static final Logger logger = LoggerFactory.getLogger(Dom4JTest.class);

	// 临时缓存
	private static Map<String, List<Person>> dataMap = new HashMap<String, List<Person>>();
	// 桌面存放xml的文件夹名称
	private static String FOLDER_NAME = "xml";
	// xml文件名称
	private static String XML_FILE_NAME = "content.xml";

	private static String ZIP_FILE_NAME = "1234.xmind";

	private static String TARGET_ZIP_FILE_NAME = "target.xmind";

	private static Map<String, String> params = new HashMap<String, String>();
	
	
public  static  String DESKTOP_PATH="";
	
	static {
		// 获取桌面路径
				FileSystemView fsv = FileSystemView.getFileSystemView();
				File com = fsv.getHomeDirectory();
				DESKTOP_PATH=com.getPath();
	}
	
	

	public static void main(String[] args) throws Exception {
		// 需要修改的字符串 放到params 里面
		params.put("肖天亮", "test");

		// 解压
		unZip();

		// 使用对象方式 不使用静态方法
		Dom4JTest test = new Dom4JTest();
		// 获取文件
		File f = test.getFile();
		if (f == null) {
			logger.error("文件不存在，请检查文件夹名称、文件名称，还有是否存在于桌面！");
			return;
		}
		// 解析xml并放到临时缓存dataMap 中
		Document doc = test.parseXml(f);
		// 根据日期查询会员信息
		test.searchByDate("2017-08-10");

		// 生成新的xml覆盖原来的content.xml
		outPutXml(f.getPath(), doc);

		// 压缩
		Zip();

		// 删除生成后的xml文件夹
		deleteXmlFolder();

		// for (Entry<String, List<Person>> entry : dataMap.entrySet()) {
		// System.out.println(entry.getKey() + "->" + entry.getValue());
		// }

	}

	private static void deleteXmlFolder() throws IOException {
		// 回收资源 删除文件
		System.gc();
		logger.info("delete xml folder begin ....");
		String xmlFolderPath = DESKTOP_PATH + File.separator + FOLDER_NAME;
		FileUtils.deleteDirectory(new File(xmlFolderPath));
		logger.info("delete xml folder end ....");
	}

	/**
	 * 输出修改后的xml文档
	 * 
	 * @param originXmlFile
	 *            原来的xml文档
	 * @param doc
	 *            新的xml文档
	 */
	private static void outPutXml(String originXmlFilePath, Document doc) {
		logger.info("output xml begin ....");
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
		logger.info("output xml end ....");
	}

	/**
	 * 解压xmind
	 * 
	 * @throws Exception
	 */
	private static void unZip() throws Exception {
		logger.info("unZip  zip   begin ....");
		// 解压
		String zipPath = DESKTOP_PATH + File.separator + FOLDER_NAME + File.separator + ZIP_FILE_NAME;
		File zipFile = new File(zipPath);
		if (!zipFile.exists()) {
			// 复制压缩文件到xml文件夹中
			String srcFilePath = DESKTOP_PATH + File.separator + ZIP_FILE_NAME;
			String destDir = DESKTOP_PATH + File.separator + FOLDER_NAME;
			FileUtils.moveFileToDirectory(new File(srcFilePath), new File(destDir), true);
		}
		// 解压文件
		ZipUtil.unzip(zipPath);

		logger.info("unZip zip  end  ....");
	}

	/**
	 * 压缩
	 */
	private static void Zip() {
		logger.info("zip file begin ....");
		// 压缩
		String originFolder = DESKTOP_PATH + File.separator + FOLDER_NAME;
		String targetFilePath = DESKTOP_PATH + File.separator + TARGET_ZIP_FILE_NAME;
		ZipUtil.zip(targetFilePath, originFolder);
		logger.info("zip file end ....");
	}

	/**
	 * 解析xml
	 * 
	 * @param f
	 */
	public Document parseXml(File f) {
		logger.info("parse xml  begin ....");
		SAXReader reader = new SAXReader();
		Document document = null;
		try {
			document = reader.read(f);
		} catch (DocumentException e) {
			logger.error("读取xml文档异常！", e);
		}
		document.setXMLEncoding("UTF-8");
		Element root = document.getRootElement();
		// 处理xml文档 把数据存放在map中
		Node n = root.selectSingleNode("/*[name()='xmap-content']/*[name()='sheet']");
		treeWalk(n.getDocument());
		logger.info("parse xml  end ....");
		return document;
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
	public File getFile() {
		// 获取桌面路径
		FileSystemView fsv = FileSystemView.getFileSystemView();
		File com = fsv.getHomeDirectory();
		String xmlPath = com.getPath() + File.separator + FOLDER_NAME + File.separator + XML_FILE_NAME;
		File f = new File(xmlPath);
		if (f.exists()) {
			return f;
		} else {
			return null;
		}
	}

	/**
	 * 遍历节点
	 * 
	 * @param document
	 */
	public void treeWalk(Document document) {
		treeWalk(document.getRootElement());
	}

	/**
	 * 递归遍历节点
	 * 
	 * @param element
	 */
	public void treeWalk(Element element) {
		for (int i = 0, size = element.nodeCount(); i < size; i++) {
			Node node = element.node(i);
			if (node instanceof Element) {
				Element e = (Element) node;
				// 存在会员信息的节点才进行处理
				if (!"".equals(e.getTextTrim()) && e.getTextTrim().length() > 10) {
					Person p = new Person();
					Element parentNode = e.getParent();
					// 分割"*"
					String[] text = e.getTextTrim().split("\\*");
					String date = text[0];
					p.setDate(date.replace(" ", ""));
					p.setName(text[2].replace(" ", ""));
					if (!params.isEmpty()) {
						String txt = e.getTextTrim();
						for (Entry<String, String> entry : params.entrySet()) {
							if (!entry.getKey().equals("") && !entry.getValue().equals("")) {
								// 替换相关参数
								if (txt.contains(entry.getKey())) {
									String oldvalue = entry.getKey();
									String newValue = entry.getValue();
									txt = txt.replace(oldvalue, newValue);
									e.setText(txt);
									break;
								}

							}
						}
					}
					p.setId(text[1].replace(" ", ""));
					p.setRecommender(text[4].replace(" ", ""));
					if (parentNode != null) {
						parentNode = parentNode.getParent();
						if (parentNode != null && parentNode.getParent() != null
								&& parentNode.getParent().getParent() != null) {
							Element show = parentNode.getParent().getParent();
							String parentInfo = show.elementText("title").replace("\r\n", "");
							String[] parentText = parentInfo.split("\\*");
							p.setParentId(parentText[1].replace(" ", ""));
							// System.out.println("父亲->"+show.elementText("title").replace("\r\n",
							// ""));
						}

						for (int j = 0; j < parentNode.nodeCount();) {
							Node childNode = parentNode.node(j);
							if (j == 0 && e.getUniquePath().contains(childNode.getUniquePath())) {
								// System.out.println("当前节点位置->left");
								p.setPosition("left");
								break;
							} else {
								// System.out.println("当前节点位置->right");
								p.setPosition("right");
								break;
							}
						}
					}

					if (dataMap.get(date) != null) {
						dataMap.get(date).add(p);
					} else {
						List<Person> list = new ArrayList<Person>();
						list.add(p);
						dataMap.put(date, list);
					}
				}
				treeWalk((Element) node);
			}
			continue;
		}
	}

	/**
	 * 根据日期查询会员信息
	 * 
	 * @param date
	 */
	public void searchByDate(String date) {
		logger.info("date->{}", date);
		List<Person> pList = dataMap.get(date);
		if (pList == null) {
			logger.info("查询无结果！");
			return;
		}

		logger.debug("List result->{}", pList);
		logger.debug("Json result->{}", JSONObject.toJSONString(pList));
	}
}
