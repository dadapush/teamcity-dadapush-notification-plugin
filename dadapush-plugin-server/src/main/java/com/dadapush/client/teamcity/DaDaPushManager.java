package com.dadapush.client;

import com.dadapush.client.api.DaDaPushMessageApi;
import com.dadapush.client.model.MessagePushRequest;
import com.dadapush.client.model.ResultOfMessagePushResponse;
import com.intellij.openapi.diagnostic.Logger;
import java.util.Objects;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class DaDaPushManager {

  private static final Logger LOG = Loggers.SERVER;

  private DaDaPushSettings settings;

  private volatile DaDaPushMessageApi apiInstance;

  @Deprecated
  public synchronized void reloadIfNeeded(@NotNull DaDaPushSettings newSettings) {
    if (Objects.equals(newSettings, settings)) {
      LOG.debug("DaDaPush Notification settings has not changed");
      return;
    }
    LOG.debug("New DaDaPush channel token is received: " +
        StringUtil.truncateStringValueWithDotsAtEnd(newSettings.getChannelToken(), 6));
    this.settings = newSettings;
  }

  public void sendMessage(@NotNull String basePath,@NotNull String channelToken,@NotNull String title,
      @NotNull String content) {
    ApiClient apiClient = Configuration.getDefaultApiClient();
    if (StringUtil.isNotEmpty(basePath)) {
      apiClient.setBasePath(basePath);
    }
    apiInstance = new DaDaPushMessageApi(apiClient);
    MessagePushRequest body = new MessagePushRequest();
    body.setTitle(StringUtils.substring(title, 0, 50));
    body.setContent(StringUtils.substring(content, 0, 500));
    body.setNeedPush(true);
    try {
      ResultOfMessagePushResponse result = apiInstance
          .createMessage(body, channelToken);
      if (result.getCode() == 0) {
        LOG.info("send notification success, messageId=" + result.getData().getMessageId());
      } else {
        LOG.warn(
            "send notification fail, detail: " + result.getCode() + " " + result.getErrmsg());
      }
    } catch (ApiException e) {
      LOG.error("send notification fail", e);
    }
  }

}
