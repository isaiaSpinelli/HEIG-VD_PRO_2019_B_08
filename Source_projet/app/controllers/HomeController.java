package controllers;

import controllers.BDDpackage.BDD;
import controllers.BDDpackage.Categorie;
import controllers.BDDpackage.SousCategorie;
import controllers.BDDpackage.Utilisateur;
import controllers.BDDpackage.Pays;
import controllers.BDDpackage.Statut;
import controllers.BDDpackage.MonthlyExpense;
import controllers.BDDpackage.MonthlyExpenseForCat;
import play.mvc.*;
import play.data.DynamicForm;
import play.data.Form;
import play.data.Form.*;
import play.data.FormFactory;
import com.google.inject.Inject;
import org.mindrot.jbcrypt.BCrypt;
import java.util.*;
import java.util.HashSet;
import play.Configuration;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Cette classe permet de faire le hanlde entre les actions à faire en fonction des requêtes HTTP
 */
public class HomeController extends Controller {

    /**
     * Permet d'avoir une pool de connection partagé avec les users
     */
    public static BDD DB ;
    /**
     * Nom du paramètre ayant le username de la BDD
     */
    private static final String DB_USERNAME="db.default.username";
    /**
     * Nom du paramètre ayant le mot de passe de la BDD
     */
    private static final String DB_PASSWORD="db.default.password";
    /**
     * Nom du paramètre ayant l'URL de la BDD
     */
    private static final String DB_URL ="db.default.url";


    /**
     * Permet d'avoir accès à la configuration de la base de donnée
     */
    private final Configuration configuration;


    /**
     * Permet de récupèrer des informations d'une requête POST
     */
    private final FormFactory formFactory;
    /**
     * Permet d'afficher un message à un user
     */
    private String errorMessageLogin = "";


    /** Constructeur appelé a chaque lancement de l'app
     * @param formFactory   Permet de récupèrer des informations d'une requête POST
     * @param configuration Permet d'avoir accès à la configuration de la base de donnée
     */
    @Inject
    public HomeController(FormFactory formFactory, Configuration configuration) {
        this.configuration = configuration;
        this.formFactory = formFactory;
        // Récupère les config de la base de donnée
        String user = configuration.getString(DB_USERNAME);
        String passwd = configuration.getString(DB_PASSWORD);
        String url = configuration.getString(DB_URL);

        DB = new BDD(url, user, passwd);
    }


    /** Méthode appellé lors d'une requête GET sur /
     * Gère s'il y a une session valide
     * @return le page HTML à afficher
     */
    public Result index()  throws SQLException {

        if (session("userID") == null){
            session().clear();
            return ok( views.html.Login.render(errorMessageLogin,this.getUserSession()));
        } else {
            return ok(views.html.index.render("Compact Budget",this.getUserSession()));
        }
    }

    /** Prépare la page HTML pour les statistiques et contrôle la session
     * @return le page HTML à afficher
     */
    public Result Statistics()  throws SQLException {
        if (session("userID") == null){
            return ok( views.html.Login.render(errorMessageLogin,this.getUserSession()));
        } else {
            return ok(views.html.Statistics.render("stats", this.getUserSession(),1));
        }

    }

    /** Gère le login d'un user
     * @return le page HTML à afficher
     */
    public Result LoginSubmit() {

        DynamicForm form = formFactory.form().bindFromRequest();
        int valCo = DB.checkConnectionGetId(form.get("password"),form.get("username"));
        if(valCo != 0) {
            Utilisateur user = DB.UtilisateurByID(valCo);
            session("userName", user.nom);
            session("userID", Integer.toString(user.id));
            DB.check_recurrences(user.id);
        }
        errorMessageLogin = "Erreur, veuillez réessayer.";
        return redirect("/profil");

    }

    /** Permet de récupère en tout temps l'id de la session en cours
     * @return l'id du user en cours
     */
    public static int getIdSession(){

        int id = 0;
        String idS = "";
        if (session("userID") == null){

        } else {
            idS = session("userID");
            id = Integer.parseInt(idS);
        }
        return id;
    }

    /** Permet de récupèrer les informations du user connecté
     * @return le user connecté à la session
     */
    public static Utilisateur getUserSession(){
        return DB.UtilisateurByID(getIdSession());
    }

    /** Gestion d'un nouvel utilisateur
     * @return le page HTML à afficher
     */
    public Result RegisterSubmit(){
        DynamicForm form = formFactory.form().bindFromRequest();

        //Gestion erreur
        boolean error = false;
        ArrayList<String> messageError = new ArrayList<String>();

        //Gestion checkbox option
        int opt = (form.get("Option") == null || form.get("Option").length() == 0) ? 1 : 2;
        //return ok(views.html.index.render(Integer.toString(opt)));

        //Controle pays
        if(Integer.parseInt(form.get("pays")) == 0)
        {
            messageError.add("Erreur, veuillez choisir un pays\n");
            error = true;
        }
        if(Integer.parseInt(form.get("statut")) == 0)
        {
            messageError.add("Erreur, veuillez choisir un statut\n");
            error = true;
        }
        if(form.get("surname").length() == 0)
        {
            messageError.add("Erreur, veuillez entrer un prénom\n");
            error = true;
        }
        if(form.get("name").length() == 0)
        {
            messageError.add("Erreur, veuillez entrer un nom\n");
            error = true;
        }
        if(form.get("username").length() == 0)
        {
            messageError.add("Erreur, veuillez entrer un username\n");
            error = true;
        }
        if(form.get("email").length() == 0)
        {
            messageError.add("Erreur, veuillez entrer un e-mail\n");
            error = true;
        }
        if(form.get("password").length() == 0)
        {
            messageError.add("Erreur, veuillez entrer un mot de passe\n");
            error = true;
        }
        if(form.get("anniversaire").length() == 0)
        {
            messageError.add("Erreur, veuillez entrer une date de naissance\n");
            error = true;
        }

        double solde;
        if(form.get("solde").length() == 0)
        {
            solde = 0;
        }
        else
        {
            if(Double.parseDouble(form.get("solde")) >= 0)
            {
                solde = Double.parseDouble(form.get("solde"));
            }
            else
            {
                //Gestion faille HTML
                solde = 0;
            }
        }


        int idResult = 0;
        String nom = form.get("name");
        if(!error)
        {
            idResult = DB.addUser(form.get("surname"),nom,form.get("email"),form.get("username")
             , BCrypt.hashpw(form.get("password"), BCrypt.gensalt()), form.get("genre"),form.get("anniversaire")
             , Integer.parseInt(form.get("statut"))
             , Integer.parseInt(form.get("pays")), opt,solde);
        }
        //Recuperation pays pour affichage
        ArrayList<Pays> pays = new ArrayList<Pays>();
        pays = DB.get_Pays();

        //Recuperation statut pour affichage
        ArrayList<Statut> statut = new ArrayList<Statut>();
        statut = DB.get_Statut();
        if(idResult != 0) {
            session("userName", nom);
            session("userID", Integer.toString(idResult));
            //Utilisateur user = DB.UtilisateurByID(idResult);
            return ok( views.html.utilisateur.render( this.getUserSession(),0,"") );
        }
        else if(!error)
        {
            messageError.add("Erreur, veuillez choisir un autre username ou un autre email\n");
        }

        return ok(views.html.register.render(pays,statut,messageError,this.getUserSession()));

    }

    /** Gestion affichage submit
     * @return le page HTML à afficher
     */
    public Result Register(){

        //Recuperation pays pour affichage
        ArrayList<Pays> pays = new ArrayList<Pays>();
        pays = DB.get_Pays();

        //Recuperation statut pour affichage
        ArrayList<Statut> statut = new ArrayList<Statut>();
        statut = DB.get_Statut();

        return ok(views.html.register.render(pays,statut,null,this.getUserSession()));
    }

    /** Affiche le profil du user
     * @return le page HTML à afficher
     */
    public Result Profil() {
        // Get user_id
        if(this.getIdSession() == 0)
        {
            session().clear();
            return ok( views.html.Login.render(errorMessageLogin,this.getUserSession()));
        }
        else
        {
            return ok( views.html.utilisateur.render( this.getUserSession(),0,"") );
        }

    }

    /** Gestion de la déconnection
     * @return le page HTML à afficher
     */
    public Result Disconnect(){
        session().clear();
        errorMessageLogin = "";
        return redirect("/profil");

    }

    /** Affiche la page des catégories
     * @return le page HTML à afficher
     */
    public Result Categorie() {

        // Si il n'est pas loggé
        if( this.getIdSession() == 0)
        {   // Affiche la page de logine
            return ok( views.html.Login.render(errorMessageLogin,this.getUserSession()));
        }
        else {
            // Sinon, affiche les catégorie
            ArrayList<Categorie> listCategorie = new ArrayList<Categorie>();
            listCategorie = DB.get_Categories();

            return ok(views.html.Categorie.render(listCategorie, this.getUserSession()));
        }
    }

    /** Permet d'ajouter une sous catégorie à la base de donnée
     * @param defaultSelect la séléction par défaut de la sous catégorie
     * @return le page HTML à afficher
     */
   public Result sousCategorie(String defaultSelect ) {
        // Envoie la liste de toute les catégories au HTML
       ArrayList<Categorie> listCategorie = new ArrayList<Categorie>();
       listCategorie = DB.get_Categories();

        return ok( views.html.sousCategorie.render( listCategorie, defaultSelect,this.getUserSession()) );
    }

    /** Permet d'ajouter une sous catégorie
     * @return le page HTML à afficher
     */
    public Result addSousCategorie() {

        DynamicForm form = formFactory.form().bindFromRequest();
        // Récupère les informations depuis une requete POST
        String nom = form.get("nom");
        if (nom == "")
        {
            return redirect("/");
        }
        int categorie_id = Integer.parseInt(form.get("categorie_id"));
        int idUser = Integer.parseInt(form.get("idUser"));
        if (idUser == 0)
        {
            return redirect("/profil");
        }

        int alerte = 2;
        String message = "Insertion successful ";

        // Recherche la categorie selectionné
        Categorie categorieChoisi = DB.CategorieByID(categorie_id);
        // Cree la sous catégore souhaitée
        SousCategorie sousCategorie = new SousCategorie(nom, categorieChoisi );

        // Ajout d'une notif 
        if (!DB.insert_Sous_categorie( sousCategorie, idUser)){
            alerte = 1;
            message = "Error: insertion failed !";
        }

        // Retour a la page souhaitée (profil)
        return ok( views.html.utilisateur.render( DB.UtilisateurByID( this.getIdSession() ),alerte,message) );
    }

    // Gestion des options
    /** Afiche le profil du user
     * @return le page HTML à afficher
     */
    public Result ModifOptions()
    {
        if(this.getIdSession() == 0)
        {
            return redirect("/profil");
        }
        else
        {
            return ok(views.html.options.render(this.getUserSession().getOptions(),this.getUserSession()));
        }
    }

    /** Permet de modifier les options du user
     * @param Option Nouvelle option
     * @return le page HTML à afficher
     */
    public Result ModifOptionsSub(String Option)
    {
        if(this.getIdSession() == 0)
        {
            return ok(views.html.index.render("Compact Budget",this.getUserSession()));
        }
        else
        {
            boolean ret = DB.updateOptionUser(this.getIdSession(), Integer.parseInt(Option));
            if(ret)
            {
                int valco = this.getIdSession();
                return redirect("/profil");
            }
            else
            {
               return redirect("/options");
            }
        }
    }

    /** Permet de modifier le profil du user
     * @return le page HTML à afficher
     */
    public Result ModifProfile() {
        if (this.getIdSession() == 0) {
            session().clear();
            return ok(views.html.index.render("Compact Budget",this.getUserSession()));
        } else {
            DynamicForm form = formFactory.form().bindFromRequest();

            //Gestion erreur
            boolean error = false;
            String erreurMes = "Erreur lors de la modification du profil, veuillez réessayer.";

            if (form.get("surname").length() == 0 || form.get("name").length() == 0 || form.get("username").length() == 0 ||
                    form.get("email").length() == 0 || form.get("anniversaire").length() == 0) {
                error = true;
            }

            if(!error)
            {
                Boolean genreVal = Integer.parseInt(form.get("genre")) == 1 ? true : false;
                boolean ret = DB.updateUser(this.getIdSession(),form.get("surname"),form.get("name"),form.get("email"),form.get("username")
                        , genreVal,form.get("anniversaire")
                        , Integer.parseInt(form.get("statut"))
                        , Integer.parseInt(form.get("pays")));
                if(ret)
                {
                    return redirect("/profil");
                }
                else
                {

                    return ok( views.html.utilisateur.render( this.getUserSession(),1,erreurMes));
                }
            }

            return ok( views.html.utilisateur.render( this.getUserSession(),1,erreurMes));
        }
    }

    /** Ajout d'un revenu au user
     * @return le page HTML à afficher
     */
    public Result AddExpense(){

        return ModelTrans(1);
    }

    /** Ajoute une dépense au user
     * @return le page HTML à afficher
     */
    public Result AddIncome(){

        return ModelTrans(2);
    }


    /** Permet d'ajouter un modèle de transaction
     * @param id_trans      ID du type de transaction (Dépense/Revenu)
     * @return le page HTML à afficher
     */
    private Result ModelTrans(int id_trans){
        DynamicForm form = formFactory.form().bindFromRequest();

        double amount = Double.parseDouble(form.get("amount"));
        if (amount <= 0)
        {
            return redirect("/");
        }


        // Gestion de la sous catégorie en fonction du nouveau mouvement
        int idSubCat ;
        if (form.get("sous-categorie") == null){
            idSubCat = DB.getSousCategorieID("Sans catégorisation");
        } else {
            idSubCat = Integer.parseInt(form.get("sous-categorie"));
        }
        if(id_trans == 2 && idSubCat == 0 ){
            idSubCat = DB.getSousCategorieID("Revenu");
        }

        int userId = this.getIdSession();
        int recId = Integer.parseInt(form.get("recurrence"));
        String note = form.get("note");

        int result = DB.addMovement(userId,amount,idSubCat,recId,note,id_trans);

        return redirect("/");

    }

    /** Affiche la page de l'historique
     * @return le page HTML à afficher
     */
    public Result Historique()
    {
        if(this.getIdSession() == 0)
        {
            return redirect("/profil");
        }
        return ok(views.html.historique.render("Historique",this.getUserSession(),1));
    }

    /** Permet d'afficher l'historiques d'une catégorie spécifique
     * @param cat ID de la catégorie souhaitée
     * @return le page HTML à afficher
     */
    public Result historiqueCat(int cat)
    {
        if(this.getIdSession() == 0)
        {
            return redirect("/profil");
        }
        return ok(views.html.historique.render("Historique",this.getUserSession(),cat));
    }

    /** Permet de créer le PDF de l'historique d'un utilisateur
     * @return le page HTML à afficher
     */
    public Result creePDF()
    {
        DynamicForm form = formFactory.form().bindFromRequest();
        // Récupère les informations depuis une requete POST
        int idUser = Integer.parseInt(form.get("idUser"));
        // S'il n'est pas connecté
        if (idUser == 0 || this.getIdSession() == 0)
        {
            return redirect("/");
        }


        // Récupère l'utilisateur
        Utilisateur user = DB.UtilisateurByID(idUser);
        // Crée le PDF
        PDF pdf = new PDF(user);

        int alerte = 1;
        String message = "Fail PDF !";
        if ( pdf.cree() ){
            alerte = 2;
            message = "Successfull PDF !";
        }

        // Retourne sur la page des Historiques
        return ok( views.html.utilisateur.render( user,alerte,message) );
    }

    /** Ajout une nouvelle limite au user
     * @return le page HTML à afficher
     */
    public Result AddLimit()
    {
        DynamicForm form = formFactory.form().bindFromRequest();

        double amount = Double.parseDouble(form.get("amount"));
        if (amount <= 0)
        {
            return redirect("/");
        }
        int userId = this.getIdSession();
        int recId = Integer.parseInt(form.get("recurrence"));
        int catId = Integer.parseInt(form.get("categorie"));

        int result = DB.addLimit(amount,userId,recId,0,catId);
        return redirect("/");
    }

    /** Affiche la page des notifications du user
     * @return le page HTML à afficher
     */
    public Result Notification()
    {
        if(this.getIdSession() == 0)
        {
            return redirect("/profil");
        }

        return ok( views.html.notification.render("Notif",this.getUserSession()));
    }
}
