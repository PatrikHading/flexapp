package com.example.flexapp.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class ClientIpResolver {

    private static final String X_FORWARDED_FOR = "X-Forwarded-For";

    private final List<IpAddressMatcher> trustedProxyMatchers;

    public ClientIpResolver(@Value("${security.trusted-proxies:}") String trustedProxies) {
        this.trustedProxyMatchers = parseTrustedProxies(trustedProxies);
    }

    public String resolveClientIp(HttpServletRequest request) {
        String remoteAddr = normalize(request.getRemoteAddr());

        if (!isFromTrustedProxy(remoteAddr)) {
            return remoteAddr;
        }

        String xForwardedFor = request.getHeader(X_FORWARDED_FOR);
        if (xForwardedFor == null || xForwardedFor.isBlank()) {
            return remoteAddr;
        }

        List<String> forwardedChain = parseForwardedFor(xForwardedFor);

        for (int i = forwardedChain.size() - 1; i >= 0; i--) {
            String candidate = forwardedChain.get(i);
            if (!isTrustedProxy(candidate)) {
                return candidate;
            }
        }

        return forwardedChain.isEmpty() ? remoteAddr : forwardedChain.get(0);
    }

    private boolean isFromTrustedProxy(String remoteAddr) {
        return isTrustedProxy(remoteAddr);
    }

    private boolean isTrustedProxy(String ip) {
        if (ip == null || ip.isBlank()) {
            return false;
        }

        for (IpAddressMatcher matcher : trustedProxyMatchers) {
            if (matcher.matches(ip)) {
                return true;
            }
        }

        return false;
    }

    private List<String> parseForwardedFor(String xForwardedFor) {
        List<String> addresses = new ArrayList<>();

        String[] parts = xForwardedFor.split(",");
        for (String part : parts) {
            String candidate = normalize(part);
            if (!candidate.isBlank() && isPlainIp(candidate)) {
                addresses.add(candidate);
            }
        }

        return addresses;
    }

    private List<IpAddressMatcher> parseTrustedProxies(String trustedProxies) {
        List<IpAddressMatcher> matchers = new ArrayList<>();

        if (trustedProxies == null || trustedProxies.isBlank()) {
            return matchers;
        }

        String[] parts = trustedProxies.split(",");
        for (String part : parts) {
            String candidate = normalize(part);
            if (!candidate.isBlank()) {
                matchers.add(new IpAddressMatcher(candidate));
            }
        }

        return matchers;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isPlainIp(String value) {
        return !value.contains(" ");
    }
}