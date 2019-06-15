package com.google.codeu.servlets;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.Datastore;
import com.google.cloud.language.v1.Sentiment;
import com.google.codeu.servlets.SentimentAnalysisServlet;
import com.google.codeu.data.User;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
// import .SentimentAnalysisServlet;
import java.util.concurrent.TimeUnit;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import java.text.DecimalFormat;
/**

 * Responds with a hard-coded message for testing purposes.

 */

@WebServlet("/about")

public class AboutMeServlet extends HttpServlet {

  SentimentAnalysisServlet sentimentAnalysisServlet;
  private Datastore datastore;
  public void init() {
    datastore = new Datastore();
  }

 @Override
 public void doGet(HttpServletRequest request, HttpServletResponse response)
 throws IOException
 {
   try
 {
   response.setContentType("text/html");
    // statement(s) that might cause exception
  String userEmail = request.getParameter("user");
  if (userEmail == null || userEmail == ""){
    // So request is invalid
    return;
  }

  User user = datastore.getUser(userEmail);
  String userAboutMe = user.getAboutMe();
  Double  userAboutMeScore = user.getAboutMeScore();

  DecimalFormat df = new DecimalFormat("#.00");
  String userScore = df.format(userAboutMeScore);

  userAboutMe = userAboutMe + " | Status Score = " + userScore;
  response.getOutputStream().println(userAboutMe);
 }
 catch(Exception e)

 {
   System.out.println(e);
   response.getOutputStream().println("Please add AboutMe");
 }
}

 public void doPost(HttpServletRequest request, HttpServletResponse response)
  throws IOException {



    // score
    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()){
      response.sendRedirect("/index.html");
      return;
    }

    String userEmail = userService.getCurrentUser().getEmail();
    String aboutMe = Jsoup.clean(request.getParameter("about-me"), Whitelist.none());
    Sentiment sentiment = returnSentimentScore(aboutMe);

    Double score = 0.0;
    score += sentiment.getScore();
    String outputScore = " " + score;
    response.setContentType("text/html;");


    long begin = System.currentTimeMillis();
    User user = new User(userEmail, aboutMe, score);
    datastore.storeUser(user);
    response.sendRedirect("/user-page.html?user="+userEmail);
  }

  public Sentiment returnSentimentScore(String text) throws IOException {

    Document doc = Document.newBuilder()

        .setContent(text).setType(Document.Type.PLAIN_TEXT).build();

    LanguageServiceClient languageService = LanguageServiceClient.create();
    Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();
    Double score = 0.0;
    score += sentiment.getScore();
    languageService.close();
    return sentiment;
  }

}
