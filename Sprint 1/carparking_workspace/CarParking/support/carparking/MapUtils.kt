package carparking

import it.unibo.actor0.ActorBasicKotlin
import it.unibo.actor0.ApplMessage
import it.unibo.robotService.ApplMsgs
import it.unibo.robotService.BasicStepRobotActor
import itunibo.planner.plannerUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

fun main() {
    runBlocking {
        val mapper = Mapper()
        val message = ApplMsgs.startAny("main")
        mapper.send(message)
        delay(60000)
    }
}

class Mapper : ActorBasicKotlin("trolley") {

    val robot = BasicStepRobotActor("robot", this, scope, "localhost")
    // val planner = DirectionalPlanner("")

    val sequence = arrayOf(
        arrayOf("1", "1", "E"),
        arrayOf("1", "2", "E"),
        arrayOf("1", "3", "E"),
        arrayOf("4", "1", "W"),
        arrayOf("4", "2", "W"),
        arrayOf("4", "3", "W")
    )

    var times = 0
    var step = 0

    init {
        plannerUtil.initAI()
        plannerUtil.showMap()
    }

    override suspend fun handleInput(msg: ApplMessage) {
        if (msg.msgId == "start") {
            doMove("w")
        } else if (msg.msgId == "endmove" && times < 4) {
            val content = JSONObject(msg.msgContent)
            if (content.getBoolean("endmove") == true) {
                doMove("w")
            } else {
                plannerUtil.wallFound()
                doMove("l")
                times++
            }
        } else {
            directionalPlanner.planForGoal(sequence[step][0], sequence[step][1], sequence[step][2])
            val move = directionalPlanner.getNextPlannedMove()
            if (move.isNotEmpty()) {
                doMove(move)
            } else {
                plannerUtil.updateMapObstacleOnCurrentDirection()
                plannerUtil.showMap()
                step++
                if (step < sequence.size) {
                    directionalPlanner.planForGoal(sequence[step][0], sequence[step][1], sequence[step][2])
                    doMove(directionalPlanner.getNextPlannedMove())
                } else {
                    plannerUtil.saveRoomMap("parkingMap")
                }
            }
        }

    }

    private fun doMove(move: String) {
        when (move) {
            "w" -> robot.send(ApplMsgs.stepRobot_w(name))
            "l" -> robot.send(ApplMsgs.stepRobot_l(name))
            "r" -> robot.send(ApplMsgs.stepRobot_r(name))
        }
        plannerUtil.updateMap(move)
        plannerUtil.showMap()
    }

}
