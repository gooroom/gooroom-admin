<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>GPMS sign-in</title>
  <script src="plugins/js-sha256/sha256.min.js"></script>
  <style>
  
	body {
        background-color: #e6e6e6;
        font-family: 'Ubuntu', sans-serif;
    }
    
    .main {
        background: linear-gradient(to bottom, #ffffff, #d2d2d2);
        width: 400px;
        height: 400px;
        margin: 7em auto;
        border-radius: 0.5em;
        box-shadow: 0px 11px 35px 2px rgba(0, 0, 0, 0.14);
    }
    
    .sign {
        padding-top: 40px;
        margin-bottom: 0px;
        color: #337dd5;
        font-weight: bold;
        font-size: 40px;
    }

    .version {
        padding-top: 4px;
        margin-top: 0px;
    	margin-bottom: 20px;
        color: #337dd5;
        font-weight: bold;
        font-size: 13px;
    }
    
    .sysmsg {
        padding-top: 4px;
        margin-top: 0px;
    	margin-bottom: 20px;
        color: #d53333;
        font-weight: bold;
        font-size: 13px;
    }
    
    .userid {
	    width: 76%;
	    color: rgb(38, 50, 56);
	    font-weight: 700;
	    font-size: 14px;
	    letter-spacing: 1px;
	    background: rgba(136, 126, 126, 0.04);
	    padding: 10px 20px;
	    border: none;
	    border-radius: 20px;
	    outline: none;
	    box-sizing: border-box;
	    border: 2px solid rgba(0, 0, 0, 0.02);
	    margin-left: 46px;
	    text-align: center;
	    margin-bottom: 17px;
    }
    
    form.form1 {
        padding-top: 10px;
    }
    
    .pass {
		width: 76%;
	    color: rgb(38, 50, 56);
	    font-weight: 700;
	    font-size: 14px;
	    letter-spacing: 1px;
	    background: rgba(136, 126, 126, 0.04);
	    padding: 10px 20px;
	    border: none;
	    border-radius: 20px;
	    outline: none;
	    box-sizing: border-box;
	    border: 2px solid rgba(0, 0, 0, 0.02);
	    margin-left: 46px;
	    text-align: center;
	    margin-bottom: 35px;
    }
    
   
    .userid:focus, .pass:focus {
        border: 2px solid rgba(0, 0, 0, 0.18) !important;
        
    }
    
    .submit {
      cursor: pointer;
        border-radius: 5em;
        color: #fff;
        background: linear-gradient(to right, #2757b0, #40a5fb);
        border: 0;
        padding-left: 40px;
        padding-right: 40px;
        padding-bottom: 10px;
        padding-top: 10px;
        font-family: 'Ubuntu', sans-serif;
        margin-left: 35%;
        font-size: 13px;
        box-shadow: 0 0 20px 1px rgba(0, 0, 0, 0.04);
    }

    .force-submit {
      cursor: pointer;
        border-radius: 5em;
        color: #fff;
        background: linear-gradient(to right, #b02727, #fb4040);
        border: 0;
        padding-left: 40px;
        padding-right: 40px;
        padding-bottom: 10px;
        padding-top: 10px;
        font-family: 'Ubuntu', sans-serif;
        margin-left: 30%;
        font-size: 13px;
        box-shadow: 0 0 20px 1px rgba(0, 0, 0, 0.04);
    }
    
    .cont {
        text-shadow: 0px 0px 3px rgba(117, 117, 117, 0.12);
        color: #9c9c9c;
        padding-top: 16px;
        text-align: center;
    }
    
    a {
        text-shadow: 0px 0px 3px rgba(117, 117, 117, 0.12);
        color: #E1BEE7;
        text-decoration: none
    }
    
    @media (max-width: 600px) {
        .main {
            border-radius: 0px;
        }  
  
  </style>
</head>

<body>
  <div class="main">
    <p class="sign" align="center">GPMS</p>
<%--    <p class="version" align="center">v2.0</p>--%>
    <p class="sysmsg" align="center">${msg}</p>
    <form name='f' action="login" method='POST' onSubmit="gr_encoding()">
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
		<input type="hidden" name="userPw" />
		<input class="userid" type="text" placeholder="Admin ID" name="userId">
		<input class="pass" type="password" placeholder="Password" name="userPassword">
		<%
		String force = "0";
		if(request.getAttribute("force") != null) {
			force = (String) request.getAttribute("force");
		}
		
		if("1".equals(force)) {
%>
		<input type="hidden" name="force" value="1" />
		<input class="force-submit" name="submit" type="submit" value="Force Sign in" />
<%
		} else {
%>			
		<input class="submit" name="submit" type="submit" value="Sign in" />
<%
		}
%>			
		<p class="cont">Gooroom Platform Management Server</p>
	</form>            
    </div>
</body>
<script type="text/javascript">
	function gr_encoding() {
		var aid = document.f.userId.value;
		var apw = document.f.userPassword.value;
		document.f.userPw.value = sha256(aid+sha256(apw));
		document.f.userPassword.value = '';
	}
</script>
</html>


