package com.xunyanhui.model;


/*
 * 艺人通告视图
 */
public class ArtistAnnouncementV {

	private String id;
	/*
	 * 通告内容
	 */
	private String content;
	/*
	 * 起始时间
	 */
	private String beginTime;
	/*
	 * 结束时间
	 */
	private String endTime;
	/*
	 * 通告状态
	 */
	private int state;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "ArtistAnnouncement [id=" + id + ", content=" + content
				+ ", beginTime=" + beginTime + ", endTime=" + endTime
				+ ", state=" + state + "]";
	}

}
