var Ejst = {};
 
Ejst.toggleProperties = function(id, expand) {
    var box = CQ.Ext.get(id);
    var arrow = CQ.Ext.get(id + '-arrow');
    if (expand || !box.hasClass('open')) {
        box.addClass('open');
        arrow.update('&laquo;');
    } else {
        box.removeClass('open');
        arrow.update('&raquo;');
    }
};
 
Ejst.expandProperties = function(comp) {
    comp.refresh();
    var id = comp.path.substring(comp.path.lastIndexOf('/')+1); 
    Ejst.toggleProperties(id, true);
};

 
Ejst.x3 = {};
 
Ejst.x3.provideOptions2 = function(path, record) {
    // do something with the path or record
    return [{
        text:"Value1",
        value:"val1"
    },{
        text:"Value2",
        value:"val2"
    }];
};