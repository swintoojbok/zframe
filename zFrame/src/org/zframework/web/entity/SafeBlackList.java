package org.zframework.web.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name="sys_safeblacklist")
public class SafeBlackList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3633414993342330107L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO,generator="seq_sys_safeblacklist")
	@SequenceGenerator(name="seq_sys_safeblacklist",sequenceName="seq_sys_safeblacklist")	
	private Integer id;
	@Column
	@NotEmpty
	@Length(min=1,max=50)
	private String userName;
	@Column
	@NotEmpty
	private int enabled;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public int getEnabled() {
		return enabled;
	}
	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}
}