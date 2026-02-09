
import java.sql.*;
        import java.util.Scanner;

public class Quizapp {
    static Connection con;
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws Exception {

        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/quixapp","root","subhashish1234");

        System.out.println("QUIZ LOGIN ");
        System.out.print("Enter Roll No:  ");
        int roll = sc.nextInt();
        System.out.print("Enter Password: ");
        String pass = sc.next();

        if(login(roll, pass)) {
            int score = startQuiz();
            saveResult(roll, score);
            showResult(score);
        } else {
            System.out.println("Invalid Login!");
        }

        con.close();
    }

    static boolean login(int roll, String pass) throws Exception {
        PreparedStatement ps = con.prepareStatement("SELECT * FROM student WHERE roll_no=? AND password=?");
        ps.setInt(1, roll);
        ps.setString(2, pass);
        ResultSet rs = ps.executeQuery();
        return rs.next();
    }

    static int startQuiz() throws Exception {
        int totalScore = 0;

        for(int set=1; set<=3; set++) {
            System.out.println("\n QUESTION SET " + set + " ---");
            PreparedStatement ps = con.prepareStatement("SELECT * FROM questions WHERE question_set=?");
            ps.setInt(1, set);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                System.out.println("\n" + rs.getString("question_text"));
                System.out.println("A. " + rs.getString("option_a"));
                System.out.println("B. " + rs.getString("option_b"));
                System.out.println("C. " + rs.getString("option_c"));
                System.out.println("D. " + rs.getString("option_d"));

                System.out.print("Your Answer: ");
                String ans = sc.next().toUpperCase();

                if(ans.equals(rs.getString("correct_option")))
                    totalScore += rs.getInt("marks");
            }
        }
        return totalScore;
    }

    static void saveResult(int roll, int score) throws Exception {
        int totalMarks = 15;
        double percent = (score/15.0)*100;
        String result = percent >= 40 ? "Pass" : "Fail";

        PreparedStatement ps = con.prepareStatement(
                "INSERT INTO result (roll_no,total_marks,obtained_marks,percentage,final_result) VALUES (?,?,?,?,?)");
        ps.setInt(1, roll);
        ps.setInt(2, totalMarks);
        ps.setInt(3, score);
        ps.setDouble(4, percent);
        ps.setString(5, result);
        ps.executeUpdate();
    }

    static void showResult(int score) {
        System.out.println("\n RESULT ");
        System.out.println("Total Marks: 15");
        System.out.println("Obtained Marks: " + score);
        double percent = (score/15.0)*100;
        System.out.println("Percentage: " + percent + "%");
        System.out.println(percent >= 40 ? "PASS " : "FAIL ");
    }
}