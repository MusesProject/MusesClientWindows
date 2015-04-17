package eu.musesproject.windowsclient.connectionmanager;

import java.util.Date;

public class ResponseCookie {
	
	private boolean secure;
	private Date expires;
	private String domain;
	private String path;
	
	public ResponseCookie() {
		
	}

	public ResponseCookie(boolean secure, Date expires, String domain,
			String path) {
		super();
		this.secure = secure;
		this.expires = expires;
		this.domain = domain;
		this.path = path;
	}

	public boolean isSecure() {
		return secure;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	public Date getExpires() {
		return expires;
	}

	public void setExpires(Date expires) {
		this.expires = expires;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return "ResponseCookie [secure=" + secure + ", expires=" + expires
				+ ", domain=" + domain + ", path=" + path + "]";
	}

}
