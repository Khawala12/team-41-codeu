package com.google.codeu.data;

public class User {
  private String email;
  private String aboutMe;
  private Double  aboutMeScore;
  public User(String email, String aboutMe, Double  aboutMeScore) {
    this.email = email;
    this.aboutMe = aboutMe;
    this.aboutMeScore = aboutMeScore;
  }

  public String getEmail (){
    return email;
  }

  public String getAboutMe () {
    return aboutMe;
  }

  public Double  getAboutMeScore () {
    return aboutMeScore;
  }

}
