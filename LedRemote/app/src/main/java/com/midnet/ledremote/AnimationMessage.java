package com.midnet.ledremote;

import android.support.annotation.NonNull;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AnimationMessage {
  private float durationSeconds;
  private AnimationDialog.AnimationType animationType;
  private Boolean randomColors;

  public AnimationMessage(float durationSeconds, AnimationDialog.AnimationType animationType, Boolean randomColors) {
    this.randomColors = randomColors;
    this.animationType = animationType;
    this.durationSeconds = durationSeconds;
  }

  @NonNull
  SendDataToDeviceTaskArgs getSendDataToDeviceTaskArgs() {
    short durationInMillis = (short) (durationSeconds * 1000);
    byte animationCode = animationTypeToCode(animationType);
    Log.d(MainActivity.TAG, "Sending to led device animation");
    byte[] data = createAnimationMessage(randomColors, durationInMillis, animationCode);
    byte[] dataToSend = PacketData.encodePacketData(data);
    String message = animationMessage(durationSeconds, animationType, durationInMillis, animationCode);
    return new SendDataToDeviceTaskArgs(dataToSend, message);
  }

  String animationMessage(float durationSeconds, AnimationDialog.AnimationType animationType, short durationInMillis, byte animationCode) {
    if(animationCode == 0 || durationInMillis == 0) {
      return "Stops animation!";
    } else {
      String animationName = animationType.name().toLowerCase();
      animationName = animationName.substring(0, 1).toUpperCase() + animationName.substring(1);
      return String.format("%s animation of %.2f sec set!", animationName, durationSeconds);
    }
  }

  byte[] createAnimationMessage(Boolean randomColors, short durationInMillis, byte animationCode) {
    byte[] serializedDuration = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(durationInMillis).array();
    byte[] data = new byte[7];
    data[0] = PacketData.START_MARKER;
    data[1] = PacketData.ANIMATION_COMMAND;
    data[2] = animationCode;
    System.arraycopy(serializedDuration, 0, data, 3, 2);
    data[5] = (byte) (randomColors ? 0x01 : 0x00);
    data[6] = PacketData.END_MARKER;
    return data;
  }

  byte animationTypeToCode(AnimationDialog.AnimationType animationType) {
    byte animationCode;
    switch (animationType) {
      case FADE:
        animationCode = PacketData.FADE_ANIMATION_CODE;
        break;
      case BLINK:
        animationCode = PacketData.BLINK_ANIMATION_CODE;
        break;
      default:
        animationCode = 0; // Stops animation
        break;
    }
    return animationCode;
  }
}