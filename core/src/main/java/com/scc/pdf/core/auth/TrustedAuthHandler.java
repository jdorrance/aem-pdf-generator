package com.scc.pdf.core.auth;

import org.apache.felix.scr.annotations.*;
import org.apache.sling.auth.core.spi.AuthenticationFeedbackHandler;
import org.apache.sling.auth.core.spi.AuthenticationHandler;
import org.apache.sling.auth.core.spi.AuthenticationInfo;
import org.apache.sling.auth.core.spi.DefaultAuthenticationFeedbackHandler;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by John on 11/17/2016.
 */
@Component(
        label = "SCC Foundation Services Trusted Auth handler"
)
@Properties({
        @Property(name = Constants.SERVICE_RANKING, intValue = 10000),
        @Property(name = "path", value = "/"),
        @Property(name = Constants.SERVICE_DESCRIPTION, value = "SCC Foundation Trusted Auth handler")
})
@Service
public class TrustedAuthHandler extends DefaultAuthenticationFeedbackHandler implements AuthenticationHandler, AuthenticationFeedbackHandler {

    /**
     * The referenced auth handler requires the 'target' property so the SSO specific auth handler will be injected
     */
    @Reference(target = "(service.pid=com.adobe.granite.auth.sso.impl.SsoAuthenticationHandler)")
    private AuthenticationHandler wrappedSSOHandler;


    public static String SSO_TRUSTED_HEADER = "trustedUser";

    private Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Due to the high service ranking - this authentication handler should be relatively high up in the 'food chain'.
     *
     * Quite simply, it uses the ConfigurationService to get a list of IP addresses that can be assumed to represent a trusted user, also dictated
     * in the ConfigurationService configuration. If the request comes from that IP address, a 'trustedHeader' will be set,
     * and the OOTB SSO configuration / implementation will handle passing the user through as a trusted user, by virtue of that
     * HTTP header that is spliced in.
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @return
     */
    public AuthenticationInfo extractCredentials(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {



        // get the address, don't forget it can be the new IP format which looks like a MAC address


        // we've got a match
        doLog("IP address IS whitelisted - using SSO handler");
        final HttpServletRequest httpRequest = httpServletRequest;

        // now for the interesting part, we will wrap the request in order to 'spoof' a trusted SSO header
        HttpServletRequest httpServletRequest2 = new HttpServletRequestWrapper(httpRequest) {
            @Override
            public String getHeader(String name) {

                // Note that SSO auth handler must be set up and configured on the server for this to work
                if(name != null && name.equals(SSO_TRUSTED_HEADER)){
                    doLog("returning trusted user");
                    return "admin";
                }
                return super.getHeader(name);
            }
        };

        // now we will pass on the responsibility for this authentication to th
        return wrappedSSOHandler.extractCredentials(httpServletRequest2, httpServletResponse);
    }

    public boolean requestCredentials(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        return false;
    }

    public void dropCredentials(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        //log.trace("dropping credentials");
    }

    private void doLog(String msg){
        if(log.isTraceEnabled()){
            log.trace(msg);
        }
    }

}
