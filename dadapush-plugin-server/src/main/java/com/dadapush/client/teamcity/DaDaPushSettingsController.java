//package com.dadapush.client;
//
//import com.intellij.openapi.diagnostic.Logger;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import jetbrains.buildServer.controllers.ActionErrors;
//import jetbrains.buildServer.controllers.BaseFormXmlController;
//import jetbrains.buildServer.controllers.FormUtil;
//import jetbrains.buildServer.controllers.PublicKeyUtil;
//import jetbrains.buildServer.controllers.XmlResponseUtil;
//import jetbrains.buildServer.log.Loggers;
//import jetbrains.buildServer.util.StringUtil;
//import jetbrains.buildServer.web.openapi.WebControllerManager;
//import org.jdom.Element;
//import org.jetbrains.annotations.NotNull;
//import org.springframework.web.servlet.ModelAndView;
//
//
//public class DaDaPushSettingsController extends BaseFormXmlController {
//
//  private static final Logger LOG = Loggers.SERVER;
//
//  private static final String REQUEST_TYPE_PROP_NAME = "submitSettings";
//
//  private final DaDaPushSettingsManager dadapushSettingsManager;
//
//  public DaDaPushSettingsController(@NotNull WebControllerManager webManager,
//      @NotNull DaDaPushSettingsManager dadapushSettingsManager) {
//    webManager.registerController("/dadapush/notifierSettings.html", this);
//    this.dadapushSettingsManager = dadapushSettingsManager;
//  }
//
//  @Override
//  protected ModelAndView doGet(@NotNull HttpServletRequest request,
//                               @NotNull HttpServletResponse response) {
//    return null;
//  }
//
//  @Override
//  protected void doPost(@NotNull HttpServletRequest request,
//                        @NotNull HttpServletResponse response,
//                        @NotNull Element xmlResponse) {
//
//    String action = request.getParameter("action");
//    if (action != null) {
//      boolean pause = "disable".equals(action);
//      changePauseState(pause);
//      return;
//    }
//
//    if (PublicKeyUtil.isPublicKeyExpired(request)) {
//      PublicKeyUtil.writePublicKeyExpiredError(xmlResponse);
//      return;
//    }
//    DaDaPushSettingsBean bean = new DaDaPushSettingsBean(dadapushSettingsManager.getSettings());
//    FormUtil.bindFromRequest(request, bean);
//    if (isStoreInSessionRequest(request)) {
//      XmlResponseUtil.writeFormModifiedIfNeeded(xmlResponse, bean);
//    } else {
//      ActionErrors errors = validate(bean);
//      if (errors.hasNoErrors()) {
//        dadapushSettingsManager.saveConfiguration(bean.toSettings());
//        FormUtil.removeAllFromSession(request.getSession(), bean.getClass());
//        writeRedirect(xmlResponse, request.getContextPath() +
//            "/admin/admin.html?item=" + DaDaPushSettingsPage.PLUGIN_NAME);
//      }
//      writeErrors(xmlResponse, errors);
//    }
//  }
//
//  private void changePauseState(boolean pause) {
//    DaDaPushSettings oldSettings = dadapushSettingsManager.getSettings();
//    DaDaPushSettings newSettings = new DaDaPushSettings(oldSettings);
//    newSettings.setPaused(pause);
//    dadapushSettingsManager.saveConfiguration(newSettings);
//  }
//
//  private ActionErrors validate(@NotNull DaDaPushSettingsBean settings) {
//    ActionErrors errors = new ActionErrors();
//    if (StringUtil.isEmptyOrSpaces(settings.getChannelToken())) {
//      errors.addError("emptyChannelToken", "channel token must not be empty");
//    }
//    return errors;
//  }
//
//  private boolean isStoreInSessionRequest(HttpServletRequest request) {
//    return "storeInSession".equals(request.getParameter(REQUEST_TYPE_PROP_NAME));
//  }
//
//}
