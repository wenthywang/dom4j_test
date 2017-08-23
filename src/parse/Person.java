/**
 * Copyright(c) Guangzhou JiaxinCloud Science & Technology Ltd. 
 */
package parse;

/**
 * <pre>
 * 会员实体。
 * </pre>
 * @author 王文辉  wangwenhui@jiaxincloud.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class Person {
	/**
	 * 日期
	 */
	private String date;
	/**
	 * 会员名称
	 */
	private String name;
	/**
	 * 会员ID
	 */
	private String id;
	/**
	 * 节点位置
	 */
	private String position;
	/**
	 * 上一节点位置
	 */
	private String parentId;
	/**
	 * 推荐人ID
	 */
	private String recommender;
	/**
	 * @return 返回 date。
	 */
	public String getDate() {
		return date;
	}
	/**
	 * @param date 设置 date。
	 */
	public void setDate(String date) {
		this.date = date;
	}
	/**
	 * @return 返回 name。
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name 设置 name。
	 */
	public void setName(String name) {
		this.name = name;
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
	 * @return 返回 position。
	 */
	public String getPosition() {
		return position;
	}
	/**
	 * @param position 设置 position。
	 */
	public void setPosition(String position) {
		this.position = position;
	}
	/**
	 * @return 返回 parentId。
	 */
	public String getParentId() {
		return parentId;
	}
	/**
	 * @param parentId 设置 parentId。
	 */
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	/**
	 * @return 返回 recommender。
	 */
	public String getRecommender() {
		return recommender;
	}
	/**
	 * @param recommender 设置 recommender。
	 */
	public void setRecommender(String recommender) {
		this.recommender = recommender;
	}
	@Override
	public String toString() {
		return "Person [date=" + date + ", name=" + name + ", id=" + id + ", position=" + position + ", parentId="
				+ parentId + ", recommender=" + recommender + "]";
	}

	

}
