package fr.info.antillesinfov2.business.service;

import android.media.MediaScannerConnection;
import android.os.Environment;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import fr.info.antillesinfov2.business.model.Retrait;


/**
 * Classe permettant la création de fichiers csv
 *
 * @author neblai
 */
public class FileWriter {

    // Delimiter used in CSV file
    private static final String COMMA_DELIMITER = ",";

    private static final String NEW_LINE_SEPARATOR = "\n";

    // CSV file header
    private static final String HEADER_MOUVEMENT = "Heure,Libelle,Montant";
    private static final String HEADER_RECAP = "Récapitulatif de caisse";


    /**
     * csv appelant
     *
     * @param fileName - nom du fichier
     */

    public void writeCsvFile(final String fileName, List<Retrait> retraits, String montantCB, String montantEspece, String montantTotal) {

        java.io.FileWriter fileWriter = null;
        try {
            String baseDir = Environment.getExternalStoragePublicDirectory("csv").getAbsolutePath();
            fileWriter = new java.io.FileWriter(baseDir + fileName);

            fileWriter.append(FileWriter.HEADER_RECAP);
            fileWriter.append(FileWriter.COMMA_DELIMITER);
            Date date = new Date();
            fileWriter.append(date.toString());
            fileWriter.append(FileWriter.NEW_LINE_SEPARATOR);
            //montantCB
            fileWriter.append("Montant total Espèce:");
            fileWriter.append(FileWriter.COMMA_DELIMITER);
            fileWriter.append(montantEspece);
            fileWriter.append(FileWriter.NEW_LINE_SEPARATOR);
            //montantCheque
            fileWriter.append("Montant total CB:");
            fileWriter.append(FileWriter.COMMA_DELIMITER);
            fileWriter.append(montantCB);
            fileWriter.append(FileWriter.NEW_LINE_SEPARATOR);
            //montantTotal caisse
            fileWriter.append("Montant total Caisse:");
            fileWriter.append(FileWriter.COMMA_DELIMITER);
            fileWriter.append(montantTotal);
            fileWriter.append(FileWriter.NEW_LINE_SEPARATOR);

            //recap des mouvements
            fileWriter.append(FileWriter.HEADER_MOUVEMENT);
            fileWriter.append(FileWriter.NEW_LINE_SEPARATOR);
            for (final Retrait r : retraits) {
                fileWriter.append(r.getDateMouvement());
                fileWriter.append(FileWriter.COMMA_DELIMITER);
                fileWriter.append(r.getLibelle());
                fileWriter.append(FileWriter.COMMA_DELIMITER);
                fileWriter.append(Double.toString(r.getMontant()));
                fileWriter.append(FileWriter.NEW_LINE_SEPARATOR);
            }
            System.out.println("CSV file was created successfully !!!");
        } catch (final Exception e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (final IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
            }
        }
    }

    public void writeJsonFile(final String fileName, String fichier) {

        java.io.FileWriter fileWriter = null;
        try {
            String baseDir = Environment.getExternalStoragePublicDirectory("csv").getAbsolutePath();
            fileWriter = new java.io.FileWriter(baseDir + fileName);
            fileWriter.append(fichier);
                        System.out.println("JSON file was created successfully !!!");
        } catch (final Exception e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (final IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
            }
        }
    }
}