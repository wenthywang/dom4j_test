/**
 * Copyright(c) Guangzhou JiaxinCloud Science & Technology Ltd. 
 */
package parse;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.filechooser.FileSystemView;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

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
	//临时缓存
	private static Map<String, List<Person>> dataMap = new HashMap<String, List<Person>>();

	public static void main(String[] args) throws Exception {
	    //桌面存放xml的文件夹名称
		String folderName="xml";
		//xml文件名称
		String fileName="content.xml";
		//获取文件
		File f=getFile(folderName,fileName);
		//解析xml并放到临时缓存dataMap 中
		parseXml(f);
		
//		for (Entry<String, List<Person>> entry : dataMap.entrySet()) {
//			System.out.println(entry.getKey() + "->" + entry.getValue());
//		}
		
		//从dataMap中取数据
		searchByDate("2017-08-07");
	}

	public static void parseXml(File f){
		SAXReader reader = new SAXReader();
		Document document = null;
		try {
			document = reader.read(f);
		} catch (DocumentException e) {
			e.printStackTrace();
			System.out.println("读取xml文档异常！");
		}
		document.setXMLEncoding("UTF-8");
		Element root = document.getRootElement();
		//处理xml文档 把数据存放在map中
		Node n = root.selectSingleNode("/*[name()='xmap-content']/*[name()='sheet']");
		treeWalk(n.getDocument());
	}
	
	public static File getFile(String folderName,String fileName){
		// 获取桌面路径
		FileSystemView fsv = FileSystemView.getFileSystemView();
		File com = fsv.getHomeDirectory();
		String xmlPath = com.getPath() + File.separator + folderName + File.separator + fileName;
		File f = new File(xmlPath);
		return f;
	}
	
	
	/**
	 * 遍历节点
	 * 
	 * @param document
	 */
	public static void treeWalk(Document document) {
		treeWalk(document.getRootElement());
	}

	/**
	 * 递归遍历节点
	 * 
	 * @param element
	 */
	public static void treeWalk(Element element) {

		for (int i = 0, size = element.nodeCount(); i < size; i++) {
			Node node = element.node(i);

			if (node instanceof Element) {
				Element e = (Element) node;
				// 存在会员信息的节点才进行处理
				if (!"".equals(e.getTextTrim()) && e.getTextTrim().length() > 10) {
					Person p = new Person();
					Element parentNode = e.getParent();
				//分割"*"
					String[] text = e.getTextTrim().split("\\*");
					String date = text[0];
					p.setDate(date.replace(" ", ""));
					p.setName(text[2].replace(" ", ""));
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
							Element childNode = (Element) parentNode.node(j);
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
			} else {
				// do something…
			}
		}
	}

	/**
	 * 根据日期查询会员信息
	 * @param date
	 */
	public static void searchByDate(String date){
    	List<Person>pList =	dataMap.get(date);
    	System.out.println("List result->"+pList);
    	System.out.println("Json result->"+JSONObject.toJSONString(pList));
	}
}