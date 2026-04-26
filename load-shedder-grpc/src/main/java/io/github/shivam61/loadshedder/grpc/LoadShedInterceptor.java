package io.github.shivam61.loadshedder.grpc;
import io.github.shivam61.loadshedder.core.*;
import io.grpc.*;
public class LoadShedInterceptor implements ServerInterceptor {
    private final AdaptiveLoadShedder shedder;
    private final java.util.function.Supplier<SystemSnapshot> snapshotSupplier;
    public LoadShedInterceptor(AdaptiveLoadShedder shedder, java.util.function.Supplier<SystemSnapshot> snapshotSupplier) { this.shedder = shedder; this.snapshotSupplier = snapshotSupplier; }
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        RequestContext context = RequestContext.builder().route(call.getMethodDescriptor().getFullMethodName()).build();
        LoadShedDecision decision = shedder.evaluate(context, snapshotSupplier.get());
        if (decision.type() == DecisionType.REJECT) {
            call.close(Status.RESOURCE_EXHAUSTED.withDescription("Shed: " + decision.explanation()), new Metadata());
            return new ServerCall.Listener<ReqT>() {};
        }
        return next.startCall(call, headers);
    }
}
