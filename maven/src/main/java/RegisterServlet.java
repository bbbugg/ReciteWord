import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
/**
 * @Description: 注册
 * @Author: Bug
 * @Date: 17:03 2022/12/8
 */
         
@WebServlet(urlPatterns = "/Register")
public class RegisterServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String dbURL = "jdbc:sqlserver://localhost:1433;DatabaseName=recite_word";
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
            System.out.println("数据库连接失败！");
        }
        if (conn == null) {
            response.getWriter().print("101.5");
            return;
        }

        String username = request.getParameter("username");
        String userpassword = request.getParameter("userpassword");//服务器通过这种方式接收客户端对应键值对的值
//        String name = request.getParameter("name");
//        String age = request.getParameter("age");
//        String sex = request.getParameter("sex");
//        String phone = request.ge
//        tParameter("phone");
        if (username == null || userpassword == null) {
            System.out.println("用户名和密码不能为空！");
            response.getWriter().print("104");
            return;
        }
        username = new String(username.getBytes("ISO-8859-1"), "UTF-8");
        userpassword = new String(userpassword.getBytes("ISO-8859-1"), "UTF-8");
//        sex = new String(sex.getBytes("ISO-8859-1"), "UTF-8");
        System.out.println("username=" + username);
        System.out.println("userpassword=" + userpassword);
        ///////////////userId
        long userId = 0;
        String userIdSql = "select max(userId) from user_table";
        PreparedStatement pstmt1;
        try {
            pstmt1 = conn.prepareStatement(userIdSql);
        } catch (SQLException e) {
            response.getWriter().print("105");
            System.out.println("获取Id信息连接失败");
            throw new RuntimeException(e);
        }
        try {
            ResultSet rs1 = pstmt1.executeQuery();
            rs1.next();
            userId = rs1.getLong(1) + 1;

        } catch (SQLException e) {
            response.getWriter().print("105");
            throw new RuntimeException(e);
        }
        //////////////用户名
        String userNmaeSql = "select * from user_table where username=? ";
        PreparedStatement pstmt2;
        if (username.length() > 11 || username.length() < 2) {
            System.out.println("用户名长度范围2~11");
            response.getWriter().print("106");
            return;
        }
        try {
            pstmt2 = conn.prepareStatement(userNmaeSql);
        } catch (SQLException e) {
            System.out.println("获取用户名信息连接失败");
            response.getWriter().print("105");
            throw new RuntimeException(e);
        }
        try {
            pstmt2.setString(1, username);
        } catch (SQLException e) {
            System.out.println("用户名问题");
            response.getWriter().print("105");
            throw new RuntimeException(e);
        }
        try {
            ResultSet rs2 = pstmt2.executeQuery();
            if (rs2.next()) {
                System.out.println("用户名重复！");
                response.getWriter().print("106");
                return;
            }

        } catch (SQLException e) {
            response.getWriter().print("105");
            throw new RuntimeException(e);
        }


        ////////////////密码

        if (userpassword.length() < 6 || userpassword.length() > 20) {
            System.out.println("密码长度范围6~20！");
            response.getWriter().print("107");
            return;
        }
        if (!isLetterDigit(userpassword)) {
            System.out.println("密码仅能由大小写字母和数字组成！");
            response.getWriter().print("107");
            return;
        }

        ///////////添加数据库

        String insertSql = "insert into user_table values(?,?,?,'','','','')";
        PreparedStatement pstmt3;
        try {
            pstmt3 = conn.prepareStatement(insertSql);
        } catch (SQLException e) {
            System.out.println("获取数据库信息连接失败");
            response.getWriter().print("105");
            throw new RuntimeException(e);
        }
        try {
            pstmt3.setLong(1, userId);
        } catch (SQLException e) {
            System.out.println("Id问题");
            response.getWriter().print("105");
            throw new RuntimeException(e);
        }
        try {
            pstmt3.setString(2, username);
        } catch (SQLException e) {
            System.out.println("用户名问题");
            response.getWriter().print("105");
            throw new RuntimeException(e);
        }
        try {
            pstmt3.setString(3, userpassword);
        } catch (SQLException e) {
            System.out.println("密码问题");
            response.getWriter().print("105");
            throw new RuntimeException(e);
        }
        int checkOK = 0;
        try {
            int rs2 = pstmt3.executeUpdate();
            if (rs2 == 1) {
                System.out.println("OK!");
                checkOK = 1;
            }

        } catch (SQLException e) {
            System.out.println("失败！");
            response.getWriter().print("105");
            throw new RuntimeException(e);
        }

        if (checkOK == 0) {
            return;
        }

        ////////////查询一共多少单词
        int CET4num = 0;
        int CET6num = 0;
        String CET4numSql = "select count(*) from CET4_word";
        String CET6numSql = "select count(*) from CET6_word";
        PreparedStatement pstmt4;
        PreparedStatement pstmt5;
        try {
            pstmt4 = conn.prepareStatement(CET4numSql);
            pstmt5 = conn.prepareStatement(CET6numSql);
        } catch (SQLException e) {
            System.out.println("获取单词连接失败");
            response.getWriter().print("108");
            throw new RuntimeException(e);
        }
        try {
            ResultSet rs4 = pstmt4.executeQuery();
            ResultSet rs5 = pstmt5.executeQuery();
            rs4.next();
            rs5.next();
            CET4num = rs4.getInt(1);
            CET6num = rs5.getInt(1);
        } catch (SQLException e) {
            response.getWriter().print("108");
            throw new RuntimeException(e);
        }
        checkOK = 0;

        ///////////创建单词表
        String driverName2 = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String dbURL2 = "jdbc:sqlserver://localhost:1433;DatabaseName=user_word";
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
        if (conn2 == null) {
            response.getWriter().print("101.5");
            return;
        }
        String createCET4Sql = "create table CET4_" + userId + " (wordId int primary key,state int not null)";
        String createCET6Sql = "create table CET6_" + userId + " (wordId int primary key,state int not null)";
        PreparedStatement pstmt01;
        PreparedStatement pstmt02;
        try {
            pstmt01 = conn2.prepareStatement(createCET4Sql);
            pstmt02 = conn2.prepareStatement(createCET6Sql);
        } catch (SQLException e) {
            response.getWriter().print("108");
            System.out.println("获取数据库信息连接失败");
            throw new RuntimeException(e);
        }
        int checkOK2 = 0;
        try {
            int rs01 = pstmt01.executeUpdate();
            int rs02 = pstmt02.executeUpdate();
            if (rs01 == 0 && rs02 == 0) {
                System.out.println("创建初始化单词表成功!");
                checkOK2 = 1;
            }

        } catch (SQLException e) {
            response.getWriter().print("108");
            System.out.println("创建初始化单词表失败！");
            throw new RuntimeException(e);
        }
        if (checkOK2 == 0) {
            return;
        }
        /////////////////////初始化单词
        PreparedStatement pstmt03;
        for (int i = 1; i <= CET4num; i++) {
            String insertCET4Sql = "insert into CET4_" + userId + " values(" + i + ",0)";
            try {
                pstmt03 = conn2.prepareStatement(insertCET4Sql);
                pstmt03.executeUpdate();
            } catch (SQLException e) {
                response.getWriter().print("108");
                System.out.println("4级单词初始失败");
                throw new RuntimeException(e);
            }
        }
        for (int i = 1; i <= CET6num; i++) {
            String insertCET6Sql = "insert into CET6_" + userId + " values(" + i + ",0)";
            try {
                pstmt03 = conn2.prepareStatement(insertCET6Sql);
                pstmt03.executeUpdate();
            } catch (SQLException e) {
                response.getWriter().print("108");
                System.out.println("6级单词初始失败");
                throw new RuntimeException(e);
            }
        }
        int count1 = 0;
        int count2 = 0;
        try {
            PreparedStatement pstmt04 = conn2.prepareStatement("select count(*) from CET4_" + userId);
            PreparedStatement pstmt05 = conn2.prepareStatement("select count(*) from CET6_" + userId);
            ResultSet rs01 = pstmt04.executeQuery();
            ResultSet rs02 = pstmt05.executeQuery();
            rs01.next();
            rs02.next();
            count1 = rs01.getInt(1);
            count2 = rs02.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (count1 == CET4num && count2 == CET6num) {
            checkOK = 1;
            System.out.println("初始化单词成功！");
        } else {
            response.getWriter().print("108");
            System.out.println("初始化单词失败！");
            return;
        }

        if (checkOK == 0) {
            return;
        }
//        response.setContentType("text/html;charset=utf-8");
//        PrintWriter writer = response.getWriter();
//        writer.write("欢迎, " + username + ", 成为第" + userId + "位用户, 密码为" + userpassword);//这里可以向客户端返回相应的字符串,客户端可用String res=response.body().string();来接收该值
//        writer.flush();
//        writer.close();//注意刷新和关闭缓存
        //解决将数据传递给网页时的中文显示问题

//        response.getWriter().print("欢迎, " + username + ", 成为第" + userId + "位用户, 密码为" + userpassword);

        response.getWriter().print("200");
        System.out.println("注册成功" + ", 成为第" + userId + "位用户。");

    }


    public static boolean isLetterDigit(String str) {
        String regex = "^[a-z0-9A-Z]+$";
        return str.matches(regex);
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

}
