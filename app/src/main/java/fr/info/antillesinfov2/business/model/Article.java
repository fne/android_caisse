package fr.info.antillesinfov2.business.model;

import java.math.BigDecimal;

public class Article {
	private String idProduit;
	private Integer quantite;
	private Double price;

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    /**
	 * @return the idProduit
	 */
	public String getIdProduit() {
		return idProduit;
	}

	/**
	 * @param idProduit
	 *            the idProduit to set
	 */
	public void setIdProduit(String idProduit) {
		this.idProduit = idProduit;
	}

	/**
	 * @return the quantite
	 */
	public Integer getQuantite() {
		return quantite;
	}

	/**
	 * @param quantite
	 *            the quantite to set
	 */
	public void setQuantite(Integer quantite) {
		this.quantite = quantite;
	}
}
