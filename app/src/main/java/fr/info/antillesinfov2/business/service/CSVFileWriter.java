package fr.info.antillesinfov2.business.service;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import fr.info.antillesinfov2.business.model.Retrait;


/**
 * Classe permettant la création de fichiers csv
 *
 * @author neblai
 */
public class CSVFileWriter {

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

        FileWriter fileWriter = null;
        try {
            String baseDir = Environment.getExternalStoragePublicDirectory("csv").getAbsolutePath();
            fileWriter = new FileWriter(baseDir + fileName);

            fileWriter.append(CSVFileWriter.HEADER_RECAP);
            fileWriter.append(CSVFileWriter.COMMA_DELIMITER);
            Date date = new Date();
            fileWriter.append(date.toString());
            fileWriter.append(CSVFileWriter.NEW_LINE_SEPARATOR);
            //montantCB
            fileWriter.append("Montant total Espèce:");
            fileWriter.append(CSVFileWriter.COMMA_DELIMITER);
            fileWriter.append(montantEspece);
            fileWriter.append(CSVFileWriter.NEW_LINE_SEPARATOR);
            //montantCheque
            fileWriter.append("Montant total CB:");
            fileWriter.append(CSVFileWriter.COMMA_DELIMITER);
            fileWriter.append(montantCB);
            fileWriter.append(CSVFileWriter.NEW_LINE_SEPARATOR);
            //montantTotal caisse
            fileWriter.append("Montant total Caisse:");
            fileWriter.append(CSVFileWriter.COMMA_DELIMITER);
            fileWriter.append(montantTotal);
            fileWriter.append(CSVFileWriter.NEW_LINE_SEPARATOR);

            //recap des mouvements
            fileWriter.append(CSVFileWriter.HEADER_MOUVEMENT);
            fileWriter.append(CSVFileWriter.NEW_LINE_SEPARATOR);
            for (final Retrait r : retraits) {
                fileWriter.append(r.getDateMouvement());
                fileWriter.append(CSVFileWriter.COMMA_DELIMITER);
                fileWriter.append(r.getLibelle());
                fileWriter.append(CSVFileWriter.COMMA_DELIMITER);
                fileWriter.append(Double.toString(r.getMontant()));
                fileWriter.append(CSVFileWriter.NEW_LINE_SEPARATOR);
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
}