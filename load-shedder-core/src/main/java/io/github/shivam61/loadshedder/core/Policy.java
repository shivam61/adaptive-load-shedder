package io.github.shivam61.loadshedder.core;
public interface Policy { LoadShedDecision decide(RequestContext request, SystemSnapshot snapshot); }
