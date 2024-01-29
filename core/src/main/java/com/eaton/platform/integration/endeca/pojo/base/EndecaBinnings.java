package com.eaton.platform.integration.endeca.pojo.base;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.integration.endeca.constants.EndecaConstants;
import com.eaton.platform.core.search.api.VendorFacetGroups;

import java.util.Optional;
import java.util.Set;

/**
 * Endeca's implementation for VendorFacetGroups
 */
public class EndecaBinnings implements VendorFacetGroups<EndecaBinning> {
    private Set<EndecaBinning> binningSet;

    public Long getProductsCount() {
        return getCountForBinInContent(EndecaConstants.PRODUCTS_STRING);
    }

    public Long getServicesCount() {
        return getCountForBinInContent(EndecaConstants.SERVICES_STRING);
    }

    public Long getNewsCount() {
        return getCountForBinInContent(EndecaConstants.NEWS_STRING);
    }

    public Long getResourcesCount() {
        return getCountForBinInContent(EndecaConstants.RESOURCES_STRING);
    }

    @Override
    public Set<EndecaBinning> getFacetGroup() {
        return binningSet;
    }

    private Long getCountForBinInContent(String binLabel) {
        return getCountForBinningAndBinLabel(binLabel, CommonConstants.CONTENT_TYPE_LOWER_CASE);
    }

    private Long getCountForBinningAndBinLabel(String binLabel, String binningLabel) {
        Optional<EndecaBin> productsOptional = getEndecaBinForBinningAndBinLabel(binLabel, binningLabel);
        if (productsOptional.isPresent()) {
            return productsOptional.get().getNumberOfDocs();
        }
        return 0L;
    }

    private Optional<EndecaBin> getEndecaBinForBinningAndBinLabel(String binLabel, String binningLabel) {
        Optional<EndecaBinning> endecaBinningOptional = getEndecaBinningForLabel(binningLabel);
        return endecaBinningOptional.flatMap(endecaBinning -> endecaBinning.getFacets().stream().filter(endecaBin -> binLabel.equals(endecaBin.getLabel())).findFirst());
    }

    private Optional<EndecaBinning> getEndecaBinningForLabel(String label) {
        if (binningSet == null) {
            return Optional.empty();
        }
        return binningSet.stream().filter(endecaBinning -> label.equals(endecaBinning.getGroupLabel())).findFirst();
    }
}
