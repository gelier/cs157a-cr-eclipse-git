<%@ page import="webapp.Mainpage" %>
<%@ page import="java.io.PrintWriter" %><%--
  Created by IntelliJ IDEA.
  User: guillerdalit
  Date: 7/24/20
  Time: 9:19 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Display my recipe</title>
</head>
<body>

<%
    Mainpage mainpageHandler = new Mainpage();
    mainpageHandler.getRecipeName();
    mainpageHandler.getIngredients();
    mainpageHandler.getStep();
%>

<h1> <% out.println(mainpageHandler.getRecipeID()); %> </h1>
<h1> <% out.println(mainpageHandler.getRecipeName()); %> </h1>
<h1> <% out.println(mainpageHandler.getIngredients()); %> </h1>
<h1> <% out.println(mainpageHandler.getStep()); %> </h1>

</body>
</html>
