package com.eaton.platform.core.services.secure;

import com.eaton.platform.core.bean.secure.SecureAssetsBean;
import org.apache.sling.api.SlingHttpServletRequest;
import org.json.JSONArray;

import javax.jcr.RepositoryException;
import java.util.Date;
import java.util.List;

/**
 * ICF
 */
public interface WhatsNewService {

    /**
     *
     * This method Accepts Date Range(StartDate & EndDate) as an input and fetches data from ENDECA from the specified range.
     * @param req SlingHttpServletRequest
     * @param userId UserId
     * @param startDate StartDate
     * @param endDate EndDate
     * @return JSONArray results from ENDECA
     */
    JSONArray getLatestAssetsFromEndecaByRange(final SlingHttpServletRequest req, final String userId, Date startDate, Date endDate);

    /**
     * This method converts JSONArray Results to AssetBean objects as list
     * @param jsonArray  JSONArray
     * @param secureSelectedAssets List of Beans
     * @throws RepositoryException
     */
    void getAssetsFromHits(final JSONArray jsonArray, final List<SecureAssetsBean> secureSelectedAssets);
}
