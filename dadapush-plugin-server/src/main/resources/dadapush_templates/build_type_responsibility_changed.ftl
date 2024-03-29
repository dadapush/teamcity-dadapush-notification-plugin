<#-- Uses FreeMarker template syntax, template guide can be found at http://freemarker.org/docs/dgui.html -->

<#import "common.ftl" as common>
<#import "responsibility.ftl" as resp>

<#global title>[Investigation update]${project.fullName}</#global>
<#global content>Investigation update.
<@resp.subject responsibility '${project.fullName}::${buildType.name}'/>

<@resp.removeMethod responsibility/>
<@resp.comment responsibility/>
${link.buildTypeConfigLink}</#global>
