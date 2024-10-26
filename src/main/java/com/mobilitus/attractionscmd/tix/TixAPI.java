package com.mobilitus.attractionscmd.tix;

import com.mobilitus.attractionscmd.tix.data.TixEvent;
import com.mobilitus.attractionscmd.tix.data.TixEventList;
import com.mobilitus.util.hexia.Pair;
import com.mobilitus.util.hexia.StrUtil;
import com.mobilitus.util.httputil.HTTPUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author helgaw
 * @todo add class description.
 * @since 3/1/21 11:01
 */
public class TixAPI
{
    private static final Logger logger = Logger.getLogger(TixAPI.class);

    private String api = null;


    public TixAPI()
    {
    }

    public TixAPI(String source)
    {
//        SystemSettingsLocal systemSettings = SystemSettingsFactory.getLocal();
//
//        ThirdPartyGatewayEntity tixis;
//        if (source == null || source.isEmpty() || source.equalsIgnoreCase("tixis") ||  source.equalsIgnoreCase("tix"))
//        {
//            tixis = systemSettings.findGatewayLocal("tixis","api");
//        }
//        else
//        {
//            tixis = systemSettings.findGatewayLocal("tixis", source);
//        }
//
//        if (tixis != null)
//        {
//            if (tixis.getPartnerID() != null && !tixis.getPartnerID().isEmpty())
//            {
//                api = tixis.getCompleteURL() + "/" + tixis.getPartnerID() + "/";
//            }
//            else
//            {
//                api = tixis.getCompleteURL();
//            }
//        }

    }

    public List<TixEvent> getEvents()
    {
        if (api == null)
        {
            logger.error("No API configured for tix.is");
            return null;
        }

        List<Pair<String, String>> headers = new ArrayList<>(2);

        String json = HTTPUtil.get(api, headers, 30000);

        logger.info(StrUtil.formatAsJson(json));
        List<TixEvent> events = TixEventList.create(json);
        return events;
    }
}
