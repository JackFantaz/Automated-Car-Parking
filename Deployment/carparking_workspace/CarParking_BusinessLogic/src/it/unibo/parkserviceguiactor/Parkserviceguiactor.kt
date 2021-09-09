/* Generated by AN DISI Unibo */ 
package it.unibo.parkserviceguiactor

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Parkserviceguiactor ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "setup"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		return { //this:ActionBasciFsm
				state("setup") { //this:State
					action { //it:State
						forward("notice", "notice(system(started))" ,"parkserviceguiactor" ) 
					}
					 transition( edgeName="goto",targetState="receive", cond=doswitch() )
				}	 
				state("receive") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("slotnum(SLOTNUM)"), Term.createTerm("slotnum(SLOTNUM)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("Client's GUI feedback -> The SLOTNUM is ${payloadArg(0)}")
								updateResourceRep( "${currentMsg.msgContent()}"  
								)
						}
						if( checkMsgContent( Term.createTerm("tokenid(TOKENID)"), Term.createTerm("tokenid(TOKENID)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("Client's GUI feedback -> The TOKENID is ${payloadArg(0)}")
								updateResourceRep( "${currentMsg.msgContent()}"  
								)
						}
						if( checkMsgContent( Term.createTerm("notice(NOTICE)"), Term.createTerm("notice(MESSAGE)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("Client's GUI feedback -> Notice received: ${payloadArg(0)}")
								updateResourceRep( "${currentMsg.msgContent()}"  
								)
						}
					}
					 transition(edgeName="t23",targetState="receive",cond=whenDispatch("slotnum"))
					transition(edgeName="t24",targetState="receive",cond=whenDispatch("tokenid"))
					transition(edgeName="t25",targetState="receive",cond=whenDispatch("notice"))
					transition(edgeName="t26",targetState="businessLogicControl",cond=whenDispatch("enterRequest"))
					transition(edgeName="t27",targetState="businessLogicControl",cond=whenDispatch("carEnter"))
					transition(edgeName="t28",targetState="businessLogicControl",cond=whenDispatch("exitRequest"))
				}	 
				state("businessLogicControl") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("enterRequest(N)"), Term.createTerm("enterRequest(0)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("Client's GUI feedback -> redirecting enterRequest(0)")
								forward("enterRequest", "enterRequest(0)" ,"parkmanagerserviceactor" ) 
						}
						if( checkMsgContent( Term.createTerm("carEnter(N)"), Term.createTerm("carEnter(0)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("Client's GUI feedback -> redirecting carEnter(0)")
								forward("carEnter", "carEnter(0)" ,"parkmanagerserviceactor" ) 
						}
						if( checkMsgContent( Term.createTerm("exitRequest(TOKENID)"), Term.createTerm("exitRequest(TOKENID)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 var Temp = payloadArg(0)  
								println("Client's GUI feedback -> redirecting exitRequest($Temp)")
								forward("exitRequest", "exitRequest($Temp)" ,"parkmanagerserviceactor" ) 
						}
					}
					 transition( edgeName="goto",targetState="receive", cond=doswitch() )
				}	 
			}
		}
}
