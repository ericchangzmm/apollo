(function(window){
	
	var loginname,password,validateCode,data;
	var count = 0;
	$(".p-login-login").click(function(){
		
		loginname = $("#p-login-username").val();
		password = $("#p-login-password").val();
		if(validate()){
			
			if(validateCode){
				data = "name='" + loginname + "'&pass='" + password + "'&code='" + validateCode + "'&language='en'";
			}else{
				data = "name='" + loginname + "'&pass='" + password + "'&language='en'";
			}
			
			$.jsonAjax(
			{		
	            url: "login/data_login",
	            data: data,
	            fn: function(result)
	            {
	            	if(result.success){
	            		alert("登录成功！");
	            	}else{
	            		count ++;
	        			if(count==3){
	        				$(".p-login-verify").show();
	        				loadValidateCode();
	        			}else if(count>3){
	        				loadValidateCode();
	        			}	            		           		
	            	}
	            }		
			 });
		}else{
			count ++;
			if(count==3){
				$(".p-login-verify").show();
				loadValidateCode();
			}else if(count>3){
				loadValidateCode();
			}
		}
	});
	
	function validate(){	

		if(loginname === ""||loginname === "Username"){
			$("#p-login-userMessage .p-login-messageText").text("Please input the user name");	
			$("#p-login-userMessage").show();
			return false;
		}else if(password === ""){
			$("#p-login-passMessage .p-login-messageText").text("Please input the password");	
			$("#p-login-passMessage").show();
			return false;
		}	
		if(count >= 3){
			validateCode = 	$("#p-login-verifyinput").val();
			if(validateCode == ""||validateCode == "Input right num"){
				$("#p-login-verifyMessage .p-login-messageText").text("Please input the validate code");	
				$("#p-login-verifyMessage").show();
				return false;
			}
		}		
		return true;
	}
	
	
	$("#p-login-username").focus(function(){
		if(this.value === "Username"){
			this.value = "";
		}else{
			$(this).select();
		}
		$("#p-login-userMessage").hide();
	});
	
	$("#p-login-passwordText").focus(function(){
		$(this).hide();
		$("#p-login-password").show().focus();
		$("#p-login-passMessage").hide();
	});
	
	$("#p-login-username").blur(function(){
		if(this.value === ""){
			this.value = "Username";
		}
	});
	$("#p-login-password").focus(function(){
		$(this).select();
	});
	$("#p-login-password").blur(function(){
		if(this.value === ""){
			$(this).hide();
			$("#p-login-passwordText").show();
		}
	});
	
	$("#p-login-verifyinput").focus(function(){
		if(this.value === "Input right num"){
			this.value = "";
		}else{
			$(this).select();
		}
		$("#p-login-verifyMessage").hide();
	});
	
	$("#p-login-verifyinput").blur(function(){
		if(this.value === ""){
			this.value = "Input right num";
		}
	});
	
	function loadValidateCode(){
		$.jsonAjax(
		{		
            url: "login/data_validatecode",
            data: "",
            fn: function(result)
            {
            	if(result.src){
            		if($("#p-login-verifyshow img").length!=0){
						$("#p-login-verifyshow img").attr("src", result.src);
					}else{
						var img = $("<img alt='' src='" + result.src + "'>");
						$("#p-login-verifyshow").append(img);
					}
            	}
            }		
		 });
	}
	
	$("#p-login-verifyshow").click(function(){
		loadValidateCode();
	});
	
	$(".p-login-language span").click(function(){			
		if(!($(this).hasClass("p-login-activech")||$(this).hasClass("p-login-activeen"))){			
			
			if($(this).hasClass("p-login-ch")){
				window.location = "login.html";
			}else if($(this).hasClass("p-login-en")){
				window.location = "login-en.html";
			}			
		}
	});	
	
})(window);