%====================================================================================
% carparking description   
%====================================================================================
context(ctxcarparking, "localhost",  "TCP", "60000").
 qactor( parkmanagerserviceactor, ctxcarparking, "it.unibo.parkmanagerserviceactor.Parkmanagerserviceactor").
  qactor( trolleyactor, ctxcarparking, "it.unibo.trolleyactor.Trolleyactor").
  qactor( parkserviceguiactor, ctxcarparking, "it.unibo.parkserviceguiactor.Parkserviceguiactor").
  qactor( parkservicestatusguiactor, ctxcarparking, "it.unibo.parkservicestatusguiactor.Parkservicestatusguiactor").
  qactor( weightactor, ctxcarparking, "carparking.WeightActor").
  qactor( sonaractor, ctxcarparking, "carparking.SonarActor").
  qactor( thermometeractor, ctxcarparking, "carparking.ThermometerActor").
  qactor( fanactor, ctxcarparking, "it.unibo.fanactor.Fanactor").
  qactor( outdoorsentinelactor, ctxcarparking, "it.unibo.outdoorsentinelactor.Outdoorsentinelactor").
  qactor( temperaturesentinelactor, ctxcarparking, "it.unibo.temperaturesentinelactor.Temperaturesentinelactor").
