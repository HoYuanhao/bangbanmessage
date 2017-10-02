package com.xunyanhui.model;



public class ArtistOpusV {
	private String oid;
	private String name;// 作品名称
	private String description;// 作品描述
	private String releaseTime;// 发布时间
	private String releaseId;// 发布id
	private int type;// 作品类型
	private String fileName;// 文件名
	private String filePath;// 文件路径
	private int praiseNum;// 好评数
	private int playNum;//点击数
	private String label;//小样标签
	private String kind;//小样类别
	private String uid;//
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getReleaseTime() {
		return releaseTime;
	}

	public void setReleaseTime(String releaseTime) {
		this.releaseTime = releaseTime;
	}

	public String getReleaseId() {
		return releaseId;
	}

	public void setReleaseId(String releaseId) {
		this.releaseId = releaseId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}


	public int getPraiseNum() {
		return praiseNum;
	}

	public void setPraiseNum(int praiseNum) {
		this.praiseNum = praiseNum;
	}

	@Override
	public String toString() {
		return "ArtistOpus [id=" + oid + ", name=" + name + ", description="
				+ description + ", releaseTime=" + releaseTime + ", releaseId="
				+ releaseId + ", type=" + type + ", fileName=" + fileName
				+ ", filePath=" + filePath 
				+ ", praiseNum=" + praiseNum + "]";
	}

	public int getPlayNum() {
		return playNum;
	}

	public void setPalyNum(int playNum) {
		this.playNum = playNum;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	

	

}
