/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers.BDDpackage;

import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Types;
import java.sql.CallableStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mindrot.jbcrypt.BCrypt;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import javax.sql.DataSource;
import play.db.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Calendar;
import controllers.BDDpackage.MonthlyExpense;



/** Base de donnée (Connections et Requêtes)
 * @author Compact budget
 * @version 1.0
 * @since 1.0
 */
public class BDD {
    /**
     * l'url de la base de donnée
     */
    private static String url = "jdbc:postgresql://127.0.0.1:5432/BD_Budget";
    /**
     * le nom d'utilisateur de la base de donnée
     */
    private static String user = "postgres";
    /**
     * le mot de passe de la base de donnée (ne devrait pas être en clair)
     */
    private static String password = "123456789";
    /**
     * le driver de postgresql
     */
    private static String driver = "org.postgresql.Driver";

    /**
     * Le pool des connections pour la base de donnée
     */
    private static DataSource pool;
    /**
     * la configuration pour le pool
     */
    private static HikariConfig config = new HikariConfig();

    /**
     * Retourne le nom de l'utilisateur
     * @return le nom de l'utilisateur de la base de donnée (String)
     */
    public String getUser(){
        return user;
    }

    /**
     * Constructeur par defaut
     */
    public BDD(){
        this.Connection();
    }

    /**
     * Constructeur en donnant les config de la BDD
     * @param url       url de la base de donnée
     * @param user      user de la base de donnée
     * @param password  password de la base de donnée
     */
    public BDD(String url, String user, String password){
        this.url = url;
        this.user = user;
        this.password = password;

        this.Connection();
    }

    /**
     * Met en place le pooling des connections à la base de donnée
     */
    private void Connection(){


        // Configure which instance and what database user to connect with.
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");


        // [START cloud_sql_mysql_servlet_limit]
        // maximumPoolSize limits the total number of concurrent connections this pool will keep. Ideal
        // values for this setting are highly variable on app design, infrastructure, and database.
        config.setMaximumPoolSize(300);
        // minimumIdle is the minimum number of idle connections Hikari maintains in the pool.
        // Additional connections will be established to meet this value unless the pool is full.
        config.setMinimumIdle(1);
        // [END cloud_sql_mysql_servlet_limit]

        // [START cloud_sql_mysql_servlet_timeout]
        // setConnectionTimeout is the maximum number of milliseconds to wait for a connection checkout.
        // Any attempt to retrieve a connection from this pool that exceeds the set limit will throw an
        // SQLException.
        config.setConnectionTimeout(10000); // 10 seconds
        // idleTimeout is the maximum amount of time a connection can sit in the pool. Connections that
        // sit idle for this many milliseconds are retried if minimumIdle is exceeded.
        config.setIdleTimeout(600000); // 10 minutes
        // [END cloud_sql_mysql_servlet_timeout]

        // [START cloud_sql_mysql_servlet_backoff]
        // Hikari automatically delays between failed connection attempts, eventually reaching a
        // maximum delay of `connectionTimeout / 2` between attempts.
        // [END cloud_sql_mysql_servlet_backoff]

        // [START cloud_sql_mysql_servlet_lifetime]
        // maxLifetime is the maximum possible lifetime of a connection in the pool. Connections that
        // live longer than this many milliseconds will be closed and reestablished between uses. This
        // value should be several minutes shorter than the database's timeout value to avoid unexpected
        // terminations.
        config.setMaxLifetime(1800000); // 30 minutes
        // [END cloud_sql_mysql_servlet_lifetime]

        // [END_EXCLUDE]

        // Initialize the connection pool using the configuration object.
        pool = new HikariDataSource(config);
    }

    /** Retourne le pool des connections JDBC
     * @return le pool (DataSource)
     */
    public static DataSource getDataSource(){
        return pool;
    }

    /** Récupère une connection à la base de donnée
     * @return une connection à la base de donnée
     */
    public static Connection getConnection(){
        Connection conn = null;
        try {
            conn = pool.getConnection();
        } catch (Exception e) {
            System.out.println("Error connection !");
            System.out.println(e.getMessage());
        }
        return conn;
    }


    /**
     * Permet de convertir le nom d'une table simple avec le nom exacte
     *
     * @param TableName Nom de la table
     *
     * @return Nom de la table au bon format
     */
    private String table(String TableName) {
        return "public.\"" + TableName + "\" ";
    }
    /**
     * Permet de convertir l'id d'un pays en string
     *
     * @param PaysID Id du pays
     * @return Nom du pays
     */
    private String paysString(int PaysID) {
        String pays = "";
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;

        try {
            String SQL = "SELECT * "
                    + "FROM " + table("pays")
                    + "WHERE pays_id = ?";

            conn = getConnection();
            pstmt = conn.prepareStatement(SQL);

            pstmt.setInt(1, PaysID);
            rs = pstmt.executeQuery();
            rs.next();
            pays = rs.getString("nom");

        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { pstmt.close(); } catch (Exception e) { /* ignored */ }
            try { conn.close(); } catch (Exception e) { /* ignored */ }
        }

        return pays;

    }


    /**
     * Permet de convertir l'id des options en string
     *
     * @param OptionsID Id de l'option
     * @return Tableau des options
     */
    private ArrayList<Boolean> optionsString(int OptionsID) {
        ArrayList<Boolean> options = new ArrayList<Boolean>();

        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;

        try {
            String SQL = "SELECT * "
                    + "FROM " + table("options")
                    + " WHERE options_id = ? ;";

            conn = getConnection();
            pstmt = conn.prepareStatement(SQL);

            pstmt.setInt(1, OptionsID);
            rs = pstmt.executeQuery();

            int i = 2;

            while (rs.next()) {
                options.add( rs.getBoolean(i) );
                i++;
            }

        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { pstmt.close(); } catch (Exception e) { /* ignored */ }
            try { conn.close(); } catch (Exception e) { /* ignored */ }
        }

        return options;

    }

    /**
     * Permet de mettre à jour le profil d'un utilisateur, sans le mot de passe, les options ou le solde
     * @param userId ID de l'utilisateur à modifier
     * @param prenom prénom de l'utilisateur
     * @param nom nom de l'utilisateur
     * @param email email de l'utilisateur
     * @param pseudo pseudo de l'utilisateur
     * @param genre genre de l'utilisateur, true pour homme, false pour femme
     * @param anniversaire date de naissance de l'utilisateur
     * @param statut_id id du statut de l'utilisateur
     * @param pays_id id du pays de l'utilisateur
     * @return boolean true si update à marcher, false sinon
     */
    public boolean updateUser(int userId, String prenom,String nom,String email,String pseudo,Boolean genre,
                              String anniversaire,int statut_id, int pays_id){
        if(!checkUniqueUserWithId(email,pseudo,userId))
        {
            return false;
        }
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            String sql = "UPDATE " + table("utilisateur") + " set prenom=?, nom=?,email=?," +
                    "pseudo=?,genre=?,anniversaire=?,statut_id=?,pays_id=? where utilisateur_id=? ; ";

            Date dateAnniversaire = null;
            java.sql.Date sDate = null;
            try {
                dateAnniversaire = new SimpleDateFormat("yyyy-MM-dd").parse(anniversaire);
                sDate = new java.sql.Date(dateAnniversaire.getTime());
            }
            catch (Exception e)
            {
                Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, e);
                return false;
            }
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, prenom);
            pstmt.setString(2, nom);
            pstmt.setString(3, email);
            pstmt.setString(4, pseudo);
            pstmt.setBoolean(5, genre);
            pstmt.setDate(6, sDate);
            pstmt.setInt(7, statut_id);
            pstmt.setInt(8, pays_id);
            pstmt.setInt(9, userId);

            int count = pstmt.executeUpdate();
            return (count > 0);
        }
        catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try { pstmt.close(); } catch (Exception e) { /* ignored */ }
            try { conn.close(); } catch (Exception e) { /* ignored */ }
        }
        return false;

    }

    /**
     * Met à jour les options de l'utilisateur
     * @param userId ID de l'utilisateur à modifier
     * @param OptionId Id de l'option à modifier
     * @return true si l'option à été modifié dans l'utilisateur, false sinon
     */
    public boolean updateOptionUser(int userId, int OptionId)
    {

        try {
            String sql = "UPDATE " + table("utilisateur") + " set options_id=? where utilisateur_id=? ; ";

            PreparedStatement pstmt = getConnection().prepareStatement(sql);

            pstmt.setInt(1, OptionId);
            pstmt.setInt(2,userId);

            int count = pstmt.executeUpdate();
            return (count > 0);


        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Permet de convertir l'acronyme du genre en String
     *
     * @param genre acronyme du genre
     * @return String du genre
     */
    private String genreString(String genre) {
        if (genre == null)
            return "Pas renseigné" ;
        else if(genre.equals("t"))
            return "Homme";
        else
            return "Femme";

    }

    /**
     * Permet de convertir l'id du statut en string
     *
     * @param statutID l'ID du statut
     * @return Nom du statut
     */
    private String statutString(int statutID) {
        String Statut = "";
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;

        try {
            String SQL = "SELECT * "
                    + "FROM " + table("statut")
                    + "WHERE statut_id = ?";

            conn = getConnection();
            pstmt = conn.prepareStatement(SQL);

            pstmt.setInt(1, statutID);
            rs = pstmt.executeQuery();
            rs.next();
            Statut = rs.getString("nom");

        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { pstmt.close(); } catch (Exception e) { /* ignored */ }
            try { conn.close(); } catch (Exception e) { /* ignored */ }
        }

        return Statut;

    }

    /**
     * Cherche un utilisateur en fonciton de son ID
     *
     * @param UtilisateurID l'id de l'utilisateur
     * @return l'Utilisateur trouvé, ou null si pas trouvé
     */
    public Utilisateur UtilisateurByID(int UtilisateurID) {

        Utilisateur user = null;
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;

        try {
            String SQL = "SELECT * "
                    + "FROM " + table("utilisateur")
                    + "WHERE utilisateur_id = ?";

            conn = getConnection();
            pstmt = conn.prepareStatement(SQL);

            pstmt.setInt(1, UtilisateurID);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                user = new Utilisateur( rs.getInt("utilisateur_id"),
                        rs.getString("prenom"),
                        rs.getString("nom"),
                        rs.getString("email"),
                        rs.getString("pseudo"),
                        genreString(rs.getString("genre")),
                        rs.getString("anniversaire"),
                        rs.getString("cree_a"),
                        rs.getString("droit_id"),
                        statutString(rs.getInt("statut_id")),
                        paysString(rs.getInt("pays_id")),
                        optionsString(rs.getInt("options_id")),
                        rs.getDouble("solde"));

            }

        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { pstmt.close(); } catch (Exception e) { /* ignored */ }
            try { conn.close(); } catch (Exception e) { /* ignored */ }
        }

        return user;

    }


    /** Permet d'avoir une liste des utilisateurs de la base de donnée
     * @return Une liste des utilisateurs enregistré dans la base de donnée
     */
    private ArrayList<String> display_Utilisateurs(){

        // HashMap<String, String> users = new HashMap();
        // users.put(url, url)

        ArrayList<String> uniqueValues = new ArrayList<>();
        Connection conn = null;
        ResultSet rs = null;
        Statement st = null;

        try {
            conn = getConnection();
            st = conn.createStatement();
            rs = st.executeQuery("SELECT * FROM " + table("utilisateur") );


            while (rs.next()) {
                uniqueValues.add(rs.getString("pseudo"));
                uniqueValues.add(rs.getString("email"));

            }
        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { st.close(); } catch (Exception e) { /* ignored */ }
            try { conn.close(); } catch (Exception e) { /* ignored */ }
        }

        return uniqueValues;
    }


    /** Ajoute un user dans la base de donnée
     * @param prenom        prenom de l'utilisateur
     * @param nom           nom de l'utilisateur
     * @param email         email de l'utilisateur
     * @param pseudo        pseudo de l'utilisateur
     * @param mdp           mdp de l'utilisateur (hashé)
     * @param genre         genre de l'utilisateur
     * @param anniversaire  anniversaire de l'utilisateur
     * @param statut        statut de l'utilisateur
     * @param Pays          Pays de l'utilisateur
     * @param Option        Option de l'utilisateur
     * @param solde         solde de l'utilisateur
     * @return
     */
    public int addUser(String prenom, String nom, String email, String pseudo, String mdp,
                       String genre, String anniversaire,int statut,int Pays,int Option,double solde){
        boolean genreVal = Integer.parseInt(genre) == 1 ? true : false;
        return insert_Utilisateurs(prenom,nom,email,pseudo,mdp,genreVal,anniversaire,statut,Pays,Option,solde);
    }


    /** Permet de verifier la validiter du nouveau utilisateur
     * @param email
     * @param pseudo
     * @return
     */
    private boolean checkUniqueUser(String email, String pseudo){

        ArrayList<String> uniqueValues = display_Utilisateurs();

        if (uniqueValues.contains(email) || uniqueValues.contains(pseudo))
            return false;

        return true;
    }


    /** Permet de verifier la validité d'une modification de profil (Nom et email)
     * @param email
     * @param pseudo
     * @param userId
     * @return le résultat de la validation
     */
    private boolean checkUniqueUserWithId(String email, String pseudo, int userId) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;

        String sql = "Select * FROM " + table("utilisateur") + " WHERE (email=? OR pseudo=?) AND utilisateur_id!=?;";
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, pseudo);
            pstmt.setInt(3, userId);
            rs = pstmt.executeQuery();
            return !rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { pstmt.close(); } catch (Exception e) { /* ignored */ }
            try { conn.close(); } catch (Exception e) { /* ignored */ }
        }
        return false;


    }


    /** Permet d'insérer un nouveau utilisateur à la BDD
     * @param prenom
     * @param nom
     * @param email
     * @param pseudo
     * @param mdp
     * @param genre
     * @param anniversaire
     * @param statut_id
     * @param pays_id
     * @param options_id
     * @param solde         Solde de base du user
     * @return retourne l'ID du user ou 0 en cas d'echec
     */
    private int insert_Utilisateurs(String prenom, String nom,String email,String pseudo,String mdp,Boolean genre,String anniversaire,int statut_id, int pays_id, int options_id, double solde){
        int droit_id = 2;
        int ok = 0;

        try {

            if ( !checkUniqueUser(email,pseudo))
                return ok;


            String SQL = "INSERT INTO "
                    + table("utilisateur")
                    + "(prenom, nom, email, pseudo, mdp, genre, anniversaire, droit_id, statut_id, pays_id, options_id, solde   ) "
                    + "VALUES "
                    + "('" + prenom +"','"+ nom +"','"+ email +"','"+ pseudo +"','"+ mdp +"',"
                    + genre.toString() +",'"+ anniversaire +"',"+ droit_id +","+ statut_id +","+ pays_id +","+ options_id + ", " +solde +" )" +
                    " RETURNING utilisateur_id;";

            Statement st = getConnection().createStatement();
            st.executeUpdate(SQL,Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = st.getGeneratedKeys();
            if (rs.next()){
                ok = rs.getInt(1);
            }


        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ok;
    }


    /** Controle la connection et récupère l'id
     * @param passwd    mot de passe
     * @param userN     Nom de l'utilisateur
     * @return l'id du user ou 0 en cas d'echec
     */
    public int checkConnectionGetId(String passwd, String userN){

        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;


        try {
            String SQL = "SELECT * "
                    + "FROM " + table("utilisateur")
                    + "WHERE pseudo = ?;";

            conn = getConnection();
            pstmt = conn.prepareStatement(SQL);

            pstmt.setString(1, userN);

            rs = pstmt.executeQuery();

            if(rs.next())
            {
                //return rs.getInt("utilisateur_id");
                if(BCrypt.checkpw(passwd, rs.getString("mdp")))
                {
                    return rs.getInt("utilisateur_id");
                }
                return 0;
            }
            else
            {
                return 0;
            }

        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { pstmt.close(); } catch (Exception e) { /* ignored */ }
            try { conn.close(); } catch (Exception e) { /* ignored */ }
        }
        return 0;
    }


    /** Récupère tous les pays
     * @return tous les pays
     */
    public ArrayList<Pays> get_Pays(){

        ArrayList<Pays> listPays = new ArrayList<Pays>();
        Connection conn = null;
        ResultSet rs = null;
        Statement st = null;

        try {
            conn = getConnection();
            st = conn.createStatement();
            rs = st.executeQuery("SELECT * FROM " + table("pays") );

            while (rs.next()) {
                // Gère pas encore la couleur a voir !!
                Pays pays = new Pays( rs.getInt("pays_id"),rs.getString("nom") );
                listPays.add(pays);

            }

            rs.close();
            st.close();
        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { st.close(); } catch (Exception e) { /* ignored */ }
            try { conn.close(); } catch (Exception e) { /* ignored */ }
        }

        return listPays;
    }


    /** Récupère tous les status
     * @return tous les status
     */
    public ArrayList<Statut> get_Statut(){

        ArrayList<Statut> listStatut = new ArrayList<Statut>();
        Connection conn = null;
        ResultSet rs = null;
        Statement st = null;

        try {
            conn = getConnection();
            st = conn.createStatement();
            rs = st.executeQuery("SELECT * FROM " + table("statut") );

            while (rs.next()) {
                // Gère pas encore la couleur a voir !!
                Statut statut = new Statut( rs.getInt("statut_id"),rs.getString("nom") );
                listStatut.add(statut);

            }

            rs.close();
            st.close();
        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { st.close(); } catch (Exception e) { /* ignored */ }
            try { conn.close(); } catch (Exception e) { /* ignored */ }
        }

        return listStatut;
    }

    /** Récupère toutes les catégories de la base de donnée !!!Existe deja sous Get_AllCateogire (un truc comme ca)
     * @return une liste des catégories de la base de donnée
     */
    public ArrayList<Categorie> get_Categories(){

        ArrayList<Categorie> listCategorie = new ArrayList<>();
        Connection conn = null;
        ResultSet rs = null;
        Statement st = null;

        try {
            conn = getConnection();
            st = conn.createStatement();
            rs = st.executeQuery("SELECT * FROM " + table("categorie") );

            while (rs.next()) {
                // Gère pas encore la couleur a voir !!
                Categorie categorie = new Categorie( rs.getInt("categorie_id"),rs.getString("nom") );
                listCategorie.add(categorie);

            }
        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { st.close(); } catch (Exception e) { /* ignored */ }
            try { conn.close(); } catch (Exception e) { /* ignored */ }
        }

        return listCategorie;
    }


    /** Cherche une catégorie en fonction de son ID
     * @param CategorieID  ID de la catégorie cherchée
     * @return la catégorie trouvée
     */
    public Categorie CategorieByID(int CategorieID) {
        Categorie categorie = null;
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;

        try {
            String SQL = "SELECT * "
                    + "FROM " + table("categorie")
                    + " WHERE categorie_id = ?";

            conn = getConnection();
            pstmt = conn.prepareStatement(SQL);
            pstmt.setInt(1, CategorieID);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                categorie = new Categorie(rs.getInt(1),rs.getString("nom"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { pstmt.close(); } catch (Exception e) { /* ignored */ }
            try { conn.close(); } catch (Exception e) { /* ignored */ }
        }
        return categorie;
    }

    /** Cherche une catégorie en fonction de son nom
     * @param nom   Nom de la catégorie cherchée
     * @return la catégorie trouvée
     */
    public Categorie get_Categorie(String nom) {
        Categorie categorie = null;
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;

        try {
            String SQL = "SELECT * "
                    + "FROM " + table("categorie")
                    + " WHERE nom = ? ";

            conn = getConnection();
            pstmt = conn.prepareStatement(SQL);

            pstmt.setString(1, nom);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                categorie = new Categorie( rs.getInt(1),rs.getString("nom"));

            }
        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { pstmt.close(); } catch (Exception e) { /* ignored */ }
            try { conn.close(); } catch (Exception e) { /* ignored */ }
        }

        return categorie;

    }

    /** Retourne la liste des sous catégorie d'une catégorie en fonction de son ID
     * @param categorie_id      ID de la catégorie
     * @return la liste des sous catégorie de la catégorie
     */
    public ArrayList<SousCategorie> get_Sous_categorie(int categorie_id) {

        ArrayList<SousCategorie> listSousCategorie = new ArrayList<>();
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement st = null;

        try {
            conn = getConnection();
            st = conn.prepareStatement("SELECT * FROM " + table("sous_categorie") + " WHERE categorie_id = ?");
            st.setInt(1, categorie_id);
            rs = st.executeQuery();

            Categorie categorie = CategorieByID(categorie_id);

            while (rs.next())
            {
                SousCategorie sousCat = new SousCategorie(rs.getInt(1),rs.getString("nom"),categorie,rs.getBoolean("is_global") );
                listSousCategorie.add(sousCat);
            }

        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { st.close(); } catch (Exception e) { /* ignored */ }
            try { conn.close(); } catch (Exception e) { /* ignored */ }
        }

        return listSousCategorie;
    }

    /** Verifier la validiter d'une nouvelle sous catégorie
     * @param nom           nom de la new sous catégorie
     * @param categorie_id  ID de la new sous catégorie
     * @return retourne true si la sous catégorie est valide
     */
    private boolean checkUniqueSousCategorie(String nom, int categorie_id){

        ArrayList<SousCategorie> uniqueValues = get_Sous_categorie(categorie_id);

        for(SousCategorie sousCat : uniqueValues){
            if (sousCat.nom == nom)
                return false;
        }


        return true;
    }


    /** Insérer une nouvelle Sous_categorie à la BDD
     * @param sousCategorie Sous catégorie à insérer
     * @param idUser        id du user qui insère la sous catégorie
     * @return retourne true s'il n'y a pas eu de probleme, false autrement
     */
    public boolean insert_Sous_categorie(SousCategorie sousCategorie, int idUser){
        boolean ok = false;
        int idSousCat = 0 ;

        try {

            int idCat = sousCategorie.categorie.id;
            if ( !checkUniqueSousCategorie(sousCategorie.nom, idCat))
                return ok;


            String SQL = "INSERT INTO "
                    + table("sous_categorie")
                    + "(nom, categorie_id, is_global) "
                    + "VALUES "
                    + "('" + sousCategorie.nom +"'," + sousCategorie.categorie.id + ", false );";

            Statement st = getConnection().createStatement();

            st.executeUpdate(SQL,Statement.RETURN_GENERATED_KEYS);
            // Récupère l'id de la nouvelle sous catégorie
            ResultSet rs = st.getGeneratedKeys();
            if (rs.next()){
                idSousCat = rs.getInt(1);

                ok = true;
                updateSousCatPerso(idSousCat, idUser);
            }

        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ok;
    }

    /**
     * Renvoie l'ID d'une sous categorie suivant son nom
     *
     * @param sousCategorie nom de la sous categorie
     * @return retourne 0 s'il n'y a pas eu de probleme, -1 autrement
     */
    public int getSousCategorieID(String sousCategorie){

        int status = 0;
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;

        try {
            String SQL = "SELECT * "
                    + "FROM " + table("sous_categorie")
                    + " WHERE nom = ? ";

            conn = getConnection();
            pstmt = conn.prepareStatement(SQL);

            pstmt.setString(1, sousCategorie);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                status = rs.getInt("sous_categorie_id");
            }
        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
            status = -1;
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { pstmt.close(); } catch (Exception e) { /* ignored */ }
            try { conn.close(); } catch (Exception e) { /* ignored */ }
        }
        return status;
    }

    /** Récupère les types de transactions
     * @return  les types de transactions
     */
    public ArrayList<String> get_Type_transaction() {

        ArrayList<String> listType_transaction = new ArrayList<>();
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement st = null;

        try {
            conn = getConnection();
            st = conn.prepareStatement("SELECT * FROM " + table("type_transaction"));
            rs = st.executeQuery();

            while (rs.next())
            {
                listType_transaction.add( rs.getString("type") );
            }

        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { st.close(); } catch (Exception e) { /* ignored */ }
            try { conn.close(); } catch (Exception e) { /* ignored */ }
        }

        return listType_transaction;
    }

    /**
     * @param userID
     * @param sousCatID
     * @return
     */
    public ArrayList<Integer> getSousCategorieMonthly(int userID, int sousCatID) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;

        String SQL = "SELECT SUM(Transaction.valeur) as Somme FROM " + table("utilisateur") +
                "INNER JOIN " + table("Modele_transaction") + "ON Modele_transaction.utilisateur_id = Utilisateur.id " +
                "INNER JOIN " + table("Transaction") + "ON Modele_transaction.modele_transaction_id = Transaction.modele_transaction_id " +
                "WHERE Utilisateur.id = ? AND Modele_transaction.sous_categorie_id = ? " + "GROUP BY MONTH(Transaction.date);";

        ArrayList<Integer> sommes = new ArrayList<>();

        try{
            conn = getConnection();
            pstmt = conn.prepareStatement(SQL);

            pstmt.setInt(2, sousCatID);
            pstmt.setInt(1, userID);

            rs = pstmt.executeQuery();


            while (rs.next()) {
                sommes.add(rs.getInt(1));
            }

        }
        catch(SQLException ex){
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { pstmt.close(); } catch (Exception e) { /* ignored */ }
            try { conn.close(); } catch (Exception e) { /* ignored */ }
        }
        return sommes;
    }

    /**
     * Permet d'ajouter un revenu
     *
     * @param userID            ID de l'utilisateur
     * @param valeur            valeur du revenu
     * @param sousCategorie     sous categorie
     * @param recurrence        recurrence du payement
     * @param note              note de l'utilisateur concernant le payement
     * @return                  retourne 0 s'il n'y a pas eu de probleme, -1 autrement
     */
    public int addIncome(int userID, int valeur, String sousCategorie, String recurrence, String note){
        String SQL = "INSERT INTO " + table("modele_transaction") + "(valeur, date, note, utilisateur_id, sous_categorie_id, type_transaction_id, recurrence_id) " +
                "VALUES (?, NOW(),?, ?, ?, ?, ?);";

        try{
            PreparedStatement pstmt = getConnection().prepareStatement(SQL);

            pstmt.setInt(1, valeur);
            pstmt.setString(2, note);
            pstmt.setInt(3, userID);
            pstmt.setInt(4, getSousCategorieID(sousCategorie));
            pstmt.setInt(5, 2);
            if(recurrence == null){
                pstmt.setNull(6, Types.INTEGER);
            }else{
                pstmt.setInt(6, getRecIdByName(recurrence));
            }

            //ResultSet rs = pstmt.executeQuery();
            pstmt.executeUpdate();
            return 0;
        }
        catch(SQLException ex){
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    /**
     * Permet d'ajouter une depense
     *
     * @param userID            ID de l'utilisateur
     * @param valeur            valeur de le depense
     * @param sousCategorie     sous categorie
     * @param recurrence        recurrence du payement
     * @param note              note de l'utilisateur concernant le payement
     * @return                  retourne 0 s'il n'y a pas eu de probleme, -1 autrement
     */
    public int addExpense(int userID, int valeur, String sousCategorie, String recurrence, String note){
        String SQL = "INSERT INTO " + table("modele_transaction") + "(valeur, date, note, utilisateur_id, sous_categorie_id, type_transaction_id, recurrence_id) " +
                "VALUES (?, NOW(),?, ?, ?, ?, ?);";

        try{
            PreparedStatement pstmt = getConnection().prepareStatement(SQL);

            pstmt.setInt(1, valeur);
            pstmt.setString(2, note);
            pstmt.setInt(3, userID);
            pstmt.setInt(4, getSousCategorieID(sousCategorie));
            pstmt.setInt(5, 1);
            if(recurrence == null){
                pstmt.setNull(6, Types.INTEGER);
            }else{
                pstmt.setInt(6, getRecIdByName(recurrence));
            }

            pstmt.executeUpdate();
            return 0;
        }
        catch(SQLException ex){
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    /**
     * Permet d'ajouter une depense
     *
     * @param userID            ID de l'utilisateur
     * @param valeur            valeur de le depense
     * @param idSousCategorie   id de sous categorie
     * @param recurrenceId      id de la recurrence du payement
     * @param note              note de l'utilisateur concernant le payement
     * @param idTypeTrans       id de du type de transaction
     * @return                  retourne 0 s'il n'y a pas eu de probleme, -1 autrement
     */
    public int addMovement(int userID, double valeur, int idSousCategorie, int recurrenceId, String note, int idTypeTrans){
        String SQL = "INSERT INTO " + table("modele_transaction") + "(valeur, date, note, utilisateur_id, sous_categorie_id, type_transaction_id, recurrence_id) " +
                "VALUES ("+ valeur +", NOW(),?, ?, ?, ?, ?);";

        try {
            PreparedStatement pstmt = getConnection().prepareStatement(SQL);

            //pstmt.setBigDecimal(1, valeur);
            pstmt.setString(1, note);
            pstmt.setInt(2, userID);

            pstmt.setInt(3, idSousCategorie);

            pstmt.setInt(4, idTypeTrans);
            if(recurrenceId == 0){
                pstmt.setNull(5, Types.INTEGER);
            }else{
                pstmt.setInt(5, recurrenceId);
            }

            pstmt.executeUpdate();
            return 0;
        }
        catch(SQLException ex){
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    /** Permet de récupèrer les dépenses d'une catégorie
     * @param userId
     * @param cat
     * @param idRecurence
     * @return
     */
    public double getSumExpensesOnSpecialPeriodCategorie(int userId,int cat,int idRecurence)
    {
        ArrayList<Transaction> temp = getAllTransactionByCatId(userId,cat);
        ArrayList<Transaction> result = new ArrayList<Transaction>();
        int nbJours = translateRecurrenceInDays(idRecurence);
        double somme = 0;
        for(Transaction trans : temp)
        {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date dateTrans = null;
            try {
                dateTrans = df.parse(trans.date);
            }catch (Exception e)
            {
                Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, e);
                return 0;
            }
            LocalDate nowDate = LocalDate.now().minusDays(nbJours);

            if(dateTrans.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate().compareTo(nowDate) >= 0 ){
                somme += trans.valeur;
            }
        }
        return somme;

    }

    /** Converssion entre une récurrence et le nombre de jour
     * @param idRecurrence l'ID de la récurrence
     * @return le nombre de jour
     */
    private int translateRecurrenceInDays(int idRecurrence)
    {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        String SQL = "Select periodicite FROM " + table("recurence") + " WHERE recurence_id = ?;";
        try
        {
            conn = getConnection();
            pstmt = conn.prepareStatement(SQL);
            pstmt.setInt(1,idRecurrence);
            rs = pstmt.executeQuery();

            if(rs.next()) {
                String val = rs.getString(1);
                switch(val)
                {
                    case "Annuel":
                        return 365;

                    case"Quotidien":
                        return 1;

                    case "Trimestriel":
                        return 91;

                    case"Semestriel":
                        return 182;

                    case "Mensuel":
                        return 31;

                    case"Hebdomadaire":
                        return 7;

                    default:
                        return 0;
                }
            }
            else
            {
                return 0;
            }
        }
        catch(SQLException ex){
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { pstmt.close(); } catch (Exception e) { /* ignored */ }
            try { conn.close(); } catch (Exception e) { /* ignored */ }
        }
        return 0;
    }


    /**
     * @return
     */
    public ArrayList<Categorie> getAllCategories() {
        ArrayList<Categorie> categories = new ArrayList<Categorie>();
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            String SQL = "SELECT * " + "FROM " + table("categorie");
            conn = this.getConnection();
            st = conn.createStatement();
            rs = st.executeQuery(SQL);

            while (rs.next()) {
                Categorie cat = new Categorie(rs.getInt("categorie_id"), rs.getString("nom"));
                categories.add(cat);

            }
        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { st.close(); } catch (Exception e) { /* ignored */ }
            try { conn.close(); } catch (Exception e) { /* ignored */ }
        }
        return categories;
    }
    

    /**
     * Renvoie les 10 dernieres depenses toutes categories confondues
     *
     * @param userID        ID de l'utilisateur
     * @param sousCatID     ID de la sous categorie
     * @return              retourne une liste contenant les 10 dernieres transactions de la sous categorie
     */
    public ArrayList<Integer> getTenLastSousCategorie(int userID, int sousCatID) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;

        String SQL = "SELECT Transaction.valeur, Modele_transaction.type_transaction_id, Transaction.date FROM " + table("modele_transaction")
                + " INNER JOIN " + table("Transaction") + " ON Modele_transaction.modele_transaction_id = Transaction.modele_transaction_id"
                + " WHERE Modele_transaction.utilisateur_id = ? AND Modele_transaction.sous_categorie_id = ?"
                + " ORDER BY Transaction.date DESC LIMIT 10;";

        ArrayList<Integer> sommes = new ArrayList<>();

        try{
            conn = getConnection();
            pstmt = conn.prepareStatement(SQL);

            pstmt.setInt(1, userID);
            pstmt.setInt(2, sousCatID);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                sommes.add(rs.getInt(1));
            }
        }
        catch(SQLException ex){
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { pstmt.close(); } catch (Exception e) { /* ignored */ }
            try { conn.close(); } catch (Exception e) { /* ignored */ }
        }
        return sommes;
    }

    /**
     * @param recurrence
     * @return
     */
    private int getRecIdByName(String recurrence)
    {
        String sql = "SELECT recurence_id FROM " + table("recurence") + " WHERE periodicite = ?;";
        int result = 0;
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try{
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, recurrence);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                result = rs.getInt(1);
            }
        }
        catch(SQLException ex){
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { pstmt.close(); } catch (Exception e) { /* ignored */ }
            try { conn.close(); } catch (Exception e) { /* ignored */ }
        }
        return result;
    }

    /**
     * @param userId
     * @return
     */
    public double getSoldeById(int userId)
    {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        String sql = "SELECT solde FROM " + table("utilisateur") + " WHERE utilisateur_id = ?;";
        double result = 0;
        try{
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, userId);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                result = rs.getDouble(1);
            }
        }
        catch(SQLException ex){
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { pstmt.close(); } catch (Exception e) { /* ignored */ }
            try { conn.close(); } catch (Exception e) { /* ignored */ }
        }
        return result;
    }
    /**
     * Renvoie les 10 dernieres depenses tout confondu
     *
     * @param   userID ID de l'utilisateur
     * @return  retourne un tableau 2 dimensions avec 2 arrayList, la 1ere avec la categorie, la 2eme avec le montant
     */
    public ArrayList<Integer> DixDerniersMouvements(int userID){
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;

        String SQL = "SELECT Transaction.valeur, Modele_transaction.type_transaction_id, Modele_transaction.sous_categorie_id, Transaction.date"
                + " FROM Modele_transaction"
                + " INNER JOIN Transaction ON Modele_transaction.modele_transaction_id = Transaction.modele_transaction_id"
                + " WHERE Modele_transaction.utilisateur_id = ?"
                + " ORDER BY Transaction.date DESC LIMIT 10;";

        ArrayList<Integer> sommes = new ArrayList<>();

        try{
            conn = getConnection();
            pstmt = conn.prepareStatement(SQL);

            pstmt.setInt(1, userID);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                sommes.add(rs.getInt(1));
            }
        }
        catch(SQLException ex){
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { pstmt.close(); } catch (Exception e) { /* ignored */ }
            try { conn.close(); } catch (Exception e) { /* ignored */ }
        }
        return sommes;
    }

    /**
     * Récupère les diffèrentes récurences
     *
     * @return une ArrayList<Recurrence> contenant toutes les récurences de la base de donnée
     */
    public ArrayList<Recurrence> getRecurrence()
    {
        String sql = "SELECT * FROM " + table("recurence") + ";";
        ArrayList<Recurrence> recs = new ArrayList<>();
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;

        try{
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                recs.add(new Recurrence(rs.getInt("recurence_id"),rs.getString("periodicite")));
            }
        }
        catch(SQLException ex){
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { pstmt.close(); } catch (Exception e) { /* ignored */ }
            try { conn.close(); } catch (Exception e) { /* ignored */ }
        }
        return recs;

    }

    /** Met à jour les sous catégories personnels
     * @param id_sous_cat
     * @param id_user
     */
    public void updateSousCatPerso(int id_sous_cat, int id_user){
        String SQL  = "CALL add_sous_cat_perso(?, ?)";

        try{
            CallableStatement cs = getConnection().prepareCall(SQL);

            //Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, "id user :" + id_user, "" );

            cs.setInt(1, id_user);
            cs.setInt(2, id_sous_cat);

            cs.execute();
        }
        catch(SQLException ex){
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /** Test si une sous catégoire appartient à un user
     * @param sousCat_id    ID de la sous catégorie
     * @param user_id
     * @return true si elle appartient au user
     */
    public boolean belongToUser(int sousCat_id, int user_id) {

        boolean IsYours = false;
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement st = null;

        try {
            conn = getConnection();
            st = conn.prepareStatement("SELECT * FROM " + table("sous_categories_personnelles") + " WHERE sous_categorie_id = ? AND utilisateur_id = ?");
            st.setInt(1, sousCat_id);
            st.setInt(2, user_id);
            rs = st.executeQuery();

            IsYours = rs.next();

        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { st.close(); } catch (Exception e) { /* ignored */ }
            try { conn.close(); } catch (Exception e) { /* ignored */ }
        }

        return IsYours;
    }


    /** Récupère toutes les transactions d'un user
     * @param userId
     * @return toutes les transactions du user
     */
    public ArrayList<Transaction> getAllTransaction(int userId)
    {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        String SQL = "Select public.transaction.transaction_id, public.sous_categorie.nom, public.transaction.valeur," +

                "public.transaction.date, public.modele_transaction.recurrence_id ,public.transaction.timestamp_solde," +
                "public.modele_transaction.type_transaction_id FROM " + table("transaction") + " INNER JOIN " + table("modele_transaction")
                + " ON " + table("transaction") + ".modele_transaction_id = " + table("modele_transaction") +
                ".modele_transaction_id" + " INNER JOIN "+ table("sous_categorie")+ " ON " + table("modele_transaction")
                +".sous_categorie_id = " +table("sous_categorie") + ".sous_categorie_id  WHERE " + table("modele_transaction") + ".utilisateur_id = ? ORDER BY "
                + "public.transaction.date DESC, public.transaction.transaction_id DESC;";

        ArrayList<Transaction> trans = new ArrayList<>();

        try{
            conn = getConnection();
            pstmt = conn.prepareStatement(SQL);

            pstmt.setInt(1, userId);

            rs = pstmt.executeQuery();

            while (rs.next()) {

                trans.add(new Transaction(rs.getInt(1),rs.getString(2),rs.getDouble(3),rs.getString(4),rs.getInt(5),rs.getDouble(6), rs.getInt(7)));
            }
        }
        catch(SQLException ex){
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { pstmt.close(); } catch (Exception e) { /* ignored */ }
            try { conn.close(); } catch (Exception e) { /* ignored */ }
        }
        return trans;

    }

    /** Récupère les 5 dernières transactions d'un user
     * @param userId
     * @return 5 dernières transactions du user
     */
    public ArrayList<Transaction> getFiveLastTransactions(int userId)
    {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        String SQL = "Select public.transaction.transaction_id, public.sous_categorie.nom, public.transaction.valeur," +

                "public.transaction.date, public.modele_transaction.recurrence_id ,public.transaction.timestamp_solde," +
                "public.modele_transaction.type_transaction_id FROM " + table("transaction") + " INNER JOIN " + table("modele_transaction")
                + " ON " + table("transaction") + ".modele_transaction_id = " + table("modele_transaction") +
                ".modele_transaction_id" + " INNER JOIN "+ table("sous_categorie")+ " ON " + table("modele_transaction")
                +".sous_categorie_id = " +table("sous_categorie") + ".sous_categorie_id  WHERE " + table("modele_transaction") + ".utilisateur_id = ? ORDER BY "
                + "public.transaction.date DESC, public.transaction.transaction_id DESC;";

        ArrayList<Transaction> trans = new ArrayList<>();

        try{
            conn = getConnection();
            pstmt = conn.prepareStatement(SQL);

            pstmt.setInt(1, userId);

            rs = pstmt.executeQuery();

            int i = 5;
            while (rs.next() && i > 0) {

                trans.add(new Transaction(rs.getInt(1),rs.getString(2),rs.getDouble(3),rs.getString(4),rs.getInt(5),rs.getDouble(6), rs.getInt(7)));
                i--;
            }
        }
        catch(SQLException ex){
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { pstmt.close(); } catch (Exception e) { /* ignored */ }
            try { conn.close(); } catch (Exception e) { /* ignored */ }
        }
        return trans;
    }


    /** Récupère toutes les transaction d'un user et d'une catégorie donnée
     * @param userId
     * @param cat
     * @return toutes les transaction trouvée
     */
    public ArrayList<Transaction> getAllTransactionByCatId(int userId,int cat)
    {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        String SQL = "Select public.transaction.transaction_id, public.sous_categorie.nom, public.transaction.valeur," +

                "public.transaction.date, public.modele_transaction.recurrence_id,public.transaction.timestamp_solde, public.modele_transaction.type_transaction_id  FROM " + table("transaction") + " INNER JOIN " + table("modele_transaction")
                + " ON " + table("transaction") + ".modele_transaction_id = " + table("modele_transaction") +
                ".modele_transaction_id" + " INNER JOIN "+ table("sous_categorie")+ " ON " + table("modele_transaction")
                +".sous_categorie_id = " +table("sous_categorie") + ".sous_categorie_id  WHERE " + table("modele_transaction") + ".utilisateur_id = ? AND " + table("sous_categorie")+".categorie_id = ? ORDER BY "
                + "public.transaction.date DESC, public.transaction.transaction_id DESC;";

        ArrayList<Transaction> trans = new ArrayList<>();

        try{
            conn = getConnection();
            pstmt = conn.prepareStatement(SQL);

            pstmt.setInt(1, userId);
            pstmt.setInt(2,cat);

            rs = pstmt.executeQuery();

            while (rs.next()) {

                trans.add(new Transaction(rs.getInt(1),rs.getString(2),rs.getDouble(3),rs.getString(4),rs.getInt(5),rs.getDouble(6), rs.getInt(7)));
            }
        }
        catch(SQLException ex){
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { pstmt.close(); } catch (Exception e) { /* ignored */ }
            try { conn.close(); } catch (Exception e) { /* ignored */ }
        }
        return trans;

    }

    /** Ajout une limite à un user dans la BDD
     * @param amount
     * @param userId
     * @param recId
     * @param sousCatId
     * @param catId
     * @return retourne le statut (-1 en cas d'echec)
     */
    public int addLimit(double amount,int userId,int recId,int sousCatId,int catId)
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int status  = -1;

        String SQL = "INSERT INTO " + table("limite") + "(date, valeur, utilisateur_id, recurence_id,sous_categorie_id,categorie_id) " +
                "VALUES ( NOW(),"+amount+", ?, ?, ?, ?);";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(SQL);

            //pstmt.setBigDecimal(1, valeur);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, recId);
            pstmt.setNull(3,java.sql.Types.INTEGER);
            pstmt.setInt(4, catId);
            pstmt.executeUpdate();
            status = 0;
        }
        catch(SQLException ex){
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
            status = -1;
        }finally {
            try { pstmt.close(); } catch (Exception e) { /* ignored */ }
            try { conn.close(); } catch (Exception e) { /* ignored */ }
        }

        return status;
    }


    /** Fonction permettant la mise à jour des paiements recurrents via un appel a la procedure prevue a cet effet
     * @param user_id ID de l'utilisateur à mettre a jour
     */
    public void check_recurrences(int user_id){
        Connection conn = null;
        CallableStatement cs = null;

        String SQL  = "CALL check_recurrences(?)";
            try{
                conn = getConnection();
                cs = conn.prepareCall(SQL);
                cs.setInt(1, user_id);
                cs.execute();
            }
            catch(SQLException ex){
                Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
            }finally {
                try { cs.close(); } catch (Exception e) { /* ignored */ }
                try { conn.close(); } catch (Exception e) { /* ignored */ }
            }
    }


    /** Permet de récupèrer le solde total d'un mois
     * @param diffNowMonth
     * @return Le nom du mois et le solde
     */
    public MonthlyExpense getSoldeOverAllOneMonth(int diffNowMonth, int idUser)
    {
        MonthlyExpense monthlyExpense = null;
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;

        String[] monthName = {"Jan", "Feb",
                "Mar", "Apr", "May", "Jun", "Jul",
                "Aug", "Sep", "Oct", "Nov",
                "Dec"};

        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        double soldeMois = 0;

        int realMonth = month-diffNowMonth;
        if (realMonth < 0){
            realMonth = (12 + realMonth) ;
            year--;
        }

        // Récupèration du nom du mois
        String nameMonth = monthName[realMonth];

        String moisVoulu = String.format("%02d" , realMonth+1);

        String SQL = "Select SUM(public.transaction.valeur) from public.transaction inner join public.modele_transaction \n" +
                "\tON public.transaction.modele_transaction_id = public.modele_transaction.modele_transaction_id \n" +
                "\tWHERE public.modele_transaction.utilisateur_id = ?  AND public.transaction.date::text LIKE '"+year+"-"+moisVoulu+"%' \n" +
                "\tAND public.modele_transaction.type_transaction_id = 1";

        try{
            conn = getConnection();
            pstmt = conn.prepareStatement(SQL);

            pstmt.setInt(1, idUser);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                soldeMois = rs.getInt(1);
            }

            monthlyExpense = new MonthlyExpense(nameMonth,soldeMois);

        }
        catch(SQLException ex){
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { pstmt.close(); } catch (Exception e) { /* ignored */ }
            try { conn.close(); } catch (Exception e) { /* ignored */ }
        }
        return monthlyExpense;
    }

    /** Permet de récupèrer tous les soldes de tous les 12 derniers moins
     * @param idUser    ID du user concerné
     * @return  Une liste des noms des moins avec les soldes
     */
    public ArrayList<MonthlyExpense> getSoldeOverAllOneYear(int idUser){
        ArrayList<MonthlyExpense> tabExpense = new ArrayList<MonthlyExpense>();


        for(int i=11; i >= 0; --i){
            tabExpense.add(getSoldeOverAllOneMonth(i,idUser));
        }

        return tabExpense;
    }

    /** Récupère le solde total d'une sous catégorie
     * @param IDuser    l'ID du user
     * @param IDSousCat l'ID de la sous catégorie
     * @return Le solde total d'une sous catégorie
     */
    public double getSoldeBySousCategorie(int IDuser, int IDSousCat){
        double Solde = 0;
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;

        String SQL = "select SUM(public.transaction.valeur) from public.transaction inner join public.modele_transaction \n" +
                "\tON public.transaction.modele_transaction_id = public.modele_transaction.modele_transaction_id \n" +
                "\tWHERE public.modele_transaction.utilisateur_id = ?  AND public.modele_transaction.sous_categorie_id = ?\n" +
                "\tAND public.modele_transaction.type_transaction_id = 1;";

        try{
            conn = getConnection();
            pstmt = conn.prepareStatement(SQL);

            pstmt.setInt(1, IDuser);
            pstmt.setInt(2,IDSousCat);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                Solde = rs.getDouble(1);
            }
        }
        catch(SQLException ex){
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { pstmt.close(); } catch (Exception e) { /* ignored */ }
            try { conn.close(); } catch (Exception e) { /* ignored */ }
        }


        return Solde;
    }

    /** Récupère le solde total d'une catégorie
     * @param IDuser    ID du user
     * @param IDCat     UD de la catégorie
     * @return le solde total d'un catégorie
     */
    public double getSoldeByCategorie(int IDuser, int IDCat){
        double Solde = 0;
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;

        ArrayList<SousCategorie> tabSousCategories = get_Sous_categorie(IDCat);

        for(SousCategorie sousCategorie : tabSousCategories){
            Solde += getSoldeBySousCategorie(IDuser, sousCategorie.id );
        }

        return Solde;
    }

    /** Récupère toutes les notifications du user
     * @param userId
     * @return toutes les notifications du user
     */
    public ArrayList<Notification> getAllNotification(int userId)
    {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        String SQL = "SELECT * FROM public.notification WHERE public.notification.utilisateur_id = ? ORDER BY public.notification.notification_id DESC;";

        ArrayList<Notification> notifs = new ArrayList<>();

        try{
            conn = getConnection();
            pstmt = conn.prepareStatement(SQL);

            pstmt.setInt(1, userId);

            rs = pstmt.executeQuery();

            while (rs.next()) {

                notifs.add(new Notification(rs.getString(2),rs.getString(3)));

            }

        }
        catch(SQLException ex){
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { pstmt.close(); } catch (Exception e) { /* ignored */ }
            try { conn.close(); } catch (Exception e) { /* ignored */ }
        }
        return notifs;

    }


    /** Permet de récupérer le solde total d'un mois d'une catégorie
     * @param idUser        ID du user
     * @param IDSousCat     ID de la sous catégorie
     * @param diffNowMonth  la différence de mois en fonction du mois de maintenant
     * @return e solde total d'un mois d'une catégorie
     */
    public double getSoldeOverAllOneMonthBySousCategorie(int idUser, int IDSousCat,int diffNowMonth)
    {
        MonthlyExpense monthlyExpense = null;
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;

        double Solde = 0;

        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);

        int realMonth = month-diffNowMonth;
        if (realMonth < 0){
            realMonth = (12 + realMonth) ;
            year--;
        }

        String moisVoulu = String.format("%02d" , realMonth+1);

        String SQL = "Select SUM(public.transaction.valeur) from public.transaction inner join public.modele_transaction \n" +
                "\tON public.transaction.modele_transaction_id = public.modele_transaction.modele_transaction_id \n" +
                "\tWHERE public.modele_transaction.utilisateur_id = ?  AND public.modele_transaction.sous_categorie_id = ?  AND " +
                "public.transaction.date::text LIKE '"+year+"-"+moisVoulu+"%' \n" +
                "\tAND public.modele_transaction.type_transaction_id = 1";

        try{
            conn = getConnection();
            pstmt = conn.prepareStatement(SQL);

            pstmt.setInt(1, idUser);
            pstmt.setInt(2, IDSousCat);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                Solde = rs.getDouble(1);
            }

        }
        catch(SQLException ex){
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { pstmt.close(); } catch (Exception e) { /* ignored */ }
            try { conn.close(); } catch (Exception e) { /* ignored */ }
        }
        return Solde;
    }

    /** Permet de récupèrer le solde total d'un mois d'une catégorie
     * @param IDuser        Id du user
     * @param IDCat         ID de la catégorie
     * @param diffNowMonth  Mois de différence avec le mois de now
     * @return Le nom du mois et le solde
     */
    public MonthlyExpense getSoldeOneMonthByCategorie(int IDuser, int IDCat, int diffNowMonth)
    {

        String[] monthName = {"Jan", "Feb",
                "Mar", "Apr", "May", "Jun", "Jul",
                "Aug", "Sep", "Oct", "Nov",
                "Dec"};

        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        double soldeMois = 0;

        int realMonth = month-diffNowMonth;
        if (realMonth < 0){
            realMonth = (12 + realMonth) ;
            year--;
        }

        // Récupèration du nom du mois
        String nameMonth = monthName[realMonth];

        String moisVoulu = String.format("%02d" , realMonth+1);

        // Récupère le solde total d
        ArrayList<SousCategorie> tabSousCategories = get_Sous_categorie(IDCat);

        for(SousCategorie sousCategorie : tabSousCategories){
            soldeMois += getSoldeOverAllOneMonthBySousCategorie(IDuser, sousCategorie.id, diffNowMonth );
        }


        MonthlyExpense monthlyExpense = new MonthlyExpense(nameMonth,soldeMois);

        return monthlyExpense;
    }

    /** Récupère le solde total d'une catégorie des 12 derniers mois
     * @param IDuser    ID du user
     * @param IDCat     ID de la catégorie
     * @return le solde total d'une catégorie des 12 derniers mois
     */
    public MonthlyExpenseForCat getSoldeForOneYearByCategorie(int IDuser, int IDCat){

        ArrayList<MonthlyExpense> listMonthlyExpense = new ArrayList<MonthlyExpense>();

        for(int CompteurMois = 11; CompteurMois >= 0; --CompteurMois){
            listMonthlyExpense.add(getSoldeOneMonthByCategorie(IDuser,IDCat,CompteurMois));
        }

        MonthlyExpenseForCat monthlyExpenseForCat = new MonthlyExpenseForCat(listMonthlyExpense,IDCat);

        return monthlyExpenseForCat;
    }

    /** Pour toutes les catégorie récupère le solde des 12 derniers mois
     * @param IDuser ID du user
     * @return le solde des 12 derniers mois pour chaque catégorie
     */
    public ArrayList<MonthlyExpenseForCat> getSoldeForOneYearOfAllCategorie(int IDuser){

        ArrayList<MonthlyExpenseForCat> listMonthlyExpenseForCat = new ArrayList<MonthlyExpenseForCat>();

        for (Categorie categorie : get_Categories()){
            listMonthlyExpenseForCat.add(getSoldeForOneYearByCategorie(IDuser, categorie.id ));

        }

        return listMonthlyExpenseForCat;
    }


    /** Tests quelques fonctions de la classe
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException {

        BDD app = new BDD();
        System.out.println("------------------Utilisateurs------------------------------------------------");
        app.display_Utilisateurs();
        System.out.println("------------------Categorie------------------------------------------------");
        app.get_Categories();
        System.out.println("------------------Sous categorie------------------------------------------------");
        app.get_Sous_categorie(4);
        System.out.println("------------------Utilisateurs 1------------------------------------------------");
        app.UtilisateurByID(1);
        System.out.println("------------------Utilisateurs 2------------------------------------------------");
        app.UtilisateurByID(2);
        System.out.println("------------------Utilisateurs 3------------------------------------------------");
        app.UtilisateurByID(3);

        System.out.println("------------------Insert Utilisateurs ------------------------------------------------");
        if (app.insert_Utilisateurs( "prenom1",  "nom2", "email2", "pseudo1", "mdp_evadvservsrevervrev_vdfvdfvdF_vfdvdf_lol", true, "1950-03-11", 2,  23,  1,0) == 0)
            System.out.println("erreur !");

        System.out.println("------------------Insert Sous_categorie------------------------------------------------");


        System.out.println("------------------Insert get categorie------------------------------------------------");
        Categorie cat = app.CategorieByID( 1 );
        System.out.println(cat.id);

        System.out.println("------------------Get Type_transaction------------------------------------------------");
        System.out.println(app.get_Type_transaction());
    }
}
