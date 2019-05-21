
package controllers.BDDpackage;
import java.util.ArrayList;


import java.awt.*;

/**
 * Classe permettant de modéliser qqch
 * @author Compact budget
 * @version 1.0
 * @since 1.0
 */
public class MonthlyExpenseForCat {

    /**
     * monthlyExpense d'une catégorie
     */
    public ArrayList<MonthlyExpense> ListmonthlyExpense = new ArrayList<MonthlyExpense>();
    /**
     * ID de la catégorie
     */
    public int IDCat;


    /**
     * @param monthlyExpense
     * @param IDCat
     */
    public MonthlyExpenseForCat( ArrayList<MonthlyExpense> ListmonthlyExpense, int IDCat){
        this.ListmonthlyExpense = ListmonthlyExpense;
        this.IDCat = IDCat;
    }

}
