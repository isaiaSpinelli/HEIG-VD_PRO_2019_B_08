
package controllers.BDDpackage;


import java.awt.*;

/**
 * Classe permettant de modéliser un Modele de transaction de la base de donnée
 * @author Compact budget
 * @version 1.0
 * @since 1.0
 */
public class Modele_transaction {


    public int id;
    public double valeur;
    public String date;
    public String note;
    public Utilisateur user;
    public SousCategorie sousCategorie;
    public int type_transaction_id; // Depense ou Revenu !
    public Recurrence recurrence ;

    Modele_transaction() {

    }
    // tout
    public Modele_transaction(double valeur, String date, String note, Utilisateur user,SousCategorie sousCategorie,int type_transaction_id,Recurrence recurrence ){
        this.valeur = valeur;
        this.date = date;
        this.note = note;
        this.user = user;
        this.sousCategorie = sousCategorie;
        this.type_transaction_id = type_transaction_id;
        this.recurrence = recurrence;
    }
    // Sans note
    public Modele_transaction(double valeur, String date,Utilisateur user,SousCategorie sousCategorie,int type_transaction_id,Recurrence recurrence ){
        this(valeur,date,null,user,sousCategorie,type_transaction_id,recurrence);
    }
    // Sans recurrence
    public Modele_transaction(double valeur, String date, String note, Utilisateur user,SousCategorie sousCategorie,int type_transaction_id ){
        this(valeur,date,note,user,sousCategorie,type_transaction_id,null);
    }
    // Sans note ni recurrence
    public Modele_transaction(double valeur, String date, Utilisateur user,SousCategorie sousCategorie,int type_transaction_id ){
        this(valeur,date,null,user,sousCategorie,type_transaction_id,null);
    }

}
