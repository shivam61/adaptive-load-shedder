package io.github.shivam61.loadshedder.http;

import io.github.shivam61.loadshedder.core.*;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoadShedFilter implements Filter {

    private final AdaptiveLoadShedder shedder;
    private final java.util.function.Supplier<SystemSnapshot> snapshotSupplier;

    public LoadShedFilter(AdaptiveLoadShedder shedder, java.util.function.Supplier<SystemSnapshot> snapshotSupplier) {
        this.shedder = shedder;
        this.snapshotSupplier = snapshotSupplier;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        RequestContext context = RequestContext.builder()
                .route(req.getRequestURI())
                .build();

        LoadShedDecision decision = shedder.evaluate(context, snapshotSupplier.get());

        if (decision == LoadShedDecision.REJECT) {
            res.setStatus(429); // Too Many Requests
            res.getWriter().write("Request shed due to high load");
            return;
        }

        if (decision == LoadShedDecision.DEGRADE) {
            req.setAttribute("LOAD_SHED_DEGRADE", true);
        }

        chain.doFilter(request, response);
    }
}
