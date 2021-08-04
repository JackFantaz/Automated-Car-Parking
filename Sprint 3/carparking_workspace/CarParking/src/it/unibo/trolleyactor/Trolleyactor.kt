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
		
				var home = arrayOf("-", "-", "-")
				// var parking = arrayOf("-", "-", "-")
				var indoor = arrayOf("-", "-", "-")
				var outdoor = arrayOf("-", "-", "-")
				var parking1 = arrayOf("-", "-", "-")
				var parking2 = arrayOf("-", "-", "-")
				var parking3 = arrayOf("-", "-", "-")
				var parking4 = arrayOf("-", "-", "-")
				var parking5 = arrayOf("-", "-", "-")
				var parking6 = arrayOf("-", "-", "-")
				var goingHome = false
				
				fun parseLocation(): Array<String> { return arrayOf(getCurSol("X").toString(), getCurSol("Y").toString(), getCurSol("D").toString().toUpperCase()) }
		
		return { //this:ActionBasciFsm
				state("setup") { //this:State
					action { //it:State
						solve("consult('locationsKb.pl')","") //set resVar	
						solve("home(X,Y,D)","") //set resVar	
						 home = parseLocation()  
						solve("indoor(X,Y,D)","") //set resVar	
						 indoor = parseLocation()  
						solve("outdoor(X,Y,D)","") //set resVar	
						 outdoor = parseLocation()  
						solve("parking1(X,Y,D)","") //set resVar	
						 parking1 = parseLocation()  
						solve("parking2(X,Y,D)","") //set resVar	
						 parking2 = parseLocation()  
						solve("parking3(X,Y,D)","") //set resVar	
						 parking3 = parseLocation()  
						solve("parking4(X,Y,D)","") //set resVar	
						 parking4 = parseLocation()  
						solve("parking5(X,Y,D)","") //set resVar	
						 parking5 = parseLocation()  
						solve("parking6(X,Y,D)","") //set resVar	
						 parking6 = parseLocation()  
					}
					 transition(edgeName="t9",targetState="idle",cond=whenDispatch("goto"))
				}	 
				state("idle") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("goto(PLACE)"), Term.createTerm("goto(home)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								carparking.directionalPlanner.planFor( home  )
								 goingHome = true  
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
						if( checkMsgContent( Term.createTerm("goto(PLACE)"), Term.createTerm("goto(parking1)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								carparking.directionalPlanner.planFor( parking1  )
								 goingHome = false  
						}
						if( checkMsgContent( Term.createTerm("goto(PLACE)"), Term.createTerm("goto(parking2)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								carparking.directionalPlanner.planFor( parking2  )
								 goingHome = false  
						}
						if( checkMsgContent( Term.createTerm("goto(PLACE)"), Term.createTerm("goto(parking3)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								carparking.directionalPlanner.planFor( parking3  )
								 goingHome = false  
						}
						if( checkMsgContent( Term.createTerm("goto(PLACE)"), Term.createTerm("goto(parking4)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								carparking.directionalPlanner.planFor( parking4  )
								 goingHome = false  
						}
						if( checkMsgContent( Term.createTerm("goto(PLACE)"), Term.createTerm("goto(parking5)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								carparking.directionalPlanner.planFor( parking5  )
								 goingHome = false  
						}
						if( checkMsgContent( Term.createTerm("goto(PLACE)"), Term.createTerm("goto(parking6)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								carparking.directionalPlanner.planFor( parking6  )
								 goingHome = false  
						}
					}
					 transition( edgeName="goto",targetState="working", cond=doswitch() )
				}	 
				state("working") { //this:State
					action { //it:State
						if(  goingHome  
						 ){updateResourceRep( "idle"  
						)
						}
						else
						 {updateResourceRep( "working"  
						 )
						 }
						 val move = carparking.directionalPlanner.getNextPlannedMove()  
						if(  move.isNotEmpty()  
						 ){if(  move == "w"  
						 ){request("step", "step(340)" ,"basicrobot" )  
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
					 transition(edgeName="t10",targetState="working",cond=whenTimeout("local_tout_trolleyactor_working"))   
					transition(edgeName="t11",targetState="working",cond=whenReply("stepdone"))
					transition(edgeName="t12",targetState="idle",cond=whenDispatch("goto"))
					transition(edgeName="t13",targetState="working",cond=whenDispatch("startTrolley"))
					transition(edgeName="t14",targetState="stopped",cond=whenDispatch("stopTrolley"))
				}	 
				state("stopped") { //this:State
					action { //it:State
						updateResourceRep( "stopped"  
						)
					}
					 transition(edgeName="t15",targetState="working",cond=whenDispatch("startTrolley"))
					transition(edgeName="t16",targetState="stopped",cond=whenDispatch("stopTrolley"))
				}	 
			}
		}
}
