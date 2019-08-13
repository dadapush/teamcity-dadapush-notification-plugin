<#-- Uses FreeMarker template syntax, template guide can be found at http://freemarker.org/docs/dgui.html -->

<#import "common.ftl" as common>

<#global title>[failing]${project.fullName}</#global>
<#global content>Build is failing.\n
${project.fullName}::${buildType.name} <@common.short_build_info build/>, agent ${agentName} ${var.buildShortStatusDescription}
${link.buildResultsLink}</#global>
