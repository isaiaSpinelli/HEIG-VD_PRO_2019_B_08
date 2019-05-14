
package controllers.BDDpackage;

/**
 * Classe permettant de modéliser une Transaction de la base de donnée
 * @author Compact budget
 * @version 1.0
 * @since 1.0
 */
public class Transaction {

    public int id;
    public String name;
    public double valeur;
    public String date;
    public int idRecurrence;

    public double timestamp_solde;

    public int typeTransaction ;


    Transaction() {

    }
    // tout

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
