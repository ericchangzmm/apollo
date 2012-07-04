jQuery.fn.extend({
    coord: function(e){
        if (this.length == 0) {
            return param = {
                top: 0,
                left: 0,
                width: 0,
                height: 0
            }
        }
        else {
            var param = {};
            var obj = this.get(0);
            var param = {
                height: parseInt(obj.clientHeight),
                width: parseInt(obj.clientWidth),
                top: 0,
                left: 0
            };
            do {
                param.top += obj.offsetTop || 0;
                param.left += obj.offsetLeft || 0;
                obj = obj.offsetParent
            }
            while (obj);
            return param
        }
    },
    colorbox_open: function(param){
        if (!document.getElementById("b-colorbox")) {
            $("body").append("<div id='b-colorbox'></div><div id='b-colorbox-main'><div id='b-colorbox-content'><dl id='b-colorbox-title'><dt></dt><dd onclick='$.colorbox_close()'></dd></dl><div id='b-colorbox-list'></div></div></div>");
            $.colorbox_move();
        }
        if (this.length === 0) 
            $.dataSet.colorbox_obj = window;
        else 
            $.dataSet.colorbox_obj = this[0];
        if (param.title) 
            $("#b-colorbox-title dt").html(param.title);
        $.dataSet.colorbox_content = param.content;
        $.colorbox_load();
        if (!param.move) {
            $('#b-colorbox-content').draggable("disable")
        }
        if (param.opacity) {
            $("#b-colorbox").css({
                opacity: param.opacity,
                filter: "alpha(opacity = " + param.opacity * 100 + ")"
            })
        }
    }
});
jQuery.extend({
	print:function(p){
		$("body").append("<div id='b-printContent'><div>");
		p.resizeTo("1024","650");
		p.render("b-printContent");
		setTimeout(function(){			
			window.print();
		},1000);
		return p;
	},
	countMaxPortal: function(obj){
		var p = $(obj)[0].parentNode.parentNode;
		if ($(p).hasClass("maxTitle")) {
			var child = $(obj);
			var h = document.body.clientHeight
			if ($.iscore()) 
				h = document.documentElement.clientHeight;
			child.attr("nheight", child.height()).css({
				height: h - 44
			});
		}
	},
	execMessage: function(ms){
		$("#b-message-plug label").html(ms);
		$("#b-message-plug").slideDown(200);
		clearTimeout($.core.timerMessage);
		$.core.timerMessage = setTimeout("$.closeMessage()", 5000);
	},
	closeMessage: function(){
		$("#b-message-plug").slideUp(200);
		clearTimeout($.core.timerMessage);
	},
	warnMessage:function(ms,fn,option){
		$(window).colorbox_open({opacity:0.5});
		$("#b-warnMessage-plug .b-confirm").unbind("click");
		$("#b-warnMessage-plug .close,#b-warnMessage-plug .b-cancel,#b-warnMessage-plug .b-confirm").click(function(){
			$("#b-warnMessage-plug,#b-colorbox").hide();
		});
		$("#b-warnMessage-plug").css({
			top:(document.body.clientHeight - 150)/2,
			left:(document.body.clientWidth - 450)/2
		}).show();
		if(fn){
			$("#b-warnMessage-plug .b-confirm").bind("click",function(){
				fn(option);
			});
		}
	},	
	submitForm: function(param) {
		$(param.obj).submit(function(e) {
			var options = {
				type: "POST",
				url: $.core.location + param.url,
				success: function(data) {
					value = decodeURIComponent(data);
					value = eval("(" + value + ")");
					if (param.fn) param.fn(value)
				}
			}
			if (param.validate) {
				if (param.validate()) $(this).ajaxSubmit(options)
			} else $(this).ajaxSubmit(options);
			return false
		})
	},
	markPage:function(param){
		var obj = $(param.obj);
		if (obj.length > 0) {
			$(param.obj + " .b-mark-page").remove();
			if (param.allPage > 1) {
				param.nowPage = parseInt(param.nowPage);
				$(param.obj).append("<div class='b-mark-page'></div>");
				var markPage = $(param.obj + " .b-mark-page");
				var cName = "";
				if (param.nowPage === 1) 
					cName = "at";
				markPage.append("<label class='b-last" + cName + "'>&nbsp;</label>");
				var start = 1, stop = param.allPage;
				if (param.allPage > 7) {
					if (param.nowPage > 4) {
						if (param.allPage - param.nowPage < 3) {
							start = param.allPage - 6;
							stop = param.allPage;
						}
						else {
							start = param.nowPage - 3;
							if (param.nowPage + 3 <= param.allPage)
								stop = param.nowPage + 3;
						}
					}
					else 
						stop = 7;
				}				
				if (start >= 2) {
					markPage.append("<span>" + 1 + "</span><label>...</label>");
					start += 1;
				}
				if (stop + 1 <= param.allPage) 
					stop -= 1;
				for (var i = start; i <= stop; i++) {
					if (i == param.nowPage) 
						cName = "at";
					else 
						cName = "";
					markPage.append("<span class='" + cName + "'>" + i + "</span>");
				}
				if (stop + 1 <= param.allPage) {
					markPage.append("<label>...</label><span>" + param.allPage + "</span>");
				}
				if (param.nowPage === param.allPage) 
					cName = "at";
				markPage.append("<label class='b-next" + cName + "'>&nbsp;</label>");
				
                $("span[class!='at']", markPage).each(function(){
                    this.onclick = function(){
                        param.fn(this.innerHTML);
                    }
                });
                $(".b-last", markPage).click(function(){
                    param.nowPage -= 1;
                    param.fn(param.nowPage);
                });
                $(".b-next", markPage).click(function(){
                    param.nowPage += 1;
                    param.fn(param.nowPage);
                });
			}
		}
	},
	isPad: function() {
		return (new RegExp("Windows").test(navigator.userAgent) && new RegExp("Qt").test(navigator.userAgent)) ? false: 'ontouchstart' in window && !(/hp-tablet/gi).test(navigator.appVersion)
	},
	stopEvent: function(e) {
		e = e || window.event;
		e.stopPropagation && (e.preventDefault(), e.stopPropagation()) || (e.cancelBubble = true, e.returnValue = false)
	},
	sort: function(array, isReverse) {
		var type = null;
		for (var i in array) {
			if (isNaN(array[i])) type = true
		}
		if (type) {
			isReverse ? array.sort(function(a, b) {
				return b.localeCompare(a)
			}) : array.sort(function(a, b) {
				return a.localeCompare(b)
			})
		} else {
			isReverse ? array.sort(function sortNumber(a, b) {
				return b - a
			}) : array.sort(function sortNumber(a, b) {
				return a - b
			})
		}
		return array
	},
	isCross: function() {
		var b = true;
		if ($.core.location.indexOf(window.location.hostname) == -1) b = false;
		if (window.location.port.length > 0) {
			if ($.core.location.indexOf(window.location.port) == -1) b = false
		}
		return b
	},
	jsonAjax: function(param) {
		var data = "";
		if (param.data) {
			param.data = param.data.split("&");
			for (var i = 0; i < param.data.length; i++) {
				var b = param.data[i].split("=");
				data += b[0] + "=" + encodeURIComponent(b[1]);
				if (i < param.data.length - 1) data += "&"
			}
		}
		var type = "POST",
		dataType = "",
		async = true;
		if ($.isCross()) {
			type = "GET";
			dataType = "";
			async = false
		}
        $.ajax({
            async: async,
            type: type,
            url: $.core.location + param.url,
            data: data,
            dataType: dataType,
            success: function(value){
                value = decodeURIComponent(value);
                value = eval("(" + value + ")");
                if (value.error) 
                    $.execMessage("Error:" + value.error);
                else if (param.fn) 
                    param.fn(value)
            },
            error: function(e){
                $.execMessage(e.status + ":" + e.statusText + ",system error!");
            }
        })
	},
	getCode: function(e) {
		if (!e) var e = window.event;
		if (e.keyCode) return e.keyCode;
		else if (e.which) return e.which
	},
	colorbox_close: function() {
		$("#b-colorbox,#b-colorbox-main").hide()
	},
	colorbox_load: function() {
		if ($.dataSet.colorbox_obj) {
			var coord = $($.dataSet.colorbox_obj).coord();
			var b = "fixed";
			if ($.dataSet.colorbox_obj === window) {
				b = "fixed";
				coord = {
					width: '100%',
					height: '100%',
					top: 0,
					left: 0
				}
			} else b = "absolute";
			$("#b-colorbox,#b-colorbox-main").css({
				width: coord.width,
				height: coord.height,
				top: coord.top,
				left: coord.left,
				position: b
			}).show();
			if ($.dataSet.colorbox_content) {
				var con = $($.dataSet.colorbox_content).show().coord();
				var w = coord.width;
				var h = coord.height;
				if ($.dataSet.colorbox_obj === window) {
					w = document.body.clientWidth;
					h = document.documentElement.scrollTop + document.documentElement.clientHeight
				}
				$("#b-colorbox-content").css({
					width: con.width + 20,
					height: con.height + 45,
					left: (w - (con.width + 20)) / 2,
					top: (h - (con.height + 45)) / 2
				}).show();
				$("#b-colorbox-list").append($($.dataSet.colorbox_content))
			} else $("#b-colorbox-main").hide()
		}
	},
	colorbox_move: function() {
		$('#b-colorbox-content').draggable({
			handle: "#b-colorbox-title"
		})
	},
	toClick: function(e, param) {
		var pren = $(param[0]);
		if (pren.length != 0) {
			pren = pren[0];
			if (pren && pren.style.display != "none") {
				var arr = new Array();
				for (var i = 0; i < param.length; i++) {
					var b = window.$(param[i]);
					for (var n = 0; n < b.length; n++) {
						arr[arr.length] = b[n]
					}
				}
				var all_arr = new Array();
				for (var i = 0; i < arr.length; i++) {
					$.findNodes(arr[i], all_arr)
				}
				for (var i = 0; i < arr.length; i++) {
					all_arr[all_arr.length] = arr[i]
				}
				var m = $.getTarget($.getEvent(e));
				for (var i = 0; i < all_arr.length; i++) {
					if (m == all_arr[i]) {
						return
					}
				}
				pren.style.display = "none";
				return true;
			}
		}
	},
	findNodes: function(paren, elem) {
		for (var i = 0; i < paren.children.length; i++) {
			elem[elem.length] = paren.children[i];
			if (paren.children[i].children.length > 0) {
				$.findNodes(paren.children[i], elem)
			}
		}
	},
	getEvent: function(event) {
		return event ? event: window.event
	},
	getTarget: function(event) {
		var e = this.getEvent(event);
		return e.target || e.srcElement
	},
	getTime: function(param) {
		if (!param) var param = {};
		if (param.thanTime) {
			var time = Date.parse(param.time.replace(/-/g, '/'));
			var thanTime = Date.parse(param.thanTime.replace(/-/g, '/'));
			if (time < thanTime) return - 1;
			else if (time == thanTime) return 0;
			else return 1
		} else {
            var myDate = new Date();
            if (param.dateTime) 
                myDate = param.dateTime;
            if (param.time) 
                myDate = new Date(Date.parse(param.time.replace(/-/g, '/')));
            if (param.year) 
                myDate.setFullYear(param.year + myDate.getFullYear());
            if (param.month) 
                myDate.setMonth(param.month + myDate.getMonth());
            if (param.date) 
                myDate.setDate(param.date + myDate.getDate());
            if (param.hours) 
                myDate.setHours(param.hours + myDate.getHours());
            if (param.minute) 
                myDate.setMinutes(param.minute + myDate.getMinutes());
            if (param.second) 
                myDate.setSeconds(param.second + myDate.getSeconds());
            var year = myDate.getFullYear();
            var month = myDate.getMonth() + 1;
            var dateNow = myDate.getDate();
            var hours = myDate.getHours();
            var minute = myDate.getMinutes();
            var second = myDate.getSeconds();
            var unDate = "";
            if (month < 10) 
                unDate += "0" + month;
            else 
                unDate += month;
            if (dateNow < 10) 
                unDate += "-0" + dateNow;
            else 
                unDate += "-" + dateNow;
			if (param.holiday) {
				var rtdate = null;
				if ($.core.holiday[unDate]) 
					rtdate = $.core.holiday[unDate];
				var rtdate1 = year + "-" + unDate;
				rtdate1 = $.runGLNL(rtdate1);
				if ($.core.holiday[rtdate1]) 
					rtdate = $.core.holiday[rtdate1];
				return rtdate;
			}
            unDate = year + "-" + unDate;
            if (param.weekday) 
                unDate += "  " + $.core.MUI.get("D" + myDate.getDay());            
            return unDate
		}		
	},
	convertMonry:function(money)
	{
		var MAX_NUMBER = 99999999999.99;
		var CN_ZERO = "零";
		var CN_NOE = "壹";
		var CN_TWO = "贰";
		var CN_THREE = "叁";
		var CN_FOUR = "肆";
		var CN_FIVE = "伍";
		var CN_SIX = "陆";
		var CN_SEVEN = "柒";
		var CN_EIGHT = "捌";
		var CN_NINE = "玖";
		var CN_TEN = "拾";
		var CN_HUNDRED = "百";
		var CN_THOUSAND = "千";
		var CN_TEN_THOUSAND = "万";
		var CN_HUNDRED_MILLION = "亿";
		var CN_DOLLAR = "元";
		var CN_TEN_CENT = "角";
		var CN_CENT = "分"; 
		var CN_INTEHER = "整";
		
		var integral,decimal,outputCharacters,parts,digits,radices,bigRadices,decimals,zeroCount,i,p,d,quotient,modulus;
	    money = money.toString();
		if(isNaN(money) || $.core.language === "en")
			return "";
	    if (money == "") 
	        return "";
	    if (money.match(/[^,.\d]/) != null) 
	        return "";
		if ((money).match(/^((\d{1,3}(,\d{3})*(.((\d{3},)*\d{1,3}))?)|(\d+(.\d+)?))$/) == null) 
	        return "";
	    money = money.replace(/,/g, "");
	    money = money.replace(/^0+/, "");
	    if (Number(money) > MAX_NUMBER) 
	        return "";
	    parts = money.split(".");
	    if (parts.length > 1) 
	    {
	        integral = parts[0];
	        decimal = parts[1];
	        decimal = decimal.substr(0, 2);
	    }
	    else 
	    {
	        integral = parts[0];
	        decimal = "";
	    }
	    digits = new Array(CN_ZERO, CN_NOE, CN_TWO, CN_THREE, CN_FOUR, CN_FIVE, CN_SIX, CN_SEVEN, CN_EIGHT, CN_NINE);
	    radices = new Array("", CN_TEN, CN_HUNDRED, CN_THOUSAND);
	    bigRadices = new Array("", CN_TEN_THOUSAND, CN_HUNDRED_MILLION);
	    decimals = new Array(CN_TEN_CENT, CN_CENT);
	    outputCharacters = "";
	    if (Number(integral) > 0) 
	    {
	        zeroCount = 0;
	        for (i = 0; i < integral.length; i++) 
	        {
	            p = integral.length - i - 1;
	            d = integral.substr(i, 1);
	            quotient = p / 4;
	            modulus = p % 4;
	            if (d == "0") 
	            {
	                zeroCount++;
	            }
	            else 
	            {
	                if (zeroCount > 0) 
	                    outputCharacters += digits[0];
	                zeroCount = 0;
	                outputCharacters += digits[Number(d)] + radices[modulus];
	            }
	            if (modulus == 0 && zeroCount < 4) 
	                outputCharacters += bigRadices[quotient];
	        }
			outputCharacters += CN_DOLLAR;
	    }
		if(decimal != "")
		{
			for(i=0;i< decimal.length;i++)
			{
				d = decimal.substr(i,1);
				if(d != 0)
				outputCharacters += digits[Number(d)] + decimals[i];
			}
		}
		if(outputCharacters == "")
			outputCharacters = "零元";
		if(decimal == "")
			outputCharacters += CN_INTEHER;
		return outputCharacters;
	},
    md5: function(param, needuriencode){
        function tohex(num){
            var str = "";
            var hexstr = "0123456789abcdef";
            for (var i = 0; i <= 3; i++) {
                str = str + hexstr.charAt((num >> (i * 8 + 4)) & 0x0F) + hexstr.charAt((num >> (i * 8)) & 0x0F)
            }
            return str
        }
        function convert(param){
            var str = param;
            if (needuriencode == true) {
                str = encodeURIComponent(param)
            }
            var nblk = ((str.length + 8) >> 6) + 1;
            var blks = new Array();
            var i;
            for (i = 0; i < nblk * 16; i++) {
                blks[i] = 0
            }
            for (i = 0; i < str.length; i++) {
                blks[i >> 2] |= str.charCodeAt(i) << ((i % 4) * 8)
            }
            blks[i >> 2] |= 0x80 << ((i % 4) * 8);
            blks[nblk * 16 - 2] = str.length * 8;
            return blks
        }
        function add(x, y){
            return ((x & 0x7FFFFFFF) + (y & 0x7FFFFFFF)) ^ (x & 0x80000000) ^ (y & 0x80000000)
        }
        function rol(num, cnt){
            return (num << cnt) | (num >>> (32 - cnt))
        }
        function cmn(q, a, b, x, s, t){
            return add(rol(add(add(a, q), add(x, t)), s), b)
        }
        function ff(a, b, c, d, x, s, t){
            return cmn((b & c) | ((~ b) & d), a, b, x, s, t)
        }
        function gg(a, b, c, d, x, s, t){
            return cmn((b & d) | (c & (~ d)), a, b, x, s, t)
        }
        function hh(a, b, c, d, x, s, t){
            return cmn(b ^ c ^ d, a, b, x, s, t)
        }
        function ii(a, b, c, d, x, s, t){
            return cmn(c ^ (b | (~ d)), a, b, x, s, t)
        }
        var x = convert(param);
        var a = 0x67452301;
        var b = 0xEFCDAB89;
        var c = 0x98BADCFE;
        var d = 0x10325476;
        for (var i = 0; i < x.length; i += 16) {
            var olda = a;
            var oldb = b;
            var oldc = c;
            var oldd = d;
            a = ff(a, b, c, d, x[i + 0], 7, 0xD76AA478);
            d = ff(d, a, b, c, x[i + 1], 12, 0xE8C7B756);
            c = ff(c, d, a, b, x[i + 2], 17, 0x242070DB);
            b = ff(b, c, d, a, x[i + 3], 22, 0xC1BDCEEE);
            a = ff(a, b, c, d, x[i + 4], 7, 0xF57C0FAF);
            d = ff(d, a, b, c, x[i + 5], 12, 0x4787C62A);
            c = ff(c, d, a, b, x[i + 6], 17, 0xA8304613);
            b = ff(b, c, d, a, x[i + 7], 22, 0xFD469501);
            a = ff(a, b, c, d, x[i + 8], 7, 0x698098D8);
            d = ff(d, a, b, c, x[i + 9], 12, 0x8B44F7AF);
            c = ff(c, d, a, b, x[i + 10], 17, 0xFFFF5BB1);
            b = ff(b, c, d, a, x[i + 11], 22, 0x895CD7BE);
            a = ff(a, b, c, d, x[i + 12], 7, 0x6B901122);
            d = ff(d, a, b, c, x[i + 13], 12, 0xFD987193);
            c = ff(c, d, a, b, x[i + 14], 17, 0xA679438E);
            b = ff(b, c, d, a, x[i + 15], 22, 0x49B40821);
            a = gg(a, b, c, d, x[i + 1], 5, 0xF61E2562);
            d = gg(d, a, b, c, x[i + 6], 9, 0xC040B340);
            c = gg(c, d, a, b, x[i + 11], 14, 0x265E5A51);
            b = gg(b, c, d, a, x[i + 0], 20, 0xE9B6C7AA);
            a = gg(a, b, c, d, x[i + 5], 5, 0xD62F105D);
            d = gg(d, a, b, c, x[i + 10], 9, 0x02441453);
            c = gg(c, d, a, b, x[i + 15], 14, 0xD8A1E681);
            b = gg(b, c, d, a, x[i + 4], 20, 0xE7D3FBC8);
            a = gg(a, b, c, d, x[i + 9], 5, 0x21E1CDE6);
            d = gg(d, a, b, c, x[i + 14], 9, 0xC33707D6);
            c = gg(c, d, a, b, x[i + 3], 14, 0xF4D50D87);
            b = gg(b, c, d, a, x[i + 8], 20, 0x455A14ED);
            a = gg(a, b, c, d, x[i + 13], 5, 0xA9E3E905);
            d = gg(d, a, b, c, x[i + 2], 9, 0xFCEFA3F8);
            c = gg(c, d, a, b, x[i + 7], 14, 0x676F02D9);
            b = gg(b, c, d, a, x[i + 12], 20, 0x8D2A4C8A);
            a = hh(a, b, c, d, x[i + 5], 4, 0xFFFA3942);
            d = hh(d, a, b, c, x[i + 8], 11, 0x8771F681);
            c = hh(c, d, a, b, x[i + 11], 16, 0x6D9D6122);
            b = hh(b, c, d, a, x[i + 14], 23, 0xFDE5380C);
            a = hh(a, b, c, d, x[i + 1], 4, 0xA4BEEA44);
            d = hh(d, a, b, c, x[i + 4], 11, 0x4BDECFA9);
            c = hh(c, d, a, b, x[i + 7], 16, 0xF6BB4B60);
            b = hh(b, c, d, a, x[i + 10], 23, 0xBEBFBC70);
            a = hh(a, b, c, d, x[i + 13], 4, 0x289B7EC6);
            d = hh(d, a, b, c, x[i + 0], 11, 0xEAA127FA);
            c = hh(c, d, a, b, x[i + 3], 16, 0xD4EF3085);
            b = hh(b, c, d, a, x[i + 6], 23, 0x04881D05);
            a = hh(a, b, c, d, x[i + 9], 4, 0xD9D4D039);
            d = hh(d, a, b, c, x[i + 12], 11, 0xE6DB99E5);
            c = hh(c, d, a, b, x[i + 15], 16, 0x1FA27CF8);
            b = hh(b, c, d, a, x[i + 2], 23, 0xC4AC5665);
            a = ii(a, b, c, d, x[i + 0], 6, 0xF4292244);
            d = ii(d, a, b, c, x[i + 7], 10, 0x432AFF97);
            c = ii(c, d, a, b, x[i + 14], 15, 0xAB9423A7);
            b = ii(b, c, d, a, x[i + 5], 21, 0xFC93A039);
            a = ii(a, b, c, d, x[i + 12], 6, 0x655B59C3);
            d = ii(d, a, b, c, x[i + 3], 10, 0x8F0CCC92);
            c = ii(c, d, a, b, x[i + 10], 15, 0xFFEFF47D);
            b = ii(b, c, d, a, x[i + 1], 21, 0x85845DD1);
            a = ii(a, b, c, d, x[i + 8], 6, 0x6FA87E4F);
            d = ii(d, a, b, c, x[i + 15], 10, 0xFE2CE6E0);
            c = ii(c, d, a, b, x[i + 6], 15, 0xA3014314);
            b = ii(b, c, d, a, x[i + 13], 21, 0x4E0811A1);
            a = ii(a, b, c, d, x[i + 4], 6, 0xF7537E82);
            d = ii(d, a, b, c, x[i + 11], 10, 0xBD3AF235);
            c = ii(c, d, a, b, x[i + 2], 15, 0x2AD7D2BB);
            b = ii(b, c, d, a, x[i + 9], 21, 0xEB86D391);
            a = add(a, olda);
            b = add(b, oldb);
            c = add(c, oldc);
            d = add(d, oldd)
        }
        return tohex(a) + tohex(b) + tohex(c) + tohex(d)
    },
    runGLNL: function(now){
        now = new Date(Date.parse(now.replace(/-/g, '/')));
        return $.cnMonthofDate(now) + $.cnDayofDate(now);
    },        
    daysNumberofDate: function(DateGL){
        return parseInt((Date.parse(DateGL) - Date.parse(DateGL.getFullYear() + "/1/1")) / 86400000) + 1;
    },        
    cnDateofDate: function(DateGL){
        var CnData = new Array(0x16, 0x2a, 0xda, 0x00, 0x83, 0x49, 0xb6, 0x05, 0x0e, 0x64, 0xbb, 0x00, 0x19, 0xb2, 0x5b, 0x00, 0x87, 0x6a, 0x57, 0x04, 0x12, 0x75, 0x2b, 0x00, 0x1d, 0xb6, 0x95, 0x00, 0x8a, 0xad, 0x55, 0x02, 0x15, 0x55, 0xaa, 0x00, 0x82, 0x55, 0x6c, 0x07, 0x0d, 0xc9, 0x76, 0x00, 0x17, 0x64, 0xb7, 0x00, 0x86, 0xe4, 0xae, 0x05, 0x11, 0xea, 0x56, 0x00, 0x1b, 0x6d, 0x2a, 0x00, 0x88, 0x5a, 0xaa, 0x04, 0x14, 0xad, 0x55, 0x00, 0x81, 0xaa, 0xd5, 0x09, 0x0b, 0x52, 0xea, 0x00, 0x16, 0xa9, 0x6d, 0x00, 0x84, 0xa9, 0x5d, 0x06, 0x0f, 0xd4, 0xae, 0x00, 0x1a, 0xea, 0x4d, 0x00, 0x87, 0xba, 0x55, 0x04);
        var CnMonth = new Array();
        var CnMonthDays = new Array();
        var CnBeginDay;
        var LeapMonth;
        var Bytes = new Array();
        var I;
        var CnMonthData;
        var DaysCount;
        var CnDaysCount;
        var ResultMonth;
        var ResultDay;
        var yyyy = DateGL.getFullYear();
        var mm = DateGL.getMonth() + 1;
        var dd = DateGL.getDate();
        if (yyyy < 100) 
            yyyy += 1900;
        if ((yyyy < 1997) || (yyyy > 2020)) {
            return 0;
        }
        Bytes[0] = CnData[(yyyy - 1997) * 4];
        Bytes[1] = CnData[(yyyy - 1997) * 4 + 1];
        Bytes[2] = CnData[(yyyy - 1997) * 4 + 2];
        Bytes[3] = CnData[(yyyy - 1997) * 4 + 3];
        if ((Bytes[0] & 0x80) != 0) {
            CnMonth[0] = 12;
        }
        else {
            CnMonth[0] = 11;
        }
        CnBeginDay = (Bytes[0] & 0x7f);
        CnMonthData = Bytes[1];
        CnMonthData = CnMonthData << 8;
        CnMonthData = CnMonthData | Bytes[2];
        LeapMonth = Bytes[3];
        for (I = 15; I >= 0; I--) {
            CnMonthDays[15 - I] = 29;
            if (((1 << I) & CnMonthData) != 0) {
                CnMonthDays[15 - I]++;
            }
            if (CnMonth[15 - I] == LeapMonth) {
                CnMonth[15 - I + 1] = -LeapMonth;
            }
            else {
                if (CnMonth[15 - I] < 0) {
                    CnMonth[15 - I + 1] = -CnMonth[15 - I] + 1;
                }
                else {
                    CnMonth[15 - I + 1] = CnMonth[15 - I] + 1;
                }
                if (CnMonth[15 - I + 1] > 12) {
                    CnMonth[15 - I + 1] = 1;
                }
            }
        }
        DaysCount = $.daysNumberofDate(DateGL) - 1;
        if (DaysCount <= (CnMonthDays[0] - CnBeginDay)) {
            if ((yyyy > 1901) && ($.cnDateofDate(new Date((yyyy - 1) + "/12/31")) < 0)) {
                ResultMonth = -CnMonth[0];
            }
            else {
                ResultMonth = CnMonth[0];
            }
            ResultDay = CnBeginDay + DaysCount;
        }
        else {
            CnDaysCount = CnMonthDays[0] - CnBeginDay;
            I = 1;
            while ((CnDaysCount < DaysCount) && (CnDaysCount + CnMonthDays[I] < DaysCount)) {
                CnDaysCount += CnMonthDays[I];
                I++;
            }
            ResultMonth = CnMonth[I];
            ResultDay = DaysCount - CnDaysCount;
        }
        if (ResultMonth > 0) {
            return ResultMonth * 100 + ResultDay;
        }
        else {
            return ResultMonth * 100 - ResultDay;
        }
    },        
    cnMonthofDate: function(DateGL){
        var CnMonthStr = new Array("零", "正", "二", "三", "四", "五", "六", "七", "八", "九", "十", "冬", "腊");
        var Month;
        Month = parseInt($.cnDateofDate(DateGL) / 100);
        if (Month < 0) {
            return "闰" + CnMonthStr[-Month] + "月";
        }
        else {
            return CnMonthStr[Month] + "月";
        }
    },        
    cnDayofDate: function(DateGL){
        var CnDayStr = new Array("零", "初一", "初二", "初三", "初四", "初五", "初六", "初七", "初八", "初九", "初十", "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十", "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "三十");
        var Day;
        Day = (Math.abs($.cnDateofDate(DateGL))) % 100;
        return CnDayStr[Day];
    },
	loadHtmlJson: function(content, url){
		$.ajax({
			type: "POST",
			url: url,
			dataType: 'html',
			success: function(json){
				$(content).html(json);
			},
			error: function(e){
				$.execMessage(e.status + ":" + e.statusText+",system error!");
			}
		});
	},
	iscore: function(core){
		var ua = navigator.userAgent.toLowerCase(), str = null;
		if (window.ActiveXObject) {
			str = "ie" + ua.match(/msie ([\d.]+)/)[1];
		}
		else if (window.MessageEvent && !document.getBoxObjectFor) {
			if (ua.indexOf("chrome") != -1) {
				str = "chrome" + ua.match(/chrome\/([\d.]+)/)[1];
			}
			else if (ua.indexOf("firefox") != -1) {
				str = "firefox" + ua.match(/firefox\/([\d.]+)/)[1];
			}
			else if (ua.indexOf("opera") != -1) {
				str = "opera" + ua.match(/opera\/([\d.]+)/)[1];
			}
			else if (ua.indexOf("version") != -1) {
				str = "safari" + ua.match(/version\/([\d.]+)/)[1];
			}
		}
		if (!core) {
			core = "ie";
		}
		if (str != null && str.indexOf(core.toLocaleLowerCase()) != -1) {
			return true;
		}
		else {
			return false;
		}
		
	}
});
jQuery.core = {
	language: "ch",
	location: "/boh/ajax/",
	MUI: {
		keys: {
			"Login": ["登录", "Login"],
			"LogOut": ["登出", "Login Out"],
			"Submit": ["提交", "Submit"],
			"Cancel": ["取消", "Cancel"],
			"Ok": ["确定", "Ok"],
			"Save": ["保存", "Save"],
			"Complete": ["完成", "Complete"],
			"D1": ["星期一", "Monday"],
			"D2": ["星期二", "Tuesday"],
			"D3": ["星期三", "Wednesday"],
			"D4": ["星期四", "Thursday"],
			"D5": ["星期五", "Friday"],
			"D6": ["星期六", "Saturday"],
			"D0": ["星期日", "Sunday"],
			"More":["更多","More"],
			"unmas":["无新消息","Not Message"],
			"sys1":["刷新","Refresh"],
			"sys2":["删除","Delete"]
		},
        get: function(key){
            var str = null;
            try {
                switch ($.core.language) {
                    case "ch":
                        str = $.core.MUI.keys[key][0];
                        break;
                    case "en":
                        str = $.core.MUI.keys[key][1];
                        break;
                    default:
                        str = $.core.MUI.keys[key][1];
                        break
                }
                return str
            } 
            catch (err) {
                return "WRONG KEY"
            }
        }
	},
	holiday: {
		"01-01":'元旦',
		"02-14":'情人',
		"03-08":'妇女',
		"03-15":'消费',
		"06-01": '儿童',
		"05-01": '劳动',
		"10-01": '国庆',
		"05-04": '青年',
		"09-10":'教师',
		"11-01":'万圣',
		"11-09":'消防',
		"12-25":'圣诞',
		"正月初一": '春节',
		"正月十五": '元宵',
		"五月初五": '端午',
		"八月十五": '中秋',
		"七月初七": '七夕',
		"九月初九": '重阳',
		"腊月三十": '除夕'
	},
	history:[],
	urlNodes:null,
	timerMessage:null
};
jQuery.dataSet = {
    colorbox_obj: window,
    colorbox_content: null
};
jQuery(document).bind("keyup", function(e){
    if ($.getCode(e) == 27) 
        $.colorbox_close()
});




