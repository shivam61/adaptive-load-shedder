package io.github.shivam61.loadshedder.benchmarks;

import io.github.shivam61.loadshedder.core.*;
import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import java.util.Map;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class PolicyBenchmark {

    private StaticThresholdPolicy staticPolicy;
    private ControllerDrivenPolicy controllerPolicy;
    private TokenBucketPolicy tokenPolicy;
    private RequestContext request;
    private SystemSnapshot snapshot;

    @Setup
    public void setup() {
        staticPolicy = new StaticThresholdPolicy(1000);
        
        AimdAdaptiveController aimd = new AimdAdaptiveController();
        aimd.update(new ControlSnapshot(40.0, 50.0, 0, 1000, 500, 1000, 0, 0.05, 0, 0.05));
        controllerPolicy = new ControllerDrivenPolicy(aimd);
        
        tokenPolicy = new TokenBucketPolicy(10000.0, 1000);

        request = RequestContext.builder().priority(Priority.NORMAL).build();
        snapshot = new SystemSnapshot(500, 1000, 10, 1000, 0.5, 0.5, 30.0, 45.0, 60.0, 0.01, 0.01, true);
    }

    @Benchmark
    public LoadShedDecision benchmarkStaticPolicy() {
        return staticPolicy.decide(request, snapshot);
    }

    @Benchmark
    public LoadShedDecision benchmarkControllerDrivenPolicy() {
        return controllerPolicy.decide(request, snapshot);
    }

    @Benchmark
    public LoadShedDecision benchmarkTokenBucketPolicy() {
        return tokenPolicy.decide(request, snapshot);
    }
}
