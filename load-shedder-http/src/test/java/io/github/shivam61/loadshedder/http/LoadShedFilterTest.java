package io.github.shivam61.loadshedder.http;

import io.github.shivam61.loadshedder.core.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class LoadShedFilterTest {

    @Test
    void shouldRejectWhenOverloaded() throws Exception {
        AdaptiveLoadShedder shedder = new AdaptiveLoadShedder(
            List.of((req, snap) -> new LoadShedDecision(DecisionType.REJECT, DecisionReason.CAPACITY_EXCEEDED, 0.0, "Reject test")),
            ctx -> Optional.empty()
        );

        LoadShedFilter filter = new LoadShedFilter(shedder, () -> new SystemSnapshot(100, 100, 0, 100, 0, 0, 0, 0, 0, 0, 0, true));

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getRequestURI()).thenReturn("/api/test");
        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        filter.doFilter(request, response, chain);

        verify(response).setStatus(429);
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    void shouldSetDegradeAttribute() throws Exception {
        AdaptiveLoadShedder shedder = new AdaptiveLoadShedder(
            List.of((req, snap) -> new LoadShedDecision(DecisionType.DEGRADE, DecisionReason.CONTROLLER_REJECTION, 0.5, "Degrade test")),
            ctx -> Optional.empty()
        );

        LoadShedFilter filter = new LoadShedFilter(shedder, () -> new SystemSnapshot(50, 100, 0, 100, 0, 0, 0, 0, 0, 0, 0, true));

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getRequestURI()).thenReturn("/api/test");

        filter.doFilter(request, response, chain);

        verify(request).setAttribute("LOAD_SHED_DEGRADE", true);
        verify(chain).doFilter(request, response);
    }
}
