package org.zframework.model.query;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.zframework.model.pagination.PageBean;

public class QueryParams {
	private PageBean pageBean;
	private List<Criterion> criterions = new ArrayList<Criterion>();
	private List<Order> orders = new ArrayList<Order>();
	private List<Projections> projections = new ArrayList<Projections>();
	public PageBean getPageBean() {
		return pageBean;
	}
	public void setPageBean(PageBean pageBean) {
		this.pageBean = pageBean;
	}
	public List<Criterion> getCriterions() {
		return criterions;
	}
	public void setCriterions(Criterion criterion) {
		this.criterions.add(criterion);
	}
	public List<Order> getOrders() {
		return orders;
	}
	public void addOrders(Order order) {
		this.orders.add(order);
	}
	public List<Projections> getProjections() {
		return projections;
	}
	public void addProjections(Projections projection) {
		this.projections.add(projection);
	}
	public void clearCriterions(){
		this.criterions.clear();
	}
	public void clearOrders(){
		this.orders.clear();
	}
	public void clearProjections(){
		this.projections.clear();
	}
	public void clearAll(){
		this.criterions.clear();
		this.orders.clear();
		this.projections.clear();
	}
}
