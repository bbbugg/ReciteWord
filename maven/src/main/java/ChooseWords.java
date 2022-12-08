import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
/**
 * @Description: 选词
 * @Author: Bug
 * @Date: 17:03 2022/12/8
 */
         
@WebServlet(urlPatterns = "/Choose")
public class ChooseWords extends HttpServlet {
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
        int wordNumber = Integer.parseInt(request.getParameter("wordnumber"));
        String bookname = request.getParameter("bookname");

        long userId = getUserId(username);


        /////////////////////////////判断没选的单词
        int nonChooseNum = 0;

        String nonChooseNumSql = "select count(*) from " + bookname + "_" + userId + " where state=0 ";
        PreparedStatement pstmt1;

        try {
            pstmt1 = conn.prepareStatement(nonChooseNumSql);
            ResultSet rs1 = pstmt1.executeQuery();
            rs1.next();
            nonChooseNum = rs1.getInt(1);
        } catch (SQLException e) {
            response.getWriter().print("108");
            System.out.println("获取未选单词失败");
            throw new RuntimeException(e);
        }
        if (nonChooseNum == 0) {
            response.getWriter().print("109");
            System.out.println(bookname + "所有单词已选完!");
            return;
        }
        ///////////////////选词
        ArrayList<Word> newWord = new ArrayList<Word>();

        PreparedStatement pstmt2;
        ResultSet rs2;
        String nonChooseSql = "select * from " + bookname + "_" + userId + " where state=0 ";

        for (int j = 0; j < wordNumber; j++) {

            int num = (int) (Math.random() * (nonChooseNum) + 1);

            try {
                pstmt2 = conn.prepareStatement(nonChooseSql);
                rs2 = pstmt2.executeQuery();
            } catch (SQLException e) {
                response.getWriter().print("108");
                System.out.println("单词数据获取失败!");
                throw new RuntimeException(e);
            }
            for (int i = 0; i < num; i++) {
                try {
                    rs2.next();
                } catch (SQLException e) {
                    response.getWriter().print("108");
                    throw new RuntimeException(e);
                }
            }
            int onewordId;
            try {
                onewordId = rs2.getInt(1);
            } catch (SQLException e) {
                response.getWriter().print("108");
                throw new RuntimeException(e);
            }
            Word oneNewWord=getNewWord(onewordId,bookname);
            if(oneNewWord==null){
                response.getWriter().print("108");
                System.out.println("添加新单词失败！");
            }

            newWord.add(oneNewWord);

            //////////////标记为已背过
            String recitedSql = "update " + bookname + "_" + userId + " set state =1 where wordId=" + oneNewWord.wordId;
            try {
                pstmt2 = conn.prepareStatement(recitedSql);
                int rs3 = pstmt2.executeUpdate();
            } catch (SQLException e) {
                response.getWriter().print("108");
                throw new RuntimeException(e);
            }
            nonChooseNum--;
        }
        System.out.println("新添的单词");
        printNewWord(newWord);
        response.setCharacterEncoding("gbk");
        PrintWriter writer = response.getWriter();
        for (Word oneNewWord : newWord) {
            writer.write(oneNewWord.wordId + ":\t" + oneNewWord.word + " \t" + oneNewWord.wordTranslation + " \t" + oneNewWord.wordPhonetic + "\n");
            writer.flush();
        }
        writer.close();//注意刷新和关闭缓存
    }

    void printNewWord(ArrayList<Word> newWord) {
        for (Word oneNewWord : newWord) {
            System.out.println(oneNewWord.wordId + ":" + oneNewWord.word + " " + oneNewWord.wordTranslation + " " + oneNewWord.wordPhonetic);
        }
    }

    static Word getNewWord(int onewordId, String bookname) {
        String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String dbURL = "jdbc:sqlserver://localhost:1433;DatabaseName=recite_word";
        String userName = "sa";
        String userPwd = "12345";

        try {
            Class.forName(driverName);
//            System.out.println("加载驱动2成功！");
        } catch (Exception e) {
            e.printStackTrace();
//            System.out.println("加载驱动2失败！");
        }
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(dbURL, userName, userPwd);
//            System.out.println("连接数据库2成功！");
        } catch (Exception e) {
            e.printStackTrace();
            conn = null;
//            System.out.println("连接数据库2失败！");
        }
        if (conn == null) {
            return null;
        }
        String chooseSql = "select * from " + bookname + "_word where wordId=" + onewordId;
        PreparedStatement pstmt;
        ResultSet rs;
        try {
            pstmt = conn.prepareStatement(chooseSql);
            rs = pstmt.executeQuery();
            rs.next();
        } catch (SQLException e) {
            System.out.println("单词数据获取失败!");
            return null;
        }
        Word oneNewWord;
        try {
            String oneword = rs.getString(2);
            String onewordTranslation = rs.getString(3);
            String onewordPhonetic = rs.getString(4);
            oneNewWord = new Word(onewordId, oneword, onewordTranslation, onewordPhonetic);
        } catch (SQLException e) {
            return null;
        }
        return oneNewWord;
    }

    protected static long getUserId(String username) {
        String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String dbURL = "jdbc:sqlserver://localhost:1433;DatabaseName=recite_word";
        String userName = "sa";
        String userPwd = "12345";

        try {
            Class.forName(driverName);
            System.out.println("加载驱动2成功！");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("加载驱动2失败！");
        }
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(dbURL, userName, userPwd);
            System.out.println("连接数据库2成功！");
        } catch (Exception e) {
            e.printStackTrace();
            conn = null;
            System.out.println("连接数据库2失败！");
        }
        if (conn == null) {
            return 0;
        }
        String getUserIdSql = "select userId from user_table where username = ?";

        PreparedStatement pstmt;
        try {
            pstmt = conn.prepareStatement(getUserIdSql);
        } catch (SQLException e) {
            System.out.println("获取用户信息连接失败");
            throw new RuntimeException(e);
        }
        try {
            pstmt.setString(1, username);
        } catch (SQLException e) {
            System.out.println("用户名问题");
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
