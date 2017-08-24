/**
 * Copyright(c) Guangzhou JiaxinCloud Science & Technology Ltd. 
 */
package parseMain;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import entity.Person;
import util.OperUtil;

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
	// 桌面存放xml的文件夹名称 这个可以改
	public static String FOLDER_NAME = "xml";
	// 原始的xmind 文件名称
	public static String ZIP_FILE_NAME = "1234.xmind";
	// 修改后的xmind文件名称
	public static String TARGET_ZIP_FILE_NAME = "target.xmind";
	// 修改的参数 存放的map key为原来的会员信息 value为要修改的会员信息
	private static Map<String, String> params = new HashMap<String, String>();

	public static void main(String[] args) throws Exception {
		// 需要修改的字符串 放到params 里面
		params.put("肖天亮", "test");

		// 解压
		boolean operResult=OperUtil.unZip();
		//解压不成功不往下走
		if(!operResult){
			return;
		}
		// 使用对象方式 不使用静态方法
		Dom4JTest test = new Dom4JTest();
		// 获取文件
		File f = OperUtil.getFile();
		if (f == null) {
			logger.error("文件不存在，请检查文件夹名称、文件名称，还有是否存在于桌面！");
			return;
		}
		// 解析xml并放到临时缓存dataMap 中
		Document doc = test.parseXml(f);
		// 根据日期查询会员信息
		test.searchByDate("2017-08-10");

		// 生成新的xml覆盖原来的content.xml
		OperUtil.outPutXml(f.getPath(), doc);

		// 压缩
		OperUtil.Zip();

		// 删除生成后的xml文件夹
		OperUtil.deleteXmlFolder();

		// for (Entry<String, List<Person>> entry : dataMap.entrySet()) {
		// System.out.println(entry.getKey() + "->" + entry.getValue());
		// }

	}

	/**
	 * 解析xml
	 * 
	 * @param f
	 */
	public Document parseXml(File f) {
		logger.info("parse xml  begin（处理xml文件开始） ....");
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
		logger.info("parse xml  end（处理xml文件结束） ....");
		return document;
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
