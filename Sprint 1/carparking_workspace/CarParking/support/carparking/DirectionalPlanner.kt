package carparking

import itunibo.planner.plannerUtil
import mapRoomKotlin.Direction

// class DirectionalPlanner(map: String) {

object directionalPlanner {

	private val map = "parkingMap"
	private val toTheRight = mapOf("N" to "E", "E" to "S", "S" to "W", "W" to "N")

	private var plannedDirection = "S"

	init {
		plannerUtil.initAI()
		plannerUtil.loadRoomMap(map)
		// plannerUtil.showMap()
	}

	fun planFor(place: Array<String>) {
		planForGoal(place[0], place[1], place[2])
	}

	fun planForGoal(x: String, y: String, d: String) {
		plannerUtil.planForGoal(x, y)
		plannedDirection = d
	}

	fun getNextPlannedMove(): String {
		var move = plannerUtil.getNextPlannedMove()
		if (move.isEmpty()) {
			val currentDirection = when (plannerUtil.getDir()) {
				Direction.UP -> "N"
				Direction.DOWN -> "S"
				Direction.LEFT -> "W"
				Direction.RIGHT -> "E"
			}
			if (plannedDirection != currentDirection) {
				if (toTheRight[currentDirection] == plannedDirection) move = "r"
				else move = "l"
			}
		}
		return move
	}

	fun updateMap(move: String) {
		plannerUtil.updateMap(move)
	}

	fun getX(): String {
		return plannerUtil.getPosX().toString()
	}

	fun getY(): String {
		return plannerUtil.getPosY().toString()
	}

	fun getD(): String {
		return when (plannerUtil.getDir()) {
			Direction.UP -> "N"
			Direction.DOWN -> "S"
			Direction.LEFT -> "W"
			Direction.RIGHT -> "E"
		}
	}

}
