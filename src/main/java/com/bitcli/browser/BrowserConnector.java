package com.bitcli.browser;

public interface BrowserConnector {
    String status();

    String connectDefault();

    String disconnect();
}
