package controllers.BDDpackage;


// Pour afficher une Enum : java.util.Arrays.asList(Recurrence.values())

/**
 * Enum permettant de modéliser les Récurrences de la base de donnée
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

    public int getId(){
        return id;
    }

    public String getName() {return name;}

    
}
