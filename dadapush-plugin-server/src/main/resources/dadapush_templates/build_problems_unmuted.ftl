<#-- Uses FreeMarker template syntax, template guide can be found at http://freemarker.org/docs/dgui.html -->

<#import "common.ftl" as common>
<#import "mute.ftl" as mute>

<#global title>[problem unmuted]${project.fullName}</#global>
<#global content>Build problem unmuted <@mute.inScope scopeBean/>:
${buildProblems?first}
</#global>
