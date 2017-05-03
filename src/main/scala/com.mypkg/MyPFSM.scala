package com.mypkg

import akka.persistence.fsm.PersistentFSM
import akka.actor.Actor
import scala.reflect.ClassTag

sealed trait MyFSMState extends PersistentFSM.FSMState
case object MyFSMState1 extends MyFSMState{ override def identifier: String = "MyFSMState1" }
case object MyFSMState2 extends MyFSMState{ override def identifier: String = "MyFSMState2" }

sealed trait MyFSMEvent
case class IntAdded(i: Int) extends MyFSMEvent

class MyPFSM(implicit val domainEventClassTag: ClassTag[MyFSMEvent])
  extends Actor with PersistentFSM[MyFSMState, List[Int], MyFSMEvent] {

  override def persistenceId: String = "my-pfsm"

  override def applyEvent(event: MyFSMEvent, currentData: List[Int]): List[Int] = event match {
    case IntAdded(i) => i :: currentData
  }

  startWith(MyFSMState1, Nil)

  when(MyFSMState1){
    case Event(i: Int, _) =>
      goto(MyFSMState2) applying IntAdded(i)
  }

  when(MyFSMState2){
    case Event(i: Int, _) =>
      goto(MyFSMState1) applying IntAdded(i)
  }

}