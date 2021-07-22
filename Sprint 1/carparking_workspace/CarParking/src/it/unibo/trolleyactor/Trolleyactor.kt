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
		return "setup"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		
				// var planner = carparking.DirectionalPlanner("parkingMap")
				var home = arrayOf("-", "-", "-")
				var parking = arrayOf("-", "-", "-")
				var indoor = arrayOf("-", "-", "-")
				var outdoor = arrayOf("-", "-", "-")
				var goingHome = false
		return { //this:ActionBasciFsm
				state("setup") { //this:State
					action { //it:State
						solve("consult('locationsKb.pl')","") //set resVar	
						solve("home(X,Y,D)","") //set resVar	
						 home = arrayOf(getCurSol("X").toString(), getCurSol("Y").toString(), getCurSol("D").toString().toUpperCase())  
						solve("parking(X,Y,D)","") //set resVar	
						 parking = arrayOf(getCurSol("X").toString(), getCurSol("Y").toString(), getCurSol("D").toString().toUpperCase())  
						solve("indoor(X,Y,D)","") //set resVar	
						 indoor = arrayOf(getCurSol("X").toString(), getCurSol("Y").toString(), getCurSol("D").toString().toUpperCase())  
						solve("outdoor(X,Y,D)","") //set resVar	
						 outdoor = arrayOf(getCurSol("X").toString(), getCurSol("Y").toString(), getCurSol("D").toString().toUpperCase())  
					}
					 transition(edgeName="t6",targetState="idle",cond=whenDispatch("goto"))
				}	 
				state("idle") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("goto(PLACE)"), Term.createTerm("goto(home)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								carparking.directionalPlanner.planFor( home  )
								 goingHome = true  
						}
						if( checkMsgContent( Term.createTerm("goto(PLACE)"), Term.createTerm("goto(parking)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								carparking.directionalPlanner.planFor( parking  )
								 goingHome = false  
						}
						if( checkMsgContent( Term.createTerm("goto(PLACE)"), Term.createTerm("goto(outdoor)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								carparking.directionalPlanner.planFor( outdoor  )
								 goingHome = false  
						}
						if( checkMsgContent( Term.createTerm("goto(PLACE)"), Term.createTerm("goto(indoor)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								carparking.directionalPlanner.planFor( indoor  )
								 goingHome = false  
						}
					}
					 transition( edgeName="goto",targetState="working", cond=doswitch() )
				}	 
				state("working") { //this:State
					action { //it:State
						 val move = carparking.directionalPlanner.getNextPlannedMove()  
						if(  move.isNotEmpty()  
						 ){if(  move == "w"  
						 ){request("step", "step(350)" ,"basicrobot" )  
						}
						if(  move == "l"  
						 ){forward("cmd", "cmd(l)" ,"basicrobot" ) 
						}
						if(  move == "r"  
						 ){forward("cmd", "cmd(r)" ,"basicrobot" ) 
						}
						carparking.directionalPlanner.updateMap( move  )
						}
						else
						 {delay(1000) 
						 if(  !goingHome  
						  ){forward("movementDone", "movementDone(0)" ,"parkmanagerserviceactor" ) 
						 }
						 }
						stateTimer = TimerActor("timer_working", 
							scope, context!!, "local_tout_trolleyactor_working", 450.toLong() )
					}
					 transition(edgeName="t7",targetState="working",cond=whenTimeout("local_tout_trolleyactor_working"))   
					transition(edgeName="t8",targetState="working",cond=whenReply("stepdone"))
					transition(edgeName="t9",targetState="idle",cond=whenDispatch("goto"))
				}	 
			}
		}
}
