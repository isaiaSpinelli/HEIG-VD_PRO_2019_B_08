
package controllers.BDDpackage;

/**
 * Classe permettant de modéliser une Transaction de la base de donnée
 * @author Compact budget
 * @version 1.0
 * @since 1.0
 */
public class Transaction {

    /**
     * ID de la transaction
     */
    public int id;
    /**
     * nom de la transaction
     */
    public String name;
    /**
     * valeur de la transaction
     */
    public double valeur;
    /**
     * date de la transaction
     */
    public String date;
    /**
     * Recurrence de la transaction
     */
    public int idRecurrence;
    /**
     * timestamp_solde de la transaction
     */
    public double timestamp_solde;
    /**
     * type de la transaction
     */
    public int typeTransaction ;


    Transaction() {

    }

    /** Constructeur complet
     * @param id
     * @param name
     * @param valeur
     * @param date
     * @param idRecurence
     * @param timestamp_solde
     * @param typeTransaction
     */
    public Transaction(int id, String name, double valeur, String date, int idRecurence, double timestamp_solde, int typeTransaction){

        this.id = id;
        this.name = name;
        this.valeur = valeur;
        this.date = date;
        this.idRecurrence = idRecurence;
        this.timestamp_solde = timestamp_solde;
        this.typeTransaction = typeTransaction;

    }

}
