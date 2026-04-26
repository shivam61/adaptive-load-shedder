package io.github.shivam61.loadshedder.grpc;

import io.github.shivam61.loadshedder.core.*;
import io.grpc.*;

public class LoadShedInterceptor implements ServerInterceptor {

    private final AdaptiveLoadShedder shedder;
    // We would normally inject a way to get the SystemSnapshot, here we simplify
    private final java.util.function.Supplier<SystemSnapshot> snapshotSupplier;

    public LoadShedInterceptor(AdaptiveLoadShedder shedder, java.util.function.Supplier<SystemSnapshot> snapshotSupplier) {
        this.shedder = shedder;
        this.snapshotSupplier = snapshotSupplier;
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        RequestContext context = RequestContext.builder()
                .route(call.getMethodDescriptor().getFullMethodName())
                // extract priority from headers if present, etc.
                .build();

        LoadShedDecision decision = shedder.evaluate(context, snapshotSupplier.get());

        if (decision == LoadShedDecision.REJECT) {
            call.close(Status.RESOURCE_EXHAUSTED.withDescription("Request shed due to high load"), new Metadata());
            return new ServerCall.Listener<ReqT>() {}; // no-op listener
        }

        // Context could be passed down to support DEGRADE via ThreadLocal / Context
        return next.startCall(call, headers);
    }
}
