
package controllers.BDDpackage;


import java.awt.*;

/**
 * Classe permettant de modéliser une Notification de la base de donnée
 * @author Compact budget
 * @version 1.0
 * @since 1.0
 */
public class Notification {
    /**
     * Titre de la notif
     */
    public String titre;
    /**
     * message de la norif
     */
    public String message;

    Notification() {

    }

    /** Constructeur
     * @param titre
     * @param message
     */
    public Notification(String titre, String message){
        this.titre = titre;
        this.message = message;
    }


}
