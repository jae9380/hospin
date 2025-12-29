package com.hp.hospin.hospital.presentation.port;

import reactor.core.publisher.Flux;

public interface SymptomCheckService {
    String generate(String text);
}
