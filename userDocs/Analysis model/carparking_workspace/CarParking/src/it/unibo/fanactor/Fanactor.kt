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
		 val mock = carparking.LedMock("Fan", 350, 250)  
		return { //this:ActionBasciFsm
				state("stop") { //this:State
					action { //it:State
						mock.turnOff(  )
					}
					 transition(edgeName="t16",targetState="start",cond=whenDispatch("fanStart"))
				}	 
				state("start") { //this:State
					action { //it:State
						mock.turnOn(  )
					}
					 transition(edgeName="t17",targetState="stop",cond=whenDispatch("fanStop"))
				}	 
			}
		}
}
