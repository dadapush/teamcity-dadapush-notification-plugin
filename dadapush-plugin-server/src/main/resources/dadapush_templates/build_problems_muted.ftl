<#-- Uses FreeMarker template syntax, template guide can be found at http://freemarker.org/docs/dgui.html -->

<#import "common.ftl" as common>
<#import "mute.ftl" as mute>

<#global title>[problem muted]${project.fullName}</#global>
<#global content>Build problem muted <@mute.inScope scopeBean/> by ${muteInfo.mutingUser.descriptiveName}:
${buildProblems?first}

<@mute.comment muteInfo/>
<@mute.unmute unmuteModeBean/>
${link.mutedProblemsLink}
</#global>
