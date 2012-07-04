(function(window){
	var date = {};
    date.dateTime_addNum = 0;
    date.dateTime_setTime = null;
    date.dateTime_text = null;
	date.dateTime_mas = false;
	date.dateTime_open = false;
	date.exec = function(param){
		var txt = $(param.txt);
		if (txt.length > 0) {
			var exdate = new Date();
            date.dateTime_setTime = param.time ? param.time : null;            
            date.dateTime_mas = param.mas ? true : false;	
			date.dateTime_open = param.open ? false : true;		
			date.dateTime_addNum = 0;
			txt = date.dateTime_text = txt[0];			
			if (!txt.getAttribute("isBind") && date.dateTime_open) 
				date.banOperate(txt);
			var date_control = $("#b-dateTime-control");
			if (date_control.length < 1) {
				$("body").append("<div class='b-dateTime-control' id='b-dateTime-control'><div class='dateTime-title'><span class='dLeft'></span><label class='b-date-Txt'>" + (exdate.getMonth() + 1) + "&nbsp;/&nbsp;" + exdate.getFullYear() + "</label><span class='dRight'></span></div><table class='dateTime-content'><tbody></tbody></table></div>");
				if ($.core.language === "ch") 
					$("#b-dateTime-control .dateTime-content").append("<tr style='font-family:SimHei'><th>日</th><th>一</th><th>二</th><th>三</th><th>四</th><th>五</th><th>六</th></tr>");
				else 
					$("#b-dateTime-control .dateTime-content").append("<tr><th>SUN</th><th>MON</th><th>TUE</th><th>WED</th><th>THU</th><th>FRI</th><th>SAT</th></tr>");
				
				$(document).bind("click", function(e){
					var obj = $.getTarget($.getEvent(e));
					var rt = true;
					var objEx = obj;
					while (objEx.nodeName != "BODY" && objEx.nodeName != "HTML") {
						if (!$(objEx).hasClass("b-dateTime-control")) 
							objEx = objEx.parentNode;
						else {
							rt = false;
							objEx = objEx.parentNode;
						}
					}
					if ($(obj).hasClass("b-dateTime-text")) 
						rt = false;
					if (rt) 
						$("#b-dateTime-control").hide();
					document.getElementById("b-dateTime-control").onselectstart = function(){
						return false;
					}
				});
				$("#b-dateTime-control .dLeft").click(function(){
					date.dateTime_addNum -= 1;
					$.date.getDays();
				});
				$("#b-dateTime-control .dRight").click(function(){
					date.dateTime_addNum += 1;
					$.date.getDays();
				});
			}			
			$.date.getDays();			
			var coord = $(param.txt).coord();
			$("#b-dateTime-control").css({
				top: coord.top + coord.height + 2,
				left: coord.left
			}).show();
		}
	}
	date.getDays = function(){
		var table = $("#b-dateTime-control table").get(0);
		while (table.rows.length > 1) {
			table.deleteRow(1);
		}
		var exTime = new Date();
		if (date.dateTime_setTime) 
			exTime = $.getTime({
				time: date.dateTime_setTime,
				month: date.dateTime_addNum
			});
		else 
			exTime = $.getTime({
				time: $.getTime(),
				month: date.dateTime_addNum
			});
		
		var exDate = new Date(Date.parse(exTime.replace(/-/g, '/')));
		exDate.setDate("1");
		$("#b-dateTime-control .b-date-Txt").html((exDate.getMonth() + 1) + "&nbsp;/&nbsp;" + exDate.getFullYear());
		var nowday = exDate.getDay();
		exDate.setMonth(exDate.getMonth() + 1);
		exDate.setDate(0);
		var allDayNum = exDate.getDate(0);
		var newTr,newTd0,mhtml;
		var newTdNum = allDayNum;
		if(nowday != 7)
			 newTdNum = newTdNum + nowday;
        var forlength = 35;
        if (nowday + allDayNum > 35) 
            forlength = 42;
		var exDate_last = new Date (exDate);
			exDate_last.setMonth(exDate.getMonth());
			exDate_last.setDate(0);
		var allDayNum_last = exDate_last.getDate(0);	
		var allDayNum_next = 1;		
		var nowDate = new Date();	
		for (var i = 1; i <= forlength; i++) {
			if (i == 1 || i == 8 || i == 15 || i == 22 || i == 29 || i == 36) 
				newTr = table.insertRow(table.rows.length);
			if (nowday != 7 && i <= nowday) {
				newTd0 = newTr.insertCell((i % 7) - 1);
				newTd0.innerHTML = allDayNum_last - nowday + i;
				newTd0.className = "over";
				mhtml = exDate_last.getFullYear() + "-" + (exDate_last.getMonth() + 1);				
				mhtml += "-" + (allDayNum_last - nowday + i);
			}
			else if (i > newTdNum) {
				newTd0 = newTr.insertCell((i % 7) - 1);
				newTd0.innerHTML = allDayNum_next++;
				newTd0.className = "over";
				mhtml = exDate.getFullYear() + "-" + (exDate.getMonth() + 2);
				mhtml += "-" + newTd0.innerHTML;
			}
			else {
				newTd0 = newTr.insertCell((i % 7) - 1);
				newTd0.innerHTML = i - nowday;
				mhtml = exDate.getFullYear() + "-" + (exDate.getMonth() + 1);
				mhtml += "-" + (i - nowday);
				if (date.dateTime_addNum === 0 && nowDate.getDate() === i - nowday) 
					newTd0.className = "today";
			}	
			newTd0.setAttribute("m", mhtml);	
			if ($.core.language === "ch") {
				var hitxt = $.getTime({
					holiday: true,
					time: mhtml
				});				
				if (hitxt) {
					newTd0.innerHTML = hitxt;
					newTd0.className = "over hiliday";
				}
			}
			newTd0.onclick = function(){
				if ($.date.dateTime_mas) {
					date.dateTime_text.value = $.getTime({
						time: this.getAttribute("m"),
						weekday: true
					});
				}
				else {
					date.dateTime_text.value = $.getTime({
						time: this.getAttribute("m")
					});
				}
				$("#b-dateTime-control").hide();
			}
		}
	}
    date.banOperate = function(e){
		e.style.imeMode = "disabled";
		e.onkeydown = function(e){
			var keycode;
			if (window.ActiveXObject) 
				event.returnValue = false;
			else 
				e.preventDefault();
		}
		e.style.imeMode = "disabled";
		e.oncontextmenu = function(){
			return false;
		}
		e.onselectstart = function(){
			return false;
		}
		e.setAttribute("isBind", true);
	}
	jQuery.date = date;
})(window);

