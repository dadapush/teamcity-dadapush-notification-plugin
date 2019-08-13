package com.dadapush.client.teamcity;

import static com.dadapush.client.teamcity.DaDaPushNotificator.CHANNEL_TOKEN_PROP;
import static com.dadapush.client.teamcity.DaDaPushNotificator.NOTIFICATOR_TYPE;

import com.intellij.openapi.util.text.StringUtil;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import jetbrains.buildServer.notification.NotificationRulesManager;
import jetbrains.buildServer.users.NotificatorPropertyKey;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.users.UserModel;
import jetbrains.buildServer.users.UserNotFoundException;
import jetbrains.buildServer.web.openapi.PlaceId;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.SimplePageExtension;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import jetbrains.buildServer.web.util.SessionUser;
import jetbrains.buildServer.web.util.WebUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Extension of user's main page for "DaDaPush Notification" part
 */
public class UserDaDaPushSettingsExtension extends SimplePageExtension {

  private final NotificationRulesManager rulesManager;
  private final UserModel userModel;
  private final DaDaPushSettingsManager dadapushSettingsManager;

  public UserDaDaPushSettingsExtension(@NotNull WebControllerManager manager,
                                       @NotNull NotificationRulesManager rulesManager,
                                       @NotNull UserModel userModel,
                                       @NotNull PluginDescriptor descriptor,
                                       @NotNull DaDaPushSettingsManager dadapushSettingsManager) {
    super(manager);
    this.rulesManager = rulesManager;
    this.userModel = userModel;
    this.dadapushSettingsManager = dadapushSettingsManager;

    setPluginName(NOTIFICATOR_TYPE);
    setIncludeUrl(descriptor.getPluginResourcesPath("userDadapushSettings.jsp"));
    setPlaceId(PlaceId.NOTIFIER_SETTINGS_FRAGMENT);
    register();
    setPlaceId(PlaceId.MY_SETTINGS_NOTIFIER_SECTION);
    register();
  }

  public boolean isAvailable(@NotNull HttpServletRequest request) {
    return "/profile.html".equals(WebUtil.getPathWithoutContext(request)) ||
        getPluginName().equals(request.getParameter("notificatorType"));
  }

  public void fillModel(@NotNull Map<String, Object> model, @NotNull HttpServletRequest request) {
    SUser user = SessionUser.getUser(request);
    String userIdStr = request.getParameter("userId");
    if (userIdStr != null) {
      long userId = Long.parseLong(userIdStr);
      user = userModel.findUserById(userId);
      if (user == null) {
        throw new UserNotFoundException(userId, "User with id " + userIdStr + " does not exist");
      }
    }


    boolean telegramNotConfigured = true;
    if (rulesManager.isRulesWithEventsConfigured(user.getId(), this.getPluginName())) {
      String channelToken = user.getPropertyValue(new NotificatorPropertyKey(NOTIFICATOR_TYPE,
          CHANNEL_TOKEN_PROP));
      telegramNotConfigured = StringUtil.isEmpty(channelToken);
    }

    model.put("showDadapushNotConfiguredWarning", telegramNotConfigured);
    model.put("showDadapushPausedWarning", dadapushSettingsManager.getSettings().isPaused());
  }
}
