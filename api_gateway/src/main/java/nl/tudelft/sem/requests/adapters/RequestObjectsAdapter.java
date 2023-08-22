package nl.tudelft.sem.requests.adapters;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.Enumeration;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;


public class RequestObjectsAdapter {

    /**
     * Method to change the type of request.
     *
     * @param httpServlet request to change
     * @param url         new url to forward
     * @param header      the new header that should be added
     * @return HttpRequest
     */
    public static HttpRequest change(HttpServletRequest httpServlet,
                                     String url,
                                     String header,
                                     String method,
                                     String body) {

        HttpRequest.Builder httpRequest = HttpRequest.newBuilder();

        Enumeration<String> attrNames = httpServlet.getParameterNames();

        // Add the user id and house id to the request parameters

        if (attrNames.hasMoreElements()
                || httpServlet.getSession().getAttributeNames().hasMoreElements()) {
            url += "?request=made";
            while (attrNames.hasMoreElements()) {

                url += "&";

                String attrN = attrNames.nextElement();
                url += attrN + "=" + httpServlet.getParameter(attrN);

            }
            String userId = (String) httpServlet.getSession().getAttribute("userId");

            if (userId != null)  {
                url += "&userId=" + userId + "&creditId=" + userId;

                String house = (String) httpServlet.getSession().getAttribute("houseId");

                if (house != null) {
                    url += "&houseId=" + house;
                }
            }

        }

        if (header != null) {
            httpRequest.header("Authentication", header);
        }
        httpRequest.header("Content-Type", "application/json");
        httpRequest.method(method, HttpRequest.BodyPublishers.ofString(body));
        httpRequest.uri(URI.create(url));

        return httpRequest.build();
    }
}
