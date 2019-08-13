<#-- Uses FreeMarker template syntax, template guide can be found at http://freemarker.org/docs/dgui.html -->

<#import "common.ftl" as common>

<#global title>[failed to start]${project.fullName}</#global>
<#global content>Build failed to start.
${project.fullName}::${buildType.name} <@common.short_build_info build/>, agent ${agentName} ${var.buildShortStatusDescription}
${link.buildResultsLink}</#global>
