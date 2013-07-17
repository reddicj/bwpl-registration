package org.bwpl.registration.asa

import groovy.util.slurpersupport.GPathResult
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.HttpResponseException
import org.apache.commons.lang.StringUtils
import org.apache.http.conn.HttpHostConnectException

class ASAMemberDataRetrieval {

    public static final String ASA_MEMBERSHIP_CHECK_URL = "https://www.swimmingresults.org/membershipcheck/member_details.php"
    public static final String ASA_NUMBER_PARAMETER_NAME = "myiref"
    private static final String ASA_MEMBERSHIP_CHECK_ERROR_MESSAGE = "There was an error processing your request"

    private static final int ASA_NUMBER_JAMES_REDDICK = 283261
    private static final String SERVICE_AVAILABILITY_ERROR_MESSAGE = getServiceAvailabilityMessage()

    private final String url

    ASAMemberDataRetrieval() {
        this(ASA_MEMBERSHIP_CHECK_URL)
    }

    protected ASAMemberDataRetrieval(String url) {
        this.url = url
    }

    ASAMemberData get(final int asaNumber) {

        final ASAMemberData asaMemberData = new ASAMemberData(asaNumber)
        final HTTPBuilder http = new HTTPBuilder(url)
        try {

            final GPathResult html = http.get(query: ["$ASA_NUMBER_PARAMETER_NAME": Integer.toString(asaNumber)])
            GPathResult element = html.BODY.depthFirst().find { StringUtils.containsIgnoreCase(it.text(), ASA_MEMBERSHIP_CHECK_ERROR_MESSAGE) }
            if (element != null) {
                throw new ASAMemberDataRetrievalException(SERVICE_AVAILABILITY_ERROR_MESSAGE)
            }
            element = html.BODY.depthFirst().find {it.text() == "Fee Paying Club"}
            if (element == null) {
                throw new ASAMemberDataNotFoundException("Error getting data for ASA number: $asaNumber. Error reading html data.")
            }
            GPathResult divElement = element.parent().parent().parent()
            GPathResult tableElement = divElement.TABLE[0]

            asaMemberData.name = tableElement.TR[0].TD[1].text()
            asaMemberData.dateOfBirth = tableElement.TR[1].TD[1].text()
            asaMemberData.gender = tableElement.TR[1].TD[3].text()
            asaMemberData.membershipCategory = tableElement.TR[2].TD[3].text()

            tableElement = divElement.TABLE[1]
            for (int i = 1; i < tableElement.TR.size(); i++) {
                asaMemberData.addClub(tableElement.TR[i].TD[1].text(), tableElement.TR[i].TD[3].text(), tableElement.TR[i].TD[2].text())
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
        catch (HttpResponseException e) {
            throw new ASAMemberDataRetrievalException(e)
        }
        return asaMemberData
    }

    String getServiceError() {

        ASAMemberData asaData = null
        try {
            asaData = get(ASA_NUMBER_JAMES_REDDICK)
        }
        catch (ASAMemberDataRetrievalException e) {
            return SERVICE_AVAILABILITY_ERROR_MESSAGE
        }
        catch (ASAMemberDataValidationException e) {
            return "Error reading data from the ASA Membership system - $e.message"
        }

        boolean ok = asaData.name.contains("James") &&
                     asaData.name.contains("Reddick") &&
                     asaData.asaNumber == ASA_NUMBER_JAMES_REDDICK &&
                     asaData.isMale &&
                   (!asaData.clubs.findAll{it.name.contains("Polytechnic")}.isEmpty()) &&
                     asaData.membershipCategory == "ASA Cat 2"

        if (!ok) return SERVICE_AVAILABILITY_ERROR_MESSAGE
        return ""
    }

    private static String getServiceAvailabilityMessage() {

        StringBuilder msg = new StringBuilder()
        msg << "Validation error - cannot get data from the ASA Membership system. "
        msg << "It may be unavailable so try again later. "
        msg << "If it is available then the ASA may have changed the format of their data "
        msg << "and therefore this validation process cannot read the ASA data. "
        msg << "If the problem persists then email the BWPL Registration users group."
        return msg.toString()
    }
}
