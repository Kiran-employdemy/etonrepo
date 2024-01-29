package com.eaton.platform.core.servlets.productdatasheet;

import java.util.ArrayList;
import java.util.List;

public class ProductDatasheetGeneratorOutput {
    private List<ProductDatasheetOutput> datasheetGen;

    public void addDataSheetOutput(ProductDatasheetOutput productDatasheetOutput) {
        if (datasheetGen == null) {
            datasheetGen = new ArrayList<>();
        }
        datasheetGen.add(productDatasheetOutput);
    }

}
