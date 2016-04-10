package fr.info.antillesinfov2.business.model;



public class Categorie implements java.io.Serializable {

	private Integer categoryId;
	private String categoryType;
	
	public Categorie() {
	}	


	public Integer getCategoryId() {
		return this.categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}


	public String getCategoryType() {
		return this.categoryType;
	}

	public void setCategoryType(String categoryType) {
		this.categoryType = categoryType;
	}
}