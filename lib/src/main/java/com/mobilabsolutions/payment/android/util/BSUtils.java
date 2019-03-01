package com.mobilabsolutions.payment.android.util;

import android.util.Base64;

/**
 * BS psp utils
 */
public final class BSUtils {

    //SOAP envelope xml namespaces
    private static final String NS_SOAP = "http://www.w3.org/2003/05/soap-envelope";
    private static final String NS_NS = "http://www.voeb-zvd.de/xmlapi/1.0";

    //soap envelope parent Element
    private static final String PARENT_ELEMENT = "xmlApiRequest";
    private static final String PARENT_ELEMENT_VERSION = "1.6";
    private static final String PARENT_ELEMENT_ID = "a1";

    //soap envelope sub-parent Element
    private static final String SUB_PARENT_ELEMENT = "paymentRequest";
    private static final String SUB_PARENT_ELEMENT_ID = "b1";

    //aliasId request sub_parent_element
    private static final String REPEATED_SUB_PARENT_ELEMENT = "panAliasRequest";
    private static final String REPEATED_SUB_PARENT_ELEMENT_ID = "a12";

    private static final int SUCCESS_CODE = 0000;



    public static String getBasicAuthString(String username, String password) {
        String credentials = username + ":" + password;
        return Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
    }


}
