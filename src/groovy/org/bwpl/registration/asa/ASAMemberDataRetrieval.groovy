package org.bwpl.registration.asa

import groovy.util.slurpersupport.GPathResult
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.HttpResponseException
import org.apache.commons.lang.StringUtils
import org.apache.http.conn.HttpHostConnectException

class ASAMemberDataRetrieval {

    public static final String ASA_MEMBERSHIP_CHECK_URL = "https://www.swimmingresults.org/membershipcheck/member_details.php"
    public static final String ASA_NUMBER_PARAMETER_NAME = "myiref"

    private static final List<String> ASA_MEMBERSHIP_CHECK_ERROR_MESSAGES =
        ["There was an error processing your request", "The system is not connected to the database"]

    private static final int ASA_NUMBER_JAMES_REDDICK = 283261

    private final String url

    ASAMemberDataRetrieval() {
        this(ASA_MEMBERSHIP_CHECK_URL)
    }

    protected ASAMemberDataRetrieval(String url) {
        this.url = url
    }

    ASAMemberData get(final int asaNumber) {

        final HTTPBuilder http = new HTTPBuilder(url)
        try {

            final GPathResult html = http.get(query: ["$ASA_NUMBER_PARAMETER_NAME": Integer.toString(asaNumber)], headers: ['User-Agent': 'Apache HTTPClient    '])
            checkForServiceErrors(html)
            checkASANumberCanBeFound(html, asaNumber)
            return ASAMemberData.fromHtml(asaNumber, html)
        }
        catch (HttpHostConnectException e) {
            throw new ASAMemberDataRetrievalException(e)
        }
        catch (HttpResponseException e) {
            throw new ASAMemberDataRetrievalException(e)
        }
    }

    String getServiceError() {

        ASAMemberData asaData
        try {
            asaData = get(ASA_NUMBER_JAMES_REDDICK)
        }
        catch (ASAMemberDataRetrievalException e) {
            return getServiceErrorMessage(ASA_NUMBER_JAMES_REDDICK, e.message)
        }
        catch (ASAMemberDataValidationException e) {
            return "Error reading data from the ASA Membership system - $e.message"
        }
        catch (ASAMemberDataNotFoundException e) {
            return getServiceAvailabilityMessage()
        }

        boolean ok = asaData.name.contains("James") &&
                     asaData.name.contains("Reddick") &&
                     asaData.asaNumber == ASA_NUMBER_JAMES_REDDICK &&
                     asaData.isMale &&
                   (!asaData.clubs.findAll{it.name.contains("Polytechnic")}.isEmpty()) &&
                     asaData.membershipCategory == "ASA Cat 2"

        if (!ok) return getServiceAvailabilityMessage()
        return ""
    }

    private static void checkForServiceErrors(GPathResult html) {

        ASA_MEMBERSHIP_CHECK_ERROR_MESSAGES.each { errorMsg ->
            GPathResult element = html.BODY.depthFirst().find { StringUtils.containsIgnoreCase(it.text(), errorMsg) }
            if (element != null) {
                throw new ASAMemberDataRetrievalException(errorMsg)
            }
        }
    }

    private static void checkASANumberCanBeFound(GPathResult html, int asaNumber) {

        GPathResult element = html.BODY.depthFirst().find {it.text().trim() == "Fee Paying Club"}
        if (element == null) {
            throw new ASAMemberDataNotFoundException("Error getting data for ASA number: $asaNumber. Error reading html data.")
        }
    }

    private static String getServiceErrorMessage(int asaNumber, String errorMsg) {

        StringBuilder msg = new StringBuilder()
        msg << "ASA Membership Check system is unavailable. "
        msg << "It reports an error when checking for ASA number $asaNumber. "
        msg << "The error is: $errorMsg. "
        msg << "Please email the ASA to let them know."
        return msg.toString()
    }

    private static String getServiceAvailabilityMessage() {

        StringBuilder msg = new StringBuilder()
        msg << "Validation error - cannot get data from the ASA Membership Check system. "
        msg << "It may be unavailable so try again later. "
        msg << "If it is available then the ASA may have changed the format of their data "
        msg << "and therefore this validation process cannot read the ASA data. "
        msg << "If the problem persists then email the BWPL Registration users group."
        return msg.toString()
    }
}
