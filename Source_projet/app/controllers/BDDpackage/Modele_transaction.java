
package controllers.BDDpackage;


import java.awt.*;

/**
 * Classe permettant de modéliser un Modele de transaction de la base de donnée
 * @author Compact budget
 * @version 1.0
 * @since 1.0
 */
public class Modele_transaction {


    /**
     * ID du modele de transaction
     */
    public int id;
    /**
     * valeur du modele de transaction
     */
    public double valeur;
    /**
     * date du modele de transaction
     */
    public String date;
    /**
     * note du modele de transaction
     */
    public String note;
    /**
     * Utilisateur du modele de transaction
     */
    public Utilisateur user;
    /**
     * sous Catégorie du modele de transaction
     */
    public SousCategorie sousCategorie;
    /**
     * type transaction du modele de transaction (Depense/Revenu dans la version 1.0)
     */
    public int type_transaction_id;
    /**
     * recurrence du modele de transaction
     */
    public Recurrence recurrence ;

    Modele_transaction() {

    }

    /** Constructeur
     * @param valeur
     * @param date
     * @param note
     * @param user
     * @param sousCategorie
     * @param type_transaction_id
     * @param recurrence
     */
    public Modele_transaction(double valeur, String date, String note, Utilisateur user,SousCategorie sousCategorie,int type_transaction_id,Recurrence recurrence ){
        this.valeur = valeur;
        this.date = date;
        this.note = note;
        this.user = user;
        this.sousCategorie = sousCategorie;
        this.type_transaction_id = type_transaction_id;
        this.recurrence = recurrence;
    }

    /** Constructeur sans note
     * @param valeur
     * @param date
     * @param user
     * @param sousCategorie
     * @param type_transaction_id
     * @param recurrence
     */
    public Modele_transaction(double valeur, String date,Utilisateur user,SousCategorie sousCategorie,int type_transaction_id,Recurrence recurrence ){
        this(valeur,date,null,user,sousCategorie,type_transaction_id,recurrence);
    }

    /** Constructeur sans recurrence
     * @param valeur
     * @param date
     * @param note
     * @param user
     * @param sousCategorie
     * @param type_transaction_id
     */
    public Modele_transaction(double valeur, String date, String note, Utilisateur user,SousCategorie sousCategorie,int type_transaction_id ){
        this(valeur,date,note,user,sousCategorie,type_transaction_id,null);
    }

    /** Constructeur sans note et sans recurrence
     * @param valeur
     * @param date
     * @param user
     * @param sousCategorie
     * @param type_transaction_id
     */
    public Modele_transaction(double valeur, String date, Utilisateur user,SousCategorie sousCategorie,int type_transaction_id ){
        this(valeur,date,null,user,sousCategorie,type_transaction_id,null);
    }

}
