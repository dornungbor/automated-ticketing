package com.callsign.ticketing.domain.commands;

public interface ResourceReferenceCommand<T, R> {

    R map(T param);
}
