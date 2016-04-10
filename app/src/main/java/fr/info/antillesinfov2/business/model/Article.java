package fr.info.antillesinfov2.business.model;

public class Article {
	private Integer idProduit;
	private Integer quantite;

	/**
	 * @return the idProduit
	 */
	public Integer getIdProduit() {
		return idProduit;
	}

	/**
	 * @param idProduit
	 *            the idProduit to set
	 */
	public void setIdProduit(Integer idProduit) {
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
