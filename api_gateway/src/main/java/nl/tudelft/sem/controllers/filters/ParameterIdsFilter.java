package nl.tudelft.sem.controllers.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import nl.tudelft.sem.configuration.HttpMessages;
import nl.tudelft.sem.services.LoggerService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 *      A {@link Filter} class that checks if a request contains
 * illegal request parameters. The filter checks the request parameters
 * of the received {@link javax.servlet.http.HttpServletRequest} object.
 * If an illegal parameter is found, the filter returns a BAD REQUEST
 * HTTP response with a proper message to notify the user.
 *
 */
@Component
@Order(1)
public class ParameterIdsFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LoggerService.getInstance().logInfo(
                "Filter for request parameters initialized successfully.",
                ParameterIdsFilter.class
        );
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String userId = request.getParameter("userId");
        String houseId = request.getParameter("houseId");

        if (userId != null || houseId != null) {
            LoggerService.getInstance().logError(
                    HttpMessages.ERROR_ILLEGAL_PARAMETERS,
                    ParameterIdsFilter.class
            );
            HttpServletResponse res = (HttpServletResponse) response;
            res.setStatus(400);
            res.getWriter().print("ERROR: Your request contains either"
                    + " a userId attribute or a houseId attribute, which are illegal!");
        } else {
            chain.doFilter(request, response);
        }

    }

    @Override
    public void destroy() {
        LoggerService.getInstance().logInfo(
                "Filter for request parameters destroyed successfully.",
                ParameterIdsFilter.class
        );
    }
}
