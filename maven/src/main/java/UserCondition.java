import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
/**
 * @Description: 每个用户的背单词信息
 * @Author: Bug
 * @Date: 19:20 2022/12/8
 */

@WebServlet(urlPatterns = "/Condition")
public class UserCondition extends HttpServlet {
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
            response.getWriter().print("102");
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

        long userId = ChooseWords.getUserId(username);

        /////////////////////////////查询单词状态的个数
//        int CET4NonChooseNum,CET6NonChooseNum,CET4NonReciteNum,CET6NonReciteNum,CET4KonwnNum,CET6KonwnNum,CET4UnkonwnNum,CET6UnkonwnNum= 0;
        int[] CET4UserNum = {0, 0, 0, 0};
        int[] CET6UserNum = {0, 0, 0, 0};
        for (int i = 0; i < 4; i++) {
            String CET4UserNumSql = "select count(*) from CET4_" + userId + " where state=" + i;
            String CET6UserNumSql = "select count(*) from CET6_" + userId + " where state=" + i;
            PreparedStatement pstmt1;
            PreparedStatement pstmt2;

            try {
                pstmt1 = conn.prepareStatement(CET4UserNumSql);
                pstmt2 = conn.prepareStatement(CET6UserNumSql);
            } catch (SQLException e) {
                response.getWriter().print("108");
                System.out.println("获取单词失败");
                throw new RuntimeException(e);
            }
            try {
                ResultSet rs1 = pstmt1.executeQuery();
                ResultSet rs2 = pstmt2.executeQuery();
                rs1.next();
                rs2.next();
                CET4UserNum[i] = rs1.getInt(1);
                CET6UserNum[i] = rs2.getInt(1);

            } catch (SQLException e) {
                response.getWriter().print("108");
                System.out.println("无数据!");
                throw new RuntimeException(e);
            }
        }

        ////////////查询一共多少单词
        String driverName2 = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String dbURL2 = "jdbc:sqlserver://localhost:1433;DatabaseName=recite_word";
        String userName2 = "sa";
        String userPwd2 = "12345";

        try {
            Class.forName(driverName2);
//            System.out.println("加载驱动2成功！");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().print("101");
//            System.out.println("加载驱动2失败！");
        }
        Connection conn2 = null;
        try {
            conn2 = DriverManager.getConnection(dbURL2, userName2, userPwd2);
//            System.out.println("连接数据库2成功！");
        } catch (Exception e) {
            response.getWriter().print("102");
            e.printStackTrace();
            conn2 = null;
//            System.out.println("数据库2连接失败！");
        }

        int CET4num = 0;
        int CET6num = 0;
        String CET4numSql = "select count(*) from CET4_word";
        String CET6numSql = "select count(*) from CET6_word";
        PreparedStatement pstmt3;
        PreparedStatement pstmt4;
        try {
            pstmt3 = conn2.prepareStatement(CET4numSql);
            pstmt4 = conn2.prepareStatement(CET6numSql);
        } catch (SQLException e) {
            System.out.println("获取单词连接失败");
            response.getWriter().print("108");
            throw new RuntimeException(e);
        }
        try {
            ResultSet rs3 = pstmt3.executeQuery();
            ResultSet rs4 = pstmt4.executeQuery();
            rs3.next();
            rs4.next();
            CET4num = rs3.getInt(1);
            CET6num = rs4.getInt(1);
        } catch (SQLException e) {
            response.getWriter().print("108");
            throw new RuntimeException(e);
        }
        if (CET4num == CET4UserNum[0] + CET4UserNum[1] + CET4UserNum[2] + CET4UserNum[3] && CET6num == CET6UserNum[0] + CET6UserNum[1] + CET6UserNum[2] + CET6UserNum[3]) {
            System.out.println("查询用户单词成功!\n用户:"+username+"\nCET4:未选" + CET4UserNum[0] + " 已选未背:" + CET4UserNum[1] + " 认识:" + CET4UserNum[2] + " 不认识:" + CET4UserNum[3] + "\nCET6:未选" + CET6UserNum[0] + " 已选未背:" + CET6UserNum[1] + " 认识:" + CET6UserNum[2] + " 不认识:" + CET6UserNum[3]);

            //解决将数据传递给网页时的中文显示问题
            response.setContentType("text/html;charset=UTF-8");
            //创建的网页代码显示
            response.getWriter().print("查询用户单词成功!</br>用户:"+username+"</br>CET4:未选" + CET4UserNum[0] + " 已选未背:" + CET4UserNum[1] + " 认识:" + CET4UserNum[2] + " 不认识:" + CET4UserNum[3] + "</br>CET6:未选" + CET6UserNum[0] + " 已选未背:" + CET6UserNum[1] + " 认识:" + CET6UserNum[2] + " 不认识:" + CET6UserNum[3]);
        }
        else{
            response.getWriter().print("200");
        }


    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

}
