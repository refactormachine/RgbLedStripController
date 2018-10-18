package com.midnet.ledremote;

public class PacketData {
  private static final byte SPECIAL_BYTE = (byte) 0xFD;
  static final byte START_MARKER = (byte) 0xFE;
  static final byte END_MARKER = (byte) 0xFF;
  protected static final byte ANIMATION_COMMAND = (byte) 0x04;
  static final byte BLINK_ANIMATION_CODE = (byte) 0x02;
  static final byte FADE_ANIMATION_CODE = (byte) 0x01;

  private static int unsignedToBytes(byte b) {
      return b & 0xFF;
  }

  static byte[] encodePacketData(byte[] data) {
      int length = 2; // for start and end markers
      for (int i = 1; i < data.length - 1; i++) { // don't count start and end markers
          length++;
          if (unsignedToBytes(data[i]) >= unsignedToBytes(SPECIAL_BYTE)) {
              length++;
          }
      }
      byte[] encodedData = new byte[length];
      encodedData[0] = data[0]; // copy start marker as is
      int j = 1; // skip start marker
      for (int i = 1; i < data.length - 1; i++) { // don't take start and end markers
          if (unsignedToBytes(data[i]) >= unsignedToBytes(SPECIAL_BYTE)) {
              //encode special characters
              encodedData[j] = SPECIAL_BYTE;
              j++;
              encodedData[j] = (byte) (unsignedToBytes(data[i]) - unsignedToBytes(SPECIAL_BYTE));
          } else {
              encodedData[j] = data[i];
          }
          j++;
      }
      encodedData[j] = data[data.length - 1]; // copy end marker as is
      return encodedData;
  }
}
