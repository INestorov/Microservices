package nl.tudelft.sem.controllers.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nl.tudelft.sem.configuration.HttpMessages;
import nl.tudelft.sem.controllers.FoodForwardController;
import nl.tudelft.sem.services.LoggerService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 *      A {@link Filter} class that checks if the incoming request is
 *  a login or a register request, using the encapsulating {@link HttpServletRequest}
 *  object. If that is not the case, then the user session encapsulated in the
 *  {@link javax.servlet.http.HttpSession} session object is checked for the
 *  existence of an 'userId' attribute which indicates that the user already logged in.
 *  If it is not found, an error BAD Request response is returned notifying the user
 *  he/she has not yet logged in.
 *
 */
@Component
@Order(2)
public class CheckLoginFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LoggerService.getInstance().logInfo(
                "Filter for checking log-in of users initialized successfully.",
                ParameterIdsFilter.class
        );
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest servletRequest = (HttpServletRequest) request;

        String path = servletRequest.getPathInfo();
        if (!path.contains("sign-up") && !path.contains("login")) {

            if (servletRequest.getSession().getAttribute("userId") == null) {
                LoggerService.getInstance().logError(
                        HttpMessages.ERROR_NO_LOGIN,
                        FoodForwardController.class
                );

                HttpServletResponse res = (HttpServletResponse) response;
                res.setStatus(400);
                res.getWriter().print(HttpMessages.ERROR_NO_LOGIN);
                return;
            }

        }
        chain.doFilter(request, response);


    }

    @Override
    public void destroy() {
        LoggerService.getInstance().logInfo(
                "Filter for checking log-in of users destroyed successfully.",
                ParameterIdsFilter.class
        );
    }
}
