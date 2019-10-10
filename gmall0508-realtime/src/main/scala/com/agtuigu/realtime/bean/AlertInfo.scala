package com.agtuigu.realtime.bean

case class AlertInfo(mid: String,
                     uids: java.util.Set[String], // 领优惠券的用户
                     itemIds: java.util.Set[String],  // 优惠券对应的商品
                     events: java.util.Set[String],
                     ts: Long)
