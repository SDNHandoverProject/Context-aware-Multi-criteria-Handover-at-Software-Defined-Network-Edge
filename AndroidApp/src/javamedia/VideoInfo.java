package javamedia;

import java.io.Serializable;

public class VideoInfo implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 371516560049157079L;
	private String VideoName = "";
	private String Style = "";
	private String format = "";
	private String VideoTime = "";
	private String CreateDate = "";
	private String Author = "";
	
	public String getVideoName() {
		return VideoName;
	}
	
	public void setVideoName(String videoName) {
		VideoName = videoName;
	}
	
	public String getStyle() {
		return Style;
	}
	
	public void setStyle(String style) {
		Style = style;
	}
	
	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
	
	public String getVideoTime() {
		return VideoTime;
	}
	
	public void setVideoTime(String videoTime) {
		VideoTime = videoTime;
	}
	
	public String getCreateDate() {
		return CreateDate;
	}
	
	public void setCreateDate(String createDate) {
		CreateDate = createDate;
	}
	
	public String getAuthor() {
		return Author;
	}
	
	public void setAuthor(String author) {
		Author = author;
	}

}
