<#-- Uses FreeMarker template syntax, template guide can be found at http://freemarker.org/docs/dgui.html -->

<#import "common.ftl" as common>

<#global title>[started]${project.fullName}</#global>
<#global content>Build started<#if build.triggeredBy.triggeredByUser> (triggered by ${build.triggeredBy.user.descriptiveName})</#if>.
  ${project.fullName}::${buildType.name} <@common.short_build_info build/>, agent ${agentName}
  ${link.buildResultsLink}</#global>