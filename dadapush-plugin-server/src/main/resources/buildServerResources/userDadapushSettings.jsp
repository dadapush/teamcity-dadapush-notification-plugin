<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<jsp:useBean id="showDadapushNotConfiguredWarning" type="java.lang.Boolean" scope="request"/>
<jsp:useBean id="showDadapushPausedWarning" type="java.lang.Boolean" scope="request"/>
<c:choose>
  <c:when test="${showDadapushPausedWarning}">
    <forms:attentionComment
        additionalClasses="attentionCommentNotifier">Notification rules will not work because DaDaPush Notification is disabled.</forms:attentionComment>
  </c:when>
  <c:when test="${showDadapushNotConfiguredWarning}">
    <forms:attentionComment
        additionalClasses="attentionCommentNotifier">Notification rules will not work until you set up your DaDaPush channel token.</forms:attentionComment>
  </c:when>
</c:choose>