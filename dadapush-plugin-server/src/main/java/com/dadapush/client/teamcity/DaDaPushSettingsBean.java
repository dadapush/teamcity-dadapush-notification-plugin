package com.dadapush.client;

import jetbrains.buildServer.controllers.RememberState;
import jetbrains.buildServer.controllers.StateField;
import jetbrains.buildServer.serverSide.crypt.RSACipher;
import jetbrains.buildServer.util.StringUtil;
import org.jetbrains.annotations.NotNull;

public class DaDaPushSettingsBean extends RememberState {

  @StateField
  private String basePath;
  @StateField
  private String channelToken;
  @StateField
  private boolean paused;

  public DaDaPushSettingsBean(@NotNull DaDaPushSettings settings) {
    this.basePath = settings.getBasePath();
    this.channelToken = settings.getChannelToken();
    this.paused = settings.isPaused();
    rememberState();
  }

  public String getBasePath() {
    return basePath;
  }

  public void setBasePath(String basePath) {
    this.basePath = basePath;
  }

  public String getChannelToken() {
    return channelToken;
  }

  public void setChannelToken(String channelToken) {
    this.channelToken = channelToken;
  }

  public String getHexEncodedPublicKey() {
    return RSACipher.getHexEncodedPublicKey();
  }

  public String getEncryptedChannelToken() {
    return StringUtil.isEmpty(channelToken) ? "" : RSACipher.encryptDataForWeb(channelToken);
  }

  public void setEncryptedChannelToken(String encrypted) {
    this.channelToken = RSACipher.decryptWebRequestData(encrypted);
  }

  public boolean isPaused() {
    return paused;
  }

  public void setPaused(boolean paused) {
    this.paused = paused;
  }

  public DaDaPushSettings toSettings() {
    DaDaPushSettings settings = new DaDaPushSettings();
    settings.setBasePath(basePath);
    settings.setChannelToken(channelToken);
    settings.setPaused(paused);
    return settings;
  }

}
