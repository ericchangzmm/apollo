
var ch =  {
    "palette": "2",
    "caption": "Country Comparison",
    "showlabels": "1",
    "showvalues": "0",
    "decimals": "0",
    "numberprefix": "$",
    "clustered": "0",
    "exetime": "1.5",
    "showplotborder": "0",
    "zgapplot": "30",
    "zdepth": "90",
    "divlineeffect": "emboss",
    "startangx": "10",
    "endangx": "18",
    "startangy": "-10",
    "endangy": "-40"
  }

 var  categories1 = [
    {
      "category": [
        {
          "label": "Austria"
        },
        {
          "label": "Brazil"
        },
        {
          "label": "France"
        },
        {
          "label": "Germany"
        },
        {
          "label": "USA"
        }
      ]
    }
  ]
  var dataset1 = [
    {
      "seriesname": "1998",
      "color": "8BBA00",
      "showvalues": "0",
      "data": [
        {
          "value": "45000.65"
        },
        {
          "value": "44835.76"
        },
        {
          "value": "18722.18"
        },
        {
          "value": "77557.31"
        },
        {
          "value": "92633.68"
        }
      ]
    },
    {
      "seriesname": "1997",
      "color": "F6BD0F",
      "showvalues": "0",
      "data": [
        {
          "value": "57401.85"
        },
        {
          "value": "41941.19"
        },
        {
          "value": "45263.37"
        },
        {
          "value": "117320.16"
        },
        {
          "value": "114845.27"
        }
      ]
    },
    {
      "seriesname": "1996",
      "color": "AFD8F8",
      "showvalues": "0",
      "data": [
        {
          "value": "25601.34"
        },
        {
          "value": "20148.82"
        },
        {
          "value": "17372.76"
        },
        {
          "value": "35407.15"
        },
        {
          "value": "38105.68"
        }
      ]
    }
  ]

chart1 = new Chart('MSColumn2D', 'ChartId', '100%', '90%', '#ptest0', {
    'dataSet': dataset1,
    'categories': categories1,
    'chart': ch
});

