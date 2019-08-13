package com.dadapush.client;

import com.intellij.openapi.diagnostic.Logger;
import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jetbrains.buildServer.Build;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.notification.NotificatorAdapter;
import jetbrains.buildServer.notification.NotificatorRegistry;
import jetbrains.buildServer.notification.TemplateMessageBuilder;
import jetbrains.buildServer.responsibility.ResponsibilityEntry;
import jetbrains.buildServer.responsibility.TestNameResponsibilityEntry;
import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.serverSide.STest;
import jetbrains.buildServer.serverSide.TeamCityProperties;
import jetbrains.buildServer.serverSide.UserForm;
import jetbrains.buildServer.serverSide.UserPropertyInfo;
import jetbrains.buildServer.serverSide.UserPropertyValidator;
import jetbrains.buildServer.serverSide.mute.MuteInfo;
import jetbrains.buildServer.serverSide.problems.BuildProblemInfo;
import jetbrains.buildServer.tests.TestName;
import jetbrains.buildServer.users.NotificatorPropertyKey;
import jetbrains.buildServer.users.PropertyKey;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.util.StringUtil;
import jetbrains.buildServer.vcs.VcsRoot;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DaDaPushNotificator extends NotificatorAdapter {

  private static final Logger LOG = Loggers.SERVER;

  static final String CHANNEL_TOKEN_PROP = "channel-token";

  private static final String BASE_PATH_PROP = "base-path";

  static final String NOTIFICATOR_TYPE = "dadapush";

  // UserPropertyValidator not work?
  @SuppressWarnings("SpellCheckingInspection")
  private static List<UserPropertyInfo> USER_PROPERTIES = Arrays
      .asList(new UserPropertyInfo(BASE_PATH_PROP, "Base Path"
//              ,
//              "https://www.dadapush.com",
//              (UserPropertyValidator) (propertyValue, editee, currentUserData) -> {
//                LOG.info("base-path="+propertyValue);
//                return null;
//              }
              ),
          new UserPropertyInfo(CHANNEL_TOKEN_PROP, "Channel Token"
//              , "ctXXXXX",
//              (UserPropertyValidator) (propertyValue, editee, currentUserData) -> {
//                LOG.info("channel-token="+propertyValue);
//                String trimToNull = StringUtils.trimToNull(propertyValue);
//                if(StringUtils.isNotEmpty(trimToNull)){
//                  return null;
//                }else{
//                  return "Channel Token is not set";
//                }
//              }
              ));


  private final TemplateMessageBuilder messageBuilder;

  private final Configuration freeMarkerConfig;

  private final DaDaPushManager dadapushManager;

  public DaDaPushNotificator(@NotNull NotificatorRegistry registry,
      @NotNull TemplateMessageBuilder messageBuilder,
      @NotNull DaDaPushSettingsManager settingsManager,
      @NotNull DaDaPushManager dadapushManager

  ) throws IOException {
    this.dadapushManager = dadapushManager;
    this.messageBuilder = messageBuilder;
    freeMarkerConfig = createFreeMarkerConfig(settingsManager.getSettingsDir());
    registry.register(this, USER_PROPERTIES);
  }

  @NotNull
  @Override
  public String getNotificatorType() {
    return NOTIFICATOR_TYPE;
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return "DaDaPush Notification";
  }

  @Override
  public void notifyBuildStarted(@NotNull SRunningBuild build, @NotNull Set<SUser> users) {
    Map<String, Object> props = messageBuilder.getBuildStartedMap(build, users);
    sendNotification(props, users, "build_started");
  }

  @Override
  public void notifyBuildSuccessful(@NotNull SRunningBuild build, @NotNull Set<SUser> users) {
    Map<String, Object> props = messageBuilder.getBuildSuccessfulMap(build, users);
    sendNotification(props, users, "build_successful");
  }

  @Override
  public void notifyBuildFailed(@NotNull SRunningBuild build, @NotNull Set<SUser> users) {
    Map<String, Object> props = messageBuilder.getBuildFailedMap(build, users);
    sendNotification(props, users, "build_failed");
  }

  @Override
  public void notifyBuildFailedToStart(@NotNull SRunningBuild build, @NotNull Set<SUser> users) {
    Map<String, Object> props = messageBuilder.getBuildFailedToStartMap(build, users);
    sendNotification(props, users, "build_failed_to_start");
  }

  @Override
  public void notifyLabelingFailed(@NotNull Build build, @NotNull VcsRoot root,
      @NotNull Throwable exception, @NotNull Set<SUser> users) {
    Map<String, Object> props = messageBuilder.
        getLabelingFailedMap((SBuild) build, root, exception, users);
    sendNotification(props, users, "labeling_failed");
  }

  @Override
  public void notifyBuildFailing(@NotNull SRunningBuild build, @NotNull Set<SUser> users) {
    Map<String, Object> props = messageBuilder.getBuildFailedMap(build, users);
    sendNotification(props, users, "build_failed");
  }

  @Override
  public void notifyBuildProbablyHanging(@NotNull SRunningBuild build, @NotNull Set<SUser> users) {
    Map<String, Object> props = messageBuilder.getBuildProbablyHangingMap(build, users);
    sendNotification(props, users, "build_probably_hanging");
  }

  @Override
  public void notifyResponsibleChanged(@NotNull SBuildType buildType, @NotNull Set<SUser> users) {
    Map<String, Object> props = messageBuilder.
        getBuildTypeResponsibilityChangedMap(buildType, users);
    sendNotification(props, users, "build_type_responsibility_changed");
  }

  @Override
  public void notifyResponsibleAssigned(@NotNull SBuildType buildType, @NotNull Set<SUser> users) {
    Map<String, Object> props = messageBuilder.
        getBuildTypeResponsibilityAssignedMap(buildType, users);
    sendNotification(props, users, "build_type_responsibility_assigned_to_me");
  }

  @Override
  public void notifyResponsibleChanged(@Nullable TestNameResponsibilityEntry oldValue,
      @NotNull TestNameResponsibilityEntry newValue,
      @NotNull SProject project,
      @NotNull Set<SUser> users) {
    Map<String, Object> props = messageBuilder.
        getTestResponsibilityChangedMap(newValue, oldValue, project, users);
    sendNotification(props, users, "test_responsibility_changed");
  }

  @Override
  public void notifyResponsibleAssigned(@Nullable TestNameResponsibilityEntry oldValue,
      @NotNull TestNameResponsibilityEntry newValue,
      @NotNull SProject project,
      @NotNull Set<SUser> users) {
    Map<String, Object> root = messageBuilder.
        getTestResponsibilityAssignedMap(newValue, oldValue, project, users);
    this.sendNotification(root, users, "test_responsibility_assigned_to_me");
  }

  @Override
  public void notifyResponsibleChanged(@NotNull Collection<TestName> testNames,
      @NotNull ResponsibilityEntry entry,
      @NotNull SProject project,
      @NotNull Set<SUser> users) {
    Map<String, Object> root = messageBuilder.
        getTestResponsibilityAssignedMap(testNames, entry, project, users);
    this.sendNotification(root, users, "multiple_test_responsibility_changed");
  }

  @Override
  public void notifyResponsibleAssigned(@NotNull Collection<TestName> testNames,
      @NotNull ResponsibilityEntry entry,
      @NotNull SProject project,
      @NotNull Set<SUser> users) {
    Map<String, Object> root = messageBuilder.
        getTestResponsibilityChangedMap(testNames, entry, project, users);
    this.sendNotification(root, users, "multiple_test_responsibility_assigned_to_me");
  }

  @Override
  public void notifyBuildProblemResponsibleAssigned(
      @NotNull Collection<BuildProblemInfo> buildProblems,
      @NotNull ResponsibilityEntry entry,
      @NotNull SProject project,
      @NotNull Set<SUser> users) {
    Map<String, Object> root = messageBuilder.
        getBuildProblemsResponsibilityAssignedMap(buildProblems, entry, project, users);
    this.sendNotification(root, users, "build_problem_responsibility_assigned_to_me");
  }

  @Override
  public void notifyBuildProblemResponsibleChanged(
      @NotNull Collection<BuildProblemInfo> buildProblems,
      @NotNull ResponsibilityEntry entry,
      @NotNull SProject project,
      @NotNull Set<SUser> users) {
    Map<String, Object> root = messageBuilder.
        getBuildProblemsResponsibilityAssignedMap(buildProblems, entry, project, users);
    this.sendNotification(root, users, "build_problem_responsibility_changed");
  }

  @Override
  public void notifyTestsMuted(@NotNull Collection<STest> tests,
      @NotNull MuteInfo muteInfo,
      @NotNull Set<SUser> users) {
    Map<String, Object> root = messageBuilder.
        getTestsMutedMap(tests, muteInfo, users);
    this.sendNotification(root, users, "tests_muted");
  }

  @Override
  public void notifyTestsUnmuted(@NotNull Collection<STest> tests,
      @NotNull MuteInfo muteInfo,
      @Nullable SUser user,
      @NotNull Set<SUser> users) {
    Map<String, Object> root = messageBuilder.
        getTestsUnmutedMap(tests, muteInfo, user, users);
    this.sendNotification(root, users, "tests_unmuted");
  }

  @Override
  public void notifyBuildProblemsMuted(@NotNull Collection<BuildProblemInfo> buildProblems,
      @NotNull MuteInfo muteInfo,
      @NotNull Set<SUser> users) {
    Map<String, Object> root = messageBuilder.
        getBuildProblemsMutedMap(buildProblems, muteInfo, users);
    this.sendNotification(root, users, "build_problems_muted");
  }

  @Override
  public void notifyBuildProblemsUnmuted(@NotNull Collection<BuildProblemInfo> buildProblems,
      @NotNull MuteInfo muteInfo,
      @Nullable SUser user,
      @NotNull Set<SUser> users) {
    Map<String, Object> root = messageBuilder.
        getBuildProblemsMutedMap(buildProblems, muteInfo, users);
    this.sendNotification(root, users, "build_problems_unmuted");
  }

  /**
   * Send notifications to telegram users
   *
   * @param props template parameters
   * @param users users to send messages
   * @param templateName template name
   */
  private void sendNotification(@NotNull Map<String, Object> props,
      @NotNull Set<SUser> users,
      @NotNull String templateName) {
    String title = null;
    String content = null;
    try (StringWriter writer = new StringWriter()) {
      Template template = freeMarkerConfig.getTemplate(templateName + ".ftl");
      Environment env = template.createProcessingEnvironment(props, writer, null);
      env.process();
      if (!env.getKnownVariableNames().contains("title")) {
        LOG.warn("Can't extract title from template. Message will not be sended");
        return;
      }
      if (!env.getKnownVariableNames().contains("content")) {
        LOG.warn("Can't extract content from template. Message will not be sended");
        return;
      }
      title = env.getVariable("title").toString();
      content = env.getVariable("content").toString();
    } catch (IOException | TemplateException ex) {
      LOG.error("Can't execute template '" + templateName + ".ftl': ", ex);
      return;
    }

    final PropertyKey DADAPUSH_CHANNEL_TOKEN_PROP_KEY = new NotificatorPropertyKey(NOTIFICATOR_TYPE,
        CHANNEL_TOKEN_PROP);
    final PropertyKey DADAPUSH_BASE_PATH_PROP_KEY = new NotificatorPropertyKey(NOTIFICATOR_TYPE,
        BASE_PATH_PROP);

    for (SUser user : users) {
      String channelToken = user.getPropertyValue(DADAPUSH_CHANNEL_TOKEN_PROP_KEY);
      String basePath = user.getPropertyValue(DADAPUSH_BASE_PATH_PROP_KEY);
      if (StringUtil.isNotEmpty(channelToken)) {
        dadapushManager.sendMessage(basePath, channelToken, title, content);
      }
    }
  }

  private Configuration createFreeMarkerConfig(@NotNull Path configDir) throws IOException {
    Configuration cfg = new Configuration();
    cfg.setDefaultEncoding("UTF-8");
    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    cfg.setDirectoryForTemplateLoading(configDir.toFile());
    cfg.setTemplateUpdateDelay(TeamCityProperties.getInteger(
        "teamcity.notification.template.update.interval", 60));
    return cfg;
  }

}
