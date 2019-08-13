# teamcity-dadapush-notification-plugin

[DaDaPush: Real-time Notifications App][1]
===========

Send real-time notifications through our API without coding and maintaining your own app for iOS or Android devices.

Usage
=====
- download release dadapush-plugin.zip
- open your TeamCity server, go to page `Administration -> Plugins List
`
- click button `upload plugin zip`, select zip file and enable.

Setting
=====
- go to [DaDaPush](https://www.dadapush.com), sign in or register an account.
- create new channel, save your channel token.
- go to `your profile`,`Notification Rules`
- parameter `Base Path` *OPTIONAL* default value: `https://www.dadapush.com`
- parameter `Channel Token` *REQUIRED* look like: `ctXXXXXXXXX`

[1]: https://www.dadapush.com