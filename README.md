# springboot-netty-im

A simple example of real-time communication using springboot+netty+redis.

## 功能

基于netty实现websocket的在线单聊、群聊；

基于Redis实现消息的发布和订阅，支持集群部署；

基于Redis实现离线消息的存储和上线后推送；

## 待办列表

使用Vue重构前端；

消息发送失败后通知发送者；

支持消息的存储；