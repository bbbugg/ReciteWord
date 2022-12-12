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
        System.out.println("\nChoose:"+ LoginServlet.getIpAddr(request));
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
        int wordNumber = Integer.parseInt(request.getParameter("wordnumber"));
        String bookname = request.getParameter("bookname");

        long userId = getUserId(username,response);
        if(userId==0){
            return;
        }


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
            return;
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
            if (nonChooseNum == 0) {
                System.out.println("最多添加" + j + "个单词,已添加。");
                break;
            }

            int num = (int) (Math.random() * (nonChooseNum) + 1);

            try {
                pstmt2 = conn.prepareStatement(nonChooseSql);
                rs2 = pstmt2.executeQuery();
            } catch (SQLException e) {
                response.getWriter().print("108");
                System.out.println("单词数据获取失败!");
                return;
            }
            for (int i = 0; i < num; i++) {
                try {
                    rs2.next();
                } catch (SQLException e) {
                    response.getWriter().print("108");
                    return;
                }
            }
            int onewordId;
            try {
                onewordId = rs2.getInt(1);
            } catch (SQLException e) {
                response.getWriter().print("108");
                return;
            }
            Word oneNewWord = getNewWord(onewordId, bookname);
            if (oneNewWord == null) {
                response.getWriter().print("108");
                System.out.println("添加新单词失败！");
                return;
            }

            newWord.add(oneNewWord);

            //////////////标记为已背过
            String recitedSql = "update " + bookname + "_" + userId + " set state =1 where wordId=" + oneNewWord.wordId;
            int checkOK=0;
            try {
                pstmt2 = conn.prepareStatement(recitedSql);
                int rs3 = pstmt2.executeUpdate();
                if(rs3==1){
                    checkOK=1;
                }
            } catch (SQLException e) {
                response.getWriter().print("108");
                return;
            }
            if(checkOK == 0){
                response.getWriter().print("108");
                System.out.println("添加新单词失败！");
                return;
            }
            nonChooseNum--;
        }
        System.out.println("新添的单词");
        printNewWord(newWord);
//        response.setCharacterEncoding("gbk");
//        PrintWriter writer = response.getWriter();
//        for (Word oneNewWord : newWord) {
//            writer.write(oneNewWord.wordId + ":\t" + oneNewWord.word + " \t" + oneNewWord.wordTranslation + " \t" + oneNewWord.wordPhonetic + "\n");
//            writer.flush();
//        }
//        writer.close();//注意刷新和关闭缓存


        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().print("<table width=\"100%\"border=\"1\"cellspacing=\"0\">\n" +
                "  <tr height=\"10\">\n" +
                "    <th>单词Id</th>\n" +
                "    <th>单词</th>\n" +
                "    <th>意思</th>\n" +
                "    <th>音标</th>\n" +
                "  </tr>");
        for (Word oneNewWord : newWord) {
            response.getWriter().print("  <tr align=\"center\">\n" +
                    "    <td>" + oneNewWord.wordId + "</td>\n" +
                    "    <td>" + oneNewWord.word + "</td>\n" +
                    "    <td>" + oneNewWord.wordTranslation + "</td>\n" +
                    "    <td>" + oneNewWord.wordPhonetic + "</td>\n" +
                    "  </tr>");
//            response.getWriter().print(oneNewWord.wordId + ":&#09" + oneNewWord.word + " &#09" + oneNewWord.wordTranslation + " &#09" + oneNewWord.wordPhonetic + "</br>");
        }
        response.getWriter().print("</table>");
    }

    void printNewWord(ArrayList<Word> newWord) {
        for (Word oneNewWord : newWord) {
            System.out.println(oneNewWord.wordId + ":" + oneNewWord.word + " " + oneNewWord.wordTranslation + " " + oneNewWord.wordPhonetic);
        }
    }

    static Word getNewWord(int onewordId, String bookname) {
        String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String dbURL = "jdbc:sqlserver://localhost:1433;DatabaseName=recite_word;encrypt=false";
        String userName = "sa";
        String userPwd = "12345";

        try {
            Class.forName(driverName);
        } catch (Exception e) {
            return null;
        }
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(dbURL, userName, userPwd);
        } catch (Exception e) {
            return null;
        }

        String chooseSql = "select * from " + bookname + "_word where wordId=" + onewordId;
        PreparedStatement pstmt;
        ResultSet rs;
        Word oneNewWord;
        try {
            pstmt = conn.prepareStatement(chooseSql);
            rs = pstmt.executeQuery();
            rs.next();
            String oneword = rs.getString(2);
            String onewordTranslation = rs.getString(3);
            String onewordPhonetic = rs.getString(4);
            oneNewWord = new Word(onewordId, oneword, onewordTranslation, onewordPhonetic);
        } catch (SQLException e) {
            System.out.println("单词数据获取失败!");
            return null;
        }
        return oneNewWord;
    }

    protected static long getUserId(String username,HttpServletResponse response) throws IOException {
        String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String dbURL = "jdbc:sqlserver://localhost:1433;DatabaseName=recite_word;encrypt=false";
        String userName = "sa";
        String userPwd = "12345";

        try {
            Class.forName(driverName);
            System.out.println("加载驱动2成功！");
        } catch (Exception e) {
            System.out.println("加载驱动2失败！");
            response.getWriter().print("101");
            return 0;
        }
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(dbURL, userName, userPwd);
            System.out.println("连接数据库2成功！");
        } catch (Exception e) {
            response.getWriter().print("102");
            System.out.println("连接数据库2失败！");
            return 0;
        }
        String getUserIdSql = "select userId from user_table where username = ?";

        PreparedStatement pstmt;
        ResultSet rs;
        long userId;
        try {
            pstmt = conn.prepareStatement(getUserIdSql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();
            rs.next();
            userId = rs.getLong(1);
        } catch (SQLException e) {
            System.out.println("获取用户信息失败");
            response.getWriter().print("105");
            return 0;
        }
        return userId;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
