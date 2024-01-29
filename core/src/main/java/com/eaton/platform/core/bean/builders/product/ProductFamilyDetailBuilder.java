package com.eaton.platform.core.bean.builders.product;

import com.eaton.platform.core.bean.ProductFamilyPIMDetails;
import com.eaton.platform.core.bean.builders.product.exception.MissingFillingBeanException;

public class ProductFamilyDetailBuilder {
    private BeanFiller[] filler;
    public ProductFamilyDetailBuilder prepare(BeanFiller... filler){
        this.filler = filler;
        return this;
    }

    public ProductFamilyPIMDetails build() throws MissingFillingBeanException {
        if(filler == null){
            throw new IllegalArgumentException("Unable to build because missing Bean Filler Object");
        }
        ProductFamilyPIMDetails bean = new ProductFamilyPIMDetails();
        for(BeanFiller<ProductFamilyPIMDetails> curFiller : filler){
            curFiller.fill(bean);
        }
        return bean;
    }

}
