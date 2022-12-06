import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet(urlPatterns = "/Recite")//目录匹配
public class ReciteWord extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String dbURL = "jdbc:sqlserver://localhost:1433;DatabaseName=user_word";
        String userName = "sa";
        String userPwd = "12345";

        try {
            Class.forName(driverName);
            System.out.println("\n加载驱动成功！");
        } catch (Exception e) {
            response.getWriter().print("101");
            e.printStackTrace();
            System.out.println("加载驱动失败！");
        }
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(dbURL, userName, userPwd);
            System.out.println("连接数据库成功！");
        } catch (Exception e) {
            response.getWriter().print("101");
            e.printStackTrace();
            conn = null;
            System.out.println("SQL Server连接失败！");
        }
        if (conn == null) {
            response.getWriter().print("101.5");
            return;
        }


        String username = request.getParameter("username");
        username = new String(username.getBytes("ISO-8859-1"), "UTF-8");
        int wordId = Integer.parseInt(request.getParameter("wordId"));
        String bookname = request.getParameter("bookname");
        int wordState = Integer.parseInt(request.getParameter("wordstate"));/////////2是认识，3是不认识

        long userId = getUserId(username);

        //////////更改

        String recitedSql1 = "update " + bookname + "_" + userId + " set state =2 where wordId=" + wordId;
        String recitedSql2 = "update " + bookname + "_" + userId + " set state =3 where wordId=" + wordId;
        PreparedStatement pstmt = null;
        int rs;
        try {
            if (wordState == 2) {
                pstmt = conn.prepareStatement(recitedSql1);
            } else {
                pstmt = conn.prepareStatement(recitedSql2);
            }
            rs = pstmt.executeUpdate();
            response.getWriter().print("200");
            System.out.println("单词上传成功!");
        } catch (SQLException e) {
            response.getWriter().print("108");
            System.out.println("单词数据上传失败!");
            throw new RuntimeException(e);
        }

    }


    protected long getUserId(String username) {
        String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String dbURL = "jdbc:sqlserver://localhost:1433;DatabaseName=recite_word";
        String userName = "sa";
        String userPwd = "12345";

        try {
            Class.forName(driverName);
//            System.out.println("加载驱动2成功！");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("加载驱动2失败！");
        }
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(dbURL, userName, userPwd);
//            System.out.println("连接数据库2成功！");
        } catch (Exception e) {
            e.printStackTrace();
            conn = null;
            System.out.println("连接数据库2失败！");
        }
        if (conn == null) {
            return 0;
        }
        String getUserIdSql = "select userId from user_table where username = '" + username + "'";

        PreparedStatement pstmt;
        try {
            pstmt = conn.prepareStatement(getUserIdSql);
        } catch (SQLException e) {
            System.out.println("获取用户信息连接失败");
            throw new RuntimeException(e);
        }
        int checkOK = 0;
        ResultSet rs;
        long userId;
        try {
            rs = pstmt.executeQuery();
            rs.next();
            userId = rs.getLong(1);
        } catch (SQLException e) {
            System.out.println("获取用户信息失败");
            throw new RuntimeException(e);
        }
        return userId;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);


    }


}
