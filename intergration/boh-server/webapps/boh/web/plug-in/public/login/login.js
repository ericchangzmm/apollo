(function(window){
	
	var loginname,password,validateCode,data;
	var count = 0;	
	$.submitForm(
	{
        url: "login/data_login",
        obj: "#p-login-submit",
		validate: function(){
			if(validate()){
				return true;
			}else{
				count ++;
				if(count==3){
					$(".p-login-verify").show();
					loadValidateCode();
				}else if(count>3){
					loadValidateCode();
				}
				return false;
			}			
		},
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
	
	function validate(){	
		loginname = $("#p-login-username").val();
		password = $("#p-login-password").val();
		if(loginname === ""||loginname === "用户名"){
			$("#p-login-userMessage .p-login-messageText").text("请输入用户名");	
			$("#p-login-userMessage").show();
			return false;
		}else if(password === ""){
			$("#p-login-passMessage .p-login-messageText").text("请输入密码");	
			$("#p-login-passMessage").show();
			return false;
		}	
		if(count >= 3){
			validateCode = 	$("#p-login-verifyinput").val();
			if(validateCode == ""||validateCode == "请输入右边数字"){
				$("#p-login-verifyMessage .p-login-messageText").text("请输入验证码");	
				$("#p-login-verifyMessage").show();
				return false;
			}
		}		
		return true;
	}
	
	
	$("#p-login-username").focus(function(){
		if(this.value === "用户名"){
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
			this.value = "用户名";
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
		if(this.value === "请输入右边数字"){
			this.value = "";
		}else{
			$(this).select();
		}
		$("#p-login-verifyMessage").hide();
	});
	
	$("#p-login-verifyinput").blur(function(){
		if(this.value === ""){
			this.value = "请输入右边数字";
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
				$(this).addClass("p-login-activech");
				$(".p-login-en").removeClass("p-login-activeen");
				$(".p-login-container").css({
					'font-family': 'Microsoft Yahei'
				});

			}else if($(this).hasClass("p-login-en")){
				$(this).addClass("p-login-activeen");
				$(".p-login-ch").removeClass("p-login-activech");
				$(".p-login-container").css({
					'font-family': 'georgia'
				});
			}			
		}
	});	
	
})(window);