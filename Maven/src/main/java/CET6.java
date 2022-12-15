import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

/**
 * @Description: 返回六级单词状态
 * @Author: Bug
 * @Date: 20:09 2022/12/13
 */

@WebServlet(urlPatterns = "/CET6")
public class CET6 extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("\nCET6:" + LoginServlet.getIpAddr(request));
        LoginServlet.writeFile("\nCET6:" + LoginServlet.getIpAddr(request) + "\n");
        LoginServlet.getTime();
        String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String dbURL = "jdbc:sqlserver://localhost:1433;DatabaseName=user_word;encrypt=false";
        String userName = "sa";
        String userPwd = "12345";
        try {
            Class.forName(driverName);
            System.out.println("加载驱动成功！");
        } catch (Exception e) {
            response.getWriter().print("101");
            System.out.println("加载驱动失败！");
            LoginServlet.writeFile("加载驱动失败！\n");
            return;
        }
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(dbURL, userName, userPwd);
            System.out.println("连接数据库成功！");
        } catch (Exception e) {
            response.getWriter().print("102");
            System.out.println("数据库连接失败！");
            LoginServlet.writeFile("数据库连接失败！\n");
            return;
        }

        String username = request.getParameter("username");
        username = new String(username.getBytes("ISO-8859-1"), "UTF-8");

        long userId = ChooseWords.getUserId(username, response);
        if (userId == 0) {
            return;
        }

        /////////////////////////////查询单词状态的个数
//        int CET4NonChooseNum,CET6NonChooseNum,CET4NonReciteNum,CET6NonReciteNum,CET4KonwnNum,CET6KonwnNum,CET4UnkonwnNum,CET6UnkonwnNum= 0;
        int[] CET6UserNum = {0, 0, 0, 0};
        for (int i = 0; i < 4; i++) {
            String CET6UserNumSql = "select count(*) from CET6_" + userId + " where state=" + i;
            PreparedStatement pstmt2;

            try {
                pstmt2 = conn.prepareStatement(CET6UserNumSql);
                ResultSet rs2 = pstmt2.executeQuery();
                rs2.next();
                CET6UserNum[i] = rs2.getInt(1);

            } catch (SQLException e) {
                response.getWriter().print("108");
                System.out.println(username + "无数据!");
                LoginServlet.writeFile(username + "无数据!\n");
                return;
            }
        }

        ////////////查询一共多少单词
        String driverName2 = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String dbURL2 = "jdbc:sqlserver://localhost:1433;DatabaseName=recite_word;encrypt=false";
        String userName2 = "sa";
        String userPwd2 = "12345";

        try {
            Class.forName(driverName2);
            System.out.println("加载驱动2成功！");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().print("101");
            System.out.println("加载驱动2失败！");
            LoginServlet.writeFile("加载驱动2失败！\n");
        }
        Connection conn2 = null;
        try {
            conn2 = DriverManager.getConnection(dbURL2, userName2, userPwd2);
            System.out.println("连接数据库2成功！");
        } catch (Exception e) {
            response.getWriter().print("102");
            e.printStackTrace();
            conn2 = null;
            System.out.println("数据库2连接失败！");
            LoginServlet.writeFile("数据库2连接失败！\n");
        }

        int CET6num = 0;
        PreparedStatement pstmt4;
        String CET6numSql = "select count(*) from CET6_word";
        try {
            pstmt4 = conn2.prepareStatement(CET6numSql);
            ResultSet rs4 = pstmt4.executeQuery();
            rs4.next();
            CET6num = rs4.getInt(1);
        } catch (SQLException e) {
            response.getWriter().print("108");
            return;
        }


        if (CET6num == CET6UserNum[0] + CET6UserNum[1] + CET6UserNum[2] + CET6UserNum[3]) {
            System.out.println("查询用户单词成功!\n用户:" + username + "\nCET6:未选" + CET6UserNum[0] + " 认识:" + CET6UserNum[2] + " 不认识:" + CET6UserNum[3]);
            LoginServlet.writeFile("查询用户单词成功!\n用户:" + username + "\nCET6:未选" + CET6UserNum[0] + " 认识:" + CET6UserNum[2] + " 不认识:" + CET6UserNum[3] + "\n");

            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json; charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write("{\"username\":\"" + username + "\",\"CET6NonChooseNum\":\"" + CET6UserNum[0] + "\",\"CET6KonwnNum\":\"" + CET6UserNum[2] + "\",\"CET6UnkonwnNum\":\"" + CET6UserNum[3] + "\"}");


        } else {
            response.getWriter().print("108");
        }

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
