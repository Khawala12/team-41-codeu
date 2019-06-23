<% boolean isUserLoggedIn = (boolean) request.getAttribute("isUserLoggedIn"); %>

<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8">
    <title>CodeU Starter Project</title>
    <link rel="stylesheet" href="/css/main.css">
  </head>
  <body>
    <nav>
      <ul id="navigation">
        <li><a href="/">Home</a></li>
      
    <%
      if (isUserLoggedIn) {
        String username = (String) request.getAttribute("username");
    %>
        <li><a href="/user-page.html?user=<%= username %>">Your Page</a></li>
        <li><a href="/logout">Logout</a></li>
    <% } else {   %>
        <li><a href="/login">Login</a></li>
    <% } %>

        <li><a href="/aboutus.html">About Our Team</a></li>
        <li><a href="/community.html">Community Page</a></li>
        <li><a href="map.html">Google Map</a> </li>
        <li><a href="/feed.html">User Posts</a></li>

      </ul>
    </nav>
    <h1>CodeU Starter Project</h1>
  </body>
</html>