import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.*;

@WebServlet(urlPatterns = "/CheckUser")
public class CheckUserServlet extends HttpServlet {
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
            e.printStackTrace();
            response.getWriter().print("101");
            System.out.println("加载驱动失败！");
        }
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(dbURL, userName, userPwd);
            System.out.println("连接数据库成功！");
        } catch (Exception e) {
            e.printStackTrace();
            conn = null;
            response.getWriter().print("102");
            System.out.println("SQL Server连接失败！");
        }
        if (conn == null) {
            response.getWriter().print("101.5");
            return;
        }

        String LoginSql = "select * from user_table ";

        PreparedStatement pstmt;
        try {
            pstmt = conn.prepareStatement(LoginSql);
        } catch (SQLException e) {
            response.getWriter().print("105");
            System.out.println("获取登录信息连接失败");
            throw new RuntimeException(e);
        }
        int checkOK = 0;
        ResultSet rs;
        try {
            rs = pstmt.executeQuery();
            response.setCharacterEncoding("gbk");
            PrintWriter writer = response.getWriter();
            while (rs.next()) {
                long id = rs.getLong(1);
                String username = rs.getString(2);
                String password = rs.getString(3);
                String name = rs.getString(4);
                String sex = rs.getString(5);
                String age = rs.getString(6);
                String phone = rs.getString(7);
                writer.write("id:" + id + "\tusername:" + username + "\tpassword:" + password + "\tname:" + name + "\tsex:" + sex + "\tage:" + age + "\tphone:" + phone + "\n");//这里可以向客户端返回相应的字符串,客户端可用String res=response.body().string();来接收该值
                writer.flush();
            }
            writer.close();//注意刷新和关闭缓存
            System.out.println("输出完成!");
        } catch (SQLException e) {
            response.getWriter().print("105");
            throw new RuntimeException(e);
        }


    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
