package com.midnet.ledremote;

public class SendDataToDeviceTaskArgs {
  public String onSentMessage;
  byte[] dataToSend;

  SendDataToDeviceTaskArgs(byte[] dataToSend) {
    this(dataToSend, null);
  }

  SendDataToDeviceTaskArgs(byte[] dataToSend, String onSentMessage) {
    this.dataToSend = dataToSend;
    this.onSentMessage = onSentMessage;
  }
}