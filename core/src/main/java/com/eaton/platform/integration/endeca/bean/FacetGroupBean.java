package com.eaton.platform.integration.endeca.bean;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.models.PdhConfigModel;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Object to use for converting FacetGroup information to and from json
 */
@JsonSerialize(include=JsonSerialize.Inclusion.ALWAYS)
@JsonPropertyOrder({
"label",
"bsId",
"bin"
})
public class FacetGroupBean implements Serializable {
	private static final long serialVersionUID = -633007230290240499L;

	@JsonProperty("label")
    private String facetGroupLabel;
    @JsonProperty("bsId")
    private String facetGroupId;
    @JsonProperty("bin")

    private List<FacetValueBean> facetValueList;
    private String inputType;
	private boolean gridFacet;
	private boolean facetSearchEnabled;
	private boolean singleFacetEnabled;
	private boolean secure;
	private String facetTempGroupId;

	private Integer sortOrder;


	public Integer getSortOrder() {
		if (sortOrder != null) {
			return sortOrder;
		}
		return Integer.valueOf(0);
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getInputType() {
		return inputType;
	}

	public void setInputType(String inputType) {
		this.inputType = inputType;
	}

	public String getFacetGroupLabel() {
		return facetGroupLabel;
	}

	public void setFacetGroupLabel(String facetGroupLabel) {
		this.facetGroupLabel = facetGroupLabel;
	}

	public boolean isSecure(){
		return secure;
	}

	public void setSecure(boolean secure){
		this.secure = secure;
	}

	/**
	 * Sets the group label using as parameters
	 * @param request The current sling request coming into AEM. This will be used while determining the i18n dictionary to use.
	 * @param page The current page to use while determining the i18n dictionary to use.
	 * @param pdhConfigModel The PDH config model containing a list of combo attributes which will be used to convert the
	 *                       AEM attribute into an i18n key. This i18n key is then used to set the label of this FacetGroupBean.
	 *                       If no i18n key can be found then the facet group label is left untouched. If the pdh config model
	 *                       is not present then the facet group label will not be set.
	 */
	public void setFacetGroupLabel(SlingHttpServletRequest request, Page page, PdhConfigModel pdhConfigModel) {
        if (pdhConfigModel != null) {
            if (facetGroupId != null) {
            	// This assumes that the returned facetGroupId has already been converted into the AEM response attribute.
                Optional<String> i18nKeyOptional = pdhConfigModel.getI18nKey(facetGroupId);
				i18nKeyOptional.ifPresent(i18nKey -> this.setFacetGroupLabel(CommonUtil.getI18NFromResourceBundle(request, page, i18nKey, i18nKey)));
            }
        }
	}

	public String getFacetGroupId() {
		return facetGroupId;
	}

	public void setFacetGroupId(String facetGroupId) {
		this.facetGroupId = facetGroupId;
	}

	public List<FacetValueBean> getFacetValueList() {
		return facetValueList;
	}

	public void setFacetValueList(List<FacetValueBean> facetValueList) {
		this.facetValueList = facetValueList;
	}

	public boolean isGridFacet() {
		return gridFacet;
	}

	public void setGridFacet(boolean gridFacet) {
		this.gridFacet = gridFacet;
	}

	public boolean isFacetSearchEnabled() {
		return facetSearchEnabled;
	}

	public void setFacetSearchEnabled(boolean facetSearchEnabled) {
		this.facetSearchEnabled = facetSearchEnabled;
	}

	public final void setSingleFacetEnabled(final boolean singleFacetEnabled) {
		this.singleFacetEnabled = singleFacetEnabled;
	}

	public boolean isSingleFacetEnabled() {
		return singleFacetEnabled;
	}

	public String getFacetTempGroupId() {
		return facetTempGroupId;
	}

	/**
	 * This method stores original Tag namespace(groupId) from ENDECA
	 * @param facetTempGroupId
	 */
	public void setFacetTempGroupId(String facetTempGroupId) {
		this.facetTempGroupId = facetTempGroupId;
	}

	@Override
	public String toString() {
		return "FacetGroupBean{" +
				"facetGroupLabel='" + facetGroupLabel + '\'' +
				", facetGroupId='" + facetGroupId + '\'' +
				", facetValueList=" + facetValueList +
				", inputType='" + inputType + '\'' +
				", gridFacet=" + gridFacet +
				", facetSearchEnabled=" + facetSearchEnabled +
				", singleFacetEnabled=" + singleFacetEnabled +
				", secure=" + secure +
				", facetTempGroupId='" + facetTempGroupId + '\'' +
				", sortOrder=" + sortOrder +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		FacetGroupBean that = (FacetGroupBean) o;
		return gridFacet == that.gridFacet && facetSearchEnabled == that.facetSearchEnabled
				&& singleFacetEnabled == that.singleFacetEnabled && secure == that.secure
				&& Objects.equals(facetGroupLabel, that.facetGroupLabel)
				&& Objects.equals(facetGroupId, that.facetGroupId) && Objects.equals(facetValueList, that.facetValueList)
				&& Objects.equals(inputType, that.inputType) && Objects.equals(facetTempGroupId, that.facetTempGroupId)
				&& Objects.equals(sortOrder, that.sortOrder);
	}

	@Override
	public int hashCode() {
		return Objects.hash(facetGroupLabel, facetGroupId, facetValueList, inputType, gridFacet, facetSearchEnabled, singleFacetEnabled, secure, facetTempGroupId, sortOrder);
	}
}