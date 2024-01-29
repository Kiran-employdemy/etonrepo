package com.eaton.platform.integration.endeca.bean;

import com.eaton.platform.core.constants.CommonConstants;

import java.util.ArrayList;
import java.util.List;

import static com.eaton.platform.integration.endeca.bean.FacetValueBeanFixtures.createFacetValue;
import static com.eaton.platform.integration.endeca.constants.EndecaConstants.EATON_SECURE_FACET_GROUP_LABEL;
import static com.eaton.platform.integration.endeca.constants.EndecaConstants.SECURE_ONLY_FILTER_VALUE;

public class FacetGroupBeanFixtures {

    public static final String SUPER_MARKETS_GROUP_LABEL = "supermarkets_market";
    public static final String OTHER_GROUP_1_LABEL = "other-group-1";

    public static List<FacetGroupBean> createFacetGroupBeansWithStatus(String statusLabel) {
        ArrayList<FacetGroupBean> facetGroupBeans = new ArrayList<>();
        facetGroupBeans.add(formFactorFacetGroup());
        facetGroupBeans.add(inputConnectionFacetGroup());
        facetGroupBeans.add(inputVoltageFacetGroup());
        facetGroupBeans.add(mountingDirectionFacetGroup(145));
        facetGroupBeans.add(statusFacetGroupWithGroupId(statusLabel));
        return facetGroupBeans;
    }

    public static FacetGroupBean formFactorFacetGroup() {
        ArrayList<FacetValueBean> facetValueBeans = new ArrayList<>();
        facetValueBeans.add(createFacetValue("1U", "193604915", 61));
        facetValueBeans.add(createFacetValue("2U", "2758892035", 77));
        facetValueBeans.add(createFacetValue("3U", "2566035805", 6));
        facetValueBeans.add(createFacetValue("5U", "1275241078", 1));
        facetValueBeans.add(createFacetValue("7U", "2444893480", 1));
        return facetGroupBean("Form factor", "Form_Factor", facetValueBeans);
    }

    public static FacetGroupBean inputConnectionFacetGroup() {
        ArrayList<FacetValueBean> facetValueBeans = new ArrayList<>();
        facetValueBeans.add(createFacetValue("5-15P", "4055962489", 4));
        facetValueBeans.add(createFacetValue("5-20P", "3836595498", 4));
        facetValueBeans.add(createFacetValue("C20", "21943785", 8));
        facetValueBeans.add(createFacetValue("L21-30P", "265441823", 3));
        facetValueBeans.add(createFacetValue("L5-30P", "1029561042", 1));
        facetValueBeans.add(createFacetValue("L6-30P", "3007294413", 3));
        return facetGroupBean("Input connection", "Input_Connection", facetValueBeans);
    }

    public static FacetGroupBean inputVoltageFacetGroup() {
        ArrayList<FacetValueBean> facetValueBeans = new ArrayList<>();
        facetValueBeans.add(createFacetValue("(2) 120/208V WYE", "4155879110", 2));
        facetValueBeans.add(createFacetValue("100-240V", "2673471490", 5));
        facetValueBeans.add(createFacetValue("110-125V", "3461807081", 38));
        facetValueBeans.add(createFacetValue("110-125V, 120/208V WYE", "3421129400", 8));
        return facetGroupBean("Input voltage", "Input_Voltage", facetValueBeans);
    }

    public static FacetGroupBean mountingDirectionFacetGroup(int facetValueDocs) {
        ArrayList<FacetValueBean> facetValueBeans = new ArrayList<>();
        facetValueBeans.add(createFacetValue("Horizontal", "3565711030", facetValueDocs));
        return facetGroupBean("Mounting direction", "Mounting_Direction", facetValueBeans);
    }

    public static FacetGroupBean contentTypeGroup() {
        ArrayList<FacetValueBean> facetValueBeans = new ArrayList<>();
        facetValueBeans.add(createFacetValue("Products", "1316679294", 0));
        facetValueBeans.add(createFacetValue("News & insights", "842808312", 0));
        facetValueBeans.add(createFacetValue("Resources", "968989543", 0));
        return facetGroupBean("Content Type", "content-type", facetValueBeans);
    }

    public static FacetGroupBean categoryGroup() {
        ArrayList<FacetValueBean> facetValueBeans = new ArrayList<>();
        facetValueBeans.add(createFacetValue("Accessories For Fire Alarm Systems", "4170307893", 0));
        facetValueBeans.add(createFacetValue("Air Ckt Breaker Renewal Part/Accessories", "584559396", 0));
        facetValueBeans.add(createFacetValue("Circuit Interrupter Wiring Devices", "3869039960", 0));
        return facetGroupBean("Product category", "Category_Name", facetValueBeans);
    }
    public static FacetGroupBean topologyGroup() {
        ArrayList<FacetValueBean> facetValueBeans = new ArrayList<>();
        facetValueBeans.add(createFacetValue("Line-interactive", "3973989620", 0));
        facetValueBeans.add(createFacetValue("Multi-mode", "3017103790", 0));
        facetValueBeans.add(createFacetValue("Online", "1600114535", 0));
        return facetGroupBean("Topology", "product-attributes_topology", facetValueBeans);
    }

    public static FacetGroupBean statusFacetGroupWithGroupId(String statusGroupId) {
        ArrayList<FacetValueBean> facetValueBeans = new ArrayList<>();
        facetValueBeans.add(createFacetValue(CommonConstants.STATUS_ACTIVE, "3562001493", 145));
        facetValueBeans.add(createFacetValue("Discontinued", "4174978688", 145));
        return facetGroupBean("Product Status", statusGroupId, facetValueBeans);
    }

    public static FacetGroupBean facetGroupBean(String facetGroupLabel, String facetGroupId, List<FacetValueBean> facetValues) {
        FacetGroupBean facetGroupBean = new FacetGroupBean();
        facetGroupBean.setFacetGroupLabel(facetGroupLabel);
        facetGroupBean.setFacetGroupId(facetGroupId);
        facetGroupBean.setFacetValueList(facetValues);
        return facetGroupBean;
    }
    public static List<FacetGroupBean> createWithoutSecureFacetFixtureSingle() {
        ArrayList<FacetGroupBean> facetGroupBeans = new ArrayList<>();
        facetGroupBeans.add(createFacetGroup(SUPER_MARKETS_GROUP_LABEL));
        return facetGroupBeans;
    }

    public static List<FacetGroupBean> createWithoutSecureFacetFixtureMulti() {
        ArrayList<FacetGroupBean> facetGroupBeans = new ArrayList<>();
        facetGroupBeans.add(createFacetGroup(SUPER_MARKETS_GROUP_LABEL));
        facetGroupBeans.add(createFacetGroup("other-group"));
        facetGroupBeans.add(createFacetGroup(OTHER_GROUP_1_LABEL));
        return facetGroupBeans;
    }

    public static List<FacetGroupBean> createWithSecureFacetFixtureMultiWithCount(int count) {
        ArrayList<FacetGroupBean> facetGroupBeans = new ArrayList<>();
        facetGroupBeans.add(createFacetGroup(SUPER_MARKETS_GROUP_LABEL));
        facetGroupBeans.add(createSecureFacetWithCount(count));
        facetGroupBeans.add(createFacetGroup(OTHER_GROUP_1_LABEL));
        return facetGroupBeans;
    }
    public static List<FacetGroupBean> createSingleSecureFacetFixtureWithCount(int count) {
        ArrayList<FacetGroupBean> facetGroupBeans = new ArrayList<>();
        facetGroupBeans.add(createSecureFacetWithCount(count));
        return facetGroupBeans;
    }

    public static FacetGroupBean createSecureFacetWithCount(int count) {
        FacetGroupBean facetGroup = createFacetGroup(EATON_SECURE_FACET_GROUP_LABEL);
        facetGroup.setFacetValueList(createSecureFacetValue(count));
        return facetGroup;
    }

    private static List<FacetValueBean> createSecureFacetValue(int count) {
        ArrayList<FacetValueBean> facetValueBeans = new ArrayList<>();
        facetValueBeans.add(creatValue(SECURE_ONLY_FILTER_VALUE, count));
        return facetValueBeans;
    }

    public static FacetGroupBean createFacetGroup(String groupLabel) {
        FacetGroupBean facetGroupBean = new FacetGroupBean();
        facetGroupBean.setFacetGroupId(groupLabel);
        facetGroupBean.setFacetGroupLabel(groupLabel);
        facetGroupBean.setFacetValueList(createValues());
        return facetGroupBean;
    }

    private static List<FacetValueBean> createValues() {
        ArrayList<FacetValueBean> facetValueBeans = new ArrayList<>();
        facetValueBeans.add(creatValue("label-1", 2));
        facetValueBeans.add(creatValue("label-2", 4));
        return facetValueBeans;
    }

    private static FacetValueBean creatValue(String facetValueLabel, int facetValueDocs) {
        FacetValueBean facetValueBean = new FacetValueBean();
        facetValueBean.setFacetValueLabel(facetValueLabel);
        facetValueBean.setFacetValueDocs(facetValueDocs);
        return facetValueBean;
    }

    public static List<FacetGroupBean> coupleOfFacetGroupBeansWithSecureFilter() {
        ArrayList<FacetGroupBean> facetGroupBeans = new ArrayList<>();
        facetGroupBeans.add(createSecureFacetWithCount(55));
        facetGroupBeans.add(contentTypeGroup());
        facetGroupBeans.add(categoryGroup());
        facetGroupBeans.add(topologyGroup());
        facetGroupBeans.add(formFactorFacetGroup());
        return facetGroupBeans;
    }

    public static List<FacetGroupBean> coupleOfFacetGroupBeansWithSecureFilterAndMountingGroup(int pageCountForMountingGroup) {
        ArrayList<FacetGroupBean> facetGroupBeans = new ArrayList<>();
        facetGroupBeans.add(createSecureFacetWithCount(55));
        facetGroupBeans.add(contentTypeGroup());
        facetGroupBeans.add(categoryGroup());
        facetGroupBeans.add(topologyGroup());
        facetGroupBeans.add(formFactorFacetGroup());
        facetGroupBeans.add(mountingDirectionFacetGroup(pageCountForMountingGroup));
        return facetGroupBeans;
    }
}