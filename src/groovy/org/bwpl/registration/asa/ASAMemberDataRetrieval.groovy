package org.bwpl.registration.asa

import groovy.util.slurpersupport.GPathResult
import groovyx.net.http.HTTPBuilder

import org.apache.http.conn.HttpHostConnectException
import org.apache.commons.lang.StringUtils

public class ASAMemberDataRetrieval {

    private final String url

    public ASAMemberDataRetrieval() {
        this("https://www.swimmingresults.org/membershipcheck/member_details.php")
    }

    protected ASAMemberDataRetrieval(String url) {
        this.url = url
    }

    public ASAMemberData get(final int asaNumber) {

        final ASAMemberData asaMemberData = new ASAMemberData(asaNumber)
        final HTTPBuilder http = new HTTPBuilder(url)
        try {

            final GPathResult html = http.get(query: [tiref: Integer.toString(asaNumber)])
            GPathResult element = html.BODY.depthFirst().find {it.text() == "Fee Paying Club"}
            if (element == null) {
                throw new ASAMemberDataRetrievalException("Error getting data for ASA number: $asaNumber. Error reading html data.")
            }
            GPathResult divElement = element.parent().parent().parent()
            GPathResult tableElement = divElement.TABLE[0]

            asaMemberData.name = tableElement.TR[0].TD[1].text()
            asaMemberData.gender = tableElement.TR[1].TD[3].text()
            asaMemberData.membershipCategory = tableElement.TR[2].TD[3].text()

            tableElement = divElement.TABLE[1]
            for (int i = 1; i < tableElement.TR.size(); i++) {
                if (!StringUtils.containsIgnoreCase(tableElement.TR[i].TD[2].text(), "Non Member")) {
                    asaMemberData.addClub(tableElement.TR[i].TD[1].text(), tableElement.TR[i].TD[3].text())
                }
            }

            /*
            tableElement = divElement.TABLE[2]
            for (int i = 1; i < tableElement.TR.size(); i++) {
                if (StringUtils.containsIgnoreCase(tableElement.TR[i].TD[1].text(), "Membership Category")) {
                    asaMemberData.membershipCategory = tableElement.TR[i].TD[2].text()
                    asaMemberData.membershipFromDate = tableElement.TR[i].TD[3].text()
                    break
                }
            } */
        }

        catch (HttpHostConnectException e) {
            throw new ASAMemberDataRetrievalException(e)
        }
        return asaMemberData
    }
}
