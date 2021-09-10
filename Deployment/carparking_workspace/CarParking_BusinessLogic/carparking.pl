%====================================================================================
% carparking description   
%====================================================================================
context(ctxcarparking, "localhost",  "TCP", "60000").
context(ctxbasicrobot, "127.0.0.1",  "TCP", "8020").
context(ctxsonar, "192.168.1.153",  "TCP", "60001").
 qactor( basicrobot, ctxbasicrobot, "external").
  qactor( sonaractor, ctxsonar, "external").
  qactor( parkmanagerserviceactor, ctxcarparking, "it.unibo.parkmanagerserviceactor.Parkmanagerserviceactor").
  qactor( trolleyactor, ctxcarparking, "it.unibo.trolleyactor.Trolleyactor").
  qactor( sensorsbrokeractor, ctxcarparking, "it.unibo.sensorsbrokeractor.Sensorsbrokeractor").
  qactor( parkserviceguiactor, ctxcarparking, "it.unibo.parkserviceguiactor.Parkserviceguiactor").
  qactor( parkservicestatusguiactor, ctxcarparking, "it.unibo.parkservicestatusguiactor.Parkservicestatusguiactor").
  qactor( weightactor, ctxcarparking, "it.unibo.weightactor.Weightactor").
  qactor( thermometeractor, ctxcarparking, "it.unibo.thermometeractor.Thermometeractor").
  qactor( fanactor, ctxcarparking, "it.unibo.fanactor.Fanactor").
  qactor( temperaturesentinelactor, ctxcarparking, "it.unibo.temperaturesentinelactor.Temperaturesentinelactor").
  qactor( outdoorsentinelactor, ctxcarparking, "it.unibo.outdoorsentinelactor.Outdoorsentinelactor").
