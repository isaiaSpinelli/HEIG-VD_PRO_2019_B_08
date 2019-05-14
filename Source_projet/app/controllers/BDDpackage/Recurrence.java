package controllers.BDDpackage;

/**
 * Classe permettant de modéliser les Récurrences de la base de donnée
 * @author Compact budget
 * @version 1.0
 * @since 1.0
 */
public class Recurrence {

    private final int id;
    private final String name;

    Recurrence(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * @return ID de la recurrence
     */
    public int getId(){
        return id;
    }

    /**
     * @return le nom de la recurrence
     */
    public String getName() {return name;}

    
}
