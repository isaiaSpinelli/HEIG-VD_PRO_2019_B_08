/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers.BDDpackage;


/**
 * Classe permettant de modéliser une sous catégorie de la base de donnée
 * @author Compact budget
 * @version 1.0
 * @since 1.0
 */
public class SousCategorie {

    /**
     * Nom de la sous catégorie
     */
    public String nom;
    /**
     * id de la sous catégorie
     */
    public Integer id = null;
    /**
     * Categorie associé à la sous catégorie
     */
    public Categorie categorie;
    /**
     * type de la sous catégorie (visibilité)
     */
    public Boolean is_global;

    /** Constructeur complet
     * @param id        : ID de la sous catégorie
     * @param nom       : nom de la sous catégorie
     * @param categorie : catégorie associée
     * @param is_global : Visibilité de la sous catégorie
     */
    SousCategorie(Integer id, String nom, Categorie categorie, Boolean is_global){
        this.id = id;
        this.nom = nom;
        this.categorie = categorie;
        this.is_global = is_global;
    }

    /** Constructeur
     * @param nom       : nom de la sous catégorie
     * @param categorie : catégorie associée
     */
    public SousCategorie(String nom, Categorie categorie){
        this(null,nom,categorie,true);
    }

    /** Constructeur sans catégorie (pour manipulation)
     * @param id    : ID de la sous catégorie
     * @param nom   : nom de la sous catégorie
     */
    SousCategorie(int id, String nom){
        this(id,nom,null,true);
    }


}
