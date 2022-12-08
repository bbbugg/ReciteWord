import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
/**
 * @Description: 标记单词
 * @Author: Bug
 * @Date: 16:57 2022/12/8
 */

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

        long userId = ChooseWords.getUserId(username);

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


    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }


}
