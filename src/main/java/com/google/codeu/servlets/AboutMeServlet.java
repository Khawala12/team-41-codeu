package com.google.codeu.servlets;


import java.io.IOException;


import javax.servlet.annotation.WebServlet;

import javax.servlet.http.HttpServlet;

import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;

import com.google.appengine.api.users.UserServiceFactory;

import com.google.codeu.data.Datastore;

import com.google.codeu.data.User;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;


/**

 * Responds with a hard-coded message for testing purposes.

 */

@WebServlet("/about")

public class AboutMeServlet extends HttpServlet{

  private Datastore datastore;

  public void init(){

    datastore = new Datastore();

  }

 @Override

 public void doGet(HttpServletRequest request, HttpServletResponse response)
 throws IOException
 {

   try
{
    // statement(s) that might cause exception


  response.setContentType("text/html");
  String user_email = request.getParameter("user");
  if (user_email == null || user_email == ""){
    // So request is invalid
    return;
  }

  User user = datastore.getUser(user_email);

  String user_about_me = user.getAboutMe();




  response.getOutputStream().println(user_about_me);

 }

 catch(Exception e)
 {

   response.getOutputStream().println("Please add AboutMe");

 }


}

 public void doPost(HttpServletRequest request, HttpServletResponse response)
  throws IOException {

    UserService userService = UserServiceFactory.getUserService();

    if (!userService.isUserLoggedIn()){
      response.sendRedirect("/index.html");
      return;
    }

    String userEmail = userService.getCurrentUser().getEmail();

    String aboutMe = Jsoup.clean(request.getParameter("about-me"), Whitelist.none());
    // String aboutMe = request.getParameter("about-me");

    User user = new User(userEmail, aboutMe);
    datastore.storeUser(user);



    System.out.println("Saving me for " + userEmail);

    response.sendRedirect("/user-page.html?user="+userEmail);



  }

}