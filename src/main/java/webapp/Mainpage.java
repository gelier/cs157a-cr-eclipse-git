package webapp;

import controller.DBHandler;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;


@WebServlet(name ="mainpage")
public class Mainpage extends HttpServlet {

    DBHandler dbHandler = new DBHandler();
    WebHandler webHandler = new WebHandler();

    private ArrayList<String> commentList = new ArrayList<String>();
    private ArrayList<String> usernameList = new ArrayList<String>();
    private ArrayList<String> datePostedList = new ArrayList<String>();

    private static boolean status = false;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession sess;
        String currentUser = "";
        int currentUserID = 0;
       try{
           sess = request.getSession(false); //use false to use the existing session
            currentUser = (String) sess.getAttribute("currentUserName");//this will return username anytime in the session
            currentUserID = (int) sess.getAttribute("currentUserID");//this will return id anytime in the session
       }
       catch (Exception e){
           e.printStackTrace();
       }
        request.setAttribute("currentUserName", currentUser);
        request.setAttribute("currentUserID", currentUserID);


        int recipeID = 0;

        int recipeCount = webHandler.getRecipeCount();
        for (int index = 0; index <= recipeCount; index++){
            if (request.getParameter("gotoRecipe" + String.valueOf(index)) != null){
                recipeID = index;
                break;
            }
            if (request.getParameter("addToFavorites" + String.valueOf(index)) != null){
                recipeID = index;
                break;
            }
            if (request.getParameter("unAddToFavorites" + String.valueOf(index)) != null){
                recipeID = index;
                break;
            }
        }
        //if the user click gotoRecipe
        if (request.getParameter("gotoRecipe" + recipeID) != null){
            sess = request.getSession(); //use false to use the existing session
            sess.setAttribute("currentRecipeId",recipeID);//this will return id anytime in the session
            getRecipeComments(recipeID, request);
            displayRecipe(recipeID, request);
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/displayRecipe.jsp");
            dispatcher.forward(request,response);
            //response.sendRedirect("/displayRecipe.jsp");
        }
        if (request.getParameter("addToFavorites" + recipeID) != null){

            try{
                dbHandler.startConnection();
                dbHandler.insertMyFavoriteToDB(currentUserID, recipeID);
                dbHandler.closeConnection();

            }
            catch(Exception e){
                e.printStackTrace();
            }
            sess = request.getSession(false); //use false to use the existing session
            String heartPath = "Images/heart-filled-red.png";
            request.setAttribute("heartPath",heartPath);
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/mainpage.jsp");
            dispatcher.forward(request,response);


        }
        if (request.getParameter("unAddToFavorites" + recipeID) != null){

            try{
                Connection connection = dbHandler.startConnection();
                Statement statement = connection.createStatement();
                statement.executeUpdate("DELETE FROM myfavorite WHERE recipe_id='"+recipeID+"'");
                dbHandler.closeConnection();
            }
            catch (Exception e){
                e.printStackTrace();
            }



            String unHeartPath = "Images/heart-empty-black.png";
            request.setAttribute("unheart",unHeartPath);
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/mainpage.jsp");
            dispatcher.forward(request,response);
        }

    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String text = "some text";
        response.setContentType("text/plain");  // Set content type of the response so that jQuery knows what it can expect.
        response.setCharacterEncoding("UTF-8"); // You want world domination, huh?
        response.getWriter().write(text);
    }
    public void displayRecipe(int recipeID, HttpServletRequest request){

        int recipePrivacy = 0;
        String recipeName = "";
        String recipeImage = "";
        String recipeStep = "";

        //get all user comments to this recipe and store them in accountList and commentList
        webHandler.setAccountComments(recipeID);
        try{

            Connection connection = dbHandler.startConnection();
            String selectSql = "SELECT * FROM recipe WHERE recipe_id='"+recipeID+"'";
            Statement statement = connection.createStatement();
            ResultSet rs_recipe = statement.executeQuery(selectSql);
            rs_recipe.first();

            recipeName = rs_recipe.getString("recipe_name");
            recipeStep = rs_recipe.getString("step");
            recipePrivacy = rs_recipe.getInt("privacy");
            recipeImage = webHandler.getImage(recipeID);

            //available access to the next JSP
            HttpSession sess = request.getSession();
            sess.setAttribute("recipeID", recipeID);
            sess.setAttribute("recipeName", recipeName);
            sess.setAttribute("recipeStep", recipeStep);
            sess.setAttribute("recipeImage", recipeImage);


        }
        catch (Exception e){

        }
    }
    public void getRecipeComments(int recipeID, HttpServletRequest request){
        HttpSession sess = request.getSession();
        commentList.clear();
        usernameList.clear();

        try{
            Connection connection = dbHandler.startConnection();
            String selectSql = "SELECT * FROM comment WHERE recipe_id='"+recipeID+"'";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(selectSql);

            while (rs.next()){
                commentList.add(rs.getString("text"));
                usernameList.add(rs.getString("username"));
                datePostedList.add(String.valueOf(rs.getDate("date_posted")));
            }
            rs.close();
            statement.close();
            connection.close();

            sess.setAttribute("commentList", commentList);
            sess.setAttribute("usernameList", usernameList);
        }
        catch (Exception e){

        }

    }

}
