import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 每个用户的背单词信息
 * @Author: Bug
 * @Date: 19:20 2022/12/8
 */

@WebServlet(urlPatterns = "/Condition")
public class UserCondition extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("\nCondition:" + LoginServlet.getIpAddr(request));
        LoginServlet.writeFile("\nCondition:" + LoginServlet.getIpAddr(request) + "\n");
        LoginServlet.getTime();
//        String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
//        String dbURL = "jdbc:sqlserver://localhost:1433;DatabaseName=user_word;encrypt=false";
//        String userName = "sa";
//        String userPwd = "12345";
//        try {
//            Class.forName(driverName);
//            System.out.println("\n加载驱动成功！");
//        } catch (Exception e) {
//            response.getWriter().print("101");
//            System.out.println("\n加载驱动失败！");
//            return;
//        }
//        Connection conn = null;
//        try {
//            conn = DriverManager.getConnection(dbURL, userName, userPwd);
//            System.out.println("连接数据库成功！");
//        } catch (Exception e) {
//            response.getWriter().print("102");
//            System.out.println("数据库连接失败！");
//            return;
//        }

        String username = request.getParameter("username");
//        username = new String(username.getBytes("ISO-8859-1"), "UTF-8");

        long userId = ChooseWords.getUserId(username, response);
        if (userId == 0) {
            return;
        }

//        /////////////////////////////查询单词状态的个数
////        int CET4NonChooseNum,CET6NonChooseNum,CET4NonReciteNum,CET6NonReciteNum,CET4KonwnNum,CET6KonwnNum,CET4UnkonwnNum,CET6UnkonwnNum= 0;
//        int[] CET4UserNum = {0, 0, 0, 0};
//        int[] CET6UserNum = {0, 0, 0, 0};
//        for (int i = 0; i < 4; i++) {
//            String CET4UserNumSql = "select count(*) from CET4_" + userId + " where state=" + i;
//            String CET6UserNumSql = "select count(*) from CET6_" + userId + " where state=" + i;
//            PreparedStatement pstmt1;
//            PreparedStatement pstmt2;
//
//            try {
//                pstmt1 = conn.prepareStatement(CET4UserNumSql);
//                pstmt2 = conn.prepareStatement(CET6UserNumSql);
//            } catch (SQLException e) {
//                response.getWriter().print("108");
//                System.out.println("获取单词失败");
//                return;
//            }
//            try {
//                ResultSet rs1 = pstmt1.executeQuery();
//                ResultSet rs2 = pstmt2.executeQuery();
//                rs1.next();
//                rs2.next();
//                CET4UserNum[i] = rs1.getInt(1);
//                CET6UserNum[i] = rs2.getInt(1);
//
//            } catch (SQLException e) {
//                response.getWriter().print("108");
//                System.out.println(username+"无数据!");
//                return;
//            }
//        }
//
//        ////////////查询一共多少单词
        String driverName2 = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String dbURL2 = "jdbc:sqlserver://localhost:1433;DatabaseName=recite_word;encrypt=false";
        String userName2 = "sa";
        String userPwd2 = "12345";

        try {
            Class.forName(driverName2);
            System.out.println("加载驱动成功！");
        } catch (Exception e) {
            response.getWriter().print("101");
            System.out.println("加载驱动失败！");
            LoginServlet.writeFile("加载驱动失败！\n");
            return;
        }
        Connection conn2 = null;
        try {
            conn2 = DriverManager.getConnection(dbURL2, userName2, userPwd2);
            System.out.println("连接数据库成功！");
        } catch (Exception e) {
            response.getWriter().print("102");
            LoginServlet.writeFile("数据库连接失败！\n");
            return;
        }
//
//        int CET4num = 0;
//        int CET6num = 0;
//        String CET4numSql = "select count(*) from CET4_word";
//        String CET6numSql = "select count(*) from CET6_word";
//        PreparedStatement pstmt3;
//        PreparedStatement pstmt4;
//        try {
//            pstmt3 = conn2.prepareStatement(CET4numSql);
//            pstmt4 = conn2.prepareStatement(CET6numSql);
//        } catch (SQLException e) {
//            System.out.println("获取单词连接失败");
//            response.getWriter().print("108");
//            return;
//        }
//        try {
//            ResultSet rs3 = pstmt3.executeQuery();
//            ResultSet rs4 = pstmt4.executeQuery();
//            rs3.next();
//            rs4.next();
//            CET4num = rs3.getInt(1);
//            CET6num = rs4.getInt(1);
//        } catch (SQLException e) {
//            response.getWriter().print("108");
//            return;
//        }
        ///////////查询用户信息

        String userpassword, age, phone, sex, name;

        String getUserInfoSql = "select * from user_table where userId=" + userId;
        PreparedStatement pstmt5;
        try {
            pstmt5 = conn2.prepareStatement(getUserInfoSql);
            ResultSet rs5 = pstmt5.executeQuery();
            rs5.next();
            userpassword = rs5.getString(3);
            name = rs5.getString(4);
            sex = rs5.getString(5);
            age = rs5.getString(6);
            phone = rs5.getString(7);
        } catch (SQLException e) {
            response.getWriter().print("105");
            return;
        }
//
//
//        if (CET4num == CET4UserNum[0] + CET4UserNum[1] + CET4UserNum[2] + CET4UserNum[3] && CET6num == CET6UserNum[0] + CET6UserNum[1] + CET6UserNum[2] + CET6UserNum[3]) {
//            System.out.println("查询用户单词成功!\n用户:" + username + " 密码:"+userpassword+" 姓名:"+name+" 性别:"+sex+" 年龄:"+age+" 电话:"+phone+"\nCET4:未选" + CET4UserNum[0] + " 已选未背:" + CET4UserNum[1] + " 认识:" + CET4UserNum[2] + " 不认识:" + CET4UserNum[3] + "\nCET6:未选" + CET6UserNum[0] + " 已选未背:" + CET6UserNum[1] + " 认识:" + CET6UserNum[2] + " 不认识:" + CET6UserNum[3]);
        System.out.println("查询用户单词成功!\n用户:" + username + " 密码:" + userpassword + " 姓名:" + name + " 性别:" + sex + " 年龄:" + age + " 电话:" + phone);
        LoginServlet.writeFile("查询用户单词成功!\n用户:" + username + " 密码:" + userpassword + " 姓名:" + name + " 性别:" + sex + " 年龄:" + age + " 电话:" + phone + "\n");

//            //解决将数据传递给网页时的中文显示问题
//            response.setContentType("text/html;charset=UTF-8");
//            //创建的网页代码显示
////            response.getWriter().print("查询用户单词成功!</br>用户:"+username+"</br>CET4:未选" + CET4UserNum[0] + " 已选未背:" + CET4UserNum[1] + " 认识:" + CET4UserNum[2] + " 不认识:" + CET4UserNum[3] + "</br>CET6:未选" + CET6UserNum[0] + " 已选未背:" + CET6UserNum[1] + " 认识:" + CET6UserNum[2] + " 不认识:" + CET6UserNum[3]);
//            response.getWriter().print("查询用户单词成功!</br>用户:" + username + " 密码:"+userpassword+" 姓名:"+name+" 性别:"+sex+" 年龄:"+age+" 电话:"+phone+"</br>");
//            response.getWriter().print("<table width=\"100%\"border=\"1\"cellspacing=\"0\">\n" +
//                    "  <tr height=\"10\">\n" +
//                    "    <th>单词书</th>\n" +
//                    "    <th>未选</th>\n" +
//                    "    <th>已选未背</th>\n" +
//                    "    <th>认识</th>\n" +
//                    "    <th>不认识</th>\n" +
//                    "  </tr>\n" +
//                    "  <tr align=\"center\">\n" +
//                    "    <td>CET4</td>\n" +
//                    "    <td>" + CET4UserNum[0] + "</td>\n" +
//                    "    <td>" + CET4UserNum[1] + "</td>\n" +
//                    "    <td>" + CET4UserNum[2] + "</td>\n" +
//                    "    <td>" + CET4UserNum[3] + "</td>\n" +
//                    "  </tr>\n" +
//                    "  </tr>\n" +
//                    "  <tr align=\"center\">\n" +
//                    "    <td>CET6</td>\n" +
//                    "    <td>" + CET6UserNum[0] + "</td>\n" +
//                    "    <td>" + CET6UserNum[1] + "</td>\n" +
//                    "    <td>" + CET6UserNum[2] + "</td>\n" +
//                    "    <td>" + CET6UserNum[3] + "</td>\n" +
//                    "  </tr>\n" +
//                    "</table>");


        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = response.getWriter();
//            Map<String, String> map = new HashMap<>();
//            map.put("\"username\"", "\""+username+"\"");
//            map.put("\"userpassword\"", "\""+userpassword+"\"");
//            map.put("\"name\"", "\""+name+"\"");
//            map.put("\"sex\"", "\""+sex+"\"");
//            map.put("\"age\"", "\""+age+"\"");
//            map.put("\"phone", "\""+phone+"\"");
//            map.put("\"CET4NonChooseNum\"", "\""+String.valueOf(CET4UserNum[0])+"\"");
//            map.put("\"CET4NonReciteNum\"", "\""+String.valueOf(CET4UserNum[1])+"\"");
//            map.put("\"CET4KonwnNum\"", "\""+String.valueOf(CET4UserNum[2])+"\"");
//            map.put("\"CET4UnkonwnNum\"", "\""+String.valueOf(CET4UserNum[3])+"\"");
//            map.put("\"CET6NonChooseNum\"", "\""+String.valueOf(CET6UserNum[0])+"\"");
//            map.put("\"CET6NonReciteNum\"", "\""+String.valueOf(CET6UserNum[1])+"\"");
//            map.put("\"CET6KonwnNum\"", "\""+String.valueOf(CET6UserNum[2])+"\"");
//            map.put("\"CET6UnkonwnNum\"", "\""+String.valueOf(CET6UserNum[3])+"\"");
//            writer.write(map.toString());
//            writer.write("{\"username\":\""+username+"\",\"userpassword\":\""+userpassword+"\",\"name\":\""+name+"\",\"sex\":\""+sex+"\",\"age\":\""+age+"\",\"phone\":\""+phone+"\",\"CET4NonChooseNum\":\""+CET4UserNum[0]+"\",\"CET4NonReciteNum\":\""+CET4UserNum[1]+"\",\"CET4KonwnNum\":\""+CET4UserNum[2]+"\",\"CET4UnkonwnNum\":\""+CET4UserNum[3]+"\",\"CET6NonChooseNum\":\""+CET6UserNum[0]+"\",\"CET6NonReciteNum\":\""+CET6UserNum[1]+"\",\"CET6KonwnNum\":\""+CET6UserNum[2]+"\",\"CET6UnkonwnNum\":\""+CET6UserNum[3]+"\"}");
        writer.write("{\"username\":\"" + username + "\",\"userpassword\":\"" + userpassword + "\",\"name\":\"" + name + "\",\"sex\":\"" + sex + "\",\"age\":\"" + age + "\",\"phone\":\"" + phone + "\"}");


//        } else {
//            response.getWriter().print("108");
//        }

    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
