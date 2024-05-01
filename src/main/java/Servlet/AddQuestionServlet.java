package Servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Model.Question;

@WebServlet("/addQuestions")
public class AddQuestionServlet extends HttpServlet{
	
	private static List<Question> questionList=new LinkedList<>();
	
	private Connection connection;
	
	@Override
	public void init() throws ServletException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection=DriverManager.getConnection("jdbc:mysql://localhost:3306/mcq","root","sql123");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String button=req.getParameter("operation");
		
		if(button.equals("ADD QUESTIONS")) {
			addQuestions(req, resp);
		}else {
			if(questionList.size()>0) {
				addQuestionToTheDataBase(req, resp);
				questionList.removeAll(questionList);
			}
		}
		
	}
	
	private void addQuestions(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		String questionPaperTitle=req.getParameter("QuestionPaperTitle");
		String question=req.getParameter("Question");
		String option1=req.getParameter("Option1");
		String option2=req.getParameter("Option2");
		String option3=req.getParameter("Option3");
		String option4=req.getParameter("Option4");
		String difficultyLevel=req.getParameter("difficulty-level");
		String answerKey=req.getParameter("AnswerKey");
	
		
		questionList.add(new Question(questionPaperTitle,question,option1,option2,option3,option4,difficultyLevel,answerKey));
		
		resp.setContentType("text/html");
		PrintWriter pw=resp.getWriter();
		RequestDispatcher rd=req.getRequestDispatcher("addMcqQuestions.html");
		
		pw.print("<div align=center>");
		pw.print("<h5 style='color:green;'>QUESTION ADDED SUCCESSFULLY</h5>");
		pw.print("<div>");
		rd.include(req, resp);
		
		System.out.println(questionList.size());
	}

	private void addQuestionToTheDataBase(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		String insertQuery="insert into question values(?,?,?,?,?,?,?,?,?)";
		
		try {
			PreparedStatement pstm=connection.prepareStatement(insertQuery);
			
			for(Question temp:questionList) {
				pstm.setInt(1, 0);
				
				pstm.setString(2, temp.getQuestionPaperTitle());
				pstm.setString(3, temp.getQuestion());
				pstm.setString(4, temp.getOption1());
				pstm.setString(5, temp.getOption2());
				pstm.setString(6, temp.getOption3());
				pstm.setString(7, temp.getOption4());
				pstm.setString(8, temp.getDifficultyLevel());
				pstm.setString(9, temp.getAnswerKey());
				
				pstm.addBatch();
			}
			
			int[] count=pstm.executeBatch();
			
			resp.setContentType("text/html");
			PrintWriter pw=resp.getWriter();
			RequestDispatcher rd=req.getRequestDispatcher("addMcqQuestions.html");
			
			pw.print("<div align=center>");
			pw.print("<h5 style='color:green;'>"+count.length+" QUESTION ADDED SUCCESSFULLY</h5>");
			pw.print("<div>");
			rd.include(req, resp);
			
			System.out.println(questionList.size());
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
