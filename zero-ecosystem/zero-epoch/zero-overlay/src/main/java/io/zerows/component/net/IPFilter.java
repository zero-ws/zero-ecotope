package io.zerows.component.net;

public interface IPFilter {
    String IPv6KeyWord = ":";

    boolean accept(String ipAddress);
}
