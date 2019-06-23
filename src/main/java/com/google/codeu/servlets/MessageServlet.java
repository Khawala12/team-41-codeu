/*
 * Copyright 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.codeu.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.cloud.language.v1.Sentiment;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.Message;
import com.google.gson.Gson;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.io.File;
import java.util.List;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.DataBufferByte;
/** Handles fetching and saving {@link Message} instances. */
@WebServlet("/messages")
public class MessageServlet extends HttpServlet {

  private Datastore datastore;
  String errorString = "";
  @Override
  public void init() {
    datastore = new Datastore();
  }

  /**
   * Responds with a JSON representation of {@link Message} data for a specific user. Responds with
   * an empty array if the user is not provided.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    response.setContentType("application/json");

    String user = request.getParameter("user");

    if (user == null || user.equals("")) {
      // Request is invalid, return empty array
      response.getWriter().println("[]");
      return;
    }

    List<Message> messages = datastore.getMessages(user);
    Gson gson = new Gson();
    String json = gson.toJson(messages);

    response.getWriter().println(json);
  }

  /** Stores a new {@link Message}. */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/index.html");
      return;
    }

    String user = userService.getCurrentUser().getEmail();
    String text = Jsoup.clean(request.getParameter("text"), Whitelist.relaxed());
    String regex = "(https?://\\S+\\.(png|jpg|gif|mp4|mp3))";
    String replacement = "<img src=\"$1\" />";
    String textReplaced = text.replaceAll(regex, replacement);
    String [] textSplit = textReplaced.split("<");
    String testUrl;
    int testUrlLength;
    boolean validUrl = false;
    String outputString = textSplit[0];
    BufferedImage bufferedImage = null;

    for (int i = 1; i < textSplit.length; i++)
    {

        testUrl = textSplit[i];
        testUrlLength = testUrl.length() -3;
        testUrl = testUrl.substring(8,testUrlLength);
        testUrl = (String) testUrl;
        try {
            int startIndex = testUrl.indexOf("\"");
            int endIndex = testUrl.lastIndexOf("\"");
            testUrl = testUrl.substring(startIndex + 1, endIndex);
            System.out.println(testUrl);
            URL url = new URL(testUrl);
            bufferedImage = ImageIO.read(url.openStream());
            File outputfile = new File("/tmp/image.jpg");
            ImageIO.write(bufferedImage, "jpg", outputfile);
            validUrl = true;
            outputString += testUrl.replaceAll(regex, replacement) + " ";
            }
        catch (MalformedURLException e) {
            System.out.println(e);
            }
        catch (Exception e) {
            System.out.println(e);
            }
    }

    if (validUrl){
      try {

        ByteString imageBytes = ByteString.readFrom(new FileInputStream("/tmp/image.jpg"));
        List<EntityAnnotation> imageLabels  = getImageLabels(imageBytes);
        outputString += "<ul>";
        for(EntityAnnotation label : imageLabels){

          outputString += "<li>" + label.getDescription() + " " + label.getScore();
        }
        outputString += "</ul>";

      }
      catch(Exception e ){
      System.out.println(e);
    }
  }

    Double messageSentimentScore = returnSentimentScore(outputString);

    if (validUrl){
    }

    else {
    if (messageSentimentScore > 0) {
      outputString = outputString + " | Message emoji   &#128515 ";
      }

    if (messageSentimentScore < 0) {
      outputString = outputString + " | Message emoji   &#128542 ";
      }
    }

    Message message = new Message(user, outputString);
    datastore.storeMessage(message);
    response.sendRedirect("/user-page.html?user=" + user);
  }


  public Double returnSentimentScore(String text) throws IOException {

    Document doc = Document.newBuilder()

        .setContent(text).setType(Document.Type.PLAIN_TEXT).build();

    LanguageServiceClient languageService = LanguageServiceClient.create();
    Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();
    Double score = 0.0;
    score += sentiment.getScore();
    languageService.close();
    return score;
  }


  private List<EntityAnnotation> getImageLabels(ByteString byteString) throws IOException {
    System.out.println("I am in Get Image Labels");
    Image image = Image.newBuilder().setContent(byteString).build();
    Feature feature = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
    AnnotateImageRequest request =
        AnnotateImageRequest.newBuilder().addFeatures(feature).setImage(image).build();
    List<AnnotateImageRequest> requests = new ArrayList<>();
    requests.add(request);
    ImageAnnotatorClient client = ImageAnnotatorClient.create();
    BatchAnnotateImagesResponse batchResponse = client.batchAnnotateImages(requests);
    client.close();
    List<AnnotateImageResponse> imageResponses = batchResponse.getResponsesList();
    AnnotateImageResponse imageResponse = imageResponses.get(0);
    if (imageResponse.hasError()) {
      System.err.println("Error getting image labels: " + imageResponse.getError().getMessage());
      return null;
    }
    return imageResponse.getLabelAnnotationsList();
  }
}
