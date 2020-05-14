package org.atricore.idbus.kernel.main.sms;

public interface SMSSender {

    public void init();

    public String getName();

    public void sendMessage(String sender, String phoneNumber, String message);
}
