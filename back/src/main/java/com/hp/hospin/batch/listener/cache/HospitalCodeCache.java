package com.hp.hospin.batch.listener.cache;

import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class HospitalCodeCache {
    private Set<String> codes;
    public void initialize(Set<String> codes) {
        this.codes = codes;
    }
    public boolean contains(String code) {
        return codes != null && codes.contains(code);
    }
    public void clear() {
        this.codes = null;
    }
}
