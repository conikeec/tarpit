package io.shiftleft.tarpit;

import io.shiftleft.tarpit.io.shiftleft.tarpit.model.User;
import java.io.IOException;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "simpleServlet", urlPatterns = {"/hello"}, loadOnStartup = 1)
public class ServletTarPit extends HttpServlet {

  private static final long serialVersionUID = -3462096228274971485L;
  private Connection connection;
  private PreparedStatement preparedStatement;
  private ResultSet resultSet;

  private final static Logger LOGGER = Logger.getLogger(ServletTarPit.class.getName());

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String login = request.getParameter("login");
    String password = request.getParameter("password");
    boolean keepOnline = (request.getParameter("keeponline") != null);
    try {
      getConnection(); 

      String sql = "SELECT * FROM USER WHERE LOGIN = '" + login + "' AND PASSWORD = '" + password + "'";
      preparedStatement = connection.prepareStatement(sql);
      resultSet = preparedStatement.getResultSet();

      if(resultSet.next()) {

        login = resultSet.getString("login");
        password = resultSet.getString("password");

        User user = new User(login,
            resultSet.getString("fname"),
            resultSet.getString("lname"),
            resultSet.getString("passportnum"),
            resultSet.getString("address1"),
            resultSet.getString("address2"),
            resultSet.getString("zipCode"));

        Cookie cookie = new Cookie("login", login);
        cookie.setMaxAge(864000);
        cookie.setPath("/");
        response.addCookie(cookie);

        request.setAttribute("user", user);
        request.setAttribute("login",login);


        LOGGER.info(" User " + user + " successfully logged in ");

        getServletContext().getRequestDispatcher("/dashboard.jsp").forward(request,response);
      } else {
        request.setAttribute("login", login);
        request.setAttribute("password", password);
        request.setAttribute("keepOnline", keepOnline);
        request.setAttribute("message", "Failed ti Sign in. Please verify credentials");

        LOGGER.info(" UserId " + login + " failed to logged in ");

        getServletContext().getRequestDispatcher("/signIn.jsp").forward(request,response);
      }
    } catch (Exception e) {
      throw new ServletException(e);
    }

  }

  private void getConnection() throws ClassNotFoundException, SQLException {
    Class.forName("com.mysql.jdbc.Driver");
    connection = DriverManager.getConnection("jdbc:mysql://localhost/DBPROD", "admin", "1234");
  }

}