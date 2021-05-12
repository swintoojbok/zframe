package org.zframework.database;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.zframework.core.ApplicationContextHelper;
import org.zframework.core.util.ObjectUtil;

/**
 * 多数据源转换类
 * @author ZENGCHAO
 *
 */
public class MultiDataSource implements DataSource{
	private DataSource dataSource = null;
	private DataSource defaultDataSource = null;
	private String dataSourceName = "";
	public MultiDataSource(){
		
	}
	public MultiDataSource(String dataSourceName){
		this.dataSource = getDataSource(dataSourceName);
	}
	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
		if(ObjectUtil.isNull(defaultDataSource)){
			this.defaultDataSource = dataSource;
		}
	}
	public DataSource getDataSource(){
		if(ObjectUtil.isNotEmpty(dataSourceName))
			return getDataSource(dataSourceName);
		return this.dataSource;
	}
	/**
	 * 从Spring配置文件中动态读取数据源
	 * @param 数据源Bean的ID
	 * @return
	 */
	public DataSource getDataSource(String dataSourceName){
		return ApplicationContextHelper.getInstance().getBean(dataSourceName);
	}
	/**
	 * 还原为默认数据源
	 */
	public void restoreDefaultDataSource(){
		this.dataSource = this.defaultDataSource;
	}
	
	public PrintWriter getLogWriter() throws SQLException { 
		return getDataSource().getLogWriter();
	}

	public void setLogWriter(PrintWriter out) throws SQLException {
		getDataSource().setLogWriter(out);
	}

	public void setLoginTimeout(int seconds) throws SQLException {
		getDataSource().setLoginTimeout(seconds);
	}

	public int getLoginTimeout() throws SQLException {
		return getDataSource().getLoginTimeout();
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		return getDataSource().unwrap(iface);
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return getDataSource().isWrapperFor(iface);
	}

	public Connection getConnection() throws SQLException {
		return getDataSource().getConnection();
	}
	public Connection getConnection(String username, String password)
			throws SQLException {
		return getDataSource().getConnection(username, password);
	}
}
