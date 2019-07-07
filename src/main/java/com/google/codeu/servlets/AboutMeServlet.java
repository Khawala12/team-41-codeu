package com.google.codeu.servlets;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.Datastore;
import com.google.cloud.language.v1.Sentiment;
import com.google.codeu.data.User;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import java.util.concurrent.TimeUnit;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import java.text.DecimalFormat;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.File;

/**


 * Responds with a hard-coded message for testing purposes.

 */

@WebServlet("/about")

public class AboutMeServlet extends HttpServlet {

  private Datastore datastore;
  String userGetCvName;


  public void init() {
    datastore = new Datastore();
  }

 @Override
 public void doGet(HttpServletRequest request, HttpServletResponse response)
 throws IOException
 {
   try
 {

  String userEmail = request.getParameter("user");
  userGetCvName = userEmail;


  if (userEmail == null || userEmail == ""){
    // So request is invalid
    return;
  }




  response.setContentType("text/html");

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

    String act = request.getParameter("act");
    String userEmailrequest = request.getParameter("user");

    System.out.println(act);


    if (act != null) {
        System.out.println("Download Submit button is pressed Successfully");

        String fileCvName = userGetCvName + ".pdf";
        File file = new File(fileCvName);
        OutputStream out = response.getOutputStream();
        FileInputStream in = new FileInputStream(file);
        byte[] buffer = new byte[4096];
        int length;
        while ((length = in.read(buffer)) > 0){
            out.write(buffer, 0, length);
        }
        in.close();
        out.flush();




        // response.sendRedirect("/user-page.html?user="+);
    }
    else if (act == null){

    UserService userServiceGet = UserServiceFactory.getUserService();
    String userEmailGet = userServiceGet.getCurrentUser().getEmail();
    System.out.println("About Me Get is called  wut userEmailGet " + userEmailGet);



    // score
    UserService userService = UserServiceFactory.getUserService();
    String userEmail = userService.getCurrentUser().getEmail();





    if (!userService.isUserLoggedIn()){
      response.sendRedirect("/index.html");
      return;
    }


    String aboutMe = Jsoup.clean(request.getParameter("about-me"), Whitelist.basic());
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
