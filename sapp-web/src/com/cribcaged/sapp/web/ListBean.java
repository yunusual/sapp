package com.cribcaged.sapp.web;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.event.ActionEvent;
import javax.faces.view.facelets.FaceletContext;
import javax.servlet.http.HttpSession;

import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

/**
 * Base class of backing beans for CRUD operations.
 */
public abstract class ListBean<E> extends AbstractBean {

	private static final long serialVersionUID = 1L;

	private static String rowsPerPageTemplate = "25,50,100,200";
	private static int rowsPerPage = 25;

	protected List<E> elements;
	protected E[] selectedElements;
	protected E currentElement;

	protected abstract void loadList();
	protected abstract void sortList();

	/**
	 * It is implemented for initialize sub-class
	 */
	public abstract void initializeBean();

	// return true if deleted operation is successful
	public abstract boolean deleteElement(E e);

	/**
	 * Initialization of resources and DAO implementations
	 */
	@PostConstruct
	protected void init() {
		initializeBean();
		reload();
	}

	/**
	 * Reset the elements in the list.
	 */
	public void reload(){
		elements = null;
		selectedElements = null;
		createDummyElement();
	}

	public List<E> getElements() {
		if (elements == null) {
			loadList();
			sortList();
		}
		return elements;
	}

	public E[] getSelectedElements() {
		return selectedElements;
	}

	public void setSelectedElements(E[] selectedElements) {
		this.selectedElements = selectedElements;
	}

	public E getSelectedElement(int i) {
		return selectedElements[i];
	}

	public E getCurrentElement() {
		return currentElement;
	}

	public void setCurrentElement(E currentElement) {
		this.currentElement = currentElement;
	}

	public int getSelectedElementCount() {
		if(selectedElements != null) {
			return selectedElements.length;
		}
		return 0;
	}

	public int getElementCount() {
		if(elements != null) {
			return elements.size();
		}
		return 0;
	}

	/**
	 * Sets the current instance.
	 * @param event
	 */
	public void selectElement(ActionEvent event) { // NOSONAR
		if (selectedElements.length == 1) {
			currentElement = selectedElements.clone()[0];
			// Clears values press edit button
			super.reset();
		} else {
			createDummyElement();
		}
	}

	/**
	 * Creates new empty element
	 *
	 * @param event
	 */
	public void createElement(ActionEvent event) { // NOSONAR
		// Clears values press add button
		super.reset();
	}

	/**
	 * Creates dummy current element to prevent JSF exception. Calls
	 * createElement as defaults. If cost of calling createElement is high you
	 * can implement the method
	 */
	public void createDummyElement() {
		createElement(null);
	}

	/**
	 * Deletes the selected elements.
	 *
	 * @param event
	 */
	public void deleteElements(ActionEvent event) { // NOSONAR
		for (E e : selectedElements) {
			deleteElement(e);
		}
		reload();
	}

	/**
	 * Deletes the selected element.
	 * @param event
	 */
	public void deleteElement(ActionEvent event) { // NOSONAR
		deleteElement(currentElement);
		reload();
	}

	public void onRowSelect(SelectEvent event) { // NOSONAR
	}

	public void onRowUnselect(UnselectEvent event) { // NOSONAR
	}

	public void onDummyRowSelect(SelectEvent event) { // NOSONAR
	}

	public void onDummyRowUnselect(UnselectEvent event) { // NOSONAR
	}

	public String getPaginatorTemplate() {
		return "{FirstPageLink} {PreviousPageLink} {PageLinks} {NextPageLink} {LastPageLink} {RowsPerPageDropdown} {CurrentPageReport}";
	}

	public int getRowsPerPage() {
		return ListBean.rowsPerPage;
	}

	public void setRowsPerPage(int rowsPerPage) {
		ListBean.rowsPerPage = rowsPerPage;
	}

	public String getRowsPerPageTemplate() {
		return ListBean.rowsPerPageTemplate;
	}

	private String getPageBasedAttributeName(String propertyName){
		FaceletContext faceletContext = getFaceletContext();
		String module = (String) faceletContext.getAttribute("module");
		String page = (String) faceletContext.getAttribute("page");
		return module+"_"+page+"_"+propertyName;

	}
	public Object getPageBasedAttributeValueFromSession(String propertyName){
		HttpSession session = getSession(true);
		Object sessionSelectView =  session.getAttribute(getPageBasedAttributeName(propertyName));
		return sessionSelectView;
	}

	public void setPageBasedAttributeOnSession(String propertyName, Object value){
		HttpSession session = getSession(true);
		session.setAttribute(getPageBasedAttributeName(propertyName), value);
	}

}
