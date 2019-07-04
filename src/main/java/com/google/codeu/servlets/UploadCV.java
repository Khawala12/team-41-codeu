package com.google.codeu.servlets;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.MultipartConfigElement;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;



import javax.servlet.http.Part;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@MultipartConfig(fileSizeThreshold=1024*1024*10, 	// 10 MB
                 maxFileSize=1024*1024*50,      	// 50 MB
                 maxRequestSize=1024*1024*100)

@WebServlet("/cv")
public class UploadCV extends HttpServlet {
    private static final long serialVersionUID = 1L;
    String UPLOAD_DIR = "uploads";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        UserService userService = UserServiceFactory.getUserService();
        String userEmail = userService.getCurrentUser().getEmail();
        String fileCvName = userEmail + ".pdf";

        File f = new File(fileCvName);

        if (f.exists()) {
          response.getOutputStream().println("CV uploaded Successfully");
        }

        else {
          response.getOutputStream().println("Please upload CV");
        }


    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        // Instantiates a client


        String user = request.getParameter("user");
        String applicationPath = request.getServletContext().getRealPath("");
        // constructs path of the dzzirectory to save uploaded file
        String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;


        UserService userService = UserServiceFactory.getUserService();

        final Part filePart = request.getPart("fileToUpload");

        String userEmail = userService.getCurrentUser().getEmail();
        // final String fileName = getFileName(filePart);

        String fileCvName = userEmail + ".pdf";




        // fileName = getFileName(part);
          // filePart.write("Uploads/" + userEmail + ".pdf");

        OutputStream out = null;
        InputStream filecontent = null;
        final PrintWriter writer = response.getWriter();

        try {

          // System.out.println("Working Directory = " +
          //     System.getProperty("user.dir"));

          File file = new File(fileCvName);


          out = new FileOutputStream(file);
          filecontent = filePart.getInputStream();

          int read = 0;
          final byte[] bytes = new byte[1024];

          while ((read = filecontent.read(bytes)) != -1) {
              out.write(bytes, 0, read);

        }
        System.out.println("CV writed Successfully");
      }
       catch (Exception fne) {

          System.out.println("Sorry");
          }

        if (!userService.isUserLoggedIn()){
          response.sendRedirect("/index.html");
          return;
        }


        response.sendRedirect("/user-page.html?user="+userEmail);


//         String targetFileStr ="";
//         List<FileItem> fileName = null;
//         Storage storage = StorageOptions.getDefaultInstance().getService();
//
//         // The name for the new bucket
//         String bucketName = "vendor-bucket13";  // "my-new-bucket";
//
//         // Creates the new bucket
//         Bucket bucket = storage.create(BucketInfo.of(bucketName));
//
//
//         //Object requestedFile = request.getParameter("filename");
//
//
//         ServletFileUpload sfu = new ServletFileUpload(new DiskFileItemFactory());
//         try {
//              fileName = sfu.parseRequest(request);
//              for(FileItem f:fileName)
//                 {
//                 try {
//                     f.write (new File("/Users/tkmajdt/Documents/workspace/File1POC1/" + f.getName()));
//                 } catch (Exception e) {
//                     // TODO Auto-generated catch block
//                     e.printStackTrace();
//                 }
//                 //targetFileStr = readFile("/Users/tkmajdt/Documents/workspace/File1POC1/" + f.getName(),Charset.defaultCharset());
//                 targetFileStr = new String(Files.readAllBytes(Paths.get("/Users/tkmajdt/Documents/workspace/File1POC1/" + f.getName())));
//                 }
//         }
//
//     //response.getWriter().print("File Uploaded Successfully");
//
//
// //String content = readFile("test.txt", Charset.defaultCharset());
//
//         catch (FileUploadException e) {
//             // TODO Auto-generated catch block
//             e.printStackTrace();
//         }
//
//
//
//         /*if(requestedFile==null)
//         {
//             response.getWriter().print("File Not Found");
//         }*/
//         /*else
//         {
//             //String fileName = (String)requestedFile;
//             FileInputStream fisTargetFile = new FileInputStream(fileName);
//
//             targetFileStr = IOUtils.toString(fisTargetFile, "UTF-8");
//         }*/
//
//
//
//         BlobId blobId = BlobId.of(bucketName, "my_blob_name");
//         //Blob blob = bucket.create("my_blob_name", "a simple blob".getBytes("UTF-8"), "text/plain");
//         Blob blob = bucket.create("my_blob_name", targetFileStr.getBytes("UTF-8"), "text/plain");
//
//         //storage.delete("vendor-bucket3");
    }

    private String getFileName(final Part part) {
    final String partHeader = part.getHeader("content-disposition");
    for (String content : part.getHeader("content-disposition").split(";")) {
        if (content.trim().startsWith("filename")) {
            return content.substring(
                    content.indexOf('=') + 1).trim().replace("\"", "");
        }
      }
      return null;
    }





}
