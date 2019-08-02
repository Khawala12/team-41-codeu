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

    <style>

    input[type=text], select {
    width: 100%;
    padding: 12px 20px;
    margin: 8px 0;
    display: inline-block;
    border: 1px solid #ccc;
    border-radius: 4px;
    box-sizing: border-box;
  }
  input[type=submit] {
    width: 100%;
    background-color: #4CAF50;
    color: white;
    padding: 14px 20px;
    margin: 8px 0;
    border: none;
    border-radius: 4px;
    cursor: pointer;
  }
  input[type=submit]:hover {
    background-color: #45a049;
  }
  div {
    border-radius: 5px;
    padding: 20px;
  }
ul{
      padding:0px;
      list-style: none;
      float: right;
    }
    ul li{
      float:left;
      width:140px;
      height:50px;
      opacity: .8;
      line-height: 40px;
      text-align: center;
      font-size: 20px;
    }
    ul li a{
      text-decoration: none;
      color: white;
      display: block;
      border: 1px solid  #fff;
      font-size: 20px;
      font-style: initial;
      background-color: black;
      transition: 0.6s ease;
    }
    ul li a:hover{
      background-color: white;
      color: #000;
    }
    ul li ul li{
      display:none;
    }
    ul li:hover ul li{
      display: block;
    }
    .logo img{
      float: left;
      width:100px;
      height: auto;
      margin-top: -15px;
    }
    .container1{
      max-width: 1200px;
      margin:auto;
    }
    ul li.active a{
      background-color: white;
      color: #000;

    }
    
  </style>
</head>


<body onload="buildUI();addLoginOrLogoutLinkToNavigation();" >
<div class="container1">
  <nav id="nav-menu-container">
    <ul class="nav-menu" id="navigation">
      <li class="logo"><img src="img/logo.png"></li>
      <li><a href="/">Home</a></li>
      <li><a href="/aboutus.html">About</a></li>
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