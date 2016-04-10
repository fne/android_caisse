/**
 * 
 */
package fr.info.antillesinfov2.business.model;

import java.util.Date;
import java.util.List;

/**
 * @author Frankito
 * 	"articles":[{"1":5},{"2":6}],
	"prixTotal":50.25,
	"nbArticles":11
 */
public class PanierCommande {
	private List<Article> articles;
	private Double prixTotal;
	private Integer nbArticles;
	private Integer moyenPaiement;
	private String date;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * @return the prixTotal
	 */
	public Double getPrixTotal() {
		return prixTotal;
	}

	/**
	 * @param prixTotal
	 *            the prixTotal to set
	 */
	public void setPrixTotal(Double prixTotal) {
		this.prixTotal = prixTotal;
	}

	/**
	 * @return the nbArticles
	 */
	public Integer getNbArticles() {
		return nbArticles;
	}

	/**
	 * @param nbArticles
	 *            the nbArticles to set
	 */
	public void setNbArticles(Integer nbArticles) {
		this.nbArticles = nbArticles;
	}

	/**
	 * @return the articles
	 */
	public List<Article> getArticles() {
		return articles;
	}

	/**
	 * @param articles the articles to set
	 */
	public void setArticles(List<Article> articles) {
		this.articles = articles;
	}

	/**
	 * @return the moyenPaiement
	 */
	public Integer getMoyenPaiement() {
		return moyenPaiement;
	}

	/**
	 * @param moyenPaiement the moyenPaiement to set
	 */
	public void setMoyenPaiement(Integer moyenPaiement) {
		this.moyenPaiement = moyenPaiement;
	}

}
