//package com.dadapush.client;
//
//import java.util.Map;
//import javax.servlet.http.HttpServletRequest;
//import jetbrains.buildServer.controllers.admin.AdminPage;
//import jetbrains.buildServer.serverSide.auth.Permission;
//import jetbrains.buildServer.web.openapi.PagePlaces;
//import jetbrains.buildServer.web.openapi.PluginDescriptor;
//import jetbrains.buildServer.web.openapi.PositionConstraint;
//import org.jetbrains.annotations.NotNull;
//
//
//public class DaDaPushSettingsPage extends AdminPage {
//
//  public static final String SETTINGS_NAME = "dadapushSettings";
//  public static final String PLUGIN_NAME = "dadapush";
//
//
//  private final DaDaPushSettingsManager dadapushSettingsManager;
//
//  public DaDaPushSettingsPage(@NotNull PagePlaces places,
//                              @NotNull PluginDescriptor descriptor,
//                              @NotNull DaDaPushSettingsManager dadapushSettingsManager) {
//    super(places);
//    setPluginName(PLUGIN_NAME);
//    setTabTitle("DaDaPush Notification");
//    setIncludeUrl(descriptor.getPluginResourcesPath("dadapushSettings.jsp"));
//    setPosition(PositionConstraint.after("email", "jabber"));
//    register();
//    this.dadapushSettingsManager = dadapushSettingsManager;
//  }
//
//  @NotNull
//  @Override
//  public String getGroup() {
//    return SERVER_RELATED_GROUP;
//  }
//
//  @Override
//  public boolean isAvailable(@NotNull HttpServletRequest request) {
//    return super.isAvailable(request) &&
//        checkHasGlobalPermission(request, Permission.CHANGE_SERVER_SETTINGS);
//  }
//
//  @Override
//  public void fillModel(@NotNull Map<String, Object> model, @NotNull HttpServletRequest request) {
//    super.fillModel(model, request);
//    model.put(SETTINGS_NAME, new DaDaPushSettingsBean(dadapushSettingsManager.getSettings()));
//  }
//}
