package com.agtuigu.realtime.bean

/*
{"payment_way":"2","delivery_address":"QxOBoTzRMiEyDwAjwJQk","consignee":"FVMYIu","create_time":"2019-05-16 02:38:24","order_comment":"BdOKhFJoDXdyjxNxXvlS","expire_time":"","order_status":"1","out_trade_no":"6504097957","tracking_no":"","total_amount":"898.0","user_id":"4","img_url":"","province_id":"7","consignee_tel":"13977601512","trade_body":"","id":"29","parent_order_id":"","operate_time":""}
 */
case class OrderInfo(id: String,
                     province_id: String,
                     var consignee: String,
                     order_comment: String,
                     var consignee_tel: String,
                     order_status: String,
                     payment_way: String,
                     user_id: String,
                     img_url: String,
                     total_amount: Double,
                     expire_time: String,
                     delivery_address: String,
                     create_time: String,
                     operate_time: String,
                     tracking_no: String,
                     parent_order_id: String,
                     out_trade_no: String,
                     trade_body: String,
                     var create_date: String,
                     var create_hour: String)
