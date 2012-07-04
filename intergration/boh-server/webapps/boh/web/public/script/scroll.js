(function(window){

    var Scroll = function(el, options){    
        var that = this;
        //默认参数
        this.param = {
            hScroll: true,
            vScroll: true
        };        
        //设置参数
        for (this.i in options) 
            this.param[this.i] = options[this.i];        
        var vendor = new RegExp("Firefox").test(navigator.userAgent) ? 'Moz' : '';        
        this.hasTouch = $.isPad();        
        //Events
        this.WHEEL_EV = vendor == 'Moz' ? 'DOMMouseScroll' : 'mousewheel';        
        this.wrapper = typeof el == 'object' ? el : $(el); 
        this.wrapper.addClass("b-scroll-wrapper").wrapInner("<div class='b-scroll-scroller'></div>");
        this.scroller = $(this.wrapper[0].children[0]);        
        this.scroller.addClass("b-scroll-scroller");
        if (this.param.vScroll) {
            this.scrollerHeight = this.scroller.height();
            this.vScrollBarBg = $("<div class='b-scroll-vscrollbarbg'></div>");
            this.vScrollBar = $("<div class='b-scroll-vscrollbar'></div>");
            this.vScrollBarState = true;
            if (!this.hasTouch) {
                if (this.scrollerHeight <= this.wrapper.height()) {
                    this.operateScrollBar('v', 'hide', this.param.hidefun);
                }
            }
            else {
                this.operateScrollBar('v', 'hide');
            }
            
            this.vScrollMenu = $("<div class='b-scroll-vscrollmenu'></div>");
            this.vScrollBar.append(this.vScrollMenu);
            this.wrapper.append(this.vScrollBarBg, this.vScrollBar);
        }       
        if (this.param.hScroll) {
            this.scrollerWidth = this.scroller.width();
            this.hScrollBarBg = $("<div class='b-scroll-hscrollbarbg'></div>");
            this.hScrollBar = $("<div class='b-scroll-hscrollbar'></div>");
			this.hScrollBarState = true;
            if (this.hasTouch || this.scrollerWidth <= this.wrapper.width()) {
                 this.operateScrollBar('h', 'hide');
            }
            this.hScrollMenu = $("<div class='b-scroll-hscrollmenu'></div>");
            this.hScrollBar.append(this.hScrollMenu);
            this.wrapper.append(this.hScrollBarBg, this.hScrollBar);
        }  
		this.crossSpace = this.param.vScroll&&this.param.hScroll? 10 : 0;      
        if (this.param.vScroll) {
            this.scrollMenuHeight = this.scroller.height() < this.wrapper.height() ? this.wrapper.height() : this.wrapper.height() * this.wrapper.height() / this.scroller.height();
            this.vScrollMenu.css({
                height: this.scrollMenuHeight - 2,
                top: 0
            });
            this.vScrollBar[0].onselectstart = function(){
                return false;
            };
            this.vScrollBarBg[0].onselectstart = function(){
                return false;
            };
            this.scroller[0].onselectstart = function(){
                return false;
            };
        }        
        if (this.param.hScroll) {
            this.scrollMenuWidth = this.scroller.width() < this.wrapper.width() ? this.wrapper.width() : this.wrapper.width() * this.wrapper.width() / this.scroller.width();
            this.hScrollMenu.css({
                width: this.scrollMenuWidth - 2,
                left: 0
            });
            this.hScrollBar[0].onselectstart = function(){
                return false;
            };
            this.hScrollBarBg[0].onselectstart = function(){
                return false;
            };
            this.scroller[0].onselectstart = function(){
                return false;
            };
        }        
        if (this.param.showBackTop) {
            this.backTop = $("<div class='b-scroll-backtop' style='display:none;'>返回顶部</div>");
            this.wrapper.append(this.backTop);
			this.bindEvent(this.backTop[0],"click");       
            if (this.scroller.height() < 2 * this.wrapper.height()) 
                this.backTop.hide();
        }
        this.currentY = 0;
        this.currentX = 0;    
		
		this.bindEvent(this.scroller[0],"touchstart"); 
		this.bindEvent(this.scroller[0],"touchmove");
		this.bindEvent(this.scroller[0],"touchend");
		this.bindEvent(this.scroller[0],"touchcancel");
		this.bindEvent(this.scroller[0],this.WHEEL_EV);
		
		if (this.param.vScroll) {
			this.bindEvent(this.vScrollMenu[0],'mousedown');
			this.bindEvent(this.vScrollBar[0],'click');
        }            
        if (this.param.hScroll) {
			this.bindEvent(this.hScrollMenu[0],'mousedown');
			this.bindEvent( this.hScrollBar[0],'click');
        }
		this.bindEvent(document,'mousemove');
		this.bindEvent(document,'mouseup');		
               
        this.checkDOMTime = setInterval(function(){
            that._checkDOMChanges();
        }, 500);
    };
    
    Scroll.prototype = {    
        moveStart: null,
        movedX: null,
        movedY: null,
        nowClientY: null,
        nowClientX: null,
        scrollY: 0,
        scrollX: 0,
        activeScrollMenu: null,        
        handleEvent: function(e){
            var that = this;
            e.target = e.target ? e.target : e.srcElement;
            switch (e.type) {
                case 'touchstart':
                    this.start(e);
                    break;
                case 'touchmove':
                    if (this.moveStart) {
                        this.moved(e);
                    }
                    break;
                case 'touchend':
                case 'touchcancel':
                    this.moveStart = false;
                    if (this.param.vScroll) {
                        this.vScrollBar.delay(1000).fadeOut("slow,100");
                        this.vScrollBarBg.delay(1000).fadeOut("slow,100");
                    }
                    if (this.param.hScroll) {
                        this.hScrollBar.delay(1000).fadeOut("slow,100");
                        this.hScrollBarBg.delay(1000).fadeOut("slow,100");
                    }
                    break;
                case this.WHEEL_EV:
                    var wheelDeltaX, wheelDeltaY;
                    if ('wheelDeltaX' in e) {
                        wheelDeltaX = e.wheelDeltaX;
                        wheelDeltaY = e.wheelDeltaY;
                    }
                    else if ('wheelDelta' in e) 
                        wheelDeltaX = wheelDeltaY = e.wheelDelta;
                    else if ('detail' in e) 
                        wheelDeltaX = wheelDeltaY = -e.detail * 5;
                    else {
                        return;
                    }                    
                    if (this.param.vScroll) {
                        if (this.currentY + wheelDeltaY > 0) 
                            this.currentY = 0;
                        else if (this.currentY + wheelDeltaY < this.wrapper.height() - this.scroller.height()) 
                            this.currentY = this.wrapper.height() - this.scroller.height();
                        else {
                            this.currentY = this.currentY + wheelDeltaY;
                            $.stopEvent(e);
                        }
                        this.setScrollPostion();
                        return;
                    }
                    if (this.param.hScroll) {
                        if (this.currentX + wheelDeltaX > 0) 
                            this.currentX = 0;
                        else if (this.currentX + wheelDeltaX < this.wrapper.width() - this.scroller.width()) 
                            this.currentX = this.wrapper.width() - this.scroller.width();
                        else {
                            this.currentX = this.currentX + wheelDeltaX;
                            $.stopEvent(e);
                        }
                        this.setScrollPostion();
                    }
                    break;
                case 'click':
                    if (this.backTop && e.target === this.backTop[0]) 
						this.backToTop();
                    else if (this.vScrollBar && e.target === this.vScrollBar[0]) {                    
                        if (e.clientY > this.vScrollMenu.coord().top) 
							this.scrollY = this.scrollY + this.scrollMenuHeight > this.wrapper.height() - this.scrollMenuHeight - this.crossSpace ? this.wrapper.height() - this.scrollMenuHeight - this.crossSpace : this.scrollY + this.scrollMenuHeight;
                        else 
							this.scrollY = this.scrollY - this.scrollMenuHeight < 0? 0:this.scrollY - this.scrollMenuHeight;
                        this.vScrollMenu.css({top: this.scrollY});
                        this.setScrollerPosition("v");                        
                    }
                    else if (this.hScrollBar && e.target === this.hScrollBar[0]) {
                        if (e.clientX > this.hScrollMenu.coord().left) 
							this.scrollX = this.scrollX + this.scrollMenuWidth > this.wrapper.width() - this.scrollMenuWidth - this.crossSpace ? this.wrapper.width() - this.scrollMenuWidth - this.crossSpace : this.scrollX + this.scrollMenuWidth;
                        else 
							this.scrollX = this.scrollX - this.scrollMenuWidth < 0? 0 : this.scrollX - this.scrollMenuWidth;
                        this.hScrollMenu.css({left: this.scrollX});
                        this.setScrollerPosition("h");
                    }
                    break;
                case 'mousedown':
                    this.start(e);
                    break;
                case 'mousemove':
                    if (this.moveStart) 
                        this.scrollMoved(e);
                    break;
                case 'mouseup':
                    this.moveStart = false;
                    break;
            }
        },    
		
		bindEvent: function(obj, type){
			var that = this;
			if (window.addEventListener) 
	            obj.addEventListener(type, this, false);  
	        else 
	            obj.attachEvent('on' + type, function(e){
	                that.handleEvent(e);
	            });	           
		},
		 
        setScrollPostion: function(){
            if (this.param.vScroll) {
                this.scroller.css({top: this.currentY});
				this.scrollY = Math.abs(this.currentY) * (this.wrapper.height()) / this.scroller.height() > this.wrapper.height() - this.scrollMenuHeight - this.crossSpace ? this.wrapper.height() - this.scrollMenuHeight - this.crossSpace : Math.abs(this.currentY) * (this.wrapper.height()) / this.scroller.height();
                this.vScrollMenu.css({top: this.scrollY});				
				this.showBackTopBtn();
            }
            if (this.param.hScroll) {
                this.scroller.css({left: this.currentX});
				this.scrollX = Math.abs(this.currentX) * this.wrapper.width() / this.scroller.width() > this.wrapper.width() - this.scrollMenuWidth - this.crossSpace ? this.wrapper.width() - this.scrollMenuWidth - this.crossSpace : Math.abs(this.currentX) * this.wrapper.width() / this.scroller.width();
                this.hScrollMenu.css({left: this.scrollX});
            }
        },        
        start: function(e){
            this.moveStart = true;
            var e = this.hasTouch ? e.touches[0] : e;
            this.nowClientY = e.clientY;
            this.nowClientX = e.clientX;
            this.activeScrollMenu = e.target;
        }, 
		showBackTopBtn: function(){
			if (this.param.showBackTop){
				if(Math.abs(this.currentY) > this.wrapper.height() * 2)
					this.backTop.show();
				else
					this.backTop.hide();
			} 	
		},		    
        moved: function(e){
            var point = this.hasTouch ? e.touches[0] : e;
            if (this.param.vScroll && this.scroller.height() > this.wrapper.height()) {
                this.vScrollBar.show();
                this.vScrollBarBg.show();
                this.movedY = point.clientY - this.nowClientY;
                this.nowClientY = point.clientY;
                this.currentY = this.scroller.position().top + this.movedY;                
                if (this.currentY > 0) 
                    this.currentY = 0;
                else if (this.currentY < this.wrapper.height() - this.scroller.height()) 
                    this.currentY = this.wrapper.height() - this.scroller.height();
                else {
                    $.stopEvent(e);
                    this.vScrollBar.delay(1000).fadeOut("slow,100");
                    this.vScrollBarBg.delay(1000).fadeOut("slow,100");
                }
                this.scroller.css({top: this.currentY});
            }            
            if (this.param.hScroll && this.scroller.width() > this.wrapper.width()) {
                this.hScrollBar.show();
                this.hScrollBarBg.show();
                this.movedX = point.clientX - this.nowClientX;
                this.nowClientX = point.clientX;
                this.currentX = this.scroller.position().left + this.movedX;
                
                if (this.currentX > 0) 
                    this.currentX = 0;
                else if (this.currentX < this.wrapper.width() - this.scroller.width()) 
                    this.currentX = this.wrapper.width() - this.scroller.width();
                else {
                    $.stopEvent(e);
                    this.hScrollBar.delay(1000).fadeOut("slow,100");
                    this.hScrollBarBg.delay(1000).fadeOut("slow,100");
                }
                this.scroller.css({left: this.currentX});
            }
            this.setScrollPostion();
        },        
        scrollMoved: function(e){
            var point = e;
            if (this.vScrollMenu && this.activeScrollMenu === this.vScrollMenu[0]) {
                this.movedY = point.clientY - this.nowClientY;
                this.nowClientY = point.clientY;
                this.scrollY = this.vScrollMenu.position().top + this.movedY;
				this.scrollY = this.scrollY < 0 ? 0: this.scrollY > this.wrapper.height() - this.scrollMenuHeight - this.crossSpace ? this.wrapper.height() - this.scrollMenuHeight - this.crossSpace : this.scrollY;
                this.vScrollMenu.css({top: this.scrollY});
                this.setScrollerPosition("v");                
            }
            else if (this.hScrollMenu && this.activeScrollMenu === this.hScrollMenu[0]) {
                this.movedX = point.clientX - this.nowClientX;
                this.nowClientX = point.clientX;
                this.scrollX = this.hScrollMenu.position().left + this.movedX;
				this.scrollX = this.scrollX < 0 ? 0 : this.scrollX > this.wrapper.width() - this.scrollMenuWidth - this.crossSpace ? this.wrapper.width() - this.scrollMenuWidth - this.crossSpace : this.scrollX;
                this.hScrollMenu.css({left: this.scrollX});
                this.setScrollerPosition("h");
            }
        },        
        setScrollerPosition: function(type){
            if (type == "v") {
                this.currentY = -this.scrollY * this.scroller.height() / this.wrapper.height();
                this.scroller.css({top: this.currentY});		
				this.showBackTopBtn();
            }
            else if (type == "h") {
                this.currentX = -this.scrollX * this.scroller.width() / this.wrapper.width();
                this.scroller.css({left: this.currentX});
            }            
        },
        backToTop: function(){
            var that = this;
            this.currentY = 0;
            this.scrollY = Math.abs(this.currentY) * this.wrapper.height() / this.scroller.height();
            this.scroller.animate({top: this.currentY}, 500);
            this.vScrollMenu.animate({top: this.scrollY}, 500, function(){
                that.backTop.hide();
            });
        },        
        _checkDOMChanges: function(){
            this.refresh();
        },        
         refresh: function(){
			var flag = false;
            if (this.param.vScroll) {
                this.scrollerHeight = this.scroller.height();
                this.scrollMenuHeight = this.scroller.height() > this.wrapper.height() ? this.wrapper.height() * this.wrapper.height() / this.scroller.height() : this.wrapper.height();
                this.vScrollMenu.css({height: this.scrollMenuHeight - 2});				
				if (!this.hasTouch) {
					if (this.scrollerHeight <= this.wrapper.height()) {
						this.operateScrollBar('v', 'hide', this.param.hidefun);
						this.currentY = 0;
						flag = true;
					}
					else {
						this.operateScrollBar('v', 'show', this.param.showfun);
					}
				}				
            }else{
				flag = true;
			}            
            if (this.param.hScroll) {
                this.scrollerWidth = this.scroller.width();
                this.scrollMenuWidth = this.scroller.width() > this.wrapper.width() ? this.wrapper.width() * this.wrapper.width() / this.scroller.width() : this.wrapper.width();
                this.hScrollMenu.css({width: this.scrollMenuWidth - 2});
				
				if (!this.hasTouch) {
					if (this.scrollerWidth <= this.wrapper.width()) {
						this.operateScrollBar('h', 'hide');
						this.currentX = 0;
						flag = true;
					}
					else {
						this.operateScrollBar('h', 'show');
					}
				}
            }else{
				flag = true;
			}  
			if(flag) 
				this.crossSpace = 0;
			else
				this.crossSpace = 10;			
            this.setScrollPostion();
        },		
		operateScrollBar: function(scrollbarType, operate, fun){
			if(scrollbarType === 'v'){
				if(operate === 'show'){
					if(!this.vScrollBarState){
						if(fun) fun();
						this.vScrollBar.show();
						this.vScrollBarBg.show();
						this.vScrollBarState = true;
					}
					
				}else if(operate === 'hide'){
					if (this.vScrollBarState) {
						this.vScrollBar.hide();
						this.vScrollBarBg.hide();
						if (fun) fun();
						this.vScrollBarState = false;
					}
				}
			}else if(scrollbarType === 'h'){
				if(operate === 'show'){
					if (!this.hScrollBarState) {
						if(fun) fun();
						this.hScrollBar.show();
						this.hScrollBarBg.show();
						this.hScrollBarState = true;
					}					
				}else if(operate === 'hide'){
					if (this.hScrollBarState){
						this.hScrollBar.hide();
						this.hScrollBarBg.hide();
						if(fun) fun();
						this.hScrollBarState = false;
					}					
				}
			}			
		}
    };
    window.Scroll = Scroll;
})(window);

