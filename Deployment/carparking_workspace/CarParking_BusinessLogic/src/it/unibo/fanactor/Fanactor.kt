/* Generated by AN DISI Unibo */ 
package it.unibo.fanactor

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Fanactor ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "stop"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		 val mock = carparking.temperature.MockFan()  
		return { //this:ActionBasciFsm
				state("stop") { //this:State
					action { //it:State
						mock.turnOff(  )
						updateResourceRep( "fanStop(0)"  
						)
					}
					 transition(edgeName="t44",targetState="start",cond=whenDispatch("fanStart"))
					transition(edgeName="t45",targetState="stop",cond=whenDispatch("fanStop"))
				}	 
				state("start") { //this:State
					action { //it:State
						mock.turnOn(  )
						updateResourceRep( "fanStart(0)"  
						)
					}
					 transition(edgeName="t46",targetState="start",cond=whenDispatch("fanStart"))
					transition(edgeName="t47",targetState="stop",cond=whenDispatch("fanStop"))
				}	 
			}
		}
}
