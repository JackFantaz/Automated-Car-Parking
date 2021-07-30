%====================================================================================
% carparking description   
%====================================================================================
context(ctxcarparking, "localhost",  "TCP", "60000").
context(ctxbasicrobot, "127.0.0.1",  "TCP", "8020").
 qactor( basicrobot, ctxbasicrobot, "external").
  qactor( parkmanagerserviceactor, ctxcarparking, "it.unibo.parkmanagerserviceactor.Parkmanagerserviceactor").
  qactor( trolleyactor, ctxcarparking, "it.unibo.trolleyactor.Trolleyactor").
  qactor( sensorsbrokeractor, ctxcarparking, "it.unibo.sensorsbrokeractor.Sensorsbrokeractor").
  qactor( parkserviceguiactor, ctxcarparking, "it.unibo.parkserviceguiactor.Parkserviceguiactor").
  qactor( weightactor, ctxcarparking, "it.unibo.weightactor.Weightactor").
  qactor( sonaractor, ctxcarparking, "it.unibo.sonaractor.Sonaractor").
  qactor( thermometeractor, ctxcarparking, "it.unibo.thermometeractor.Thermometeractor").
  qactor( fanactor, ctxcarparking, "it.unibo.fanactor.Fanactor").
