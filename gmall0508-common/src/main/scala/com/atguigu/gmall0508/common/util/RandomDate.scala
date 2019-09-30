package com.atguigu.gmall0508.common.util


import java.util.Date

object RandomDate {
    def apply(startDate: Date, stopDate: Date, step: Int) = {
        val randomDate: RandomDate = new RandomDate
        val avgStepTime: Long = (stopDate.getTime - startDate.getTime) / step
        randomDate.maxStepTime = 4 * avgStepTime
        randomDate.lastDateTIme = startDate.getTime
        randomDate
    }
}

class RandomDate {
    // 上次 action 的时间
    var lastDateTIme: Long = _
    // 每次最大的步长时间
    var maxStepTime: Long = _
    
    /**
      * 得到一个随机时间
      * @return
      */
    def getRandomDate: Date = {
        // 这次操作的相比上次的步长
        val timeStep: Long = RandomNumUtil.randomLong(0, maxStepTime)
        lastDateTIme += timeStep
        new Date(lastDateTIme)
    }
}
