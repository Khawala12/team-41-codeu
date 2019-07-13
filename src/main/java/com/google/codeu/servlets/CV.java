package com.google.codeu.servlets;

import javax.servlet.annotation.WebServlet;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;



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

@WebServlet("/create")

public class CV extends HttpServlet {
	private final GcsService gcsService =
		    GcsServiceFactory.createGcsService(
		        new RetryParams.Builder()
		            .initialRetryDelayMillis(10)
		            .retryMaxAttempts(10)
		            .totalRetryPeriodMillis(15000)
		            .build());
	
	private static final int BUFFER_SIZE = 2 * 1024 * 1024;
	private final String bucket = "[cv_store]";
	
	private String storeImage(Part image) throws IOException {

		  String filename = uploadedFilename(image); // Extract filename
		  GcsFileOptions.Builder builder = new GcsFileOptions.Builder();

		  builder.acl("public-read"); // Set the file to be publicly viewable
		  GcsFileOptions instance = GcsFileOptions.getDefaultInstance();
		  GcsOutputChannel outputChannel;
		  GcsFilename gcsFile = new GcsFilename(bucket, filename);
		  outputChannel = gcsService.createOrReplace(gcsFile, instance);
		  copy(filePart.getInputStream(), Channels.newOutputStream(outputChannel));

		  return filename; // Return the filename without GCS/bucket appendage
		}
	private String uploadedFilename(final Part part) {

		  final String partHeader = part.getHeader("content-disposition");

		  for (String content : part.getHeader("content-disposition").split(";")) {
		    if (content.trim().startsWith("filename")) {
		      // Append a date and time to the filename
		      DateTimeFormatter dtf = DateTimeFormat.forPattern("-YYYY-MM-dd-HHmmssSSS");
		      DateTime dt = DateTime.now(DateTimeZone.UTC);
		      String dtString = dt.toString(dtf);
		      final String fileName =
		          dtString + content.substring(content.indexOf('=') + 1).trim().replace("\"", "");

		      return fileName;
		    }
		  }
		  return null;
		}
	
	private void copy(InputStream input, OutputStream output) throws IOException {

		  try {
		    byte[] buffer = new byte[BUFFER_SIZE];
		    int bytesRead = input.read(buffer);
		    while (bytesRead != -1) {
		      output.write(buffer, 0, bytesRead);
		      bytesRead = input.read(buffer);
		    }
		  } finally {
		    input.close();
		    output.close();
		  }

		}
	
	final String createImageTableSql =
		    "CREATE TABLE IF NOT EXISTS images ( image_id INT NOT NULL "
		        + "AUTO_INCREMENT, filename VARCHAR(256) NOT NULL, "
		        + "PRIMARY KEY (image_id) )";

		conn.createStatement().executeUpdate(createImageTableSql);
}
