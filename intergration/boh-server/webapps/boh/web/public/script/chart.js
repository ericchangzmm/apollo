(function(window){
	
	var Chart = function(defaultChartType, chartId, width, height, container, param){
		
		this.container = $(container);
		this.width = width;
		this.height = height;
		this.chartId = chartId;
		this.data = param.data;
		this.dataSet = param.dataSet;
		this.categories = param.categories;
		var that = this;
		
		var select = $("<select class='b-chart-select'  style='border:1px solid #DBDBDB;float:left;'>");
		var selectDiv = $("<div class='b-chart-selectDiv'></div>").width(this.width).append(select).appendTo(this.container);
		var singleChartsTypes = ["Column2D", "Column3D", "Line", "Area2D", "Bar2D", "Pie2D","Pie3D","Doughnut2D","Doughnut3D","Pareto2D","Pareto3D"];
		var multiChartsTypes = ["MSColumn2D","MSColumn3D","MSLine","MSBar2D","MSBar3D","MSArea","Marimekko"
		,"MSCombi3D","MSCombi2D","MSCombiDY2D","MSColumn3DLineDY"];
		if(this.dataSet){
			$.each(multiChartsTypes, function(index, item){
				var option = $("<option value='" + item + "'>" + item + "</option>").appendTo(select);	
				if(defaultChartType === item){
					option.attr({
						selected: "selected"
					});
				}
			});
		}else if(this.data){
			$.each(singleChartsTypes, function(index, item){
				var option = $("<option value='" + item + "'>" + item + "</option>").appendTo(select);		
				if(defaultChartType === item){
					option.attr({
						selected: "selected"
					});
				}							
			});
		}
		
		select.change(function(){
			that.loadChart(this.value);
		});
		
		this.div = $("<div style='text-align:center;display:block;'>").appendTo(this.container);		
		
		this.chart = {
		    "caption": "Chart",
		    "useroundedges": "1",
		    "bgcolor": "#FFFFFF",
		    "showborder": "0",
			"exportEnabled": "1"
		  };
		for(this.i in param.chart) 
            this.chart[this.i] = param.chart[this.i];    
		  
		this.jsonData = {
		  "chart": this.chart,
		  "data": this.data,
		  "dataSet": this.dataSet,
		  "categories": this.categories
		};
		
		this.loadChart(defaultChartType);		

	};
	
	Chart.prototype = {
		 loadChart: function(chartType){
		 	FusionCharts.printManager.enabled(true);
		 	if (FusionCharts(this.chartId)) {
		 		FusionCharts(this.chartId).dispose();
		 	}
		 	var fcharts = new FusionCharts(chartType, this.chartId, this.width, this.height);
		 	this.fcharts = fcharts;
		 	fcharts.setJSONData(this.jsonData);
		 	fcharts.render(this.div[0]);
		 
		 }		
	};
	
	window.Chart = Chart;
})(window);