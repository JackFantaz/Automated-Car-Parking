/* Generated by AN DISI Unibo */ 
package it.unibo.trolleyactor

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Trolleyactor ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "nop"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		
				val planner = carparking.DirectionalPlanner("parkingMap")
				val proxy = carparking.RobotProxy(this, "localhost")
				val home = arrayOf("0", "0", "S")
				val parking = arrayOf("1", "1", "E")
				val indoor = arrayOf("6", "0", "N")
				val outdoor = arrayOf("6", "4", "S")
				var goingHome = false
		return { //this:ActionBasciFsm
				state("nop") { //this:State
					action { //it:State
					}
					 transition(edgeName="t6",targetState="idle",cond=whenDispatch("goto"))
				}	 
				state("idle") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("goto(PLACE)"), Term.createTerm("goto(home)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								planner.planFor( home  )
								 goingHome = true  
						}
						if( checkMsgContent( Term.createTerm("goto(PLACE)"), Term.createTerm("goto(parking)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								planner.planFor( parking  )
								 goingHome = false  
						}
						if( checkMsgContent( Term.createTerm("goto(PLACE)"), Term.createTerm("goto(outdoor)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								planner.planFor( outdoor  )
								 goingHome = false  
						}
						if( checkMsgContent( Term.createTerm("goto(PLACE)"), Term.createTerm("goto(indoor)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								planner.planFor( indoor  )
								 goingHome = false  
						}
					}
					 transition( edgeName="goto",targetState="working", cond=doswitch() )
				}	 
				state("working") { //this:State
					action { //it:State
						 val move = planner.getNextPlannedMove()  
						if(  move.isNotEmpty()  
						 ){proxy.doMove( move  )
						planner.updateMap( move  )
						}
						else
						 {if(  !goingHome  
						  ){forward("movementDone", "movementDone(0)" ,"parkmanagerserviceactor" ) 
						 }
						 }
					}
					 transition(edgeName="t7",targetState="working",cond=whenDispatch("endmove"))
					transition(edgeName="t8",targetState="idle",cond=whenDispatch("goto"))
				}	 
			}
		}
}