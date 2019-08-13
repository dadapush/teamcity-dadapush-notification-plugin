package com.dadapush.client.teamcity;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.JDOMUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import jetbrains.buildServer.configuration.ChangeListener;
import jetbrains.buildServer.configuration.ChangeObserver;
import jetbrains.buildServer.configuration.FileWatcher;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.ServerPaths;
import jetbrains.buildServer.serverSide.crypt.EncryptUtil;
import jetbrains.buildServer.util.FileUtil;
import jetbrains.buildServer.util.StringUtil;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;

public class DaDaPushSettingsManager implements ChangeListener {
  private static final Logger LOG = Loggers.SERVER;

  private static final String PAUSE_ATTR = "paused";

  private static final String BASE_PATH_ATTR = "base-path";

  private static final String CHANNEL_TOKEN_ATTR = "channel-token";

  private static final String CONFIG_FILE_NAME = "dadapush-config.xml";

  private final Path configFile;

  private final Path configDir;

  private final ChangeObserver changeObserver;

  private DaDaPushSettings settings;

  private DaDaPushManager daDaPushManager;

  public DaDaPushSettingsManager(@NotNull ServerPaths paths,
      DaDaPushManager daDaPushManager) throws JDOMException, IOException {
    configDir = Paths.get(paths.getConfigDir()).resolve("_notifications").
        resolve("dadapush");
    this.daDaPushManager = daDaPushManager;
    configFile = configDir.resolve(CONFIG_FILE_NAME);

    initResources();
    reloadConfiguration();

    changeObserver = new FileWatcher(configFile.toFile());
    changeObserver.setSleepingPeriod(10000L);
    changeObserver.registerListener(this);
    changeObserver.start();

  }


  @Override
  public void changeOccured(String s) {
    try {
      reloadConfiguration();
    } catch (IOException | JDOMException ex) {
      throw new RuntimeException(ex);
    }
  }

  @NotNull
  public DaDaPushSettings getSettings() {
    return settings;
  }

  @NotNull
  public Path getSettingsDir() {
    return configDir;
  }

  /**
   * Save configuration on disk
   * @param newSettings {@link DaDaPushSettings}
   */
  public synchronized void saveConfiguration(@NotNull DaDaPushSettings newSettings) {
    changeObserver.runActionWithDisabledObserver(() ->
        FileUtil.processXmlFile(configFile.toFile(), (root) -> {
          root.setAttribute(BASE_PATH_ATTR, scramble(newSettings.getBasePath()));
          root.setAttribute(CHANNEL_TOKEN_ATTR, scramble(newSettings.getChannelToken()));
          root.setAttribute(PAUSE_ATTR, Boolean.toString(newSettings.isPaused()));
        }));
    settings = newSettings;
    daDaPushManager.reloadIfNeeded(settings);
  }

  private synchronized void reloadConfiguration() throws JDOMException, IOException {
    LOG.info("Loading configuration file: " + configFile);
    Document document = JDOMUtil.loadDocument(configFile.toFile());

    Element root = document.getRootElement();

    DaDaPushSettings newSettings = new DaDaPushSettings();
    newSettings.setBasePath(unscramble(root.getAttributeValue(BASE_PATH_ATTR)));
    newSettings.setChannelToken(unscramble(root.getAttributeValue(CHANNEL_TOKEN_ATTR)));
    newSettings.setPaused(Boolean.parseBoolean(root.getAttributeValue(PAUSE_ATTR)));
    settings = newSettings;
    daDaPushManager.reloadIfNeeded(settings);
  }

  private void initResources() {
    try {
      Files.createDirectories(configDir);
      copyResourceIfNotExists(configDir, CONFIG_FILE_NAME);
      copyResourceIfNotExists(configDir, "dadapush-config.dtd");
      copyResourceIfNotExists(configDir, "build_failed.ftl");
      copyResourceIfNotExists(configDir, "build_failed_to_start.ftl");
      copyResourceIfNotExists(configDir, "build_failing.ftl");
      copyResourceIfNotExists(configDir, "build_probably_hanging.ftl");
      copyResourceIfNotExists(configDir, "build_problem_responsibility_assigned_to_me.ftl");
      copyResourceIfNotExists(configDir, "build_problem_responsibility_changed.ftl");
      copyResourceIfNotExists(configDir, "build_problems_muted.ftl");
      copyResourceIfNotExists(configDir, "build_problems_unmuted.ftl");
      copyResourceIfNotExists(configDir, "build_started.ftl");
      copyResourceIfNotExists(configDir, "build_successful.ftl");
      copyResourceIfNotExists(configDir, "build_type_responsibility_assigned_to_me.ftl");
      copyResourceIfNotExists(configDir, "build_type_responsibility_changed.ftl");
      copyResourceIfNotExists(configDir, "common.ftl");
      copyResourceIfNotExists(configDir, "labeling_failed.ftl");
      copyResourceIfNotExists(configDir, "multiple_test_responsibility_assigned_to_me.ftl");
      copyResourceIfNotExists(configDir, "multiple_test_responsibility_changed.ftl");
      copyResourceIfNotExists(configDir, "mute.ftl");
      copyResourceIfNotExists(configDir, "responsibility.ftl");
    } catch (IOException ex) {
      LOG.error("Failed to create dadapush plugin config directory", ex);
    }
  }

  private void copyResourceIfNotExists(@NotNull Path configDir, @NotNull String name) {
    FileUtil.copyResourceIfNotExists(this.getClass(),
        "/dadapush_templates/" + name, configDir.resolve(name).toFile());
  }

  private String scramble(String str) {
    return StringUtil.isEmpty(str) ? str : EncryptUtil.scramble(str);
  }

  private String unscramble(String str) {
    return StringUtil.isEmpty(str) ? str : EncryptUtil.unscramble(str);
  }

}
