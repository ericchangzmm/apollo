//var dataString = {
//  'chart': {
//    'palette': '2',
//    'caption': 'Sales',
//    'subcaption': 'March 2006',
//    'showvalues': '0',
//    'divlinedecimalprecision': '1',
//    'limitsdecimalprecision': '1',
//    'pyaxisname': 'Amount',
//    'syaxisname': 'Quantity',
//    'numberprefix': '$',
//    'formatnumberscale': '0'
//  },
//  'categories': [
//    {
//      'category': [
//        {
//          'label': 'A'
//        },
//        {
//          'label': 'B'
//        },
//        {
//          'label': 'C'
//        },
//        {
//          'label': 'D'
//        },
//        {
//          'label': 'E'
//        },
//        {
//          'label': 'F'
//        },
//        {
//          'label': 'G'
//        },
//        {
//          'label': 'H'
//        },
//        {
//          'label': 'I'
//        },
//        {
//          'label': 'J'
//        }
//      ]
//    }
//  ],
//  'dataset': [
//    {
//      'seriesname': 'Revenue',
//      'data': [
//        {
//          'value': '5854'
//        },
//        {
//          'value': '4171'
//        },
//        {
//          'value': '1375'
//        },
//        {
//          'value': '1875'
//        },
//        {
//          'value': '2246'
//        },
//        {
//          'value': '2696'
//        },
//        {
//          'value': '1287'
//        },
//        {
//          'value': '2140'
//        },
//        {
//          'value': '1603'
//        },
//        {
//          'value': '1628'
//        }
//      ]
//    },
//    {
//      'seriesname': 'Profit',
//      'renderas': 'Line',
//      'data': [
//        {
//          'value': '3242'
//        },
//        {
//          'value': '3171'
//        },
//        {
//          'value': '700'
//        },
//        {
//          'value': '1287'
//        },
//        {
//          'value': '1856'
//        },
//        {
//          'value': '1126'
//        },
//        {
//          'value': '987'
//        },
//        {
//          'value': '1610'
//        },
//        {
//          'value': '903'
//        },
//        {
//          'value': '928'
//        }
//      ]
//    }
//  ]
//};
//var chart = new FusionCharts('MSCombi2D', 'ChartId', '100%', '100%');
//chart.setJSONData(dataString);
//chart.render('ptest4');
//

var chart, div;
var data = [{
    'label': '6',
    'value': '420'
}, {
    'label': '8',
    'value': '910'
}, {
    'label': '10',
    'value': '720'
}, {
    'label': '12',
    'value': '550'
}];

var categories = [{
    'category': [{
        'label': '6'
    }, {
        'label': '8'
    }, {
        'label': '10'
    }, {
        'label': '12'
    }]
}];

var dataSet = [{
    'seriesname': '肯德基汉堡',
    'data': [{
        'value': '585'
    }, {
        'value': '417'
    }, {
        'value': '137'
    }, {
        'value': '187'
    }]
}, {
    'seriesname': '炸鸡翅',
    'renderas': 'Line',
    'data': [{
        'value': '324'
    }, {
        'value': '317'
    }, {
        'value': '700'
    }, {
        'value': '128'
    }]
}, {
    'seriesname': '奶茶',
    'renderas': 'Area',
    'data': [{
        'value': '58'
    }, {
        'value': '41'
    }, {
        'value': '13'
    }, {
        'value': '18'
    }]
}, {
    'seriesname': '可乐',
    'renderas': 'Line',
    'data': [{
        'value': '234'
    }, {
        'value': '141'
    }, {
        'value': '213'
    }, {
        'value': '118'
    }]
}];

var chart = {
    'caption': '肯德基汉堡销售情况',
    'subcaption': '今天 6-12 点 ', //副标题
    'yaxisname': '售卖数(个)'
};
chart = new Chart('MSColumn3D', 'ChartId_1', '100%', '90%', '#ptest4', {
    'dataSet': dataSet,
    'categories': categories,
    'chart': chart
});
