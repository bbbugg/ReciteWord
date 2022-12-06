import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet(urlPatterns = "/Login")//目录匹配
public class LoginServlet extends HttpServlet {


//    public LoginServlet() {
//        super();
//    }

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
            System.out.println("用户名或密码错误");
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
            System.out.println("用户名或密码错误");
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
        if (username == null || userpassword == null) {
            System.out.println("用户名和密码不能为空！");
            response.getWriter().print("104");
            return;
        }

        username = new String(username.getBytes("ISO-8859-1"), "UTF-8");
        userpassword = new String(userpassword.getBytes("ISO-8859-1"), "UTF-8");
        System.out.println("username=" + username);
        System.out.println("userpassword=" + userpassword);

        String LoginSql = "select * from user_table where username=? and password= ?";

        PreparedStatement pstmt;
        try {
            pstmt = conn.prepareStatement(LoginSql);
        } catch (SQLException e) {
            System.out.println("获取登录信息连接失败");
            response.getWriter().print("105");
            throw new RuntimeException(e);
        }
        try {
            pstmt.setString(1, username);
        } catch (SQLException e) {
            System.out.println("用户名问题");
            response.getWriter().print("105");
            throw new RuntimeException(e);
        }
        try {
            pstmt.setString(2, userpassword);
        } catch (SQLException e) {
            System.out.println("密码问题");
            response.getWriter().print("105");
            throw new RuntimeException(e);
        }
        int checkOK=0;
        try {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                checkOK=1;
                System.out.println("登陆成功");
            } else {
                //解决将数据传递给网页时的中文显示问题
                //创建的网页代码显示
                response.getWriter().print("103");
                System.out.println("用户名或密码错误");
            }

        } catch (SQLException e) {

            throw new RuntimeException(e);
        }
        if(checkOK==0){
            return;
        }

        //解决将数据传递给网页时的中文显示问题
//         TODO Auto-generated method stub
        //创建的网页代码显示
        response.getWriter().print("200");

    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);


    }
}