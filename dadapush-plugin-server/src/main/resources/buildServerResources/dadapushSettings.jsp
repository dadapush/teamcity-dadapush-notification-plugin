<%@ include file="/include.jsp" %>
<%@ taglib prefix="admin" tagdir="/WEB-INF/tags/admin" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>

<jsp:useBean id="dadapushSettings"
             scope="request"
             type="com.dadapush.client.teamcity.DaDaPushSettingsBean"/>
<bs:linkCSS dynamic="${true}">
    /css/admin/adminMain.css
    /css/admin/serverConfig.css
    /plugins/dadapush-plugin/css/dadapushSettings.css
</bs:linkCSS>
<bs:linkScript>
    /js/bs/testConnection.js
    /plugins/dadapush-plugin/js/dadapushSettings.js
</bs:linkScript>
<script type="text/javascript">
    $j(function() {
        Dadapush.SettingsForm.setupEventHandlers();
    });
</script>

<c:url var="url" value="/dadapush/notifierSettings.html"/>
<div id="settingsContainer">
    <form action="${url}" method="post" onsubmit="return Dadapush.SettingsForm.submitSettings()" autocomplete="off">
        <div class="editNotificatorSettingsPage">
            <c:choose>
                <c:when test="${dadapushSettings.paused}">
                    <div class="headerNote">
                        <span class="icon icon16 build-status-icon build-status-icon_paused"></span>
                        The notifier is <strong>disabled</strong>. All DaDaPush notifications are suspended&nbsp;&nbsp;
                        <a class="btn btn_mini" href="#" id="enable-btn">Enable</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="enableNote">
                        The notifier is <strong>enabled</strong>&nbsp;&nbsp;
                        <a class="btn btn_mini" href="#" id="disable-btn">Disable</a>
                    </div>
                </c:otherwise>
            </c:choose>

            <bs:messages key="settingsSaved"/>
            <table class="runnerFormTable">
                <tr>
                    <th><label for="basePath">Base Path: </label></th>
                    <td>
                        <forms:textField name="basePath" value="${dadapushSettings.basePath}"/>
                        <span class="smallNote">Optional. default value: https://www.dadapush.com</span>
                        <span class="error" id="errorBasePath"></span>
                    </td>
                </tr>
                <tr>
                    <th><label for="channelToken">Channel token: <l:star/></label></th>
                    <td>
                        <forms:passwordField name="channelToken"
                                             encryptedPassword="${dadapushSettings.encryptedChannelToken}"/>
                        <span class="error" id="errorChannelToken"></span>
                    </td>
                </tr>
                <tr class="noBorder">
                    <td colspan="2">
                        The templates for DaDaPush notifications
                        <a target="_blank"
                           href="<bs:helpUrlPrefix/>/Customizing+Notifications"
                           showdiscardchangesmessage="false">
                            can be customized
                        </a>.
                    </td>
                </tr>
            </table>

            <div class="saveButtonsBlock">
                <forms:submit type="submit" label="Save"/>
                <input type="hidden" id="submitSettings" name="submitSettings" value="store"/>
                <input type="hidden" id="publicKey" name="publicKey"
                       value="<c:out value='${dadapushSettings.hexEncodedPublicKey}'/>"/>
                <forms:saving/>
            </div>
        </div>
    </form>
</div>
<forms:modified/>

<script type="text/javascript">
    (function($) {
        var sendAction = function(enable) {
            $.post("${url}?action=" + (enable ? 'enable' : 'disable'), function() {
                BS.reload(true);
            });
            // looks like Teamcity should support very old browsers so don't use
            // event.preventDefault() but return boolean from click event...
            return false;
        };

        $("#enable-btn").click(function() {
            return sendAction(true);
        });

        $("#disable-btn").click(function() {
            if (!confirm("DaDaPush notifications will not be sent until enabled. Disable the notifier?"))
                return false;
            return sendAction(false);
        });
    })(jQuery);
</script>
