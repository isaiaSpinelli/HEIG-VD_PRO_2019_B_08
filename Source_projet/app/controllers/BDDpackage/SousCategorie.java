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

    public String nom;
    public Integer id = null;
    public Categorie categorie;
    public Boolean is_global;

    SousCategorie(Integer id, String nom, Categorie categorie, Boolean is_global){
        this.id = id;
        this.nom = nom;
        this.categorie = categorie;
        this.is_global = is_global;
    }

    public SousCategorie(String nom, Categorie categorie){
        this(null,nom,categorie,true);
    }

    SousCategorie(int id, String nom){
        this(id,nom,null,true);
    }


}
