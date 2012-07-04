//加载动画
$(".b-load-page span").animate({
    width: 445
}, 1500, function(){
    //显示
    $(".b-load-page").hide();
});

$(document).ready(function(){
    //数据请求发送
    $.jsonAjax({
        url: "public/initializeIndex",
        fn: _pageRetuen
    });
    function _pageRetuen(data){
        //软件信息
        $(".b-logo-info").html("<img src='" + data.logo + "' alt='LOGO'><h1>" + data.name + "</h1>");
        //设定语言
        $.core.language = data.language;
        if (data.language == "en") 
            $("body").css({
                fontFamily: "Georgia"
            });
        //用户信息
        $(".b-user-icon").html("<img src='" + data.user.head + "' alt=''>");
        $("#b-user-text").attr("title", data.user.userName + "[" + data.user.userNo + "]").html("<label id='b-user-name'>" + data.user.userName + "</label>[" + data.user.userNo + "]");
        //系统菜单-系统菜单触发
        bindSystems(data.system);
        //项目菜单-事件触发
        bindMenus(data.menu);
        //绑定portal对象数据
        bindPortal(data.portal,data.character);
        
		document.documentElement.scrollTop = 0;
 		document.body.scrollTop = 0;		
    }
    /**
     * 绑定三级应用事件
     */
    function bindTreeEvent(){
		$(".b-two-list dl").click(function(){
			execControlone(this.getAttribute("dataid"), null);
		});
		
		$("#b-Control-one .b-Control-title .close").click(function(){
			$("#b-Control-one").hide();
			$("#b-Control-two").show();
			$("#b-portal-parent").hide();
		});
		$("#b-Control-one .b-Control-title .max").hide();		
	}
    /**
     * 默认加载项
     * @param {Object} data
     */
    function defaulLoadInfo(data){
        var n = 0;
        if (data) {
            $("#b-menu-one li").each(function(i){
                if ($(this).attr("dataid") == data.oneMenu) {
                    n = i;
                }
            });
            $("#b-more-links dd").each(function(i){
                if ($(this).attr("dataid") == data.oneMenu) {
                    n = 4;
                    oneMenuMore(this);
                }
            });
            
            //调出默认应用
            execControlone(data.threeMenu, data.type);
        }       
    }
    function execControlone(id, type){
		$("#b-Control-two").hide();
		var h = document.body.clientHeight
		if ($.iscore()) 
			h = document.documentElement.clientHeight;
		$("#b-Control-one .b-Control-content").html("");
		$("#b-Control-one").css({
			height: h - 52
		}).show();
		$("#b-Control-one .b-Control-content").css({
			height: h - 96
		});
		$("#b-portal-parent").hide();
		$.jsonAjax({
			url: "public/control",
			data: "id=" + id + "&type=" + type,
			fn: function(data){
				$("#b-Control-one .b-Control-content").attr("dataid", data.id);
				$("#b-Control-one .b-Control-title label").html(data.name);
				$.loadHtmlJson("#b-Control-one .b-Control-content", data.url);
			}
		});
		$.core.urlNodes = "public/control?id=" + id + "&type=" + type;
		setHistory($.core.urlNodes);
	}
    /**
     * 绑定Portal
     */
    function bindPortal(data,character){
        $("#b-portal-left,#b-portal-right").html("");
        if (data.length > 0) {
            for (var i = 0; i < data.length; i++) {
                var div = document.createElement("div");
                div.className = "b-portal-plug";
                div.id = data[i].id;
                var title = document.createElement("div");
                title.className = "b-portal-title";
                div.appendChild(title);
                var ltext = document.createElement("label");
                ltext.innerHTML = data[i].name;
                title.appendChild(ltext);
                var botton = document.createElement("div");
                botton.className = "b-portal-botton";
                title.appendChild(botton);
                if (data[i].authority.length > 0) {
                    var dplan = document.createElement("div");
                    dplan.className = "b-sys-plan";
                    botton.appendChild(dplan);
                    dplan.innerHTML = "<span class='sys'></span>";
                    var pul = document.createElement("ul");
                    dplan.appendChild(pul);
                    for (var s = 0; s < data[i].authority.length; s++) {
                        var pli = document.createElement("li");
                        pli.className = "b-sysList-" + data[i].authority[s];
                        pli.innerHTML = $.core.MUI.get("sys" + data[i].authority[s]);
                        pul.appendChild(pli);
                    }
                }
                botton.innerHTML += "<span class='max'></span>";
                var content = document.createElement("div");
                content.className = "b-portal-content";
                div.appendChild(content);
                if (data[i].seat == 1) 
                    $("#b-portal-left").append(div);
                else 
                    $("#b-portal-right").append(div);
                
                $.loadHtmlJson(content, data[i].url);
            }
            //绑定portal对象事件
            bindPortalEvent(character);
        }
    }
    /**
     * 绑定Portal 操作事件
     */
    function bindPortalEvent(character){
        if ($.isPad()) {
            $(".b-portal-plug .sys").click(function(){
                if ($(this).hasClass("sysAt")) {
                    $(this).removeClass("sysAt");
                    $("ul", this.parentNode).hide();
                }
                else {
                    $(this).addClass("sysAt");
                    var coord = $(this).coord();
                    $("ul", this.parentNode).css({
                        top: coord.top + 23 - document.getElementById("b-portal-parent").scrollTop,
                        left: coord.left - 68
                    }).show();
                }
            });
        }
        else {
            $(".b-portal-plug .b-sys-plan").mouseover(function(){
                var coord = $(this).coord();
                $("ul", this).css({
                    top: coord.top + 23 - document.getElementById("b-portal-parent").scrollTop,
                    left: coord.left - 68
                }).show();
                $(".sys", this).addClass("sysAt");
            });
            $(".b-portal-plug .b-sys-plan").mouseout(function(){
                $("ul", this.parentNode).hide();
                $(".sys", this).removeClass("sysAt");
            });
        }
        $(".b-portal-plug .max").click(function(){
            if ($(this).hasClass("min")) {
				$(this).removeClass("min");
				var plan = this.parentNode.parentNode.parentNode;
				$(".b-portal-content", plan).html("");
				$(plan).removeClass("maxTitle");
				$.jsonAjax({
					url: "portal/getPortalPlugin",
					data: "id=" + $(plan).attr("dataid"),
					fn: function(data){
						$(plan).attr("dataid", data.id);
						$(".b-portal-title label", plan).html(data.name);
						var content = $(".b-portal-content", plan);
						$.loadHtmlJson(content, data.url);
					}
				});
				document.ontouchmove = function(e){}
			}
			else {
				$(this).addClass("min");
				var plan = this.parentNode.parentNode.parentNode;
				$(plan).addClass("maxTitle");
				var child = $(".b-portal-content > div", plan);
				var h = document.body.clientHeight
				if ($.iscore()) 
					h = document.documentElement.clientHeight;
				child.attr("nheight", child.height()).css({
					height: h - 44
				});
				document.ontouchmove = function(e){
					e.preventDefault();
				}
			}
        });
        //绑定插件操作事件
        bindPortalAction(character);
    }
    /**
     * 绑定插件操作事件
     */
    function bindPortalAction(character){
        //刷新
        $(".b-portal-title .b-sysList-1").live("click", function(){
            actionoverClick(this);
            var plug = this.parentNode.parentNode.parentNode.parentNode.parentNode;			
            $.jsonAjax({
                url: "portal/getPortalPlugin",
                data: "id=" + $(plug).attr("dataid"),
                fn: function(data){
                    $(plug).attr("dataid", data.id);
                    $(".b-portal-title label", plug).html(data.name);
                    var content = $(".b-portal-content", plug);
                    $.loadHtmlJson(content, data.url);
                }
            });
        });
        //删除
        $(".b-portal-title .b-sysList-2").live("click", function(){
            var plug = this.parentNode.parentNode.parentNode.parentNode.parentNode;
            $.jsonAjax({
                url: "portal/deletePlugin",
                data: "id=" + plug.id,
                fn: function(data){
                    if (data.result) {
                        $(plug).remove();
                    }
                    $.execMessage(data.message);
                }
            });
            actionoverClick(this);
        });
		
		//默认加载项
        defaulLoadInfo(character);
        //绑定应用
        bindTreeEvent();
    }
    
    function actionoverClick(b){
        var plan = b.parentNode.parentNode;
        $(".sys", plan).removeClass("sysAt");
        $("ul", plan).hide();
    }
    function setHistory(url){
        if ($.core.history.length >= 5) {
            $.core.history[4] = url;
        }
        else {
            $.core.history[$.core.history.length] = url;
        }
        if ($.core.history.length > 1) {
            $("#b-go-history").show();
        }
        else {
            $("#b-go-history").hide();
        }
    }
    /**
     * 一级菜单点击事件
     */
    function menuAction(b){
        try {
            if (!b.nodeName) 
                b = this;
        } 
        catch (e) {
            b = this;
        }
        if (b.className != "iconAt") {
            //遍历
            $("#b-menu-one li").each(function(n){
                if (this.className === "iconAt") {
                    this.className = "";
                    var nsrc = $("img", this).attr("dsrc");
                    $("img", this).attr("dsrc", $("img", this).attr("src")).attr("src", nsrc);
                }
            });
            //绑定
            b.className = "iconAt";
            var nsrc = $("img", b).attr("dsrc");
            $("img", b).attr("dsrc", $("img", b).attr("src")).attr("src", nsrc);
            $("#b-Control-two .b-two-list").hide();
            var l = $("#b-Control-two #twoNodes-" + $(b).attr("dataid")).show();
            $("#b-Control-two .b-Control-title label").html(b.getAttribute("title"));
            var h = document.body.clientHeight
            if ($.iscore()) 
                h = document.documentElement.clientHeight;
            $("#b-Control-two").css({
                height: h - 52
            }).show();
			$("#b-Control-two .b-Control-content").css({
				height: h - 96
			});
            $("#b-Control-one").hide();
			$("body").css({
				//overflowY:"hidden"
			});
			$("#b-portal-parent").hide();
        }
    }
    /**
     * 绑定一级、二级菜单数据及事件
     * @param {Object} data
     */
    function bindMenus(data){
        var firstMenu = $("#b-menu-one");
        for (var i = 0; i < data.length; i++) {
            if (i >= 5) {
                if (i == 5) {
                    var li = document.createElement("li");
                    li.id = "b-menu-more";
                    li.setAttribute("title", $.core.MUI.get("More"));
                    li.innerHTML = "<img src='public/images/icons/menu-more-deful.png' dsrc='public/images/icons/menu-more-deful.png' alt='' ><div id='b-more-links'><span class='more-link'></span><dl></dl></div>";
                    firstMenu.append(li);
                    //绑定更多
                    li.onclick = function(){
                        $("#b-more-links").show();
                    }
                    $("#b-more-links dd").live("click", function(){
                        oneMenuMore(this);
                    });
                }
                $("#b-more-links dl").append("<dd title='" + data[i].name + "' dataid='" + data[i].id + "'><img src='" + data[i].icon + "' alt='' dsrc='" + data[i].focusIcon + "'></dd>");
            }
            else {
                var li = document.createElement("li");
                li.setAttribute("dataid", data[i].id);
                li.setAttribute("title", data[i].name);
                li.innerHTML = "<img src='" + data[i].icon + "' dsrc='" + data[i].focusIcon + "' alt='' >";
                li.onclick = menuAction;
                firstMenu.append(li);
            }
            //绑定二级内容
            bindTowMessage(data[i]);
        }
        $("#b-Control-two .b-Control-title .close").click(function(){
            $("#b-Control-two,#b-Control-one").hide();
			$("#b-portal-parent").show();
            $("#b-menu-one li").each(function(n){
                if (this.className === "iconAt") {
                    this.className = "";
                    var nsrc = $("img", this).attr("dsrc");
                    $("img", this).attr("dsrc", $("img", this).attr("src")).attr("src", nsrc);
                }
            });
        });
    }
    /**
     * 绑定二级内容
     * @param {Object} data
     */
    function bindTowMessage(data){
        var div = document.createElement("div");
        div.className = "b-two-list";
        div.id = "twoNodes-" + data.id;
        if (data.list.length === 0) 
            div.innerHTML = "<span class='b-two-uninfo'>" + $.core.MUI.get("unmas") + "</span>";
        else {
            for (var i = 0; i < data.list.length; i++) {
                var ulist = document.createElement("div");
                ulist.className = "b-listmenu-col";
                div.appendChild(ulist);
                var h2 = document.createElement("h2");
                h2.innerHTML = data.list[i].name;
                h2.setAttribute("dataid", data.list[i].id);
                ulist.appendChild(h2);
                var useCol = document.createElement("div");
                if (data.list[i].list.length > 0) {
                    useCol.className = "b-listmenu-usecol";
                    ulist.appendChild(useCol);
                }
                for (var m = 0; m < data.list[i].list.length; m++) {
                    var dl = document.createElement("dl");
                    dl.setAttribute("dataid", data.list[i].list[m].id);
                    dl.innerHTML = "<dt><img src='" + data.list[i].list[m].icon + "'></dt><dd>" + data.list[i].list[m].name + "</dd>"
                    useCol.appendChild(dl);
                }
            }
        }
        $("#b-Control-two .b-Control-content").append(div);
    }
    /**
     * 一级菜单More执行
     */
    function oneMenuMore(b){
        var li = $("#b-menu-one li").eq(4);
        var img = $("img", li);
        if (li.hasClass("iconAt")) 
            $("#b-more-links dl").append("<dd dataid='" + li.attr("dataid") + "' title='" + li.attr("title") + "'><img dsrc='" + img.attr("src") + "' alt='' src='" + img.attr("dsrc") + "'></dd>");
        else 
            $("#b-more-links dl").append("<dd dataid='" + li.attr("dataid") + "' title='" + li.attr("title") + "'><img dsrc='" + img.attr("dsrc") + "' alt='' src='" + img.attr("src") + "'></dd>");
        
        li.attr("dataid", b.getAttribute("dataid"));
        li.attr("title", b.getAttribute("title"));
        li.html(b.innerHTML);
        li.removeClass("iconAt");
        
        $("#b-more-links").hide();
        //执行点击
        menuAction($("#b-menu-one li")[4]);
        //删除点击元素
        $(b).remove();
    }
    /**
     * 绑定系统菜单数据，事件
     * @param {Object} data
     */
    function bindSystems(data){
        var sysmenu = $("#b-system-list").html("");
        for (var i = 0; i < data.length; i++) {
            if (data[i].id == 100) 
                continue;
            var li = document.createElement("li");
            li.innerHTML = "<img src='" + data[i].icon + "' alt=''><strong>" + data[i].name + "</strong>";
            if (data[i].list) {
                li.innerHTML += "<label></label>";
                var dl = document.createElement("dl");
                for (var j = 0; j < data[i].list.length; j++) {
                    var dd = document.createElement("dd");
                    if (data[i].list[j].selected) 
                        dd.className = "selected";
                    dd.setAttribute("dataid", data[i].list[j].id);
                    dd.innerHTML = data[i].list[j].name;
                    dl.appendChild(dd);
                }
                switch (data[i].id) {
                    case "110":
                        //更换皮肤
                        $("dd", dl).click(function(){
                            if (!$(this).hasClass("selected")) {
                                $("dd", this.parentNode).removeClass("selected");
                                this.className = "selected";
                                $.jsonAjax({
                                    url: "systems/skin",
                                    data: "id=" + $(this).attr("dataid"),
                                    fn: function(data){
                                        $("#b-baseCss").attr("src", data.bsrc);
                                        $("#b-publicCss").attr("src", data.psrc);
                                    }
                                });
                            }
                            return false;
                        });
                        break;
                    case "120":
                        //更换主题
                        $("dd", dl).click(function(){
                            if (!$(this).hasClass("selected")) {
                                $("dd", this.parentNode).removeClass("selected");
                                this.className = "selected";
                                $.jsonAjax({
                                    url: "systems/theme",
                                    data: "id=" + $(this).attr("dataid"),
                                    fn: function(data){
                                        window.location.href = window.location.href;
                                    }
                                });
                            }
                            return false;
                        });
                        break;
                    case "130":
                        //切换模板
                        $("dd", dl).click(function(){
                            if (!$(this).hasClass("selected")) {
                                $("dd", this.parentNode).removeClass("selected");
                                this.className = "selected";
                                $.jsonAjax({
                                    url: "systems/template",
                                    data: "id=" + $(this).attr("dataid"),
                                    fn: function(data){
                                        window.location.href = window.location.href;
                                    }
                                });
                            }
                            return false;
                        });
                        break;
                    case "140":
                        //切换语言
                        $("dd", dl).click(function(){
                            if (!$(this).hasClass("selected")) {
                                $("dd", this.parentNode).removeClass("selected");
                                this.className = "selected";
                                $.jsonAjax({
                                    url: "systems/language",
                                    data: "id=" + $(this).attr("dataid"),
                                    fn: function(data){
                                        window.location.href = window.location.href;
                                    }
                                });
                            }
                            return false;
                        });
                        break;
                    case "150":
                        //帮助中心
                        $("dd", dl).click(function(){
                            if (!$(this).hasClass("selected")) {
                            //$("dd", this.parentNode).removeClass("selected");
                            //this.className = "selected";
                            }
                            return false;
                        });
                        break;
                }
                li.appendChild(dl);
                //系统菜单列表
                li.onclick = function(){
                    $("#b-system-list dl").hide();
                    $("dl", this).slideToggle("100");
                }
            }
            else {
                li.onclick = function(){
                    $("#b-system-list dl").hide();
                }
            }
            li.setAttribute("dataid", data[i].id);
            switch (data[i].id) {
                case "160":
                    li.onclick = function(){
                        aboutBoh($("strong", this).html());
                    };
                    break;
                case "170":
                    li.onclick = logout;
                    break;
            }
            sysmenu.append(li);
        }
        
        $("#b-user-action").click(function(){
            if (this.className === "b-user-action") {
                $("#b-system-list").slideDown(200);
                this.className = "b-user-actionAt";
                $("#b-system-list dl").hide();
            }
            else {
                $("#b-system-list").slideUp(200);
                this.className = "b-user-action";
            }
        });
    }
    /**
     * 注销用户
     */
    function logout(){
        window.location.pathname = $.core.location.replace("ajax", "web") + "login.html";
    }
    /***
     * 关于BOH
     */
    function aboutBoh(h){
        $("body").colorbox_open({
            opacity: 0.6,
            move: true,
            title: h,
            content: "#b-about-boh"
        })
    }
    
});
jQuery(document).bind("click", function(e){
    if ($.toClick(e, ["#b-system-list", "#b-user-action"])) {
        $("#b-user-action").removeClass("b-user-actionAt").addClass("b-user-action");
    }
    $.toClick(e, ["#b-more-links", "#b-menu-more"]);
});

function getHistory(){
    var rt = false, url = null, n;
    for (var i = $.core.history.length - 1; i >= 0; i--) {
        if ($.core.urlNodes == $.core.history[i] && i > 0) {
            rt = true;
            n = i;
            url = $.core.history[i - 1];
        }
    }
    if (rt) {
        $.jsonAjax({
            url: url,
            fn: function(data){
                $("#b-Control-one .b-Control-content").attr("dataid", data.id);
                $("#b-Control-one .b-Control-title label").html(data.name);
                $.loadHtmlJson("#b-Control-one .b-Control-content", data.url);
            }
        });
        $.core.urlNodes = url;
        if (n < 2) 
            $("#b-go-history").hide();
    }
    else 
        $("#b-go-history").hide();
}
