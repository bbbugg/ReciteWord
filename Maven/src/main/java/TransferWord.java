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
 * @Description: 传给客户端单词
 * @Author: Bug
 * @Date: 19:24 2022/12/8
 */

@WebServlet(urlPatterns = "/Transfer")
public class TransferWord extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("\nTransfer:" + LoginServlet.getIpAddr(request));
        LoginServlet.writeFile("\nTransfer:" + LoginServlet.getIpAddr(request) + "\n");
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
        String bookname = request.getParameter("bookname");
        int reciteState = Integer.parseInt(request.getParameter("recitestate"));//1是未选的，2是不认识的，3是未选+不认识的
//        int reciteState = Integer.parseInt(request.getParameter("recitestate"));//1是已选未背的，2是不认识的，3是未背+不认识的
        long userId = ChooseWords.getUserId(username, response);
        if (userId == 0) {
            return;
        }


        ///////////////////选单词
        String nonReciteNumSql = "select count(*) from " + bookname + "_" + userId + " where state=0 ";
        String nonKnownNumSql = "select count(*) from " + bookname + "_" + userId + " where state=3 ";
        String nonReciteNonKnownNumSql = "select count(*) from " + bookname + "_" + userId + " where state=0 or state=3";

        int nonNum = 0;

        PreparedStatement pstmt1;

        try {
            if (reciteState == 1) {
                pstmt1 = conn.prepareStatement(nonReciteNumSql);
            } else if (reciteState == 2) {
                pstmt1 = conn.prepareStatement(nonKnownNumSql);
            } else {
                pstmt1 = conn.prepareStatement(nonReciteNonKnownNumSql);
            }
            ResultSet rs1 = pstmt1.executeQuery();
            rs1.next();
            nonNum = rs1.getInt(1);
        } catch (SQLException e) {
            response.getWriter().print("108");
            System.out.println("获取未选单词失败");
            LoginServlet.writeFile("获取未选单词失败\n");
            return;
        }

        if (nonNum == 0) {
            response.getWriter().print("109");
            System.out.println(username + "的" + bookname + "所选单词已背完!");
            LoginServlet.writeFile(username + "的" + bookname + "所选单词已背完!\n");
            return;
        }

        int randomNonNum = (int) (Math.random() * (nonNum) + 1);
        ResultSet rs2;
        PreparedStatement pstmt2;
        String nonReciteSql = "select * from " + bookname + "_" + userId + " where state=0 ";
        String nonKnownSql = "select * from " + bookname + "_" + userId + " where state=0 ";
        String nonReciteNonKnownSql = "select * from " + bookname + "_" + userId + " where state=0 or state=3";

        try {
            if (reciteState == 1) {
                pstmt2 = conn.prepareStatement(nonReciteSql);
            } else if (reciteState == 2) {
                pstmt2 = conn.prepareStatement(nonKnownSql);
            } else {
                pstmt2 = conn.prepareStatement(nonReciteNonKnownSql);
            }
            rs2 = pstmt2.executeQuery();
        } catch (SQLException e) {
            response.getWriter().print("108");
            System.out.println("单词数据获取失败!");
            LoginServlet.writeFile("单词数据获取失败!\n");
            return;
        }
        for (int i = 0; i < randomNonNum; i++) {
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
        Word oneNewWord = ChooseWords.getNewWord(onewordId, bookname);
        if (oneNewWord == null) {
            response.getWriter().print("108");
            System.out.println(username+"传输新单词失败！");
            LoginServlet.writeFile(username+"传输新单词失败！\n");
            return;
        }

        System.out.println(username+"新添的单词");
        LoginServlet.writeFile(username+"新添的单词\n");
        System.out.println(oneNewWord.wordId + ":" + oneNewWord.word + " " + oneNewWord.wordTranslation + " " + oneNewWord.wordPhonetic);
        LoginServlet.writeFile(oneNewWord.wordId + ":" + oneNewWord.word + " " + oneNewWord.wordTranslation + " " + oneNewWord.wordPhonetic + "\n");


        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = response.getWriter();
//        Map<String, String> map = new HashMap<>();
//        map.put("wordId", String.valueOf(oneNewWord.wordId));
//        map.put("word", oneNewWord.word);
//        map.put("wordTranslation", oneNewWord.wordTranslation);
//        map.put("wordPhonetic", oneNewWord.wordPhonetic);
//        writer.write(map.toString());
        writer.write("{\"wordId\":\"" + oneNewWord.wordId + "\",\"word\":\"" + oneNewWord.word + "\",\"wordTranslation\":\"" + oneNewWord.wordTranslation + "\",\"wordPhonetic\":\"" + oneNewWord.wordPhonetic + "\"}");


    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

}
