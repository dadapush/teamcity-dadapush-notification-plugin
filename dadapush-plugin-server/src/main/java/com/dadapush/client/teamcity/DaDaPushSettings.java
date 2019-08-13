package com.dadapush.client;

public class DaDaPushSettings {

  private String basePath;
  private String channelToken;
  private boolean paused;

  public DaDaPushSettings(DaDaPushSettings oldSettings) {
    this.basePath=oldSettings.basePath;
    this.channelToken=oldSettings.channelToken;
    this.paused=oldSettings.paused;
  }

  public DaDaPushSettings() {
  }

  public String getChannelToken() {
    return channelToken;
  }

  public void setChannelToken(String channelToken) {
    this.channelToken = channelToken;
  }

  public boolean isPaused() {
    return paused;
  }

  public void setPaused(boolean paused) {
    this.paused = paused;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    DaDaPushSettings that = (DaDaPushSettings) o;

    if (paused != that.paused) {
      return false;
    }
    return channelToken.equals(that.channelToken);
  }

  @Override
  public int hashCode() {
    int result = channelToken.hashCode();
    result = 31 * result + (paused ? 1 : 0);
    return result;
  }

  public String getBasePath() {
    return basePath;
  }

  public void setBasePath(String basePath) {
    this.basePath = basePath;
  }
}
