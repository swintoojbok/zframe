package org.zframework.core;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
@SuppressWarnings("unchecked")
public class ApplicationContextHelper implements ApplicationContextAware{
	private ApplicationContext applicationContext;
	private static ApplicationContextHelper instance = null;
	private ApplicationContextHelper(){
		
	}
	public static ApplicationContextHelper getInstance(){
		if(instance == null)
			instance = new ApplicationContextHelper();
		return instance;
	}
	public void setApplicationContext(ApplicationContext arg0)
			throws BeansException {
		instance = this;
		this.applicationContext = arg0;
	}
	/**
	 * 获取ApplicationContext
	 * @return
	 */
	public ApplicationContext getApplicationContext(){
		checkApplicationContext();
		return this.applicationContext;
	}
	/**
	 * 根据BeanName获取Bean
	 * @param <T>
	 * @param beanName
	 * @return
	 */
	public <T> T getBean(String beanName){
		return (T) this.applicationContext.getBean(beanName);
	}
	/**
	 * 获取Bean
	 * @param <T>
	 * @param beanName
	 * @return
	 */
	public <T> T getBean(Class<T> clazz){
		return (T) this.applicationContext.getBean(clazz);
	}
	/**
	 * 清除ApplicationContext
	 */
	public  void clearApplicationContext(){
		this.applicationContext = null;
	}
	/**
	 * 检查
	 */
	private void checkApplicationContext() {
		if(this.applicationContext == null){
			System.out.println("applicationContext未注入，请在service-context.xml文件中配置!");
		}
	}
}
