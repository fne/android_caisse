/**
 *
 */
package fr.info.antillesinfov2.business.model;

import java.util.List;

/**
 * @author Frankito
 *         "articles":[{"1":5},{"2":6}],
 *         "prixTotal":50.25,
 *         "nbArticles":11
 */
public class DetailPanierCommande {
    private List<Produit> produits;
    private Double prixTotal;
    private Integer nbArticles;
    private String moyenPaiement;
    private String date;
    private String sessionId;
    private String caisseId;


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
     * @param prixTotal the prixTotal to set
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
     * @param nbArticles the nbArticles to set
     */
    public void setNbArticles(Integer nbArticles) {
        this.nbArticles = nbArticles;
    }

    /**
     * @return the articles
     */
    public List<Produit> getProduits() {
        return produits;
    }

    /**
     * @param produits the articles to set
     */
    public void setProduits(List<Produit> produits) {
        this.produits = produits;
    }

    /**
     * @return the moyenPaiement
     */
    public String getMoyenPaiement() {
        return moyenPaiement;
    }

    /**
     * @param moyenPaiement the moyenPaiement to set
     */
    public void setMoyenPaiement(String moyenPaiement) {
        this.moyenPaiement = moyenPaiement;
    }

    public String getCaisseId() {
        return caisseId;
    }

    public void setCaisseId(String caisseId) {
        this.caisseId = caisseId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

}
