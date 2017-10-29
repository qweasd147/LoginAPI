<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
	<title>Home</title>
</head>
<body>
<h1>
	Hello world!  
</h1>

<P>  The time on the server is ${serverTime}. </P>
<p>
	<a href="<c:url value="/login"/>">로그인 페이지</a>
	<a href="#none" id="checkSession">세션 확인</a>
	<a href="#none" id="logOut">로그아웃</a>
</p>
<script type="text/javascript" src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
<script type="text/javascript">
	$(document).ready(function(){
		$('#checkSession').on('click',function(){
			$.ajax({  
				  type: 'POST',  
				  url: "/checkSession",  
				  data: null,  
				  success: function(data){
					  alert('ajax 통신 성공');
				  },  
				  error : function(e){
					  console.warn("ERROR");
				  }
				});  
		});
		
		$('#logOut').on('click',function(){
			$.ajax({  
				  type: 'POST',  
				  url: "/logOut",  
				  data: null,  
				  success: function(data){
					  alert('ajax 통신 성공');
				  },  
				  error : function(e){
					  console.warn("ERROR");
				  }
				});  
		});
	});
</script>
</body>
</html>
