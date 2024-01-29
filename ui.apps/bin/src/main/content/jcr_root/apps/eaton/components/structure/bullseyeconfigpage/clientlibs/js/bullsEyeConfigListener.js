function afterLayoutEventTrigger(el){

    el.findParentByType('multi-field-panel').items.items[1].setValue(
                                        JSON.parse(el.findParentByType('multi-field-panel').items.items[3].getValue()).categoryId);

}