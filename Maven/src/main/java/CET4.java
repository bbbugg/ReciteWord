import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

/**
 * @Description: 返回四级单词状态
 * @Author: Bug
 * @Date: 20:09 2022/12/13
 */

@WebServlet(urlPatterns = "/CET4")
public class CET4 extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("\nCET4:"+ LoginServlet.getIpAddr(request));
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
            return;
        }
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(dbURL, userName, userPwd);
            System.out.println("连接数据库成功！");
        } catch (Exception e) {
            response.getWriter().print("102");
            System.out.println("数据库连接失败！");
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
        int[] CET4UserNum = {0, 0, 0, 0};
        for (int i = 0; i < 4; i++) {
            String CET4UserNumSql = "select count(*) from CET4_" + userId + " where state=" + i;
            PreparedStatement pstmt1;

            try {
                pstmt1 = conn.prepareStatement(CET4UserNumSql);
                ResultSet rs1 = pstmt1.executeQuery();
                rs1.next();
                CET4UserNum[i] = rs1.getInt(1);

            } catch (SQLException e) {
                response.getWriter().print("108");
                System.out.println(username + "无数据!");
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
        }

        int CET4num = 0;
        String CET4numSql = "select count(*) from CET4_word";
        PreparedStatement pstmt3;

        try {
            pstmt3 = conn2.prepareStatement(CET4numSql);
            ResultSet rs3 = pstmt3.executeQuery();
            rs3.next();
            CET4num = rs3.getInt(1);
        } catch (SQLException e) {
            response.getWriter().print("108");
            return;
        }


        if (CET4num == CET4UserNum[0] + CET4UserNum[1] + CET4UserNum[2] + CET4UserNum[3]) {
            System.out.println("查询用户单词成功!\n用户:" + username + "\nCET4:未选" + CET4UserNum[0] + " 认识:" + CET4UserNum[2] + " 不认识:" + CET4UserNum[3]);

            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json; charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write("{\"username\":\"" + username + "\",\"CET4NonChooseNum\":\"" + CET4UserNum[0] + "\",\"CET4KonwnNum\":\"" + CET4UserNum[2] + "\",\"CET4UnkonwnNum\":\"" + CET4UserNum[3] + "\"}");

        } else {
            response.getWriter().print("108");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
