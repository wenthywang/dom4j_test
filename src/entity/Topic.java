/**
 * Copyright(c) Guangzhou JiaxinCloud Science & Technology Ltd. 
 */
package entity;

import java.util.List;

/**
 * <pre>
 * 程序的中文名称。
 * </pre>
 * @author 王文辉  wangwenhui@jiaxincloud.com
 * @version 1.00.00
 * @date 2017年8月25日 
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class Topic {
	
	private String id;
	private String title;
	private List<Topic>children;
	
	
	/**
	 * 
	 */
	public Topic() {
	}


	/**
	 * @param id
	 * @param title
	 * @param children
	 */
	public Topic(String id, String title, List<Topic> children) {
		super();
		this.id = id;
		this.title = title;
		this.children = children;
	}


	/**
	 * @return 返回 id。
	 */
	public String getId() {
		return id;
	}


	/**
	 * @param id 设置 id。
	 */
	public void setId(String id) {
		this.id = id;
	}


	/**
	 * @return 返回 title。
	 */
	public String getTitle() {
		return title;
	}


	/**
	 * @param title 设置 title。
	 */
	public void setTitle(String title) {
		this.title = title;
	}


	/**
	 * @return 返回 children。
	 */
	public List<Topic> getChildren() {
		return children;
	}


	/**
	 * @param children 设置 children。
	 */
	public void setChildren(List<Topic> children) {
		this.children = children;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Topic [id=" + id + ", title=" + title + ", children=" + children + "]";
	}
	
	

}
