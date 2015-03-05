package com.zxsoft.crawler.storage;

public class Website implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5354652608097154552L;
	private Integer id;
	private String site;
	private String comment;
	private String region;
	private String status;
	/**
	 * 对应oracle中的ly
	 */
	private int tid;
	
	public Website() {
	}


	public Website(Integer id, String site, String comment, int tid) {
                super();
                this.id = id;
                this.site = site;
                this.comment = comment;
                this.tid = tid;
        }

        public int getTid() {
                return tid;
        }


        public void setTid(int tid) {
                this.tid = tid;
        }


        public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSite() {
		return this.site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getRegion() {
		return this.region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
