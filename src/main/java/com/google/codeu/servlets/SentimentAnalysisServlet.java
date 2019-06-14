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

/**

 * Responds with a hard-coded message for testing purposes.

 */

@WebServlet("/sentiment")


public class SentimentAnalysisServlet extends HttpServlet {

 @Override
 public void doGet(HttpServletRequest request, HttpServletResponse response)
 throws IOException
 {


   response.setContentType("text/html");
    // statement(s) that might cause exception

  String aboutMe = Jsoup.clean(request.getParameter("about-me"), Whitelist.none());

  Sentiment sentiment = returnSentimentScore(aboutMe);

  float score = sentiment.getScore();



  String sentimentScore = " " + score;
  response.getOutputStream().println(sentimentScore);
}



 public void doPost(HttpServletRequest request, HttpServletResponse response)
  throws IOException {
    // String buttonInfo = request.getParameter("checkScore");


    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()){
      response.sendRedirect("/index.html");
      return;
    }

    //
    // String aboutMe = Jsoup.clean(request.getParameter("about-me"), Whitelist.none());
    //
    // Sentiment sentiment = returnSentimentScore(aboutMe);
    //
    // float score = sentiment.getScore();
    // response.setContentType("text/html;");
    // // response.getOutputStream().println("Your intent score is " + score);
    // long begin = System.currentTimeMillis();
    // boolean flag = true;
    //
    // while (true) {
    //
    // if ("buttonAgree".equals(buttonInfo)) {
    //       flag = true;
    //       break;
    //     }
    // else if ("buttonDisAgree".equals(buttonInfo)) {
    //       flag = false;
    //       break;
    //     }
    // }
    //
    // if (flag) {
    // User user = new User(userEmail, aboutMe);
    // datastore.storeUser(user);
    //           }
    //
    // // System.out.println("Saving me for " + userEmail);
    // response.sendRedirect("/user-page.html?user="+userEmail);

    // System.out.println("Score is "+ score);

    // User user = new User(userEmail, aboutMe);
    // datastore.storeUser(user);
    // System.out.println("Saving me for " + userEmail);
    // response.sendRedirect("/user-page.html?user="+userEmail);
  }

  public Sentiment returnSentimentScore(String text) throws IOException {

    System.out.println("Working on returnSentimentScore");
    String message = text;


    Document doc = Document.newBuilder()

        .setContent(message).setType(Document.Type.PLAIN_TEXT).build();

    LanguageServiceClient languageService = LanguageServiceClient.create();

    Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();

    float score = sentiment.getScore();

    languageService.close();

    return sentiment;
  }

}
