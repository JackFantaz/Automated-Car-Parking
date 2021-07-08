package carparking

import it.unibo.actor0.ActorBasicKotlin
import it.unibo.actor0.ApplMessage
import it.unibo.kactor.ActorBasicFsm
import it.unibo.kactor.MsgUtil
import it.unibo.robotService.ApplMsgs
import it.unibo.robotService.BasicStepRobotActor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class RobotProxy(private val master: ActorBasicFsm, wenvAddr: String) : ActorBasicKotlin("robotproxy") {

    private val robot = BasicStepRobotActor("basicsteprobotactor", this, this.scope, wenvAddr)

	fun doMove(move: String) {
		when (move) {
			"w" -> moveForward()
			"l" -> rotateLeft()
			"r" -> rotateRight()
		}
	}
	
    fun moveForward() {
        robot.send(ApplMsgs.stepRobot_w(master.name))
    }

    fun rotateLeft() {
        robot.send(ApplMsgs.stepRobot_l(master.name))
    }

    fun rotateRight() {
        robot.send(ApplMsgs.stepRobot_r(master.name))
    }

    override suspend fun handleInput(msg: ApplMessage) {
        val message = MsgUtil.buildDispatch(name, msg.msgId, msg.msgContent, master.name)
        MsgUtil.sendMsg(message, master)
    }

}
