package io.github.shivam61.loadshedder.grpc;

import io.github.shivam61.loadshedder.core.*;
import io.grpc.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class LoadShedInterceptorTest {

    @Test
    void shouldRejectCall() {
        AdaptiveLoadShedder shedder = new AdaptiveLoadShedder(
            List.of((req, snap) -> new LoadShedDecision(DecisionType.REJECT, DecisionReason.CAPACITY_EXCEEDED, 0.0, "Reject test")),
            ctx -> Optional.empty()
        );

        LoadShedInterceptor interceptor = new LoadShedInterceptor(shedder, () -> new SystemSnapshot(100, 100, 0, 100, 0, 0, 0, 0, 0, 0, 0, true));

        ServerCall call = mock(ServerCall.class);
        Metadata headers = new Metadata();
        ServerCallHandler handler = mock(ServerCallHandler.class);

        MethodDescriptor method = MethodDescriptor.newBuilder()
            .setType(MethodDescriptor.MethodType.UNARY)
            .setFullMethodName("test/Method")
            .setRequestMarshaller(mock(MethodDescriptor.Marshaller.class))
            .setResponseMarshaller(mock(MethodDescriptor.Marshaller.class))
            .build();

        when(call.getMethodDescriptor()).thenReturn(method);

        interceptor.interceptCall(call, headers, handler);

        verify(call).close(argThat(status -> status.getCode() == Status.Code.RESOURCE_EXHAUSTED), any());
        verify(handler, never()).startCall(any(), any());
    }
}
