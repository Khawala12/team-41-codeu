<!DOCTYPE html>
<html>
<head>
<title>Message Feed</title>
<!-- <link rel="stylesheet" href="/css/main.css"> -->
<link rel="stylesheet" href="/css/user-page.css">
<script src="/js/navigation-loader.js"></script>

<script>
    
  // Fetch messages and add them to the page.
  function fetchMessages(messages){
    const url = '/feed';
    var messages = messages;
      const messageContainer = document.getElementById('message-container');
      if(messages.length == 0){
       messageContainer.innerHTML = '<p>There are no posts yet.</p>';
      }
      else{
       messageContainer.innerHTML = '';  
      }
      messages.forEach((message) => {  
       const messageDiv = buildMessageDiv(message);
       messageContainer.appendChild(messageDiv);
      });
  }
  
  function buildMessageDiv(message) {
    const headerDiv = document.createElement('div');
    headerDiv.classList.add('message-header');
    headerDiv.appendChild(document.createTextNode(
        message.user + ' - ' + new Date(message.timestamp)));

    const bodyDiv = document.createElement('div');
    bodyDiv.classList.add('message-body');
    bodyDiv.innerHTML = message.text;

    const messageDiv = document.createElement('div');
    messageDiv.classList.add('message-div');
    messageDiv.appendChild(headerDiv);
    messageDiv.appendChild(bodyDiv);

    return messageDiv;
  }
    
  // Fetch data and populate the UI of the page.
  function buildUI(){
    <%String messages = (String) request.getAttribute("messages");%>
   fetchMessages(<%=messages%>);
  }
</script>

</head>
<body onload="addLoginOrLogoutLinkToNavigation(); buildUI();" >
<div class="container1">
  <nav id="nav-menu-container">
    <ul class="nav-menu" id="navigation">
      <li class="logo"><img src="img/logo.png"></li>
      <li><a href="/">Home</a></li>
      <li class="active"><a href="/aboutus.html">About</a></li>
      <li><a href="/community.html">Community</a></li>
      <li><a href="/feed">User Posts</a></li>
      <li><a>Companies</a>
        <ul>
          <li><a href="map.html">Location</a> </li>
          <li><a href="chart.html">Reviews</a> </li>
        </ul>
      </li>
    </ul>
  </nav><!-- #nav-menu-container -->
</div>
<br>
<br>
<br>
 <div id="content">
  <h1>Message Feed</h1>
  <hr/>
  <div id="message-container">Loading...</div>
 </div>
</body>
</html>