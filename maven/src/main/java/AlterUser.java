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
import java.util.Objects;

/**
 * @Description: 修改用户信息
 * @Author: Bug
 * @Date: 16:27 2022/12/9
 */
@WebServlet(urlPatterns = "/Alter")
public class AlterUser extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String dbURL = "jdbc:sqlserver://localhost:1433;DatabaseName=recite_word";
        String userName = "sa";
        String userPwd = "12345";
        try {
            Class.forName(driverName);
            System.out.println("\n加载驱动成功！");
        } catch (Exception e) {
            //解决将数据传递给网页时的中文显示问题
            response.setContentType("text/html;charset=UTF-8");
            //创建的网页代码显示
            response.getWriter().print("101");
            e.printStackTrace();
            System.out.println("加载驱动失败！");
        }
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(dbURL, userName, userPwd);
            System.out.println("连接数据库成功！");
        } catch (Exception e) {
            //解决将数据传递给网页时的中文显示问题
            //创建的网页代码显示
            response.getWriter().print("102");
            e.printStackTrace();
            conn = null;
            System.out.println("数据库连接失败！");
        }
        if (conn == null) {
            response.getWriter().print("101.5");
            return;
        }
        String username = request.getParameter("username");
        String userpassword = request.getParameter("userpassword");//服务器通过这种方式接收客户端对应键值对的值
        String name = request.getParameter("name");//服务器通过这种方式接收客户端对应键值对的值
        String sex = request.getParameter("sex");//服务器通过这种方式接收客户端对应键值对的值
        String ageStr = request.getParameter("age");//服务器通过这种方式接收客户端对应键值对的值
        String phone = request.getParameter("phone");//服务器通过这种方式接收客户端对应键值对的值

        username = new String(username.getBytes("ISO-8859-1"), "UTF-8");

        //////////////////判断是否合法
        String getUserInfoSql = "select * from user_table where username=?";
        PreparedStatement pstmt1;
        try {
            pstmt1 = conn.prepareStatement(getUserInfoSql);
            pstmt1.setString(1, username);
            ResultSet rs1 = pstmt1.executeQuery();
            if (!rs1.next()) {
                System.out.println("获取用户信息失败");
                response.getWriter().print("105");
                return;
            }
        } catch (SQLException e) {
            System.out.println("获取用户信息失败");
            response.getWriter().print("105");
            return;
        }


        if (!Objects.equals(userpassword, "")) {
            if (userpassword.length() < 6 || userpassword.length() > 20) {
                System.out.println("密码长度范围6~20！");
                response.getWriter().print("107");
                return;
            }
            if (!RegisterServlet.isLetterDigit(userpassword)) {
                System.out.println("密码仅能由大小写字母和数字组成！");
                response.getWriter().print("107");
                return;
            }
        }
        if (!Objects.equals(name, "")) {
            name = new String(name.getBytes("ISO-8859-1"), "UTF-8");
            if (name.length() > 10) {
                System.out.println("姓名长度小于等于10！");
                response.getWriter().print("110");
                return;
            }
        }
        if (!Objects.equals(sex, "")) {
            sex = new String(sex.getBytes("ISO-8859-1"), "UTF-8");
            if (!Objects.equals(sex, "男") && !Objects.equals(sex, "女")) {
                System.out.println("性别只能是\"男\"或\"女\"!");
                response.getWriter().print("113");
                return;
            }
        }
        int age = 0;
        if (!Objects.equals(ageStr, "")) {
            if (!isDigit(ageStr)) {
                System.out.println("年龄只能由数字构成！");
                response.getWriter().print("111");
                return;
            }
            if (ageStr.length() > 3) {
                System.out.println("年龄过大！");
                response.getWriter().print("111");
                return;
            }
            age = Integer.parseInt(ageStr);
        }
        if (!Objects.equals(phone, "")) {
            if (!isDigit(phone)) {
                System.out.println("电话号码只能由数字构成！");
                response.getWriter().print("112");
                return;
            }
            if (phone.length() > 11) {
                System.out.println("电话号码过长！");
                response.getWriter().print("112");
                return;
            }
        }


        //////////修改
        if (!Objects.equals(userpassword, "")) {
            String userpasswordAlterSql = "update user_table set password =? where username=?";
            PreparedStatement pstmt2;
            int rs;
            try {
                pstmt2 = conn.prepareStatement(userpasswordAlterSql);
                pstmt2.setString(1, userpassword);
                pstmt2.setString(2, username);
                rs = pstmt2.executeUpdate();
                System.out.println("密码修改成功!");
            } catch (SQLException e) {
                response.getWriter().print("105");
                System.out.println("密码修改失败!");
                return;
            }
        }
        if (!Objects.equals(name, "")) {
            String nameAlterSql = "update user_table set name =? where username=?";
            PreparedStatement pstmt3;
            int rs;
            try {
                pstmt3 = conn.prepareStatement(nameAlterSql);
                pstmt3.setString(1, name);
                pstmt3.setString(2, username);
                rs = pstmt3.executeUpdate();
                System.out.println("姓名修改成功!");
            } catch (SQLException e) {
                response.getWriter().print("105");
                System.out.println("姓名修改失败!");
                return;
            }
        }
        if (!Objects.equals(sex, "")) {
            String sexAlterSql = "update user_table set sex =? where username=?";
            PreparedStatement pstmt4;
            int rs;
            try {
                pstmt4 = conn.prepareStatement(sexAlterSql);
                pstmt4.setString(1, sex);
                pstmt4.setString(2, username);
                rs = pstmt4.executeUpdate();
                System.out.println("性别修改成功!");
            } catch (SQLException e) {
                response.getWriter().print("105");
                System.out.println("性别修改失败!");
                return;
            }
        }
        if (!Objects.equals(ageStr, "")) {
            String ageAlterSql = "update user_table set age =? where username=?";
            PreparedStatement pstmt5;
            int rs;
            try {
                pstmt5 = conn.prepareStatement(ageAlterSql);
                pstmt5.setInt(1, age);
                pstmt5.setString(2, username);
                rs = pstmt5.executeUpdate();
                System.out.println("年龄修改成功!");
            } catch (SQLException e) {
                response.getWriter().print("105");
                System.out.println("年龄修改失败!");
                return;
            }
        }
        if (!Objects.equals(phone, "")) {
            String phoneAlterSql = "update user_table set phone =? where username=?";
            PreparedStatement pstmt6;
            int rs;
            try {
                pstmt6 = conn.prepareStatement(phoneAlterSql);
                pstmt6.setString(1, phone);
                pstmt6.setString(2, username);
                rs = pstmt6.executeUpdate();
                System.out.println("电话号码修改成功!");
            } catch (SQLException e) {
                response.getWriter().print("105");
                System.out.println("电话号码修改失败!");
                return;
            }
        }

        response.getWriter().print("200");
        System.out.println("修改成功!\nusername=" + username);
        System.out.println("userpassword=" + userpassword);
        System.out.println("name=" + name);
        System.out.println("sex=" + sex);
        System.out.println("age=" + ageStr);
        System.out.println("phone=" + phone);


    }

    public static boolean isDigit(String str) {
        String regex = "^[0-9]+$";
        return str.matches(regex);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
