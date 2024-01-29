import groovy.transform.Field

@Field final HOME_PAGE_TEMPLATE = "/conf/eaton/settings/wcm/templates/home-page"
@Field final CQ_TEMPLATE = "cq:template"
@Field final ERROR_PAGES = "errorPages"
@Field final PAGE_404 = "/404";
@Field final EMPTY = "";


def eaton_site =["/content/eaton"] as String[]
//def other_sites =["/content/eaton-cummins", "/content/myeaton", "/content/greenswitching", "/content/order-center", "/content/phoenixtec"] as String[]
def final dry_run = true

eaton_site.each { pageBasePath ->
    getPage(pageBasePath).recurse { page ->
        def pageNode =page.node;
        if(pageNode.hasProperty(CQ_TEMPLATE)){
            String templatePath = pageNode.getProperty(CQ_TEMPLATE).value.toString();
            if(templatePath.equals(HOME_PAGE_TEMPLATE)){
                if(pageNode.hasProperty(ERROR_PAGES)){
                    String errorPageBaseLocation = pageNode.getProperty(ERROR_PAGES).value.toString();
                    if(errorPageBaseLocation != null){
                        // Resets the error handling path to current locale path.
                        errorPageBaseLocation = errorPageBaseLocation.replace(PAGE_404,EMPTY);
                        pageNode.setProperty(ERROR_PAGES,errorPageBaseLocation);
                        println("Updated Location -->"+ errorPageBaseLocation)
                    }
                }else{
                    try{
                        // Add new property (errorPages) with current page path as value.
                        pageNode.setProperty(ERROR_PAGES,pageNode.parent.path);
                        println("Property Not Found -> Added New Property(errorPages) -->"+ pageNode.parent.path);
                    }catch(Exception e){
                        println("Unable to Update the path"+e.getMessage());
                    }
                }
            }
        }
    }
}

if(!dry_run){
    session.save()
}else{
    println("This is dry run.")
}

