package fr.info.antillesinfov2.business.model;

import java.security.PrivateKey;

/**
 * Created by Frankito on 06/02/16.
 */
public class Retrait {
    private Double montant;
    private String libelle;
    private String dateMouvement;

    public String getLibelle() {
        return libelle;
    }

    public String getDateMouvement() {
        return dateMouvement;
    }

    public void setDateMouvement(String dateMouvement) {
        this.dateMouvement = dateMouvement;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Double getMontant() {
        return montant;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }
}
