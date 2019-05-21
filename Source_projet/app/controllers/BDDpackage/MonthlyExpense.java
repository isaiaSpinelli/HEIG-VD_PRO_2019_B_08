
package controllers.BDDpackage;


import java.awt.*;

/**
 * Classe permettant de modéliser qqch
 * @author Compact budget
 * @version 1.0
 * @since 1.0
 */
public class MonthlyExpense {
    /**
     * Nom
     */
    public String nameMonth;
    /**
     * Solde
     */
    public double solde;


    /**
     * @param nameMonth
     * @param solde
     */
    public MonthlyExpense(String nameMonth, double solde){
        this.nameMonth = nameMonth;
        this.solde = solde;
    }

}
